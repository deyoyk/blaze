package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;

public class CraftItemFrame extends CraftHanging implements ItemFrame {
   public CraftItemFrame(CraftServer server, EntityItemFrame entity) {
      super(server, entity);
   }

   @Override
   public boolean setFacingDirection(BlockFace face, boolean force) {
      if (!super.setFacingDirection(face, force)) {
         return false;
      } else {
         this.update();
         return true;
      }
   }

   private void update() {
      EntityItemFrame old = this.getHandle();
      WorldServer world = ((CraftWorld)this.getWorld()).getHandle();
      BlockPosition position = old.getBlockPosition();
      EnumDirection direction = old.getDirection();
      ItemStack item = old.getItem() != null ? old.getItem().cloneItemStack() : null;
      old.die();
      EntityItemFrame frame = new EntityItemFrame(world, position, direction);
      frame.setItem(item);
      world.addEntity(frame);
      this.entity = frame;
   }

   @Override
   public void setItem(org.bukkit.inventory.ItemStack item) {
      if (item != null && item.getTypeId() != 0) {
         this.getHandle().setItem(CraftItemStack.asNMSCopy(item));
      } else {
         this.getHandle().setItem(null);
      }
   }

   @Override
   public org.bukkit.inventory.ItemStack getItem() {
      return CraftItemStack.asBukkitCopy(this.getHandle().getItem());
   }

   @Override
   public org.bukkit.Rotation getRotation() {
      return this.toBukkitRotation(this.getHandle().getRotation());
   }

   org.bukkit.Rotation toBukkitRotation(int value) {
      switch (value) {
         case 0:
            return org.bukkit.Rotation.NONE;
         case 1:
            return org.bukkit.Rotation.CLOCKWISE_45;
         case 2:
            return org.bukkit.Rotation.CLOCKWISE;
         case 3:
            return org.bukkit.Rotation.CLOCKWISE_135;
         case 4:
            return org.bukkit.Rotation.FLIPPED;
         case 5:
            return org.bukkit.Rotation.FLIPPED_45;
         case 6:
            return org.bukkit.Rotation.COUNTER_CLOCKWISE;
         case 7:
            return org.bukkit.Rotation.COUNTER_CLOCKWISE_45;
         default:
            throw new AssertionError("Unknown rotation " + value + " for " + this.getHandle());
      }
   }

   @Override
   public void setRotation(org.bukkit.Rotation rotation) {
      Validate.notNull(rotation, "Rotation cannot be null");
      this.getHandle().setRotation(toInteger(rotation));
   }

   static int toInteger(org.bukkit.Rotation rotation) {
      switch (rotation) {
         case NONE:
            return 0;
         case CLOCKWISE_45:
            return 1;
         case CLOCKWISE:
            return 2;
         case CLOCKWISE_135:
            return 3;
         case FLIPPED:
            return 4;
         case FLIPPED_45:
            return 5;
         case COUNTER_CLOCKWISE:
            return 6;
         case COUNTER_CLOCKWISE_45:
            return 7;
         default:
            throw new IllegalArgumentException(rotation + " is not applicable to an ItemFrame");
      }
   }

   public EntityItemFrame getHandle() {
      return (EntityItemFrame)this.entity;
   }

   @Override
   public String toString() {
      return "CraftItemFrame{item=" + this.getItem() + ", rotation=" + this.getRotation() + "}";
   }

   @Override
   public EntityType getType() {
      return EntityType.ITEM_FRAME;
   }
}
