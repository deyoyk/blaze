package net.minecraft.server.v1_8_R3;

public class BlockQuartz extends Block {
   public static final BlockStateEnum<BlockQuartz.EnumQuartzVariant> VARIANT = BlockStateEnum.of("variant", BlockQuartz.EnumQuartzVariant.class);

   public BlockQuartz() {
      super(Material.STONE);
      this.j(this.blockStateList.getBlockData().set(VARIANT, BlockQuartz.EnumQuartzVariant.DEFAULT));
      this.a(CreativeModeTab.b);
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      if (i == BlockQuartz.EnumQuartzVariant.LINES_Y.a()) {
         switch (BlockQuartz.SyntheticClass_1.a[enumdirection.k().ordinal()]) {
            case 1:
               return this.getBlockData().set(VARIANT, BlockQuartz.EnumQuartzVariant.LINES_Z);
            case 2:
               return this.getBlockData().set(VARIANT, BlockQuartz.EnumQuartzVariant.LINES_X);
            case 3:
            default:
               return this.getBlockData().set(VARIANT, BlockQuartz.EnumQuartzVariant.LINES_Y);
         }
      } else {
         return i == BlockQuartz.EnumQuartzVariant.CHISELED.a()
            ? this.getBlockData().set(VARIANT, BlockQuartz.EnumQuartzVariant.CHISELED)
            : this.getBlockData().set(VARIANT, BlockQuartz.EnumQuartzVariant.DEFAULT);
      }
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      BlockQuartz.EnumQuartzVariant blockquartz_enumquartzvariant = iblockdata.get(VARIANT);
      return blockquartz_enumquartzvariant != BlockQuartz.EnumQuartzVariant.LINES_X && blockquartz_enumquartzvariant != BlockQuartz.EnumQuartzVariant.LINES_Z
         ? blockquartz_enumquartzvariant.a()
         : BlockQuartz.EnumQuartzVariant.LINES_Y.a();
   }

   @Override
   protected ItemStack i(IBlockData iblockdata) {
      BlockQuartz.EnumQuartzVariant blockquartz_enumquartzvariant = iblockdata.get(VARIANT);
      return blockquartz_enumquartzvariant != BlockQuartz.EnumQuartzVariant.LINES_X && blockquartz_enumquartzvariant != BlockQuartz.EnumQuartzVariant.LINES_Z
         ? super.i(iblockdata)
         : new ItemStack(Item.getItemOf(this), 1, BlockQuartz.EnumQuartzVariant.LINES_Y.a());
   }

   @Override
   public MaterialMapColor g(IBlockData iblockdata) {
      return MaterialMapColor.p;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(VARIANT, BlockQuartz.EnumQuartzVariant.a(i));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, VARIANT);
   }

   public static enum EnumQuartzVariant implements INamable {
      DEFAULT(0, "default", "default"),
      CHISELED(1, "chiseled", "chiseled"),
      LINES_Y(2, "lines_y", "lines"),
      LINES_X(3, "lines_x", "lines"),
      LINES_Z(4, "lines_z", "lines");

      private static final BlockQuartz.EnumQuartzVariant[] f = new BlockQuartz.EnumQuartzVariant[values().length];
      private final int g;
      private final String h;
      private final String i;

      static {
         BlockQuartz.EnumQuartzVariant[] var3;
         for (BlockQuartz.EnumQuartzVariant blockquartz_enumquartzvariant : var3 = values()) {
            f[blockquartz_enumquartzvariant.a()] = blockquartz_enumquartzvariant;
         }
      }

      private EnumQuartzVariant(int i, String s, String s1) {
         this.g = i;
         this.h = s;
         this.i = s1;
      }

      public int a() {
         return this.g;
      }

      @Override
      public String toString() {
         return this.i;
      }

      public static BlockQuartz.EnumQuartzVariant a(int i) {
         if (i < 0 || i >= f.length) {
            i = 0;
         }

         return f[i];
      }

      @Override
      public String getName() {
         return this.h;
      }
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.EnumAxis.values().length];

      static {
         try {
            a[EnumDirection.EnumAxis.Z.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.EnumAxis.X.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.EnumAxis.Y.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
