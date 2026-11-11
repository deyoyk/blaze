package net.minecraft.server.v1_8_R3;

public class MapIcon {
   private byte type;
   private byte x;
   private byte y;
   private byte rotation;

   public MapIcon(byte var1, byte var2, byte var3, byte var4) {
      this.type = var1;
      this.x = var2;
      this.y = var3;
      this.rotation = var4;
   }

   public MapIcon(MapIcon var1) {
      this.type = var1.type;
      this.x = var1.x;
      this.y = var1.y;
      this.rotation = var1.rotation;
   }

   public byte getType() {
      return this.type;
   }

   public byte getX() {
      return this.x;
   }

   public byte getY() {
      return this.y;
   }

   public byte getRotation() {
      return this.rotation;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MapIcon)) {
         return false;
      } else {
         MapIcon var2 = (MapIcon)var1;
         if (this.type != var2.type) {
            return false;
         } else if (this.rotation != var2.rotation) {
            return false;
         } else {
            return this.x != var2.x ? false : this.y == var2.y;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.type;
      var1 = 31 * var1 + this.x;
      var1 = 31 * var1 + this.y;
      return 31 * var1 + this.rotation;
   }
}
