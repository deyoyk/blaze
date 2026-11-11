package net.minecraft.server.v1_8_R3;

public class BlockWallSign extends BlockSign {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);

   public BlockWallSign() {
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH));
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      EnumDirection enumdirection = iblockaccess.getType(blockposition).get(FACING);
      float f = 0.28125F;
      float f1 = 0.78125F;
      float f2 = 0.0F;
      float f3 = 1.0F;
      float f4 = 0.125F;
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      switch (BlockWallSign.SyntheticClass_1.a[enumdirection.ordinal()]) {
         case 1:
            this.a(f2, f, 1.0F - f4, f3, f1, 1.0F);
            break;
         case 2:
            this.a(f2, f, 0.0F, f3, f1, f4);
            break;
         case 3:
            this.a(1.0F - f4, f, f2, 1.0F, f1, f3);
            break;
         case 4:
            this.a(0.0F, f, f2, f4, f1, f3);
      }
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      EnumDirection enumdirection = iblockdata.get(FACING);
      if (!world.getType(blockposition.shift(enumdirection.opposite())).getBlock().getMaterial().isBuildable()) {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }

      super.doPhysics(world, blockposition, iblockdata, block);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      EnumDirection enumdirection = EnumDirection.fromType1(i);
      if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
         enumdirection = EnumDirection.NORTH;
      }

      return this.getBlockData().set(FACING, enumdirection);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(FACING).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING);
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.values().length];

      static {
         try {
            a[EnumDirection.NORTH.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[EnumDirection.SOUTH.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.WEST.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.EAST.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
