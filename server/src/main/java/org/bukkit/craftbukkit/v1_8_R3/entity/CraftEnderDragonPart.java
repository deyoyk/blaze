package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityComplexPart;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.util.NumberConversions;

public class CraftEnderDragonPart extends CraftComplexPart implements EnderDragonPart {
   public CraftEnderDragonPart(CraftServer server, EntityComplexPart entity) {
      super(server, entity);
   }

   @Override
   public EnderDragon getParent() {
      return (EnderDragon)super.getParent();
   }

   @Override
   public EntityComplexPart getHandle() {
      return (EntityComplexPart)this.entity;
   }

   @Override
   public String toString() {
      return "CraftEnderDragonPart";
   }

   @Override
   public void damage(double amount) {
      this.getParent().damage(amount);
   }

   @Override
   public void damage(double amount, Entity source) {
      this.getParent().damage(amount, source);
   }

   @Override
   public double getHealth() {
      return this.getParent().getHealth();
   }

   @Override
   public void setHealth(double health) {
      this.getParent().setHealth(health);
   }

   @Override
   public double getMaxHealth() {
      return this.getParent().getMaxHealth();
   }

   @Override
   public void setMaxHealth(double health) {
      this.getParent().setMaxHealth(health);
   }

   @Override
   public void resetMaxHealth() {
      this.getParent().resetMaxHealth();
   }

   @Deprecated
   @Override
   public void _INVALID_damage(int amount) {
      this.damage(amount);
   }

   @Deprecated
   @Override
   public void _INVALID_damage(int amount, Entity source) {
      this.damage(amount, source);
   }

   @Deprecated
   @Override
   public int _INVALID_getHealth() {
      return NumberConversions.ceil(this.getHealth());
   }

   @Deprecated
   @Override
   public void _INVALID_setHealth(int health) {
      this.setHealth(health);
   }

   @Deprecated
   @Override
   public int _INVALID_getMaxHealth() {
      return NumberConversions.ceil(this.getMaxHealth());
   }

   @Deprecated
   @Override
   public void _INVALID_setMaxHealth(int health) {
      this.setMaxHealth(health);
   }
}
