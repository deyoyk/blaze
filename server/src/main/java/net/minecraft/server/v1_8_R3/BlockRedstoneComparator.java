package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.Random;

public class BlockRedstoneComparator extends BlockDiodeAbstract implements IContainer {
   public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
   public static final BlockStateEnum<BlockRedstoneComparator.EnumComparatorMode> MODE = BlockStateEnum.of(
      "mode", BlockRedstoneComparator.EnumComparatorMode.class
   );

   public BlockRedstoneComparator(boolean flag) {
      super(flag);
      this.j(
         this.blockStateList
            .getBlockData()
            .set(FACING, EnumDirection.NORTH)
            .set(POWERED, Boolean.FALSE)
            .set(MODE, BlockRedstoneComparator.EnumComparatorMode.COMPARE)
      );
      this.isTileEntity = true;
   }

   @Override
   public String getName() {
      return LocaleI18n.get("item.comparator.name");
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Items.COMPARATOR;
   }

   @Override
   protected int d(IBlockData iblockdata) {
      return 2;
   }

   @Override
   protected IBlockData e(IBlockData iblockdata) {
      Boolean obool = iblockdata.get(POWERED);
      BlockRedstoneComparator.EnumComparatorMode blockredstonecomparator_enumcomparatormode = iblockdata.get(MODE);
      EnumDirection enumdirection = iblockdata.get(FACING);
      return Blocks.POWERED_COMPARATOR.getBlockData().set(FACING, enumdirection).set(POWERED, obool).set(MODE, blockredstonecomparator_enumcomparatormode);
   }

   @Override
   protected IBlockData k(IBlockData iblockdata) {
      Boolean obool = iblockdata.get(POWERED);
      BlockRedstoneComparator.EnumComparatorMode blockredstonecomparator_enumcomparatormode = iblockdata.get(MODE);
      EnumDirection enumdirection = iblockdata.get(FACING);
      return Blocks.UNPOWERED_COMPARATOR.getBlockData().set(FACING, enumdirection).set(POWERED, obool).set(MODE, blockredstonecomparator_enumcomparatormode);
   }

   @Override
   protected boolean l(IBlockData iblockdata) {
      return this.N || iblockdata.get(POWERED);
   }

   @Override
   protected int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
      TileEntity tileentity = iblockaccess.getTileEntity(blockposition);
      return tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).b() : 0;
   }

   private int j(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return iblockdata.get(MODE) == BlockRedstoneComparator.EnumComparatorMode.SUBTRACT
         ? Math.max(this.f(world, blockposition, iblockdata) - this.c(world, blockposition, iblockdata), 0)
         : this.f(world, blockposition, iblockdata);
   }

   @Override
   protected boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      int i = this.f(world, blockposition, iblockdata);
      if (i >= 15) {
         return true;
      } else if (i == 0) {
         return false;
      } else {
         int j = this.c(world, blockposition, iblockdata);
         return j == 0 || i >= j;
      }
   }

   @Override
   protected int f(World world, BlockPosition blockposition, IBlockData iblockdata) {
      int i = super.f(world, blockposition, iblockdata);
      EnumDirection enumdirection = iblockdata.get(FACING);
      BlockPosition blockposition1 = blockposition.shift(enumdirection);
      Block block = world.getType(blockposition1).getBlock();
      if (block.isComplexRedstone()) {
         i = block.l(world, blockposition1);
      } else if (i < 15 && block.isOccluding()) {
         blockposition1 = blockposition1.shift(enumdirection);
         block = world.getType(blockposition1).getBlock();
         if (block.isComplexRedstone()) {
            i = block.l(world, blockposition1);
         } else if (block.getMaterial() == Material.AIR) {
            EntityItemFrame entityitemframe = this.a(world, enumdirection, blockposition1);
            if (entityitemframe != null) {
               i = entityitemframe.q();
            }
         }
      }

      return i;
   }

   private EntityItemFrame a(World world, EnumDirection enumdirection, BlockPosition blockposition) {
      List<EntityItemFrame> list = world.a(
         EntityItemFrame.class,
         new AxisAlignedBB(
            blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition.getX() + 1, blockposition.getY() + 1, blockposition.getZ() + 1
         ),
         (com.google.common.base.Predicate<EntityItemFrame>)(entity -> entity != null && entity.getDirection() == enumdirection)
      );
      return list.size() == 1 ? list.get(0) : null;
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (!entityhuman.abilities.mayBuild) {
         return false;
      } else {
         iblockdata = iblockdata.a(MODE);
         world.makeSound(
            blockposition.getX() + 0.5,
            blockposition.getY() + 0.5,
            blockposition.getZ() + 0.5,
            "random.click",
            0.3F,
            iblockdata.get(MODE) == BlockRedstoneComparator.EnumComparatorMode.SUBTRACT ? 0.55F : 0.5F
         );
         world.setTypeAndData(blockposition, iblockdata, 2);
         this.k(world, blockposition, iblockdata);
         return true;
      }
   }

   @Override
   protected void g(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (!world.a(blockposition, this)) {
         int i = this.j(world, blockposition, iblockdata);
         TileEntity tileentity = world.getTileEntity(blockposition);
         int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).b() : 0;
         if (i != j || this.l(iblockdata) != this.e(world, blockposition, iblockdata)) {
            if (this.i(world, blockposition, iblockdata)) {
               world.a(blockposition, this, 2, -1);
            } else {
               world.a(blockposition, this, 2, 0);
            }
         }
      }
   }

   @Override
   protected void g(World world, Chunk chunk, BlockPosition blockposition, IBlockData iblockdata) {
      if (!world.a(blockposition, this)) {
         int i = this.j(world, blockposition, iblockdata);
         TileEntity tileentity = world.getTileEntity(chunk, blockposition, iblockdata.getBlock());
         int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator)tileentity).b() : 0;
         if (i != j || this.l(iblockdata) != this.e(world, blockposition, iblockdata)) {
            if (this.i(world, blockposition, iblockdata)) {
               world.a(blockposition, chunk, this, 2, -1);
            } else {
               world.a(blockposition, chunk, this, 2, 0);
            }
         }
      }
   }

   private void k(World world, BlockPosition blockposition, IBlockData iblockdata) {
      int i = this.j(world, blockposition, iblockdata);
      TileEntity tileentity = world.getTileEntity(blockposition);
      int j = 0;
      if (tileentity instanceof TileEntityComparator) {
         TileEntityComparator tileentitycomparator = (TileEntityComparator)tileentity;
         j = tileentitycomparator.b();
         tileentitycomparator.a(i);
      }

      if (j != i || iblockdata.get(MODE) == BlockRedstoneComparator.EnumComparatorMode.COMPARE) {
         boolean flag = this.e(world, blockposition, iblockdata);
         boolean flag1 = this.l(iblockdata);
         if (flag1 && !flag) {
            world.setTypeAndData(blockposition, iblockdata.set(POWERED, false), 2);
         } else if (!flag1 && flag) {
            world.setTypeAndData(blockposition, iblockdata.set(POWERED, true), 2);
         }

         this.h(world, blockposition, iblockdata);
      }
   }

   private void k(World world, Chunk chunk, BlockPosition blockposition, IBlockData iblockdata) {
      int i = this.j(world, blockposition, iblockdata);
      TileEntity tileentity = world.getTileEntity(chunk, blockposition, iblockdata.getBlock());
      int j = 0;
      if (tileentity instanceof TileEntityComparator) {
         TileEntityComparator tileentitycomparator = (TileEntityComparator)tileentity;
         j = tileentitycomparator.b();
         tileentitycomparator.a(i);
      }

      if (j != i || iblockdata.get(MODE) == BlockRedstoneComparator.EnumComparatorMode.COMPARE) {
         boolean flag = this.e(world, blockposition, iblockdata);
         boolean flag1 = this.l(iblockdata);
         if (flag1 && !flag) {
            world.setTypeAndData(chunk, blockposition, iblockdata.set(POWERED, false), 2, false, false);
         } else if (!flag1 && flag) {
            world.setTypeAndData(chunk, blockposition, iblockdata.set(POWERED, true), 2, false, false);
         }

         this.h(world, blockposition, iblockdata);
      }
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if (this.N) {
         world.setTypeAndData(blockposition, this.k(iblockdata).set(POWERED, true), 4);
      }

      this.k(world, blockposition, iblockdata);
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.onPlace(world, blockposition, iblockdata);
      world.setTileEntity(blockposition, this.a(world, 0));
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.remove(world, blockposition, iblockdata);
      world.t(blockposition);
      this.h(world, blockposition, iblockdata);
   }

   @Override
   public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
      super.a(world, blockposition, iblockdata, i, j);
      TileEntity tileentity = world.getTileEntity(blockposition);
      return tileentity != null && tileentity.c(i, j);
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntityComparator();
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData()
         .set(FACING, EnumDirection.fromType2(i))
         .set(POWERED, (i & 8) > 0)
         .set(MODE, (i & 4) > 0 ? BlockRedstoneComparator.EnumComparatorMode.SUBTRACT : BlockRedstoneComparator.EnumComparatorMode.COMPARE);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(FACING).b();
      if (iblockdata.get(POWERED)) {
         i |= 8;
      }

      if (iblockdata.get(MODE) == BlockRedstoneComparator.EnumComparatorMode.SUBTRACT) {
         i |= 4;
      }

      return i;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING, MODE, POWERED);
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return this.getBlockData()
         .set(FACING, entityliving.getDirection().opposite())
         .set(POWERED, Boolean.FALSE)
         .set(MODE, BlockRedstoneComparator.EnumComparatorMode.COMPARE);
   }

   public static enum EnumComparatorMode implements INamable {
      COMPARE("compare"),
      SUBTRACT("subtract");

      private final String c;

      private EnumComparatorMode(String s) {
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
}
