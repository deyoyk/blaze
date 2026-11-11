package lol.deyo.blaze.optimizations;

import gnu.trove.set.TByteSet;
import gnu.trove.set.hash.TByteHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.World.Environment;
import org.spigotmc.SpigotWorldConfig;

public class FastAntiXray {
   public final boolean[] obfuscateBlocks = new boolean[32767];
   public final boolean[] occludingBlocks = new boolean[32767];
   public final byte[] replacementOres;
   public boolean queueUpdates = true;
   public final List<BlockPosition> pendingUpdates = new ArrayList<>();
   private static final char STONE_STATE = (char)Block.d.b(Blocks.STONE.getBlockData());
   private static final char NETHER_RACK_STATE = (char)Block.d.b(Blocks.NETHERRACK.getBlockData());
   private static final char END_STONE_STATE = (char)Block.d.b(Blocks.END_STONE.getBlockData());

   public FastAntiXray(SpigotWorldConfig config) {
      Set<Block> configuratedBlocks = new HashSet<>();

      for (int id : config.engineMode == 1 ? config.hiddenBlocks : config.replaceBlocks) {
         configuratedBlocks.add(Block.getById(id));
      }

      IBlockData[] registry = ((ConstantRegistryID)Block.d).getAllStates();

      for (int i = 0; i < registry.length; i++) {
         IBlockData blockData = registry[i];
         if (blockData != null) {
            Block block = blockData.getBlock();
            this.occludingBlocks[i] = isSolidBlock(block);
            if (configuratedBlocks.contains(block)) {
               this.obfuscateBlocks[i] = true;
            }
         }
      }

      TByteSet blocks = new TByteHashSet();

      for (Integer ix : config.hiddenBlocks) {
         Block block = Block.getById(ix);
         if (block != null && !block.isTileEntity()) {
            blocks.add((byte)ix.intValue());
         }
      }

      this.replacementOres = blocks.toArray();
   }

   public boolean isOccluding(char block) {
      return this.occludingBlocks[block];
   }

   public void flushUpdates(World world) {
      if (world.spigotConfig.antiXray && !this.pendingUpdates.isEmpty()) {
         this.queueUpdates = false;

         for (BlockPosition position : this.pendingUpdates) {
            this.updateNearbyBlocks(world, position);
         }

         this.pendingUpdates.clear();
         this.queueUpdates = true;
      }
   }

   public void updateNearbyBlocks(World world, BlockPosition position) {
      if (world.spigotConfig.antiXray) {
         if (this.queueUpdates) {
            this.pendingUpdates.add(position);
            return;
         }

         this.updateNearbyBlocks(world, position.getX(), position.getY(), position.getZ());
      }
   }

   private void updateNearbyBlocks(World world, int x, int y, int z) {
      int startX = x - 2;
      int endX = x + 2;
      int startY = Math.max(0, y - 2);
      int endY = Math.min(255, y + 2);
      int startZ = z - 2;
      int endZ = z + 2;
      Chunk chunk = null;

      for (int x1 = startX; x1 <= endX; x1++) {
         for (int y1 = startY; y1 <= endY; y1++) {
            for (int z1 = startZ; z1 <= endZ; z1++) {
               if (!(x1 == x & y1 == y & z1 == z)) {
                  int chunkX = x1 >> 4;
                  int chunkZ = z1 >> 4;
                  Chunk updating;
                  if (chunk != null && chunk.locX == chunkX && chunk.locZ == chunkZ) {
                     updating = chunk;
                  } else {
                     updating = world.getChunkIfLoaded(chunkX, chunkZ);
                     chunk = updating;
                  }

                  this.updateBlock(updating, world, x1, y1, z1);
               }
            }
         }
      }
   }

   private void updateBlock(Chunk chunk, World world, int x, int y, int z) {
      if (chunk != null) {
         int sectionId = y >> 4;
         if (sectionId >= 0 && sectionId <= 15) {
            ChunkSection section = chunk.getSections()[sectionId];
            if (section != null) {
               int index = (y & 15) << 8 | (z & 15) << 4 | x & 15;
               if (this.obfuscateBlocks[section.getIdArray()[index]]) {
                  world.notify(x, y, z);
               }
            }
         }
      }
   }

   private static boolean isSolidBlock(Block block) {
      return block.isOccluding() && block != Blocks.MOB_SPAWNER && block != Blocks.BARRIER;
   }

   public char getReplaceId(Environment environment) {
      switch (environment) {
         case NETHER:
            return NETHER_RACK_STATE;
         case THE_END:
            return END_STONE_STATE;
         default:
            return STONE_STATE;
      }
   }

   public char getReplacedIdEngine1(Chunk chunk, ChunkSection section, ChunkSection[] sending, char blockId, int k, int bitmask, int i, int totalSections) {
      World world = chunk.world;
      int x = k & 15;
      int y = k >> 8 & 15;
      int z = k >> 4 & 15;
      int posX = x + 1;
      int negX = x - 1;
      int posY = y + 1;
      int negY = y - 1;
      int posZ = z + 1;
      int negZ = z - 1;
      if (posY > 15 && i + 1 < totalSections) {
         ChunkSection upperSection = sending[i + 1];
         if (bitmask != 65535 || upperSection == null) {
            return blockId;
         }

         int newIndex = k & 0xFF;
         if (!this.isOccluding(upperSection.getBlockId(newIndex))) {
            return blockId;
         }
      } else if (posY <= 15) {
         int newIndex = k & 0xFF | posY << 8;
         if (!this.isOccluding(section.getBlockId(newIndex))) {
            return blockId;
         }
      }

      if (negY < 0 && i - 1 >= 0) {
         ChunkSection upperSectionx = sending[i - 1];
         if (bitmask != 65535 || upperSectionx == null) {
            return blockId;
         }

         int newIndex = k & 0xFF | 3840;
         if (!this.isOccluding(upperSectionx.getBlockId(newIndex))) {
            return blockId;
         }
      } else if (negY >= 0) {
         int newIndex = k & 0xFF | negY << 8;
         if (!this.isOccluding(section.getBlockId(newIndex))) {
            return blockId;
         }
      }

      if (posX > 15) {
         Chunk neighbourChunk = world.getChunkIfLoaded(chunk.locX + 1, chunk.locZ);
         if (neighbourChunk == null) {
            return blockId;
         }

         ChunkSection neighbourSection = neighbourChunk.getSection(section.getYPosition());
         if (neighbourSection == null) {
            return blockId;
         }

         int newIndex = k & 4080;
         if (!this.isOccluding(neighbourSection.getBlockId(newIndex))) {
            return blockId;
         }
      } else {
         int newIndex = k & 4080 | posX;
         if (!this.isOccluding(section.getBlockId(newIndex))) {
            return blockId;
         }
      }

      if (negX < 0) {
         Chunk neighbourChunkx = world.getChunkIfLoaded(chunk.locX - 1, chunk.locZ);
         if (neighbourChunkx == null) {
            return blockId;
         }

         ChunkSection neighbourSectionx = neighbourChunkx.getSection(section.getYPosition());
         if (neighbourSectionx == null) {
            return blockId;
         }

         int newIndexx = k & 4080 | 15;
         if (!this.isOccluding(neighbourSectionx.getBlockId(newIndexx))) {
            return blockId;
         }
      } else {
         int newIndexx = k & 4080 | negX;
         if (!this.isOccluding(section.getBlockId(newIndexx))) {
            return blockId;
         }
      }

      if (posZ > 15) {
         Chunk neighbourChunkxx = world.getChunkIfLoaded(chunk.locX, chunk.locZ + 1);
         if (neighbourChunkxx == null) {
            return blockId;
         }

         ChunkSection neighbourSectionxx = neighbourChunkxx.getSection(section.getYPosition());
         if (neighbourSectionxx == null) {
            return blockId;
         }

         int newIndexxx = k & 3855;
         if (!this.isOccluding(neighbourSectionxx.getBlockId(newIndexxx))) {
            return blockId;
         }
      } else {
         int newIndexxx = k & 3855 | posZ << 4;
         if (!this.isOccluding(section.getBlockId(newIndexxx))) {
            return blockId;
         }
      }

      if (negZ < 0) {
         Chunk neighbourChunkxxx = world.getChunkIfLoaded(chunk.locX, chunk.locZ - 1);
         if (neighbourChunkxxx == null) {
            return blockId;
         }

         ChunkSection neighbourSectionxxx = neighbourChunkxxx.getSection(section.getYPosition());
         if (neighbourSectionxxx == null) {
            return blockId;
         }

         int newIndexxxx = k & 3855 | 240;
         if (!this.isOccluding(neighbourSectionxxx.getBlockId(newIndexxxx))) {
            return blockId;
         }
      } else {
         int newIndexxxx = k & 3855 | negZ << 4;
         if (!this.isOccluding(section.getBlockId(newIndexxxx))) {
            return blockId;
         }
      }

      return this.getReplaceId(world.getWorld().getEnvironment());
   }
}
