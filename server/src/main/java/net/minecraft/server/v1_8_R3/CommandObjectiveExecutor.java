package net.minecraft.server.v1_8_R3;

public class CommandObjectiveExecutor {
   private static final int a = CommandObjectiveExecutor.EnumCommandResult.values().length;
   private static final String[] b = new String[a];
   private String[] c = b;
   private String[] d = b;

   public void a(final ICommandListener var1, CommandObjectiveExecutor.EnumCommandResult var2, int var3) {
      String var4 = this.c[var2.a()];
      if (var4 != null) {
         ICommandListener var5 = new ICommandListener() {
            @Override
            public String getName() {
               return var1.getName();
            }

            @Override
            public IChatBaseComponent getScoreboardDisplayName() {
               return var1.getScoreboardDisplayName();
            }

            @Override
            public void sendMessage(IChatBaseComponent var1x) {
               var1.sendMessage(var1x);
            }

            @Override
            public boolean a(int var1x, String var2x) {
               return true;
            }

            @Override
            public BlockPosition getChunkCoordinates() {
               return var1.getChunkCoordinates();
            }

            @Override
            public Vec3D d() {
               return var1.d();
            }

            @Override
            public World getWorld() {
               return var1.getWorld();
            }

            @Override
            public Entity f() {
               return var1.f();
            }

            @Override
            public boolean getSendCommandFeedback() {
               return var1.getSendCommandFeedback();
            }

            @Override
            public void a(CommandObjectiveExecutor.EnumCommandResult var1x, int var2x) {
               var1.a(var1x, var2x);
            }
         };

         String var6;
         try {
            var6 = CommandAbstract.e(var5, var4);
         } catch (ExceptionEntityNotFound var11) {
            return;
         }

         String var7 = this.d[var2.a()];
         if (var7 != null) {
            Scoreboard var8 = var1.getWorld().getScoreboard();
            ScoreboardObjective var9 = var8.getObjective(var7);
            if (var9 != null) {
               if (var8.b(var6, var9)) {
                  ScoreboardScore var10 = var8.getPlayerScoreForObjective(var6, var9);
                  var10.setScore(var3);
               }
            }
         }
      }
   }

   public void a(NBTTagCompound var1) {
      if (var1.hasKeyOfType("CommandStats", 10)) {
         NBTTagCompound var2 = var1.getCompound("CommandStats");

         for (CommandObjectiveExecutor.EnumCommandResult var6 : CommandObjectiveExecutor.EnumCommandResult.values()) {
            String var7 = var6.b() + "Name";
            String var8 = var6.b() + "Objective";
            if (var2.hasKeyOfType(var7, 8) && var2.hasKeyOfType(var8, 8)) {
               String var9 = var2.getString(var7);
               String var10 = var2.getString(var8);
               a(this, var6, var9, var10);
            }
         }
      }
   }

   public void b(NBTTagCompound var1) {
      NBTTagCompound var2 = new NBTTagCompound();

      for (CommandObjectiveExecutor.EnumCommandResult var6 : CommandObjectiveExecutor.EnumCommandResult.values()) {
         String var7 = this.c[var6.a()];
         String var8 = this.d[var6.a()];
         if (var7 != null && var8 != null) {
            var2.setString(var6.b() + "Name", var7);
            var2.setString(var6.b() + "Objective", var8);
         }
      }

      if (!var2.isEmpty()) {
         var1.set("CommandStats", var2);
      }
   }

   public static void a(CommandObjectiveExecutor var0, CommandObjectiveExecutor.EnumCommandResult var1, String var2, String var3) {
      if (var2 != null && var2.length() != 0 && var3 != null && var3.length() != 0) {
         if (var0.c == b || var0.d == b) {
            var0.c = new String[a];
            var0.d = new String[a];
         }

         var0.c[var1.a()] = var2;
         var0.d[var1.a()] = var3;
      } else {
         a(var0, var1);
      }
   }

   private static void a(CommandObjectiveExecutor var0, CommandObjectiveExecutor.EnumCommandResult var1) {
      if (var0.c != b && var0.d != b) {
         var0.c[var1.a()] = null;
         var0.d[var1.a()] = null;
         boolean var2 = true;

         for (CommandObjectiveExecutor.EnumCommandResult var6 : CommandObjectiveExecutor.EnumCommandResult.values()) {
            if (var0.c[var6.a()] != null && var0.d[var6.a()] != null) {
               var2 = false;
               break;
            }
         }

         if (var2) {
            var0.c = b;
            var0.d = b;
         }
      }
   }

   public void a(CommandObjectiveExecutor var1) {
      for (CommandObjectiveExecutor.EnumCommandResult var5 : CommandObjectiveExecutor.EnumCommandResult.values()) {
         a(this, var5, var1.c[var5.a()], var1.d[var5.a()]);
      }
   }

   public static enum EnumCommandResult {
      SUCCESS_COUNT(0, "SuccessCount"),
      AFFECTED_BLOCKS(1, "AffectedBlocks"),
      AFFECTED_ENTITIES(2, "AffectedEntities"),
      AFFECTED_ITEMS(3, "AffectedItems"),
      QUERY_RESULT(4, "QueryResult");

      final int f;
      final String g;

      private EnumCommandResult(int var3, String var4) {
         this.f = var3;
         this.g = var4;
      }

      public int a() {
         return this.f;
      }

      public String b() {
         return this.g;
      }

      public static String[] c() {
         String[] var0 = new String[values().length];
         int var1 = 0;

         for (CommandObjectiveExecutor.EnumCommandResult var5 : values()) {
            var0[var1++] = var5.b();
         }

         return var0;
      }

      public static CommandObjectiveExecutor.EnumCommandResult a(String var0) {
         for (CommandObjectiveExecutor.EnumCommandResult var4 : values()) {
            if (var4.b().equals(var0)) {
               return var4;
            }
         }

         return null;
      }
   }
}
