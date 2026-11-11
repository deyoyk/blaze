package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityMinecartAbstract;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Minecart;
import org.bukkit.material.MaterialData;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public abstract class CraftMinecart extends CraftVehicle implements Minecart {
   public CraftMinecart(CraftServer server, EntityMinecartAbstract entity) {
      super(server, entity);
   }

   @Override
   public void setDamage(double damage) {
      this.getHandle().setDamage((float)damage);
   }

   @Override
   public double getDamage() {
      return this.getHandle().getDamage();
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
   public boolean isSlowWhenEmpty() {
      return this.getHandle().slowWhenEmpty;
   }

   @Override
   public void setSlowWhenEmpty(boolean slow) {
      this.getHandle().slowWhenEmpty = slow;
   }

   @Override
   public Vector getFlyingVelocityMod() {
      return this.getHandle().getFlyingVelocityMod();
   }

   @Override
   public void setFlyingVelocityMod(Vector flying) {
      this.getHandle().setFlyingVelocityMod(flying);
   }

   @Override
   public Vector getDerailedVelocityMod() {
      return this.getHandle().getDerailedVelocityMod();
   }

   @Override
   public void setDerailedVelocityMod(Vector derailed) {
      this.getHandle().setDerailedVelocityMod(derailed);
   }

   public EntityMinecartAbstract getHandle() {
      return (EntityMinecartAbstract)this.entity;
   }

   @Deprecated
   @Override
   public void _INVALID_setDamage(int damage) {
      this.setDamage(damage);
   }

   @Deprecated
   @Override
   public int _INVALID_getDamage() {
      return NumberConversions.ceil(this.getDamage());
   }

   @Override
   public void setDisplayBlock(MaterialData material) {
      if (material != null) {
         IBlockData block = CraftMagicNumbers.getBlock(material.getItemTypeId()).fromLegacyData(material.getData());
         this.getHandle().setDisplayBlock(block);
      } else {
         this.getHandle().setDisplayBlock(Blocks.AIR.getBlockData());
         this.getHandle().a(false);
      }
   }

   @Override
   public MaterialData getDisplayBlock() {
      IBlockData blockData = this.getHandle().getDisplayBlock();
      return CraftMagicNumbers.getMaterial(blockData.getBlock()).getNewData((byte)blockData.getBlock().toLegacyData(blockData));
   }

   @Override
   public void setDisplayBlockOffset(int offset) {
      this.getHandle().SetDisplayBlockOffset(offset);
   }

   @Override
   public int getDisplayBlockOffset() {
      return this.getHandle().getDisplayBlockOffset();
   }
}
