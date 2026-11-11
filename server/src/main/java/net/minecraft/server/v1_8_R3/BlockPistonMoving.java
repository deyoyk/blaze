package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockPistonMoving extends BlockContainer {
   public static final BlockStateDirection FACING = BlockPistonExtension.FACING;
   public static final BlockStateEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;

   public BlockPistonMoving() {
      super(Material.PISTON);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT));
      this.c(-1.0F);
   }

   public static TileEntity a(IBlockData paramIBlockData, EnumDirection paramEnumDirection, boolean paramBoolean1, boolean paramBoolean2) {
      return new TileEntityPiston(paramIBlockData, paramEnumDirection, paramBoolean1, paramBoolean2);
   }

   @Override
   public TileEntity a(World paramWorld, int paramInt) {
      return null;
   }

   @Override
   public void remove(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData) {
      TileEntity tileEntity = paramWorld.getTileEntity(paramBlockPosition);
      if (tileEntity instanceof TileEntityPiston) {
         ((TileEntityPiston)tileEntity).h();
      } else {
         super.remove(paramWorld, paramBlockPosition, paramIBlockData);
      }
   }

   @Override
   public boolean canPlace(World paramWorld, BlockPosition paramBlockPosition) {
      return false;
   }

   @Override
   public boolean canPlace(World paramWorld, BlockPosition paramBlockPosition, EnumDirection paramEnumDirection) {
      return false;
   }

   @Override
   public void postBreak(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData) {
      BlockPosition blockPosition = paramBlockPosition.shift(paramIBlockData.get(FACING).opposite());
      IBlockData iBlockData = paramWorld.getType(blockPosition);
      if (iBlockData.getBlock() instanceof BlockPiston && iBlockData.get(BlockPiston.EXTENDED)) {
         paramWorld.setAir(blockPosition);
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
   public boolean interact(
      World paramWorld,
      BlockPosition paramBlockPosition,
      IBlockData paramIBlockData,
      EntityHuman paramEntityHuman,
      EnumDirection paramEnumDirection,
      float paramFloat1,
      float paramFloat2,
      float paramFloat3
   ) {
      if (paramWorld.getTileEntity(paramBlockPosition) == null) {
         paramWorld.setAir(paramBlockPosition);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Item getDropType(IBlockData paramIBlockData, Random paramRandom, int paramInt) {
      return null;
   }

   @Override
   public void dropNaturally(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, float paramFloat, int paramInt) {
      TileEntityPiston tileEntityPiston = this.e((IBlockAccess)paramWorld, paramBlockPosition);
      if (tileEntityPiston != null) {
         IBlockData iBlockData = tileEntityPiston.b();
         iBlockData.getBlock().b(paramWorld, paramBlockPosition, iBlockData, 0);
      }
   }

   @Override
   public MovingObjectPosition a(World paramWorld, BlockPosition paramBlockPosition, Vec3D paramVec3D1, Vec3D paramVec3D2) {
      return null;
   }

   @Override
   public void doPhysics(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, Block paramBlock) {
      paramWorld.getTileEntity(paramBlockPosition);
   }

   @Override
   public AxisAlignedBB a(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData) {
      TileEntityPiston tileEntityPiston = this.e((IBlockAccess)paramWorld, paramBlockPosition);
      if (tileEntityPiston == null) {
         return null;
      } else {
         float f = tileEntityPiston.a(0.0F);
         if (tileEntityPiston.d()) {
            f = 1.0F - f;
         }

         return this.a(paramWorld, paramBlockPosition, tileEntityPiston.b(), f, tileEntityPiston.e());
      }
   }

   @Override
   public void updateShape(IBlockAccess paramIBlockAccess, BlockPosition paramBlockPosition) {
      TileEntityPiston tileEntityPiston = this.e(paramIBlockAccess, paramBlockPosition);
      if (tileEntityPiston != null) {
         IBlockData iBlockData = tileEntityPiston.b();
         Block block = iBlockData.getBlock();
         if (block == this || block.getMaterial() == Material.AIR) {
            return;
         }

         float f = tileEntityPiston.a(0.0F);
         if (tileEntityPiston.d()) {
            f = 1.0F - f;
         }

         block.updateShape(paramIBlockAccess, paramBlockPosition);
         if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
            f = 0.0F;
         }

         EnumDirection enumDirection = tileEntityPiston.e();
         this.minX = block.B() - enumDirection.getAdjacentX() * f;
         this.minY = block.D() - enumDirection.getAdjacentY() * f;
         this.minZ = block.F() - enumDirection.getAdjacentZ() * f;
         this.maxX = block.C() - enumDirection.getAdjacentX() * f;
         this.maxY = block.E() - enumDirection.getAdjacentY() * f;
         this.maxZ = block.G() - enumDirection.getAdjacentZ() * f;
      }
   }

   public AxisAlignedBB a(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, float paramFloat, EnumDirection paramEnumDirection) {
      Block block = paramIBlockData.getBlock();
      if (block != this && block.getMaterial() != Material.AIR) {
         AxisAlignedBB axisAlignedBB = block.a(paramWorld, paramBlockPosition, paramIBlockData);
         if (axisAlignedBB == null) {
            return null;
         } else {
            double d1 = axisAlignedBB.a;
            double d2 = axisAlignedBB.b;
            double d3 = axisAlignedBB.c;
            double d4 = axisAlignedBB.d;
            double d5 = axisAlignedBB.e;
            double d6 = axisAlignedBB.f;
            if (paramEnumDirection.getAdjacentX() < 0) {
               d1 -= paramEnumDirection.getAdjacentX() * paramFloat;
            } else {
               d4 -= paramEnumDirection.getAdjacentX() * paramFloat;
            }

            if (paramEnumDirection.getAdjacentY() < 0) {
               d2 -= paramEnumDirection.getAdjacentY() * paramFloat;
            } else {
               d5 -= paramEnumDirection.getAdjacentY() * paramFloat;
            }

            if (paramEnumDirection.getAdjacentZ() < 0) {
               d3 -= paramEnumDirection.getAdjacentZ() * paramFloat;
            } else {
               d6 -= paramEnumDirection.getAdjacentZ() * paramFloat;
            }

            return new AxisAlignedBB(d1, d2, d3, d4, d5, d6);
         }
      } else {
         return null;
      }
   }

   private TileEntityPiston e(IBlockAccess paramIBlockAccess, BlockPosition paramBlockPosition) {
      TileEntity tileEntity = paramIBlockAccess.getTileEntity(paramBlockPosition);
      return tileEntity instanceof TileEntityPiston ? (TileEntityPiston)tileEntity : null;
   }

   @Override
   public IBlockData fromLegacyData(int paramInt) {
      return this.getBlockData()
         .set(FACING, BlockPistonExtension.b(paramInt))
         .set(TYPE, (paramInt & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
   }

   @Override
   public int toLegacyData(IBlockData paramIBlockData) {
      int i = 0;
      i |= paramIBlockData.get(FACING).a();
      if (paramIBlockData.get(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
         i |= 8;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING, TYPE);
   }
}
