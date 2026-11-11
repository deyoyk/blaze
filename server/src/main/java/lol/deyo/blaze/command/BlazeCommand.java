package lol.deyo.blaze.command;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.framework.commands.Command;
import lol.deyo.blaze.menu.MainBlazeMenu;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlazeCommand extends Command {
   public BlazeCommand() {
      super("blaze");
      this.setPermission("blaze.config");
      this.setAliases(Arrays.asList("spigotconfig", "config"));
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
         long now = System.currentTimeMillis();
         Blaze.getInstance().reload();
         long after = System.currentTimeMillis() - now;
         this.sendMessage(sender, new String[]{LanguageConfig.configReloaded.getValue().replace("%ms%", String.valueOf(after))});
      } else if (!(sender instanceof Player)) {
         this.sendMessage(sender, new String[]{LanguageConfig.onlyPlayer.getValue()});
      } else {
         Player player = (Player)sender;
         new MainBlazeMenu(player).open();
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         String string = args[args.length - 1];
         return Stream.of("reload").filter(s -> s.regionMatches(true, 0, string, 0, string.length())).collect(Collectors.toList());
      } else {
         return super.tabComplete(sender, args);
      }
   }
}
