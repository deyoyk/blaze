package lol.deyo.blaze.pathsearch;

import lol.deyo.blaze.pathsearch.cache.SearchCacheEntry;
import lol.deyo.blaze.pathsearch.cache.SearchCacheEntryEntity;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.NavigationAbstract;

public class PathSearchJobEntity extends PathSearchJob {
   public Entity target;

   public PathSearchJobEntity(NavigationAbstract navigation, Entity target) {
      super(navigation);
      this.target = target;
   }

   public PathSearchJob call() throws Exception {
      if (!this.issued) {
         this.issued = true;
         this.pathEntity = this.navigation.doPathSearch(this.chunkCache, this.target);
         this.navigation.setSearchResult(this);
         this.cleanup();
      }

      return this;
   }

   @Override
   protected boolean isEntitySearch() {
      return true;
   }

   @Override
   public void cleanup() {
      super.cleanup();
      this.target = null;
   }

   public UUID getCacheEntryKey() {
      return this.target.getUniqueID();
   }

   public SearchCacheEntry getCacheEntryValue() {
      return this.pathEntity != null ? new SearchCacheEntryEntity(this.navigation.getEntity(), this.target, this.pathEntity) : null;
   }
}
