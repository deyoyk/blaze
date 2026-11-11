package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.List;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Explosive;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class EntityTNTPrimed extends Entity {
   public int fuseTicks;
   public float yield = 4.0F;
   public boolean isIncendiary = false;
   public Location sourceLoc;
   private EntityLiving source;

   public EntityTNTPrimed(World world) {
      this(null, world);
   }

   public EntityTNTPrimed(Location loc, World world) {
      super(world);
      this.sourceLoc = loc;
      this.k = true;
      this.setSize(0.98F, 0.98F);
      this.loadChunks = world.paperSpigotConfig.loadUnloadedTNTEntities;
   }

   public EntityTNTPrimed(Location loc, World world, double d0, double d1, double d2, EntityLiving entityliving) {
      this(loc, world);
      this.setPosition(d0, d1, d2);
      if (world.paperSpigotConfig.fixCannons) {
         this.motX = this.motZ = 0.0;
      } else {
         float f = (float)(Math.random() * (float) Math.PI * 2.0);
         this.motX = -((float)FastMath.sin(f)) * 0.02F;
         this.motZ = -((float)FastMath.cos(f)) * 0.02F;
      }

      this.motY = 0.2F;
      this.fuseTicks = 80;
      this.lastX = d0;
      this.lastY = d1;
      this.lastZ = d2;
      this.source = entityliving;
   }

   @Override
   protected void h() {
   }

   @Override
   protected boolean s_() {
      return false;
   }

   @Override
   public boolean ad() {
      return !this.dead;
   }

   @Override
   public void t_() {
      this.lastX = this.locX;
      this.lastY = this.locY;
      this.lastZ = this.locZ;
      this.motY -= 0.04F;
      this.move(this.motX, this.motY, this.motZ);
      if (this.world.paperSpigotConfig.tntEntityHeightNerf != 0 && this.locY > this.world.paperSpigotConfig.tntEntityHeightNerf) {
         this.die();
      }

      if (this.inUnloadedChunk && this.world.paperSpigotConfig.removeUnloadedTNTEntities) {
         this.die();
         this.fuseTicks = 2;
      }

      this.motX *= 0.98F;
      this.motY *= 0.98F;
      this.motZ *= 0.98F;
      if (this.onGround) {
         this.motX *= 0.7F;
         this.motY *= -0.5;
         this.motZ *= 0.7F;
      }

      if (this.fuseTicks-- <= 0) {
         this.respawn();
         this.explode();
         this.die();
      } else {
         this.W();
         if ((Boolean)BlazeConfig.tntParticles.getValue()) {
            this.world.addParticle(EnumParticle.SMOKE_NORMAL, this.locX, this.locY + 0.5, this.locZ, 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
         }
      }
   }

   @Override
   public boolean merge(Entity entity) {
      return (Boolean)BlazeConfig.tntMerging.getValue()
         && entity.ticksLived == this.ticksLived + 1
         && entity.lastX == this.locX
         && entity.lastY == this.locY
         && entity.lastZ == this.locZ
         && entity.lastMotX == this.motX
         && entity.lastMotY == this.motY - 0.04F
         && entity.lastMotZ == this.motZ
         && entity.getClass() == this.getClass()
         && ((EntityTNTPrimed)entity).fuseTicks == this.fuseTicks - 1
         && this.fuseTicks > 0;
   }

   @Override
   protected void respawn() {
      if (this.potential > 1) {
         EntityTNTPrimed temp = new EntityTNTPrimed(
            new Location(this.world.getWorld(), this.lastX, this.lastY, this.lastZ), this.world, this.lastX, this.lastY, this.lastZ, this.source
         );
         temp.motX = this.lastMotX;
         temp.motY = this.lastMotY + 0.04F;
         temp.motZ = this.lastMotZ;
         temp.recalcPosition();
         Chunk chunk = this.world.getChunkAtWorldCoords(MathHelper.floor(temp.locX), MathHelper.floor(temp.locZ));
         chunk.addCannonEntity(temp);

         for (int i = 1; i < this.potential; i++) {
            this.explode();
            this.locX = this.lastX;
            this.locY = this.lastY;
            this.locZ = this.lastZ;
            this.motX = temp.motX;
            this.motY = temp.motY;
            this.motZ = temp.motZ;
            this.a(temp.boundingBox);
            this.fuseTicks = 1;
            this.t_();
         }

         chunk.removeCannonEntity(temp, temp.af);
      }
   }

   private void explode() {
      ChunkProviderServer chunkProviderServer = (ChunkProviderServer)this.world.chunkProvider;
      boolean forceChunkLoad = chunkProviderServer.forceChunkLoad;
      if (this.world.paperSpigotConfig.loadUnloadedTNTEntities) {
         chunkProviderServer.forceChunkLoad = true;
      }

      CraftServer server = this.world.getServer();
      ExplosionPrimeEvent event = new ExplosionPrimeEvent((Explosive)CraftEntity.getEntity(server, this));
      server.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         this.world.createExplosion(this, this.locX, this.locY + this.length / 2.0F, this.locZ, event.getRadius(), event.getFire(), true);
      }

      if (this.world.paperSpigotConfig.loadUnloadedTNTEntities) {
         chunkProviderServer.forceChunkLoad = forceChunkLoad;
      }
   }

   @Override
   protected void b(NBTTagCompound nbttagcompound) {
      nbttagcompound.setByte("Fuse", (byte)this.fuseTicks);
      if (this.sourceLoc != null) {
         nbttagcompound.setInt("SourceLoc_x", this.sourceLoc.getBlockX());
         nbttagcompound.setInt("SourceLoc_y", this.sourceLoc.getBlockY());
         nbttagcompound.setInt("SourceLoc_z", this.sourceLoc.getBlockZ());
      }
   }

   @Override
   protected void a(NBTTagCompound nbttagcompound) {
      this.fuseTicks = nbttagcompound.getByte("Fuse");
      if (nbttagcompound.hasKey("SourceLoc_x")) {
         this.sourceLoc = new Location(
            this.world.getWorld(), nbttagcompound.getInt("SourceLoc_x"), nbttagcompound.getInt("SourceLoc_y"), nbttagcompound.getInt("SourceLoc_z")
         );
      }
   }

   public EntityLiving getSource() {
      return this.source;
   }

   @Override
   public double f(double d0, double d1, double d2) {
      double d3 = this.locX - d0;
      double d4 = this.locY + this.getHeadHeight() - d1;
      double d5 = this.locZ - d2;
      return MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
   }

   @Override
   public boolean aL() {
      return !this.world.paperSpigotConfig.fixCannons && super.aL();
   }

   @Override
   public float getHeadHeight() {
      return this.world.paperSpigotConfig.fixCannons ? this.length / 2.0F : 0.0F;
   }

   @Override
   public boolean W() {
      return this.world.paperSpigotConfig.fixCannons ? this.inWater : super.W();
   }

   @Override
   public void move(double d0, double d1, double d2) {
      if (this.loadChunks) {
         this.loadChunks();
      }

      if (this.noclip) {
         this.a(this.boundingBox.c(d0, d1, d2));
         this.recalcPosition();
      } else {
         this.lastMotX = this.motX;
         this.lastMotY = this.motY;
         this.lastMotZ = this.motZ;
         if (this.world.movementCache.move(this)) {
            return;
         }

         try {
            this.checkBlockCollisions();
         } catch (Throwable var27) {
            CrashReport crashreport = CrashReport.a(var27, "Checking entity block collision");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");
            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
         }

         if (d0 == 0.0 && d1 == 0.0 && d2 == 0.0 && this.vehicle == null && this.passenger == null) {
            return;
         }

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
         AxisAlignedBB totalArea = this.boundingBox.a(d0, d1, d2);
         double xLength = totalArea.d - totalArea.a;
         double yLength = totalArea.e - totalArea.b;
         double zLength = totalArea.f - totalArea.c;
         boolean axisScan = (Boolean)BlazeConfig.optimizeTNTMovement.getValue() && xLength * yLength * zLength > 10.0;
         List<AxisAlignedBB> list = this.world.getCubes(this, axisScan ? this.boundingBox.a(0.0, d1, 0.0) : totalArea);

         for (AxisAlignedBB axisalignedbb1 : list) {
            d1 = axisalignedbb1.b(this.boundingBox, d1);
         }

         this.a(this.boundingBox.c(0.0, d1, 0.0));
         if ((Boolean)BlazeConfig.fixEastWestCannon.getValue() && Math.abs(d0) > Math.abs(d2)) {
            if (axisScan) {
               list = this.world.getCubes(this, this.boundingBox.a(0.0, 0.0, d2));
            }

            for (AxisAlignedBB axisalignedbb2 : list) {
               d2 = axisalignedbb2.c(this.boundingBox, d2);
            }

            this.a(this.boundingBox.c(0.0, 0.0, d2));
            if (axisScan) {
               list = this.world.getCubes(this, this.boundingBox.a(d0, 0.0, 0.0));
            }

            for (AxisAlignedBB axisalignedbb2 : list) {
               d0 = axisalignedbb2.a(this.boundingBox, d0);
            }

            this.a(this.boundingBox.c(d0, 0.0, 0.0));
         } else {
            if (axisScan) {
               list = this.world.getCubes(this, this.boundingBox.a(d0, 0.0, 0.0));
            }

            for (AxisAlignedBB axisalignedbb2 : list) {
               d0 = axisalignedbb2.a(this.boundingBox, d0);
            }

            this.a(this.boundingBox.c(d0, 0.0, 0.0));
            if (axisScan) {
               list = this.world.getCubes(this, this.boundingBox.a(0.0, 0.0, d2));
            }

            for (AxisAlignedBB axisalignedbb2 : list) {
               d2 = axisalignedbb2.c(this.boundingBox, d2);
            }

            this.a(this.boundingBox.c(0.0, 0.0, d2));
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

         this.world.movementCache.cache(this);
      }
   }

   @Override
   public EntityTrackerEntry getEntry(int i, int j, boolean flag) {
      return new EntityTrackerCannonEntry(this, i, j, flag);
   }
}
