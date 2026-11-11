package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum Difficulty {
   PEACEFUL(0),
   EASY(1),
   NORMAL(2),
   HARD(3);

   private final int value;
   private static final Map<Integer, org.bukkit.Difficulty> BY_ID = Maps.newHashMap();

   private Difficulty(int value) {
      this.value = value;
   }

   @Deprecated
   public int getValue() {
      return this.value;
   }

   @Deprecated
   public static org.bukkit.Difficulty getByValue(int value) {
      return BY_ID.get(value);
   }

   static {
      for (org.bukkit.Difficulty diff : values()) {
         BY_ID.put(diff.value, diff);
      }
   }
}
