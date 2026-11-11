package org.bukkit.material;

import java.util.ArrayList;
import java.util.List;

public class MonsterEggs extends TexturedMaterial {
   private static final List<org.bukkit.Material> textures = new ArrayList<>();

   public MonsterEggs() {
      super(org.bukkit.Material.MONSTER_EGGS);
   }

   @Deprecated
   public MonsterEggs(int type) {
      super(type);
   }

   public MonsterEggs(org.bukkit.Material type) {
      super(textures.contains(type) ? org.bukkit.Material.MONSTER_EGGS : type);
      if (textures.contains(type)) {
         this.setMaterial(type);
      }
   }

   @Deprecated
   public MonsterEggs(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public MonsterEggs(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   @Override
   public List<org.bukkit.Material> getTextures() {
      return textures;
   }

   public MonsterEggs clone() {
      return (MonsterEggs)super.clone();
   }

   static {
      textures.add(org.bukkit.Material.STONE);
      textures.add(org.bukkit.Material.COBBLESTONE);
      textures.add(org.bukkit.Material.SMOOTH_BRICK);
   }
}
