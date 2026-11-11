package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.optimizations.ObjectMapList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.spigotmc.TrackingRange;

public class EntityTrackerEntry {
   public final Entity tracker;
   public final Set<EntityPlayer> trackedPlayers = new ObjectMapList();
   protected final boolean u;
   public int b;
   public int c;
   public int xLoc;
   public int yLoc;
   public int zLoc;
   public int yRot;
   public int xRot;
   public int i;
   public double j;
   public double k;
   public double l;
   public int m;
   public boolean n;
   protected double q;
   protected double r;
   protected double s;
   protected boolean isMoving;
   protected int v;
   protected Entity w;
   protected boolean x;
   protected boolean y;
   private final int addRemoveRate;
   public int addRemoveCooldown;

   public EntityTrackerEntry(Entity entity, int b, int c, boolean flag) {
      this.tracker = entity;
      this.b = b;
      this.c = c;
      this.u = flag;
      this.xLoc = MathHelper.floor(entity.locX * 32.0);
      this.yLoc = MathHelper.floor(entity.locY * 32.0);
      this.zLoc = MathHelper.floor(entity.locZ * 32.0);
      this.yRot = MathHelper.d(entity.yaw * 256.0F / 360.0F);
      this.xRot = MathHelper.d(entity.pitch * 256.0F / 360.0F);
      this.i = MathHelper.d(entity.getHeadRotation() * 256.0F / 360.0F);
      if (this.tracker instanceof EntityArrow || this.tracker instanceof EntityProjectile) {
         this.addRemoveRate = 5;
      } else if (this.tracker instanceof EntityPlayer) {
         this.addRemoveRate = 5;
      } else {
         this.addRemoveRate = 10;
      }

      this.addRemoveCooldown = this.tracker.getId() % this.addRemoveRate;
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof EntityTrackerEntry && ((EntityTrackerEntry)object).tracker.getId() == this.tracker.getId();
   }

   @Override
   public int hashCode() {
      return this.tracker.getId();
   }

   public void update(int trackerThread) {
      if (--this.addRemoveCooldown <= 0) {
         this.removeFarPlayers(trackerThread);
         this.addNearPlayers(trackerThread);
         this.addRemoveCooldown = this.addRemoveRate;
      }

      this.track(null, trackerThread);
   }

   private void removeFarPlayers(int trackerThread) {
      Iterator<EntityPlayer> iterator = this.trackedPlayers.iterator();

      while (iterator.hasNext()) {
         EntityPlayer entityPlayer = iterator.next();
         if (!this.c(entityPlayer)) {
            iterator.remove();
            entityPlayer.playerConnection.queueEntityTrackerPacket(new PacketPlayOutEntityDestroy(this.tracker.getId()), trackerThread);
         }
      }
   }

   public void addNearPlayers(int trackerThread) {
      this.tracker.world.playerMap.forEachNearby(this.tracker.locX, this.tracker.locY, this.tracker.locZ, this.getRange(), false, entityPlayer -> {
         if (entityPlayer != this.tracker) {
            this.updatePlayer(entityPlayer, trackerThread);
         }
      });
   }

   public void track(List<EntityHuman> list, int trackerThread) {
      this.n = false;
      if (!this.isMoving || this.tracker.e(this.q, this.r, this.s) > 16.0) {
         this.q = this.tracker.locX;
         this.r = this.tracker.locY;
         this.s = this.tracker.locZ;
         this.isMoving = true;
         this.n = true;
      }

      if (this.w != this.tracker.vehicle || this.tracker.vehicle != null && this.m % 60 == 0) {
         this.w = this.tracker.vehicle;
         this.queueBroadcastTracked(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle), trackerThread);
      }

      if (this.tracker instanceof EntityItemFrame) {
         EntityItemFrame entityitemframe = (EntityItemFrame)this.tracker;
         ItemStack itemstack = entityitemframe.getItem();
         if (this.m % 10 == 0 && itemstack != null && itemstack.getItem() instanceof ItemWorldMap) {
            WorldMap worldmap = Items.FILLED_MAP.getSavedMap(itemstack, this.tracker.world);

            for (EntityPlayer entityplayer : this.trackedPlayers) {
               worldmap.a(entityplayer, itemstack);
               Packet<?> packet = Items.FILLED_MAP.c(itemstack, this.tracker.world, entityplayer);
               if (packet != null) {
                  entityplayer.playerConnection.queueEntityTrackerPacket(packet, trackerThread);
               }
            }
         }

         this.b(trackerThread);
      }

      if (this.m % this.c == 0 || this.tracker.ai || this.tracker.getDataWatcher().a()) {
         if (this.tracker.vehicle == null) {
            this.v++;
            int i = (int)Math.round(this.tracker.locX * 32.0);
            int j = (int)Math.round(this.tracker.locY * 32.0);
            int k = (int)Math.round(this.tracker.locZ * 32.0);
            int l = Math.round(this.tracker.yaw * 256.0F / 360.0F);
            int i1 = Math.round(this.tracker.pitch * 256.0F / 360.0F);
            int j1 = i - this.xLoc;
            int k1 = j - this.yLoc;
            int l1 = k - this.zLoc;
            Packet<?> object = null;
            boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4 || this.m % 60 == 0;
            boolean flag1 = Math.abs(l - this.yRot) >= 4 || Math.abs(i1 - this.xRot) >= 4;
            if (this.m > 0 || this.tracker instanceof EntityArrow) {
               if (flag) {
                  this.xLoc = i;
                  this.yLoc = j;
                  this.zLoc = k;
               }

               if (flag1) {
                  this.yRot = l;
                  this.xRot = i1;
               }

               if (j1 >= -128
                  && j1 < 128
                  && k1 >= -128
                  && k1 < 128
                  && l1 >= -128
                  && l1 < 128
                  && this.v <= 400
                  && !this.x
                  && this.y == this.tracker.onGround
                  && !(this.tracker instanceof EntityFishingHook)) {
                  if ((!flag || !flag1) && !(this.tracker instanceof EntityArrow)) {
                     if (flag) {
                        object = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(this.tracker.getId(), (byte)j1, (byte)k1, (byte)l1, this.tracker.onGround);
                     } else if (flag1) {
                        object = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte)l, (byte)i1, this.tracker.onGround);
                     }
                  } else {
                     object = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                        this.tracker.getId(), (byte)j1, (byte)k1, (byte)l1, (byte)l, (byte)i1, this.tracker.onGround
                     );
                  }
               } else {
                  this.y = this.tracker.onGround;
                  this.v = 0;
                  object = new PacketPlayOutEntityTeleport(this.tracker.getId(), i, j, k, (byte)l, (byte)i1, this.tracker.onGround);
               }
            }

            if (this.u) {
               double d0 = this.tracker.motX - this.j;
               double d1 = this.tracker.motY - this.k;
               double d2 = this.tracker.motZ - this.l;
               double d3 = 0.02;
               double d4 = d0 * d0 + d1 * d1 + d2 * d2;
               if (d4 > d3 * d3 || d4 > 0.0 && this.tracker.motX == 0.0 && this.tracker.motY == 0.0 && this.tracker.motZ == 0.0) {
                  this.j = this.tracker.motX;
                  this.k = this.tracker.motY;
                  this.l = this.tracker.motZ;
                  this.queueBroadcastTracked(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.j, this.k, this.l), trackerThread);
               }
            }

            if (object != null) {
               this.queueBroadcastTracked(object, trackerThread);
            }

            this.b(trackerThread);
            this.x = false;
         } else {
            int ix = Math.round(this.tracker.yaw * 256.0F / 360.0F);
            int jx = Math.round(this.tracker.pitch * 256.0F / 360.0F);
            boolean flag2 = Math.abs(ix - this.yRot) >= 4 || Math.abs(jx - this.xRot) >= 4;
            if (flag2) {
               this.queueBroadcastTracked(
                  new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte)ix, (byte)jx, this.tracker.onGround), trackerThread
               );
               this.yRot = ix;
               this.xRot = jx;
            }

            this.xLoc = (int)Math.round(this.tracker.locX * 32.0);
            this.yLoc = (int)Math.round(this.tracker.locY * 32.0);
            this.zLoc = (int)Math.round(this.tracker.locZ * 32.0);
            this.b(trackerThread);
            this.x = true;
         }

         int var26 = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
         if (Math.abs(var26 - this.i) >= 4) {
            this.queueBroadcastTracked(new PacketPlayOutEntityHeadRotation(this.tracker, (byte)var26), trackerThread);
            this.i = var26;
         }

         this.tracker.ai = false;
      }

      this.m++;
      if (this.tracker.velocityChanged) {
         this.tracker.velocityChanged = false;
      }
   }

   private void b(int trackerThread) {
      DataWatcher datawatcher = this.tracker.getDataWatcher();
      if (datawatcher.a()) {
         this.queueBroadcastTracked(new PacketPlayOutEntityMetadata(this.tracker.getId(), datawatcher.b()), trackerThread);
      }

      if (this.tracker instanceof EntityLiving) {
         AttributeMapServer attributemapserver = (AttributeMapServer)((EntityLiving)this.tracker).getAttributeMap();
         Set<AttributeInstance> updatedAttributes = attributemapserver.getAttributes();
         if (this.tracker.passenger instanceof EntityPlayer) {
            List<AttributeInstance> usefulAttributes = new ArrayList<>();

            for (AttributeInstance updatedAttribute : updatedAttributes) {
               if (updatedAttribute.getAttribute() != GenericAttributes.MOVEMENT_SPEED || this.tracker instanceof EntityHorse) {
                  usefulAttributes.add(updatedAttribute);
               }
            }

            if (!updatedAttributes.isEmpty()) {
               ((EntityPlayer)this.tracker.passenger)
                  .playerConnection
                  .queueEntityTrackerPacket(new PacketPlayOutUpdateAttributes(this.tracker.getId(), usefulAttributes), trackerThread);
            }
         }

         updatedAttributes.clear();
      }
   }

   public void broadcast(Packet<?> packet) {
      for (EntityPlayer p : this.trackedPlayers) {
         p.playerConnection.sendPacket(packet);
      }
   }

   public void broadcastLazily(Packet<?> packet) {
      for (EntityPlayer p : this.trackedPlayers) {
         p.playerConnection.queuePacketMainThread(packet);
      }
   }

   public void queueBroadcastTracked(Packet<?> packet, int trackerThread) {
      for (EntityPlayer trackedPlayer : this.trackedPlayers) {
         trackedPlayer.playerConnection.queueEntityTrackerPacket(packet, trackerThread);
      }
   }

   public void broadcastIncludingSelf(Packet<?> packet) {
      this.broadcast(packet);
   }

   public void a() {
      for (EntityPlayer p : this.trackedPlayers) {
         p.d(this.tracker);
      }
   }

   public void updatePlayer(EntityPlayer entityplayer, int trackerThread) {
      if (entityplayer != this.tracker) {
         if (!this.c(entityplayer)) {
            if (this.trackedPlayers.remove(entityplayer)) {
               entityplayer.d(this.tracker);
            }
         } else {
            if (!this.trackedPlayers.contains(entityplayer) && (this.e(entityplayer) || this.tracker.attachedToPlayer)) {
               if (!entityplayer.canSee(this.tracker)) {
                  return;
               }

               this.trackedPlayers.add(entityplayer);
               Packet<?> packet = this.c();
               if (packet == null) {
                  return;
               }

               PlayerConnection playerConnection = entityplayer.playerConnection;
               playerConnection.queueEntityTrackerPacket(packet, trackerThread);
               int id = this.tracker.getId();
               if (!this.tracker.getDataWatcher().d()) {
                  playerConnection.queueEntityTrackerPacket(new PacketPlayOutEntityMetadata(id, this.tracker.getDataWatcher(), true), trackerThread);
               }

               NBTTagCompound nbttagcompound = this.tracker.getNBTTag();
               if (nbttagcompound != null) {
                  playerConnection.queueEntityTrackerPacket(new PacketPlayOutUpdateEntityNBT(id, nbttagcompound), trackerThread);
               }

               this.j = this.tracker.motX;
               this.k = this.tracker.motY;
               this.l = this.tracker.motZ;
               boolean oneTimeMotion = this.tracker instanceof EntityFishingHook
                  || this.tracker instanceof EntityPotion
                  || this.tracker instanceof EntityEnderPearl
                  || this.tracker instanceof EntitySnowball
                  || this.tracker instanceof EntityEgg;
               if (this.u && !(packet instanceof PacketPlayOutSpawnEntityLiving) || oneTimeMotion) {
                  playerConnection.queueEntityTrackerPacket(
                     new PacketPlayOutEntityVelocity(id, this.tracker.motX, this.tracker.motY, this.tracker.motZ), trackerThread
                  );
               }

               if (this.tracker instanceof EntityLiving) {
                  EntityLiving entityliving = (EntityLiving)this.tracker;

                  for (int i = 0; i < 5; i++) {
                     ItemStack itemstack = entityliving.getEquipment(i);
                     if (itemstack != null) {
                        playerConnection.queueEntityTrackerPacket(
                           new PacketPlayOutEntityEquipment(id, i, itemstack, (Boolean)BlazeConfig.obfuscateEntityEquipment.getValue()), trackerThread
                        );
                     }
                  }

                  for (MobEffect effect : entityliving.getEffects()) {
                     playerConnection.queueEntityTrackerPacket(new PacketPlayOutEntityEffect(id, effect), trackerThread);
                  }

                  if (this.tracker instanceof EntityInsentient && ((EntityInsentient)this.tracker).getLeashHolder() != null) {
                     playerConnection.queueEntityTrackerPacket(
                        new PacketPlayOutAttachEntity(1, this.tracker, ((EntityInsentient)this.tracker).getLeashHolder()), trackerThread
                     );
                  }

                  playerConnection.queueEntityTrackerPacket(
                     new PacketPlayOutEntityHeadRotation(this.tracker, (byte)(this.i = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F))),
                     trackerThread
                  );
               }

               if (this.tracker.vehicle != null) {
                  playerConnection.queueEntityTrackerPacket(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle), trackerThread);
               }
            }
         }
      }
   }

   protected boolean e(EntityPlayer entityplayer) {
      return entityplayer.u().getPlayerChunkMap().a(entityplayer, this.tracker.ae, this.tracker.ag);
   }

   protected Packet<PacketListenerPlayOut> c() {
      if (this.tracker.dead) {
         return null;
      } else if (this.tracker instanceof EntityPlayer) {
         return new PacketPlayOutNamedEntitySpawn((EntityHuman)this.tracker);
      } else if (this.tracker instanceof EntityItem) {
         return new PacketPlayOutSpawnEntity(this.tracker, 2, 1);
      } else if (this.tracker instanceof EntityPotion) {
         return new PacketPlayOutSpawnEntity(this.tracker, 73, ((EntityPotion)this.tracker).getPotionValue());
      } else if (this.tracker instanceof EntityArrow) {
         Entity entity = ((EntityArrow)this.tracker).shooter;
         return new PacketPlayOutSpawnEntity(this.tracker, 60, entity != null ? entity.getId() : this.tracker.getId());
      } else if (this.tracker instanceof EntityFishingHook) {
         EntityHuman entityhuman = ((EntityFishingHook)this.tracker).owner;
         return new PacketPlayOutSpawnEntity(this.tracker, 90, entityhuman != null ? entityhuman.getId() : this.tracker.getId());
      } else if (this.tracker instanceof EntityEnderPearl) {
         return new PacketPlayOutSpawnEntity(this.tracker, 65);
      } else if (this.tracker instanceof EntityMinecartAbstract) {
         EntityMinecartAbstract entityminecartabstract = (EntityMinecartAbstract)this.tracker;
         return new PacketPlayOutSpawnEntity(this.tracker, 10, entityminecartabstract.s().a());
      } else if (this.tracker instanceof EntityBoat) {
         return new PacketPlayOutSpawnEntity(this.tracker, 1);
      } else if (this.tracker instanceof IAnimal) {
         this.i = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
         return new PacketPlayOutSpawnEntityLiving((EntityLiving)this.tracker);
      } else if (this.tracker instanceof EntitySnowball) {
         return new PacketPlayOutSpawnEntity(this.tracker, 61);
      } else if (this.tracker instanceof EntityThrownExpBottle) {
         return new PacketPlayOutSpawnEntity(this.tracker, 75);
      } else if (this.tracker instanceof EntityEnderSignal) {
         return new PacketPlayOutSpawnEntity(this.tracker, 72);
      } else if (this.tracker instanceof EntityFireworks) {
         return new PacketPlayOutSpawnEntity(this.tracker, 76);
      } else if (this.tracker instanceof EntityFireball) {
         EntityFireball entityfireball = (EntityFireball)this.tracker;
         byte b0 = 63;
         if (this.tracker instanceof EntitySmallFireball) {
            b0 = 64;
         } else if (this.tracker instanceof EntityWitherSkull) {
            b0 = 66;
         }

         PacketPlayOutSpawnEntity packetplayoutspawnentity;
         if (entityfireball.shooter != null) {
            packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, b0, ((EntityFireball)this.tracker).shooter.getId());
         } else {
            packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, b0, 0);
         }

         packetplayoutspawnentity.d((int)(entityfireball.dirX * 8000.0));
         packetplayoutspawnentity.e((int)(entityfireball.dirY * 8000.0));
         packetplayoutspawnentity.f((int)(entityfireball.dirZ * 8000.0));
         return packetplayoutspawnentity;
      } else if (this.tracker instanceof EntityEgg) {
         return new PacketPlayOutSpawnEntity(this.tracker, 62);
      } else if (this.tracker instanceof EntityEnderCrystal) {
         return new PacketPlayOutSpawnEntity(this.tracker, 51);
      } else if (this.tracker instanceof EntityArmorStand) {
         return new PacketPlayOutSpawnEntity(this.tracker, 78);
      } else if (this.tracker instanceof EntityPainting) {
         return new PacketPlayOutSpawnEntityPainting((EntityPainting)this.tracker);
      } else if (this.tracker instanceof EntityItemFrame) {
         EntityItemFrame entityitemframe = (EntityItemFrame)this.tracker;
         PacketPlayOutSpawnEntity packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, 71, entityitemframe.direction.b());
         BlockPosition blockposition = entityitemframe.getBlockPosition();
         packetplayoutspawnentity.a(MathHelper.d((float)(blockposition.getX() * 32)));
         packetplayoutspawnentity.b(MathHelper.d((float)(blockposition.getY() * 32)));
         packetplayoutspawnentity.c(MathHelper.d((float)(blockposition.getZ() * 32)));
         return packetplayoutspawnentity;
      } else if (this.tracker instanceof EntityLeash) {
         EntityLeash entityleash = (EntityLeash)this.tracker;
         PacketPlayOutSpawnEntity packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, 77);
         BlockPosition blockposition = entityleash.getBlockPosition();
         packetplayoutspawnentity.a(MathHelper.d((float)(blockposition.getX() * 32)));
         packetplayoutspawnentity.b(MathHelper.d((float)(blockposition.getY() * 32)));
         packetplayoutspawnentity.c(MathHelper.d((float)(blockposition.getZ() * 32)));
         return packetplayoutspawnentity;
      } else if (this.tracker instanceof EntityExperienceOrb) {
         return new PacketPlayOutSpawnEntityExperienceOrb((EntityExperienceOrb)this.tracker);
      } else {
         throw new IllegalArgumentException("Don't know how to add " + this.tracker.getClass() + "!");
      }
   }

   public boolean c(EntityPlayer entityplayer) {
      double d0 = entityplayer.locX - this.tracker.locX;
      double d1 = entityplayer.locZ - this.tracker.locZ;
      return d0 >= -this.b && d0 <= this.b && d1 >= -this.b && d1 <= this.b;
   }

   public void clear(EntityPlayer entityplayer) {
      if (this.trackedPlayers.remove(entityplayer)) {
         entityplayer.d(this.tracker);
      }
   }

   public void scanPlayers(List<EntityHuman> playerList) {
   }

   public void updatePlayer(EntityPlayer entityPlayer) {
      this.updatePlayer(entityPlayer, 3);
   }

   public void track(List<EntityHuman> list) {
      this.track(list, 3);
   }

   private void b() {
      this.b(3);
   }

   public int getRange() {
      return this.tracker.passenger == null ? this.b : Math.max(this.b, TrackingRange.getEntityTrackingRange(this.tracker.passenger, 0));
   }
}
