package org.bukkit.potion;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.LivingEntity;

@SerializableAs("PotionEffect")
public class PotionEffect implements ConfigurationSerializable {
   private static final String AMPLIFIER = "amplifier";
   private static final String DURATION = "duration";
   private static final String TYPE = "effect";
   private static final String AMBIENT = "ambient";
   private static final String PARTICLES = "has-particles";
   private final int amplifier;
   private final int duration;
   private final PotionEffectType type;
   private final boolean ambient;
   private final boolean particles;

   public PotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
      Validate.notNull(type, "effect type cannot be null");
      this.type = type;
      this.duration = duration;
      this.amplifier = amplifier;
      this.ambient = ambient;
      this.particles = particles;
   }

   public PotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient) {
      this(type, duration, amplifier, ambient, true);
   }

   public PotionEffect(PotionEffectType type, int duration, int amplifier) {
      this(type, duration, amplifier, true);
   }

   public PotionEffect(Map<String, Object> map) {
      this(getEffectType(map), getInt(map, "duration"), getInt(map, "amplifier"), getBool(map, "ambient", false), getBool(map, "has-particles", true));
   }

   private static PotionEffectType getEffectType(Map<?, ?> map) {
      int type = getInt(map, "effect");
      PotionEffectType effect = PotionEffectType.getById(type);
      if (effect != null) {
         return effect;
      } else {
         throw new NoSuchElementException(map + " does not contain " + "effect");
      }
   }

   private static int getInt(Map<?, ?> map, Object key) {
      Object num = map.get(key);
      if (num instanceof Integer) {
         return (Integer)num;
      } else {
         throw new NoSuchElementException(map + " does not contain " + key);
      }
   }

   private static boolean getBool(Map<?, ?> map, Object key, boolean def) {
      Object bool = map.get(key);
      return bool instanceof Boolean ? (Boolean)bool : def;
   }

   @Override
   public Map<String, Object> serialize() {
      return ImmutableMap.of(
         "effect", this.type.getId(), "duration", this.duration, "amplifier", this.amplifier, "ambient", this.ambient, "has-particles", this.particles
      );
   }

   public boolean apply(LivingEntity entity) {
      return entity.addPotionEffect(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof PotionEffect)) {
         return false;
      } else {
         PotionEffect that = (PotionEffect)obj;
         return this.type.equals(that.type)
            && this.ambient == that.ambient
            && this.amplifier == that.amplifier
            && this.duration == that.duration
            && this.particles == that.particles;
      }
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public int getDuration() {
      return this.duration;
   }

   public PotionEffectType getType() {
      return this.type;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean hasParticles() {
      return this.particles;
   }

   @Override
   public int hashCode() {
      int hash = 1;
      hash = hash * 31 + this.type.hashCode();
      hash = hash * 31 + this.amplifier;
      hash = hash * 31 + this.duration;
      hash ^= 572662306 >> (this.ambient ? 1 : -1);
      return hash ^ 572662306 >> (this.particles ? 1 : -1);
   }

   @Override
   public String toString() {
      return this.type.getName() + (this.ambient ? ":(" : ":") + this.duration + "t-x" + this.amplifier + (this.ambient ? ")" : "");
   }
}
