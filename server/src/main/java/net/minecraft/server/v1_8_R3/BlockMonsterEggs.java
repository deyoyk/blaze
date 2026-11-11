package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class BlockMonsterEggs extends Block {
   public static final BlockStateEnum<BlockMonsterEggs.EnumMonsterEggVarient> VARIANT = BlockStateEnum.of(
      "variant", BlockMonsterEggs.EnumMonsterEggVarient.class
   );

   public BlockMonsterEggs() {
      super(Material.CLAY);
      this.j(this.blockStateList.getBlockData().set(VARIANT, BlockMonsterEggs.EnumMonsterEggVarient.STONE));
      this.c(0.0F);
      this.a(CreativeModeTab.c);
   }

   @Override
   public int a(Random random) {
      return 0;
   }

   public static boolean d(IBlockData iblockdata) {
      Block block = iblockdata.getBlock();
      return iblockdata == Blocks.STONE.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.STONE)
         || block == Blocks.COBBLESTONE
         || block == Blocks.STONEBRICK;
   }

   @Override
   protected ItemStack i(IBlockData iblockdata) {
      switch (BlockMonsterEggs.SyntheticClass_1.a[iblockdata.get(VARIANT).ordinal()]) {
         case 1:
            return new ItemStack(Blocks.COBBLESTONE);
         case 2:
            return new ItemStack(Blocks.STONEBRICK);
         case 3:
            return new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.EnumStonebrickType.MOSSY.a());
         case 4:
            return new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.EnumStonebrickType.CRACKED.a());
         case 5:
            return new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.EnumStonebrickType.CHISELED.a());
         default:
            return new ItemStack(Blocks.STONE);
      }
   }

   @Override
   public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
      if (world.getGameRules().getBoolean("doTileDrops")) {
         EntitySilverfish entitysilverfish = new EntitySilverfish(world);
         entitysilverfish.setPositionRotation(blockposition.getX() + 0.5, blockposition.getY(), blockposition.getZ() + 0.5, 0.0F, 0.0F);
         world.addEntity(entitysilverfish, SpawnReason.SILVERFISH_BLOCK);
         entitysilverfish.y();
      }
   }

   @Override
   public int getDropData(World world, BlockPosition blockposition) {
      IBlockData iblockdata = world.getType(blockposition);
      return iblockdata.getBlock().toLegacyData(iblockdata);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(VARIANT, BlockMonsterEggs.EnumMonsterEggVarient.a(i));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, VARIANT);
   }

   public static enum EnumMonsterEggVarient implements INamable {
      STONE(0, "stone") {
         @Override
         public IBlockData d() {
            return Blocks.STONE.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.STONE);
         }
      },
      COBBLESTONE(1, "cobblestone", "cobble") {
         @Override
         public IBlockData d() {
            return Blocks.COBBLESTONE.getBlockData();
         }
      },
      STONEBRICK(2, "stone_brick", "brick") {
         @Override
         public IBlockData d() {
            return Blocks.STONEBRICK.getBlockData().set(BlockSmoothBrick.VARIANT, BlockSmoothBrick.EnumStonebrickType.DEFAULT);
         }
      },
      MOSSY_STONEBRICK(3, "mossy_brick", "mossybrick") {
         @Override
         public IBlockData d() {
            return Blocks.STONEBRICK.getBlockData().set(BlockSmoothBrick.VARIANT, BlockSmoothBrick.EnumStonebrickType.MOSSY);
         }
      },
      CRACKED_STONEBRICK(4, "cracked_brick", "crackedbrick") {
         @Override
         public IBlockData d() {
            return Blocks.STONEBRICK.getBlockData().set(BlockSmoothBrick.VARIANT, BlockSmoothBrick.EnumStonebrickType.CRACKED);
         }
      },
      CHISELED_STONEBRICK(5, "chiseled_brick", "chiseledbrick") {
         @Override
         public IBlockData d() {
            return Blocks.STONEBRICK.getBlockData().set(BlockSmoothBrick.VARIANT, BlockSmoothBrick.EnumStonebrickType.CHISELED);
         }
      };

      private static final BlockMonsterEggs.EnumMonsterEggVarient[] g = new BlockMonsterEggs.EnumMonsterEggVarient[values().length];
      private final int h;
      private final String i;
      private final String j;

      static {
         BlockMonsterEggs.EnumMonsterEggVarient[] var3;
         for (BlockMonsterEggs.EnumMonsterEggVarient blockmonstereggs_enummonstereggvarient : var3 = values()) {
            g[blockmonstereggs_enummonstereggvarient.a()] = blockmonstereggs_enummonstereggvarient;
         }
      }

      private EnumMonsterEggVarient(int i, String s) {
         this(i, s, s);
      }

      private EnumMonsterEggVarient(int i, String s, String s1) {
         this.h = i;
         this.i = s;
         this.j = s1;
      }

      public int a() {
         return this.h;
      }

      @Override
      public String toString() {
         return this.i;
      }

      public static BlockMonsterEggs.EnumMonsterEggVarient a(int i) {
         if (i < 0 || i >= g.length) {
            i = 0;
         }

         return g[i];
      }

      @Override
      public String getName() {
         return this.i;
      }

      public String c() {
         return this.j;
      }

      public abstract IBlockData d();

      public static BlockMonsterEggs.EnumMonsterEggVarient a(IBlockData iblockdata) {
         BlockMonsterEggs.EnumMonsterEggVarient[] var4;
         for (BlockMonsterEggs.EnumMonsterEggVarient blockmonstereggs_enummonstereggvarient : var4 = values()) {
            if (iblockdata == blockmonstereggs_enummonstereggvarient.d()) {
               return blockmonstereggs_enummonstereggvarient;
            }
         }

         return STONE;
      }

      private EnumMonsterEggVarient(int i, String s, BlockMonsterEggs.SyntheticClass_1 blockmonstereggs_syntheticclass_1) {
         this(i, s);
      }

      private EnumMonsterEggVarient(int i, String s, String s1, BlockMonsterEggs.SyntheticClass_1 blockmonstereggs_syntheticclass_1) {
         this(i, s, s1);
      }
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[BlockMonsterEggs.EnumMonsterEggVarient.values().length];

      static {
         try {
            a[BlockMonsterEggs.EnumMonsterEggVarient.COBBLESTONE.ordinal()] = 1;
         } catch (NoSuchFieldError var5) {
         }

         try {
            a[BlockMonsterEggs.EnumMonsterEggVarient.STONEBRICK.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[BlockMonsterEggs.EnumMonsterEggVarient.MOSSY_STONEBRICK.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[BlockMonsterEggs.EnumMonsterEggVarient.CRACKED_STONEBRICK.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[BlockMonsterEggs.EnumMonsterEggVarient.CHISELED_STONEBRICK.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
