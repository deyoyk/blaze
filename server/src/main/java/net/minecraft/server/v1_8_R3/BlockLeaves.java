package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.Random;
import org.bukkit.event.block.LeavesDecayEvent;

public abstract class BlockLeaves extends BlockTransparent {
   public static final BlockStateBoolean DECAYABLE = BlockStateBoolean.of("decayable");
   public static final BlockStateBoolean CHECK_DECAY = BlockStateBoolean.of("check_decay");
   int[] N;

   public BlockLeaves() {
      super(Material.LEAVES, false);
      this.a(true);
      this.a(CreativeModeTab.c);
      this.c(0.2F);
      this.e(1);
      this.a(h);
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if ((Boolean)BlazeConfig.leafDecaying.getValue()) {
         byte b0 = 1;
         int i = b0 + 1;
         int j = blockposition.getX();
         int k = blockposition.getY();
         int l = blockposition.getZ();
         if (world.areChunksLoadedBetween(j - i, k - i, l - i, j + i, k + i, l + i)) {
            for (int i1 = -b0; i1 <= b0; i1++) {
               for (int j1 = -b0; j1 <= b0; j1++) {
                  for (int k1 = -b0; k1 <= b0; k1++) {
                     BlockPosition blockposition1 = blockposition.a(i1, j1, k1);
                     IBlockData iblockdata1 = world.getType(blockposition1);
                     if (iblockdata1.getBlock().getMaterial() == Material.LEAVES && !iblockdata1.get(CHECK_DECAY)) {
                        world.setTypeAndData(blockposition1, iblockdata1.set(CHECK_DECAY, true), 4);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if ((Boolean)BlazeConfig.leafDecaying.getValue() && iblockdata.get(CHECK_DECAY) && iblockdata.get(DECAYABLE)) {
         int j = blockposition.getX();
         int k = blockposition.getY();
         int l = blockposition.getZ();
         if (this.N == null) {
            this.N = new int[32768];
         }

         if (world.areChunksLoadedBetween(j - 5, k - 5, l - 5, j + 5, k + 5, l + 5)) {
            for (int k1 = -4; k1 <= 4; k1++) {
               for (int l1 = -4; l1 <= 4; l1++) {
                  for (int i2 = -4; i2 <= 4; i2++) {
                     Block block = world.getType(j + k1, k + l1, l + i2).getBlock();
                     int i1 = (k1 + 16) * 1024 + (l1 + 16) * 32 + i2 + 16;
                     if (block != Blocks.LOG && block != Blocks.LOG2) {
                        if (block.getMaterial() == Material.LEAVES) {
                           this.N[i1] = -2;
                        } else {
                           this.N[i1] = -1;
                        }
                     } else {
                        this.N[i1] = 0;
                     }
                  }
               }
            }

            for (int var23 = 1; var23 <= 4; var23++) {
               for (int l1 = -4; l1 <= 4; l1++) {
                  for (int i2x = -4; i2x <= 4; i2x++) {
                     for (int j2 = -4; j2 <= 4; j2++) {
                        int x = l1 + 16;
                        int bitX = x * 1024;
                        int y = i2x + 16;
                        int bitY = y * 32;
                        int bitZ = j2 + 16;
                        if (this.N[bitX + bitY + bitZ] == var23 - 1) {
                           int i1 = (x - 1) * 1024 + bitY + bitZ;
                           if (this.N[i1] == -2) {
                              this.N[i1] = var23;
                           }

                           int i3 = (x + 1) * 1024 + bitY + bitZ;
                           if (this.N[i3] == -2) {
                              this.N[i3] = var23;
                           }

                           int i4 = bitX + (y - 1) * 32 + bitZ;
                           if (this.N[i4] == -2) {
                              this.N[i4] = var23;
                           }

                           int i5 = bitX + (y + 1) * 32 + bitZ;
                           if (this.N[i5] == -2) {
                              this.N[i5] = var23;
                           }

                           int i6 = bitX + bitY + (bitZ - 1);
                           if (this.N[i6] == -2) {
                              this.N[i6] = var23;
                           }

                           int i7 = bitX + bitY + bitZ + 1;
                           if (this.N[i7] == -2) {
                              this.N[i7] = var23;
                           }
                        }
                     }
                  }
               }
            }
         }

         int k2 = this.N[16912];
         if (k2 >= 0) {
            world.setTypeAndData(blockposition, iblockdata.set(CHECK_DECAY, false), 4);
         } else {
            this.e(world, blockposition);
         }
      }
   }

   private void e(World world, BlockPosition blockposition) {
      LeavesDecayEvent event = new LeavesDecayEvent(world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
      world.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled() && world.getType(blockposition).getBlock() == this) {
         this.b(world, blockposition, world.getType(blockposition), 0);
         world.setAir(blockposition);
      }
   }

   @Override
   public int a(Random random) {
      return random.nextInt(20) == 0 ? 1 : 0;
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Item.getItemOf(Blocks.SAPLING);
   }

   @Override
   public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
      int j = this.d(iblockdata);
      if (i > 0) {
         j -= 2 << i;
         if (j < 10) {
            j = 10;
         }
      }

      if (world.random.nextInt(j) == 0) {
         this.a(world, blockposition, new ItemStack(this.getDropType(iblockdata, world.random, i), 1, this.getDropData(iblockdata)));
      }

      j = 200;
      if (i > 0) {
         j -= 10 << i;
         if (j < 40) {
            j = 40;
         }
      }

      this.a(world, blockposition, iblockdata, j);
   }

   protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
   }

   protected int d(IBlockData iblockdata) {
      return 20;
   }

   @Override
   public boolean c() {
      return !this.R;
   }

   @Override
   public boolean w() {
      return false;
   }

   public abstract BlockWood.EnumLogVariant b(int var1);
}
