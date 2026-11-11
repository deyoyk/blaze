package org.bukkit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Firework")
public final class FireworkEffect implements ConfigurationSerializable {
   private static final String FLICKER = "flicker";
   private static final String TRAIL = "trail";
   private static final String COLORS = "colors";
   private static final String FADE_COLORS = "fade-colors";
   private static final String TYPE = "type";
   private final boolean flicker;
   private final boolean trail;
   private final ImmutableList<org.bukkit.Color> colors;
   private final ImmutableList<org.bukkit.Color> fadeColors;
   private final org.bukkit.FireworkEffect.Type type;
   private String string = null;

   public static org.bukkit.FireworkEffect.Builder builder() {
      return new org.bukkit.FireworkEffect.Builder();
   }

   FireworkEffect(
      boolean flicker, boolean trail, ImmutableList<org.bukkit.Color> colors, ImmutableList<org.bukkit.Color> fadeColors, org.bukkit.FireworkEffect.Type type
   ) {
      if (colors.isEmpty()) {
         throw new IllegalStateException("Cannot make FireworkEffect without any color");
      } else {
         this.flicker = flicker;
         this.trail = trail;
         this.colors = colors;
         this.fadeColors = fadeColors;
         this.type = type;
      }
   }

   public boolean hasFlicker() {
      return this.flicker;
   }

   public boolean hasTrail() {
      return this.trail;
   }

   public List<org.bukkit.Color> getColors() {
      return this.colors;
   }

   public List<org.bukkit.Color> getFadeColors() {
      return this.fadeColors;
   }

   public org.bukkit.FireworkEffect.Type getType() {
      return this.type;
   }

   public static ConfigurationSerializable deserialize(Map<String, Object> map) {
      org.bukkit.FireworkEffect.Type type = org.bukkit.FireworkEffect.Type.valueOf((String)map.get("type"));
      if (type == null) {
         throw new IllegalArgumentException(map.get("type") + " is not a valid Type");
      } else {
         return builder()
            .flicker((Boolean)map.get("flicker"))
            .trail((Boolean)map.get("trail"))
            .withColor((Iterable<?>)map.get("colors"))
            .withFade((Iterable<?>)map.get("fade-colors"))
            .with(type)
            .build();
      }
   }

   @Override
   public Map<String, Object> serialize() {
      return ImmutableMap.of("flicker", this.flicker, "trail", this.trail, "colors", this.colors, "fade-colors", this.fadeColors, "type", this.type.name());
   }

   @Override
   public String toString() {
      String string = this.string;
      return string == null ? (this.string = "FireworkEffect:" + this.serialize()) : string;
   }

   @Override
   public int hashCode() {
      int PRIME = 31;
      int TRUE = 1231;
      int FALSE = 1237;
      int hash = 1;
      hash = hash * 31 + (this.flicker ? 1231 : 1237);
      hash = hash * 31 + (this.trail ? 1231 : 1237);
      hash = hash * 31 + this.type.hashCode();
      hash = hash * 31 + this.colors.hashCode();
      return hash * 31 + this.fadeColors.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof org.bukkit.FireworkEffect)) {
         return false;
      } else {
         org.bukkit.FireworkEffect that = (org.bukkit.FireworkEffect)obj;
         return this.flicker == that.flicker
            && this.trail == that.trail
            && this.type == that.type
            && this.colors.equals(that.colors)
            && this.fadeColors.equals(that.fadeColors);
      }
   }

   public static final class Builder {
      boolean flicker = false;
      boolean trail = false;
      final com.google.common.collect.ImmutableList.Builder<org.bukkit.Color> colors = ImmutableList.builder();
      com.google.common.collect.ImmutableList.Builder<org.bukkit.Color> fadeColors = null;
      org.bukkit.FireworkEffect.Type type = org.bukkit.FireworkEffect.Type.BALL;

      Builder() {
      }

      public org.bukkit.FireworkEffect.Builder with(org.bukkit.FireworkEffect.Type type) throws IllegalArgumentException {
         Validate.notNull(type, "Cannot have null type");
         this.type = type;
         return this;
      }

      public org.bukkit.FireworkEffect.Builder withFlicker() {
         this.flicker = true;
         return this;
      }

      public org.bukkit.FireworkEffect.Builder flicker(boolean flicker) {
         this.flicker = flicker;
         return this;
      }

      public org.bukkit.FireworkEffect.Builder withTrail() {
         this.trail = true;
         return this;
      }

      public org.bukkit.FireworkEffect.Builder trail(boolean trail) {
         this.trail = trail;
         return this;
      }

      public org.bukkit.FireworkEffect.Builder withColor(org.bukkit.Color color) throws IllegalArgumentException {
         Validate.notNull(color, "Cannot have null color");
         this.colors.add(color);
         return this;
      }

      public org.bukkit.FireworkEffect.Builder withColor(org.bukkit.Color... colors) throws IllegalArgumentException {
         Validate.notNull(colors, "Cannot have null colors");
         if (colors.length == 0) {
            return this;
         } else {
            com.google.common.collect.ImmutableList.Builder<org.bukkit.Color> list = this.colors;

            for (org.bukkit.Color color : colors) {
               Validate.notNull(color, "Color cannot be null");
               list.add(color);
            }

            return this;
         }
      }

      public org.bukkit.FireworkEffect.Builder withColor(Iterable<?> colors) throws IllegalArgumentException {
         Validate.notNull(colors, "Cannot have null colors");
         com.google.common.collect.ImmutableList.Builder<org.bukkit.Color> list = this.colors;

         for (Object color : colors) {
            if (!(color instanceof org.bukkit.Color)) {
               throw new IllegalArgumentException(color + " is not a Color in " + colors);
            }

            list.add((org.bukkit.Color)color);
         }

         return this;
      }

      public org.bukkit.FireworkEffect.Builder withFade(org.bukkit.Color color) throws IllegalArgumentException {
         Validate.notNull(color, "Cannot have null color");
         if (this.fadeColors == null) {
            this.fadeColors = ImmutableList.builder();
         }

         this.fadeColors.add(color);
         return this;
      }

      public org.bukkit.FireworkEffect.Builder withFade(org.bukkit.Color... colors) throws IllegalArgumentException {
         Validate.notNull(colors, "Cannot have null colors");
         if (colors.length == 0) {
            return this;
         } else {
            com.google.common.collect.ImmutableList.Builder<org.bukkit.Color> list = this.fadeColors;
            if (list == null) {
               list = this.fadeColors = ImmutableList.builder();
            }

            for (org.bukkit.Color color : colors) {
               Validate.notNull(color, "Color cannot be null");
               list.add(color);
            }

            return this;
         }
      }

      public org.bukkit.FireworkEffect.Builder withFade(Iterable<?> colors) throws IllegalArgumentException {
         Validate.notNull(colors, "Cannot have null colors");
         com.google.common.collect.ImmutableList.Builder<org.bukkit.Color> list = this.fadeColors;
         if (list == null) {
            list = this.fadeColors = ImmutableList.builder();
         }

         for (Object color : colors) {
            if (!(color instanceof org.bukkit.Color)) {
               throw new IllegalArgumentException(color + " is not a Color in " + colors);
            }

            list.add((org.bukkit.Color)color);
         }

         return this;
      }

      public org.bukkit.FireworkEffect build() {
         return new org.bukkit.FireworkEffect(
            this.flicker, this.trail, this.colors.build(), this.fadeColors == null ? ImmutableList.of() : this.fadeColors.build(), this.type
         );
      }
   }

   public static enum Type {
      BALL,
      BALL_LARGE,
      STAR,
      BURST,
      CREEPER;
   }
}
