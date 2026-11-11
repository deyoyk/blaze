package net.minecraft.server.v1_8_R3;

public class BlockFenceGate extends BlockDirectional {
   public static final BlockStateBoolean OPEN = BlockStateBoolean.of("open");
   public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
   public static final BlockStateBoolean IN_WALL = BlockStateBoolean.of("in_wall");

   public BlockFenceGate(BlockWood.EnumLogVariant blockwood_enumlogvariant) {
      super(Material.WOOD, blockwood_enumlogvariant.c());
      this.j(this.blockStateList.getBlockData().set(OPEN, Boolean.FALSE).set(POWERED, Boolean.FALSE).set(IN_WALL, Boolean.FALSE));
      this.a(CreativeModeTab.d);
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      EnumDirection.EnumAxis enumdirection_enumaxis = iblockdata.get(FACING).k();
      if (enumdirection_enumaxis == EnumDirection.EnumAxis.Z
            && (
               iblockaccess.getType(blockposition.west()).getBlock() == Blocks.COBBLESTONE_WALL
                  || iblockaccess.getType(blockposition.east()).getBlock() == Blocks.COBBLESTONE_WALL
            )
         || enumdirection_enumaxis == EnumDirection.EnumAxis.X
            && (
               iblockaccess.getType(blockposition.north()).getBlock() == Blocks.COBBLESTONE_WALL
                  || iblockaccess.getType(blockposition.south()).getBlock() == Blocks.COBBLESTONE_WALL
            )) {
         iblockdata = iblockdata.set(IN_WALL, Boolean.TRUE);
      }

      return iblockdata;
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      return world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock().getMaterial().isBuildable()
         && super.canPlace(world, blockposition);
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (iblockdata.get(OPEN)) {
         return null;
      } else {
         EnumDirection.EnumAxis enumdirection_enumaxis = iblockdata.get(FACING).k();
         return enumdirection_enumaxis == EnumDirection.EnumAxis.Z
            ? new AxisAlignedBB(
               blockposition.getX(),
               blockposition.getY(),
               blockposition.getZ() + 0.375F,
               blockposition.getX() + 1,
               blockposition.getY() + 1.5F,
               blockposition.getZ() + 0.625F
            )
            : new AxisAlignedBB(
               blockposition.getX() + 0.375F,
               blockposition.getY(),
               blockposition.getZ(),
               blockposition.getX() + 0.625F,
               blockposition.getY() + 1.5F,
               blockposition.getZ() + 1
            );
      }
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      EnumDirection.EnumAxis enumdirection_enumaxis = iblockaccess.getType(blockposition).get(FACING).k();
      if (enumdirection_enumaxis == EnumDirection.EnumAxis.Z) {
         this.a(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
      } else {
         this.a(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
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
   public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return iblockaccess.getType(blockposition).get(OPEN);
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return this.getBlockData().set(FACING, entityliving.getDirection()).set(OPEN, Boolean.FALSE).set(POWERED, Boolean.FALSE).set(IN_WALL, Boolean.FALSE);
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (iblockdata.get(OPEN)) {
         iblockdata = iblockdata.set(OPEN, Boolean.FALSE);
      } else {
         EnumDirection enumdirection1 = EnumDirection.fromAngle(entityhuman.yaw);
         if (iblockdata.get(FACING) == enumdirection1.opposite()) {
            iblockdata = iblockdata.set(FACING, enumdirection1);
         }

         iblockdata = iblockdata.set(OPEN, Boolean.TRUE);
      }

      world.setTypeAndData(blockposition, iblockdata, 2);
      world.a(entityhuman, iblockdata.get(OPEN) ? 1003 : 1006, blockposition, 0);
      return true;
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      boolean flag = world.isBlockIndirectlyPowered(blockposition);
      if (flag || block.isPowerSource()) {
         if (flag && !iblockdata.get(OPEN) && !iblockdata.get(POWERED)) {
            world.setTypeAndData(blockposition, iblockdata.set(OPEN, Boolean.TRUE).set(POWERED, Boolean.TRUE), 2);
            world.a(null, 1003, blockposition, 0);
         } else if (!flag && iblockdata.get(OPEN) && iblockdata.get(POWERED)) {
            world.setTypeAndData(blockposition, iblockdata.set(OPEN, Boolean.FALSE).set(POWERED, Boolean.FALSE), 2);
            world.a(null, 1006, blockposition, 0);
         } else if (flag != iblockdata.get(POWERED)) {
            world.setTypeAndData(blockposition, iblockdata.set(POWERED, flag), 2);
         }
      }
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(FACING, EnumDirection.fromType2(i)).set(OPEN, (i & 4) != 0).set(POWERED, (i & 8) != 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(FACING).b();
      if (iblockdata.get(POWERED)) {
         i |= 8;
      }

      if (iblockdata.get(OPEN)) {
         i |= 4;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING, OPEN, POWERED, IN_WALL);
   }
}
