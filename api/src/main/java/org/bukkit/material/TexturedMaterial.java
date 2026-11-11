package org.bukkit.material;

import java.util.List;

public abstract class TexturedMaterial extends MaterialData {
   public TexturedMaterial(org.bukkit.Material m) {
      super(m);
   }

   @Deprecated
   public TexturedMaterial(int type) {
      super(type);
   }

   @Deprecated
   public TexturedMaterial(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public TexturedMaterial(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public abstract List<org.bukkit.Material> getTextures();

   public org.bukkit.Material getMaterial() {
      int n = this.getTextureIndex();
      if (n > this.getTextures().size() - 1) {
         n = 0;
      }

      return this.getTextures().get(n);
   }

   public void setMaterial(org.bukkit.Material material) {
      if (this.getTextures().contains(material)) {
         this.setTextureIndex(this.getTextures().indexOf(material));
      } else {
         this.setTextureIndex(0);
      }
   }

   @Deprecated
   protected int getTextureIndex() {
      return this.getData();
   }

   @Deprecated
   protected void setTextureIndex(int idx) {
      this.setData((byte)idx);
   }

   @Override
   public String toString() {
      return this.getMaterial() + " " + super.toString();
   }

   public TexturedMaterial clone() {
      return (TexturedMaterial)super.clone();
   }
}
