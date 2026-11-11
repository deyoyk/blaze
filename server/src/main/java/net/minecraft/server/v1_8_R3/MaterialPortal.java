package net.minecraft.server.v1_8_R3;

public class MaterialPortal extends Material {
   public MaterialPortal(MaterialMapColor var1) {
      super(var1);
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
