package net.minecraft.server.v1_8_R3;

public class BlockLadder extends Block {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);

   protected BlockLadder() {
      super(Material.ORIENTABLE);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH));
      this.a(CreativeModeTab.c);
   }

   @Override
   public AxisAlignedBB a(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData) {
      this.updateShape(paramWorld, paramBlockPosition);
      return super.a(paramWorld, paramBlockPosition, paramIBlockData);
   }

   @Override
   public void updateShape(IBlockAccess paramIBlockAccess, BlockPosition paramBlockPosition) {
      IBlockData iBlockData = paramIBlockAccess.getType(paramBlockPosition);
      if (iBlockData.getBlock() == this) {
         switch (BlockLadder.SyntheticClass_1.a[iBlockData.get(FACING).ordinal()]) {
            case 1:
               this.a(0.0F, 0.0F, 0.875F, 1.0F, 1.0F, 1.0F);
               return;
            case 2:
               this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F);
               return;
            case 3:
               this.a(0.875F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
               return;
            default:
               this.a(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean canPlace(World world, BlockPosition pos) {
      return world.getType(pos.getX(), pos.getY(), pos.getZ() - 1).getBlock().isOccluding()
         || world.getType(pos.getX() + 1, pos.getY(), pos.getZ()).getBlock().isOccluding()
         || world.getType(pos.getX() - 1, pos.getY(), pos.getZ()).getBlock().isOccluding()
         || world.getType(pos.getX(), pos.getY(), pos.getZ() + 1).getBlock().isOccluding();
   }

   @Override
   public IBlockData getPlacedState(
      World paramWorld,
      BlockPosition paramBlockPosition,
      EnumDirection paramEnumDirection,
      float paramFloat1,
      float paramFloat2,
      float paramFloat3,
      int paramInt,
      EntityLiving paramEntityLiving
   ) {
      if (paramEnumDirection.k().c() && this.a(paramWorld, paramBlockPosition, paramEnumDirection)) {
         return this.getBlockData().set(FACING, paramEnumDirection);
      } else {
         for (EnumDirection enumDirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            if (this.a(paramWorld, paramBlockPosition, enumDirection)) {
               return this.getBlockData().set(FACING, enumDirection);
            }
         }

         return this.getBlockData();
      }
   }

   @Override
   public void doPhysics(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, Block paramBlock) {
      EnumDirection enumDirection = paramIBlockData.get(FACING);
      if (!this.a(paramWorld, paramBlockPosition, enumDirection)) {
         this.b(paramWorld, paramBlockPosition, paramIBlockData, 0);
         paramWorld.setAir(paramBlockPosition);
      }

      super.doPhysics(paramWorld, paramBlockPosition, paramIBlockData, paramBlock);
   }

   protected boolean a(World paramWorld, BlockPosition paramBlockPosition, EnumDirection paramEnumDirection) {
      return paramWorld.getType(paramBlockPosition.shift(paramEnumDirection.opposite())).getBlock().isOccluding();
   }

   @Override
   public IBlockData fromLegacyData(int paramInt) {
      EnumDirection enumDirection = EnumDirection.fromType1(paramInt);
      if (enumDirection.k() == EnumDirection.EnumAxis.Y) {
         enumDirection = EnumDirection.NORTH;
      }

      return this.getBlockData().set(FACING, enumDirection);
   }

   @Override
   public int toLegacyData(IBlockData paramIBlockData) {
      return paramIBlockData.get(FACING).a();
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
