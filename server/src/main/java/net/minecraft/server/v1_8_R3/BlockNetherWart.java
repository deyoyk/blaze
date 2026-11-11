package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class BlockNetherWart extends BlockPlant {
   public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 3);

   protected BlockNetherWart() {
      super(Material.PLANT, MaterialMapColor.D);
      this.j(this.blockStateList.getBlockData().set(AGE, 0));
      this.a(true);
      float f = 0.5F;
      this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
      this.a((CreativeModeTab)null);
   }

   @Override
   protected boolean c(Block block) {
      return block == Blocks.SOUL_SAND;
   }

   @Override
   public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return this.c(world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock());
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      int i = iblockdata.get(AGE);
      if (i < 3 && random.nextInt(Math.max(1, (int)world.growthOdds / world.spigotConfig.wartModifier * 10)) == 0) {
         iblockdata = iblockdata.set(AGE, i + 1);
         CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this, this.toLegacyData(iblockdata));
      }

      super.b(world, blockposition, iblockdata, random);
   }

   @Override
   public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
      int j = 1;
      if (iblockdata.get(AGE) >= 3) {
         j = 2 + world.random.nextInt(3);
         if (i > 0) {
            j += world.random.nextInt(i + 1);
         }
      }

      for (int k = 0; k < j; k++) {
         this.a(world, blockposition, new ItemStack(Items.NETHER_WART));
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return null;
   }

   @Override
   public int a(Random random) {
      return 0;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(AGE, i);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(AGE);
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, AGE);
   }
}
