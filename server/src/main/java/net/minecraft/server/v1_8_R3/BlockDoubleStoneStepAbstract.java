package net.minecraft.server.v1_8_R3;

import java.util.Random;

public abstract class BlockDoubleStoneStepAbstract extends BlockStepAbstract {
   public static final BlockStateBoolean SEAMLESS = BlockStateBoolean.of("seamless");
   public static final BlockStateEnum<BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant> VARIANT = BlockStateEnum.of(
      "variant", BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant.class
   );

   public BlockDoubleStoneStepAbstract() {
      super(Material.STONE);
      IBlockData iblockdata = this.blockStateList.getBlockData();
      if (this.l()) {
         iblockdata = iblockdata.set(SEAMLESS, Boolean.FALSE);
      } else {
         iblockdata = iblockdata.set(HALF, BlockStepAbstract.EnumSlabHalf.BOTTOM);
      }

      this.j(iblockdata.set(VARIANT, BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant.RED_SANDSTONE));
      this.a(CreativeModeTab.b);
   }

   @Override
   public String getName() {
      return LocaleI18n.get(this.a() + ".red_sandstone.name");
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Item.getItemOf(Blocks.STONE_SLAB2);
   }

   @Override
   public String b(int i) {
      return super.a() + "." + BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant.a(i).d();
   }

   @Override
   public IBlockState<?> n() {
      return VARIANT;
   }

   @Override
   public Object a(ItemStack itemstack) {
      return BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant.a(itemstack.getData() & 7);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      IBlockData iblockdata = this.getBlockData().set(VARIANT, BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant.a(i & 7));
      if (this.l()) {
         iblockdata = iblockdata.set(SEAMLESS, (i & 8) != 0);
      } else {
         iblockdata = iblockdata.set(HALF, (i & 8) == 0 ? BlockStepAbstract.EnumSlabHalf.BOTTOM : BlockStepAbstract.EnumSlabHalf.TOP);
      }

      return iblockdata;
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(VARIANT).a();
      if (this.l()) {
         if (iblockdata.get(SEAMLESS)) {
            i |= 8;
         }
      } else if (iblockdata.get(HALF) == BlockStepAbstract.EnumSlabHalf.TOP) {
         i |= 8;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return this.l() ? new BlockStateList(this, SEAMLESS, VARIANT) : new BlockStateList(this, HALF, VARIANT);
   }

   @Override
   public MaterialMapColor g(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).c();
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   public static enum EnumStoneSlab2Variant implements INamable {
      RED_SANDSTONE(0, "red_sandstone", BlockSand.EnumSandVariant.RED_SAND.c());

      private static final BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant[] b = new BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant[values().length];
      private final int c;
      private final String d;
      private final MaterialMapColor e;

      static {
         BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant[] var3;
         for (BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant blockdoublestonestepabstract_enumstoneslab2variant : var3 = values()) {
            b[blockdoublestonestepabstract_enumstoneslab2variant.a()] = blockdoublestonestepabstract_enumstoneslab2variant;
         }
      }

      private EnumStoneSlab2Variant(int i, String s, MaterialMapColor materialmapcolor) {
         this.c = i;
         this.d = s;
         this.e = materialmapcolor;
      }

      public int a() {
         return this.c;
      }

      public MaterialMapColor c() {
         return this.e;
      }

      @Override
      public String toString() {
         return this.d;
      }

      public static BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant a(int i) {
         if (i < 0 || i >= b.length) {
            i = 0;
         }

         return b[i];
      }

      @Override
      public String getName() {
         return this.d;
      }

      public String d() {
         return this.d;
      }
   }
}
