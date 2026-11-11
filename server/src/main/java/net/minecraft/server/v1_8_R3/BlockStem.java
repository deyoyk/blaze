package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class BlockStem extends BlockPlant implements IBlockFragilePlantElement {
   public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 7);
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", new Predicate() {
      public boolean a(EnumDirection enumdirection) {
         return enumdirection != EnumDirection.DOWN;
      }

      public boolean apply(Object object) {
         return this.a((EnumDirection)object);
      }
   });
   private final Block blockFruit;

   protected BlockStem(Block block) {
      this.j(this.blockStateList.getBlockData().set(AGE, 0).set(FACING, EnumDirection.UP));
      this.blockFruit = block;
      this.a(true);
      float f = 0.125F;
      this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
      this.a((CreativeModeTab)null);
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      iblockdata = iblockdata.set(FACING, EnumDirection.UP);

      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         if (iblockaccess.getType(blockposition.shift(enumdirection)).getBlock() == this.blockFruit) {
            iblockdata = iblockdata.set(FACING, enumdirection);
            break;
         }
      }

      return iblockdata;
   }

   @Override
   protected boolean c(Block block) {
      return block == Blocks.FARMLAND;
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      super.b(world, blockposition, iblockdata, random);
      if (world.getLightLevel(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()) >= 9) {
         float f = BlockCrops.a(this, world, blockposition);
         if (random.nextInt(
               (int)(world.growthOdds / (this == Blocks.PUMPKIN_STEM ? world.spigotConfig.pumpkinModifier : world.spigotConfig.melonModifier) * (25.0F / f))
                  + 1
            )
            == 0) {
            int i = iblockdata.get(AGE);
            if (i < 7) {
               iblockdata = iblockdata.set(AGE, i + 1);
               CraftEventFactory.handleBlockGrowEvent(
                  world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this, this.toLegacyData(iblockdata)
               );
            } else {
               for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                  if (world.getType(blockposition.shift(enumdirection)).getBlock() == this.blockFruit) {
                     return;
                  }
               }

               blockposition = blockposition.shift(EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random));
               Block block = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
               if (world.getType(blockposition).getBlock().material == Material.AIR
                  && (block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.GRASS)) {
                  CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this.blockFruit, 0);
               }
            }
         }
      }
   }

   public void g(World world, BlockPosition blockposition, IBlockData iblockdata) {
      int i = iblockdata.get(AGE) + MathHelper.nextInt(world.random, 2, 5);
      CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this, Math.min(7, i));
   }

   @Override
   public void j() {
      float f = 0.125F;
      this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      this.maxY = (iblockaccess.getType(blockposition).get(AGE) * 2 + 2) / 16.0F;
      float f = 0.125F;
      this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float)this.maxY, 0.5F + f);
   }

   @Override
   public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
      super.dropNaturally(world, blockposition, iblockdata, f, i);
      Item item = this.l();
      if (item != null) {
         int j = iblockdata.get(AGE);

         for (int k = 0; k < 3; k++) {
            if (world.random.nextInt(15) <= j) {
               this.a(world, blockposition, new ItemStack(item));
            }
         }
      }
   }

   protected Item l() {
      return this.blockFruit == Blocks.PUMPKIN ? Items.PUMPKIN_SEEDS : (this.blockFruit == Blocks.MELON_BLOCK ? Items.MELON_SEEDS : null);
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return null;
   }

   @Override
   public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
      return iblockdata.get(AGE) != 7;
   }

   @Override
   public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      return true;
   }

   @Override
   public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      this.g(world, blockposition, iblockdata);
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
      return new BlockStateList(this, AGE, FACING);
   }
}
