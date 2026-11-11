package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;

public class BlockSoil extends Block {
   public static final BlockStateInteger MOISTURE = BlockStateInteger.of("moisture", 0, 7);

   protected BlockSoil() {
      super(Material.EARTH);
      this.j(this.blockStateList.getBlockData().set(MOISTURE, 0));
      this.a(true);
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
      this.e(255);
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return new AxisAlignedBB(
         blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition.getX() + 1, blockposition.getY() + 1, blockposition.getZ() + 1
      );
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
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      int i = iblockdata.get(MOISTURE);
      if (!this.f(world, blockposition) && !world.isRainingAt(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ())) {
         if (i > 0) {
            world.setTypeAndData(blockposition, iblockdata.set(MOISTURE, i - 1), 2);
         } else if (!this.e(world, blockposition)) {
            org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            if (CraftEventFactory.callBlockFadeEvent(block, Blocks.DIRT).isCancelled()) {
               return;
            }

            world.setTypeUpdate(blockposition, Blocks.DIRT.getBlockData());
         }
      } else if (i < 7) {
         world.setTypeAndData(blockposition, iblockdata.set(MOISTURE, 7), 2);
      }
   }

   @Override
   public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
      super.fallOn(world, blockposition, entity, f);
      if (entity instanceof EntityLiving && world.random.nextFloat() < f - 0.5F) {
         if (!(entity instanceof EntityHuman) && !world.getGameRules().getBoolean("mobGriefing")) {
            return;
         }

         Cancellable cancellable;
         if (entity instanceof EntityHuman) {
            cancellable = CraftEventFactory.callPlayerInteractEvent((EntityHuman)entity, Action.PHYSICAL, blockposition, null, null);
         } else {
            cancellable = new EntityInteractEvent(
               entity.getBukkitEntity(), world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ())
            );
            world.getServer().getPluginManager().callEvent((EntityInteractEvent)cancellable);
         }

         if (cancellable.isCancelled()) {
            return;
         }

         if (CraftEventFactory.callEntityChangeBlockEvent(entity, blockposition.getX(), blockposition.getY(), blockposition.getZ(), Blocks.DIRT, 0)
            .isCancelled()) {
            return;
         }

         world.setTypeUpdate(blockposition, Blocks.DIRT.getBlockData());
      }
   }

   private boolean e(World world, BlockPosition blockposition) {
      Block block = world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock();
      return block instanceof BlockCrops || block instanceof BlockStem;
   }

   private boolean f(World world, BlockPosition blockposition) {
      for (BlockPosition.MutableBlockPosition blockposition_mutableblockposition : BlockPosition.b(blockposition.a(-4, 0, -4), blockposition.a(4, 1, 4))) {
         if (world.getType(blockposition_mutableblockposition).getBlock().getMaterial() == Material.WATER) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      super.doPhysics(world, blockposition, iblockdata, block);
      if (world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().getMaterial().isBuildable()) {
         world.setTypeUpdate(blockposition, Blocks.DIRT.getBlockData());
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Blocks.DIRT.getDropType(Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT), random, i);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(MOISTURE, i & 7);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(MOISTURE);
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, MOISTURE);
   }
}
