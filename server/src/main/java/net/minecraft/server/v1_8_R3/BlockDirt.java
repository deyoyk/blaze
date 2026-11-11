package net.minecraft.server.v1_8_R3;

public class BlockDirt extends Block {
   public static final BlockStateEnum<BlockDirt.EnumDirtVariant> VARIANT = BlockStateEnum.of("variant", BlockDirt.EnumDirtVariant.class);
   public static final BlockStateBoolean SNOWY = BlockStateBoolean.of("snowy");

   protected BlockDirt() {
      super(Material.EARTH);
      this.j(this.blockStateList.getBlockData().set(VARIANT, BlockDirt.EnumDirtVariant.DIRT).set(SNOWY, Boolean.FALSE));
      this.a(CreativeModeTab.b);
   }

   @Override
   public MaterialMapColor g(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).d();
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      if (iblockdata.get(VARIANT) == BlockDirt.EnumDirtVariant.PODZOL) {
         Block block = iblockaccess.getType(blockposition.up()).getBlock();
         iblockdata = iblockdata.set(SNOWY, block == Blocks.SNOW || block == Blocks.SNOW_LAYER);
      }

      return iblockdata;
   }

   @Override
   public int getDropData(World world, BlockPosition blockposition) {
      IBlockData iblockdata = world.getType(blockposition);
      return iblockdata.getBlock() != this ? 0 : iblockdata.get(VARIANT).a();
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(VARIANT, BlockDirt.EnumDirtVariant.a(i));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, VARIANT, SNOWY);
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      BlockDirt.EnumDirtVariant blockdirt_enumdirtvariant = iblockdata.get(VARIANT);
      if (blockdirt_enumdirtvariant == BlockDirt.EnumDirtVariant.PODZOL) {
         blockdirt_enumdirtvariant = BlockDirt.EnumDirtVariant.DIRT;
      }

      return blockdirt_enumdirtvariant.a();
   }

   public static enum EnumDirtVariant implements INamable {
      DIRT(0, "dirt", "default", MaterialMapColor.l),
      COARSE_DIRT(1, "coarse_dirt", "coarse", MaterialMapColor.l),
      PODZOL(2, "podzol", MaterialMapColor.J);

      private static final BlockDirt.EnumDirtVariant[] d = new BlockDirt.EnumDirtVariant[values().length];
      private final int e;
      private final String f;
      private final String g;
      private final MaterialMapColor h;

      static {
         BlockDirt.EnumDirtVariant[] var3;
         for (BlockDirt.EnumDirtVariant blockdirt_enumdirtvariant : var3 = values()) {
            d[blockdirt_enumdirtvariant.a()] = blockdirt_enumdirtvariant;
         }
      }

      private EnumDirtVariant(int i, String s, MaterialMapColor materialmapcolor) {
         this(i, s, s, materialmapcolor);
      }

      private EnumDirtVariant(int i, String s, String s1, MaterialMapColor materialmapcolor) {
         this.e = i;
         this.f = s;
         this.g = s1;
         this.h = materialmapcolor;
      }

      public int a() {
         return this.e;
      }

      public String c() {
         return this.g;
      }

      public MaterialMapColor d() {
         return this.h;
      }

      @Override
      public String toString() {
         return this.f;
      }

      public static BlockDirt.EnumDirtVariant a(int i) {
         if (i < 0 || i >= d.length) {
            i = 0;
         }

         return d[i];
      }

      @Override
      public String getName() {
         return this.f;
      }
   }
}
