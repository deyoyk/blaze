package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockPackedIce extends Block {
   public BlockPackedIce() {
      super(Material.SNOW_LAYER);
      this.frictionFactor = 0.98F;
      this.a(CreativeModeTab.b);
   }

   @Override
   public int a(Random var1) {
      return 0;
   }
}
