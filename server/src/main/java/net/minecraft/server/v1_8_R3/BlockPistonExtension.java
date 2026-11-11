package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.Random;

public class BlockPistonExtension extends Block {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing");
   public static final BlockStateEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockStateEnum.of("type", BlockPistonExtension.EnumPistonType.class);
   public static final BlockStateBoolean SHORT = BlockStateBoolean.of("short");

   public BlockPistonExtension() {
      super(Material.PISTON);
      this.j(
         this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT).set(SHORT, Boolean.FALSE)
      );
      this.a(i);
      this.c(0.5F);
   }

   public static EnumDirection b(int paramInt) {
      int i = paramInt & 7;
      return i > 5 ? null : EnumDirection.fromType1(i);
   }

   @Override
   public void a(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, EntityHuman paramEntityHuman) {
      if (paramEntityHuman.abilities.canInstantlyBuild) {
         EnumDirection enumDirection = paramIBlockData.get(FACING);
         if (enumDirection != null) {
            BlockPosition blockPosition = paramBlockPosition.shift(enumDirection.opposite());
            Block block = paramWorld.getType(blockPosition).getBlock();
            if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
               paramWorld.setAir(blockPosition);
            }
         }
      }

      super.a(paramWorld, paramBlockPosition, paramIBlockData, paramEntityHuman);
   }

   @Override
   public void remove(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData) {
      super.remove(paramWorld, paramBlockPosition, paramIBlockData);
      EnumDirection enumDirection = paramIBlockData.get(FACING).opposite();
      paramBlockPosition = paramBlockPosition.shift(enumDirection);
      IBlockData iBlockData = paramWorld.getType(paramBlockPosition);
      if ((iBlockData.getBlock() == Blocks.PISTON || iBlockData.getBlock() == Blocks.STICKY_PISTON) && iBlockData.get(BlockPiston.EXTENDED)) {
         iBlockData.getBlock().b(paramWorld, paramBlockPosition, iBlockData, 0);
         paramWorld.setAir(paramBlockPosition);
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
   public boolean canPlace(World paramWorld, BlockPosition paramBlockPosition) {
      return false;
   }

   @Override
   public boolean canPlace(World paramWorld, BlockPosition paramBlockPosition, EnumDirection paramEnumDirection) {
      return false;
   }

   @Override
   public int a(Random paramRandom) {
      return 0;
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
      this.d(paramIBlockData);
      super.a(paramWorld, paramBlockPosition, paramIBlockData, paramAxisAlignedBB, paramList, paramEntity);
      this.e(paramIBlockData);
      super.a(paramWorld, paramBlockPosition, paramIBlockData, paramAxisAlignedBB, paramList, paramEntity);
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   private void e(IBlockData paramIBlockData) {
      switch (BlockPistonExtension.SyntheticClass_1.a[paramIBlockData.get(FACING).ordinal()]) {
         case 1:
            this.a(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
            break;
         case 2:
            this.a(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
            break;
         case 3:
            this.a(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
            break;
         case 4:
            this.a(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
            break;
         case 5:
            this.a(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
            break;
         case 6:
            this.a(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
      }
   }

   @Override
   public void updateShape(IBlockAccess paramIBlockAccess, BlockPosition paramBlockPosition) {
      this.d(paramIBlockAccess.getType(paramBlockPosition));
   }

   public void d(IBlockData paramIBlockData) {
      EnumDirection enumDirection = paramIBlockData.get(FACING);
      if (enumDirection != null) {
         switch (BlockPistonExtension.SyntheticClass_1.a[enumDirection.ordinal()]) {
            case 1:
               this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
               break;
            case 2:
               this.a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case 3:
               this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
               break;
            case 4:
               this.a(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
               break;
            case 5:
               this.a(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
               break;
            case 6:
               this.a(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public void doPhysics(World paramWorld, BlockPosition paramBlockPosition, IBlockData paramIBlockData, Block paramBlock) {
      EnumDirection enumDirection = paramIBlockData.get(FACING);
      BlockPosition blockPosition = paramBlockPosition.shift(enumDirection.opposite());
      IBlockData iBlockData = paramWorld.getType(blockPosition);
      if (iBlockData.getBlock() != Blocks.PISTON && iBlockData.getBlock() != Blocks.STICKY_PISTON) {
         paramWorld.setAir(paramBlockPosition);
      } else {
         iBlockData.getBlock().doPhysics(paramWorld, blockPosition, iBlockData, paramBlock);
      }
   }

   @Override
   public IBlockData fromLegacyData(int paramInt) {
      return this.getBlockData()
         .set(FACING, b(paramInt))
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
      return new BlockStateList(this, FACING, TYPE, SHORT);
   }

   public static enum EnumPistonType implements INamable {
      DEFAULT("normal"),
      STICKY("sticky");

      private final String c;

      private EnumPistonType(String param1String1) {
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

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.values().length];

      static {
         try {
            a[EnumDirection.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            a[EnumDirection.UP.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
         }

         try {
            a[EnumDirection.NORTH.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[EnumDirection.SOUTH.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.WEST.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.EAST.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
