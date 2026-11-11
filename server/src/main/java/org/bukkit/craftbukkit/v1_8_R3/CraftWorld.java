package org.bukkit.craftbukkit.v1_8_R3;

import com.google.common.base.Preconditions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BlockDiodeAbstract;
import net.minecraft.server.v1_8_R3.BlockLeaves;
import net.minecraft.server.v1_8_R3.BlockLeaves1;
import net.minecraft.server.v1_8_R3.BlockLog1;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.EmptyChunk;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.EntityBlaze;
import net.minecraft.server.v1_8_R3.EntityBoat;
import net.minecraft.server.v1_8_R3.EntityCaveSpider;
import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.EntityCow;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityEgg;
import net.minecraft.server.v1_8_R3.EntityEnderCrystal;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import net.minecraft.server.v1_8_R3.EntityEnderSignal;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.EntityEndermite;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.EntityFireball;
import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.EntityGhast;
import net.minecraft.server.v1_8_R3.EntityGiantZombie;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.EntityHanging;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.EntityLargeFireball;
import net.minecraft.server.v1_8_R3.EntityLeash;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.EntityMinecartChest;
import net.minecraft.server.v1_8_R3.EntityMinecartFurnace;
import net.minecraft.server.v1_8_R3.EntityMinecartHopper;
import net.minecraft.server.v1_8_R3.EntityMinecartMobSpawner;
import net.minecraft.server.v1_8_R3.EntityMinecartRideable;
import net.minecraft.server.v1_8_R3.EntityMinecartTNT;
import net.minecraft.server.v1_8_R3.EntityMushroomCow;
import net.minecraft.server.v1_8_R3.EntityOcelot;
import net.minecraft.server.v1_8_R3.EntityPainting;
import net.minecraft.server.v1_8_R3.EntityPig;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.EntityPotion;
import net.minecraft.server.v1_8_R3.EntityRabbit;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EntitySilverfish;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.EntitySmallFireball;
import net.minecraft.server.v1_8_R3.EntitySnowball;
import net.minecraft.server.v1_8_R3.EntitySnowman;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.EntitySquid;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.EntityThrownExpBottle;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.EntityWitherSkull;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.EnumDifficulty;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateTime;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.WorldGenAcaciaTree;
import net.minecraft.server.v1_8_R3.WorldGenBigTree;
import net.minecraft.server.v1_8_R3.WorldGenForest;
import net.minecraft.server.v1_8_R3.WorldGenForestTree;
import net.minecraft.server.v1_8_R3.WorldGenGroundBush;
import net.minecraft.server.v1_8_R3.WorldGenHugeMushroom;
import net.minecraft.server.v1_8_R3.WorldGenJungleTree;
import net.minecraft.server.v1_8_R3.WorldGenMegaTree;
import net.minecraft.server.v1_8_R3.WorldGenSwampTree;
import net.minecraft.server.v1_8_R3.WorldGenTaiga1;
import net.minecraft.server.v1_8_R3.WorldGenTaiga2;
import net.minecraft.server.v1_8_R3.WorldGenTrees;
import net.minecraft.server.v1_8_R3.WorldGenerator;
import net.minecraft.server.v1_8_R3.WorldProvider;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.BlockWood.EnumLogVariant;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLightningStrike;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.metadata.SpecializedBlockMetadataStore;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Weather;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.util.Vector;
import org.github.paperspigot.exception.ServerInternalException;
import org.spigotmc.AsyncCatcher;

public class CraftWorld implements org.bukkit.World {
   public static final int CUSTOM_DIMENSION_OFFSET = 10;
   private static final Random rand = new Random();
   private final WorldServer world;
   private final CraftServer server = (CraftServer)org.bukkit.Bukkit.getServer();
   private final ChunkGenerator generator;
   private final List<BlockPopulator> populators = new ArrayList<>();
   private final SpecializedBlockMetadataStore blockMetadata = new SpecializedBlockMetadataStore(this);
   private org.bukkit.WorldBorder worldBorder;
   private org.bukkit.World.Environment environment;
   private int monsterSpawn = -1;
   private int animalSpawn = -1;
   private int waterAnimalSpawn = -1;
   private int ambientSpawn = -1;
   private int chunkLoadCount = 0;
   private int chunkGCTickCount;
   private final org.bukkit.World.Spigot spigot = new org.bukkit.World.Spigot() {
      @Override
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
         Validate.notNull(location, "Location cannot be null");
         Validate.notNull(effect, "Effect cannot be null");
         Validate.notNull(location.getWorld(), "World cannot be null");
         Packet packet;
         if (effect.getType() != org.bukkit.Effect.Type.PARTICLE) {
            int packetData = effect.getId();
            packet = new PacketPlayOutWorldEvent(packetData, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), id, false);
         } else {
            EnumParticle particle = null;
            int[] extra = null;

            EnumParticle[] var17;
            for (EnumParticle p : var17 = EnumParticle.values()) {
               if (effect.getName().startsWith(p.b().replace("_", ""))) {
                  particle = p;
                  if (effect.getData() != null) {
                     if (effect.getData().equals(org.bukkit.Material.class)) {
                        extra = new int[]{id};
                     } else {
                        extra = new int[]{data << 12 | id & 4095};
                     }
                  }
                  break;
               }
            }

            if (extra == null) {
               extra = new int[0];
            }

            packet = new PacketPlayOutWorldParticles(
               particle, true, (float)location.getX(), (float)location.getY(), (float)location.getZ(), offsetX, offsetY, offsetZ, speed, particleCount, extra
            );
         }

         radius *= radius;

         for (Player player : CraftWorld.this.getPlayers()) {
            if (((CraftPlayer)player).getHandle().playerConnection != null && location.getWorld().equals(player.getWorld())) {
               int distance = (int)player.getLocation().distanceSquared(location);
               if (distance <= radius) {
                  ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
               }
            }
         }
      }

      @Override
      public void playEffect(org.bukkit.Location location, org.bukkit.Effect effect) {
         CraftWorld.this.playEffect(location, effect, 0);
      }

      @Override
      public LightningStrike strikeLightning(org.bukkit.Location loc, boolean isSilent) {
         EntityLightning lightning = new EntityLightning(CraftWorld.this.world, loc.getX(), loc.getY(), loc.getZ(), false, isSilent);
         CraftWorld.this.world.strikeLightning(lightning);
         return new CraftLightningStrike(CraftWorld.this.server, lightning);
      }

      @Override
      public LightningStrike strikeLightningEffect(org.bukkit.Location loc, boolean isSilent) {
         EntityLightning lightning = new EntityLightning(CraftWorld.this.world, loc.getX(), loc.getY(), loc.getZ(), true, isSilent);
         CraftWorld.this.world.strikeLightning(lightning);
         return new CraftLightningStrike(CraftWorld.this.server, lightning);
      }
   };

   public CraftWorld(WorldServer world, ChunkGenerator gen, org.bukkit.World.Environment env) {
      this.world = world;
      this.generator = gen;
      this.environment = env;
      if (this.server.chunkGCPeriod > 0) {
         this.chunkGCTickCount = rand.nextInt(this.server.chunkGCPeriod);
      }
   }

   private static void randomLocationWithinBlock(org.bukkit.Location loc, double xs, double ys, double zs) {
      double prevX = loc.getX();
      double prevY = loc.getY();
      double prevZ = loc.getZ();
      loc.add(xs, ys, zs);
      if (loc.getX() < Math.floor(prevX)) {
         loc.setX(Math.floor(prevX));
      }

      if (loc.getX() >= Math.ceil(prevX)) {
         loc.setX(Math.ceil(prevX - 0.01));
      }

      if (loc.getY() < Math.floor(prevY)) {
         loc.setY(Math.floor(prevY));
      }

      if (loc.getY() >= Math.ceil(prevY)) {
         loc.setY(Math.ceil(prevY - 0.01));
      }

      if (loc.getZ() < Math.floor(prevZ)) {
         loc.setZ(Math.floor(prevZ));
      }

      if (loc.getZ() >= Math.ceil(prevZ)) {
         loc.setZ(Math.ceil(prevZ - 0.01));
      }
   }

   @Override
   public Block getBlockAt(int x, int y, int z) {
      return this.getChunkAt(x >> 4, z >> 4).getBlock(x & 15, y, z & 15);
   }

   @Override
   public int getBlockTypeIdAt(int x, int y, int z) {
      return CraftMagicNumbers.getId(this.world.getType(x, y, z).getBlock());
   }

   @Override
   public int getHighestBlockYAt(int x, int z) {
      if (!this.isChunkLoaded(x >> 4, z >> 4)) {
         this.loadChunk(x >> 4, z >> 4);
      }

      return this.world.getHighestBlockYAt(x, z, true);
   }

   @Override
   public org.bukkit.Location getSpawnLocation() {
      BlockPosition spawn = this.world.getSpawn();
      return new org.bukkit.Location(this, spawn.getX(), spawn.getY(), spawn.getZ());
   }

   @Override
   public boolean setSpawnLocation(int x, int y, int z) {
      try {
         org.bukkit.Location previousLocation = this.getSpawnLocation();
         this.world.worldData.setSpawn(new BlockPosition(x, y, z));
         SpawnChangeEvent event = new SpawnChangeEvent(this, previousLocation);
         this.server.getPluginManager().callEvent(event);
         return true;
      } catch (Exception var6) {
         return false;
      }
   }

   @Override
   public void getChunkAtAsync(int x, int z, org.bukkit.World.ChunkLoadCallback callback) {
      ChunkProviderServer cps = this.world.chunkProviderServer;
      cps.getChunkAt(x, z, () -> callback.onLoad(cps.getChunkAt(x, z).bukkitChunk));
   }

   @Override
   public void getChunkAtAsync(Block block, org.bukkit.World.ChunkLoadCallback callback) {
      this.getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, callback);
   }

   @Override
   public void getChunkAtAsync(org.bukkit.Location location, org.bukkit.World.ChunkLoadCallback callback) {
      this.getChunkAtAsync(location.getBlockX() >> 4, location.getBlockZ() >> 4, callback);
   }

   @Override
   public org.bukkit.Chunk getChunkAt(int x, int z) {
      return this.world.chunkProviderServer.getChunkAt(x, z).bukkitChunk;
   }

   @Override
   public org.bukkit.Chunk getChunkAt(Block block) {
      return this.getChunkAt(block.getX() >> 4, block.getZ() >> 4);
   }

   @Override
   public boolean isChunkLoaded(int x, int z) {
      return this.world.chunkProviderServer.isChunkLoaded(x, z);
   }

   @Override
   public org.bukkit.Chunk[] getLoadedChunks() {
      Object[] chunks = this.world.chunkProviderServer.chunks.values().toArray();
      org.bukkit.Chunk[] craftChunks = new CraftChunk[chunks.length];

      for (int i = 0; i < chunks.length; i++) {
         net.minecraft.server.v1_8_R3.Chunk chunk = (net.minecraft.server.v1_8_R3.Chunk)chunks[i];
         craftChunks[i] = chunk.bukkitChunk;
      }

      return craftChunks;
   }

   @Override
   public void loadChunk(int x, int z) {
      this.loadChunk(x, z, true);
   }

   @Override
   public boolean unloadChunk(org.bukkit.Chunk chunk) {
      return this.unloadChunk(chunk.getX(), chunk.getZ());
   }

   @Override
   public boolean unloadChunk(int x, int z) {
      return this.unloadChunk(x, z, true);
   }

   @Override
   public boolean unloadChunk(int x, int z, boolean save) {
      return this.unloadChunk(x, z, save, false);
   }

   @Override
   public boolean unloadChunkRequest(int x, int z) {
      return this.unloadChunkRequest(x, z, true);
   }

   @Override
   public boolean unloadChunkRequest(int x, int z, boolean safe) {
      AsyncCatcher.catchOp("chunk unload");
      if (safe && this.isChunkInUse(x, z)) {
         return false;
      } else {
         this.world.chunkProviderServer.queueUnload(x, z);
         return true;
      }
   }

   @Override
   public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
      AsyncCatcher.catchOp("chunk unload");
      if (safe && this.isChunkInUse(x, z)) {
         return false;
      } else {
         long hash = LongHash.toLong(x, z);
         net.minecraft.server.v1_8_R3.Chunk chunk = this.world.chunkProviderServer.getChunkIfLoaded(hash);
         if (chunk != null && System.currentTimeMillis() - chunk.lastActive >= 25000L) {
            if (chunk.mustSave) {
               save = true;
            }

            chunk.removeEntities();
            if (save && !(chunk instanceof EmptyChunk)) {
               this.world.chunkProviderServer.saveChunk(chunk);
               this.world.chunkProviderServer.saveChunkNOP(chunk);
            }

            this.world.chunkProviderServer.unloadQueue.remove(hash);
            this.world.chunkProviderServer.chunks.remove(hash);
            this.world.chunkTickList.remove(hash);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean regenerateChunk(int x, int z) {
      long hash = LongHash.toLong(x, z);
      this.unloadChunk(x, z, false, false);
      this.world.chunkProviderServer.unloadQueue.remove(hash);
      net.minecraft.server.v1_8_R3.Chunk chunk;
      if (this.world.chunkProviderServer.chunkProvider == null) {
         chunk = this.world.chunkProviderServer.emptyChunk;
      } else {
         chunk = this.world.chunkProviderServer.chunkProvider.getOrCreateChunk(hash, x, z);
      }

      this.chunkLoadPostProcess(chunk, x, z);
      this.refreshChunk(x, z);
      return chunk != null;
   }

   @Override
   public boolean refreshChunk(int x, int z) {
      if (!this.isChunkLoaded(x, z)) {
         return false;
      } else {
         int px = x << 4;
         int pz = z << 4;
         int height = this.getMaxHeight() / 16;

         for (int idx = 0; idx < 64; idx++) {
            this.world.notify(px + idx / height, idx % height * 16, pz);
         }

         this.world.notify(px + 15, height * 16 - 1, pz + 15);
         return true;
      }
   }

   @Override
   public boolean isChunkInUse(int x, int z) {
      return this.world.getPlayerChunkMap().isChunkInUse(x, z);
   }

   @Override
   public boolean loadChunk(int x, int z, boolean generate) {
      AsyncCatcher.catchOp("chunk load");
      this.chunkLoadCount++;
      if (generate) {
         return this.world.chunkProviderServer.getChunkAt(x, z) != null;
      } else {
         long hash = LongHash.toLong(x, z);
         this.world.chunkProviderServer.unloadQueue.remove(hash);
         net.minecraft.server.v1_8_R3.Chunk chunk = (net.minecraft.server.v1_8_R3.Chunk)this.world.chunkProviderServer.chunks.get(hash);
         if (chunk == null) {
            chunk = this.world.chunkProviderServer.loadChunk(x, z);
            this.chunkLoadPostProcess(chunk, x, z);
         }

         return chunk != null;
      }
   }

   private void chunkLoadPostProcess(net.minecraft.server.v1_8_R3.Chunk chunk, int cx, int cz) {
      if (chunk != null) {
         this.world.chunkProviderServer.chunks.put(chunk.chunkHashKey, chunk);
         chunk.addEntities();

         for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
               if (x != 0 || z != 0) {
                  net.minecraft.server.v1_8_R3.Chunk neighbor = this.world.chunkProviderServer.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                  if (neighbor != null) {
                     neighbor.setNeighborLoaded(-x, -z);
                     chunk.setNeighborLoaded(x, z);
                  }
               }
            }
         }

         chunk.loadNearby(this.world.chunkProviderServer, this.world.chunkProviderServer, cx, cz);
      }
   }

   @Override
   public boolean isChunkLoaded(org.bukkit.Chunk chunk) {
      return this.isChunkLoaded(chunk.getX(), chunk.getZ());
   }

   @Override
   public void loadChunk(org.bukkit.Chunk chunk) {
      this.loadChunk(chunk.getX(), chunk.getZ());
      ((CraftChunk)this.getChunkAt(chunk.getX(), chunk.getZ())).getHandle().bukkitChunk = chunk;
   }

   public WorldServer getHandle() {
      return this.world;
   }

   @Override
   public Item dropItem(org.bukkit.Location loc, ItemStack item) {
      return this.dropItem(loc, item, null);
   }

   public Item dropItem(org.bukkit.Location loc, ItemStack item, net.minecraft.server.v1_8_R3.Entity owner) {
      Validate.notNull(item, "Cannot drop a Null item.");
      Validate.isTrue(item.getTypeId() != 0, "Cannot drop AIR.");
      EntityItem entity = new EntityItem(this.world, loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
      entity.owner = owner;
      entity.pickupDelay = 10;
      this.world.addEntity(entity);
      return new CraftItem(this.world.getServer(), entity);
   }

   @Override
   public Item dropItemNaturally(org.bukkit.Location loc, ItemStack item) {
      return this.dropItemNaturally(loc, item, null);
   }

   public Item dropItemNaturally(org.bukkit.Location loc, ItemStack item, net.minecraft.server.v1_8_R3.Entity owner) {
      double xs = this.world.random.nextFloat() * 0.7F - 0.35;
      double ys = this.world.random.nextFloat() * 0.7F - 0.35;
      double zs = this.world.random.nextFloat() * 0.7F - 0.35;
      loc = loc.clone();
      randomLocationWithinBlock(loc, xs, ys, zs);
      return this.dropItem(loc, item, owner);
   }

   @Override
   public Arrow spawnArrow(org.bukkit.Location loc, Vector velocity, float speed, float spread) {
      Validate.notNull(loc, "Can not spawn arrow with a null location");
      Validate.notNull(velocity, "Can not spawn arrow with a null velocity");
      EntityArrow arrow = new EntityArrow(this.world);
      arrow.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
      arrow.shoot(velocity.getX(), velocity.getY(), velocity.getZ(), speed, spread);
      this.world.addEntity(arrow);
      return (Arrow)arrow.getBukkitEntity();
   }

   @Deprecated
   @Override
   public LivingEntity spawnCreature(org.bukkit.Location loc, CreatureType creatureType) {
      return this.spawnCreature(loc, creatureType.toEntityType());
   }

   @Deprecated
   @Override
   public LivingEntity spawnCreature(org.bukkit.Location loc, EntityType creatureType) {
      Validate.isTrue(creatureType.isAlive(), "EntityType not instance of LivingEntity");
      return (LivingEntity)this.spawnEntity(loc, creatureType);
   }

   @Override
   public Entity spawnEntity(org.bukkit.Location loc, EntityType entityType) {
      return this.spawn(loc, entityType.getEntityClass());
   }

   @Override
   public LightningStrike strikeLightning(org.bukkit.Location loc) {
      EntityLightning lightning = new EntityLightning(this.world, loc.getX(), loc.getY(), loc.getZ());
      this.world.strikeLightning(lightning);
      return new CraftLightningStrike(this.server, lightning);
   }

   @Override
   public LightningStrike strikeLightningEffect(org.bukkit.Location loc) {
      EntityLightning lightning = new EntityLightning(this.world, loc.getX(), loc.getY(), loc.getZ(), true);
      this.world.strikeLightning(lightning);
      return new CraftLightningStrike(this.server, lightning);
   }

   @Override
   public boolean generateTree(org.bukkit.Location loc, org.bukkit.TreeType type) {
      WorldGenerator gen;
      switch (type) {
         case TREE:
         default:
            gen = new WorldGenTrees(true);
            break;
         case BIG_TREE:
            gen = new WorldGenBigTree(true);
            break;
         case REDWOOD:
            gen = new WorldGenTaiga2(true);
            break;
         case TALL_REDWOOD:
            gen = new WorldGenTaiga1();
            break;
         case BIRCH:
            gen = new WorldGenForest(true, false);
            break;
         case JUNGLE: {
            IBlockData iblockdata1 = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, EnumLogVariant.JUNGLE);
            IBlockData iblockdata2 = Blocks.LEAVES.getBlockData().set(BlockLeaves1.VARIANT, EnumLogVariant.JUNGLE).set(BlockLeaves.CHECK_DECAY, false);
            gen = new WorldGenJungleTree(true, 10, 20, iblockdata1, iblockdata2);
            break;
         }
         case SMALL_JUNGLE: {
            IBlockData iblockdata1 = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, EnumLogVariant.JUNGLE);
            IBlockData iblockdata2 = Blocks.LEAVES.getBlockData().set(BlockLeaves1.VARIANT, EnumLogVariant.JUNGLE).set(BlockLeaves.CHECK_DECAY, false);
            gen = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockdata1, iblockdata2, false);
            break;
         }
         case COCOA_TREE: {
            IBlockData iblockdata1 = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, EnumLogVariant.JUNGLE);
            IBlockData iblockdata2 = Blocks.LEAVES.getBlockData().set(BlockLeaves1.VARIANT, EnumLogVariant.JUNGLE).set(BlockLeaves.CHECK_DECAY, false);
            gen = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockdata1, iblockdata2, true);
            break;
         }
         case JUNGLE_BUSH: {
            IBlockData iblockdata1 = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, EnumLogVariant.JUNGLE);
            IBlockData iblockdata2 = Blocks.LEAVES.getBlockData().set(BlockLeaves1.VARIANT, EnumLogVariant.OAK).set(BlockLeaves.CHECK_DECAY, false);
            gen = new WorldGenGroundBush(iblockdata1, iblockdata2);
            break;
         }
         case RED_MUSHROOM:
            gen = new WorldGenHugeMushroom(Blocks.RED_MUSHROOM_BLOCK);
            break;
         case BROWN_MUSHROOM:
            gen = new WorldGenHugeMushroom(Blocks.BROWN_MUSHROOM_BLOCK);
            break;
         case SWAMP:
            gen = new WorldGenSwampTree();
            break;
         case ACACIA:
            gen = new WorldGenAcaciaTree(true);
            break;
         case DARK_OAK:
            gen = new WorldGenForestTree(true);
            break;
         case MEGA_REDWOOD:
            gen = new WorldGenMegaTree(false, rand.nextBoolean());
            break;
         case TALL_BIRCH:
            gen = new WorldGenForest(true, true);
      }

      return gen.generate(this.world, rand, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
   }

   @Override
   public boolean generateTree(org.bukkit.Location loc, org.bukkit.TreeType type, org.bukkit.BlockChangeDelegate delegate) {
      this.world.captureTreeGeneration = true;
      this.world.captureBlockStates = true;
      boolean grownTree = this.generateTree(loc, type);
      this.world.captureBlockStates = false;
      this.world.captureTreeGeneration = false;
      if (!grownTree) {
         this.world.capturedBlockStates.clear();
         return false;
      } else {
         for (BlockState blockstate : this.world.capturedBlockStates) {
            int x = blockstate.getX();
            int y = blockstate.getY();
            int z = blockstate.getZ();
            BlockPosition position = new BlockPosition(x, y, z);
            net.minecraft.server.v1_8_R3.Block oldBlock = this.world.getType(position).getBlock();
            int typeId = blockstate.getTypeId();
            int data = blockstate.getRawData();
            int flag = ((CraftBlockState)blockstate).getFlag();
            delegate.setTypeIdAndData(x, y, z, typeId, data);
            net.minecraft.server.v1_8_R3.Block newBlock = this.world.getType(position).getBlock();
            this.world.notifyAndUpdatePhysics(position, null, oldBlock, newBlock, flag);
         }

         this.world.capturedBlockStates.clear();
         return true;
      }
   }

   public TileEntity getTileEntityAt(int x, int y, int z) {
      return this.world.getTileEntity(new BlockPosition(x, y, z));
   }

   @Override
   public String getName() {
      return this.world.worldData.getName();
   }

   @Deprecated
   public long getId() {
      return this.world.worldData.getSeed();
   }

   @Override
   public UUID getUID() {
      return this.world.getDataManager().getUUID();
   }

   @Override
   public String toString() {
      return "CraftWorld{name=" + this.getName() + '}';
   }

   @Override
   public long getTime() {
      long time = this.getFullTime() % 24000L;
      if (time < 0L) {
         time += 24000L;
      }

      return time;
   }

   @Override
   public void setTime(long time) {
      long margin = (time - this.getFullTime()) % 24000L;
      if (margin < 0L) {
         margin += 24000L;
      }

      this.setFullTime(this.getFullTime() + margin);
   }

   @Override
   public long getFullTime() {
      return this.world.getDayTime();
   }

   @Override
   public void setFullTime(long time) {
      this.world.setDayTime(time);

      for (Player p : this.getPlayers()) {
         CraftPlayer cp = (CraftPlayer)p;
         if (cp.getHandle().playerConnection != null) {
            cp.getHandle()
               .playerConnection
               .sendPacket(
                  new PacketPlayOutUpdateTime(
                     cp.getHandle().world.getTime(), cp.getHandle().getPlayerTime(), cp.getHandle().world.getGameRules().getBoolean("doDaylightCycle")
                  )
               );
         }
      }
   }

   @Override
   public boolean createExplosion(double x, double y, double z, float power) {
      return this.createExplosion(x, y, z, power, false, true);
   }

   @Override
   public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
      return this.createExplosion(x, y, z, power, setFire, true);
   }

   @Override
   public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
      return !this.world.createExplosion(null, x, y, z, power, setFire, breakBlocks).wasCanceled;
   }

   @Override
   public boolean createExplosion(org.bukkit.Location loc, float power) {
      return this.createExplosion(loc, power, false);
   }

   @Override
   public boolean createExplosion(org.bukkit.Location loc, float power, boolean setFire) {
      return this.createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire);
   }

   @Override
   public org.bukkit.World.Environment getEnvironment() {
      return this.environment;
   }

   public void setEnvironment(org.bukkit.World.Environment env) {
      if (this.environment != env) {
         this.environment = env;
         this.world.worldProvider = WorldProvider.byDimension(this.environment.getId());
      }
   }

   @Override
   public Block getBlockAt(org.bukkit.Location location) {
      return this.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   @Override
   public int getBlockTypeIdAt(org.bukkit.Location location) {
      return this.getBlockTypeIdAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   @Override
   public int getHighestBlockYAt(org.bukkit.Location location) {
      return this.getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
   }

   @Override
   public org.bukkit.Chunk getChunkAt(org.bukkit.Location location) {
      return this.getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
   }

   @Override
   public ChunkGenerator getGenerator() {
      return this.generator;
   }

   @Override
   public List<BlockPopulator> getPopulators() {
      return this.populators;
   }

   @Override
   public Block getHighestBlockAt(int x, int z) {
      return this.getBlockAt(x, this.getHighestBlockYAt(x, z), z);
   }

   @Override
   public Block getHighestBlockAt(org.bukkit.Location location) {
      return this.getHighestBlockAt(location.getBlockX(), location.getBlockZ());
   }

   @Override
   public Biome getBiome(int x, int z) {
      return CraftBlock.biomeBaseToBiome(this.world.getBiome(x, 0, z));
   }

   @Override
   public void setBiome(int x, int z, Biome bio) {
      BiomeBase bb = CraftBlock.biomeToBiomeBase(bio);
      if (this.world.isLoaded(x, 0, z)) {
         net.minecraft.server.v1_8_R3.Chunk chunk = this.world.getChunkAtWorldCoords(x, z);
         if (chunk != null) {
            byte[] biomevals = chunk.getBiomeIndex();
            biomevals[(z & 15) << 4 | x & 15] = (byte)bb.id;
         }
      }
   }

   @Override
   public double getTemperature(int x, int z) {
      return this.world.getBiome(x, 0, z).temperature;
   }

   @Override
   public double getHumidity(int x, int z) {
      return this.world.getBiome(x, 0, z).humidity;
   }

   @Override
   public List<Entity> getEntities() {
      List<Entity> list = new ArrayList<>();

      for (net.minecraft.server.v1_8_R3.Entity mcEnt : this.world.entityList) {
         Entity bukkitEntity = mcEnt.getBukkitEntity();
         if (bukkitEntity != null) {
            list.add(bukkitEntity);
         }
      }

      return list;
   }

   @Override
   public List<LivingEntity> getLivingEntities() {
      List<LivingEntity> list = new ArrayList<>();

      for (net.minecraft.server.v1_8_R3.Entity mcEnt : this.world.entityList) {
         Entity bukkitEntity = mcEnt.getBukkitEntity();
         if (bukkitEntity instanceof LivingEntity) {
            list.add((LivingEntity)bukkitEntity);
         }
      }

      return list;
   }

   @Deprecated
   @Override
   public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
      return (Collection<T>)this.getEntitiesByClasses(classes);
   }

   @Override
   public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> clazz) {
      Collection<T> list = new ArrayList<>();

      for (net.minecraft.server.v1_8_R3.Entity entity : this.world.entityList) {
         Entity bukkitEntity = entity.getBukkitEntity();
         if (bukkitEntity != null) {
            Class<?> bukkitClass = bukkitEntity.getClass();
            if (clazz.isAssignableFrom(bukkitClass)) {
               list.add((T)bukkitEntity);
            }
         }
      }

      return list;
   }

   @Override
   public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
      Collection<Entity> list = new ArrayList<>();

      for (net.minecraft.server.v1_8_R3.Entity entity : this.world.entityList) {
         Entity bukkitEntity = entity.getBukkitEntity();
         if (bukkitEntity != null) {
            Class<?> bukkitClass = bukkitEntity.getClass();

            for (Class<?> clazz : classes) {
               if (clazz.isAssignableFrom(bukkitClass)) {
                  list.add(bukkitEntity);
                  break;
               }
            }
         }
      }

      return list;
   }

   @Override
   public Collection<Entity> getNearbyEntities(org.bukkit.Location location, double x, double y, double z) {
      if (location != null && location.getWorld().equals(this)) {
         AxisAlignedBB bb = new AxisAlignedBB(
            location.getX() - x, location.getY() - y, location.getZ() - z, location.getX() + x, location.getY() + y, location.getZ() + z
         );
         List<net.minecraft.server.v1_8_R3.Entity> entityList = this.getHandle().a((net.minecraft.server.v1_8_R3.Entity)null, bb, (com.google.common.base.Predicate<net.minecraft.server.v1_8_R3.Entity>)null);
         List<Entity> bukkitEntityList = new ArrayList<>(entityList.size());

         for (net.minecraft.server.v1_8_R3.Entity entity : entityList) {
            bukkitEntityList.add(entity.getBukkitEntity());
         }

         return bukkitEntityList;
      } else {
         return Collections.emptyList();
      }
   }

   @Override
   public List<Player> getPlayers() {
      List<Player> list = new ArrayList<>(this.world.players.size());

      for (EntityHuman human : this.world.players) {
         HumanEntity bukkitEntity = human.getBukkitEntity();
         if (bukkitEntity instanceof Player) {
            list.add((Player)bukkitEntity);
         }
      }

      return list;
   }

   @Override
   public void save() {
      this.save(true);
   }

   public void save(boolean forceSave) {
      this.server.checkSaveState();

      try {
         boolean oldSave = this.world.savingDisabled;
         this.world.savingDisabled = false;
         this.world.save(forceSave, null);
         this.world.savingDisabled = oldSave;
      } catch (ExceptionWorldConflict var3) {
         var3.printStackTrace();
         ServerInternalException.reportInternalException(var3);
      }
   }

   @Override
   public boolean isAutoSave() {
      return !this.world.savingDisabled;
   }

   @Override
   public void setAutoSave(boolean value) {
      this.world.savingDisabled = !value;
   }

   @Override
   public org.bukkit.Difficulty getDifficulty() {
      return org.bukkit.Difficulty.getByValue(this.getHandle().getDifficulty().ordinal());
   }

   @Override
   public void setDifficulty(org.bukkit.Difficulty difficulty) {
      this.getHandle().worldData.setDifficulty(EnumDifficulty.getById(difficulty.getValue()));
   }

   public SpecializedBlockMetadataStore getBlockMetadata() {
      return this.blockMetadata;
   }

   @Override
   public boolean hasStorm() {
      return this.world.worldData.hasStorm();
   }

   @Override
   public void setStorm(boolean hasStorm) {
      this.world.worldData.setStorm(hasStorm);
   }

   @Override
   public int getWeatherDuration() {
      return this.world.worldData.getWeatherDuration();
   }

   @Override
   public void setWeatherDuration(int duration) {
      this.world.worldData.setWeatherDuration(duration);
   }

   @Override
   public boolean isThundering() {
      return this.world.worldData.isThundering();
   }

   @Override
   public void setThundering(boolean thundering) {
      this.world.worldData.setThundering(thundering);
   }

   @Override
   public int getThunderDuration() {
      return this.world.worldData.getThunderDuration();
   }

   @Override
   public void setThunderDuration(int duration) {
      this.world.worldData.setThunderDuration(duration);
   }

   @Override
   public long getSeed() {
      return this.world.worldData.getSeed();
   }

   @Override
   public boolean getPVP() {
      return this.world.pvpMode;
   }

   @Override
   public void setPVP(boolean pvp) {
      this.world.pvpMode = pvp;
   }

   public void playEffect(Player player, org.bukkit.Effect effect, int data) {
      this.playEffect(player.getLocation(), effect, data, 0);
   }

   @Override
   public void playEffect(org.bukkit.Location location, org.bukkit.Effect effect, int data) {
      this.playEffect(location, effect, data, 64);
   }

   @Override
   public <T> void playEffect(org.bukkit.Location loc, org.bukkit.Effect effect, T data) {
      this.playEffect(loc, effect, data, 64);
   }

   @Override
   public <T> void playEffect(org.bukkit.Location loc, org.bukkit.Effect effect, T data, int radius) {
      if (data != null) {
         Validate.isTrue(data.getClass().isAssignableFrom(effect.getData()), "Wrong kind of data for this effect!");
      } else {
         Validate.isTrue(effect.getData() == null, "Wrong kind of data for this effect!");
      }

      if (data != null && data.getClass().equals(MaterialData.class)) {
         MaterialData materialData = (MaterialData)data;
         Validate.isTrue(materialData.getItemType().isBlock(), "Material must be block");
         this.spigot().playEffect(loc, effect, materialData.getItemType().getId(), materialData.getData(), 0.0F, 0.0F, 0.0F, 1.0F, 1, radius);
      } else {
         int dataValue = data == null ? 0 : CraftEffect.getDataValue(effect, data);
         this.playEffect(loc, effect, dataValue, radius);
      }
   }

   @Override
   public void playEffect(org.bukkit.Location location, org.bukkit.Effect effect, int data, int radius) {
      this.spigot().playEffect(location, effect, data, 0, 0.0F, 0.0F, 0.0F, 1.0F, 1, radius);
   }

   @Override
   public <T extends Entity> T spawn(org.bukkit.Location location, Class<T> clazz) throws IllegalArgumentException {
      return this.spawn(location, clazz, CreatureSpawnEvent.SpawnReason.CUSTOM);
   }

   @Override
   public FallingBlock spawnFallingBlock(org.bukkit.Location location, org.bukkit.Material material, byte data) throws IllegalArgumentException {
      Validate.notNull(location, "Location cannot be null");
      Validate.notNull(material, "Material cannot be null");
      Validate.isTrue(material.isBlock(), "Material must be a block");
      double x = location.getBlockX() + 0.5;
      double y = location.getBlockY() + 0.5;
      double z = location.getBlockZ() + 0.5;
      location = location.clone();
      EntityFallingBlock entity = new EntityFallingBlock(
         location, this.world, x, y, z, net.minecraft.server.v1_8_R3.Block.getById(material.getId()).fromLegacyData(data)
      );
      entity.ticksLived = 1;
      this.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
      return (FallingBlock)entity.getBukkitEntity();
   }

   @Override
   public FallingBlock spawnFallingBlock(org.bukkit.Location location, int blockId, byte blockData) throws IllegalArgumentException {
      return this.spawnFallingBlock(location, org.bukkit.Material.getMaterial(blockId), blockData);
   }

   public net.minecraft.server.v1_8_R3.Entity createEntity(org.bukkit.Location location, Class<? extends Entity> clazz) throws IllegalArgumentException {
      if (location != null && clazz != null) {
         net.minecraft.server.v1_8_R3.Entity entity = null;
         double x = location.getX();
         double y = location.getY();
         double z = location.getZ();
         float pitch = location.getPitch();
         float yaw = location.getYaw();
         if (Boat.class.isAssignableFrom(clazz)) {
            entity = new EntityBoat(this.world, x, y, z);
         } else if (FallingBlock.class.isAssignableFrom(clazz)) {
            x = location.getBlockX();
            y = location.getBlockY();
            z = location.getBlockZ();
            IBlockData blockData = this.world.getType(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
            int type = CraftMagicNumbers.getId(blockData.getBlock());
            int data = blockData.getBlock().toLegacyData(blockData);
            location = location.clone();
            entity = new EntityFallingBlock(
               location, this.world, x + 0.5, y + 0.5, z + 0.5, net.minecraft.server.v1_8_R3.Block.getById(type).fromLegacyData(data)
            );
         } else if (Projectile.class.isAssignableFrom(clazz)) {
            if (Snowball.class.isAssignableFrom(clazz)) {
               entity = new EntitySnowball(this.world, x, y, z);
            } else if (Egg.class.isAssignableFrom(clazz)) {
               entity = new EntityEgg(this.world, x, y, z);
            } else if (Arrow.class.isAssignableFrom(clazz)) {
               entity = new EntityArrow(this.world);
               entity.setPositionRotation(x, y, z, 0.0F, 0.0F);
            } else if (ThrownExpBottle.class.isAssignableFrom(clazz)) {
               entity = new EntityThrownExpBottle(this.world);
               entity.setPositionRotation(x, y, z, 0.0F, 0.0F);
            } else if (EnderPearl.class.isAssignableFrom(clazz)) {
               entity = new EntityEnderPearl(this.world);
               entity.setPositionRotation(x, y, z, 0.0F, 0.0F);
            } else if (ThrownPotion.class.isAssignableFrom(clazz)) {
               entity = new EntityPotion(this.world, x, y, z, CraftItemStack.asNMSCopy(new ItemStack(org.bukkit.Material.POTION, 1)));
            } else if (Fireball.class.isAssignableFrom(clazz)) {
               if (SmallFireball.class.isAssignableFrom(clazz)) {
                  entity = new EntitySmallFireball(this.world);
               } else if (WitherSkull.class.isAssignableFrom(clazz)) {
                  entity = new EntityWitherSkull(this.world);
               } else {
                  entity = new EntityLargeFireball(this.world);
               }

               entity.setPositionRotation(x, y, z, yaw, pitch);
               Vector direction = location.getDirection().multiply(10);
               ((EntityFireball)entity).setDirection(direction.getX(), direction.getY(), direction.getZ());
            }
         } else if (Minecart.class.isAssignableFrom(clazz)) {
            if (PoweredMinecart.class.isAssignableFrom(clazz)) {
               entity = new EntityMinecartFurnace(this.world, x, y, z);
            } else if (StorageMinecart.class.isAssignableFrom(clazz)) {
               entity = new EntityMinecartChest(this.world, x, y, z);
            } else if (ExplosiveMinecart.class.isAssignableFrom(clazz)) {
               entity = new EntityMinecartTNT(this.world, x, y, z);
            } else if (HopperMinecart.class.isAssignableFrom(clazz)) {
               entity = new EntityMinecartHopper(this.world, x, y, z);
            } else if (SpawnerMinecart.class.isAssignableFrom(clazz)) {
               entity = new EntityMinecartMobSpawner(this.world, x, y, z);
            } else {
               entity = new EntityMinecartRideable(this.world, x, y, z);
            }
         } else if (EnderSignal.class.isAssignableFrom(clazz)) {
            entity = new EntityEnderSignal(this.world, x, y, z);
         } else if (EnderCrystal.class.isAssignableFrom(clazz)) {
            entity = new EntityEnderCrystal(this.world);
            entity.setPositionRotation(x, y, z, 0.0F, 0.0F);
         } else if (LivingEntity.class.isAssignableFrom(clazz)) {
            if (Chicken.class.isAssignableFrom(clazz)) {
               entity = new EntityChicken(this.world);
            } else if (Cow.class.isAssignableFrom(clazz)) {
               if (MushroomCow.class.isAssignableFrom(clazz)) {
                  entity = new EntityMushroomCow(this.world);
               } else {
                  entity = new EntityCow(this.world);
               }
            } else if (Golem.class.isAssignableFrom(clazz)) {
               if (Snowman.class.isAssignableFrom(clazz)) {
                  entity = new EntitySnowman(this.world);
               } else if (IronGolem.class.isAssignableFrom(clazz)) {
                  entity = new EntityIronGolem(this.world);
               }
            } else if (Creeper.class.isAssignableFrom(clazz)) {
               entity = new EntityCreeper(this.world);
            } else if (Ghast.class.isAssignableFrom(clazz)) {
               entity = new EntityGhast(this.world);
            } else if (Pig.class.isAssignableFrom(clazz)) {
               entity = new EntityPig(this.world);
            } else if (!Player.class.isAssignableFrom(clazz)) {
               if (Sheep.class.isAssignableFrom(clazz)) {
                  entity = new EntitySheep(this.world);
               } else if (Horse.class.isAssignableFrom(clazz)) {
                  entity = new EntityHorse(this.world);
               } else if (Skeleton.class.isAssignableFrom(clazz)) {
                  entity = new EntitySkeleton(this.world);
               } else if (Slime.class.isAssignableFrom(clazz)) {
                  if (MagmaCube.class.isAssignableFrom(clazz)) {
                     entity = new EntityMagmaCube(this.world);
                  } else {
                     entity = new EntitySlime(this.world);
                  }
               } else if (Spider.class.isAssignableFrom(clazz)) {
                  if (CaveSpider.class.isAssignableFrom(clazz)) {
                     entity = new EntityCaveSpider(this.world);
                  } else {
                     entity = new EntitySpider(this.world);
                  }
               } else if (Squid.class.isAssignableFrom(clazz)) {
                  entity = new EntitySquid(this.world);
               } else if (Tameable.class.isAssignableFrom(clazz)) {
                  if (Wolf.class.isAssignableFrom(clazz)) {
                     entity = new EntityWolf(this.world);
                  } else if (Ocelot.class.isAssignableFrom(clazz)) {
                     entity = new EntityOcelot(this.world);
                  }
               } else if (PigZombie.class.isAssignableFrom(clazz)) {
                  entity = new EntityPigZombie(this.world);
               } else if (Zombie.class.isAssignableFrom(clazz)) {
                  entity = new EntityZombie(this.world);
               } else if (Giant.class.isAssignableFrom(clazz)) {
                  entity = new EntityGiantZombie(this.world);
               } else if (Silverfish.class.isAssignableFrom(clazz)) {
                  entity = new EntitySilverfish(this.world);
               } else if (Enderman.class.isAssignableFrom(clazz)) {
                  entity = new EntityEnderman(this.world);
               } else if (Blaze.class.isAssignableFrom(clazz)) {
                  entity = new EntityBlaze(this.world);
               } else if (Villager.class.isAssignableFrom(clazz)) {
                  entity = new EntityVillager(this.world);
               } else if (Witch.class.isAssignableFrom(clazz)) {
                  entity = new EntityWitch(this.world);
               } else if (Wither.class.isAssignableFrom(clazz)) {
                  entity = new EntityWither(this.world);
               } else if (ComplexLivingEntity.class.isAssignableFrom(clazz)) {
                  if (EnderDragon.class.isAssignableFrom(clazz)) {
                     entity = new EntityEnderDragon(this.world);
                  }
               } else if (Ambient.class.isAssignableFrom(clazz)) {
                  if (Bat.class.isAssignableFrom(clazz)) {
                     entity = new EntityBat(this.world);
                  }
               } else if (Rabbit.class.isAssignableFrom(clazz)) {
                  entity = new EntityRabbit(this.world);
               } else if (Endermite.class.isAssignableFrom(clazz)) {
                  entity = new EntityEndermite(this.world);
               } else if (Guardian.class.isAssignableFrom(clazz)) {
                  entity = new EntityGuardian(this.world);
               } else if (ArmorStand.class.isAssignableFrom(clazz)) {
                  entity = new EntityArmorStand(this.world, x, y, z);
               }
            }

            if (entity != null) {
               entity.setLocation(x, y, z, yaw, pitch);
            }
         } else if (Hanging.class.isAssignableFrom(clazz)) {
            Block block = this.getBlockAt(location);
            BlockFace face = BlockFace.SELF;
            int width = 16;
            int height = 16;
            if (ItemFrame.class.isAssignableFrom(clazz)) {
               width = 12;
               height = 12;
            } else if (LeashHitch.class.isAssignableFrom(clazz)) {
               width = 9;
               height = 9;
            }

            BlockFace[] faces = new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
            BlockPosition pos = new BlockPosition((int)x, (int)y, (int)z);

            for (BlockFace dir : faces) {
               net.minecraft.server.v1_8_R3.Block nmsBlock = CraftMagicNumbers.getBlock(block.getRelative(dir));
               if (nmsBlock.getMaterial().isBuildable() || BlockDiodeAbstract.d(nmsBlock)) {
                  boolean taken = false;
                  AxisAlignedBB bb = EntityHanging.calculateBoundingBox(pos, CraftBlock.blockFaceToNotch(dir).opposite(), width, height);
                  List<net.minecraft.server.v1_8_R3.Entity> list = this.world.getEntities(null, bb);
                  Iterator<net.minecraft.server.v1_8_R3.Entity> it = list.iterator();

                  while (!taken && it.hasNext()) {
                     net.minecraft.server.v1_8_R3.Entity e = it.next();
                     if (e instanceof EntityHanging) {
                        taken = true;
                     }
                  }

                  if (!taken) {
                     face = dir;
                     break;
                  }
               }
            }

            EnumDirection dirx = CraftBlock.blockFaceToNotch(face).opposite();
            if (Painting.class.isAssignableFrom(clazz)) {
               entity = new EntityPainting(this.world, new BlockPosition((int)x, (int)y, (int)z), dirx);
            } else if (ItemFrame.class.isAssignableFrom(clazz)) {
               entity = new EntityItemFrame(this.world, new BlockPosition((int)x, (int)y, (int)z), dirx);
            } else if (LeashHitch.class.isAssignableFrom(clazz)) {
               entity = new EntityLeash(this.world, new BlockPosition((int)x, (int)y, (int)z));
               entity.attachedToPlayer = true;
            }

            if (entity != null && !((EntityHanging)entity).survives()) {
               throw new IllegalArgumentException("Cannot spawn hanging entity for " + clazz.getName() + " at " + location);
            }
         } else if (TNTPrimed.class.isAssignableFrom(clazz)) {
            org.bukkit.Location loc = new org.bukkit.Location(this.world.getWorld(), x, y, z);
            entity = new EntityTNTPrimed(loc, this.world, x, y, z, null);
         } else if (ExperienceOrb.class.isAssignableFrom(clazz)) {
            entity = new EntityExperienceOrb(this.world, x, y, z, 0);
         } else if (Weather.class.isAssignableFrom(clazz)) {
            if (LightningStrike.class.isAssignableFrom(clazz)) {
               entity = new EntityLightning(this.world, x, y, z);
            }
         } else if (Firework.class.isAssignableFrom(clazz)) {
            entity = new EntityFireworks(this.world, x, y, z, null);
         }

         if (entity != null) {
            if (entity instanceof EntityOcelot) {
               ((EntityOcelot)entity).spawnBonus = false;
            }

            return entity;
         } else {
            throw new IllegalArgumentException("Cannot spawn an entity for " + clazz.getName());
         }
      } else {
         throw new IllegalArgumentException("Location or entity class cannot be null");
      }
   }

   public <T extends Entity> T addEntity(net.minecraft.server.v1_8_R3.Entity entity, CreatureSpawnEvent.SpawnReason reason) throws IllegalArgumentException {
      Preconditions.checkArgument(entity != null, "Cannot spawn null entity");
      if (entity instanceof EntityInsentient) {
         ((EntityInsentient)entity)
            .prepare(this.getHandle().E(MathHelper.floor(entity.locX), MathHelper.floor(entity.locY), MathHelper.floor(entity.locZ)), null);
      }

      this.world.addEntity(entity, reason);
      return (T)entity.getBukkitEntity();
   }

   public <T extends Entity> T spawn(org.bukkit.Location location, Class<T> clazz, CreatureSpawnEvent.SpawnReason reason) throws IllegalArgumentException {
      net.minecraft.server.v1_8_R3.Entity entity = this.createEntity(location, clazz);
      return this.addEntity(entity, reason);
   }

   @Override
   public org.bukkit.ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
      return CraftChunk.getEmptyChunkSnapshot(x, z, this, includeBiome, includeBiomeTempRain);
   }

   @Override
   public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
      this.world.setSpawnFlags(allowMonsters, allowAnimals);
   }

   @Override
   public boolean getAllowAnimals() {
      return this.world.allowAnimals;
   }

   @Override
   public boolean getAllowMonsters() {
      return this.world.allowMonsters;
   }

   @Override
   public int getMaxHeight() {
      return this.world.getHeight();
   }

   @Override
   public int getSeaLevel() {
      return 64;
   }

   @Override
   public boolean getKeepSpawnInMemory() {
      return this.world.keepSpawnInMemory;
   }

   @Override
   public void setKeepSpawnInMemory(boolean keepLoaded) {
      this.world.keepSpawnInMemory = keepLoaded;
      BlockPosition chunkcoordinates = this.world.getSpawn();
      int chunkCoordX = chunkcoordinates.getX() >> 4;
      int chunkCoordZ = chunkcoordinates.getZ() >> 4;

      for (int x = -12; x <= 12; x++) {
         for (int z = -12; z <= 12; z++) {
            if (keepLoaded) {
               this.loadChunk(chunkCoordX + x, chunkCoordZ + z);
            } else if (this.isChunkLoaded(chunkCoordX + x, chunkCoordZ + z)) {
               if (this.getHandle().getChunkAt(chunkCoordX + x, chunkCoordZ + z) instanceof EmptyChunk) {
                  this.unloadChunk(chunkCoordX + x, chunkCoordZ + z, false);
               } else {
                  this.unloadChunk(chunkCoordX + x, chunkCoordZ + z);
               }
            }
         }
      }
   }

   @Override
   public int hashCode() {
      return this.getUID().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftWorld other = (CraftWorld)obj;
         return this.getUID() == other.getUID();
      }
   }

   @Override
   public File getWorldFolder() {
      return this.world.getDataManager().getDirectory();
   }

   @Override
   public void sendPluginMessage(Plugin source, String channel, byte[] message) {
      StandardMessenger.validatePluginMessage(this.server.getMessenger(), source, channel, message);

      for (Player player : this.getPlayers()) {
         player.sendPluginMessage(source, channel, message);
      }
   }

   @Override
   public Set<String> getListeningPluginChannels() {
      Set<String> result = new HashSet<>();

      for (Player player : this.getPlayers()) {
         result.addAll(player.getListeningPluginChannels());
      }

      return result;
   }

   @Override
   public org.bukkit.WorldType getWorldType() {
      return org.bukkit.WorldType.getByName(this.world.getWorldData().getType().name());
   }

   @Override
   public boolean canGenerateStructures() {
      return this.world.getWorldData().shouldGenerateMapFeatures();
   }

   @Override
   public long getTicksPerAnimalSpawns() {
      return this.world.ticksPerAnimalSpawns;
   }

   @Override
   public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
      this.world.ticksPerAnimalSpawns = ticksPerAnimalSpawns;
   }

   @Override
   public long getTicksPerMonsterSpawns() {
      return this.world.ticksPerMonsterSpawns;
   }

   @Override
   public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
      this.world.ticksPerMonsterSpawns = ticksPerMonsterSpawns;
   }

   @Override
   public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
      this.server.getWorldMetadata().setMetadata(this, metadataKey, newMetadataValue);
   }

   @Override
   public List<MetadataValue> getMetadata(String metadataKey) {
      return this.server.getWorldMetadata().getMetadata(this, metadataKey);
   }

   @Override
   public boolean hasMetadata(String metadataKey) {
      return this.server.getWorldMetadata().hasMetadata(this, metadataKey);
   }

   @Override
   public void removeMetadata(String metadataKey, Plugin owningPlugin) {
      this.server.getWorldMetadata().removeMetadata(this, metadataKey, owningPlugin);
   }

   @Override
   public int getMonsterSpawnLimit() {
      return this.monsterSpawn < 0 ? this.server.getMonsterSpawnLimit() : this.monsterSpawn;
   }

   @Override
   public void setMonsterSpawnLimit(int limit) {
      this.monsterSpawn = limit;
   }

   @Override
   public int getAnimalSpawnLimit() {
      return this.animalSpawn < 0 ? this.server.getAnimalSpawnLimit() : this.animalSpawn;
   }

   @Override
   public void setAnimalSpawnLimit(int limit) {
      this.animalSpawn = limit;
   }

   @Override
   public int getWaterAnimalSpawnLimit() {
      return this.waterAnimalSpawn < 0 ? this.server.getWaterAnimalSpawnLimit() : this.waterAnimalSpawn;
   }

   @Override
   public void setWaterAnimalSpawnLimit(int limit) {
      this.waterAnimalSpawn = limit;
   }

   @Override
   public int getAmbientSpawnLimit() {
      return this.ambientSpawn < 0 ? this.server.getAmbientSpawnLimit() : this.ambientSpawn;
   }

   @Override
   public void setAmbientSpawnLimit(int limit) {
      this.ambientSpawn = limit;
   }

   @Override
   public void playSound(org.bukkit.Location loc, org.bukkit.Sound sound, float volume, float pitch) {
      if (loc != null && sound != null) {
         double x = loc.getX();
         double y = loc.getY();
         double z = loc.getZ();
         this.getHandle().makeSound(x, y, z, CraftSound.getSound(sound), volume, pitch);
      }
   }

   @Override
   public String getGameRuleValue(String rule) {
      return this.getHandle().getGameRules().get(rule);
   }

   @Override
   public boolean setGameRuleValue(String rule, String value) {
      if (rule == null || value == null) {
         return false;
      } else if (!this.isGameRule(rule)) {
         return false;
      } else {
         this.getHandle().getGameRules().set(rule, value);
         return true;
      }
   }

   @Override
   public String[] getGameRules() {
      return this.getHandle().getGameRules().getGameRules();
   }

   @Override
   public boolean isGameRule(String rule) {
      return this.getHandle().getGameRules().contains(rule);
   }

   @Override
   public org.bukkit.WorldBorder getWorldBorder() {
      if (this.worldBorder == null) {
         this.worldBorder = new CraftWorldBorder(this);
      }

      return this.worldBorder;
   }

   public void processChunkGC() {
      this.chunkGCTickCount++;
      int chunkGCLoadThreshold = this.server.chunkGCLoadThresh;
      if (this.chunkLoadCount > chunkGCLoadThreshold && chunkGCLoadThreshold > 0) {
         this.chunkLoadCount = 0;
      } else {
         if (this.chunkGCTickCount < this.server.chunkGCPeriod || this.server.chunkGCPeriod <= 0) {
            return;
         }

         this.chunkGCTickCount = 0;
      }

      ChunkProviderServer cps = this.world.chunkProviderServer;

      for (net.minecraft.server.v1_8_R3.Chunk chunk : cps.chunks.values()) {
         if (!this.isChunkInUse(chunk.locX, chunk.locZ) && !cps.unloadQueue.contains(chunk.chunkHashKey)) {
            cps.queueUnload(chunk.chunkHashKey, chunk.locX, chunk.locZ);
         }
      }
   }

   @Override
   public org.bukkit.World.Spigot spigot() {
      return this.spigot;
   }
}
