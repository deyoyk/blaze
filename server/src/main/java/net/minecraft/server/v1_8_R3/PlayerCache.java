package net.minecraft.server.v1_8_R3;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PlayerCache<K, V> extends LinkedHashMap<K, V> {
   private static final long serialVersionUID = 5272337123874421616L;

   public PlayerCache() {
      super(100, 0.75F, true);
   }

   @Override
   protected boolean removeEldestEntry(Entry<K, V> eldest) {
      return this.size() > MinecraftServer.getServer().getPlayerList().getPlayerCount();
   }
}
