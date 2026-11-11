package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Map;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

public abstract class EntityMinecartAbstract extends Entity implements INamableTileEntity {
   private boolean a;
   private String b;
   private static final int[][][] matrix = new int[][][]{
      {{0, 0, -1}, {0, 0, 1}},
      {{-1, 0, 0}, {1, 0, 0}},
      {{-1, -1, 0}, {1, 0, 0}},
      {{-1, 0, 0}, {1, -1, 0}},
      {{0, 0, -1}, {0, -1, 1}},
      {{0, -1, -1}, {0, 0, 1}},
      {{0, 0, 1}, {1, 0, 0}},
      {{0, 0, 1}, {-1, 0, 0}},
      {{0, 0, -1}, {-1, 0, 0}},
      {{0, 0, -1}, {1, 0, 0}}
   };
   public boolean slowWhenEmpty = true;
   private double derailedX = 0.5;
   private double derailedY = 0.5;
   private double derailedZ = 0.5;
   private double flyingX = 0.95;
   private double flyingY = 0.95;
   private double flyingZ = 0.95;
   public double maxSpeed = 0.4;

   public EntityMinecartAbstract(World world) {
      super(world);
      this.k = true;
      this.setSize(0.98F, 0.7F);
   }

   public static EntityMinecartAbstract a(
      World world, double d0, double d1, double d2, EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype
   ) {
      switch (EntityMinecartAbstract.SyntheticClass_1.a[entityminecartabstract_enumminecarttype.ordinal()]) {
         case 1:
            return new EntityMinecartChest(world, d0, d1, d2);
         case 2:
            return new EntityMinecartFurnace(world, d0, d1, d2);
         case 3:
            return new EntityMinecartTNT(world, d0, d1, d2);
         case 4:
            return new EntityMinecartMobSpawner(world, d0, d1, d2);
         case 5:
            return new EntityMinecartHopper(world, d0, d1, d2);
         case 6:
            return new EntityMinecartCommandBlock(world, d0, d1, d2);
         default:
            return new EntityMinecartRideable(world, d0, d1, d2);
      }
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
      this.datawatcher.a(20, 0);
      this.datawatcher.a(21, 6);
      this.datawatcher.a(22, (byte)0);
   }

   @Override
   public AxisAlignedBB j(Entity entity) {
      return entity.ae() ? entity.getBoundingBox() : null;
   }

   @Override
   public AxisAlignedBB S() {
      return null;
   }

   @Override
   public boolean ae() {
      return true;
   }

   public EntityMinecartAbstract(World world, double d0, double d1, double d2) {
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
      return 0.0;
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (this.dead) {
         return true;
      } else if (this.isInvulnerable(damagesource)) {
         return false;
      } else {
         Vehicle vehicle = (Vehicle)this.getBukkitEntity();
         org.bukkit.entity.Entity passenger = damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity();
         VehicleDamageEvent event = new VehicleDamageEvent(vehicle, passenger, f);
         this.world.getServer().getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return false;
         } else {
            f = (float)event.getDamage();
            this.k(-this.r());
            this.j(10);
            this.ac();
            this.setDamage(this.getDamage() + f * 10.0F);
            boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman)damagesource.getEntity()).abilities.canInstantlyBuild;
            if (flag || this.getDamage() > 40.0F) {
               VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, passenger);
               this.world.getServer().getPluginManager().callEvent(destroyEvent);
               if (destroyEvent.isCancelled()) {
                  this.setDamage(40.0F);
                  return true;
               }

               if (this.passenger != null) {
                  this.passenger.mount(null);
               }

               if (flag && !this.hasCustomName()) {
                  this.die();
               } else {
                  this.a(damagesource);
               }
            }

            return true;
         }
      }
   }

   public void a(DamageSource damagesource) {
      this.die();
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemstack = new ItemStack(Items.MINECART, 1);
         if (this.b != null) {
            itemstack.c(this.b);
         }

         this.a(itemstack, 0.0F);
      }
   }

   @Override
   public boolean ad() {
      return !this.dead;
   }

   @Override
   public void die() {
      super.die();
   }

   @Override
   public void t_() {
      double prevX = this.locX;
      double prevY = this.locY;
      double prevZ = this.locZ;
      float prevYaw = this.yaw;
      float prevPitch = this.pitch;
      if (this.getType() > 0) {
         this.j(this.getType() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.locY < -64.0) {
         this.O();
      }

      if (this.world instanceof WorldServer) {
         MinecraftServer minecraftserver = ((WorldServer)this.world).getMinecraftServer();
         int i = this.L();
         if (this.ak) {
            if (minecraftserver.getAllowNether()) {
               if (this.vehicle == null && this.al++ >= i) {
                  this.al = i;
                  this.portalCooldown = this.aq();
                  byte b0;
                  if (this.world.worldProvider.getDimension() == -1) {
                     b0 = 0;
                  } else {
                     b0 = -1;
                  }

                  this.c(b0);
               }

               this.ak = false;
            }
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

      this.lastX = this.locX;
      this.lastY = this.locY;
      this.lastZ = this.locZ;
      this.motY -= 0.04F;
      int j = MathHelper.floor(this.locX);
      int ix = MathHelper.floor(this.locY);
      int k = MathHelper.floor(this.locZ);
      if (BlockMinecartTrackAbstract.e(this.world, j, ix - 1, k)) {
         ix--;
      }

      IBlockData iblockdata = this.world.getType(j, ix, k);
      if (BlockMinecartTrackAbstract.d(iblockdata)) {
         this.a(j, ix, k, iblockdata);
         if (iblockdata.getBlock() == Blocks.ACTIVATOR_RAIL) {
            this.a(j, ix, k, iblockdata.get(BlockPoweredRail.POWERED));
         }
      } else {
         this.n();
      }

      this.checkBlockCollisions();
      this.pitch = 0.0F;
      double d4 = this.lastX - this.locX;
      double d5 = this.lastZ - this.locZ;
      if (d4 * d4 + d5 * d5 > 0.001) {
         this.yaw = (float)(MathHelper.b(d5, d4) * 180.0 / Math.PI);
         if (this.a) {
            this.yaw += 180.0F;
         }
      }

      double d6 = MathHelper.g(this.yaw - this.lastYaw);
      if (d6 < -170.0 || d6 >= 170.0) {
         this.yaw += 180.0F;
         this.a = !this.a;
      }

      this.setYawPitch(this.yaw, this.pitch);
      org.bukkit.World bworld = this.world.getWorld();
      Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
      Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
      Vehicle vehicle = (Vehicle)this.getBukkitEntity();
      this.world.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(vehicle));
      if (!from.equals(to)) {
         this.world.getServer().getPluginManager().callEvent(new VehicleMoveEvent(vehicle, from, to));
      }

      for (Entity entity : this.world.getEntities(this, this.getBoundingBox().grow(0.2F, 0.0, 0.2F))) {
         if (entity != this.passenger && entity.ae() && entity instanceof EntityMinecartAbstract) {
            entity.collide(this);
         }
      }

      if (this.passenger != null && this.passenger.dead) {
         if (this.passenger.vehicle == this) {
            this.passenger.vehicle = null;
         }

         this.passenger = null;
      }

      this.W();
   }

   protected double m() {
      return this.maxSpeed;
   }

   public void a(int i, int j, int k, boolean flag) {
   }

   protected void n() {
      double d0 = this.m();
      this.motX = MathHelper.a(this.motX, -d0, d0);
      this.motZ = MathHelper.a(this.motZ, -d0, d0);
      if (this.onGround) {
         this.motX = this.motX * this.derailedX;
         this.motY = this.motY * this.derailedY;
         this.motZ = this.motZ * this.derailedZ;
      }

      this.move(this.motX, this.motY, this.motZ);
      if (!this.onGround) {
         this.motX = this.motX * this.flyingX;
         this.motY = this.motY * this.flyingY;
         this.motZ = this.motZ * this.flyingZ;
      }
   }

   protected void a(int x, int y, int z, IBlockData iblockdata) {
      this.fallDistance = 0.0F;
      Vec3D vec3d = this.k(this.locX, this.locY, this.locZ);
      this.locY = y;
      boolean flag = false;
      boolean flag1 = false;
      BlockMinecartTrackAbstract blockminecarttrackabstract = (BlockMinecartTrackAbstract)iblockdata.getBlock();
      if (blockminecarttrackabstract == Blocks.GOLDEN_RAIL) {
         flag = iblockdata.get(BlockPoweredRail.POWERED);
         flag1 = !flag;
      }

      BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = iblockdata.get(blockminecarttrackabstract.n());
      switch (EntityMinecartAbstract.SyntheticClass_1.b[blockminecarttrackabstract_enumtrackposition.ordinal()]) {
         case 1:
            this.motX -= 0.0078125;
            this.locY++;
            break;
         case 2:
            this.motX += 0.0078125;
            this.locY++;
            break;
         case 3:
            this.motZ += 0.0078125;
            this.locY++;
            break;
         case 4:
            this.motZ -= 0.0078125;
            this.locY++;
      }

      int[][] aint = matrix[blockminecarttrackabstract_enumtrackposition.a()];
      double d1 = aint[1][0] - aint[0][0];
      double d2 = aint[1][2] - aint[0][2];
      double d3 = Math.sqrt(d1 * d1 + d2 * d2);
      double d4 = this.motX * d1 + this.motZ * d2;
      if (d4 < 0.0) {
         d1 = -d1;
         d2 = -d2;
      }

      double d5 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
      if (d5 > 2.0) {
         d5 = 2.0;
      }

      this.motX = d5 * d1 / d3;
      this.motZ = d5 * d2 / d3;
      if (this.passenger instanceof EntityLiving) {
         double d6 = ((EntityLiving)this.passenger).ba;
         if (d6 > 0.0) {
            double d7 = -FastMath.sin(this.passenger.yaw * (float) Math.PI / 180.0F);
            double d8 = FastMath.cos(this.passenger.yaw * (float) Math.PI / 180.0F);
            double d9 = this.motX * this.motX + this.motZ * this.motZ;
            if (d9 < 0.01) {
               this.motX += d7 * 0.1;
               this.motZ += d8 * 0.1;
               flag1 = false;
            }
         }
      }

      if (flag1) {
         double d6 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
         if (d6 < 0.03) {
            this.motX *= 0.0;
            this.motY *= 0.0;
            this.motZ *= 0.0;
         } else {
            this.motX *= 0.5;
            this.motY *= 0.0;
            this.motZ *= 0.5;
         }
      }

      double d7 = x + 0.5 + aint[0][0] * 0.5;
      double d8 = z + 0.5 + aint[0][2] * 0.5;
      double d9 = x + 0.5 + aint[1][0] * 0.5;
      double d10 = z + 0.5 + aint[1][2] * 0.5;
      d1 = d9 - d7;
      d2 = d10 - d8;
      double d6;
      if (d1 == 0.0) {
         d6 = this.locZ - z;
      } else if (d2 == 0.0) {
         d6 = this.locX - x;
      } else {
         double d11 = this.locX - d7;
         double d12 = this.locZ - d8;
         d6 = (d11 * d1 + d12 * d2) * 2.0;
      }

      this.locX = d7 + d1 * d6;
      this.locZ = d8 + d2 * d6;
      this.setPosition(this.locX, this.locY, this.locZ);
      double d11 = this.motX;
      double d12 = this.motZ;
      if (this.passenger != null) {
         d11 *= 0.75;
         d12 *= 0.75;
      }

      double d13 = this.m();
      d11 = MathHelper.a(d11, -d13, d13);
      d12 = MathHelper.a(d12, -d13, d13);
      this.move(d11, 0.0, d12);
      if (aint[0][1] != 0 && MathHelper.floor(this.locX) - x == aint[0][0] && MathHelper.floor(this.locZ) - z == aint[0][2]) {
         this.setPosition(this.locX, this.locY + aint[0][1], this.locZ);
      } else if (aint[1][1] != 0 && MathHelper.floor(this.locX) - x == aint[1][0] && MathHelper.floor(this.locZ) - z == aint[1][2]) {
         this.setPosition(this.locX, this.locY + aint[1][1], this.locZ);
      }

      this.o();
      Vec3D vec3d1 = this.k(this.locX, this.locY, this.locZ);
      if (vec3d1 != null && vec3d != null) {
         double d14 = (vec3d.b - vec3d1.b) * 0.05;
         d5 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
         if (d5 > 0.0) {
            this.motX = this.motX / d5 * (d5 + d14);
            this.motZ = this.motZ / d5 * (d5 + d14);
         }

         this.setPosition(this.locX, vec3d1.b, this.locZ);
      }

      int i = MathHelper.floor(this.locX);
      int j = MathHelper.floor(this.locZ);
      if (i != x || j != z) {
         d5 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
         this.motX = d5 * (i - x);
         this.motZ = d5 * (j - z);
      }

      if (flag) {
         double d15 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
         if (d15 > 0.01) {
            double d16 = 0.06;
            this.motX = this.motX + this.motX / d15 * d16;
            this.motZ = this.motZ + this.motZ / d15 * d16;
         } else if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST) {
            EnumDirection w = EnumDirection.WEST;
            EnumDirection e = EnumDirection.EAST;
            if (this.world.getType(x + w.getAdjacentX() * i, y + w.getAdjacentY() * i, z + w.getAdjacentZ()).getBlock().isOccluding()) {
               this.motX = 0.02;
            } else if (this.world.getType(x + e.getAdjacentX() * i, y + e.getAdjacentY() * i, z + e.getAdjacentZ()).getBlock().isOccluding()) {
               this.motX = -0.02;
            }
         } else if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH) {
            EnumDirection w = EnumDirection.NORTH;
            EnumDirection e = EnumDirection.SOUTH;
            if (this.world.getType(x + w.getAdjacentX() * i, y + w.getAdjacentY() * i, z + w.getAdjacentZ()).getBlock().isOccluding()) {
               this.motX = 0.02;
            } else if (this.world.getType(x + e.getAdjacentX() * i, y + e.getAdjacentY() * i, z + e.getAdjacentZ()).getBlock().isOccluding()) {
               this.motX = -0.02;
            }
         }
      }
   }

   protected void o() {
      if (this.passenger == null && this.slowWhenEmpty) {
         this.motX *= 0.96F;
         this.motY *= 0.0;
         this.motZ *= 0.96F;
      } else {
         this.motX *= 0.997F;
         this.motY *= 0.0;
         this.motZ *= 0.997F;
      }
   }

   @Override
   public void setPosition(double d0, double d1, double d2) {
      this.locX = d0;
      this.locY = d1;
      this.locZ = d2;
      float f = this.width / 2.0F;
      float f1 = this.length;
      this.a(new AxisAlignedBB(d0 - f, d1, d2 - f, d0 + f, d1 + f1, d2 + f));
   }

   public Vec3D k(double d0, double d1, double d2) {
      int i = MathHelper.floor(d0);
      int j = MathHelper.floor(d1);
      int k = MathHelper.floor(d2);
      if (BlockMinecartTrackAbstract.e(this.world, i, j - 1, k)) {
         j--;
      }

      IBlockData iblockdata = this.world.getType(i, j, k);
      if (BlockMinecartTrackAbstract.d(iblockdata)) {
         BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = iblockdata.get(
            ((BlockMinecartTrackAbstract)iblockdata.getBlock()).n()
         );
         int[][] aint = matrix[blockminecarttrackabstract_enumtrackposition.a()];
         double d4 = i + 0.5 + aint[0][0] * 0.5;
         double d5 = j + 0.0625 + aint[0][1] * 0.5;
         double d6 = k + 0.5 + aint[0][2] * 0.5;
         double d7 = i + 0.5 + aint[1][0] * 0.5;
         double d8 = j + 0.0625 + aint[1][1] * 0.5;
         double d9 = k + 0.5 + aint[1][2] * 0.5;
         double d10 = d7 - d4;
         double d11 = (d8 - d5) * 2.0;
         double d12 = d9 - d6;
         double d3;
         if (d10 == 0.0) {
            d3 = d2 - k;
         } else if (d12 == 0.0) {
            d3 = d0 - i;
         } else {
            double d13 = d0 - d4;
            double d14 = d2 - d6;
            d3 = (d13 * d10 + d14 * d12) * 2.0;
         }

         d0 = d4 + d10 * d3;
         d1 = d5 + d11 * d3;
         d2 = d6 + d12 * d3;
         if (d11 < 0.0) {
            d1++;
         }

         if (d11 > 0.0) {
            d1 += 0.5;
         }

         return new Vec3D(d0, d1, d2);
      } else {
         return null;
      }
   }

   @Override
   protected void a(NBTTagCompound nbttagcompound) {
      if (nbttagcompound.getBoolean("CustomDisplayTile")) {
         int i = nbttagcompound.getInt("DisplayData");
         Block block;
         if (nbttagcompound.hasKeyOfType("DisplayTile", 8)) {
            block = Block.getByName(nbttagcompound.getString("DisplayTile"));
         } else {
            block = Block.getById(nbttagcompound.getInt("DisplayTile"));
         }

         if (block == null) {
            this.setDisplayBlock(Blocks.AIR.getBlockData());
         } else {
            this.setDisplayBlock(block.fromLegacyData(i));
         }

         this.SetDisplayBlockOffset(nbttagcompound.getInt("DisplayOffset"));
      }

      if (nbttagcompound.hasKeyOfType("CustomName", 8) && nbttagcompound.getString("CustomName").length() > 0) {
         this.b = nbttagcompound.getString("CustomName");
      }
   }

   @Override
   protected void b(NBTTagCompound nbttagcompound) {
      if (this.x()) {
         nbttagcompound.setBoolean("CustomDisplayTile", true);
         IBlockData iblockdata = this.getDisplayBlock();
         MinecraftKey minecraftkey = Block.REGISTRY.c(iblockdata.getBlock());
         nbttagcompound.setString("DisplayTile", minecraftkey == null ? "" : minecraftkey.toString());
         nbttagcompound.setInt("DisplayData", iblockdata.getBlock().toLegacyData(iblockdata));
         nbttagcompound.setInt("DisplayOffset", this.getDisplayBlockOffset());
      }

      if (this.b != null && this.b.length() > 0) {
         nbttagcompound.setString("CustomName", this.b);
      }
   }

   @Override
   public void collide(Entity entity) {
      if (!entity.noclip && !this.noclip && entity != this.passenger) {
         Vehicle vehicle = (Vehicle)this.getBukkitEntity();
         org.bukkit.entity.Entity hitEntity = entity.getBukkitEntity();
         VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent(vehicle, hitEntity);
         this.world.getServer().getPluginManager().callEvent(collisionEvent);
         if (collisionEvent.isCancelled()) {
            return;
         }

         if (entity instanceof EntityLiving
            && !(entity instanceof EntityHuman)
            && !(entity instanceof EntityIronGolem)
            && this.s() == EntityMinecartAbstract.EnumMinecartType.RIDEABLE
            && this.motX * this.motX + this.motZ * this.motZ > 0.01
            && this.passenger == null
            && entity.vehicle == null) {
            entity.mount(this);
         }

         double d0 = entity.locX - this.locX;
         double d1 = entity.locZ - this.locZ;
         double d2 = d0 * d0 + d1 * d1;
         if (d2 >= 1.0E-4F && !collisionEvent.isCollisionCancelled()) {
            d2 = MathHelper.sqrt(d2);
            d0 /= d2;
            d1 /= d2;
            double d3 = 1.0 / d2;
            if (d3 > 1.0) {
               d3 = 1.0;
            }

            d0 *= d3;
            d1 *= d3;
            d0 *= 0.1F;
            d1 *= 0.1F;
            d0 *= 1.0F - this.U;
            d1 *= 1.0F - this.U;
            d0 *= 0.5;
            d1 *= 0.5;
            if (entity instanceof EntityMinecartAbstract) {
               double d4 = entity.locX - this.locX;
               double d5 = entity.locZ - this.locZ;
               Vec3D vec3d = new Vec3D(d4, 0.0, d5).a();
               Vec3D vec3d1 = new Vec3D(MathHelper.cos(this.yaw * (float) Math.PI / 180.0F), 0.0, MathHelper.sin(this.yaw * (float) Math.PI / 180.0F)).a();
               double d6 = Math.abs(vec3d.b(vec3d1));
               if (d6 < 0.8F) {
                  return;
               }

               double d7 = entity.motX + this.motX;
               double d8 = entity.motZ + this.motZ;
               if (((EntityMinecartAbstract)entity).s() == EntityMinecartAbstract.EnumMinecartType.FURNACE
                  && this.s() != EntityMinecartAbstract.EnumMinecartType.FURNACE) {
                  this.motX *= 0.2F;
                  this.motZ *= 0.2F;
                  this.g(entity.motX - d0, 0.0, entity.motZ - d1);
                  entity.motX *= 0.95F;
                  entity.motZ *= 0.95F;
               } else if (((EntityMinecartAbstract)entity).s() != EntityMinecartAbstract.EnumMinecartType.FURNACE
                  && this.s() == EntityMinecartAbstract.EnumMinecartType.FURNACE) {
                  entity.motX *= 0.2F;
                  entity.motZ *= 0.2F;
                  entity.g(this.motX + d0, 0.0, this.motZ + d1);
                  this.motX *= 0.95F;
                  this.motZ *= 0.95F;
               } else {
                  d7 /= 2.0;
                  d8 /= 2.0;
                  this.motX *= 0.2F;
                  this.motZ *= 0.2F;
                  this.g(d7 - d0, 0.0, d8 - d1);
                  entity.motX *= 0.2F;
                  entity.motZ *= 0.2F;
                  entity.g(d7 + d0, 0.0, d8 + d1);
               }
            } else {
               this.g(-d0, 0.0, -d1);
               entity.g(d0 / 4.0, 0.0, d1 / 4.0);
            }
         }
      }
   }

   public void setDamage(float f) {
      this.datawatcher.watch(19, f);
   }

   public float getDamage() {
      return this.datawatcher.getFloat(19);
   }

   public void j(int i) {
      this.datawatcher.watch(17, i);
   }

   public int getType() {
      return this.datawatcher.getInt(17);
   }

   public void k(int i) {
      this.datawatcher.watch(18, i);
   }

   public int r() {
      return this.datawatcher.getInt(18);
   }

   public abstract EntityMinecartAbstract.EnumMinecartType s();

   public IBlockData getDisplayBlock() {
      return !this.x() ? this.u() : Block.getByCombinedId(this.getDataWatcher().getInt(20));
   }

   public IBlockData u() {
      return Blocks.AIR.getBlockData();
   }

   public int getDisplayBlockOffset() {
      return !this.x() ? this.w() : this.getDataWatcher().getInt(21);
   }

   public int w() {
      return 6;
   }

   public void setDisplayBlock(IBlockData iblockdata) {
      this.getDataWatcher().watch(20, Block.getCombinedId(iblockdata));
      this.a(true);
   }

   public void SetDisplayBlockOffset(int i) {
      this.getDataWatcher().watch(21, i);
      this.a(true);
   }

   public boolean x() {
      return this.getDataWatcher().getByte(22) == 1;
   }

   public void a(boolean flag) {
      this.getDataWatcher().watch(22, (byte)(flag ? 1 : 0));
   }

   @Override
   public void setCustomName(String s) {
      this.b = s;
   }

   @Override
   public String getName() {
      return this.b != null ? this.b : super.getName();
   }

   @Override
   public boolean hasCustomName() {
      return this.b != null;
   }

   @Override
   public String getCustomName() {
      return this.b;
   }

   @Override
   public IChatBaseComponent getScoreboardDisplayName() {
      if (this.hasCustomName()) {
         ChatComponentText chatcomponenttext = new ChatComponentText(this.b);
         chatcomponenttext.getChatModifier().setChatHoverable(this.aQ());
         chatcomponenttext.getChatModifier().setInsertion(this.getUniqueID().toString());
         return chatcomponenttext;
      } else {
         ChatMessage chatmessage = new ChatMessage(this.getName());
         chatmessage.getChatModifier().setChatHoverable(this.aQ());
         chatmessage.getChatModifier().setInsertion(this.getUniqueID().toString());
         return chatmessage;
      }
   }

   public Vector getFlyingVelocityMod() {
      return new Vector(this.flyingX, this.flyingY, this.flyingZ);
   }

   public void setFlyingVelocityMod(Vector flying) {
      this.flyingX = flying.getX();
      this.flyingY = flying.getY();
      this.flyingZ = flying.getZ();
   }

   public Vector getDerailedVelocityMod() {
      return new Vector(this.derailedX, this.derailedY, this.derailedZ);
   }

   public void setDerailedVelocityMod(Vector derailed) {
      this.derailedX = derailed.getX();
      this.derailedY = derailed.getY();
      this.derailedZ = derailed.getZ();
   }

   public static enum EnumMinecartType {
      RIDEABLE(0, "MinecartRideable"),
      CHEST(1, "MinecartChest"),
      FURNACE(2, "MinecartFurnace"),
      TNT(3, "MinecartTNT"),
      SPAWNER(4, "MinecartSpawner"),
      HOPPER(5, "MinecartHopper"),
      COMMAND_BLOCK(6, "MinecartCommandBlock");

      private static final Map<Integer, EntityMinecartAbstract.EnumMinecartType> h = Maps.newHashMap();
      private final int i;
      private final String j;

      static {
         EntityMinecartAbstract.EnumMinecartType[] var3;
         for (EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype : var3 = values()) {
            h.put(entityminecartabstract_enumminecarttype.a(), entityminecartabstract_enumminecarttype);
         }
      }

      private EnumMinecartType(int i, String s) {
         this.i = i;
         this.j = s;
      }

      public int a() {
         return this.i;
      }

      public String b() {
         return this.j;
      }

      public static EntityMinecartAbstract.EnumMinecartType a(int i) {
         EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype = h.get(i);
         return entityminecartabstract_enumminecarttype == null ? RIDEABLE : entityminecartabstract_enumminecarttype;
      }
   }

   static class SyntheticClass_1 {
      static final int[] a;
      static final int[] b = new int[BlockMinecartTrackAbstract.EnumTrackPosition.values().length];

      static {
         try {
            b[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST.ordinal()] = 1;
         } catch (NoSuchFieldError var10) {
         }

         try {
            b[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST.ordinal()] = 2;
         } catch (NoSuchFieldError var9) {
         }

         try {
            b[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH.ordinal()] = 3;
         } catch (NoSuchFieldError var8) {
         }

         try {
            b[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH.ordinal()] = 4;
         } catch (NoSuchFieldError var7) {
         }

         a = new int[EntityMinecartAbstract.EnumMinecartType.values().length];

         try {
            a[EntityMinecartAbstract.EnumMinecartType.CHEST.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            a[EntityMinecartAbstract.EnumMinecartType.FURNACE.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
         }

         try {
            a[EntityMinecartAbstract.EnumMinecartType.TNT.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[EntityMinecartAbstract.EnumMinecartType.SPAWNER.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EntityMinecartAbstract.EnumMinecartType.HOPPER.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
