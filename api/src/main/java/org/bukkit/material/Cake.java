package org.bukkit.material;

public class Cake extends MaterialData {
   public Cake() {
      super(org.bukkit.Material.CAKE_BLOCK);
   }

   @Deprecated
   public Cake(int type) {
      super(type);
   }

   public Cake(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Cake(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Cake(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public int getSlicesEaten() {
      return this.getData();
   }

   public int getSlicesRemaining() {
      return 6 - this.getData();
   }

   public void setSlicesEaten(int n) {
      if (n < 6) {
         this.setData((byte)n);
      }
   }

   public void setSlicesRemaining(int n) {
      if (n > 6) {
         n = 6;
      }

      this.setData((byte)(6 - n));
   }

   @Override
   public String toString() {
      return super.toString() + " " + this.getSlicesEaten() + "/" + this.getSlicesRemaining() + " slices eaten/remaining";
   }

   public Cake clone() {
      return (Cake)super.clone();
   }
}
