package net.minecraft.server.v1_8_R3;

public class MaterialDecoration extends Material {
   public MaterialDecoration(MaterialMapColor var1) {
      super(var1);
      this.p();
   }

   @Override
   public boolean isBuildable() {
      return false;
   }

   @Override
   public boolean blocksLight() {
      return false;
   }

   @Override
   public boolean isSolid() {
      return false;
   }
}
