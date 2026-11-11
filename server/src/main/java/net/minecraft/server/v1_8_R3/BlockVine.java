package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class BlockVine extends Block {
   public static final BlockStateBoolean UP = BlockStateBoolean.of("up");
   public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
   public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
   public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
   public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");
   public static final BlockStateBoolean[] Q = new BlockStateBoolean[]{UP, NORTH, SOUTH, WEST, EAST};

   public BlockVine() {
      super(Material.REPLACEABLE_PLANT);
      this.j(this.blockStateList.getBlockData().set(UP, false).set(NORTH, false).set(EAST, false).set(SOUTH, false).set(WEST, false));
      this.a(true);
      this.a(CreativeModeTab.c);
   }

   public static BlockStateBoolean getDirection(EnumDirection enumdirection) {
      switch (BlockVine.SyntheticClass_1.a[enumdirection.ordinal()]) {
         case 1:
            return UP;
         case 2:
            return NORTH;
         case 3:
            return SOUTH;
         case 4:
            return EAST;
         case 5:
            return WEST;
         default:
            throw new IllegalArgumentException(enumdirection + " is an invalid choice");
      }
   }

   public static int d(IBlockData iblockdata) {
      int i = 0;

      for (BlockStateBoolean blockstateboolean : Q) {
         if (iblockdata.get(blockstateboolean)) {
            i++;
         }
      }

      return i;
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      return iblockdata.set(UP, iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().u());
   }

   @Override
   public void j() {
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
   public boolean a(World world, BlockPosition blockposition) {
      return true;
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      float f1 = 1.0F;
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 0.0F;
      float f5 = 0.0F;
      float f6 = 0.0F;
      boolean flag = false;
      if (iblockaccess.getType(blockposition).get(WEST)) {
         f4 = 0.0625F;
         f1 = 0.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         f3 = 0.0F;
         f6 = 1.0F;
         flag = true;
      }

      if (iblockaccess.getType(blockposition).get(EAST)) {
         f1 = Math.min(f1, 0.9375F);
         f4 = 1.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         f3 = 0.0F;
         f6 = 1.0F;
         flag = true;
      }

      if (iblockaccess.getType(blockposition).get(NORTH)) {
         f6 = Math.max(f6, 0.0625F);
         f3 = 0.0F;
         f1 = 0.0F;
         f4 = 1.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         flag = true;
      }

      if (iblockaccess.getType(blockposition).get(SOUTH)) {
         f3 = Math.min(f3, 0.9375F);
         f6 = 1.0F;
         f1 = 0.0F;
         f4 = 1.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         flag = true;
      }

      if (!flag && this.c(iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock())) {
         f2 = 0.9375F;
         f5 = 1.0F;
         f1 = 0.0F;
         f4 = 1.0F;
         f3 = 0.0F;
         f6 = 1.0F;
      }

      this.a(f1, f2, f3, f4, f5, f6);
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return null;
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
      switch (BlockVine.SyntheticClass_1.a[enumdirection.ordinal()]) {
         case 1:
            return this.c(world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock());
         case 2:
         case 3:
         case 4:
         case 5:
            return this.c(world.getType(blockposition.shift(enumdirection.opposite())).getBlock());
         default:
            return false;
      }
   }

   private boolean c(Block block) {
      return block.d() && block.material.isSolid();
   }

   private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      IBlockData iblockdata1 = iblockdata;

      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         BlockStateBoolean blockstateboolean = getDirection(enumdirection);
         if (iblockdata.get(blockstateboolean) && !this.c(world.getType(blockposition.shift(enumdirection)).getBlock())) {
            IBlockData iblockdata2 = world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ());
            if (iblockdata2.getBlock() != this || !iblockdata2.get(blockstateboolean)) {
               iblockdata = iblockdata.set(blockstateboolean, false);
            }
         }
      }

      if (d(iblockdata) == 0) {
         return false;
      } else {
         if (iblockdata1 != iblockdata) {
            world.setTypeAndData(blockposition, iblockdata, 2);
         }

         return true;
      }
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (!this.e(world, blockposition, iblockdata)) {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if (world.random.nextInt(4) == 0) {
         byte b0 = 4;
         int i = 5;
         boolean flag = false;

         label174:
         for (int j = -b0; j <= b0; j++) {
            for (int k = -b0; k <= b0; k++) {
               for (int l = -1; l <= 1; l++) {
                  if (world.getType(blockposition.a(j, l, k)).getBlock() == this) {
                     if (--i <= 0) {
                        flag = true;
                        break label174;
                     }
                  }
               }
            }
         }

         EnumDirection enumdirection = EnumDirection.a(random);
         BlockPosition blockposition1 = blockposition.up();
         if (enumdirection == EnumDirection.UP && blockposition.getY() < 255 && world.isEmpty(blockposition1)) {
            if (!flag) {
               IBlockData iblockdata1 = iblockdata;

               for (EnumDirection enumDirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                  if (random.nextBoolean() || !this.c(world.getType(blockposition1.shift(enumDirection)).getBlock())) {
                     iblockdata1 = iblockdata1.set(getDirection(enumDirection), Boolean.FALSE);
                  }
               }

               if (iblockdata1.get(NORTH) || iblockdata1.get(EAST) || iblockdata1.get(SOUTH) || iblockdata1.get(WEST)) {
                  org.bukkit.block.Block source = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                  org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
                  CraftEventFactory.handleBlockSpreadEvent(block, source, this, this.toLegacyData(iblockdata1));
               }
            }
         } else if (!enumdirection.k().c() || iblockdata.get(getDirection(enumdirection))) {
            if (blockposition.getY() > 1) {
               BlockPosition blockposition2 = blockposition.down();
               IBlockData iblockdata2 = world.getType(blockposition2);
               Block block1 = iblockdata2.getBlock();
               if (block1.material == Material.AIR) {
                  IBlockData iblockdata3 = iblockdata;

                  for (EnumDirection enumdirection3 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                     if (random.nextBoolean()) {
                        iblockdata3 = iblockdata3.set(getDirection(enumdirection3), false);
                     }
                  }

                  if (iblockdata3.get(NORTH) || iblockdata3.get(EAST) || iblockdata3.get(SOUTH) || iblockdata3.get(WEST)) {
                     org.bukkit.block.Block source = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                     org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(blockposition2.getX(), blockposition2.getY(), blockposition2.getZ());
                     CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this, this.toLegacyData(iblockdata3));
                  }
               } else if (block1 == this) {
                  IBlockData iblockdata3 = iblockdata2;

                  for (EnumDirection enumdirection3x : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                     BlockStateBoolean blockstateboolean = getDirection(enumdirection3x);
                     if (random.nextBoolean() && iblockdata.get(blockstateboolean)) {
                        iblockdata3 = iblockdata3.set(blockstateboolean, Boolean.TRUE);
                     }
                  }

                  if (iblockdata3.get(NORTH) || iblockdata3.get(EAST) || iblockdata3.get(SOUTH) || iblockdata3.get(WEST)) {
                     world.setTypeAndData(blockposition2, iblockdata3, 2);
                  }
               }
            }
         } else if (!flag) {
            BlockPosition blockposition2 = blockposition.shift(enumdirection);
            Block block = world.getType(blockposition2).getBlock();
            if (block.material == Material.AIR) {
               EnumDirection enumdirection1 = enumdirection.e();
               EnumDirection enumdirection2 = enumdirection.f();
               boolean flag1 = iblockdata.get(getDirection(enumdirection1));
               boolean flag2 = iblockdata.get(getDirection(enumdirection2));
               BlockPosition blockposition3 = blockposition2.shift(enumdirection1);
               BlockPosition blockposition4 = blockposition2.shift(enumdirection2);
               org.bukkit.block.Block source = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
               org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(blockposition2.getX(), blockposition2.getY(), blockposition2.getZ());
               if (flag1 && this.c(world.getType(blockposition3).getBlock())) {
                  CraftEventFactory.handleBlockSpreadEvent(
                     bukkitBlock, source, this, this.toLegacyData(this.getBlockData().set(getDirection(enumdirection1), true))
                  );
               } else if (flag2 && this.c(world.getType(blockposition4).getBlock())) {
                  CraftEventFactory.handleBlockSpreadEvent(
                     bukkitBlock, source, this, this.toLegacyData(this.getBlockData().set(getDirection(enumdirection2), true))
                  );
               } else if (flag1 && world.isEmpty(blockposition3) && this.c(world.getType(blockposition.shift(enumdirection1)).getBlock())) {
                  bukkitBlock = world.getWorld().getBlockAt(blockposition3.getX(), blockposition3.getY(), blockposition3.getZ());
                  CraftEventFactory.handleBlockSpreadEvent(
                     bukkitBlock, source, this, this.toLegacyData(this.getBlockData().set(getDirection(enumdirection.opposite()), true))
                  );
               } else if (flag2 && world.isEmpty(blockposition4) && this.c(world.getType(blockposition.shift(enumdirection2)).getBlock())) {
                  bukkitBlock = world.getWorld().getBlockAt(blockposition4.getX(), blockposition4.getY(), blockposition4.getZ());
                  CraftEventFactory.handleBlockSpreadEvent(
                     bukkitBlock, source, this, this.toLegacyData(this.getBlockData().set(getDirection(enumdirection.opposite()), true))
                  );
               } else if (this.c(world.getType(blockposition2.getX(), blockposition2.getY() + 1, blockposition2.getZ()).getBlock())) {
                  CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this, this.toLegacyData(this.getBlockData()));
               }
            } else if (block.material.k() && block.d()) {
               world.setTypeAndData(blockposition, iblockdata.set(getDirection(enumdirection), true), 2);
            }
         }
      }
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      IBlockData iblockdata = this.getBlockData().set(UP, false).set(NORTH, false).set(EAST, false).set(SOUTH, false).set(WEST, false);
      return enumdirection.k().c() ? iblockdata.set(getDirection(enumdirection.opposite()), true) : iblockdata;
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return null;
   }

   @Override
   public int a(Random random) {
      return 0;
   }

   @Override
   public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
      if (entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
         this.a(world, blockposition, new ItemStack(Blocks.VINE, 1, 0));
      } else {
         super.a(world, entityhuman, blockposition, iblockdata, tileentity);
      }
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(SOUTH, (i & 1) > 0).set(WEST, (i & 2) > 0).set(NORTH, (i & 4) > 0).set(EAST, (i & 8) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      int i = 0;
      if (iblockdata.get(SOUTH)) {
         i |= 1;
      }

      if (iblockdata.get(WEST)) {
         i |= 2;
      }

      if (iblockdata.get(NORTH)) {
         i |= 4;
      }

      if (iblockdata.get(EAST)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, UP, NORTH, EAST, SOUTH, WEST);
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.values().length];

      static {
         try {
            a[EnumDirection.UP.ordinal()] = 1;
         } catch (NoSuchFieldError var5) {
         }

         try {
            a[EnumDirection.NORTH.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[EnumDirection.SOUTH.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.EAST.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.WEST.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
