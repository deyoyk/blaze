package net.minecraft.server.v1_8_R3;

import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockLever extends Block {
   public static final BlockStateEnum<BlockLever.EnumLeverPosition> FACING = BlockStateEnum.of("facing", BlockLever.EnumLeverPosition.class);
   public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");

   protected BlockLever() {
      super(Material.ORIENTABLE);
      this.j(this.blockStateList.getBlockData().set(FACING, BlockLever.EnumLeverPosition.NORTH).set(POWERED, false));
      this.a(CreativeModeTab.d);
   }

   protected static boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      return BlockButtonAbstract.a(world, blockposition, enumdirection);
   }

   public static int a(EnumDirection enumdirection) {
      switch (BlockLever.SyntheticClass_1.a[enumdirection.ordinal()]) {
         case 1:
            return 0;
         case 2:
            return 5;
         case 3:
            return 4;
         case 4:
            return 3;
         case 5:
            return 2;
         case 6:
            return 1;
         default:
            return -1;
      }
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

   @Override
   public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      return a(world, blockposition, enumdirection.opposite());
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      EnumDirection[] var6;
      for (EnumDirection enumdirection : var6 = EnumDirection.values()) {
         if (a(world, blockposition, enumdirection)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      IBlockData iblockdata = this.getBlockData().set(POWERED, Boolean.FALSE);
      if (a(world, blockposition, enumdirection.opposite())) {
         return iblockdata.set(FACING, BlockLever.EnumLeverPosition.a(enumdirection, entityliving.getDirection()));
      } else {
         for (EnumDirection enumdirection1 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            if (enumdirection1 != enumdirection && a(world, blockposition, enumdirection1.opposite())) {
               return iblockdata.set(FACING, BlockLever.EnumLeverPosition.a(enumdirection1, entityliving.getDirection()));
            }
         }

         return World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ())
            ? iblockdata.set(FACING, BlockLever.EnumLeverPosition.a(EnumDirection.UP, entityliving.getDirection()))
            : iblockdata;
      }
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (this.e(world, blockposition, iblockdata) && !a(world, blockposition, iblockdata.get(FACING).c().opposite())) {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }
   }

   private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (this.canPlace(world, blockposition)) {
         return true;
      } else {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
         return false;
      }
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      float f = 0.1875F;
      switch (BlockLever.SyntheticClass_1.b[iblockaccess.getType(blockposition).get(FACING).ordinal()]) {
         case 1:
            this.a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
            break;
         case 2:
            this.a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
            break;
         case 3:
            this.a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
            break;
         case 4:
            this.a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
            break;
         case 5:
         case 6:
            f = 0.25F;
            this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
            break;
         case 7:
         case 8:
            f = 0.25F;
            this.a(0.5F - f, 0.4F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
      }
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      boolean powered = iblockdata.get(POWERED);
      org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
      int old = powered ? 15 : 0;
      int current = !powered ? 15 : 0;
      BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
      world.getServer().getPluginManager().callEvent(eventRedstone);
      if (eventRedstone.getNewCurrent() > 0 == powered) {
         return true;
      } else {
         iblockdata = iblockdata.a(POWERED);
         world.setTypeAndData(blockposition, iblockdata, 3);
         world.makeSound(
            blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5, "random.click", 0.3F, iblockdata.get(POWERED) ? 0.6F : 0.5F
         );
         world.applyPhysics(blockposition, this);
         EnumDirection enumdirection1 = iblockdata.get(FACING).c();
         world.applyPhysics(blockposition.shift(enumdirection1.opposite()), this);
         return true;
      }
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (iblockdata.get(POWERED)) {
         world.applyPhysics(blockposition, this);
         EnumDirection enumdirection = iblockdata.get(FACING).c();
         world.applyPhysics(blockposition.shift(enumdirection.opposite()), this);
      }

      super.remove(world, blockposition, iblockdata);
   }

   @Override
   public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return iblockdata.get(POWERED) ? 15 : 0;
   }

   @Override
   public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return !iblockdata.get(POWERED) ? 0 : (iblockdata.get(FACING).c() == enumdirection ? 15 : 0);
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(FACING, BlockLever.EnumLeverPosition.a(i & 7)).set(POWERED, (i & 8) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(FACING).a();
      if (iblockdata.get(POWERED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, FACING, POWERED);
   }

   public static enum EnumLeverPosition implements INamable {
      DOWN_X(0, "down_x", EnumDirection.DOWN),
      EAST(1, "east", EnumDirection.EAST),
      WEST(2, "west", EnumDirection.WEST),
      SOUTH(3, "south", EnumDirection.SOUTH),
      NORTH(4, "north", EnumDirection.NORTH),
      UP_Z(5, "up_z", EnumDirection.UP),
      UP_X(6, "up_x", EnumDirection.UP),
      DOWN_Z(7, "down_z", EnumDirection.DOWN);

      private static final BlockLever.EnumLeverPosition[] i = new BlockLever.EnumLeverPosition[values().length];
      private final int j;
      private final String k;
      private final EnumDirection l;

      static {
         BlockLever.EnumLeverPosition[] var3;
         for (BlockLever.EnumLeverPosition blocklever_enumleverposition : var3 = values()) {
            i[blocklever_enumleverposition.a()] = blocklever_enumleverposition;
         }
      }

      private EnumLeverPosition(int i, String s, EnumDirection enumdirection) {
         this.j = i;
         this.k = s;
         this.l = enumdirection;
      }

      public static BlockLever.EnumLeverPosition a(int i) {
         if (i < 0 || i >= BlockLever.EnumLeverPosition.i.length) {
            i = 0;
         }

         return BlockLever.EnumLeverPosition.i[i];
      }

      public static BlockLever.EnumLeverPosition a(EnumDirection enumdirection, EnumDirection enumdirection1) {
         switch (BlockLever.SyntheticClass_1.a[enumdirection.ordinal()]) {
            case 1:
               switch (BlockLever.SyntheticClass_1.c[enumdirection1.k().ordinal()]) {
                  case 1:
                     return DOWN_X;
                  case 2:
                     return DOWN_Z;
                  default:
                     throw new IllegalArgumentException("Invalid entityFacing " + enumdirection1 + " for facing " + enumdirection);
               }
            case 2:
               switch (BlockLever.SyntheticClass_1.c[enumdirection1.k().ordinal()]) {
                  case 1:
                     return UP_X;
                  case 2:
                     return UP_Z;
                  default:
                     throw new IllegalArgumentException("Invalid entityFacing " + enumdirection1 + " for facing " + enumdirection);
               }
            case 3:
               return NORTH;
            case 4:
               return SOUTH;
            case 5:
               return WEST;
            case 6:
               return EAST;
            default:
               throw new IllegalArgumentException("Invalid facing: " + enumdirection);
         }
      }

      public int a() {
         return this.j;
      }

      public EnumDirection c() {
         return this.l;
      }

      @Override
      public String toString() {
         return this.k;
      }

      @Override
      public String getName() {
         return this.k;
      }
   }

   static class SyntheticClass_1 {
      static final int[] a;
      static final int[] b;
      static final int[] c = new int[EnumDirection.EnumAxis.values().length];

      static {
         try {
            c[EnumDirection.EnumAxis.X.ordinal()] = 1;
         } catch (NoSuchFieldError var16) {
         }

         try {
            c[EnumDirection.EnumAxis.Z.ordinal()] = 2;
         } catch (NoSuchFieldError var15) {
         }

         b = new int[BlockLever.EnumLeverPosition.values().length];

         try {
            b[BlockLever.EnumLeverPosition.EAST.ordinal()] = 1;
         } catch (NoSuchFieldError var14) {
         }

         try {
            b[BlockLever.EnumLeverPosition.WEST.ordinal()] = 2;
         } catch (NoSuchFieldError var13) {
         }

         try {
            b[BlockLever.EnumLeverPosition.SOUTH.ordinal()] = 3;
         } catch (NoSuchFieldError var12) {
         }

         try {
            b[BlockLever.EnumLeverPosition.NORTH.ordinal()] = 4;
         } catch (NoSuchFieldError var11) {
         }

         try {
            b[BlockLever.EnumLeverPosition.UP_Z.ordinal()] = 5;
         } catch (NoSuchFieldError var10) {
         }

         try {
            b[BlockLever.EnumLeverPosition.UP_X.ordinal()] = 6;
         } catch (NoSuchFieldError var9) {
         }

         try {
            b[BlockLever.EnumLeverPosition.DOWN_X.ordinal()] = 7;
         } catch (NoSuchFieldError var8) {
         }

         try {
            b[BlockLever.EnumLeverPosition.DOWN_Z.ordinal()] = 8;
         } catch (NoSuchFieldError var7) {
         }

         a = new int[EnumDirection.values().length];

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
