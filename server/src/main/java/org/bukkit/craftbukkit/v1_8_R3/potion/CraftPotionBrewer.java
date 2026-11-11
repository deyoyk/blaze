package org.bukkit.craftbukkit.v1_8_R3.potion;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_8_R3.MobEffect;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CraftPotionBrewer implements PotionBrewer {
   private static final Map<Integer, Collection<PotionEffect>> cache = Maps.newHashMap();

   @Override
   public Collection<PotionEffect> getEffectsFromDamage(int damage) {
      if (cache.containsKey(damage)) {
         return cache.get(damage);
      } else {
         List<?> mcEffects = net.minecraft.server.v1_8_R3.PotionBrewer.getEffects(damage, false);
         List<PotionEffect> effects = new ArrayList<>();
         if (mcEffects == null) {
            return effects;
         } else {
            for (Object raw : mcEffects) {
               if (raw instanceof MobEffect) {
                  MobEffect mcEffect = (MobEffect)raw;
                  PotionEffect effect = new PotionEffect(PotionEffectType.getById(mcEffect.getEffectId()), mcEffect.getDuration(), mcEffect.getAmplifier());
                  effects.add(effect);
               }
            }

            cache.put(damage, effects);
            return effects;
         }
      }
   }

   @Override
   public PotionEffect createEffect(PotionEffectType potion, int duration, int amplifier) {
      return new PotionEffect(potion, potion.isInstant() ? 1 : (int)(duration * potion.getDurationModifier()), amplifier);
   }
}
