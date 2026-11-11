package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class BlockCactus extends Block {
   public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 15);

   protected BlockCactus() {
      super(Material.CACTUS);
      this.j(this.blockStateList.getBlockData().set(AGE, 0));
      this.a(true);
      this.a(CreativeModeTab.c);
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      BlockPosition blockposition1 = blockposition.up();
      if (world.isEmpty(blockposition1)) {
         int i = 1;

         while (world.getType(blockposition.getX(), blockposition.getY() - i, blockposition.getZ()).getBlock() == this) {
            i++;
         }

         if (i < world.paperSpigotConfig.cactusMaxHeight) {
            int j = iblockdata.get(AGE);
            if (j >= (byte)range(3.0F, world.growthOdds / world.spigotConfig.cactusModifier * 15.0F + 0.5F, 15.0F)) {
               IBlockData iblockdata1 = iblockdata.set(AGE, 0);
               CraftEventFactory.handleBlockGrowEvent(world, blockposition1.getX(), blockposition1.getY(), blockposition1.getZ(), this, 0);
               world.setTypeAndData(blockposition, iblockdata1, 4);
               this.doPhysics(world, blockposition1, iblockdata1, this);
            } else {
               world.setTypeAndData(blockposition, iblockdata.set(AGE, j + 1), 4);
            }
         }
      }
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return new AxisAlignedBB(
         blockposition.getX() + 0.0625F,
         blockposition.getY(),
         blockposition.getZ() + 0.0625F,
         blockposition.getX() + 1 - 0.0625F,
         blockposition.getY() + 1 - 0.0625F,
         blockposition.getZ() + 1 - 0.0625F
      );
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      return super.canPlace(world, blockposition) && this.e(world, blockposition);
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (!this.e(world, blockposition)) {
         world.setAir(blockposition, true);
      }
   }

   public boolean e(World world, BlockPosition blockposition) {
      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         if (world.getType(blockposition.getX() + enumdirection.getAdjacentX(), blockposition.getY(), blockposition.getZ() + enumdirection.getAdjacentZ())
            .getBlock()
            .getMaterial()
            .isBuildable()) {
            return false;
         }
      }

      Block block = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
      return block == Blocks.CACTUS || block == Blocks.SAND;
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
      CraftEventFactory.blockDamage = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
      entity.damageEntity(DamageSource.CACTUS, 1.0F);
      CraftEventFactory.blockDamage = null;
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
   protected BlockStateList getStateList() {
      return new BlockStateList(this, AGE);
   }
}
