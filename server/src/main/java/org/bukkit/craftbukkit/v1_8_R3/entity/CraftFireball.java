package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityFireball;
import net.minecraft.server.v1_8_R3.MathHelper;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class CraftFireball extends AbstractProjectile implements Fireball {
   public CraftFireball(CraftServer server, EntityFireball entity) {
      super(server, entity);
   }

   @Override
   public float getYield() {
      return this.getHandle().bukkitYield;
   }

   @Override
   public boolean isIncendiary() {
      return this.getHandle().isIncendiary;
   }

   @Override
   public void setIsIncendiary(boolean isIncendiary) {
      this.getHandle().isIncendiary = isIncendiary;
   }

   @Override
   public void setYield(float yield) {
      this.getHandle().bukkitYield = yield;
   }

   @Override
   public ProjectileSource getShooter() {
      return this.getHandle().projectileSource;
   }

   @Override
   public void setShooter(ProjectileSource shooter) {
      if (shooter instanceof CraftLivingEntity) {
         this.getHandle().shooter = ((CraftLivingEntity)shooter).getHandle();
      } else {
         this.getHandle().shooter = null;
      }

      this.getHandle().projectileSource = shooter;
   }

   @Override
   public Vector getDirection() {
      return new Vector(this.getHandle().dirX, this.getHandle().dirY, this.getHandle().dirZ);
   }

   @Override
   public void setDirection(Vector direction) {
      Validate.notNull(direction, "Direction can not be null");
      double x = direction.getX();
      double y = direction.getY();
      double z = direction.getZ();
      double magnitude = MathHelper.sqrt(x * x + y * y + z * z);
      this.getHandle().dirX = x / magnitude;
      this.getHandle().dirY = y / magnitude;
      this.getHandle().dirZ = z / magnitude;
   }

   public EntityFireball getHandle() {
      return (EntityFireball)this.entity;
   }

   @Override
   public String toString() {
      return "CraftFireball";
   }

   @Override
   public EntityType getType() {
      return EntityType.UNKNOWN;
   }

   @Deprecated
   @Override
   public void _INVALID_setShooter(LivingEntity shooter) {
      this.setShooter(shooter);
   }

   @Deprecated
   @Override
   public LivingEntity _INVALID_getShooter() {
      return this.getHandle().shooter != null ? (LivingEntity)this.getHandle().shooter.getBukkitEntity() : null;
   }
}
