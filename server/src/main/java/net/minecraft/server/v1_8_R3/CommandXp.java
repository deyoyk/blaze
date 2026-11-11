package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandXp extends CommandAbstract {
   @Override
   public String getCommand() {
      return "xp";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.xp.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new ExceptionUsage("commands.xp.usage");
      } else {
         String var3 = var2[0];
         boolean var4 = var3.endsWith("l") || var3.endsWith("L");
         if (var4 && var3.length() > 1) {
            var3 = var3.substring(0, var3.length() - 1);
         }

         int var5 = a(var3);
         boolean var6 = var5 < 0;
         if (var6) {
            var5 *= -1;
         }

         EntityPlayer var7 = var2.length > 1 ? a(var1, var2[1]) : b(var1);
         if (var4) {
            var1.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, var7.expLevel);
            if (var6) {
               var7.levelDown(-var5);
               a(var1, this, "commands.xp.success.negative.levels", new Object[]{var5, var7.getName()});
            } else {
               var7.levelDown(var5);
               a(var1, this, "commands.xp.success.levels", new Object[]{var5, var7.getName()});
            }
         } else {
            var1.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, var7.expTotal);
            if (var6) {
               throw new CommandException("commands.xp.failure.widthdrawXp");
            }

            var7.giveExp(var5);
            a(var1, this, "commands.xp.success", new Object[]{var5, var7.getName()});
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length == 2 ? a(var2, this.d()) : null;
   }

   protected String[] d() {
      return MinecraftServer.getServer().getPlayers();
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 1;
   }
}
