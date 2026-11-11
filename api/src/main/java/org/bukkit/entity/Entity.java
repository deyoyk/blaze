package org.bukkit.entity;

import java.util.List;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.Metadatable;
import org.bukkit.util.Vector;

public interface Entity extends Metadatable, CommandSender {
   org.bukkit.Location getLocation();

   org.bukkit.Location getLocation(org.bukkit.Location var1);

   void setVelocity(Vector var1);

   Vector getVelocity();

   boolean isOnGround();

   org.bukkit.World getWorld();

   boolean teleport(org.bukkit.Location var1);

   boolean teleport(org.bukkit.Location var1, PlayerTeleportEvent.TeleportCause var2);

   boolean teleport(Entity var1);

   boolean teleport(Entity var1, PlayerTeleportEvent.TeleportCause var2);

   List<Entity> getNearbyEntities(double var1, double var3, double var5);

   int getEntityId();

   int getFireTicks();

   int getMaxFireTicks();

   void setFireTicks(int var1);

   void remove();

   boolean isDead();

   boolean isValid();

   @Override
   org.bukkit.Server getServer();

   Entity getPassenger();

   boolean setPassenger(Entity var1);

   boolean isEmpty();

   boolean eject();

   float getFallDistance();

   void setFallDistance(float var1);

   void setLastDamageCause(EntityDamageEvent var1);

   EntityDamageEvent getLastDamageCause();

   UUID getUniqueId();

   int getTicksLived();

   void setTicksLived(int var1);

   void playEffect(org.bukkit.EntityEffect var1);

   EntityType getType();

   boolean isInsideVehicle();

   boolean leaveVehicle();

   Entity getVehicle();

   void setCustomName(String var1);

   String getCustomName();

   void setCustomNameVisible(boolean var1);

   boolean isCustomNameVisible();

   Entity.Spigot spigot();

   public static class Spigot {
      public boolean isInvulnerable() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
