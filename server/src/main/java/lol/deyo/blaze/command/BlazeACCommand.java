package lol.deyo.blaze.command;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.anticheat.PunishmentManager;
import lol.deyo.blaze.anticheat.menu.AnticheatMainMenu;
import lol.deyo.blaze.configs.AnticheatConfigHolder;
import lol.deyo.blaze.framework.commands.Command;
import lol.deyo.blaze.utils.CC;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlazeACCommand extends Command {
   private static final String ADMIN_PERMISSION = "blaze.ac.admin";

   public BlazeACCommand() {
      super("blazeac");
      this.setDescription("Blaze anticheat alerts, stats and punishments");
      this.setPermission("blaze.ac.alerts");
      this.setAliases(Arrays.asList("ac", "blazeanticheat"));
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      if (args.length == 0 || args[0].equalsIgnoreCase("alerts")) {
         ACManager.INSTANCE.toggleAlerts();
         this.sendMessage(sender, new String[]{"&6Blaze AC &e Alerts " + (ACManager.INSTANCE.alertsToggled() ? "&aenabled" : "&cdisabled")});
         return;
      }

      if (args[0].equalsIgnoreCase("stats")) {
         if (args.length >= 2) {
            ACPlayer data = this.findPlayer(args[1]);
            if (data == null) {
               this.sendMessage(sender, new String[]{"&cPlayer not tracked: " + args[1]});
               return;
            }

            this.sendMessage(sender, new String[]{"&6Blaze AC &e " + data.player.getName() + " ping=" + data.getPingMillis() + "ms"});
            for (int i = 0; i < data.violations.size(); i++) {
               double vl = data.violations.levelAt(i);
               if (vl > 0.0) {
                  this.sendMessage(sender, new String[]{"&7- &c" + data.violations.nameAt(i) + "&7 vl=" + String.format("%.1f", vl)
                     + " &7violations=" + data.violations.violationCountAt(i)});
               }
            }
         } else {
            this.sendMessage(sender, new String[]{"&6Blaze AC &e tracking " + ACManager.INSTANCE.getOnlineTracked() + " players"});
         }
         return;
      }

      if (args[0].equalsIgnoreCase("flush")) {
         ACManager.INSTANCE.getStore().flush();
         this.sendMessage(sender, new String[]{"&6Blaze AC &e violation store flushed"});
         return;
      }

      if (args[0].equalsIgnoreCase("punishments")) {
         if (!sender.hasPermission(ADMIN_PERMISSION)) {
            this.sendMessage(sender, new String[]{"&cYou do not have permission for this."});
            return;
         }

         List<PunishmentManager.Record> records;
         if (args.length >= 2) {
            records = ACManager.INSTANCE.getPunishments().getPunishmentsByName(args[1]);
            this.sendMessage(sender, new String[]{"&6Blaze AC &e punishments for &f" + args[1]});
         } else {
            records = ACManager.INSTANCE.getPunishments().getActiveBans();
            this.sendMessage(sender, new String[]{"&6Blaze AC &e active anticheat bans"});
         }

         if (records.isEmpty()) {
            this.sendMessage(sender, new String[]{"&7none"});
            return;
         }

         for (PunishmentManager.Record record : records) {
            this.sendMessage(sender, new String[]{this.formatRecord(record)});
         }
         return;
      }

      if (args[0].equalsIgnoreCase("unban")) {
         if (!sender.hasPermission(ADMIN_PERMISSION)) {
            this.sendMessage(sender, new String[]{"&cYou do not have permission for this."});
            return;
         }

         if (args.length < 2) {
            this.sendMessage(sender, new String[]{"&6Blaze AC &e Usage: /blazeac unban <punishmentId|player>"});
            return;
         }

         String target = args[1];
         PunishmentManager punishments = ACManager.INSTANCE.getPunishments();
         PunishmentManager.Record record = null;
         try {
            record = punishments.getPunishment(Long.parseLong(target));
         } catch (NumberFormatException ignored) {
         }

         if (record == null) {
            List<PunishmentManager.Record> records = punishments.getPunishmentsByName(target);
            for (PunishmentManager.Record r : records) {
               if (r.active && r.type == PunishmentManager.PunishmentType.BAN) {
                  record = r;
                  break;
               }
            }
         }

         if (record == null) {
            this.sendMessage(sender, new String[]{"&cNo anticheat punishment found for: " + target});
            return;
         }

         if (record.type != PunishmentManager.PunishmentType.BAN) {
            this.sendMessage(sender, new String[]{"&cPunishment " + record.id + " is a " + record.type + ", not a ban."});
            return;
         }

         if (!record.active) {
            this.sendMessage(sender, new String[]{"&cPunishment " + record.id + " is already lifted."});
            return;
         }

         String by = sender instanceof Player ? ((Player)sender).getName() : "console";
         if (punishments.unban(record.id, by)) {
            this.sendMessage(sender, new String[]{"&6Blaze AC &a lifted anticheat ban &e" + record.id + " &afor &f" + record.name});
         } else {
            this.sendMessage(sender, new String[]{"&cFailed to lift punishment " + record.id});
         }
         return;
      }

      if (args[0].equalsIgnoreCase("gui")) {
         if (!(sender instanceof Player)) {
            this.sendMessage(sender, new String[]{"&cThis command can only be used by players."});
            return;
         }

         Player player = (Player)sender;
         if (!player.hasPermission(ADMIN_PERMISSION)) {
            this.sendMessage(sender, new String[]{"&cYou do not have permission for the anticheat GUI."});
            return;
         }

         new AnticheatMainMenu(player).open();
         return;
      }

      if (args[0].equalsIgnoreCase("toggle")) {
         if (!sender.hasPermission(ADMIN_PERMISSION)) {
            this.sendMessage(sender, new String[]{"&cYou do not have permission for this."});
            return;
         }

         boolean newState = !(Boolean)AnticheatConfig.enabled.getValue();
         AnticheatConfig.enabled.set(newState, true);
         AnticheatConfigHolder.ENABLED = newState;
         this.sendMessage(sender, new String[]{"&6Blaze AC &e anticheat is now "
            + (newState ? "&aenabled" : "&cdisabled")});
         return;
      }

      if (args[0].equalsIgnoreCase("cps")) {
         if (args.length < 2) {
            this.sendMessage(sender, new String[]{"&6Blaze AC &e Usage: /blazeac cps <username>"});
            return;
         }

         ACPlayer data = this.findPlayer(args[1]);
         if (data == null) {
            this.sendMessage(sender, new String[]{"&cPlayer not tracked: " + args[1]});
            return;
         }

         int cps = data.getClicksThisSecond();
         java.util.Deque<Long> intervals = data.getClickIntervals();
         double avg = 0.0;
         if (!intervals.isEmpty()) {
            long sum = 0L;
            for (long interval : intervals) {
               sum += interval;
            }

            avg = intervals.size() > 0 ? 1.0E9 / (sum / intervals.size()) : 0.0;
         }

         String message = CC.t("&6Blaze AC &e " + data.player.getName() + " &7cps: &f" + cps
            + " &7avg: &f" + String.format("%.1f", avg));
         String json = "{\"text\":\"" + message.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
         EntityPlayer target = data.player;
         target.playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(json), (byte) 2));
         this.sendMessage(sender, new String[]{"&6Blaze AC &e CPS of &f" + data.player.getName() + " &7sent to their action bar"});
         return;
      }

      this.sendMessage(sender, new String[]{"&6Blaze AC &e Usage: /blazeac <alerts|stats [player]|flush|punishments [player]|unban <id|player>|gui|toggle|cps <username>>"});
   }

   private String formatRecord(PunishmentManager.Record record) {
      String expires = record.expires > 0
         ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date(record.expires))
         : "permanent";
      return "&7#" + record.id + " &f" + record.name + " &7" + record.type
         + " &7check=&c" + record.check + " &7expires=" + expires
         + (record.active ? " &aactive" : " &8lifted");
   }

   private ACPlayer findPlayer(String name) {
      Map<UUID, ACPlayer> unused = null;
      for (Player online : Bukkit.getOnlinePlayers()) {
         if (online.getName().equalsIgnoreCase(name)) {
            return ACManager.INSTANCE.get(online.getUniqueId());
         }
      }

      return null;
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         String partial = args[args.length - 1];
         return Stream.of("alerts", "stats", "flush", "punishments", "unban", "gui", "toggle", "cps")
            .filter(s -> s.regionMatches(true, 0, partial, 0, partial.length())).collect(Collectors.toList());
      }

      if (args.length == 2 && (args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("unban")
         || args[0].equalsIgnoreCase("punishments"))) {
         String partial = args[1];
         return Bukkit.getOnlinePlayers().stream().map(Player::getName)
            .filter(n -> n.regionMatches(true, 0, partial, 0, partial.length())).collect(Collectors.toList());
      }

      return super.tabComplete(sender, args);
   }
}
