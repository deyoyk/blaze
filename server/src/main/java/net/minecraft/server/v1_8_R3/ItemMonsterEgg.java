package net.minecraft.server.v1_8_R3;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class ItemMonsterEgg extends Item {
   public ItemMonsterEgg() {
      this.a(true);
      this.a(CreativeModeTab.f);
   }

   public static Entity a(World world, int i, double d0, double d1, double d2) {
      return spawnCreature(world, i, d0, d1, d2, SpawnReason.SPAWNER_EGG);
   }

   public static Entity spawnCreature(World world, int i, double d0, double d1, double d2, SpawnReason spawnReason) {
      if (!EntityTypes.eggInfo.containsKey(i)) {
         return null;
      } else {
         Entity entity = null;

         for (int j = 0; j < 1; j++) {
            entity = EntityTypes.a(i, world);
            if (entity instanceof EntityLiving) {
               EntityInsentient entityinsentient = (EntityInsentient)entity;
               entity.setPositionRotation(d0, d1, d2, MathHelper.g(world.random.nextFloat() * 360.0F), 0.0F);
               entityinsentient.aK = entityinsentient.yaw;
               entityinsentient.aI = entityinsentient.yaw;
               entityinsentient.prepare(
                  world.E(MathHelper.floor(entityinsentient.locX), MathHelper.floor(entityinsentient.locY), MathHelper.floor(entityinsentient.locZ)), null
               );
               if (!world.addEntity(entity, spawnReason)) {
                  entity = null;
               } else {
                  entityinsentient.x();
               }
            }
         }

         return entity;
      }
   }

   @Override
   public String a(ItemStack itemstack) {
      String s = LocaleI18n.get(this.getName() + ".name").trim();
      String s1 = EntityTypes.b(itemstack.getData());
      if (s1 != null) {
         s = s + " " + LocaleI18n.get("entity." + s1 + ".name");
      }

      return s;
   }

   @Override
   public boolean interactWith(
      ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (!entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack)) {
         return false;
      } else {
         IBlockData iblockdata = world.getType(blockposition);
         if (iblockdata.getBlock() == Blocks.MOB_SPAWNER) {
            TileEntity tileentity = world.getTileEntity(blockposition);
            if (tileentity instanceof TileEntityMobSpawner) {
               MobSpawnerAbstract mobspawnerabstract = ((TileEntityMobSpawner)tileentity).getSpawner();
               mobspawnerabstract.setMobName(EntityTypes.b(itemstack.getData()));
               tileentity.update();
               world.notify(blockposition);
               if (!entityhuman.abilities.canInstantlyBuild) {
                  itemstack.count--;
               }

               return true;
            }
         }

         blockposition = blockposition.shift(enumdirection);
         double d0 = 0.0;
         if (enumdirection == EnumDirection.UP && iblockdata instanceof BlockFence) {
            d0 = 0.5;
         }

         Entity entity = a(world, itemstack.getData(), blockposition.getX() + 0.5, blockposition.getY() + d0, blockposition.getZ() + 0.5);
         if (entity != null) {
            if (entity instanceof EntityLiving && itemstack.hasName()) {
               entity.setCustomName(itemstack.getName());
            }

            if (!entityhuman.abilities.canInstantlyBuild) {
               itemstack.count--;
            }
         }

         return true;
      }
   }

   @Override
   public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
      MovingObjectPosition movingobjectposition = this.a(world, entityhuman, true);
      if (movingobjectposition != null && movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
         BlockPosition blockposition = movingobjectposition.a();
         if (!world.a(entityhuman, blockposition)) {
            return itemstack;
         }

         if (!entityhuman.a(blockposition, movingobjectposition.direction, itemstack)) {
            return itemstack;
         }

         if (world.getType(blockposition).getBlock() instanceof BlockFluids) {
            Entity entity = a(world, itemstack.getData(), blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5);
            if (entity != null) {
               if (entity instanceof EntityLiving && itemstack.hasName()) {
                  entity.setCustomName(itemstack.getName());
               }

               if (!entityhuman.abilities.canInstantlyBuild) {
                  itemstack.count--;
               }
            }
         }
      }

      return itemstack;
   }
}
