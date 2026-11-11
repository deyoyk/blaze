package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandKill extends CommandAbstract {
   @Override
   public String getCommand() {
      return "kill";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.kill.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length == 0) {
         EntityPlayer var4 = b(var1);
         var4.G();
         a(var1, this, "commands.kill.successful", new Object[]{var4.getScoreboardDisplayName()});
      } else {
         Entity var3 = b(var1, var2[0]);
         var3.G();
         a(var1, this, "commands.kill.successful", new Object[]{var3.getScoreboardDisplayName()});
      }
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 0;
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length == 1 ? a(var2, MinecraftServer.getServer().getPlayers()) : null;
   }
}
