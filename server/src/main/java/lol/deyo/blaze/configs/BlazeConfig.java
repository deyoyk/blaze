package lol.deyo.blaze.configs;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlazeConfig extends Config {
   public static final ConfigSetting<String> serverBranding = new ConfigSetting<>("Blaze", "", "Used for commands and server ping data", false);
   public static final ConfigSetting<Boolean> disableBlockLighting = new ConfigSetting<>(false, "worlds.ticking");
   public static final ConfigSetting<Integer> randomTickSpeed = new ConfigSetting<>(3, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickBlocks = new ConfigSetting<>(true, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickTileEntities = new ConfigSetting<>(true, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickBeacons = new ConfigSetting<>(true, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickBrewingStands = new ConfigSetting<>(true, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickHoppers = new ConfigSetting<>(true, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickVillages = new ConfigSetting<>(false, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickFurnaces = new ConfigSetting<>(true, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickChunks = new ConfigSetting<>(true, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickBiomeCache = new ConfigSetting<>(true, "worlds.ticking");
   public static final ConfigSetting<Boolean> tickWeather = new ConfigSetting<>(true, "worlds.ticking");
   public static final ConfigSetting<Boolean> enableSpigotConfigCommand = new ConfigSetting<>(true, "worlds.commands");
   public static final ConfigSetting<Boolean> enablePingCommand = new ConfigSetting<>(true, "worlds.commands");
   public static final ConfigSetting<Boolean> enableTPSCommand = new ConfigSetting<>(true, "worlds.commands");
   public static final ConfigSetting<Boolean> enableChunkUnloadCommand = new ConfigSetting<>(true, "worlds.commands");
   public static final ConfigSetting<Boolean> enableClearLagCommand = new ConfigSetting<>(true, "worlds.commands");
   public static final ConfigSetting<Boolean> enableSetMaxPlayersCommand = new ConfigSetting<>(true, "worlds.commands");
   public static final ConfigSetting<Boolean> cacheChunkMaps = new ConfigSetting<>(
      true,
      "worlds.chunks",
      "IMPORTANT! This patch is from paper and we highly suggest using. However if you get chunk corruption bugs turn this off. We have fixed a major issue with it."
   );
   public static final ConfigSetting<Integer> chunkLoadingThreads = new ConfigSetting<>(3, "worlds.chunks", false);
   public static final ConfigSetting<Integer> playersPerThread = new ConfigSetting<>(100, "worlds.chunks");
   public static final ConfigSetting<Boolean> chunkUnloading = new ConfigSetting<>(true, "worlds.chunks");
   public static final ConfigSetting<Integer> unloadPerTick = new ConfigSetting<>(150, "worlds.chunks");
   public static final ConfigSetting<Boolean> enableAsyncPathSearch = new ConfigSetting<>(true, "worlds.async-pathsearch");
   public static final ConfigSetting<Integer> pathSearchThreads = new ConfigSetting<>(3, "worlds.async-pathsearch", false);
   public static final ConfigSetting<Double> minimumDistanceToTarget = new ConfigSetting<>(0.0, "worlds.async-pathsearch", false);
   public static final ConfigSetting<List<String>> entitiesPathSearchOffload = new ConfigSetting<>(
      Arrays.asList(
         "BAT",
         "BLAZE",
         "CHICKEN",
         "COW",
         "CREEPER",
         "ENDERMAN",
         "HORSE",
         "IRON_GOLEM",
         "MAGMA_CUBE",
         "MUSHROOM_COW",
         "PIG",
         "PIG_ZOMBIE",
         "RABBIT",
         "SHEEP",
         "SKELETON",
         "SILVERFISH",
         "SLIME",
         "SNOWMAN",
         "SQUID",
         "WITCH",
         "ZOMBIE"
      ),
      "worlds.async-pathsearch",
      false
   );
   public static final ConfigSetting<Float> arrowTrajectoryRandomness = new ConfigSetting<>(1.0F, "worlds.projectiles");
   public static final ConfigSetting<Float> rodTrajectoryRandomness = new ConfigSetting<>(1.0F, "worlds.projectiles");
   public static final ConfigSetting<Float> snowballTrajectoryRandomness = new ConfigSetting<>(1.0F, "worlds.projectiles");
   public static final ConfigSetting<Float> eggTrajectoryRandomness = new ConfigSetting<>(1.0F, "worlds.projectiles");
   public static final ConfigSetting<Float> pearlTrajectoryRandomness = new ConfigSetting<>(1.0F, "worlds.projectiles");
   public static final ConfigSetting<Float> potionTrajectoryRandomness = new ConfigSetting<>(1.0F, "worlds.projectiles");
   public static final ConfigSetting<Boolean> itemFizzSounds = new ConfigSetting<>(true, "worlds.sounds");
   public static final ConfigSetting<Boolean> fluidFizzSounds = new ConfigSetting<>(true, "worlds.sounds");
   public static final ConfigSetting<Boolean> mobRandomSounds = new ConfigSetting<>(true, "worlds.sounds");
   public static final ConfigSetting<Boolean> explosionSounds = new ConfigSetting<>(true, "worlds.sounds");
   public static final ConfigSetting<Boolean> footstepSounds = new ConfigSetting<>(false, "worlds.sounds");
   public static final ConfigSetting<Boolean> dispenserEffects = new ConfigSetting<>(true, "worlds.particles");
   public static final ConfigSetting<Boolean> spawnerParticles = new ConfigSetting<>(true, "worlds.particles");
   public static final ConfigSetting<Boolean> explosionParticles = new ConfigSetting<>(true, "worlds.particles");
   public static final ConfigSetting<Boolean> tntParticles = new ConfigSetting<>(true, "worlds.particles");
   public static final ConfigSetting<Boolean> footstepParticles = new ConfigSetting<>(false, "worlds.particles");
   public static final ConfigSetting<Double> furnaceModifier = new ConfigSetting<>(1.0, "worlds.modifiers");
   public static final ConfigSetting<Double> brewingModifier = new ConfigSetting<>(1.0, "worlds.modifiers");
   public static final ConfigSetting<Double> fallDamageModifier = new ConfigSetting<>(1.0, "worlds.modifiers");
   public static final ConfigSetting<Double> critDamageModifier = new ConfigSetting<>(1.5, "worlds.modifiers");
   public static final ConfigSetting<Boolean> firePlayerMoveEvent = new ConfigSetting<>(true, "worlds.events");
   public static final ConfigSetting<Boolean> fireBlockPhysicsEvent = new ConfigSetting<>(true, "worlds.events");
   public static final ConfigSetting<Boolean> fireHopperMoveItemEvent = new ConfigSetting<>(true, "worlds.events");
   public static final ConfigSetting<Boolean> fireSandBlockChangeEvent = new ConfigSetting<>(true, "worlds.events");
   public static final ConfigSetting<Boolean> fireSandPhysicsEvent = new ConfigSetting<>(true, "worlds.events");
   public static final ConfigSetting<Boolean> fireRedstonePhysicsEvent = new ConfigSetting<>(true, "worlds.events");
   public static final ConfigSetting<Boolean> fireTNTDispenseEvent = new ConfigSetting<>(true, "worlds.events");
   public static final ConfigSetting<String> redstoneOptimization = new ConfigSetting<>(
      "EIGENCRAFT", "worlds.blocks", "Redstone implementation. Choose EIGENCRAFT/PANDAWIRE/DEFAULT", false
   );
   public static final ConfigSetting<Boolean> gravelFall = new ConfigSetting<>(true, "worlds.blocks");
   public static final ConfigSetting<Boolean> grassSpread = new ConfigSetting<>(true, "worlds.blocks");
   public static final ConfigSetting<Boolean> grassSpreadLight = new ConfigSetting<>(true, "worlds.blocks");
   public static final ConfigSetting<Boolean> leafDecaying = new ConfigSetting<>(true, "worlds.blocks");
   public static final ConfigSetting<Boolean> dropFloatingPlants = new ConfigSetting<>(true, "worlds.blocks");
   public static final ConfigSetting<Boolean> dropExplosionBlocks = new ConfigSetting<>(true, "worlds.blocks");
   public static final ConfigSetting<Boolean> dispenserRandomness = new ConfigSetting<>(true, "worlds.blocks", "Optimizes cannons");
   public static final ConfigSetting<Boolean> pistonPushEntities = new ConfigSetting<>(true, "worlds.blocks", "Push entities with piston");
   public static final ConfigSetting<Integer> pistonMaxBlockPush = new ConfigSetting<>(12, "worlds.blocks", "Maximum blocks pushed by pistons");
   public static final ConfigSetting<Boolean> infiniteWaterSources = new ConfigSetting<>(true, "worlds.blocks");
   public static final ConfigSetting<Boolean> waterDestroyRedstone = new ConfigSetting<>(true, "worlds.blocks");
   public static final ConfigSetting<Boolean> oldEnchantingSystem = new ConfigSetting<>(false, "worlds.blocks", "Toggle for 1.7 enchanting system");
   public static final ConfigSetting<Boolean> cobbleGenerating = new ConfigSetting<>(true, "worlds.blocks.generating", "Cobble generation from lava and water");
   public static final ConfigSetting<Boolean> stoneGenerating = new ConfigSetting<>(true, "worlds.blocks.generating", "Stone generation from lava and water");
   public static final ConfigSetting<Boolean> obsidianGenerating = new ConfigSetting<>(
      true, "worlds.blocks.generating", "Obsidian generation from lava and water"
   );
   public static final ConfigSetting<Integer> spongeRadius = new ConfigSetting<>(4, "worlds.blocks.sponges", "Sponge activation radius, 0 to disable");
   public static final ConfigSetting<Boolean> spongePreventWaterFlow = new ConfigSetting<>(false, "worlds.blocks.sponges");
   public static final ConfigSetting<Boolean> spongeInstantBreak = new ConfigSetting<>(false, "worlds.blocks.sponges", false);
   public static final ConfigSetting<Integer> chunkMobLimit = new ConfigSetting<>(
      0, "worlds.spawners", "Number of mobs in a chunk to prevent spawning. 0 - to disable requirement"
   );
   public static final ConfigSetting<Integer> minimumSpawnDelay = new ConfigSetting<>(200, "worlds.spawners");
   public static final ConfigSetting<Integer> maximumSpawnDelay = new ConfigSetting<>(800, "worlds.spawners");
   public static final ConfigSetting<Integer> spawnCount = new ConfigSetting<>(4, "worlds.spawners");
   public static final ConfigSetting<Double> spawnRange = new ConfigSetting<>(4.0, "worlds.spawners", "Spawn range for entities around the spawner");
   public static final ConfigSetting<Double> playerSpawnRange = new ConfigSetting<>(16.0, "worlds.spawners", "Radius for players around to spawn");
   public static final ConfigSetting<Integer> maximumNearbyEntities = new ConfigSetting<>(
      0, "worlds.spawners", "Amount of entities around the spawner to prevent spawning. 0 - to disable requirement"
   );
   public static final ConfigSetting<Boolean> lightLevelCheck = new ConfigSetting<>(true, "worlds.spawners");
   public static final ConfigSetting<Boolean> playerBorderDamage = new ConfigSetting<>(false, "worlds.players");
   public static final ConfigSetting<Boolean> playerMovedTooQuicklyCheck = new ConfigSetting<>(
      true, "worlds.players", "Toggle spigots moved too quickly check."
   );
   public static final ConfigSetting<Boolean> checkPlayerSleep = new ConfigSetting<>(true, "worlds.players");
   public static final ConfigSetting<Boolean> playerIdleTimer = new ConfigSetting<>(false, "worlds.players");
   public static final ConfigSetting<Double> playerPhaseLeniency = new ConfigSetting<>(
      1.0E-7,
      "worlds.players",
      "Defines the length a player can go inside a block, if you experience issues with server setting back due to phase, we recommend setting it to 0.0625"
   );
   public static final ConfigSetting<Boolean> playerPhaseChestsAndAnvils = new ConfigSetting<>(
      false,
      "worlds.players",
      "Toggles chests & anvil bounding boxes, it is only recommended enabling this if you want to have protection against people glitching through chests & anvils"
   );
   public static final ConfigSetting<Boolean> dynamicParalellisedTracking = new ConfigSetting<>(
      true, "worlds.players", "Async player tracking. Server will decide when it should be used"
   );
   public static final ConfigSetting<Integer> playersNeededParalellisedTracking = new ConfigSetting<>(
      100, "worlds.players", "How many players should be tracked for us to run paralellised"
   );
   public static final ConfigSetting<Boolean> hideEntitiesThrownHidden = new ConfigSetting<>(
      false, "worlds.players", "Toggle hiding entities thrown by hidden players"
   );
   public static final ConfigSetting<Boolean> protRandomness = new ConfigSetting<>(
      true, "worlds.players", "Toggle protection randomness (HCF server toggle off and check below)"
   );
   public static final ConfigSetting<Double> protectionModifier = new ConfigSetting<>(
      25.0,
      "worlds.players",
      "Protection damage modifier. If prot randomness is disabled make sure to change this to 20-23. Lower for more damage and higher for less damage"
   );
   public static final ConfigSetting<Double> armorDamageDivision = new ConfigSetting<>(4.0, "worlds.players", "Armor damage modifier (HCF should be 12)");
   public static final ConfigSetting<Boolean> optimizePlayerPrinter = new ConfigSetting<>(false, "worlds.players", "Remove block place delay");
   public static final ConfigSetting<Boolean> autoRespawnPlayer = new ConfigSetting<>(false, "worlds.players");
   public static final ConfigSetting<List<String>> potionLaunchOnClick = new ConfigSetting<>(Arrays.asList("HOPPER", "CHEST", "ENDER_CHEST"), "worlds.players");
   public static final ConfigSetting<Boolean> ipConsoleLog = new ConfigSetting<>(false, "worlds.players.networking");
   public static final ConfigSetting<Boolean> writeAddPlayerPacketsLazily = new ConfigSetting<>(
      false, "worlds.players.networking", "Write add player packets lazily instead of using our queue implementation."
   );
   public static final ConfigSetting<Boolean> networkBlockingJoin = new ConfigSetting<>(
      true,
      "worlds.players.networking",
      "PaperSpigot patch for preventing blocking while ticking. Toggle off if you have issues with your anticheat on join.",
      false
   );
   public static final ConfigSetting<Boolean> tickPlayerLatency = new ConfigSetting<>(true, "worlds.players.networking", "Tick player latency on tablist");
   public static final ConfigSetting<Integer> parentNettyThreads = new ConfigSetting<>(
      1, "worlds.players.networking", "1 Is sufficient as it is just responsible for accepting connections and offloading to child group", false
   );
   public static final ConfigSetting<Integer> childNettyThreads = new ConfigSetting<>(
      0, "worlds.players.networking", "Default is 4, 0 is recommended which uses all processors available but will increase cpu usage.", false
   );
   public static final ConfigSetting<Integer> pingSampleIntervalTicks = new ConfigSetting<>(
      20, "worlds.players.networking", "Keep-alive interval used for ping and jitter tracking, in ticks."
   );
   public static final ConfigSetting<Boolean> flushConsolidation = new ConfigSetting<>(
      true, "worlds.players.networking", "Consolidate non-critical play-state Netty flushes while keeping combat packets instant."
   );
   public static final ConfigSetting<Integer> maxPacketsPerDrain = new ConfigSetting<>(
      4096, "worlds.players.networking", "Maximum queued packets handled in one tick-loop drain."
   );
   public static final ConfigSetting<Boolean> combatLagCompensation = new ConfigSetting<>(
      true, "worlds.players.combat", "Validate player attacks against the victim's historical hitbox."
   );
   public static final ConfigSetting<Boolean> combatDebug = new ConfigSetting<>(
      false, "worlds.players.combat", "Log every accepted and rejected melee attack for tuning."
   );
   public static final ConfigSetting<Double> combatSurvivalReach = new ConfigSetting<>(
      3.0, "worlds.players.combat", "Base survival attack reach in blocks."
   );
   public static final ConfigSetting<Double> combatCreativeReach = new ConfigSetting<>(
      5.0, "worlds.players.combat", "Base creative attack reach in blocks."
   );
   public static final ConfigSetting<Double> combatReachTolerance = new ConfigSetting<>(
      0.15, "worlds.players.combat", "Extra reach allowed for 1.8 hitbox and interpolation differences."
   );
   public static final ConfigSetting<Integer> combatMaxRewindMillis = new ConfigSetting<>(
      300, "worlds.players.combat", "Maximum victim position rewind in milliseconds."
   );
   public static final ConfigSetting<Double> combatRewindMultiplier = new ConfigSetting<>(
      1.0, "worlds.players.combat", "Multiplier applied to the attacker's measured ping when rewinding."
   );
   public static final ConfigSetting<Boolean> combatLineOfSight = new ConfigSetting<>(
      true, "worlds.players.combat", "Require a clear block line of sight to the historical hitbox."
   );
   public static final ConfigSetting<Double> combatLineOfSightTolerance = new ConfigSetting<>(
      0.35, "worlds.players.combat", "Block ray tolerance used for line-of-sight checks."
   );
   public static final ConfigSetting<Double> combatMinAimDot = new ConfigSetting<>(
      0.0, "worlds.players.combat", "Minimum look-direction dot product for an attack. 0 disables the check."
   );
   public static final ConfigSetting<Integer> combatAttackRateLimitMs = new ConfigSetting<>(
      25, "worlds.players.combat", "Minimum time between accepted attack packets from one player."
   );
   public static final ConfigSetting<Boolean> timestampHitDelay = new ConfigSetting<>(
      true, "worlds.players.combat", "Use real-time hit timestamps instead of server ticks for player hit delay."
   );
   public static final ConfigSetting<String> knockbackSyncMode = new ConfigSetting<>(
      "CLIENT", "worlds.players.combat", "CLIENT keeps existing knockback behavior; SERVER also applies velocity to server motion."
   );
   public static final ConfigSetting<Integer> playerTrackingInterval = new ConfigSetting<>(
      1, "worlds.players.combat", "Ticks between player position updates. 1 is recommended for competitive PvP."
   );
   public static final ConfigSetting<String> globalActiveKnockback = new ConfigSetting<>(
      "Default", "worlds.players.knockback", "Which profile should we use as the default", false
   );
   public static final ConfigSetting<Boolean> overrideDefaultKnockback = new ConfigSetting<>(
      true, "worlds.players.knockback", "Should we use our custom knockback"
   );
   public static final ConfigSetting<Boolean> overrideRodKnockback = new ConfigSetting<>(true, "worlds.players.knockback", "Should we use our custom knockback");
   public static final ConfigSetting<Boolean> overrideArrowKnockback = new ConfigSetting<>(
      true, "worlds.players.knockback", "Should we use our custom knockback"
   );
   public static final ConfigSetting<Boolean> patchArrowBounceBug = new ConfigSetting<>(
      true, "worlds.players.patches", "Toggles the arrow hit delay in knockback profiles."
   );
   public static final ConfigSetting<Boolean> patchDoubleHitBug = new ConfigSetting<>(false, "worlds.players.patches", "Toggles the double hit bug fix");
   public static final ConfigSetting<Boolean> patchDropEatBug = new ConfigSetting<>(true, "worlds.players.patches");
   public static final ConfigSetting<Boolean> patchGhostBuckets = new ConfigSetting<>(
      true, "worlds.players.patches", "This makes sure the server uses the correct location for placing liquids by waiting for the client tick packet"
   );
   public static final ConfigSetting<Boolean> patchProjectileMisdirection = new ConfigSetting<>(
      true, "worlds.players.patches", "This makes the server use the correct yaw & pitch for projectiles"
   );
   public static final ConfigSetting<Boolean> patchHCFRegenBug = new ConfigSetting<>(
      true, "worlds.players.patches", "This makes regeneration weaker as players in teamfights are often invincible with regular regen."
   );
   public static final ConfigSetting<Boolean> obfuscateEntityEquipment = new ConfigSetting<>(
      true, "worlds.players.patches", "Obfuscates equipment to deny a few cheats"
   );
   public static final ConfigSetting<Boolean> fillerEnabled = new ConfigSetting<>(true, "worlds.players.menus", false);
   public static final ConfigSetting<String> fillerType = new ConfigSetting<>("STAINED_GLASS_PANE", "worlds.players.menus", false);
   public static final ConfigSetting<Integer> fillerData = new ConfigSetting<>(7, "worlds.players.menus", false);
   public static final ConfigSetting<String> pageType = new ConfigSetting<>("CARPET", "worlds.players.menus", false);
   public static final ConfigSetting<Integer> pageData = new ConfigSetting<>(2, "worlds.players.menus", false);
   public static final ConfigSetting<String> pageBackDisplay = new ConfigSetting<>("&dPrevious Page", "worlds.players.menus", false);
   public static final ConfigSetting<List<String>> pageBackLore = new ConfigSetting<>(
      Collections.singletonList("&e(Current Page: &2%page%/%max-pages%&e)"), "worlds.players.menus", false
   );
   public static final ConfigSetting<String> pageForwardDisplay = new ConfigSetting<>("&dNext Page", "worlds.players.menus", false);
   public static final ConfigSetting<List<String>> pageForwardLore = new ConfigSetting<>(
      Collections.singletonList("&e(Current Page: &2%page%/%max-pages%&e)"), "worlds.players.menus", false
   );
   public static final ConfigSetting<String> backButtonType = new ConfigSetting<>("BED", "worlds.players.menus", false);
   public static final ConfigSetting<Integer> backButtonData = new ConfigSetting<>(0, "worlds.players.menus", false);
   public static final ConfigSetting<String> backButtonDisplay = new ConfigSetting<>("&cBack", "worlds.players.menus", false);
   public static final ConfigSetting<Boolean> mobAI = new ConfigSetting<>(true, "worlds.mobs");
   public static final ConfigSetting<Boolean> naturalMobSpawn = new ConfigSetting<>(true, "worlds.mobs");
   public static final ConfigSetting<Boolean> ironGolemFallDamage = new ConfigSetting<>(true, "worlds.mobs");
   public static final ConfigSetting<Boolean> snowGolemFallDamage = new ConfigSetting<>(true, "worlds.mobs");
   public static final ConfigSetting<Boolean> babyZombieSpawning = new ConfigSetting<>(true, "worlds.mobs");
   public static final ConfigSetting<Boolean> zombieOnChickenSpawning = new ConfigSetting<>(true, "worlds.mobs");
   public static final ConfigSetting<Boolean> villagerZombieSpawning = new ConfigSetting<>(true, "worlds.mobs");
   public static final ConfigSetting<Boolean> skeletonOnSpiderSpawning = new ConfigSetting<>(true, "worlds.mobs");
   public static final ConfigSetting<Boolean> entityCollisions = new ConfigSetting<>(true, "worlds.mobs", "Toggle this off if you want the best performance");
   public static final ConfigSetting<Integer> entityCollisionsDelay = new ConfigSetting<>(1, "worlds.mobs", "A delay of 4 is recommended");
   public static final ConfigSetting<Boolean> fixFreecamCegging = new ConfigSetting<>(
      false, "factions", "Only enable if it is an issue! May cause performance issues"
   );
   public static final ConfigSetting<Boolean> sandMerging = new ConfigSetting<>(false, "factions");
   public static final ConfigSetting<Boolean> tntMerging = new ConfigSetting<>(false, "factions");
   public static final ConfigSetting<Boolean> hidePistonAnimations = new ConfigSetting<>(false, "factions");
   public static final ConfigSetting<Boolean> hideTntEntity = new ConfigSetting<>(false, "factions");
   public static final ConfigSetting<Boolean> hideSandEntity = new ConfigSetting<>(false, "factions");
   public static final ConfigSetting<Boolean> fixSandChunkUnloading = new ConfigSetting<>(true, "factions");
   public static final ConfigSetting<Boolean> optimizeTNTMovement = new ConfigSetting<>(false, "factions");
   public static final ConfigSetting<Boolean> optimizeSandMovement = new ConfigSetting<>(false, "factions");
   public static final ConfigSetting<Boolean> fixEastWestCannon = new ConfigSetting<>(true, "factions");
   public static final ConfigSetting<Boolean> explosionDestroyer = new ConfigSetting<>(false, "factions.durability");
   public static final ConfigSetting<String> durabilityItemChecker = new ConfigSetting<>("potato", "factions.durability", false);
   public static final ConfigSetting<Boolean> damageOnlyByTNT = new ConfigSetting<>(false, "factions.durability");
   public static final ConfigSetting<List<String>> blockDurabilities = new ConfigSetting<>(
      Arrays.asList("obsidian:3", "bedrock:5", "end_stone:5"), "factions.durability", false
   );
   public static final ConfigSetting<List<String>> blockResistances = new ConfigSetting<>(
      Arrays.asList("obsidian:35.0", "bedrock:60.0", "end_stone:45.0", "anvil:2000.0"), "factions.durability", false
   );

   public BlazeConfig(Blaze instance) {
      super(instance, "blaze", CC.getConfigHeader("Blaze configuration", "You will find all the main mechanics here"));
   }
}
