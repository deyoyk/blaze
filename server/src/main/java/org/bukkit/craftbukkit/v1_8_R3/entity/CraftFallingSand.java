package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingSand;

public class CraftFallingSand extends CraftEntity implements FallingSand {
   public CraftFallingSand(CraftServer server, EntityFallingBlock entity) {
      super(server, entity);
   }

   public EntityFallingBlock getHandle() {
      return (EntityFallingBlock)this.entity;
   }

   @Override
   public String toString() {
      return "CraftFallingSand";
   }

   @Override
   public EntityType getType() {
      return EntityType.FALLING_BLOCK;
   }

   @Override
   public org.bukkit.Material getMaterial() {
      return org.bukkit.Material.getMaterial(this.getBlockId());
   }

   @Override
   public int getBlockId() {
      return CraftMagicNumbers.getId(this.getHandle().getBlock().getBlock());
   }

   @Override
   public byte getBlockData() {
      return (byte)this.getHandle().getBlock().getBlock().toLegacyData(this.getHandle().getBlock());
   }

   @Override
   public boolean getDropItem() {
      return this.getHandle().dropItem;
   }

   @Override
   public void setDropItem(boolean drop) {
      this.getHandle().dropItem = drop;
   }

   @Override
   public boolean canHurtEntities() {
      return this.getHandle().hurtEntities;
   }

   @Override
   public void setHurtEntities(boolean hurtEntities) {
      this.getHandle().hurtEntities = hurtEntities;
   }

   @Override
   public org.bukkit.Location getSourceLoc() {
      return this.getHandle().sourceLoc;
   }
}
