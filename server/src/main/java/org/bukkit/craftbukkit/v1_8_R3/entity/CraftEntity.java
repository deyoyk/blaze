package org.bukkit.craftbukkit.v1_8_R3.entity;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityAmbient;
import net.minecraft.server.v1_8_R3.EntityAnimal;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.EntityBlaze;
import net.minecraft.server.v1_8_R3.EntityBoat;
import net.minecraft.server.v1_8_R3.EntityCaveSpider;
import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.EntityComplexPart;
import net.minecraft.server.v1_8_R3.EntityCow;
import net.minecraft.server.v1_8_R3.EntityCreature;
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
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityFlying;
import net.minecraft.server.v1_8_R3.EntityGhast;
import net.minecraft.server.v1_8_R3.EntityGiantZombie;
import net.minecraft.server.v1_8_R3.EntityGolem;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.EntityHanging;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.EntityLargeFireball;
import net.minecraft.server.v1_8_R3.EntityLeash;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.EntityMinecartAbstract;
import net.minecraft.server.v1_8_R3.EntityMinecartChest;
import net.minecraft.server.v1_8_R3.EntityMinecartCommandBlock;
import net.minecraft.server.v1_8_R3.EntityMinecartFurnace;
import net.minecraft.server.v1_8_R3.EntityMinecartHopper;
import net.minecraft.server.v1_8_R3.EntityMinecartMobSpawner;
import net.minecraft.server.v1_8_R3.EntityMinecartRideable;
import net.minecraft.server.v1_8_R3.EntityMinecartTNT;
import net.minecraft.server.v1_8_R3.EntityMonster;
import net.minecraft.server.v1_8_R3.EntityMushroomCow;
import net.minecraft.server.v1_8_R3.EntityOcelot;
import net.minecraft.server.v1_8_R3.EntityPainting;
import net.minecraft.server.v1_8_R3.EntityPig;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityPotion;
import net.minecraft.server.v1_8_R3.EntityProjectile;
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
import net.minecraft.server.v1_8_R3.EntityTameableAnimal;
import net.minecraft.server.v1_8_R3.EntityThrownExpBottle;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.EntityWaterAnimal;
import net.minecraft.server.v1_8_R3.EntityWeather;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.EntityWitherSkull;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EntityZombie;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.github.paperspigot.PaperSpigotConfig;

public abstract class CraftEntity implements org.bukkit.entity.Entity {
   private static final PermissibleBase perm = new PermissibleBase(new ServerOperator() {
      @Override
      public boolean isOp() {
         return false;
      }

      @Override
      public void setOp(boolean value) {
      }
   });
   protected final CraftServer server;
   protected Entity entity;
   private EntityDamageEvent lastDamageEvent;
   private final org.bukkit.entity.Entity.Spigot spigot = new org.bukkit.entity.Entity.Spigot() {
      @Override
      public boolean isInvulnerable() {
         return CraftEntity.this.getHandle().isInvulnerable(DamageSource.GENERIC);
      }
   };

   public CraftEntity(CraftServer server, Entity entity) {
      this.server = server;
      this.entity = entity;
   }

   public static CraftEntity getEntity(CraftServer server, Entity entity) {
      if (entity instanceof EntityTNTPrimed) {
         return new CraftTNTPrimed(server, (EntityTNTPrimed)entity);
      } else if (entity instanceof EntityFallingBlock) {
         return new CraftFallingSand(server, (EntityFallingBlock)entity);
      } else {
         if (entity instanceof EntityLiving) {
            if (entity instanceof EntityHuman) {
               if (entity instanceof EntityPlayer) {
                  return new CraftPlayer(server, (EntityPlayer)entity);
               }

               return new CraftHumanEntity(server, (EntityHuman)entity);
            }

            if (entity instanceof EntityWaterAnimal) {
               if (entity instanceof EntitySquid) {
                  return new CraftSquid(server, (EntitySquid)entity);
               }

               return new CraftWaterMob(server, (EntityWaterAnimal)entity);
            }

            if (!(entity instanceof EntityCreature)) {
               if (entity instanceof EntitySlime) {
                  if (entity instanceof EntityMagmaCube) {
                     return new CraftMagmaCube(server, (EntityMagmaCube)entity);
                  }

                  return new CraftSlime(server, (EntitySlime)entity);
               }

               if (entity instanceof EntityFlying) {
                  if (entity instanceof EntityGhast) {
                     return new CraftGhast(server, (EntityGhast)entity);
                  }

                  return new CraftFlying(server, (EntityFlying)entity);
               }

               if (entity instanceof EntityEnderDragon) {
                  return new CraftEnderDragon(server, (EntityEnderDragon)entity);
               }

               if (entity instanceof EntityAmbient) {
                  if (entity instanceof EntityBat) {
                     return new CraftBat(server, (EntityBat)entity);
                  }

                  return new CraftAmbient(server, (EntityAmbient)entity);
               }

               if (entity instanceof EntityArmorStand) {
                  return new CraftArmorStand(server, (EntityArmorStand)entity);
               }

               return new CraftLivingEntity(server, (EntityLiving)entity);
            }

            if (entity instanceof EntityAnimal) {
               if (entity instanceof EntityChicken) {
                  return new CraftChicken(server, (EntityChicken)entity);
               }

               if (entity instanceof EntityCow) {
                  if (entity instanceof EntityMushroomCow) {
                     return new CraftMushroomCow(server, (EntityMushroomCow)entity);
                  }

                  return new CraftCow(server, (EntityCow)entity);
               }

               if (entity instanceof EntityPig) {
                  return new CraftPig(server, (EntityPig)entity);
               }

               if (!(entity instanceof EntityTameableAnimal)) {
                  if (entity instanceof EntitySheep) {
                     return new CraftSheep(server, (EntitySheep)entity);
                  }

                  if (entity instanceof EntityHorse) {
                     return new CraftHorse(server, (EntityHorse)entity);
                  }

                  if (entity instanceof EntityRabbit) {
                     return new CraftRabbit(server, (EntityRabbit)entity);
                  }

                  return new CraftAnimals(server, (EntityAnimal)entity);
               }

               if (entity instanceof EntityWolf) {
                  return new CraftWolf(server, (EntityWolf)entity);
               }

               if (entity instanceof EntityOcelot) {
                  return new CraftOcelot(server, (EntityOcelot)entity);
               }
            } else {
               if (entity instanceof EntityMonster) {
                  if (entity instanceof EntityZombie) {
                     if (entity instanceof EntityPigZombie) {
                        return new CraftPigZombie(server, (EntityPigZombie)entity);
                     }

                     return new CraftZombie(server, (EntityZombie)entity);
                  }

                  if (entity instanceof EntityCreeper) {
                     return new CraftCreeper(server, (EntityCreeper)entity);
                  }

                  if (entity instanceof EntityEnderman) {
                     return new CraftEnderman(server, (EntityEnderman)entity);
                  }

                  if (entity instanceof EntitySilverfish) {
                     return new CraftSilverfish(server, (EntitySilverfish)entity);
                  }

                  if (entity instanceof EntityGiantZombie) {
                     return new CraftGiant(server, (EntityGiantZombie)entity);
                  }

                  if (entity instanceof EntitySkeleton) {
                     return new CraftSkeleton(server, (EntitySkeleton)entity);
                  }

                  if (entity instanceof EntityBlaze) {
                     return new CraftBlaze(server, (EntityBlaze)entity);
                  }

                  if (entity instanceof EntityWitch) {
                     return new CraftWitch(server, (EntityWitch)entity);
                  }

                  if (entity instanceof EntityWither) {
                     return new CraftWither(server, (EntityWither)entity);
                  }

                  if (entity instanceof EntitySpider) {
                     if (entity instanceof EntityCaveSpider) {
                        return new CraftCaveSpider(server, (EntityCaveSpider)entity);
                     }

                     return new CraftSpider(server, (EntitySpider)entity);
                  }

                  if (entity instanceof EntityEndermite) {
                     return new CraftEndermite(server, (EntityEndermite)entity);
                  }

                  if (entity instanceof EntityGuardian) {
                     return new CraftGuardian(server, (EntityGuardian)entity);
                  }

                  return new CraftMonster(server, (EntityMonster)entity);
               }

               if (!(entity instanceof EntityGolem)) {
                  if (entity instanceof EntityVillager) {
                     return new CraftVillager(server, (EntityVillager)entity);
                  }

                  return new CraftCreature(server, (EntityCreature)entity);
               }

               if (entity instanceof EntitySnowman) {
                  return new CraftSnowman(server, (EntitySnowman)entity);
               }

               if (entity instanceof EntityIronGolem) {
                  return new CraftIronGolem(server, (EntityIronGolem)entity);
               }
            }
         } else {
            if (entity instanceof EntityComplexPart) {
               EntityComplexPart part = (EntityComplexPart)entity;
               if (part.owner instanceof EntityEnderDragon) {
                  return new CraftEnderDragonPart(server, (EntityComplexPart)entity);
               }

               return new CraftComplexPart(server, (EntityComplexPart)entity);
            }

            if (entity instanceof EntityExperienceOrb) {
               return new CraftExperienceOrb(server, (EntityExperienceOrb)entity);
            }

            if (entity instanceof EntityArrow) {
               return new CraftArrow(server, (EntityArrow)entity);
            }

            if (entity instanceof EntityBoat) {
               return new CraftBoat(server, (EntityBoat)entity);
            }

            if (entity instanceof EntityProjectile) {
               if (entity instanceof EntityEgg) {
                  return new CraftEgg(server, (EntityEgg)entity);
               }

               if (entity instanceof EntitySnowball) {
                  return new CraftSnowball(server, (EntitySnowball)entity);
               }

               if (entity instanceof EntityPotion) {
                  return new CraftThrownPotion(server, (EntityPotion)entity);
               }

               if (entity instanceof EntityEnderPearl) {
                  return new CraftEnderPearl(server, (EntityEnderPearl)entity);
               }

               if (entity instanceof EntityThrownExpBottle) {
                  return new CraftThrownExpBottle(server, (EntityThrownExpBottle)entity);
               }
            } else {
               if (entity instanceof EntityFireball) {
                  if (entity instanceof EntitySmallFireball) {
                     return new CraftSmallFireball(server, (EntitySmallFireball)entity);
                  }

                  if (entity instanceof EntityLargeFireball) {
                     return new CraftLargeFireball(server, (EntityLargeFireball)entity);
                  }

                  if (entity instanceof EntityWitherSkull) {
                     return new CraftWitherSkull(server, (EntityWitherSkull)entity);
                  }

                  return new CraftFireball(server, (EntityFireball)entity);
               }

               if (entity instanceof EntityEnderSignal) {
                  return new CraftEnderSignal(server, (EntityEnderSignal)entity);
               }

               if (entity instanceof EntityEnderCrystal) {
                  return new CraftEnderCrystal(server, (EntityEnderCrystal)entity);
               }

               if (entity instanceof EntityFishingHook) {
                  return new CraftFish(server, (EntityFishingHook)entity);
               }

               if (entity instanceof EntityItem) {
                  return new CraftItem(server, (EntityItem)entity);
               }

               if (entity instanceof EntityWeather) {
                  if (entity instanceof EntityLightning) {
                     return new CraftLightningStrike(server, (EntityLightning)entity);
                  }

                  return new CraftWeather(server, (EntityWeather)entity);
               }

               if (entity instanceof EntityMinecartAbstract) {
                  if (entity instanceof EntityMinecartFurnace) {
                     return new CraftMinecartFurnace(server, (EntityMinecartFurnace)entity);
                  }

                  if (entity instanceof EntityMinecartChest) {
                     return new CraftMinecartChest(server, (EntityMinecartChest)entity);
                  }

                  if (entity instanceof EntityMinecartTNT) {
                     return new CraftMinecartTNT(server, (EntityMinecartTNT)entity);
                  }

                  if (entity instanceof EntityMinecartHopper) {
                     return new CraftMinecartHopper(server, (EntityMinecartHopper)entity);
                  }

                  if (entity instanceof EntityMinecartMobSpawner) {
                     return new CraftMinecartMobSpawner(server, (EntityMinecartMobSpawner)entity);
                  }

                  if (entity instanceof EntityMinecartRideable) {
                     return new CraftMinecartRideable(server, (EntityMinecartRideable)entity);
                  }

                  if (entity instanceof EntityMinecartCommandBlock) {
                     return new CraftMinecartCommand(server, (EntityMinecartCommandBlock)entity);
                  }
               } else {
                  if (entity instanceof EntityHanging) {
                     if (entity instanceof EntityPainting) {
                        return new CraftPainting(server, (EntityPainting)entity);
                     }

                     if (entity instanceof EntityItemFrame) {
                        return new CraftItemFrame(server, (EntityItemFrame)entity);
                     }

                     if (entity instanceof EntityLeash) {
                        return new CraftLeash(server, (EntityLeash)entity);
                     }

                     return new CraftHanging(server, (EntityHanging)entity);
                  }

                  if (entity instanceof EntityFireworks) {
                     return new CraftFirework(server, (EntityFireworks)entity);
                  }
               }
            }
         }

         throw new AssertionError("Unknown entity " + (entity == null ? null : entity.getClass()));
      }
   }

   @Override
   public org.bukkit.Location getLocation() {
      return new org.bukkit.Location(this.getWorld(), this.entity.locX, this.entity.locY, this.entity.locZ, this.entity.yaw, this.entity.pitch);
   }

   @Override
   public org.bukkit.Location getLocation(org.bukkit.Location loc) {
      if (loc != null) {
         loc.setWorld(this.getWorld());
         loc.setX(this.entity.locX);
         loc.setY(this.entity.locY);
         loc.setZ(this.entity.locZ);
         loc.setYaw(this.entity.yaw);
         loc.setPitch(this.entity.pitch);
      }

      return loc;
   }

   @Override
   public Vector getVelocity() {
      return new Vector(this.entity.motX, this.entity.motY, this.entity.motZ);
   }

   @Override
   public void setVelocity(Vector vel) {
      if (PaperSpigotConfig.warnForExcessiveVelocity
         && (vel.getX() > 4.0 || vel.getX() < -4.0 || vel.getY() > 4.0 || vel.getY() < -4.0 || vel.getZ() > 4.0 || vel.getZ() < -4.0)) {
         this.getServer()
            .getLogger()
            .warning(
               "Excessive velocity set detected: tried to set velocity of entity #"
                  + this.getEntityId()
                  + " to ("
                  + vel.getX()
                  + ","
                  + vel.getY()
                  + ","
                  + vel.getZ()
                  + ")."
            );
         Thread.dumpStack();
      }

      this.entity.motX = vel.getX();
      this.entity.motY = vel.getY();
      this.entity.motZ = vel.getZ();
      this.entity.velocityChanged = true;
   }

   @Override
   public boolean isOnGround() {
      return this.entity instanceof EntityArrow ? ((EntityArrow)this.entity).isInGround() : this.entity.onGround;
   }

   @Override
   public org.bukkit.World getWorld() {
      return this.entity.world.getWorld();
   }

   @Override
   public boolean teleport(org.bukkit.Location location) {
      return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
   }

   @Override
   public boolean teleport(org.bukkit.Location location, PlayerTeleportEvent.TeleportCause cause) {
      if (this.entity.passenger == null && !this.entity.dead) {
         this.entity.mount(null);
         if (!location.getWorld().equals(this.getWorld())) {
            this.entity.teleportTo(location, cause.equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL));
            return true;
         } else {
            this.entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            this.entity.world.entityJoinedWorld(this.entity, false);
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean teleport(org.bukkit.entity.Entity destination) {
      return this.teleport(destination.getLocation());
   }

   @Override
   public boolean teleport(org.bukkit.entity.Entity destination, PlayerTeleportEvent.TeleportCause cause) {
      return this.teleport(destination.getLocation(), cause);
   }

   @Override
   public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z) {
      List<Entity> notchEntityList = this.entity.world.a(this.entity, this.entity.getBoundingBox().grow(x, y, z), null);
      List<org.bukkit.entity.Entity> bukkitEntityList = new ArrayList<>(notchEntityList.size());

      for (Entity e : notchEntityList) {
         bukkitEntityList.add(e.getBukkitEntity());
      }

      return bukkitEntityList;
   }

   @Override
   public int getEntityId() {
      return this.entity.getId();
   }

   @Override
   public int getFireTicks() {
      return this.entity.fireTicks;
   }

   @Override
   public int getMaxFireTicks() {
      return this.entity.maxFireTicks;
   }

   @Override
   public void setFireTicks(int ticks) {
      this.entity.fireTicks = ticks;
   }

   @Override
   public void remove() {
      this.entity.dead = true;
   }

   @Override
   public boolean isDead() {
      return !this.entity.isAlive();
   }

   @Override
   public boolean isValid() {
      return this.entity.isAlive() && this.entity.valid;
   }

   @Override
   public org.bukkit.Server getServer() {
      return this.server;
   }

   public Vector getMomentum() {
      return this.getVelocity();
   }

   public void setMomentum(Vector value) {
      this.setVelocity(value);
   }

   @Override
   public org.bukkit.entity.Entity getPassenger() {
      return this.isEmpty() ? null : this.getHandle().passenger.getBukkitEntity();
   }

   @Override
   public boolean setPassenger(org.bukkit.entity.Entity passenger) {
      Preconditions.checkArgument(!this.equals(passenger), "Entity cannot ride itself.");
      if (passenger instanceof CraftEntity) {
         ((CraftEntity)passenger).getHandle().mount(this.getHandle());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isEmpty() {
      return this.getHandle().passenger == null;
   }

   @Override
   public boolean eject() {
      if (this.getHandle().passenger == null) {
         return false;
      } else {
         this.getHandle().passenger.mount(null);
         return true;
      }
   }

   @Override
   public float getFallDistance() {
      return this.getHandle().fallDistance;
   }

   @Override
   public void setFallDistance(float distance) {
      this.getHandle().fallDistance = distance;
   }

   @Override
   public void setLastDamageCause(EntityDamageEvent event) {
      this.lastDamageEvent = event;
   }

   @Override
   public EntityDamageEvent getLastDamageCause() {
      return this.lastDamageEvent;
   }

   @Override
   public UUID getUniqueId() {
      return this.getHandle().getUniqueID();
   }

   @Override
   public int getTicksLived() {
      return this.getHandle().ticksLived;
   }

   @Override
   public void setTicksLived(int value) {
      if (value <= 0) {
         throw new IllegalArgumentException("Age must be at least 1 tick");
      } else {
         this.getHandle().ticksLived = value;
      }
   }

   public Entity getHandle() {
      return this.entity;
   }

   @Override
   public void playEffect(org.bukkit.EntityEffect type) {
      this.getHandle().world.broadcastEntityEffect(this.getHandle(), type.getData());
   }

   public void setHandle(Entity entity) {
      this.entity = entity;
   }

   @Override
   public String toString() {
      return "CraftEntity{id=" + this.getEntityId() + '}';
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftEntity other = (CraftEntity)obj;
         return this.getEntityId() == other.getEntityId();
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      return 29 * hash + this.getEntityId();
   }

   @Override
   public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
      this.server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
   }

   @Override
   public List<MetadataValue> getMetadata(String metadataKey) {
      return this.server.getEntityMetadata().getMetadata(this, metadataKey);
   }

   @Override
   public boolean hasMetadata(String metadataKey) {
      return this.server.getEntityMetadata().hasMetadata(this, metadataKey);
   }

   @Override
   public void removeMetadata(String metadataKey, Plugin owningPlugin) {
      this.server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
   }

   @Override
   public boolean isInsideVehicle() {
      return this.getHandle().vehicle != null;
   }

   @Override
   public boolean leaveVehicle() {
      if (this.getHandle().vehicle == null) {
         return false;
      } else {
         this.getHandle().mount(null);
         return true;
      }
   }

   @Override
   public org.bukkit.entity.Entity getVehicle() {
      return this.getHandle().vehicle == null ? null : this.getHandle().vehicle.getBukkitEntity();
   }

   @Override
   public void setCustomName(String name) {
      if (name == null) {
         name = "";
      }

      this.getHandle().setCustomName(name);
   }

   @Override
   public String getCustomName() {
      String name = this.getHandle().getCustomName();
      return name != null && name.length() != 0 ? name : null;
   }

   @Override
   public void setCustomNameVisible(boolean flag) {
      this.getHandle().setCustomNameVisible(flag);
   }

   @Override
   public boolean isCustomNameVisible() {
      return this.getHandle().getCustomNameVisible();
   }

   @Override
   public void sendMessage(String message) {
   }

   @Override
   public void sendMessage(String[] messages) {
   }

   @Override
   public String getName() {
      return this.getHandle().getName();
   }

   @Override
   public boolean isPermissionSet(String name) {
      return perm.isPermissionSet(name);
   }

   @Override
   public boolean isPermissionSet(Permission perm) {
      return CraftEntity.perm.isPermissionSet(perm);
   }

   @Override
   public boolean hasPermission(String name) {
      return perm.hasPermission(name);
   }

   @Override
   public boolean hasPermission(Permission perm) {
      return CraftEntity.perm.hasPermission(perm);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
      return perm.addAttachment(plugin, name, value);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin) {
      return perm.addAttachment(plugin);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
      return perm.addAttachment(plugin, name, value, ticks);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
      return perm.addAttachment(plugin, ticks);
   }

   @Override
   public void removeAttachment(PermissionAttachment attachment) {
      perm.removeAttachment(attachment);
   }

   @Override
   public void recalculatePermissions() {
      perm.recalculatePermissions();
   }

   @Override
   public Set<PermissionAttachmentInfo> getEffectivePermissions() {
      return perm.getEffectivePermissions();
   }

   @Override
   public boolean isOp() {
      return perm.isOp();
   }

   @Override
   public void setOp(boolean value) {
      perm.setOp(value);
   }

   @Override
   public org.bukkit.entity.Entity.Spigot spigot() {
      return this.spigot;
   }
}
