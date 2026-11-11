package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import lol.deyo.blaze.optimizations.ObjectMapList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.v1_8_R3.chunkio.ChunkIOExecutor;

public class PlayerChunkMap {
   private static final Logger a = LogManager.getLogger();
   private final WorldServer world;
   private final List<EntityPlayer> managedPlayers = new ObjectArrayList();
   private final LongHashMap<PlayerChunkMap.PlayerChunk> d = new LongHashMap<>();
   private final List<PlayerChunkMap.PlayerChunk> e = new ArrayList<>();
   private final int[][] i = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
   private int g;

   public PlayerChunkMap(WorldServer worldserver, int viewDistance) {
      this.world = worldserver;
      this.a(viewDistance);
   }

   public static int getFurthestViewableBlock(int i) {
      return i * 16 - 16;
   }

   public WorldServer a() {
      return this.world;
   }

   public void flush() {
      for (PlayerChunkMap.PlayerChunk o : this.e) {
         o.b();
      }

      this.e.clear();
   }

   public boolean a(int i, int j) {
      return this.d.getEntry(i + 2147483647L | j + 2147483647L << 32) != null;
   }

   private PlayerChunkMap.PlayerChunk a(int i, int j, boolean flag) {
      long k = i + 2147483647L | j + 2147483647L << 32;
      PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.d.getEntry(k);
      if (playerchunkmap_playerchunk == null && flag) {
         playerchunkmap_playerchunk = new PlayerChunkMap.PlayerChunk(i, j);
         this.d.put(k, playerchunkmap_playerchunk);
      }

      return playerchunkmap_playerchunk;
   }

   public final boolean isChunkInUse(int x, int z) {
      PlayerChunkMap.PlayerChunk pi = this.a(x, z, false);
      return pi != null ? !pi.b.isEmpty() : false;
   }

   public void flagDirty(BlockPosition blockposition) {
      this.flagDirty(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public void flagDirty(int x, int y, int z) {
      int i = x >> 4;
      int j = z >> 4;
      PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(i, j, false);
      if (playerchunkmap_playerchunk != null) {
         playerchunkmap_playerchunk.a(x & 15, y, z & 15);
      }
   }

   public void addPlayer(EntityPlayer entityplayer) {
      int i = MathHelper.floor(entityplayer.locX) >> 4;
      int j = MathHelper.floor(entityplayer.locZ) >> 4;
      entityplayer.d = entityplayer.locX;
      entityplayer.e = entityplayer.locZ;
      List<ChunkCoordIntPair> chunkList = new ArrayList<>();
      int view = entityplayer.viewDistance;

      for (int k = i - view; k <= i + view; k++) {
         for (int l = j - view; l <= j + view; l++) {
            chunkList.add(new ChunkCoordIntPair(k, l));
         }
      }

      chunkList.sort(new PlayerChunkMap.ChunkCoordComparator(entityplayer));

      for (ChunkCoordIntPair pair : chunkList) {
         this.a(pair.x, pair.z, true).a(entityplayer);
      }

      this.managedPlayers.add(entityplayer);
      this.b(entityplayer);
   }

   public void b(EntityPlayer entityplayer) {
      ArrayList<ChunkCoordIntPair> arraylist = Lists.newArrayList(entityplayer.chunkCoordIntPairQueue);
      int i = 0;
      int j = entityplayer.viewDistance;
      int k = MathHelper.floor(entityplayer.locX) >> 4;
      int l = MathHelper.floor(entityplayer.locZ) >> 4;
      int i1 = 0;
      int j1 = 0;
      ChunkCoordIntPair chunkcoordintpair = this.a(k, l, true).location;
      entityplayer.chunkCoordIntPairQueue.clear();
      if (arraylist.contains(chunkcoordintpair)) {
         entityplayer.chunkCoordIntPairQueue.add(chunkcoordintpair);
      }

      for (int k1 = 1; k1 <= j * 2; k1++) {
         for (int l1 = 0; l1 < 2; l1++) {
            int[] aint = this.i[i++ % 4];

            for (int i2 = 0; i2 < k1; i2++) {
               i1 += aint[0];
               j1 += aint[1];
               chunkcoordintpair = this.a(k + i1, l + j1, true).location;
               if (arraylist.contains(chunkcoordintpair)) {
                  entityplayer.chunkCoordIntPairQueue.add(chunkcoordintpair);
               }
            }
         }
      }

      i %= 4;

      for (int var17 = 0; var17 < j * 2; var17++) {
         i1 += this.i[i][0];
         j1 += this.i[i][1];
         chunkcoordintpair = this.a(k + i1, l + j1, true).location;
         if (arraylist.contains(chunkcoordintpair)) {
            entityplayer.chunkCoordIntPairQueue.add(chunkcoordintpair);
         }
      }
   }

   public void removePlayer(EntityPlayer entityplayer) {
      int i = MathHelper.floor(entityplayer.d) >> 4;
      int j = MathHelper.floor(entityplayer.e) >> 4;
      int view = entityplayer.viewDistance;

      for (int k = i - view; k <= i + view; k++) {
         for (int l = j - view; l <= j + view; l++) {
            PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(k, l, false);
            if (playerchunkmap_playerchunk != null) {
               playerchunkmap_playerchunk.b(entityplayer);
            }
         }
      }

      this.managedPlayers.remove(entityplayer);
   }

   private boolean a(int i, int j, int k, int l, int i1) {
      int j1 = i - k;
      int k1 = j - l;
      return j1 >= -i1 && j1 <= i1 && k1 >= -i1 && k1 <= i1;
   }

   public void movePlayer(EntityPlayer entityplayer) {
      int i = MathHelper.floor(entityplayer.locX) >> 4;
      int j = MathHelper.floor(entityplayer.locZ) >> 4;
      double d0 = entityplayer.d - entityplayer.locX;
      double d1 = entityplayer.e - entityplayer.locZ;
      if (d0 * d0 + d1 * d1 >= 64.0) {
         int k = MathHelper.floor(entityplayer.d) >> 4;
         int l = MathHelper.floor(entityplayer.e) >> 4;
         int i1 = entityplayer.viewDistance;
         int j1 = i - k;
         int k1 = j - l;
         List<ChunkCoordIntPair> chunksToLoad = new ArrayList<>();
         if (j1 != 0 || k1 != 0) {
            for (int l1 = i - i1; l1 <= i + i1; l1++) {
               for (int i2 = j - i1; i2 <= j + i1; i2++) {
                  if (!this.a(l1, i2, k, l, i1)) {
                     chunksToLoad.add(new ChunkCoordIntPair(l1, i2));
                  }

                  if (!this.a(l1 - j1, i2 - k1, i, j, i1)) {
                     PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(l1 - j1, i2 - k1, false);
                     if (playerchunkmap_playerchunk != null) {
                        playerchunkmap_playerchunk.b(entityplayer);
                     }
                  }
               }
            }

            this.b(entityplayer);
            entityplayer.d = entityplayer.locX;
            entityplayer.e = entityplayer.locZ;
            chunksToLoad.sort(new PlayerChunkMap.ChunkCoordComparator(entityplayer));

            for (ChunkCoordIntPair pair : chunksToLoad) {
               this.a(pair.x, pair.z, true).a(entityplayer);
            }

            if (j1 > 1 || j1 < -1 || k1 > 1 || k1 < -1) {
               entityplayer.chunkCoordIntPairQueue.sort(new PlayerChunkMap.ChunkCoordComparator(entityplayer));
            }
         }
      }
   }

   public boolean a(EntityPlayer entityplayer, int i, int j) {
      PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(i, j, false);
      return playerchunkmap_playerchunk != null
         && playerchunkmap_playerchunk.b.contains(entityplayer)
         && !entityplayer.chunkCoordIntPairQueue.contains(playerchunkmap_playerchunk.location);
   }

   public void a(int i) {
      i = MathHelper.clamp(i, 3, 32);
      if (i != this.g) {
         int j = i - this.g;

         for (EntityPlayer entityplayer : Lists.newArrayList(this.managedPlayers)) {
            int k = (int)entityplayer.locX >> 4;
            int l = (int)entityplayer.locZ >> 4;
            if (j > 0) {
               for (int i1 = k - i; i1 <= k + i; i1++) {
                  for (int j1 = l - i; j1 <= l + i; j1++) {
                     PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(i1, j1, true);
                     if (!playerchunkmap_playerchunk.b.contains(entityplayer)) {
                        playerchunkmap_playerchunk.a(entityplayer);
                     }
                  }
               }
            } else {
               for (int i1 = k - this.g; i1 <= k + this.g; i1++) {
                  for (int j1x = l - this.g; j1x <= l + this.g; j1x++) {
                     if (!this.a(i1, j1x, k, l, i)) {
                        this.a(i1, j1x, true).b(entityplayer);
                     }
                  }
               }
            }
         }

         this.g = i;
      }
   }

   public void updateViewDistance(EntityPlayer player, int viewDistance) {
      viewDistance = MathHelper.clamp(viewDistance, 3, 32);
      if (viewDistance != player.viewDistance) {
         int cx = (int)player.locX >> 4;
         int cz = (int)player.locZ >> 4;
         if (viewDistance - player.viewDistance > 0) {
            for (int x = cx - viewDistance; x <= cx + viewDistance; x++) {
               for (int z = cz - viewDistance; z <= cz + viewDistance; z++) {
                  PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(x, z, true);
                  if (!playerchunkmap_playerchunk.b.contains(player)) {
                     playerchunkmap_playerchunk.a(player);
                  }
               }
            }
         } else {
            for (int x = cx - player.viewDistance; x <= cx + player.viewDistance; x++) {
               for (int zx = cz - player.viewDistance; zx <= cz + player.viewDistance; zx++) {
                  if (!this.a(x, zx, cx, cz, viewDistance)) {
                     this.a(x, zx, true).b(player);
                  }
               }
            }
         }

         player.viewDistance = viewDistance;
      }
   }

   public void resend(int x, int z) {
      PlayerChunkMap.PlayerChunk chunk = this.a(x, z, false);
      if (chunk != null) {
         chunk.resend();
      }
   }

   private static class ChunkCoordComparator implements Comparator<ChunkCoordIntPair> {
      private final int x;
      private final int z;

      public ChunkCoordComparator(EntityPlayer entityplayer) {
         this.x = MathHelper.floor(entityplayer.locX) >> 4;
         this.z = MathHelper.floor(entityplayer.locZ) >> 4;
      }

      public int compare(ChunkCoordIntPair a, ChunkCoordIntPair b) {
         if (a.equals(b)) {
            return 0;
         } else {
            int ax = a.x - this.x;
            int az = a.z - this.z;
            int bx = b.x - this.x;
            int bz = b.z - this.z;
            int result = (ax - bx) * (ax + bx) + (az - bz) * (az + bz);
            if (result != 0) {
               return result;
            } else if (ax < 0) {
               return bx < 0 ? bz - az : -1;
            } else {
               return bx < 0 ? 1 : az - bz;
            }
         }
      }
   }

   class PlayerChunk {
      private final List<EntityPlayer> b = new ObjectMapList();
      private final ChunkCoordIntPair location;
      private final Map<Integer, Runnable> players = new Int2ObjectOpenHashMap();
      private final short[] dirtyBlocks = new short[64];
      private int dirtyCount;
      private int f;
      private long g;
      private boolean loaded = false;
      private final Runnable loadedRunnable = () -> this.loaded = true;

      public void resend() {
         if (this.dirtyCount == 0) {
            PlayerChunkMap.this.a().getPlayerChunkMap().e.add(this);
         }

         this.dirtyCount = 64;
         this.f = 65535;
      }

      public PlayerChunk(int i, int j) {
         this.location = new ChunkCoordIntPair(i, j);
         PlayerChunkMap.this.a().chunkProviderServer.getChunkAt(i, j, this.loadedRunnable);
      }

      public void a(EntityPlayer entityplayer) {
         if (this.b.contains(entityplayer)) {
            PlayerChunkMap.a.debug("Failed to add player. {} already is in chunk {}, {}", new Object[]{entityplayer, this.location.x, this.location.z});
         } else {
            if (this.b.isEmpty()) {
               this.g = PlayerChunkMap.this.world.getTime();
            }

            this.b.add(entityplayer);
            Runnable playerRunnable = null;
            if (this.loaded) {
               entityplayer.chunkCoordIntPairQueue.add(this.location);
            } else {
               playerRunnable = () -> entityplayer.chunkCoordIntPairQueue.add(this.location);
               PlayerChunkMap.this.a().chunkProviderServer.getChunkAt(this.location.x, this.location.z, playerRunnable);
            }

            this.players.put(entityplayer.getId(), playerRunnable);
         }
      }

      public void b(EntityPlayer entityplayer) {
         if (this.b.contains(entityplayer)) {
            if (!this.loaded) {
               ChunkIOExecutor.dropQueuedChunkLoad(PlayerChunkMap.this.a(), this.location.x, this.location.z, this.players.get(entityplayer.getId()));
               this.b.remove(entityplayer);
               this.players.remove(entityplayer.getId());
               if (this.b.isEmpty()) {
                  ChunkIOExecutor.dropQueuedChunkLoad(PlayerChunkMap.this.a(), this.location.x, this.location.z, this.loadedRunnable);
                  PlayerChunkMap.this.d.remove(this.location.x + 2147483647L | this.location.z + 2147483647L << 32);
               }

               return;
            }

            Chunk chunk = PlayerChunkMap.this.world.getChunkAt(this.location.x, this.location.z);
            if (chunk.isReady() && !entityplayer.playerConnection.processedDisconnect) {
               entityplayer.playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk));
            }

            this.players.remove(entityplayer.getId());
            this.b.remove(entityplayer);
            entityplayer.chunkCoordIntPairQueue.remove(this.location);
            if (this.b.isEmpty()) {
               this.a(chunk);
               PlayerChunkMap.this.d.remove(this.location.x + 2147483647L | this.location.z + 2147483647L << 32);
               if (this.dirtyCount > 0) {
                  PlayerChunkMap.this.e.remove(this);
               }

               PlayerChunkMap.this.a().chunkProviderServer.queueUnload(this.location.x, this.location.z);
            }
         }
      }

      public void a() {
         this.a(PlayerChunkMap.this.world.getChunkAt(this.location.x, this.location.z));
      }

      private void a(Chunk chunk) {
         chunk.c(chunk.w() + PlayerChunkMap.this.world.getTime() - this.g);
         this.g = PlayerChunkMap.this.world.getTime();
      }

      public void a(int i, int j, int k) {
         if (this.dirtyCount == 0) {
            PlayerChunkMap.this.e.add(this);
         }

         this.f |= 1 << (j >> 4);
         if (this.dirtyCount < 64) {
            short short0 = (short)(i << 12 | k << 8 | j);

            for (int l = 0; l < this.dirtyCount; l++) {
               if (this.dirtyBlocks[l] == short0) {
                  return;
               }
            }

            this.dirtyBlocks[this.dirtyCount++] = short0;
         }
      }

      public void a(Packet<?> packet) {
         for (EntityPlayer entityplayer : this.b) {
            if (!entityplayer.chunkCoordIntPairQueue.contains(this.location)) {
               entityplayer.playerConnection.sendPacket(packet);
            }
         }
      }

      public void b() {
         if (this.dirtyCount != 0) {
            if (this.dirtyCount == 1) {
               short dirtyBlock = this.dirtyBlocks[0];
               int i = (dirtyBlock >> 12 & 15) + this.location.x * 16;
               int j = dirtyBlock & 255;
               int k = (dirtyBlock >> 8 & 15) + this.location.z * 16;
               BlockPosition pos = new BlockPosition(i, j, k);
               Chunk chunk = PlayerChunkMap.this.world.getChunkAtWorldCoords(i, k);
               IBlockData iblockdata = PlayerChunkMap.this.world.getType(chunk, i, j, k, true);
               Block block = iblockdata.getBlock();
               this.a(new PacketPlayOutBlockChange(pos, iblockdata));
               if (block.isTileEntity()) {
                  this.a(PlayerChunkMap.this.world.getTileEntity(chunk, pos, block));
               }
            } else if (this.dirtyCount != 64) {
               this.a(
                  new PacketPlayOutMultiBlockChange(this.dirtyCount, this.dirtyBlocks, PlayerChunkMap.this.world.getChunkAt(this.location.x, this.location.z))
               );

               for (int i = 0; i < this.dirtyCount; i++) {
                  short dirtyBlock = this.dirtyBlocks[i];
                  int j = (dirtyBlock >> 12 & 15) + this.location.x * 16;
                  int k = dirtyBlock & 255;
                  int l = (dirtyBlock >> 8 & 15) + this.location.z * 16;
                  Chunk chunk = PlayerChunkMap.this.world.getChunkAtWorldCoords(j, l);
                  Block block = PlayerChunkMap.this.world.getType(chunk, j, k, l, true).getBlock();
                  if (block.isTileEntity()) {
                     this.a(PlayerChunkMap.this.world.getTileEntity(chunk, new BlockPosition(j, k, l), block));
                  }
               }
            } else {
               int ix = this.location.x * 16;
               int j = this.location.z * 16;
               PacketPlayOutMapChunk packet = null;

               for (EntityPlayer entityplayer : this.b) {
                  if (!entityplayer.chunkCoordIntPairQueue.contains(this.location)) {
                     if (packet == null) {
                        packet = new PacketPlayOutMapChunk(PlayerChunkMap.this.world.getChunkAt(this.location.x, this.location.z), false, this.f);
                     }

                     entityplayer.sendPacket(packet);
                  }
               }

               for (int k = 0; k < 16; k++) {
                  if ((this.f & 1 << k) != 0) {
                     int l = k << 4;

                     for (TileEntity tileEntity : PlayerChunkMap.this.world.getTileEntities(ix, l, j, ix + 16, l + 16, j + 16)) {
                        this.a(tileEntity);
                     }
                  }
               }
            }

            this.dirtyCount = 0;
            this.f = 0;
         }
      }

      private void a(TileEntity tileentity) {
         if (tileentity != null) {
            Packet<?> packet = tileentity.getUpdatePacket();
            if (packet != null) {
               this.a(packet);
            }
         }
      }
   }
}
