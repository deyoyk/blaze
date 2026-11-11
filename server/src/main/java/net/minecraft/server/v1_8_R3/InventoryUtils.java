package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class InventoryUtils {
   private static final Random a = new Random();

   public static void dropInventory(World var0, BlockPosition var1, IInventory var2) {
      dropInventory(var0, var1.getX(), var1.getY(), var1.getZ(), var2);
   }

   public static void dropEntity(World var0, Entity var1, IInventory var2) {
      dropInventory(var0, var1.locX, var1.locY, var1.locZ, var2);
   }

   private static void dropInventory(World var0, double var1, double var3, double var5, IInventory var7) {
      for (int var8 = 0; var8 < var7.getSize(); var8++) {
         ItemStack var9 = var7.getItem(var8);
         if (var9 != null) {
            dropItem(var0, var1, var3, var5, var9);
         }
      }
   }

   private static void dropItem(World var0, double var1, double var3, double var5, ItemStack var7) {
      float var8 = a.nextFloat() * 0.8F + 0.1F;
      float var9 = a.nextFloat() * 0.8F + 0.1F;
      float var10 = a.nextFloat() * 0.8F + 0.1F;

      while (var7.count > 0) {
         int var11 = a.nextInt(21) + 10;
         if (var11 > var7.count) {
            var11 = var7.count;
         }

         var7.count -= var11;
         EntityItem var12 = new EntityItem(var0, var1 + var8, var3 + var9, var5 + var10, new ItemStack(var7.getItem(), var11, var7.getData()));
         if (var7.hasTag()) {
            var12.getItemStack().setTag((NBTTagCompound)var7.getTag().clone());
         }

         float var13 = 0.05F;
         var12.motX = a.nextGaussian() * var13;
         var12.motY = a.nextGaussian() * var13 + 0.2F;
         var12.motZ = a.nextGaussian() * var13;
         var0.addEntity(var12);
      }
   }
}
