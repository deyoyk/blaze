package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum SandstoneType {
   CRACKED(0),
   GLYPHED(1),
   SMOOTH(2);

   private final byte data;
   private static final Map<Byte, org.bukkit.SandstoneType> BY_DATA = Maps.newHashMap();

   private SandstoneType(int data) {
      this.data = (byte)data;
   }

   @Deprecated
   public byte getData() {
      return this.data;
   }

   @Deprecated
   public static org.bukkit.SandstoneType getByData(byte data) {
      return BY_DATA.get(data);
   }

   static {
      for (org.bukkit.SandstoneType type : values()) {
         BY_DATA.put(type.data, type);
      }
   }
}
