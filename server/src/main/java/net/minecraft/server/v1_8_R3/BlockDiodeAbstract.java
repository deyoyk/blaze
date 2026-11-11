package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public abstract class BlockDiodeAbstract extends BlockDirectional {
   protected final boolean N;

   protected BlockDiodeAbstract(boolean flag) {
      super(Material.ORIENTABLE);
      this.N = flag;
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   public static boolean d(Block block) {
      return Blocks.UNPOWERED_REPEATER.e(block) || Blocks.UNPOWERED_COMPARATOR.e(block);
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      return World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()) && super.canPlace(world, blockposition);
   }

   public boolean e(World world, BlockPosition blockposition) {
      return World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
   }

   public boolean e(World world, Chunk chunk, BlockPosition blockposition) {
      return World.a(world, chunk, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if (!this.b(world, blockposition, iblockdata)) {
         boolean flag = this.e(world, blockposition, iblockdata);
         if (this.N && !flag) {
            if (CraftEventFactory.callRedstoneChange(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 15, 0).getNewCurrent() != 0) {
               return;
            }

            world.setTypeAndData(blockposition, this.k(iblockdata), 2, false, false);
         } else if (!this.N) {
            if (CraftEventFactory.callRedstoneChange(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 0, 15).getNewCurrent() != 15) {
               return;
            }

            world.setTypeAndData(blockposition, this.e(iblockdata), 2, false, false);
            if (!flag) {
               world.a(blockposition, this.e(iblockdata).getBlock(), this.m(iblockdata), -1);
            }
         }
      }
   }

   protected boolean l(IBlockData iblockdata) {
      return this.N;
   }

   @Override
   public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return this.a(iblockaccess, blockposition, iblockdata, enumdirection);
   }

   @Override
   public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return !this.l(iblockdata) ? 0 : (iblockdata.get(FACING) == enumdirection ? this.a(iblockaccess, blockposition, iblockdata) : 0);
   }

   protected void g(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (!this.b(world, blockposition, iblockdata)) {
         boolean flag = this.e(world, blockposition, iblockdata);
         if ((this.N && !flag || !this.N && flag) && !world.a(blockposition, this)) {
            byte b0 = -1;
            if (this.i(world, blockposition, iblockdata)) {
               b0 = -3;
            } else if (this.N) {
               b0 = -2;
            }

            world.a(blockposition, this, this.d(iblockdata), b0);
         }
      }
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (this.e(world, blockposition)) {
         this.g(world, blockposition, iblockdata);
      } else {
         this.b(world, blockposition, iblockdata, 0);
         world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3, false, false);
         this.applyPhysics(world, blockposition);
      }
   }

   @Override
   public void doChunkPhysics(World world, Chunk chunk, BlockPosition blockposition, IBlockData iblockdata, Block block, BlockPosition source) {
      if (this.e(world, chunk, blockposition)) {
         this.g(world, chunk, blockposition, iblockdata);
      } else {
         this.b(world, blockposition, iblockdata, 0);
         world.setTypeAndData(chunk, blockposition, Blocks.AIR.getBlockData(), 3, false, false);
         this.applyPhysics(world, blockposition);
      }
   }

   protected void g(World world, Chunk chunk, BlockPosition blockposition, IBlockData iblockdata) {
      if (!this.b(world, blockposition, iblockdata)) {
         boolean flag = this.e(world, blockposition, iblockdata);
         if (this.N && !flag || !this.N && flag && !world.a(blockposition, this)) {
            byte b0 = -1;
            if (this.i(world, blockposition, iblockdata)) {
               b0 = -3;
            } else if (this.N) {
               b0 = -2;
            }

            world.a(blockposition, chunk, this, this.d(iblockdata), b0);
         }
      }
   }

   public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
      return false;
   }

   protected boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return this.f(world, blockposition, iblockdata) > 0;
   }

   protected int f(World world, BlockPosition blockposition, IBlockData iblockdata) {
      EnumDirection enumdirection = iblockdata.get(FACING);
      BlockPosition blockposition1 = blockposition.shift(enumdirection);
      int i = world.getBlockFacePower(blockposition1, enumdirection);
      if (i >= 15) {
         return i;
      } else {
         IBlockData iblockdata1 = world.getType(blockposition1);
         return Math.max(i, iblockdata1.getBlock() == Blocks.REDSTONE_WIRE ? iblockdata1.get(BlockRedstoneWire.POWER) : 0);
      }
   }

   protected int c(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
      EnumDirection enumdirection = iblockdata.get(FACING);
      EnumDirection enumdirection1 = enumdirection.e();
      EnumDirection enumdirection2 = enumdirection.f();
      return Math.max(
         this.c(iblockaccess, blockposition.shift(enumdirection1), enumdirection1), this.c(iblockaccess, blockposition.shift(enumdirection2), enumdirection2)
      );
   }

   protected int c(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
      IBlockData iblockdata = iblockaccess.getType(blockposition);
      Block block = iblockdata.getBlock();
      return this.c(block)
         ? (block == Blocks.REDSTONE_WIRE ? iblockdata.get(BlockRedstoneWire.POWER) : iblockaccess.getBlockPower(blockposition, enumdirection))
         : 0;
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return this.getBlockData().set(FACING, entityliving.getDirection().opposite());
   }

   @Override
   public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
      if (this.e(world, blockposition, iblockdata)) {
         world.a(blockposition, this, 1);
      }
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.h(world, blockposition, iblockdata);
   }

   protected void h(World world, BlockPosition blockposition, IBlockData iblockdata) {
      EnumDirection enumdirection = iblockdata.get(FACING);
      EnumDirection opposite = enumdirection.opposite();
      int x = blockposition.getX() + opposite.getAdjacentX();
      int y = blockposition.getY() + opposite.getAdjacentY();
      int z = blockposition.getZ() + opposite.getAdjacentZ();
      world.d(x, y, z, this, blockposition);
      world.a(x, y, z, this, enumdirection);
   }

   @Override
   public void postBreak(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (this.N) {
         this.applyPhysics(world, blockposition);
      } else {
         super.postBreak(world, blockposition, iblockdata);
      }
   }

   public void applyPhysics(World world, BlockPosition blockposition) {
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

   @Override
   public boolean c() {
      return false;
   }

   protected boolean c(Block block) {
      return block.isPowerSource();
   }

   protected int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
      return 15;
   }

   public boolean e(Block block) {
      return block == this.e(this.getBlockData()).getBlock() || block == this.k(this.getBlockData()).getBlock();
   }

   public boolean i(World world, BlockPosition blockposition, IBlockData iblockdata) {
      EnumDirection enumdirection = iblockdata.get(FACING).opposite();
      IBlockData iblockdata1 = world.getType(
         blockposition.getX() + enumdirection.getAdjacentX(),
         blockposition.getY() + enumdirection.getAdjacentY(),
         blockposition.getZ() + enumdirection.getAdjacentZ()
      );
      return d(iblockdata1.getBlock()) && iblockdata1.get(FACING) != enumdirection;
   }

   protected int m(IBlockData iblockdata) {
      return this.d(iblockdata);
   }

   protected abstract int d(IBlockData var1);

   protected abstract IBlockData e(IBlockData var1);

   protected abstract IBlockData k(IBlockData var1);

   @Override
   public boolean b(Block block) {
      return this.e(block);
   }
}
