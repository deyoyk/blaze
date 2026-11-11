package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;

public enum EntityEffect {
   HURT(2),
   DEATH(3),
   WOLF_SMOKE(6),
   WOLF_HEARTS(7),
   WOLF_SHAKE(8),
   SHEEP_EAT(10),
   IRON_GOLEM_ROSE(11),
   VILLAGER_HEART(12),
   VILLAGER_ANGRY(13),
   VILLAGER_HAPPY(14),
   WITCH_MAGIC(15),
   ZOMBIE_TRANSFORM(16),
   FIREWORK_EXPLODE(17);

   private final byte data;
   private static final Map<Byte, org.bukkit.EntityEffect> BY_DATA = Maps.newHashMap();

   private EntityEffect(int data) {
      this.data = (byte)data;
   }

   @Deprecated
   public byte getData() {
      return this.data;
   }

   @Deprecated
   public static org.bukkit.EntityEffect getByData(byte data) {
      return BY_DATA.get(data);
   }

   static {
      for (org.bukkit.EntityEffect entityEffect : values()) {
         BY_DATA.put(entityEffect.data, entityEffect);
      }
   }
}
