package net.minecraft.server.v1_8_R3;

import java.util.List;

public class EntityTrackerCannonEntry extends EntityTrackerEntry {
   private boolean movingX;
   private boolean movingY;
   private boolean movingZ;

   public EntityTrackerCannonEntry(Entity entity, int i, int j, boolean flag) {
      super(entity, i, j, flag);
      this.movingX = entity.motX != 0.0;
      this.movingY = true;
      this.movingZ = entity.motZ != 0.0;
   }

   @Override
   public void track(List<EntityHuman> list, int trackerThread) {
      boolean motionX = this.tracker.motX != 0.0;
      boolean motionY = this.tracker.motY != 0.0;
      boolean motionZ = this.tracker.motZ != 0.0;
      if (this.tracker.ai || motionX != this.movingX || motionY != this.movingY || motionZ != this.movingZ) {
         if (motionX || motionY || motionZ) {
            DataWatcher datawatcher = this.tracker.getDataWatcher();
            if (datawatcher.a()) {
               this.queueBroadcastTracked(new PacketPlayOutEntityMetadata(this.tracker.getId(), datawatcher, false), trackerThread);
            }

            if (this.tracker.lastX != this.tracker.locX || this.tracker.lastY != this.tracker.locY || this.tracker.lastZ != this.tracker.locZ) {
               this.queueBroadcastTracked(new PacketPlayOutEntityTeleport(this.tracker), trackerThread);
            }

            this.queueBroadcastTracked(new PacketPlayOutEntityVelocity(this.tracker), trackerThread);
         }

         this.tracker.ai = false;
         this.movingX = motionX;
         this.movingY = motionY;
         this.movingZ = motionZ;
      }
   }

   @Override
   public void updatePlayer(EntityPlayer entityplayer, int trackerThread) {
      if (this.c(entityplayer)) {
         if (this.trackedPlayers.add(entityplayer) && (this.e(entityplayer) || this.tracker.attachedToPlayer)) {
            Packet<?> packet = this.c();
            if (packet == null) {
               return;
            }

            PlayerConnection playerConnection = entityplayer.playerConnection;
            playerConnection.queueEntityTrackerPacket(packet, trackerThread);
            if (this.tracker.getCustomNameVisible()) {
               playerConnection.queueEntityTrackerPacket(
                  new PacketPlayOutEntityMetadata(this.tracker.getId(), this.tracker.getDataWatcher(), true), trackerThread
               );
            }

            playerConnection.queueEntityTrackerPacket(
               new PacketPlayOutEntityVelocity(this.tracker.getId(), this.tracker.motX, this.tracker.motY, this.tracker.motZ), trackerThread
            );
            if (this.tracker.vehicle != null) {
               playerConnection.queueEntityTrackerPacket(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle), trackerThread);
            }
         }
      } else if (this.trackedPlayers.remove(entityplayer)) {
         entityplayer.d(this.tracker);
      }
   }

   @Override
   protected Packet<PacketListenerPlayOut> c() {
      if (this.tracker.dead) {
         return null;
      } else if (this.tracker instanceof EntityTNTPrimed) {
         return new PacketPlayOutSpawnEntity(this.tracker, 50);
      } else {
         EntityFallingBlock entityfallingblock = (EntityFallingBlock)this.tracker;
         return new PacketPlayOutSpawnEntity(this.tracker, 70, Block.getCombinedId(entityfallingblock.getBlock()));
      }
   }
}
