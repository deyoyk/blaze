package lol.deyo.blaze.pathsearch.cache;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathEntity;

public class SearchCacheEntryEntity extends SearchCacheEntry {
   private Entity target;

   public SearchCacheEntryEntity(EntityInsentient entity, Entity target, PathEntity path) {
      super(entity, path);
      this.target = target;
      this.positionTarget = this.getBlockPosition(this.target);
   }

   @Override
   public boolean isStillValid() {
      return super.isStillValid() ? this.getBlockPosition(this.target).equals(this.positionTarget) : false;
   }

   @Override
   public void cleanup() {
      super.cleanup();
      this.target = null;
   }
}
