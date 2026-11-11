package org.bukkit.material;

import java.util.ArrayList;
import java.util.List;

public class Step extends TexturedMaterial {
   private static final List<org.bukkit.Material> textures = new ArrayList<>();

   public Step() {
      super(org.bukkit.Material.STEP);
   }

   @Deprecated
   public Step(int type) {
      super(type);
   }

   public Step(org.bukkit.Material type) {
      super(textures.contains(type) ? org.bukkit.Material.STEP : type);
      if (textures.contains(type)) {
         this.setMaterial(type);
      }
   }

   @Deprecated
   public Step(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Step(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public List<org.bukkit.Material> getTextures() {
      return textures;
   }

   public boolean isInverted() {
      return (this.getData() & 8) != 0;
   }

   public void setInverted(boolean inv) {
      int dat = this.getData() & 7;
      if (inv) {
         dat |= 8;
      }

      this.setData((byte)dat);
   }

   @Deprecated
   @Override
   protected int getTextureIndex() {
      return this.getData() & 7;
   }

   @Deprecated
   @Override
   protected void setTextureIndex(int idx) {
      this.setData((byte)(this.getData() & 8 | idx));
   }

   public Step clone() {
      return (Step)super.clone();
   }

   @Override
   public String toString() {
      return super.toString() + (this.isInverted() ? "inverted" : "");
   }

   static {
      textures.add(org.bukkit.Material.STONE);
      textures.add(org.bukkit.Material.SANDSTONE);
      textures.add(org.bukkit.Material.WOOD);
      textures.add(org.bukkit.Material.COBBLESTONE);
      textures.add(org.bukkit.Material.BRICK);
      textures.add(org.bukkit.Material.SMOOTH_BRICK);
      textures.add(org.bukkit.Material.NETHER_BRICK);
      textures.add(org.bukkit.Material.QUARTZ_BLOCK);
   }
}
