package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Projectile;

public abstract class AbstractProjectile extends CraftEntity implements Projectile {
   private boolean doesBounce = false;

   public AbstractProjectile(CraftServer server, Entity entity) {
      super(server, entity);
   }

   @Override
   public boolean doesBounce() {
      return this.doesBounce;
   }

   @Override
   public void setBounce(boolean doesBounce) {
      this.doesBounce = doesBounce;
   }
}
