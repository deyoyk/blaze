package org.bukkit.craftbukkit.v1_8_R3;

import lol.deyo.blaze.api.chunk.ChunkSectionSnapshot;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.EmptyChunk;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.IInventory;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.WorldChunkManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;

public class CraftChunk implements org.bukkit.Chunk {
   private static final byte[] emptyData = new byte[2048];
   private static final short[] emptyBlockIDs = new short[4096];
   private static final byte[] emptySkyLight = new byte[2048];
   private final WorldServer worldServer;
   private final int x;
   private final int z;
   private WeakReference<net.minecraft.server.v1_8_R3.Chunk> weakChunk;

   static {
      Arrays.fill(emptySkyLight, (byte)-1);
   }

   public CraftChunk(net.minecraft.server.v1_8_R3.Chunk chunk) {
      if (!(chunk instanceof EmptyChunk)) {
         this.weakChunk = new WeakReference<>(chunk);
      }

      this.worldServer = (WorldServer)this.getHandle().world;
      this.x = this.getHandle().locX;
      this.z = this.getHandle().locZ;
   }

   @Override
   public int getEntityCount() {
      int count = 0;
      net.minecraft.server.v1_8_R3.Chunk chunk = this.getHandle();

      for (int i = 0; i < 16; i++) {
         count += chunk.entitySlices[i].size();
      }

      return count;
   }

   public static org.bukkit.ChunkSnapshot getEmptyChunkSnapshot(int x, int z, CraftWorld world, boolean includeBiome, boolean includeBiomeTempRain) {
      BiomeBase[] biome = null;
      double[] biomeTemp = null;
      double[] biomeRain = null;
      if (includeBiome || includeBiomeTempRain) {
         WorldChunkManager wcm = world.getHandle().getWorldChunkManager();
         if (includeBiome) {
            biome = new BiomeBase[256];

            for (int i = 0; i < 256; i++) {
               biome[i] = world.getHandle().getBiome((x << 4) + (i & 15), 0, (z << 4) + (i >> 4));
            }
         }

         if (includeBiomeTempRain) {
            biomeTemp = new double[256];
            biomeRain = new double[256];
            float[] dat = getTemperatures(wcm, x << 4, z << 4);

            for (int i = 0; i < 256; i++) {
               biomeTemp[i] = dat[i];
            }

            dat = wcm.getWetness(null, x << 4, z << 4, 16, 16);

            for (int i = 0; i < 256; i++) {
               biomeRain[i] = dat[i];
            }
         }
      }

      int hSection = world.getMaxHeight() >> 4;
      short[][] blockIDs = new short[hSection][];
      byte[][] skyLight = new byte[hSection][];
      byte[][] emitLight = new byte[hSection][];
      byte[][] blockData = new byte[hSection][];
      boolean[] empty = new boolean[hSection];

      for (int i = 0; i < hSection; i++) {
         blockIDs[i] = emptyBlockIDs;
         skyLight[i] = emptySkyLight;
         emitLight[i] = emptyData;
         blockData[i] = emptyData;
         empty[i] = true;
      }

      return new CraftChunkSnapshot(
         x, z, world.getName(), world.getFullTime(), blockIDs, blockData, skyLight, emitLight, empty, new int[256], biome, biomeTemp, biomeRain
      );
   }

   private static float[] getTemperatures(WorldChunkManager chunkmanager, int chunkX, int chunkZ) {
      BiomeBase[] biomes = chunkmanager.getBiomes(null, chunkX, chunkZ, 16, 16);
      float[] temps = new float[biomes.length];

      for (int i = 0; i < biomes.length; i++) {
         float temp = biomes[i].temperature;
         if (temp > 1.0F) {
            temp = 1.0F;
         }

         temps[i] = temp;
      }

      return temps;
   }

   @Override
   public org.bukkit.World getWorld() {
      return this.worldServer.getWorld();
   }

   public CraftWorld getCraftWorld() {
      return (CraftWorld)this.getWorld();
   }

   public net.minecraft.server.v1_8_R3.Chunk getHandle() {
      net.minecraft.server.v1_8_R3.Chunk c = this.weakChunk.get();
      if (c == null) {
         c = this.worldServer.getChunkAt(this.x, this.z);
         if (!(c instanceof EmptyChunk)) {
            this.weakChunk = new WeakReference<>(c);
         }
      }

      return c;
   }

   void breakLink() {
      this.weakChunk.clear();
   }

   @Override
   public int getX() {
      return this.x;
   }

   @Override
   public int getZ() {
      return this.z;
   }

   @Override
   public String toString() {
      return "CraftChunk{x=" + this.getX() + "z=" + this.getZ() + '}';
   }

   @Override
   public Block getBlock(int x, int y, int z) {
      return new CraftBlock(this, this.getX() << 4 | x & 15, y, this.getZ() << 4 | z & 15);
   }

   @Override
   public Entity[] getEntities() {
      int count = 0;
      int index = 0;
      net.minecraft.server.v1_8_R3.Chunk chunk = this.getHandle();

      for (int i = 0; i < 16; i++) {
         count += chunk.entitySlices[i].size();
      }

      Entity[] entities = new Entity[count];

      for (int i = 0; i < 16; i++) {
         for (net.minecraft.server.v1_8_R3.Entity entity : chunk.entitySlices[i]) {
            if (entity != null) {
               entities[index++] = entity.getBukkitEntity();
            }
         }
      }

      return entities;
   }

   @Override
   public BlockState[] getTileEntities() {
      int index = 0;
      net.minecraft.server.v1_8_R3.Chunk chunk = this.getHandle();
      BlockState[] entities = new BlockState[chunk.tileEntities.size()];

      Object[] var7;
      for (Object obj : var7 = chunk.tileEntities.keySet().toArray()) {
         if (obj instanceof BlockPosition) {
            BlockPosition position = (BlockPosition)obj;
            entities[index++] = this.worldServer.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()).getState();
         }
      }

      return entities;
   }

   @Override
   public boolean isLoaded() {
      return this.getWorld().isChunkLoaded(this);
   }

   @Override
   public boolean load() {
      return this.getWorld().loadChunk(this.getX(), this.getZ(), true);
   }

   @Override
   public boolean load(boolean generate) {
      return this.getWorld().loadChunk(this.getX(), this.getZ(), generate);
   }

   @Override
   public boolean unload() {
      return this.getWorld().unloadChunk(this.getX(), this.getZ());
   }

   @Override
   public boolean unload(boolean save) {
      return this.getWorld().unloadChunk(this.getX(), this.getZ(), save);
   }

   @Override
   public boolean unload(boolean save, boolean safe) {
      return this.getWorld().unloadChunk(this.getX(), this.getZ(), save, safe);
   }

   @Override
   public org.bukkit.ChunkSnapshot getChunkSnapshot() {
      return this.getChunkSnapshot(true, false, false);
   }

   @Override
   public org.bukkit.ChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome, boolean includeBiomeTempRain) {
      net.minecraft.server.v1_8_R3.Chunk chunk = this.getHandle();
      ChunkSection[] cs = chunk.getSections();
      short[][] sectionBlockIDs = new short[cs.length][];
      byte[][] sectionBlockData = new byte[cs.length][];
      byte[][] sectionSkyLights = new byte[cs.length][];
      byte[][] sectionEmitLights = new byte[cs.length][];
      boolean[] sectionEmpty = new boolean[cs.length];

      for (int i = 0; i < cs.length; i++) {
         if (cs[i] == null) {
            sectionBlockIDs[i] = emptyBlockIDs;
            sectionBlockData[i] = emptyData;
            sectionSkyLights[i] = emptySkyLight;
            sectionEmitLights[i] = emptyData;
            sectionEmpty[i] = true;
         } else {
            short[] blockids = new short[4096];
            byte[] dataValues = sectionBlockData[i] = new byte[2048];

            for (int j = 0; j < 4096; j++) {
               char baseId = cs[i].getBlockId(j);
               if (baseId != 0) {
                  IBlockData blockData = (IBlockData)net.minecraft.server.v1_8_R3.Block.d.a(baseId);
                  if (blockData != null) {
                     blockids[j] = (short)net.minecraft.server.v1_8_R3.Block.getId(blockData.getBlock());
                     int data = blockData.getBlock().toLegacyData(blockData);
                     int jj = j >> 1;
                     if ((j & 1) == 0) {
                        dataValues[jj] = (byte)(dataValues[jj] & 240 | data & 15);
                     } else {
                        dataValues[jj] = (byte)(dataValues[jj] & 15 | (data & 15) << 4);
                     }
                  }
               }
            }

            sectionBlockIDs[i] = blockids;
            if (cs[i].getSkyLightArray() == null) {
               sectionSkyLights[i] = emptyData;
            } else {
               sectionSkyLights[i] = new byte[2048];
               System.arraycopy(cs[i].getSkyLightArray().a(), 0, sectionSkyLights[i], 0, 2048);
            }

            sectionEmitLights[i] = new byte[2048];
            System.arraycopy(cs[i].getEmittedLightArray().a(), 0, sectionEmitLights[i], 0, 2048);
         }
      }

      int[] hmap = null;
      if (includeMaxBlockY) {
         hmap = new int[256];
         System.arraycopy(chunk.heightMap, 0, hmap, 0, 256);
      }

      BiomeBase[] biome = null;
      double[] biomeTemp = null;
      double[] biomeRain = null;
      if (includeBiome || includeBiomeTempRain) {
         WorldChunkManager wcm = chunk.world.getWorldChunkManager();
         if (includeBiome) {
            biome = new BiomeBase[256];

            for (int ix = 0; ix < 256; ix++) {
               biome[ix] = chunk.getBiome(ix & 15, ix >> 4, wcm);
            }
         }

         if (includeBiomeTempRain) {
            biomeTemp = new double[256];
            biomeRain = new double[256];
            float[] dat = getTemperatures(wcm, this.getX() << 4, this.getZ() << 4);

            for (int ix = 0; ix < 256; ix++) {
               biomeTemp[ix] = dat[ix];
            }

            dat = wcm.getWetness(null, this.getX() << 4, this.getZ() << 4, 16, 16);

            for (int ix = 0; ix < 256; ix++) {
               biomeRain[ix] = dat[ix];
            }
         }
      }

      org.bukkit.World world = this.getWorld();
      return new CraftChunkSnapshot(
         this.getX(),
         this.getZ(),
         world.getName(),
         world.getFullTime(),
         sectionBlockIDs,
         sectionBlockData,
         sectionSkyLights,
         sectionEmitLights,
         sectionEmpty,
         hmap,
         biome,
         biomeTemp,
         biomeRain
      );
   }

   @Override
   public lol.deyo.blaze.api.chunk.ChunkSnapshot takeSnapshot() {
      net.minecraft.server.v1_8_R3.Chunk handle = this.getHandle();
      lol.deyo.blaze.api.chunk.CraftChunkSnapshot snap = new lol.deyo.blaze.api.chunk.CraftChunkSnapshot();

      for (int i = 0; i < 16; i++) {
         ChunkSection section = handle.getSections()[i];
         if (section != null) {
            snap.getSections()[i] = section.createSnapshot();
         }
      }

      for (Entry<BlockPosition, TileEntity> entry : handle.tileEntities.entrySet()) {
         NBTTagCompound nbt = new NBTTagCompound();
         entry.getValue().b(nbt);
         snap.getTileEntities().add(nbt);
      }

      return snap;
   }

   @Override
   public void restoreSnapshot(lol.deyo.blaze.api.chunk.ChunkSnapshot snapshot) {
      lol.deyo.blaze.api.chunk.CraftChunkSnapshot snap = (lol.deyo.blaze.api.chunk.CraftChunkSnapshot)snapshot;
      net.minecraft.server.v1_8_R3.Chunk handle = this.getHandle();

      for (int i = 0; i < 16; i++) {
         ChunkSectionSnapshot section = snap.getSections()[i];
         if (section == null) {
            handle.getSections()[i] = null;
         } else {
            handle.getSections()[i] = new ChunkSection(i << 4, !this.worldServer.worldProvider.o());
            handle.getSections()[i].restoreSnapshot(section);
         }
      }

      for (TileEntity tileEntity : handle.tileEntities.values()) {
         if (tileEntity instanceof IInventory) {
            for (HumanEntity h : new ArrayList<HumanEntity>(((IInventory)tileEntity).getViewers())) {
               if (h instanceof CraftHumanEntity) {
                  ((CraftHumanEntity)h).getHandle().closeInventory();
               }
            }
         }

         this.worldServer.a(tileEntity);
      }

      handle.tileEntities.clear();

      for (NBTTagCompound nbt : snap.getTileEntities()) {
         TileEntity tileEntity = TileEntity.c(nbt);
         tileEntity.position.setX(tileEntity.position.getX() & 15 | handle.locX << 4);
         tileEntity.position.setZ(tileEntity.position.getZ() & 15 | handle.locZ << 4);
         handle.a(tileEntity);
      }

      handle.q = true;
      this.worldServer.getPlayerChunkMap().resend(this.x, this.z);
   }
}
