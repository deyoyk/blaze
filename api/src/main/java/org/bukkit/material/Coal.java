package org.bukkit.material;

public class Coal extends MaterialData {
   public Coal() {
      super(org.bukkit.Material.COAL);
   }

   public Coal(org.bukkit.CoalType type) {
      this();
      this.setType(type);
   }

   @Deprecated
   public Coal(int type) {
      super(type);
   }

   public Coal(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Coal(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Coal(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public org.bukkit.CoalType getType() {
      return org.bukkit.CoalType.getByData(this.getData());
   }

   public void setType(org.bukkit.CoalType type) {
      this.setData(type.getData());
   }

   @Override
   public String toString() {
      return this.getType() + " " + super.toString();
   }

   public Coal clone() {
      return (Coal)super.clone();
   }
}
