package lol.deyo.blaze.pathsearch.cache;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathEntity;

public class SearchCacheEntryPosition extends SearchCacheEntry {
   public SearchCacheEntryPosition(EntityInsentient entity, BlockPosition blockposition, PathEntity path) {
      super(entity, path);
      this.positionTarget = blockposition;
   }

   @Override
   public boolean isStillValid() {
      return super.isStillValid() ? this.getBlockPosition(this.entity).equals(this.positionStart) : false;
   }
}
