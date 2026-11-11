package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

public class CommandAchievement extends CommandAbstract {
   @Override
   public String getCommand() {
      return "achievement";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.achievement.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new ExceptionUsage("commands.achievement.usage");
      } else {
         final Statistic var3 = StatisticList.getStatistic(var2[1]);
         if (var3 == null && !var2[1].equals("*")) {
            throw new CommandException("commands.achievement.unknownAchievement", var2[1]);
         } else {
            final EntityPlayer var4 = var2.length >= 3 ? a(var1, var2[2]) : b(var1);
            boolean var5 = var2[0].equalsIgnoreCase("give");
            boolean var6 = var2[0].equalsIgnoreCase("take");
            if (var5 || var6) {
               if (var3 == null) {
                  if (var5) {
                     for (Achievement var17 : AchievementList.e) {
                        var4.b(var17);
                     }

                     a(var1, this, "commands.achievement.give.success.all", new Object[]{var4.getName()});
                  } else if (var6) {
                     for (Achievement var18 : Lists.reverse(AchievementList.e)) {
                        var4.a(var18);
                     }

                     a(var1, this, "commands.achievement.take.success.all", new Object[]{var4.getName()});
                  }
               } else {
                  if (var3 instanceof Achievement) {
                     Achievement var7 = (Achievement)var3;
                     if (var5) {
                        if (var4.getStatisticManager().hasAchievement(var7)) {
                           throw new CommandException("commands.achievement.alreadyHave", var4.getName(), var3.j());
                        }

                        ArrayList<Achievement> var8;
                        for (var8 = Lists.newArrayList(); var7.c != null && !var4.getStatisticManager().hasAchievement(var7.c); var7 = var7.c) {
                           var8.add(var7.c);
                        }

                        for (Achievement var10 : Lists.reverse(var8)) {
                           var4.b(var10);
                        }
                     } else if (var6) {
                        if (!var4.getStatisticManager().hasAchievement(var7)) {
                           throw new CommandException("commands.achievement.dontHave", var4.getName(), var3.j());
                        }

                        ArrayList<Achievement> var16 = Lists.newArrayList(Iterators.filter(AchievementList.e.iterator(), new Predicate<Achievement>() {
                           @Override
                           public boolean apply(Achievement var1) {
                              return var4.getStatisticManager().hasAchievement(var1) && var1 != var3;
                           }
                        }));
                        ArrayList<Achievement> var19 = Lists.newArrayList(var16);

                        for (Achievement var11 : var16) {
                           Achievement var12 = var11;

                           boolean var13;
                           for (var13 = false; var12 != null; var12 = var12.c) {
                              if (var12 == var3) {
                                 var13 = true;
                              }
                           }

                           if (!var13) {
                              for (Achievement var23 = var11; var23 != null; var23 = var23.c) {
                                 var19.remove(var11);
                              }
                           }
                        }

                        for (Achievement var22 : var19) {
                           var4.a(var22);
                        }
                     }
                  }

                  if (var5) {
                     var4.b(var3);
                     a(var1, this, "commands.achievement.give.success.one", new Object[]{var4.getName(), var3.j()});
                  } else if (var6) {
                     var4.a(var3);
                     a(var1, this, "commands.achievement.take.success.one", new Object[]{var3.j(), var4.getName()});
                  }
               }
            }
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         return a(var2, new String[]{"give", "take"});
      } else if (var2.length != 2) {
         return var2.length == 3 ? a(var2, MinecraftServer.getServer().getPlayers()) : null;
      } else {
         ArrayList var4 = Lists.newArrayList();

         for (Statistic var6 : StatisticList.stats) {
            var4.add(var6.name);
         }

         return a(var2, var4);
      }
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 2;
   }
}
