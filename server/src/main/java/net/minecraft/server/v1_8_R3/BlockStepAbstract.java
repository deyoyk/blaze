package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.Random;

public abstract class BlockStepAbstract extends Block {
   public static final BlockStateEnum<BlockStepAbstract.EnumSlabHalf> HALF = BlockStateEnum.of("half", BlockStepAbstract.EnumSlabHalf.class);

   public BlockStepAbstract(Material paramMaterial) {
      super(paramMaterial);
      if (this.l()) {
         this.r = true;
      } else {
         this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }

      this.e(255);
   }

   @Override
   protected boolean I() {
      return false;
   }

   @Override
   public void updateShape(IBlockAccess paramIBlockAccess, BlockPosition paramBlockPosition) {
      if (this.l()) {
         this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         IBlockData iBlockData = paramIBlockAccess.getType(paramBlockPosition);
         if (iBlockData.getBlock() == this) {
            if (iBlockData.get(HALF) == BlockStepAbstract.EnumSlabHalf.TOP) {
               this.a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
         }
      }
   }

   @Override
   public void j() {
      if (this.l()) {
         this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   @Override
   public void a(
      World paramWorld,
      BlockPosition paramBlockPosition,
      IBlockData paramIBlockData,
      AxisAlignedBB paramAxisAlignedBB,
      List<AxisAlignedBB> paramList,
      Entity paramEntity
   ) {
      this.updateShape(paramWorld, paramBlockPosition);
      super.a(paramWorld, paramBlockPosition, paramIBlockData, paramAxisAlignedBB, paramList, paramEntity);
   }

   @Override
   public boolean c() {
      return this.l();
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
      IBlockData iBlockData = super.getPlacedState(
            paramWorld, paramBlockPosition, paramEnumDirection, paramFloat1, paramFloat2, paramFloat3, paramInt, paramEntityLiving
         )
         .set(HALF, BlockStepAbstract.EnumSlabHalf.BOTTOM);
      if (this.l()) {
         return iBlockData;
      } else {
         return paramEnumDirection != EnumDirection.DOWN && (paramEnumDirection == EnumDirection.UP || !(paramFloat2 > 0.5))
            ? iBlockData
            : iBlockData.set(HALF, BlockStepAbstract.EnumSlabHalf.TOP);
      }
   }

   @Override
   public int a(Random paramRandom) {
      return this.l() ? 2 : 1;
   }

   @Override
   public boolean d() {
      return this.l();
   }

   public abstract String b(int var1);

   @Override
   public int getDropData(World paramWorld, BlockPosition paramBlockPosition) {
      return super.getDropData(paramWorld, paramBlockPosition) & 7;
   }

   public abstract boolean l();

   public abstract IBlockState<?> n();

   public abstract Object a(ItemStack var1);

   public static enum EnumSlabHalf implements INamable {
      TOP("top"),
      BOTTOM("bottom");

      private final String c;

      private EnumSlabHalf(String param1String1) {
         this.c = param1String1;
      }

      @Override
      public String toString() {
         return this.c;
      }

      @Override
      public String getName() {
         return this.c;
      }
   }
}
