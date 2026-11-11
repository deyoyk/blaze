package org.bukkit.material;

public class Leaves extends MaterialData {
   public Leaves() {
      super(org.bukkit.Material.LEAVES);
   }

   public Leaves(org.bukkit.TreeSpecies species) {
      this();
      this.setSpecies(species);
   }

   @Deprecated
   public Leaves(int type) {
      super(type);
   }

   public Leaves(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public Leaves(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public Leaves(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public org.bukkit.TreeSpecies getSpecies() {
      return org.bukkit.TreeSpecies.getByData((byte)(this.getData() & 3));
   }

   public void setSpecies(org.bukkit.TreeSpecies species) {
      this.setData(species.getData());
   }

   @Override
   public String toString() {
      return this.getSpecies() + " " + super.toString();
   }

   public Leaves clone() {
      return (Leaves)super.clone();
   }
}
