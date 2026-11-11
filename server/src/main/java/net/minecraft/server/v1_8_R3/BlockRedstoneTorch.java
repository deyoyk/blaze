package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.PluginManager;

public class BlockRedstoneTorch extends BlockTorch {
   private final boolean isOn;

   protected BlockRedstoneTorch(boolean flag) {
      this.isOn = flag;
      this.a(true);
      this.a((CreativeModeTab)null);
   }

   @Override
   public int a(World world) {
      return 2;
   }

   public void applyPhysics(World world, BlockPosition blockposition) {
      if (this.isOn) {
         int x = blockposition.getX();
         int y = blockposition.getY();
         int z = blockposition.getZ();
         world.applyPhysics(x, y - 1, z, this);
         world.applyPhysics(x, y + 1, z, this);
         world.applyPhysics(x - 1, y, z, this);
         world.applyPhysics(x + 1, y, z, this);
         world.applyPhysics(x, y, z + 1, this);
         world.applyPhysics(x, y, z - 1, this);
      }
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.applyPhysics(world, blockposition);
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.applyPhysics(world, blockposition);
   }

   @Override
   public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return this.isOn && iblockdata.get(FACING) != enumdirection ? 15 : 0;
   }

   private boolean g(World world, BlockPosition blockposition, IBlockData iblockdata) {
      EnumDirection enumdirection = iblockdata.get(FACING).opposite();
      return world.isBlockFacePowered(blockposition.shift(enumdirection), enumdirection);
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      boolean flag = this.g(world, blockposition, iblockdata);
      if (this.isOn == flag) {
         PluginManager manager = world.getServer().getPluginManager();
         org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
         int oldCurrent = this.isOn ? 15 : 0;
         BlockRedstoneEvent event = new BlockRedstoneEvent(block, oldCurrent, oldCurrent);
         if (this.isOn) {
            event.setNewCurrent(0);
            manager.callEvent(event);
            if (event.getNewCurrent() != 0) {
               return;
            }

            world.setTypeAndData(blockposition, Blocks.UNLIT_REDSTONE_TORCH.getBlockData().set(FACING, iblockdata.get(FACING)), 3);
         } else {
            event.setNewCurrent(15);
            manager.callEvent(event);
            if (event.getNewCurrent() != 15) {
               return;
            }

            world.setTypeAndData(blockposition, Blocks.REDSTONE_TORCH.getBlockData().set(FACING, iblockdata.get(FACING)), 3);
         }
      }
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (!this.e(world, blockposition, iblockdata) && this.isOn == this.g(world, blockposition, iblockdata)) {
         world.a(blockposition, this, this.a(world));
      }
   }

   @Override
   public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return enumdirection == EnumDirection.DOWN ? this.a(iblockaccess, blockposition, iblockdata, enumdirection) : 0;
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Item.getItemOf(Blocks.REDSTONE_TORCH);
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public boolean b(Block block) {
      return block == Blocks.UNLIT_REDSTONE_TORCH || block == Blocks.REDSTONE_TORCH;
   }
}
