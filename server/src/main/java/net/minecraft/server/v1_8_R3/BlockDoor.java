package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockDoor extends Block {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);
   public static final BlockStateBoolean OPEN = BlockStateBoolean.of("open");
   public static final BlockStateEnum<BlockDoor.EnumDoorHinge> HINGE = BlockStateEnum.of("hinge", BlockDoor.EnumDoorHinge.class);
   public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
   public static final BlockStateEnum<BlockDoor.EnumDoorHalf> HALF = BlockStateEnum.of("half", BlockDoor.EnumDoorHalf.class);

   protected BlockDoor(Material material) {
      super(material);
      this.j(
         this.blockStateList
            .getBlockData()
            .set(FACING, EnumDirection.NORTH)
            .set(OPEN, false)
            .set(HINGE, BlockDoor.EnumDoorHinge.LEFT)
            .set(POWERED, Boolean.FALSE)
            .set(HALF, BlockDoor.EnumDoorHalf.LOWER)
      );
   }

   @Override
   public String getName() {
      return LocaleI18n.get((this.a() + ".name").replaceAll("tile", "item"));
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return g(e(iblockaccess, blockposition));
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.updateShape(world, blockposition);
      return super.a(world, blockposition, iblockdata);
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      this.k(e(iblockaccess, blockposition));
   }

   private void k(int i) {
      float f = 0.1875F;
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
      EnumDirection enumdirection = f(i);
      boolean flag = g(i);
      boolean flag1 = j(i);
      if (flag) {
         if (enumdirection == EnumDirection.EAST) {
            if (!flag1) {
               this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            } else {
               this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
         } else if (enumdirection == EnumDirection.SOUTH) {
            if (!flag1) {
               this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
         } else if (enumdirection == EnumDirection.WEST) {
            if (!flag1) {
               this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            } else {
               this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
         } else if (enumdirection == EnumDirection.NORTH) {
            if (!flag1) {
               this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            } else {
               this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      } else if (enumdirection == EnumDirection.EAST) {
         this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
      } else if (enumdirection == EnumDirection.SOUTH) {
         this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
      } else if (enumdirection == EnumDirection.WEST) {
         this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else if (enumdirection == EnumDirection.NORTH) {
         this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (this.material == Material.ORE) {
         return true;
      } else {
         BlockPosition blockposition1 = iblockdata.get(HALF) == BlockDoor.EnumDoorHalf.LOWER ? blockposition : blockposition.down();
         IBlockData iblockdata1 = blockposition.equals(blockposition1) ? iblockdata : world.getType(blockposition1);
         if (iblockdata1.getBlock() != this) {
            return false;
         } else {
            iblockdata = iblockdata1.a(OPEN);
            world.setTypeAndData(blockposition1, iblockdata, 2);
            world.b(blockposition1, blockposition);
            world.a(entityhuman, iblockdata.get(OPEN) ? 1003 : 1006, blockposition, 0);
            return true;
         }
      }
   }

   public void setDoor(World world, BlockPosition blockposition, boolean flag) {
      IBlockData iblockdata = world.getType(blockposition);
      if (iblockdata.getBlock() == this) {
         BlockPosition blockposition1 = iblockdata.get(HALF) == BlockDoor.EnumDoorHalf.LOWER ? blockposition : blockposition.down();
         IBlockData iblockdata1 = blockposition == blockposition1 ? iblockdata : world.getType(blockposition1);
         if (iblockdata1.getBlock() == this && iblockdata1.get(OPEN) != flag) {
            world.setTypeAndData(blockposition1, iblockdata1.set(OPEN, flag), 2);
            world.b(blockposition1, blockposition);
            world.a(null, flag ? 1003 : 1006, blockposition, 0);
         }
      }
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (iblockdata.get(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         BlockPosition blockposition1 = blockposition.down();
         IBlockData iblockdata1 = world.getType(blockposition1);
         if (iblockdata1.getBlock() != this) {
            world.setAir(blockposition);
         } else if (block != this) {
            this.doPhysics(world, blockposition1, iblockdata1, block);
         }
      } else {
         boolean flag = false;
         BlockPosition blockposition2 = blockposition.up();
         IBlockData iblockdata2 = world.getType(blockposition2);
         if (iblockdata2.getBlock() != this) {
            world.setAir(blockposition);
            flag = true;
         }

         if (!World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ())) {
            world.setAir(blockposition);
            flag = true;
            if (iblockdata2.getBlock() == this) {
               world.setAir(blockposition2);
            }
         }

         if (flag) {
            this.b(world, blockposition, iblockdata, 0);
         } else {
            org.bukkit.World bworld = world.getWorld();
            org.bukkit.block.Block bukkitBlock = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            org.bukkit.block.Block blockTop = bworld.getBlockAt(blockposition2.getX(), blockposition2.getY(), blockposition2.getZ());
            int power = bukkitBlock.getBlockPower();
            int powerTop = blockTop.getBlockPower();
            if (powerTop > power) {
               power = powerTop;
            }

            int oldPower = iblockdata2.get(POWERED) ? 15 : 0;
            if (oldPower == 0 ^ power == 0) {
               BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, oldPower, power);
               world.getServer().getPluginManager().callEvent(eventRedstone);
               boolean flag1 = eventRedstone.getNewCurrent() > 0;
               world.setTypeAndData(blockposition2, iblockdata2.set(POWERED, flag1), 2);
               if (flag1 != iblockdata.get(OPEN)) {
                  world.setTypeAndData(blockposition, iblockdata.set(OPEN, flag1), 2);
                  world.b(blockposition, blockposition);
                  world.a(null, flag1 ? 1003 : 1006, blockposition, 0);
               }
            }
         }
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return iblockdata.get(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : this.l();
   }

   @Override
   public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
      this.updateShape(world, blockposition);
      return super.a(world, blockposition, vec3d, vec3d1);
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      return blockposition.getY() < 255
         && World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ())
         && super.canPlace(world, blockposition)
         && super.canPlace(world, blockposition.up());
   }

   @Override
   public int k() {
      return 1;
   }

   public static int e(IBlockAccess iblockaccess, BlockPosition blockposition) {
      IBlockData iblockdata = iblockaccess.getType(blockposition);
      int i = iblockdata.getBlock().toLegacyData(iblockdata);
      boolean flag = i(i);
      IBlockData iblockdata1 = iblockaccess.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
      int j = iblockdata1.getBlock().toLegacyData(iblockdata1);
      int k = flag ? j : i;
      IBlockData iblockdata2 = iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ());
      int l = iblockdata2.getBlock().toLegacyData(iblockdata2);
      int i1 = flag ? i : l;
      boolean flag1 = (i1 & 1) != 0;
      boolean flag2 = (i1 & 2) != 0;
      return b(k) | (flag ? 8 : 0) | (flag1 ? 16 : 0) | (flag2 ? 32 : 0);
   }

   private Item l() {
      return this == Blocks.IRON_DOOR
         ? Items.IRON_DOOR
         : (
            this == Blocks.SPRUCE_DOOR
               ? Items.SPRUCE_DOOR
               : (
                  this == Blocks.BIRCH_DOOR
                     ? Items.BIRCH_DOOR
                     : (
                        this == Blocks.JUNGLE_DOOR
                           ? Items.JUNGLE_DOOR
                           : (this == Blocks.ACACIA_DOOR ? Items.ACACIA_DOOR : (this == Blocks.DARK_OAK_DOOR ? Items.DARK_OAK_DOOR : Items.WOODEN_DOOR))
                     )
               )
         );
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
      BlockPosition blockposition1 = blockposition.down();
      if (entityhuman.abilities.canInstantlyBuild && iblockdata.get(HALF) == BlockDoor.EnumDoorHalf.UPPER && world.getType(blockposition1).getBlock() == this) {
         world.setAir(blockposition1);
      }
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      if (iblockdata.get(HALF) == BlockDoor.EnumDoorHalf.LOWER) {
         IBlockData iblockdata1 = iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ());
         if (iblockdata1.getBlock() == this) {
            iblockdata = iblockdata.set(HINGE, iblockdata1.get(HINGE)).set(POWERED, iblockdata1.get(POWERED));
         }
      } else {
         IBlockData iblockdata1 = iblockaccess.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
         if (iblockdata1.getBlock() == this) {
            iblockdata = iblockdata.set(FACING, iblockdata1.get(FACING)).set(OPEN, iblockdata1.get(OPEN));
         }
      }

      return iblockdata;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return (i & 8) > 0
         ? this.getBlockData()
            .set(HALF, BlockDoor.EnumDoorHalf.UPPER)
            .set(HINGE, (i & 1) > 0 ? BlockDoor.EnumDoorHinge.RIGHT : BlockDoor.EnumDoorHinge.LEFT)
            .set(POWERED, (i & 2) > 0)
         : this.getBlockData().set(HALF, BlockDoor.EnumDoorHalf.LOWER).set(FACING, EnumDirection.fromType2(i & 3).f()).set(OPEN, (i & 4) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i;
      if (iblockdata.get(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         i = b0 | 8;
         if (iblockdata.get(HINGE) == BlockDoor.EnumDoorHinge.RIGHT) {
            i |= 1;
         }

         if (iblockdata.get(POWERED)) {
            i |= 2;
         }
      } else {
         i = b0 | iblockdata.get(FACING).e().b();
         if (iblockdata.get(OPEN)) {
            i |= 4;
         }
      }

      return i;
   }

   protected static int b(int i) {
      return i & 7;
   }

   public static boolean f(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return g(e(iblockaccess, blockposition));
   }

   public static EnumDirection h(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return f(e(iblockaccess, blockposition));
   }

   public static EnumDirection f(int i) {
      return EnumDirection.fromType2(i & 3).f();
   }

   protected static boolean g(int i) {
      return (i & 4) != 0;
   }

   protected static boolean i(int i) {
      return (i & 8) != 0;
   }

   protected static boolean j(int i) {
      return (i & 16) != 0;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, HALF, FACING, OPEN, HINGE, POWERED);
   }

   public static enum EnumDoorHalf implements INamable {
      UPPER,
      LOWER;

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this == UPPER ? "upper" : "lower";
      }
   }

   public static enum EnumDoorHinge implements INamable {
      LEFT,
      RIGHT;

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this == LEFT ? "left" : "right";
      }
   }
}
