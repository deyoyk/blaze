package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class EntityTrackerPlayerEntry extends EntityTrackerEntry {
   public EntityTrackerPlayerEntry(Entity entity, int b, int c, boolean flag) {
      super(entity, b, c, flag);
   }

   @Override
   public void track(List<EntityHuman> list, int trackerThread) {
      if (this.w != this.tracker.vehicle || this.tracker.vehicle != null && this.m % 60 == 0) {
         this.w = this.tracker.vehicle;
         this.queueBroadcastTracked(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle), trackerThread);
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
            Packet<PacketListenerPlayOut> packet = null;
            boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4 || this.m % 60 == 0;
            boolean flag1 = Math.abs(l - this.yRot) >= 4 || Math.abs(i1 - this.xRot) >= 4;
            if (this.m > 0) {
               if (flag) {
                  this.xLoc = i;
                  this.yLoc = j;
                  this.zLoc = k;
               }

               if (flag1) {
                  this.yRot = l;
                  this.xRot = i1;
               }

               if (j1 < -128 || j1 >= 128 || k1 < -128 || k1 >= 128 || l1 < -128 || l1 >= 128 || this.v > 150 || this.x || this.y != this.tracker.onGround) {
                  this.y = this.tracker.onGround;
                  this.v = 0;
                  packet = new PacketPlayOutEntityTeleport(this.tracker.getId(), i, j, k, (byte)l, (byte)i1, this.tracker.onGround);
               } else if (flag && flag1) {
                  packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                     this.tracker.getId(), (byte)j1, (byte)k1, (byte)l1, (byte)l, (byte)i1, this.tracker.onGround
                  );
               } else if (flag) {
                  packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(this.tracker.getId(), (byte)j1, (byte)k1, (byte)l1, this.tracker.onGround);
               } else if (flag1) {
                  packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte)l, (byte)i1, this.tracker.onGround);
               }
            }

            if (packet != null) {
               this.queueBroadcastTracked(packet, trackerThread);
            }

            this.b(trackerThread);
            this.x = false;
         } else {
            int yawSerialized = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
            int pitchSerialized = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
            if (Math.abs(yawSerialized - this.yRot) >= 4 || Math.abs(pitchSerialized - this.xRot) >= 4) {
               this.queueBroadcastTracked(
                  new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte)yawSerialized, (byte)pitchSerialized, this.tracker.onGround),
                  trackerThread
               );
               this.yRot = yawSerialized;
               this.xRot = pitchSerialized;
            }

            this.xLoc = (int)Math.round(this.tracker.locX * 32.0);
            this.yLoc = (int)Math.round(this.tracker.locY * 32.0);
            this.zLoc = (int)Math.round(this.tracker.locZ * 32.0);
            this.b(trackerThread);
            this.x = true;
         }

         int ix = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
         if (Math.abs(ix - this.i) >= 4) {
            this.queueBroadcastTracked(new PacketPlayOutEntityHeadRotation(this.tracker, (byte)ix), trackerThread);
            this.i = ix;
         }

         this.tracker.ai = false;
      }

      this.m++;
      if (this.tracker.velocityChanged) {
         boolean cancelled = false;
         Player player = (Player)this.tracker.getBukkitEntity();
         Vector velocity = player.getVelocity();
         PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity);
         this.tracker.world.getServer().getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            cancelled = true;
         } else if (!velocity.equals(event.getVelocity())) {
            player.setVelocity(velocity);
         }

         if (!cancelled) {
            ((EntityPlayer)this.tracker).playerConnection.queueEntityTrackerPacket(new PacketPlayOutEntityVelocity(this.tracker), trackerThread);
         }

         this.tracker.velocityChanged = false;
      }
   }

   private void b(int trackerThread) {
      DataWatcher datawatcher = this.tracker.getDataWatcher();
      EntityPlayer player = (EntityPlayer)this.tracker;
      if (datawatcher.a()) {
         List<DataWatcher.WatchableObject> changedMetadata = datawatcher.c();
         changedMetadata.removeIf(watchable -> watchable.a() == 6 && (Float)watchable.b() > 0.0F);
         PacketPlayOutEntityMetadata modifiedPacket = new PacketPlayOutEntityMetadata(this.tracker.getId(), changedMetadata);
         this.queueBroadcastTracked(modifiedPacket, trackerThread);
         ((EntityPlayer)this.tracker)
            .playerConnection
            .queueEntityTrackerPacket(new PacketPlayOutEntityMetadata(this.tracker.getId(), datawatcher, false), trackerThread);
      }

      Set<AttributeInstance> set = ((AttributeMapServer)player.getAttributeMap()).getAttributes();
      if (!set.isEmpty()) {
         player.getBukkitEntity().injectScaledMaxHealth(set, false);
         ((EntityPlayer)this.tracker).playerConnection.queueEntityTrackerPacket(new PacketPlayOutUpdateAttributes(this.tracker.getId(), set), trackerThread);
      }

      set.clear();
   }

   @Override
   public void updatePlayer(EntityPlayer entityplayer, int trackerThread) {
      if (entityplayer != this.tracker) {
         if (this.c(entityplayer)) {
            if (!this.trackedPlayers.contains(entityplayer) && this.e(entityplayer)) {
               EntityPlayer player = (EntityPlayer)this.tracker;
               if (!entityplayer.getBukkitEntity().canSee(player.getBukkitEntity())) {
                  return;
               }

               this.trackedPlayers.add(entityplayer);
               PlayerConnection playerConnection = entityplayer.playerConnection;
               Packet<?> packet = !this.tracker.dead ? new PacketPlayOutNamedEntitySpawn(player) : null;
               if (packet == null) {
                  return;
               }

               playerConnection.queueEntityTrackerPacket(packet, trackerThread);
               if (player.isSleeping()) {
                  entityplayer.playerConnection.queueEntityTrackerPacket(new PacketPlayOutBed(player, new BlockPosition(this.tracker)), trackerThread);
               }

               int id = this.tracker.getId();
               if (!this.tracker.getDataWatcher().d()) {
                  PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(id, this.tracker.getDataWatcher(), true);
                  if (this.tracker.isAlive()) {
                     meta.obfuscateHealth();
                  }

                  playerConnection.queueEntityTrackerPacket(meta, trackerThread);
               }

               EntityLiving entityliving = (EntityLiving)this.tracker;
               Collection<AttributeInstance> collection = ((AttributeMapServer)entityliving.getAttributeMap()).c();
               if (id == entityplayer.getId()) {
                  ((EntityPlayer)this.tracker).getBukkitEntity().injectScaledMaxHealth(collection, false);
               }

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

               playerConnection.queueEntityTrackerPacket(
                  new PacketPlayOutEntityHeadRotation(this.tracker, (byte)(this.i = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F))),
                  trackerThread
               );
               if (this.tracker.vehicle != null) {
                  playerConnection.queueEntityTrackerPacket(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle), trackerThread);
               }
            }
         } else if (this.trackedPlayers.remove(entityplayer)) {
            entityplayer.d(this.tracker);
         }
      }
   }

   @Override
   public boolean c(EntityPlayer entityplayer) {
      return super.c(entityplayer) && this.tracker.a(entityplayer);
   }

   @Override
   public void broadcastIncludingSelf(Packet<?> packet) {
      this.broadcast(packet);
      ((EntityPlayer)this.tracker).playerConnection.sendPacket(packet);
   }

   @Override
   protected Packet<PacketListenerPlayOut> c() {
      return this.tracker.dead ? null : new PacketPlayOutNamedEntitySpawn((EntityHuman)this.tracker);
   }
}
