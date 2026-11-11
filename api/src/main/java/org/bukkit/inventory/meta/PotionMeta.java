package org.bukkit.inventory.meta;

import java.util.List;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public interface PotionMeta extends ItemMeta {
   boolean hasCustomEffects();

   List<PotionEffect> getCustomEffects();

   boolean addCustomEffect(PotionEffect var1, boolean var2);

   boolean removeCustomEffect(PotionEffectType var1);

   boolean hasCustomEffect(PotionEffectType var1);

   boolean setMainEffect(PotionEffectType var1);

   boolean clearCustomEffects();

   PotionMeta clone();
}
