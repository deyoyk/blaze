package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandStats extends CommandAbstract {
   @Override
   public String getCommand() {
      return "stats";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.stats.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new ExceptionUsage("commands.stats.usage");
      } else {
         boolean var3;
         if (var2[0].equals("entity")) {
            var3 = false;
         } else {
            if (!var2[0].equals("block")) {
               throw new ExceptionUsage("commands.stats.usage");
            }

            var3 = true;
         }

         int var4;
         if (var3) {
            if (var2.length < 5) {
               throw new ExceptionUsage("commands.stats.block.usage");
            }

            var4 = 4;
         } else {
            if (var2.length < 3) {
               throw new ExceptionUsage("commands.stats.entity.usage");
            }

            var4 = 2;
         }

         String var5 = var2[var4++];
         if ("set".equals(var5)) {
            if (var2.length < var4 + 3) {
               if (var4 == 5) {
                  throw new ExceptionUsage("commands.stats.block.set.usage");
               }

               throw new ExceptionUsage("commands.stats.entity.set.usage");
            }
         } else {
            if (!"clear".equals(var5)) {
               throw new ExceptionUsage("commands.stats.usage");
            }

            if (var2.length < var4 + 1) {
               if (var4 == 5) {
                  throw new ExceptionUsage("commands.stats.block.clear.usage");
               }

               throw new ExceptionUsage("commands.stats.entity.clear.usage");
            }
         }

         CommandObjectiveExecutor.EnumCommandResult var6 = CommandObjectiveExecutor.EnumCommandResult.a(var2[var4++]);
         if (var6 == null) {
            throw new CommandException("commands.stats.failed");
         } else {
            World var7 = var1.getWorld();
            CommandObjectiveExecutor var8;
            if (var3) {
               BlockPosition var9 = a(var1, var2, 1, false);
               TileEntity var10 = var7.getTileEntity(var9);
               if (var10 == null) {
                  throw new CommandException("commands.stats.noCompatibleBlock", var9.getX(), var9.getY(), var9.getZ());
               }

               if (var10 instanceof TileEntityCommand) {
                  var8 = ((TileEntityCommand)var10).c();
               } else {
                  if (!(var10 instanceof TileEntitySign)) {
                     throw new CommandException("commands.stats.noCompatibleBlock", var9.getX(), var9.getY(), var9.getZ());
                  }

                  var8 = ((TileEntitySign)var10).d();
               }
            } else {
               Entity var14 = b(var1, var2[1]);
               var8 = var14.aU();
            }

            if ("set".equals(var5)) {
               String var15 = var2[var4++];
               String var17 = var2[var4];
               if (var15.length() == 0 || var17.length() == 0) {
                  throw new CommandException("commands.stats.failed");
               }

               CommandObjectiveExecutor.a(var8, var6, var15, var17);
               a(var1, this, "commands.stats.success", new Object[]{var6.b(), var17, var15});
            } else if ("clear".equals(var5)) {
               CommandObjectiveExecutor.a(var8, var6, null, null);
               a(var1, this, "commands.stats.cleared", new Object[]{var6.b()});
            }

            if (var3) {
               BlockPosition var16 = a(var1, var2, 1, false);
               TileEntity var18 = var7.getTileEntity(var16);
               var18.update();
            }
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         return a(var2, new String[]{"entity", "block"});
      } else if (var2.length == 2 && var2[0].equals("entity")) {
         return a(var2, this.d());
      } else if (var2.length >= 2 && var2.length <= 4 && var2[0].equals("block")) {
         return a(var2, 1, var3);
      } else if ((var2.length != 3 || !var2[0].equals("entity")) && (var2.length != 5 || !var2[0].equals("block"))) {
         if ((var2.length != 4 || !var2[0].equals("entity")) && (var2.length != 6 || !var2[0].equals("block"))) {
            return (var2.length != 6 || !var2[0].equals("entity")) && (var2.length != 8 || !var2[0].equals("block")) ? null : a(var2, this.e());
         } else {
            return a(var2, CommandObjectiveExecutor.EnumCommandResult.c());
         }
      } else {
         return a(var2, new String[]{"set", "clear"});
      }
   }

   protected String[] d() {
      return MinecraftServer.getServer().getPlayers();
   }

   protected List<String> e() {
      Collection<ScoreboardObjective> var1 = MinecraftServer.getServer().getWorldServer(0).getScoreboard().getObjectives();
      ArrayList var2 = Lists.newArrayList();

      for (ScoreboardObjective var4 : var1) {
         if (!var4.getCriteria().isReadOnly()) {
            var2.add(var4.getName());
         }
      }

      return var2;
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var1.length > 0 && var1[0].equals("entity") && var2 == 1;
   }
}
