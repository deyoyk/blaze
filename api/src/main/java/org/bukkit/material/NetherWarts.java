package org.bukkit.material;

public class NetherWarts extends MaterialData {
   public NetherWarts() {
      super(org.bukkit.Material.NETHER_WARTS);
   }

   public NetherWarts(org.bukkit.NetherWartsState state) {
      this();
      this.setState(state);
   }

   @Deprecated
   public NetherWarts(int type) {
      super(type);
   }

   public NetherWarts(org.bukkit.Material type) {
      super(type);
   }

   @Deprecated
   public NetherWarts(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public NetherWarts(org.bukkit.Material type, byte data) {
      super(type, data);
   }

   public org.bukkit.NetherWartsState getState() {
      switch (this.getData()) {
         case 0:
            return org.bukkit.NetherWartsState.SEEDED;
         case 1:
            return org.bukkit.NetherWartsState.STAGE_ONE;
         case 2:
            return org.bukkit.NetherWartsState.STAGE_TWO;
         default:
            return org.bukkit.NetherWartsState.RIPE;
      }
   }

   public void setState(org.bukkit.NetherWartsState state) {
      switch (state) {
         case SEEDED:
            this.setData((byte)0);
            return;
         case STAGE_ONE:
            this.setData((byte)1);
            return;
         case STAGE_TWO:
            this.setData((byte)2);
            return;
         case RIPE:
            this.setData((byte)3);
            return;
      }
   }

   @Override
   public String toString() {
      return this.getState() + " " + super.toString();
   }

   public NetherWarts clone() {
      return (NetherWarts)super.clone();
   }
}
