package net.minecraft.server.v1_8_R3;

public class BlockTorch extends Block {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", enumdirection -> enumdirection != EnumDirection.DOWN);

   protected BlockTorch() {
      super(Material.ORIENTABLE);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.UP));
      this.a(true);
      this.a(CreativeModeTab.c);
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return null;
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean d() {
      return false;
   }

   private boolean e(World world, BlockPosition blockposition) {
      if (World.a(world, blockposition)) {
         return true;
      } else {
         Block block = world.getType(blockposition).getBlock();
         return block instanceof BlockFence || block == Blocks.GLASS || block == Blocks.COBBLESTONE_WALL || block == Blocks.STAINED_GLASS;
      }
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      for (EnumDirection enumdirection : FACING.c()) {
         if (this.a(world, blockposition, enumdirection)) {
            return true;
         }
      }

      return false;
   }

   private boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
      return enumdirection.k().c() && world.d(blockposition1, true) || enumdirection.equals(EnumDirection.UP) && this.e(world, blockposition1);
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      if (this.a(world, blockposition, enumdirection)) {
         return this.getBlockData().set(FACING, enumdirection);
      } else {
         for (EnumDirection enumdirection1 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            if (world.d(blockposition.shift(enumdirection1.opposite()), true)) {
               return this.getBlockData().set(FACING, enumdirection1);
            }
         }

         return this.getBlockData();
      }
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.f(world, blockposition, iblockdata);
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      this.e(world, blockposition, iblockdata);
   }

   protected boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (!this.f(world, blockposition, iblockdata)) {
         return true;
      } else {
         EnumDirection enumdirection = iblockdata.get(FACING);
         EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.k();
         EnumDirection enumdirection1 = enumdirection.opposite();
         boolean flag = false;
         if (enumdirection_enumaxis.c() && !world.d(blockposition.shift(enumdirection1), true)) {
            flag = true;
         } else if (enumdirection_enumaxis.b() && !this.e(world, blockposition.shift(enumdirection1))) {
            flag = true;
         }

         if (flag) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (iblockdata.getBlock() == this && this.a(world, blockposition, iblockdata.get(FACING))) {
         return true;
      } else {
         if (world.getType(blockposition).getBlock() == this) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
         }

         return false;
      }
   }

   @Override
   public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
      EnumDirection enumdirection = world.getType(blockposition).get(FACING);
      float f = 0.15F;
      if (enumdirection == EnumDirection.EAST) {
         this.a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
      } else if (enumdirection == EnumDirection.WEST) {
         this.a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
      } else if (enumdirection == EnumDirection.SOUTH) {
         this.a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
      } else if (enumdirection == EnumDirection.NORTH) {
         this.a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
      } else {
         f = 0.1F;
         this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
      }

      return super.a(world, blockposition, vec3d, vec3d1);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      IBlockData iblockdata = this.getBlockData();
      switch (i) {
         case 1:
            iblockdata = iblockdata.set(FACING, EnumDirection.EAST);
            break;
         case 2:
            iblockdata = iblockdata.set(FACING, EnumDirection.WEST);
            break;
         case 3:
            iblockdata = iblockdata.set(FACING, EnumDirection.SOUTH);
            break;
         case 4:
            iblockdata = iblockdata.set(FACING, EnumDirection.NORTH);
            break;
         case 5:
         default:
            iblockdata = iblockdata.set(FACING, EnumDirection.UP);
      }

      return iblockdata;
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i;
      switch (BlockTorch.SyntheticClass_1.a[iblockdata.get(FACING).ordinal()]) {
         case 1:
            i = b0 | 1;
            break;
         case 2:
            i = b0 | 2;
            break;
         case 3:
            i = b0 | 3;
            break;
         case 4:
            i = b0 | 4;
            break;
         case 5:
         case 6:
         default:
            i = b0 | 5;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING);
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.values().length];

      static {
         try {
            a[EnumDirection.EAST.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            a[EnumDirection.WEST.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
         }

         try {
            a[EnumDirection.SOUTH.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[EnumDirection.NORTH.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.DOWN.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.UP.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
