package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.MathHelper;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;

public class CraftFish extends AbstractProjectile implements Fish {
   private double biteChance = -1.0;

   public CraftFish(CraftServer server, EntityFishingHook entity) {
      super(server, entity);
   }

   @Override
   public ProjectileSource getShooter() {
      return this.getHandle().owner != null ? this.getHandle().owner.getBukkitEntity() : null;
   }

   @Override
   public void setShooter(ProjectileSource shooter) {
      if (shooter instanceof CraftHumanEntity) {
         this.getHandle().owner = (EntityHuman)((CraftHumanEntity)shooter).entity;
      }
   }

   public EntityFishingHook getHandle() {
      return (EntityFishingHook)this.entity;
   }

   @Override
   public String toString() {
      return "CraftFish";
   }

   @Override
   public EntityType getType() {
      return EntityType.FISHING_HOOK;
   }

   @Override
   public double getBiteChance() {
      EntityFishingHook hook = this.getHandle();
      if (this.biteChance == -1.0) {
         return hook.world.isRainingAt(MathHelper.floor(hook.locX), MathHelper.floor(hook.locY) + 1, MathHelper.floor(hook.locZ))
            ? 0.0033333333333333335
            : 0.002;
      } else {
         return this.biteChance;
      }
   }

   @Override
   public void setBiteChance(double chance) {
      Validate.isTrue(chance >= 0.0 && chance <= 1.0, "The bite chance must be between 0 and 1.");
      this.biteChance = chance;
   }

   @Deprecated
   @Override
   public LivingEntity _INVALID_getShooter() {
      return (LivingEntity)this.getShooter();
   }

   @Deprecated
   @Override
   public void _INVALID_setShooter(LivingEntity shooter) {
      this.setShooter(shooter);
   }
}
