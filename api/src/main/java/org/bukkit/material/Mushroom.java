package org.bukkit.material;

import java.util.EnumSet;
import java.util.Set;
import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockFace;

public class Mushroom extends MaterialData {
   private static final byte SHROOM_NONE = 0;
   private static final byte SHROOM_STEM = 10;
   private static final byte NORTH_LIMIT = 4;
   private static final byte SOUTH_LIMIT = 6;
   private static final byte EAST_WEST_LIMIT = 3;
   private static final byte EAST_REMAINDER = 0;
   private static final byte WEST_REMAINDER = 1;
   private static final byte NORTH_SOUTH_MOD = 3;
   private static final byte EAST_WEST_MOD = 1;

   public Mushroom(org.bukkit.Material shroom) {
      super(shroom);
      Validate.isTrue(shroom == org.bukkit.Material.HUGE_MUSHROOM_1 || shroom == org.bukkit.Material.HUGE_MUSHROOM_2, "Not a mushroom!");
   }

   @Deprecated
   public Mushroom(org.bukkit.Material shroom, byte data) {
      super(shroom, data);
      Validate.isTrue(shroom == org.bukkit.Material.HUGE_MUSHROOM_1 || shroom == org.bukkit.Material.HUGE_MUSHROOM_2, "Not a mushroom!");
   }

   @Deprecated
   public Mushroom(int type, byte data) {
      super(type, data);
      Validate.isTrue(type == org.bukkit.Material.HUGE_MUSHROOM_1.getId() || type == org.bukkit.Material.HUGE_MUSHROOM_2.getId(), "Not a mushroom!");
   }

   public boolean isStem() {
      return this.getData() == 10;
   }

   public void setStem() {
      this.setData((byte)10);
   }

   public boolean isFacePainted(BlockFace face) {
      byte data = this.getData();
      if (data != 0 && data != 10) {
         switch (face) {
            case WEST:
               return data < 4;
            case EAST:
               return data > 6;
            case NORTH:
               return data % 3 == 0;
            case SOUTH:
               return data % 3 == 1;
            case UP:
               return true;
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   public void setFacePainted(BlockFace face, boolean painted) {
      if (painted != this.isFacePainted(face)) {
         byte data = this.getData();
         if (data == 10) {
            data = 5;
         }

         switch (face) {
            case WEST:
               if (painted) {
                  data = (byte)(data - 3);
               } else {
                  data = (byte)(data + 3);
               }
               break;
            case EAST:
               if (painted) {
                  data = (byte)(data + 3);
               } else {
                  data = (byte)(data - 3);
               }
               break;
            case NORTH:
               if (painted) {
                  data++;
               } else {
                  data--;
               }
               break;
            case SOUTH:
               if (painted) {
                  data--;
               } else {
                  data++;
               }
               break;
            case UP:
               if (!painted) {
                  data = 0;
               }
               break;
            default:
               throw new IllegalArgumentException("Can't paint that face of a mushroom!");
         }

         this.setData(data);
      }
   }

   public Set<BlockFace> getPaintedFaces() {
      EnumSet<BlockFace> faces = EnumSet.noneOf(BlockFace.class);
      if (this.isFacePainted(BlockFace.WEST)) {
         faces.add(BlockFace.WEST);
      }

      if (this.isFacePainted(BlockFace.NORTH)) {
         faces.add(BlockFace.NORTH);
      }

      if (this.isFacePainted(BlockFace.SOUTH)) {
         faces.add(BlockFace.SOUTH);
      }

      if (this.isFacePainted(BlockFace.EAST)) {
         faces.add(BlockFace.EAST);
      }

      if (this.isFacePainted(BlockFace.UP)) {
         faces.add(BlockFace.UP);
      }

      return faces;
   }

   @Override
   public String toString() {
      return org.bukkit.Material.getMaterial(this.getItemTypeId()).toString() + (this.isStem() ? "{STEM}" : this.getPaintedFaces());
   }

   public Mushroom clone() {
      return (Mushroom)super.clone();
   }
}
