package lol.deyo.blaze.command;

import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.framework.commands.Command;
import java.util.Arrays;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.command.CommandSender;

public class SetMaxPlayersCommand extends Command {
   public SetMaxPlayersCommand() {
      super("setmaxplayers");
      this.setPermission("blaze.setmaxplayers");
      this.setAliases(Arrays.asList("setslots", "setmaxonline"));
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      if (args.length == 0) {
         this.sendMessage(sender, new String[]{LanguageConfig.setMaxPlayersUsage.getValue()});
      } else {
         Integer amount = this.getInt(args[0]);
         if (amount == null) {
            this.sendMessage(
               sender, new String[]{LanguageConfig.notValidType.getValue().replace("%value%", args[0]).replace("%type%", Integer.class.getSimpleName())}
            );
         } else {
            MinecraftServer.getServer().getPlayerList().maxPlayers = amount;
            this.sendMessage(sender, new String[]{LanguageConfig.setMaxPlayers.getValue().replace("%amount%", amount.toString())});
         }
      }
   }
}
