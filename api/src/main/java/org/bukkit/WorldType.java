package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum WorldType {
   NORMAL("DEFAULT"),
   FLAT("FLAT"),
   VERSION_1_1("DEFAULT_1_1"),
   LARGE_BIOMES("LARGEBIOMES"),
   AMPLIFIED("AMPLIFIED"),
   CUSTOMIZED("CUSTOMIZED");

   private static final Map<String, org.bukkit.WorldType> BY_NAME = Maps.newHashMap();
   private final String name;

   private WorldType(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public static org.bukkit.WorldType getByName(String name) {
      return BY_NAME.get(name.toUpperCase());
   }

   static {
      for (org.bukkit.WorldType type : values()) {
         BY_NAME.put(type.name, type);
      }
   }
}
