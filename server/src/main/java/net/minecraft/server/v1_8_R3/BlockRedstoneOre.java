package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockRedstoneOre extends Block {
   public BlockRedstoneOre(boolean flag) {
      super(Material.STONE);
      if (flag) {
         this.a(true);
      }
   }

   @Override
   public int a(World world) {
      return 30;
   }

   @Override
   public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
      this.e(world, blockposition, entityhuman);
      super.attack(world, blockposition, entityhuman);
   }

   @Override
   public void a(World world, BlockPosition blockposition, Entity entity) {
      if (entity instanceof EntityHuman) {
         PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent((EntityHuman)entity, Action.PHYSICAL, blockposition, null, null);
         if (!event.isCancelled()) {
            this.e(world, blockposition, entity);
            super.a(world, blockposition, entity);
         }
      } else {
         EntityInteractEvent event = new EntityInteractEvent(
            entity.getBukkitEntity(), world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ())
         );
         world.getServer().getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            this.e(world, blockposition, entity);
            super.a(world, blockposition, entity);
         }
      }
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      this.e(world, blockposition, entityhuman);
      return super.interact(world, blockposition, iblockdata, entityhuman, enumdirection, f, f1, f2);
   }

   private void e(World world, BlockPosition blockposition, Entity entity) {
      if (this == Blocks.REDSTONE_ORE) {
         if (CraftEventFactory.callEntityChangeBlockEvent(entity, blockposition.getX(), blockposition.getY(), blockposition.getZ(), Blocks.LIT_REDSTONE_ORE, 0)
            .isCancelled()) {
            return;
         }

         world.setTypeUpdate(blockposition, Blocks.LIT_REDSTONE_ORE.getBlockData());
      }
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if (this == Blocks.LIT_REDSTONE_ORE) {
         if (CraftEventFactory.callBlockFadeEvent(
               world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), Blocks.REDSTONE_ORE
            )
            .isCancelled()) {
            return;
         }

         world.setTypeUpdate(blockposition, Blocks.REDSTONE_ORE.getBlockData());
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Items.REDSTONE;
   }

   @Override
   public int getDropCount(int i, Random random) {
      return this.a(random) + random.nextInt(i + 1);
   }

   @Override
   public int a(Random random) {
      return 4 + random.nextInt(2);
   }

   @Override
   public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
      super.dropNaturally(world, blockposition, iblockdata, f, i);
   }

   @Override
   public int getExpDrop(World world, IBlockData data, int i) {
      return this.getDropType(data, world.random, i) != Item.getItemOf(this) ? 1 + world.random.nextInt(5) : 0;
   }

   private void f(World world, BlockPosition blockposition) {
      Random random = world.random;
      double d0 = 0.0625;

      for (int i = 0; i < 6; i++) {
         double d1 = blockposition.getX() + random.nextFloat();
         double d2 = blockposition.getY() + random.nextFloat();
         double d3 = blockposition.getZ() + random.nextFloat();
         if (i == 0 && !world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().c()) {
            d2 = blockposition.getY() + d0 + 1.0;
         }

         if (i == 1 && !world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock().c()) {
            d2 = blockposition.getY() - d0;
         }

         if (i == 2 && !world.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1).getBlock().c()) {
            d3 = blockposition.getZ() + d0 + 1.0;
         }

         if (i == 3 && !world.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1).getBlock().c()) {
            d3 = blockposition.getZ() - d0;
         }

         if (i == 4 && !world.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ()).getBlock().c()) {
            d1 = blockposition.getX() + d0 + 1.0;
         }

         if (i == 5 && !world.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ()).getBlock().c()) {
            d1 = blockposition.getX() - d0;
         }

         if (d1 < blockposition.getX()
            || d1 > blockposition.getX() + 1
            || d2 < 0.0
            || d2 > blockposition.getY() + 1
            || d3 < blockposition.getZ()
            || d3 > blockposition.getZ() + 1) {
            world.addParticle(EnumParticle.REDSTONE, d1, d2, d3, 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
         }
      }
   }

   @Override
   protected ItemStack i(IBlockData iblockdata) {
      return new ItemStack(Blocks.REDSTONE_ORE);
   }
}
