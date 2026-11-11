package org.bukkit.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public interface LivingEntity extends Entity, Damageable, ProjectileSource {
   double getEyeHeight();

   double getEyeHeight(boolean var1);

   org.bukkit.Location getEyeLocation();

   @Deprecated
   List<Block> getLineOfSight(HashSet<Byte> var1, int var2);

   List<Block> getLineOfSight(Set<org.bukkit.Material> var1, int var2);

   @Deprecated
   Block getTargetBlock(HashSet<Byte> var1, int var2);

   Block getTargetBlock(Set<org.bukkit.Material> var1, int var2);

   @Deprecated
   List<Block> getLastTwoTargetBlocks(HashSet<Byte> var1, int var2);

   List<Block> getLastTwoTargetBlocks(Set<org.bukkit.Material> var1, int var2);

   @Deprecated
   Egg throwEgg();

   @Deprecated
   Snowball throwSnowball();

   @Deprecated
   Arrow shootArrow();

   int getRemainingAir();

   void setRemainingAir(int var1);

   int getMaximumAir();

   void setMaximumAir(int var1);

   int getMaximumNoDamageTicks();

   void setMaximumNoDamageTicks(int var1);

   double getLastDamage();

   void setLastDamage(double var1);

   @Deprecated
   int _INVALID_getLastDamage();

   @Deprecated
   void _INVALID_setLastDamage(int var1);

   int getNoDamageTicks();

   void setNoDamageTicks(int var1);

   Player getKiller();

   boolean addPotionEffect(PotionEffect var1);

   boolean addPotionEffect(PotionEffect var1, boolean var2);

   boolean addPotionEffect(PotionEffect var1, boolean var2, PotionEffectAddEvent.EffectCause var3);

   boolean addPotionEffects(Collection<PotionEffect> var1);

   boolean hasPotionEffect(PotionEffectType var1);

   void removePotionEffect(PotionEffectType var1);

   Collection<PotionEffect> getActivePotionEffects();

   boolean hasLineOfSight(Entity var1);

   boolean getRemoveWhenFarAway();

   void setRemoveWhenFarAway(boolean var1);

   EntityEquipment getEquipment();

   boolean getCanPickupItems();

   void setCanPickupItems(boolean var1);

   boolean isLeashed();

   Entity getLeashHolder() throws IllegalStateException;

   boolean setLeashHolder(Entity var1);
}
