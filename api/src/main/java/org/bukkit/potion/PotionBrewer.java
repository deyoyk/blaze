package org.bukkit.potion;

import java.util.Collection;

public interface PotionBrewer {
   PotionEffect createEffect(PotionEffectType var1, int var2, int var3);

   @Deprecated
   Collection<PotionEffect> getEffectsFromDamage(int var1);
}
