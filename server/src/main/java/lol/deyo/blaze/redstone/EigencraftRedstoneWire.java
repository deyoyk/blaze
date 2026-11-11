package lol.deyo.blaze.redstone;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.BlockRedstoneWire;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.EnumDirection.EnumDirectionLimit;
import org.bukkit.event.block.BlockRedstoneEvent;

public class EigencraftRedstoneWire extends BlockRedstoneWire {
   private List<EigencraftRedstoneWire.UpdateNode> updateQueue0 = Lists.newArrayList();
   private List<EigencraftRedstoneWire.UpdateNode> updateQueue1 = Lists.newArrayList();
   private List<EigencraftRedstoneWire.UpdateNode> updateQueue2 = Lists.newArrayList();
   private static final boolean[] update_redstone = new boolean[]{
      true,
      true,
      false,
      false,
      true,
      true,
      false,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      false,
      true,
      true,
      false,
      false
   };
   private static final int North = 0;
   private static final int East = 1;
   private static final int South = 2;
   private static final int West = 3;
   private static final int[] forward_is_north = new int[]{2, 3, 16, 19, 0, 4, 1, 5, 7, 8, 17, 20, 12, 13, 18, 21, 6, 9, 22, 14, 11, 10, 23, 15};
   private static final int[] forward_is_east = new int[]{2, 3, 16, 19, 4, 1, 5, 0, 17, 20, 12, 13, 18, 21, 7, 8, 22, 14, 11, 15, 23, 9, 6, 10};
   private static final int[] forward_is_south = new int[]{2, 3, 16, 19, 1, 5, 0, 4, 12, 13, 18, 21, 7, 8, 17, 20, 11, 15, 23, 10, 6, 14, 22, 9};
   private static final int[] forward_is_west = new int[]{2, 3, 16, 19, 5, 0, 4, 1, 18, 21, 7, 8, 17, 20, 12, 13, 23, 10, 6, 9, 22, 15, 11, 14};
   private static final int[][] reordering = new int[][]{forward_is_north, forward_is_east, forward_is_south, forward_is_west};
   private final Map<BlockPosition, EigencraftRedstoneWire.UpdateNode> nodeCache = Maps.newHashMap();
   private static final boolean old_current_change = false;
   private int currentWalkLayer = 0;
   private static final int[] rs_neighbors = new int[]{4, 5, 6, 7};
   private static final int[] rs_neighbors_up = new int[]{9, 11, 13, 15};
   private static final int[] rs_neighbors_dn = new int[]{8, 10, 12, 14};

   public static BlockPosition[] computeAllNeighbors(BlockPosition pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      return new BlockPosition[]{
         new BlockPosition(x - 1, y, z),
         new BlockPosition(x + 1, y, z),
         new BlockPosition(x, y - 1, z),
         new BlockPosition(x, y + 1, z),
         new BlockPosition(x, y, z - 1),
         new BlockPosition(x, y, z + 1),
         new BlockPosition(x - 2, y, z),
         new BlockPosition(x - 1, y - 1, z),
         new BlockPosition(x - 1, y + 1, z),
         new BlockPosition(x - 1, y, z - 1),
         new BlockPosition(x - 1, y, z + 1),
         new BlockPosition(x + 2, y, z),
         new BlockPosition(x + 1, y - 1, z),
         new BlockPosition(x + 1, y + 1, z),
         new BlockPosition(x + 1, y, z - 1),
         new BlockPosition(x + 1, y, z + 1),
         new BlockPosition(x, y - 2, z),
         new BlockPosition(x, y - 1, z - 1),
         new BlockPosition(x, y - 1, z + 1),
         new BlockPosition(x, y + 2, z),
         new BlockPosition(x, y + 1, z - 1),
         new BlockPosition(x, y + 1, z + 1),
         new BlockPosition(x, y, z - 2),
         new BlockPosition(x, y, z + 2)
      };
   }

   private static void orientNeighbors(EigencraftRedstoneWire.UpdateNode[] src, EigencraftRedstoneWire.UpdateNode[] dst, int heading) {
      int[] re = reordering[heading];

      for (int i = 0; i < 24; i++) {
         dst[i] = src[re[i]];
      }
   }

   private void identifyNode(World worldIn, EigencraftRedstoneWire.UpdateNode upd1) {
      BlockPosition pos = upd1.self;
      IBlockData oldState = worldIn.getType(pos);
      upd1.currentState = oldState;
      Block block = oldState.getBlock();
      if (block != this) {
         upd1.type = EigencraftRedstoneWire.UpdateNode.Type.OTHER;
      } else if (!this.canPlace(worldIn, pos)) {
         this.b(worldIn, pos, oldState, 0);
         worldIn.setAir(pos);
         upd1.type = EigencraftRedstoneWire.UpdateNode.Type.OTHER;
      } else {
         upd1.type = EigencraftRedstoneWire.UpdateNode.Type.REDSTONE;
      }
   }

   private static int computeHeading(int rx, int rz) {
      int code = rx + 1 + 3 * (rz + 1);
      switch (code) {
         case 0: {
            int j = ThreadLocalRandom.current().nextInt(0, 1);
            return j == 0 ? 0 : 3;
         }
         case 1:
            return 0;
         case 2: {
            int j = ThreadLocalRandom.current().nextInt(0, 1);
            return j == 0 ? 0 : 1;
         }
         case 3:
            return 3;
         case 4:
            return ThreadLocalRandom.current().nextInt(0, 4);
         case 5:
            return 1;
         case 6: {
            int j = ThreadLocalRandom.current().nextInt(0, 1);
            return j == 0 ? 2 : 3;
         }
         case 7:
            return 2;
         case 8: {
            int j = ThreadLocalRandom.current().nextInt(0, 1);
            return j == 0 ? 2 : 1;
         }
         default:
            return ThreadLocalRandom.current().nextInt(0, 4);
      }
   }

   private void updateNode(World worldIn, EigencraftRedstoneWire.UpdateNode upd1, int layer) {
      BlockPosition pos = upd1.self;
      upd1.visited = true;
      IBlockData oldState = upd1.currentState;
      IBlockData newState = this.calculateCurrentChanges(worldIn, upd1);
      if (newState != oldState) {
         upd1.currentState = newState;
         this.propagateChanges(worldIn, upd1, layer);
      }
   }

   private void findNeighbors(World worldIn, EigencraftRedstoneWire.UpdateNode upd1) {
      BlockPosition pos = upd1.self;
      BlockPosition[] neighbors = computeAllNeighbors(pos);
      EigencraftRedstoneWire.UpdateNode[] neighbor_nodes = new EigencraftRedstoneWire.UpdateNode[24];
      upd1.neighbor_nodes = new EigencraftRedstoneWire.UpdateNode[24];

      for (int i = 0; i < 24; i++) {
         BlockPosition pos2 = neighbors[i];
         EigencraftRedstoneWire.UpdateNode upd2 = this.nodeCache.get(pos2);
         if (upd2 == null) {
            upd2 = new EigencraftRedstoneWire.UpdateNode();
            upd2.self = pos2;
            upd2.parent = pos;
            this.nodeCache.put(pos2, upd2);
            this.identifyNode(worldIn, upd2);
         }

         if (update_redstone[i] || upd2.type != EigencraftRedstoneWire.UpdateNode.Type.REDSTONE) {
            neighbor_nodes[i] = upd2;
         }
      }

      boolean fromWest = neighbor_nodes[0].visited || neighbor_nodes[7].visited || neighbor_nodes[8].visited;
      boolean fromEast = neighbor_nodes[1].visited || neighbor_nodes[12].visited || neighbor_nodes[13].visited;
      boolean fromNorth = neighbor_nodes[4].visited || neighbor_nodes[17].visited || neighbor_nodes[20].visited;
      boolean fromSouth = neighbor_nodes[5].visited || neighbor_nodes[18].visited || neighbor_nodes[21].visited;
      int cx = 0;
      int cz = 0;
      if (fromWest) {
         cx++;
      }

      if (fromEast) {
         cx--;
      }

      if (fromNorth) {
         cz++;
      }

      if (fromSouth) {
         cz--;
      }

      int heading;
      if (cx == 0 && cz == 0) {
         heading = computeHeading(upd1.xbias, upd1.zbias);

         for (int i = 0; i < 24; i++) {
            EigencraftRedstoneWire.UpdateNode nn = neighbor_nodes[i];
            if (nn != null) {
               nn.xbias = upd1.xbias;
               nn.zbias = upd1.zbias;
            }
         }
      } else {
         if (cx != 0 && cz != 0) {
            if (upd1.xbias != 0) {
               cz = 0;
            }

            if (upd1.zbias != 0) {
               cx = 0;
            }
         }

         heading = computeHeading(cx, cz);

         for (int ix = 0; ix < 24; ix++) {
            EigencraftRedstoneWire.UpdateNode nn = neighbor_nodes[ix];
            if (nn != null) {
               nn.xbias = cx;
               nn.zbias = cz;
            }
         }
      }

      orientNeighbors(neighbor_nodes, upd1.neighbor_nodes, heading);
   }

   private void propagateChanges(World worldIn, EigencraftRedstoneWire.UpdateNode upd1, int layer) {
      if (upd1.neighbor_nodes == null) {
         this.findNeighbors(worldIn, upd1);
      }

      BlockPosition pos = upd1.self;
      int layer1 = layer + 1;

      for (int i = 0; i < 24; i++) {
         EigencraftRedstoneWire.UpdateNode upd2 = upd1.neighbor_nodes[i];
         if (upd2 != null && layer1 > upd2.layer) {
            upd2.layer = layer1;
            this.updateQueue1.add(upd2);
            upd2.parent = pos;
         }
      }

      int layer2 = layer + 2;

      for (int ix = 0; ix < 4; ix++) {
         EigencraftRedstoneWire.UpdateNode upd2 = upd1.neighbor_nodes[ix];
         if (upd2 != null && layer2 > upd2.layer) {
            upd2.layer = layer2;
            this.updateQueue2.add(upd2);
            upd2.parent = pos;
         }
      }
   }

   private void shiftQueue() {
      List<EigencraftRedstoneWire.UpdateNode> t = this.updateQueue0;
      t.clear();
      this.updateQueue0 = this.updateQueue1;
      this.updateQueue1 = this.updateQueue2;
      this.updateQueue2 = t;
   }

   private void breadthFirstWalk(World worldIn) {
      this.shiftQueue();

      for (this.currentWalkLayer = 1; !this.updateQueue0.isEmpty() || !this.updateQueue1.isEmpty(); this.currentWalkLayer++) {
         for (EigencraftRedstoneWire.UpdateNode upd : this.updateQueue0) {
            if (upd.type == EigencraftRedstoneWire.UpdateNode.Type.REDSTONE) {
               this.updateNode(worldIn, upd, this.currentWalkLayer);
            } else {
               BlockPosition self = upd.self;
               worldIn.d(self.getX(), self.getY(), self.getZ(), this, upd.parent);
            }
         }

         this.shiftQueue();
      }

      this.currentWalkLayer = 0;
   }

   private void scheduleReentrantNeighborChanged(World worldIn, BlockPosition pos, IBlockData newState, BlockPosition source) {
      if (source != null) {
         EigencraftRedstoneWire.UpdateNode src = this.nodeCache.get(source);
         if (src == null) {
            src = new EigencraftRedstoneWire.UpdateNode();
            src.self = source;
            src.parent = source;
            src.visited = true;
            this.identifyNode(worldIn, src);
            this.nodeCache.put(source, src);
         }
      }

      EigencraftRedstoneWire.UpdateNode upd = this.nodeCache.get(pos);
      if (upd == null) {
         upd = new EigencraftRedstoneWire.UpdateNode();
         upd.self = pos;
         upd.parent = pos;
         upd.visited = true;
         this.identifyNode(worldIn, upd);
         this.nodeCache.put(pos, upd);
      }

      upd.currentState = newState;
      if (upd.neighbor_nodes != null) {
         for (int i = 0; i < 24; i++) {
            EigencraftRedstoneWire.UpdateNode upd2 = upd.neighbor_nodes[i];
            if (upd2 != null) {
               upd2.type = EigencraftRedstoneWire.UpdateNode.Type.UNKNOWN;
               upd2.currentState = null;
               this.identifyNode(worldIn, upd2);
            }
         }
      }

      this.propagateChanges(worldIn, upd, this.currentWalkLayer);
   }

   public void updateSurroundingRedstone(World worldIn, BlockPosition pos, IBlockData state, BlockPosition source) {
      IBlockData newState = this.calculateCurrentChanges(worldIn, pos, pos, state);
      if (newState != state) {
         if (this.currentWalkLayer <= 0 && this.nodeCache.isEmpty()) {
            if (source != null) {
               EigencraftRedstoneWire.UpdateNode src = new EigencraftRedstoneWire.UpdateNode();
               src.self = source;
               src.parent = source;
               src.visited = true;
               this.nodeCache.put(source, src);
               this.identifyNode(worldIn, src);
            }

            EigencraftRedstoneWire.UpdateNode upd = new EigencraftRedstoneWire.UpdateNode();
            upd.self = pos;
            upd.parent = source != null ? source : pos;
            upd.currentState = newState;
            upd.type = EigencraftRedstoneWire.UpdateNode.Type.REDSTONE;
            upd.visited = true;
            this.nodeCache.put(pos, upd);
            this.propagateChanges(worldIn, upd, 0);
            this.breadthFirstWalk(worldIn);
            this.nodeCache.clear();
         } else {
            this.scheduleReentrantNeighborChanged(worldIn, pos, newState, source);
         }
      }
   }

   private IBlockData calculateCurrentChanges(World worldIn, EigencraftRedstoneWire.UpdateNode upd) {
      IBlockData state = upd.currentState;
      int i = (Integer)state.get(BlockRedstoneWire.POWER);
      int l = 0;
      this.setPowerSource(false);
      int k = worldIn.A(upd.self);
      this.setPowerSource(true);
      if (k < 15) {
         if (upd.neighbor_nodes == null) {
            this.findNeighbors(worldIn, upd);
         }

         EigencraftRedstoneWire.UpdateNode center_up = upd.neighbor_nodes[1];
         boolean center_up_is_cube = center_up.currentState.getBlock().isOccluding();

         for (int m = 0; m < 4; m++) {
            int n = rs_neighbors[m];
            EigencraftRedstoneWire.UpdateNode neighbor = upd.neighbor_nodes[n];
            l = getMaxCurrentStrength(neighbor, l);
            boolean neighbor_is_cube = neighbor.currentState.getBlock().isOccluding();
            if (!neighbor_is_cube) {
               EigencraftRedstoneWire.UpdateNode neighbor_down = upd.neighbor_nodes[rs_neighbors_dn[m]];
               l = getMaxCurrentStrength(neighbor_down, l);
            } else if (!center_up_is_cube) {
               EigencraftRedstoneWire.UpdateNode neighbor_up = upd.neighbor_nodes[rs_neighbors_up[m]];
               l = getMaxCurrentStrength(neighbor_up, l);
            }
         }
      }

      int j = l - 1;
      if (k > j) {
         j = k;
      }

      if (i != j) {
         BlockRedstoneEvent event = new BlockRedstoneEvent(worldIn.getWorld().getBlockAt(upd.self.getX(), upd.self.getY(), upd.self.getZ()), i, j);
         worldIn.getServer().getPluginManager().callEvent(event);
         j = event.getNewCurrent();
      }

      if (i != j) {
         BlockPosition pos = new BlockPosition(upd.self.getX(), upd.self.getY(), upd.self.getZ());
         if (this.canPlace(worldIn, pos)) {
            state = state.set(BlockRedstoneWire.POWER, j);
            worldIn.setTypeAndData(upd.self, state, 2);
         }
      }

      return state;
   }

   public IBlockData calculateCurrentChanges(World worldIn, BlockPosition pos1, BlockPosition pos2, IBlockData state) {
      IBlockData iblockstate = state;
      int i = (Integer)state.get(POWER);
      int j = 0;
      this.getPower(worldIn, pos2, j);
      this.setPowerSource(false);
      int k = worldIn.A(pos1);
      this.setPowerSource(true);
      int l = 0;
      if (k < 15) {
         for (EnumDirection enumfacing : EnumDirectionLimit.HORIZONTAL) {
            BlockPosition blockpos = pos1.shift(enumfacing);
            boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();
            if (flag) {
               l = this.getPower(worldIn, blockpos, l);
            }

            if (worldIn.getType(blockpos).getBlock().isOccluding() && !worldIn.getType(pos1.up()).getBlock().isOccluding()) {
               if (flag && pos1.getY() >= pos2.getY()) {
                  l = this.getPower(worldIn, blockpos.up(), l);
               }
            } else if (!worldIn.getType(blockpos).getBlock().isOccluding() && flag && pos1.getY() <= pos2.getY()) {
               l = this.getPower(worldIn, blockpos.down(), l);
            }
         }
      }

      j = l - 1;
      if (k > j) {
         j = k;
      }

      if (i != j) {
         state = state.set(POWER, j);
         if (worldIn.getType(pos1) == iblockstate) {
            worldIn.setTypeAndData(pos1, state, 2);
         }
      }

      return state;
   }

   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.updateSurroundingRedstone(world, blockposition, iblockdata, null);

      for (EnumDirection enumdirection : EnumDirectionLimit.VERTICAL) {
         world.applyPhysics(blockposition.shift(enumdirection), this);
      }

      for (EnumDirection enumdirection : EnumDirectionLimit.HORIZONTAL) {
         this.e(world, blockposition.shift(enumdirection));
      }

      for (EnumDirection enumdirection : EnumDirectionLimit.HORIZONTAL) {
         BlockPosition blockposition1 = blockposition.shift(enumdirection);
         if (world.getType(blockposition1).getBlock().isOccluding()) {
            this.e(world, blockposition1.up());
         } else {
            this.e(world, blockposition1.down());
         }
      }
   }

   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.remove(world, blockposition, iblockdata);

      EnumDirection[] var7;
      for (EnumDirection enumdirection : var7 = EnumDirection.values()) {
         world.applyPhysics(blockposition.shift(enumdirection), this);
      }

      this.updateSurroundingRedstone(world, blockposition, iblockdata, null);

      for (EnumDirection enumdirection1 : EnumDirectionLimit.HORIZONTAL) {
         this.e(world, blockposition.shift(enumdirection1));
      }

      for (EnumDirection enumdirection1 : EnumDirectionLimit.HORIZONTAL) {
         BlockPosition blockposition1 = blockposition.shift(enumdirection1);
         if (world.getType(blockposition1).getBlock().isOccluding()) {
            this.e(world, blockposition1.up());
         } else {
            this.e(world, blockposition1.down());
         }
      }
   }

   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block, BlockPosition source) {
      if (this.canPlace(world, blockposition)) {
         this.updateSurroundingRedstone(world, blockposition, iblockdata, source);
      } else {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }
   }

   private static int getMaxCurrentStrength(EigencraftRedstoneWire.UpdateNode upd, int strength) {
      if (upd.type != EigencraftRedstoneWire.UpdateNode.Type.REDSTONE) {
         return strength;
      } else {
         int i = (Integer)upd.currentState.get(BlockRedstoneWire.POWER);
         return Math.max(i, strength);
      }
   }

   private static class UpdateNode {
      IBlockData currentState;
      EigencraftRedstoneWire.UpdateNode[] neighbor_nodes;
      BlockPosition self;
      BlockPosition parent;
      EigencraftRedstoneWire.UpdateNode.Type type = EigencraftRedstoneWire.UpdateNode.Type.UNKNOWN;
      int layer;
      boolean visited;
      int xbias;
      int zbias;

      private UpdateNode() {
      }

      public static enum Type {
         UNKNOWN,
         REDSTONE,
         OTHER;
      }
   }
}
