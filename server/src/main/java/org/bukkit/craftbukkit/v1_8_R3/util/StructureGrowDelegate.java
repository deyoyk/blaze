package org.bukkit.craftbukkit.v1_8_R3.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.Blocks;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.material.MaterialData;

public class StructureGrowDelegate implements org.bukkit.BlockChangeDelegate {
   private final CraftWorld world;
   private final List<BlockState> blocks = new ArrayList<>();

   public StructureGrowDelegate(net.minecraft.server.v1_8_R3.World world) {
      this.world = world.getWorld();
   }

   @Override
   public boolean setRawTypeId(int x, int y, int z, int type) {
      return this.setRawTypeIdAndData(x, y, z, type, 0);
   }

   @Override
   public boolean setRawTypeIdAndData(int x, int y, int z, int type, int data) {
      BlockState state = this.world.getBlockAt(x, y, z).getState();
      state.setTypeId(type);
      state.setData(new MaterialData(type, (byte)data));
      this.blocks.add(state);
      return true;
   }

   @Override
   public boolean setTypeId(int x, int y, int z, int typeId) {
      return this.setRawTypeId(x, y, z, typeId);
   }

   @Override
   public boolean setTypeIdAndData(int x, int y, int z, int typeId, int data) {
      return this.setRawTypeIdAndData(x, y, z, typeId, data);
   }

   @Override
   public int getTypeId(int x, int y, int z) {
      for (BlockState state : this.blocks) {
         if (state.getX() == x && state.getY() == y && state.getZ() == z) {
            return state.getTypeId();
         }
      }

      return this.world.getBlockTypeIdAt(x, y, z);
   }

   @Override
   public int getHeight() {
      return this.world.getMaxHeight();
   }

   public List<BlockState> getBlocks() {
      return this.blocks;
   }

   @Override
   public boolean isEmpty(int x, int y, int z) {
      for (BlockState state : this.blocks) {
         if (state.getX() == x && state.getY() == y && state.getZ() == z) {
            if (Block.getById(state.getTypeId()) == Blocks.AIR) {
               return true;
            }

            return false;
         }
      }

      return this.world.getBlockAt(x, y, z).isEmpty();
   }
}
