package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.optimizations.FastAntiXray;
import java.io.IOException;

public class PacketPlayOutMapChunk implements Packet<PacketListenerPlayOut> {
   private int a;
   private int b;
   private PacketPlayOutMapChunk.ChunkMap c;
   private boolean d;

   public PacketPlayOutMapChunk() {
   }

   public PacketPlayOutMapChunk(Chunk chunk, boolean groundUp, int bitmask) {
      this.a = chunk.locX;
      this.b = chunk.locZ;
      this.d = groundUp;
      this.c = chunk.getChunkMap(groundUp, bitmask);
   }

   public PacketPlayOutMapChunk(Chunk chunk) {
      this.a = chunk.locX;
      this.b = chunk.locZ;
      this.d = true;
      PacketPlayOutMapChunk.ChunkMap chunkMap = new PacketPlayOutMapChunk.ChunkMap();
      chunkMap.a = new byte[0];
      this.c = chunkMap;
   }

   @Override
   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      this.a = packetdataserializer.readInt();
      this.b = packetdataserializer.readInt();
      this.d = packetdataserializer.readBoolean();
      this.c = new PacketPlayOutMapChunk.ChunkMap();
      this.c.b = packetdataserializer.readShort();
      this.c.a = packetdataserializer.a();
   }

   @Override
   public void b(PacketDataSerializer packetdataserializer) throws IOException {
      packetdataserializer.writeInt(this.a);
      packetdataserializer.writeInt(this.b);
      packetdataserializer.writeBoolean(this.d);
      packetdataserializer.writeShort((short)(this.c.b & 65535));
      packetdataserializer.a(this.c.a);
   }

   public void a(PacketListenerPlayOut packetlistenerplayout) {
      packetlistenerplayout.a(this);
   }

   protected static int getChunkDataSize(int sections, boolean sky, boolean groundUp) {
      return sections * 2 * 16 * 16 * 16 + sections * 16 * 16 * 16 / 2 + (sky ? sections * 16 * 16 * 16 / 2 : 0) + (groundUp ? 256 : 0);
   }

   public static PacketPlayOutMapChunk.ChunkMap serializeToChunkMap(Chunk chunk, boolean groundUp, boolean hasSky, int primaryBitmask) {
      if (chunk.world.spigotConfig.antiXray) {
         return serializeToChunkMapWithAntiXray(chunk, groundUp, hasSky, primaryBitmask);
      } else {
         ChunkSection[] achunksection = chunk.getSections();
         PacketPlayOutMapChunk.ChunkMap map = new PacketPlayOutMapChunk.ChunkMap();
         ChunkSection[] sendingSections = new ChunkSection[Integer.bitCount(primaryBitmask)];
         int totalSections = 0;

         for (int i = 0; i < achunksection.length; i++) {
            ChunkSection chunksection = achunksection[i];
            if (chunksection != null && (!groundUp || !chunksection.a()) && (primaryBitmask & 1 << i) != 0) {
               map.b |= 1 << i;
               sendingSections[totalSections++] = chunksection;
            }
         }

         int blockWriteIndex = 0;
         int emittedLightIndex = 8192 * totalSections;
         int skylightIndex = emittedLightIndex + 2048 * totalSections;
         int biomeIndex = hasSky ? skylightIndex + 2048 * totalSections : skylightIndex;
         map.a = new byte[biomeIndex + 256];

         for (int var18 = 0; var18 < totalSections; var18++) {
            ChunkSection section = sendingSections[var18];

            for (int index = 0; index < 4096; index++) {
               char blockId = section.getBlockId(index);
               map.a[blockWriteIndex++] = (byte)(blockId & 255);
               map.a[blockWriteIndex++] = (byte)(blockId >> '\b' & 0xFF);
            }

            byte[] emittedLight = section.getEmittedLightArray().a();
            System.arraycopy(emittedLight, 0, map.a, emittedLightIndex, 2048);
            emittedLightIndex += 2048;
            if (hasSky) {
               byte[] skylight = section.getSkyLightArray().a();
               System.arraycopy(skylight, 0, map.a, skylightIndex, 2048);
               skylightIndex += 2048;
            }
         }

         if (groundUp) {
            byte[] biome = chunk.getBiomeIndex();
            System.arraycopy(biome, 0, map.a, biomeIndex, 256);
         }

         return map;
      }
   }

   public static PacketPlayOutMapChunk.ChunkMap serializeToChunkMapWithAntiXray(Chunk chunk, boolean groundUp, boolean hasSky, int primaryBitmask) {
      ChunkSection[] achunksection = chunk.getSections();
      PacketPlayOutMapChunk.ChunkMap map = new PacketPlayOutMapChunk.ChunkMap();
      ChunkSection[] sendingSections = new ChunkSection[Integer.bitCount(primaryBitmask)];
      int totalSections = 0;

      for (int i = 0; i < achunksection.length; i++) {
         ChunkSection chunksection = achunksection[i];
         if (chunksection != null && (!groundUp || !chunksection.a()) && (primaryBitmask & 1 << i) != 0) {
            map.b |= 1 << i;
            sendingSections[totalSections++] = chunksection;
         }
      }

      World world = chunk.world;
      FastAntiXray antiXray = world.spigotConfig.antiXrayInstance;
      int randomOre = 0;
      int blockWriteIndex = 0;
      int emittedLightIndex = 8192 * totalSections;
      int skylightIndex = emittedLightIndex + 2048 * totalSections;
      int biomeIndex = hasSky ? skylightIndex + 2048 * totalSections : skylightIndex;
      map.a = new byte[biomeIndex + 256];

      for (int var20 = 0; var20 < totalSections; var20++) {
         ChunkSection section = sendingSections[var20];

         for (int k = 0; k < 4096; k++) {
            char blockId = section.getBlockId(k);
            if (antiXray.obfuscateBlocks[blockId]) {
               switch (world.spigotConfig.engineMode) {
                  case 1:
                     blockId = antiXray.getReplacedIdEngine1(chunk, section, sendingSections, blockId, k, primaryBitmask, var20, totalSections);
                     break;
                  case 2:
                     if (randomOre >= antiXray.replacementOres.length) {
                        randomOre = 0;
                     }

                     blockId = (char)(antiXray.replacementOres[randomOre] & 255);
                     randomOre++;
               }
            }

            map.a[blockWriteIndex++] = (byte)(blockId & 255);
            map.a[blockWriteIndex++] = (byte)(blockId >> '\b' & 0xFF);
         }

         byte[] emittedLight = section.getEmittedLightArray().a();
         System.arraycopy(emittedLight, 0, map.a, emittedLightIndex, 2048);
         emittedLightIndex += 2048;
         if (hasSky) {
            byte[] skylight = section.getSkyLightArray().a();
            System.arraycopy(skylight, 0, map.a, skylightIndex, 2048);
            skylightIndex += 2048;
         }
      }

      if (groundUp) {
         byte[] biome = chunk.getBiomeIndex();
         System.arraycopy(biome, 0, map.a, biomeIndex, 256);
      }

      return map;
   }

   public static class ChunkMap {
      public byte[] a;
      public int b;
   }
}
