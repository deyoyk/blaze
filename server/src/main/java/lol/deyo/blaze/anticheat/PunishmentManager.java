package lol.deyo.blaze.anticheat;

import com.mojang.authlib.GameProfile;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.GameProfileBanEntry;
import net.minecraft.server.v1_8_R3.GameProfileBanList;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public final class PunishmentManager {
   public enum PunishmentType {
      WARN, KICK, BAN
   }

   public static final class ScheduledPunishment {
      public final int violationNumber;
      public final PunishmentType type;
      public final long durationMillis;
      public final String source;

      ScheduledPunishment(int violationNumber, PunishmentType type, long durationMillis, String source) {
         this.violationNumber = violationNumber;
         this.type = type;
         this.durationMillis = durationMillis;
         this.source = source;
      }
   }

   public static final class Record {
      public final long id;
      public final java.util.UUID uuid;
      public final String name;
      public final String check;
      public final PunishmentType type;
      public final String reason;
      public final long createdAt;
      public final long expires;
      public volatile boolean active;

      public Record(long id, java.util.UUID uuid, String name, String check, PunishmentType type,
             String reason, long createdAt, long expires, boolean active) {         this.id = id;
         this.uuid = uuid;
         this.name = name;
         this.check = check;
         this.type = type;
         this.reason = reason;
         this.createdAt = createdAt;
         this.expires = expires;
         this.active = active;
      }
   }

   private static final String REASON = "Blaze Anticheat: Unfair advantage";
   private static final long ID_MODULUS = 1000000000000L;

   final Map<String, List<ScheduledPunishment>> schedule = new HashMap<>();
   private final Random idRandom = new Random();
   private volatile boolean scheduleLoaded;

   public void loadSchedule(AnticheatPunishmentsConfig config) {
      synchronized (this.schedule) {
         this.schedule.clear();
         if (config != null) {
            for (ConfigSetting<?> setting : config.getSettings()) {
               String[] pathParts = setting.getFullPath().split("\\.");
               if (pathParts.length != 2) {
                  continue;
               }

               String source = pathParts[0];
               Object value = setting.getValue();
               if (value == null) {
                  continue;
               }

               ScheduledPunishment parsed = this.parseEntry(source, value.toString());
               if (parsed != null) {
                  this.schedule.computeIfAbsent(source.toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(parsed);
               }
            }

            for (List<ScheduledPunishment> list : this.schedule.values()) {
               list.sort((a, b) -> Integer.compare(a.violationNumber, b.violationNumber));
            }
         }

         this.scheduleLoaded = true;
      }
   }

   ScheduledPunishment parseEntry(String source, String raw) {
      if (raw == null || raw.trim().isEmpty()) {
         return null;
      }

      String[] parts = raw.trim().split(":");
      if (parts.length < 2) {
         return null;
      }

      int violationNumber;
      try {
         violationNumber = Integer.parseInt(parts[0].trim());
      } catch (NumberFormatException e) {
         return null;
      }

      PunishmentType type;
      switch (parts[1].trim().toUpperCase(Locale.ROOT)) {
         case "WARN": type = PunishmentType.WARN; break;
         case "KICK": type = PunishmentType.KICK; break;
         case "BAN": type = PunishmentType.BAN; break;
         default: return null;
      }

      long duration = parts.length >= 3 ? parseDuration(parts[2]) : 0L;
      return new ScheduledPunishment(violationNumber, type, duration, source);
   }

   static long parseDuration(String raw) {
      if (raw == null || raw.isEmpty() || raw.equalsIgnoreCase("0") || raw.equalsIgnoreCase("perm")
         || raw.equalsIgnoreCase("permanent") || raw.equalsIgnoreCase("forever")) {
         return 0L;
      }

      raw = raw.toLowerCase(Locale.ROOT).trim();
      long unitMillis;
      String numberPart;
      if (raw.endsWith("d")) {
         unitMillis = 86400000L;
         numberPart = raw.substring(0, raw.length() - 1);
      } else if (raw.endsWith("h")) {
         unitMillis = 3600000L;
         numberPart = raw.substring(0, raw.length() - 1);
      } else if (raw.endsWith("m")) {
         unitMillis = 60000L;
         numberPart = raw.substring(0, raw.length() - 1);
      } else if (raw.endsWith("s")) {
         unitMillis = 1000L;
         numberPart = raw.substring(0, raw.length() - 1);
      } else {
         unitMillis = 86400000L;
         numberPart = raw;
      }

      try {
         return Long.parseLong(numberPart.trim()) * unitMillis;
      } catch (NumberFormatException e) {
         return 0L;
      }
   }

   public ScheduledPunishment resolve(String checkName, int violationCount) {
      List<ScheduledPunishment> list = this.schedule.get(checkName.toLowerCase(Locale.ROOT));
      if (list == null || list.isEmpty()) {
         list = this.schedule.get("global");
      }

      if (list == null || list.isEmpty()) {
         return null;
      }

      ScheduledPunishment best = null;
      for (ScheduledPunishment sp : list) {
         if (sp.violationNumber <= violationCount && (best == null || sp.violationNumber > best.violationNumber)) {
            best = sp;
         }
      }

      return best;
   }

   public boolean punish(ACPlayer data, Check check, ScheduledPunishment punishment, int violationCount) {
      EntityPlayer entityPlayer = data.player;
      Player player = entityPlayer.getBukkitEntity();
      long id = this.generateId();
      long now = System.currentTimeMillis();
      long expires = punishment.durationMillis > 0 ? now + punishment.durationMillis : 0L;
      String checkName = check != null ? check.getName() : "Unknown";

      Record record = new Record(id, entityPlayer.getUniqueID(), player.getName(), checkName,
         punishment.type, REASON, now, expires, true);

      ACManager.INSTANCE.getStore().queuePunishment(record);

      switch (punishment.type) {
         case WARN:
            this.sendWarn(player, checkName, record);
            break;
         case KICK:
            this.sendKick(player, checkName, record);
            break;
         case BAN:
            this.applyBan(entityPlayer, record);
            this.sendBan(player, checkName, record);
            this.broadcast(record,
               () -> (Boolean)AnticheatMessagesConfig.banBroadcastEnabled.getValue(),
               () -> AnticheatMessagesConfig.banBroadcast.getValue());
            break;
      }

      return true;
   }

   private void sendWarn(Player player, String checkName, Record record) {
      if ((Boolean)AnticheatMessagesConfig.warnMessageEnabled.getValue()) {
         String message = this.replace(AnticheatMessagesConfig.warnMessage.getValue(), player.getName(), checkName, record);
         MinecraftServer.getServer().postToMainThread(() -> player.sendMessage(CC.t(message)));
      }
   }

   private void sendKick(Player player, String checkName, Record record) {
      this.broadcast(record,
         () -> (Boolean)AnticheatMessagesConfig.kickBroadcastEnabled.getValue(),
         () -> AnticheatMessagesConfig.kickBroadcast.getValue());

      String message;
      if ((Boolean)AnticheatMessagesConfig.kickMessageEnabled.getValue()) {
         message = this.replace(AnticheatMessagesConfig.kickMessage.getValue(), player.getName(), checkName, record);
      } else {
         message = "";
      }

      MinecraftServer.getServer().postToMainThread(() -> player.kickPlayer(CC.t(message)));
   }

   private void sendBan(Player player, String checkName, Record record) {
      String message;
      if ((Boolean)AnticheatMessagesConfig.banMessageEnabled.getValue()) {
         message = this.replace(AnticheatMessagesConfig.banMessage.getValue(), player.getName(), checkName, record);
      } else {
         message = "";
      }

      MinecraftServer.getServer().postToMainThread(() -> player.kickPlayer(CC.t(message)));
   }

   private void applyBan(EntityPlayer entityPlayer, Record record) {
      GameProfileBanEntry entry = new GameProfileBanEntry(
         entityPlayer.getProfile(),
         new java.util.Date(record.createdAt),
         "Blaze Anticheat",
         record.expires > 0 ? new java.util.Date(record.expires) : null,
         record.reason + " [ID:" + record.id + "]"
      );
      MinecraftServer.getServer().getPlayerList().getProfileBans().add(entry);
   }

   private void broadcast(Record record, java.util.function.Supplier<Boolean> enabled, java.util.function.Supplier<String> format) {
      if (!enabled.get()) {
         return;
      }

      String message = CC.t(this.replace(format.get(), record.name, record.check, record));
      MinecraftServer.getServer().postToMainThread(() -> {
         for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(message);
         }

         MinecraftServer.LOGGER.info(message);
      });
   }

   private String replace(String template, String playerName, String checkName, Record record) {
      return template
         .replace("%player%", playerName)
         .replace("%check%", checkName)
         .replace("%reason%", record.reason)
         .replace("%id%", String.valueOf(record.id))
         .replace("%expires%", record.expires > 0
            ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date(record.expires))
            : "Permanent");
   }

   private long generateId() {
      return Math.abs(this.idRandom.nextLong()) % ID_MODULUS;
   }

   public boolean unban(long id, String by) {
      Record record = ACManager.INSTANCE.getStore().getPunishment(id);
      if (record == null || !record.active || record.type != PunishmentType.BAN) {
         return false;
      }

      GameProfileBanList bans = MinecraftServer.getServer().getPlayerList().getProfileBans();
      GameProfile profile = bans.a(record.name);
      if (profile == null) {
         for (GameProfileBanEntry entry : bans.getValues()) {
            if (entry.getKey().getId() != null && entry.getKey().getId().equals(record.uuid)) {
               profile = entry.getKey();
               break;
            }
         }
      }

      if (profile != null) {
         bans.remove(profile);
      }

      ACManager.INSTANCE.getStore().markPunishmentInactive(id, by);
      return true;
   }

   public List<Record> getPunishments(java.util.UUID uuid) {
      return ACManager.INSTANCE.getStore().getPunishments(uuid);
   }

   public List<Record> getPunishmentsByName(String name) {
      return ACManager.INSTANCE.getStore().getPunishmentsByName(name);
   }

   public List<Record> getActiveBans() {
      return ACManager.INSTANCE.getStore().getActiveBans();
   }

   public Record getPunishment(long id) {
      return ACManager.INSTANCE.getStore().getPunishment(id);
   }

   public int countPunishments(PunishmentType type, long sinceMillis) {
      return ACManager.INSTANCE.getStore().countPunishments(type, sinceMillis);
   }

   public String topCheck(long sinceMillis) {
      return ACManager.INSTANCE.getStore().topPunishedCheck(sinceMillis);
   }

   public boolean isScheduleLoaded() {
      return this.scheduleLoaded;
   }

   public Collection<List<ScheduledPunishment>> schedules() {
      synchronized (this.schedule) {
         return new ArrayList<>(this.schedule.values());
      }
   }
}
