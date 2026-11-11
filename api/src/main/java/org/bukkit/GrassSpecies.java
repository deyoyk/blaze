package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum GrassSpecies {
   DEAD(0),
   NORMAL(1),
   FERN_LIKE(2);

   private final byte data;
   private static final Map<Byte, org.bukkit.GrassSpecies> BY_DATA = Maps.newHashMap();

   private GrassSpecies(int data) {
      this.data = (byte)data;
   }

   @Deprecated
   public byte getData() {
      return this.data;
   }

   @Deprecated
   public static org.bukkit.GrassSpecies getByData(byte data) {
      return BY_DATA.get(data);
   }

   static {
      for (org.bukkit.GrassSpecies grassSpecies : values()) {
         BY_DATA.put(grassSpecies.getData(), grassSpecies);
      }
   }
}
