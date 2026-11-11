package net.minecraft.server.v1_8_R3;

public class BlockPrismarine extends Block {
   public static final BlockStateEnum<BlockPrismarine.EnumPrismarineVariant> VARIANT = BlockStateEnum.of("variant", BlockPrismarine.EnumPrismarineVariant.class);
   public static final int b = BlockPrismarine.EnumPrismarineVariant.ROUGH.a();
   public static final int N = BlockPrismarine.EnumPrismarineVariant.BRICKS.a();
   public static final int O = BlockPrismarine.EnumPrismarineVariant.DARK.a();

   public BlockPrismarine() {
      super(Material.STONE);
      this.j(this.blockStateList.getBlockData().set(VARIANT, BlockPrismarine.EnumPrismarineVariant.ROUGH));
      this.a(CreativeModeTab.b);
   }

   @Override
   public String getName() {
      return LocaleI18n.get(this.a() + "." + BlockPrismarine.EnumPrismarineVariant.ROUGH.c() + ".name");
   }

   @Override
   public MaterialMapColor g(IBlockData iblockdata) {
      return iblockdata.get(VARIANT) == BlockPrismarine.EnumPrismarineVariant.ROUGH ? MaterialMapColor.y : MaterialMapColor.G;
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, VARIANT);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(VARIANT, BlockPrismarine.EnumPrismarineVariant.a(i));
   }

   public static enum EnumPrismarineVariant implements INamable {
      ROUGH(0, "prismarine", "rough"),
      BRICKS(1, "prismarine_bricks", "bricks"),
      DARK(2, "dark_prismarine", "dark");

      private static final BlockPrismarine.EnumPrismarineVariant[] d = new BlockPrismarine.EnumPrismarineVariant[values().length];
      private final int e;
      private final String f;
      private final String g;

      static {
         BlockPrismarine.EnumPrismarineVariant[] var3;
         for (BlockPrismarine.EnumPrismarineVariant blockprismarine_enumprismarinevariant : var3 = values()) {
            d[blockprismarine_enumprismarinevariant.a()] = blockprismarine_enumprismarinevariant;
         }
      }

      private EnumPrismarineVariant(int i, String s, String s1) {
         this.e = i;
         this.f = s;
         this.g = s1;
      }

      public int a() {
         return this.e;
      }

      @Override
      public String toString() {
         return this.f;
      }

      public static BlockPrismarine.EnumPrismarineVariant a(int i) {
         if (i < 0 || i >= d.length) {
            i = 0;
         }

         return d[i];
      }

      @Override
      public String getName() {
         return this.f;
      }

      public String c() {
         return this.g;
      }
   }
}
