package net.minecraft.server.v1_8_R3;

import org.github.paperspigot.PaperSpigotConfig;

public class ItemMilkBucket extends Item {
   public ItemMilkBucket() {
      this.c(1);
      this.a(CreativeModeTab.f);
   }

   @Override
   public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
      if (!entityhuman.abilities.canInstantlyBuild) {
         itemstack.count--;
      }

      entityhuman.removeAllEffects();
      if (PaperSpigotConfig.stackableMilkBuckets) {
         if (itemstack.count <= 0) {
            return new ItemStack(Items.BUCKET);
         }

         if (!entityhuman.inventory.pickup(new ItemStack(Items.BUCKET))) {
            entityhuman.drop(new ItemStack(Items.BUCKET), false);
         }
      }

      return itemstack.count <= 0 ? new ItemStack(Items.BUCKET) : itemstack;
   }

   @Override
   public int d(ItemStack itemstack) {
      return 32;
   }

   @Override
   public EnumAnimation e(ItemStack itemstack) {
      return EnumAnimation.DRINK;
   }

   @Override
   public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
      entityhuman.a(itemstack, this.d(itemstack));
      return itemstack;
   }
}
