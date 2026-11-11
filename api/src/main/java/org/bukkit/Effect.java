package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;

public enum Effect {
   CLICK2(1000, org.bukkit.Effect.Type.SOUND),
   CLICK1(1001, org.bukkit.Effect.Type.SOUND),
   BOW_FIRE(1002, org.bukkit.Effect.Type.SOUND),
   DOOR_TOGGLE(1003, org.bukkit.Effect.Type.SOUND),
   EXTINGUISH(1004, org.bukkit.Effect.Type.SOUND),
   RECORD_PLAY(1005, org.bukkit.Effect.Type.SOUND, org.bukkit.Material.class),
   GHAST_SHRIEK(1007, org.bukkit.Effect.Type.SOUND),
   GHAST_SHOOT(1008, org.bukkit.Effect.Type.SOUND),
   BLAZE_SHOOT(1009, org.bukkit.Effect.Type.SOUND),
   ZOMBIE_CHEW_WOODEN_DOOR(1010, org.bukkit.Effect.Type.SOUND),
   ZOMBIE_CHEW_IRON_DOOR(1011, org.bukkit.Effect.Type.SOUND),
   ZOMBIE_DESTROY_DOOR(1012, org.bukkit.Effect.Type.SOUND),
   SMOKE(2000, org.bukkit.Effect.Type.VISUAL, BlockFace.class),
   STEP_SOUND(2001, org.bukkit.Effect.Type.SOUND, org.bukkit.Material.class),
   POTION_BREAK(2002, org.bukkit.Effect.Type.VISUAL, Potion.class),
   ENDER_SIGNAL(2003, org.bukkit.Effect.Type.VISUAL),
   MOBSPAWNER_FLAMES(2004, org.bukkit.Effect.Type.VISUAL),
   FIREWORKS_SPARK("fireworksSpark", org.bukkit.Effect.Type.PARTICLE),
   CRIT("crit", org.bukkit.Effect.Type.PARTICLE),
   MAGIC_CRIT("magicCrit", org.bukkit.Effect.Type.PARTICLE),
   POTION_SWIRL("mobSpell", org.bukkit.Effect.Type.PARTICLE),
   POTION_SWIRL_TRANSPARENT("mobSpellAmbient", org.bukkit.Effect.Type.PARTICLE),
   SPELL("spell", org.bukkit.Effect.Type.PARTICLE),
   INSTANT_SPELL("instantSpell", org.bukkit.Effect.Type.PARTICLE),
   WITCH_MAGIC("witchMagic", org.bukkit.Effect.Type.PARTICLE),
   NOTE("note", org.bukkit.Effect.Type.PARTICLE),
   PORTAL("portal", org.bukkit.Effect.Type.PARTICLE),
   FLYING_GLYPH("enchantmenttable", org.bukkit.Effect.Type.PARTICLE),
   FLAME("flame", org.bukkit.Effect.Type.PARTICLE),
   LAVA_POP("lava", org.bukkit.Effect.Type.PARTICLE),
   FOOTSTEP("footstep", org.bukkit.Effect.Type.PARTICLE),
   SPLASH("splash", org.bukkit.Effect.Type.PARTICLE),
   PARTICLE_SMOKE("smoke", org.bukkit.Effect.Type.PARTICLE),
   EXPLOSION_HUGE("hugeexplosion", org.bukkit.Effect.Type.PARTICLE),
   EXPLOSION_LARGE("largeexplode", org.bukkit.Effect.Type.PARTICLE),
   EXPLOSION("explode", org.bukkit.Effect.Type.PARTICLE),
   VOID_FOG("depthsuspend", org.bukkit.Effect.Type.PARTICLE),
   SMALL_SMOKE("townaura", org.bukkit.Effect.Type.PARTICLE),
   CLOUD("cloud", org.bukkit.Effect.Type.PARTICLE),
   COLOURED_DUST("reddust", org.bukkit.Effect.Type.PARTICLE),
   SNOWBALL_BREAK("snowballpoof", org.bukkit.Effect.Type.PARTICLE),
   WATERDRIP("dripWater", org.bukkit.Effect.Type.PARTICLE),
   LAVADRIP("dripLava", org.bukkit.Effect.Type.PARTICLE),
   SNOW_SHOVEL("snowshovel", org.bukkit.Effect.Type.PARTICLE),
   SLIME("slime", org.bukkit.Effect.Type.PARTICLE),
   HEART("heart", org.bukkit.Effect.Type.PARTICLE),
   VILLAGER_THUNDERCLOUD("angryVillager", org.bukkit.Effect.Type.PARTICLE),
   HAPPY_VILLAGER("happyVillager", org.bukkit.Effect.Type.PARTICLE),
   LARGE_SMOKE("largesmoke", org.bukkit.Effect.Type.PARTICLE),
   ITEM_BREAK("iconcrack", org.bukkit.Effect.Type.PARTICLE, org.bukkit.Material.class),
   TILE_BREAK("blockcrack", org.bukkit.Effect.Type.PARTICLE, MaterialData.class),
   TILE_DUST("blockdust", org.bukkit.Effect.Type.PARTICLE, MaterialData.class);

   private final int id;
   private final org.bukkit.Effect.Type type;
   private final Class<?> data;
   private static final Map<Integer, org.bukkit.Effect> BY_ID = Maps.newHashMap();
   private static final Map<String, org.bukkit.Effect> BY_NAME = Maps.newHashMap();
   private final String particleName;

   private Effect(int id, org.bukkit.Effect.Type type) {
      this(id, type, null);
   }

   private Effect(int id, org.bukkit.Effect.Type type, Class<?> data) {
      this.id = id;
      this.type = type;
      this.data = data;
      this.particleName = null;
   }

   private Effect(String particleName, org.bukkit.Effect.Type type, Class<?> data) {
      this.particleName = particleName;
      this.type = type;
      this.id = 0;
      this.data = data;
   }

   private Effect(String particleName, org.bukkit.Effect.Type type) {
      this.particleName = particleName;
      this.type = type;
      this.id = 0;
      this.data = null;
   }

   @Deprecated
   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.particleName;
   }

   public org.bukkit.Effect.Type getType() {
      return this.type;
   }

   public Class<?> getData() {
      return this.data;
   }

   @Deprecated
   public static org.bukkit.Effect getById(int id) {
      return BY_ID.get(id);
   }

   public static org.bukkit.Effect getByName(String name) {
      return BY_NAME.get(name);
   }

   static {
      for (org.bukkit.Effect effect : values()) {
         if (effect.type != org.bukkit.Effect.Type.PARTICLE) {
            BY_ID.put(effect.id, effect);
         }
      }

      for (org.bukkit.Effect effectx : values()) {
         if (effectx.type == org.bukkit.Effect.Type.PARTICLE) {
            BY_NAME.put(effectx.particleName, effectx);
         }
      }
   }

   public static enum Type {
      SOUND,
      VISUAL,
      PARTICLE;
   }
}
