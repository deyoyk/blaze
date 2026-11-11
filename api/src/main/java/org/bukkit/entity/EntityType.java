package org.bukkit.entity;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;

public enum EntityType {
   DROPPED_ITEM("Item", Item.class, 1, false),
   EXPERIENCE_ORB("XPOrb", ExperienceOrb.class, 2),
   LEASH_HITCH("LeashKnot", LeashHitch.class, 8),
   PAINTING("Painting", Painting.class, 9),
   ARROW("Arrow", Arrow.class, 10),
   SNOWBALL("Snowball", Snowball.class, 11),
   FIREBALL("Fireball", LargeFireball.class, 12),
   SMALL_FIREBALL("SmallFireball", SmallFireball.class, 13),
   ENDER_PEARL("ThrownEnderpearl", EnderPearl.class, 14),
   ENDER_SIGNAL("EyeOfEnderSignal", EnderSignal.class, 15),
   THROWN_EXP_BOTTLE("ThrownExpBottle", ThrownExpBottle.class, 17),
   ITEM_FRAME("ItemFrame", ItemFrame.class, 18),
   WITHER_SKULL("WitherSkull", WitherSkull.class, 19),
   PRIMED_TNT("PrimedTnt", TNTPrimed.class, 20),
   FALLING_BLOCK("FallingSand", FallingBlock.class, 21, false),
   FIREWORK("FireworksRocketEntity", Firework.class, 22, false),
   ARMOR_STAND("ArmorStand", ArmorStand.class, 30, false),
   MINECART_COMMAND("MinecartCommandBlock", CommandMinecart.class, 40),
   BOAT("Boat", Boat.class, 41),
   MINECART("MinecartRideable", RideableMinecart.class, 42),
   MINECART_CHEST("MinecartChest", org.bukkit.entity.minecart.StorageMinecart.class, 43),
   MINECART_FURNACE("MinecartFurnace", org.bukkit.entity.minecart.PoweredMinecart.class, 44),
   MINECART_TNT("MinecartTNT", ExplosiveMinecart.class, 45),
   MINECART_HOPPER("MinecartHopper", HopperMinecart.class, 46),
   MINECART_MOB_SPAWNER("MinecartMobSpawner", SpawnerMinecart.class, 47),
   CREEPER("Creeper", Creeper.class, 50, "Creeper"),
   SKELETON("Skeleton", Skeleton.class, 51, "Skeleton"),
   SPIDER("Spider", Spider.class, 52, "Spider"),
   GIANT("Giant", Giant.class, 53, "Giant"),
   ZOMBIE("Zombie", Zombie.class, 54, "Zombie"),
   SLIME("Slime", Slime.class, 55, "Slime"),
   GHAST("Ghast", Ghast.class, 56, "Ghast"),
   PIG_ZOMBIE("PigZombie", PigZombie.class, 57, "Zombie Pigman"),
   ENDERMAN("Enderman", Enderman.class, 58, "Enderman"),
   CAVE_SPIDER("CaveSpider", CaveSpider.class, 59, "Cave Spider"),
   SILVERFISH("Silverfish", Silverfish.class, 60, "Silverfish"),
   BLAZE("Blaze", Blaze.class, 61, "Blaze"),
   MAGMA_CUBE("LavaSlime", MagmaCube.class, 62, "Magma Cube"),
   ENDER_DRAGON("EnderDragon", EnderDragon.class, 63, "Enderdragon"),
   WITHER("WitherBoss", Wither.class, 64, "Wither"),
   BAT("Bat", Bat.class, 65, "Bat"),
   WITCH("Witch", Witch.class, 66, "Witch"),
   ENDERMITE("Endermite", Endermite.class, 67, "Endermite"),
   GUARDIAN("Guardian", Guardian.class, 68, "Guardian"),
   PIG("Pig", Pig.class, 90, "Pig"),
   SHEEP("Sheep", Sheep.class, 91, "Sheep"),
   COW("Cow", Cow.class, 92, "Cow"),
   CHICKEN("Chicken", Chicken.class, 93, "Chicken"),
   SQUID("Squid", Squid.class, 94, "Squid"),
   WOLF("Wolf", Wolf.class, 95, "Wolf"),
   MUSHROOM_COW("MushroomCow", MushroomCow.class, 96, "Mooshroom"),
   SNOWMAN("SnowMan", Snowman.class, 97, "Snowman"),
   OCELOT("Ozelot", Ocelot.class, 98, "Ocelot"),
   IRON_GOLEM("VillagerGolem", IronGolem.class, 99, "Iron Golem"),
   HORSE("EntityHorse", Horse.class, 100, "Horse"),
   RABBIT("Rabbit", Rabbit.class, 101, "Rabbit"),
   VILLAGER("Villager", Villager.class, 120, "Villager"),
   ENDER_CRYSTAL("EnderCrystal", EnderCrystal.class, 200),
   SPLASH_POTION(null, ThrownPotion.class, -1, false),
   EGG(null, Egg.class, -1, false),
   FISHING_HOOK(null, Fish.class, -1, false),
   LIGHTNING(null, LightningStrike.class, -1, false),
   WEATHER(null, Weather.class, -1, false),
   PLAYER(null, Player.class, -1, false),
   COMPLEX_PART(null, ComplexEntityPart.class, -1, false),
   UNKNOWN(null, null, -1, false);

   private String name;
   private Class<? extends Entity> clazz;
   private short typeId;
   private boolean independent;
   private boolean living;
   private String displayName;
   private static final Map<String, EntityType> NAME_MAP = new HashMap<>();
   private static final Map<String, EntityType> DISPLAYNAME_MAP = new HashMap<>();
   private static final Map<Short, EntityType> ID_MAP = new HashMap<>();

   private EntityType(String name, Class<? extends Entity> clazz, int typeId) {
      this(name, clazz, typeId, true, "");
   }

   private EntityType(String name, Class<? extends Entity> clazz, int typeId, String displayName) {
      this(name, clazz, typeId, true, displayName);
   }

   private EntityType(String name, Class<? extends Entity> clazz, int typeId, boolean independent) {
      this(name, clazz, typeId, independent, "");
   }

   private EntityType(String name, Class<? extends Entity> clazz, int typeId, boolean independent, String displayName) {
      this.name = name;
      this.clazz = clazz;
      this.typeId = (short)typeId;
      this.independent = independent;
      if (clazz != null) {
         this.living = LivingEntity.class.isAssignableFrom(clazz);
      }

      this.displayName = displayName;
   }

   @Deprecated
   public String getName() {
      return this.name;
   }

   public Class<? extends Entity> getEntityClass() {
      return this.clazz;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   @Deprecated
   public short getTypeId() {
      return this.typeId;
   }

   @Deprecated
   public static EntityType fromName(String name) {
      return name == null ? null : NAME_MAP.get(name.toLowerCase());
   }

   public static EntityType fromDisplayName(String displayName) {
      return displayName == null ? null : DISPLAYNAME_MAP.get(displayName.toLowerCase());
   }

   @Deprecated
   public static EntityType fromId(int id) {
      return id > 32767 ? null : ID_MAP.get((short)id);
   }

   public boolean isSpawnable() {
      return this.independent;
   }

   public boolean isAlive() {
      return this.living;
   }

   static {
      for (EntityType type : values()) {
         if (type.name != null) {
            NAME_MAP.put(type.name.toLowerCase(), type);
         }

         if (type.typeId > 0) {
            ID_MAP.put(type.typeId, type);
         }

         if (type.displayName != null) {
            DISPLAYNAME_MAP.put(type.displayName.toLowerCase(), type);
         }
      }
   }
}
