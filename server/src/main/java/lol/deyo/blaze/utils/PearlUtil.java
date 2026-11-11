package lol.deyo.blaze.utils;

import lol.deyo.blaze.configs.PearlConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Gate;

public class PearlUtil {
   public static String direction(Location l) {
      double d = (l.getYaw() - 90.0F) % 360.0F;
      if (d < 0.0) {
         d += 360.0;
      }

      if (0.0 <= d && d < 22.5) {
         return "W";
      } else if (22.5 <= d && d < 67.5) {
         return "NW";
      } else if (67.5 <= d && d < 112.5) {
         return "N";
      } else if (112.5 <= d && d < 157.5) {
         return "NE";
      } else if (157.5 <= d && d < 202.5) {
         return "E";
      } else if (202.5 <= d && d < 247.5) {
         return "SE";
      } else if (247.5 <= d && d < 292.5) {
         return "S";
      } else if (292.5 <= d && d < 337.5) {
         return "SW";
      } else {
         return 337.5 <= d && d < 360.0 ? "W" : "";
      }
   }

   public static boolean distance(Block b, Location l, double d) {
      return b.getLocation().clone().add(0.5, 0.0, 0.5).distance(l) <= d;
   }

   public static boolean isStairs(Material material) {
      return material.name().contains("STAIRS");
   }

   public static boolean isPlantBukkit(Material type) {
      String blockName = type.toString();
      return type == Material.GRASS
         || blockName.contains("MUSHROOM")
         || type == Material.RED_ROSE
         || type == Material.CARROT
         || type == Material.POTATO
         || type == Material.WHEAT
         || type == Material.LONG_GRASS
         || type == Material.YELLOW_FLOWER
         || type == Material.SAPLING;
   }

   public static boolean isSlab(Material material) {
      String name = material.name();
      return (name.contains("STEP") || name.equals("STONE_SLAB2")) && !name.contains("DOUBLE");
   }

   public static boolean isOpenFenceGate(Block block) {
      BlockState state = block.getState();
      return state.getData() instanceof Gate && ((Gate)state.getData()).isOpen();
   }

   public static boolean isCritblock(Material material) {
      for (PearlConfig.PearlConfigSetting pearlSetting : PearlConfig.pearlSettings) {
         if (pearlSetting.aliases.isApplicable(material)) {
            return pearlSetting.critblock.getValue();
         }
      }

      return false;
   }

   public static boolean crossPearl(Material material) {
      for (PearlConfig.PearlConfigSetting pearlSetting : PearlConfig.pearlSettings) {
         if (pearlSetting.aliases.isApplicable(material)) {
            return pearlSetting.crosspearl.getValue();
         }
      }

      return false;
   }

   public static boolean diagonalPearl(Material material) {
      for (PearlConfig.PearlConfigSetting pearlSetting : PearlConfig.pearlSettings) {
         if (pearlSetting.aliases.isApplicable(material)) {
            return pearlSetting.diagonal.getValue();
         }
      }

      return false;
   }

   public static boolean thruEnabled(Material material) {
      for (PearlConfig.PearlConfigSetting pearlSetting : PearlConfig.pearlSettings) {
         if (pearlSetting.aliases.isApplicable(material)) {
            return pearlSetting.enabled.getValue();
         }
      }

      return false;
   }

   public static boolean isApplicable(Material material) {
      for (PearlConfig.PearlConfigSetting pearlSetting : PearlConfig.pearlSettings) {
         if (pearlSetting.aliases.isApplicable(material)) {
            return true;
         }
      }

      return false;
   }

   public static boolean customTransparent(Block b) {
      return !isTransparentBlock(b);
   }

   public static boolean isTransparentBlock(Block block) {
      Material type = block.getType();
      String name = type.name();
      return type.isTransparent()
         || block.isLiquid()
         || name.contains("FENCE")
         || isSlab(type)
         || isStairs(type)
         || type == Material.WEB
         || type == Material.PISTON_EXTENSION
         || type == Material.ANVIL
         || PearlConfig.BlockAliases.BEDS.isApplicable(type)
         || type == Material.DRAGON_EGG
         || type == Material.COBBLE_WALL
         || type == Material.ENDER_PORTAL_FRAME
         || type == Material.ENCHANTMENT_TABLE
         || name.contains("PLATE")
         || name.contains("SIGN")
         || name.contains("DAYLIGHT")
         || name.contains("BANNER")
         || name.contains("CAKE")
         || name.contains("DOOR")
         || name.contains("CHEST");
   }

   public static boolean isRiskyBlock(Block block) {
      Material type = block.getType();
      String name = type.name();
      return !type.isTransparent()
         && !block.isLiquid()
         && type != Material.WEB
         && !name.contains("FENCE")
         && !name.contains("PLATE")
         && !name.contains("WALL")
         && !name.contains("SIGN")
         && !name.contains("BANNER")
         && type != Material.PISTON_EXTENSION;
   }

   public static boolean isCritBlock(Block block) {
      Material type = block.getType();
      String name = type.name();
      return !type.isTransparent() && !block.isLiquid() && !name.contains("PLATE") && !name.contains("BANNER") && !name.contains("SIGN");
   }

   public static BlockFace getBehind(String d) {
      switch (d.hashCode()) {
         case 69:
            if (d.equals("E")) {
               return BlockFace.WEST;
            }
            break;
         case 78:
            if (d.equals("N")) {
               return BlockFace.SOUTH;
            }
            break;
         case 83:
            if (d.equals("S")) {
               return BlockFace.NORTH;
            }
            break;
         case 87:
            if (d.equals("W")) {
               return BlockFace.EAST;
            }
      }

      return null;
   }
}
