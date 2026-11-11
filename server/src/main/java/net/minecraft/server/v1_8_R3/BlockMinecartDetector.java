package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockMinecartDetector extends BlockMinecartTrackAbstract {
   public static final BlockStateEnum<BlockMinecartTrackAbstract.EnumTrackPosition> SHAPE = BlockStateEnum.a(
      "shape",
      BlockMinecartTrackAbstract.EnumTrackPosition.class,
      new Predicate() {
         public boolean a(BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition) {
            return blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST
               && blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST
               && blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST
               && blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST;
         }

         public boolean apply(Object object) {
            return this.a((BlockMinecartTrackAbstract.EnumTrackPosition)object);
         }
      }
   );
   public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");

   public BlockMinecartDetector() {
      super(true);
      this.j(this.blockStateList.getBlockData().set(POWERED, Boolean.FALSE).set(SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH));
      this.a(true);
   }

   @Override
   public int a(World world) {
      return 20;
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
      if (!iblockdata.get(POWERED)) {
         this.e(world, blockposition, iblockdata);
      }
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if (iblockdata.get(POWERED)) {
         this.e(world, blockposition, iblockdata);
      }
   }

   @Override
   public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return iblockdata.get(POWERED) ? 15 : 0;
   }

   @Override
   public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return !iblockdata.get(POWERED) ? 0 : (enumdirection == EnumDirection.UP ? 15 : 0);
   }

   private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      boolean flag = iblockdata.get(POWERED);
      boolean flag1 = false;
      List<EntityMinecartAbstract> list = this.a(world, blockposition, EntityMinecartAbstract.class);
      if (!list.isEmpty()) {
         flag1 = true;
      }

      int x = blockposition.getX();
      int y = blockposition.getY();
      int z = blockposition.getZ();
      if (flag != flag1) {
         org.bukkit.block.Block block = world.getWorld().getBlockAt(x, y, z);
         BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, flag ? 15 : 0, flag1 ? 15 : 0);
         world.getServer().getPluginManager().callEvent(eventRedstone);
         flag1 = eventRedstone.getNewCurrent() > 0;
      }

      if (flag1 && !flag) {
         world.setTypeAndData(blockposition, iblockdata.set(POWERED, true), 3);
         world.applyPhysics(x, y, z, this);
         world.applyPhysics(x, y - 1, z, this);
         world.b(x, y, z, x, y, z);
      }

      if (!flag1 && flag) {
         world.setTypeAndData(blockposition, iblockdata.set(POWERED, false), 3);
         world.applyPhysics(x, y, z, this);
         world.applyPhysics(x, y - 1, z, this);
         world.b(x, y, z, x, y, z);
      }

      if (flag1) {
         world.a(blockposition, this, this.a(world));
      }

      world.updateAdjacentComparators(new BlockPosition(x, y, z), this);
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.onPlace(world, blockposition, iblockdata);
      this.e(world, blockposition, iblockdata);
   }

   @Override
   public IBlockState<BlockMinecartTrackAbstract.EnumTrackPosition> n() {
      return SHAPE;
   }

   @Override
   public boolean isComplexRedstone() {
      return true;
   }

   @Override
   public int l(World world, BlockPosition blockposition) {
      if (world.getType(blockposition).get(POWERED)) {
         List<EntityMinecartCommandBlock> list = this.a(world, blockposition, EntityMinecartCommandBlock.class);
         if (!list.isEmpty()) {
            return list.get(0).getCommandBlock().j();
         }

         List<EntityMinecartAbstract> list1 = this.a(world, blockposition, EntityMinecartAbstract.class, IEntitySelector.c);
         if (!list1.isEmpty()) {
            return Container.b((IInventory)list1.get(0));
         }
      }

      return 0;
   }

   protected <T extends EntityMinecartAbstract> List<T> a(World world, BlockPosition blockposition, Class<T> oclass, Predicate<Entity>... apredicate) {
      AxisAlignedBB axisalignedbb = this.a(blockposition);
      return apredicate.length != 1 ? world.a(oclass, axisalignedbb) : world.a(oclass, axisalignedbb, apredicate[0]);
   }

   private AxisAlignedBB a(BlockPosition blockposition) {
      return new AxisAlignedBB(
         blockposition.getX() + 0.2F,
         blockposition.getY(),
         blockposition.getZ() + 0.2F,
         blockposition.getX() + 1 - 0.2F,
         blockposition.getY() + 1 - 0.2F,
         blockposition.getZ() + 1 - 0.2F
      );
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.a(i & 7)).set(POWERED, (i & 8) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(SHAPE).a();
      if (iblockdata.get(POWERED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, SHAPE, POWERED);
   }
}
