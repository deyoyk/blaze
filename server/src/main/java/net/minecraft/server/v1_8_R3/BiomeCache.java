package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;

public class BiomeCache {
   private final WorldChunkManager a;
   private long b;
   private final Long2ObjectOpenHashMap<BiomeCache.BiomeCacheBlock> c = new Long2ObjectOpenHashMap();
   private final List<BiomeCache.BiomeCacheBlock> d = Lists.newArrayList();

   public BiomeCache(WorldChunkManager worldchunkmanager) {
      this.a = worldchunkmanager;
   }

   public BiomeCache.BiomeCacheBlock a(int i, int j) {
      i >>= 4;
      j >>= 4;
      long k = LongHash.toLong(i, j);
      BiomeCache.BiomeCacheBlock cacheBlock = (BiomeCache.BiomeCacheBlock)this.c.get(k);
      if (cacheBlock == null) {
         cacheBlock = new BiomeCache.BiomeCacheBlock(i, j);
         this.c.put(k, cacheBlock);
         this.d.add(cacheBlock);
      }

      cacheBlock.e = MinecraftServer.az();
      return cacheBlock;
   }

   public BiomeBase a(int i, int j, BiomeBase biomebase) {
      BiomeBase biomebase1 = this.a(i, j).a(i, j);
      return biomebase1 == null ? biomebase : biomebase1;
   }

   public void a() {
      long i = MinecraftServer.az();
      long j = i - this.b;
      if (j > 7500L || j < 0L) {
         this.b = i;

         for (int k = 0; k < this.d.size(); k++) {
            BiomeCache.BiomeCacheBlock biomecache_biomecacheblock = this.d.get(k);
            long l = i - biomecache_biomecacheblock.e;
            if (l > 30000L || l < 0L) {
               this.d.remove(k--);
               this.c.remove(LongHash.toLong(biomecache_biomecacheblock.c, biomecache_biomecacheblock.d));
            }
         }
      }
   }

   public BiomeBase[] c(int i, int j) {
      return this.a(i, j).b;
   }

   public class BiomeCacheBlock {
      public BiomeBase[] b = new BiomeBase[256];
      public int c;
      public int d;
      public long e;

      public BiomeCacheBlock(int i, int j) {
         this.c = i;
         this.d = j;
         BiomeCache.this.a.a(this.b, i << 4, j << 4, 16, 16, false);
      }

      public BiomeBase a(int i, int j) {
         return this.b[i & 15 | (j & 15) << 4];
      }
   }
}
