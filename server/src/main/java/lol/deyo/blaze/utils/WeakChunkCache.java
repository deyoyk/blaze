package lol.deyo.blaze.utils;

import java.util.Arrays;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.IBlockAccess;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.World;

public class WeakChunkCache implements IBlockAccess {
   private final int lowerChunkX;
   private final int lowerChunkZ;
   private final Chunk[][] chunks;
   private boolean ownArray = false;

   public WeakChunkCache(Chunk[][] chunks, int lowerChunkX, int lowerChunkZ) {
      this.chunks = chunks;
      this.lowerChunkX = lowerChunkX;
      this.lowerChunkZ = lowerChunkZ;
   }

   public WeakChunkCache(World world, int chunkX, int chunkZ, int chunkRadius) {
      this(world, chunkX - chunkRadius, chunkZ - chunkRadius, chunkX + chunkRadius, chunkZ + chunkRadius);
   }

   public WeakChunkCache(World world, int lowerChunkX, int lowerChunkZ, int upperChunkX, int upperChunkZ) {
      this.lowerChunkX = lowerChunkX;
      this.lowerChunkZ = lowerChunkZ;
      this.chunks = new Chunk[upperChunkX - this.lowerChunkX + 1][upperChunkZ - this.lowerChunkZ + 1];
      this.ownArray = true;

      for (int chunkX = this.lowerChunkX; chunkX <= upperChunkX; chunkX++) {
         for (int chunkZ = this.lowerChunkZ; chunkZ <= upperChunkZ; chunkZ++) {
            this.chunks[chunkX - this.lowerChunkX][chunkZ - this.lowerChunkZ] = world.getChunkIfLoaded(chunkX, chunkZ);
         }
      }
   }

   public WeakChunkCache(World world, int lowerBlockX, int lowerBlockZ, int upperBlockX, int upperBlockZ, int blockRadius) {
      this(world, lowerBlockX - blockRadius >> 4, lowerBlockZ - blockRadius >> 4, upperBlockX + blockRadius >> 4, upperBlockZ + blockRadius >> 4);
   }

   public IBlockData getType(BlockPosition position) {
      IBlockData block = Blocks.AIR.getBlockData();
      if (position.getY() >= 0 && position.getY() < 256) {
         int indexX = (position.getX() >> 4) - this.lowerChunkX;
         int indexZ = (position.getZ() >> 4) - this.lowerChunkZ;
         if (indexX >= 0 && indexX < this.chunks.length && indexZ >= 0 && indexZ < this.chunks[indexX].length) {
            Chunk chunk = this.chunks[indexX][indexZ];
            if (chunk != null) {
               block = chunk.getType(position.getX() & 15, position.getY(), position.getZ() & 15).getBlockData();
            }
         }
      }

      return block;
   }

   public TileEntity getTileEntity(BlockPosition position) {
      int indexX = (position.getX() >> 4) - this.lowerChunkX;
      int indexZ = (position.getZ() >> 4) - this.lowerChunkZ;
      Chunk chunk = this.chunks[indexX][indexZ];
      return chunk != null ? chunk.i(position) : null;
   }

   public boolean isEmpty(BlockPosition blockPosition) {
      return this.getType(blockPosition) == Blocks.AIR.getBlockData();
   }

   public int getBlockPower(BlockPosition blockPosition, EnumDirection enumDirection) {
      IBlockData iblockdata = this.getType(blockPosition);
      return iblockdata.getBlock().b(this, blockPosition, iblockdata, enumDirection);
   }

   public boolean isLoaded(int x, int z) {
      int indexX = (x >> 4) - this.lowerChunkX;
      int indexZ = (z >> 4) - this.lowerChunkZ;
      return indexX >= 0 && indexX < this.chunks.length && indexZ >= 0 && indexZ < this.chunks[indexX].length ? this.chunks[indexX][indexZ] != null : false;
   }

   public void clear() {
      if (this.ownArray) {
         for (Chunk[] chunk : this.chunks) {
            Arrays.fill(chunk, null);
         }
      }
   }
}
