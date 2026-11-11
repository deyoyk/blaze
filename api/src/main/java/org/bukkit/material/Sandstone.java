package org.bukkit.material;

public class Sandstone extends MaterialData {
   public Sandstone() {
      super(org.bukkit.Material.SANDSTONE);
   }

   public Sandstone(org.bukkit.SandstoneType type) {
      this();
      this.setType(type);
   }

   @Deprecated
   public Sandstone(int type) {
      super(type);
   }

   public Sandstone(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Sandstone(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Sandstone(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public org.bukkit.SandstoneType getType() {
      return org.bukkit.SandstoneType.getByData(this.getData());
   }

   public void setType(org.bukkit.SandstoneType type) {
      this.setData(type.getData());
   }

   @Override
   public String toString() {
      return this.getType() + " " + super.toString();
   }

   public Sandstone clone() {
      return (Sandstone)super.clone();
   }
}
