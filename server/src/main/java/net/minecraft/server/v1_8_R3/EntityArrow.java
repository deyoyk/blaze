package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.knockback.Knockback;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

public class EntityArrow extends Entity implements IProjectile {
   public boolean inGround = false;
   public int fromPlayer;
   public int shake;
   public Entity shooter;
   private float shooterYaw;
   public int knockbackStrength;
   private int d = -1;
   private int e = -1;
   private int f = -1;
   private Block g;
   private int h;
   private int ar;
   private int as;
   private double damage = 2.0;

   public EntityArrow(World world) {
      super(world);
      this.j = 10.0;
      this.setSize(0.5F, 0.5F);
   }

   public EntityArrow(World world, double d0, double d1, double d2) {
      super(world);
      this.j = 10.0;
      this.setSize(0.5F, 0.5F);
      this.setPosition(d0, d1, d2);
   }

   public EntityArrow(World world, EntityLiving entityliving, EntityLiving entityliving1, float f, float f1) {
      super(world);
      this.j = 10.0;
      this.shooter = entityliving;
      this.shooterYaw = this.shooter.yaw;
      this.projectileSource = (LivingEntity)entityliving.getBukkitEntity();
      if (entityliving instanceof EntityPlayer) {
         this.fromPlayer = 1;
      }

      this.locY = entityliving.locY + entityliving.getHeadHeight() - 0.1F;
      double d0 = entityliving1.locX - entityliving.locX;
      double d1 = entityliving1.boundingBox.b + entityliving1.length / 3.0F - this.locY;
      double d2 = entityliving1.locZ - entityliving.locZ;
      double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
      if (d3 >= 1.0E-7) {
         float f2 = (float)(MathHelper.b(d2, d0) * 180.0 / (float) Math.PI) - 90.0F;
         float f3 = (float)(-(MathHelper.b(d1, d3) * 180.0 / (float) Math.PI));
         this.setPositionRotation(entityliving.locX + d0 / d3, this.locY, entityliving.locZ + d2 / d3, f2, f3);
         this.shoot(d0, d1 + d3 * 0.2F, d2, f, f1);
      }
   }

   public EntityArrow(World world, EntityLiving entityliving, float f) {
      super(world);
      this.j = 10.0;
      this.shooter = entityliving;
      this.shooterYaw = this.shooter.yaw;
      this.projectileSource = (LivingEntity)entityliving.getBukkitEntity();
      if (entityliving instanceof EntityPlayer) {
         this.fromPlayer = 1;
      }

      this.setSize(0.5F, 0.5F);
      this.setPositionRotation(entityliving.locX, entityliving.locY + entityliving.getHeadHeight(), entityliving.locZ, entityliving.yaw, entityliving.pitch);
      this.locX = this.locX - MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F;
      this.locY -= 0.1F;
      this.locZ = this.locZ - MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F;
      this.setPosition(this.locX, this.locY, this.locZ);
      this.motX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI);
      this.motZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI);
      this.motY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI);
      this.shoot(this.motX, this.motY, this.motZ, f * 1.5F, (Float)BlazeConfig.arrowTrajectoryRandomness.getValue());
   }

   @Override
   public void inactiveTick() {
      if (this.inGround) {
         this.ar++;
      }

      super.inactiveTick();
   }

   @Override
   protected void h() {
      this.datawatcher.a(16, (byte)0);
   }

   @Override
   public void shoot(double d0, double d1, double d2, float f, float f1) {
      float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
      d0 /= f2;
      d1 /= f2;
      d2 /= f2;
      d0 += this.random.nextGaussian() * (this.random.nextBoolean() ? -1 : 1) * 0.0075F * f1;
      d1 += this.random.nextGaussian() * (this.random.nextBoolean() ? -1 : 1) * 0.0075F * f1;
      d2 += this.random.nextGaussian() * (this.random.nextBoolean() ? -1 : 1) * 0.0075F * f1;
      d0 *= f;
      d1 *= f;
      d2 *= f;
      this.motX = d0;
      this.motY = d1;
      this.motZ = d2;
      float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
      this.lastYaw = this.yaw = (float)(MathHelper.b(d0, d2) * 180.0 / (float) Math.PI);
      this.lastPitch = this.pitch = (float)(MathHelper.b(d1, f3) * 180.0 / (float) Math.PI);
      this.ar = 0;
   }

   @Override
   public void t_() {
      super.t_();
      if (this.lastPitch == 0.0F && this.lastYaw == 0.0F) {
         this.lastYaw = this.yaw = (float)(MathHelper.b(this.motX, this.motZ) * 180.0 / (float) Math.PI);
         this.lastPitch = this.pitch = (float)(
            MathHelper.b(this.motY, MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ)) * 180.0 / (float) Math.PI
         );
      }

      IBlockData iblockdata = this.world.getType(this.d, this.e, this.f);
      Block block = iblockdata.getBlock();
      if (block.getMaterial() != Material.AIR) {
         BlockPosition blockposition = new BlockPosition(this.d, this.e, this.f);
         block.updateShape(this.world, blockposition);
         AxisAlignedBB axisalignedbb = block.a(this.world, blockposition, iblockdata);
         if (axisalignedbb != null && axisalignedbb.a(new Vec3D(this.locX, this.locY, this.locZ))) {
            this.inGround = true;
         }
      }

      if (this.shake > 0) {
         this.shake--;
      }

      if (this.inGround) {
         int i = block.toLegacyData(iblockdata);
         if (block == this.g && i == this.h) {
            this.ar++;
            if (this.ar >= this.world.spigotConfig.arrowDespawnRate) {
               this.die();
            }
         } else {
            this.inGround = false;
            this.motX = this.motX * (this.random.nextFloat() * 0.2F);
            this.motY = this.motY * (this.random.nextFloat() * 0.2F);
            this.motZ = this.motZ * (this.random.nextFloat() * 0.2F);
            this.ar = 0;
            this.as = 0;
         }
      } else {
         this.as++;
         Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
         Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
         MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1, false, true, false);
         vec3d = new Vec3D(this.locX, this.locY, this.locZ);
         vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
         if (movingobjectposition != null) {
            vec3d1 = new Vec3D(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
         }

         Entity foundEntity = null;
         double d0 = 0.0;

         for (Entity entity1 : this.world.getEntities(this, this.boundingBox.a(this.motX, this.motY, this.motZ).grow(1.0, 1.0, 1.0))) {
            if (entity1.ad() && (entity1 != this.shooter || this.as >= 5)) {
               AxisAlignedBB axisalignedbb1 = entity1.boundingBox.grow(0.3, 0.3, 0.3);
               MovingObjectPosition movingobjectposition1 = axisalignedbb1.a(vec3d, vec3d1);
               if (movingobjectposition1 != null) {
                  double d1 = vec3d.distanceSquared(movingobjectposition1.pos);
                  if (d1 < d0 || d0 == 0.0) {
                     foundEntity = entity1;
                     d0 = d1;
                  }
               }
            }
         }

         if (foundEntity != null) {
            movingobjectposition = new MovingObjectPosition(foundEntity);
         }

         if (movingobjectposition != null && foundEntity instanceof EntityPlayer && this.shooter != null) {
            EntityHuman entityhuman = (EntityHuman)foundEntity;
            if (!entityhuman.abilities.isInvulnerable && (!(this.shooter instanceof EntityPlayer) || ((EntityHuman)this.shooter).a(entityhuman))) {
               if (this.shooter instanceof EntityPlayer
                  && !((EntityPlayer)this.shooter).getBukkitEntity().canSee(((EntityPlayer)foundEntity).getBukkitEntity())) {
                  movingobjectposition = null;
               }
            } else {
               movingobjectposition = null;
            }
         }

         if (movingobjectposition != null) {
            CraftEventFactory.callProjectileHitEvent(this);
            Entity entity = movingobjectposition.entity;
            if (entity == null) {
               BlockPosition blockposition1 = movingobjectposition.a();
               this.d = blockposition1.getX();
               this.e = blockposition1.getY();
               this.f = blockposition1.getZ();
               IBlockData iblockdata1 = this.world.getType(this.d, this.e, this.f);
               this.g = iblockdata1.getBlock();
               this.h = this.g.toLegacyData(iblockdata1);
               this.motX = (float)(movingobjectposition.pos.a - this.locX);
               this.motY = (float)(movingobjectposition.pos.b - this.locY);
               this.motZ = (float)(movingobjectposition.pos.c - this.locZ);
               double f1 = MathHelper.sqrt(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ);
               this.locX = this.locX - this.motX / f1 * 0.05F;
               this.locY = this.locY - this.motY / f1 * 0.05F;
               this.locZ = this.locZ - this.motZ / f1 * 0.05F;
               this.makeSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
               this.inGround = true;
               this.shake = 7;
               this.setCritical(false);
               if (this.g.getMaterial() != Material.AIR) {
                  this.g.a(this.world, blockposition1, iblockdata1, this);
               }
            } else {
               float f2 = MathHelper.sqrt(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ);
               int k = MathHelper.f(f2 * this.damage);
               if (this.isCritical()) {
                  k += this.random.nextInt(k / 2 + 2);
               }

               DamageSource damagesource;
               if (this.shooter == null) {
                  damagesource = DamageSource.arrow(this, this);
               } else {
                  damagesource = DamageSource.arrow(this, this.shooter);
               }

               damagesource.hitNanos = System.nanoTime();
               double victimMotX = entity.motX;
               double victimMotY = entity.motY;
               double victimMotZ = entity.motZ;
               if (!entity.damageEntity(damagesource, k)) {
                  this.motX *= -0.1F;
                  this.motY *= -0.1F;
                  this.motZ *= -0.1F;
                  this.yaw += 180.0F;
                  this.lastYaw += 180.0F;
                  this.as = 0;
               } else {
                  if (this.isBurning()
                     && !(entity instanceof EntityEnderman)
                     && (!(entity instanceof EntityPlayer) || !(this.shooter instanceof EntityPlayer) || this.world.pvpMode)) {
                     EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 5);
                     Bukkit.getPluginManager().callEvent(combustEvent);
                     if (!combustEvent.isCancelled()) {
                        entity.setOnFire(combustEvent.getDuration());
                     }
                  }

                  if (entity instanceof EntityLiving) {
                     EntityLiving entityliving = (EntityLiving)entity;
                     entityliving.o(entityliving.bv() + 1);
                     boolean overrided = false;
                     if (this.shooter instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer)this.shooter;
                        Knockback profile = player.getKnockbackProfile();
                        overrided = (Boolean)BlazeConfig.overrideArrowKnockback.getValue();
                        if (overrided) {
                           boolean bowBoost = this.knockbackStrength > 0 && (Boolean)profile.bowBoostDirectional.getValue();
                           double punchMod = this.knockbackStrength * (Double)profile.bowPunchMultiplier.getValue() + 1.0;
                           if (!bowBoost || (Boolean)profile.bowBoostKbOnlyShooter.getValue() && this.shooter != entity) {
                              Vector unitVector = new Vector(this.motX, this.motY, this.motZ).normalize();
                              double velX = unitVector.getX() / 1.6 * (Double)profile.bowHorizontal.getValue();
                              double velZ = unitVector.getZ() / 1.6 * (Double)profile.bowHorizontal.getValue();
                              if (this.knockbackStrength > 0) {
                                 velX *= punchMod;
                                 velZ *= punchMod;
                              }

                              entity.motX = velX;
                              entity.motY = 0.36 * (Double)profile.bowVertical.getValue();
                              entity.motZ = velZ;
                           } else {
                              entity.motX = -MathHelper.sin(this.shooterYaw * (float) Math.PI / 180.0F) * (float)punchMod
                                 * (Double)profile.bowBoostHorizontal.getValue();
                              entity.motY = (Double)profile.bowBoostVertical.getValue();
                              entity.motZ = MathHelper.cos(this.shooterYaw * (float) Math.PI / 180.0F) * (float)punchMod
                                 * (Double)profile.bowBoostHorizontal.getValue();
                           }

                           if (entity instanceof EntityPlayer && entity.velocityChanged) {
                              ((EntityPlayer)entity).sendVelocityPacket(victimMotX, victimMotY, victimMotZ, bowBoost);
                           }
                        }
                     }

                     if (!overrided) {
                        float f3 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                        if (f3 > 0.0F && this.knockbackStrength > 0) {
                           entity.g(this.motX * this.knockbackStrength * 0.6F / f3, 0.1, this.motZ * this.knockbackStrength * 0.6F / f3);
                        }
                     }

                     if (this.shooter instanceof EntityLiving) {
                        EnchantmentManager.a(entityliving, this.shooter);
                        EnchantmentManager.b((EntityLiving)this.shooter, entityliving);
                     }

                     if (entity != this.shooter && entity instanceof EntityHuman && this.shooter instanceof EntityPlayer) {
                        ((EntityPlayer)this.shooter).playerConnection.sendPacket(new PacketPlayOutGameStateChange(6, 0.0F));
                     }
                  }

                  this.makeSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                  if (!(entity instanceof EntityEnderman)) {
                     this.die();
                  }
               }
            }
         }

         if (this.isCritical()) {
            for (int j = 0; j < 4; j++) {
               this.world
                  .addParticle(
                     EnumParticle.CRIT,
                     this.locX + this.motX * j / 4.0,
                     this.locY + this.motY * j / 4.0,
                     this.locZ + this.motZ * j / 4.0,
                     -this.motX,
                     -this.motY + 0.2,
                     -this.motZ,
                     EnumParticle.EMPTY_ARRAY
                  );
            }
         }

         this.locX = this.locX + this.motX;
         this.locY = this.locY + this.motY;
         this.locZ = this.locZ + this.motZ;
         float f2x = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
         this.yaw = (float)(MathHelper.b(this.motX, this.motZ) * 180.0 / (float) Math.PI);
         this.pitch = (float)(MathHelper.b(this.motY, f2x) * 180.0 / (float) Math.PI);

         while (this.pitch - this.lastPitch < -180.0F) {
            this.lastPitch -= 360.0F;
         }

         while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
         }

         while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
         }

         while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
         }

         this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
         this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
         float f4 = 0.99F;
         if (this.V()) {
            for (int l = 0; l < 4; l++) {
               this.world
                  .addParticle(
                     EnumParticle.WATER_BUBBLE,
                     this.locX - this.motX * 0.25,
                     this.locY - this.motY * 0.25,
                     this.locZ - this.motZ * 0.25,
                     this.motX,
                     this.motY,
                     this.motZ,
                     EnumParticle.EMPTY_ARRAY
                  );
            }

            f4 = 0.6F;
         }

         if (this.U()) {
            this.extinguish();
         }

         this.motX *= f4;
         this.motY *= f4;
         this.motZ *= f4;
         this.motY -= 0.05;
         this.setPosition(this.locX, this.locY, this.locZ);
         this.checkBlockCollisions();
      }
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      nbttagcompound.setShort("xTile", (short)this.d);
      nbttagcompound.setShort("yTile", (short)this.e);
      nbttagcompound.setShort("zTile", (short)this.f);
      nbttagcompound.setShort("life", (short)this.ar);
      MinecraftKey minecraftkey = Block.REGISTRY.c(this.g);
      nbttagcompound.setString("inTile", minecraftkey == null ? "" : minecraftkey.toString());
      nbttagcompound.setByte("inData", (byte)this.h);
      nbttagcompound.setByte("shake", (byte)this.shake);
      nbttagcompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      nbttagcompound.setByte("pickup", (byte)this.fromPlayer);
      nbttagcompound.setDouble("damage", this.damage);
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      this.d = nbttagcompound.getShort("xTile");
      this.e = nbttagcompound.getShort("yTile");
      this.f = nbttagcompound.getShort("zTile");
      this.ar = nbttagcompound.getShort("life");
      if (nbttagcompound.hasKeyOfType("inTile", 8)) {
         this.g = Block.getByName(nbttagcompound.getString("inTile"));
      } else {
         this.g = Block.getById(nbttagcompound.getByte("inTile") & 255);
      }

      this.h = nbttagcompound.getByte("inData") & 255;
      this.shake = nbttagcompound.getByte("shake") & 255;
      this.inGround = nbttagcompound.getByte("inGround") == 1;
      if (nbttagcompound.hasKeyOfType("damage", 99)) {
         this.damage = nbttagcompound.getDouble("damage");
      }

      if (nbttagcompound.hasKeyOfType("pickup", 99)) {
         this.fromPlayer = nbttagcompound.getByte("pickup");
      } else if (nbttagcompound.hasKeyOfType("player", 99)) {
         this.fromPlayer = nbttagcompound.getBoolean("player") ? 1 : 0;
      }
   }

   @Override
   public void d(EntityHuman entityhuman) {
      if (this.inGround && this.shake <= 0) {
         if (!((EntityPlayer)entityhuman).canSee(this)) {
            return;
         }

         ItemStack itemstack = new ItemStack(Items.ARROW);
         if (this.fromPlayer == 1 && entityhuman.inventory.canHold(itemstack) > 0) {
            EntityItem item = new EntityItem(this.world, this.locX, this.locY, this.locZ, itemstack);
            PlayerPickupItemEvent event = new PlayerPickupItemEvent((Player)entityhuman.getBukkitEntity(), new CraftItem(this.world.getServer(), this, item), 0);
            this.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               return;
            }
         }

         boolean flag = this.fromPlayer == 1 || this.fromPlayer == 2 && entityhuman.abilities.canInstantlyBuild;
         if (this.fromPlayer == 1 && !entityhuman.inventory.pickup(new ItemStack(Items.ARROW, 1))) {
            flag = false;
         }

         if (flag) {
            this.makeSound("random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            entityhuman.receive(this, 1);
            this.die();
         }
      }
   }

   @Override
   protected boolean s_() {
      return false;
   }

   public void b(double d0) {
      this.damage = d0;
   }

   public double j() {
      return this.damage;
   }

   public void setKnockbackStrength(int i) {
      this.knockbackStrength = i;
   }

   @Override
   public boolean aD() {
      return false;
   }

   @Override
   public float getHeadHeight() {
      return 0.0F;
   }

   public boolean isCritical() {
      byte b0 = this.datawatcher.getByte(16);
      return (b0 & 1) != 0;
   }

   public void setCritical(boolean flag) {
      byte b0 = this.datawatcher.getByte(16);
      if (flag) {
         this.datawatcher.watch(16, (byte)(b0 | 1));
      } else {
         this.datawatcher.watch(16, (byte)(b0 & -2));
      }
   }

   public boolean isInGround() {
      return this.inGround;
   }
}
