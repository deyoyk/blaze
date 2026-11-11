package net.minecraft.server.v1_8_R3;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class CommandAbstract implements ICommand {
   private static ICommandDispatcher a;

   public int a() {
      return 4;
   }

   @Override
   public List<String> b() {
      return Collections.emptyList();
   }

   @Override
   public boolean canUse(ICommandListener var1) {
      return var1.a(this.a(), this.getCommand());
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return null;
   }

   public static int a(String var0) throws ExceptionInvalidNumber {
      try {
         return Integer.parseInt(var0);
      } catch (NumberFormatException var2) {
         throw new ExceptionInvalidNumber("commands.generic.num.invalid", var0);
      }
   }

   public static int a(String var0, int var1) throws ExceptionInvalidNumber {
      return a(var0, var1, Integer.MAX_VALUE);
   }

   public static int a(String var0, int var1, int var2) throws ExceptionInvalidNumber {
      int var3 = a(var0);
      if (var3 < var1) {
         throw new ExceptionInvalidNumber("commands.generic.num.tooSmall", var3, var1);
      } else if (var3 > var2) {
         throw new ExceptionInvalidNumber("commands.generic.num.tooBig", var3, var2);
      } else {
         return var3;
      }
   }

   public static long b(String var0) throws ExceptionInvalidNumber {
      try {
         return Long.parseLong(var0);
      } catch (NumberFormatException var2) {
         throw new ExceptionInvalidNumber("commands.generic.num.invalid", var0);
      }
   }

   public static long a(String var0, long var1, long var3) throws ExceptionInvalidNumber {
      long var5 = b(var0);
      if (var5 < var1) {
         throw new ExceptionInvalidNumber("commands.generic.num.tooSmall", var5, var1);
      } else if (var5 > var3) {
         throw new ExceptionInvalidNumber("commands.generic.num.tooBig", var5, var3);
      } else {
         return var5;
      }
   }

   public static BlockPosition a(ICommandListener var0, String[] var1, int var2, boolean var3) throws ExceptionInvalidNumber {
      BlockPosition var4 = var0.getChunkCoordinates();
      return new BlockPosition(
         b(var4.getX(), var1[var2], -30000000, 30000000, var3),
         b(var4.getY(), var1[var2 + 1], 0, 256, false),
         b(var4.getZ(), var1[var2 + 2], -30000000, 30000000, var3)
      );
   }

   public static double c(String var0) throws ExceptionInvalidNumber {
      try {
         double var1 = Double.parseDouble(var0);
         if (!Doubles.isFinite(var1)) {
            throw new ExceptionInvalidNumber("commands.generic.num.invalid", var0);
         } else {
            return var1;
         }
      } catch (NumberFormatException var3) {
         throw new ExceptionInvalidNumber("commands.generic.num.invalid", var0);
      }
   }

   public static double a(String var0, double var1) throws ExceptionInvalidNumber {
      return a(var0, var1, Double.MAX_VALUE);
   }

   public static double a(String var0, double var1, double var3) throws ExceptionInvalidNumber {
      double var5 = c(var0);
      if (var5 < var1) {
         throw new ExceptionInvalidNumber("commands.generic.double.tooSmall", var5, var1);
      } else if (var5 > var3) {
         throw new ExceptionInvalidNumber("commands.generic.double.tooBig", var5, var3);
      } else {
         return var5;
      }
   }

   public static boolean d(String var0) throws CommandException {
      if (var0.equals("true") || var0.equals("1")) {
         return true;
      } else if (!var0.equals("false") && !var0.equals("0")) {
         throw new CommandException("commands.generic.boolean.invalid", var0);
      } else {
         return false;
      }
   }

   public static EntityPlayer b(ICommandListener var0) throws ExceptionPlayerNotFound {
      if (var0 instanceof EntityPlayer) {
         return (EntityPlayer)var0;
      } else {
         throw new ExceptionPlayerNotFound("You must specify which player you wish to perform this action on.");
      }
   }

   public static EntityPlayer a(ICommandListener var0, String var1) throws ExceptionPlayerNotFound {
      EntityPlayer var2 = PlayerSelector.getPlayer(var0, var1);
      if (var2 == null) {
         try {
            var2 = MinecraftServer.getServer().getPlayerList().a(UUID.fromString(var1));
         } catch (IllegalArgumentException var4) {
         }
      }

      if (var2 == null) {
         var2 = MinecraftServer.getServer().getPlayerList().getPlayer(var1);
      }

      if (var2 == null) {
         throw new ExceptionPlayerNotFound();
      } else {
         return var2;
      }
   }

   public static Entity b(ICommandListener var0, String var1) throws ExceptionEntityNotFound {
      return a(var0, var1, Entity.class);
   }

   public static <T extends Entity> T a(ICommandListener var0, String var1, Class<? extends T> var2) throws ExceptionEntityNotFound {
      Object var3 = PlayerSelector.getEntity(var0, var1, var2);
      MinecraftServer var4 = MinecraftServer.getServer();
      if (var3 == null) {
         var3 = var4.getPlayerList().getPlayer(var1);
      }

      if (var3 == null) {
         try {
            UUID var5 = UUID.fromString(var1);
            var3 = var4.a(var5);
            if (var3 == null) {
               var3 = var4.getPlayerList().a(var5);
            }
         } catch (IllegalArgumentException var6) {
            throw new ExceptionEntityNotFound("commands.generic.entity.invalidUuid");
         }
      }

      if (var3 != null && var2.isAssignableFrom(var3.getClass())) {
         return (T)var3;
      } else {
         throw new ExceptionEntityNotFound();
      }
   }

   public static List<Entity> c(ICommandListener var0, String var1) throws ExceptionEntityNotFound {
      return (List<Entity>)(PlayerSelector.isPattern(var1)
         ? PlayerSelector.getPlayers(var0, var1, Entity.class)
         : Lists.newArrayList(new Entity[]{b(var0, var1)}));
   }

   public static String d(ICommandListener var0, String var1) throws ExceptionPlayerNotFound {
      try {
         return a(var0, var1).getName();
      } catch (ExceptionPlayerNotFound var3) {
         if (PlayerSelector.isPattern(var1)) {
            throw var3;
         } else {
            return var1;
         }
      }
   }

   public static String e(ICommandListener var0, String var1) throws ExceptionEntityNotFound {
      try {
         return a(var0, var1).getName();
      } catch (ExceptionPlayerNotFound var5) {
         try {
            return b(var0, var1).getUniqueID().toString();
         } catch (ExceptionEntityNotFound var4) {
            if (PlayerSelector.isPattern(var1)) {
               throw var4;
            } else {
               return var1;
            }
         }
      }
   }

   public static IChatBaseComponent a(ICommandListener var0, String[] var1, int var2) throws ExceptionPlayerNotFound {
      return b(var0, var1, var2, false);
   }

   public static IChatBaseComponent b(ICommandListener var0, String[] var1, int var2, boolean var3) throws ExceptionPlayerNotFound {
      ChatComponentText var4 = new ChatComponentText("");

      for (int var5 = var2; var5 < var1.length; var5++) {
         if (var5 > var2) {
            var4.a(" ");
         }

         Object var6 = new ChatComponentText(var1[var5]);
         if (var3) {
            IChatBaseComponent var7 = PlayerSelector.getPlayerNames(var0, var1[var5]);
            if (var7 == null) {
               if (PlayerSelector.isPattern(var1[var5])) {
                  throw new ExceptionPlayerNotFound();
               }
            } else {
               var6 = var7;
            }
         }

         var4.addSibling((IChatBaseComponent)var6);
      }

      return var4;
   }

   public static String a(String[] var0, int var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = var1; var3 < var0.length; var3++) {
         if (var3 > var1) {
            var2.append(" ");
         }

         String var4 = var0[var3];
         var2.append(var4);
      }

      return var2.toString();
   }

   public static CommandAbstract.CommandNumber a(double var0, String var2, boolean var3) throws ExceptionInvalidNumber {
      return a(var0, var2, -30000000, 30000000, var3);
   }

   public static CommandAbstract.CommandNumber a(double var0, String var2, int var3, int var4, boolean var5) throws ExceptionInvalidNumber {
      boolean var6 = var2.startsWith("~");
      if (var6 && Double.isNaN(var0)) {
         throw new ExceptionInvalidNumber("commands.generic.num.invalid", var0);
      } else {
         double var7 = 0.0;
         if (!var6 || var2.length() > 1) {
            boolean var9 = var2.contains(".");
            if (var6) {
               var2 = var2.substring(1);
            }

            var7 += c(var2);
            if (!var9 && !var6 && var5) {
               var7 += 0.5;
            }
         }

         if (var3 != 0 || var4 != 0) {
            if (var7 < var3) {
               throw new ExceptionInvalidNumber("commands.generic.double.tooSmall", var7, var3);
            }

            if (var7 > var4) {
               throw new ExceptionInvalidNumber("commands.generic.double.tooBig", var7, var4);
            }
         }

         return new CommandAbstract.CommandNumber(var7 + (var6 ? var0 : 0.0), var7, var6);
      }
   }

   public static double b(double var0, String var2, boolean var3) throws ExceptionInvalidNumber {
      return b(var0, var2, -30000000, 30000000, var3);
   }

   public static double b(double var0, String var2, int var3, int var4, boolean var5) throws ExceptionInvalidNumber {
      boolean var6 = var2.startsWith("~");
      if (var6 && Double.isNaN(var0)) {
         throw new ExceptionInvalidNumber("commands.generic.num.invalid", var0);
      } else {
         double var7 = var6 ? var0 : 0.0;
         if (!var6 || var2.length() > 1) {
            boolean var9 = var2.contains(".");
            if (var6) {
               var2 = var2.substring(1);
            }

            var7 += c(var2);
            if (!var9 && !var6 && var5) {
               var7 += 0.5;
            }
         }

         if (var3 != 0 || var4 != 0) {
            if (var7 < var3) {
               throw new ExceptionInvalidNumber("commands.generic.double.tooSmall", var7, var3);
            }

            if (var7 > var4) {
               throw new ExceptionInvalidNumber("commands.generic.double.tooBig", var7, var4);
            }
         }

         return var7;
      }
   }

   public static Item f(ICommandListener var0, String var1) throws ExceptionInvalidNumber {
      MinecraftKey var2 = new MinecraftKey(var1);
      Item var3 = Item.REGISTRY.get(var2);
      if (var3 == null) {
         throw new ExceptionInvalidNumber("commands.give.item.notFound", var2);
      } else {
         return var3;
      }
   }

   public static Block g(ICommandListener var0, String var1) throws ExceptionInvalidNumber {
      MinecraftKey var2 = new MinecraftKey(var1);
      if (!Block.REGISTRY.d(var2)) {
         throw new ExceptionInvalidNumber("commands.give.block.notFound", var2);
      } else {
         Block var3 = Block.REGISTRY.get(var2);
         if (var3 == null) {
            throw new ExceptionInvalidNumber("commands.give.block.notFound", var2);
         } else {
            return var3;
         }
      }
   }

   public static String a(Object[] var0) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < var0.length; var2++) {
         String var3 = var0[var2].toString();
         if (var2 > 0) {
            if (var2 == var0.length - 1) {
               var1.append(" and ");
            } else {
               var1.append(", ");
            }
         }

         var1.append(var3);
      }

      return var1.toString();
   }

   public static IChatBaseComponent a(List<IChatBaseComponent> var0) {
      ChatComponentText var1 = new ChatComponentText("");

      for (int var2 = 0; var2 < var0.size(); var2++) {
         if (var2 > 0) {
            if (var2 == var0.size() - 1) {
               var1.a(" and ");
            } else if (var2 > 0) {
               var1.a(", ");
            }
         }

         var1.addSibling((IChatBaseComponent)var0.get(var2));
      }

      return var1;
   }

   public static String a(Collection<String> var0) {
      return a(var0.toArray(new String[var0.size()]));
   }

   public static List<String> a(String[] var0, int var1, BlockPosition var2) {
      if (var2 == null) {
         return null;
      } else {
         int var4 = var0.length - 1;
         String var3;
         if (var4 == var1) {
            var3 = Integer.toString(var2.getX());
         } else if (var4 == var1 + 1) {
            var3 = Integer.toString(var2.getY());
         } else {
            if (var4 != var1 + 2) {
               return null;
            }

            var3 = Integer.toString(var2.getZ());
         }

         return Lists.newArrayList(new String[]{var3});
      }
   }

   public static List<String> b(String[] var0, int var1, BlockPosition var2) {
      if (var2 == null) {
         return null;
      } else {
         int var4 = var0.length - 1;
         String var3;
         if (var4 == var1) {
            var3 = Integer.toString(var2.getX());
         } else {
            if (var4 != var1 + 1) {
               return null;
            }

            var3 = Integer.toString(var2.getZ());
         }

         return Lists.newArrayList(new String[]{var3});
      }
   }

   public static boolean a(String var0, String var1) {
      return var1.regionMatches(true, 0, var0, 0, var0.length());
   }

   public static List<String> a(String[] var0, String... var1) {
      return a(var0, Arrays.asList(var1));
   }

   public static List<String> a(String[] var0, Collection<?> var1) {
      String var2 = var0[var0.length - 1];
      ArrayList var3 = Lists.newArrayList();
      if (!var1.isEmpty()) {
         for (String var5 : Iterables.transform(var1, Functions.toStringFunction())) {
            if (a(var2, var5)) {
               var3.add(var5);
            }
         }

         if (var3.isEmpty()) {
            for (Object var7 : var1) {
               if (var7 instanceof MinecraftKey && a(var2, ((MinecraftKey)var7).a())) {
                  var3.add(String.valueOf(var7));
               }
            }
         }
      }

      return var3;
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return false;
   }

   public static void a(ICommandListener var0, ICommand var1, String var2, Object... var3) {
      a(var0, var1, 0, var2, var3);
   }

   public static void a(ICommandListener var0, ICommand var1, int var2, String var3, Object... var4) {
      if (a != null) {
         a.a(var0, var1, var2, var3, var4);
      }
   }

   public static void a(ICommandDispatcher var0) {
      a = var0;
   }

   @Override
   public int compareTo(ICommand var1) {
      return this.getCommand().compareTo(var1.getCommand());
   }

   public static class CommandNumber {
      private final double a;
      private final double b;
      private final boolean c;

      protected CommandNumber(double var1, double var3, boolean var5) {
         this.a = var1;
         this.b = var3;
         this.c = var5;
      }

      public double a() {
         return this.a;
      }

      public double b() {
         return this.b;
      }

      public boolean c() {
         return this.c;
      }
   }
}
