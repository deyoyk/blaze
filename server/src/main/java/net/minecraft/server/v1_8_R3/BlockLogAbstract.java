package net.minecraft.server.v1_8_R3;

public abstract class BlockLogAbstract extends BlockRotatable {
   public static final BlockStateEnum<BlockLogAbstract.EnumLogRotation> AXIS = BlockStateEnum.of("axis", BlockLogAbstract.EnumLogRotation.class);

   public BlockLogAbstract() {
      super(Material.WOOD);
      this.a(CreativeModeTab.b);
      this.c(2.0F);
      this.a(f);
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      byte b0 = 4;
      int i = b0 + 1;
      if (world.areChunksLoadedBetween(blockposition.a(-i, -i, -i), blockposition.a(i, i, i))) {
         for (BlockPosition blockposition1 : BlockPosition.a(blockposition.a(-b0, -b0, -b0), blockposition.a((int)b0, (int)b0, (int)b0))) {
            IBlockData iblockdata1 = world.getType(blockposition1);
            if (iblockdata1.getBlock().getMaterial() == Material.LEAVES && !iblockdata1.get(BlockLeaves.CHECK_DECAY)) {
               world.setTypeAndData(blockposition1, iblockdata1.set(BlockLeaves.CHECK_DECAY, true), 4);
            }
         }
      }
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return super.getPlacedState(world, blockposition, enumdirection, f, f1, f2, i, entityliving)
         .set(AXIS, BlockLogAbstract.EnumLogRotation.a(enumdirection.k()));
   }

   public static enum EnumLogRotation implements INamable {
      X("x"),
      Y("y"),
      Z("z"),
      NONE("none");

      private final String e;

      private EnumLogRotation(String s) {
         this.e = s;
      }

      @Override
      public String toString() {
         return this.e;
      }

      public static BlockLogAbstract.EnumLogRotation a(EnumDirection.EnumAxis enumdirection_enumaxis) {
         switch (BlockLogAbstract.SyntheticClass_1.a[enumdirection_enumaxis.ordinal()]) {
            case 1:
               return X;
            case 2:
               return Y;
            case 3:
               return Z;
            default:
               return NONE;
         }
      }

      @Override
      public String getName() {
         return this.e;
      }
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.EnumAxis.values().length];

      static {
         try {
            a[EnumDirection.EnumAxis.X.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.EnumAxis.Y.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.EnumAxis.Z.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
