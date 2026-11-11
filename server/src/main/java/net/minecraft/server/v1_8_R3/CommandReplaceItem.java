package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

public class CommandReplaceItem extends CommandAbstract {
   private static final Map<String, Integer> a = Maps.newHashMap();

   @Override
   public String getCommand() {
      return "replaceitem";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.replaceitem.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new ExceptionUsage("commands.replaceitem.usage");
      } else {
         boolean var3;
         if (var2[0].equals("entity")) {
            var3 = false;
         } else {
            if (!var2[0].equals("block")) {
               throw new ExceptionUsage("commands.replaceitem.usage");
            }

            var3 = true;
         }

         int var4;
         if (var3) {
            if (var2.length < 6) {
               throw new ExceptionUsage("commands.replaceitem.block.usage");
            }

            var4 = 4;
         } else {
            if (var2.length < 4) {
               throw new ExceptionUsage("commands.replaceitem.entity.usage");
            }

            var4 = 2;
         }

         int var5 = this.e(var2[var4++]);

         Item var6;
         try {
            var6 = f(var1, var2[var4]);
         } catch (ExceptionInvalidNumber var15) {
            if (Block.getByName(var2[var4]) != Blocks.AIR) {
               throw var15;
            }

            var6 = null;
         }

         var4++;
         int var7 = var2.length > var4 ? a(var2[var4++], 1, 64) : 1;
         int var8 = var2.length > var4 ? a(var2[var4++]) : 0;
         ItemStack var9 = new ItemStack(var6, var7, var8);
         if (var2.length > var4) {
            String var10 = a(var1, var2, var4).c();

            try {
               var9.setTag(MojangsonParser.parse(var10));
            } catch (MojangsonParseException var14) {
               throw new CommandException("commands.replaceitem.tagError", var14.getMessage());
            }
         }

         if (var9.getItem() == null) {
            var9 = null;
         }

         if (var3) {
            var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, 0);
            BlockPosition var18 = a(var1, var2, 1, false);
            World var11 = var1.getWorld();
            TileEntity var12 = var11.getTileEntity(var18);
            if (var12 == null || !(var12 instanceof IInventory)) {
               throw new CommandException("commands.replaceitem.noContainer", var18.getX(), var18.getY(), var18.getZ());
            }

            IInventory var13 = (IInventory)var12;
            if (var5 >= 0 && var5 < var13.getSize()) {
               var13.setItem(var5, var9);
            }
         } else {
            Entity var19 = b(var1, var2[1]);
            var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, 0);
            if (var19 instanceof EntityHuman) {
               ((EntityHuman)var19).defaultContainer.b();
            }

            if (!var19.d(var5, var9)) {
               throw new CommandException("commands.replaceitem.failed", var5, var7, var9 == null ? "Air" : var9.C());
            }

            if (var19 instanceof EntityHuman) {
               ((EntityHuman)var19).defaultContainer.b();
            }
         }

         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, var7);
         a(var1, this, "commands.replaceitem.success", new Object[]{var5, var7, var9 == null ? "Air" : var9.C()});
      }
   }

   private int e(String var1) throws CommandException {
      if (!a.containsKey(var1)) {
         throw new CommandException("commands.generic.parameter.invalid", var1);
      } else {
         return a.get(var1);
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
         return (var2.length != 4 || !var2[0].equals("entity")) && (var2.length != 6 || !var2[0].equals("block")) ? null : a(var2, Item.REGISTRY.keySet());
      } else {
         return a(var2, a.keySet());
      }
   }

   protected String[] d() {
      return MinecraftServer.getServer().getPlayers();
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var1.length > 0 && var1[0].equals("entity") && var2 == 1;
   }

   static {
      for (int var0 = 0; var0 < 54; var0++) {
         a.put("slot.container." + var0, var0);
      }

      for (int var1 = 0; var1 < 9; var1++) {
         a.put("slot.hotbar." + var1, var1);
      }

      for (int var2 = 0; var2 < 27; var2++) {
         a.put("slot.inventory." + var2, 9 + var2);
      }

      for (int var3 = 0; var3 < 27; var3++) {
         a.put("slot.enderchest." + var3, 200 + var3);
      }

      for (int var4 = 0; var4 < 8; var4++) {
         a.put("slot.villager." + var4, 300 + var4);
      }

      for (int var5 = 0; var5 < 15; var5++) {
         a.put("slot.horse." + var5, 500 + var5);
      }

      a.put("slot.weapon", 99);
      a.put("slot.armor.head", 103);
      a.put("slot.armor.chest", 102);
      a.put("slot.armor.legs", 101);
      a.put("slot.armor.feet", 100);
      a.put("slot.horse.saddle", 400);
      a.put("slot.horse.armor", 401);
      a.put("slot.horse.chest", 499);
   }
}
