package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.Random;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityInteractEvent;

public abstract class BlockButtonAbstract extends Block {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing");
   public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
   private final boolean N;

   protected BlockButtonAbstract(boolean flag) {
      super(Material.ORIENTABLE);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(POWERED, Boolean.FALSE));
      this.a(true);
      this.a(CreativeModeTab.d);
      this.N = flag;
   }

   protected static boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      BlockPosition blockposition1 = blockposition.shift(enumdirection);
      return enumdirection == EnumDirection.DOWN ? World.a(world, blockposition1) : world.getType(blockposition1).getBlock().isOccluding();
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return null;
   }

   @Override
   public int a(World world) {
      return this.N ? 30 : 20;
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
   public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      return a(world, blockposition, enumdirection.opposite());
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      EnumDirection[] var6;
      for (EnumDirection enumdirection : var6 = EnumDirection.values()) {
         if (a(world, blockposition, enumdirection)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return a(world, blockposition, enumdirection.opposite())
         ? this.getBlockData().set(FACING, enumdirection).set(POWERED, Boolean.FALSE)
         : this.getBlockData().set(FACING, EnumDirection.DOWN).set(POWERED, Boolean.FALSE);
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (this.e(world, blockposition, iblockdata) && !a(world, blockposition, iblockdata.get(FACING).opposite())) {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }
   }

   private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (this.canPlace(world, blockposition)) {
         return true;
      } else {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
         return false;
      }
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      this.d(iblockaccess.getType(blockposition));
   }

   private void d(IBlockData iblockdata) {
      EnumDirection enumdirection = iblockdata.get(FACING);
      boolean flag = iblockdata.get(POWERED);
      float f2 = (flag ? 1 : 2) / 16.0F;
      switch (BlockButtonAbstract.SyntheticClass_1.a[enumdirection.ordinal()]) {
         case 1:
            this.a(0.0F, 0.375F, 0.3125F, f2, 0.625F, 0.6875F);
            break;
         case 2:
            this.a(1.0F - f2, 0.375F, 0.3125F, 1.0F, 0.625F, 0.6875F);
            break;
         case 3:
            this.a(0.3125F, 0.375F, 0.0F, 0.6875F, 0.625F, f2);
            break;
         case 4:
            this.a(0.3125F, 0.375F, 1.0F - f2, 0.6875F, 0.625F, 1.0F);
            break;
         case 5:
            this.a(0.3125F, 0.0F, 0.375F, 0.6875F, 0.0F + f2, 0.625F);
            break;
         case 6:
            this.a(0.3125F, 1.0F - f2, 0.375F, 0.6875F, 1.0F, 0.625F);
      }
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (!iblockdata.get(POWERED)) {
         boolean powered = iblockdata.get(POWERED);
         org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
         int old = powered ? 15 : 0;
         int current = !powered ? 15 : 0;
         BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
         world.getServer().getPluginManager().callEvent(eventRedstone);
         if (eventRedstone.getNewCurrent() > 0 == powered) {
            return true;
         }

         world.setTypeAndData(blockposition, iblockdata.set(POWERED, true), 3);
         world.b(blockposition, blockposition);
         world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5, "random.click", 0.3F, 0.6F);
         this.c(world, blockposition, iblockdata.get(FACING));
         world.a(blockposition, this, this.a(world));
      }

      return true;
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (iblockdata.get(POWERED)) {
         this.c(world, blockposition, iblockdata.get(FACING));
      }

      super.remove(world, blockposition, iblockdata);
   }

   @Override
   public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return iblockdata.get(POWERED) ? 15 : 0;
   }

   @Override
   public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return !iblockdata.get(POWERED) ? 0 : (iblockdata.get(FACING) == enumdirection ? 15 : 0);
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if (iblockdata.get(POWERED)) {
         if (this.N) {
            this.f(world, blockposition, iblockdata);
         } else {
            org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, 15, 0);
            world.getServer().getPluginManager().callEvent(eventRedstone);
            if (eventRedstone.getNewCurrent() > 0) {
               return;
            }

            world.setTypeUpdate(blockposition, iblockdata.set(POWERED, false));
            this.c(world, blockposition, iblockdata.get(FACING));
            world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5, "random.click", 0.3F, 0.5F);
            world.b(blockposition, blockposition);
         }
      }
   }

   @Override
   public void j() {
      float f = 0.1875F;
      float f1 = 0.125F;
      float f2 = 0.125F;
      this.a(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
      if (this.N && !iblockdata.get(POWERED)) {
         this.f(world, blockposition, iblockdata);
      }
   }

   private void f(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.d(iblockdata);
      List<Entity> list = world.a(
         EntityArrow.class,
         new AxisAlignedBB(
            blockposition.getX() + this.minX,
            blockposition.getY() + this.minY,
            blockposition.getZ() + this.minZ,
            blockposition.getX() + this.maxX,
            blockposition.getY() + this.maxY,
            blockposition.getZ() + this.maxZ
         )
      );
      boolean flag = !list.isEmpty();
      boolean flag1 = iblockdata.get(POWERED);
      if (flag1 != flag && flag) {
         org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
         boolean allowed = false;

         for (Entity entity : list) {
            EntityInteractEvent event = new EntityInteractEvent(entity.getBukkitEntity(), block);
            world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               allowed = true;
               break;
            }
         }

         if (!allowed) {
            return;
         }
      }

      if (flag && !flag1) {
         org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
         BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, 0, 15);
         world.getServer().getPluginManager().callEvent(eventRedstone);
         if (eventRedstone.getNewCurrent() <= 0) {
            return;
         }

         world.setTypeUpdate(blockposition, iblockdata.set(POWERED, true));
         this.c(world, blockposition, iblockdata.get(FACING));
         world.b(blockposition, blockposition);
         world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (!flag && flag1) {
         org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
         BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, 15, 0);
         world.getServer().getPluginManager().callEvent(eventRedstone);
         if (eventRedstone.getNewCurrent() > 0) {
            return;
         }

         world.setTypeUpdate(blockposition, iblockdata.set(POWERED, false));
         this.c(world, blockposition, iblockdata.get(FACING));
         world.b(blockposition, blockposition);
         world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5, "random.click", 0.3F, 0.5F);
      }

      if (flag) {
         world.a(blockposition, this, this.a(world));
      }
   }

   private void c(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      world.applyPhysics(blockposition, this);
      world.applyPhysics(blockposition.shift(enumdirection.opposite()), this);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      EnumDirection enumdirection;
      switch (i & 7) {
         case 0:
            enumdirection = EnumDirection.DOWN;
            break;
         case 1:
            enumdirection = EnumDirection.EAST;
            break;
         case 2:
            enumdirection = EnumDirection.WEST;
            break;
         case 3:
            enumdirection = EnumDirection.SOUTH;
            break;
         case 4:
            enumdirection = EnumDirection.NORTH;
            break;
         case 5:
         default:
            enumdirection = EnumDirection.UP;
      }

      return this.getBlockData().set(FACING, enumdirection).set(POWERED, (i & 8) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      int i;
      switch (BlockButtonAbstract.SyntheticClass_1.a[iblockdata.get(FACING).ordinal()]) {
         case 1:
            i = 1;
            break;
         case 2:
            i = 2;
            break;
         case 3:
            i = 3;
            break;
         case 4:
            i = 4;
            break;
         case 5:
         default:
            i = 5;
            break;
         case 6:
            i = 0;
      }

      if (iblockdata.get(POWERED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, FACING, POWERED);
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.values().length];

      static {
         try {
            a[EnumDirection.EAST.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            a[EnumDirection.WEST.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
         }

         try {
            a[EnumDirection.SOUTH.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[EnumDirection.NORTH.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.UP.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.DOWN.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
