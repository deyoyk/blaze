package lol.deyo.blaze.command;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.framework.commands.Command;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlazeACCommand extends Command {
   public BlazeACCommand() {
      super("blazeac");
      this.setDescription("Blaze anticheat alerts and stats");
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
                  this.sendMessage(sender, new String[]{"&7- &c" + data.violations.nameAt(i) + "&7 vl=" + String.format("%.1f", vl)});
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

      this.sendMessage(sender, new String[]{"&6Blaze AC &e Usage: /blazeac <alerts|stats [player]|flush>"});
   }

   private ACPlayer findPlayer(String name) {
      Map<UUID, ACPlayer> unused = null;
      for (Player online : org.bukkit.Bukkit.getOnlinePlayers()) {
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
         return Stream.of("alerts", "stats", "flush").filter(s -> s.regionMatches(true, 0, partial, 0, partial.length())).collect(Collectors.toList());
      }

      if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
         String partial = args[1];
         return org.bukkit.Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(n -> n.regionMatches(true, 0, partial, 0, partial.length())).collect(Collectors.toList());
      }

      return super.tabComplete(sender, args);
   }
}
