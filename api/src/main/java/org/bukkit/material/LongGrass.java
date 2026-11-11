package org.bukkit.material;

public class LongGrass extends MaterialData {
   public LongGrass() {
      super(org.bukkit.Material.LONG_GRASS);
   }

   public LongGrass(org.bukkit.GrassSpecies species) {
      this();
      this.setSpecies(species);
   }

   @Deprecated
   public LongGrass(int type) {
      super(type);
   }

   public LongGrass(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public LongGrass(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public LongGrass(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public org.bukkit.GrassSpecies getSpecies() {
      return org.bukkit.GrassSpecies.getByData(this.getData());
   }

   public void setSpecies(org.bukkit.GrassSpecies species) {
      this.setData(species.getData());
   }

   @Override
   public String toString() {
      return this.getSpecies() + " " + super.toString();
   }

   public LongGrass clone() {
      return (LongGrass)super.clone();
   }
}
