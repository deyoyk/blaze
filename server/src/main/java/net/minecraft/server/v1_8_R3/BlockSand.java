package net.minecraft.server.v1_8_R3;

public class BlockSand extends BlockFalling {
   public static final BlockStateEnum<BlockSand.EnumSandVariant> VARIANT = BlockStateEnum.of("variant", BlockSand.EnumSandVariant.class);

   public BlockSand() {
      this.j(this.blockStateList.getBlockData().set(VARIANT, BlockSand.EnumSandVariant.SAND));
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   public MaterialMapColor g(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).c();
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(VARIANT, BlockSand.EnumSandVariant.a(i));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, VARIANT);
   }

   public static enum EnumSandVariant implements INamable {
      SAND(0, "sand", "default", MaterialMapColor.d),
      RED_SAND(1, "red_sand", "red", MaterialMapColor.q);

      private static final BlockSand.EnumSandVariant[] c = new BlockSand.EnumSandVariant[values().length];
      private final int d;
      private final String e;
      private final MaterialMapColor f;
      private final String g;

      static {
         BlockSand.EnumSandVariant[] var3;
         for (BlockSand.EnumSandVariant blocksand_enumsandvariant : var3 = values()) {
            c[blocksand_enumsandvariant.a()] = blocksand_enumsandvariant;
         }
      }

      private EnumSandVariant(int i, String s, String s1, MaterialMapColor materialmapcolor) {
         this.d = i;
         this.e = s;
         this.f = materialmapcolor;
         this.g = s1;
      }

      public int a() {
         return this.d;
      }

      @Override
      public String toString() {
         return this.e;
      }

      public MaterialMapColor c() {
         return this.f;
      }

      public static BlockSand.EnumSandVariant a(int i) {
         if (i < 0 || i >= c.length) {
            i = 0;
         }

         return c[i];
      }

      @Override
      public String getName() {
         return this.e;
      }

      public String d() {
         return this.g;
      }
   }
}
