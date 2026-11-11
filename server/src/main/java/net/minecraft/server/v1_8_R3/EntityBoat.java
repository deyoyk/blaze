package net.minecraft.server.v1_8_R3;

import java.util.List;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;

public class EntityBoat extends Entity {
   private double b;
   public double maxSpeed = 0.4;
   public double occupiedDeceleration = 0.2;
   public double unoccupiedDeceleration = -1.0;
   public boolean landBoats = false;

   @Override
   public void collide(Entity entity) {
      org.bukkit.entity.Entity hitEntity = entity == null ? null : entity.getBukkitEntity();
      VehicleEntityCollisionEvent event = new VehicleEntityCollisionEvent((Vehicle)this.getBukkitEntity(), hitEntity);
      this.world.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         super.collide(entity);
      }
   }

   public EntityBoat(World world) {
      super(world);
      this.b = 0.07;
      this.k = true;
      this.setSize(1.5F, 0.6F);
   }

   @Override
   protected boolean s_() {
      return false;
   }

   @Override
   protected void h() {
      this.datawatcher.a(17, 0);
      this.datawatcher.a(18, 1);
      this.datawatcher.a(19, 0.0F);
   }

   @Override
   public AxisAlignedBB j(Entity entity) {
      return entity.getBoundingBox();
   }

   @Override
   public AxisAlignedBB S() {
      return this.boundingBox;
   }

   @Override
   public boolean ae() {
      return true;
   }

   public EntityBoat(World world, double d0, double d1, double d2) {
      this(world);
      this.setPosition(d0, d1, d2);
      this.motX = 0.0;
      this.motY = 0.0;
      this.motZ = 0.0;
      this.lastX = d0;
      this.lastY = d1;
      this.lastZ = d2;
      this.world.getServer().getPluginManager().callEvent(new VehicleCreateEvent((Vehicle)this.getBukkitEntity()));
   }

   @Override
   public double an() {
      return -0.3;
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (this.isInvulnerable(damagesource)) {
         return false;
      } else if (this.dead) {
         return true;
      } else if (this.passenger != null && this.passenger == damagesource.getEntity() && damagesource instanceof EntityDamageSourceIndirect) {
         return false;
      } else {
         Vehicle vehicle = (Vehicle)this.getBukkitEntity();
         org.bukkit.entity.Entity attacker = damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity();
         VehicleDamageEvent event = new VehicleDamageEvent(vehicle, attacker, f);
         this.world.getServer().getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return false;
         } else {
            this.b(-this.m());
            this.a(10);
            this.setDamage(this.j() + f * 10.0F);
            this.ac();
            boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman)damagesource.getEntity()).abilities.canInstantlyBuild;
            if (flag || this.j() > 40.0F) {
               VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, attacker);
               this.world.getServer().getPluginManager().callEvent(destroyEvent);
               if (destroyEvent.isCancelled()) {
                  this.setDamage(40.0F);
                  return true;
               }

               if (this.passenger != null) {
                  this.passenger.mount(this);
               }

               if (!flag && this.world.getGameRules().getBoolean("doEntityDrops")) {
                  this.a(Items.BOAT, 1, 0.0F);
               }

               this.die();
            }

            return true;
         }
      }
   }

   @Override
   public boolean ad() {
      return !this.dead;
   }

   @Override
   public void t_() {
      double prevX = this.locX;
      double prevY = this.locY;
      double prevZ = this.locZ;
      float prevYaw = this.yaw;
      float prevPitch = this.pitch;
      super.t_();
      if (this.l() > 0) {
         this.a(this.l() - 1);
      }

      if (this.j() > 0.0F) {
         this.setDamage(this.j() - 1.0F);
      }

      this.lastX = this.locX;
      this.lastY = this.locY;
      this.lastZ = this.locZ;
      byte b0 = 5;
      double d0 = 0.0;

      for (int i = 0; i < b0; i++) {
         double d1 = this.boundingBox.b + (this.boundingBox.e - this.boundingBox.b) * i / b0 - 0.125;
         double d2 = this.boundingBox.b + (this.boundingBox.e - this.boundingBox.b) * (i + 1) / b0 - 0.125;
         AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.boundingBox.a, d1, this.boundingBox.c, this.boundingBox.d, d2, this.boundingBox.f);
         if (this.world.b(axisalignedbb, Material.WATER)) {
            d0 += 1.0 / b0;
         }
      }

      double d3 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
      if (d3 > 0.2975) {
         double d4 = FastMath.cos(this.yaw * Math.PI / 180.0);
         double d5 = FastMath.sin(this.yaw * Math.PI / 180.0);

         for (int j = 0; j < 1.0 + d3 * 60.0; j++) {
            double d6 = this.random.nextFloat() * 2.0F - 1.0F;
            double d7 = (this.random.nextInt(2) * 2 - 1) * 0.7;
            if (this.random.nextBoolean()) {
               double d8 = this.locX - d4 * d6 * 0.8 + d5 * d7;
               double d9 = this.locZ - d5 * d6 * 0.8 - d4 * d7;
               this.world.addParticle(EnumParticle.WATER_SPLASH, d8, this.locY - 0.125, d9, this.motX, this.motY, this.motZ, EnumParticle.EMPTY_ARRAY);
            } else {
               double d8 = this.locX + d4 + d5 * d6 * 0.7;
               double d9 = this.locZ + d5 - d4 * d6 * 0.7;
               this.world.addParticle(EnumParticle.WATER_SPLASH, d8, this.locY - 0.125, d9, this.motX, this.motY, this.motZ, EnumParticle.EMPTY_ARRAY);
            }
         }
      }

      if (d0 < 1.0) {
         double d4 = d0 * 2.0 - 1.0;
         this.motY += 0.04F * d4;
      } else {
         if (this.motY < 0.0) {
            this.motY /= 2.0;
         }

         this.motY += 0.007F;
      }

      if (this.passenger instanceof EntityLiving) {
         EntityLiving entityliving = (EntityLiving)this.passenger;
         float f = this.passenger.yaw + -entityliving.aZ * 90.0F;
         this.motX = this.motX + -FastMath.sin(f * (float) Math.PI / 180.0F) * this.b * entityliving.ba * 0.05F;
         this.motZ = this.motZ + FastMath.cos(f * (float) Math.PI / 180.0F) * this.b * entityliving.ba * 0.05F;
      } else if (this.unoccupiedDeceleration >= 0.0) {
         this.motX = this.motX * this.unoccupiedDeceleration;
         this.motZ = this.motZ * this.unoccupiedDeceleration;
         if (this.motX <= 1.0E-5) {
            this.motX = 0.0;
         }

         if (this.motZ <= 1.0E-5) {
            this.motZ = 0.0;
         }
      }

      double d4 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
      if (d4 > 0.35) {
         double d5 = 0.35 / d4;
         this.motX *= d5;
         this.motZ *= d5;
         d4 = 0.35;
      }

      if (d4 > d3 && this.b < 0.35) {
         this.b = this.b + (0.35 - this.b) / 35.0;
         if (this.b > 0.35) {
            this.b = 0.35;
         }
      } else {
         this.b = this.b - (this.b - 0.07) / 35.0;
         if (this.b < 0.07) {
            this.b = 0.07;
         }
      }

      BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition();

      for (int k = 0; k < 4; k++) {
         int l = MathHelper.floor(this.locX + (k % 2 - 0.5) * 0.8);
         int jx = MathHelper.floor(this.locZ + (k / 2 - 0.5) * 0.8);

         for (int i1 = 0; i1 < 2; i1++) {
            int j1 = MathHelper.floor(this.locY) + i1;
            Block block = this.world.getType(l, j1, jx).getBlock();
            if (block == Blocks.SNOW_LAYER) {
               if (!CraftEventFactory.callEntityChangeBlockEvent(this, l, j1, jx, Blocks.AIR, 0).isCancelled()) {
                  this.world.setAir(pos.c(l, j1, jx));
                  this.positionChanged = false;
               }
            } else if (block == Blocks.WATERLILY && !CraftEventFactory.callEntityChangeBlockEvent(this, l, j1, jx, Blocks.AIR, 0).isCancelled()) {
               this.world.setAir(pos.c(l, j1, jx), true);
               this.positionChanged = false;
            }
         }
      }

      if (this.onGround && !this.landBoats) {
         this.motX *= 0.5;
         this.motY *= 0.5;
         this.motZ *= 0.5;
      }

      this.move(this.motX, this.motY, this.motZ);
      if (!this.positionChanged || !(d3 > 0.2975)) {
         this.motX *= 0.99F;
         this.motY *= 0.95F;
         this.motZ *= 0.99F;
      } else if (!this.dead) {
         Vehicle vehicle = (Vehicle)this.getBukkitEntity();
         VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, null);
         this.world.getServer().getPluginManager().callEvent(destroyEvent);
         if (!destroyEvent.isCancelled()) {
            this.die();
            if (this.world.getGameRules().getBoolean("doEntityDrops")) {
               this.breakNaturally();
            }
         }
      }

      this.pitch = 0.0F;
      double d5 = this.yaw;
      double d10 = this.lastX - this.locX;
      double d11 = this.lastZ - this.locZ;
      if (d10 * d10 + d11 * d11 > 0.001) {
         d5 = (float)(MathHelper.b(d11, d10) * 180.0 / Math.PI);
      }

      double d12 = MathHelper.g(d5 - this.yaw);
      if (d12 > 20.0) {
         d12 = 20.0;
      }

      if (d12 < -20.0) {
         d12 = -20.0;
      }

      this.yaw = (float)(this.yaw + d12);
      this.setYawPitch(this.yaw, this.pitch);
      Server server = this.world.getServer();
      org.bukkit.World bworld = this.world.getWorld();
      Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
      Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
      Vehicle vehicle = (Vehicle)this.getBukkitEntity();
      server.getPluginManager().callEvent(new VehicleUpdateEvent(vehicle));
      if (!from.equals(to)) {
         VehicleMoveEvent event = new VehicleMoveEvent(vehicle, from, to);
         server.getPluginManager().callEvent(event);
      }

      List<Entity> list = this.world.getEntities(this, this.boundingBox.grow(0.2F, 0.0, 0.2F));
      if (list != null && !list.isEmpty()) {
         for (Entity entity : list) {
            if (entity != this.passenger && entity.ae() && entity instanceof EntityBoat) {
               entity.collide(this);
            }
         }
      }

      if (this.passenger != null && this.passenger.dead) {
         this.passenger.vehicle = null;
         this.passenger = null;
      }
   }

   @Override
   public void al() {
      if (this.passenger != null) {
         this.passenger
            .setPosition(
               this.locX + FastMath.cos(this.yaw * Math.PI / 180.0) * 0.4,
               this.locY + this.an() + this.passenger.am(),
               this.locZ + FastMath.sin(this.yaw * Math.PI / 180.0) * 0.4
            );
      }
   }

   @Override
   protected void b(NBTTagCompound nbttagcompound) {
   }

   @Override
   protected void a(NBTTagCompound nbttagcompound) {
   }

   @Override
   public boolean e(EntityHuman entityhuman) {
      if (this.passenger == null || !(this.passenger instanceof EntityPlayer) || this.passenger == entityhuman) {
         entityhuman.mount(this);
      }

      return true;
   }

   @Override
   protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
      if (flag) {
         if (this.fallDistance > 3.0F) {
            this.e(this.fallDistance, 1.0F);
            if (!this.dead) {
               Vehicle vehicle = (Vehicle)this.getBukkitEntity();
               VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, null);
               this.world.getServer().getPluginManager().callEvent(destroyEvent);
               if (!destroyEvent.isCancelled()) {
                  this.die();
                  if (this.world.getGameRules().getBoolean("doEntityDrops")) {
                     this.breakNaturally();
                  }
               }
            }

            this.fallDistance = 0.0F;
         }
      } else if (this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.locY) - 1, MathHelper.floor(this.locZ)).getBlock().getMaterial()
            != Material.WATER
         && d0 < 0.0) {
         this.fallDistance = (float)(this.fallDistance - d0);
      }
   }

   public void setDamage(float f) {
      this.datawatcher.watch(19, f);
   }

   public float j() {
      return this.datawatcher.getFloat(19);
   }

   public void a(int i) {
      this.datawatcher.watch(17, i);
   }

   public int l() {
      return this.datawatcher.getInt(17);
   }

   public void b(int i) {
      this.datawatcher.watch(18, i);
   }

   public int m() {
      return this.datawatcher.getInt(18);
   }

   public void breakNaturally() {
      if (this.world.paperSpigotConfig.boatsDropBoats) {
         this.a(Items.BOAT, 1, 0.0F);
      } else {
         for (int k = 0; k < 3; k++) {
            this.a(Item.getItemOf(Blocks.PLANKS), 1, 0.0F);
         }

         for (int k = 0; k < 2; k++) {
            this.a(Items.STICK, 1, 0.0F);
         }
      }
   }
}
