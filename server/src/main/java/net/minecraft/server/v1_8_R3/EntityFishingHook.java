package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.knockback.Knockback;
import java.util.Arrays;
import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.util.Vector;

public class EntityFishingHook extends Entity {
   private static final List<PossibleFishingResult> d = Arrays.asList(
      new PossibleFishingResult(new ItemStack(Items.LEATHER_BOOTS), 10).a(0.9F),
      new PossibleFishingResult(new ItemStack(Items.LEATHER), 10),
      new PossibleFishingResult(new ItemStack(Items.BONE), 10),
      new PossibleFishingResult(new ItemStack(Items.POTION), 10),
      new PossibleFishingResult(new ItemStack(Items.STRING), 5),
      new PossibleFishingResult(new ItemStack(Items.FISHING_ROD), 2).a(0.9F),
      new PossibleFishingResult(new ItemStack(Items.BOWL), 10),
      new PossibleFishingResult(new ItemStack(Items.STICK), 5),
      new PossibleFishingResult(new ItemStack(Items.DYE, 10, EnumColor.BLACK.getInvColorIndex()), 1),
      new PossibleFishingResult(new ItemStack(Blocks.TRIPWIRE_HOOK), 10),
      new PossibleFishingResult(new ItemStack(Items.ROTTEN_FLESH), 10)
   );
   private static final List<PossibleFishingResult> e = Arrays.asList(
      new PossibleFishingResult(new ItemStack(Blocks.WATERLILY), 1),
      new PossibleFishingResult(new ItemStack(Items.NAME_TAG), 1),
      new PossibleFishingResult(new ItemStack(Items.SADDLE), 1),
      new PossibleFishingResult(new ItemStack(Items.BOW), 1).a(0.25F).a(),
      new PossibleFishingResult(new ItemStack(Items.FISHING_ROD), 1).a(0.25F).a(),
      new PossibleFishingResult(new ItemStack(Items.BOOK), 1).a()
   );
   private static final List<PossibleFishingResult> f = Arrays.asList(
      new PossibleFishingResult(new ItemStack(Items.FISH, 1, ItemFish.EnumFish.COD.a()), 60),
      new PossibleFishingResult(new ItemStack(Items.FISH, 1, ItemFish.EnumFish.SALMON.a()), 25),
      new PossibleFishingResult(new ItemStack(Items.FISH, 1, ItemFish.EnumFish.CLOWNFISH.a()), 2),
      new PossibleFishingResult(new ItemStack(Items.FISH, 1, ItemFish.EnumFish.PUFFERFISH.a()), 13)
   );
   public int a;
   public EntityHuman owner;
   public Entity hooked;
   private int g = -1;
   private int h = -1;
   private int i = -1;
   private Block ar;
   private boolean as;
   private int at;
   private int au;
   private int av;
   private int aw;
   private int ax;
   private float ay;
   private int az;

   public EntityFishingHook(World world) {
      super(world);
      this.setSize(0.25F, 0.25F);
      this.ah = true;
   }

   public EntityFishingHook(World world, EntityHuman entityhuman) {
      super(world);
      this.ah = true;
      this.owner = entityhuman;
      this.owner.hookedFish = this;
      this.setSize(0.25F, 0.25F);
      this.setPositionRotation(entityhuman.locX, entityhuman.locY + entityhuman.getHeadHeight(), entityhuman.locZ, entityhuman.yaw, entityhuman.pitch);
      this.locX = this.locX - MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F;
      this.locY -= 0.1F;
      this.locZ = this.locZ - MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F;
      this.setPosition(this.locX, this.locY, this.locZ);
      this.motX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * 0.4F;
      this.motZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * 0.4F;
      this.motY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * 0.4F;
      this.c(this.motX, this.motY, this.motZ, 1.5F, (Float)BlazeConfig.rodTrajectoryRandomness.getValue());
   }

   public static List<PossibleFishingResult> j() {
      return f;
   }

   @Override
   protected void h() {
   }

   public void c(double d0, double d1, double d2, float f, float f1) {
      if ((Boolean)BlazeConfig.overrideRodKnockback.getValue()) {
         f *= (float)(this.owner != null ? this.owner.getKnockbackProfile() : Blaze.getInstance().getKnockbackManager().getActiveProfile()).getRodSpeed();
      }

      float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
      d0 /= f2;
      d1 /= f2;
      d2 /= f2;
      d0 += this.random.nextGaussian() * 0.0075F * f1;
      d1 += this.random.nextGaussian() * 0.0075F * f1;
      d2 += this.random.nextGaussian() * 0.0075F * f1;
      d0 *= f;
      d1 *= f;
      d2 *= f;
      this.motX = d0;
      this.motY = d1;
      this.motZ = d2;
      float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
      this.lastYaw = this.yaw = (float)(MathHelper.b(d0, d2) * 180.0 / (float) Math.PI);
      this.lastPitch = this.pitch = (float)(MathHelper.b(d1, f3) * 180.0 / (float) Math.PI);
      this.at = 0;
   }

   @Override
   public void t_() {
      super.t_();
      if (this.az > 0) {
         this.yaw = this.yaw + MathHelper.g(0.0F - this.yaw) / this.az;
         this.pitch = this.pitch + (0.0F - this.pitch) / this.az;
         this.az--;
         this.setPosition(this.locX + (0.0 - this.locX) / this.az, this.locY + (0.0 - this.locY) / this.az, this.locZ + (0.0 - this.locZ) / this.az);
         this.setYawPitch(this.yaw, this.pitch);
      } else {
         ItemStack itemstack = this.owner.bZ();
         if (this.owner.dead || !this.owner.isAlive() || itemstack == null || itemstack.getItem() != Items.FISHING_ROD || this.h(this.owner) > 1024.0) {
            this.die();
            this.owner.hookedFish = null;
            return;
         }

         if (this.hooked != null) {
            if (!this.hooked.dead) {
               this.motX = 0.0;
               this.motY = 0.0;
               this.motZ = 0.0;
               this.locX = this.hooked.locX;
               double d4 = this.hooked.length;
               this.locY = this.hooked.boundingBox.b + d4 * 0.55;
               this.locZ = this.hooked.locZ;
               return;
            }

            this.hooked = null;
            WorldServer worldServer = (WorldServer)this.world;
            EntityTrackerEntry entry = worldServer.tracker.trackedEntities.get(this.getId());
            entry.c = Integer.MAX_VALUE;
         }

         if (this.a > 0) {
            this.a--;
         }

         if (this.as) {
            if (this.world.getType(this.g, this.h, this.i).getBlock() == this.ar) {
               this.at++;
               if (this.at == 1200) {
                  this.die();
               }

               return;
            }

            this.as = false;
            this.motX = this.motX * (this.random.nextFloat() * 0.2F);
            this.motY = this.motY * (this.random.nextFloat() * 0.2F);
            this.motZ = this.motZ * (this.random.nextFloat() * 0.2F);
            this.at = 0;
            this.au = 0;
         } else {
            this.au++;
         }

         Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
         Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
         MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1);
         vec3d = new Vec3D(this.locX, this.locY, this.locZ);
         vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
         if (movingobjectposition != null) {
            vec3d1 = new Vec3D(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
         }

         Entity entity = null;
         double d5 = 0.0;

         for (Entity entity1 : this.world.getEntities(this, this.boundingBox.a(this.motX, this.motY, this.motZ).grow(1.0, 1.0, 1.0))) {
            if (entity1.ad() && (entity1 != this.owner || this.au >= 5)) {
               AxisAlignedBB axisalignedbb = entity1.boundingBox.grow(0.3, 0.3, 0.3);
               MovingObjectPosition movingobjectposition1 = axisalignedbb.a(vec3d, vec3d1);
               if (movingobjectposition1 != null) {
                  double d6 = vec3d.distanceSquared(movingobjectposition1.pos);
                  if (d6 < d5 || d5 == 0.0) {
                     entity = entity1;
                     d5 = d6;
                  }
               }
            }
         }

         if (entity != null) {
            movingobjectposition = new MovingObjectPosition(entity);
         }

         if (movingobjectposition != null
            && entity instanceof EntityPlayer
            && this.owner instanceof EntityPlayer
            && !((EntityPlayer)this.owner).getBukkitEntity().canSee(((EntityPlayer)entity).getBukkitEntity())) {
            movingobjectposition = null;
            entity = null;
         }

         if (movingobjectposition != null) {
            CraftEventFactory.callProjectileHitEvent(this);
            if (entity != null) {
               if (this.owner != null) {
                  double victimMotX = entity.motX;
                  double victimMotY = entity.motY;
                  double victimMotZ = entity.motZ;
                  if (entity.damageEntity(DamageSource.projectile(this, this.owner), 0.0F)) {
                     Knockback profile = this.owner.getKnockbackProfile();
                     if ((Boolean)BlazeConfig.overrideRodKnockback.getValue()) {
                        Vector unitVector = new Vector(this.motX, this.motY, this.motZ).normalize();
                        double horizontal = (Double)profile.rodHorizontal.getValue();
                        entity.motX = unitVector.getX() / 1.6 * horizontal;
                        entity.motY = 0.36 * (Double)profile.rodVertical.getValue();
                        entity.motZ = unitVector.getZ() / 1.6 * horizontal;
                        if (entity instanceof EntityPlayer && entity.velocityChanged) {
                           ((EntityPlayer)entity).sendVelocityPacket(victimMotX, victimMotY, victimMotZ, false);
                        }
                     }

                     this.hooked = entity;
                     this.motX = 0.0;
                     this.motY = 0.0;
                     this.motZ = 0.0;
                     WorldServer worldServer = (WorldServer)this.world;
                     EntityTrackerEntry entry = worldServer.tracker.trackedEntities.get(this.getId());
                     entry.c = 1;
                  }
               }
            } else {
               this.as = true;
            }
         }

         if (!this.as) {
            this.move(this.motX, this.motY, this.motZ);
            float f1 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
            this.yaw = (float)(MathHelper.b(this.motX, this.motZ) * 180.0 / (float) Math.PI);
            this.pitch = (float)(MathHelper.b(this.motY, f1) * 180.0 / (float) Math.PI);

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
            float f2 = 0.92F;
            if (this.onGround || this.positionChanged) {
               f2 = 0.5F;
            }

            byte b0 = 5;
            double d7 = 0.0;

            for (int j = 0; j < b0; j++) {
               AxisAlignedBB axisalignedbb1 = this.boundingBox;
               double d9 = axisalignedbb1.e - axisalignedbb1.b;
               double d10 = axisalignedbb1.b + d9 * j / b0;
               double d8 = axisalignedbb1.b + d9 * (j + 1) / b0;
               AxisAlignedBB axisalignedbb2 = new AxisAlignedBB(axisalignedbb1.a, d10, axisalignedbb1.c, axisalignedbb1.d, d8, axisalignedbb1.f);
               if (this.world.b(axisalignedbb2, Material.WATER)) {
                  d7 += 1.0 / b0;
                  WorldServer worldServer = (WorldServer)this.world;
                  EntityTrackerEntry entry = worldServer.tracker.trackedEntities.get(this.getId());
                  entry.c = 1;
               }
            }

            if (d7 > 0.0) {
               WorldServer worldserver = (WorldServer)this.world;
               int k = 1;
               int x = MathHelper.floor(this.locX);
               int y = MathHelper.floor(this.locY + 1.0);
               int z = MathHelper.floor(this.locZ);
               if (this.random.nextFloat() < 0.25F && this.world.isRainingAt(x, y, z)) {
                  k = 2;
               }

               if (this.random.nextFloat() < 0.5F && !this.world.i(x, y, z)) {
                  k--;
               }

               if (this.av > 0) {
                  this.av--;
                  if (this.av <= 0) {
                     this.aw = 0;
                     this.ax = 0;
                  }
               } else if (this.ax > 0) {
                  this.ax -= k;
                  if (this.ax <= 0) {
                     this.motY -= 0.2F;
                     this.makeSound("random.splash", 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                     float f3 = MathHelper.floor(this.boundingBox.b);
                     worldserver.a(
                        EnumParticle.WATER_BUBBLE, this.locX, f3 + 1.0F, this.locZ, (int)(1.0F + this.width * 20.0F), this.width, 0.0, this.width, 0.2F
                     );
                     worldserver.a(
                        EnumParticle.WATER_WAKE, this.locX, f3 + 1.0F, this.locZ, (int)(1.0F + this.width * 20.0F), this.width, 0.0, this.width, 0.2F
                     );
                     this.av = MathHelper.nextInt(this.random, 10, 30);
                  } else {
                     this.ay = (float)(this.ay + this.random.nextGaussian() * 4.0);
                     float f3 = this.ay * (float) (Math.PI / 180.0);
                     float f5 = MathHelper.sin(f3);
                     float f4 = MathHelper.cos(f3);
                     double d8 = this.locX + f5 * this.ax * 0.1F;
                     double d12 = MathHelper.floor(this.boundingBox.b) + 1.0F;
                     double d11 = this.locZ + f4 * this.ax * 0.1F;
                     Block block = worldserver.getType((int)d8, (int)d12 - 1, (int)d11).getBlock();
                     if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                        if (this.random.nextFloat() < 0.15F) {
                           worldserver.a(EnumParticle.WATER_BUBBLE, d8, d12 - 0.1F, d11, 1, f5, 0.1, f4, 0.0);
                        }

                        float f6 = f5 * 0.04F;
                        float f7 = f4 * 0.04F;
                        worldserver.a(EnumParticle.WATER_WAKE, d8, d12, d11, 0, f7, 0.01, -f6, 1.0);
                        worldserver.a(EnumParticle.WATER_WAKE, d8, d12, d11, 0, -f7, 0.01, f6, 1.0);
                     }
                  }
               } else if (this.aw > 0) {
                  this.aw -= k;
                  float f3 = 0.15F;
                  if (this.aw < 20) {
                     f3 = (float)(f3 + (20 - this.aw) * 0.05);
                  } else if (this.aw < 40) {
                     f3 = (float)(f3 + (40 - this.aw) * 0.02);
                  } else if (this.aw < 60) {
                     f3 = (float)(f3 + (60 - this.aw) * 0.01);
                  }

                  if (this.random.nextFloat() < f3) {
                     float f5 = MathHelper.a(this.random, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
                     float f4 = MathHelper.a(this.random, 25.0F, 60.0F);
                     double d8 = this.locX + MathHelper.sin(f5) * f4 * 0.1F;
                     double d12 = MathHelper.floor(this.boundingBox.b) + 1.0F;
                     double d11 = this.locZ + MathHelper.cos(f5) * f4 * 0.1F;
                     Block block = worldserver.getType((int)d8, (int)d12 - 1, (int)d11).getBlock();
                     if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                        worldserver.a(EnumParticle.WATER_SPLASH, d8, d12, d11, 2 + this.random.nextInt(2), 0.1F, 0.0, 0.1F, 0.0, EnumParticle.EMPTY_ARRAY);
                     }
                  }

                  if (this.aw <= 0) {
                     this.ay = MathHelper.a(this.random, 0.0F, 360.0F);
                     this.ax = MathHelper.nextInt(this.random, 20, 80);
                  }
               } else {
                  this.aw = MathHelper.nextInt(this.random, this.world.paperSpigotConfig.fishingMinTicks, this.world.paperSpigotConfig.fishingMaxTicks);
                  this.aw = this.aw - EnchantmentManager.h(this.owner) * 20 * 5;
               }

               if (this.av > 0) {
                  this.motY = this.motY - this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat() * 0.2;
               }
            }

            double d6 = d7 * 2.0 - 1.0;
            this.motY += 0.04F * d6;
            if (d7 > 0.0) {
               f2 = (float)(f2 * 0.9);
               this.motY *= 0.8;
            }

            this.motX *= f2;
            this.motY *= f2;
            this.motZ *= f2;
            this.setPosition(this.locX, this.locY, this.locZ);
         }
      }
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      nbttagcompound.setShort("xTile", (short)this.g);
      nbttagcompound.setShort("yTile", (short)this.h);
      nbttagcompound.setShort("zTile", (short)this.i);
      MinecraftKey minecraftkey = Block.REGISTRY.c(this.ar);
      nbttagcompound.setString("inTile", minecraftkey == null ? "" : minecraftkey.toString());
      nbttagcompound.setByte("shake", (byte)this.a);
      nbttagcompound.setByte("inGround", (byte)(this.as ? 1 : 0));
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      this.g = nbttagcompound.getShort("xTile");
      this.h = nbttagcompound.getShort("yTile");
      this.i = nbttagcompound.getShort("zTile");
      if (nbttagcompound.hasKeyOfType("inTile", 8)) {
         this.ar = Block.getByName(nbttagcompound.getString("inTile"));
      } else {
         this.ar = Block.getById(nbttagcompound.getByte("inTile") & 255);
      }

      this.a = nbttagcompound.getByte("shake") & 255;
      this.as = nbttagcompound.getByte("inGround") == 1;
   }

   public int l() {
      byte b0 = 0;
      if (this.hooked != null) {
         PlayerFishEvent playerFishEvent = new PlayerFishEvent(
            (Player)this.owner.getBukkitEntity(), this.hooked.getBukkitEntity(), (Fish)this.getBukkitEntity(), State.CAUGHT_ENTITY
         );
         this.world.getServer().getPluginManager().callEvent(playerFishEvent);
         if (playerFishEvent.isCancelled()) {
            return 0;
         }

         double d0 = this.owner.locX - this.locX;
         double d1 = this.owner.locY - this.locY;
         double d2 = this.owner.locZ - this.locZ;
         double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
         this.hooked.motX += d0 * 0.1;
         this.hooked.motY = this.hooked.motY + (d1 * 0.1 + MathHelper.sqrt(d3) * 0.08);
         this.hooked.motZ += d2 * 0.1;
         b0 = 3;
      } else if (this.av > 0) {
         EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY, this.locZ, this.m());
         PlayerFishEvent playerFishEvent = new PlayerFishEvent(
            (Player)this.owner.getBukkitEntity(), entityitem.getBukkitEntity(), (Fish)this.getBukkitEntity(), State.CAUGHT_FISH
         );
         playerFishEvent.setExpToDrop(this.random.nextInt(6) + 1);
         this.world.getServer().getPluginManager().callEvent(playerFishEvent);
         if (playerFishEvent.isCancelled()) {
            return 0;
         }

         double d5 = this.owner.locX - this.locX;
         double d6 = this.owner.locY - this.locY;
         double d7 = this.owner.locZ - this.locZ;
         double d8 = MathHelper.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
         entityitem.motX = d5 * 0.1;
         entityitem.motY = d6 * 0.1 + MathHelper.sqrt(d8) * 0.08;
         entityitem.motZ = d7 * 0.1;
         this.world.addEntity(entityitem);
         if (playerFishEvent.getExpToDrop() > 0) {
            this.owner
               .world
               .addEntity(
                  new EntityExperienceOrb(this.owner.world, this.owner.locX, this.owner.locY + 0.5, this.owner.locZ + 0.5, playerFishEvent.getExpToDrop())
               );
         }

         b0 = 1;
      }

      if (this.as) {
         PlayerFishEvent playerFishEventx = new PlayerFishEvent((Player)this.owner.getBukkitEntity(), null, (Fish)this.getBukkitEntity(), State.IN_GROUND);
         this.world.getServer().getPluginManager().callEvent(playerFishEventx);
         if (playerFishEventx.isCancelled()) {
            return 0;
         }

         b0 = 2;
      }

      if (b0 == 0) {
         PlayerFishEvent playerFishEventx = new PlayerFishEvent((Player)this.owner.getBukkitEntity(), null, (Fish)this.getBukkitEntity(), State.FAILED_ATTEMPT);
         this.world.getServer().getPluginManager().callEvent(playerFishEventx);
         if (playerFishEventx.isCancelled()) {
            return 0;
         }
      }

      this.die();
      this.owner.hookedFish = null;
      return b0;
   }

   private ItemStack m() {
      float f = this.world.random.nextFloat();
      int i = EnchantmentManager.g(this.owner);
      int j = EnchantmentManager.h(this.owner);
      float f1 = 0.1F - i * 0.025F - j * 0.01F;
      float f2 = 0.05F + i * 0.01F - j * 0.01F;
      f1 = MathHelper.a(f1, 0.0F, 1.0F);
      f2 = MathHelper.a(f2, 0.0F, 1.0F);
      if (f < f1) {
         return WeightedRandom.a(this.random, d).a(this.random);
      } else {
         f -= f1;
         return f < f2 ? WeightedRandom.a(this.random, e).a(this.random) : WeightedRandom.a(this.random, EntityFishingHook.f).a(this.random);
      }
   }

   @Override
   public void die() {
      super.die();
      if (this.owner != null) {
         this.owner.hookedFish = null;
      }
   }
}
