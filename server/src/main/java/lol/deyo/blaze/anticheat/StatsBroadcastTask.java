package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.utils.CC;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class StatsBroadcastTask implements Runnable {
   private static final long SLEEP_MILLIS = 10000L;
   private volatile boolean running;
   private Thread thread;
   private long nextRunMillis;

   @Override
   public void run() {
      while (this.running) {
         long now = System.currentTimeMillis();
         if (now >= this.nextRunMillis) {
            this.nextRunMillis = now + (long)(Integer)AnticheatMessagesConfig.statsIntervalMinutes.getValue() * 60000L;
            this.broadcastStats(now);
         }

         try {
            Thread.sleep(SLEEP_MILLIS);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
         }
      }
   }

   public void start() {
      if (!(Boolean)AnticheatMessagesConfig.statsEnabled.getValue()) {
         return;
      }

      this.running = true;
      this.nextRunMillis = System.currentTimeMillis()
         + (long)(Integer)AnticheatMessagesConfig.statsIntervalMinutes.getValue() * 60000L;
      this.thread = new Thread(this, "Blaze-AC-StatsBroadcast");
      this.thread.setDaemon(true);
      this.thread.start();
   }

   public void stop() {
      this.running = false;
      if (this.thread != null) {
         this.thread.interrupt();
         this.thread = null;
      }
   }

   private void broadcastStats(long now) {
      PunishmentManager punishments = ACManager.INSTANCE.getPunishments();
      long windowMillis = (long)(Integer)AnticheatMessagesConfig.statsDays.getValue() * 86400000L;
      long since = now - windowMillis;
      int bans = 0;
      int kicks = 0;
      if ((Boolean)AnticheatMessagesConfig.statsIncludeBans.getValue()) {
         bans = punishments.countPunishments(PunishmentManager.PunishmentType.BAN, since);
      }

      if ((Boolean)AnticheatMessagesConfig.statsIncludeKicks.getValue()) {
         kicks = punishments.countPunishments(PunishmentManager.PunishmentType.KICK, since);
      }

      String topCheck = "";
      if ((Boolean)AnticheatMessagesConfig.statsIncludeChecks.getValue()) {
         String check = punishments.topCheck(since);
         if (check != null) {
            topCheck = check;
         }
      }

      String window;
      int days = (Integer)AnticheatMessagesConfig.statsDays.getValue();
      if (days == 1) {
         window = "day";
      } else if (days == 7) {
         window = "week";
      } else if (days == 30 || days == 31) {
         window = "month";
      } else {
         window = days + " days";
      }

      String message = CC.t(((String)AnticheatMessagesConfig.statsMessage.getValue())
         .replace("%bans%", String.valueOf(bans))
         .replace("%kicks%", String.valueOf(kicks))
         .replace("%window%", window)
         .replace("%topcheck%", topCheck));

      MinecraftServer.getServer().postToMainThread(() -> {
         for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(message);
         }

         MinecraftServer.LOGGER.info(message);
      });
   }
}
