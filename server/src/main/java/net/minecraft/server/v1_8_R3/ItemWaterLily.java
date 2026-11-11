package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

public class ItemWaterLily extends ItemWithAuxData {
   public ItemWaterLily(Block block) {
      super(block, false);
   }

   @Override
   public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
      MovingObjectPosition movingobjectposition = this.a(world, entityhuman, true);
      if (movingobjectposition != null && movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
         BlockPosition blockposition = movingobjectposition.a();
         if (!world.a(entityhuman, blockposition)) {
            return itemstack;
         }

         if (!entityhuman.a(blockposition.shift(movingobjectposition.direction), movingobjectposition.direction, itemstack)) {
            return itemstack;
         }

         BlockPosition blockposition1 = blockposition.up();
         IBlockData iblockdata = world.getType(blockposition);
         if (iblockdata.getBlock().getMaterial() == Material.WATER && iblockdata.get(BlockFluids.LEVEL) == 0 && world.isEmpty(blockposition1)) {
            org.bukkit.block.BlockState blockstate = CraftBlockState.getBlockState(world, blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
            world.setTypeUpdate(blockposition1, Blocks.WATERLILY.getBlockData());
            BlockPlaceEvent placeEvent = CraftEventFactory.callBlockPlaceEvent(
               world, entityhuman, blockstate, blockposition.getX(), blockposition.getY(), blockposition.getZ()
            );
            if (placeEvent.isCancelled() || !placeEvent.canBuild()) {
               blockstate.update(true, false);
               return itemstack;
            }

            if (!entityhuman.abilities.canInstantlyBuild) {
               itemstack.count--;
            }
         }
      }

      return itemstack;
   }
}
