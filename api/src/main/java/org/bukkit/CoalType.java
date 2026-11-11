package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum CoalType {
   COAL(0),
   CHARCOAL(1);

   private final byte data;
   private static final Map<Byte, org.bukkit.CoalType> BY_DATA = Maps.newHashMap();

   private CoalType(int data) {
      this.data = (byte)data;
   }

   @Deprecated
   public byte getData() {
      return this.data;
   }

   @Deprecated
   public static org.bukkit.CoalType getByData(byte data) {
      return BY_DATA.get(data);
   }

   static {
      for (org.bukkit.CoalType type : values()) {
         BY_DATA.put(type.data, type);
      }
   }
}
