package org.bukkit.craftbukkit.v1_8_R3.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.block.BlockState;

public class BlockStateListPopulator {
   private final org.bukkit.World world;
   private final List<BlockState> list;

   public BlockStateListPopulator(org.bukkit.World world) {
      this(world, new ArrayList<>());
   }

   public BlockStateListPopulator(org.bukkit.World world, List<BlockState> list) {
      this.world = world;
      this.list = list;
   }

   public void setTypeAndData(int x, int y, int z, Block block, int data, int light) {
      BlockState state = this.world.getBlockAt(x, y, z).getState();
      state.setTypeId(Block.getId(block));
      state.setRawData((byte)data);
      this.list.add(state);
   }

   public void setTypeId(int x, int y, int z, int type) {
      BlockState state = this.world.getBlockAt(x, y, z).getState();
      state.setTypeId(type);
      this.list.add(state);
   }

   public void setTypeUpdate(int x, int y, int z, Block block) {
      this.setType(x, y, z, block);
   }

   public void setTypeUpdate(BlockPosition position, IBlockData data) {
      this.setTypeAndData(position.getX(), position.getY(), position.getZ(), data.getBlock(), data.getBlock().toLegacyData(data), 0);
   }

   public void setType(int x, int y, int z, Block block) {
      BlockState state = this.world.getBlockAt(x, y, z).getState();
      state.setTypeId(Block.getId(block));
      this.list.add(state);
   }

   public void updateList() {
      for (BlockState state : this.list) {
         state.update(true);
      }
   }

   public List<BlockState> getList() {
      return this.list;
   }

   public org.bukkit.World getWorld() {
      return this.world;
   }
}
