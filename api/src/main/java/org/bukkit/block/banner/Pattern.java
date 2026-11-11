package org.bukkit.block.banner;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Pattern")
public class Pattern implements ConfigurationSerializable {
   private static final String COLOR = "color";
   private static final String PATTERN = "pattern";
   private final org.bukkit.DyeColor color;
   private final PatternType pattern;

   public Pattern(org.bukkit.DyeColor color, PatternType pattern) {
      this.color = color;
      this.pattern = pattern;
   }

   public Pattern(Map<String, Object> map) {
      this.color = org.bukkit.DyeColor.valueOf(getString(map, "color"));
      this.pattern = PatternType.getByIdentifier(getString(map, "pattern"));
   }

   private static String getString(Map<?, ?> map, Object key) {
      Object str = map.get(key);
      if (str instanceof String) {
         return (String)str;
      } else {
         throw new NoSuchElementException(map + " does not contain " + key);
      }
   }

   @Override
   public Map<String, Object> serialize() {
      return ImmutableMap.of("color", this.color.toString(), "pattern", this.pattern.getIdentifier());
   }

   public org.bukkit.DyeColor getColor() {
      return this.color;
   }

   public PatternType getPattern() {
      return this.pattern;
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 97 * hash + (this.color != null ? this.color.hashCode() : 0);
      return 97 * hash + (this.pattern != null ? this.pattern.hashCode() : 0);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Pattern other = (Pattern)obj;
         return this.color == other.color && this.pattern == other.pattern;
      }
   }
}
