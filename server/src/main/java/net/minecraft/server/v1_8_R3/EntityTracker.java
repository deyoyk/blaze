package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.optimizations.ObjectMapList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spigotmc.AsyncCatcher;
import org.spigotmc.TrackingRange;

public class EntityTracker {
   private static final Logger a = LogManager.getLogger();
   private final ObjectMapList<EntityTrackerEntry> c = new ObjectMapList();
   public final IntHashMap<EntityTrackerEntry> trackedEntities = new IntHashMap<>();
   private final WorldServer world;
   private final int e;

   public EntityTracker(WorldServer worldserver) {
      this.world = worldserver;
      this.e = 128;
   }

   public void track(Entity entity) {
      if (entity instanceof EntityPlayer) {
         this.addEntity(entity, 512, 2);
      } else if (entity instanceof EntityFishingHook) {
         this.addEntity(entity, 64, Integer.MAX_VALUE, false);
      } else if (entity instanceof EntityPotion) {
         this.addEntity(entity, 64, Integer.MAX_VALUE, false);
      } else if (entity instanceof EntityEnderPearl) {
         this.addEntity(entity, 64, Integer.MAX_VALUE, false);
      } else if (entity instanceof EntityArrow) {
         this.addEntity(entity, 64, 20, false);
      } else if (entity instanceof EntitySmallFireball) {
         this.addEntity(entity, 64, 10, false);
      } else if (entity instanceof EntityFireball) {
         this.addEntity(entity, 64, 10, false);
      } else if (entity instanceof EntitySnowball) {
         this.addEntity(entity, 64, Integer.MAX_VALUE, false);
      } else if (entity instanceof EntityEnderSignal) {
         this.addEntity(entity, 64, 4, true);
      } else if (entity instanceof EntityEgg) {
         this.addEntity(entity, 64, Integer.MAX_VALUE, false);
      } else if (entity instanceof EntityThrownExpBottle) {
         this.addEntity(entity, 64, 10, true);
      } else if (entity instanceof EntityFireworks) {
         this.addEntity(entity, 64, 10, true);
      } else if (entity instanceof EntityItem) {
         this.addEntity(entity, 64, 20, true);
      } else if (entity instanceof EntityMinecartAbstract) {
         this.addEntity(entity, 80, 3, true);
      } else if (entity instanceof EntityBoat) {
         this.addEntity(entity, 80, 3, true);
      } else if (entity instanceof EntitySquid) {
         this.addEntity(entity, 64, 3, true);
      } else if (entity instanceof EntityWither) {
         this.addEntity(entity, 80, 3, false);
      } else if (entity instanceof EntityBat) {
         this.addEntity(entity, 80, 3, false);
      } else if (entity instanceof EntityEnderDragon) {
         this.addEntity(entity, 160, 3, true);
      } else if (entity instanceof IAnimal) {
         this.addEntity(entity, 80, 3, true);
      } else if (entity instanceof EntityTNTPrimed) {
         this.addEntity(entity, 160, 10, true);
      } else if (entity instanceof EntityFallingBlock) {
         this.addEntity(entity, 160, 20, true);
      } else if (entity instanceof EntityHanging) {
         this.addEntity(entity, 160, Integer.MAX_VALUE, false);
      } else if (entity instanceof EntityArmorStand) {
         this.addEntity(entity, 160, 3, true);
      } else if (entity instanceof EntityExperienceOrb) {
         this.addEntity(entity, 160, 20, true);
      } else if (entity instanceof EntityEnderCrystal) {
         this.addEntity(entity, 256, Integer.MAX_VALUE, false);
      }
   }

   public void addEntity(Entity entity, int trackingRange, int updateFrequency) {
      this.addEntity(entity, trackingRange, updateFrequency, false);
   }

   public void addEntity(Entity entity, int trackingRange, int trackingFrequency, boolean flag) {
      if ((!(Boolean)BlazeConfig.hideSandEntity.getValue() || !(entity instanceof EntityFallingBlock))
         && (!(Boolean)BlazeConfig.hideTntEntity.getValue() || !(entity instanceof EntityTNTPrimed))) {
         AsyncCatcher.catchOp("entity track");
         trackingRange = TrackingRange.getEntityTrackingRange(entity, trackingRange);
         if (trackingRange > this.e) {
            trackingRange = this.e;
         }

         try {
            if (this.trackedEntities.b(entity.getId())) {
               throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry entitytrackerentry = entity.getEntry(trackingRange, trackingFrequency, flag);
            this.trackedEntities.a(entity.getId(), entitytrackerentry);
            this.c.add(entitytrackerentry);
            entitytrackerentry.addRemoveCooldown = 0;
         } catch (Throwable var12) {
            CrashReport crashreport = CrashReport.a(var12, "Adding entity to track");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity To Track");
            crashreportsystemdetails.a("Tracking range", trackingRange + " blocks");
            int finalI = trackingRange;
            crashreportsystemdetails.a("Update interval", () -> {
               String s = "Once per " + finalI + " ticks";
               if (finalI == Integer.MAX_VALUE) {
                  s = "Maximum (" + s + ")";
               }

               return s;
            });
            entity.appendEntityCrashDetails(crashreportsystemdetails);
            CrashReportSystemDetails crashreportsystemdetails1 = crashreport.a("Entity That Is Already Tracked");
            this.trackedEntities.get(entity.getId()).tracker.appendEntityCrashDetails(crashreportsystemdetails1);

            try {
               throw new ReportedException(crashreport);
            } catch (ReportedException var11) {
               a.error("\"Silently\" catching entity tracking error.", var11);
            }
         }
      }
   }

   public void untrackEntity(Entity entity) {
      AsyncCatcher.catchOp("entity untrack");
      if (entity instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entity;

         for (EntityTrackerEntry entitytrackerentry : this.c) {
            entitytrackerentry.clear(entityplayer);
         }
      }

      EntityTrackerEntry entitytrackerentry1 = this.trackedEntities.d(entity.getId());
      if (entitytrackerentry1 != null) {
         this.c.remove(entitytrackerentry1);
         entitytrackerentry1.a();
      }
   }

   public void updatePlayers() {
      Blaze.getInstance().getThreadingManager().getTrackerExecutor().updatePlayers(this.c);
   }

   public void a(EntityPlayer entityplayer) {
   }

   public void a(EntityPlayer entityplayer, Chunk chunk) {
      for (EntityTrackerEntry entitytrackerentry : this.c) {
         if (entitytrackerentry.tracker != entityplayer && entitytrackerentry.tracker.ae == chunk.locX && entitytrackerentry.tracker.ag == chunk.locZ) {
            entitytrackerentry.updatePlayer(entityplayer);
         }
      }
   }

   public void a(Entity entity, Packet<?> packet) {
      EntityTrackerEntry entitytrackerentry = this.trackedEntities.get(entity.getId());
      if (entitytrackerentry != null) {
         entitytrackerentry.broadcast(packet);
      }
   }

   public void sendPacketToEntity(Entity entity, Packet<?> packet) {
      EntityTrackerEntry entitytrackerentry = this.trackedEntities.get(entity.getId());
      if (entitytrackerentry != null) {
         entitytrackerentry.broadcastIncludingSelf(packet);
      }
   }

   public void untrackPlayer(EntityPlayer entityplayer) {
      for (EntityTrackerEntry entitytrackerentry : this.c) {
         entitytrackerentry.clear(entityplayer);
      }
   }
}
