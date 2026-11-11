package net.minecraft.server.v1_8_R3;

import com.mojang.authlib.GameProfile;
import java.util.List;

public class CommandDeop extends CommandAbstract {
   @Override
   public String getCommand() {
      return "deop";
   }

   @Override
   public int a() {
      return 3;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.deop.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.getServer();
         GameProfile var4 = var3.getPlayerList().getOPs().a(var2[0]);
         if (var4 == null) {
            throw new CommandException("commands.deop.failed", var2[0]);
         } else {
            var3.getPlayerList().removeOp(var4);
            a(var1, this, "commands.deop.success", new Object[]{var2[0]});
         }
      } else {
         throw new ExceptionUsage("commands.deop.usage");
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length == 1 ? a(var2, MinecraftServer.getServer().getPlayerList().n()) : null;
   }
}
