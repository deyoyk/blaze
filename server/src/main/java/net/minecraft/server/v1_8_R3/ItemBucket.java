package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.utils.Utils;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.github.paperspigot.PaperSpigotConfig;

public class ItemBucket extends Item {
   private final Block a;

   public ItemBucket(Block block) {
      this.maxStackSize = 1;
      this.a = block;
      this.a(CreativeModeTab.f);
   }

   @Override
   public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
      boolean flag = this.a == Blocks.AIR;
      MovingObjectPosition movingobjectposition = this.a(world, entityhuman, flag);
      if (movingobjectposition != null && movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
         BlockPosition blockposition = movingobjectposition.a();
         if (!world.a(entityhuman, blockposition)) {
            return itemstack;
         }

         if (flag) {
            if (!entityhuman.a(blockposition.shift(movingobjectposition.direction), movingobjectposition.direction, itemstack)) {
               return itemstack;
            }

            IBlockData iblockdata = world.getType(blockposition);
            Material material = iblockdata.getBlock().getMaterial();
            if (material == Material.WATER && iblockdata.get(BlockFluids.LEVEL) == 0) {
               PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(
                  entityhuman, blockposition.getX(), blockposition.getY(), blockposition.getZ(), null, itemstack, Items.WATER_BUCKET
               );
               if (event.isCancelled()) {
                  ((EntityPlayer)entityhuman).updateInventory(entityhuman.activeContainer);
                  entityhuman.sendPacket(new PacketPlayOutBlockChange(world, blockposition));
                  return itemstack;
               }

               world.setAir(blockposition);
               ItemStack result = this.a(itemstack, entityhuman, Items.WATER_BUCKET, event.getItemStack());
               this.notifyChanges(entityhuman, world, blockposition);
               return result;
            }

            if (material == Material.LAVA && iblockdata.get(BlockFluids.LEVEL) == 0) {
               PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(
                  entityhuman, blockposition.getX(), blockposition.getY(), blockposition.getZ(), null, itemstack, Items.LAVA_BUCKET
               );
               if (event.isCancelled()) {
                  ((EntityPlayer)entityhuman).updateInventory(entityhuman.activeContainer);
                  entityhuman.sendPacket(new PacketPlayOutBlockChange(world, blockposition));
                  return itemstack;
               }

               world.setAir(blockposition);
               ItemStack result = this.a(itemstack, entityhuman, Items.LAVA_BUCKET, event.getItemStack());
               this.notifyChanges(entityhuman, world, blockposition);
               return result;
            }
         } else {
            BlockPosition blockposition1 = blockposition.shift(movingobjectposition.direction);
            if (!entityhuman.a(blockposition1, movingobjectposition.direction, itemstack)) {
               return itemstack;
            }

            PlayerBucketEmptyEvent event = CraftEventFactory.callPlayerBucketEmptyEvent(
               entityhuman, blockposition.getX(), blockposition.getY(), blockposition.getZ(), movingobjectposition.direction, itemstack
            );
            int radius = (Integer)BlazeConfig.spongeRadius.getValue();
            if (!event.isCancelled() && radius > 0 && (Boolean)BlazeConfig.spongePreventWaterFlow.getValue()) {
               int x = blockposition.getX();
               int y = blockposition.getY();
               int z = blockposition.getZ();
               org.bukkit.World bukkitWorld = world.getWorld();

               for (int searchX = -radius; searchX <= radius; searchX++) {
                  for (int searchY = -radius; searchY <= radius; searchY++) {
                     for (int searchZ = -radius; searchZ <= radius; searchZ++) {
                        if (bukkitWorld.getBlockAt(x + searchX, y + searchY, z + searchZ).getType() == org.bukkit.Material.SPONGE) {
                           event.setCancelled(true);
                           break;
                        }
                     }
                  }
               }
            }

            if (event.isCancelled()) {
               ((EntityPlayer)entityhuman).updateInventory(entityhuman.activeContainer);
               entityhuman.sendPacket(new PacketPlayOutBlockChange(world, blockposition1));
               return itemstack;
            }

            if (this.a(entityhuman, world, blockposition1) && !entityhuman.abilities.canInstantlyBuild) {
               this.notifyChanges(entityhuman, world, blockposition);
               ItemStack stack = CraftItemStack.asNMSCopy(event.getItemStack());
               if (this == Items.LAVA_BUCKET && PaperSpigotConfig.stackableLavaBuckets || this == Items.WATER_BUCKET && PaperSpigotConfig.stackableWaterBuckets
                  )
                {
                  if (--itemstack.count <= 0) {
                     return stack;
                  }

                  if (!entityhuman.inventory.pickup(stack)) {
                     entityhuman.drop(stack, false);
                  }

                  return itemstack;
               }

               return stack;
            }
         }
      }

      return itemstack;
   }

   private ItemStack a(ItemStack itemstack, EntityHuman entityhuman, Item item, org.bukkit.inventory.ItemStack result) {
      if (entityhuman.abilities.canInstantlyBuild) {
         return itemstack;
      } else if (--itemstack.count <= 0) {
         return CraftItemStack.asNMSCopy(result);
      } else {
         if (!entityhuman.inventory.pickup(CraftItemStack.asNMSCopy(result))) {
            entityhuman.drop(CraftItemStack.asNMSCopy(result), false);
         }

         return itemstack;
      }
   }

   public boolean a(EntityHuman entityhuman, World world, BlockPosition blockposition) {
      if (this.a == Blocks.AIR) {
         return false;
      } else {
         Material material = world.getType(blockposition).getBlock().getMaterial();
         boolean flag = !material.isBuildable();
         if (!world.isEmpty(blockposition) && !flag) {
            return false;
         } else {
            if (world.worldProvider.n() && this.a == Blocks.FLOWING_WATER) {
               int i = blockposition.getX();
               int j = blockposition.getY();
               int k = blockposition.getZ();
               world.makeSound(i + 0.5F, j + 0.5F, k + 0.5F, "random.fizz", 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

               for (int l = 0; l < 8; l++) {
                  world.addParticle(EnumParticle.SMOKE_LARGE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
               }
            } else {
               if (flag && !material.isLiquid()) {
                  world.setAir(blockposition, true);
               }

               world.setTypeAndData(blockposition, this.a.getBlockData(), 3);
            }

            return true;
         }
      }
   }

   private void notifyChanges(EntityHuman entityhuman, World world, BlockPosition position) {
      if (entityhuman != null) {
         Chunk chunk = null;

         for (int radiusX = -1; radiusX <= 1; radiusX++) {
            for (int radiusZ = -1; radiusZ <= 1; radiusZ++) {
               int locX = position.getX() + radiusX;
               int locZ = position.getZ() + radiusZ;
               int chunkX = locX >> 4;
               int chunkZ = locZ >> 4;
               if (Utils.isInvalidChunk(chunk, chunkX, chunkZ)) {
                  chunk = world.getChunkAt(chunkX, chunkZ);
               }

               for (int radiusY = -1; radiusY <= 1; radiusY++) {
                  entityhuman.sendPacket(new PacketPlayOutBlockChange(world, chunk, new BlockPosition(locX, position.getY() + radiusY, locZ)));
               }
            }
         }

         ((EntityPlayer)entityhuman).updateInventory(entityhuman.activeContainer);
      }
   }
}
