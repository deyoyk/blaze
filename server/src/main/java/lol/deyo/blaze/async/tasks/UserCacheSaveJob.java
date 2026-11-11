package lol.deyo.blaze.async.tasks;

import java.util.List;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.UserCache.UserCacheEntry;

public class UserCacheSaveJob implements Runnable {
   private final List<UserCacheEntry> entries;

   public UserCacheSaveJob(List<UserCacheEntry> entries) {
      this.entries = entries;
   }

   @Override
   public void run() {
      MinecraftServer.getServer().getUserCache().saveEntries(this.entries);
   }
}
