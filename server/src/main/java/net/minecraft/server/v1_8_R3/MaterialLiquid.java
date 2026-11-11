package net.minecraft.server.v1_8_R3;

public class MaterialLiquid extends Material {
   public MaterialLiquid(MaterialMapColor var1) {
      super(var1);
      this.i();
      this.n();
   }

   @Override
   public boolean isLiquid() {
      return true;
   }

   @Override
   public boolean isSolid() {
      return false;
   }

   @Override
   public boolean isBuildable() {
      return false;
   }
}
