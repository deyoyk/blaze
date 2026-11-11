package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum Instrument {
   PIANO(0),
   BASS_DRUM(1),
   SNARE_DRUM(2),
   STICKS(3),
   BASS_GUITAR(4);

   private final byte type;
   private static final Map<Byte, org.bukkit.Instrument> BY_DATA = Maps.newHashMap();

   private Instrument(int type) {
      this.type = (byte)type;
   }

   @Deprecated
   public byte getType() {
      return this.type;
   }

   @Deprecated
   public static org.bukkit.Instrument getByType(byte type) {
      return BY_DATA.get(type);
   }

   static {
      for (org.bukkit.Instrument instrument : values()) {
         BY_DATA.put(instrument.getType(), instrument);
      }
   }
}
