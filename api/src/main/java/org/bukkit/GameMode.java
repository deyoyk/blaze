package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum GameMode {
   CREATIVE(1),
   SURVIVAL(0),
   ADVENTURE(2),
   SPECTATOR(3);

   private final int value;
   private static final Map<Integer, org.bukkit.GameMode> BY_ID = Maps.newHashMap();

   private GameMode(int value) {
      this.value = value;
   }

   @Deprecated
   public int getValue() {
      return this.value;
   }

   @Deprecated
   public static org.bukkit.GameMode getByValue(int value) {
      return BY_ID.get(value);
   }

   static {
      for (org.bukkit.GameMode mode : values()) {
         BY_ID.put(mode.getValue(), mode);
      }
   }
}
