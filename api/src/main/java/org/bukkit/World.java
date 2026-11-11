package org.bukkit;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.util.Vector;

public interface World extends PluginMessageRecipient, Metadatable {
   Block getBlockAt(int var1, int var2, int var3);

   Block getBlockAt(org.bukkit.Location var1);

   @Deprecated
   int getBlockTypeIdAt(int var1, int var2, int var3);

   @Deprecated
   int getBlockTypeIdAt(org.bukkit.Location var1);

   int getHighestBlockYAt(int var1, int var2);

   int getHighestBlockYAt(org.bukkit.Location var1);

   Block getHighestBlockAt(int var1, int var2);

   Block getHighestBlockAt(org.bukkit.Location var1);

   org.bukkit.Chunk getChunkAt(int var1, int var2);

   org.bukkit.Chunk getChunkAt(org.bukkit.Location var1);

   org.bukkit.Chunk getChunkAt(Block var1);

   void getChunkAtAsync(int var1, int var2, org.bukkit.World.ChunkLoadCallback var3);

   void getChunkAtAsync(org.bukkit.Location var1, org.bukkit.World.ChunkLoadCallback var2);

   void getChunkAtAsync(Block var1, org.bukkit.World.ChunkLoadCallback var2);

   boolean isChunkLoaded(org.bukkit.Chunk var1);

   org.bukkit.Chunk[] getLoadedChunks();

   void loadChunk(org.bukkit.Chunk var1);

   boolean isChunkLoaded(int var1, int var2);

   boolean isChunkInUse(int var1, int var2);

   void loadChunk(int var1, int var2);

   boolean loadChunk(int var1, int var2, boolean var3);

   boolean unloadChunk(org.bukkit.Chunk var1);

   boolean unloadChunk(int var1, int var2);

   boolean unloadChunk(int var1, int var2, boolean var3);

   boolean unloadChunk(int var1, int var2, boolean var3, boolean var4);

   boolean unloadChunkRequest(int var1, int var2);

   boolean unloadChunkRequest(int var1, int var2, boolean var3);

   boolean regenerateChunk(int var1, int var2);

   @Deprecated
   boolean refreshChunk(int var1, int var2);

   Item dropItem(org.bukkit.Location var1, ItemStack var2);

   Item dropItemNaturally(org.bukkit.Location var1, ItemStack var2);

   Arrow spawnArrow(org.bukkit.Location var1, Vector var2, float var3, float var4);

   boolean generateTree(org.bukkit.Location var1, org.bukkit.TreeType var2);

   boolean generateTree(org.bukkit.Location var1, org.bukkit.TreeType var2, org.bukkit.BlockChangeDelegate var3);

   Entity spawnEntity(org.bukkit.Location var1, EntityType var2);

   @Deprecated
   LivingEntity spawnCreature(org.bukkit.Location var1, EntityType var2);

   @Deprecated
   LivingEntity spawnCreature(org.bukkit.Location var1, CreatureType var2);

   LightningStrike strikeLightning(org.bukkit.Location var1);

   LightningStrike strikeLightningEffect(org.bukkit.Location var1);

   List<Entity> getEntities();

   List<LivingEntity> getLivingEntities();

   @Deprecated
   <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... var1);

   <T extends Entity> Collection<T> getEntitiesByClass(Class<T> var1);

   Collection<Entity> getEntitiesByClasses(Class<?>... var1);

   List<Player> getPlayers();

   Collection<Entity> getNearbyEntities(org.bukkit.Location var1, double var2, double var4, double var6);

   String getName();

   UUID getUID();

   org.bukkit.Location getSpawnLocation();

   boolean setSpawnLocation(int var1, int var2, int var3);

   long getTime();

   void setTime(long var1);

   long getFullTime();

   void setFullTime(long var1);

   boolean hasStorm();

   void setStorm(boolean var1);

   int getWeatherDuration();

   void setWeatherDuration(int var1);

   boolean isThundering();

   void setThundering(boolean var1);

   int getThunderDuration();

   void setThunderDuration(int var1);

   boolean createExplosion(double var1, double var3, double var5, float var7);

   boolean createExplosion(double var1, double var3, double var5, float var7, boolean var8);

   boolean createExplosion(double var1, double var3, double var5, float var7, boolean var8, boolean var9);

   boolean createExplosion(org.bukkit.Location var1, float var2);

   boolean createExplosion(org.bukkit.Location var1, float var2, boolean var3);

   org.bukkit.World.Environment getEnvironment();

   long getSeed();

   boolean getPVP();

   void setPVP(boolean var1);

   ChunkGenerator getGenerator();

   void save();

   List<BlockPopulator> getPopulators();

   <T extends Entity> T spawn(org.bukkit.Location var1, Class<T> var2) throws IllegalArgumentException;

   @Deprecated
   FallingBlock spawnFallingBlock(org.bukkit.Location var1, org.bukkit.Material var2, byte var3) throws IllegalArgumentException;

   @Deprecated
   FallingBlock spawnFallingBlock(org.bukkit.Location var1, int var2, byte var3) throws IllegalArgumentException;

   void playEffect(org.bukkit.Location var1, org.bukkit.Effect var2, int var3);

   void playEffect(org.bukkit.Location var1, org.bukkit.Effect var2, int var3, int var4);

   <T> void playEffect(org.bukkit.Location var1, org.bukkit.Effect var2, T var3);

   <T> void playEffect(org.bukkit.Location var1, org.bukkit.Effect var2, T var3, int var4);

   org.bukkit.ChunkSnapshot getEmptyChunkSnapshot(int var1, int var2, boolean var3, boolean var4);

   void setSpawnFlags(boolean var1, boolean var2);

   boolean getAllowAnimals();

   boolean getAllowMonsters();

   Biome getBiome(int var1, int var2);

   void setBiome(int var1, int var2, Biome var3);

   double getTemperature(int var1, int var2);

   double getHumidity(int var1, int var2);

   int getMaxHeight();

   int getSeaLevel();

   boolean getKeepSpawnInMemory();

   void setKeepSpawnInMemory(boolean var1);

   boolean isAutoSave();

   void setAutoSave(boolean var1);

   void setDifficulty(org.bukkit.Difficulty var1);

   org.bukkit.Difficulty getDifficulty();

   File getWorldFolder();

   org.bukkit.WorldType getWorldType();

   boolean canGenerateStructures();

   long getTicksPerAnimalSpawns();

   void setTicksPerAnimalSpawns(int var1);

   long getTicksPerMonsterSpawns();

   void setTicksPerMonsterSpawns(int var1);

   int getMonsterSpawnLimit();

   void setMonsterSpawnLimit(int var1);

   int getAnimalSpawnLimit();

   void setAnimalSpawnLimit(int var1);

   int getWaterAnimalSpawnLimit();

   void setWaterAnimalSpawnLimit(int var1);

   int getAmbientSpawnLimit();

   void setAmbientSpawnLimit(int var1);

   void playSound(org.bukkit.Location var1, org.bukkit.Sound var2, float var3, float var4);

   String[] getGameRules();

   String getGameRuleValue(String var1);

   boolean setGameRuleValue(String var1, String var2);

   boolean isGameRule(String var1);

   org.bukkit.World.Spigot spigot();

   org.bukkit.WorldBorder getWorldBorder();

   public interface ChunkLoadCallback {
      void onLoad(org.bukkit.Chunk var1);
   }

   public static enum Environment {
      NORMAL(0),
      NETHER(-1),
      THE_END(1);

      private final int id;
      private static final Map<Integer, org.bukkit.World.Environment> lookup = new HashMap<>();

      private Environment(int id) {
         this.id = id;
      }

      @Deprecated
      public int getId() {
         return this.id;
      }

      @Deprecated
      public static org.bukkit.World.Environment getEnvironment(int id) {
         return lookup.get(id);
      }

      static {
         for (org.bukkit.World.Environment env : values()) {
            lookup.put(env.getId(), env);
         }
      }
   }

   public static class Spigot {
      public void playEffect(org.bukkit.Location location, org.bukkit.Effect effect) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void playEffect(
         org.bukkit.Location location,
         org.bukkit.Effect effect,
         int id,
         int data,
         float offsetX,
         float offsetY,
         float offsetZ,
         float speed,
         int particleCount,
         int radius
      ) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public LightningStrike strikeLightning(org.bukkit.Location loc, boolean isSilent) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public LightningStrike strikeLightningEffect(org.bukkit.Location loc, boolean isSilent) {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
