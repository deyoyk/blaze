package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class SlotFurnaceResult extends Slot {
   private final EntityHuman a;
   private int b;

   public SlotFurnaceResult(EntityHuman entityhuman, IInventory iinventory, int i, int j, int k) {
      super(iinventory, i, j, k);
      this.a = entityhuman;
   }

   @Override
   public boolean isAllowed(ItemStack itemstack) {
      return false;
   }

   @Override
   public ItemStack a(int i) {
      if (this.hasItem()) {
         this.b = this.b + Math.min(i, this.getItem().count);
      }

      return super.a(i);
   }

   @Override
   public void a(EntityHuman entityhuman, ItemStack itemstack) {
      this.c(itemstack);
      super.a(entityhuman, itemstack);
   }

   @Override
   protected void a(ItemStack itemstack, int i) {
      this.b += i;
      this.c(itemstack);
   }

   @Override
   protected void c(ItemStack itemstack) {
      itemstack.a(this.a.world, this.a, this.b);
      int i = this.b;
      float f = RecipesFurnace.getInstance().b(itemstack);
      if (f == 0.0F) {
         i = 0;
      } else if (f < 1.0F) {
         int j = MathHelper.d(i * f);
         if (j < MathHelper.f(i * f) && Math.random() < i * f - j) {
            j++;
         }

         i = j;
      }

      Player player = (Player)this.a.getBukkitEntity();
      TileEntityFurnace furnace = (TileEntityFurnace)this.inventory;
      org.bukkit.block.Block block = this.a.world.getWorld().getBlockAt(furnace.position.getX(), furnace.position.getY(), furnace.position.getZ());
      if (this.b != 0) {
         FurnaceExtractEvent event = new FurnaceExtractEvent(player, block, CraftMagicNumbers.getMaterial(itemstack.getItem()), this.b, i);
         this.a.world.getServer().getPluginManager().callEvent(event);
         i = event.getExpToDrop();
      }

      while (i > 0) {
         int j = EntityExperienceOrb.getOrbValue(i);
         i -= j;
         this.a.world.addEntity(new EntityExperienceOrb(this.a.world, this.a.locX, this.a.locY + 0.5, this.a.locZ + 0.5, j));
      }

      this.b = 0;
   }
}
