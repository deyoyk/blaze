package net.minecraft.server.v1_8_R3;

public class BlockWood extends Block {
   public static final BlockStateEnum<BlockWood.EnumLogVariant> VARIANT = BlockStateEnum.of("variant", BlockWood.EnumLogVariant.class);

   public BlockWood() {
      super(Material.WOOD);
      this.j(this.blockStateList.getBlockData().set(VARIANT, BlockWood.EnumLogVariant.OAK));
      this.a(CreativeModeTab.b);
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(VARIANT, BlockWood.EnumLogVariant.a(i));
   }

   @Override
   public MaterialMapColor g(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).c();
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, VARIANT);
   }

   public static enum EnumLogVariant implements INamable {
      OAK(0, "oak", MaterialMapColor.o),
      SPRUCE(1, "spruce", MaterialMapColor.J),
      BIRCH(2, "birch", MaterialMapColor.d),
      JUNGLE(3, "jungle", MaterialMapColor.l),
      ACACIA(4, "acacia", MaterialMapColor.q),
      DARK_OAK(5, "dark_oak", "big_oak", MaterialMapColor.B);

      private static final BlockWood.EnumLogVariant[] g = new BlockWood.EnumLogVariant[values().length];
      private final int h;
      private final String i;
      private final String j;
      private final MaterialMapColor k;

      static {
         BlockWood.EnumLogVariant[] var3;
         for (BlockWood.EnumLogVariant blockwood_enumlogvariant : var3 = values()) {
            g[blockwood_enumlogvariant.a()] = blockwood_enumlogvariant;
         }
      }

      private EnumLogVariant(int i, String s, MaterialMapColor materialmapcolor) {
         this(i, s, s, materialmapcolor);
      }

      private EnumLogVariant(int i, String s, String s1, MaterialMapColor materialmapcolor) {
         this.h = i;
         this.i = s;
         this.j = s1;
         this.k = materialmapcolor;
      }

      public int a() {
         return this.h;
      }

      public MaterialMapColor c() {
         return this.k;
      }

      @Override
      public String toString() {
         return this.i;
      }

      public static BlockWood.EnumLogVariant a(int i) {
         if (i < 0 || i >= g.length) {
            i = 0;
         }

         return g[i];
      }

      @Override
      public String getName() {
         return this.i;
      }

      public String d() {
         return this.j;
      }
   }
}
