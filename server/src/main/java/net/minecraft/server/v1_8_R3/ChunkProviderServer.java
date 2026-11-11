package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R3.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.generator.BlockPopulator;

public class ChunkProviderServer implements IChunkProvider {
   private static final Logger b = LogManager.getLogger();
   public LongSet unloadQueue = new LongOpenHashSet();
   public Chunk emptyChunk;
   public IChunkProvider chunkProvider;
   public IChunkLoader chunkLoader;
   public boolean forceChunkLoad = false;
   protected Chunk lastChunkByPos = null;
   public Long2ObjectOpenHashMap<Chunk> chunks = new Long2ObjectOpenHashMap<Chunk>(8192, 0.5F) {
      public Chunk get(long key) {
         return ChunkProviderServer.this.lastChunkByPos != null && key == ChunkProviderServer.this.lastChunkByPos.chunkHashKey
            ? ChunkProviderServer.this.lastChunkByPos
            : (ChunkProviderServer.this.lastChunkByPos = (Chunk)super.get(key));
      }

      public Chunk remove(long key) {
         if (ChunkProviderServer.this.lastChunkByPos != null && key == ChunkProviderServer.this.lastChunkByPos.chunkHashKey) {
            ChunkProviderServer.this.lastChunkByPos = null;
         }

         return (Chunk)super.remove(key);
      }
   };
   public WorldServer world;

   public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
      this.emptyChunk = new EmptyChunk(worldserver, Integer.MIN_VALUE, Integer.MAX_VALUE);
      this.world = worldserver;
      this.chunkLoader = ichunkloader;
      this.chunkProvider = ichunkprovider;
   }

   @Override
   public boolean isChunkLoaded(int i, int j) {
      return this.chunks.containsKey(LongHash.toLong(i, j));
   }

   public Collection<Chunk> a() {
      return this.chunks.values();
   }

   public void queueUnload(long hash, int i, int j) {
      this.world.chunkTickList.remove(hash);
      if ((Boolean)BlazeConfig.chunkUnloading.getValue()) {
         Chunk chunk = (Chunk)this.chunks.get(hash);
         if (chunk != null) {
            if (System.currentTimeMillis() - chunk.lastActive < 25000L
               || chunk.world.paperSpigotConfig.useAsyncLighting
                  && (chunk.pendingLightUpdates.get() > 0 || chunk.world.getTime() - chunk.lightUpdateTime < 20L)) {
               return;
            }

            List[] var9 = chunk.entitySlices;
            int var8 = chunk.entitySlices.length;

            for (int var7 = 0; var7 < var8; var7++) {
               for (Entity entity : (List<Entity>)var9[var7]) {
                  if (entity.loadChunks) {
                     return;
                  }
               }
            }

            if (this.world.worldProvider.e()) {
               if (!this.world.c(i, j)) {
                  this.unloadQueue.add(hash);
                  chunk.mustSave = true;
               }
            } else {
               this.unloadQueue.add(hash);
               chunk.mustSave = true;
            }
         }
      }
   }

   public void queueUnload(int i, int j) {
      this.queueUnload(LongHash.toLong(i, j), i, j);
   }

   public void b() {
      for (Chunk chunk : this.chunks.values()) {
         this.queueUnload(chunk.locX, chunk.locZ);
      }
   }

   public Chunk getChunkIfLoaded(int x, int z) {
      return (Chunk)this.chunks.get(LongHash.toLong(x, z));
   }

   public Chunk getChunkIfLoaded(long hash) {
      return (Chunk)this.chunks.get(hash);
   }

   public Chunk getChunkAt(int i, int j) {
      return this.getChunkAt(LongHash.toLong(i, j), i, j, null);
   }

   public Chunk getChunkAt(int i, int j, Runnable runnable) {
      return this.getChunkAt(LongHash.toLong(i, j), i, j, runnable);
   }

   public Chunk getChunkAt(long hash, int i, int j) {
      return this.getChunkAt(hash, i, j, null);
   }

   public Chunk getChunkAt(long hash, int i, int j, Runnable runnable) {
      this.unloadQueue.remove(hash);
      Chunk chunk = (Chunk)this.chunks.get(hash);
      ChunkRegionLoader loader = null;
      if (this.chunkLoader instanceof ChunkRegionLoader) {
         loader = (ChunkRegionLoader)this.chunkLoader;
      }

      if (chunk == null && loader != null && loader.chunkExists(i, j)) {
         if (runnable != null) {
            ChunkIOExecutor.queueChunkLoad(this.world, loader, this, i, j, runnable);
            return null;
         }

         chunk = ChunkIOExecutor.syncChunkLoad(this.world, loader, this, i, j);
      } else if (chunk == null) {
         chunk = this.originalGetChunkAt(i, j);
      }

      if (runnable != null) {
         runnable.run();
      }

      return chunk;
   }

   public Chunk getChunkAtWithoutCreation(int i, int j) {
      long hash = LongHash.toLong(i, j);
      this.unloadQueue.remove(hash);
      Chunk chunk = (Chunk)this.chunks.get(hash);
      ChunkRegionLoader loader = null;
      if (this.chunkLoader instanceof ChunkRegionLoader) {
         loader = (ChunkRegionLoader)this.chunkLoader;
      }

      if (chunk == null && loader != null && loader.chunkExists(i, j)) {
         chunk = ChunkIOExecutor.syncChunkLoad(this.world, loader, this, i, j);
      }

      return chunk;
   }

   public Chunk originalGetChunkAt(int i, int j) {
      long key = LongHash.toLong(i, j);
      this.unloadQueue.remove(key);
      Chunk chunk = (Chunk)this.chunks.get(key);
      boolean newChunk = false;
      Server server = this.world.getServer();
      if (chunk == null) {
         chunk = this.loadChunk(i, j);
         if (chunk == null) {
            if (this.chunkProvider == null) {
               chunk = this.emptyChunk;
            } else {
               try {
                  chunk = this.chunkProvider.getOrCreateChunk(i, j);
               } catch (Throwable var11) {
                  CrashReport crashreport = CrashReport.a(var11, "Exception generating new chunk");
                  CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");
                  crashreportsystemdetails.a("Location", String.format("%d,%d", i, j));
                  crashreportsystemdetails.a("Position hash", key);
                  crashreportsystemdetails.a("Generator", this.chunkProvider.getName());
                  throw new ReportedException(crashreport);
               }
            }

            newChunk = true;
         }

         this.chunks.put(key, chunk);
         chunk.addEntities();
         if (server != null) {
            server.getPluginManager().callEvent(new ChunkLoadEvent(chunk.bukkitChunk, newChunk));
         }

         for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
               if (x != 0 || z != 0) {
                  Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                  if (neighbor != null) {
                     neighbor.setNeighborLoaded(-x, -z);
                     chunk.setNeighborLoaded(x, z);
                  }
               }
            }
         }

         chunk.loadNearby(this, this, i, j);
      }

      return chunk;
   }

   @Override
   public Chunk getOrCreateChunk(long hash, int i, int j) {
      Chunk chunk = (Chunk)this.chunks.get(hash);
      chunk = chunk == null ? (!this.world.ad() && !this.forceChunkLoad ? this.emptyChunk : this.getChunkAt(hash, i, j)) : chunk;
      if (chunk == this.emptyChunk) {
         return chunk;
      } else {
         if (i != chunk.locX || j != chunk.locZ) {
            String msg = "Chunk (" + chunk.locX + ", " + chunk.locZ + ") stored at  (" + i + ", " + j + ") in world '" + this.world.getWorld().getName() + "'";
            b.error(msg);
            b.error(chunk.getClass().getName());
            Throwable ex = new Throwable();
            ex.fillInStackTrace();
            ex.printStackTrace();
         }

         return chunk;
      }
   }

   @Override
   public Chunk getOrCreateChunk(int i, int j) {
      return this.getOrCreateChunk(LongHash.toLong(i, j), i, j);
   }

   public Chunk loadChunk(int i, int j) {
      if (this.chunkLoader == null) {
         return null;
      } else {
         try {
            Chunk chunk = this.chunkLoader.a(this.world, i, j);
            if (chunk != null) {
               chunk.setLastSaved(this.world.getTime());
               if (this.chunkProvider != null) {
                  this.chunkProvider.recreateStructures(chunk, i, j);
               }
            }

            return chunk;
         } catch (Exception var5) {
            String msg = "Couldn't load chunk";
            b.error(msg, var5);
            return null;
         }
      }
   }

   public void saveChunkNOP(Chunk chunk) {
      if (this.chunkLoader != null) {
         try {
            this.chunkLoader.b(this.world, chunk);
         } catch (Exception var3) {
            b.error("Couldn't save entities", var3);
         }
      }
   }

   public void saveChunk(Chunk chunk) {
      if (this.chunkLoader != null) {
         try {
            chunk.setLastSaved(this.world.getTime());
            this.chunkLoader.a(this.world, chunk);
         } catch (IOException var3) {
            b.error("Couldn't save chunk", var3);
         } catch (ExceptionWorldConflict var4) {
            b.error("Couldn't save chunk; already in use by another instance of Minecraft?", var4);
         }
      }
   }

   @Override
   public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
      Chunk chunk = this.getOrCreateChunk(i, j);
      if (!chunk.isDone()) {
         chunk.n();
         if (this.chunkProvider != null) {
            this.chunkProvider.getChunkAt(ichunkprovider, i, j);
            BlockSand.instaFall = true;
            Random random = new Random();
            random.setSeed(this.world.getSeed());
            long xRand = random.nextLong() / 2L * 2L + 1L;
            long zRand = random.nextLong() / 2L * 2L + 1L;
            random.setSeed(i * xRand + j * zRand ^ this.world.getSeed());
            org.bukkit.World world = this.world.getWorld();
            if (world != null) {
               this.world.populating = true;

               try {
                  for (BlockPopulator populator : world.getPopulators()) {
                     populator.populate(world, random, chunk.bukkitChunk);
                  }
               } finally {
                  this.world.populating = false;
               }
            }

            BlockSand.instaFall = false;
            this.world.getServer().getPluginManager().callEvent(new ChunkPopulateEvent(chunk.bukkitChunk));
            chunk.e();
         }
      }
   }

   @Override
   public boolean a(IChunkProvider ichunkprovider, Chunk chunk, int i, int j) {
      if (this.chunkProvider != null && this.chunkProvider.a(ichunkprovider, chunk, i, j)) {
         this.getOrCreateChunk(i, j).e();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
      int i = 0;

      for (Chunk chunk : this.chunks.values()) {
         if (flag) {
            this.saveChunkNOP(chunk);
         }

         if (chunk.a(flag)) {
            this.saveChunk(chunk);
            chunk.f(false);
            if (!flag && ++i >= 6) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public void c() {
      if (this.chunkLoader != null) {
         this.chunkLoader.b();
      }
   }

   @Override
   public boolean unloadChunks() {
      if (!this.world.savingDisabled) {
         Server server = this.world.getServer();
         int perTick = (Integer)BlazeConfig.unloadPerTick.getValue();
         LongIterator iterator = this.unloadQueue.iterator();

         for (int i = 0; i < perTick && iterator.hasNext(); i++) {
            long chunkcoordinates = iterator.nextLong();
            iterator.remove();
            Chunk chunk = (Chunk)this.chunks.get(chunkcoordinates);
            if (chunk != null && System.currentTimeMillis() - chunk.lastActive >= 25000L) {
               ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk);
               server.getPluginManager().callEvent(event);
               if (!event.isCancelled()) {
                  chunk.removeEntities();
                  this.saveChunk(chunk);
                  this.saveChunkNOP(chunk);
                  this.chunks.remove(chunkcoordinates);
                  this.world.chunkTickList.remove(chunk);

                  for (int x = -2; x < 3; x++) {
                     for (int z = -2; z < 3; z++) {
                        if (x != 0 || z != 0) {
                           Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                           if (neighbor != null) {
                              neighbor.setNeighborUnloaded(-x, -z);
                              chunk.setNeighborUnloaded(x, z);
                           }
                        }
                     }
                  }
               }
            }
         }

         if (this.chunkLoader != null) {
            this.chunkLoader.a();
         }
      }

      return this.chunkProvider.unloadChunks();
   }

   @Override
   public boolean canSave() {
      return !this.world.savingDisabled;
   }

   @Override
   public String getName() {
      return "ServerChunkCache: " + this.chunks.size() + " Drop: " + this.unloadQueue.size();
   }

   @Override
   public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
      return this.chunkProvider.getMobsFor(enumcreaturetype, blockposition);
   }

   @Override
   public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition) {
      return this.chunkProvider.findNearestMapFeature(world, s, blockposition);
   }

   @Override
   public int getLoadedChunks() {
      return this.chunks.size();
   }

   @Override
   public void recreateStructures(Chunk chunk, int i, int j) {
   }

   @Override
   public Chunk getChunkAt(BlockPosition blockposition) {
      return this.getOrCreateChunk(blockposition.getX() >> 4, blockposition.getZ() >> 4);
   }
}
