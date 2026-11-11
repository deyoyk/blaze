package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.generator.Generator;
import lol.deyo.blaze.optimizations.ChunkTickList;
import lol.deyo.blaze.optimizations.OptimizedNonEmptyList;
import lol.deyo.blaze.optimizations.PlayerMap;
import lol.deyo.blaze.optimizations.SimpleRandom;
import lol.deyo.blaze.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.generator.ChunkGenerator;
import org.github.paperspigot.PaperSpigotWorldConfig;
import org.github.paperspigot.PaperWorldEntityList;
import org.github.paperspigot.event.ServerExceptionEvent;
import org.github.paperspigot.exception.ServerInternalException;
import org.spigotmc.ActivationRange;
import org.spigotmc.AsyncCatcher;
import org.spigotmc.SpigotWorldConfig;

public abstract class World implements IBlockAccess {
   public static final Explosion emptyExplosion = new Explosion(null, null, 0.0, 0.0, 0.0, 0.0F, false, false).cancel();
   public static boolean haveWeSilencedAPhysicsCrash;
   public static String blockLocation;
   public List<TileEntity> tileEntityList = Lists.newArrayList();
   public List<EntityHuman> players = Lists.newArrayList();
   public List<Entity> k = Lists.newArrayList();
   public MethodProfiler methodProfiler;
   public Random random = new Random();
   public SimpleRandom simpleRandom = new SimpleRandom(0L);
   public boolean isClientSide;
   public final Int2FloatMap explosionDensityCache = new Int2FloatOpenHashMap();
   public final Map<BlockPosition, IBlockData> rayTraceCache = Maps.newHashMap();
   private final Queue<Entity> queuedSpawn = new ArrayDeque<>();
   public final CannonCache movementCache = new CannonCache();
   public SpigotWorldConfig spigotConfig;
   public PaperSpigotWorldConfig paperSpigotConfig;
   public Generator generatorConfig;
   protected Set<Entity> g = new ArraySet<>();
   protected IntHashMap<Entity> entitiesById = new IntHashMap<>();
   protected int n = 1013904223;
   protected IDataManager dataManager;
   public ChunkTickList chunkTickList;
   private final List<TileEntity> b = Lists.newArrayList();
   private final Set<TileEntity> c = new ArraySet<>();
   private final Calendar K = Calendar.getInstance();
   private final WorldBorder N;
   private final CraftWorld world;
   private final List<Runnable> interceptedSounds = new ArrayList<>();
   public byte chunkTickRadius;
   public WorldProvider worldProvider;
   public WorldData worldData;
   public PersistentCollection worldMaps;
   public Scoreboard scoreboard = new Scoreboard();
   public boolean allowMonsters;
   public boolean allowAnimals;
   public boolean pvpMode;
   public boolean keepSpawnInMemory;
   public ChunkGenerator generator;
   public boolean captureBlockStates = false;
   public boolean captureTreeGeneration = false;
   public ArrayList<org.bukkit.block.BlockState> capturedBlockStates = new ArrayList<org.bukkit.block.BlockState>() {
      public boolean add(org.bukkit.block.BlockState blockState) {
         for (org.bukkit.block.BlockState blockState1 : this) {
            if (blockState1.getLocation().equals(blockState.getLocation())) {
               return false;
            }
         }

         return super.add(blockState);
      }
   };
   public long ticksPerAnimalSpawns;
   public long ticksPerMonsterSpawns;
   public boolean populating;
   public ExecutorService lightingExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Paper - Lighting Thread").build());
   public Map<BlockPosition, TileEntity> capturedTileEntities = Maps.newHashMap();
   protected boolean e;
   protected int m = this.random.nextInt();
   protected float o;
   protected float p;
   protected float q;
   protected float r;
   protected List<IWorldAccess> u = Lists.newArrayList();
   protected IChunkProvider chunkProvider;
   protected boolean isLoading;
   protected PersistentVillage villages;
   protected float growthOdds = 100.0F;
   protected float modifiedOdds = 100.0F;
   int[] H;
   private int a = 63;
   public int I;
   private boolean M;
   private int tickPosition;
   public boolean guardEntityList;
   public List<Entity> entityList = new PaperWorldEntityList(this);
   private boolean interceptSounds;
   public final PlayerMap playerMap = new PlayerMap();

   protected World(
      IDataManager idatamanager,
      WorldData worlddata,
      WorldProvider worldprovider,
      MethodProfiler methodprofiler,
      boolean flag,
      ChunkGenerator gen,
      Environment env
   ) {
      this.explosionDensityCache.defaultReturnValue(-1.0F);
      this.generatorConfig = env == Environment.NORMAL
         ? Blaze.getInstance().getGeneratorManager().getOrCreate(worlddata.getName())
         : Blaze.getInstance().getGeneratorManager().getDefaultConfig();
      this.spigotConfig = new SpigotWorldConfig(worlddata.getName());
      this.paperSpigotConfig = new PaperSpigotWorldConfig(worlddata.getName());
      this.generator = gen;
      this.world = new CraftWorld((WorldServer)this, gen, env);
      this.ticksPerAnimalSpawns = this.getServer().getTicksPerAnimalSpawns();
      this.ticksPerMonsterSpawns = this.getServer().getTicksPerMonsterSpawns();
      this.chunkTickRadius = (byte)(this.getServer().getViewDistance() < 7 ? this.getServer().getViewDistance() : 7);
      this.chunkTickList = new ChunkTickList();
      this.methodProfiler = methodprofiler;
      this.allowMonsters = true;
      this.allowAnimals = true;
      this.H = new int[32768];
      this.dataManager = idatamanager;
      this.worldData = worlddata;
      this.worldProvider = worldprovider;
      this.isClientSide = false;
      this.N = worldprovider.getWorldBorder();
      this.N
         .a(
            new IWorldBorderListener() {
               @Override
               public void a(WorldBorder worldborder, double d0) {
                  World.this.getServer()
                     .getHandle()
                     .sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE), World.this);
               }

               @Override
               public void a(WorldBorder worldborder, double d0, double d1, long i) {
                  World.this.getServer()
                     .getHandle()
                     .sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE), World.this);
               }

               @Override
               public void a(WorldBorder worldborder, double d0, double d1) {
                  World.this.getServer()
                     .getHandle()
                     .sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER), World.this);
               }

               @Override
               public void a(WorldBorder worldborder, int i) {
                  World.this.getServer()
                     .getHandle()
                     .sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_TIME), World.this);
               }

               @Override
               public void b(WorldBorder worldborder, int i) {
                  World.this.getServer()
                     .getHandle()
                     .sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS), World.this);
               }

               @Override
               public void b(WorldBorder worldborder, double d0) {
               }

               @Override
               public void c(WorldBorder worldborder, double d0) {
               }
            }
         );
      this.getServer().addWorld(this.world);
      this.keepSpawnInMemory = this.paperSpigotConfig.keepSpawnInMemory;
   }

   public static long chunkToKey(int x, int z) {
      return LongHash.toLong(x, z);
   }

   public static int keyToX(long k) {
      return (int)(k >> 16 & 4294901760L | k & 65535L);
   }

   public static int keyToZ(long k) {
      return (int)(k >> 32 & 4294901760L | k >> 16 & 65535L);
   }

   public static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public static boolean a(IBlockAccess iblockaccess, int x, int y, int z) {
      IBlockData iblockdata = iblockaccess.getType(x, y, z);
      Block block = iblockdata.getBlock();
      return block.getMaterial().k() && block.d()
         || (
            block instanceof BlockStairs
               ? iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP
               : (
                  block instanceof BlockStepAbstract
                     ? iblockdata.get(BlockStepAbstract.HALF) == BlockStepAbstract.EnumSlabHalf.TOP
                     : block instanceof BlockHopper || block instanceof BlockSnow && iblockdata.get(BlockSnow.LAYERS) == 7
               )
         );
   }

   public static boolean a(World world, Chunk chunk, int x, int y, int z) {
      IBlockData iblockdata = world.getType(chunk, x, y, z, true);
      Block block = iblockdata.getBlock();
      return block.getMaterial().k() && block.d()
         || (
            block instanceof BlockStairs
               ? iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP
               : (
                  block instanceof BlockStepAbstract
                     ? iblockdata.get(BlockStepAbstract.HALF) == BlockStepAbstract.EnumSlabHalf.TOP
                     : block instanceof BlockHopper || block instanceof BlockSnow && iblockdata.get(BlockSnow.LAYERS) == 7
               )
         );
   }

   public static boolean a(IBlockData iblockdata) {
      Block block = iblockdata.getBlock();
      return block.getMaterial().k() && block.d()
         || (
            block instanceof BlockStairs
               ? iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP
               : (
                  block instanceof BlockStepAbstract
                     ? iblockdata.get(BlockStepAbstract.HALF) == BlockStepAbstract.EnumSlabHalf.TOP
                     : block instanceof BlockHopper || block instanceof BlockSnow && iblockdata.get(BlockSnow.LAYERS) == 7
               )
         );
   }

   public CraftWorld getWorld() {
      return this.world;
   }

   public CraftServer getServer() {
      return (CraftServer)Bukkit.getServer();
   }

   public Chunk getChunkIfLoaded(int x, int z) {
      return ((ChunkProviderServer)this.chunkProvider).getChunkIfLoaded(x, z);
   }

   public Chunk getChunkIfLoaded(long hash) {
      return ((ChunkProviderServer)this.chunkProvider).getChunkIfLoaded(hash);
   }

   public World b() {
      return this;
   }

   public BiomeBase getBiome(BlockPosition blockposition) {
      return this.getBiome(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public WorldChunkManager getWorldChunkManager() {
      return this.worldProvider.m();
   }

   public boolean isValidLocation(int x, int y, int z) {
      return this.isValidLocation(x, z) && y >= 0 && y < 256;
   }

   public IBlockData getCapturedBlockType(int x, int y, int z) {
      for (org.bukkit.block.BlockState previous : this.capturedBlockStates) {
         if (previous.getX() == x && previous.getY() == y && previous.getZ() == z) {
            return CraftMagicNumbers.getBlock(previous.getTypeId()).fromLegacyData(previous.getRawData());
         }
      }

      return null;
   }

   public DifficultyDamageScaler E(int x, int y, int z) {
      long i = 0L;
      float f = 0.0F;
      if (this.isLoaded(x, y, z)) {
         f = this.y();
         i = this.getChunkAtWorldCoords(x, z).w();
      }

      return new DifficultyDamageScaler(this.getDifficulty(), this.getDayTime(), i, f);
   }

   public IBlockData getTypeIfLoaded(int x, int y, int z) {
      if (this.captureTreeGeneration) {
         IBlockData previous = this.getCapturedBlockType(x, y, z);
         if (previous != null) {
            return previous;
         }
      }

      Chunk chunk = ((ChunkProviderServer)this.chunkProvider).getChunkIfLoaded(x >> 4, z >> 4);
      return chunk != null ? chunk.getBlockData(x, y, z) : null;
   }

   public boolean areChunksLoadedBetween(int x, int y, int z, int x1, int y1, int z1) {
      return this.isAreaLoaded(x, y, z, x1, y1, z1, true);
   }

   public boolean isLoaded(int x, int y, int z) {
      return this.isValidLocation(x, y, z) && this.isChunkLoaded(x >> 4, z >> 4, true);
   }

   public boolean i(int x, int y, int z) {
      return this.getChunkAtWorldCoords(x, z).d(x, y, z);
   }

   public int getLightLevel(BlockPosition blockposition) {
      return BlazeConfig.disableBlockLighting.getValue() ? 15 : this.c(blockposition.getX(), blockposition.getY(), blockposition.getZ(), true);
   }

   public int getLightLevel(int x, int y, int z) {
      return BlazeConfig.disableBlockLighting.getValue() ? 15 : this.c(x, y, z, true);
   }

   public int c(int x, int y, int z, boolean flag) {
      if (this.isValidLocation(x, z)) {
         if (flag && this.getType(x, y, z).getBlock().s()) {
            int i = this.c(x, y + 1, z, false);
            int j = this.c(x + 1, y, z, false);
            int k = this.c(x - 1, y, z, false);
            int l = this.c(x, y, z + 1, false);
            int i1 = this.c(x, y, z - 1, false);
            if (j > i) {
               i = j;
            }

            if (k > i) {
               i = k;
            }

            if (l > i) {
               i = l;
            }

            if (i1 > i) {
               i = i1;
            }

            return i;
         } else if (y < 0) {
            return 0;
         } else {
            if (y >= 256) {
               y = 255;
            }

            Chunk chunk = this.getChunkIfLoaded(x >> 4, z >> 4);
            return chunk == null ? 0 : chunk.a(x, y, z, this.I);
         }
      } else {
         return 15;
      }
   }

   public void queueSpawn(Entity entity) {
      this.queuedSpawn.add(entity);
   }

   public void dispatch() {
      Entity entity;
      while ((entity = this.queuedSpawn.poll()) != null) {
         this.addEntity(entity);
         if ((Boolean)BlazeConfig.dispenserEffects.getValue()) {
            this.makeSound(entity, "game.tnt.primed", 1.0F, 1.0F);
         }
      }
   }

   public BiomeBase getBiome(int x, int y, int z) {
      if (this.isValidLocation(x, y, z)) {
         Chunk ch = this.getChunkIfLoaded(x, z);
         if (ch != null) {
            try {
               return ch.getBiome(x, z, this.worldProvider.m());
            } catch (Throwable var8) {
               CrashReport crashreport = CrashReport.a(var8, "Getting biome");
               CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Coordinates of biome request");
               crashreportsystemdetails.a("Location", () -> CrashReportSystemDetails.a(new BlockPosition(x, y, z)));
               throw new ReportedException(crashreport);
            }
         } else {
            return this.worldProvider.m().getBiome(x, z, BiomeBase.PLAINS);
         }
      } else {
         return this.worldProvider.m().getBiome(x, z, BiomeBase.PLAINS);
      }
   }

   public boolean isValidLocation(int x, int z) {
      return x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000;
   }

   public void a(EnumSkyBlock enumskyblock, int x, int y, int z, int i) {
      if (this.isLoaded(x, y, z)) {
         this.getChunkAtWorldCoords(x, z).a(enumskyblock, x, y, z, i);
         this.n(new BlockPosition(x, y, z));
      }
   }

   private int a(int x, int y, int z, EnumSkyBlock enumskyblock) {
      if (enumskyblock == EnumSkyBlock.SKY && this.i(x, y, z)) {
         return 15;
      } else {
         Block block = this.getType(x, y, z).getBlock();
         int i = enumskyblock == EnumSkyBlock.SKY ? 0 : block.r();
         int j = block.p();
         if (j >= 15 && block.r() > 0) {
            j = 1;
         }

         if (j < 1) {
            j = 1;
         }

         if (j >= 15) {
            return 0;
         } else if (i >= 14) {
            return i;
         } else {
            EnumDirection[] var11;
            for (EnumDirection enumdirection : var11 = EnumDirection.values()) {
               int i1 = this.b(enumskyblock, x + enumdirection.getAdjacentX(), y + enumdirection.getAdjacentY(), z + enumdirection.getAdjacentZ()) - j;
               if (i1 > i) {
                  i = i1;
               }

               if (i >= 14) {
                  return i;
               }
            }

            return i;
         }
      }
   }

   public void applyPhysics(int x, int y, int z, Block block) {
      BlockPosition source = new BlockPosition(x, y, z);
      this.d(x - 1, y, z, block, source);
      this.d(x + 1, y, z, block, source);
      this.d(x, y - 1, z, block, source);
      this.d(x, y + 1, z, block, source);
      this.d(x, y, z - 1, block, source);
      this.d(x, y, z + 1, block, source);
   }

   public void applyPhysics(BlockPosition blockposition, Block block) {
      this.applyPhysics(blockposition.getX(), blockposition.getY(), blockposition.getZ(), block);
      this.spigotConfig.antiXrayInstance.updateNearbyBlocks(this, blockposition);
   }

   public IBlockData getType(Chunk chunk, BlockPosition position) {
      return this.getType(chunk, position.getX(), position.getY(), position.getZ(), true);
   }

   public IBlockData getType(Chunk chunk, int x, int y, int z, boolean useCaptured) {
      if (this.captureTreeGeneration && useCaptured) {
         IBlockData previous = this.getCapturedBlockType(x, y, z);
         if (previous != null) {
            return previous;
         }
      }

      return !this.isValidLocation(x, y, z) ? Blocks.AIR.getBlockData() : chunk.getBlockData(x, y, z);
   }

   protected abstract IChunkProvider k();

   public void a(WorldSettings worldsettings) {
      this.worldData.d(true);
   }

   public Block c1(int x, int z) {
      int y1 = this.F();

      while (!this.isEmpty(x, y1 + 1, z)) {
         y1++;
      }

      return this.getType(x, y1, z).getBlock();
   }

   public Block c(BlockPosition blockposition) {
      return this.c1(blockposition.getX(), blockposition.getZ());
   }

   public boolean isValidLocation(BlockPosition blockposition) {
      return blockposition.getX() >= -30000000
         && blockposition.getZ() >= -30000000
         && blockposition.getX() < 30000000
         && blockposition.getZ() < 30000000
         && blockposition.getY() >= 0
         && blockposition.getY() < 256;
   }

   @Override
   public boolean isEmpty(BlockPosition blockposition) {
      return this.getType(blockposition).getBlock().getMaterial() == Material.AIR;
   }

   public boolean isEmpty(int x, int y, int z) {
      return this.getType(x, y, z).getBlock().getMaterial() == Material.AIR;
   }

   public boolean isLoaded(BlockPosition blockposition) {
      return this.getChunkIfLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4) != null;
   }

   public boolean a(BlockPosition blockposition, boolean flag) {
      return this.isValidLocation(blockposition) && this.isChunkLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4, flag);
   }

   public boolean areChunksLoaded(BlockPosition blockposition, int i) {
      return this.areChunksLoaded(blockposition, i, true);
   }

   public boolean areChunksLoaded(BlockPosition blockposition, int i, boolean flag) {
      return this.isAreaLoaded(
         blockposition.getX() - i,
         blockposition.getY() - i,
         blockposition.getZ() - i,
         blockposition.getX() + i,
         blockposition.getY() + i,
         blockposition.getZ() + i,
         flag
      );
   }

   public boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1) {
      return this.areChunksLoadedBetween(blockposition, blockposition1, true);
   }

   public boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1, boolean flag) {
      return this.isAreaLoaded(
         blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ(), flag
      );
   }

   public boolean a(StructureBoundingBox structureboundingbox) {
      return this.b(structureboundingbox, true);
   }

   public boolean b(StructureBoundingBox structureboundingbox, boolean flag) {
      return this.isAreaLoaded(
         structureboundingbox.a, structureboundingbox.b, structureboundingbox.c, structureboundingbox.d, structureboundingbox.e, structureboundingbox.f, flag
      );
   }

   private boolean isAreaLoaded(int i, int j, int k, int l, int i1, int j1, boolean flag) {
      if (i1 >= 0 && j < 256) {
         i >>= 4;
         k >>= 4;
         l >>= 4;
         j1 >>= 4;

         for (int k1 = i; k1 <= l; k1++) {
            for (int l1 = k; l1 <= j1; l1++) {
               if (!this.isChunkLoaded(k1, l1, flag)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean isChunkLoaded(int i, int j, boolean flag) {
      return this.chunkProvider.isChunkLoaded(i, j) && (flag || !this.chunkProvider.getOrCreateChunk(i, j).isEmpty());
   }

   public Chunk getChunkAtWorldCoords(BlockPosition blockposition) {
      return this.getChunkAt(blockposition.getX() >> 4, blockposition.getZ() >> 4);
   }

   public Chunk getChunkAt(int i, int j) {
      return this.chunkProvider.getOrCreateChunk(i, j);
   }

   public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i) {
      return this.setTypeAndData(blockposition, iblockdata, i, true, false);
   }

   public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i, boolean light, boolean sand) {
      return this.setTypeAndData(null, blockposition, iblockdata, i, light, sand);
   }

   public boolean setTypeAndData(Chunk chunk, BlockPosition blockposition, IBlockData iblockdata, int i, boolean light, boolean sand) {
      if (this.captureTreeGeneration) {
         org.bukkit.block.BlockState blockstate = null;
         Iterator<org.bukkit.block.BlockState> it = this.capturedBlockStates.iterator();

         while (it.hasNext()) {
            org.bukkit.block.BlockState previous = it.next();
            if (previous.getX() == blockposition.getX() && previous.getY() == blockposition.getY() && previous.getZ() == blockposition.getZ()) {
               blockstate = previous;
               it.remove();
               break;
            }
         }

         if (blockstate == null) {
            blockstate = CraftBlockState.getBlockState(this, blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
         }

         blockstate.setTypeId(CraftMagicNumbers.getId(iblockdata.getBlock()));
         blockstate.setRawData((byte)iblockdata.getBlock().toLegacyData(iblockdata));
         this.capturedBlockStates.add(blockstate);
         return true;
      } else if (!this.isValidLocation(blockposition)) {
         return false;
      } else {
         if (chunk == null) {
            chunk = this.getChunkAtWorldCoords(blockposition);
         }

         Block block = iblockdata.getBlock();
         org.bukkit.block.BlockState blockstate = null;
         if (this.captureBlockStates) {
            blockstate = CraftBlockState.getBlockState(this, blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
            this.capturedBlockStates.add(blockstate);
         }

         IBlockData iblockdata1 = chunk.a(blockposition, iblockdata, light);
         if (iblockdata1 == null) {
            if (this.captureBlockStates) {
               this.capturedBlockStates.remove(blockstate);
            }

            return false;
         } else {
            Block block1 = iblockdata1.getBlock();
            if (light && block.p() != block1.p() || block.r() != block1.r()) {
               this.x(blockposition);
            }

            this.movementCache.clear();
            if (!this.captureBlockStates) {
               this.notifyAndUpdatePhysics(blockposition, chunk, block1, block, i, sand);
            }

            return true;
         }
      }
   }

   public void notifyAndUpdatePhysics(BlockPosition blockposition, Chunk chunk, Block oldBlock, Block newBlock, int flag) {
      this.notifyAndUpdatePhysics(blockposition, chunk, oldBlock, newBlock, flag, false);
   }

   public void notifyAndUpdatePhysics(BlockPosition blockposition, Chunk chunk, Block oldBlock, Block newBlock, int flag, boolean sand) {
      if ((flag & 2) != 0 && (chunk == null || chunk.isReady())) {
         this.notify(blockposition);
      }

      if ((flag & 1) != 0) {
         if (!sand) {
            this.update(blockposition, oldBlock);
            if (newBlock.isComplexRedstone()) {
               this.updateAdjacentComparators(blockposition, newBlock);
            }
         } else {
            this.d(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ(), oldBlock, blockposition);
         }
      }
   }

   public boolean setAir(BlockPosition blockposition) {
      return this.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
   }

   public boolean setAir(BlockPosition blockposition, boolean flag) {
      IBlockData iblockdata = this.getType(blockposition);
      Block block = iblockdata.getBlock();
      if (block.getMaterial() == Material.AIR) {
         return false;
      } else {
         this.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
         if (flag) {
            block.b(this, blockposition, iblockdata, 0);
         }

         return this.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
      }
   }

   public boolean setTypeUpdate(BlockPosition blockposition, IBlockData iblockdata) {
      return this.setTypeAndData(blockposition, iblockdata, 3);
   }

   public void notify(BlockPosition blockposition) {
      this.notify(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public void notify(int x, int y, int z) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.a(x, y, z);
      }
   }

   public void update(BlockPosition blockposition, Block block) {
      if (!this.populating) {
         this.applyPhysics(blockposition, block);
      }
   }

   public void a(int i, int j, int k, int l) {
      if (k > l) {
         int i1 = l;
         l = k;
         k = i1;
      }

      if (!this.worldProvider.o()) {
         for (int i1 = k; i1 <= l; i1++) {
            this.updateLight(EnumSkyBlock.SKY, i, i1, j);
         }
      }

      this.b(i, k, j, i, l, j);
   }

   public void b(BlockPosition blockposition, BlockPosition blockposition1) {
      this.b(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
   }

   public void b(int i, int j, int k, int l, int i1, int j1) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.a(i, j, k, l, i1, j1);
      }
   }

   public void a(int x, int y, int z, Block block, EnumDirection enumdirection) {
      BlockPosition source = new BlockPosition(x, y, z);
      if (enumdirection != EnumDirection.WEST) {
         this.d(x - 1, y, z, block, source);
      }

      if (enumdirection != EnumDirection.EAST) {
         this.d(x + 1, y, z, block, source);
      }

      if (enumdirection != EnumDirection.DOWN) {
         this.d(x, y - 1, z, block, source);
      }

      if (enumdirection != EnumDirection.UP) {
         this.d(x, y + 1, z, block, source);
      }

      if (enumdirection != EnumDirection.NORTH) {
         this.d(x, y, z - 1, block, source);
      }

      if (enumdirection != EnumDirection.SOUTH) {
         this.d(x, y, z + 1, block, source);
      }
   }

   public void d(int x, int y, int z, Block block, EnumDirection enumdirection) {
      this.a(x, y, z, block, enumdirection);
   }

   public void a(BlockPosition blockposition, Block block, EnumDirection enumdirection) {
      this.a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), block, enumdirection);
   }

   public void d(BlockPosition blockposition, Block block) {
      this.d(blockposition.getX(), blockposition.getY(), blockposition.getZ(), block);
   }

   public void d(int x, int y, int z, Block block) {
      this.d(x, y, z, block, (BlockPosition)null);
   }

   public void d(int x, int y, int z, final Block block, BlockPosition source) {
      Chunk chunk = null;
      IBlockData iblockdata = this.captureTreeGeneration ? this.getCapturedBlockType(x, y, z) : null;
      if (iblockdata == null) {
         if (!this.isValidLocation(x, y, z)) {
            return;
         }

         chunk = this.getChunkAtWorldCoords(x, z);
         iblockdata = chunk.getBlockData(x, y, z);
      }

      try {
         CraftWorld world = this.getWorld();
         if ((Boolean)BlazeConfig.fireBlockPhysicsEvent.getValue()
            && world != null
            && (
               (Boolean)BlazeConfig.fireRedstonePhysicsEvent.getValue()
                  || !(block instanceof BlockRedstoneWire) && !(block instanceof BlockRedstoneTorch) && !(block instanceof BlockRepeater)
            )
            && ((Boolean)BlazeConfig.fireSandPhysicsEvent.getValue() || !(block instanceof BlockFalling))) {
            BlockPhysicsEvent event = new BlockPhysicsEvent(world.getBlockAt(x, y, z), CraftMagicNumbers.getId(block));
            this.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               return;
            }
         }

         iblockdata.getBlock().doChunkPhysics(this, chunk, new BlockPosition(x, y, z), iblockdata, block, source);
      } catch (StackOverflowError var11) {
         haveWeSilencedAPhysicsCrash = true;
         blockLocation = x + ", " + y + ", " + z;
      } catch (Throwable var12) {
         CrashReport crashreport = CrashReport.a(var12, "Exception while updating neighbours");
         CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being updated");
         crashreportsystemdetails.a("Source block type", new Callable<String>() {
            public String a() {
               try {
                  return String.format("ID #%d (%s // %s)", Block.getId(block), block.a(), block.getClass().getCanonicalName());
               } catch (Throwable var2) {
                  return "ID #" + Block.getId(block);
               }
            }

            public String call() {
               return this.a();
            }
         });
         CrashReportSystemDetails.a(crashreportsystemdetails, null, iblockdata);
         throw new ReportedException(crashreport);
      }
   }

   public boolean a(BlockPosition blockposition, Block block) {
      return false;
   }

   public boolean i(BlockPosition blockposition) {
      return this.i(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public boolean j(BlockPosition blockposition) {
      int x = blockposition.getX();
      int y = blockposition.getY();
      int z = blockposition.getZ();
      if (y >= this.a) {
         return this.i(x, y, z);
      } else {
         int y1 = this.a;
         if (!this.i(x, y1, z)) {
            return false;
         } else {
            y1--;

            while (y1 > y) {
               Block block = this.getType(x, y1, z).getBlock();
               if (block.p() > 0 && !block.getMaterial().isLiquid()) {
                  return false;
               }

               y1--;
            }

            return true;
         }
      }
   }

   public int k(int x, int y, int z) {
      if (y < 0) {
         return 0;
      } else {
         if (y >= 256) {
            y = 255;
         }

         Chunk chunk = this.getChunkIfLoaded(x >> 4, z >> 4);
         return chunk != null ? chunk.a(x, y, z, 0) : 0;
      }
   }

   public int k(BlockPosition blockposition) {
      return this.k(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public int c(BlockPosition blockposition, boolean flag) {
      return this.c(blockposition.getX(), blockposition.getY(), blockposition.getZ(), flag);
   }

   public BlockPosition getHighestBlockYAt(BlockPosition blockposition) {
      return this.getHighestBlockYAt(blockposition.getX(), blockposition.getZ());
   }

   public BlockPosition getHighestBlockYAt(int x, int z) {
      int i = 0;
      if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
         Chunk chunk = this.getChunkIfLoaded(x >> 4, z >> 4);
         if (chunk != null) {
            i = chunk.b(x & 15, z & 15);
         }
      } else {
         i = this.F() + 1;
      }

      return new BlockPosition(x, i, z);
   }

   public int getHighestBlockYAt(int x, int z, boolean chunkAlreadyLoaded) {
      if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
         return !chunkAlreadyLoaded && !this.chunkProvider.isChunkLoaded(x >> 4, z >> 4) ? 0 : this.getChunkAt(x >> 4, z >> 4).b(x & 15, z & 15);
      } else {
         return this.F() + 1;
      }
   }

   public boolean areChunksLoaded(int x, int y, int z, int i) {
      return this.isAreaLoaded(x - i, y - i, z - i, x + i, y + i, z + i, true);
   }

   public int b(int i, int j) {
      return this.b(i, j, false);
   }

   public int b(int i, int j, boolean chunkAlreadyLoaded) {
      if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
         return !chunkAlreadyLoaded && !this.chunkProvider.isChunkLoaded(i >> 4, j >> 4) ? 0 : this.getChunkAt(i >> 4, j >> 4).v();
      } else {
         return this.F() + 1;
      }
   }

   public int b(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
      return this.b(enumskyblock, blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public int b(EnumSkyBlock enumskyblock, int x, int y, int z) {
      if (y < 0) {
         y = 0;
      }

      return !this.isLoaded(x, y, z) ? enumskyblock.c : this.getChunkAtWorldCoords(x, z).getBrightness(enumskyblock, x, y, z);
   }

   public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
      if (this.isLoaded(blockposition)) {
         this.getChunkAtWorldCoords(blockposition).a(enumskyblock, blockposition, i);
         this.n(blockposition);
      }
   }

   public void n(BlockPosition blockposition) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.b(blockposition);
      }
   }

   public float o(BlockPosition blockposition) {
      return this.o(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public float o(int x, int y, int z) {
      return this.worldProvider.p()[this.getLightLevel(x, y, z)];
   }

   @Override
   public IBlockData getType(BlockPosition blockposition) {
      return this.getType(blockposition, true);
   }

   public IBlockData getType(BlockPosition blockposition, boolean useCaptured) {
      if (this.captureTreeGeneration && useCaptured) {
         IBlockData previous = this.getCapturedBlockType(blockposition.getX(), blockposition.getY(), blockposition.getZ());
         if (previous != null) {
            return previous;
         }
      }

      return !this.isValidLocation(blockposition) ? Blocks.AIR.getBlockData() : this.getChunkAtWorldCoords(blockposition).getBlockData(blockposition);
   }

   @Override
   public IBlockData getType(int x, int y, int z) {
      return this.getType(x, y, z, true);
   }

   public IBlockData getType(int x, int y, int z, boolean useCaptured) {
      if (this.captureTreeGeneration && useCaptured) {
         IBlockData previous = this.getCapturedBlockType(x, y, z);
         if (previous != null) {
            return previous;
         }
      }

      return !this.isValidLocation(x, y, z) ? Blocks.AIR.getBlockData() : this.getChunkAtWorldCoords(x, z).getBlockData(x, y, z);
   }

   public Chunk getChunkAtWorldCoords(int x, int z) {
      return this.getChunkAt(x >> 4, z >> 4);
   }

   public boolean w() {
      return this.I < 4;
   }

   public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1) {
      return this.rayTrace(vec3d, vec3d1, false, false, false);
   }

   public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, boolean flag) {
      return this.rayTrace(vec3d, vec3d1, flag, false, false);
   }

   public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, boolean flag, boolean flag1, boolean flag2) {
      if (Double.isNaN(vec3d.a) || Double.isNaN(vec3d.b) || Double.isNaN(vec3d.c)) {
         return null;
      } else if (!Double.isNaN(vec3d1.a) && !Double.isNaN(vec3d1.b) && !Double.isNaN(vec3d1.c)) {
         int i = MathHelper.floor(vec3d1.a);
         int j = MathHelper.floor(vec3d1.b);
         int k = MathHelper.floor(vec3d1.c);
         int l = MathHelper.floor(vec3d.a);
         int i1 = MathHelper.floor(vec3d.b);
         int j1 = MathHelper.floor(vec3d.c);
         BlockPosition blockposition = new BlockPosition(l, i1, j1);
         IBlockData iblockdata = this.getType(blockposition);
         Block block = iblockdata.getBlock();
         if ((!flag1 || block.a(this, blockposition, iblockdata) != null) && block.a(iblockdata, flag)) {
            MovingObjectPosition movingobjectposition = block.a(this, blockposition, vec3d, vec3d1);
            if (movingobjectposition != null) {
               return movingobjectposition;
            }
         }

         MovingObjectPosition movingobjectposition1 = null;
         int k1 = 200;

         while (k1-- >= 0) {
            if (Double.isNaN(vec3d.a) || Double.isNaN(vec3d.b) || Double.isNaN(vec3d.c)) {
               return null;
            }

            if (l == i && i1 == j && j1 == k) {
               return flag2 ? movingobjectposition1 : null;
            }

            boolean flag3 = true;
            boolean flag4 = true;
            boolean flag5 = true;
            double d0 = 999.0;
            double d1 = 999.0;
            double d2 = 999.0;
            if (i > l) {
               d0 = l + 1.0;
            } else if (i < l) {
               d0 = l + 0.0;
            } else {
               flag3 = false;
            }

            if (j > i1) {
               d1 = i1 + 1.0;
            } else if (j < i1) {
               d1 = i1 + 0.0;
            } else {
               flag4 = false;
            }

            if (k > j1) {
               d2 = j1 + 1.0;
            } else if (k < j1) {
               d2 = j1 + 0.0;
            } else {
               flag5 = false;
            }

            double d3 = 999.0;
            double d4 = 999.0;
            double d5 = 999.0;
            double d6 = vec3d1.a - vec3d.a;
            double d7 = vec3d1.b - vec3d.b;
            double d8 = vec3d1.c - vec3d.c;
            if (flag3) {
               d3 = (d0 - vec3d.a) / d6;
            }

            if (flag4) {
               d4 = (d1 - vec3d.b) / d7;
            }

            if (flag5) {
               d5 = (d2 - vec3d.c) / d8;
            }

            if (d3 == -0.0) {
               d3 = -1.0E-4;
            }

            if (d4 == -0.0) {
               d4 = -1.0E-4;
            }

            if (d5 == -0.0) {
               d5 = -1.0E-4;
            }

            EnumDirection enumdirection;
            if (d3 < d4 && d3 < d5) {
               enumdirection = i > l ? EnumDirection.WEST : EnumDirection.EAST;
               vec3d = new Vec3D(d0, vec3d.b + d7 * d3, vec3d.c + d8 * d3);
            } else if (d4 < d5) {
               enumdirection = j > i1 ? EnumDirection.DOWN : EnumDirection.UP;
               vec3d = new Vec3D(vec3d.a + d6 * d4, d1, vec3d.c + d8 * d4);
            } else {
               enumdirection = k > j1 ? EnumDirection.NORTH : EnumDirection.SOUTH;
               vec3d = new Vec3D(vec3d.a + d6 * d5, vec3d.b + d7 * d5, d2);
            }

            l = MathHelper.floor(vec3d.a) - (enumdirection == EnumDirection.EAST ? 1 : 0);
            i1 = MathHelper.floor(vec3d.b) - (enumdirection == EnumDirection.UP ? 1 : 0);
            j1 = MathHelper.floor(vec3d.c) - (enumdirection == EnumDirection.SOUTH ? 1 : 0);
            blockposition = new BlockPosition(l, i1, j1);
            IBlockData iblockdata1 = this.getType(blockposition);
            Block block1 = iblockdata1.getBlock();
            if (!flag1 || block1.a(this, blockposition, iblockdata1) != null) {
               if (block1.a(iblockdata1, flag)) {
                  MovingObjectPosition movingobjectposition2 = block1.a(this, blockposition, vec3d, vec3d1);
                  if (movingobjectposition2 != null) {
                     return movingobjectposition2;
                  }
               } else {
                  movingobjectposition1 = new MovingObjectPosition(MovingObjectPosition.EnumMovingObjectType.MISS, vec3d, enumdirection, blockposition);
               }
            }
         }

         return flag2 ? movingobjectposition1 : null;
      } else {
         return null;
      }
   }

   public boolean isThroughBlock(Vec3D start, Vec3D end, Collection<BlockPosition> toIgnore) {
      if (Double.isNaN(start.a) || Double.isNaN(start.b) || Double.isNaN(start.c)) {
         return false;
      } else if (!Double.isNaN(end.a) && !Double.isNaN(end.b) && !Double.isNaN(end.c)) {
         boolean chestsAndAnvil = (Boolean)BlazeConfig.playerPhaseChestsAndAnvils.getValue();
         int endX = MathHelper.floor(end.a);
         int endY = MathHelper.floor(end.b);
         int endZ = MathHelper.floor(end.c);
         int x = MathHelper.floor(start.a);
         int y = MathHelper.floor(start.b);
         int z = MathHelper.floor(start.c);
         MovingObjectPosition movingobjectposition1 = null;
         int k1 = 200;

         label155:
         while (k1-- >= 0) {
            if (Double.isNaN(start.a) || Double.isNaN(start.b) || Double.isNaN(start.c)) {
               return false;
            }

            if (x == endX && y == endY && z == endZ) {
               return false;
            }

            boolean flag3 = true;
            boolean flag4 = true;
            boolean flag5 = true;
            double d0 = 999.0;
            double d1 = 999.0;
            double d2 = 999.0;
            if (endX > x) {
               d0 = x + 1.0;
            } else if (endX < x) {
               d0 = x + 0.0;
            } else {
               flag3 = false;
            }

            if (endY > y) {
               d1 = y + 1.0;
            } else if (endY < y) {
               d1 = y + 0.0;
            } else {
               flag4 = false;
            }

            if (endZ > z) {
               d2 = z + 1.0;
            } else if (endZ < z) {
               d2 = z + 0.0;
            } else {
               flag5 = false;
            }

            double d3 = 999.0;
            double d4 = 999.0;
            double d5 = 999.0;
            double d6 = end.a - start.a;
            double d7 = end.b - start.b;
            double d8 = end.c - start.c;
            if (flag3) {
               d3 = (d0 - start.a) / d6;
            }

            if (flag4) {
               d4 = (d1 - start.b) / d7;
            }

            if (flag5) {
               d5 = (d2 - start.c) / d8;
            }

            if (d3 == -0.0) {
               d3 = -1.0E-4;
            }

            if (d4 == -0.0) {
               d4 = -1.0E-4;
            }

            if (d5 == -0.0) {
               d5 = -1.0E-4;
            }

            EnumDirection enumdirection;
            if (d3 < d4 && d3 < d5) {
               enumdirection = endX > x ? EnumDirection.WEST : EnumDirection.EAST;
               start = new Vec3D(d0, start.b + d7 * d3, start.c + d8 * d3);
            } else if (d4 < d5) {
               enumdirection = endY > y ? EnumDirection.DOWN : EnumDirection.UP;
               start = new Vec3D(start.a + d6 * d4, d1, start.c + d8 * d4);
            } else {
               enumdirection = endZ > z ? EnumDirection.NORTH : EnumDirection.SOUTH;
               start = new Vec3D(start.a + d6 * d5, start.b + d7 * d5, d2);
            }

            x = MathHelper.floor(start.a) - (enumdirection == EnumDirection.EAST ? 1 : 0);
            y = MathHelper.floor(start.b) - (enumdirection == EnumDirection.UP ? 1 : 0);
            z = MathHelper.floor(start.c) - (enumdirection == EnumDirection.SOUTH ? 1 : 0);

            for (BlockPosition ignore : toIgnore) {
               if (ignore.getX() == x && ignore.getY() == y && ignore.getZ() == z) {
                  continue label155;
               }
            }

            BlockPosition blockposition = new BlockPosition(x, y, z);
            IBlockData iblockdata1 = this.getType(blockposition);
            Block block1 = iblockdata1.getBlock();
            if ((chestsAndAnvil || !(block1 instanceof BlockChest) && !(block1 instanceof BlockAnvil))
               && block1.a(this, blockposition, iblockdata1) != null
               && block1.a(iblockdata1, false)) {
               MovingObjectPosition movingobjectposition2 = block1.a(this, blockposition, start, end);
               if (movingobjectposition2 != null) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void interceptSounds() {
      this.interceptSounds = true;
   }

   public void sendInterceptedSounds() {
      for (Runnable r : this.interceptedSounds) {
         r.run();
      }

      this.interceptedSounds.clear();
      this.interceptSounds = false;
   }

   public void clearInterceptedSounds() {
      this.interceptedSounds.clear();
      this.interceptSounds = false;
   }

   public void makeSound(double d0, double d1, double d2, String s, float f, float f1) {
      if (this.interceptSounds && Bukkit.isPrimaryThread()) {
         this.interceptedSounds.add(() -> {
            for (IWorldAccess wx : this.u) {
               wx.a(s, d0, d1, d2, f, f1);
            }
         });
      } else {
         for (IWorldAccess w : this.u) {
            w.a(s, d0, d1, d2, f, f1);
         }
      }
   }

   public void makeSound(Entity entity, String s, float f, float f1) {
      for (IWorldAccess anU : this.u) {
         anU.a(s, entity.locX, entity.locY, entity.locZ, f, f1);
      }
   }

   public void makeSound(Entity entity, String s, float f, float f1, boolean self) {
      for (IWorldAccess anU : this.u) {
         anU.a(s, entity.locX, entity.locY, entity.locZ, f, f1);
      }
   }

   public void a(EntityHuman entityhuman, String s, float f, float f1) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.a(entityhuman, s, entityhuman.locX, entityhuman.locY, entityhuman.locZ, f, f1);
      }
   }

   public void a(BlockPosition blockposition, String s) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.a(s, blockposition);
      }
   }

   public void addParticle(EnumParticle enumparticle, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {
      this.a(enumparticle.c(), enumparticle.e(), d0, d1, d2, d3, d4, d5, aint);
   }

   private void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.a(i, flag, d0, d1, d2, d3, d4, d5, aint);
      }
   }

   public boolean strikeLightning(Entity entity) {
      this.k.add(entity);
      return true;
   }

   public boolean addEntity(EntityPlayer entity) {
      if (entity == null) {
         return false;
      } else {
         this.players.add(entity);
         this.playerMap.add(entity);
         this.everyoneSleeping();
         this.getChunkAt(MathHelper.floor(entity.locX / 16.0), MathHelper.floor(entity.locZ / 16.0)).a(entity);
         this.entityList.add(entity);
         this.a(entity);
         return true;
      }
   }

   public boolean addEntity(EntityFallingBlock entity) {
      if (entity == null) {
         return false;
      } else {
         int i = MathHelper.floor(entity.locX / 16.0);
         int j = MathHelper.floor(entity.locZ / 16.0);
         Chunk chunk = this.getChunkIfLoaded(i, j);
         if (!entity.attachedToPlayer && chunk == null) {
            entity.dead = true;
            return false;
         } else {
            chunk.addCannonEntity(entity);
            this.entityList.add(entity);
            this.a(entity);
            return true;
         }
      }
   }

   public boolean addEntity(EntityTNTPrimed entity) {
      if (entity == null) {
         return false;
      } else {
         int i = MathHelper.floor(entity.locX / 16.0);
         int j = MathHelper.floor(entity.locZ / 16.0);
         Chunk chunk = this.getChunkIfLoaded(i, j);
         if (!entity.attachedToPlayer && chunk == null) {
            entity.dead = true;
            return false;
         } else {
            chunk.addCannonEntity(entity);
            this.entityList.add(entity);
            this.a(entity);
            return true;
         }
      }
   }

   public boolean addEntity(Entity entity) {
      return this.addEntity(entity, SpawnReason.DEFAULT);
   }

   public boolean addEntity(Entity entity, SpawnReason spawnReason) {
      AsyncCatcher.catchOp("entity add");
      if (entity == null) {
         return false;
      } else {
         if (entity.spawnReason == null) {
            entity.spawnReason = spawnReason;
         }

         int i = MathHelper.floor(entity.locX / 16.0);
         int j = MathHelper.floor(entity.locZ / 16.0);
         boolean flag = entity.attachedToPlayer;
         if (entity instanceof EntityHuman) {
            flag = true;
         }

         Cancellable event = null;
         if (entity instanceof EntityLiving && !(entity instanceof EntityPlayer)) {
            boolean isAnimal = entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal || entity instanceof EntityGolem;
            boolean isMonster = entity instanceof EntityMonster || entity instanceof EntityGhast || entity instanceof EntitySlime;
            if (spawnReason != SpawnReason.CUSTOM && (isAnimal && !this.allowAnimals || isMonster && !this.allowMonsters)) {
               entity.dead = true;
               return false;
            }

            event = CraftEventFactory.callCreatureSpawnEvent((EntityLiving)entity, spawnReason);
         } else if (entity instanceof EntityItem) {
            event = CraftEventFactory.callItemSpawnEvent((EntityItem)entity);
         } else if (entity.getBukkitEntity() instanceof Projectile) {
            event = CraftEventFactory.callProjectileLaunchEvent(entity);
         } else if (entity instanceof EntityExperienceOrb) {
            EntityExperienceOrb xp = (EntityExperienceOrb)entity;
            double radius = this.spigotConfig.expMerge;
            if (radius > 0.0) {
               for (Entity e : this.getEntities(entity, entity.getBoundingBox().grow(radius, radius, radius))) {
                  if (e instanceof EntityExperienceOrb) {
                     EntityExperienceOrb loopItem = (EntityExperienceOrb)e;
                     if (!loopItem.dead) {
                        xp.value = xp.value + loopItem.value;
                        loopItem.die();
                     }
                  }
               }
            }
         }

         if (event == null || !event.isCancelled() && !entity.dead) {
            Chunk chunk = this.getChunkIfLoaded(i, j);
            if (!flag && chunk == null) {
               entity.dead = true;
               return false;
            } else {
               if (entity instanceof EntityHuman) {
                  EntityHuman entityhuman = (EntityHuman)entity;
                  this.players.add(entityhuman);
                  this.everyoneSleeping();
               }

               (chunk == null ? this.getChunkAt(i, j) : chunk).a(entity);
               this.entityList.add(entity);
               this.a(entity);
               return true;
            }
         } else {
            entity.dead = true;
            return false;
         }
      }
   }

   protected void a(Entity entity) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.a(entity);
      }

      entity.valid = true;
   }

   protected void b(Entity entity) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.b(entity);
      }

      entity.valid = false;
   }

   public void kill(Entity entity) {
      if (entity.passenger != null) {
         entity.passenger.mount(null);
      }

      if (entity.vehicle != null) {
         entity.mount(null);
      }

      entity.die();
      if (entity instanceof EntityHuman) {
         this.players.remove(entity);
         this.playerMap.remove((EntityPlayer)entity);

         for (Object o : this.worldMaps.c) {
            if (o instanceof WorldMap) {
               WorldMap map = (WorldMap)o;
               map.i.remove(entity);
               map.g.removeIf(worldMapHumanTracker -> worldMapHumanTracker.trackee == entity);
            }
         }

         this.everyoneSleeping();
         this.b(entity);
      }
   }

   public void removeEntity(Entity entity) {
      entity.die();
      if (entity instanceof EntityHuman) {
         this.players.remove(entity);
         this.playerMap.remove((EntityPlayer)entity);
         this.everyoneSleeping();
      }

      if (!this.guardEntityList) {
         int i = entity.ae;
         int j = entity.ag;
         if (entity.ad) {
            Chunk chunk = this.getChunkIfLoaded(i, j);
            if (chunk != null) {
               chunk.b(entity);
            }

            ((PaperWorldEntityList)this.entityList).updateEntityCount(entity, -1);
         }

         int index = this.entityList.indexOf(entity);
         if (index != -1) {
            if (index <= this.tickPosition) {
               this.tickPosition--;
            }

            this.entityList.remove(index);
         }
      }

      this.b(entity);
   }

   public void addIWorldAccess(IWorldAccess iworldaccess) {
      this.u.add(iworldaccess);
   }

   public List<AxisAlignedBB> getCubes(Entity entity, AxisAlignedBB axisalignedbb) {
      List<AxisAlignedBB> arraylist = Lists.newArrayList();
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d + 1.0);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e + 1.0);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f + 1.0);
      WorldBorder worldborder = this.getWorldBorder();
      boolean flag = entity.aT();
      boolean flag1 = this.a(worldborder, entity);
      boolean collides = entity.world.paperSpigotConfig.fallingBlocksCollideWithSigns && entity instanceof EntityTNTPrimed
         || entity instanceof EntityFallingBlock;
      int ystart = Math.max(k - 1, 0);
      BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition();

      for (int chunkx = i >> 4; chunkx <= j - 1 >> 4; chunkx++) {
         int cx = chunkx << 4;

         for (int chunkz = i1 >> 4; chunkz <= j1 - 1 >> 4; chunkz++) {
            Chunk chunk = entity.loadChunks && chunkx == Entity.lastChunkX && chunkz == Entity.lastChunkZ
               ? Entity.lastChunk
               : (entity.loadChunks ? ((ChunkProviderServer)entity.world.chunkProvider).getChunkAt(chunkx, chunkz) : this.getChunkIfLoaded(chunkx, chunkz));
            if (chunk == null) {
               entity.inUnloadedChunk = true;
            } else {
               int cz = chunkz << 4;
               int xstart = Math.max(i, cx);
               int xend = Math.min(j, cx + 16);
               int zstart = Math.max(i1, cz);
               int zend = Math.min(j1, cz + 16);

               for (int x = xstart; x < xend; x++) {
                  for (int z = zstart; z < zend; z++) {
                     for (int y = ystart; y < l; y++) {
                        if (flag && flag1) {
                           entity.h(false);
                        } else if (!flag && !flag1) {
                           entity.h(true);
                        }

                        IBlockData block;
                        if (!this.getWorldBorder().a(x, z) && flag1) {
                           block = Blocks.STONE.getBlockData();
                        } else {
                           block = chunk.getBlockData(x, y, z);
                        }

                        if (block != null) {
                           Block b = block.getBlock();
                           if (collides
                              && (
                                 b instanceof BlockSign
                                    || b instanceof BlockFenceGate
                                    || b instanceof BlockTorch
                                    || b instanceof BlockButtonAbstract
                                    || b instanceof BlockLever
                                    || b instanceof BlockTripwireHook
                                    || b instanceof BlockTripwire
                                    || b instanceof BlockChest
                                    || b instanceof BlockSlowSand
                                    || b instanceof BlockBed
                                    || b instanceof BlockEnderChest
                                    || b instanceof BlockEnchantmentTable
                                    || b instanceof BlockBrewingStand
                              )) {
                              AxisAlignedBB aabb = AxisAlignedBB.a(x, y, z, x + 1.0, y + 1.0, z + 1.0);
                              if (axisalignedbb.b(aabb)) {
                                 arraylist.add(aabb);
                              }
                           } else {
                              b.a(this, pos.c(x, y, z), block, axisalignedbb, arraylist, entity);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return arraylist;
   }

   public boolean isEmpty(Entity entity, AxisAlignedBB axisalignedbb) {
      OptimizedNonEmptyList<AxisAlignedBB> arraylist = new OptimizedNonEmptyList();
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d + 1.0);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e + 1.0);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f + 1.0);
      WorldBorder worldborder = this.getWorldBorder();
      boolean flag = entity.aT();
      boolean flag1 = this.a(worldborder, entity);
      boolean collides = entity.world.paperSpigotConfig.fallingBlocksCollideWithSigns && entity instanceof EntityTNTPrimed
         || entity instanceof EntityFallingBlock;
      int ystart = Math.max(k - 1, 0);
      BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition();

      for (int chunkx = i >> 4; chunkx <= j - 1 >> 4; chunkx++) {
         int cx = chunkx << 4;

         for (int chunkz = i1 >> 4; chunkz <= j1 - 1 >> 4; chunkz++) {
            Chunk chunk = entity.loadChunks && chunkx == Entity.lastChunkX && chunkz == Entity.lastChunkZ
               ? Entity.lastChunk
               : (entity.loadChunks ? ((ChunkProviderServer)entity.world.chunkProvider).getChunkAt(chunkx, chunkz) : this.getChunkIfLoaded(chunkx, chunkz));
            if (chunk == null) {
               entity.inUnloadedChunk = true;
            } else {
               int cz = chunkz << 4;
               int xstart = Math.max(i, cx);
               int xend = Math.min(j, cx + 16);
               int zstart = Math.max(i1, cz);
               int zend = Math.min(j1, cz + 16);

               for (int x = xstart; x < xend; x++) {
                  for (int z = zstart; z < zend; z++) {
                     for (int y = ystart; y < l; y++) {
                        if (flag && flag1) {
                           entity.h(false);
                        } else if (!flag && !flag1) {
                           entity.h(true);
                        }

                        IBlockData block;
                        if (!this.getWorldBorder().a(x, z) && flag1) {
                           block = Blocks.STONE.getBlockData();
                        } else {
                           block = chunk.getBlockData(x, y, z);
                        }

                        if (block != null) {
                           Block b = block.getBlock();
                           if (collides
                              && (
                                 b instanceof BlockSign
                                    || b instanceof BlockFenceGate
                                    || b instanceof BlockTorch
                                    || b instanceof BlockButtonAbstract
                                    || b instanceof BlockLever
                                    || b instanceof BlockTripwireHook
                                    || b instanceof BlockTripwire
                                    || b instanceof BlockChest
                                    || b instanceof BlockSlowSand
                                    || b instanceof BlockBed
                                    || b instanceof BlockEnderChest
                                    || b instanceof BlockEnchantmentTable
                                    || b instanceof BlockBrewingStand
                              )) {
                              AxisAlignedBB aabb = AxisAlignedBB.a(x, y, z, x + 1.0, y + 1.0, z + 1.0);
                              if (axisalignedbb.b(aabb)) {
                                 arraylist.add(aabb);
                              }
                           } else {
                              b.a(this, pos.c(x, y, z), block, axisalignedbb, arraylist, entity);
                           }

                           if (!arraylist.isEmpty()) {
                              return false;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   public List<BlockPosition> getCollisionBlocks(Entity entity, AxisAlignedBB axisalignedbb) {
      List<BlockPosition> blockPositions = new ArrayList<>();
      OptimizedNonEmptyList<AxisAlignedBB> arraylist = new OptimizedNonEmptyList();
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d + 1.0);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e + 1.0);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f + 1.0);
      WorldBorder worldborder = this.getWorldBorder();
      boolean flag = entity.aT();
      boolean flag1 = this.a(worldborder, entity);
      boolean chestsAndAnvil = (Boolean)BlazeConfig.playerPhaseChestsAndAnvils.getValue();
      int ystart = Math.max(k - 1, 0);
      BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition();

      for (int chunkx = i >> 4; chunkx <= j - 1 >> 4; chunkx++) {
         int cx = chunkx << 4;

         for (int chunkz = i1 >> 4; chunkz <= j1 - 1 >> 4; chunkz++) {
            Chunk chunk = entity.loadChunks && chunkx == Entity.lastChunkX && chunkz == Entity.lastChunkZ
               ? Entity.lastChunk
               : (entity.loadChunks ? ((ChunkProviderServer)entity.world.chunkProvider).getChunkAt(chunkx, chunkz) : this.getChunkIfLoaded(chunkx, chunkz));
            if (chunk == null) {
               entity.inUnloadedChunk = true;
            } else {
               int cz = chunkz << 4;
               int xstart = Math.max(i, cx);
               int xend = Math.min(j, cx + 16);
               int zstart = Math.max(i1, cz);
               int zend = Math.min(j1, cz + 16);

               for (int x = xstart; x < xend; x++) {
                  for (int z = zstart; z < zend; z++) {
                     for (int y = ystart; y < l; y++) {
                        if (flag && flag1) {
                           entity.h(false);
                        } else if (!flag && !flag1) {
                           entity.h(true);
                        }

                        IBlockData block;
                        if (!this.getWorldBorder().a(x, z) && flag1) {
                           block = Blocks.STONE.getBlockData();
                        } else {
                           block = chunk.getBlockData(x, y, z);
                        }

                        if (block != null) {
                           Block b = block.getBlock();
                           if (chestsAndAnvil || !(b instanceof BlockAnvil) && !(b instanceof BlockChest)) {
                              b.a(this, pos.c(x, y, z), block, axisalignedbb, arraylist, entity);
                              if (!arraylist.isEmpty()) {
                                 blockPositions.add(new BlockPosition(x, y, z));
                                 arraylist.clear();
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return blockPositions;
   }

   public boolean a(WorldBorder worldborder, Entity entity) {
      double d0 = worldborder.b();
      double d1 = worldborder.c();
      double d2 = worldborder.d();
      double d3 = worldborder.e();
      if (entity.aT()) {
         d0++;
         d1++;
         d2--;
         d3--;
      } else {
         d0--;
         d1--;
         d2++;
         d3++;
      }

      return entity.locX > d0 && entity.locX < d2 && entity.locZ > d1 && entity.locZ < d3;
   }

   public List<AxisAlignedBB> a(AxisAlignedBB axisalignedbb) {
      List<AxisAlignedBB> arraylist = Lists.newArrayList();
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d + 1.0);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e + 1.0);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f + 1.0);
      BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

      for (int k1 = i; k1 < j; k1++) {
         for (int l1 = i1; l1 < j1; l1++) {
            if (this.isLoaded(k1, 64, l1)) {
               for (int i2 = k - 1; i2 < l; i2++) {
                  blockposition_mutableblockposition.c(k1, i2, l1);
                  IBlockData iblockdata;
                  if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
                     iblockdata = this.getType(blockposition_mutableblockposition);
                  } else {
                     iblockdata = Blocks.BEDROCK.getBlockData();
                  }

                  iblockdata.getBlock().a(this, blockposition_mutableblockposition, iblockdata, axisalignedbb, arraylist, null);
               }
            }
         }
      }

      return arraylist;
   }

   public int a(float f) {
      float f1 = this.c(f);
      float f2 = 1.0F - (MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
      f2 = MathHelper.a(f2, 0.0F, 1.0F);
      f2 = 1.0F - f2;
      f2 = (float)(f2 * (1.0 - this.j(f) * 5.0F / 16.0));
      f2 = (float)(f2 * (1.0 - this.h(f) * 5.0F / 16.0));
      f2 = 1.0F - f2;
      return (int)(f2 * 11.0F);
   }

   public float c(float f) {
      return this.worldProvider.a(this.worldData.getDayTime(), f);
   }

   public float y() {
      return WorldProvider.a[this.worldProvider.a(this.worldData.getDayTime())];
   }

   public float d(float f) {
      return this.c(f) * (float) Math.PI * 2.0F;
   }

   public BlockPosition q(BlockPosition blockposition) {
      return this.getChunkAtWorldCoords(blockposition).h(blockposition);
   }

   public BlockPosition q(int x, int z) {
      return this.getChunkAtWorldCoords(x, z).h(x, z);
   }

   public BlockPosition r(int x, int z) {
      Chunk chunk = this.getChunkAtWorldCoords(x, z);
      int y1 = chunk.g() + 16;

      while (y1 >= 0) {
         int y2 = y1 - 1;
         Material material = chunk.getTypeAbs(x, y2, z).getMaterial();
         if (material.isSolid() && material != Material.LEAVES) {
            break;
         }

         y1 = y2;
      }

      return new BlockPosition(x, y1, z);
   }

   public BlockPosition r(BlockPosition blockposition) {
      return this.r(blockposition.getX(), blockposition.getZ());
   }

   public void a(BlockPosition blockposition, Block block, int i) {
   }

   public void a(BlockPosition blockposition, Block block, int i, int j) {
   }

   public void b(BlockPosition blockposition, Block block, int i, int j) {
   }

   public void tickEntities() {
      for (int i = 0; i < this.k.size(); i++) {
         Entity entity = this.k.get(i);
         if (entity != null) {
            try {
               entity.ticksLived++;
               entity.t_();
            } catch (Throwable var18) {
               CrashReport crashreport = CrashReport.a(var18, "Ticking entity");
               CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being ticked");
               entity.appendEntityCrashDetails(crashreportsystemdetails);
               throw new ReportedException(crashreport);
            }

            if (entity.dead) {
               this.k.remove(i--);
            }
         }
      }

      this.entityList.removeAll(this.g);
      Chunk chunk = null;

      for (Entity en : this.g) {
         int j = en.ae;
         int k = en.ag;
         if (en.ad) {
            if (chunk == null || !chunk.o() || chunk.locX != j || chunk.locZ != k) {
               chunk = this.getChunkIfLoaded(j, k);
            }

            if (chunk != null) {
               chunk.b(en);
            }
         }
      }

      for (Entity enx : this.g) {
         this.b(enx);
      }

      this.g.clear();
      ActivationRange.activateEntities(this);
      this.guardEntityList = true;
      Entity lastEntity = null;

      for (this.tickPosition = 0; this.tickPosition < this.entityList.size(); this.tickPosition++) {
         Entity entity = this.entityList.get(this.tickPosition);
         if (entity.vehicle != null) {
            if (!entity.vehicle.dead && entity.vehicle.passenger == entity) {
               continue;
            }

            entity.vehicle.passenger = null;
            entity.vehicle = null;
         }

         if (!entity.dead) {
            try {
               if (lastEntity != null && entity.merge(lastEntity)) {
                  entity.die();
                  lastEntity.potential = lastEntity.potential + entity.potential;
               } else {
                  lastEntity = entity;
                  this.g(entity);
               }
            } catch (Throwable var20) {
               String msg = "Entity threw exception at " + entity.world.getWorld().getName() + ":" + entity.locX + "," + entity.locY + "," + entity.locZ;
               System.err.println(msg);
               var20.printStackTrace();
               entity.dead = true;
            }
         }

         if (entity.dead) {
            int j = entity.ae;
            int k = entity.ag;
            if (entity.ad) {
               if (chunk == null || !chunk.o() || chunk.locX != j || chunk.locZ != k) {
                  chunk = this.getChunkIfLoaded(j, k);
               }

               if (chunk != null) {
                  chunk.b(entity);
               }
            }

            this.guardEntityList = false;
            this.entityList.remove(this.tickPosition--);
            this.guardEntityList = true;
            this.b(entity);
         }
      }

      Entity.lastChunkZ = Integer.MAX_VALUE;
      Entity.lastChunkX = Integer.MAX_VALUE;
      Entity.lastChunk = null;
      this.guardEntityList = false;
      if ((Boolean)BlazeConfig.tickTileEntities.getValue()) {
         this.M = true;
         if (!this.c.isEmpty()) {
            Set<TileEntity> toRemove = Collections.newSetFromMap(new IdentityHashMap<>());
            toRemove.addAll(this.c);
            this.tileEntityList.removeAll(toRemove);
            this.c.clear();
         }

         Chunk tileChunk = null;

         for (int tileTickPosition = 0; tileTickPosition < this.tileEntityList.size(); tileTickPosition++) {
            TileEntity tileentity = this.tileEntityList.get(tileTickPosition);
            if (tileentity == null) {
               this.getServer().getLogger().severe("Spigot has detected a null entity and has removed it, preventing a crash");
               this.tileEntityList.remove(tileTickPosition--);
            } else {
               if (!tileentity.x() && tileentity.t()) {
                  BlockPosition pos = tileentity.getPosition();
                  int chunkX = pos.getX() >> 4;
                  int chunkZ = pos.getZ() >> 4;
                  boolean loaded;
                  if (tileChunk != null && tileChunk.locX == chunkX && tileChunk.locZ == chunkZ) {
                     loaded = true;
                  } else {
                     tileChunk = this.getChunkIfLoaded(chunkX, chunkZ);
                     loaded = tileChunk != null;
                  }

                  if (loaded && this.N.a(pos)) {
                     try {
                        ((IUpdatePlayerListBox)tileentity).c();
                     } catch (Throwable var19) {
                        String msg = "TileEntity threw exception at "
                           + tileentity.world.getWorld().getName()
                           + ":"
                           + pos.getX()
                           + ","
                           + pos.getY()
                           + ","
                           + pos.getZ();
                        System.err.println(msg);
                        var19.printStackTrace();
                        this.getServer().getPluginManager().callEvent(new ServerExceptionEvent(new ServerInternalException(msg, var19)));
                        this.tileEntityList.remove(tileTickPosition--);
                        continue;
                     }
                  }
               }

               if (tileentity.x()) {
                  this.tileEntityList.remove(tileTickPosition--);
                  BlockPosition position = tileentity.getPosition();
                  int chunkXx = position.getX() >> 4;
                  int chunkZx = position.getZ() >> 4;
                  if (Utils.isInvalidChunk(chunk, chunkXx, chunkZx)) {
                     chunk = this.getChunkIfLoaded(chunkXx, chunkZx);
                  }

                  if (chunk != null) {
                     chunk.e(position);
                  }
               }
            }
         }

         this.M = false;
         if (!this.b.isEmpty()) {
            for (TileEntity tileentity1 : this.b) {
               if (!tileentity1.x()) {
                  BlockPosition positionx = tileentity1.getPosition();
                  int chunkXxx = positionx.getX() >> 4;
                  int chunkZxx = positionx.getZ() >> 4;
                  if (Utils.isInvalidChunk(chunk, chunkXxx, chunkZxx)) {
                     chunk = this.getChunkIfLoaded(chunkXxx, chunkZxx);
                  }

                  if (chunk != null) {
                     chunk.a(positionx, tileentity1);
                  }

                  this.notify(positionx);
               }
            }

            this.b.clear();
         }
      }
   }

   public boolean a(TileEntity tileentity) {
      if (tileentity instanceof IUpdatePlayerListBox) {
         this.tileEntityList.add(tileentity);
      }

      return true;
   }

   public void a(Collection<TileEntity> collection) {
      if (this.M) {
         this.b.addAll(collection);
      } else {
         for (TileEntity tileentity : collection) {
            if (tileentity instanceof IUpdatePlayerListBox) {
               this.tileEntityList.add(tileentity);
            }
         }
      }
   }

   public void g(Entity entity) {
      this.entityJoinedWorld(entity, true);
   }

   public void entityJoinedWorld(Entity entity, boolean flag) {
      int i = MathHelper.floor(entity.locX);
      int j = MathHelper.floor(entity.locZ);
      if (!ActivationRange.checkIfActive(entity)) {
         entity.ticksLived++;
         entity.inactiveTick();
         if (!this.isChunkLoaded(i, j, true)
            && (
               entity instanceof EntityEnderPearl && this.paperSpigotConfig.removeUnloadedEnderPearls
                  || entity instanceof EntityFallingBlock && this.paperSpigotConfig.removeUnloadedFallingBlocks
                  || entity instanceof EntityTNTPrimed && this.paperSpigotConfig.removeUnloadedTNTEntities
            )) {
            entity.inUnloadedChunk = true;
            entity.die();
         }
      } else {
         entity.P = entity.locX;
         entity.Q = entity.locY;
         entity.R = entity.locZ;
         entity.lastYaw = entity.yaw;
         entity.lastPitch = entity.pitch;
         if (flag && entity.ad) {
            entity.ticksLived++;
            if (entity.vehicle != null) {
               entity.ak();
            } else {
               entity.t_();
            }
         }

         if (Double.isNaN(entity.locX) || Double.isInfinite(entity.locX)) {
            entity.locX = entity.P;
         }

         if (Double.isNaN(entity.locY) || Double.isInfinite(entity.locY)) {
            entity.locY = entity.Q;
         }

         if (Double.isNaN(entity.locZ) || Double.isInfinite(entity.locZ)) {
            entity.locZ = entity.R;
         }

         if (Double.isNaN(entity.pitch) || Double.isInfinite(entity.pitch)) {
            entity.pitch = entity.lastPitch;
         }

         if (Double.isNaN(entity.yaw) || Double.isInfinite(entity.yaw)) {
            entity.yaw = entity.lastYaw;
         }

         int k = MathHelper.floor(entity.locX / 16.0);
         int l = MathHelper.floor(entity.locY / 16.0);
         int i1 = MathHelper.floor(entity.locZ / 16.0);
         if (!entity.ad || entity.ae != k || entity.af != l || entity.ag != i1) {
            if (entity.loadChunks) {
               entity.loadChunks();
            }

            if (entity.ad) {
               Chunk chunk = this.getChunkIfLoaded(entity.ae, entity.ag);
               if (chunk != null) {
                  chunk.a(entity, entity.af);
               }
            }

            Chunk chunk = this.getChunkIfLoaded(k, i1);
            if (chunk != null) {
               entity.ad = true;
               chunk.a(entity);
            } else {
               entity.ad = false;
            }
         }

         if (flag && entity.ad && entity.passenger != null) {
            if (!entity.passenger.dead && entity.passenger.vehicle == entity) {
               this.g(entity.passenger);
            } else {
               entity.passenger.vehicle = null;
               entity.passenger = null;
            }
         }
      }
   }

   public boolean b(AxisAlignedBB axisalignedbb) {
      return this.a(axisalignedbb, (Entity)null);
   }

   public boolean a(AxisAlignedBB axisalignedbb, Entity entity) {
      for (Entity entity1 : this.getEntities(null, axisalignedbb)) {
         if ((
               !(entity instanceof EntityPlayer)
                  || !(entity1 instanceof EntityPlayer)
                  || ((EntityPlayer)entity).getBukkitEntity().canSee(((EntityPlayer)entity1).getBukkitEntity())
            )
            && !entity1.dead
            && entity1.k
            && entity1 != entity
            && (entity == null || entity.vehicle != entity1 && entity.passenger != entity1)) {
            return false;
         }
      }

      return true;
   }

   public boolean c(AxisAlignedBB axisalignedbb) {
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f);

      for (int k1 = i; k1 <= j; k1++) {
         for (int l1 = k; l1 <= l; l1++) {
            for (int i2 = i1; i2 <= j1; i2++) {
               if (this.getType(k1, l1, i2).getBlock().getMaterial() != Material.AIR) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean containsLiquid(AxisAlignedBB axisalignedbb) {
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f);

      for (int k1 = i; k1 <= j; k1++) {
         for (int l1 = k; l1 <= l; l1++) {
            for (int i2 = i1; i2 <= j1; i2++) {
               if (this.getType(k1, l1, i2).getBlock().getMaterial().isLiquid()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean e(AxisAlignedBB axisalignedbb) {
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d + 1.0);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e + 1.0);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f + 1.0);
      if (this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
         for (int k1 = i; k1 < j; k1++) {
            for (int l1 = k; l1 < l; l1++) {
               for (int i2 = i1; i2 < j1; i2++) {
                  Block block = this.getType(k1, l1, i2).getBlock();
                  if (block == Blocks.FIRE || block == Blocks.FLOWING_LAVA || block == Blocks.LAVA) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean a(AxisAlignedBB axisalignedbb, Material material, Entity entity) {
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d + 1.0);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e + 1.0);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f + 1.0);
      if (!this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
         return false;
      } else {
         boolean flag = false;
         Vec3D vec3d = new Vec3D(0.0, 0.0, 0.0);
         BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

         for (int k1 = i; k1 < j; k1++) {
            for (int l1 = k; l1 < l; l1++) {
               for (int i2 = i1; i2 < j1; i2++) {
                  IBlockData iblockdata = this.getType(k1, l1, i2);
                  Block block = iblockdata.getBlock();
                  if (block.getMaterial() == material && l >= l1 + 1 - BlockFluids.b(iblockdata.get(BlockFluids.LEVEL))) {
                     flag = true;
                     vec3d = block.a(this, blockposition_mutableblockposition.c(k1, l1, i2), entity, vec3d);
                  }
               }
            }
         }

         if (vec3d.b() > 0.0 && entity.aL()) {
            vec3d = vec3d.a();
            entity.motX = entity.motX + vec3d.a * 0.014;
            entity.motY = entity.motY + vec3d.b * 0.014;
            entity.motZ = entity.motZ + vec3d.c * 0.014;
         }

         return flag;
      }
   }

   public boolean a(AxisAlignedBB axisalignedbb, Material material) {
      return this.a(this, axisalignedbb, material);
   }

   public boolean a(IBlockAccess iblockaccess, AxisAlignedBB axisalignedbb, Material material) {
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d + 1.0);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e + 1.0);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f + 1.0);
      BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

      for (int k1 = i; k1 < j; k1++) {
         for (int l1 = k; l1 < l; l1++) {
            for (int i2 = i1; i2 < j1; i2++) {
               if (iblockaccess.getType(blockposition_mutableblockposition.c(k1, l1, i2)).getBlock().getMaterial() == material) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean b(AxisAlignedBB axisalignedbb, Material material) {
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.d + 1.0);
      int k = MathHelper.floor(axisalignedbb.b);
      int l = MathHelper.floor(axisalignedbb.e + 1.0);
      int i1 = MathHelper.floor(axisalignedbb.c);
      int j1 = MathHelper.floor(axisalignedbb.f + 1.0);

      for (int k1 = i; k1 < j; k1++) {
         for (int l1 = k; l1 < l; l1++) {
            for (int i2 = i1; i2 < j1; i2++) {
               IBlockData iblockdata = this.getType(k1, l1, i2);
               Block block = iblockdata.getBlock();
               if (block.getMaterial() == material) {
                  int j2 = iblockdata.get(BlockFluids.LEVEL);
                  double d0 = l1 + 1;
                  if (j2 < 8) {
                     d0 = l1 + 1 - j2 / 8.0;
                  }

                  if (d0 >= axisalignedbb.b) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public Explosion explode(Entity entity, double d0, double d1, double d2, float f, boolean flag) {
      return this.createExplosion(entity, d0, d1, d2, f, false, flag);
   }

   public Explosion createExplosion(Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
      Explosion explosion = new Explosion(this, entity, d0, d1, d2, f, flag, flag1);
      explosion.a();
      explosion.a(true);
      return explosion;
   }

   public int getDurability(int x, int y, int z) {
      return this.getChunkAtWorldCoords(x, z).getBlockDurability(x, y, z);
   }

   public int getDurability(Chunk chunk, int x, int y, int z) {
      return !this.isValidLocation(x, y, z) ? 0 : chunk.getBlockDurability(x, y, z);
   }

   public void setDurability(Chunk chunk, int x, int y, int z, int durability) {
      if (this.isValidLocation(x, y, z) && y > 1) {
         chunk.setBlockDurability(x, y, z, durability);
      }
   }

   public float getExplosionDensity(Vec3D vec3d, AxisAlignedBB axisalignedbb) {
      int arrived = 0;
      int rays = 0;

      for (double[] vector : Explosion.densityVectors) {
         if (rays == 8 && arrived == 8) {
            return 1.0F;
         }

         if (this.explosionRayTrace(
               new Vec3D(
                  axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * vector[0] + 0.1621621665160441,
                  axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * vector[1],
                  axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * vector[2] + 0.1621621665160441
               ),
               vec3d
            )
            == null) {
            arrived++;
         }

         rays++;
      }

      return (float)arrived / rays;
   }

   public MovingObjectPosition explosionRayTrace(Vec3D vec3d, Vec3D vec3d1) {
      int i = MathHelper.floor(vec3d1.a);
      int j = MathHelper.floor(vec3d1.b);
      int k = MathHelper.floor(vec3d1.c);
      int l = MathHelper.floor(vec3d.a);
      int i1 = MathHelper.floor(vec3d.b);
      int j1 = MathHelper.floor(vec3d.c);
      BlockPosition blockposition = new BlockPosition(l, i1, j1);
      IBlockData iblockdata = this.rayTraceCache.get(blockposition);
      if (iblockdata == null) {
         iblockdata = this.getType(l, i1, j1);
         this.rayTraceCache.put(blockposition, iblockdata);
      }

      Block block = iblockdata.getBlock();
      if (block.a(iblockdata, false)) {
         MovingObjectPosition movingobjectposition = block.a(this, blockposition, vec3d, vec3d1);
         if (movingobjectposition != null) {
            return movingobjectposition;
         }
      }

      int k1 = 200;

      while (k1-- >= 0) {
         if (l == i && i1 == j && j1 == k) {
            return null;
         }

         int flag3 = 1;
         int flag4 = 1;
         int flag5 = 1;
         double d0 = 999.0;
         double d1 = 999.0;
         double d2 = 999.0;
         if (i > l) {
            d0 = l + 1.0;
         } else if (i < l) {
            d0 = l + 0.0;
         } else {
            flag3 = 0;
         }

         if (j > i1) {
            d1 = i1 + 1.0;
         } else if (j < i1) {
            d1 = i1 + 0.0;
         } else {
            flag4 = 0;
         }

         if (k > j1) {
            d2 = j1 + 1.0;
         } else if (k < j1) {
            d2 = j1 + 0.0;
         } else {
            flag5 = 0;
         }

         double d3 = 999.0;
         double d4 = 999.0;
         double d5 = 999.0;
         double d6 = vec3d1.a - vec3d.a;
         double d7 = vec3d1.b - vec3d.b;
         double d8 = vec3d1.c - vec3d.c;
         if (flag3 != 0) {
            d3 = (d0 - vec3d.a) / d6;
         }

         if (flag4 != 0) {
            d4 = (d1 - vec3d.b) / d7;
         }

         if (flag5 != 0) {
            d5 = (d2 - vec3d.c) / d8;
         }

         if (d3 == -0.0) {
            d3 = -1.0E-4;
         }

         if (d4 == -0.0) {
            d4 = -1.0E-4;
         }

         if (d5 == -0.0) {
            d5 = -1.0E-4;
         }

         EnumDirection enumdirection;
         if (d3 < d4 && d3 < d5) {
            enumdirection = i > l ? EnumDirection.WEST : EnumDirection.EAST;
            vec3d = new Vec3D(d0, vec3d.b + d7 * d3, vec3d.c + d8 * d3);
         } else if (d4 < d5) {
            enumdirection = j > i1 ? EnumDirection.DOWN : EnumDirection.UP;
            vec3d = new Vec3D(vec3d.a + d6 * d4, d1, vec3d.c + d8 * d4);
         } else {
            enumdirection = k > j1 ? EnumDirection.NORTH : EnumDirection.SOUTH;
            vec3d = new Vec3D(vec3d.a + d6 * d5, vec3d.b + d7 * d5, d2);
         }

         l = MathHelper.floor(vec3d.a) - (enumdirection == EnumDirection.EAST ? 1 : 0);
         i1 = MathHelper.floor(vec3d.b) - (enumdirection == EnumDirection.UP ? 1 : 0);
         j1 = MathHelper.floor(vec3d.c) - (enumdirection == EnumDirection.SOUTH ? 1 : 0);
         blockposition = new BlockPosition(l, i1, j1);
         iblockdata = this.rayTraceCache.get(blockposition);
         if (iblockdata == null) {
            iblockdata = this.getType(l, i1, j1);
            this.rayTraceCache.put(blockposition, iblockdata);
         }

         block = iblockdata.getBlock();
         if (block.a(iblockdata, false)) {
            MovingObjectPosition movingobjectposition = block.a(this, blockposition, vec3d, vec3d1);
            if (movingobjectposition != null) {
               return movingobjectposition;
            }
         }
      }

      return null;
   }

   public float a(Vec3D vec3d, AxisAlignedBB axisalignedbb) {
      double d0 = 1.0 / ((axisalignedbb.d - axisalignedbb.a) * 2.0 + 1.0);
      double d1 = 1.0 / ((axisalignedbb.e - axisalignedbb.b) * 2.0 + 1.0);
      double d2 = 1.0 / ((axisalignedbb.f - axisalignedbb.c) * 2.0 + 1.0);
      double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
      double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
      if (d0 >= 0.0 && d1 >= 0.0 && d2 >= 0.0) {
         int i = 0;
         int j = 0;

         for (float f = 0.0F; f <= 1.0F; f = (float)(f + d0)) {
            for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)(f1 + d1)) {
               for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2)) {
                  if (this.rayTrace(
                        new Vec3D(
                           axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * f + d3,
                           axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * f1,
                           axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * f2 + d4
                        ),
                        vec3d
                     )
                     == null) {
                     i++;
                  }

                  j++;
               }
            }
         }

         return (float)i / j;
      } else {
         return 0.0F;
      }
   }

   public boolean douseFire(EntityHuman entityhuman, BlockPosition blockposition, EnumDirection enumdirection) {
      blockposition = blockposition.shift(enumdirection);
      if (this.getType(blockposition).getBlock() == Blocks.FIRE) {
         this.a(entityhuman, 1004, blockposition, 0);
         this.setAir(blockposition);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public TileEntity getTileEntity(BlockPosition blockposition) {
      return this.getTileEntity(null, blockposition);
   }

   public TileEntity getTileEntity(Chunk chunk, BlockPosition blockposition) {
      if (!this.isValidLocation(blockposition)) {
         return null;
      } else {
         TileEntity tileentity = null;
         if (!this.capturedTileEntities.isEmpty()) {
            tileentity = this.capturedTileEntities.get(blockposition);
            if (tileentity != null) {
               return tileentity;
            }
         }

         if (this.M) {
            for (int i = 0; i < this.b.size(); i++) {
               TileEntity tileentity1 = this.b.get(i);
               if (!tileentity1.x() && tileentity1.getPosition().equals(blockposition)) {
                  tileentity = tileentity1;
                  break;
               }
            }
         }

         if (tileentity == null) {
            tileentity = (chunk != null ? chunk : this.getChunkAtWorldCoords(blockposition)).a(blockposition, Chunk.EnumTileEntityState.IMMEDIATE);
         }

         if (tileentity == null) {
            for (int ix = 0; ix < this.b.size(); ix++) {
               TileEntity tileentity1 = this.b.get(ix);
               if (!tileentity1.x() && tileentity1.getPosition().equals(blockposition)) {
                  tileentity = tileentity1;
                  break;
               }
            }
         }

         return tileentity;
      }
   }

   public TileEntity getTileEntity(Chunk chunk, BlockPosition blockposition, Block block) {
      return this.getTileEntity(chunk, blockposition);
   }

   public void setTileEntity(BlockPosition blockposition, TileEntity tileentity) {
      if (tileentity != null && !tileentity.x()) {
         if (this.captureBlockStates) {
            tileentity.a(this);
            tileentity.a(blockposition);
            this.capturedTileEntities.put(blockposition, tileentity);
            return;
         }

         if (this.M) {
            tileentity.a(blockposition);
            Iterator<TileEntity> iterator = this.b.iterator();

            while (iterator.hasNext()) {
               TileEntity tileentity1 = iterator.next();
               if (tileentity1.getPosition().equals(blockposition)) {
                  tileentity1.y();
                  iterator.remove();
               }
            }

            tileentity.a(this);
            this.b.add(tileentity);
         } else {
            this.a(tileentity);
            this.getChunkAtWorldCoords(blockposition).a(blockposition, tileentity);
         }
      }
   }

   public void t(BlockPosition blockposition) {
      TileEntity tileentity = this.getTileEntity(blockposition);
      if (tileentity != null && this.M) {
         tileentity.y();
         this.b.remove(tileentity);
      } else {
         if (tileentity != null) {
            this.b.remove(tileentity);
            this.tileEntityList.remove(tileentity);
         }

         this.getChunkAtWorldCoords(blockposition).e(blockposition);
      }
   }

   public void b(TileEntity tileentity) {
      this.c.add(tileentity);
   }

   public boolean u(BlockPosition blockposition) {
      IBlockData iblockdata = this.getType(blockposition);
      AxisAlignedBB axisalignedbb = iblockdata.getBlock().a(this, blockposition, iblockdata);
      return axisalignedbb != null && axisalignedbb.a() >= 1.0;
   }

   public boolean d(BlockPosition blockposition, boolean flag) {
      if (!this.isValidLocation(blockposition)) {
         return flag;
      } else if (this.chunkProvider.getChunkAt(blockposition).isEmpty()) {
         return flag;
      } else {
         Block block = this.getType(blockposition).getBlock();
         return block.getMaterial().k() && block.d();
      }
   }

   public void B() {
      int i = this.a(1.0F);
      if (i != this.I) {
         this.I = i;
      }
   }

   public void setSpawnFlags(boolean flag, boolean flag1) {
      this.allowMonsters = flag;
      this.allowAnimals = flag1;
   }

   public void doTick() {
      this.p();
   }

   protected void C() {
      if (this.worldData.hasStorm()) {
         this.p = 1.0F;
         if (this.worldData.isThundering()) {
            this.r = 1.0F;
         }
      }
   }

   protected void p() {
      if (!this.worldProvider.o()) {
         int i = this.worldData.A();
         if (i > 0) {
            this.worldData.i(--i);
            this.worldData.setThunderDuration(this.worldData.isThundering() ? 1 : 2);
            this.worldData.setWeatherDuration(this.worldData.hasStorm() ? 1 : 2);
         }

         int j = this.worldData.getThunderDuration();
         if (j <= 0) {
            if (this.worldData.isThundering()) {
               this.worldData.setThunderDuration(this.random.nextInt(12000) + 3600);
            } else {
               this.worldData.setThunderDuration(this.random.nextInt(168000) + 12000);
            }
         } else {
            this.worldData.setThunderDuration(--j);
            if (j <= 0) {
               this.worldData.setThundering(!this.worldData.isThundering());
            }
         }

         this.q = this.r;
         if (this.worldData.isThundering()) {
            this.r = (float)(this.r + 0.01);
         } else {
            this.r = (float)(this.r - 0.01);
         }

         this.r = MathHelper.a(this.r, 0.0F, 1.0F);
         int k = this.worldData.getWeatherDuration();
         if (k <= 0) {
            if (this.worldData.hasStorm()) {
               this.worldData.setWeatherDuration(this.random.nextInt(12000) + 12000);
            } else {
               this.worldData.setWeatherDuration(this.random.nextInt(168000) + 12000);
            }
         } else {
            this.worldData.setWeatherDuration(--k);
            if (k <= 0) {
               this.worldData.setStorm(!this.worldData.hasStorm());
            }
         }

         this.o = this.p;
         if (this.worldData.hasStorm()) {
            this.p = (float)(this.p + 0.01);
         } else {
            this.p = (float)(this.p - 0.01);
         }

         this.p = MathHelper.a(this.p, 0.0F, 1.0F);

         for (EntityHuman player : this.players) {
            if (player.world == this) {
               ((EntityPlayer)player).tickWeather();
            }
         }
      }
   }

   protected void D() {
      this.addChunksToTickList();
   }

   private void addChunksToTickList() {
      int optimalChunks = this.spigotConfig.chunksPerTick;
      if (optimalChunks > 0) {
         int chunksPerPlayer = Math.min(200, Math.max(1, (int)((double)(optimalChunks - this.players.size()) / this.players.size() + 0.5)));
         int randRange = 3 + chunksPerPlayer / 30;
         randRange = randRange > this.chunkTickRadius ? this.chunkTickRadius : randRange;
         this.growthOdds = this.modifiedOdds = Math.max(35.0F, Math.min(100.0F, (chunksPerPlayer + 1) * 100.0F / 15.0F));

         for (EntityHuman entityhuman : this.players) {
            int x = MathHelper.floor(entityhuman.locX / 16.0);
            int z = MathHelper.floor(entityhuman.locZ / 16.0);
            long key = chunkToKey(x, z);
            if (!this.chunkTickList.contains(key)) {
               Chunk chunk = ((ChunkProviderServer)this.chunkProvider).getChunkIfLoaded(key);
               if (chunk != null) {
                  this.chunkTickList.add(chunk);
               }
            }

            for (int chunk = 0; chunk < chunksPerPlayer; chunk++) {
               int dx = (this.simpleRandom.nextBoolean() ? 1 : -1) * this.simpleRandom.nextInt(randRange);
               int dz = (this.simpleRandom.nextBoolean() ? 1 : -1) * this.simpleRandom.nextInt(randRange);
               long hash = chunkToKey(dx + x, dz + z);
               Chunk c = ((ChunkProviderServer)this.chunkProvider).getChunkIfLoaded(hash);
               if (c != null) {
                  this.chunkTickList.add(c);
               }
            }
         }
      }
   }

   protected abstract int q();

   protected void a(int i, int j, Chunk chunk) {
      chunk.m();
   }

   protected void h() {
      this.D();
   }

   public void a(Block block, BlockPosition blockposition, Random random) {
      this.e = true;
      block.b(this, blockposition, this.getType(blockposition), random);
      this.e = false;
   }

   public boolean v(BlockPosition blockposition) {
      return this.e(blockposition, false);
   }

   public boolean w(BlockPosition blockposition) {
      return this.e(blockposition, true);
   }

   public boolean e(BlockPosition blockposition, boolean flag) {
      int y = blockposition.getY();
      int x = blockposition.getX();
      int z = blockposition.getZ();
      if (y >= 0 && y < 256 && this.getBiome(x, y, z).aV(x, y, z) <= 0.15F && this.b(EnumSkyBlock.BLOCK, x, y, z) < 10) {
         IBlockData iblockdata = this.getType(x, y, z);
         Block block = iblockdata.getBlock();
         if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && iblockdata.get(BlockFluids.LEVEL) == 0) {
            if (!flag) {
               return true;
            }

            return this.getType(x - 1, y, z).getBlock().getMaterial() != Material.WATER
               || this.getType(x + 1, y, z).getBlock().getMaterial() != Material.WATER
               || this.getType(x, y, z - 1).getBlock().getMaterial() != Material.WATER
               || this.getType(x, y, z + 1).getBlock().getMaterial() != Material.WATER;
         }
      }

      return false;
   }

   public boolean canBlockFreeze(Chunk chunk, BiomeBase biome, int x, int y, int z) {
      if (biome.aV(x, y, z) <= 0.15F && chunk.getBrightness(EnumSkyBlock.BLOCK, x, Math.max(y, 0), z) < 10) {
         IBlockData iblockdata = this.getType(x, y, z);
         Block block = iblockdata.getBlock();
         if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && iblockdata.get(BlockFluids.LEVEL) == 0) {
            return this.getType(x - 1, y, z).getBlock().getMaterial() != Material.WATER
               || this.getType(x + 1, y, z).getBlock().getMaterial() != Material.WATER
               || this.getType(x, y, z - 1).getBlock().getMaterial() != Material.WATER
               || this.getType(x, y, z + 1).getBlock().getMaterial() != Material.WATER;
         }
      }

      return false;
   }

   public boolean f(BlockPosition blockposition, boolean flag) {
      return this.f(blockposition.getX(), blockposition.getY(), blockposition.getZ(), flag);
   }

   public boolean f(int x, int y, int z, boolean flag) {
      return this.f(this.getBiome(x, y, z), x, y, z, flag);
   }

   public boolean f(BiomeBase biome, int x, int y, int z, boolean flag) {
      float f = biome.aV(x, y, z);
      if (f > 0.15F) {
         return false;
      } else if (!flag) {
         return true;
      } else {
         return y < 0 || y >= 256 || this.b(EnumSkyBlock.BLOCK, x, y, z) >= 10
            ? false
            : this.getType(x, y, z).getBlock().getMaterial() == Material.AIR && this.canPlaceSnow(x, y, z);
      }
   }

   public boolean canSnowAt(Chunk chunk, BiomeBase biome, int x, int y, int z, boolean flag) {
      float f = biome.aV(x, y, z);
      if (f > 0.15F) {
         return false;
      } else if (!flag) {
         return true;
      } else {
         return y < 0 || chunk.getBrightness(EnumSkyBlock.BLOCK, x, y, z) >= 10
            ? false
            : this.getType(x, y, z).getBlock().getMaterial() == Material.AIR && this.canPlaceSnow(x, y, z);
      }
   }

   private boolean canPlaceSnow(int x, int y, int z) {
      IBlockData iblockdata = this.getType(x, y - 1, z);
      Block block = iblockdata.getBlock();
      return block != Blocks.ICE
         && block != Blocks.PACKED_ICE
         && (
            block.getMaterial() == Material.LEAVES
               || block == Blocks.SNOW_LAYER && iblockdata.get(BlockSnow.LAYERS) >= 7
               || block.c() && block.material.isSolid()
         );
   }

   public boolean x(BlockPosition position) {
      return this.x(position.getX(), position.getY(), position.getZ());
   }

   public boolean x(int x, int y, int z) {
      if ((Boolean)BlazeConfig.disableBlockLighting.getValue()) {
         return true;
      } else {
         boolean flag = false;
         if (!this.worldProvider.o()) {
            flag = this.updateLight(EnumSkyBlock.SKY, x, y, z);
         }

         return flag | this.updateLight(EnumSkyBlock.BLOCK, x, y, z);
      }
   }

   public boolean c(EnumSkyBlock enumskyblock, BlockPosition blockposition, Chunk chunk, List<Chunk> neighbors) {
      return this.c(enumskyblock, blockposition.getX(), blockposition.getY(), blockposition.getZ(), chunk, neighbors);
   }

   public boolean c(EnumSkyBlock enumskyblock, int x, int y, int z, Chunk chunk, List<Chunk> neighbors) {
      if (chunk == null) {
         return false;
      } else {
         int i = 0;
         int j = 0;
         int k = this.b(enumskyblock, x, y, z);
         int l = this.a(x, y, z, enumskyblock);
         if (l > k) {
            this.H[j++] = 133152;
         } else if (l < k) {
            this.H[j++] = 133152 | k << 18;

            while (i < j) {
               int l1 = this.H[i++];
               int i2 = (l1 & 63) - 32 + x;
               int j2 = (l1 >> 6 & 63) - 32 + y;
               int k2 = (l1 >> 12 & 63) - 32 + z;
               int l3 = l1 >> 18 & 15;
               int l2 = this.b(enumskyblock, i2, j2, k2);
               if (l2 == l3) {
                  this.a(enumskyblock, i2, j2, k2, 0);
                  if (l3 > 0) {
                     int i3 = MathHelper.a(i2 - x);
                     int j3 = MathHelper.a(j2 - y);
                     int k3 = MathHelper.a(k2 - z);
                     if (i3 + j3 + k3 < 17) {
                        EnumDirection[] var23;
                        for (EnumDirection enumdirection : var23 = EnumDirection.values()) {
                           int k4 = i2 + enumdirection.getAdjacentX();
                           int l4 = j2 + enumdirection.getAdjacentY();
                           int i5 = k2 + enumdirection.getAdjacentZ();
                           int j5 = Math.max(1, this.getType(k4, l4, i5).getBlock().p());
                           l2 = this.b(enumskyblock, k4, l4, i5);
                           if (l2 == l3 - j5 && j < this.H.length) {
                              this.H[j++] = k4 - x + 32 | l4 - y + 32 << 6 | i5 - z + 32 << 12 | l3 - j5 << 18;
                           }
                        }
                     }
                  }
               }
            }

            i = 0;
         }

         while (i < j) {
            int l1 = this.H[i++];
            int i2 = (l1 & 63) - 32 + x;
            int j2 = (l1 >> 6 & 63) - 32 + y;
            int k2 = (l1 >> 12 & 63) - 32 + z;
            int k5 = this.b(enumskyblock, i2, j2, k2);
            int l2 = this.a(i2, j2, k2, enumskyblock);
            if (l2 != k5) {
               this.a(enumskyblock, i2, j2, k2, l2);
               if (l2 > k5) {
                  int i3 = Math.abs(i2 - x);
                  int j3 = Math.abs(j2 - y);
                  int k3 = Math.abs(k2 - z);
                  boolean flag = j < this.H.length - 6;
                  if (i3 + j3 + k3 < 17 && flag) {
                     if (this.b(enumskyblock, i2 - 1, j2, k2) < l2) {
                        this.H[j++] = i2 - 1 - x + 32 + (j2 - y + 32 << 6) + (k2 - z + 32 << 12);
                     }

                     if (this.b(enumskyblock, i2 + 1, j2, k2) < l2) {
                        this.H[j++] = i2 + 1 - x + 32 + (j2 - y + 32 << 6) + (k2 - z + 32 << 12);
                     }

                     if (this.b(enumskyblock, i2, j2 - 1, k2) < l2) {
                        this.H[j++] = i2 - x + 32 + (j2 - 1 - y + 32 << 6) + (k2 - z + 32 << 12);
                     }

                     if (this.b(enumskyblock, i2, j2 + 1, k2) < l2) {
                        this.H[j++] = i2 - x + 32 + (j2 + 1 - y + 32 << 6) + (k2 - z + 32 << 12);
                     }

                     if (this.b(enumskyblock, i2, j2, k2 - 1) < l2) {
                        this.H[j++] = i2 - x + 32 + (j2 - y + 32 << 6) + (k2 - 1 - z + 32 << 12);
                     }

                     if (this.b(enumskyblock, i2, j2, k2 + 1) < l2) {
                        this.H[j++] = i2 - x + 32 + (j2 - y + 32 << 6) + (k2 + 1 - z + 32 << 12);
                     }
                  }
               }
            }
         }

         if (chunk.world.paperSpigotConfig.useAsyncLighting) {
            chunk.pendingLightUpdates.decrementAndGet();
            if (neighbors != null) {
               for (Chunk neighbor : neighbors) {
                  neighbor.pendingLightUpdates.decrementAndGet();
               }
            }
         }

         return true;
      }
   }

   public boolean updateLight(EnumSkyBlock enumskyblock, int x, int y, int z) {
      if ((Boolean)BlazeConfig.disableBlockLighting.getValue()) {
         return true;
      } else {
         Chunk chunk = this.getChunkIfLoaded(x >> 4, z >> 4);
         if (chunk != null && chunk.areNeighborsLoaded(2)) {
            if (!chunk.world.paperSpigotConfig.useAsyncLighting) {
               return this.c(enumskyblock, x, y, z, chunk, null);
            } else {
               chunk.pendingLightUpdates.incrementAndGet();
               chunk.lightUpdateTime = chunk.world.getTime();
               List<Chunk> neighbors = new ArrayList<>();
               int chunkX = chunk.locX;
               int chunkZ = chunk.locZ;

               for (int cx = chunkX - 2; cx <= chunkX + 2; cx++) {
                  for (int cz = chunkZ - 2; cz <= chunkZ + 2; cz++) {
                     if (cx != chunkX || cz != chunkZ) {
                        Chunk neighbor = this.getChunkIfLoaded(cx, cz);
                        if (neighbor != null) {
                           neighbor.pendingLightUpdates.incrementAndGet();
                           neighbor.lightUpdateTime = chunk.world.getTime();
                           neighbors.add(neighbor);
                        }
                     }
                  }
               }

               if (!Bukkit.isPrimaryThread()) {
                  return this.c(enumskyblock, x, y, z, chunk, neighbors);
               } else {
                  this.lightingExecutor.submit(() -> this.c(enumskyblock, x, y, z, chunk, neighbors));
                  return true;
               }
            }
         } else {
            return false;
         }
      }
   }

   public boolean updateLight(EnumSkyBlock enumskyblock, BlockPosition position) {
      return this.updateLight(enumskyblock, position.getX(), position.getY(), position.getZ());
   }

   public boolean a(boolean flag) {
      return false;
   }

   public List<NextTickListEntry> a(Chunk chunk, boolean flag) {
      return null;
   }

   public void a(BlockPosition blockposition, Chunk chunk, Block block, int i, int j) {
   }

   public List<NextTickListEntry> a(StructureBoundingBox structureboundingbox, boolean flag) {
      return null;
   }

   public List<Entity> getEntities(Entity entity, AxisAlignedBB axisalignedbb) {
      return this.a(entity, axisalignedbb, IEntitySelector.d);
   }

   public List<Entity> a(Entity entity, AxisAlignedBB axisalignedbb, Predicate<? super Entity> predicate) {
      List<Entity> arraylist = Lists.newArrayList();
      int i = MathHelper.floor((axisalignedbb.a - 2.0) / 16.0);
      int j = MathHelper.floor((axisalignedbb.d + 2.0) / 16.0);
      int k = MathHelper.floor((axisalignedbb.c - 2.0) / 16.0);
      int l = MathHelper.floor((axisalignedbb.f + 2.0) / 16.0);

      for (int i1 = i; i1 <= j; i1++) {
         for (int j1 = k; j1 <= l; j1++) {
            Chunk chunk = this.getChunkIfLoaded(i1, j1);
            if (chunk != null) {
               chunk.a(entity, axisalignedbb, arraylist, predicate);
            }
         }
      }

      return arraylist;
   }

   public <T extends Entity> List<T> a(Class<? extends T> oclass, Predicate<? super T> predicate) {
      List<T> arraylist = Lists.newArrayList();

      for (Entity entity : this.entityList) {
         if (oclass.isAssignableFrom(entity.getClass()) && ((Predicate<Entity>)predicate).apply(entity)) {
            arraylist.add((T)entity);
         }
      }

      return arraylist;
   }

   public <T extends Entity> List<T> b(Class<? extends T> oclass, Predicate<? super T> predicate) {
      List<T> arraylist = Lists.newArrayList();

      for (Entity entity : this.players) {
         if (oclass.isAssignableFrom(entity.getClass()) && ((Predicate<Entity>)predicate).apply(entity)) {
            arraylist.add((T)entity);
         }
      }

      return arraylist;
   }

   public <T extends Entity> List<T> a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb) {
      return this.a(oclass, axisalignedbb, IEntitySelector.d);
   }

   public <T extends Entity> List<T> a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, Predicate<? super T> predicate) {
      int i = MathHelper.floor((axisalignedbb.a - 2.0) / 16.0);
      int j = MathHelper.floor((axisalignedbb.d + 2.0) / 16.0);
      int k = MathHelper.floor((axisalignedbb.c - 2.0) / 16.0);
      int l = MathHelper.floor((axisalignedbb.f + 2.0) / 16.0);
      List<T> arraylist = Lists.newArrayList();

      for (int i1 = i; i1 <= j; i1++) {
         for (int j1 = k; j1 <= l; j1++) {
            Chunk chunk = this.getChunkIfLoaded(i1, j1);
            if (chunk != null) {
               chunk.a(oclass, axisalignedbb, arraylist, predicate);
            }
         }
      }

      return arraylist;
   }

   public <T extends Entity> T a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, T t0) {
      Entity entity = null;
      double d0 = Double.MAX_VALUE;

      for (Entity entity1 : this.a(oclass, axisalignedbb)) {
         if (entity1 != t0 && IEntitySelector.d.apply(entity1)) {
            double d1 = t0.h(entity1);
            if (d1 <= d0) {
               entity = entity1;
               d0 = d1;
            }
         }
      }

      return (T)entity;
   }

   public Entity a(int i) {
      return this.entitiesById.get(i);
   }

   public void b(BlockPosition blockposition, TileEntity tileentity) {
      if (this.isValidLocation(blockposition)) {
         Chunk chunk = this.getChunkIfLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4);
         if (chunk != null) {
            chunk.e();
         }
      }
   }

   public int a(Class<?> oclass) {
      int i = 0;

      for (Entity entity : this.entityList) {
         if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient)entity;
            if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent()) {
               continue;
            }
         }

         if (oclass.isAssignableFrom(entity.getClass())) {
            i++;
         }
      }

      return i;
   }

   public void b(Collection<Entity> collection) {
      for (Entity entity : collection) {
         this.entityList.add(entity);
         this.a(entity);
      }
   }

   public void c(Collection<Entity> collection) {
      this.g.addAll(collection);
   }

   public boolean a(Block block, BlockPosition blockposition, boolean flag, EnumDirection enumdirection, Entity entity, ItemStack itemstack) {
      Block block1 = this.getType(blockposition).getBlock();
      AxisAlignedBB axisalignedbb = flag ? null : block.a(this, blockposition, block.getBlockData());
      boolean defaultReturn = (axisalignedbb == null || this.a(axisalignedbb, entity))
         && (
            block1.getMaterial() == Material.ORIENTABLE && block == Blocks.ANVIL
               || block1.getMaterial().isReplaceable() && block.canPlace(this, blockposition, enumdirection, itemstack)
         );
      BlockCanBuildEvent event = new BlockCanBuildEvent(
         this.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), CraftMagicNumbers.getId(block), defaultReturn
      );
      this.getServer().getPluginManager().callEvent(event);
      return event.isBuildable();
   }

   public int F() {
      return this.a;
   }

   public void b(int i) {
      this.a = i;
   }

   @Override
   public int getBlockPower(BlockPosition blockposition, EnumDirection enumdirection) {
      IBlockData iblockdata = this.getType(blockposition);
      return iblockdata.getBlock().b(this, blockposition, iblockdata, enumdirection);
   }

   public WorldType G() {
      return this.worldData.getType();
   }

   public int getBlockPower(BlockPosition blockposition) {
      int i = Math.max(0, this.getBlockPower(blockposition.down(), EnumDirection.DOWN));
      if (i >= 15) {
         return i;
      } else {
         i = Math.max(i, this.getBlockPower(blockposition.up(), EnumDirection.UP));
         if (i >= 15) {
            return i;
         } else {
            i = Math.max(i, this.getBlockPower(blockposition.north(), EnumDirection.NORTH));
            if (i >= 15) {
               return i;
            } else {
               i = Math.max(i, this.getBlockPower(blockposition.south(), EnumDirection.SOUTH));
               if (i >= 15) {
                  return i;
               } else {
                  i = Math.max(i, this.getBlockPower(blockposition.west(), EnumDirection.WEST));
                  return i >= 15 ? i : Math.max(i, this.getBlockPower(blockposition.east(), EnumDirection.EAST));
               }
            }
         }
      }
   }

   public boolean isBlockFacePowered(BlockPosition blockposition, EnumDirection enumdirection) {
      return this.getBlockFacePower(blockposition, enumdirection) > 0;
   }

   public int getBlockFacePower(BlockPosition blockposition, EnumDirection enumdirection) {
      IBlockData iblockdata = this.getType(blockposition);
      Block block = iblockdata.getBlock();
      return block.isOccluding() ? this.getBlockPower(blockposition) : block.a(this, blockposition, iblockdata, enumdirection);
   }

   public boolean isBlockIndirectlyPowered(BlockPosition blockposition) {
      return this.getBlockFacePower(blockposition.down(), EnumDirection.DOWN) > 0
         || this.getBlockFacePower(blockposition.up(), EnumDirection.UP) > 0
         || this.getBlockFacePower(blockposition.north(), EnumDirection.NORTH) > 0
         || this.getBlockFacePower(blockposition.south(), EnumDirection.SOUTH) > 0
         || this.getBlockFacePower(blockposition.west(), EnumDirection.WEST) > 0
         || this.getBlockFacePower(blockposition.east(), EnumDirection.EAST) > 0;
   }

   public int A(BlockPosition blockposition) {
      int i = 0;

      EnumDirection[] var6;
      for (EnumDirection enumdirection : var6 = EnumDirection.values()) {
         int l = this.getBlockFacePower(blockposition.shift(enumdirection), enumdirection);
         if (l >= 15) {
            return 15;
         }

         if (l > i) {
            i = l;
         }
      }

      return i;
   }

   public EntityHuman findNearbyPlayer(Entity entity, double d0) {
      return this.findNearbyPlayer(entity.locX, entity.locY, entity.locZ, d0);
   }

   public EntityHuman findNearbyPlayer(double d0, double d1, double d2, double d3) {
      if (0.0 <= d3 && d3 <= 64.0) {
         return this.playerMap.getNearestPlayer(d0, d1, d2, d3);
      } else {
         double d4 = -1.0;
         double squaredDistance = d3 * d3;
         EntityHuman entityhuman = null;

         for (EntityHuman entityhuman1 : this.players) {
            if (!entityhuman1.dead && !entityhuman1.isSpectator()) {
               double d5 = entityhuman1.e(d0, d1, d2);
               if ((d3 < 0.0 || d5 < squaredDistance) && (d4 == -1.0 || d5 < d4)) {
                  d4 = d5;
                  entityhuman = entityhuman1;
               }
            }
         }

         return entityhuman;
      }
   }

   public boolean isPlayerNearby(double d0, double d1, double d2, double d3) {
      double squaredDistance = d3 * d3;

      for (EntityHuman entityhuman : this.players) {
         if (!entityhuman.isSpectator()) {
            double d4 = entityhuman.e(d0, d1, d2);
            if (d3 < 0.0 || d4 < squaredDistance) {
               return true;
            }
         }
      }

      return false;
   }

   public EntityHuman findNearbyPlayerWhoAffectsSpawning(Entity entity, double d0) {
      return this.findNearbyPlayerWhoAffectsSpawning(entity.locX, entity.locY, entity.locZ, d0);
   }

   public EntityHuman findNearbyPlayerWhoAffectsSpawning(double x, double y, double z, double distance) {
      if (0.0 <= distance && distance <= 64.0) {
         return this.playerMap.getNearbyPlayer(x, y, z, distance, true);
      } else {
         double d4 = -1.0;
         double squaredDistance = distance * distance;
         EntityHuman entityhuman = null;

         for (EntityHuman entityhuman1 : this.players) {
            if (!entityhuman1.dead && entityhuman1.affectsSpawning) {
               double d5 = entityhuman1.e(x, y, z);
               if ((distance < 0.0 || d5 < squaredDistance) && (d4 == -1.0 || d5 < d4) && !entityhuman1.isSpectator()) {
                  d4 = d5;
                  entityhuman = entityhuman1;
               }
            }
         }

         return entityhuman;
      }
   }

   public boolean isPlayerNearbyWhoAffectsSpawning(double d0, double d1, double d2, double d3) {
      if (!(0.0 <= d3) || !(d3 <= 64.0)) {
         double squaredDistance = d3 * d3;

         for (EntityHuman entityhuman : this.players) {
            if (entityhuman.affectsSpawning && !entityhuman.isSpectator()) {
               double d4 = entityhuman.e(d0, d1, d2);
               if (d3 < 0.0 || d4 < squaredDistance) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return this.playerMap.getNearbyPlayer(d0, d1, d2, d3, true) != null;
      }
   }

   public EntityHuman a(String s) {
      for (EntityHuman entityhuman : this.players) {
         if (s.equals(entityhuman.getName())) {
            return entityhuman;
         }
      }

      return null;
   }

   public EntityHuman b(UUID uuid) {
      for (EntityHuman entityhuman : this.players) {
         if (uuid.equals(entityhuman.getUniqueID())) {
            return entityhuman;
         }
      }

      return null;
   }

   public void checkSession() throws ExceptionWorldConflict {
      this.dataManager.checkSession();
   }

   public long getSeed() {
      return this.worldData.getSeed();
   }

   public long getTime() {
      return this.worldData.getTime();
   }

   public long getDayTime() {
      return this.worldData.getDayTime();
   }

   public void setDayTime(long i) {
      this.worldData.setDayTime(i);
   }

   public BlockPosition getSpawn() {
      BlockPosition blockposition = new BlockPosition(this.worldData.c(), this.worldData.d(), this.worldData.e());
      if (!this.getWorldBorder().a(blockposition)) {
         blockposition = this.getHighestBlockYAt(MathHelper.floor(this.getWorldBorder().getCenterX()), MathHelper.floor(this.getWorldBorder().getCenterZ()));
      }

      return blockposition;
   }

   public void B(BlockPosition blockposition) {
      this.worldData.setSpawn(blockposition);
   }

   public boolean a(EntityHuman entityhuman, BlockPosition blockposition) {
      return true;
   }

   public void broadcastEntityEffect(Entity entity, byte b0) {
   }

   public IChunkProvider N() {
      return this.chunkProvider;
   }

   public void playBlockAction(BlockPosition blockposition, Block block, int i, int j) {
      block.a(this, blockposition, this.getType(blockposition), i, j);
   }

   public IDataManager getDataManager() {
      return this.dataManager;
   }

   public WorldData getWorldData() {
      return this.worldData;
   }

   public GameRules getGameRules() {
      return this.worldData.x();
   }

   public void everyoneSleeping() {
   }

   public void checkSleepStatus() {
      this.everyoneSleeping();
   }

   public float h(float f) {
      return (this.q + (this.r - this.q) * f) * this.j(f);
   }

   public float j(float f) {
      return this.o + (this.p - this.o) * f;
   }

   public boolean R() {
      return this.h(1.0F) > 0.9;
   }

   public boolean S() {
      return this.j(1.0F) > 0.2;
   }

   public boolean isRainingAt(int x, int y, int z) {
      if (this.S() && this.i(x, y, z) && this.q(x, z).getY() <= y) {
         BiomeBase biomebase = this.getBiome(x, y, z);
         return !biomebase.d() && !this.f(biomebase, x, y, z, false) && biomebase.e();
      } else {
         return false;
      }
   }

   public boolean isRainingAt(BlockPosition blockposition) {
      return this.isRainingAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public boolean D(BlockPosition blockposition) {
      return this.getBiome(blockposition).f();
   }

   public PersistentCollection T() {
      return this.worldMaps;
   }

   public void a(String s, PersistentBase persistentbase) {
      this.worldMaps.a(s, persistentbase);
   }

   public PersistentBase a(Class<? extends PersistentBase> oclass, String s) {
      return this.worldMaps.get(oclass, s);
   }

   public int b(String s) {
      return this.worldMaps.a(s);
   }

   public void a(int i, BlockPosition blockposition, int j) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.a(i, blockposition, j);
      }
   }

   public void triggerEffect(int i, BlockPosition blockposition, int j) {
      this.a(null, i, blockposition, j);
   }

   public void a(EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
      try {
         for (IWorldAccess iWorldAccess : this.u) {
            iWorldAccess.a(entityhuman, i, blockposition, j);
         }
      } catch (Throwable var8) {
         CrashReport crashreport = CrashReport.a(var8, "Playing level event");
         CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Level event being played");
         crashreportsystemdetails.a("Block coordinates", CrashReportSystemDetails.a(blockposition));
         crashreportsystemdetails.a("Event source", entityhuman);
         crashreportsystemdetails.a("Event type", i);
         crashreportsystemdetails.a("Event data", j);
         throw new ReportedException(crashreport);
      }
   }

   public int getHeight() {
      return 256;
   }

   public int V() {
      return this.worldProvider.o() ? 128 : 256;
   }

   public Random a(int i, int j, int k) {
      long l = i * 341873128712L + j * 132897987541L + this.getWorldData().getSeed() + k;
      this.random.setSeed(l);
      return this.random;
   }

   public BlockPosition a(String s, BlockPosition blockposition) {
      return this.N().findNearestMapFeature(this, s, blockposition);
   }

   public CrashReportSystemDetails a(CrashReport crashreport) {
      CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Affected level", 1);
      crashreportsystemdetails.a("Level name", this.worldData == null ? "????" : this.worldData.getName());
      crashreportsystemdetails.a("All players", this.players.size() + " total; " + this.players);
      crashreportsystemdetails.a("Chunk stats", this.chunkProvider.getName());

      try {
         this.worldData.a(crashreportsystemdetails);
      } catch (Throwable var4) {
         crashreportsystemdetails.a("Level Data Unobtainable", var4);
      }

      return crashreportsystemdetails;
   }

   public void c(int i, BlockPosition blockposition, int j) {
      for (IWorldAccess iWorldAccess : this.u) {
         iWorldAccess.b(i, blockposition, j);
      }
   }

   public Calendar Y() {
      if (this.getTime() % 600L == 0L) {
         this.K.setTimeInMillis(MinecraftServer.az());
      }

      return this.K;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public void updateAdjacentComparators(BlockPosition blockposition, Block block) {
      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         BlockPosition blockposition1 = blockposition.shift(enumdirection);
         if (this.isLoaded(blockposition1)) {
            IBlockData iblockdata = this.getType(blockposition1);
            if (Blocks.UNPOWERED_COMPARATOR.e(iblockdata.getBlock())) {
               iblockdata.getBlock().doPhysics(this, blockposition1, iblockdata, block, blockposition);
            } else if (iblockdata.getBlock().isOccluding()) {
               blockposition1 = blockposition1.shift(enumdirection);
               iblockdata = this.getType(blockposition1);
               if (Blocks.UNPOWERED_COMPARATOR.e(iblockdata.getBlock())) {
                  iblockdata.getBlock().doPhysics(this, blockposition1, iblockdata, block, blockposition);
               }
            }
         }
      }
   }

   public DifficultyDamageScaler E(BlockPosition blockposition) {
      return this.E(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   public EnumDifficulty getDifficulty() {
      return this.getWorldData().getDifficulty();
   }

   public int ab() {
      return this.I;
   }

   public void c(int i) {
      this.I = i;
   }

   public boolean ad() {
      return this.isLoading;
   }

   public PersistentVillage ae() {
      return this.villages;
   }

   public WorldBorder getWorldBorder() {
      return this.N;
   }

   public boolean c(int i, int j) {
      BlockPosition blockposition = this.getSpawn();
      int k = i * 16 + 8 - blockposition.getX();
      int l = j * 16 + 8 - blockposition.getZ();
      return k >= -128 && k <= 128 && l >= -128 && l <= 128 && this.keepSpawnInMemory;
   }
}
