package net.minecraft.server.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import lol.deyo.blaze.configs.BlazeConfig;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TravelAgent;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.projectiles.ProjectileSource;
import org.spigotmc.ActivationRange;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public abstract class Entity implements ICommandListener {
   public static int lastChunkX = Integer.MAX_VALUE;
   public static int lastChunkZ = Integer.MAX_VALUE;
   public static Chunk lastChunk = null;
   private static final int CURRENT_LEVEL = 2;
   private static final AxisAlignedBB a = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   private static int entityCount = 1;
   public final byte activationType = ActivationRange.initializeEntityActivationType(this);
   public final boolean defaultActivationState;
   private final CommandObjectiveExecutor au;
   public double j;
   public boolean k;
   public Entity passenger;
   public Entity vehicle;
   public boolean attachedToPlayer;
   public World world;
   public double lastX;
   public double lastY;
   public double lastZ;
   public boolean forceExplosionKnockback;
   public double lastMotX;
   public double lastMotY;
   public double lastMotZ;
   public double locX;
   public double locY;
   public double locZ;
   public double spawnX;
   public double spawnY;
   public double spawnZ;
   public double motX;
   public double motY;
   public double motZ;
   public float yaw;
   public float pitch;
   public float lastYaw;
   public float lastPitch;
   public int lastLavaTick;
   public int lastCollisionTick;
   public AxisAlignedBB boundingBox;
   public boolean onGround;
   public boolean positionChanged;
   public boolean E;
   public boolean F;
   public boolean velocityChanged;
   public boolean dead;
   public float width;
   public float length;
   public float L;
   public float M;
   public float N;
   public float fallDistance;
   public double P;
   public double Q;
   public double R;
   public float S;
   public boolean noclip;
   public float U;
   public int ticksLived;
   public int maxFireTicks;
   public int fireTicks;
   public boolean inWater;
   public boolean inLava;
   public boolean hasBeenCounted = false;
   public SpawnReason spawnReason;
   public int noDamageTicks;
   public int noDamageTicksArrow;
   public boolean ad;
   public int ae;
   public int af;
   public int ag;
   public boolean ah;
   public boolean ai;
   public int portalCooldown;
   public int dimension;
   public boolean valid;
   public ProjectileSource projectileSource;
   public boolean inUnloadedChunk = false;
   public boolean loadChunks = false;
   public long activatedTick = -2147483648L;
   public boolean fromMobSpawner;
   protected boolean H;
   protected boolean justCreated;
   protected boolean fireProof;
   protected DataWatcher datawatcher;
   protected boolean ak;
   protected int al;
   protected BlockPosition an;
   protected Vec3D ao;
   protected EnumDirection ap;
   protected UUID uniqueID;
   protected CraftEntity bukkitEntity;
   protected int potential = 1;
   int numCollisions = 0;
   private int id = entityCount++;
   private boolean g;
   private int h;
   private double ar;
   private double as;
   private boolean invulnerable;
   public final Random random;
   public static final Random SHARED_RANDOM = new Random() {
      private boolean locked = false;

      @Override
      public synchronized void setSeed(long seed) {
         if (this.locked) {
            MinecraftServer.LOGGER.error("Ignoring setSeed on Entity.SHARED_RANDOM", new Throwable());
         } else {
            super.setSeed(seed);
            this.locked = true;
         }
      }
   };

   public Entity(World world) {
      this.j = 1.0;
      this.boundingBox = a;
      this.width = 0.6F;
      this.length = 1.8F;
      this.h = 1;
      this.maxFireTicks = 1;
      this.justCreated = true;
      this.random = SHARED_RANDOM;
      this.uniqueID = MathHelper.a(this.random);
      this.au = new CommandObjectiveExecutor();
      this.world = world;
      this.setPosition(0.0, 0.0, 0.0);
      if (world != null) {
         this.dimension = world.worldProvider.getDimension();
         this.defaultActivationState = ActivationRange.initializeEntityActivationState(this, world.spigotConfig);
      } else {
         this.defaultActivationState = false;
      }

      this.datawatcher = new DataWatcher(this);
      this.datawatcher.a(0, (byte)0);
      this.datawatcher.a(1, (short)300);
      this.datawatcher.a(3, (byte)0);
      this.datawatcher.a(2, "");
      this.datawatcher.a(4, (byte)0);
      this.h();
   }

   static boolean isLevelAtLeast(NBTTagCompound tag, int level) {
      return tag.hasKey("Bukkit.updateLevel") && tag.getInt("Bukkit.updateLevel") >= level;
   }

   public int getH() {
      return this.h;
   }

   public void setH(int h) {
      this.h = h;
   }

   public boolean merge(Entity entity) {
      return false;
   }

   protected void respawn() {
   }

   public boolean isAddedToChunk() {
      int chunkX = MathHelper.floor(this.locX / 16.0);
      int chunkY = MathHelper.floor(this.locY / 16.0);
      int chunkZ = MathHelper.floor(this.locZ / 16.0);
      return this.ad && this.getChunkX() == chunkX && this.getChunkY() == chunkY || this.getChunkZ() == chunkZ;
   }

   public int getChunkX() {
      return this.ae;
   }

   public int getChunkY() {
      return this.af;
   }

   public int getChunkZ() {
      return this.ag;
   }

   public void inactiveTick() {
   }

   public int getId() {
      return this.id;
   }

   public void d(int i) {
      this.id = i;
   }

   public void G() {
      this.die();
   }

   protected abstract void h();

   public DataWatcher getDataWatcher() {
      return this.datawatcher;
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof Entity && ((Entity)object).id == this.id;
   }

   @Override
   public int hashCode() {
      return this.id;
   }

   public void die() {
      this.dead = true;
   }

   public void setSize(float f, float f1) {
      if (f != this.width || f1 != this.length) {
         float f2 = this.width;
         this.width = f;
         this.length = f1;
         this.a(
            new AxisAlignedBB(
               this.boundingBox.a,
               this.boundingBox.b,
               this.boundingBox.c,
               this.boundingBox.a + this.width,
               this.boundingBox.b + this.length,
               this.boundingBox.c + this.width
            )
         );
         if (this.width > f2 && !this.justCreated) {
            this.move(f2 - this.width, 0.0, f2 - this.width);
         }
      }
   }

   protected void setYawPitch(float f, float f1) {
      if (Float.isNaN(f)) {
         f = 0.0F;
      }

      if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
         if (this instanceof EntityPlayer) {
            this.world.getServer().getLogger().warning(this.getName() + " was caught trying to crash the server with an invalid yaw");
            ((CraftPlayer)this.getBukkitEntity()).kickPlayer("Infinite yaw (Hacking?)");
         }

         f = 0.0F;
      }

      if (Float.isNaN(f1)) {
         f1 = 0.0F;
      }

      if (f1 == Float.POSITIVE_INFINITY || f1 == Float.NEGATIVE_INFINITY) {
         if (this instanceof EntityPlayer) {
            this.world.getServer().getLogger().warning(this.getName() + " was caught trying to crash the server with an invalid pitch");
            ((CraftPlayer)this.getBukkitEntity()).kickPlayer("Infinite pitch (Hacking?)");
         }

         f1 = 0.0F;
      }

      this.yaw = f % 360.0F;
      this.pitch = f1 % 360.0F;
   }

   public void setPosition(double d0, double d1, double d2) {
      this.locX = d0;
      this.locY = d1;
      this.locZ = d2;
      float f = this.width / 2.0F;
      this.a(new AxisAlignedBB(d0 - f, d1, d2 - f, d0 + f, d1 + this.length, d2 + f));
   }

   public void t_() {
      this.K();
   }

   private boolean paperNetherCheck() {
      return this.world.paperSpigotConfig.netherVoidTopDamage && this.world.getWorld().getEnvironment() == Environment.NETHER && this.locY >= 128.0;
   }

   public void K() {
      if (this.vehicle != null && this.vehicle.dead) {
         this.vehicle = null;
      }

      this.L = this.M;
      this.lastX = this.locX;
      this.lastY = this.locY;
      this.lastZ = this.locZ;
      this.lastPitch = this.pitch;
      this.lastYaw = this.yaw;
      if (this.world instanceof WorldServer && !(this instanceof EntityProjectile) && !(this instanceof EntityArrow)) {
         if (this.ak) {
            int i = this.L();
            if (this.vehicle == null && this.al++ >= i) {
               this.al = i;
               this.portalCooldown = this.aq();
               byte b0;
               if (this.world.worldProvider.getDimension() == -1) {
                  b0 = 0;
               } else {
                  b0 = -1;
               }

               this.c((int)b0);
            }

            this.ak = false;
         } else {
            if (this.al > 0) {
               this.al -= 4;
            }

            if (this.al < 0) {
               this.al = 0;
            }
         }

         if (this.portalCooldown > 0) {
            this.portalCooldown--;
         }
      }

      this.Y();
      this.W();
      if (this.fireTicks > 0) {
         if (this.fireProof) {
            this.fireTicks -= 4;
            if (this.fireTicks < 0) {
               this.fireTicks = 0;
            }
         } else {
            if (this.fireTicks % 20 == 0) {
               this.damageEntity(DamageSource.BURN, 1.0F);
            }

            this.fireTicks--;
         }
      }

      if (this.ab()) {
         this.burnFromLava();
         this.fallDistance *= 0.5F;
      }

      if (this.locY < -64.0 || this.paperNetherCheck()) {
         this.O();
      }

      this.b(0, this.fireTicks > 0);
      this.justCreated = false;
   }

   public int L() {
      return 0;
   }

   protected void burnFromLava() {
      if (!this.fireProof) {
         this.damageEntity(DamageSource.LAVA, 4.0F);
         if (this instanceof EntityLiving) {
            if (this.fireTicks <= 0) {
               org.bukkit.entity.Entity damagee = this.getBukkitEntity();
               EntityCombustEvent combustEvent = new EntityCombustByBlockEvent(null, damagee, 15);
               this.world.getServer().getPluginManager().callEvent(combustEvent);
               if (!combustEvent.isCancelled()) {
                  this.setOnFire(combustEvent.getDuration());
               }
            } else {
               this.setOnFire(15);
            }

            return;
         }

         this.setOnFire(15);
      }
   }

   public void setOnFire(int i) {
      int j = i * 20;
      j = EnchantmentProtection.a(this, j);
      if (this.fireTicks < j) {
         this.fireTicks = j;
      }
   }

   public void extinguish() {
      this.fireTicks = 0;
   }

   protected void O() {
      this.die();
   }

   public boolean c(double d0, double d1, double d2) {
      return this.b(this.boundingBox.c(d0, d1, d2));
   }

   private boolean b(AxisAlignedBB axisalignedbb) {
      return this.world.getCubes(this, axisalignedbb).isEmpty() && !this.world.containsLiquid(axisalignedbb);
   }

   public void loadChunks() {
      int chunkX = MathHelper.floor(this.locX) >> 4;
      int chunkZ = MathHelper.floor(this.locZ) >> 4;
      if (lastChunkX != chunkX || lastChunkZ != chunkZ) {
         Chunk chunk;
         if (!(this instanceof EntityTNTPrimed) && !(this instanceof EntityFallingBlock)) {
            chunk = ((ChunkProviderServer)this.world.chunkProvider).getChunkAt(chunkX, chunkZ);
         } else {
            chunk = ((ChunkProviderServer)this.world.chunkProvider).getChunkAtWithoutCreation(chunkX, chunkZ);
            chunk.lastActive = System.currentTimeMillis();
         }

         lastChunk = chunk;
         lastChunkX = chunkX;
         lastChunkZ = chunkZ;
      }
   }

   public void move(double d0, double d1, double d2) {
      if (this.loadChunks) {
         this.loadChunks();
      }

      if (this.noclip) {
         this.a(this.getBoundingBox().c(d0, d1, d2));
         this.recalcPosition();
      } else {
         try {
            this.checkBlockCollisions();
         } catch (Throwable var56) {
            CrashReport crashreport = CrashReport.a(var56, "Checking entity block collision");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");
            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
         }

         if (d0 == 0.0 && d1 == 0.0 && d2 == 0.0 && this.vehicle == null && this.passenger == null) {
            return;
         }

         double d3 = this.locX;
         double d4 = this.locY;
         double d5 = this.locZ;
         if (this.H) {
            this.H = false;
            d0 *= 0.25;
            d1 *= 0.05F;
            d2 *= 0.25;
            this.motX = 0.0;
            this.motY = 0.0;
            this.motZ = 0.0;
         }

         double d6 = d0;
         double d7 = d1;
         double d8 = d2;
         boolean flag = this.onGround && this.isSneaking() && this instanceof EntityHuman;
         if (flag) {
            double d9;
            for (d9 = 0.05; d0 != 0.0 && this.world.isEmpty(this, this.getBoundingBox().c(d0, -1.0, 0.0)); d6 = d0) {
               if (d0 < d9 && d0 >= -d9) {
                  d0 = 0.0;
               } else if (d0 > 0.0) {
                  d0 -= d9;
               } else {
                  d0 += d9;
               }
            }

            for (; d2 != 0.0 && this.world.isEmpty(this, this.getBoundingBox().c(0.0, -1.0, d2)); d8 = d2) {
               if (d2 < d9 && d2 >= -d9) {
                  d2 = 0.0;
               } else if (d2 > 0.0) {
                  d2 -= d9;
               } else {
                  d2 += d9;
               }
            }

            for (; d0 != 0.0 && d2 != 0.0 && this.world.isEmpty(this, this.getBoundingBox().c(d0, -1.0, d2)); d8 = d2) {
               if (d0 < d9 && d0 >= -d9) {
                  d0 = 0.0;
               } else if (d0 > 0.0) {
                  d0 -= d9;
               } else {
                  d0 += d9;
               }

               d6 = d0;
               if (d2 < d9 && d2 >= -d9) {
                  d2 = 0.0;
               } else if (d2 > 0.0) {
                  d2 -= d9;
               } else {
                  d2 += d9;
               }
            }
         }

         List<AxisAlignedBB> list = this.world.getCubes(this, this.getBoundingBox().a(d0, d1, d2));
         AxisAlignedBB axisalignedbb = this.getBoundingBox();

         for (AxisAlignedBB axisalignedbb2 : list) {
            d1 = axisalignedbb2.b(this.getBoundingBox(), d1);
         }

         this.a(this.getBoundingBox().c(0.0, d1, 0.0));
         boolean flag1 = this.onGround || d7 != d1 && !(d7 >= 0.0);

         for (AxisAlignedBB axisalignedbb2 : list) {
            d0 = axisalignedbb2.a(this.getBoundingBox(), d0);
         }

         this.a(this.getBoundingBox().c(d0, 0.0, 0.0));

         for (AxisAlignedBB axisalignedbb2 : list) {
            d2 = axisalignedbb2.c(this.getBoundingBox(), d2);
         }

         this.a(this.getBoundingBox().c(0.0, 0.0, d2));
         if (this.S > 0.0F && flag1 && (d6 != d0 || d8 != d2)) {
            double d10 = d0;
            double d11 = d1;
            double d12 = d2;
            AxisAlignedBB axisalignedbb3 = this.getBoundingBox();
            this.a(axisalignedbb);
            d1 = this.S;
            List<AxisAlignedBB> list1 = this.world.getCubes(this, this.getBoundingBox().a(d6, d1, d8));
            AxisAlignedBB axisalignedbb4 = this.getBoundingBox();
            AxisAlignedBB axisalignedbb5 = axisalignedbb4.a(d6, 0.0, d8);
            double d13 = d1;

            for (AxisAlignedBB axisalignedbb6 : list1) {
               d13 = axisalignedbb6.b(axisalignedbb5, d13);
            }

            axisalignedbb4 = axisalignedbb4.c(0.0, d13, 0.0);
            double d14 = d6;

            for (AxisAlignedBB axisalignedbb7 : list1) {
               d14 = axisalignedbb7.a(axisalignedbb4, d14);
            }

            axisalignedbb4 = axisalignedbb4.c(d14, 0.0, 0.0);
            double d15 = d8;

            for (AxisAlignedBB axisalignedbb8 : list1) {
               d15 = axisalignedbb8.c(axisalignedbb4, d15);
            }

            axisalignedbb4 = axisalignedbb4.c(0.0, 0.0, d15);
            AxisAlignedBB axisalignedbb9 = this.getBoundingBox();
            double d16 = d1;

            for (AxisAlignedBB axisalignedbb10 : list1) {
               d16 = axisalignedbb10.b(axisalignedbb9, d16);
            }

            axisalignedbb9 = axisalignedbb9.c(0.0, d16, 0.0);
            double d17 = d6;

            for (AxisAlignedBB axisalignedbb11 : list1) {
               d17 = axisalignedbb11.a(axisalignedbb9, d17);
            }

            axisalignedbb9 = axisalignedbb9.c(d17, 0.0, 0.0);
            double d18 = d8;

            for (AxisAlignedBB axisalignedbb12 : list1) {
               d18 = axisalignedbb12.c(axisalignedbb9, d18);
            }

            axisalignedbb9 = axisalignedbb9.c(0.0, 0.0, d18);
            if (d14 * d14 + d15 * d15 > d17 * d17 + d18 * d18) {
               d0 = d14;
               d2 = d15;
               d1 = -d13;
               this.a(axisalignedbb4);
            } else {
               d0 = d17;
               d2 = d18;
               d1 = -d16;
               this.a(axisalignedbb9);
            }

            for (AxisAlignedBB axisalignedbb13 : list1) {
               d1 = axisalignedbb13.b(this.getBoundingBox(), d1);
            }

            this.a(this.getBoundingBox().c(0.0, d1, 0.0));
            if (d10 * d10 + d12 * d12 >= d0 * d0 + d2 * d2) {
               d0 = d10;
               d1 = d11;
               d2 = d12;
               this.a(axisalignedbb3);
            }
         }

         this.recalcPosition();
         this.positionChanged = d6 != d0 || d8 != d2;
         this.E = d7 != d1;
         this.onGround = this.E && d7 < 0.0;
         this.F = this.positionChanged || this.E;
         BlockPosition blockposition = new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.locY - 0.2F), MathHelper.floor(this.locZ));
         Block block = this.world.getType(blockposition).getBlock();
         if (block.getMaterial() == Material.AIR) {
            Block block1 = this.world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
            if (block1 instanceof BlockFence || block1 instanceof BlockCobbleWall || block1 instanceof BlockFenceGate) {
               block = block1;
               blockposition.setY(blockposition.getY() - 1);
            }
         }

         this.a(d1, this.onGround, block, blockposition);
         if (d6 != d0) {
            this.motX = 0.0;
         }

         if (d8 != d2) {
            this.motZ = 0.0;
         }

         if (d7 != d1) {
            block.a(this.world, this);
         }

         if (this.positionChanged && this.getBukkitEntity() instanceof Vehicle) {
            org.bukkit.block.Block bl = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
            if (d6 > d0) {
               bl = bl.getRelative(BlockFace.EAST);
            } else if (d6 < d0) {
               bl = bl.getRelative(BlockFace.WEST);
            } else if (d8 > d2) {
               bl = bl.getRelative(BlockFace.SOUTH);
            } else if (d8 < d2) {
               bl = bl.getRelative(BlockFace.NORTH);
            }

            VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent((Vehicle)this.getBukkitEntity(), bl);
            this.world.getServer().getPluginManager().callEvent(event);
         }

         if (this.s_() && !flag && this.vehicle == null) {
            double d21 = this.locX - d3;
            double d22 = this.locY - d4;
            double d23 = this.locZ - d5;
            if (block != Blocks.LADDER) {
               d22 = 0.0;
            }

            this.M = (float)(this.M + MathHelper.sqrt(d21 * d21 + d23 * d23) * 0.6);
            this.N = (float)(this.N + MathHelper.sqrt(d21 * d21 + d22 * d22 + d23 * d23) * 0.6);
            if (this.N > this.h && block.getMaterial() != Material.AIR) {
               this.h = (int)this.N + 1;
               if (this.V()) {
                  float f = MathHelper.sqrt(this.motX * this.motX * 0.2F + this.motY * this.motY + this.motZ * this.motZ * 0.2F) * 0.35F;
                  if (f > 1.0F) {
                     f = 1.0F;
                  }

                  this.makeSound(this.P(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
               }

               this.a(blockposition, block);
               block.a(this.world, blockposition, this);
            }
         }

         boolean flag2 = this.U();
         if (this.world.e(this.getBoundingBox().shrink(0.001, 0.001, 0.001))) {
            this.burn(1.0F);
            if (!flag2) {
               this.fireTicks++;
               if (this.fireTicks <= 0) {
                  EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), 8);
                  this.world.getServer().getPluginManager().callEvent(event);
                  if (!event.isCancelled()) {
                     this.setOnFire(event.getDuration());
                  }
               } else {
                  this.setOnFire(8);
               }
            }
         } else if (this.fireTicks <= 0) {
            this.fireTicks = -this.maxFireTicks;
         }

         if (flag2 && this.fireTicks > 0) {
            this.makeSound("random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            this.fireTicks = -this.maxFireTicks;
         }
      }
   }

   protected void recalcPosition() {
      this.locX = (this.boundingBox.a + this.boundingBox.d) / 2.0;
      this.locY = this.boundingBox.b;
      this.locZ = (this.boundingBox.c + this.boundingBox.f) / 2.0;
   }

   protected String P() {
      return "game.neutral.swim";
   }

   protected void checkBlockCollisions() {
      int startX = MathHelper.floor(this.boundingBox.a + 0.001);
      int startY = Math.max(Math.min(MathHelper.floor(this.boundingBox.b + 0.001), 256), 0);
      int startZ = MathHelper.floor(this.boundingBox.c + 0.001);
      int endX = MathHelper.floor(this.boundingBox.d - 0.001);
      int endY = Math.max(Math.min(MathHelper.floor(this.boundingBox.e - 0.001), 256), 0);
      int endZ = MathHelper.floor(this.boundingBox.f - 0.001);
      double currentX = this.locX;
      double currentY = this.locY;
      double currentZ = this.locZ;
      World currentWorld = this.world;
      if (this.loadChunks || this.world.areChunksLoadedBetween(startX, startY, startZ, endX, endY, endZ)) {
         for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
               for (int z = startZ; z <= endZ; z++) {
                  int chunkX = x >> 4;
                  int chunkZ = z >> 4;
                  if (lastChunkX != chunkX || lastChunkZ != chunkZ || lastChunk == null || this.lastCollisionTick != MinecraftServer.currentTick) {
                     this.lastCollisionTick = MinecraftServer.currentTick;
                     lastChunkX = chunkX;
                     lastChunkZ = chunkZ;
                     lastChunk = this.loadChunks ? this.world.getChunkAt(lastChunkX, lastChunkZ) : this.world.getChunkIfLoaded(lastChunkX, lastChunkZ);
                  }

                  IBlockData iblockdata = lastChunk.getBlockData(x, y, z);

                  try {
                     iblockdata.getBlock().a(this.world, new BlockPosition(x, y, z), iblockdata, this);
                     if (currentWorld != this.world || currentX != this.locX || currentY != this.locY || currentZ != this.locZ) {
                        return;
                     }
                  } catch (Throwable var23) {
                     CrashReport crashreport = CrashReport.a(var23, "Colliding entity with block");
                     CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being collided with");
                     CrashReportSystemDetails.a(crashreportsystemdetails, null, iblockdata);
                     throw new ReportedException(crashreport);
                  }
               }
            }
         }
      }
   }

   protected void a(BlockPosition blockposition, Block block) {
      this.a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), block);
   }

   protected void a(int x, int y, int z, Block block) {
      if ((Boolean)BlazeConfig.footstepSounds.getValue()) {
         Block.StepSound block_stepsound = block.stepSound;
         if (this.world.getType(x, y + 1, z).getBlock() == Blocks.SNOW_LAYER) {
            block_stepsound = Blocks.SNOW_LAYER.stepSound;
            this.makeSound(block_stepsound.getStepSound(), block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
         } else if (!block.getMaterial().isLiquid()) {
            this.makeSound(block_stepsound.getStepSound(), block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
         }
      }
   }

   public void makeSound(String s, float f, float f1) {
      this.makeSound(s, f, f1, false);
   }

   public void makeSound(String s, float f, float f1, boolean self) {
      if (!this.R()) {
         this.world.makeSound(this, s, f, f1, self);
      }
   }

   public boolean R() {
      return this.datawatcher.getByte(4) == 1;
   }

   public void b(boolean flag) {
      this.datawatcher.watch(4, (byte)(flag ? 1 : 0));
   }

   protected boolean s_() {
      return true;
   }

   protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
      if (flag) {
         if (this.fallDistance > 0.0F) {
            if (block != null) {
               block.fallOn(this.world, blockposition, this, this.fallDistance);
            } else {
               this.e(this.fallDistance, 1.0F);
            }

            this.fallDistance = 0.0F;
         }
      } else if (d0 < 0.0) {
         this.fallDistance = (float)(this.fallDistance - d0);
      }
   }

   public AxisAlignedBB S() {
      return null;
   }

   protected void burn(float i) {
      if (!this.fireProof) {
         this.damageEntity(DamageSource.FIRE, i);
      }
   }

   public final boolean isFireProof() {
      return this.fireProof;
   }

   public void e(float f, float f1) {
      if (this.passenger != null) {
         this.passenger.e(f, f1);
      }
   }

   public boolean U() {
      int x = MathHelper.floor(this.locX);
      int y = MathHelper.floor(this.locY);
      int z = MathHelper.floor(this.locZ);
      return this.inWater || this.world.isRainingAt(x, y, z) || this.world.isRainingAt(x, MathHelper.floor(y + this.length), z);
   }

   public boolean V() {
      return this.inWater;
   }

   public boolean W() {
      if (this.world.a(this.boundingBox.grow(0.0, -0.4F, 0.0).shrink(0.001, 0.001, 0.001), Material.WATER, this)) {
         this.fallDistance = 0.0F;
         this.inWater = true;
         this.fireTicks = 0;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   protected void X() {
      float f = MathHelper.sqrt(this.motX * this.motX * 0.2F + this.motY * this.motY + this.motZ * this.motZ * 0.2F) * 0.2F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      this.makeSound(this.aa(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      float f1 = MathHelper.floor(this.boundingBox.b);

      for (int i = 0; i < 1.0F + this.width * 20.0F; i++) {
         float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
         float f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
         this.world
            .addParticle(
               EnumParticle.WATER_BUBBLE,
               this.locX + f2,
               f1 + 1.0F,
               this.locZ + f3,
               this.motX,
               this.motY - this.random.nextFloat() * 0.2F,
               this.motZ,
               EnumParticle.EMPTY_ARRAY
            );
      }

      for (int var6 = 0; var6 < 1.0F + this.width * 20.0F; var6++) {
         float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
         float f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
         this.world
            .addParticle(EnumParticle.WATER_SPLASH, this.locX + f2, f1 + 1.0F, this.locZ + f3, this.motX, this.motY, this.motZ, EnumParticle.EMPTY_ARRAY);
      }
   }

   public void Y() {
      if ((Boolean)BlazeConfig.footstepParticles.getValue() && this.isSprinting() && !this.V()) {
         this.Z();
      }
   }

   protected void Z() {
      int i = MathHelper.floor(this.locX);
      int j = MathHelper.floor(this.locY - 0.2F);
      int k = MathHelper.floor(this.locZ);
      IBlockData iblockdata = this.world.getType(i, j, k);
      Block block = iblockdata.getBlock();
      if (block.b() != -1) {
         this.world
            .addParticle(
               EnumParticle.BLOCK_CRACK,
               this.locX + (this.random.nextFloat() - 0.5) * this.width,
               this.boundingBox.b + 0.1,
               this.locZ + (this.random.nextFloat() - 0.5) * this.width,
               -this.motX * 4.0,
               1.5,
               -this.motZ * 4.0,
               Block.getCombinedId(iblockdata)
            );
      }
   }

   protected String aa() {
      return "game.neutral.swim.splash";
   }

   public boolean a(Material material) {
      double d0 = this.locY + this.getHeadHeight();
      IBlockData iblockdata = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(d0), MathHelper.floor(this.locZ));
      return iblockdata.getBlock().getMaterial() == material
         ? d0 < MathHelper.floor(d0) + 1 - (BlockFluids.b(iblockdata.getBlock().toLegacyData(iblockdata)) - 0.11111111F)
         : false;
   }

   public boolean ab() {
      return this.ab(this.world);
   }

   public boolean ab(IBlockAccess iblockaccess) {
      int currentTick = MinecraftServer.currentTick;
      if (this.lastLavaTick != currentTick) {
         this.lastLavaTick = currentTick;
         this.inLava = this.world.a(iblockaccess, this.getBoundingBox().grow(-0.1F, -0.4F, -0.1F), Material.LAVA);
      }

      return this.inLava;
   }

   public void a(float f, float f1, float f2) {
      float f3 = f * f + f1 * f1;
      if (f3 >= 1.0E-4F) {
         f3 = MathHelper.c(f3);
         if (f3 < 1.0F) {
            f3 = 1.0F;
         }

         f3 = f2 / f3;
         f *= f3;
         f1 *= f3;
         float f4 = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F);
         float f5 = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F);
         this.motX += f * f5 - f1 * f4;
         this.motZ += f1 * f5 + f * f4;
      }
   }

   public float c(float f) {
      int x = MathHelper.floor(this.locX);
      int y = MathHelper.floor(this.locY + this.getHeadHeight());
      int z = MathHelper.floor(this.locZ);
      return this.world.isLoaded(x, y, z) ? this.world.o(x, y, z) : 0.0F;
   }

   public void spawnIn(World world) {
      if (world == null) {
         this.die();
         this.world = ((CraftWorld)Bukkit.getServer().getWorlds().get(0)).getHandle();
      } else {
         this.world = world;
      }
   }

   public void setLocation(double d0, double d1, double d2, float f, float f1) {
      this.lastX = this.locX = d0;
      this.lastY = this.locY = d1;
      this.lastZ = this.locZ = d2;
      this.lastYaw = this.yaw = f;
      this.lastPitch = this.pitch = f1;
      double d3 = this.lastYaw - f;
      if (d3 < -180.0) {
         this.lastYaw += 360.0F;
      }

      if (d3 >= 180.0) {
         this.lastYaw -= 360.0F;
      }

      this.setPosition(this.locX, this.locY, this.locZ);
      this.setYawPitch(f, f1);
   }

   public void setPositionRotation(BlockPosition blockposition, float f, float f1) {
      this.setPositionRotation(blockposition.getX() + 0.5, blockposition.getY(), blockposition.getZ() + 0.5, f, f1);
   }

   public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
      this.P = this.lastX = this.locX = d0;
      this.Q = this.lastY = this.locY = d1;
      this.R = this.lastZ = this.locZ = d2;
      this.yaw = f;
      this.pitch = f1;
      this.setPosition(this.locX, this.locY, this.locZ);
   }

   public float g(Entity entity) {
      float f = (float)(this.locX - entity.locX);
      float f1 = (float)(this.locY - entity.locY);
      float f2 = (float)(this.locZ - entity.locZ);
      return MathHelper.c(f * f + f1 * f1 + f2 * f2);
   }

   public double e(double d0, double d1, double d2) {
      double d3 = this.locX - d0;
      double d4 = this.locY - d1;
      double d5 = this.locZ - d2;
      return d3 * d3 + d4 * d4 + d5 * d5;
   }

   public double b(BlockPosition blockposition) {
      return blockposition.c(this.locX, this.locY, this.locZ);
   }

   public double c(BlockPosition blockposition) {
      return blockposition.d(this.locX, this.locY, this.locZ);
   }

   public double f(double d0, double d1, double d2) {
      double d3 = this.locX - d0;
      double d4 = this.locY - d1;
      double d5 = this.locZ - d2;
      return MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
   }

   public double h(Entity entity) {
      double d0 = this.locX - entity.locX;
      double d1 = this.locY - entity.locY;
      double d2 = this.locZ - entity.locZ;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public void d(EntityHuman entityhuman) {
   }

   public void collide(Entity entity) {
      if (entity.passenger != this && entity.vehicle != this && !entity.noclip && !this.noclip) {
         double d0 = entity.locX - this.locX;
         double d1 = entity.locZ - this.locZ;
         double d2 = MathHelper.a(d0, d1);
         if (d2 >= 0.01F) {
            d2 = MathHelper.sqrt(d2);
            d0 /= d2;
            d1 /= d2;
            double d3 = 1.0 / d2;
            if (d3 > 1.0) {
               d3 = 1.0;
            }

            d0 *= d3;
            d1 *= d3;
            d0 *= 0.05F;
            d1 *= 0.05F;
            d0 *= 1.0F - this.U;
            d1 *= 1.0F - this.U;
            if (this.passenger == null) {
               this.g(-d0, 0.0, -d1);
            }

            if (entity.passenger == null) {
               entity.g(d0, 0.0, d1);
            }
         }
      }
   }

   public void g(double d0, double d1, double d2) {
      this.motX += d0;
      this.motY += d1;
      this.motZ += d2;
      this.ai = true;
   }

   protected void ac() {
      this.velocityChanged = true;
   }

   public boolean damageEntity(DamageSource damagesource, float f) {
      if (!this.isInvulnerable(damagesource)) {
         this.ac();
      }

      return false;
   }

   public Vec3D d(float f) {
      if (f == 1.0F) {
         return this.f(this.pitch, this.yaw);
      } else {
         float f1 = this.lastPitch + (this.pitch - this.lastPitch) * f;
         float f2 = this.lastYaw + (this.yaw - this.lastYaw) * f;
         return this.f(f1, f2);
      }
   }

   protected final Vec3D f(float f, float f1) {
      float f2 = MathHelper.cos(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = MathHelper.sin(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
      float f5 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
      return new Vec3D(f3 * f4, f5, f2 * f4);
   }

   public boolean ad() {
      return false;
   }

   public boolean ae() {
      return false;
   }

   public void b(Entity entity, int i) {
   }

   public boolean c(NBTTagCompound nbttagcompound) {
      String s = this.ag();
      if (!this.dead && s != null) {
         nbttagcompound.setString("id", s);
         this.e(nbttagcompound);
         return true;
      } else {
         return false;
      }
   }

   public boolean d(NBTTagCompound nbttagcompound) {
      String s = this.ag();
      if (!this.dead && s != null && this.passenger == null) {
         nbttagcompound.setString("id", s);
         this.e(nbttagcompound);
         return true;
      } else {
         return false;
      }
   }

   public void e(NBTTagCompound nbttagcompound) {
      try {
         nbttagcompound.set("Pos", this.a(this.locX, this.locY, this.locZ));
         nbttagcompound.set("Motion", this.a(this.motX, this.motY, this.motZ));
         nbttagcompound.set("Spawn", this.a(this.spawnX, this.spawnY, this.spawnZ));
         if (Float.isNaN(this.yaw)) {
            this.yaw = 0.0F;
         }

         if (Float.isNaN(this.pitch)) {
            this.pitch = 0.0F;
         }

         nbttagcompound.set("Rotation", this.a(this.yaw, this.pitch));
         nbttagcompound.setFloat("FallDistance", this.fallDistance);
         nbttagcompound.setShort("Fire", (short)this.fireTicks);
         nbttagcompound.setShort("Air", (short)this.getAirTicks());
         nbttagcompound.setBoolean("OnGround", this.onGround);
         nbttagcompound.setInt("Dimension", this.dimension);
         nbttagcompound.setBoolean("Invulnerable", this.invulnerable);
         nbttagcompound.setInt("PortalCooldown", this.portalCooldown);
         nbttagcompound.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
         nbttagcompound.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
         nbttagcompound.setLong("WorldUUIDLeast", this.world.getDataManager().getUUID().getLeastSignificantBits());
         nbttagcompound.setLong("WorldUUIDMost", this.world.getDataManager().getUUID().getMostSignificantBits());
         nbttagcompound.setInt("Bukkit.updateLevel", 2);
         nbttagcompound.setInt("Spigot.ticksLived", this.ticksLived);
         if (this.getCustomName() != null && this.getCustomName().length() > 0) {
            nbttagcompound.setString("CustomName", this.getCustomName());
            nbttagcompound.setBoolean("CustomNameVisible", this.getCustomNameVisible());
         }

         this.au.b(nbttagcompound);
         if (this.R()) {
            nbttagcompound.setBoolean("Silent", this.R());
         }

         this.b(nbttagcompound);
         if (this.vehicle != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            if (this.vehicle.c(nbttagcompound1)) {
               nbttagcompound.set("Riding", nbttagcompound1);
            }
         }

         if (this.spawnReason != null) {
            nbttagcompound.setString("Paper.SpawnReason", this.spawnReason.name());
         }
      } catch (Throwable var5) {
         CrashReport crashreport = CrashReport.a(var5, "Saving entity NBT");
         CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being saved");
         this.appendEntityCrashDetails(crashreportsystemdetails);
         throw new ReportedException(crashreport);
      }
   }

   public void f(NBTTagCompound nbttagcompound) {
      try {
         NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
         NBTTagList nbttaglist1 = nbttagcompound.getList("Motion", 6);
         NBTTagList nbttaglist2 = nbttagcompound.getList("Rotation", 5);
         NBTTagList nbttaglist3 = nbttagcompound.getList("Spawn", 6);
         this.spawnX = nbttaglist3.d(0);
         this.spawnY = nbttaglist3.d(1);
         this.spawnZ = nbttaglist3.d(2);
         this.motX = nbttaglist1.d(0);
         this.motY = nbttaglist1.d(1);
         this.motZ = nbttaglist1.d(2);
         this.lastX = this.P = this.locX = nbttaglist.d(0);
         this.lastY = this.Q = this.locY = nbttaglist.d(1);
         this.lastZ = this.R = this.locZ = nbttaglist.d(2);
         this.lastYaw = this.yaw = nbttaglist2.e(0);
         this.lastPitch = this.pitch = nbttaglist2.e(1);
         this.f(this.yaw);
         this.g(this.yaw);
         this.fallDistance = nbttagcompound.getFloat("FallDistance");
         this.fireTicks = nbttagcompound.getShort("Fire");
         this.setAirTicks(nbttagcompound.getShort("Air"));
         this.onGround = nbttagcompound.getBoolean("OnGround");
         this.dimension = nbttagcompound.getInt("Dimension");
         this.invulnerable = nbttagcompound.getBoolean("Invulnerable");
         this.portalCooldown = nbttagcompound.getInt("PortalCooldown");
         if (nbttagcompound.hasKeyOfType("UUIDMost", 4) && nbttagcompound.hasKeyOfType("UUIDLeast", 4)) {
            this.uniqueID = new UUID(nbttagcompound.getLong("UUIDMost"), nbttagcompound.getLong("UUIDLeast"));
         } else if (nbttagcompound.hasKeyOfType("UUID", 8)) {
            this.uniqueID = FastUUID.parseUUID(nbttagcompound.getString("UUID"));
         }

         this.setPosition(this.locX, this.locY, this.locZ);
         this.setYawPitch(this.yaw, this.pitch);
         if (nbttagcompound.hasKeyOfType("CustomName", 8) && nbttagcompound.getString("CustomName").length() > 0) {
            this.setCustomName(nbttagcompound.getString("CustomName"));
         }

         this.setCustomNameVisible(nbttagcompound.getBoolean("CustomNameVisible"));
         this.au.a(nbttagcompound);
         this.b(nbttagcompound.getBoolean("Silent"));
         this.a(nbttagcompound);
         if (this.af()) {
            this.setPosition(this.locX, this.locY, this.locZ);
         }

         if (this instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving)this;
            this.ticksLived = nbttagcompound.getInt("Spigot.ticksLived");
            if (entity instanceof EntityTameableAnimal && !isLevelAtLeast(nbttagcompound, 2) && !nbttagcompound.getBoolean("PersistenceRequired")) {
               EntityInsentient entityinsentient = (EntityInsentient)entity;
               entityinsentient.persistent = !entityinsentient.isTypeNotPersistent();
            }
         }

         if (!(this.getBukkitEntity() instanceof Vehicle)) {
            if (Math.abs(this.motX) > 10.0) {
               this.motX = 0.0;
            }

            if (Math.abs(this.motY) > 10.0) {
               this.motY = 0.0;
            }

            if (Math.abs(this.motZ) > 10.0) {
               this.motZ = 0.0;
            }
         }

         if (this instanceof EntityPlayer) {
            Server server = Bukkit.getServer();
            String worldName = nbttagcompound.getString("world");
            org.bukkit.World bworld;
            if (nbttagcompound.hasKey("WorldUUIDMost") && nbttagcompound.hasKey("WorldUUIDLeast")) {
               UUID uid = new UUID(nbttagcompound.getLong("WorldUUIDMost"), nbttagcompound.getLong("WorldUUIDLeast"));
               bworld = server.getWorld(uid);
            } else {
               bworld = server.getWorld(worldName);
            }

            if (bworld == null) {
               EntityPlayer entityPlayer = (EntityPlayer)this;
               bworld = ((CraftServer)server).getServer().getWorldServer(entityPlayer.dimension).getWorld();
            }

            this.spawnIn(bworld == null ? null : ((CraftWorld)bworld).getHandle());
         }

         if (nbttagcompound.hasKey("Paper.SpawnReason")) {
            String spawnReasonName = nbttagcompound.getString("Paper.SpawnReason");

            try {
               this.spawnReason = SpawnReason.valueOf(spawnReasonName);
            } catch (Exception var10) {
               LogManager.getLogger().error("Unknown SpawnReason {} for {}", new Object[]{spawnReasonName, this});
            }
         }

         if (this.spawnReason == null
            && this instanceof EntityInsentient
            && this instanceof IAnimal
            && !((EntityInsentient)this).isTypeNotPersistent()
            && !nbttagcompound.getBoolean("PersistenceRequired")) {
            this.spawnReason = SpawnReason.NATURAL;
         }

         if (this.spawnReason == null) {
            this.spawnReason = SpawnReason.DEFAULT;
         }
      } catch (Throwable var11) {
         CrashReport crashreport = CrashReport.a(var11, "Loading entity NBT");
         CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being loaded");
         this.appendEntityCrashDetails(crashreportsystemdetails);
         throw new ReportedException(crashreport);
      }
   }

   protected boolean af() {
      return true;
   }

   protected final String ag() {
      return EntityTypes.b(this);
   }

   protected abstract void a(NBTTagCompound var1);

   protected abstract void b(NBTTagCompound var1);

   public void ah() {
   }

   protected NBTTagList a(double... adouble) {
      NBTTagList nbttaglist = new NBTTagList();

      for (double d0 : adouble) {
         nbttaglist.add(new NBTTagDouble(d0));
      }

      return nbttaglist;
   }

   protected NBTTagList a(float... afloat) {
      NBTTagList nbttaglist = new NBTTagList();

      for (float d0 : afloat) {
         nbttaglist.add(new NBTTagFloat(d0));
      }

      return nbttaglist;
   }

   public EntityItem a(Item item, int i) {
      return this.a(item, i, 0.0F);
   }

   public EntityItem a(Item item, int i, float f) {
      return this.a(new ItemStack(item, i, 0), f);
   }

   public EntityItem a(ItemStack itemstack, float f) {
      if (itemstack.count == 0 || itemstack.getItem() == null) {
         return null;
      } else if (this instanceof EntityLiving && ((EntityLiving)this).drops != null) {
         ((EntityLiving)this).drops.add(CraftItemStack.asBukkitCopy(itemstack));
         return null;
      } else {
         EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY + f, this.locZ, itemstack);
         entityitem.p();
         this.world.addEntity(entityitem);
         return entityitem;
      }
   }

   public boolean isAlive() {
      return !this.dead;
   }

   public boolean inBlock() {
      if (!this.noclip) {
         BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(
            Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE
         );

         for (int i = 0; i < 8; i++) {
            int j = MathHelper.floor(this.locY + (i % 2 - 0.5F) * 0.1F + this.getHeadHeight());
            int k = MathHelper.floor(this.locX + ((i >> 1) % 2 - 0.5F) * this.width * 0.8F);
            int l = MathHelper.floor(this.locZ + ((i >> 2) % 2 - 0.5F) * this.width * 0.8F);
            if (blockposition_mutableblockposition.getX() != k
               || blockposition_mutableblockposition.getY() != j
               || blockposition_mutableblockposition.getZ() != l) {
               blockposition_mutableblockposition.c(k, j, l);
               if (this.world.getType(blockposition_mutableblockposition).getBlock().w()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean e(EntityHuman entityhuman) {
      return false;
   }

   public AxisAlignedBB j(Entity entity) {
      return null;
   }

   public void ak() {
      if (this.vehicle.dead) {
         this.vehicle = null;
      } else {
         this.motX = 0.0;
         this.motY = 0.0;
         this.motZ = 0.0;
         this.t_();
         if (this.vehicle != null) {
            this.vehicle.al();
            this.as = this.as + (this.vehicle.yaw - this.vehicle.lastYaw);
            this.ar = this.ar + (this.vehicle.pitch - this.vehicle.lastPitch);

            while (this.as >= 180.0) {
               this.as -= 360.0;
            }

            while (this.as < -180.0) {
               this.as += 360.0;
            }

            while (this.ar >= 180.0) {
               this.ar -= 360.0;
            }

            while (this.ar < -180.0) {
               this.ar += 360.0;
            }

            double d0 = this.as * 0.5;
            double d1 = this.ar * 0.5;
            float f = 10.0F;
            if (d0 > f) {
               d0 = f;
            }

            if (d0 < -f) {
               d0 = -f;
            }

            if (d1 > f) {
               d1 = f;
            }

            if (d1 < -f) {
               d1 = -f;
            }

            this.as -= d0;
            this.ar -= d1;
         }
      }
   }

   public void al() {
      if (this.passenger != null) {
         this.passenger.setPosition(this.locX, this.locY + this.an() + this.passenger.am(), this.locZ);
      }
   }

   public double am() {
      return 0.0;
   }

   public double an() {
      return this.length * 0.75;
   }

   public CraftEntity getBukkitEntity() {
      if (this.bukkitEntity == null) {
         this.bukkitEntity = CraftEntity.getEntity(this.world.getServer(), this);
      }

      return this.bukkitEntity;
   }

   public void mount(Entity entity) {
      Entity originalVehicle = this.vehicle;
      Entity originalPassenger = this.vehicle == null ? null : this.vehicle.passenger;
      PluginManager pluginManager = Bukkit.getPluginManager();
      this.getBukkitEntity();
      this.ar = 0.0;
      this.as = 0.0;
      if (entity == null) {
         if (this.vehicle != null) {
            if (this.bukkitEntity instanceof LivingEntity && this.vehicle.getBukkitEntity() instanceof Vehicle) {
               VehicleExitEvent event = new VehicleExitEvent((Vehicle)this.vehicle.getBukkitEntity(), (LivingEntity)this.bukkitEntity);
               pluginManager.callEvent(event);
               if (event.isCancelled() || this.vehicle != originalVehicle) {
                  return;
               }
            }

            EntityDismountEvent dismountEvent = new EntityDismountEvent(this.getBukkitEntity(), this.vehicle.getBukkitEntity());
            pluginManager.callEvent(dismountEvent);
            if (dismountEvent.isCancelled()) {
               return;
            }

            this.setPositionRotation(this.vehicle.locX, this.vehicle.boundingBox.b + this.vehicle.length, this.vehicle.locZ, this.yaw, this.pitch);
            this.vehicle.passenger = null;
         }

         this.vehicle = null;
      } else {
         if (this.bukkitEntity instanceof LivingEntity
            && entity.getBukkitEntity() instanceof Vehicle
            && entity.world.isChunkLoaded(MathHelper.floor(entity.locX) >> 4, MathHelper.floor(entity.locZ) >> 4, true)) {
            VehicleExitEvent exitEvent = null;
            if (this.vehicle != null && this.vehicle.getBukkitEntity() instanceof Vehicle) {
               exitEvent = new VehicleExitEvent((Vehicle)this.vehicle.getBukkitEntity(), (LivingEntity)this.bukkitEntity);
               pluginManager.callEvent(exitEvent);
               if (exitEvent.isCancelled() || this.vehicle != originalVehicle || this.vehicle != null && this.vehicle.passenger != originalPassenger) {
                  return;
               }
            }

            VehicleEnterEvent event = new VehicleEnterEvent((Vehicle)entity.getBukkitEntity(), this.bukkitEntity);
            pluginManager.callEvent(event);
            if (event.isCancelled() || this.vehicle != originalVehicle || this.vehicle != null && this.vehicle.passenger != originalPassenger) {
               if (exitEvent != null && this.vehicle == originalVehicle && this.vehicle != null && this.vehicle.passenger == originalPassenger) {
                  this.setPositionRotation(this.vehicle.locX, this.vehicle.boundingBox.b + this.vehicle.length, this.vehicle.locZ, this.yaw, this.pitch);
                  this.vehicle.passenger = null;
                  this.vehicle = null;
               }

               return;
            }
         }

         if (entity.world.isChunkLoaded(MathHelper.floor(entity.locX) >> 4, MathHelper.floor(entity.locZ) >> 4, true)) {
            EntityMountEvent event = new EntityMountEvent(this.getBukkitEntity(), entity.getBukkitEntity());
            pluginManager.callEvent(event);
            if (event.isCancelled()) {
               return;
            }
         }

         if (this.vehicle != null) {
            this.vehicle.passenger = null;
         }

         for (Entity entity1 = entity.vehicle; entity1 != null; entity1 = entity1.vehicle) {
            if (entity1 == this) {
               return;
            }
         }

         this.vehicle = entity;
         entity.passenger = this;
      }
   }

   public float ao() {
      return 0.1F;
   }

   public Vec3D ap() {
      return null;
   }

   public void d(BlockPosition blockposition) {
      if (this.portalCooldown > 0) {
         this.portalCooldown = this.aq();
      } else {
         if (!blockposition.equals(this.an)) {
            this.an = blockposition;
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = Blocks.PORTAL.f(this.world, blockposition);
            double d0 = shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X
               ? shapedetector_shapedetectorcollection.a().getZ()
               : shapedetector_shapedetectorcollection.a().getX();
            double d1 = shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X ? this.locZ : this.locX;
            d1 = Math.abs(
               MathHelper.c(
                  d1 - (shapedetector_shapedetectorcollection.b().e().c() == EnumDirection.EnumAxisDirection.NEGATIVE ? 1 : 0),
                  d0,
                  d0 - shapedetector_shapedetectorcollection.d()
               )
            );
            double d2 = MathHelper.c(
               this.locY - 1.0,
               shapedetector_shapedetectorcollection.a().getY(),
               shapedetector_shapedetectorcollection.a().getY() - shapedetector_shapedetectorcollection.e()
            );
            this.ao = new Vec3D(d1, d2, 0.0);
            this.ap = shapedetector_shapedetectorcollection.b();
         }

         this.ak = true;
      }
   }

   public int aq() {
      return 300;
   }

   public ItemStack[] getEquipment() {
      return null;
   }

   public void setEquipment(int i, ItemStack itemstack) {
   }

   public boolean isBurning() {
      return !this.fireProof && this.fireTicks > 0;
   }

   public boolean au() {
      return this.vehicle != null;
   }

   public boolean isSneaking() {
      return this.g(1);
   }

   public void setSneaking(boolean flag) {
      this.b(1, flag);
   }

   public boolean isSprinting() {
      return this.g(3);
   }

   public void setSprinting(boolean flag) {
      this.b(3, flag);
   }

   public boolean isInvisible() {
      return this.g(5);
   }

   public void setInvisible(boolean flag) {
      this.b(5, flag);
   }

   public void f(boolean flag) {
      this.b(4, flag);
   }

   protected boolean g(int i) {
      return (this.datawatcher.getByte(0) & 1 << i) != 0;
   }

   protected void b(int i, boolean flag) {
      byte b0 = this.datawatcher.getByte(0);
      if (flag) {
         this.datawatcher.watch(0, (byte)(b0 | 1 << i));
      } else {
         this.datawatcher.watch(0, (byte)(b0 & ~(1 << i)));
      }
   }

   public int getAirTicks() {
      return this.datawatcher.getShort(1);
   }

   public void setAirTicks(int i) {
      this.datawatcher.watch(1, (short)i);
   }

   public void onLightningStrike(EntityLightning entitylightning) {
      org.bukkit.entity.Entity thisBukkitEntity = this.getBukkitEntity();
      org.bukkit.entity.Entity stormBukkitEntity = entitylightning.getBukkitEntity();
      PluginManager pluginManager = Bukkit.getPluginManager();
      if (thisBukkitEntity instanceof Hanging) {
         HangingBreakByEntityEvent hangingEvent = new HangingBreakByEntityEvent((Hanging)thisBukkitEntity, stormBukkitEntity);
         PaintingBreakByEntityEvent paintingEvent = null;
         if (thisBukkitEntity instanceof Painting) {
            paintingEvent = new PaintingBreakByEntityEvent((Painting)thisBukkitEntity, stormBukkitEntity);
         }

         pluginManager.callEvent(hangingEvent);
         if (paintingEvent != null) {
            paintingEvent.setCancelled(hangingEvent.isCancelled());
            pluginManager.callEvent(paintingEvent);
         }

         if (hangingEvent.isCancelled() || paintingEvent != null && paintingEvent.isCancelled()) {
            return;
         }
      }

      if (!this.fireProof) {
         CraftEventFactory.entityDamage = entitylightning;
         if (!this.damageEntity(DamageSource.LIGHTNING, 5.0F)) {
            CraftEventFactory.entityDamage = null;
         } else {
            this.fireTicks++;
            if (this.fireTicks == 0) {
               EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(stormBukkitEntity, thisBukkitEntity, 8);
               pluginManager.callEvent(entityCombustEvent);
               if (!entityCombustEvent.isCancelled()) {
                  this.setOnFire(entityCombustEvent.getDuration());
               }
            }
         }
      }
   }

   public void a(EntityLiving entityliving) {
   }

   protected boolean j(double d0, double d1, double d2) {
      BlockPosition blockposition = new BlockPosition(d0, d1, d2);
      double d3 = d0 - blockposition.getX();
      double d4 = d1 - blockposition.getY();
      double d5 = d2 - blockposition.getZ();
      List<AxisAlignedBB> list = this.world.a(this.boundingBox);
      if (list.isEmpty() && !this.world.u(blockposition)) {
         return false;
      } else {
         byte b0 = 3;
         double d6 = 9999.0;
         if (!this.world.u(blockposition.west()) && d3 < d6) {
            d6 = d3;
            b0 = 0;
         }

         if (!this.world.u(blockposition.east()) && 1.0 - d3 < d6) {
            d6 = 1.0 - d3;
            b0 = 1;
         }

         if (!this.world.u(blockposition.up()) && 1.0 - d4 < d6) {
            d6 = 1.0 - d4;
            b0 = 3;
         }

         if (!this.world.u(blockposition.north()) && d5 < d6) {
            d6 = d5;
            b0 = 4;
         }

         if (!this.world.u(blockposition.south()) && 1.0 - d5 < d6) {
            b0 = 5;
         }

         float f = this.random.nextFloat() * 0.2F + 0.1F;
         if (b0 == 0) {
            this.motX = -f;
         }

         if (b0 == 1) {
            this.motX = f;
         }

         if (b0 == 3) {
            this.motY = f;
         }

         if (b0 == 4) {
            this.motZ = -f;
         }

         if (b0 == 5) {
            this.motZ = f;
         }

         return true;
      }
   }

   public void aA() {
      this.H = true;
      this.fallDistance = 0.0F;
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomName();
      } else {
         String s = EntityTypes.b(this);
         if (s == null) {
            s = "generic";
         }

         return LocaleI18n.get("entity." + s + ".name");
      }
   }

   public Entity[] aB() {
      return null;
   }

   public boolean k(Entity entity) {
      return this == entity;
   }

   public float getHeadRotation() {
      return 0.0F;
   }

   public void f(float f) {
   }

   public void g(float f) {
   }

   public boolean aD() {
      return true;
   }

   public boolean l(Entity entity) {
      return false;
   }

   @Override
   public String toString() {
      return String.format(
         "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
         this.getClass().getSimpleName(),
         this.getName(),
         this.id,
         this.world == null ? "~NULL~" : this.world.getWorldData().getName(),
         this.locX,
         this.locY,
         this.locZ
      );
   }

   public boolean isInvulnerable(DamageSource damagesource) {
      return this.invulnerable && damagesource != DamageSource.OUT_OF_WORLD && !damagesource.u();
   }

   public void m(Entity entity) {
      this.setPositionRotation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
   }

   public void n(Entity entity) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      entity.e(nbttagcompound);
      this.f(nbttagcompound);
      this.portalCooldown = entity.portalCooldown;
      this.an = entity.an;
      this.ao = entity.ao;
      this.ap = entity.ap;
   }

   public void c(int i) {
      if (!this.dead) {
         MinecraftServer minecraftserver = MinecraftServer.getServer();
         WorldServer exitWorld = null;
         if (this.dimension < 10) {
            for (WorldServer world : minecraftserver.worlds) {
               if (world.dimension == i) {
                  exitWorld = world;
               }
            }
         }

         Location enter = this.getBukkitEntity().getLocation();
         Location exit = exitWorld != null ? minecraftserver.getPlayerList().calculateTarget(enter, minecraftserver.getWorldServer(i)) : null;
         boolean useTravelAgent = exitWorld != null && (this.dimension != 1 || exitWorld.dimension != 1);
         TravelAgent agent = exit != null ? (TravelAgent)((CraftWorld)exit.getWorld()).getHandle().getTravelAgent() : CraftTravelAgent.DEFAULT;
         EntityPortalEvent event = new EntityPortalEvent(this.getBukkitEntity(), enter, exit, agent);
         event.useTravelAgent(useTravelAgent);
         event.getEntity().getServer().getPluginManager().callEvent(event);
         if (event.isCancelled() || event.getTo() == null || event.getTo().getWorld() == null || !this.isAlive()) {
            return;
         }

         exit = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
         this.teleportTo(exit, true);
      }
   }

   public void teleportTo(Location exit, boolean portal) {
      WorldServer worldserver1 = ((CraftWorld)exit.getWorld()).getHandle();
      this.dimension = worldserver1.dimension;
      this.world.kill(this);
      this.dead = false;
      boolean before = worldserver1.chunkProviderServer.forceChunkLoad;
      worldserver1.chunkProviderServer.forceChunkLoad = true;
      worldserver1.getMinecraftServer().getPlayerList().repositionEntity(this, exit, portal);
      worldserver1.chunkProviderServer.forceChunkLoad = before;
      Entity entity = EntityTypes.createEntityByName(EntityTypes.b(this), worldserver1);
      if (entity != null) {
         entity.n(this);
         worldserver1.addEntity(entity);
         this.getBukkitEntity().setHandle(entity);
         entity.bukkitEntity = this.getBukkitEntity();
         if (this instanceof EntityInsentient) {
            ((EntityInsentient)this).unleash(true, false);
         }
      }

      this.dead = true;
   }

   public float a(Explosion explosion, World world, BlockPosition blockposition, IBlockData iblockdata) {
      return iblockdata.getBlock().a(this);
   }

   public boolean a(Explosion explosion, World world, BlockPosition blockposition, IBlockData iblockdata, float f) {
      return true;
   }

   public int aE() {
      return 3;
   }

   public Vec3D aG() {
      return this.ao;
   }

   public EnumDirection aH() {
      return this.ap;
   }

   public boolean aI() {
      return false;
   }

   public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
      crashreportsystemdetails.a("Entity Type", () -> EntityTypes.b(this) + " (" + this.getClass().getCanonicalName() + ")");
      crashreportsystemdetails.a("Entity ID", this.id);
      crashreportsystemdetails.a("Entity Name", this::getName);
      crashreportsystemdetails.a("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.locX, this.locY, this.locZ));
      crashreportsystemdetails.a(
         "Entity's Block location", CrashReportSystemDetails.a(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))
      );
      crashreportsystemdetails.a("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.motX, this.motY, this.motZ));
      crashreportsystemdetails.a("Entity's Rider", () -> this.passenger.toString());
      crashreportsystemdetails.a("Entity's Vehicle", () -> this.vehicle.toString());
   }

   public UUID getUniqueID() {
      return this.uniqueID;
   }

   public boolean aL() {
      return true;
   }

   @Override
   public IChatBaseComponent getScoreboardDisplayName() {
      ChatComponentText chatcomponenttext = new ChatComponentText(this.getName());
      chatcomponenttext.getChatModifier().setChatHoverable(this.aQ());
      chatcomponenttext.getChatModifier().setInsertion(this.getUniqueID().toString());
      return chatcomponenttext;
   }

   public String getCustomName() {
      return this.datawatcher.getString(2);
   }

   public void setCustomName(String s) {
      if (s.length() > 256) {
         s = s.substring(0, 256);
      }

      this.datawatcher.watch(2, s);
   }

   public boolean hasCustomName() {
      return this.datawatcher.getString(2).length() > 0;
   }

   public boolean getCustomNameVisible() {
      return this.datawatcher.getByte(3) == 1;
   }

   public void setCustomNameVisible(boolean flag) {
      this.datawatcher.watch(3, (byte)(flag ? 1 : 0));
   }

   public void enderTeleportTo(double d0, double d1, double d2) {
      this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
   }

   public void i(int i) {
   }

   public EnumDirection getDirection() {
      return EnumDirection.fromType2(MathHelper.floor(this.yaw * 4.0F / 360.0F + 0.5) & 3);
   }

   protected ChatHoverable aQ() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      String s = EntityTypes.b(this);
      nbttagcompound.setString("id", this.getUniqueID().toString());
      if (s != null) {
         nbttagcompound.setString("type", s);
      }

      nbttagcompound.setString("name", this.getName());
      return new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_ENTITY, new ChatComponentText(nbttagcompound.toString()));
   }

   public boolean a(EntityPlayer entityplayer) {
      return true;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public void a(AxisAlignedBB axisalignedbb) {
      double a = axisalignedbb.a;
      double b = axisalignedbb.b;
      double c = axisalignedbb.c;
      double d = axisalignedbb.d;
      double e = axisalignedbb.e;
      double f = axisalignedbb.f;
      double len = axisalignedbb.d - axisalignedbb.a;
      if (len < 0.0) {
         d = a;
      }

      if (len > 64.0) {
         d = a + 64.0;
      }

      len = axisalignedbb.e - axisalignedbb.b;
      if (len < 0.0) {
         e = b;
      }

      if (len > 64.0) {
         e = b + 64.0;
      }

      len = axisalignedbb.f - axisalignedbb.c;
      if (len < 0.0) {
         f = c;
      }

      if (len > 64.0) {
         f = c + 64.0;
      }

      this.boundingBox = new AxisAlignedBB(a, b, c, d, e, f);
   }

   public float getHeadHeight() {
      return this.length * 0.85F;
   }

   public boolean aT() {
      return this.g;
   }

   public void h(boolean flag) {
      this.g = flag;
   }

   public boolean d(int i, ItemStack itemstack) {
      return false;
   }

   @Override
   public void sendMessage(IChatBaseComponent ichatbasecomponent) {
   }

   public EntityTrackerEntry getEntry(int b, int c, boolean flag) {
      return new EntityTrackerEntry(this, b, c, flag);
   }

   @Override
   public boolean a(int i, String s) {
      return true;
   }

   @Override
   public BlockPosition getChunkCoordinates() {
      return new BlockPosition(this.locX, this.locY + 0.5, this.locZ);
   }

   @Override
   public Vec3D d() {
      return new Vec3D(this.locX, this.locY, this.locZ);
   }

   @Override
   public World getWorld() {
      return this.world;
   }

   @Override
   public Entity f() {
      return this;
   }

   @Override
   public boolean getSendCommandFeedback() {
      return false;
   }

   @Override
   public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
      this.au.a(this, commandobjectiveexecutor_enumcommandresult, i);
   }

   public CommandObjectiveExecutor aU() {
      return this.au;
   }

   public void o(Entity entity) {
      this.au.a(entity.aU());
   }

   public NBTTagCompound getNBTTag() {
      return null;
   }

   public boolean a(EntityHuman entityhuman, Vec3D vec3d) {
      return false;
   }

   public boolean aW() {
      return false;
   }

   public double getX() {
      return this.locX;
   }

   public double getY() {
      return this.locY;
   }

   public double getZ() {
      return this.locZ;
   }

   protected void a(EntityLiving entityliving, Entity entity) {
      if (entity instanceof EntityLiving) {
         EnchantmentManager.a((EntityLiving)entity, entityliving);
      }

      EnchantmentManager.b(entityliving, entity);
   }
}
