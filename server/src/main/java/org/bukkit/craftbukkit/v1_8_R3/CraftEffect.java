package org.bukkit.craftbukkit.v1_8_R3;

import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.Potion;

public class CraftEffect {
   public static <T> int getDataValue(org.bukkit.Effect effect, T data) {
      int datavalue;
      switch (effect) {
         case RECORD_PLAY:
            Validate.isTrue(((org.bukkit.Material)data).isRecord(), "Invalid record type!");
            datavalue = ((org.bukkit.Material)data).getId();
            break;
         case SMOKE:
            switch ((BlockFace)data) {
               case NORTH:
                  return 7;
               case EAST:
                  return 3;
               case SOUTH:
                  return 1;
               case WEST:
                  return 5;
               case UP:
               case SELF:
                  return 4;
               case DOWN:
               case WEST_NORTH_WEST:
               case NORTH_NORTH_WEST:
               case NORTH_NORTH_EAST:
               case EAST_NORTH_EAST:
               case EAST_SOUTH_EAST:
               case SOUTH_SOUTH_EAST:
               case SOUTH_SOUTH_WEST:
               case WEST_SOUTH_WEST:
               default:
                  throw new IllegalArgumentException("Bad smoke direction!");
               case NORTH_EAST:
                  return 6;
               case NORTH_WEST:
                  return 8;
               case SOUTH_EAST:
                  return 0;
               case SOUTH_WEST:
                  return 2;
            }
         case STEP_SOUND:
            Validate.isTrue(((org.bukkit.Material)data).isBlock(), "Material is not a block!");
            datavalue = ((org.bukkit.Material)data).getId();
            break;
         case POTION_BREAK:
            datavalue = ((Potion)data).toDamageValue() & 63;
            break;
         case ITEM_BREAK:
            datavalue = ((org.bukkit.Material)data).getId();
            break;
         default:
            datavalue = 0;
      }

      return datavalue;
   }
}
