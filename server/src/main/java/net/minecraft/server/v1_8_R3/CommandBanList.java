package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandBanList extends CommandAbstract {
   @Override
   public String getCommand() {
      return "banlist";
   }

   @Override
   public int a() {
      return 3;
   }

   @Override
   public boolean canUse(ICommandListener var1) {
      return (MinecraftServer.getServer().getPlayerList().getIPBans().isEnabled() || MinecraftServer.getServer().getPlayerList().getProfileBans().isEnabled())
         && super.canUse(var1);
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.banlist.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length >= 1 && var2[0].equalsIgnoreCase("ips")) {
         var1.sendMessage(new ChatMessage("commands.banlist.ips", MinecraftServer.getServer().getPlayerList().getIPBans().getEntries().length));
         var1.sendMessage(new ChatComponentText(a(MinecraftServer.getServer().getPlayerList().getIPBans().getEntries())));
      } else {
         var1.sendMessage(new ChatMessage("commands.banlist.players", MinecraftServer.getServer().getPlayerList().getProfileBans().getEntries().length));
         var1.sendMessage(new ChatComponentText(a(MinecraftServer.getServer().getPlayerList().getProfileBans().getEntries())));
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length == 1 ? a(var2, new String[]{"players", "ips"}) : null;
   }
}
