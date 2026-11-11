package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;

public class CommandOp extends CommandAbstract {
   @Override
   public String getCommand() {
      return "op";
   }

   @Override
   public int a() {
      return 3;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.op.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.getServer();
         GameProfile var4 = var3.getUserCache().getProfile(var2[0]);
         if (var4 == null) {
            throw new CommandException("commands.op.failed", var2[0]);
         } else {
            var3.getPlayerList().addOp(var4);
            a(var1, this, "commands.op.success", new Object[]{var2[0]});
         }
      } else {
         throw new ExceptionUsage("commands.op.usage");
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         String var4 = var2[var2.length - 1];
         ArrayList var5 = Lists.newArrayList();

         for (GameProfile var9 : MinecraftServer.getServer().L()) {
            if (!MinecraftServer.getServer().getPlayerList().isOp(var9) && a(var4, var9.getName())) {
               var5.add(var9.getName());
            }
         }

         return var5;
      } else {
         return null;
      }
   }
}
