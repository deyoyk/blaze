package org.bukkit.material;

public class FlowerPot extends MaterialData {
   public FlowerPot() {
      super(org.bukkit.Material.FLOWER_POT);
   }

   @Deprecated
   public FlowerPot(int type) {
      super(type);
   }

   public FlowerPot(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public FlowerPot(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public FlowerPot(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public MaterialData getContents() {
      switch (this.getData()) {
         case 1:
            return new MaterialData(org.bukkit.Material.RED_ROSE);
         case 2:
            return new MaterialData(org.bukkit.Material.YELLOW_FLOWER);
         case 3:
            return new Tree(org.bukkit.TreeSpecies.GENERIC);
         case 4:
            return new Tree(org.bukkit.TreeSpecies.REDWOOD);
         case 5:
            return new Tree(org.bukkit.TreeSpecies.BIRCH);
         case 6:
            return new Tree(org.bukkit.TreeSpecies.JUNGLE);
         case 7:
            return new MaterialData(org.bukkit.Material.RED_MUSHROOM);
         case 8:
            return new MaterialData(org.bukkit.Material.BROWN_MUSHROOM);
         case 9:
            return new MaterialData(org.bukkit.Material.CACTUS);
         case 10:
            return new MaterialData(org.bukkit.Material.DEAD_BUSH);
         case 11:
            return new LongGrass(org.bukkit.GrassSpecies.FERN_LIKE);
         default:
            return null;
      }
   }

   public void setContents(MaterialData materialData) {
      org.bukkit.Material mat = materialData.getItemType();
      if (mat == org.bukkit.Material.RED_ROSE) {
         this.setData((byte)1);
      } else if (mat == org.bukkit.Material.YELLOW_FLOWER) {
         this.setData((byte)2);
      } else if (mat == org.bukkit.Material.RED_MUSHROOM) {
         this.setData((byte)7);
      } else if (mat == org.bukkit.Material.BROWN_MUSHROOM) {
         this.setData((byte)8);
      } else if (mat == org.bukkit.Material.CACTUS) {
         this.setData((byte)9);
      } else if (mat == org.bukkit.Material.DEAD_BUSH) {
         this.setData((byte)10);
      } else if (mat == org.bukkit.Material.SAPLING) {
         org.bukkit.TreeSpecies species = ((Tree)materialData).getSpecies();
         if (species == org.bukkit.TreeSpecies.GENERIC) {
            this.setData((byte)3);
         } else if (species == org.bukkit.TreeSpecies.REDWOOD) {
            this.setData((byte)4);
         } else if (species == org.bukkit.TreeSpecies.BIRCH) {
            this.setData((byte)5);
         } else {
            this.setData((byte)6);
         }
      } else if (mat == org.bukkit.Material.LONG_GRASS) {
         org.bukkit.GrassSpecies species = ((LongGrass)materialData).getSpecies();
         if (species == org.bukkit.GrassSpecies.FERN_LIKE) {
            this.setData((byte)11);
         }
      }
   }

   @Override
   public String toString() {
      return super.toString() + " containing " + this.getContents();
   }

   public FlowerPot clone() {
      return (FlowerPot)super.clone();
   }
}
