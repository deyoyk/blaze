package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.regex.Matcher;

public class CommandPardonIP extends CommandAbstract {
   @Override
   public String getCommand() {
      return "pardon-ip";
   }

   @Override
   public int a() {
      return 3;
   }

   @Override
   public boolean canUse(ICommandListener var1) {
      return MinecraftServer.getServer().getPlayerList().getIPBans().isEnabled() && super.canUse(var1);
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.unbanip.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length == 1 && var2[0].length() > 1) {
         Matcher var3 = CommandBanIp.a.matcher(var2[0]);
         if (var3.matches()) {
            MinecraftServer.getServer().getPlayerList().getIPBans().remove(var2[0]);
            a(var1, this, "commands.unbanip.success", new Object[]{var2[0]});
         } else {
            throw new ExceptionInvalidSyntax("commands.unbanip.invalid");
         }
      } else {
         throw new ExceptionUsage("commands.unbanip.usage");
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length == 1 ? a(var2, MinecraftServer.getServer().getPlayerList().getIPBans().getEntries()) : null;
   }
}
