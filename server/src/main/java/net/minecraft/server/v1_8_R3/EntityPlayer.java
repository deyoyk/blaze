package net.minecraft.server.v1_8_R3;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import lol.deyo.blaze.combat.CombatResult;
import lol.deyo.blaze.configs.BlazeConfig;
import io.netty.buffer.Unpooled;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class EntityPlayer extends EntityHuman implements ICrafting {
   public final MinecraftServer server;
   public final PlayerInteractManager playerInteractManager;
   public final List<ChunkCoordIntPair> chunkCoordIntPairQueue = Lists.newLinkedList();
   private final ServerStatisticManager bK;
   public String locale = "en_US";
   public PlayerConnection playerConnection;
   public double d;
   public double e;
   public int lastSentExp = -99999999;
   public int invulnerableTicks = 60;
   public boolean g;
   public int ping;
   public boolean viewingCredits;
   private boolean bS = true;
   public String displayName;
   public IChatBaseComponent listName;
   public Location compassTarget;
   public int newExp = 0;
   public int newLevel = 0;
   public int newTotalExp = 0;
   public boolean keepLevel = false;
   public double maxHealthCache;
   public boolean joining = true;
   public boolean collidesWithEntities = true;
   public int viewDistance;
   public transient long lastActivity = System.currentTimeMillis();
   public long timeOffset = 0L;
   public boolean relativeTime = true;
   public WeatherType weather = null;
   private float bL = Float.MIN_VALUE;
   private float bM = -1.0E8F;
   private int bN = -99999999;
   private boolean bO = true;
   private EntityHuman.EnumChatVisibility bR;
   private long bT = System.currentTimeMillis();
   private Entity bU = null;
   private int containerCounter;
   private int containerUpdateDelay;
   private float pluginRainPosition;
   private float pluginRainPositionPrevious;
   public int playerMapX;
   public int playerMapZ;
   private final Deque<ItemStack> useItemQueue = new ArrayDeque<>();
   public boolean inventoryOpen;

   public EntityPlayer(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
      super(worldserver, gameprofile);
      this.viewDistance = this.world.spigotConfig.viewDistance;
      playerinteractmanager.player = this;
      this.playerInteractManager = playerinteractmanager;
      BlockPosition blockposition = worldserver.getSpawn();
      if (!worldserver.worldProvider.o() && worldserver.getWorldData().getGameType() != WorldSettings.EnumGamemode.ADVENTURE) {
         int i = Math.max(5, minecraftserver.getSpawnProtection() - 6);
         int j = MathHelper.floor(worldserver.getWorldBorder().b(blockposition.getX(), blockposition.getZ()));
         if (j < i) {
            i = j;
         }

         if (j <= 1) {
            i = 1;
         }

         blockposition = worldserver.r(blockposition.a(this.random.nextInt(i * 2) - i, 0, this.random.nextInt(i * 2) - i));
      }

      this.server = minecraftserver;
      this.bK = minecraftserver.getPlayerList().a((EntityHuman)this);
      this.S = 0.0F;
      this.setPositionRotation(blockposition, 0.0F, 0.0F);

      while (!worldserver.getCubes(this, this.getBoundingBox()).isEmpty() && this.locY < 255.0) {
         this.setPosition(this.locX, this.locY + 1.0, this.locZ);
      }

      this.displayName = this.getName();
      this.maxHealthCache = this.getMaxHealth();
   }

   @Override
   public boolean ad() {
      return this.collidesWithEntities && super.ad();
   }

   public int getPing() {
      return this.ping;
   }

   @Override
   public boolean ae() {
      return this.collidesWithEntities && super.ae();
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      if (nbttagcompound.hasKeyOfType("playerGameType", 99)) {
         if (MinecraftServer.getServer().getForceGamemode()) {
            this.playerInteractManager.setGameMode(MinecraftServer.getServer().getGamemode());
         } else {
            this.playerInteractManager.setGameMode(WorldSettings.EnumGamemode.getById(nbttagcompound.getInt("playerGameType")));
         }
      }

      this.getBukkitEntity().readExtraData(nbttagcompound);
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setInt("playerGameType", this.playerInteractManager.getGameMode().getId());
      this.getBukkitEntity().setExtraData(nbttagcompound);
   }

   @Override
   public void spawnIn(World world) {
      super.spawnIn(world);
      if (world == null) {
         this.dead = false;
         BlockPosition position = null;
         if (this.spawnWorld != null && !this.spawnWorld.isEmpty()) {
            CraftWorld cworld = (CraftWorld)Bukkit.getServer().getWorld(this.spawnWorld);
            if (cworld != null && this.getBed() != null) {
               world = cworld.getHandle();
               position = EntityHuman.getBed(cworld.getHandle(), this.getBed(), false);
            }
         }

         if (world == null || position == null) {
            world = ((CraftWorld)Bukkit.getServer().getWorlds().get(0)).getHandle();
            position = world.getSpawn();
         }

         this.world = world;
         this.setPosition(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);
      }

      this.dimension = ((WorldServer)this.world).dimension;
      this.playerInteractManager.a((WorldServer)world);
   }

   @Override
   public void levelDown(int i) {
      super.levelDown(i);
      this.lastSentExp = -1;
   }

   @Override
   public void enchantDone(int i) {
      super.enchantDone(i);
      this.lastSentExp = -1;
   }

   public void syncInventory() {
      this.activeContainer.addSlotListener(this);
   }

   @Override
   public void t_() {
      if (this.joining) {
         this.joining = false;
      }

      this.playerInteractManager.a();
      this.invulnerableTicks--;
      if (this.noDamageTicks > 0) {
         this.noDamageTicks--;
      }

      if (this.noDamageTicksArrow > 0) {
         this.noDamageTicksArrow--;
      }

      if (--this.containerUpdateDelay <= 0) {
         this.activeContainer.b();
         this.containerUpdateDelay = this.world.paperSpigotConfig.containerUpdateTickRate;
      }

      if (!this.activeContainer.a(this)) {
         this.closeInventory();
         this.activeContainer = this.defaultContainer;
      }

      if (!this.chunkCoordIntPairQueue.isEmpty()) {
         List<Chunk> arraylist = new ArrayList<>();
         List<TileEntity> arraylist1 = new ArrayList<>();
         Iterator<ChunkCoordIntPair> iterator1 = this.chunkCoordIntPairQueue.iterator();

         while (iterator1.hasNext() && arraylist.size() < this.world.spigotConfig.maxBulkChunk) {
            ChunkCoordIntPair chunkcoordintpair = iterator1.next();
            if (chunkcoordintpair != null) {
               if (this.world.isValidLocation(chunkcoordintpair.x << 4, chunkcoordintpair.z << 4)) {
                  Chunk chunk = this.world.getChunkIfLoaded(chunkcoordintpair.x, chunkcoordintpair.z);
                  if (chunk != null && chunk.isReady()) {
                     arraylist.add(chunk);
                     arraylist1.addAll(chunk.tileEntities.values());
                     iterator1.remove();
                  }
               }
            } else {
               iterator1.remove();
            }
         }

         if (!arraylist.isEmpty()) {
            if (arraylist.size() == 1) {
               this.playerConnection.sendPacket(new PacketPlayOutMapChunk(arraylist.get(0), true, 65535));
            } else {
               this.playerConnection.sendPacket(new PacketPlayOutMapChunkBulk(arraylist));
            }

            for (TileEntity tile : arraylist1) {
               this.a(tile);
            }
         }
      }

      Entity entity = this.C();
      if (entity != this) {
         if (!entity.isAlive()) {
            this.setSpectatorTarget(this);
         } else {
            this.setLocation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
            this.server.getPlayerList().d(this);
            if (this.isSneaking()) {
               this.setSpectatorTarget(this);
            }
         }
      }

      if (this.playerConnection != null) {
         this.playerConnection.positionHistory.record(this, System.nanoTime());
      }
   }

   public void l() {
      try {
         super.t_();

         for (int i = 0; i < this.inventory.getSize(); i++) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (itemstack != null && itemstack.getItem().f()) {
               Packet<?> packet = ((ItemWorldMapBase)itemstack.getItem()).c(itemstack, this.world, this);
               if (packet != null) {
                  this.playerConnection.sendPacket(packet);
               }
            }
         }

         if (this.getHealth() != this.bM || this.bN != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.bO) {
            this.playerConnection
               .sendPacket(
                  new PacketPlayOutUpdateHealth(this.getBukkitEntity().getScaledHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel())
               );
            this.bM = this.getHealth();
            this.bN = this.foodData.getFoodLevel();
            this.bO = this.foodData.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionHearts() != this.bL) {
            this.bL = this.getHealth() + this.getAbsorptionHearts();

            for (ScoreboardObjective scoreboardObjective : this.getScoreboard().getObjectivesForCriteria(IScoreboardCriteria.g)) {
               this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardObjective).updateForList(Arrays.asList(this));
            }

            this.world.getServer().getScoreboardManager().updateAllScoresForList(IScoreboardCriteria.g, this.getName(), ImmutableList.of(this));
         }

         if (this.maxHealthCache != this.getMaxHealth()) {
            this.getBukkitEntity().updateScaledHealth();
         }

         if (this.expTotal != this.lastSentExp) {
            this.lastSentExp = this.expTotal;
            this.playerConnection.sendPacket(new PacketPlayOutExperience(this.exp, this.expTotal, this.expLevel));
         }

         if (this.oldLevel == -1) {
            this.oldLevel = this.expLevel;
         }

         if (this.oldLevel != this.expLevel) {
            CraftEventFactory.callPlayerLevelChangeEvent(this.world.getServer().getPlayer(this), this.oldLevel, this.expLevel);
            this.oldLevel = this.expLevel;
         }
      } catch (Throwable var4) {
         CrashReport crashreport = CrashReport.a(var4, "Ticking player");
         CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Player being ticked");
         this.appendEntityCrashDetails(crashreportsystemdetails);
         throw new ReportedException(crashreport);
      }
   }

   protected void i_() {
   }

   @Override
   public void die(DamageSource damagesource) {
      if (!this.dead) {
         List<org.bukkit.inventory.ItemStack> loot = new ArrayList<>();
         boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");
         if (!keepInventory) {
            for (int i = 0; i < this.inventory.items.length; i++) {
               if (this.inventory.items[i] != null) {
                  loot.add(CraftItemStack.asCraftMirror(this.inventory.items[i]));
               }
            }

            for (int var15 = 0; var15 < this.inventory.armor.length; var15++) {
               if (this.inventory.armor[var15] != null) {
                  loot.add(CraftItemStack.asCraftMirror(this.inventory.armor[var15]));
               }
            }
         }

         IChatBaseComponent chatmessage = damagesource == DamageSource.GENERIC ? damagesource.getLocalizedDeathMessage(this) : this.bs().b();
         String deathmessage = chatmessage.c();
         PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(this, loot, deathmessage, keepInventory);
         this.closeInventory();
         this.bs().g();
         this.b(StatisticList.y);
         String deathMessage = event.getDeathMessage();
         if (deathMessage != null && !deathMessage.isEmpty() && this.world.getGameRules().getBoolean("showDeathMessages")) {
            if (deathMessage.equals(deathmessage)) {
               this.server.getPlayerList().sendMessage(chatmessage);
            } else {
               this.server.getPlayerList().sendMessage(CraftChatMessage.fromString(deathMessage));
            }
         }

         if (!event.getKeepInventory()) {
            for (int ix = 0; ix < this.inventory.items.length; ix++) {
               this.inventory.setItem(ix, null);
            }

            for (int var17 = 0; var17 < this.inventory.armor.length; var17++) {
               this.inventory.player.setEquipment(var17, null);
            }
         }

         this.setSpectatorTarget(this);

         for (ScoreboardScore scoreboardScore : this.world
            .getServer()
            .getScoreboardManager()
            .getScoreboardScores(IScoreboardCriteria.d, this.getName(), new ArrayList<ScoreboardScore>())) {
            scoreboardScore.incrementScore();
         }

         EntityLiving entityliving = this.bt();
         if (entityliving != null) {
            entityliving.b(this, this.aW);
         }

         CraftPlayer entity = this.getBukkitEntity();
         if (entity.isOnline() && (Boolean)BlazeConfig.autoRespawnPlayer.getValue()) {
            BlockPosition respawnPosition = this.getBed();
            Location location = null;
            boolean isBedSpawn = false;
            CraftWorld world = (CraftWorld)this.server.server.getWorld(this.spawnWorld);
            if (world != null && respawnPosition != null) {
               BlockPosition bedPosition = EntityHuman.getBed(world.getHandle(), respawnPosition, this.isRespawnForced());
               if (bedPosition != null) {
                  isBedSpawn = true;
                  location = new Location(world, bedPosition.getY() + 0.5, bedPosition.getY(), bedPosition.getZ() + 0.5);
               } else {
                  this.setRespawnPosition(null, true);
                  this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(0, 0.0F));
               }
            }

            if (location == null) {
               world = (CraftWorld)this.server.server.getWorlds().get(0);
               respawnPosition = world.getHandle().getSpawn();
               location = new Location(world, respawnPosition.getX() + 0.5, respawnPosition.getY(), respawnPosition.getZ() + 0.5, 0.0F, 0.0F);
            }

            PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(entity, location, isBedSpawn);
            Bukkit.getPluginManager().callEvent(respawnEvent);
            if (this.playerConnection.isDisconnected()) {
               return;
            }

            this.reset();
            entity.teleport(respawnEvent.getRespawnLocation());
            entity.setVelocity(new Vector(0, 0, 0));
            this.extinguish();
            this.dead = false;
         }
      }
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (this.isInvulnerable(damagesource)) {
         return false;
      } else {
         boolean flag = this.cr() && "fall".equals(damagesource.translationIndex);
         if (!flag && this.invulnerableTicks > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion()) {
               Entity entity = damagesource.getEntity();
               if (entity instanceof EntityHuman && !this.a((EntityHuman)entity)) {
                  return false;
               }

               if (entity instanceof EntityArrow) {
                  EntityArrow entityarrow = (EntityArrow)entity;
                  if (entityarrow.shooter instanceof EntityHuman && !this.a((EntityHuman)entityarrow.shooter)) {
                     return false;
                  }
               }
            }

            return super.damageEntity(damagesource, f);
         }
      }
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return this.cr() && super.a(entityhuman);
   }

   private boolean cr() {
      return this.world.pvpMode;
   }

   @Override
   public void c(int i) {
      boolean endPortal = this.dimension == 1 && i == 1;
      if (endPortal && !this.world.paperSpigotConfig.disableEndCredits) {
         this.world.kill(this);
         this.viewingCredits = true;
         this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, 0.0F));
      }

      if (!endPortal || this.world.paperSpigotConfig.disableEndCredits) {
         TeleportCause cause = !endPortal && this.dimension != 1 && i != 1 ? TeleportCause.NETHER_PORTAL : TeleportCause.END_PORTAL;
         this.server.getPlayerList().changeDimension(this, i, cause);
         this.lastSentExp = -1;
         this.bM = -1.0F;
         this.bN = -1;
      }
   }

   @Override
   public boolean a(EntityPlayer entityplayer) {
      return entityplayer.isSpectator() ? this.C() == this : !this.isSpectator() && super.a(entityplayer);
   }

   public void a(TileEntity tileentity) {
      if (tileentity != null) {
         Packet<?> packet = tileentity.getUpdatePacket();
         if (packet != null) {
            this.playerConnection.sendPacket(packet);
         }
      }
   }

   @Override
   public void receive(Entity entity, int i) {
      super.receive(entity, i);
      this.activeContainer.b();
   }

   @Override
   public EntityHuman.EnumBedResult a(BlockPosition blockposition) {
      EntityHuman.EnumBedResult entityhuman_enumbedresult = super.a(blockposition);
      if (entityhuman_enumbedresult == EntityHuman.EnumBedResult.OK) {
         PacketPlayOutBed packetplayoutbed = new PacketPlayOutBed(this, blockposition);
         this.u().getTracker().a(this, packetplayoutbed);
         this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
         this.playerConnection.sendPacket(packetplayoutbed);
      }

      return entityhuman_enumbedresult;
   }

   @Override
   public void a(boolean flag, boolean flag1, boolean flag2) {
      if (this.sleeping) {
         if (this.isSleeping()) {
            this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(this, 2));
         }

         super.a(flag, flag1, flag2);
         if (this.playerConnection != null) {
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
         }
      }
   }

   @Override
   public void mount(Entity entity) {
      Entity entity1 = this.vehicle;
      super.mount(entity);
      if (this.vehicle != entity1) {
         this.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, this, this.vehicle));
         this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
         if (this.vehicle instanceof EntityLiving) {
            AttributeMapServer attributemapserver = (AttributeMapServer)((EntityLiving)this.vehicle).getAttributeMap();
            Collection<AttributeInstance> collection = attributemapserver.getUsefulAttributes(this.vehicle instanceof EntityHorse);
            if (!collection.isEmpty()) {
               this.playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(this.vehicle.getId(), collection));
            }
         }
      }
   }

   @Override
   protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
   }

   public void a(double d0, boolean flag) {
      int i = MathHelper.floor(this.locX);
      int j = MathHelper.floor(this.locY - 0.2F);
      int k = MathHelper.floor(this.locZ);
      IBlockData iblockdata = this.world.getType(i, j, k);
      if (iblockdata.getBlock().getMaterial() == Material.AIR) {
         IBlockData down = this.world.getType(i, j - 1, k);
         Block block = down.getBlock();
         if (block instanceof BlockFence || block instanceof BlockCobbleWall || block instanceof BlockFenceGate) {
            j--;
            iblockdata = down;
         }
      }

      Block block = iblockdata.getBlock();
      if ((Boolean)BlazeConfig.footstepParticles.getValue() && this.fallDistance > 3.0F && flag) {
         float f = MathHelper.f(this.fallDistance - 3.0F);
         if (block.getMaterial() != Material.AIR) {
            double d1 = Math.min(0.2F + f / 15.0F, 10.0F);
            if (d1 > 2.5) {
               d1 = 2.5;
            }

            ((WorldServer)this.world)
               .sendParticles(
                  this,
                  EnumParticle.BLOCK_DUST,
                  false,
                  this.locX,
                  this.locY,
                  this.locZ,
                  (int)(150.0 * d1),
                  0.0,
                  0.0,
                  0.0,
                  0.15F,
                  Block.getCombinedId(iblockdata)
               );
         }
      }

      super.a(d0, flag, block, new BlockPosition(i, j, k));
   }

   @Override
   public void openSign(TileEntitySign tileentitysign) {
      tileentitysign.a(this);
      this.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(tileentitysign.getPosition()));
   }

   public int nextContainerCounter() {
      this.containerCounter = this.containerCounter % 100 + 1;
      return this.containerCounter;
   }

   @Override
   public void openTileEntity(ITileEntityContainer itileentitycontainer) {
      Container container = CraftEventFactory.callInventoryOpenEvent(this, itileentitycontainer.createContainer(this.inventory, this));
      if (container != null) {
         this.nextContainerCounter();
         this.playerConnection
            .sendPacket(
               new PacketPlayOutOpenWindow(this.containerCounter, itileentitycontainer.getContainerName(), itileentitycontainer.getScoreboardDisplayName())
            );
         this.activeContainer = container;
         this.activeContainer.windowId = this.containerCounter;
         this.activeContainer.addSlotListener(this);
      }
   }

   @Override
   public void openContainer(IInventory iinventory) {
      boolean cancelled = false;
      if (iinventory instanceof ITileInventory) {
         ITileInventory itileinventory = (ITileInventory)iinventory;
         cancelled = itileinventory.r_() && !this.a(itileinventory.i()) && !this.isSpectator();
      }

      Container container;
      if (iinventory instanceof ITileEntityContainer) {
         container = ((ITileEntityContainer)iinventory).createContainer(this.inventory, this);
      } else {
         container = new ContainerChest(this.inventory, iinventory, this);
      }

      container = CraftEventFactory.callInventoryOpenEvent(this, container, cancelled);
      if (container == null && !cancelled) {
         iinventory.closeContainer(this);
      } else {
         if (this.activeContainer != this.defaultContainer) {
            this.closeInventory();
         }

         if (iinventory instanceof ITileInventory) {
            ITileInventory itileinventory = (ITileInventory)iinventory;
            if (itileinventory.r_() && !this.a(itileinventory.i()) && !this.isSpectator() && container == null) {
               this.playerConnection.sendPacket(new PacketPlayOutChat(new ChatMessage("container.isLocked", iinventory.getScoreboardDisplayName()), (byte)2));
               this.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect("random.door_close", this.locX, this.locY, this.locZ, 1.0F, 1.0F));
               iinventory.closeContainer(this);
               return;
            }
         }

         this.nextContainerCounter();
         if (iinventory instanceof ITileEntityContainer) {
            this.playerConnection
               .sendPacket(
                  new PacketPlayOutOpenWindow(
                     this.containerCounter, ((ITileEntityContainer)iinventory).getContainerName(), iinventory.getScoreboardDisplayName(), iinventory.getSize()
                  )
               );
         } else {
            this.playerConnection
               .sendPacket(
                  new PacketPlayOutOpenWindow(this.containerCounter, "minecraft:container", iinventory.getScoreboardDisplayName(), iinventory.getSize())
               );
         }

         this.activeContainer = container;
         this.activeContainer.windowId = this.containerCounter;
         this.activeContainer.addSlotListener(this);
      }
   }

   @Override
   public void openTrade(IMerchant imerchant) {
      Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerMerchant(this.inventory, imerchant, this.world));
      if (container != null) {
         this.nextContainerCounter();
         this.activeContainer = container;
         this.activeContainer.windowId = this.containerCounter;
         this.activeContainer.addSlotListener(this);
         InventoryMerchant inventorymerchant = ((ContainerMerchant)this.activeContainer).e();
         IChatBaseComponent ichatbasecomponent = imerchant.getScoreboardDisplayName();
         this.playerConnection
            .sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, "minecraft:villager", ichatbasecomponent, inventorymerchant.getSize()));
         MerchantRecipeList merchantrecipelist = imerchant.getOffers(this);
         if (merchantrecipelist != null) {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());
            packetdataserializer.writeInt(this.containerCounter);
            merchantrecipelist.a(packetdataserializer);
            this.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", packetdataserializer));
         }
      }
   }

   @Override
   public void openHorseInventory(EntityHorse entityhorse, IInventory iinventory) {
      Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerHorse(this.inventory, iinventory, entityhorse, this));
      if (container == null) {
         iinventory.closeContainer(this);
      } else {
         if (this.activeContainer != this.defaultContainer) {
            this.closeInventory();
         }

         this.nextContainerCounter();
         this.playerConnection
            .sendPacket(
               new PacketPlayOutOpenWindow(
                  this.containerCounter, "EntityHorse", iinventory.getScoreboardDisplayName(), iinventory.getSize(), entityhorse.getId()
               )
            );
         this.activeContainer = container;
         this.activeContainer.windowId = this.containerCounter;
         this.activeContainer.addSlotListener(this);
      }
   }

   @Override
   public void openBook(ItemStack itemstack) {
      Item item = itemstack.getItem();
      if (item == Items.WRITTEN_BOOK) {
         this.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(Unpooled.buffer())));
      }
   }

   @Override
   public void a(Container container, int i, ItemStack itemstack) {
      if (!(container.getSlot(i) instanceof SlotResult) && !this.g) {
         this.playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, i, itemstack));
      }
   }

   public void updateInventory(Container container) {
      this.a(container, container.a());
   }

   @Override
   public void a(Container container, List<ItemStack> list) {
      this.playerConnection.sendPacket(new PacketPlayOutWindowItems(container.windowId, list));
      this.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.inventory.getCarried()));
      InventoryType type = container.getBukkitView().getType();
      if (type == InventoryType.CRAFTING || type == InventoryType.WORKBENCH) {
         this.playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, 0, container.getSlot(0).getItem()));
      }
   }

   public boolean canSee(Entity entity) {
      if (!(Boolean)BlazeConfig.hideEntitiesThrownHidden.getValue()) {
         return true;
      } else {
         Player bukkit = this.getBukkitEntity();
         Player target = null;
         if (entity instanceof EntityProjectile) {
            EntityProjectile projectile = (EntityProjectile)entity;
            if (projectile.shooter instanceof EntityPlayer) {
               target = ((EntityPlayer)projectile.shooter).getBukkitEntity();
            }
         } else if (entity instanceof EntityItem) {
            EntityItem item = (EntityItem)entity;
            if (item.owner instanceof EntityPlayer) {
               target = ((EntityPlayer)item.owner).getBukkitEntity();
            }
         } else if (entity instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow)entity;
            if (arrow.shooter instanceof EntityPlayer) {
               target = ((EntityPlayer)arrow.shooter).getBukkitEntity();
            }
         } else if (entity instanceof EntityFishingHook) {
            EntityFishingHook hook = (EntityFishingHook)entity;
            if (hook.owner instanceof EntityPlayer) {
               target = ((EntityPlayer)hook.owner).getBukkitEntity();
            }
         }

         if (target == null && entity instanceof EntityPlayer) {
            target = ((EntityPlayer)entity).getBukkitEntity();
         }

         return target == null || bukkit.equals(target) || bukkit.canSee(target);
      }
   }

   @Override
   public void setContainerData(Container container, int i, int j) {
      this.playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, i, j));
   }

   @Override
   public void setContainerData(Container container, IInventory iinventory) {
      for (int i = 0; i < iinventory.g(); i++) {
         this.playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, i, iinventory.getProperty(i)));
      }
   }

   @Override
   public void closeInventory() {
      CraftEventFactory.handleInventoryCloseEvent(this);
      this.playerConnection.sendPacket(new PacketPlayOutCloseWindow(this.activeContainer.windowId));
      this.inventoryOpen = false;
      this.p();
   }

   public void broadcastCarriedItem() {
      if (!this.g) {
         this.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.inventory.getCarried()));
      }
   }

   public void p() {
      this.activeContainer.b(this);
      this.activeContainer = this.defaultContainer;
   }

   public void a(float f, float f1, boolean flag, boolean flag1) {
      if (this.vehicle != null) {
         if (f >= -1.0F && f <= 1.0F) {
            this.aZ = f;
         }

         if (f1 >= -1.0F && f1 <= 1.0F) {
            this.ba = f1;
         }

         this.aY = flag;
         this.setSneaking(flag1);
      }
   }

   @Override
   public void a(Statistic statistic, int i) {
      if (statistic != null) {
         this.bK.b(this, statistic, i);

         for (ScoreboardObjective scoreboardObjective : this.getScoreboard().getObjectivesForCriteria(statistic.k())) {
            this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardObjective).addScore(i);
         }

         if (this.bK.e()) {
            this.bK.a(this);
         }
      }
   }

   @Override
   public void a(Statistic statistic) {
      if (statistic != null) {
         this.bK.setStatistic(this, statistic, 0);

         for (ScoreboardObjective scoreboardObjective : this.getScoreboard().getObjectivesForCriteria(statistic.k())) {
            this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardObjective).setScore(0);
         }

         if (this.bK.e()) {
            this.bK.a(this);
         }
      }
   }

   public void q() {
      if (this.passenger != null) {
         this.passenger.mount(this);
      }

      if (this.sleeping) {
         this.a(true, false, false);
      }
   }

   public void triggerHealthUpdate() {
      this.bM = -1.0E8F;
      this.lastSentExp = -1;
   }

   public void sendMessage(IChatBaseComponent[] ichatbasecomponent) {
      for (IChatBaseComponent component : ichatbasecomponent) {
         this.sendMessage(component);
      }
   }

   @Override
   public void b(IChatBaseComponent ichatbasecomponent) {
      this.playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent));
   }

   @Override
   protected void s() {
      this.playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte)9));
      super.s();
   }

   @Override
   public void a(ItemStack itemstack, int i) {
      super.a(itemstack, i);
      if (itemstack != null && itemstack.getItem() != null && itemstack.getItem().e(itemstack) == EnumAnimation.EAT) {
         this.u().getTracker().a(this, new PacketPlayOutAnimation(this, 3));
      }
   }

   @Override
   public void copyTo(EntityHuman entityhuman, boolean flag) {
      super.copyTo(entityhuman, flag);
      this.lastSentExp = -1;
      this.bM = -1.0F;
      this.bN = -1;
   }

   @Override
   protected void a(MobEffect mobeffect) {
      super.a(mobeffect);
      this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
   }

   @Override
   protected void a(MobEffect mobeffect, boolean flag) {
      super.a(mobeffect, flag);
      this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
   }

   @Override
   protected void b(MobEffect mobeffect) {
      super.b(mobeffect);
      this.playerConnection.sendPacket(new PacketPlayOutRemoveEntityEffect(this.getId(), mobeffect));
   }

   @Override
   public void enderTeleportTo(double d0, double d1, double d2) {
      this.playerConnection.a(d0, d1, d2, this.yaw, this.pitch);
   }

   @Override
   public void b(Entity entity) {
      this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 4));
   }

   @Override
   public void c(Entity entity) {
      this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 5));
   }

   @Override
   public void updateAbilities() {
      if (this.playerConnection != null) {
         this.playerConnection.sendPacket(new PacketPlayOutAbilities(this.abilities));
         this.B();
      }
   }

   public WorldServer u() {
      return (WorldServer)this.world;
   }

   @Override
   public void a(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
      this.getBukkitEntity().setGameMode(GameMode.getByValue(worldsettings_enumgamemode.getId()));
   }

   @Override
   public boolean isSpectator() {
      return this.playerInteractManager.getGameMode().equals(WorldSettings.EnumGamemode.SPECTATOR);
   }

   @Override
   public void sendMessage(IChatBaseComponent ichatbasecomponent) {
      this.playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent));
   }

   @Override
   public boolean a(int i, String s) {
      return "@".equals(s) ? this.getBukkitEntity().hasPermission("minecraft.command.selector") : true;
   }

   public String w() {
      String s = this.playerConnection.networkManager.getSocketAddress().toString();
      s = s.substring(s.indexOf("/") + 1);
      return s.substring(0, s.indexOf(":"));
   }

   public void a(PacketPlayInSettings packetplayinsettings) {
      String oldLocale = this.locale;
      this.locale = packetplayinsettings.a();
      if (!this.locale.equals(oldLocale)) {
         CraftEventFactory.callPlayerLocaleChangeEvent(this, oldLocale, this.locale);
      }

      this.bR = packetplayinsettings.c();
      this.bS = packetplayinsettings.d();
      this.getDataWatcher().watch(10, (byte)packetplayinsettings.e());
   }

   public EntityHuman.EnumChatVisibility getChatFlags() {
      return this.bR;
   }

   public void setResourcePack(String s, String s1) {
      this.playerConnection.sendPacket(new PacketPlayOutResourcePackSend(s, s1));
   }

   public void resetIdleTimer() {
      if ((Boolean)BlazeConfig.playerIdleTimer.getValue()) {
         this.bT = MinecraftServer.az();
      }
   }

   public ServerStatisticManager getStatisticManager() {
      return this.bK;
   }

   public void d(Entity entity) {
      this.playerConnection.queueEntityTrackerPacket(new PacketPlayOutEntityDestroy(entity.getId()), 3);
   }

   @Override
   protected void B() {
      if (this.isSpectator()) {
         this.bj();
         this.setInvisible(true);
      } else {
         super.B();
      }
   }

   public Entity C() {
      return (Entity)(this.bU == null ? this : this.bU);
   }

   public void setSpectatorTarget(Entity entity) {
      Entity entity1 = this.C();
      this.bU = (Entity)(entity == null ? this : entity);
      if (entity1 != this.bU) {
         this.playerConnection.sendPacket(new PacketPlayOutCamera(this.bU));
         this.enderTeleportTo(this.bU.locX, this.bU.locY, this.bU.locZ);
      }
   }

   @Override
   public void attack(Entity entity) {
      this.attack(entity, null);
   }

   @Override
   public void attack(Entity entity, CombatResult combat) {
      if (this.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.SPECTATOR) {
         this.setSpectatorTarget(entity);
      } else {
         super.attack(entity, combat);
      }
   }

   public long D() {
      return this.bT;
   }

   public IChatBaseComponent getPlayerListName() {
      return this.listName;
   }

   public long getPlayerTime() {
      return this.relativeTime ? this.world.getDayTime() + this.timeOffset : this.world.getDayTime() - this.world.getDayTime() % 24000L + this.timeOffset;
   }

   public WeatherType getPlayerWeather() {
      return this.weather;
   }

   public void setPlayerWeather(WeatherType type, boolean plugin) {
      if (plugin || this.weather == null) {
         if (plugin) {
            this.weather = type;
         }

         if (type == WeatherType.DOWNFALL) {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0.0F));
         } else {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(1, 0.0F));
         }
      }
   }

   public void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder) {
      if (this.weather == null) {
         if (oldRain != newRain) {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, newRain));
         }
      } else if (this.pluginRainPositionPrevious != this.pluginRainPosition) {
         this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, this.pluginRainPosition));
      }

      if (oldThunder != newThunder) {
         if (this.weather != WeatherType.DOWNFALL && this.weather != null) {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, 0.0F));
         } else {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, newThunder));
         }
      }
   }

   public void tickWeather() {
      if (this.weather != null) {
         this.pluginRainPositionPrevious = this.pluginRainPosition;
         if (this.weather == WeatherType.DOWNFALL) {
            this.pluginRainPosition += 0.01F;
         } else {
            this.pluginRainPosition -= 0.01F;
         }

         this.pluginRainPosition = MathHelper.a(this.pluginRainPosition, 0.0F, 1.0F);
      }
   }

   public void resetPlayerWeather() {
      this.weather = null;
      this.setPlayerWeather(this.world.getWorldData().hasStorm() ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
   }

   @Override
   public String toString() {
      return super.toString() + "(" + this.getName() + " at " + this.locX + "," + this.locY + "," + this.locZ + ")";
   }

   public void reset() {
      float exp = 0.0F;
      boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");
      if (this.keepLevel || keepInventory) {
         exp = this.exp;
         this.newTotalExp = this.expTotal;
         this.newLevel = this.expLevel;
      }

      this.setHealth(this.getMaxHealth());
      this.fireTicks = 0;
      this.fallDistance = 0.0F;
      this.foodData = new FoodMetaData(this);
      this.expLevel = this.newLevel;
      this.expTotal = this.newTotalExp;
      this.exp = 0.0F;
      this.deathTicks = 0;
      this.removeAllEffects();
      this.updateEffects = true;
      this.activeContainer = this.defaultContainer;
      this.killer = null;
      this.lastDamager = null;
      this.combatTracker = new CombatTracker(this);
      this.lastSentExp = -1;
      if (!this.keepLevel && !keepInventory) {
         this.giveExp(this.newExp);
      } else {
         this.exp = exp;
      }

      this.keepLevel = false;
   }

   public CraftPlayer getBukkitEntity() {
      return (CraftPlayer)super.getBukkitEntity();
   }

   @Override
   public void g(float f, float f1) {
      double d0 = this.locX;
      double d1 = this.locY;
      double d2 = this.locZ;
      if (this.abilities.isFlying && this.vehicle == null) {
         double d3 = this.motY;
         float f2 = this.aM;
         this.aM = this.abilities.a() * (this.isSprinting() ? 2 : 1);
         this.moveEntityWithNoHeading();
         this.motY = d3 * 0.6;
         this.aM = f2;
      } else {
         this.moveEntityWithNoHeading();
      }

      this.checkMovement(this.locX - d0, this.locY - d1, this.locZ - d2);
   }

   public void moveEntityWithNoHeading() {
      if (this.bM()) {
         if (this.V() && !this.abilities.isFlying) {
            double d0 = this.locY;
            float waterFriction = 0.8F;
            float depthStriderMultiplier = EnchantmentManager.b(this);
            if (depthStriderMultiplier > 3.0F) {
               depthStriderMultiplier = 3.0F;
            }

            if (!this.onGround) {
               depthStriderMultiplier *= 0.5F;
            }

            if (depthStriderMultiplier > 0.0F) {
               waterFriction += (0.54600006F - waterFriction) * depthStriderMultiplier / 3.0F;
            }

            this.move(this.motX, this.motY, this.motZ);
            this.motX *= waterFriction;
            this.motY *= 0.8F;
            this.motZ *= waterFriction;
            this.motY -= 0.02;
            if (this.positionChanged && this.c(this.motX, this.motY + 0.6F - this.locY + d0, this.motZ)) {
               this.motY = 0.3F;
            }
         } else if (this.ab() && !this.abilities.isFlying) {
            double d0x = this.locY;
            this.move(this.motX, this.motY, this.motZ);
            this.motX *= 0.5;
            this.motY *= 0.5;
            this.motZ *= 0.5;
            this.motY -= 0.02;
            if (this.positionChanged && this.c(this.motX, this.motY + 0.6F - this.locY + d0x, this.motZ)) {
               this.motY = 0.3F;
            }
         } else {
            float blockFriction = 0.91F;
            if (this.onGround) {
               blockFriction = this.world
                     .getType(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ))
                     .getBlock()
                     .frictionFactor
                  * 0.91F;
            }

            if (this.k_()) {
               this.motX = MathHelper.a(this.motX, -0.15F, 0.15F);
               this.motZ = MathHelper.a(this.motZ, -0.15F, 0.15F);
               this.fallDistance = 0.0F;
               if (this.motY < -0.15) {
                  this.motY = -0.15;
               }

               if (this.isSneaking() && this.motY < 0.0) {
                  this.motY = 0.0;
               }
            }

            this.move(this.motX, this.motY, this.motZ);
            if (this.positionChanged && this.k_()) {
               this.motY = 0.2;
            }

            this.motY -= 0.08;
            this.motY *= 0.98F;
            this.motX *= blockFriction;
            this.motZ *= blockFriction;
         }
      }

      this.aA = this.aB;
      double d0x = this.locX - this.lastX;
      double d1 = this.locZ - this.lastZ;
      float f2 = MathHelper.sqrt(d0x * d0x + d1 * d1) * 4.0F;
      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      this.aB = this.aB + (f2 - this.aB) * 0.4F;
      this.aC = this.aC + this.aB;
   }

   public void addToUseItemQueue(ItemStack itemStack) {
      this.useItemQueue.add(itemStack);
   }

   public void processItemQueue(PacketPlayInFlying packet) {
      float savedYaw = this.yaw;
      float savedPitch = this.pitch;
      if (packet.hasLook) {
         this.yaw = packet.yaw;
         this.pitch = packet.pitch;
      }

      ItemStack using;
      while ((using = this.useItemQueue.poll()) != null) {
         this.playerInteractManager.useItem(this, this.world, using);
      }

      this.yaw = savedYaw;
      this.pitch = savedPitch;
   }

   public void checkBackpacking() {
      if (this.inventoryOpen) {
         this.closeInventory();
      }
   }
}
