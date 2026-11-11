package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockRepeater extends BlockDiodeAbstract {
   public static final BlockStateBoolean LOCKED = BlockStateBoolean.of("locked");
   public static final BlockStateInteger DELAY = BlockStateInteger.of("delay", 1, 4);

   protected BlockRepeater(boolean flag) {
      super(flag);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(DELAY, 1).set(LOCKED, false));
   }

   @Override
   public String getName() {
      return LocaleI18n.get("item.diode.name");
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      return iblockdata.set(LOCKED, this.b(iblockaccess, blockposition, iblockdata));
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (!entityhuman.abilities.mayBuild) {
         return false;
      } else {
         world.setTypeAndData(blockposition, iblockdata.a(DELAY), 3, false, false);
         return true;
      }
   }

   @Override
   protected int d(IBlockData iblockdata) {
      return iblockdata.get(DELAY) * 2;
   }

   @Override
   protected IBlockData e(IBlockData iblockdata) {
      return Blocks.POWERED_REPEATER.getBlockData().set(FACING, iblockdata.get(FACING)).set(DELAY, iblockdata.get(DELAY)).set(LOCKED, iblockdata.get(LOCKED));
   }

   @Override
   protected IBlockData k(IBlockData iblockdata) {
      return Blocks.UNPOWERED_REPEATER.getBlockData().set(FACING, iblockdata.get(FACING)).set(DELAY, iblockdata.get(DELAY)).set(LOCKED, iblockdata.get(LOCKED));
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Items.REPEATER;
   }

   @Override
   public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
      return this.c(iblockaccess, blockposition, iblockdata) > 0;
   }

   @Override
   protected boolean c(Block block) {
      return d(block);
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.remove(world, blockposition, iblockdata);
      this.h(world, blockposition, iblockdata);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(FACING, EnumDirection.fromType2(i)).set(LOCKED, false).set(DELAY, 1 + (i >> 2));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(FACING).b();
      return i | iblockdata.get(DELAY) - 1 << 2;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, FACING, DELAY, LOCKED);
   }
}
