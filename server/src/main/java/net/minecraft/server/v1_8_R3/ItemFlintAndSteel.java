package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

public class ItemFlintAndSteel extends Item {
   public ItemFlintAndSteel() {
      this.maxStackSize = 1;
      this.setMaxDurability(64);
      this.a(CreativeModeTab.i);
   }

   @Override
   public boolean interactWith(
      ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      blockposition = blockposition.shift(enumdirection);
      if (!entityhuman.a(blockposition, enumdirection, itemstack)) {
         return false;
      } else {
         if (world.getType(blockposition).getBlock().getMaterial() == Material.AIR) {
            if (CraftEventFactory.callBlockIgniteEvent(
                  world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), IgniteCause.FLINT_AND_STEEL, entityhuman
               )
               .isCancelled()) {
               itemstack.damage(1, entityhuman);
               return false;
            }

            CraftBlockState blockState = CraftBlockState.getBlockState(world, blockposition.getX(), blockposition.getY(), blockposition.getZ());
            world.makeSound(
               blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5, "fire.ignite", 1.0F, g.nextFloat() * 0.4F + 0.8F
            );
            world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
            BlockPlaceEvent placeEvent = CraftEventFactory.callBlockPlaceEvent(
               world, entityhuman, blockState, blockposition.getX(), blockposition.getY(), blockposition.getZ()
            );
            if (placeEvent.isCancelled() || !placeEvent.canBuild()) {
               placeEvent.getBlockPlaced().setTypeIdAndData(0, (byte)0, false);
               return false;
            }
         }

         itemstack.damage(1, entityhuman);
         return true;
      }
   }
}
