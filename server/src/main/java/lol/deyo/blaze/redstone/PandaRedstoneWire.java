package lol.deyo.blaze.redstone;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lol.deyo.blaze.utils.LinkedArraySet;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.server.v1_8_R3.BaseBlockPosition;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockDiodeAbstract;
import net.minecraft.server.v1_8_R3.BlockPiston;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.BlockRedstoneComparator;
import net.minecraft.server.v1_8_R3.BlockRedstoneTorch;
import net.minecraft.server.v1_8_R3.BlockRedstoneWire;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.IBlockAccess;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.EnumDirection.EnumDirectionLimit;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.event.block.BlockRedstoneEvent;

public class PandaRedstoneWire extends BlockRedstoneWire {
   private final List<BlockPosition> turnOff = Lists.newArrayList();
   private final List<BlockPosition> turnOn = Lists.newArrayList();
   private final Set<BlockPosition> updatedRedstoneWire = new LinkedArraySet<>();
   private static final EnumDirection[] facingsHorizontal = new EnumDirection[]{
      EnumDirection.WEST, EnumDirection.EAST, EnumDirection.NORTH, EnumDirection.SOUTH
   };
   private static final EnumDirection[] facingsVertical = new EnumDirection[]{EnumDirection.DOWN, EnumDirection.UP};
   private static final EnumDirection[] facings = (EnumDirection[])ArrayUtils.addAll(facingsVertical, facingsHorizontal);
   private static final BaseBlockPosition[] surroundingBlocksOffset;
   private boolean canProvidePower = true;

   static {
      Set<BaseBlockPosition> set = Sets.newLinkedHashSet();

      for (EnumDirection facing : facings) {
         set.add(PandaRedstoneWire.ReflectUtil.getOfT(facing, BaseBlockPosition.class));
      }

      for (EnumDirection facing1 : facings) {
         BaseBlockPosition v1 = PandaRedstoneWire.ReflectUtil.getOfT(facing1, BaseBlockPosition.class);

         for (EnumDirection facing2 : facings) {
            BaseBlockPosition v2 = PandaRedstoneWire.ReflectUtil.getOfT(facing2, BaseBlockPosition.class);
            if (v2 != null && v1 != null) {
               set.add(new BlockPosition(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ()));
            }
         }
      }

      set.remove(BlockPosition.ZERO);
      surroundingBlocksOffset = set.toArray(new BaseBlockPosition[0]);
   }

   private void updateSurroundingRedstone(World worldIn, BlockPosition pos, IBlockData iblockdata) {
      this.calculateCurrentChanges(worldIn, pos, iblockdata);
      Set<BlockPosition> blocksNeedingUpdate = Sets.newLinkedHashSet();

      for (BlockPosition posi : this.updatedRedstoneWire) {
         this.addBlocksNeedingUpdate(worldIn, posi, blocksNeedingUpdate);
      }

      Iterator<BlockPosition> it = Lists.newLinkedList(this.updatedRedstoneWire).descendingIterator();

      while (it.hasNext()) {
         this.addAllSurroundingBlocks(it.next(), blocksNeedingUpdate);
      }

      blocksNeedingUpdate.removeAll(this.updatedRedstoneWire);
      this.updatedRedstoneWire.clear();

      for (BlockPosition posi : blocksNeedingUpdate) {
         worldIn.d(posi, this);
      }
   }

   protected void calculateCurrentChanges(World worldIn, BlockPosition position, IBlockData state) {
      if (state.getBlock() == this) {
         this.turnOff.add(position);
      } else {
         this.checkSurroundingWires(worldIn, position);
      }

      while (!this.turnOff.isEmpty()) {
         BlockPosition pos = this.turnOff.remove(0);
         state = worldIn.getType(pos);
         int oldPower = (Integer)state.get(POWER);
         this.canProvidePower = false;
         int blockPower = worldIn.A(pos);
         this.canProvidePower = true;
         int wirePower = this.getSurroundingWirePower(worldIn, pos);
         int newPower = Math.max(blockPower, --wirePower);
         if (newPower < oldPower) {
            if (blockPower > 0 && !this.turnOn.contains(pos)) {
               this.turnOn.add(pos);
            }

            this.setWireState(worldIn, pos, state, 0);
         } else if (newPower > oldPower) {
            this.setWireState(worldIn, pos, state, newPower);
         }

         this.checkSurroundingWires(worldIn, pos);
      }

      while (!this.turnOn.isEmpty()) {
         BlockPosition pos = this.turnOn.remove(0);
         state = worldIn.getType(pos);
         int oldPower = (Integer)state.get(POWER);
         this.canProvidePower = false;
         int blockPower = worldIn.A(pos);
         this.canProvidePower = true;
         int wirePower = this.getSurroundingWirePower(worldIn, pos);
         int newPower = Math.max(blockPower, --wirePower);
         if (oldPower != newPower) {
            BlockRedstoneEvent event = new BlockRedstoneEvent(worldIn.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), oldPower, newPower);
            worldIn.getServer().getPluginManager().callEvent(event);
            newPower = event.getNewCurrent();
         }

         if (newPower > oldPower) {
            this.setWireState(worldIn, pos, state, newPower);
         }

         this.checkSurroundingWires(worldIn, pos);
      }

      this.turnOff.clear();
   }

   protected void addWireToList(World worldIn, BlockPosition pos, int otherPower) {
      IBlockData state = worldIn.getType(pos);
      if (state.getBlock() == this) {
         int power = (Integer)state.get(POWER);
         if (power < otherPower - 1 && !this.turnOn.contains(pos)) {
            this.turnOn.add(pos);
         }

         if (power > otherPower && !this.turnOff.contains(pos)) {
            this.turnOff.add(pos);
         }
      }
   }

   protected void checkSurroundingWires(World worldIn, BlockPosition pos) {
      IBlockData state = worldIn.getType(pos);
      int ownPower = 0;
      if (state.getBlock() == Blocks.REDSTONE_WIRE) {
         ownPower = (Integer)state.get(POWER);
      }

      for (EnumDirection facingHorizontal : facingsHorizontal) {
         this.addWireToList(worldIn, pos.shift(facingHorizontal), ownPower);
      }

      for (EnumDirection facingVertical : facingsVertical) {
         BlockPosition offsetPos = pos.shift(facingVertical);
         Block block = worldIn.getType(offsetPos).getBlock();
         boolean solidBlock = block.u();

         for (EnumDirection facingHorizontal : facingsHorizontal) {
            if (facingVertical == EnumDirection.UP && (!solidBlock || block == Blocks.GLOWSTONE)
               || facingVertical == EnumDirection.DOWN && solidBlock && !worldIn.getType(offsetPos.shift(facingHorizontal)).getBlock().isOccluding()) {
               this.addWireToList(worldIn, offsetPos.shift(facingHorizontal), ownPower);
            }
         }
      }
   }

   private int getSurroundingWirePower(World worldIn, BlockPosition pos) {
      int wirePower = 0;

      for (EnumDirection enumfacing : EnumDirectionLimit.HORIZONTAL) {
         BlockPosition offsetPos = pos.shift(enumfacing);
         IBlockData iblockdata = worldIn.getType(offsetPos);
         boolean occluding = iblockdata.getBlock().isOccluding();
         wirePower = this.getPower(iblockdata, wirePower);
         if (occluding && !worldIn.getType(pos.up()).getBlock().isOccluding()) {
            wirePower = this.getPower(worldIn, offsetPos.up(), wirePower);
         } else if (!occluding) {
            wirePower = this.getPower(worldIn, offsetPos.down(), wirePower);
         }
      }

      return wirePower;
   }

   private void addBlocksNeedingUpdate(World worldIn, BlockPosition pos, Set<BlockPosition> set) {
      Set<EnumDirection> connectedSides = this.getSidesToPower(worldIn, pos);

      for (EnumDirection facing : facings) {
         BlockPosition offsetPos = pos.shift(facing);
         IBlockData state = worldIn.getType(offsetPos);
         boolean flag = connectedSides.contains(facing.opposite()) || facing == EnumDirection.DOWN;
         if ((flag || facing.k().c() && a(state, facing)) && this.canBlockBePoweredFromSide(state, facing, true)) {
            set.add(offsetPos);
         }

         if (flag && worldIn.getType(offsetPos).getBlock().isOccluding()) {
            for (EnumDirection facing1 : facings) {
               if (this.canBlockBePoweredFromSide(worldIn.getType(offsetPos.shift(facing1)), facing1, false)) {
                  set.add(offsetPos.shift(facing1));
               }
            }
         }
      }
   }

   private boolean canBlockBePoweredFromSide(IBlockData state, EnumDirection side, boolean isWire) {
      Block block = state.getBlock();
      if (block == Blocks.AIR) {
         return false;
      } else if (block instanceof BlockPiston && state.get(BlockPiston.FACING) == side.opposite()) {
         return false;
      } else {
         return block instanceof BlockDiodeAbstract && state.get(BlockDiodeAbstract.FACING) != side.opposite()
            ? isWire && block instanceof BlockRedstoneComparator && ((EnumDirection)state.get(BlockRedstoneComparator.FACING)).k() != side.k() && side.k().c()
            : !(state.getBlock() instanceof BlockRedstoneTorch) || !isWire && state.get(BlockRedstoneTorch.FACING) == side;
      }
   }

   private Set<EnumDirection> getSidesToPower(World worldIn, BlockPosition pos) {
      Set<EnumDirection> retval = Sets.newHashSet();

      for (EnumDirection facing : facingsHorizontal) {
         if (this.isPowerSourceAt(worldIn, pos, facing)) {
            retval.add(facing);
         }
      }

      if (retval.isEmpty()) {
         return Sets.newHashSet(facingsHorizontal);
      } else {
         boolean northsouth = retval.contains(EnumDirection.NORTH) || retval.contains(EnumDirection.SOUTH);
         boolean eastwest = retval.contains(EnumDirection.EAST) || retval.contains(EnumDirection.WEST);
         if (northsouth) {
            retval.remove(EnumDirection.EAST);
            retval.remove(EnumDirection.WEST);
         }

         if (eastwest) {
            retval.remove(EnumDirection.NORTH);
            retval.remove(EnumDirection.SOUTH);
         }

         return retval;
      }
   }

   private boolean canSidePower(World worldIn, BlockPosition pos, EnumDirection side) {
      Set<EnumDirection> retval = Sets.newHashSet();

      for (EnumDirection facing : facingsHorizontal) {
         if (this.isPowerSourceAt(worldIn, pos, facing)) {
            retval.add(facing);
         }
      }

      if (retval.isEmpty()) {
         return true;
      } else {
         boolean northsouth = retval.contains(EnumDirection.NORTH) || retval.contains(EnumDirection.SOUTH);
         boolean eastwest = retval.contains(EnumDirection.EAST) || retval.contains(EnumDirection.WEST);
         if (northsouth) {
            retval.remove(EnumDirection.EAST);
            retval.remove(EnumDirection.WEST);
         }

         if (eastwest) {
            retval.remove(EnumDirection.NORTH);
            retval.remove(EnumDirection.SOUTH);
         }

         return retval.contains(side);
      }
   }

   private void addAllSurroundingBlocks(BlockPosition pos, Set<BlockPosition> set) {
      for (BaseBlockPosition vect : surroundingBlocksOffset) {
         set.add(pos.a(vect));
      }
   }

   private void setWireState(World worldIn, BlockPosition pos, IBlockData state, int power) {
      state = state.set(POWER, power);
      worldIn.setTypeAndData(pos, state, 2);
      this.updatedRedstoneWire.add(pos);
   }

   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.updateSurroundingRedstone(world, blockposition, world.getType(blockposition));

      EnumDirection[] var7;
      for (EnumDirection enumdirection : var7 = EnumDirection.values()) {
         world.applyPhysics(blockposition.shift(enumdirection), this);
      }
   }

   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      EnumDirection[] var7;
      for (EnumDirection enumdirection : var7 = EnumDirection.values()) {
         world.applyPhysics(blockposition.shift(enumdirection), this);
      }

      this.updateSurroundingRedstone(world, blockposition, world.getType(blockposition));
   }

   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block, BlockPosition source) {
      if (this.canPlace(world, blockposition)) {
         this.updateSurroundingRedstone(world, blockposition, iblockdata);
      } else {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }
   }

   protected final int getPower(IBlockData state, int power) {
      if (state.getBlock() != Blocks.REDSTONE_WIRE) {
         return power;
      } else {
         int j = (Integer)state.get(BlockRedstoneWire.POWER);
         return Math.max(j, power);
      }
   }

   public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      if (!this.canProvidePower) {
         return 0;
      } else {
         int i = (Integer)iblockdata.get(BlockRedstoneWire.POWER);
         if (i == 0) {
            return 0;
         } else if (enumdirection == EnumDirection.UP) {
            return i;
         } else {
            return this.canSidePower((World)iblockaccess, blockposition, enumdirection) ? i : 0;
         }
      }
   }

   private boolean isPowerSourceAt(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
      BlockPosition blockpos = blockposition.shift(enumdirection);
      IBlockData iblockdata = iblockaccess.getType(blockpos);
      Block block = iblockdata.getBlock();
      boolean flag = block.isOccluding();
      boolean flag1 = iblockaccess.getType(blockposition.up()).getBlock().isOccluding();
      return !flag1 && flag && e(iblockaccess, blockpos.up())
         || a(iblockdata, enumdirection)
         || block == Blocks.POWERED_REPEATER && iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection
         || !flag && e(iblockaccess, blockpos.down());
   }

   public static class ReflectUtil {
      public static <T> T getOfT(Object obj, Class<T> type) {
         Field[] var5;
         for (Field field : var5 = obj.getClass().getDeclaredFields()) {
            if (type.equals(field.getType())) {
               return get(obj, field, type);
            }
         }

         return null;
      }

      public static <T> T get(Object obj, Field field, Class<T> type) {
         try {
            field.setAccessible(true);
            return type.cast(field.get(obj));
         } catch (ReflectiveOperationException var4) {
            var4.printStackTrace();
            return null;
         }
      }
   }
}
