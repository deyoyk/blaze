package net.minecraft.server.v1_8_R3;

import java.util.Random;

class WorldGenRegistration$WorldGenJungleTemple$WorldGenJungleTemple$WorldGenJungleTemplePiece extends StructurePiece.StructurePieceBlockSelector {
   private WorldGenRegistration$WorldGenJungleTemple$WorldGenJungleTemple$WorldGenJungleTemplePiece() {
   }

   @Override
   public void a(Random random, int i, int j, int k, boolean flag) {
      if (random.nextFloat() < 0.4F) {
         this.a = Blocks.COBBLESTONE.getBlockData();
      } else {
         this.a = Blocks.MOSSY_COBBLESTONE.getBlockData();
      }
   }

   WorldGenRegistration$WorldGenJungleTemple$WorldGenJungleTemple$WorldGenJungleTemplePiece(
      StructurePiece.SyntheticClass_1 worldgenregistration_syntheticclass_1
   ) {
      this();
   }
}
