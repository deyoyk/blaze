package lol.deyo.blaze.pathsearch;

import lol.deyo.blaze.async.factory.NamePriorityThreadFactory;
import lol.deyo.blaze.configs.BlazeConfig;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.entity.EntityType;

public class PathSearchThrottlerThread extends ThreadPoolExecutor {
   private int queueLimit;
   private final LinkedHashMap<PathSearchJob, PathSearchJob> filter;
   private final Set<Integer> activeSearchHashes;
   private static PathSearchThrottlerThread instance;
   private final Set<EntityType> entitiesWithOffloadedPathSearches = new HashSet<>();

   public PathSearchThrottlerThread(int poolSize) {
      super(poolSize, poolSize, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new NamePriorityThreadFactory(1, "Blaze_PathFinder"));
      instance = this;
      adjustPoolSize(poolSize);
      this.filter = new LinkedHashMap<>();
      this.activeSearchHashes = new HashSet<>();
      this.loadEntitiesWithOffloadedPathSearches();
   }

   public boolean queuePathSearch(PathSearchJob newJob) {
      boolean jobHasBeenQueued = false;
      if (newJob != null) {
         synchronized (this.filter) {
            if (this.filter.containsKey(newJob) || this.filter.size() < 1000) {
               jobHasBeenQueued = true;
               PathSearchJob previousJob = this.filter.put(newJob, newJob);
               if (previousJob != null) {
                  previousJob.cleanup();
               }
            }
         }
      }

      synchronized (this.filter) {
         Iterator<Entry<PathSearchJob, PathSearchJob>> iterator = this.filter.entrySet().iterator();

         while (iterator.hasNext() && this.getQueue().size() < this.queueLimit) {
            PathSearchJob jobToExecute = iterator.next().getValue();
            if (!this.activeSearchHashes.contains(jobToExecute.getSearchHash())) {
               iterator.remove();
               this.activeSearchHashes.add(jobToExecute.getSearchHash());
               this.submit(jobToExecute);
               if (newJob != null) {
                  break;
               }
            }
         }

         return jobHasBeenQueued;
      }
   }

   @Override
   public void shutdown() {
      this.getQueue().clear();
      super.shutdown();
   }

   @Override
   protected void afterExecute(Runnable runnable, Throwable throwable) {
      super.afterExecute(runnable, throwable);
      if (runnable instanceof FutureTask) {
         FutureTask<PathSearchJob> task = (FutureTask<PathSearchJob>)runnable;
         PathSearchJob job = null;

         try {
            job = task.get();
         } catch (ExecutionException | InterruptedException var7) {
         }

         if (job != null) {
            synchronized (this.filter) {
               this.activeSearchHashes.remove(job.getSearchHash());
            }
         }
      }

      this.queuePathSearch(null);
   }

   public static void adjustPoolSize(int size) {
      if (instance != null) {
         if (size > instance.getMaximumPoolSize()) {
            instance.setMaximumPoolSize(size);
            instance.setCorePoolSize(size);
         } else if (size < instance.getMaximumPoolSize()) {
            instance.setCorePoolSize(size);
            instance.setMaximumPoolSize(size);
         }

         instance.queueLimit = size * 8;
      }
   }

   private void loadEntitiesWithOffloadedPathSearches() {
      this.entitiesWithOffloadedPathSearches.clear();

      for (String name : BlazeConfig.entitiesPathSearchOffload.getValue()) {
         try {
            EntityType type = EntityType.valueOf(name.toUpperCase());
            this.entitiesWithOffloadedPathSearches.add(type);
         } catch (IllegalArgumentException var4) {
            var4.printStackTrace();
         }
      }
   }

   public boolean isPathSearchOffloadedFor(EntityInsentient entity) {
      return BlazeConfig.enableAsyncPathSearch.getValue() && this.entitiesWithOffloadedPathSearches.contains(entity.getBukkitEntity().getType());
   }
}
