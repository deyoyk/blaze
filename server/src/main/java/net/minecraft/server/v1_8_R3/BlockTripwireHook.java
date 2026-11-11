package net.minecraft.server.v1_8_R3;

import com.google.common.base.Objects;
import java.util.Random;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockTripwireHook extends Block {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);
   public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
   public static final BlockStateBoolean ATTACHED = BlockStateBoolean.of("attached");
   public static final BlockStateBoolean SUSPENDED = BlockStateBoolean.of("suspended");

   public BlockTripwireHook() {
      super(Material.ORIENTABLE);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(POWERED, false).set(ATTACHED, false).set(SUSPENDED, false));
      this.a(CreativeModeTab.d);
      this.a(true);
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      return iblockdata.set(SUSPENDED, !World.a(iblockaccess, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()));
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
   public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      return enumdirection.k().c() && world.getType(blockposition.shift(enumdirection.opposite())).getBlock().isOccluding();
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         if (world.getType(blockposition.shift(enumdirection)).getBlock().isOccluding()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      IBlockData iblockdata = this.getBlockData().set(POWERED, false).set(ATTACHED, false).set(SUSPENDED, Boolean.FALSE);
      if (enumdirection.k().c()) {
         iblockdata = iblockdata.set(FACING, enumdirection);
      }

      return iblockdata;
   }

   @Override
   public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
      this.a(world, blockposition, iblockdata, false, false, -1, null);
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (block != this && this.e(world, blockposition, iblockdata)) {
         EnumDirection enumdirection = iblockdata.get(FACING);
         if (!world.getType(blockposition.shift(enumdirection.opposite())).getBlock().isOccluding()) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
         }
      }
   }

   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag, boolean flag1, int i, IBlockData iblockdata1) {
      EnumDirection enumdirection = iblockdata.get(FACING);
      boolean flag2 = iblockdata.get(ATTACHED);
      boolean flag3 = iblockdata.get(POWERED);
      boolean flag4 = !World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
      boolean flag5 = !flag;
      boolean flag6 = false;
      int j = 0;
      IBlockData[] aiblockdata = new IBlockData[42];

      for (int k = 1; k < 42; k++) {
         BlockPosition blockposition1 = blockposition.shift(enumdirection, k);
         IBlockData iblockdata2 = world.getType(blockposition1);
         if (iblockdata2.getBlock() == Blocks.TRIPWIRE_HOOK) {
            if (iblockdata2.get(FACING) == enumdirection.opposite()) {
               j = k;
            }
            break;
         }

         if (iblockdata2.getBlock() != Blocks.TRIPWIRE && k != i) {
            aiblockdata[k] = null;
            flag5 = false;
         } else {
            if (k == i) {
               iblockdata2 = (IBlockData)Objects.firstNonNull(iblockdata1, iblockdata2);
            }

            boolean flag7 = !iblockdata2.get(BlockTripwire.DISARMED);
            boolean flag8 = iblockdata2.get(BlockTripwire.POWERED);
            boolean flag9 = iblockdata2.get(BlockTripwire.SUSPENDED);
            flag5 &= flag9 == flag4;
            flag6 |= flag7 && flag8;
            aiblockdata[k] = iblockdata2;
            if (k == i) {
               world.a(blockposition, this, this.a(world));
               flag5 &= flag7;
            }
         }
      }

      flag5 &= j > 1;
      flag6 &= flag5;
      IBlockData iblockdata3 = this.getBlockData().set(ATTACHED, flag5).set(POWERED, flag6);
      if (j > 0) {
         BlockPosition blockposition1x = blockposition.shift(enumdirection, j);
         EnumDirection enumdirection1 = enumdirection.opposite();
         world.setTypeAndData(blockposition1x, iblockdata3.set(FACING, enumdirection1), 3);
         this.a(world, blockposition1x, enumdirection1);
         this.a(world, blockposition1x, flag5, flag6, flag2, flag3);
      }

      org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
      BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, 15, 0);
      world.getServer().getPluginManager().callEvent(eventRedstone);
      if (eventRedstone.getNewCurrent() <= 0) {
         this.a(world, blockposition, flag5, flag6, flag2, flag3);
         if (!flag) {
            world.setTypeAndData(blockposition, iblockdata3.set(FACING, enumdirection), 3);
            if (flag1) {
               this.a(world, blockposition, enumdirection);
            }
         }

         if (flag2 != flag5) {
            for (int l = 1; l < j; l++) {
               BlockPosition blockposition2 = blockposition.shift(enumdirection, l);
               IBlockData iblockdata4 = aiblockdata[l];
               if (iblockdata4 != null && world.getType(blockposition2).getBlock() != Blocks.AIR) {
                  world.setTypeAndData(blockposition2, iblockdata4.set(ATTACHED, flag5), 3);
               }
            }
         }
      }
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      this.a(world, blockposition, iblockdata, false, true, -1, null);
   }

   private void a(World world, BlockPosition blockposition, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
      if (flag1 && !flag3) {
         world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.1, blockposition.getZ() + 0.5, "random.click", 0.4F, 0.6F);
      } else if (!flag1 && flag3) {
         world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.1, blockposition.getZ() + 0.5, "random.click", 0.4F, 0.5F);
      } else if (flag && !flag2) {
         world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.1, blockposition.getZ() + 0.5, "random.click", 0.4F, 0.7F);
      } else if (!flag && flag2) {
         world.makeSound(
            blockposition.getX() + 0.5,
            blockposition.getY() + 0.1,
            blockposition.getZ() + 0.5,
            "random.bowhit",
            0.4F,
            1.2F / (world.random.nextFloat() * 0.2F + 0.9F)
         );
      }
   }

   private void a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      world.applyPhysics(blockposition, this);
      world.applyPhysics(blockposition.shift(enumdirection.opposite()), this);
   }

   private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (!this.canPlace(world, blockposition)) {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      float f = 0.1875F;
      switch (BlockTripwireHook.SyntheticClass_1.a[iblockaccess.getType(blockposition).get(FACING).ordinal()]) {
         case 1:
            this.a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
            break;
         case 2:
            this.a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
            break;
         case 3:
            this.a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
            break;
         case 4:
            this.a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
      }
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      boolean flag = iblockdata.get(ATTACHED);
      boolean flag1 = iblockdata.get(POWERED);
      if (flag || flag1) {
         this.a(world, blockposition, iblockdata, true, false, -1, null);
      }

      if (flag1) {
         world.applyPhysics(blockposition, this);
         world.applyPhysics(blockposition.shift(iblockdata.get(FACING).opposite()), this);
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
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(FACING, EnumDirection.fromType2(i & 3)).set(POWERED, (i & 8) > 0).set(ATTACHED, (i & 4) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(FACING).b();
      if (iblockdata.get(POWERED)) {
         i |= 8;
      }

      if (iblockdata.get(ATTACHED)) {
         i |= 4;
      }

      return i;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, FACING, POWERED, ATTACHED, SUSPENDED);
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.values().length];

      static {
         try {
            a[EnumDirection.EAST.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[EnumDirection.WEST.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.SOUTH.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.NORTH.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
