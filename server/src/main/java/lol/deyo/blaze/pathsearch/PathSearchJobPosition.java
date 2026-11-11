package lol.deyo.blaze.pathsearch;

import lol.deyo.blaze.pathsearch.cache.SearchCacheEntryPosition;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.NavigationAbstract;

public class PathSearchJobPosition extends PathSearchJob {
   private final PositionPathSearchType type;
   private BlockPosition blockposition;

   public PathSearchJobPosition(NavigationAbstract navigation, BlockPosition blockposition, PositionPathSearchType type) {
      super(navigation);
      this.blockposition = blockposition;
      this.type = type;
   }

   public PathSearchJob call() throws Exception {
      if (!this.issued) {
         this.issued = true;
         this.pathEntity = this.navigation.doPathSearch(this.chunkCache, this.blockposition);
         this.navigation.setSearchResult(this);
         this.cleanup();
      }

      return this;
   }

   public PositionPathSearchType getCacheEntryKey() {
      return this.type;
   }

   public SearchCacheEntryPosition getCacheEntryValue() {
      return this.pathEntity != null ? new SearchCacheEntryPosition(this.navigation.getEntity(), this.blockposition, this.pathEntity) : null;
   }

   @Override
   public int hashCode() {
      return this.type.hashCode() ^ super.hashCode() << 4;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof PathSearchJobPosition)) {
         return false;
      } else {
         PathSearchJobPosition other = (PathSearchJobPosition)o;
         return this.type.equals(other.type) && this.hashCode() == other.hashCode();
      }
   }

   @Override
   public void cleanup() {
      super.cleanup();
      this.blockposition = null;
   }
}
