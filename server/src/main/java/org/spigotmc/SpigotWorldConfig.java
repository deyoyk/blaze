package org.spigotmc;

import lol.deyo.blaze.optimizations.FastAntiXray;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpigotWorldConfig {
   private final String worldName;
   private final YamlConfiguration config;
   private boolean verbose;
   public int chunksPerTick;
   public boolean clearChunksOnTick;
   public int cactusModifier;
   public int caneModifier;
   public int melonModifier;
   public int mushroomModifier;
   public int pumpkinModifier;
   public int saplingModifier;
   public int wheatModifier;
   public int wartModifier;
   public double itemMerge;
   public double expMerge;
   public int viewDistance;
   public byte mobSpawnRange;
   public int animalActivationRange = 32;
   public int monsterActivationRange = 32;
   public int miscActivationRange = 16;
   public int playerTrackingRange = 48;
   public int animalTrackingRange = 48;
   public int monsterTrackingRange = 48;
   public int miscTrackingRange = 32;
   public int otherTrackingRange = 64;
   public int hopperTransfer;
   public int hopperCheck;
   public int hopperAmount;
   public boolean randomLightUpdates;
   public boolean saveStructureInfo;
   public int itemDespawnRate;
   public int arrowDespawnRate;
   public boolean antiXray;
   public int engineMode;
   public List<Integer> hiddenBlocks;
   public List<Integer> replaceBlocks;
   public FastAntiXray antiXrayInstance;
   public boolean zombieAggressiveTowardsVillager;
   public boolean nerfSpawnerMobs;
   public boolean enableZombiePigmenPortalSpawns;
   public int maxBulkChunk;
   public int maxCollisionsPerEntity;
   public int dragonDeathSoundRadius;
   public int witherSpawnSoundRadius;
   public int villageSeed;
   public int largeFeatureSeed;
   public float walkExhaustion;
   public float sprintExhaustion;
   public float combatExhaustion;
   public float regenExhaustion;
   public int hangingTickFrequency;
   public int tileMaxTickTime;
   public int entityMaxTickTime;

   public SpigotWorldConfig(String worldName) {
      this.worldName = worldName;
      this.config = org.spigotmc.SpigotConfig.config;
      this.init();
   }

   public void init() {
      this.verbose = this.getBoolean("verbose", true);
      org.spigotmc.SpigotConfig.readConfig(org.spigotmc.SpigotWorldConfig.class, this);
   }

   private void log(String s) {
      if (this.verbose) {
         Bukkit.getLogger().info(s);
      }
   }

   private void set(String path, Object val) {
      this.config.set("world-settings.default." + path, val);
   }

   private boolean getBoolean(String path, boolean def) {
      this.config.addDefault("world-settings.default." + path, def);
      return this.config.getBoolean("world-settings." + this.worldName + "." + path, this.config.getBoolean("world-settings.default." + path));
   }

   private double getDouble(String path, double def) {
      this.config.addDefault("world-settings.default." + path, def);
      return this.config.getDouble("world-settings." + this.worldName + "." + path, this.config.getDouble("world-settings.default." + path));
   }

   private int getInt(String path, int def) {
      this.config.addDefault("world-settings.default." + path, def);
      return this.config.getInt("world-settings." + this.worldName + "." + path, this.config.getInt("world-settings.default." + path));
   }

   private <T> List getList(String path, T def) {
      this.config.addDefault("world-settings.default." + path, def);
      return this.config.getList("world-settings." + this.worldName + "." + path, this.config.getList("world-settings.default." + path));
   }

   private String getString(String path, String def) {
      this.config.addDefault("world-settings.default." + path, def);
      return this.config.getString("world-settings." + this.worldName + "." + path, this.config.getString("world-settings.default." + path));
   }

   private void chunksPerTick() {
      this.chunksPerTick = this.getInt("chunks-per-tick", 650);
      this.clearChunksOnTick = this.getBoolean("clear-tick-list", false);
   }

   private int getAndValidateGrowth(String crop) {
      int modifier = this.getInt("growth." + crop.toLowerCase() + "-modifier", 100);
      if (modifier == 0) {
         modifier = 100;
      }

      return modifier;
   }

   private void growthModifiers() {
      this.cactusModifier = this.getAndValidateGrowth("Cactus");
      this.caneModifier = this.getAndValidateGrowth("Cane");
      this.melonModifier = this.getAndValidateGrowth("Melon");
      this.mushroomModifier = this.getAndValidateGrowth("Mushroom");
      this.pumpkinModifier = this.getAndValidateGrowth("Pumpkin");
      this.saplingModifier = this.getAndValidateGrowth("Sapling");
      this.wheatModifier = this.getAndValidateGrowth("Wheat");
      this.wartModifier = this.getAndValidateGrowth("NetherWart");
   }

   private void itemMerge() {
      this.itemMerge = this.getDouble("merge-radius.item", 2.5);
   }

   private void expMerge() {
      this.expMerge = this.getDouble("merge-radius.exp", 3.0);
   }

   private void viewDistance() {
      this.viewDistance = this.getInt("view-distance", Bukkit.getViewDistance());
   }

   private void mobSpawnRange() {
      this.mobSpawnRange = (byte)this.getInt("mob-spawn-range", 4);
   }

   private void activationRange() {
      this.animalActivationRange = this.getInt("entity-activation-range.animals", this.animalActivationRange);
      this.monsterActivationRange = this.getInt("entity-activation-range.monsters", this.monsterActivationRange);
      this.miscActivationRange = this.getInt("entity-activation-range.misc", this.miscActivationRange);
   }

   private void trackingRange() {
      this.playerTrackingRange = this.getInt("entity-tracking-range.players", this.playerTrackingRange);
      this.animalTrackingRange = this.getInt("entity-tracking-range.animals", this.animalTrackingRange);
      this.monsterTrackingRange = this.getInt("entity-tracking-range.monsters", this.monsterTrackingRange);
      this.miscTrackingRange = this.getInt("entity-tracking-range.misc", this.miscTrackingRange);
      this.otherTrackingRange = this.getInt("entity-tracking-range.other", this.otherTrackingRange);
   }

   private void hoppers() {
      this.hopperTransfer = this.getInt("ticks-per.hopper-transfer", 8);
      this.hopperCheck = this.getInt("ticks-per.hopper-check", this.hopperTransfer);
      this.hopperAmount = this.getInt("hopper-amount", 1);
   }

   private void lightUpdates() {
      this.randomLightUpdates = this.getBoolean("random-light-updates", false);
   }

   private void structureInfo() {
      this.saveStructureInfo = this.getBoolean("save-structure-info", true);
   }

   private void itemDespawnRate() {
      this.itemDespawnRate = this.getInt("item-despawn-rate", 6000);
   }

   private void arrowDespawnRate() {
      this.arrowDespawnRate = this.getInt("arrow-despawn-rate", 1200);
   }

   private void antiXray() {
      this.antiXray = this.getBoolean("anti-xray.enabled", true);
      this.engineMode = this.getInt("anti-xray.engine-mode", 1);
      if (org.spigotmc.SpigotConfig.version < 5) {
         this.set("anti-xray.blocks", null);
      }

      this.hiddenBlocks = this.getList("anti-xray.hide-blocks", Arrays.asList(14, 15, 16, 21, 48, 49, 54, 56, 73, 74, 82, 129, 130));
      this.replaceBlocks = this.getList("anti-xray.replace-blocks", Arrays.asList(1, 5));
      this.antiXrayInstance = new FastAntiXray(this);
   }

   private void zombieAggressiveTowardsVillager() {
      this.zombieAggressiveTowardsVillager = this.getBoolean("zombie-aggressive-towards-villager", true);
   }

   private void nerfSpawnerMobs() {
      this.nerfSpawnerMobs = this.getBoolean("nerf-spawner-mobs", false);
   }

   private void enableZombiePigmenPortalSpawns() {
      this.enableZombiePigmenPortalSpawns = this.getBoolean("enable-zombie-pigmen-portal-spawns", true);
   }

   private void bulkChunkCount() {
      this.maxBulkChunk = this.getInt("max-bulk-chunks", 10);
   }

   private void maxEntityCollision() {
      this.maxCollisionsPerEntity = this.getInt("max-entity-collisions", 8);
   }

   private void keepDragonDeathPerWorld() {
      this.dragonDeathSoundRadius = this.getInt("dragon-death-sound-radius", 0);
   }

   private void witherSpawnSoundRadius() {
      this.witherSpawnSoundRadius = this.getInt("wither-spawn-sound-radius", 0);
   }

   private void initWorldGenSeeds() {
      this.villageSeed = this.getInt("seed-village", 10387312);
      this.largeFeatureSeed = this.getInt("seed-feature", 14357617);
   }

   private void initHunger() {
      this.walkExhaustion = (float)this.getDouble("hunger.walk-exhaustion", 0.2);
      this.sprintExhaustion = (float)this.getDouble("hunger.sprint-exhaustion", 0.8);
      this.combatExhaustion = (float)this.getDouble("hunger.combat-exhaustion", 0.3);
      this.regenExhaustion = (float)this.getDouble("hunger.regen-exhaustion", 3.0);
   }

   private void hangingTickFrequency() {
      this.hangingTickFrequency = this.getInt("hanging-tick-frequency", 100);
   }

   private void maxTickTimes() {
      this.tileMaxTickTime = this.getInt("max-tick-time.tile", 50);
      this.entityMaxTickTime = this.getInt("max-tick-time.entity", 50);
   }
}
