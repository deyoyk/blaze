package lol.deyo.blaze.pathsearch;

import lol.deyo.blaze.utils.WeakChunkCache;
import java.util.concurrent.Callable;
import net.minecraft.server.v1_8_R3.NavigationAbstract;
import net.minecraft.server.v1_8_R3.PathEntity;

public abstract class PathSearchJob implements Callable<PathSearchJob> {
   public NavigationAbstract navigation;
   protected WeakChunkCache chunkCache;
   protected boolean issued;
   private final int hashCode;
   protected PathEntity pathEntity;

   public PathSearchJob(NavigationAbstract navigation) {
      this.navigation = navigation;
      this.hashCode = this.navigation.hashCode();
      this.issued = false;
      this.chunkCache = this.navigation.createChunkCache(this.isEntitySearch());
   }

   protected boolean isEntitySearch() {
      return false;
   }

   public void cleanup() {
      this.navigation = null;
      this.chunkCache = null;
      this.pathEntity = null;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   public final int getSearchHash() {
      return this.hashCode;
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof PathSearchJob) ? false : this.hashCode() == o.hashCode();
   }
}
