package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;

public class EntitySnowball extends EntityProjectile {
   public EntitySnowball(World world) {
      super(world);
   }

   public EntitySnowball(World world, EntityLiving entityliving) {
      super(world, entityliving);
   }

   public EntitySnowball(World world, double d0, double d1, double d2) {
      super(world, d0, d1, d2);
   }

   @Override
   public float getRandomness() {
      return (Float)BlazeConfig.snowballTrajectoryRandomness.getValue();
   }

   @Override
   protected void a(MovingObjectPosition movingobjectposition) {
      if (movingobjectposition.entity != null) {
         byte b0 = 0;
         if (movingobjectposition.entity instanceof EntityBlaze) {
            b0 = 3;
         }

         movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), b0);
      }

      for (int i = 0; i < 8; i++) {
         this.world.addParticle(EnumParticle.SNOWBALL, this.locX, this.locY, this.locZ, 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
      }

      this.die();
   }
}
