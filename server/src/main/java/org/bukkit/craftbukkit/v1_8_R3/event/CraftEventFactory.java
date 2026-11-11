package org.bukkit.craftbukkit.v1_8_R3.event;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.configs.PearlConfig;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.BoundingBox;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.ChatModifier;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityDamageSource;
import net.minecraft.server.v1_8_R3.EntityDamageSourceIndirect;
import net.minecraft.server.v1_8_R3.EntityEnderCrystal;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityPotion;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.Explosion;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.InventoryCrafting;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.PacketPlayInCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import net.minecraft.server.v1_8_R3.Slot;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftStatistic;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaBook;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftDamageSource;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.Stairs;
import org.bukkit.util.Vector;

public class CraftEventFactory {
   public static final DamageSource MELTING = CraftDamageSource.copyOf(DamageSource.BURN);
   public static final DamageSource POISON = CraftDamageSource.copyOf(DamageSource.MAGIC);
   private static final Function<? super Double, Double> ZERO = Functions.constant(-0.0);
   public static Block blockDamage;
   public static Entity entityDamage;

   private static boolean canBuild(CraftWorld world, Player player, int x, int z) {
      WorldServer worldServer = world.getHandle();
      int spawnSize = org.bukkit.Bukkit.getServer().getSpawnRadius();
      if (world.getHandle().dimension != 0) {
         return true;
      } else if (spawnSize <= 0) {
         return true;
      } else if (((CraftServer)org.bukkit.Bukkit.getServer()).getHandle().getOPs().isEmpty()) {
         return true;
      } else if (player.isOp()) {
         return true;
      } else {
         BlockPosition chunkcoordinates = worldServer.getSpawn();
         int distanceFromSpawn = Math.max(Math.abs(x - chunkcoordinates.getX()), Math.abs(z - chunkcoordinates.getY()));
         return distanceFromSpawn > spawnSize;
      }
   }

   public static <T extends Event> T callEvent(T event) {
      org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockMultiPlaceEvent callBlockMultiPlaceEvent(
      net.minecraft.server.v1_8_R3.World world, EntityHuman who, List<BlockState> blockStates, int clickedX, int clickedY, int clickedZ
   ) {
      CraftWorld craftWorld = world.getWorld();
      CraftServer craftServer = world.getServer();
      Player player = who == null ? null : (Player)who.getBukkitEntity();
      Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
      boolean canBuild = true;

      for (BlockState blockState : blockStates) {
         if (!canBuild(craftWorld, player, blockState.getX(), blockState.getZ())) {
            canBuild = false;
            break;
         }
      }

      BlockMultiPlaceEvent event = new BlockMultiPlaceEvent(blockStates, blockClicked, player.getItemInHand(), player, canBuild);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static BlockPlaceEvent callBlockPlaceEvent(
      net.minecraft.server.v1_8_R3.World world, EntityHuman who, BlockState replacedBlockState, int clickedX, int clickedY, int clickedZ
   ) {
      CraftWorld craftWorld = world.getWorld();
      CraftServer craftServer = world.getServer();
      Player player = who == null ? null : (Player)who.getBukkitEntity();
      Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
      Block placedBlock = replacedBlockState.getBlock();
      boolean canBuild = canBuild(craftWorld, player, placedBlock.getX(), placedBlock.getZ());
      BlockPlaceEvent event = new BlockPlaceEvent(placedBlock, replacedBlockState, blockClicked, player.getItemInHand(), player, canBuild);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static SpawnerSpawnEvent callSpawnerSpawnEvent(Entity spawnee, int spawnerX, int spawnerY, int spawnerZ) {
      CraftEntity entity = spawnee.getBukkitEntity();
      BlockState state = entity.getWorld().getBlockAt(spawnerX, spawnerY, spawnerZ).getState();
      if (!(state instanceof CreatureSpawner)) {
         state = null;
      }

      SpawnerSpawnEvent event = new SpawnerSpawnEvent(entity, (CreatureSpawner)state);
      entity.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerBucketEmptyEvent callPlayerBucketEmptyEvent(
      EntityHuman who, int clickedX, int clickedY, int clickedZ, EnumDirection clickedFace, net.minecraft.server.v1_8_R3.ItemStack itemInHand
   ) {
      return (PlayerBucketEmptyEvent)getPlayerBucketEvent(false, who, clickedX, clickedY, clickedZ, clickedFace, itemInHand, Items.BUCKET);
   }

   public static PlayerBucketFillEvent callPlayerBucketFillEvent(
      EntityHuman who, int clickedX, int clickedY, int clickedZ, EnumDirection clickedFace, net.minecraft.server.v1_8_R3.ItemStack itemInHand, Item bucket
   ) {
      return (PlayerBucketFillEvent)getPlayerBucketEvent(true, who, clickedX, clickedY, clickedZ, clickedFace, itemInHand, bucket);
   }

   private static PlayerEvent getPlayerBucketEvent(
      boolean isFilling,
      EntityHuman who,
      int clickedX,
      int clickedY,
      int clickedZ,
      EnumDirection clickedFace,
      net.minecraft.server.v1_8_R3.ItemStack itemstack,
      Item item
   ) {
      Player player = who == null ? null : (Player)who.getBukkitEntity();
      CraftItemStack itemInHand = CraftItemStack.asNewCraftStack(item);
      org.bukkit.Material bucket = CraftMagicNumbers.getMaterial(itemstack.getItem());
      CraftWorld craftWorld = (CraftWorld)player.getWorld();
      CraftServer craftServer = (CraftServer)player.getServer();
      Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
      BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);
      PlayerEvent event = null;
      if (isFilling) {
         event = new PlayerBucketFillEvent(player, blockClicked, blockFace, bucket, itemInHand);
         ((PlayerBucketFillEvent)event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
      } else {
         event = new PlayerBucketEmptyEvent(player, blockClicked, blockFace, bucket, itemInHand);
         ((PlayerBucketEmptyEvent)event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
      }

      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerInteractEvent callPlayerInteractEvent(EntityHuman who, Action action, net.minecraft.server.v1_8_R3.ItemStack itemstack) {
      if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) {
         throw new IllegalArgumentException(String.format("%s performing %s with %s", who, action, itemstack));
      } else {
         return callPlayerInteractEvent(who, action, new BlockPosition(0, 256, 0), EnumDirection.SOUTH, itemstack);
      }
   }

   public static PlayerInteractEvent callPlayerInteractEvent(
      EntityHuman who, Action action, BlockPosition position, EnumDirection direction, net.minecraft.server.v1_8_R3.ItemStack itemstack
   ) {
      return callPlayerInteractEvent(who, action, position, direction, itemstack, false);
   }

   public static PlayerInteractEvent callPlayerInteractEvent(
      EntityHuman who, Action action, BlockPosition position, EnumDirection direction, net.minecraft.server.v1_8_R3.ItemStack itemstack, boolean cancelledBlock
   ) {
      return callPlayerInteractEvent(
         who,
         action,
         who.getBukkitEntity().getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()),
         position,
         direction,
         itemstack,
         cancelledBlock
      );
   }

   public static PlayerInteractEvent callPlayerInteractEvent(
      EntityHuman who,
      Action action,
      Block blockClicked,
      BlockPosition position,
      EnumDirection direction,
      net.minecraft.server.v1_8_R3.ItemStack itemstack,
      boolean cancelledBlock
   ) {
      Player player = who == null ? null : (Player)who.getBukkitEntity();
      CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);
      BlockFace blockFace = CraftBlock.notchToBlockFace(direction);
      if (position.getY() > 255) {
         blockClicked = null;
         switch (action) {
            case LEFT_CLICK_BLOCK:
               action = Action.LEFT_CLICK_AIR;
               break;
            case RIGHT_CLICK_BLOCK:
               action = Action.RIGHT_CLICK_AIR;
         }
      }

      if (itemInHand.getType() == org.bukkit.Material.AIR || itemInHand.getAmount() == 0) {
         itemInHand = null;
      }

      PlayerInteractEvent event = new PlayerInteractEvent(player, action, itemInHand, blockClicked, blockFace);
      if (!event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         PearlConfig.handlePearl(event);
      }

      if (cancelledBlock) {
         event.setUseInteractedBlock(Event.Result.DENY);
      }

      org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
      if ((Boolean)BlazeConfig.fixFreecamCegging.getValue()
         && event.getAction() == Action.RIGHT_CLICK_BLOCK
         && !event.isCancelled()
         && (event.getItem() != null && event.getItem().getType() == org.bukkit.Material.MONSTER_EGG || blockClicked.getState() instanceof InventoryHolder)) {
         Vector origin = player.getEyeLocation().toVector();
         Vector eyeDirection = player.getEyeLocation().getDirection();
         Block rayTraced = null;

         for (double d = 0.0; d <= 5.0; d += 0.01) {
            Block currentBlock;
            org.bukkit.Location currentLoc;
            org.bukkit.Material currentMat = (currentBlock = (currentLoc = origin.clone().add(eyeDirection.clone().multiply(d)).toLocation(player.getWorld()))
                  .getBlock())
               .getType();
            if (isSolidBlock(currentBlock, currentMat, currentLoc)) {
               rayTraced = currentBlock;
               break;
            }
         }

         if (!blockClicked.equals(rayTraced)) {
            event.setCancelled(true);
         }
      }

      return event;
   }

   private static boolean isSolidBlock(Block block, org.bukkit.Material material, org.bukkit.Location location) {
      if (material != org.bukkit.Material.AIR && !block.isLiquid() && !(block.getState() instanceof Sign) && !(block.getState().getData() instanceof Stairs)) {
         BoundingBox aabb = new BoundingBox(block);
         return !(location.getX() < aabb.getMin().getX())
            && !(location.getX() > aabb.getMax().getX())
            && !(location.getY() < aabb.getMin().getY())
            && !(location.getY() > aabb.getMax().getY())
            && !(location.getZ() < aabb.getMin().getZ())
            && !(location.getZ() > aabb.getMax().getZ());
      } else {
         return false;
      }
   }

   public static EntityShootBowEvent callEntityShootBowEvent(
      EntityLiving who, net.minecraft.server.v1_8_R3.ItemStack itemstack, EntityArrow entityArrow, float force
   ) {
      LivingEntity shooter = (LivingEntity)who.getBukkitEntity();
      CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);
      Arrow arrow = (Arrow)entityArrow.getBukkitEntity();
      if (itemInHand.getType() == org.bukkit.Material.AIR || itemInHand.getAmount() == 0) {
         itemInHand = null;
      }

      EntityShootBowEvent event = new EntityShootBowEvent(shooter, itemInHand, arrow, force);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static BlockDamageEvent callBlockDamageEvent(EntityHuman who, Block block, net.minecraft.server.v1_8_R3.ItemStack itemstack, boolean instaBreak) {
      Player player = who == null ? null : (Player)who.getBukkitEntity();
      BlockDamageEvent event = new BlockDamageEvent(player, block, CraftItemStack.asCraftMirror(itemstack), instaBreak);
      player.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static CreatureSpawnEvent callCreatureSpawnEvent(EntityLiving entityliving, CreatureSpawnEvent.SpawnReason spawnReason) {
      LivingEntity entity = (LivingEntity)entityliving.getBukkitEntity();
      CraftServer craftServer = (CraftServer)entity.getServer();
      CreatureSpawnEvent event = new CreatureSpawnEvent(entity, spawnReason);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static EntityTameEvent callEntityTameEvent(EntityInsentient entity, EntityHuman tamer) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      AnimalTamer bukkitTamer = tamer != null ? tamer.getBukkitEntity() : null;
      CraftServer craftServer = (CraftServer)bukkitEntity.getServer();
      entity.persistent = true;
      EntityTameEvent event = new EntityTameEvent((LivingEntity)bukkitEntity, bukkitTamer);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static ItemSpawnEvent callItemSpawnEvent(EntityItem entityitem) {
      org.bukkit.entity.Item entity = (org.bukkit.entity.Item)entityitem.getBukkitEntity();
      CraftServer craftServer = (CraftServer)entity.getServer();
      ItemSpawnEvent event = new ItemSpawnEvent(entity, entity.getLocation());
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static ItemDespawnEvent callItemDespawnEvent(EntityItem entityitem) {
      org.bukkit.entity.Item entity = (org.bukkit.entity.Item)entityitem.getBukkitEntity();
      ItemDespawnEvent event = new ItemDespawnEvent(entity, entity.getLocation());
      entity.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static ItemMergeEvent callItemMergeEvent(EntityItem merging, EntityItem mergingWith) {
      org.bukkit.entity.Item entityMerging = (org.bukkit.entity.Item)merging.getBukkitEntity();
      org.bukkit.entity.Item entityMergingWith = (org.bukkit.entity.Item)mergingWith.getBukkitEntity();
      ItemMergeEvent event = new ItemMergeEvent(entityMerging, entityMergingWith);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static PotionSplashEvent callPotionSplashEvent(EntityPotion potion, Map<LivingEntity, Double> affectedEntities) {
      ThrownPotion thrownPotion = (ThrownPotion)potion.getBukkitEntity();
      PotionSplashEvent event = new PotionSplashEvent(thrownPotion, affectedEntities);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static BlockFadeEvent callBlockFadeEvent(Block block, net.minecraft.server.v1_8_R3.Block type) {
      BlockState state = block.getState();
      state.setTypeId(net.minecraft.server.v1_8_R3.Block.getId(type));
      BlockFadeEvent event = new BlockFadeEvent(block, state);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static void handleBlockSpreadEvent(Block block, Block source, net.minecraft.server.v1_8_R3.Block type, int data) {
      BlockState state = block.getState();
      state.setTypeId(net.minecraft.server.v1_8_R3.Block.getId(type));
      state.setRawData((byte)data);
      BlockSpreadEvent event = new BlockSpreadEvent(block, source, state);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         state.update(true);
      }
   }

   public static EntityDeathEvent callEntityDeathEvent(EntityLiving victim) {
      return callEntityDeathEvent(victim, new ArrayList<>(0));
   }

   public static EntityDeathEvent callEntityDeathEvent(EntityLiving victim, List<ItemStack> drops) {
      CraftLivingEntity entity = (CraftLivingEntity)victim.getBukkitEntity();
      EntityDeathEvent event = new EntityDeathEvent(entity, drops, victim.getExpReward());
      CraftWorld world = (CraftWorld)entity.getWorld();
      org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
      victim.expToDrop = event.getDroppedExp();

      for (ItemStack stack : event.getDrops()) {
         if (stack != null && stack.getType() != org.bukkit.Material.AIR && stack.getAmount() != 0) {
            world.dropItemNaturally(entity.getLocation(), stack, victim);
         }
      }

      return event;
   }

   public static PlayerDeathEvent callPlayerDeathEvent(EntityPlayer victim, List<ItemStack> drops, String deathMessage, boolean keepInventory) {
      CraftPlayer entity = victim.getBukkitEntity();
      PlayerDeathEvent event = new PlayerDeathEvent(entity, drops, victim.getExpReward(), 0, deathMessage);
      event.setKeepInventory(keepInventory);
      CraftWorld world = (CraftWorld)entity.getWorld();
      org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
      victim.keepLevel = event.getKeepLevel();
      victim.newLevel = event.getNewLevel();
      victim.newTotalExp = event.getNewTotalExp();
      victim.expToDrop = event.getDroppedExp();
      victim.newExp = event.getNewExp();
      if (event.getKeepInventory()) {
         return event;
      } else {
         for (ItemStack stack : event.getDrops()) {
            if (stack != null && stack.getType() != org.bukkit.Material.AIR) {
               world.dropItemNaturally(entity.getLocation(), stack, victim);
            }
         }

         return event;
      }
   }

   public static ServerListPingEvent callServerListPingEvent(org.bukkit.Server craftServer, InetAddress address, String motd, int numPlayers, int maxPlayers) {
      ServerListPingEvent event = new ServerListPingEvent(address, motd, numPlayers, maxPlayers);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   private static EntityDamageEvent handleEntityDamageEvent(
      Entity entity,
      DamageSource source,
      Map<EntityDamageEvent.DamageModifier, Double> modifiers,
      Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions
   ) {
      if (source.isExplosion()) {
         Entity damager = entityDamage;
         entityDamage = null;
         EntityDamageEvent event;
         if (damager == null) {
            event = new EntityDamageByBlockEvent(null, entity.getBukkitEntity(), EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, modifiers, modifierFunctions);
         } else if (entity instanceof EntityEnderDragon && ((EntityEnderDragon)entity).target == damager) {
            event = new EntityDamageEvent(entity.getBukkitEntity(), EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, modifiers, modifierFunctions);
         } else {
            EntityDamageEvent.DamageCause damageCause;
            if (damager instanceof TNTPrimed) {
               damageCause = EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;
            } else {
               damageCause = EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;
            }

            event = new EntityDamageByEntityEvent(damager.getBukkitEntity(), entity.getBukkitEntity(), damageCause, modifiers, modifierFunctions);
         }

         callEvent(event);
         if (!event.isCancelled()) {
            event.getEntity().setLastDamageCause(event);
         }

         return event;
      } else if (source instanceof EntityDamageSource) {
         Entity damagerx = source.getEntity();
         EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.ENTITY_ATTACK;
         if (source instanceof EntityDamageSourceIndirect) {
            damagerx = ((EntityDamageSourceIndirect)source).getProximateDamageSource();
            if (damagerx.getBukkitEntity() instanceof ThrownPotion) {
               cause = EntityDamageEvent.DamageCause.MAGIC;
            } else if (damagerx.getBukkitEntity() instanceof Projectile) {
               cause = EntityDamageEvent.DamageCause.PROJECTILE;
            }
         } else if ("thorns".equals(source.translationIndex)) {
            cause = EntityDamageEvent.DamageCause.THORNS;
         }

         return callEntityDamageEvent(damagerx, entity, cause, modifiers, modifierFunctions);
      } else if (source == DamageSource.OUT_OF_WORLD) {
         EntityDamageEvent eventx = callEvent(
            new EntityDamageByBlockEvent(null, entity.getBukkitEntity(), EntityDamageEvent.DamageCause.VOID, modifiers, modifierFunctions)
         );
         if (!eventx.isCancelled()) {
            eventx.getEntity().setLastDamageCause(eventx);
         }

         return eventx;
      } else if (source == DamageSource.LAVA) {
         EntityDamageEvent eventx = callEvent(
            new EntityDamageByBlockEvent(null, entity.getBukkitEntity(), EntityDamageEvent.DamageCause.LAVA, modifiers, modifierFunctions)
         );
         if (!eventx.isCancelled()) {
            eventx.getEntity().setLastDamageCause(eventx);
         }

         return eventx;
      } else if (blockDamage != null) {
         Block damagerx = blockDamage;
         blockDamage = null;
         if (source == DamageSource.CACTUS) {
            EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.CONTACT;
            EntityDamageEvent eventx = callEvent(new EntityDamageByBlockEvent(damagerx, entity.getBukkitEntity(), cause, modifiers, modifierFunctions));
            if (!eventx.isCancelled()) {
               eventx.getEntity().setLastDamageCause(eventx);
            }

            return eventx;
         } else {
            throw new RuntimeException(String.format("Unhandled damage of %s by %s from %s", entity, damagerx, source.translationIndex));
         }
      } else if (entityDamage == null) {
         EntityDamageEvent.DamageCause cause = null;
         if (source == DamageSource.FIRE) {
            cause = EntityDamageEvent.DamageCause.FIRE;
         } else if (source == DamageSource.STARVE) {
            cause = EntityDamageEvent.DamageCause.STARVATION;
         } else if (source == DamageSource.WITHER) {
            cause = EntityDamageEvent.DamageCause.WITHER;
         } else if (source == DamageSource.STUCK) {
            cause = EntityDamageEvent.DamageCause.SUFFOCATION;
         } else if (source == DamageSource.DROWN) {
            cause = EntityDamageEvent.DamageCause.DROWNING;
         } else if (source == DamageSource.BURN) {
            cause = EntityDamageEvent.DamageCause.FIRE_TICK;
         } else if (source == MELTING) {
            cause = EntityDamageEvent.DamageCause.MELTING;
         } else if (source == POISON) {
            cause = EntityDamageEvent.DamageCause.POISON;
         } else if (source == DamageSource.MAGIC) {
            cause = EntityDamageEvent.DamageCause.MAGIC;
         } else if (source == DamageSource.FALL) {
            cause = EntityDamageEvent.DamageCause.FALL;
         } else if (source == DamageSource.GENERIC) {
            return new EntityDamageEvent(entity.getBukkitEntity(), null, modifiers, modifierFunctions);
         }

         if (cause != null) {
            return callEntityDamageEvent(null, entity, cause, modifiers, modifierFunctions);
         } else {
            throw new RuntimeException(String.format("Unhandled damage of %s from %s", entity, source.translationIndex));
         }
      } else {
         CraftEntity damagerx = entityDamage.getBukkitEntity();
         entityDamage = null;
         EntityDamageEvent.DamageCause causex;
         if (source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) {
            causex = EntityDamageEvent.DamageCause.FALLING_BLOCK;
         } else if (damagerx instanceof LightningStrike) {
            causex = EntityDamageEvent.DamageCause.LIGHTNING;
         } else {
            if (source != DamageSource.FALL) {
               throw new RuntimeException(String.format("Unhandled damage of %s by %s from %s", entity, damagerx.getHandle(), source.translationIndex));
            }

            causex = EntityDamageEvent.DamageCause.FALL;
         }

         EntityDamageEvent eventx = callEvent(new EntityDamageByEntityEvent(damagerx, entity.getBukkitEntity(), causex, modifiers, modifierFunctions));
         if (!eventx.isCancelled()) {
            eventx.getEntity().setLastDamageCause(eventx);
         }

         return eventx;
      }
   }

   private static EntityDamageEvent callEntityDamageEvent(
      Entity damager,
      Entity damagee,
      EntityDamageEvent.DamageCause cause,
      Map<EntityDamageEvent.DamageModifier, Double> modifiers,
      Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions
   ) {
      EntityDamageEvent event;
      if (damager != null) {
         event = new EntityDamageByEntityEvent(damager.getBukkitEntity(), damagee.getBukkitEntity(), cause, modifiers, modifierFunctions);
      } else {
         event = new EntityDamageEvent(damagee.getBukkitEntity(), cause, modifiers, modifierFunctions);
      }

      callEvent(event);
      if (!event.isCancelled()) {
         event.getEntity().setLastDamageCause(event);
      }

      return event;
   }

   public static EntityDamageEvent handleLivingEntityDamageEvent(
      Entity damagee,
      DamageSource source,
      double rawDamage,
      double hardHatModifier,
      double blockingModifier,
      double armorModifier,
      double resistanceModifier,
      double magicModifier,
      double absorptionModifier,
      Function<Double, Double> hardHat,
      Function<Double, Double> blocking,
      Function<Double, Double> armor,
      Function<Double, Double> resistance,
      Function<Double, Double> magic,
      Function<Double, Double> absorption
   ) {
      Map<EntityDamageEvent.DamageModifier, Double> modifiers = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
      Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
      modifiers.put(EntityDamageEvent.DamageModifier.BASE, rawDamage);
      modifierFunctions.put(EntityDamageEvent.DamageModifier.BASE, ZERO);
      if (source == DamageSource.FALLING_BLOCK || source == DamageSource.ANVIL) {
         modifiers.put(EntityDamageEvent.DamageModifier.HARD_HAT, hardHatModifier);
         modifierFunctions.put(EntityDamageEvent.DamageModifier.HARD_HAT, hardHat);
      }

      if (damagee instanceof EntityHuman) {
         modifiers.put(EntityDamageEvent.DamageModifier.BLOCKING, blockingModifier);
         modifierFunctions.put(EntityDamageEvent.DamageModifier.BLOCKING, blocking);
      }

      modifiers.put(EntityDamageEvent.DamageModifier.ARMOR, armorModifier);
      modifierFunctions.put(EntityDamageEvent.DamageModifier.ARMOR, armor);
      modifiers.put(EntityDamageEvent.DamageModifier.RESISTANCE, resistanceModifier);
      modifierFunctions.put(EntityDamageEvent.DamageModifier.RESISTANCE, resistance);
      modifiers.put(EntityDamageEvent.DamageModifier.MAGIC, magicModifier);
      modifierFunctions.put(EntityDamageEvent.DamageModifier.MAGIC, magic);
      modifiers.put(EntityDamageEvent.DamageModifier.ABSORPTION, absorptionModifier);
      modifierFunctions.put(EntityDamageEvent.DamageModifier.ABSORPTION, absorption);
      return handleEntityDamageEvent(damagee, source, modifiers, modifierFunctions);
   }

   public static boolean handleNonLivingEntityDamageEvent(Entity entity, DamageSource source, double damage) {
      return handleNonLivingEntityDamageEvent(entity, source, damage, true);
   }

   public static boolean handleNonLivingEntityDamageEvent(Entity entity, DamageSource source, double damage, boolean cancelOnZeroDamage) {
      if (entity instanceof EntityEnderCrystal && !(source instanceof EntityDamageSource)) {
         return false;
      } else {
         EnumMap<EntityDamageEvent.DamageModifier, Double> modifiers = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
         EnumMap<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> functions = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
         modifiers.put(EntityDamageEvent.DamageModifier.BASE, damage);
         functions.put(EntityDamageEvent.DamageModifier.BASE, ZERO);
         EntityDamageEvent event = handleEntityDamageEvent(entity, source, modifiers, functions);
         return event.isCancelled() || cancelOnZeroDamage && event.getDamage() == 0.0;
      }
   }

   public static PlayerLevelChangeEvent callPlayerLevelChangeEvent(Player player, int oldLevel, int newLevel) {
      PlayerLevelChangeEvent event = new PlayerLevelChangeEvent(player, oldLevel, newLevel);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerExpChangeEvent callPlayerExpChangeEvent(EntityHuman entity, int expAmount) {
      Player player = (Player)entity.getBukkitEntity();
      PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, expAmount);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static void handleBlockGrowEvent(net.minecraft.server.v1_8_R3.World world, int x, int y, int z, net.minecraft.server.v1_8_R3.Block type, int data) {
      Block block = world.getWorld().getBlockAt(x, y, z);
      CraftBlockState state = (CraftBlockState)block.getState();
      state.setTypeId(net.minecraft.server.v1_8_R3.Block.getId(type));
      state.setRawData((byte)data);
      BlockGrowEvent event = new BlockGrowEvent(block, state);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         state.update(true);
      }
   }

   public static FoodLevelChangeEvent callFoodLevelChangeEvent(EntityHuman entity, int level) {
      FoodLevelChangeEvent event = new FoodLevelChangeEvent(entity.getBukkitEntity(), level);
      entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static PigZapEvent callPigZapEvent(Entity pig, Entity lightning, Entity pigzombie) {
      PigZapEvent event = new PigZapEvent((Pig)pig.getBukkitEntity(), (LightningStrike)lightning.getBukkitEntity(), (PigZombie)pigzombie.getBukkitEntity());
      pig.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static HorseJumpEvent callHorseJumpEvent(Entity horse, float power) {
      HorseJumpEvent event = new HorseJumpEvent((Horse)horse.getBukkitEntity(), power);
      horse.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static EntityChangeBlockEvent callEntityChangeBlockEvent(org.bukkit.entity.Entity entity, Block block, org.bukkit.Material material) {
      return callEntityChangeBlockEvent(entity, block, material, 0);
   }

   public static EntityChangeBlockEvent callEntityChangeBlockEvent(Entity entity, Block block, org.bukkit.Material material) {
      return callEntityChangeBlockEvent(entity.getBukkitEntity(), block, material, 0);
   }

   public static EntityChangeBlockEvent callEntityChangeBlockEvent(Entity entity, Block block, org.bukkit.Material material, boolean cancelled) {
      return callEntityChangeBlockEvent(entity.getBukkitEntity(), block, material, 0, cancelled);
   }

   public static EntityChangeBlockEvent callEntityChangeBlockEvent(Entity entity, int x, int y, int z, net.minecraft.server.v1_8_R3.Block type, int data) {
      Block block = entity.world.getWorld().getBlockAt(x, y, z);
      org.bukkit.Material material = CraftMagicNumbers.getMaterial(type);
      return callEntityChangeBlockEvent(entity.getBukkitEntity(), block, material, data);
   }

   public static EntityChangeBlockEvent callEntityChangeBlockEvent(Entity entity, BlockPosition p, net.minecraft.server.v1_8_R3.Block type, int data) {
      return callEntityChangeBlockEvent(entity, p.getX(), p.getY(), p.getZ(), type, data);
   }

   public static EntityChangeBlockEvent callEntityChangeBlockEvent(org.bukkit.entity.Entity entity, Block block, org.bukkit.Material material, int data) {
      return callEntityChangeBlockEvent(entity, block, material, data, false);
   }

   public static EntityChangeBlockEvent callEntityChangeBlockEvent(
      org.bukkit.entity.Entity entity, Block block, org.bukkit.Material material, int data, boolean cancelled
   ) {
      EntityChangeBlockEvent event = new EntityChangeBlockEvent(entity, block, material, (byte)data);
      event.setCancelled(cancelled);
      entity.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static CreeperPowerEvent callCreeperPowerEvent(Entity creeper, Entity lightning, CreeperPowerEvent.PowerCause cause) {
      CreeperPowerEvent event = new CreeperPowerEvent((Creeper)creeper.getBukkitEntity(), (LightningStrike)lightning.getBukkitEntity(), cause);
      creeper.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static EntityTargetEvent callEntityTargetEvent(Entity entity, Entity target, EntityTargetEvent.TargetReason reason) {
      EntityTargetEvent event = new EntityTargetEvent(entity.getBukkitEntity(), target == null ? null : target.getBukkitEntity(), reason);
      entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static EntityTargetLivingEntityEvent callEntityTargetLivingEvent(Entity entity, EntityLiving target, EntityTargetEvent.TargetReason reason) {
      EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(entity.getBukkitEntity(), (LivingEntity)target.getBukkitEntity(), reason);
      entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static EntityBreakDoorEvent callEntityBreakDoorEvent(Entity entity, int x, int y, int z) {
      org.bukkit.entity.Entity entity1 = entity.getBukkitEntity();
      Block block = entity1.getWorld().getBlockAt(x, y, z);
      EntityBreakDoorEvent event = new EntityBreakDoorEvent((LivingEntity)entity1, block);
      entity1.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static Container callInventoryOpenEvent(EntityPlayer player, Container container) {
      return callInventoryOpenEvent(player, container, false);
   }

   public static Container callInventoryOpenEvent(EntityPlayer player, Container container, boolean cancelled) {
      if (player.activeContainer != player.defaultContainer) {
         player.playerConnection.a(new PacketPlayInCloseWindow(player.activeContainer.windowId));
      }

      CraftServer server = player.world.getServer();
      CraftPlayer craftPlayer = player.getBukkitEntity();
      player.activeContainer.transferTo(container, craftPlayer);
      InventoryOpenEvent event = new InventoryOpenEvent(container.getBukkitView());
      event.setCancelled(cancelled);
      server.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         container.transferTo(player.activeContainer, craftPlayer);
         return null;
      } else {
         return container;
      }
   }

   public static net.minecraft.server.v1_8_R3.ItemStack callPreCraftEvent(
      InventoryCrafting matrix, net.minecraft.server.v1_8_R3.ItemStack result, InventoryView lastCraftView, boolean isRepair
   ) {
      CraftInventoryCrafting inventory = new CraftInventoryCrafting(matrix, matrix.resultInventory);
      inventory.setResult(CraftItemStack.asCraftMirror(result));
      PrepareItemCraftEvent event = new PrepareItemCraftEvent(inventory, lastCraftView, isRepair);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      ItemStack bitem = event.getInventory().getResult();
      return CraftItemStack.asNMSCopy(bitem);
   }

   public static ProjectileLaunchEvent callProjectileLaunchEvent(Entity entity) {
      Projectile bukkitEntity = (Projectile)entity.getBukkitEntity();
      ProjectileLaunchEvent event = new ProjectileLaunchEvent(bukkitEntity);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static ProjectileHitEvent callProjectileHitEvent(Entity entity) {
      ProjectileHitEvent event = new ProjectileHitEvent((Projectile)entity.getBukkitEntity());
      entity.world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static ExpBottleEvent callExpBottleEvent(Entity entity, int exp) {
      ThrownExpBottle bottle = (ThrownExpBottle)entity.getBukkitEntity();
      ExpBottleEvent event = new ExpBottleEvent(bottle, exp);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static BlockRedstoneEvent callRedstoneChange(net.minecraft.server.v1_8_R3.World world, int x, int y, int z, int oldCurrent, int newCurrent) {
      BlockRedstoneEvent event = new BlockRedstoneEvent(world.getWorld().getBlockAt(x, y, z), oldCurrent, newCurrent);
      world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static NotePlayEvent callNotePlayEvent(net.minecraft.server.v1_8_R3.World world, int x, int y, int z, byte instrument, byte note) {
      NotePlayEvent event = new NotePlayEvent(world.getWorld().getBlockAt(x, y, z), org.bukkit.Instrument.getByType(instrument), new org.bukkit.Note(note));
      world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static void callPlayerItemBreakEvent(EntityHuman human, net.minecraft.server.v1_8_R3.ItemStack brokenItem) {
      CraftItemStack item = CraftItemStack.asCraftMirror(brokenItem);
      PlayerItemBreakEvent event = new PlayerItemBreakEvent((Player)human.getBukkitEntity(), item);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
   }

   public static BlockIgniteEvent callBlockIgniteEvent(net.minecraft.server.v1_8_R3.World world, int x, int y, int z, Explosion explosion) {
      CraftWorld craftWorld = world.getWorld();
      CraftEntity craftEntity = explosion.source == null ? null : explosion.source.getBukkitEntity();
      BlockIgniteEvent event = new BlockIgniteEvent(craftWorld.getBlockAt(x, y, z), BlockIgniteEvent.IgniteCause.EXPLOSION, craftEntity);
      world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockIgniteEvent callBlockIgniteEvent(net.minecraft.server.v1_8_R3.World world, int x, int y, int z, int igniterX, int igniterY, int igniterZ) {
      org.bukkit.World bukkitWorld = world.getWorld();
      Block igniter = bukkitWorld.getBlockAt(igniterX, igniterY, igniterZ);
      BlockIgniteEvent.IgniteCause cause;
      switch (igniter.getType()) {
         case LAVA:
         case STATIONARY_LAVA:
            cause = BlockIgniteEvent.IgniteCause.LAVA;
            break;
         case DISPENSER:
            cause = BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL;
            break;
         case FIRE:
         default:
            cause = BlockIgniteEvent.IgniteCause.SPREAD;
      }

      BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(x, y, z), cause, igniter);
      world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockIgniteEvent callBlockIgniteEvent(net.minecraft.server.v1_8_R3.World world, int x, int y, int z, Entity igniter) {
      org.bukkit.World bukkitWorld = world.getWorld();
      org.bukkit.entity.Entity bukkitIgniter = igniter.getBukkitEntity();
      BlockIgniteEvent.IgniteCause cause;
      switch (bukkitIgniter.getType()) {
         case FIREBALL:
         case SMALL_FIREBALL:
            cause = BlockIgniteEvent.IgniteCause.FIREBALL;
            break;
         case ENDER_CRYSTAL:
            cause = BlockIgniteEvent.IgniteCause.ENDER_CRYSTAL;
            break;
         case LIGHTNING:
            cause = BlockIgniteEvent.IgniteCause.LIGHTNING;
            break;
         default:
            cause = BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL;
      }

      BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(x, y, z), cause, bukkitIgniter);
      world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockIgniteEvent callBlockIgniteEvent(
      net.minecraft.server.v1_8_R3.World world, net.minecraft.server.v1_8_R3.Chunk chunk, int x, int y, int z, Explosion explosion
   ) {
      org.bukkit.World bukkitWorld = world.getWorld();
      org.bukkit.entity.Entity igniter = explosion.source == null ? null : explosion.source.getBukkitEntity();
      BlockIgniteEvent event = new BlockIgniteEvent(chunk.bukkitChunk.getBlock(x, y, z), BlockIgniteEvent.IgniteCause.EXPLOSION, igniter);
      world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockIgniteEvent callBlockIgniteEvent(
      net.minecraft.server.v1_8_R3.World world, int x, int y, int z, BlockIgniteEvent.IgniteCause cause, Entity igniter
   ) {
      BlockIgniteEvent event = new BlockIgniteEvent(world.getWorld().getBlockAt(x, y, z), cause, igniter.getBukkitEntity());
      world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static void handleInventoryCloseEvent(EntityHuman human) {
      InventoryCloseEvent event = new InventoryCloseEvent(human.activeContainer.getBukkitView());
      human.world.getServer().getPluginManager().callEvent(event);
      human.activeContainer.transferTo(human.defaultContainer, human.getBukkitEntity());
      Blaze.getInstance().getMenuManager().handleInventoryClose(event);
   }

   public static void handleEditBookEvent(EntityPlayer player, net.minecraft.server.v1_8_R3.ItemStack newBookItem) {
      int itemInHandIndex = player.inventory.itemInHandIndex;
      PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(
         player.getBukkitEntity(),
         player.inventory.itemInHandIndex,
         (BookMeta)CraftItemStack.getItemMeta(player.inventory.getItemInHand()),
         (BookMeta)CraftItemStack.getItemMeta(newBookItem),
         newBookItem.getItem() == Items.WRITTEN_BOOK
      );
      player.world.getServer().getPluginManager().callEvent(editBookEvent);
      net.minecraft.server.v1_8_R3.ItemStack itemInHand = player.inventory.getItem(itemInHandIndex);
      if (itemInHand != null && itemInHand.getItem() == Items.WRITABLE_BOOK) {
         if (!editBookEvent.isCancelled()) {
            if (editBookEvent.isSigning()) {
               itemInHand.setItem(Items.WRITTEN_BOOK);
            }

            CraftMetaBook meta = (CraftMetaBook)editBookEvent.getNewBookMeta();
            List<IChatBaseComponent> pages = meta.pages;

            for (int i = 0; i < pages.size(); i++) {
               pages.set(i, stripEvents(pages.get(i)));
            }

            CraftItemStack.setItemMeta(itemInHand, meta);
         }

         Slot slot = player.activeContainer.getSlot(player.inventory, itemInHandIndex);
         player.playerConnection.sendPacket(new PacketPlayOutSetSlot(player.activeContainer.windowId, slot.rawSlotIndex, itemInHand));
      }
   }

   private static IChatBaseComponent stripEvents(IChatBaseComponent c) {
      ChatModifier modi = c.getChatModifier();
      if (modi != null) {
         modi.setChatClickable(null);
         modi.setChatHoverable(null);
      }

      c.setChatModifier(modi);
      if (c instanceof ChatMessage) {
         ChatMessage cm = (ChatMessage)c;
         Object[] oo = cm.j();

         for (int i = 0; i < oo.length; i++) {
            Object o = oo[i];
            if (o instanceof IChatBaseComponent) {
               oo[i] = stripEvents((IChatBaseComponent)o);
            }
         }
      }

      List<IChatBaseComponent> ls = c.a();
      if (ls != null) {
         for (int ix = 0; ix < ls.size(); ix++) {
            ls.set(ix, stripEvents(ls.get(ix)));
         }
      }

      return c;
   }

   public static PlayerUnleashEntityEvent callPlayerUnleashEntityEvent(EntityInsentient entity, EntityHuman player) {
      PlayerUnleashEntityEvent event = new PlayerUnleashEntityEvent(entity.getBukkitEntity(), (Player)player.getBukkitEntity());
      entity.world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerLeashEntityEvent callPlayerLeashEntityEvent(EntityInsentient entity, Entity leashHolder, EntityHuman player) {
      PlayerLeashEntityEvent event = new PlayerLeashEntityEvent(entity.getBukkitEntity(), leashHolder.getBukkitEntity(), (Player)player.getBukkitEntity());
      entity.world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static Cancellable handleStatisticsIncrease(
      EntityHuman entityHuman, net.minecraft.server.v1_8_R3.Statistic statistic, int current, int incrementation
   ) {
      Player player = ((EntityPlayer)entityHuman).getBukkitEntity();
      Event event;
      if (statistic instanceof net.minecraft.server.v1_8_R3.Achievement) {
         if (current != 0) {
            return null;
         }

         event = new PlayerAchievementAwardedEvent(player, CraftStatistic.getBukkitAchievement((net.minecraft.server.v1_8_R3.Achievement)statistic));
      } else {
         org.bukkit.Statistic stat = CraftStatistic.getBukkitStatistic(statistic);
         if (stat == null) {
            System.err.println("Unhandled statistic: " + statistic);
            return null;
         }

         switch (stat) {
            case PLAY_ONE_TICK:
            case WALK_ONE_CM:
            case SWIM_ONE_CM:
            case FALL_ONE_CM:
            case CLIMB_ONE_CM:
            case FLY_ONE_CM:
            case DIVE_ONE_CM:
            case MINECART_ONE_CM:
            case BOAT_ONE_CM:
            case PIG_ONE_CM:
            case HORSE_ONE_CM:
            case SPRINT_ONE_CM:
            case CROUCH_ONE_CM:
            case TIME_SINCE_DEATH:
               return null;
            case MINE_BLOCK:
            case USE_ITEM:
            case BREAK_ITEM:
            case CRAFT_ITEM:
            case KILL_ENTITY:
            case ENTITY_KILLED_BY:
            default:
               if (stat.getType() == org.bukkit.Statistic.Type.UNTYPED) {
                  event = new PlayerStatisticIncrementEvent(player, stat, current, current + incrementation);
               } else if (stat.getType() == org.bukkit.Statistic.Type.ENTITY) {
                  EntityType entityType = CraftStatistic.getEntityTypeFromStatistic(statistic);
                  event = new PlayerStatisticIncrementEvent(player, stat, current, current + incrementation, entityType);
               } else {
                  org.bukkit.Material material = CraftStatistic.getMaterialFromStatistic(statistic);
                  event = new PlayerStatisticIncrementEvent(player, stat, current, current + incrementation, material);
               }
         }
      }

      entityHuman.world.getServer().getPluginManager().callEvent(event);
      return (Cancellable)event;
   }

   public static FireworkExplodeEvent callFireworkExplodeEvent(EntityFireworks firework) {
      FireworkExplodeEvent event = new FireworkExplodeEvent((Firework)firework.getBukkitEntity());
      firework.world.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerLocaleChangeEvent callPlayerLocaleChangeEvent(EntityHuman who, String oldLocale, String newLocale) {
      Player player = (Player)who.getBukkitEntity();
      PlayerLocaleChangeEvent event = new PlayerLocaleChangeEvent(player, oldLocale, newLocale);
      org.bukkit.Bukkit.getPluginManager().callEvent(event);
      return event;
   }
}
