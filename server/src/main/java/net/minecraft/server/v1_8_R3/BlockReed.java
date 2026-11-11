package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class BlockReed extends Block {
   public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 15);

   protected BlockReed() {
      super(Material.PLANT);
      this.j(this.blockStateList.getBlockData().set(AGE, 0));
      float f = 0.375F;
      this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
      this.a(true);
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if ((
            world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock() == Blocks.REEDS
               || (Boolean)BlazeConfig.dropFloatingPlants.getValue() && this.e(world, blockposition, iblockdata)
         )
         && world.isEmpty(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ())) {
         int i = 1;

         while (world.getType(blockposition.getX(), blockposition.getY() - i, blockposition.getZ()).getBlock() == this) {
            i++;
         }

         if (i < world.paperSpigotConfig.reedMaxHeight) {
            int j = iblockdata.get(AGE);
            if (j >= (byte)range(3.0F, world.growthOdds / world.spigotConfig.caneModifier * 15.0F + 0.5F, 15.0F)) {
               BlockPosition upPos = blockposition.up();
               CraftEventFactory.handleBlockGrowEvent(world, upPos.getX(), upPos.getY(), upPos.getZ(), this, 0);
               world.setTypeAndData(blockposition, iblockdata.set(AGE, 0), 4);
            } else {
               world.setTypeAndData(blockposition, iblockdata.set(AGE, j + 1), 4);
            }
         }
      }
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      Block block = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
      if (block == this) {
         return true;
      } else if (block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.SAND) {
         return false;
      } else {
         for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            if (world.getType(blockposition.shift(enumdirection).down()).getBlock().getMaterial() == Material.WATER) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      this.e(world, blockposition, iblockdata);
   }

   protected final boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (this.e(world, blockposition)) {
         return true;
      } else {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
         return false;
      }
   }

   public boolean e(World world, BlockPosition blockposition) {
      return this.canPlace(world, blockposition);
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return null;
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Items.REEDS;
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
