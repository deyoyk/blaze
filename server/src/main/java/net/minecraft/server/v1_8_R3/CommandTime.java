package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandTime extends CommandAbstract {
   @Override
   public String getCommand() {
      return "time";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.time.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length > 1) {
         if (var2[0].equals("set")) {
            int var6;
            if (var2[1].equals("day")) {
               var6 = 1000;
            } else if (var2[1].equals("night")) {
               var6 = 13000;
            } else {
               var6 = a(var2[1], 0);
            }

            this.a(var1, var6);
            a(var1, this, "commands.time.set", new Object[]{var6});
            return;
         }

         if (var2[0].equals("add")) {
            int var5 = a(var2[1], 0);
            this.b(var1, var5);
            a(var1, this, "commands.time.added", new Object[]{var5});
            return;
         }

         if (var2[0].equals("query")) {
            if (var2[1].equals("daytime")) {
               int var4 = (int)(var1.getWorld().getDayTime() % 2147483647L);
               var1.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, var4);
               a(var1, this, "commands.time.query", new Object[]{var4});
               return;
            }

            if (var2[1].equals("gametime")) {
               int var3 = (int)(var1.getWorld().getTime() % 2147483647L);
               var1.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, var3);
               a(var1, this, "commands.time.query", new Object[]{var3});
               return;
            }
         }
      }

      throw new ExceptionUsage("commands.time.usage");
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         return a(var2, new String[]{"set", "add", "query"});
      } else if (var2.length == 2 && var2[0].equals("set")) {
         return a(var2, new String[]{"day", "night"});
      } else {
         return var2.length == 2 && var2[0].equals("query") ? a(var2, new String[]{"daytime", "gametime"}) : null;
      }
   }

   protected void a(ICommandListener var1, int var2) {
      for (int var3 = 0; var3 < MinecraftServer.getServer().worldServer.length; var3++) {
         MinecraftServer.getServer().worldServer[var3].setDayTime(var2);
      }
   }

   protected void b(ICommandListener var1, int var2) {
      for (int var3 = 0; var3 < MinecraftServer.getServer().worldServer.length; var3++) {
         WorldServer var4 = MinecraftServer.getServer().worldServer[var3];
         var4.setDayTime(var4.getDayTime() + var2);
      }
   }
}
