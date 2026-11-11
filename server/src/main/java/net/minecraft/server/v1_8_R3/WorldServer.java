package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import lol.deyo.blaze.configs.BlazeConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_8_R3.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_8_R3.generator.CustomChunkGenerator;
import org.bukkit.craftbukkit.v1_8_R3.generator.InternalChunkGenerator;
import org.bukkit.craftbukkit.v1_8_R3.generator.NetherChunkGenerator;
import org.bukkit.craftbukkit.v1_8_R3.generator.NormalChunkGenerator;
import org.bukkit.craftbukkit.v1_8_R3.generator.SkyLandsChunkGenerator;
import org.bukkit.craftbukkit.v1_8_R3.util.HashTreeSet;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.generator.ChunkGenerator;

public class WorldServer extends World implements IAsyncTaskHandler {
   private static final Logger a = LogManager.getLogger();
   private static final List<StructurePieceTreasure> U = Lists.newArrayList(
      new StructurePieceTreasure[]{
         new StructurePieceTreasure(Items.STICK, 0, 1, 3, 10),
         new StructurePieceTreasure(Item.getItemOf(Blocks.PLANKS), 0, 1, 3, 10),
         new StructurePieceTreasure(Item.getItemOf(Blocks.LOG), 0, 1, 3, 10),
         new StructurePieceTreasure(Items.STONE_AXE, 0, 1, 1, 3),
         new StructurePieceTreasure(Items.WOODEN_AXE, 0, 1, 1, 5),
         new StructurePieceTreasure(Items.STONE_PICKAXE, 0, 1, 1, 3),
         new StructurePieceTreasure(Items.WOODEN_PICKAXE, 0, 1, 1, 5),
         new StructurePieceTreasure(Items.APPLE, 0, 2, 3, 5),
         new StructurePieceTreasure(Items.BREAD, 0, 2, 3, 3),
         new StructurePieceTreasure(Item.getItemOf(Blocks.LOG2), 0, 1, 3, 10)
      }
   );
   public final int dimension;
   protected final VillageSiege siegeManager = new VillageSiege(this);
   private final MinecraftServer server;
   private final PlayerChunkMap manager;
   private final HashTreeSet<NextTickListEntry> M = new HashTreeSet();
   private final Map<UUID, Entity> entitiesByUUID = Maps.newHashMap();
   private final PortalTravelAgent Q;
   private final SpawnerCreature R = new SpawnerCreature();
   private final WorldServer.BlockActionDataList[] S = new WorldServer.BlockActionDataList[]{
      new WorldServer.BlockActionDataList(null), new WorldServer.BlockActionDataList(null)
   };
   private final List<NextTickListEntry> V = Lists.newArrayList();
   public EntityTracker tracker;
   public ChunkProviderServer chunkProviderServer;
   public boolean savingDisabled;
   private boolean O;
   private int T;
   public boolean ticking;

   public WorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, WorldData worlddata, int i, MethodProfiler methodprofiler) {
      this(minecraftserver, idatamanager, null, i, methodprofiler, null, null);
   }

   public WorldServer(
      MinecraftServer minecraftserver,
      IDataManager idatamanager,
      WorldData worlddata,
      int i,
      MethodProfiler methodprofiler,
      Environment env,
      ChunkGenerator gen
   ) {
      super(idatamanager, worlddata, WorldProvider.byDimension(env.getId()), methodprofiler, false, gen, env);
      this.dimension = i;
      this.pvpMode = minecraftserver.getPVP();
      worlddata.world = this;
      this.server = minecraftserver;
      this.tracker = new EntityTracker(this);
      this.manager = new PlayerChunkMap(this, this.spigotConfig.viewDistance);
      this.worldProvider.a(this);
      this.chunkProvider = this.k();
      this.Q = (CraftTravelAgent)new CraftTravelAgent(this).setSearchRadius(this.paperSpigotConfig.portalSearchRadius);
      this.B();
      this.C();
      this.getWorldBorder().a(minecraftserver.aI());
   }

   public boolean checkTicking() {
      if (!this.players.isEmpty() && !this.ticking) {
         this.ticking = true;
         this.keepSpawnInMemory = this.paperSpigotConfig.keepSpawnInMemory;
      }

      return this.ticking;
   }

   @Override
   public World b() {
      this.worldMaps = new PersistentCollection(this.dataManager);
      String s = PersistentVillage.a(this.worldProvider);
      PersistentVillage persistentvillage = (PersistentVillage)this.worldMaps.get(PersistentVillage.class, s);
      if (persistentvillage == null) {
         this.villages = new PersistentVillage(this);
         this.worldMaps.a(s, this.villages);
      } else {
         this.villages = persistentvillage;
         this.villages.a(this);
      }

      if (this.getServer().getScoreboardManager() == null) {
         this.scoreboard = new ScoreboardServer(this.server);
         PersistentScoreboard persistentscoreboard = (PersistentScoreboard)this.worldMaps.get(PersistentScoreboard.class, "scoreboard");
         if (persistentscoreboard == null) {
            persistentscoreboard = new PersistentScoreboard();
            this.worldMaps.a("scoreboard", persistentscoreboard);
         }

         persistentscoreboard.a(this.scoreboard);
         ((ScoreboardServer)this.scoreboard).a(persistentscoreboard);
      } else {
         this.scoreboard = this.getServer().getScoreboardManager().getMainScoreboard().getHandle();
      }

      this.getWorldBorder().setCenter(this.worldData.C(), this.worldData.D());
      this.getWorldBorder().setDamageAmount(this.worldData.I());
      this.getWorldBorder().setDamageBuffer(this.worldData.H());
      this.getWorldBorder().setWarningDistance(this.worldData.J());
      this.getWorldBorder().setWarningTime(this.worldData.K());
      if (this.worldData.F() > 0L) {
         this.getWorldBorder().transitionSizeBetween(this.worldData.E(), this.worldData.G(), this.worldData.F());
      } else {
         this.getWorldBorder().setSize(this.worldData.E());
      }

      if (this.generator != null) {
         this.getWorld().getPopulators().addAll(this.generator.getDefaultPopulators(this.getWorld()));
      }

      return this;
   }

   @Override
   public TileEntity getTileEntity(BlockPosition pos) {
      return this.getTileEntity(null, pos, this.getType(pos).getBlock());
   }

   @Override
   public TileEntity getTileEntity(Chunk chunk, BlockPosition pos) {
      return this.getTileEntity(chunk, pos, this.getType(pos).getBlock());
   }

   @Override
   public TileEntity getTileEntity(Chunk chunk, BlockPosition pos, Block type) {
      TileEntity result = super.getTileEntity(chunk, pos);
      if (result instanceof TileEntityPiston) {
         return result;
      } else {
         if (type != Blocks.CHEST && type != Blocks.TRAPPED_CHEST) {
            if (type == Blocks.HOPPER) {
               if (!(result instanceof TileEntityHopper)) {
                  result = this.fixTileEntity(pos, type, result);
               }
            } else if (type == Blocks.FURNACE) {
               if (!(result instanceof TileEntityFurnace)) {
                  result = this.fixTileEntity(pos, type, result);
               }
            } else if (type == Blocks.DROPPER) {
               if (!(result instanceof TileEntityDropper)) {
                  result = this.fixTileEntity(pos, type, result);
               }
            } else if (type == Blocks.DISPENSER) {
               if (!(result instanceof TileEntityDispenser)) {
                  result = this.fixTileEntity(pos, type, result);
               }
            } else if (type == Blocks.JUKEBOX) {
               if (!(result instanceof BlockJukeBox.TileEntityRecordPlayer)) {
                  result = this.fixTileEntity(pos, type, result);
               }
            } else if (type == Blocks.NOTEBLOCK) {
               if (!(result instanceof TileEntityNote)) {
                  result = this.fixTileEntity(pos, type, result);
               }
            } else if (type == Blocks.MOB_SPAWNER) {
               if (!(result instanceof TileEntityMobSpawner)) {
                  result = this.fixTileEntity(pos, type, result);
               }
            } else if (type != Blocks.STANDING_SIGN && type != Blocks.WALL_SIGN) {
               if (type == Blocks.ENDER_CHEST) {
                  if (!(result instanceof TileEntityEnderChest)) {
                     result = this.fixTileEntity(pos, type, result);
                  }
               } else if (type == Blocks.BREWING_STAND) {
                  if (!(result instanceof TileEntityBrewingStand)) {
                     result = this.fixTileEntity(pos, type, result);
                  }
               } else if (type == Blocks.BEACON && !(result instanceof TileEntityBeacon)) {
                  result = this.fixTileEntity(pos, type, result);
               }
            } else if (!(result instanceof TileEntitySign)) {
               result = this.fixTileEntity(pos, type, result);
            }
         } else if (!(result instanceof TileEntityChest)) {
            result = this.fixTileEntity(pos, type, result);
         }

         return result;
      }
   }

   private TileEntity fixTileEntity(BlockPosition pos, Block type, TileEntity found) {
      this.getServer()
         .getLogger()
         .log(
            Level.SEVERE,
            "Block at {0},{1},{2} is {3} but has {4}. Bukkit will attempt to fix this, but there may be additional damage that we cannot recover.",
            new Object[]{pos.getX(), pos.getY(), pos.getZ(), Objects.requireNonNull(org.bukkit.Material.getMaterial(Block.getId(type))).toString(), found}
         );
      if (type instanceof IContainer) {
         TileEntity replacement = ((IContainer)type).a(this, type.toLegacyData(type.getBlockData()));
         replacement.world = this;
         this.setTileEntity(pos, replacement);
         return replacement;
      } else {
         this.getServer().getLogger().severe("Don't know how to fix for this type... Can't do anything! :(");
         return found;
      }
   }

   private boolean canSpawn(int x, int z) {
      return this.generator != null ? this.generator.canSpawn(this.getWorld(), x, z) : this.worldProvider.canSpawn(x, z);
   }

   @Override
   public void doTick() {
      if ((Boolean)BlazeConfig.tickWeather.getValue()) {
         super.doTick();
      }

      if ((Boolean)BlazeConfig.tickBiomeCache.getValue()) {
         this.worldProvider.m().b();
      }

      boolean dayLightCycle = this.getGameRules().getBoolean("doDaylightCycle");
      if ((Boolean)BlazeConfig.checkPlayerSleep.getValue() && this.everyoneDeeplySleeping()) {
         if (dayLightCycle) {
            long i = this.worldData.getDayTime() + 24000L;
            this.worldData.setDayTime(i - i % 24000L);
         }

         this.e();
      }

      long time = this.worldData.getTime();
      if ((Boolean)BlazeConfig.naturalMobSpawn.getValue() && (this.allowMonsters || this.allowAnimals) && !this.players.isEmpty()) {
         this.R
            .a(
               this,
               this.allowMonsters && this.ticksPerMonsterSpawns != 0L && time % this.ticksPerMonsterSpawns == 0L,
               this.allowAnimals && this.ticksPerAnimalSpawns != 0L && time % this.ticksPerAnimalSpawns == 0L,
               this.worldData.getTime() % 400L == 0L
            );
      }

      if ((Boolean)BlazeConfig.chunkUnloading.getValue()) {
         this.chunkProvider.unloadChunks();
      }

      int j = this.a(1.0F);
      if (j != this.ab()) {
         this.c(j);
      }

      this.worldData.setTime(time + 1L);
      if (dayLightCycle) {
         this.worldData.setDayTime(this.worldData.getDayTime() + 1L);
      }

      if ((Boolean)BlazeConfig.tickBlocks.getValue()) {
         this.a(false);
      }

      if ((Boolean)BlazeConfig.tickChunks.getValue()) {
         this.h();
      }

      this.spigotConfig.antiXrayInstance.flushUpdates(this);
      this.manager.flush();
      if ((Boolean)BlazeConfig.tickVillages.getValue()) {
         this.villages.tick();
         this.siegeManager.a();
      }

      this.Q.a(this.getTime());
      this.ak();
      if ((Boolean)BlazeConfig.chunkUnloading.getValue()) {
         this.getWorld().processChunkGC();
      }
   }

   public BiomeBase.BiomeMeta a(List<BiomeBase.BiomeMeta> mobs) {
      return mobs != null && !mobs.isEmpty() ? WeightedRandom.a(this.random, mobs) : null;
   }

   public boolean a(List<BiomeBase.BiomeMeta> mobs, BiomeBase.BiomeMeta biomebase_biomemeta) {
      return mobs != null && !mobs.isEmpty() && mobs.contains(biomebase_biomemeta);
   }

   public BiomeBase.BiomeMeta a(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
      List<BiomeBase.BiomeMeta> list = this.N().getMobsFor(enumcreaturetype, blockposition);
      return list != null && !list.isEmpty() ? WeightedRandom.a(this.random, list) : null;
   }

   public boolean a(EnumCreatureType enumcreaturetype, BiomeBase.BiomeMeta biomebase_biomemeta, BlockPosition blockposition) {
      List<BiomeBase.BiomeMeta> list = this.N().getMobsFor(enumcreaturetype, blockposition);
      return list != null && !list.isEmpty() && list.contains(biomebase_biomemeta);
   }

   @Override
   public void everyoneSleeping() {
      if ((Boolean)BlazeConfig.checkPlayerSleep.getValue()) {
         this.O = false;
         if (!this.players.isEmpty()) {
            int i = 0;
            int j = 0;

            for (EntityHuman entityhuman : this.players) {
               if (entityhuman.isSpectator()) {
                  i++;
               } else if (entityhuman.isSleeping() || entityhuman.fauxSleeping) {
                  j++;
               }
            }

            this.O = j > 0 && j >= this.players.size() - i;
         }
      }
   }

   protected void e() {
      this.O = false;

      for (EntityHuman entityhuman : this.players) {
         if (entityhuman.isSleeping()) {
            entityhuman.a(false, false, true);
         }
      }

      this.ag();
   }

   private void ag() {
      this.worldData.setStorm(false);
      if (!this.worldData.hasStorm()) {
         this.worldData.setWeatherDuration(0);
      }

      this.worldData.setThundering(false);
      if (!this.worldData.isThundering()) {
         this.worldData.setThunderDuration(0);
      }
   }

   public boolean everyoneDeeplySleeping() {
      if (this.O) {
         Iterator<EntityHuman> iterator = this.players.iterator();
         boolean foundActualSleepers = false;

         EntityHuman entityhuman;
         do {
            if (!iterator.hasNext()) {
               return foundActualSleepers;
            }

            entityhuman = iterator.next();
            if (entityhuman.isDeeplySleeping()) {
               foundActualSleepers = true;
            }
         } while (!entityhuman.isSpectator() || entityhuman.isDeeplySleeping() || entityhuman.fauxSleeping);
      }

      return false;
   }

   @Override
   protected void h() {
      super.h();
      int randomTickSpeed = (Integer)BlazeConfig.randomTickSpeed.getValue();
      boolean triggerFormEvent = BlockFormEvent.getHandlerList().getRegisteredListeners().length > 0;
      boolean isRaining = this.S();
      boolean isThundering = this.R();

      for (int chunkI = 0; chunkI < this.chunkTickList.size(); chunkI++) {
         Chunk chunk = this.chunkTickList.get(chunkI);
         int worldX = chunk.locX * 16;
         int worldZ = chunk.locZ * 16;
         chunk.m();
         chunk.b(false);
         if (!this.paperSpigotConfig.disableThunder && isRaining && isThundering && this.simpleRandom.nextInt(100000) == 0) {
            this.m = this.m * 3 + 1013904223;
            int i = this.m >> 2;
            BlockPosition blockposition = this.a(worldX + (i & 15), worldZ + (i >> 8 & 15));
            if (this.isRainingAt(blockposition)) {
               this.strikeLightning(new EntityLightning(this, blockposition.getX(), blockposition.getY(), blockposition.getZ()));
            }
         }

         if (!this.paperSpigotConfig.disableIceAndSnow && this.simpleRandom.nextInt(16) == 0) {
            this.m = this.m * 3 + 1013904223;
            int i = this.m >> 2;
            int xSection = i & 15;
            int zSection = i >> 8 & 15;
            int y = chunk.getPrecipitationHeight(xSection, zSection);
            int yDown = y - 1;
            int x = xSection | worldX;
            int z = zSection | worldZ;
            BiomeBase biome = chunk.getBiome(x, z, this.worldProvider.m());
            if (this.canBlockFreeze(chunk, biome, x, yDown, z)) {
               org.bukkit.block.BlockState blockState = this.getWorld().getBlockAt(x, yDown, z).getState();
               blockState.setTypeId(Block.getId(Blocks.ICE));
               if (triggerFormEvent) {
                  BlockFormEvent iceBlockForm = new BlockFormEvent(blockState.getBlock(), blockState);
                  this.getServer().getPluginManager().callEvent(iceBlockForm);
                  if (!iceBlockForm.isCancelled()) {
                     blockState.update(true);
                  }
               }
            }

            if (isRaining) {
               if (this.canSnowAt(chunk, biome, x, y, z, true)) {
                  org.bukkit.block.BlockState blockState = this.getWorld().getBlockAt(x, y, z).getState();
                  blockState.setTypeId(Block.getId(Blocks.SNOW_LAYER));
                  if (triggerFormEvent) {
                     BlockFormEvent snow = new BlockFormEvent(blockState.getBlock(), blockState);
                     this.getServer().getPluginManager().callEvent(snow);
                     if (!snow.isCancelled()) {
                        blockState.update(true);
                     }
                  }
               }

               if (biome.e()) {
                  this.getType(x, yDown, z).getBlock().k(this, new BlockPosition(x, yDown, z));
               }
            }
         }

         if (randomTickSpeed > 0) {
            ChunkSection[] var24;
            for (ChunkSection section : var24 = chunk.getSections()) {
               if (section != null) {
                  int tickingBlocks = section.getTickingBlockCount();
                  if (tickingBlocks != 0) {
                     for (int l1 = 0; l1 < randomTickSpeed; l1++) {
                        int index = this.simpleRandom.nextInt() & 4095;
                        if (index < tickingBlocks) {
                           short loc = section.getTickList().getRaw(index);
                           IBlockData iblockdata = section.getType(loc);
                           Block block = iblockdata.getBlock();
                           block.optimizedTick(
                              chunk, loc & 15 | worldX, loc >> 8 & 15 | section.getYPosition(), loc >> 4 & 15 | worldZ, iblockdata, this.random
                           );
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.spigotConfig.clearChunksOnTick) {
         this.chunkTickList.clear();
      }
   }

   protected BlockPosition a(BlockPosition blockposition) {
      return this.a(blockposition.getX(), blockposition.getZ());
   }

   protected BlockPosition a(int x, int z) {
      BlockPosition blockposition1 = this.q(x, z);
      AxisAlignedBB axisalignedbb = new AxisAlignedBB(
            blockposition1.getX(), blockposition1.getY(), blockposition1.getZ(), blockposition1.getX(), this.getHeight(), blockposition1.getZ()
         )
         .grow(3.0, 3.0, 3.0);
      List<EntityLiving> list = this.a(
         EntityLiving.class, axisalignedbb, (com.google.common.base.Predicate<EntityLiving>)(entityliving -> entityliving != null && entityliving.isAlive() && this.i(entityliving.getChunkCoordinates()))
      );
      return !list.isEmpty() ? list.get(this.random.nextInt(list.size())).getChunkCoordinates() : blockposition1;
   }

   @Override
   public boolean a(BlockPosition blockposition, Block block) {
      return this.V.contains(new NextTickListEntry(blockposition, block));
   }

   @Override
   public void a(BlockPosition blockposition, Block block, int i) {
      this.a(blockposition, block, i, 0);
   }

   @Override
   public void a(BlockPosition blockposition, Block block, int i, int j) {
      this.a(blockposition, null, block, i, j);
   }

   @Override
   public void a(BlockPosition blockposition, Chunk chunk, Block block, int i, int j) {
      NextTickListEntry nextticklistentry = new NextTickListEntry(blockposition, block);
      if (this.e && block.getMaterial() != Material.AIR) {
         if (block.N()) {
            if (this.areChunksLoadedBetween(blockposition.a(-8, -8, -8), blockposition.a(8, 8, 8))) {
               if (chunk == null) {
                  chunk = this.getChunkAtWorldCoords(blockposition);
               }

               IBlockData iblockdata = this.getType(chunk, blockposition);
               Block iblock = iblockdata.getBlock();
               if (iblock.getMaterial() != Material.AIR && iblock == nextticklistentry.a()) {
                  iblock.b(this, blockposition, iblockdata, this.random);
               }
            }

            return;
         }

         i = 1;
      }

      if (chunk == null) {
         chunk = this.getChunkIfLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4);
      }

      if (chunk != null) {
         if (block.getMaterial() != Material.AIR) {
            nextticklistentry.a(i + this.worldData.getTime());
            nextticklistentry.a(j);
         }

         if (!this.M.contains(nextticklistentry)) {
            this.M.add(nextticklistentry);
         }
      }
   }

   @Override
   public void b(BlockPosition blockposition, Block block, int i, int j) {
      NextTickListEntry nextticklistentry = new NextTickListEntry(blockposition, block);
      nextticklistentry.a(j);
      if (block.getMaterial() != Material.AIR) {
         nextticklistentry.a(i + this.worldData.getTime());
      }

      if (!this.M.contains(nextticklistentry)) {
         this.M.add(nextticklistentry);
      }
   }

   @Override
   public boolean a(boolean flag) {
      int i = this.M.size();
      if (i > this.paperSpigotConfig.tickNextTickCap) {
         i = this.paperSpigotConfig.tickNextTickCap;
      }

      for (int j = 0; j < i; j++) {
         NextTickListEntry nextticklistentry = (NextTickListEntry)this.M.first();
         if (!flag && nextticklistentry.b > this.worldData.getTime()) {
            break;
         }

         this.M.remove(nextticklistentry);
         this.V.add(nextticklistentry);
      }

      if (this.paperSpigotConfig.tickNextTickListCapIgnoresRedstone) {
         Iterator<NextTickListEntry> iterator1 = this.M.iterator();

         while (iterator1.hasNext()) {
            NextTickListEntry next = iterator1.next();
            if (!flag && next.b > this.worldData.getTime()) {
               break;
            }

            Block block = next.a();
            if (block.isPowerSource() || block instanceof IContainer || block instanceof BlockFalling) {
               iterator1.remove();
               this.V.add(next);
            }
         }
      }

      Iterator<NextTickListEntry> iterator = this.V.iterator();
      Chunk previousChunk = null;
      int previousChunkX = -1;
      int previousChunkZ = -1;

      while (iterator.hasNext()) {
         NextTickListEntry nextticklistentry = iterator.next();
         iterator.remove();
         BlockPosition position = nextticklistentry.a;
         int chunkX = position.getX() >> 4;
         int chunkZ = position.getZ() >> 4;
         Chunk chunk;
         if (previousChunkX == chunkX && previousChunkZ == chunkZ) {
            chunk = previousChunk;
         } else {
            chunk = this.getChunkIfLoaded(chunkX, chunkZ);
            previousChunk = chunk;
            previousChunkX = chunkX;
            previousChunkZ = chunkZ;
         }

         if (chunk != null) {
            IBlockData iblockdata = this.getType(chunk, position);
            Block block = iblockdata.getBlock();
            if (block.getMaterial() != Material.AIR && Block.a(block, nextticklistentry.a())) {
               try {
                  block.b(this, position, iblockdata, this.random);
               } catch (Throwable var17) {
                  CrashReport crashreport = CrashReport.a(var17, "Exception while ticking a block");
                  CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being ticked");
                  CrashReportSystemDetails.a(crashreportsystemdetails, position, iblockdata);
                  throw new ReportedException(crashreport);
               }
            }
         } else {
            this.a(position, nextticklistentry.a(), 0);
         }
      }

      this.dispatch();
      this.V.clear();
      return !this.M.isEmpty();
   }

   @Override
   public List<NextTickListEntry> a(Chunk chunk, boolean flag) {
      ChunkCoordIntPair chunkcoordintpair = chunk.j();
      int i = (chunkcoordintpair.x << 4) - 2;
      int k = (chunkcoordintpair.z << 4) - 2;
      return this.a(new StructureBoundingBox(i, 0, k, i + 16 + 2, 256, k + 16 + 2), flag);
   }

   @Override
   public List<NextTickListEntry> a(StructureBoundingBox structureboundingbox, boolean flag) {
      ArrayList<NextTickListEntry> arraylist = null;

      for (int i = 0; i < 2; i++) {
         Iterator<NextTickListEntry> iterator;
         if (i == 0) {
            iterator = this.M.iterator();
         } else {
            iterator = this.V.iterator();
         }

         while (iterator.hasNext()) {
            NextTickListEntry nextticklistentry = iterator.next();
            BlockPosition blockposition = nextticklistentry.a;
            if (blockposition.getX() >= structureboundingbox.a
               && blockposition.getX() < structureboundingbox.d
               && blockposition.getZ() >= structureboundingbox.c
               && blockposition.getZ() < structureboundingbox.f) {
               if (flag) {
                  iterator.remove();
               }

               if (arraylist == null) {
                  arraylist = Lists.newArrayList();
               }

               arraylist.add(nextticklistentry);
            }
         }
      }

      return arraylist;
   }

   @Override
   protected IChunkProvider k() {
      IChunkLoader ichunkloader = this.dataManager.createChunkLoader(this.worldProvider);
      InternalChunkGenerator gen;
      if (this.generator != null) {
         gen = new CustomChunkGenerator(this, this.getSeed(), this.generator);
      } else if (this.worldProvider instanceof WorldProviderHell) {
         gen = new NetherChunkGenerator(this, this.getSeed());
      } else if (this.worldProvider instanceof WorldProviderTheEnd) {
         gen = new SkyLandsChunkGenerator(this, this.getSeed());
      } else {
         gen = new NormalChunkGenerator(this, this.getSeed());
      }

      this.chunkProviderServer = new ChunkProviderServer(this, ichunkloader, gen);
      return this.chunkProviderServer;
   }

   public List<TileEntity> getTileEntities(int i, int j, int k, int l, int i1, int j1) {
      List<TileEntity> arraylist = Lists.newArrayList();
      int minX = i >> 4;
      int minZ = k >> 4;
      int maxX = l - 1 >> 4;
      int maxZ = j1 - 1 >> 4;

      for (int chunkX = minX; chunkX <= maxX; chunkX++) {
         for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
            Chunk chunk = this.getChunkAt(chunkX, chunkZ);
            if (chunk != null) {
               for (TileEntity tileentity : chunk.tileEntities.values()) {
                  if (tileentity.position.getX() >= i
                     && tileentity.position.getY() >= j
                     && tileentity.position.getZ() >= k
                     && tileentity.position.getX() < l
                     && tileentity.position.getY() < i1
                     && tileentity.position.getZ() < j1) {
                     arraylist.add(tileentity);
                  }
               }
            }
         }
      }

      return arraylist;
   }

   @Override
   public boolean a(EntityHuman entityhuman, BlockPosition blockposition) {
      return !this.server.a(this, blockposition, entityhuman) && this.getWorldBorder().a(blockposition);
   }

   @Override
   public void a(WorldSettings worldsettings) {
      if (!this.worldData.w()) {
         try {
            this.b(worldsettings);
            super.a(worldsettings);
         } catch (Throwable var6) {
            CrashReport crashreport = CrashReport.a(var6, "Exception initializing level");

            try {
               this.a(crashreport);
            } catch (Throwable var5) {
            }

            throw new ReportedException(crashreport);
         }

         this.worldData.d(true);
      }
   }

   private void b(WorldSettings worldsettings) {
      if (!this.worldProvider.e()) {
         this.worldData.setSpawn(BlockPosition.ZERO.up(this.worldProvider.getSeaLevel()));
      } else {
         this.isLoading = true;
         WorldChunkManager worldchunkmanager = this.worldProvider.m();
         List<BiomeBase> list = worldchunkmanager.a();
         Random random = new Random(this.getSeed());
         BlockPosition blockposition = worldchunkmanager.a(0, 0, 256, list, random);
         int i = 0;
         int j = this.worldProvider.getSeaLevel();
         int k = 0;
         if (this.generator != null) {
            Random rand = new Random(this.getSeed());
            Location spawn = this.generator.getFixedSpawnLocation(this.getWorld(), rand);
            if (spawn != null) {
               if (spawn.getWorld() != this.getWorld()) {
                  throw new IllegalStateException(
                     "Cannot set spawn point for " + this.worldData.getName() + " to be in another world (" + spawn.getWorld().getName() + ")"
                  );
               }

               this.worldData.setSpawn(new BlockPosition(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()));
               this.isLoading = false;
               return;
            }
         }

         if (blockposition != null) {
            i = blockposition.getX();
            k = blockposition.getZ();
         } else {
            a.warn("Unable to find spawn biome");
         }

         int l = 0;

         while (!this.canSpawn(i, k)) {
            i += random.nextInt(64) - random.nextInt(64);
            k += random.nextInt(64) - random.nextInt(64);
            if (++l == 1000) {
               break;
            }
         }

         this.worldData.setSpawn(new BlockPosition(i, j, k));
         this.isLoading = false;
         if (worldsettings.c()) {
            this.l();
         }
      }
   }

   protected void l() {
      WorldGenBonusChest worldgenbonuschest = new WorldGenBonusChest(U, 10);

      for (int i = 0; i < 10; i++) {
         int j = this.worldData.c() + this.random.nextInt(6) - this.random.nextInt(6);
         int k = this.worldData.e() + this.random.nextInt(6) - this.random.nextInt(6);
         BlockPosition blockposition = this.r(j, k).up();
         if (worldgenbonuschest.generate(this, this.random, blockposition)) {
            break;
         }
      }
   }

   public BlockPosition getDimensionSpawn() {
      return this.worldProvider.h();
   }

   public void save(boolean flag, IProgressUpdate iprogressupdate) throws ExceptionWorldConflict {
      if (this.chunkProvider.canSave()) {
         Bukkit.getPluginManager().callEvent(new WorldSaveEvent(this.getWorld()));
         if (iprogressupdate != null) {
            iprogressupdate.a("Saving level");
         }

         this.a();
         if (iprogressupdate != null) {
            iprogressupdate.c("Saving chunks");
         }

         this.chunkProvider.saveChunks(flag, iprogressupdate);

         for (Chunk chunk : this.chunkProviderServer.a()) {
            if (chunk != null && !this.manager.a(chunk.locX, chunk.locZ)) {
               this.chunkProviderServer.queueUnload(chunk.locX, chunk.locZ);
            }
         }
      }
   }

   public void flushSave() {
      if (this.chunkProvider.canSave()) {
         this.chunkProvider.c();
      }
   }

   protected void a() throws ExceptionWorldConflict {
      this.checkSession();
      this.worldData.a(this.getWorldBorder().getSize());
      this.worldData.d(this.getWorldBorder().getCenterX());
      this.worldData.c(this.getWorldBorder().getCenterZ());
      this.worldData.e(this.getWorldBorder().getDamageBuffer());
      this.worldData.f(this.getWorldBorder().getDamageAmount());
      this.worldData.j(this.getWorldBorder().getWarningDistance());
      this.worldData.k(this.getWorldBorder().getWarningTime());
      this.worldData.b(this.getWorldBorder().j());
      this.worldData.e(this.getWorldBorder().i());
      if (!(this instanceof SecondaryWorldServer)) {
         this.worldMaps.a();
      }

      this.dataManager.saveWorldData(this.worldData, this.server.getPlayerList().t());
   }

   @Override
   protected void a(Entity entity) {
      super.a(entity);
      this.entitiesById.a(entity.getId(), entity);
      if (!(entity instanceof EntityTNTPrimed) && !(entity instanceof EntityFallingBlock)) {
         this.entitiesByUUID.put(entity.getUniqueID(), entity);
         Entity[] aentity = entity.aB();
         if (aentity != null) {
            for (Entity value : aentity) {
               this.entitiesById.a(value.getId(), value);
            }
         }
      }
   }

   @Override
   protected void b(Entity entity) {
      super.b(entity);
      this.entitiesById.d(entity.getId());
      if (!(entity instanceof EntityTNTPrimed) && !(entity instanceof EntityFallingBlock)) {
         this.entitiesByUUID.remove(entity.getUniqueID());
         Entity[] aentity = entity.aB();
         if (aentity != null) {
            for (Entity value : aentity) {
               this.entitiesById.d(value.getId());
            }
         }
      }
   }

   @Override
   public boolean strikeLightning(Entity entity) {
      LightningStrikeEvent lightning = new LightningStrikeEvent(this.getWorld(), (LightningStrike)entity.getBukkitEntity());
      this.getServer().getPluginManager().callEvent(lightning);
      if (lightning.isCancelled()) {
         return false;
      } else if (super.strikeLightning(entity)) {
         this.server
            .getPlayerList()
            .sendPacketNearby(entity.locX, entity.locY, entity.locZ, 512.0, this.dimension, new PacketPlayOutSpawnEntityWeather(entity));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void broadcastEntityEffect(Entity entity, byte b0) {
      this.getTracker().sendPacketToEntity(entity, new PacketPlayOutEntityStatus(entity, b0));
   }

   @Override
   public Explosion createExplosion(Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
      Explosion explosion = super.createExplosion(entity, d0, d1, d2, f, flag, flag1);
      if (explosion.wasCanceled) {
         return explosion;
      } else {
         if (!flag1) {
            explosion.clearBlocks();
         }

         if ((Boolean)BlazeConfig.explosionParticles.getValue()) {
            for (EntityHuman entityhuman : this.players) {
               if (entityhuman.e(d0, d1, d2) < 4096.0) {
                  entityhuman.sendPacket(new PacketPlayOutExplosion(d0, d1, d2, f, explosion.getBlocks(), explosion.b().get(entityhuman)));
               }
            }
         }

         return explosion;
      }
   }

   @Override
   public void playBlockAction(BlockPosition blockposition, Block block, int i, int j) {
      BlockActionData blockactiondata = new BlockActionData(blockposition, block, i, j);

      for (BlockActionData blockactiondata1 : this.S[this.T]) {
         if (blockactiondata1.equals(blockactiondata)) {
            return;
         }
      }

      this.S[this.T].add(blockactiondata);
   }

   private void ak() {
      while (!this.S[this.T].isEmpty()) {
         int i = this.T;
         this.T ^= 1;

         for (BlockActionData blockactiondata : this.S[i]) {
            if (this.a(blockactiondata)
               && (!(Boolean)BlazeConfig.hidePistonAnimations.getValue() || !(blockactiondata.d() instanceof BlockPiston))
               && !(blockactiondata.d() instanceof BlockPistonExtension)) {
               this.server
                  .getPlayerList()
                  .sendPacketNearby(
                     blockactiondata.a().getX(),
                     blockactiondata.a().getY(),
                     blockactiondata.a().getZ(),
                     64.0,
                     this.dimension,
                     new PacketPlayOutBlockAction(blockactiondata.a(), blockactiondata.d(), blockactiondata.b(), blockactiondata.c())
                  );
            }
         }

         this.S[i].clear();
      }
   }

   private boolean a(BlockActionData blockactiondata) {
      IBlockData iblockdata = this.getType(blockactiondata.a());
      return iblockdata.getBlock() == blockactiondata.d()
         && iblockdata.getBlock().a(this, blockactiondata.a(), iblockdata, blockactiondata.b(), blockactiondata.c());
   }

   public void saveLevel() {
      this.dataManager.a();
   }

   @Override
   protected void p() {
      boolean flag = this.S();
      super.p();
      if (flag != this.S()) {
         for (EntityHuman player : this.players) {
            if (player.world == this) {
               ((EntityPlayer)player).setPlayerWeather(!flag ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
            }
         }
      }

      for (EntityHuman playerx : this.players) {
         if (playerx.world == this) {
            ((EntityPlayer)playerx).updateWeather(this.o, this.p, this.q, this.r);
         }
      }
   }

   @Override
   protected int q() {
      return this.server.getPlayerList().s();
   }

   public MinecraftServer getMinecraftServer() {
      return this.server;
   }

   public EntityTracker getTracker() {
      return this.tracker;
   }

   public PlayerChunkMap getPlayerChunkMap() {
      return this.manager;
   }

   public PortalTravelAgent getTravelAgent() {
      return this.Q;
   }

   public void a(EnumParticle enumparticle, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, int... aint) {
      this.a(enumparticle, false, d0, d1, d2, i, d3, d4, d5, d6, aint);
   }

   public void a(EnumParticle enumparticle, boolean flag, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, int... aint) {
      this.sendParticles(null, enumparticle, flag, d0, d1, d2, i, d3, d4, d5, d6, aint);
   }

   public void sendParticles(
      EntityPlayer sender,
      EnumParticle enumparticle,
      boolean flag,
      double d0,
      double d1,
      double d2,
      int i,
      double d3,
      double d4,
      double d5,
      double d6,
      int... aint
   ) {
      PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(
         enumparticle, flag, (float)d0, (float)d1, (float)d2, (float)d3, (float)d4, (float)d5, (float)d6, i, aint
      );

      for (EntityHuman entityHuman : this.players) {
         EntityPlayer player = (EntityPlayer)entityHuman;
         if (sender == null || player.getBukkitEntity().canSee(sender.getBukkitEntity())) {
            BlockPosition blockposition = player.getChunkCoordinates();
            double d7 = blockposition.c(d0, d1, d2);
            if (d7 <= 256.0 || flag && d7 <= 65536.0) {
               player.playerConnection.sendPacket(packetplayoutworldparticles);
            }
         }
      }
   }

   public Entity getEntity(UUID uuid) {
      return this.entitiesByUUID.get(uuid);
   }

   @Override
   public ListenableFuture<Object> postToMainThread(Runnable runnable) {
      return this.server.postToMainThread(runnable);
   }

   @Override
   public boolean isMainThread() {
      return this.server.isMainThread();
   }

   static class BlockActionDataList extends ArrayList<BlockActionData> {
      private BlockActionDataList() {
      }

      BlockActionDataList(Object object) {
         this();
      }
   }
}
