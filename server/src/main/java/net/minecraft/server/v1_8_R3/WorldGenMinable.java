package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenMinable extends WorldGenerator {
   private final IBlockData a;
   private final int b;
   private final Predicate<IBlockData> c;
   private boolean mustTouchAir;

   public WorldGenMinable(IBlockData iblockdata, int i) {
      this(iblockdata, i, BlockPredicate.a(Blocks.STONE));
   }

   public WorldGenMinable(IBlockData iblockdata, int i, Predicate<IBlockData> predicate) {
      this.a = iblockdata;
      this.b = i;
      this.c = predicate;
   }

   public WorldGenMinable(IBlockData block, int size, boolean mustTouchAir) {
      this(block, size);
      this.mustTouchAir = mustTouchAir;
   }

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      float f = random.nextFloat() * (float) Math.PI;
      double d0 = blockposition.getX() + 8 + MathHelper.sin(f) * this.b / 8.0F;
      double d1 = blockposition.getX() + 8 - MathHelper.sin(f) * this.b / 8.0F;
      double d2 = blockposition.getZ() + 8 + MathHelper.cos(f) * this.b / 8.0F;
      double d3 = blockposition.getZ() + 8 - MathHelper.cos(f) * this.b / 8.0F;
      double d4 = blockposition.getY() + random.nextInt(3) - 2;
      double d5 = blockposition.getY() + random.nextInt(3) - 2;
      boolean touchedAir = false;
      List<BlockPosition> blocks = this.mustTouchAir ? new ArrayList<>() : null;

      for (int i = 0; i < this.b; i++) {
         float f1 = (float)i / this.b;
         double d6 = d0 + (d1 - d0) * f1;
         double d7 = d4 + (d5 - d4) * f1;
         double d8 = d2 + (d3 - d2) * f1;
         double d9 = random.nextDouble() * this.b / 16.0;
         double d10 = (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0;
         double d11 = (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0;
         int j = MathHelper.floor(d6 - d10 / 2.0);
         int k = MathHelper.floor(d7 - d11 / 2.0);
         int l = MathHelper.floor(d8 - d10 / 2.0);
         int i1 = MathHelper.floor(d6 + d10 / 2.0);
         int j1 = MathHelper.floor(d7 + d11 / 2.0);
         int k1 = MathHelper.floor(d8 + d10 / 2.0);

         for (int l1 = j; l1 <= i1; l1++) {
            double d12 = (l1 + 0.5 - d6) / (d10 / 2.0);
            if (d12 * d12 < 1.0) {
               for (int i2 = k; i2 <= j1; i2++) {
                  double d13 = (i2 + 0.5 - d7) / (d11 / 2.0);
                  if (d12 * d12 + d13 * d13 < 1.0) {
                     for (int j2 = l; j2 <= k1; j2++) {
                        double d14 = (j2 + 0.5 - d8) / (d10 / 2.0);
                        if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0 && this.c.apply(world.getType(l1, i2, j2))) {
                           BlockPosition blockposition1 = new BlockPosition(l1, i2, j2);
                           if (this.mustTouchAir) {
                              blocks.add(blockposition1);
                              touchedAir |= world.getType(l1 + 1, i2, j2).getBlock() == Blocks.AIR
                                 || world.getType(l1 - 1, i2, j2).getBlock() == Blocks.AIR
                                 || world.getType(l1, i2 + 1, j2).getBlock() == Blocks.AIR
                                 || world.getType(l1, i2 - 1, j2).getBlock() == Blocks.AIR
                                 || world.getType(l1, i2, j2 + 1).getBlock() == Blocks.AIR
                                 || world.getType(l1, i2, j2 - 1).getBlock() == Blocks.AIR;
                           } else {
                              world.setTypeAndData(blockposition1, this.a, 2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.mustTouchAir && touchedAir) {
         for (BlockPosition pos : blocks) {
            world.setTypeAndData(pos, this.a, 2);
         }
      }

      return true;
   }
}
