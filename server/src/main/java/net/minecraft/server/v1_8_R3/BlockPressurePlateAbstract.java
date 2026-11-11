package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.PluginManager;

public abstract class BlockPressurePlateAbstract extends Block {
   protected BlockPressurePlateAbstract(Material material) {
      this(material, material.r());
   }

   protected BlockPressurePlateAbstract(Material material, MaterialMapColor materialmapcolor) {
      super(material, materialmapcolor);
      this.a(CreativeModeTab.d);
      this.a(true);
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      this.d(iblockaccess.getType(blockposition));
   }

   protected void d(IBlockData iblockdata) {
      if (this.e(iblockdata) > 0) {
         this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
      } else {
         this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
      }
   }

   @Override
   public int a(World world) {
      return 20;
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return null;
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
   public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return true;
   }

   @Override
   public boolean g() {
      return true;
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      return this.m(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (!this.m(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ())) {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }
   }

   private boolean m(World world, int x, int y, int z) {
      IBlockData data = world.getType(x, y, z);
      return World.a(data) || data.getBlock() instanceof BlockFence;
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      int i = this.e(iblockdata);
      if (i > 0) {
         this.a(world, blockposition, iblockdata, i);
      }
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
      int i = this.e(iblockdata);
      if (i == 0) {
         this.a(world, blockposition, iblockdata, i);
      }
   }

   protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
      int j = this.f(world, blockposition);
      boolean flag = i > 0;
      boolean flag1 = j > 0;
      org.bukkit.World bworld = world.getWorld();
      PluginManager manager = world.getServer().getPluginManager();
      if (flag != flag1) {
         BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), i, j);
         manager.callEvent(eventRedstone);
         flag1 = eventRedstone.getNewCurrent() > 0;
         j = eventRedstone.getNewCurrent();
      }

      if (i != j) {
         iblockdata = this.a(iblockdata, j);
         world.setTypeAndData(blockposition, iblockdata, 2);
         this.e(world, blockposition);
         world.b(blockposition, blockposition);
      }

      if (!flag1 && flag) {
         world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.1, blockposition.getZ() + 0.5, "random.click", 0.3F, 0.5F);
      } else if (flag1 && !flag) {
         world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.1, blockposition.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (flag1) {
         world.a(blockposition, this, this.a(world));
      }
   }

   protected AxisAlignedBB getBoundingBox(BlockPosition blockposition) {
      return new AxisAlignedBB(
         blockposition.getX() + 0.125F,
         blockposition.getY(),
         blockposition.getZ() + 0.125F,
         blockposition.getX() + 1 - 0.125F,
         blockposition.getY() + 0.25,
         blockposition.getZ() + 1 - 0.125F
      );
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (this.e(iblockdata) > 0) {
         this.e(world, blockposition);
      }

      super.remove(world, blockposition, iblockdata);
   }

   protected void e(World world, BlockPosition blockposition) {
      world.applyPhysics(blockposition, this);
      world.applyPhysics(blockposition.down(), this);
   }

   @Override
   public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return this.e(iblockdata);
   }

   @Override
   public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return enumdirection == EnumDirection.UP ? this.e(iblockdata) : 0;
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public void j() {
      this.a(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
   }

   @Override
   public int k() {
      return 1;
   }

   protected abstract int f(World var1, BlockPosition var2);

   protected abstract int e(IBlockData var1);

   protected abstract IBlockData a(IBlockData var1, int var2);
}
