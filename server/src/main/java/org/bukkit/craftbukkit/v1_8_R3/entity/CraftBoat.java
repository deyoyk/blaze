package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityBoat;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;

public class CraftBoat extends CraftVehicle implements Boat {
   public CraftBoat(CraftServer server, EntityBoat entity) {
      super(server, entity);
   }

   @Override
   public double getMaxSpeed() {
      return this.getHandle().maxSpeed;
   }

   @Override
   public void setMaxSpeed(double speed) {
      if (speed >= 0.0) {
         this.getHandle().maxSpeed = speed;
      }
   }

   @Override
   public double getOccupiedDeceleration() {
      return this.getHandle().occupiedDeceleration;
   }

   @Override
   public void setOccupiedDeceleration(double speed) {
      if (speed >= 0.0) {
         this.getHandle().occupiedDeceleration = speed;
      }
   }

   @Override
   public double getUnoccupiedDeceleration() {
      return this.getHandle().unoccupiedDeceleration;
   }

   @Override
   public void setUnoccupiedDeceleration(double speed) {
      this.getHandle().unoccupiedDeceleration = speed;
   }

   @Override
   public boolean getWorkOnLand() {
      return this.getHandle().landBoats;
   }

   @Override
   public void setWorkOnLand(boolean workOnLand) {
      this.getHandle().landBoats = workOnLand;
   }

   public EntityBoat getHandle() {
      return (EntityBoat)this.entity;
   }

   @Override
   public String toString() {
      return "CraftBoat";
   }

   @Override
   public EntityType getType() {
      return EntityType.BOAT;
   }
}
