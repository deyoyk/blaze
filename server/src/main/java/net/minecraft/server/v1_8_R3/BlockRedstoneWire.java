package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockRedstoneWire extends Block {
   public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> NORTH = BlockStateEnum.of(
      "north", BlockRedstoneWire.EnumRedstoneWireConnection.class
   );
   public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> EAST = BlockStateEnum.of(
      "east", BlockRedstoneWire.EnumRedstoneWireConnection.class
   );
   public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> SOUTH = BlockStateEnum.of(
      "south", BlockRedstoneWire.EnumRedstoneWireConnection.class
   );
   public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> WEST = BlockStateEnum.of(
      "west", BlockRedstoneWire.EnumRedstoneWireConnection.class
   );
   public static final BlockStateInteger POWER = BlockStateInteger.of("power", 0, 15);
   private final Set<BlockPosition> R = Sets.newHashSet();
   private boolean Q = true;

   public BlockRedstoneWire() {
      super(Material.ORIENTABLE);
      this.j(
         this.blockStateList
            .getBlockData()
            .set(NORTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
            .set(EAST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
            .set(SOUTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
            .set(WEST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
            .set(POWER, 0)
      );
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
   }

   protected static boolean e(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return d(iblockaccess.getType(blockposition));
   }

   protected static boolean d(IBlockData iblockdata) {
      return a(iblockdata, null);
   }

   protected static boolean a(IBlockData iblockdata, EnumDirection enumdirection) {
      Block block = iblockdata.getBlock();
      if (block == Blocks.REDSTONE_WIRE) {
         return true;
      } else if (Blocks.UNPOWERED_REPEATER.e(block)) {
         EnumDirection enumdirection1 = iblockdata.get(BlockRepeater.FACING);
         return enumdirection1 == enumdirection || enumdirection1.opposite() == enumdirection;
      } else {
         return block.isPowerSource() && enumdirection != null;
      }
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      iblockdata = iblockdata.set(WEST, this.c(iblockaccess, blockposition, EnumDirection.WEST));
      iblockdata = iblockdata.set(EAST, this.c(iblockaccess, blockposition, EnumDirection.EAST));
      iblockdata = iblockdata.set(NORTH, this.c(iblockaccess, blockposition, EnumDirection.NORTH));
      return iblockdata.set(SOUTH, this.c(iblockaccess, blockposition, EnumDirection.SOUTH));
   }

   private BlockRedstoneWire.EnumRedstoneWireConnection c(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
      BlockPosition blockposition1 = blockposition.shift(enumdirection);
      Block block = iblockaccess.getType(blockposition.shift(enumdirection)).getBlock();
      if (!a(iblockaccess.getType(blockposition1), enumdirection)
         && (block.u() || !d(iblockaccess.getType(blockposition1.getX(), blockposition1.getY() - 1, blockposition1.getZ())))) {
         Block block1 = iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock();
         return !block1.u() && block.u() && d(iblockaccess.getType(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ()))
            ? BlockRedstoneWire.EnumRedstoneWireConnection.UP
            : BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
      } else {
         return BlockRedstoneWire.EnumRedstoneWireConnection.SIDE;
      }
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
   public boolean canPlace(World world, BlockPosition blockposition) {
      IBlockData data = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
      return World.a(data) || data.getBlock() == Blocks.GLOWSTONE;
   }

   public boolean canPlace(World world, Chunk chunk, BlockPosition blockposition) {
      IBlockData data = world.getType(chunk, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ(), true);
      return World.a(data) || data.getBlock() == Blocks.GLOWSTONE;
   }

   private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.a(world, blockposition, blockposition, iblockdata);
      ArrayList<BlockPosition> arraylist = Lists.newArrayList(this.R);
      this.R.clear();

      for (BlockPosition blockposition1 : arraylist) {
         world.applyPhysics(blockposition1, this);
      }
   }

   private void a(World world, BlockPosition blockposition, BlockPosition blockposition1, IBlockData iblockdata) {
      int i = iblockdata.get(POWER);
      byte b0 = 0;
      int j = this.getPower(world, blockposition1, b0);
      this.Q = false;
      int k = world.A(blockposition);
      this.Q = true;
      if (k > 0 && k > j - 1) {
         j = k;
      }

      int l = 0;

      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         BlockPosition blockposition2 = blockposition.shift(enumdirection);
         boolean flag = blockposition2.getX() != blockposition1.getX() || blockposition2.getZ() != blockposition1.getZ();
         if (flag) {
            l = this.getPower(world, blockposition2, l);
         }

         if (world.getType(blockposition2).getBlock().isOccluding()
            && !world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().isOccluding()) {
            if (flag && blockposition.getY() >= blockposition1.getY()) {
               l = this.getPower(world, blockposition2.up(), l);
            }
         } else if (!world.getType(blockposition2).getBlock().isOccluding() && flag && blockposition.getY() <= blockposition1.getY()) {
            l = this.getPower(world, blockposition2.down(), l);
         }
      }

      if (l > j) {
         j = l - 1;
      } else if (j > 0) {
         j--;
      } else {
         j = 0;
      }

      if (k > j - 1) {
         j = k;
      }

      if (i != j) {
         BlockRedstoneEvent event = new BlockRedstoneEvent(world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), i, j);
         world.getServer().getPluginManager().callEvent(event);
         j = event.getNewCurrent();
      }

      if (i != j) {
         iblockdata = iblockdata.set(POWER, j);
         if (world.getType(blockposition) == iblockdata) {
            world.setTypeAndData(blockposition, iblockdata, 2);
         }

         this.R.add(blockposition);

         EnumDirection[] var21;
         for (EnumDirection enumdirection1 : var21 = EnumDirection.values()) {
            this.R.add(blockposition.shift(enumdirection1));
         }
      }
   }

   public void e(World world, BlockPosition blockposition) {
      if (world.getType(blockposition).getBlock() == this) {
         world.applyPhysics(blockposition, this);

         EnumDirection[] var6;
         for (EnumDirection direction : var6 = EnumDirection.values()) {
            world.applyPhysics(
               blockposition.getX() + direction.getAdjacentX(),
               blockposition.getY() + direction.getAdjacentY(),
               blockposition.getZ() + direction.getAdjacentZ(),
               this
            );
         }
      }
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.e(world, blockposition, iblockdata);

      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.VERTICAL) {
         world.applyPhysics(blockposition.shift(enumdirection), this);
      }

      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         this.e(world, blockposition.shift(enumdirection));
      }

      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         BlockPosition blockposition1 = blockposition.shift(enumdirection);
         if (world.getType(blockposition1).getBlock().isOccluding()) {
            this.e(world, blockposition1.up());
         } else {
            this.e(world, blockposition1.down());
         }
      }
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.remove(world, blockposition, iblockdata);

      EnumDirection[] var7;
      for (EnumDirection enumdirection : var7 = EnumDirection.values()) {
         world.applyPhysics(blockposition.shift(enumdirection), this);
      }

      this.e(world, blockposition, iblockdata);

      for (EnumDirection enumdirection1 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         this.e(world, blockposition.shift(enumdirection1));
      }

      for (EnumDirection enumdirection1 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         BlockPosition blockposition1 = blockposition.shift(enumdirection1);
         if (world.getType(blockposition1).getBlock().isOccluding()) {
            this.e(world, blockposition1.up());
         } else {
            this.e(world, blockposition1.down());
         }
      }
   }

   public int getPower(World world, BlockPosition blockposition, int i) {
      IBlockData data = world.getType(blockposition);
      return data.getBlock() != this ? i : Math.max(data.get(POWER), i);
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (this.canPlace(world, blockposition)) {
         this.e(world, blockposition, iblockdata);
      } else {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Items.REDSTONE;
   }

   @Override
   public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return !this.Q ? 0 : this.a(iblockaccess, blockposition, iblockdata, enumdirection);
   }

   @Override
   public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      if (!this.Q) {
         return 0;
      } else {
         int i = iblockdata.get(POWER);
         if (i == 0) {
            return 0;
         } else if (enumdirection == EnumDirection.UP) {
            return i;
         } else {
            EnumSet<EnumDirection> enumset = EnumSet.noneOf(EnumDirection.class);

            for (EnumDirection enumdirection1 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
               if (this.d(iblockaccess, blockposition, enumdirection1)) {
                  enumset.add(enumdirection1);
               }
            }

            if (enumdirection.k().c() && enumset.isEmpty()) {
               return i;
            } else {
               return enumset.contains(enumdirection) && !enumset.contains(enumdirection.f()) && !enumset.contains(enumdirection.e()) ? i : 0;
            }
         }
      }
   }

   private boolean d(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
      BlockPosition blockposition1 = blockposition.shift(enumdirection);
      IBlockData iblockdata = iblockaccess.getType(blockposition1);
      Block block = iblockdata.getBlock();
      boolean flag = block.isOccluding();
      boolean flag1 = iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().isOccluding();
      return !flag1 && flag && e(iblockaccess, blockposition1.up())
         || a(iblockdata, enumdirection)
         || block == Blocks.POWERED_REPEATER && iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection
         || !flag && e(iblockaccess, blockposition1.down());
   }

   @Override
   public boolean isPowerSource() {
      return this.Q;
   }

   public void setPowerSource(boolean powerSource) {
      this.Q = powerSource;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(POWER, i);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(POWER);
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, NORTH, EAST, SOUTH, WEST, POWER);
   }

   public static enum EnumRedstoneWireConnection implements INamable {
      UP("up"),
      SIDE("side"),
      NONE("none");

      private final String d;

      private EnumRedstoneWireConnection(String s) {
         this.d = s;
      }

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this.d;
      }
   }
}
