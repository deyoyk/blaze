package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityArrow;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;

public class CraftArrow extends AbstractProjectile implements Arrow {
   private final Arrow.Spigot spigot = new Arrow.Spigot() {
      @Override
      public double getDamage() {
         return CraftArrow.this.getHandle().j();
      }

      @Override
      public void setDamage(double damage) {
         CraftArrow.this.getHandle().b(damage);
      }
   };

   public CraftArrow(CraftServer server, EntityArrow entity) {
      super(server, entity);
   }

   @Override
   public void setKnockbackStrength(int knockbackStrength) {
      Validate.isTrue(knockbackStrength >= 0, "Knockback cannot be negative");
      this.getHandle().setKnockbackStrength(knockbackStrength);
   }

   @Override
   public int getKnockbackStrength() {
      return this.getHandle().knockbackStrength;
   }

   @Override
   public boolean isCritical() {
      return this.getHandle().isCritical();
   }

   @Override
   public void setCritical(boolean critical) {
      this.getHandle().setCritical(critical);
   }

   @Override
   public ProjectileSource getShooter() {
      return this.getHandle().projectileSource;
   }

   @Override
   public void setShooter(ProjectileSource shooter) {
      if (shooter instanceof LivingEntity) {
         this.getHandle().shooter = ((CraftLivingEntity)shooter).getHandle();
      } else {
         this.getHandle().shooter = null;
      }

      this.getHandle().projectileSource = shooter;
   }

   public EntityArrow getHandle() {
      return (EntityArrow)this.entity;
   }

   @Override
   public String toString() {
      return "CraftArrow";
   }

   @Override
   public EntityType getType() {
      return EntityType.ARROW;
   }

   @Deprecated
   @Override
   public LivingEntity _INVALID_getShooter() {
      return this.getHandle().shooter == null ? null : (LivingEntity)this.getHandle().shooter.getBukkitEntity();
   }

   @Deprecated
   @Override
   public void _INVALID_setShooter(LivingEntity shooter) {
      this.getHandle().shooter = ((CraftLivingEntity)shooter).getHandle();
   }

   @Override
   public Arrow.Spigot spigot() {
      return this.spigot;
   }
}
