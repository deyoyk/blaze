package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerEggThrowEvent;

public class EntityEgg extends EntityProjectile {
   public EntityEgg(World world) {
      super(world);
   }

   public EntityEgg(World world, EntityLiving entityliving) {
      super(world, entityliving);
   }

   public EntityEgg(World world, double d0, double d1, double d2) {
      super(world, d0, d1, d2);
   }

   @Override
   public float getRandomness() {
      return (Float)BlazeConfig.eggTrajectoryRandomness.getValue();
   }

   @Override
   protected void a(MovingObjectPosition movingobjectposition) {
      if (movingobjectposition.entity != null) {
         movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), 0.0F);
      }

      boolean hatching = this.random.nextInt(8) == 0;
      int numHatching = this.random.nextInt(32) == 0 ? 4 : 1;
      if (!hatching) {
         numHatching = 0;
      }

      EntityType hatchingType = EntityType.CHICKEN;
      Entity shooter = this.getShooter();
      if (shooter instanceof EntityPlayer) {
         Player player = (Player)shooter.getBukkitEntity();
         PlayerEggThrowEvent event = new PlayerEggThrowEvent(player, (Egg)this.getBukkitEntity(), hatching, (byte)numHatching, hatchingType);
         this.world.getServer().getPluginManager().callEvent(event);
         hatching = event.isHatching();
         numHatching = event.getNumHatches();
         hatchingType = event.getHatchingType();
      }

      if (hatching) {
         for (int k = 0; k < numHatching; k++) {
            Entity entity = this.world
               .getWorld()
               .createEntity(new Location(this.world.getWorld(), this.locX, this.locY, this.locZ, this.yaw, 0.0F), hatchingType.getEntityClass());
            if (entity.getBukkitEntity() instanceof Ageable) {
               ((Ageable)entity.getBukkitEntity()).setBaby();
            }

            this.world.getWorld().addEntity(entity, SpawnReason.EGG);
         }
      }

      for (int j = 0; j < 8; j++) {
         this.world
            .addParticle(
               EnumParticle.ITEM_CRACK,
               this.locX,
               this.locY,
               this.locZ,
               (this.random.nextFloat() - 0.5) * 0.08,
               (this.random.nextFloat() - 0.5) * 0.08,
               (this.random.nextFloat() - 0.5) * 0.08,
               Item.getId(Items.EGG)
            );
      }

      this.die();
   }
}
