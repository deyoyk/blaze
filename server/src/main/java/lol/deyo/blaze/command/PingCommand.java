package lol.deyo.blaze.command;

import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.framework.commands.Command;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand extends Command {
   public PingCommand() {
      super("ping");
      this.setAliases(Arrays.asList("latency", "myping"));
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      if (args.length == 0) {
         if (!(sender instanceof Player)) {
            this.sendMessage(sender, new String[]{LanguageConfig.onlyPlayer.getValue()});
         } else {
            Player player = (Player)sender;
            this.sendMessage(
               sender, new String[]{LanguageConfig.playerPing.getValue().replace("%ping%", String.valueOf(((CraftPlayer)player).getHandle().getPing()))}
            );
         }
      } else {
         Player target = Bukkit.getPlayer(args[0]);
         if (target == null) {
            this.sendMessage(sender, new String[]{LanguageConfig.playerNotFound.getValue().replace("%player%", args[0])});
         } else {
            this.sendMessage(
               sender,
               new String[]{
                  LanguageConfig.otherPlayerPing
                     .getValue()
                     .replace("%player%", target.getName())
                     .replace("%ping%", String.valueOf(((CraftPlayer)target).getHandle().getPing()))
               }
            );
         }
      }
   }
}
