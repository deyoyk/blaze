package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.optimizations.ObjectMapList;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.HumanEntity;

public class Chunk {
   private static final Logger c = LogManager.getLogger();
   public final World world;
   public final int[] heightMap;
   public final int locX;
   public final int locZ;
   public final Map<BlockPosition, TileEntity> tileEntities;
   public final List<Entity>[] entitySlices;
   private final ChunkSection[] sections;
   private final byte[] e;
   private final int[] f;
   private final boolean[] g;
   private final int[] itemCounts = new int[16];
   private final int[] inventoryEntityCounts = new int[16];
   public AtomicInteger pendingLightUpdates = new AtomicInteger();
   public long lightUpdateTime;
   public org.bukkit.Chunk bukkitChunk;
   public boolean mustSave;
   protected TObjectIntHashMap<Class<?>> entityCount = new TObjectIntHashMap();
   private boolean h;
   private boolean k;
   private boolean done;
   private boolean lit;
   public boolean q;
   private boolean r;
   private long lastSaved;
   public long lastActive;
   private int t;
   private long u;
   private int v;
   private PacketPlayOutMapChunk.ChunkMap chunkMap;
   private int emptySectionBits;
   private int neighbors = 4096;
   public final long chunkHashKey;
   private final BlockPosition chunkPosition;
   private final ChunkCoordIntPair chunkCoord;
   private final Random chunkRandom;

   public Chunk(World world, int i, int j) {
      this.sections = new ChunkSection[16];
      this.e = new byte[256];
      this.f = new int[256];
      this.g = new boolean[256];
      this.tileEntities = Maps.newHashMap();
      this.v = 4096;
      this.entitySlices = new List[16];
      this.world = world;
      this.locX = i;
      this.locZ = j;
      this.heightMap = new int[256];
      this.chunkHashKey = LongHash.toLong(this.locX, this.locZ);
      this.chunkCoord = new ChunkCoordIntPair(this.locX, this.locZ);
      this.chunkRandom = new Random(
         world.getSeed() + this.locX * this.locX * 4987142L + this.locX * 5947611L + this.locZ * this.locZ * 4392871L + this.locZ * 389711L ^ 987234911L
      );
      this.chunkPosition = new BlockPosition(this.locX << 4, 0, this.locZ << 4);

      for (int k = 0; k < this.entitySlices.length; k++) {
         this.entitySlices[k] = Collections.emptyList();
      }

      Arrays.fill(this.f, -999);
      Arrays.fill(this.e, (byte)-1);
      if (!(this instanceof EmptyChunk)) {
         this.bukkitChunk = new CraftChunk(this);
      }
   }

   public Chunk(World world, ChunkSnapshot chunksnapshot, int i, int j) {
      this(world, i, j);
      boolean flag = !world.worldProvider.o();

      for (int k = 0; k < 16; k++) {
         for (int l = 0; l < 16; l++) {
            for (int i1 = 0; i1 < 256; i1++) {
               int j1 = k * 256 * 16 | l * 256 | i1;
               IBlockData iblockdata = chunksnapshot.a(j1);
               if (iblockdata.getBlock().getMaterial() != Material.AIR) {
                  int k1 = i1 >> 4;
                  ChunkSection section = this.sections[k1];
                  if (section == null) {
                     section = new ChunkSection(k1 << 4, flag);
                     this.sections[k1] = section;
                  }

                  section.setType(k, i1 & 15, l, iblockdata);
               }
            }
         }
      }
   }

   public PacketPlayOutMapChunk.ChunkMap getChunkMap(boolean groundUpContinuous, int primaryBitMask) {
      if ((Boolean)BlazeConfig.cacheChunkMaps.getValue() && groundUpContinuous && (primaryBitMask == 0 || primaryBitMask == 65535)) {
         if (primaryBitMask == 0) {
            PacketPlayOutMapChunk.ChunkMap chunkMap = new PacketPlayOutMapChunk.ChunkMap();
            chunkMap.a = new byte[0];
            return chunkMap;
         } else {
            boolean isDirty = false;

            for (int i = 0; i < this.sections.length; i++) {
               ChunkSection section = this.sections[i];
               if (section == null) {
                  if ((this.emptySectionBits & 1 << i) == 0) {
                     isDirty = true;
                     this.emptySectionBits |= 1 << i;
                  }
               } else if ((this.emptySectionBits & 1 << i) == 1) {
                  isDirty = true;
                  this.emptySectionBits &= ~(1 << i);
                  section.isDirty = false;
               } else if (section.isDirty) {
                  isDirty = true;
                  section.isDirty = false;
               }
            }

            if (isDirty || this.chunkMap == null) {
               this.chunkMap = PacketPlayOutMapChunk.serializeToChunkMap(this, true, !this.world.worldProvider.o(), 65535);
            }

            return this.chunkMap;
         }
      } else {
         return PacketPlayOutMapChunk.serializeToChunkMap(this, groundUpContinuous, !this.world.worldProvider.o(), primaryBitMask);
      }
   }

   public boolean areNeighborsLoaded(int radius) {
      switch (radius) {
         case 1:
            int mask = 473536;
            if ((this.neighbors & 473536) == 473536) {
               return true;
            }

            return false;
         case 2:
            if (this.neighbors == 33554431) {
               return true;
            }

            return false;
         default:
            throw new UnsupportedOperationException(String.valueOf(radius));
      }
   }

   public IBlockData getBlockData(int x, int y, int z) {
      if (y >= 0 && y >> 4 < this.sections.length) {
         ChunkSection chunksection = this.sections[y >> 4];
         if (chunksection != null) {
            return chunksection.getType(x & 15, y & 15, z & 15);
         }
      }

      return Blocks.AIR.getBlockData();
   }

   public void setNeighborLoaded(int x, int z) {
      this.neighbors |= 1 << x * 5 + 12 + z;
   }

   public void setNeighborUnloaded(int x, int z) {
      this.neighbors &= ~(1 << x * 5 + 12 + z);
   }

   public boolean a(int i, int j) {
      return i == this.locX && j == this.locZ;
   }

   public int f(BlockPosition blockposition) {
      return this.b(blockposition.getX() & 15, blockposition.getZ() & 15);
   }

   public int f(int x, int z) {
      return this.b(x & 15, z & 15);
   }

   public int b(int i, int j) {
      return this.heightMap[j << 4 | i];
   }

   public int g() {
      for (int i = this.sections.length - 1; i >= 0; i--) {
         ChunkSection section = this.sections[i];
         if (section != null) {
            return section.getYPosition();
         }
      }

      return 0;
   }

   public ChunkSection[] getSections() {
      return this.sections;
   }

   public ChunkSection getSection(int y) {
      return y >= 0 && y >> 4 < this.sections.length ? this.sections[y >> 4] : null;
   }

   public void initLighting() {
      int i = this.g();
      this.t = Integer.MAX_VALUE;

      for (int j = 0; j < 16; j++) {
         for (int k = 0; k < 16; k++) {
            this.f[j + (k << 4)] = -999;

            for (int l = i + 16; l > 0; l--) {
               if (this.e(j, l - 1, k) != 0) {
                  this.heightMap[k << 4 | j] = l;
                  if (l < this.t) {
                     this.t = l;
                  }
                  break;
               }
            }

            if (!this.world.worldProvider.o()) {
               int var9 = 15;
               int i1 = i + 16 - 1;
               BlockPosition.MutableBlockPosition blockPosition = new BlockPosition.MutableBlockPosition();

               while (true) {
                  int j1 = this.e(j, i1, k);
                  if (j1 == 0 && var9 != 15) {
                     j1 = 1;
                  }

                  var9 -= j1;
                  if (var9 > 0) {
                     ChunkSection chunksection = this.sections[i1 >> 4];
                     if (chunksection != null) {
                        chunksection.a(j, i1 & 15, k, var9);
                        blockPosition.setValues(this.chunkPosition.getX() + j, i1, this.chunkPosition.getZ() + k);
                        this.world.n(blockPosition);
                     }
                  }

                  if (--i1 <= 0 || var9 <= 0) {
                     break;
                  }
               }
            }
         }
      }

      this.q = true;
   }

   private void d(int i, int j) {
      this.g[i + j * 16] = true;
      this.k = true;
   }

   public int a(int x, int y, int z, int i) {
      int j = x & 15;
      int l = z & 15;
      ChunkSection chunksection = this.sections[y >> 4];
      if (chunksection != null) {
         int i1 = this.world.worldProvider.o() ? 0 : chunksection.d(j, y & 15, l);
         i1 -= i;
         int j1 = chunksection.e(j, y & 15, l);
         if (j1 > i1) {
            i1 = j1;
         }

         return i1;
      } else {
         return !this.world.worldProvider.o() && i < EnumSkyBlock.SKY.c ? EnumSkyBlock.SKY.c - i : 0;
      }
   }

   private void h(boolean flag) {
      if (this.areNeighborsLoaded(1)) {
         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               int index = i + j * 16;
               if (this.g[index]) {
                  this.g[index] = false;
                  int k = this.b(i, j);
                  int l = this.locX * 16 + i;
                  int i1 = this.locZ * 16 + j;
                  int j1 = Integer.MAX_VALUE;

                  for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                     j1 = Math.min(j1, this.world.b(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ(), true));
                  }

                  this.c(l, i1, j1, true);

                  for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                     this.c(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ(), k, true);
                  }

                  if (flag) {
                     return;
                  }
               }
            }
         }

         this.k = false;
      }
   }

   private void c(int i, int j, int k) {
      this.c(i, j, k, false);
   }

   private void c(int i, int j, int k, boolean chunkAlreadyLoaded) {
      int l = this.world.getHighestBlockYAt(i, j, chunkAlreadyLoaded);
      if (l > k) {
         this.a(i, j, k, l + 1, !(Boolean)BlazeConfig.disableBlockLighting.getValue());
      } else if (l < k) {
         this.a(i, j, l, k + 1, !(Boolean)BlazeConfig.disableBlockLighting.getValue());
      }
   }

   public void a(EnumSkyBlock enumskyblock, int x, int y, int z, int i) {
      ChunkSection chunksection = this.sections[y >> 4];
      if (chunksection == null) {
         chunksection = this.sections[y >> 4] = new ChunkSection(y >> 4 << 4, !this.world.worldProvider.o());
         this.initLighting();
      }

      this.q = true;
      if (enumskyblock == EnumSkyBlock.SKY) {
         if (!this.world.worldProvider.o()) {
            chunksection.a(x & 15, y & 15, z & 15, i);
         }
      } else if (enumskyblock == EnumSkyBlock.BLOCK) {
         chunksection.b(x & 15, y & 15, z & 15, i);
      }
   }

   private void a(int i, int j, int k, int l, boolean lightUpdate) {
      if (l > k) {
         Chunk chunk = this.world.getChunkIfLoaded(i >> 4, j >> 4);
         if (chunk == null || !chunk.areNeighborsLoaded(1)) {
            return;
         }

         if (lightUpdate) {
            for (int i1 = k; i1 < l; i1++) {
               this.world.updateLight(EnumSkyBlock.SKY, i, i1, j);
            }
         }

         this.q = true;
      }
   }

   private void d(int i, int j, int k, boolean lightUpdate) {
      int l = this.heightMap[k << 4 | i] & 0xFF;
      int i1 = Math.max(j, l);

      while (i1 > 0 && this.e(i, i1 - 1, k) == 0) {
         i1--;
      }

      if (i1 != l) {
         this.world.a(i + this.locX * 16, k + this.locZ * 16, i1, l);
         this.heightMap[k << 4 | i] = i1;
         int j1 = this.locX * 16 + i;
         int k1 = this.locZ * 16 + k;
         if (!this.world.worldProvider.o()) {
            if (i1 < l) {
               for (int l1 = i1; l1 < l; l1++) {
                  ChunkSection chunksection = this.sections[l1 >> 4];
                  if (chunksection != null) {
                     chunksection.a(i, l1 & 15, k, 15);
                     this.world.n(new BlockPosition(this.chunkPosition.getX() + i, l1, this.chunkPosition.getZ() + k));
                  }
               }
            } else {
               for (int l1x = l; l1x < i1; l1x++) {
                  ChunkSection chunksection = this.sections[l1x >> 4];
                  if (chunksection != null) {
                     chunksection.a(i, l1x & 15, k, 0);
                     this.world.n(new BlockPosition(this.chunkPosition.getX() + i, l1x, this.chunkPosition.getZ() + k));
                  }
               }
            }

            int var15 = 15;

            while (i1 > 0 && var15 > 0) {
               int i2 = this.e(i, --i1, k);
               if (i2 == 0) {
                  i2 = 1;
               }

               var15 -= i2;
               if (var15 < 0) {
                  var15 = 0;
               }

               ChunkSection chunksection1 = this.sections[i1 >> 4];
               if (chunksection1 != null) {
                  chunksection1.a(i, i1 & 15, k, var15);
               }
            }
         }

         int l1xx = this.heightMap[k << 4 | i];
         int i2x = l;
         int j2 = l1xx;
         if (l1xx < l) {
            i2x = l1xx;
            j2 = l;
         }

         if (l1xx < this.t) {
            this.t = l1xx;
         }

         if (!this.world.worldProvider.o()) {
            for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
               this.a(j1 + enumdirection.getAdjacentX(), k1 + enumdirection.getAdjacentZ(), i2x, j2, lightUpdate);
            }

            this.a(j1, k1, i2x, j2, lightUpdate);
         }

         this.q = true;
      }
   }

   public int b(BlockPosition blockposition) {
      return this.getType(blockposition).p();
   }

   private int e(int i, int j, int k) {
      return this.getType(i, j, k).p();
   }

   public Block getType(int i, int j, int k) {
      Block block = Blocks.AIR;
      if (j >= 0 && j >> 4 < this.sections.length) {
         ChunkSection chunksection = this.sections[j >> 4];
         if (chunksection != null) {
            try {
               block = chunksection.b(i, j & 15, k);
            } catch (Throwable var8) {
               CrashReport crashreport = CrashReport.a(var8, "Getting block");
               throw new ReportedException(crashreport);
            }
         }
      }

      return block;
   }

   public Block getTypeAbs(int i, int j, int k) {
      try {
         return this.getType(i & 15, j, k & 15);
      } catch (ReportedException var6) {
         CrashReportSystemDetails crashreportsystemdetails = var6.a().a("Block being got");
         crashreportsystemdetails.a("Location", () -> CrashReportSystemDetails.a(new BlockPosition(this.locX * 16 + i, j, this.locZ * 16 + k)));
         throw var6;
      }
   }

   public Block getType(BlockPosition blockposition) {
      try {
         return this.getType(blockposition.getX() & 15, blockposition.getY(), blockposition.getZ() & 15);
      } catch (ReportedException var4) {
         CrashReportSystemDetails crashreportsystemdetails = var4.a().a("Block being got");
         crashreportsystemdetails.a("Location", () -> CrashReportSystemDetails.a(blockposition));
         throw var4;
      }
   }

   public IBlockData a(BlockPosition blockposition, IBlockData iblockdata) {
      return this.a(blockposition, iblockdata, true);
   }

   public IBlockData a(BlockPosition blockposition, IBlockData iblockdata, boolean lightUpdate) {
      if (lightUpdate && (Boolean)BlazeConfig.disableBlockLighting.getValue()) {
         lightUpdate = false;
      }

      int i = blockposition.getX() & 15;
      int j = blockposition.getY();
      int k = blockposition.getZ() & 15;
      int l = k << 4 | i;
      if (j >= this.f[l] - 1) {
         this.f[l] = -999;
      }

      int i1 = this.heightMap[l];
      IBlockData iblockdata1 = this.getBlockData(blockposition);
      if (iblockdata1 == iblockdata) {
         return null;
      } else {
         Block block = iblockdata.getBlock();
         Block block1 = iblockdata1.getBlock();
         ChunkSection chunksection = this.sections[j >> 4];
         boolean flag = false;
         if (chunksection == null) {
            if (block == Blocks.AIR) {
               return null;
            }

            chunksection = this.sections[j >> 4] = new ChunkSection(j >> 4 << 4, !this.world.worldProvider.o());
            flag = j >= i1;
         }

         chunksection.setType(i, j & 15, k, iblockdata);
         if (block1 != block) {
            block1.remove(this.world, blockposition, iblockdata1);
         }

         if (chunksection.getType(i, j & 15, k).getBlock() != block) {
            return null;
         } else {
            if (flag) {
               this.initLighting();
            } else {
               int j1 = block.p();
               int k1 = block1.p();
               if (j1 > 0) {
                  if (j >= i1) {
                     this.d(i, j + 1, k, lightUpdate);
                  }
               } else if (j == i1 - 1) {
                  this.d(i, j, k, lightUpdate);
               }

               if (j1 != k1
                  && (j1 < k1 || this.getBrightness(EnumSkyBlock.SKY, blockposition) > 0 || this.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 0)) {
                  this.d(i, k);
               }
            }

            if (block1 instanceof IContainer) {
               TileEntity tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
               if (tileentity != null) {
                  tileentity.E();
               }
            }

            if (block1 != block && (!this.world.captureBlockStates || block instanceof BlockContainer)) {
               block.onPlace(this.world, blockposition, iblockdata);
            }

            if (block instanceof IContainer) {
               TileEntity tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
               if (tileentity == null) {
                  tileentity = ((IContainer)block).a(this.world, block.toLegacyData(iblockdata));
                  this.world.setTileEntity(blockposition, tileentity);
               }

               if (tileentity != null) {
                  tileentity.E();
               }
            }

            this.q = true;
            return iblockdata1;
         }
      }
   }

   public IBlockData getBlockData(BlockPosition blockposition) {
      return this.getBlockData(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   private int g(int i, int j, int k) {
      if (j >> 4 >= this.sections.length) {
         return 0;
      } else {
         ChunkSection chunksection = this.sections[j >> 4];
         return chunksection != null ? chunksection.c(i, j & 15, k) : 0;
      }
   }

   public int c(BlockPosition blockposition) {
      return this.g(blockposition.getX() & 15, blockposition.getY(), blockposition.getZ() & 15);
   }

   public int getBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
      int i = x & 15;
      int k = z & 15;
      ChunkSection chunksection = this.sections[y >> 4];
      return chunksection == null
         ? (this.d(x, y, z) ? enumskyblock.c : 0)
         : (
            enumskyblock == EnumSkyBlock.SKY
               ? (this.world.worldProvider.o() ? 0 : chunksection.d(i, y & 15, k))
               : (enumskyblock == EnumSkyBlock.BLOCK ? chunksection.e(i, y & 15, k) : enumskyblock.c)
         );
   }

   public int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
      return this.getBrightness(enumskyblock, blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
      int j = blockposition.getX() & 15;
      int k = blockposition.getY();
      int l = blockposition.getZ() & 15;
      ChunkSection chunksection = this.sections[k >> 4];
      if (chunksection == null) {
         chunksection = this.sections[k >> 4] = new ChunkSection(k >> 4 << 4, !this.world.worldProvider.o());
         this.initLighting();
      }

      this.q = true;
      if (enumskyblock == EnumSkyBlock.SKY) {
         if (!this.world.worldProvider.o()) {
            chunksection.a(j, k & 15, l, i);
         }
      } else if (enumskyblock == EnumSkyBlock.BLOCK) {
         chunksection.b(j, k & 15, l, i);
      }
   }

   public boolean d(int x, int y, int z) {
      return y >= this.heightMap[z & 240 | x & 15];
   }

   public BiomeBase getBiome(int x, int z, WorldChunkManager worldchunkmanager) {
      int i = x & 15;
      int j = z & 15;
      int k = this.e[j << 4 | i] & 255;
      if (k == 255) {
         k = BiomeBase.PLAINS.id;
         this.e[j << 4 | i] = (byte)(k & 0xFF);
      }

      BiomeBase biomebase = BiomeBase.getBiome(k);
      return biomebase == null ? BiomeBase.PLAINS : biomebase;
   }

   public int a(BlockPosition blockposition, int i) {
      return this.a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
   }

   private List<Entity> getEntitySlice(int index) {
      List<Entity> slice = this.entitySlices[index];
      if (slice.isEmpty()) {
         slice = new ObjectMapList<>();
         this.entitySlices[index] = slice;
      }

      return slice;
   }

   public void addCannonEntity(Entity entity) {
      this.r = true;
      int k = MathHelper.floor(entity.locY / 16.0);
      if (k < 0) {
         k = 0;
      }

      if (k >= this.entitySlices.length) {
         k = this.entitySlices.length - 1;
      }

      entity.ad = true;
      entity.ae = this.locX;
      entity.af = k;
      entity.ag = this.locZ;
      this.getEntitySlice(k).add(entity);
   }

   public void a(Entity entity) {
      this.r = true;
      int i = MathHelper.floor(entity.locX / 16.0);
      int j = MathHelper.floor(entity.locZ / 16.0);
      if (i != this.locX || j != this.locZ) {
         Bukkit.getLogger().warning("Wrong location for " + entity + " in world '" + this.world.getWorld().getName() + "'!");
         Bukkit.getLogger()
            .warning("Entity is at " + entity.locX + "," + entity.locZ + " (chunk " + i + "," + j + ") but was stored in chunk " + this.locX + "," + this.locZ);
         entity.die();
      }

      int k = MathHelper.floor(entity.locY / 16.0);
      if (k < 0) {
         k = 0;
      }

      if (k >= this.entitySlices.length) {
         k = this.entitySlices.length - 1;
      }

      entity.ad = true;
      entity.ae = this.locX;
      entity.af = k;
      entity.ag = this.locZ;
      this.getEntitySlice(k).add(entity);
      if (entity instanceof EntityItem) {
         this.itemCounts[k]++;
      } else if (entity instanceof IInventory) {
         this.inventoryEntityCounts[k]++;
      }

      if (entity instanceof EntityInsentient) {
         EntityInsentient entityinsentient = (EntityInsentient)entity;
         if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent()) {
            return;
         }
      }

      EnumCreatureType[] var8;
      for (EnumCreatureType creatureType : var8 = EnumCreatureType.values()) {
         if (creatureType.a().isAssignableFrom(entity.getClass())) {
            this.entityCount.adjustOrPutValue(creatureType.a(), 1, 1);
         }
      }
   }

   public int getBlockDurability(int x, int y, int z) {
      if (y >= 0 && y >> 4 < this.sections.length) {
         ChunkSection chunksection = this.sections[y >> 4];
         if (chunksection != null) {
            return chunksection.getBlockDurability(x & 15, y & 15, z & 15);
         }
      }

      return 0;
   }

   public void setBlockDurability(int x, int y, int z, int durability) {
      if (y >= 0 && y >> 4 < this.sections.length) {
         ChunkSection chunksection = this.sections[y >> 4];
         if (chunksection != null) {
            chunksection.setBlockDurability(x & 15, y & 15, z & 15, durability);
         }
      }
   }

   public int getItemCount(BlockPosition blockPosition) {
      int k = MathHelper.floor(blockPosition.getY() / 16.0);
      k = Math.max(0, k);
      k = Math.min(this.entitySlices.length - 1, k);
      return this.itemCounts[k];
   }

   public void b(Entity entity) {
      this.a(entity, entity.af);
   }

   public void a(Entity entity, int i) {
      if (i < 0) {
         i = 0;
      }

      if (i >= this.entitySlices.length) {
         i = this.entitySlices.length - 1;
      }

      if (this.entitySlices[i].remove(entity)) {
         if (entity instanceof EntityItem) {
            this.itemCounts[i]--;
         } else if (entity instanceof IInventory) {
            this.inventoryEntityCounts[i]--;
         }

         if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient)entity;
            if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent()) {
               return;
            }
         }

         EnumCreatureType[] var6;
         for (EnumCreatureType creatureType : var6 = EnumCreatureType.values()) {
            if (creatureType.a().isAssignableFrom(entity.getClass())) {
               this.entityCount.adjustValue(creatureType.a(), -1);
            }
         }

         if (this.entitySlices[i].isEmpty()) {
            this.entitySlices[i] = Collections.emptyList();
         }
      }
   }

   public void removeCannonEntity(Entity entity, int i) {
      if (i < 0) {
         i = 0;
      }

      if (i >= this.entitySlices.length) {
         i = this.entitySlices.length - 1;
      }

      if (this.entitySlices[i].remove(entity) && this.entitySlices[i].isEmpty()) {
         this.entitySlices[i] = Collections.emptyList();
      }
   }

   public boolean d(BlockPosition blockposition) {
      return this.d(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public TileEntity i(BlockPosition blockposition) {
      Block block = this.getType(blockposition);
      return !block.isTileEntity() ? null : ((IContainer)block).a(this.world, this.c(blockposition));
   }

   public TileEntity a(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
      TileEntity tileentity = null;
      if (this.world.captureBlockStates) {
         tileentity = this.world.capturedTileEntities.get(blockposition);
      }

      if (tileentity == null) {
         tileentity = this.tileEntities.get(blockposition);
      }

      if (tileentity == null) {
         if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.IMMEDIATE) {
            tileentity = this.i(blockposition);
            this.world.setTileEntity(blockposition, tileentity);
         } else if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.QUEUED) {
            throw new IllegalStateException("Clientside tile entity request");
         }
      } else if (tileentity.x()) {
         this.tileEntities.remove(blockposition);
         return null;
      }

      return tileentity;
   }

   public void a(TileEntity tileentity) {
      this.a(tileentity.getPosition(), tileentity);
      if (this.h) {
         this.world.a(tileentity);
      }
   }

   public void a(BlockPosition blockposition, TileEntity tileentity) {
      tileentity.a(this.world);
      tileentity.a(blockposition);
      if (this.getType(blockposition) instanceof IContainer) {
         if (this.tileEntities.containsKey(blockposition)) {
            this.tileEntities.get(blockposition).y();
         }

         tileentity.D();
         this.tileEntities.put(blockposition, tileentity);
      } else if (this.world.paperSpigotConfig.removeInvalidMobSpawnerTEs
         && tileentity instanceof TileEntityMobSpawner
         && CraftMagicNumbers.getMaterial(this.getType(blockposition)) != org.bukkit.Material.MOB_SPAWNER) {
         this.tileEntities.remove(blockposition);
      }
   }

   public void e(BlockPosition blockposition) {
      if (this.h) {
         TileEntity tileentity = this.tileEntities.remove(blockposition);
         if (tileentity != null) {
            tileentity.y();
         }
      }
   }

   public void addEntities() {
      this.h = true;
      this.world.a(this.tileEntities.values());

      for (List<Entity> entitySlice : this.entitySlices) {
         for (Entity entity : entitySlice) {
            entity.ah();
         }

         this.world.b(entitySlice);
      }
   }

   public void removeEntities() {
      this.h = false;

      for (TileEntity tileentity : this.tileEntities.values()) {
         if (tileentity instanceof IInventory) {
            for (HumanEntity h : Lists.newArrayList(((IInventory)tileentity).getViewers())) {
               if (h instanceof CraftHumanEntity) {
                  ((CraftHumanEntity)h).getHandle().closeInventory();
               }
            }
         }

         this.world.b(tileentity);
      }

      for (List<Entity> entitySlice : this.entitySlices) {
         List<Entity> newList = Lists.newArrayList(entitySlice);
         Iterator<Entity> iter = newList.iterator();

         while (iter.hasNext()) {
            Entity entity = iter.next();
            if (entity instanceof IInventory) {
               for (HumanEntity hx : Lists.newArrayList(((IInventory)entity).getViewers())) {
                  if (hx instanceof CraftHumanEntity) {
                     ((CraftHumanEntity)hx).getHandle().closeInventory();
                  }
               }
            }

            if (entity instanceof EntityPlayer) {
               iter.remove();
            }
         }

         this.world.c(newList);
      }
   }

   public void e() {
      this.q = true;
   }

   public void a(Entity entity, AxisAlignedBB axisalignedbb, List<Entity> list, Predicate<? super Entity> predicate) {
      int i = MathHelper.floor((axisalignedbb.b - 2.0) / 16.0);
      int j = MathHelper.floor((axisalignedbb.e + 2.0) / 16.0);
      i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
      j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);

      for (int k = i; k <= j; k++) {
         List<Entity> slice = this.entitySlices[k];
         if (!slice.isEmpty()) {
            Iterator<Entity> iterator = slice.iterator();
            if (predicate != IEntitySelector.c || this.inventoryEntityCounts[k] > 0) {
               while (iterator.hasNext()) {
                  Entity entity1 = iterator.next();
                  if (entity1.getBoundingBox().b(axisalignedbb) && entity1 != entity) {
                     if (predicate == null || predicate.apply(entity1)) {
                        list.add(entity1);
                     }

                     Entity[] aentity = entity1.aB();
                     if (aentity != null) {
                        for (Entity value : aentity) {
                           if (value != entity && value.getBoundingBox().b(axisalignedbb) && (predicate == null || predicate.apply(value))) {
                              list.add(value);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public <T extends Entity> void a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, List<T> list, Predicate<? super T> predicate) {
      int i = MathHelper.floor((axisalignedbb.b - 2.0) / 16.0);
      int j = MathHelper.floor((axisalignedbb.e + 2.0) / 16.0);
      i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
      j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);
      int[] counts;
      if (ItemStack.class.isAssignableFrom(oclass)) {
         counts = this.itemCounts;
      } else if (IInventory.class.isAssignableFrom(oclass)) {
         counts = this.inventoryEntityCounts;
      } else {
         counts = null;
      }

      for (int k = i; k <= j; k++) {
         if (counts == null || counts[k] > 0) {
            for (Entity entity : this.entitySlices[k]) {
               if (oclass.isInstance(entity) && entity.getBoundingBox().b(axisalignedbb) && (predicate == null || ((Predicate<Entity>)predicate).apply(entity))) {
                  list.add((T)entity);
               }
            }
         }
      }
   }

   public boolean a(boolean flag) {
      return (
            flag
               ? (!this.r || this.world.getTime() == this.lastSaved) && !this.q
               : !this.r || this.world.getTime() < this.lastSaved + MinecraftServer.getServer().autosavePeriod * 4L
         )
         ? this.q
         : true;
   }

   public Random a(long i) {
      return this.chunkRandom;
   }

   public boolean isEmpty() {
      return false;
   }

   public void loadNearby(IChunkProvider ichunkprovider, IChunkProvider ichunkprovider1, int i, int j) {
      boolean flag = ichunkprovider.isChunkLoaded(i, j - 1);
      boolean flag1 = ichunkprovider.isChunkLoaded(i + 1, j);
      boolean flag2 = ichunkprovider.isChunkLoaded(i, j + 1);
      boolean flag3 = ichunkprovider.isChunkLoaded(i - 1, j);
      boolean flag4 = ichunkprovider.isChunkLoaded(i - 1, j - 1);
      boolean flag5 = ichunkprovider.isChunkLoaded(i + 1, j + 1);
      boolean flag6 = ichunkprovider.isChunkLoaded(i - 1, j + 1);
      boolean flag7 = ichunkprovider.isChunkLoaded(i + 1, j - 1);
      if (flag1 && flag2 && flag5) {
         if (!this.done) {
            ichunkprovider.getChunkAt(ichunkprovider1, i, j);
         } else {
            ichunkprovider.a(ichunkprovider1, this, i, j);
         }
      }

      if (flag3 && flag2 && flag6) {
         Chunk chunk = ichunkprovider.getOrCreateChunk(i - 1, j);
         if (!chunk.done) {
            ichunkprovider.getChunkAt(ichunkprovider1, i - 1, j);
         } else {
            ichunkprovider.a(ichunkprovider1, chunk, i - 1, j);
         }
      }

      if (flag && flag1 && flag7) {
         Chunk chunk = ichunkprovider.getOrCreateChunk(i, j - 1);
         if (!chunk.done) {
            ichunkprovider.getChunkAt(ichunkprovider1, i, j - 1);
         } else {
            ichunkprovider.a(ichunkprovider1, chunk, i, j - 1);
         }
      }

      if (flag4 && flag && flag3) {
         Chunk chunk = ichunkprovider.getOrCreateChunk(i - 1, j - 1);
         if (!chunk.done) {
            ichunkprovider.getChunkAt(ichunkprovider1, i - 1, j - 1);
         } else {
            ichunkprovider.a(ichunkprovider1, chunk, i - 1, j - 1);
         }
      }
   }

   public BlockPosition h(BlockPosition blockposition) {
      return this.h(blockposition.getX(), blockposition.getZ());
   }

   public BlockPosition h(int x, int z) {
      int i = x & 15;
      int j = z & 15;
      int k = i | j << 4;
      int y = this.f[k];
      if (y == -999) {
         y = this.g() + 15;
         int i1 = -1;

         while (y > 0 && i1 == -1) {
            Block block = this.getType(i, y, j);
            Material material = block.getMaterial();
            if (!material.isSolid() && !material.isLiquid()) {
               y--;
            } else {
               i1 = y + 1;
            }
         }

         y = i1;
         this.f[k] = i1;
      }

      return new BlockPosition(x, y, z);
   }

   public int getPrecipitationHeight(int x, int z) {
      int k = x | z << 4;
      int y = this.f[k];
      if (y == -999) {
         y = this.g() + 15;
         int i1 = -1;

         while (y > 0 && i1 == -1) {
            Block block = this.getType(x, y, z);
            Material material = block.getMaterial();
            if (!material.isSolid() && !material.isLiquid()) {
               y--;
            } else {
               i1 = y + 1;
            }
         }

         y = i1;
         this.f[k] = i1;
      }

      return y;
   }

   public void b(boolean flag) {
      if (this.k && !this.world.worldProvider.o() && !flag) {
         this.recheckGaps(false);
      }

      if (!this.lit && this.done && this.world.spigotConfig.randomLightUpdates) {
         this.n();
      }
   }

   public void recheckGaps(boolean isClientSide) {
      if (!this.world.paperSpigotConfig.useAsyncLighting) {
         this.h(isClientSide);
      } else {
         this.world.lightingExecutor.submit(() -> this.h(isClientSide));
      }
   }

   public boolean isReady() {
      return true;
   }

   public ChunkCoordIntPair j() {
      return this.chunkCoord;
   }

   public boolean c(int i, int j) {
      if (i < 0) {
         i = 0;
      }

      if (j >= 256) {
         j = 255;
      }

      for (int k = i; k <= j; k += 16) {
         ChunkSection chunksection = this.sections[k >> 4];
         if (chunksection != null && !chunksection.a()) {
            return false;
         }
      }

      return true;
   }

   public void a(ChunkSection[] achunksection) {
      if (this.sections.length != achunksection.length) {
         c.warn("Could not set level chunk sections, array length is {} instead of {}", new Object[]{achunksection.length, this.sections.length});
      } else {
         System.arraycopy(achunksection, 0, this.sections, 0, this.sections.length);
      }
   }

   public BiomeBase getBiome(BlockPosition blockposition, WorldChunkManager worldchunkmanager) {
      return this.getBiome(blockposition.getX(), blockposition.getZ(), worldchunkmanager);
   }

   public byte[] getBiomeIndex() {
      return this.e;
   }

   public void a(byte[] abyte) {
      if (this.e.length != abyte.length) {
         c.warn("Could not set level chunk biomes, array length is {} instead of {}", new Object[]{abyte.length, this.e.length});
      } else {
         for (int i = 0; i < this.e.length; i++) {
            this.e[i] = abyte[i] < 0 ? 0 : abyte[i];
         }
      }
   }

   public void l() {
      this.v = 0;
   }

   public void m() {
      for (int i = 0; i < 8; i++) {
         if (this.v >= 4096) {
            return;
         }

         int j = this.v % 16;
         int k = this.v / 16 % 16;
         int l = this.v / 256;
         this.v++;

         for (int i1 = 0; i1 < 16; i1++) {
            int x = this.chunkPosition.getX() + k;
            int y = this.chunkPosition.getY() + (j << 4) + i1;
            int z = this.chunkPosition.getZ() + l;
            boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;
            ChunkSection section = this.sections[j];
            if (section == null && flag || section != null && section.b(k, i1, l).getMaterial() == Material.AIR) {
               EnumDirection[] var14;
               for (EnumDirection enumdirection : var14 = EnumDirection.values()) {
                  int shiftedX = x + enumdirection.getAdjacentX();
                  int shiftedY = y + enumdirection.getAdjacentY();
                  int shiftedZ = z + enumdirection.getAdjacentZ();
                  if (this.world.getType(shiftedX, shiftedY, shiftedZ).getBlock().r() > 0) {
                     this.world.x(shiftedX, shiftedY, shiftedZ);
                  }
               }

               this.world.x(x, y, z);
            }
         }
      }
   }

   public void n() {
      this.done = true;
      this.lit = true;
      if (!this.world.worldProvider.o()) {
         if (this.world.areChunksLoadedBetween(this.chunkPosition.a(-1, 0, -1), this.chunkPosition.a(16, this.world.F(), 16))) {
            label44:
            for (int i = 0; i < 16; i++) {
               for (int j = 0; j < 16; j++) {
                  if (!this.e(i, j)) {
                     this.lit = false;
                     break label44;
                  }
               }
            }

            if (this.lit) {
               for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                  this.world
                     .getChunkAtWorldCoords(this.chunkPosition.shift(enumdirection, enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE ? 16 : 1))
                     .a(enumdirection.opposite());
               }

               this.y();
            }
         } else {
            this.lit = false;
         }
      }
   }

   private void y() {
      Arrays.fill(this.g, true);
      this.h(false);
   }

   private void a(EnumDirection enumdirection) {
      if (this.done) {
         if (enumdirection == EnumDirection.EAST) {
            for (int i = 0; i < 16; i++) {
               this.e(15, i);
            }
         } else if (enumdirection == EnumDirection.WEST) {
            for (int i = 0; i < 16; i++) {
               this.e(0, i);
            }
         } else if (enumdirection == EnumDirection.SOUTH) {
            for (int i = 0; i < 16; i++) {
               this.e(i, 15);
            }
         } else if (enumdirection == EnumDirection.NORTH) {
            for (int i = 0; i < 16; i++) {
               this.e(i, 0);
            }
         }
      }
   }

   private boolean e(int i, int j) {
      int k = this.g();
      boolean flag = false;
      boolean flag1 = false;
      int x = this.chunkPosition.getX() + i;
      int z = this.chunkPosition.getZ() + j;

      int y;
      for (y = k + 16 - 1; y > this.world.F() || y > 0 && !flag1; y--) {
         int i1 = this.getTypeAbs(x, y, z).p();
         if (i1 == 255 && y < this.world.F()) {
            flag1 = true;
         }

         if (!flag && i1 > 0) {
            flag = true;
         } else if (flag && i1 == 0 && !this.world.x(x, y, z)) {
            return false;
         }
      }

      for (; y > 0; y--) {
         if (this.getTypeAbs(x, y, z).r() > 0) {
            this.world.x(x, y, z);
         }
      }

      return true;
   }

   public boolean o() {
      return this.h;
   }

   public World getWorld() {
      return this.world;
   }

   public int[] q() {
      return this.heightMap;
   }

   public void a(int[] aint) {
      if (this.heightMap.length != aint.length) {
         c.warn("Could not set level chunk heightmap, array length is {} instead of {}", new Object[]{aint.length, this.heightMap.length});
      } else {
         System.arraycopy(aint, 0, this.heightMap, 0, this.heightMap.length);
      }
   }

   public Map<BlockPosition, TileEntity> getTileEntities() {
      return this.tileEntities;
   }

   public List<Entity>[] getEntitySlices() {
      return this.entitySlices;
   }

   public boolean isDone() {
      return this.done;
   }

   public void d(boolean flag) {
      this.done = flag;
   }

   public boolean u() {
      return this.lit;
   }

   public void e(boolean flag) {
      this.lit = flag;
   }

   public void f(boolean flag) {
      this.q = flag;
   }

   public void g(boolean flag) {
      this.r = flag;
   }

   public void setLastSaved(long i) {
      this.lastSaved = i;
   }

   public int v() {
      return this.t;
   }

   public long w() {
      return this.u;
   }

   public void c(long i) {
      this.u = i;
   }

   public int getLightLevel(int x, int y, int z) {
      if ((Boolean)BlazeConfig.disableBlockLighting.getValue()) {
         return 15;
      } else {
         if (y >= 256) {
            y = 255;
         }

         return x >= 0 && x <= 15 && z >= 0 && z <= 15 ? this.a(x, y, z, this.world.I) : this.world.getLightLevel(this.locX * 16 + x, y, this.locZ * 16 + z);
      }
   }

   public Block getTypePossibleOutOfBounds(int x, int y, int z) {
      Block block = Blocks.AIR;
      if (x >= 0 && !(x > 15 | z > 15) && z >= 0) {
         if (y >= 0 && y >> 4 < this.sections.length) {
            ChunkSection chunksection = this.sections[y >> 4];
            if (chunksection != null) {
               try {
                  block = chunksection.b(x, y & 15, z);
               } catch (Throwable var8) {
                  CrashReport crashreport = CrashReport.a(var8, "Getting block");
                  throw new ReportedException(crashreport);
               }
            }
         }

         return block;
      } else {
         return this.world.getType(this.locX * 16 + x, y, this.locZ * 16 + z, false).getBlock();
      }
   }

   public int getLightLevelWithNeighbours(int x, int y, int z) {
      if ((Boolean)BlazeConfig.disableBlockLighting.getValue()) {
         return 15;
      } else if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000) {
         return 15;
      } else if (this.getTypePossibleOutOfBounds(x, y, z).s()) {
         int top = this.getLightLevel(x, y + 1, z);
         int east = this.getLightLevel(x + 1, y, z);
         int west = this.getLightLevel(x - 1, y, z);
         int south = this.getLightLevel(x, y, z + 1);
         int north = this.getLightLevel(x, y, z - 1);
         if (east > top) {
            top = east;
         }

         if (west > top) {
            top = west;
         }

         if (south > top) {
            top = south;
         }

         if (north > top) {
            top = north;
         }

         return top;
      } else if (y < 0) {
         return 0;
      } else {
         return x >= 0 && x <= 15 && z >= 0 && z <= 15 ? this.getLightLevel(x, y, z) : this.world.getLightLevel(this.locX * 16 + x, y, this.locZ * 16 + z);
      }
   }

   public static enum EnumTileEntityState {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }
}
