package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.FireworkMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaFirework extends CraftMetaItem implements FireworkMeta {
   static final CraftMetaItem.ItemMetaKey FIREWORKS = new CraftMetaItem.ItemMetaKey("Fireworks");
   static final CraftMetaItem.ItemMetaKey FLIGHT = new CraftMetaItem.ItemMetaKey("Flight", "power");
   static final CraftMetaItem.ItemMetaKey EXPLOSIONS = new CraftMetaItem.ItemMetaKey("Explosions", "firework-effects");
   static final CraftMetaItem.ItemMetaKey EXPLOSION_COLORS = new CraftMetaItem.ItemMetaKey("Colors");
   static final CraftMetaItem.ItemMetaKey EXPLOSION_TYPE = new CraftMetaItem.ItemMetaKey("Type");
   static final CraftMetaItem.ItemMetaKey EXPLOSION_TRAIL = new CraftMetaItem.ItemMetaKey("Trail");
   static final CraftMetaItem.ItemMetaKey EXPLOSION_FLICKER = new CraftMetaItem.ItemMetaKey("Flicker");
   static final CraftMetaItem.ItemMetaKey EXPLOSION_FADE = new CraftMetaItem.ItemMetaKey("FadeColors");
   private List<org.bukkit.FireworkEffect> effects;
   private int power;

   CraftMetaFirework(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaFirework) {
         CraftMetaFirework that = (CraftMetaFirework)meta;
         this.power = that.power;
         if (that.hasEffects()) {
            this.effects = new ArrayList<>(that.effects);
         }
      }
   }

   CraftMetaFirework(NBTTagCompound tag) {
      super(tag);
      if (tag.hasKey(FIREWORKS.NBT)) {
         NBTTagCompound fireworks = tag.getCompound(FIREWORKS.NBT);
         this.power = 255 & fireworks.getByte(FLIGHT.NBT);
         if (fireworks.hasKey(EXPLOSIONS.NBT)) {
            NBTTagList fireworkEffects = fireworks.getList(EXPLOSIONS.NBT, 10);
            List<org.bukkit.FireworkEffect> effects = this.effects = new ArrayList<>(fireworkEffects.size());

            for (int i = 0; i < fireworkEffects.size(); i++) {
               try {
                  effects.add(getEffect(fireworkEffects.get(i)));
               } catch (IllegalArgumentException var7) {
               }
            }
         }
      }
   }

   static org.bukkit.FireworkEffect getEffect(NBTTagCompound explosion) {
      org.bukkit.FireworkEffect.Builder effect = org.bukkit.FireworkEffect.builder()
         .flicker(explosion.getBoolean(EXPLOSION_FLICKER.NBT))
         .trail(explosion.getBoolean(EXPLOSION_TRAIL.NBT))
         .with(getEffectType(255 & explosion.getByte(EXPLOSION_TYPE.NBT)));

      int[] var5;
      for (int color : var5 = explosion.getIntArray(EXPLOSION_COLORS.NBT)) {
         effect.withColor(org.bukkit.Color.fromRGB(color));
      }

      for (int color : var5 = explosion.getIntArray(EXPLOSION_FADE.NBT)) {
         effect.withFade(org.bukkit.Color.fromRGB(color));
      }

      return effect.build();
   }

   static NBTTagCompound getExplosion(org.bukkit.FireworkEffect effect) {
      NBTTagCompound explosion = new NBTTagCompound();
      if (effect.hasFlicker()) {
         explosion.setBoolean(EXPLOSION_FLICKER.NBT, true);
      }

      if (effect.hasTrail()) {
         explosion.setBoolean(EXPLOSION_TRAIL.NBT, true);
      }

      addColors(explosion, EXPLOSION_COLORS, effect.getColors());
      addColors(explosion, EXPLOSION_FADE, effect.getFadeColors());
      explosion.setByte(EXPLOSION_TYPE.NBT, (byte)getNBT(effect.getType()));
      return explosion;
   }

   static int getNBT(org.bukkit.FireworkEffect.Type type) {
      switch (type) {
         case BALL:
            return 0;
         case BALL_LARGE:
            return 1;
         case STAR:
            return 2;
         case BURST:
            return 4;
         case CREEPER:
            return 3;
         default:
            throw new IllegalStateException(type.toString());
      }
   }

   static org.bukkit.FireworkEffect.Type getEffectType(int nbt) {
      switch (nbt) {
         case 0:
            return org.bukkit.FireworkEffect.Type.BALL;
         case 1:
            return org.bukkit.FireworkEffect.Type.BALL_LARGE;
         case 2:
            return org.bukkit.FireworkEffect.Type.STAR;
         case 3:
            return org.bukkit.FireworkEffect.Type.CREEPER;
         case 4:
            return org.bukkit.FireworkEffect.Type.BURST;
         default:
            throw new IllegalStateException(Integer.toString(nbt));
      }
   }

   CraftMetaFirework(Map<String, Object> map) {
      super(map);
      Integer power = SerializableMeta.getObject(Integer.class, map, FLIGHT.BUKKIT, true);
      if (power != null) {
         this.setPower(power);
      }

      Iterable<?> effects = SerializableMeta.getObject(Iterable.class, map, EXPLOSIONS.BUKKIT, true);
      this.safelyAddEffects(effects);
   }

   @Override
   public boolean hasEffects() {
      return this.effects != null && !this.effects.isEmpty();
   }

   void safelyAddEffects(Iterable<?> collection) {
      if (collection != null && (!(collection instanceof Collection) || !((Collection)collection).isEmpty())) {
         List<org.bukkit.FireworkEffect> effects = this.effects;
         if (effects == null) {
            effects = this.effects = new ArrayList<>();
         }

         for (Object obj : collection) {
            if (!(obj instanceof org.bukkit.FireworkEffect)) {
               throw new IllegalArgumentException(obj + " in " + collection + " is not a FireworkEffect");
            }

            effects.add((org.bukkit.FireworkEffect)obj);
         }
      }
   }

   @Override
   void applyToItem(NBTTagCompound itemTag) {
      super.applyToItem(itemTag);
      if (!this.isFireworkEmpty()) {
         NBTTagCompound fireworks = itemTag.getCompound(FIREWORKS.NBT);
         itemTag.set(FIREWORKS.NBT, fireworks);
         if (this.hasEffects()) {
            NBTTagList effects = new NBTTagList();

            for (org.bukkit.FireworkEffect effect : this.effects) {
               effects.add(getExplosion(effect));
            }

            if (effects.size() > 0) {
               fireworks.set(EXPLOSIONS.NBT, effects);
            }
         }

         if (this.hasPower()) {
            fireworks.setByte(FLIGHT.NBT, (byte)this.power);
         }
      }
   }

   static void addColors(NBTTagCompound compound, CraftMetaItem.ItemMetaKey key, List<org.bukkit.Color> colors) {
      if (!colors.isEmpty()) {
         int[] colorArray = new int[colors.size()];
         int i = 0;

         for (org.bukkit.Color color : colors) {
            colorArray[i++] = color.asRGB();
         }

         compound.setIntArray(key.NBT, colorArray);
      }
   }

   @Override
   boolean applicableTo(org.bukkit.Material type) {
      return type == org.bukkit.Material.FIREWORK;
   }

   @Override
   boolean isEmpty() {
      return super.isEmpty() && this.isFireworkEmpty();
   }

   boolean isFireworkEmpty() {
      return !this.hasEffects() && !this.hasPower();
   }

   boolean hasPower() {
      return this.power != 0;
   }

   @Override
   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaFirework)) {
         return true;
      } else {
         CraftMetaFirework that = (CraftMetaFirework)meta;
         return (this.hasPower() ? that.hasPower() && this.power == that.power : !that.hasPower())
            && (this.hasEffects() ? that.hasEffects() && this.effects.equals(that.effects) : !that.hasEffects());
      }
   }

   @Override
   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaFirework || this.isFireworkEmpty());
   }

   @Override
   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasPower()) {
         hash = 61 * hash + this.power;
      }

      if (this.hasEffects()) {
         hash = 61 * hash + 13 * this.effects.hashCode();
      }

      return hash != original ? CraftMetaFirework.class.hashCode() ^ hash : hash;
   }

   @Override
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasEffects()) {
         builder.put(EXPLOSIONS.BUKKIT, ImmutableList.copyOf(this.effects));
      }

      if (this.hasPower()) {
         builder.put(FLIGHT.BUKKIT, this.power);
      }

      return builder;
   }

   public CraftMetaFirework clone() {
      CraftMetaFirework meta = (CraftMetaFirework)super.clone();
      if (this.effects != null) {
         meta.effects = new ArrayList<>(this.effects);
      }

      return meta;
   }

   @Override
   public void addEffect(org.bukkit.FireworkEffect effect) {
      Validate.notNull(effect, "Effect cannot be null");
      if (this.effects == null) {
         this.effects = new ArrayList<>();
      }

      this.effects.add(effect);
   }

   @Override
   public void addEffects(org.bukkit.FireworkEffect... effects) {
      Validate.notNull(effects, "Effects cannot be null");
      if (effects.length != 0) {
         List<org.bukkit.FireworkEffect> list = this.effects;
         if (list == null) {
            list = this.effects = new ArrayList<>();
         }

         for (org.bukkit.FireworkEffect effect : effects) {
            Validate.notNull(effect, "Effect cannot be null");
            list.add(effect);
         }
      }
   }

   @Override
   public void addEffects(Iterable<org.bukkit.FireworkEffect> effects) {
      Validate.notNull(effects, "Effects cannot be null");
      this.safelyAddEffects(effects);
   }

   @Override
   public List<org.bukkit.FireworkEffect> getEffects() {
      return this.effects == null ? ImmutableList.of() : ImmutableList.copyOf(this.effects);
   }

   @Override
   public int getEffectsSize() {
      return this.effects == null ? 0 : this.effects.size();
   }

   @Override
   public void removeEffect(int index) {
      if (this.effects == null) {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      } else {
         this.effects.remove(index);
      }
   }

   @Override
   public void clearEffects() {
      this.effects = null;
   }

   @Override
   public int getPower() {
      return this.power;
   }

   @Override
   public void setPower(int power) {
      Validate.isTrue(power >= 0, "Power cannot be less than zero: ", power);
      Validate.isTrue(power < 128, "Power cannot be more than 127: ", power);
      this.power = power;
   }
}
