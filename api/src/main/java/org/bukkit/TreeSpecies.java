package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum TreeSpecies {
   GENERIC(0),
   REDWOOD(1),
   BIRCH(2),
   JUNGLE(3),
   ACACIA(4),
   DARK_OAK(5);

   private final byte data;
   private static final Map<Byte, org.bukkit.TreeSpecies> BY_DATA = Maps.newHashMap();

   private TreeSpecies(int data) {
      this.data = (byte)data;
   }

   @Deprecated
   public byte getData() {
      return this.data;
   }

   @Deprecated
   public static org.bukkit.TreeSpecies getByData(byte data) {
      return BY_DATA.get(data);
   }

   static {
      for (org.bukkit.TreeSpecies species : values()) {
         BY_DATA.put(species.data, species);
      }
   }
}
