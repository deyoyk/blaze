package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityHanging;
import net.minecraft.server.v1_8_R3.EnumDirection;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;

public class CraftHanging extends CraftEntity implements Hanging {
   public CraftHanging(CraftServer server, EntityHanging entity) {
      super(server, entity);
   }

   @Override
   public BlockFace getAttachedFace() {
      return this.getFacing().getOppositeFace();
   }

   @Override
   public void setFacingDirection(BlockFace face) {
      this.setFacingDirection(face, false);
   }

   @Override
   public boolean setFacingDirection(BlockFace face, boolean force) {
      EntityHanging hanging = this.getHandle();
      EnumDirection dir = hanging.direction;
      switch (face) {
         case NORTH:
            this.getHandle().setDirection(EnumDirection.NORTH);
            break;
         case EAST:
            this.getHandle().setDirection(EnumDirection.EAST);
            break;
         case SOUTH:
         default:
            this.getHandle().setDirection(EnumDirection.SOUTH);
            break;
         case WEST:
            this.getHandle().setDirection(EnumDirection.WEST);
      }

      if (!force && !hanging.survives()) {
         hanging.setDirection(dir);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public BlockFace getFacing() {
      EnumDirection direction = this.getHandle().direction;
      if (direction == null) {
         return BlockFace.SELF;
      } else {
         switch (direction) {
            case NORTH:
               return BlockFace.NORTH;
            case SOUTH:
            default:
               return BlockFace.SOUTH;
            case WEST:
               return BlockFace.WEST;
            case EAST:
               return BlockFace.EAST;
         }
      }
   }

   public EntityHanging getHandle() {
      return (EntityHanging)this.entity;
   }

   @Override
   public String toString() {
      return "CraftHanging";
   }

   @Override
   public EntityType getType() {
      return EntityType.UNKNOWN;
   }
}
