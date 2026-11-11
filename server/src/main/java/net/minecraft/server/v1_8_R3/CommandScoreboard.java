package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandScoreboard extends CommandAbstract {
   @Override
   public String getCommand() {
      return "scoreboard";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.scoreboard.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (!this.b(var1, var2)) {
         if (var2.length < 1) {
            throw new ExceptionUsage("commands.scoreboard.usage");
         } else {
            if (var2[0].equalsIgnoreCase("objectives")) {
               if (var2.length == 1) {
                  throw new ExceptionUsage("commands.scoreboard.objectives.usage");
               }

               if (var2[1].equalsIgnoreCase("list")) {
                  this.d(var1);
               } else if (var2[1].equalsIgnoreCase("add")) {
                  if (var2.length < 4) {
                     throw new ExceptionUsage("commands.scoreboard.objectives.add.usage");
                  }

                  this.b(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("remove")) {
                  if (var2.length != 3) {
                     throw new ExceptionUsage("commands.scoreboard.objectives.remove.usage");
                  }

                  this.h(var1, var2[2]);
               } else {
                  if (!var2[1].equalsIgnoreCase("setdisplay")) {
                     throw new ExceptionUsage("commands.scoreboard.objectives.usage");
                  }

                  if (var2.length != 3 && var2.length != 4) {
                     throw new ExceptionUsage("commands.scoreboard.objectives.setdisplay.usage");
                  }

                  this.j(var1, var2, 2);
               }
            } else if (var2[0].equalsIgnoreCase("players")) {
               if (var2.length == 1) {
                  throw new ExceptionUsage("commands.scoreboard.players.usage");
               }

               if (var2[1].equalsIgnoreCase("list")) {
                  if (var2.length > 3) {
                     throw new ExceptionUsage("commands.scoreboard.players.list.usage");
                  }

                  this.k(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("add")) {
                  if (var2.length < 5) {
                     throw new ExceptionUsage("commands.scoreboard.players.add.usage");
                  }

                  this.l(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("remove")) {
                  if (var2.length < 5) {
                     throw new ExceptionUsage("commands.scoreboard.players.remove.usage");
                  }

                  this.l(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("set")) {
                  if (var2.length < 5) {
                     throw new ExceptionUsage("commands.scoreboard.players.set.usage");
                  }

                  this.l(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("reset")) {
                  if (var2.length != 3 && var2.length != 4) {
                     throw new ExceptionUsage("commands.scoreboard.players.reset.usage");
                  }

                  this.m(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("enable")) {
                  if (var2.length != 4) {
                     throw new ExceptionUsage("commands.scoreboard.players.enable.usage");
                  }

                  this.n(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("test")) {
                  if (var2.length != 5 && var2.length != 6) {
                     throw new ExceptionUsage("commands.scoreboard.players.test.usage");
                  }

                  this.o(var1, var2, 2);
               } else {
                  if (!var2[1].equalsIgnoreCase("operation")) {
                     throw new ExceptionUsage("commands.scoreboard.players.usage");
                  }

                  if (var2.length != 7) {
                     throw new ExceptionUsage("commands.scoreboard.players.operation.usage");
                  }

                  this.p(var1, var2, 2);
               }
            } else {
               if (!var2[0].equalsIgnoreCase("teams")) {
                  throw new ExceptionUsage("commands.scoreboard.usage");
               }

               if (var2.length == 1) {
                  throw new ExceptionUsage("commands.scoreboard.teams.usage");
               }

               if (var2[1].equalsIgnoreCase("list")) {
                  if (var2.length > 3) {
                     throw new ExceptionUsage("commands.scoreboard.teams.list.usage");
                  }

                  this.f(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("add")) {
                  if (var2.length < 3) {
                     throw new ExceptionUsage("commands.scoreboard.teams.add.usage");
                  }

                  this.c(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("remove")) {
                  if (var2.length != 3) {
                     throw new ExceptionUsage("commands.scoreboard.teams.remove.usage");
                  }

                  this.e(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("empty")) {
                  if (var2.length != 3) {
                     throw new ExceptionUsage("commands.scoreboard.teams.empty.usage");
                  }

                  this.i(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("join")) {
                  if (var2.length < 4 && (var2.length != 3 || !(var1 instanceof EntityHuman))) {
                     throw new ExceptionUsage("commands.scoreboard.teams.join.usage");
                  }

                  this.g(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("leave")) {
                  if (var2.length < 3 && !(var1 instanceof EntityHuman)) {
                     throw new ExceptionUsage("commands.scoreboard.teams.leave.usage");
                  }

                  this.h(var1, var2, 2);
               } else {
                  if (!var2[1].equalsIgnoreCase("option")) {
                     throw new ExceptionUsage("commands.scoreboard.teams.usage");
                  }

                  if (var2.length != 4 && var2.length != 5) {
                     throw new ExceptionUsage("commands.scoreboard.teams.option.usage");
                  }

                  this.d(var1, var2, 2);
               }
            }
         }
      }
   }

   private boolean b(ICommandListener var1, String[] var2) throws CommandException {
      int var3 = -1;

      for (int var4 = 0; var4 < var2.length; var4++) {
         if (this.isListStart(var2, var4) && "*".equals(var2[var4])) {
            if (var3 >= 0) {
               throw new CommandException("commands.scoreboard.noMultiWildcard");
            }

            var3 = var4;
         }
      }

      if (var3 < 0) {
         return false;
      } else {
         ArrayList var12 = Lists.newArrayList(this.d().getPlayers());
         String var5 = var2[var3];
         ArrayList var6 = Lists.newArrayList();

         for (String var8 : (List<String>)var12) {
            var2[var3] = var8;

            try {
               this.execute(var1, var2);
               var6.add(var8);
            } catch (CommandException var11) {
               ChatMessage var10 = new ChatMessage(var11.getMessage(), var11.getArgs());
               var10.getChatModifier().setColor(EnumChatFormat.RED);
               var1.sendMessage(var10);
            }
         }

         var2[var3] = var5;
         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, var6.size());
         if (var6.size() == 0) {
            throw new ExceptionUsage("commands.scoreboard.allMatchesFailed");
         } else {
            return true;
         }
      }
   }

   protected Scoreboard d() {
      return MinecraftServer.getServer().getWorldServer(0).getScoreboard();
   }

   protected ScoreboardObjective a(String var1, boolean var2) throws CommandException {
      Scoreboard var3 = this.d();
      ScoreboardObjective var4 = var3.getObjective(var1);
      if (var4 == null) {
         throw new CommandException("commands.scoreboard.objectiveNotFound", var1);
      } else if (var2 && var4.getCriteria().isReadOnly()) {
         throw new CommandException("commands.scoreboard.objectiveReadOnly", var1);
      } else {
         return var4;
      }
   }

   protected ScoreboardTeam e(String var1) throws CommandException {
      Scoreboard var2 = this.d();
      ScoreboardTeam var3 = var2.getTeam(var1);
      if (var3 == null) {
         throw new CommandException("commands.scoreboard.teamNotFound", var1);
      } else {
         return var3;
      }
   }

   protected void b(ICommandListener var1, String[] var2, int var3) throws CommandException {
      String var4 = var2[var3++];
      String var5 = var2[var3++];
      Scoreboard var6 = this.d();
      IScoreboardCriteria var7 = IScoreboardCriteria.criteria.get(var5);
      if (var7 == null) {
         throw new ExceptionUsage("commands.scoreboard.objectives.add.wrongType", var5);
      } else if (var6.getObjective(var4) != null) {
         throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", var4);
      } else if (var4.length() > 16) {
         throw new ExceptionInvalidSyntax("commands.scoreboard.objectives.add.tooLong", var4, 16);
      } else if (var4.length() == 0) {
         throw new ExceptionUsage("commands.scoreboard.objectives.add.usage");
      } else {
         if (var2.length > var3) {
            String var8 = a(var1, var2, var3).c();
            if (var8.length() > 32) {
               throw new ExceptionInvalidSyntax("commands.scoreboard.objectives.add.displayTooLong", var8, 32);
            }

            if (var8.length() > 0) {
               var6.registerObjective(var4, var7).setDisplayName(var8);
            } else {
               var6.registerObjective(var4, var7);
            }
         } else {
            var6.registerObjective(var4, var7);
         }

         a(var1, this, "commands.scoreboard.objectives.add.success", new Object[]{var4});
      }
   }

   protected void c(ICommandListener var1, String[] var2, int var3) throws CommandException {
      String var4 = var2[var3++];
      Scoreboard var5 = this.d();
      if (var5.getTeam(var4) != null) {
         throw new CommandException("commands.scoreboard.teams.add.alreadyExists", var4);
      } else if (var4.length() > 16) {
         throw new ExceptionInvalidSyntax("commands.scoreboard.teams.add.tooLong", var4, 16);
      } else if (var4.length() == 0) {
         throw new ExceptionUsage("commands.scoreboard.teams.add.usage");
      } else {
         if (var2.length > var3) {
            String var6 = a(var1, var2, var3).c();
            if (var6.length() > 32) {
               throw new ExceptionInvalidSyntax("commands.scoreboard.teams.add.displayTooLong", var6, 32);
            }

            if (var6.length() > 0) {
               var5.createTeam(var4).setDisplayName(var6);
            } else {
               var5.createTeam(var4);
            }
         } else {
            var5.createTeam(var4);
         }

         a(var1, this, "commands.scoreboard.teams.add.success", new Object[]{var4});
      }
   }

   protected void d(ICommandListener var1, String[] var2, int var3) throws CommandException {
      ScoreboardTeam var4 = this.e(var2[var3++]);
      if (var4 != null) {
         String var5 = var2[var3++].toLowerCase();
         if (!var5.equalsIgnoreCase("color")
            && !var5.equalsIgnoreCase("friendlyfire")
            && !var5.equalsIgnoreCase("seeFriendlyInvisibles")
            && !var5.equalsIgnoreCase("nametagVisibility")
            && !var5.equalsIgnoreCase("deathMessageVisibility")) {
            throw new ExceptionUsage("commands.scoreboard.teams.option.usage");
         } else if (var2.length == 4) {
            if (var5.equalsIgnoreCase("color")) {
               throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", var5, a(EnumChatFormat.a(true, false)));
            } else if (var5.equalsIgnoreCase("friendlyfire") || var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
               throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", var5, a(Arrays.asList("true", "false")));
            } else if (!var5.equalsIgnoreCase("nametagVisibility") && !var5.equalsIgnoreCase("deathMessageVisibility")) {
               throw new ExceptionUsage("commands.scoreboard.teams.option.usage");
            } else {
               throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", var5, a(ScoreboardTeamBase.EnumNameTagVisibility.a()));
            }
         } else {
            String var6 = var2[var3];
            if (var5.equalsIgnoreCase("color")) {
               EnumChatFormat var7 = EnumChatFormat.b(var6);
               if (var7 == null || var7.isFormat()) {
                  throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", var5, a(EnumChatFormat.a(true, false)));
               }

               var4.a(var7);
               var4.setPrefix(var7.toString());
               var4.setSuffix(EnumChatFormat.RESET.toString());
            } else if (var5.equalsIgnoreCase("friendlyfire")) {
               if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
                  throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", var5, a(Arrays.asList("true", "false")));
               }

               var4.setAllowFriendlyFire(var6.equalsIgnoreCase("true"));
            } else if (var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
               if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
                  throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", var5, a(Arrays.asList("true", "false")));
               }

               var4.setCanSeeFriendlyInvisibles(var6.equalsIgnoreCase("true"));
            } else if (var5.equalsIgnoreCase("nametagVisibility")) {
               ScoreboardTeamBase.EnumNameTagVisibility var10 = ScoreboardTeamBase.EnumNameTagVisibility.a(var6);
               if (var10 == null) {
                  throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", var5, a(ScoreboardTeamBase.EnumNameTagVisibility.a()));
               }

               var4.setNameTagVisibility(var10);
            } else if (var5.equalsIgnoreCase("deathMessageVisibility")) {
               ScoreboardTeamBase.EnumNameTagVisibility var11 = ScoreboardTeamBase.EnumNameTagVisibility.a(var6);
               if (var11 == null) {
                  throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", var5, a(ScoreboardTeamBase.EnumNameTagVisibility.a()));
               }

               var4.b(var11);
            }

            a(var1, this, "commands.scoreboard.teams.option.success", new Object[]{var5, var4.getName(), var6});
         }
      }
   }

   protected void e(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      ScoreboardTeam var5 = this.e(var2[var3]);
      if (var5 != null) {
         var4.removeTeam(var5);
         a(var1, this, "commands.scoreboard.teams.remove.success", new Object[]{var5.getName()});
      }
   }

   protected void f(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      if (var2.length > var3) {
         ScoreboardTeam var5 = this.e(var2[var3]);
         if (var5 == null) {
            return;
         }

         Collection var6 = var5.getPlayerNameSet();
         var1.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, var6.size());
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.player.empty", var5.getName());
         }

         ChatMessage var7 = new ChatMessage("commands.scoreboard.teams.list.player.count", var6.size(), var5.getName());
         var7.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
         var1.sendMessage(var7);
         var1.sendMessage(new ChatComponentText(a(var6.toArray())));
      } else {
         Collection var9 = var4.getTeams();
         var1.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, var9.size());
         if (var9.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.empty");
         }

         ChatMessage var10 = new ChatMessage("commands.scoreboard.teams.list.count", var9.size());
         var10.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
         var1.sendMessage(var10);

         for (ScoreboardTeam var8 : (List<ScoreboardTeam>)var9) {
            var1.sendMessage(new ChatMessage("commands.scoreboard.teams.list.entry", var8.getName(), var8.getDisplayName(), var8.getPlayerNameSet().size()));
         }
      }
   }

   protected void g(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      String var5 = var2[var3++];
      HashSet var6 = Sets.newHashSet();
      HashSet var7 = Sets.newHashSet();
      if (var1 instanceof EntityHuman && var3 == var2.length) {
         String var14 = b(var1).getName();
         if (var4.addPlayerToTeam(var14, var5)) {
            var6.add(var14);
         } else {
            var7.add(var14);
         }
      } else {
         while (var3 < var2.length) {
            String var8 = var2[var3++];
            if (var8.startsWith("@")) {
               for (Entity var11 : c(var1, var8)) {
                  String var12 = e(var1, var11.getUniqueID().toString());
                  if (var4.addPlayerToTeam(var12, var5)) {
                     var6.add(var12);
                  } else {
                     var7.add(var12);
                  }
               }
            } else {
               String var9 = e(var1, var8);
               if (var4.addPlayerToTeam(var9, var5)) {
                  var6.add(var9);
               } else {
                  var7.add(var9);
               }
            }
         }
      }

      if (!var6.isEmpty()) {
         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, var6.size());
         a(var1, this, "commands.scoreboard.teams.join.success", new Object[]{var6.size(), var5, a(var6.toArray(new String[var6.size()]))});
      }

      if (!var7.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.join.failure", var7.size(), var5, a(var7.toArray(new String[var7.size()])));
      }
   }

   protected void h(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      HashSet var5 = Sets.newHashSet();
      HashSet var6 = Sets.newHashSet();
      if (var1 instanceof EntityHuman && var3 == var2.length) {
         String var12 = b(var1).getName();
         if (var4.removePlayerFromTeam(var12)) {
            var5.add(var12);
         } else {
            var6.add(var12);
         }
      } else {
         while (var3 < var2.length) {
            String var7 = var2[var3++];
            if (var7.startsWith("@")) {
               for (Entity var10 : c(var1, var7)) {
                  String var11 = e(var1, var10.getUniqueID().toString());
                  if (var4.removePlayerFromTeam(var11)) {
                     var5.add(var11);
                  } else {
                     var6.add(var11);
                  }
               }
            } else {
               String var8 = e(var1, var7);
               if (var4.removePlayerFromTeam(var8)) {
                  var5.add(var8);
               } else {
                  var6.add(var8);
               }
            }
         }
      }

      if (!var5.isEmpty()) {
         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, var5.size());
         a(var1, this, "commands.scoreboard.teams.leave.success", new Object[]{var5.size(), a(var5.toArray(new String[var5.size()]))});
      }

      if (!var6.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.leave.failure", var6.size(), a(var6.toArray(new String[var6.size()])));
      }
   }

   protected void i(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      ScoreboardTeam var5 = this.e(var2[var3]);
      if (var5 != null) {
         ArrayList var6 = Lists.newArrayList(var5.getPlayerNameSet());
         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, var6.size());
         if (var6.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", var5.getName());
         } else {
            for (String var8 : (List<String>)var6) {
               var4.removePlayerFromTeam(var8, var5);
            }

            a(var1, this, "commands.scoreboard.teams.empty.success", new Object[]{var6.size(), var5.getName()});
         }
      }
   }

   protected void h(ICommandListener var1, String var2) throws CommandException {
      Scoreboard var3 = this.d();
      ScoreboardObjective var4 = this.a(var2, false);
      var3.unregisterObjective(var4);
      a(var1, this, "commands.scoreboard.objectives.remove.success", new Object[]{var2});
   }

   protected void d(ICommandListener var1) throws CommandException {
      Scoreboard var2 = this.d();
      Collection var3 = var2.getObjectives();
      if (var3.size() <= 0) {
         throw new CommandException("commands.scoreboard.objectives.list.empty");
      } else {
         ChatMessage var4 = new ChatMessage("commands.scoreboard.objectives.list.count", var3.size());
         var4.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
         var1.sendMessage(var4);

         for (ScoreboardObjective var6 : (List<ScoreboardObjective>)var3) {
            var1.sendMessage(new ChatMessage("commands.scoreboard.objectives.list.entry", var6.getName(), var6.getDisplayName(), var6.getCriteria().getName()));
         }
      }
   }

   protected void j(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      String var5 = var2[var3++];
      int var6 = Scoreboard.getSlotForName(var5);
      ScoreboardObjective var7 = null;
      if (var2.length == 4) {
         var7 = this.a(var2[var3], false);
      }

      if (var6 < 0) {
         throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", var5);
      } else {
         var4.setDisplaySlot(var6, var7);
         if (var7 != null) {
            a(var1, this, "commands.scoreboard.objectives.setdisplay.successSet", new Object[]{Scoreboard.getSlotName(var6), var7.getName()});
         } else {
            a(var1, this, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[]{Scoreboard.getSlotName(var6)});
         }
      }
   }

   protected void k(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      if (var2.length > var3) {
         String var5 = e(var1, var2[var3]);
         Map var6 = var4.getPlayerObjectives(var5);
         var1.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, var6.size());
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.player.empty", var5);
         }

         ChatMessage var7 = new ChatMessage("commands.scoreboard.players.list.player.count", var6.size(), var5);
         var7.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
         var1.sendMessage(var7);

         for (ScoreboardScore var9 : ((Map<String, ScoreboardScore>)var6).values()) {
            var1.sendMessage(
               new ChatMessage(
                  "commands.scoreboard.players.list.player.entry", var9.getScore(), var9.getObjective().getDisplayName(), var9.getObjective().getName()
               )
            );
         }
      } else {
         Collection var10 = var4.getPlayers();
         var1.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, var10.size());
         if (var10.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.empty");
         }

         ChatMessage var11 = new ChatMessage("commands.scoreboard.players.list.count", var10.size());
         var11.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
         var1.sendMessage(var11);
         var1.sendMessage(new ChatComponentText(a(var10.toArray())));
      }
   }

   protected void l(ICommandListener var1, String[] var2, int var3) throws CommandException {
      String var4 = var2[var3 - 1];
      int var5 = var3;
      String var6 = e(var1, var2[var3++]);
      if (var6.length() > 40) {
         throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", var6, 40);
      } else {
         ScoreboardObjective var7 = this.a(var2[var3++], true);
         int var8 = var4.equalsIgnoreCase("set") ? a(var2[var3++]) : a(var2[var3++], 0);
         if (var2.length > var3) {
            Entity var9 = b(var1, var2[var5]);

            try {
               NBTTagCompound var10 = MojangsonParser.parse(a(var2, var3));
               NBTTagCompound var11 = new NBTTagCompound();
               var9.e(var11);
               if (!GameProfileSerializer.a(var10, var11, true)) {
                  throw new CommandException("commands.scoreboard.players.set.tagMismatch", var6);
               }
            } catch (MojangsonParseException var12) {
               throw new CommandException("commands.scoreboard.players.set.tagError", var12.getMessage());
            }
         }

         Scoreboard var16 = this.d();
         ScoreboardScore var17 = var16.getPlayerScoreForObjective(var6, var7);
         if (var4.equalsIgnoreCase("set")) {
            var17.setScore(var8);
         } else if (var4.equalsIgnoreCase("add")) {
            var17.addScore(var8);
         } else {
            var17.removeScore(var8);
         }

         a(var1, this, "commands.scoreboard.players.set.success", new Object[]{var7.getName(), var6, var17.getScore()});
      }
   }

   protected void m(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      String var5 = e(var1, var2[var3++]);
      if (var2.length > var3) {
         ScoreboardObjective var6 = this.a(var2[var3++], false);
         var4.resetPlayerScores(var5, var6);
         a(var1, this, "commands.scoreboard.players.resetscore.success", new Object[]{var6.getName(), var5});
      } else {
         var4.resetPlayerScores(var5, null);
         a(var1, this, "commands.scoreboard.players.reset.success", new Object[]{var5});
      }
   }

   protected void n(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      String var5 = d(var1, var2[var3++]);
      if (var5.length() > 40) {
         throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", var5, 40);
      } else {
         ScoreboardObjective var6 = this.a(var2[var3], false);
         if (var6.getCriteria() != IScoreboardCriteria.c) {
            throw new CommandException("commands.scoreboard.players.enable.noTrigger", var6.getName());
         } else {
            ScoreboardScore var7 = var4.getPlayerScoreForObjective(var5, var6);
            var7.a(false);
            a(var1, this, "commands.scoreboard.players.enable.success", new Object[]{var6.getName(), var5});
         }
      }
   }

   protected void o(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      String var5 = e(var1, var2[var3++]);
      if (var5.length() > 40) {
         throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", var5, 40);
      } else {
         ScoreboardObjective var6 = this.a(var2[var3++], false);
         if (!var4.b(var5, var6)) {
            throw new CommandException("commands.scoreboard.players.test.notFound", var6.getName(), var5);
         } else {
            int var7 = var2[var3].equals("*") ? Integer.MIN_VALUE : a(var2[var3]);
            var3++;
            int var8 = var3 < var2.length && !var2[var3].equals("*") ? a(var2[var3], var7) : Integer.MAX_VALUE;
            ScoreboardScore var9 = var4.getPlayerScoreForObjective(var5, var6);
            if (var9.getScore() >= var7 && var9.getScore() <= var8) {
               a(var1, this, "commands.scoreboard.players.test.success", new Object[]{var9.getScore(), var7, var8});
            } else {
               throw new CommandException("commands.scoreboard.players.test.failed", var9.getScore(), var7, var8);
            }
         }
      }
   }

   protected void p(ICommandListener var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.d();
      String var5 = e(var1, var2[var3++]);
      ScoreboardObjective var6 = this.a(var2[var3++], true);
      String var7 = var2[var3++];
      String var8 = e(var1, var2[var3++]);
      ScoreboardObjective var9 = this.a(var2[var3], false);
      if (var5.length() > 40) {
         throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", var5, 40);
      } else if (var8.length() > 40) {
         throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", var8, 40);
      } else {
         ScoreboardScore var10 = var4.getPlayerScoreForObjective(var5, var6);
         if (!var4.b(var8, var9)) {
            throw new CommandException("commands.scoreboard.players.operation.notFound", var9.getName(), var8);
         } else {
            ScoreboardScore var11 = var4.getPlayerScoreForObjective(var8, var9);
            if (var7.equals("+=")) {
               var10.setScore(var10.getScore() + var11.getScore());
            } else if (var7.equals("-=")) {
               var10.setScore(var10.getScore() - var11.getScore());
            } else if (var7.equals("*=")) {
               var10.setScore(var10.getScore() * var11.getScore());
            } else if (var7.equals("/=")) {
               if (var11.getScore() != 0) {
                  var10.setScore(var10.getScore() / var11.getScore());
               }
            } else if (var7.equals("%=")) {
               if (var11.getScore() != 0) {
                  var10.setScore(var10.getScore() % var11.getScore());
               }
            } else if (var7.equals("=")) {
               var10.setScore(var11.getScore());
            } else if (var7.equals("<")) {
               var10.setScore(Math.min(var10.getScore(), var11.getScore()));
            } else if (var7.equals(">")) {
               var10.setScore(Math.max(var10.getScore(), var11.getScore()));
            } else {
               if (!var7.equals("><")) {
                  throw new CommandException("commands.scoreboard.players.operation.invalidOperation", var7);
               }

               int var12 = var10.getScore();
               var10.setScore(var11.getScore());
               var11.setScore(var12);
            }

            a(var1, this, "commands.scoreboard.players.operation.success", new Object[0]);
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         return a(var2, new String[]{"objectives", "players", "teams"});
      } else {
         if (var2[0].equalsIgnoreCase("objectives")) {
            if (var2.length == 2) {
               return a(var2, new String[]{"list", "add", "remove", "setdisplay"});
            }

            if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length == 4) {
                  Set var4 = IScoreboardCriteria.criteria.keySet();
                  return a(var2, var4);
               }
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length == 3) {
                  return a(var2, this.a(false));
               }
            } else if (var2[1].equalsIgnoreCase("setdisplay")) {
               if (var2.length == 3) {
                  return a(var2, Scoreboard.h());
               }

               if (var2.length == 4) {
                  return a(var2, this.a(false));
               }
            }
         } else if (var2[0].equalsIgnoreCase("players")) {
            if (var2.length == 2) {
               return a(var2, new String[]{"set", "add", "remove", "reset", "list", "enable", "test", "operation"});
            }

            if (!var2[1].equalsIgnoreCase("set")
               && !var2[1].equalsIgnoreCase("add")
               && !var2[1].equalsIgnoreCase("remove")
               && !var2[1].equalsIgnoreCase("reset")) {
               if (var2[1].equalsIgnoreCase("enable")) {
                  if (var2.length == 3) {
                     return a(var2, MinecraftServer.getServer().getPlayers());
                  }

                  if (var2.length == 4) {
                     return a(var2, this.e());
                  }
               } else if (!var2[1].equalsIgnoreCase("list") && !var2[1].equalsIgnoreCase("test")) {
                  if (var2[1].equalsIgnoreCase("operation")) {
                     if (var2.length == 3) {
                        return a(var2, this.d().getPlayers());
                     }

                     if (var2.length == 4) {
                        return a(var2, this.a(true));
                     }

                     if (var2.length == 5) {
                        return a(var2, new String[]{"+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><"});
                     }

                     if (var2.length == 6) {
                        return a(var2, MinecraftServer.getServer().getPlayers());
                     }

                     if (var2.length == 7) {
                        return a(var2, this.a(false));
                     }
                  }
               } else {
                  if (var2.length == 3) {
                     return a(var2, this.d().getPlayers());
                  }

                  if (var2.length == 4 && var2[1].equalsIgnoreCase("test")) {
                     return a(var2, this.a(false));
                  }
               }
            } else {
               if (var2.length == 3) {
                  return a(var2, MinecraftServer.getServer().getPlayers());
               }

               if (var2.length == 4) {
                  return a(var2, this.a(true));
               }
            }
         } else if (var2[0].equalsIgnoreCase("teams")) {
            if (var2.length == 2) {
               return a(var2, new String[]{"add", "remove", "join", "leave", "empty", "list", "option"});
            }

            if (var2[1].equalsIgnoreCase("join")) {
               if (var2.length == 3) {
                  return a(var2, this.d().getTeamNames());
               }

               if (var2.length >= 4) {
                  return a(var2, MinecraftServer.getServer().getPlayers());
               }
            } else {
               if (var2[1].equalsIgnoreCase("leave")) {
                  return a(var2, MinecraftServer.getServer().getPlayers());
               }

               if (!var2[1].equalsIgnoreCase("empty") && !var2[1].equalsIgnoreCase("list") && !var2[1].equalsIgnoreCase("remove")) {
                  if (var2[1].equalsIgnoreCase("option")) {
                     if (var2.length == 3) {
                        return a(var2, this.d().getTeamNames());
                     }

                     if (var2.length == 4) {
                        return a(var2, new String[]{"color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"});
                     }

                     if (var2.length == 5) {
                        if (var2[3].equalsIgnoreCase("color")) {
                           return a(var2, EnumChatFormat.a(true, false));
                        }

                        if (var2[3].equalsIgnoreCase("nametagVisibility") || var2[3].equalsIgnoreCase("deathMessageVisibility")) {
                           return a(var2, ScoreboardTeamBase.EnumNameTagVisibility.a());
                        }

                        if (var2[3].equalsIgnoreCase("friendlyfire") || var2[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                           return a(var2, new String[]{"true", "false"});
                        }
                     }
                  }
               } else if (var2.length == 3) {
                  return a(var2, this.d().getTeamNames());
               }
            }
         }

         return null;
      }
   }

   protected List<String> a(boolean var1) {
      Collection var2 = this.d().getObjectives();
      ArrayList var3 = Lists.newArrayList();

      for (ScoreboardObjective var5 : (List<ScoreboardObjective>)var2) {
         if (!var1 || !var5.getCriteria().isReadOnly()) {
            var3.add(var5.getName());
         }
      }

      return var3;
   }

   protected List<String> e() {
      Collection var1 = this.d().getObjectives();
      ArrayList var2 = Lists.newArrayList();

      for (ScoreboardObjective var4 : (List<ScoreboardObjective>)var1) {
         if (var4.getCriteria() == IScoreboardCriteria.c) {
            var2.add(var4.getName());
         }
      }

      return var2;
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      if (!var1[0].equalsIgnoreCase("players")) {
         return var1[0].equalsIgnoreCase("teams") ? var2 == 2 : false;
      } else {
         return var1.length > 1 && var1[1].equalsIgnoreCase("operation") ? var2 == 2 || var2 == 5 : var2 == 2;
      }
   }
}
