package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum CropState {
   SEEDED(0),
   GERMINATED(1),
   VERY_SMALL(2),
   SMALL(3),
   MEDIUM(4),
   TALL(5),
   VERY_TALL(6),
   RIPE(7);

   private final byte data;
   private static final Map<Byte, org.bukkit.CropState> BY_DATA = Maps.newHashMap();

   private CropState(int data) {
      this.data = (byte)data;
   }

   @Deprecated
   public byte getData() {
      return this.data;
   }

   @Deprecated
   public static org.bukkit.CropState getByData(byte data) {
      return BY_DATA.get(data);
   }

   static {
      for (org.bukkit.CropState cropState : values()) {
         BY_DATA.put(cropState.getData(), cropState);
      }
   }
}
