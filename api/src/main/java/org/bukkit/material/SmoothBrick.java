package org.bukkit.material;

import java.util.ArrayList;
import java.util.List;

public class SmoothBrick extends TexturedMaterial {
   private static final List<org.bukkit.Material> textures = new ArrayList<>();

   public SmoothBrick() {
      super(org.bukkit.Material.SMOOTH_BRICK);
   }

   @Deprecated
   public SmoothBrick(int type) {
      super(type);
   }

   public SmoothBrick(org.bukkit.Material type) {
      super(textures.contains(type) ? org.bukkit.Material.SMOOTH_BRICK : type);
      if (textures.contains(type)) {
         this.setMaterial(type);
      }
   }

   @Deprecated
   public SmoothBrick(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public SmoothBrick(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public List<org.bukkit.Material> getTextures() {
      return textures;
   }

   public SmoothBrick clone() {
      return (SmoothBrick)super.clone();
   }

   static {
      textures.add(org.bukkit.Material.STONE);
      textures.add(org.bukkit.Material.MOSSY_COBBLESTONE);
      textures.add(org.bukkit.Material.COBBLESTONE);
      textures.add(org.bukkit.Material.SMOOTH_BRICK);
   }
}
