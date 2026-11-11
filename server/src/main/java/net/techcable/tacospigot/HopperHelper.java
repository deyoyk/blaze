package net.techcable.tacospigot;

import lol.deyo.blaze.configs.BlazeConfig;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockChest;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.IHopper;
import net.minecraft.server.v1_8_R3.IInventory;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityHopper;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class HopperHelper {
   public static TileEntityHopper getHopper(World world, BlockPosition pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      IBlockData iblockdata = null;
      if (world.captureTreeGeneration) {
         iblockdata = world.getCapturedBlockType(x, y, z);
      }

      if (iblockdata == null && !world.isValidLocation(x, y, z)) {
         return null;
      } else {
         Chunk chunk = world.getChunkAtWorldCoords(x, z);
         Block block = chunk.getBlockData(x, y, z).getBlock();
         if (block != Blocks.HOPPER) {
            return null;
         } else {
            TileEntity tileEntity = world.getTileEntity(chunk, pos, block);
            return tileEntity instanceof TileEntityHopper ? (TileEntityHopper)tileEntity : null;
         }
      }
   }

   public static IInventory getInventory(World world, BlockPosition position) {
      if (!world.isValidLocation(position.getX(), position.getY(), position.getZ())) {
         return null;
      } else {
         Chunk chunk = world.getChunkIfLoaded(position.getX() >> 4, position.getZ() >> 4);
         if (chunk == null) {
            return null;
         } else {
            Block block = world.getType(chunk, position).getBlock();
            if (block instanceof BlockChest) {
               return ((BlockChest)block).f(world, position);
            } else {
               if (block.isTileEntity()) {
                  TileEntity tile = world.getTileEntity(position);
                  if (tile instanceof IInventory) {
                     return (IInventory)tile;
                  }
               }

               return null;
            }
         }
      }
   }

   public static boolean isFireInventoryMoveItemEvent(IHopper hopper) {
      return (Boolean)BlazeConfig.fireHopperMoveItemEvent.getValue() && InventoryMoveItemEvent.getHandlerList().getRegisteredListeners().length > 0;
   }
}
