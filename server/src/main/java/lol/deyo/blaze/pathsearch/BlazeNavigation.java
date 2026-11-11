package lol.deyo.blaze.pathsearch;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.pathsearch.cache.SearchCacheEntry;
import lol.deyo.blaze.pathsearch.cache.SearchCacheEntryEntity;
import lol.deyo.blaze.pathsearch.cache.SearchCacheEntryPosition;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Navigation;
import net.minecraft.server.v1_8_R3.PathEntity;
import net.minecraft.server.v1_8_R3.World;

public class BlazeNavigation extends Navigation {
   private final Map<UUID, SearchCacheEntry> searchCache;
   private final Map<PositionPathSearchType, SearchCacheEntryPosition> positionSearchCache;
   private final Object jobLock = new Object();
   private int cleanUpDelay = 0;
   private PathSearchJob lastQueuedJob;

   public BlazeNavigation(EntityInsentient entityinsentient, World world) {
      super(entityinsentient, world);
      this.searchCache = new HashMap<>();
      this.positionSearchCache = new HashMap<>();
   }

   private double getMinimumDistanceForOffloadingSquared() {
      return BlazeConfig.minimumDistanceToTarget.getValue();
   }

   private boolean hasAsyncSearchIssued() {
      synchronized (this.jobLock) {
         return this.lastQueuedJob != null;
      }
   }

   private void queueSearch(PathSearchJob job) {
      synchronized (this.jobLock) {
         if (Blaze.getInstance().getThreadingManager().getPathSearchExecutor().queuePathSearch(job)) {
            this.lastQueuedJob = job;
         }
      }
   }

   private void issueSearch(Entity target) {
      this.queueSearch(new PathSearchJobEntity(this, target));
   }

   private void issueSearch(BlockPosition blockposition, PositionPathSearchType type) {
      this.queueSearch(new PathSearchJobPosition(this, blockposition, type));
   }

   public void setSearchResult(PathSearchJobEntity pathSearch) {
      synchronized (this.jobLock) {
         if (this.lastQueuedJob == pathSearch) {
            this.lastQueuedJob = null;
         }
      }

      SearchCacheEntry entry = pathSearch.getCacheEntryValue();
      if (entry != null && entry.didSearchSucceed()) {
         synchronized (this.searchCache) {
            UUID key = pathSearch.getCacheEntryKey();
            this.searchCache.remove(key);
            this.searchCache.put(key, entry);
         }
      }
   }

   public void setSearchResult(PathSearchJobPosition pathSearch) {
      synchronized (this.jobLock) {
         if (this.lastQueuedJob == pathSearch) {
            this.lastQueuedJob = null;
         }
      }

      SearchCacheEntryPosition entry = pathSearch.getCacheEntryValue();
      if (entry != null && entry.didSearchSucceed()) {
         synchronized (this.positionSearchCache) {
            PositionPathSearchType key = pathSearch.getCacheEntryKey();
            this.positionSearchCache.remove(key);
            this.positionSearchCache.put(key, entry);
         }
      }
   }

   public PathEntity a(Entity entity) {
      if (!this.offloadSearches() || this.b.h(entity) < this.getMinimumDistanceForOffloadingSquared()) {
         return super.a(entity);
      } else if (!this.b()) {
         return null;
      } else {
         SearchCacheEntry entry = null;
         UUID id = entity.getUniqueID();
         synchronized (this.searchCache) {
            if (this.searchCache.containsKey(id)) {
               entry = this.searchCache.get(id);
            }
         }

         PathEntity resultPath = null;
         if (entry != null) {
            resultPath = entry.getAdjustedPathEntity();
            if (!entry.isStillValid()) {
               this.issueSearch(entity);
            }
         }

         if (entry == null && !this.hasAsyncSearchIssued()) {
            resultPath = super.a(entity);
            if (resultPath != null) {
               SearchCacheEntry var9 = new SearchCacheEntryEntity(this.b, entity, resultPath);
               synchronized (this.searchCache) {
                  SearchCacheEntry oldEntry = this.searchCache.put(id, var9);
                  if (oldEntry != null) {
                     oldEntry.cleanup();
                  }
               }
            }
         }

         return resultPath;
      }
   }

   public PathEntity a(BlockPosition blockposition) {
      return this.a(blockposition, PositionPathSearchType.ANOTHER);
   }

   public PathEntity a(BlockPosition blockposition, PositionPathSearchType type) {
      if (!this.offloadSearches() || this.b.c(blockposition) < this.getMinimumDistanceForOffloadingSquared()) {
         return super.a(blockposition);
      } else if (!this.b()) {
         return null;
      } else {
         SearchCacheEntryPosition entry = null;
         synchronized (this.positionSearchCache) {
            if (this.positionSearchCache.containsKey(type)) {
               entry = this.positionSearchCache.get(type);
            }
         }

         PathEntity resultPath = null;
         if (entry != null) {
            resultPath = entry.getAdjustedPathEntity();
            if (!entry.isStillValid()) {
               this.issueSearch(blockposition, type);
            }
         }

         if (entry == null && !this.hasAsyncSearchIssued()) {
            resultPath = super.a(blockposition);
            if (resultPath != null) {
               entry = new SearchCacheEntryPosition(this.b, blockposition, resultPath);
               synchronized (this.positionSearchCache) {
                  SearchCacheEntry oldEntry = this.positionSearchCache.put(type, entry);
                  if (oldEntry != null) {
                     oldEntry.cleanup();
                  }
               }
            }
         }

         return resultPath;
      }
   }

   public PathEntity a(double d0, double d1, double d2, PositionPathSearchType type) {
      return this.a(new BlockPosition(MathHelper.floor(d0), (int)d1, MathHelper.floor(d2)), type);
   }

   public boolean a(double d0, double d1, double d2, double d3, PositionPathSearchType type) {
      PathEntity pathentity = this.a(MathHelper.floor(d0), (int)d1, MathHelper.floor(d2), type);
      return this.a(pathentity, d3);
   }

   public void cleanUpExpiredSearches() {
      this.cleanUpDelay++;
      if (this.cleanUpDelay > 100) {
         this.cleanUpDelay = 0;
         synchronized (this.searchCache) {
            Iterator<Entry<UUID, SearchCacheEntry>> iterator = this.searchCache.entrySet().iterator();

            while (iterator.hasNext()) {
               SearchCacheEntry entry = iterator.next().getValue();
               if (!entry.hasExpired()) {
                  break;
               }

               iterator.remove();
               entry.cleanup();
            }
         }

         synchronized (this.positionSearchCache) {
            Iterator<Entry<PositionPathSearchType, SearchCacheEntryPosition>> iterator = this.positionSearchCache.entrySet().iterator();

            while (iterator.hasNext()) {
               SearchCacheEntryPosition entry = iterator.next().getValue();
               if (!entry.hasExpired()) {
                  break;
               }

               iterator.remove();
               entry.cleanup();
            }
         }
      }
   }

   private boolean offloadSearches() {
      return Blaze.getInstance().getThreadingManager().getPathSearchExecutor().isPathSearchOffloadedFor(this.b);
   }
}
