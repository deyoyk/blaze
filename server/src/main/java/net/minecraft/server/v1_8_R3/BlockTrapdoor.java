package net.minecraft.server.v1_8_R3;

import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockTrapdoor extends Block {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);
   public static final BlockStateBoolean OPEN = BlockStateBoolean.of("open");
   public static final BlockStateEnum<BlockTrapdoor.EnumTrapdoorHalf> HALF = BlockStateEnum.of("half", BlockTrapdoor.EnumTrapdoorHalf.class);

   protected BlockTrapdoor(Material material) {
      super(material);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(OPEN, false).set(HALF, BlockTrapdoor.EnumTrapdoorHalf.BOTTOM));
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.a(CreativeModeTab.d);
   }

   protected static EnumDirection b(int i) {
      switch (i & 3) {
         case 0:
            return EnumDirection.NORTH;
         case 1:
            return EnumDirection.SOUTH;
         case 2:
            return EnumDirection.WEST;
         case 3:
         default:
            return EnumDirection.EAST;
      }
   }

   protected static int a(EnumDirection enumdirection) {
      switch (BlockTrapdoor.SyntheticClass_1.a[enumdirection.ordinal()]) {
         case 1:
            return 0;
         case 2:
            return 1;
         case 3:
            return 2;
         case 4:
         default:
            return 3;
      }
   }

   private static boolean c(Block block) {
      return block.material.k() && block.d() || block == Blocks.GLOWSTONE || block instanceof BlockStepAbstract || block instanceof BlockStairs;
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
   public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return !iblockaccess.getType(blockposition).get(OPEN);
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.updateShape(world, blockposition);
      return super.a(world, blockposition, iblockdata);
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      this.d(iblockaccess.getType(blockposition));
   }

   @Override
   public void j() {
      this.a(0.0F, 0.40625F, 0.0F, 1.0F, 0.59375F, 1.0F);
   }

   public void d(IBlockData iblockdata) {
      if (iblockdata.getBlock() == this) {
         if (iblockdata.get(HALF) == BlockTrapdoor.EnumTrapdoorHalf.TOP) {
            this.a(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
         } else {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
         }

         if (iblockdata.get(OPEN)) {
            EnumDirection enumdirection = iblockdata.get(FACING);
            if (enumdirection == EnumDirection.NORTH) {
               this.a(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
            } else if (enumdirection == EnumDirection.SOUTH) {
               this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
            } else if (enumdirection == EnumDirection.WEST) {
               this.a(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else if (enumdirection == EnumDirection.EAST) {
               this.a(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
            }
         }
      }
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (this.material != Material.ORE) {
         iblockdata = iblockdata.a(OPEN);
         world.setTypeAndData(blockposition, iblockdata, 2);
         world.a(entityhuman, iblockdata.get(OPEN) ? 1003 : 1006, blockposition, 0);
      }

      return true;
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      BlockPosition blockposition1 = blockposition.shift(iblockdata.get(FACING).opposite());
      if (!c(world.getType(blockposition1).getBlock())) {
         world.setAir(blockposition);
         this.b(world, blockposition, iblockdata, 0);
      } else {
         boolean flag = world.isBlockIndirectlyPowered(blockposition);
         if (flag || block.isPowerSource()) {
            org.bukkit.World bworld = world.getWorld();
            org.bukkit.block.Block bblock = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            int power = bblock.getBlockPower();
            int oldPower = iblockdata.get(OPEN) ? 15 : 0;
            if (oldPower == 0 ^ power == 0 || block.isPowerSource()) {
               BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bblock, oldPower, power);
               world.getServer().getPluginManager().callEvent(eventRedstone);
               flag = eventRedstone.getNewCurrent() > 0;
            }

            boolean flag1 = iblockdata.get(OPEN);
            if (flag1 != flag) {
               world.setTypeAndData(blockposition, iblockdata.set(OPEN, flag), 2);
               world.a(null, flag ? 1003 : 1006, blockposition, 0);
            }
         }
      }
   }

   @Override
   public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
      this.updateShape(world, blockposition);
      return super.a(world, blockposition, vec3d, vec3d1);
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      IBlockData iblockdata = this.getBlockData();
      if (enumdirection.k().c()) {
         iblockdata = iblockdata.set(FACING, enumdirection).set(OPEN, false);
         iblockdata = iblockdata.set(HALF, f1 > 0.5F ? BlockTrapdoor.EnumTrapdoorHalf.TOP : BlockTrapdoor.EnumTrapdoorHalf.BOTTOM);
      }

      return iblockdata;
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      return !enumdirection.k().b() && c(world.getType(blockposition.shift(enumdirection.opposite())).getBlock());
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData()
         .set(FACING, b(i))
         .set(OPEN, (i & 4) != 0)
         .set(HALF, (i & 8) == 0 ? BlockTrapdoor.EnumTrapdoorHalf.BOTTOM : BlockTrapdoor.EnumTrapdoorHalf.TOP);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | a(iblockdata.get(FACING));
      if (iblockdata.get(OPEN)) {
         i |= 4;
      }

      if (iblockdata.get(HALF) == BlockTrapdoor.EnumTrapdoorHalf.TOP) {
         i |= 8;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING, OPEN, HALF);
   }

   public static enum EnumTrapdoorHalf implements INamable {
      TOP("top"),
      BOTTOM("bottom");

      private final String c;

      private EnumTrapdoorHalf(String s) {
         this.c = s;
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
