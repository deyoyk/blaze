package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class EntityExperienceOrb extends Entity {
   public int a;
   public int b;
   public int c;
   public int value;
   private int d = 5;
   private EntityHuman targetPlayer;
   private int targetTime;

   public EntityExperienceOrb(World world, double d0, double d1, double d2, int i) {
      super(world);
      this.setSize(0.5F, 0.5F);
      this.setPosition(d0, d1, d2);
      this.yaw = (float)(Math.random() * 360.0);
      this.motX = (float)(Math.random() * 0.2F - 0.1F) * 2.0F;
      this.motY = (float)(Math.random() * 0.2) * 2.0F;
      this.motZ = (float)(Math.random() * 0.2F - 0.1F) * 2.0F;
      this.value = i;
   }

   public EntityExperienceOrb(World world) {
      super(world);
      this.setSize(0.25F, 0.25F);
   }

   public static int getOrbValue(int i) {
      if (i > 162670129) {
         return i - 100000;
      } else if (i > 81335063) {
         return 81335063;
      } else if (i > 40667527) {
         return 40667527;
      } else if (i > 20333759) {
         return 20333759;
      } else if (i > 10166857) {
         return 10166857;
      } else if (i > 5083423) {
         return 5083423;
      } else if (i > 2541701) {
         return 2541701;
      } else if (i > 1270849) {
         return 1270849;
      } else if (i > 635413) {
         return 635413;
      } else if (i > 317701) {
         return 317701;
      } else if (i > 158849) {
         return 158849;
      } else if (i > 79423) {
         return 79423;
      } else if (i > 39709) {
         return 39709;
      } else if (i > 19853) {
         return 19853;
      } else if (i > 9923) {
         return 9923;
      } else if (i > 4957) {
         return 4957;
      } else {
         return i >= 2477
            ? 2477
            : (
               i >= 1237
                  ? 1237
                  : (i >= 617 ? 617 : (i >= 307 ? 307 : (i >= 149 ? 149 : (i >= 73 ? 73 : (i >= 37 ? 37 : (i >= 17 ? 17 : (i >= 7 ? 7 : (i >= 3 ? 3 : 1))))))))
            );
      }
   }

   @Override
   protected boolean s_() {
      return false;
   }

   @Override
   protected void h() {
   }

   @Override
   public void t_() {
      super.t_();
      EntityHuman prevTarget = this.targetPlayer;
      if (this.c > 0) {
         this.c--;
      }

      this.lastX = this.locX;
      this.lastY = this.locY;
      this.lastZ = this.locZ;
      this.motY -= 0.03F;
      if (this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)).getBlock().getMaterial() == Material.LAVA) {
         this.motY = 0.2F;
         this.motX = (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
         this.motZ = (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
         this.makeSound("random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
      }

      this.j(this.locX, (this.getBoundingBox().b + this.getBoundingBox().e) / 2.0, this.locZ);
      double d0 = 8.0;
      if (this.targetTime < this.a - 20 + this.getId() % 100) {
         if (this.targetPlayer == null || this.targetPlayer.h(this) > d0 * d0) {
            this.targetPlayer = this.world.findNearbyPlayer(this, d0);
         }

         this.targetTime = this.a;
      }

      if (this.targetPlayer != null && this.targetPlayer.isSpectator()) {
         this.targetPlayer = null;
      }

      if (this.targetPlayer != null) {
         boolean cancelled = false;
         if (this.targetPlayer != prevTarget) {
            EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(this, this.targetPlayer, TargetReason.CLOSEST_PLAYER);
            EntityLiving target = event.getTarget() == null ? null : ((CraftLivingEntity)event.getTarget()).getHandle();
            this.targetPlayer = target instanceof EntityHuman ? (EntityHuman)target : null;
            cancelled = event.isCancelled();
         }

         if (!cancelled && this.targetPlayer != null) {
            double d1 = (this.targetPlayer.locX - this.locX) / d0;
            double d2 = (this.targetPlayer.locY + this.targetPlayer.getHeadHeight() - this.locY) / d0;
            double d3 = (this.targetPlayer.locZ - this.locZ) / d0;
            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
            double d5 = 1.0 - d4;
            if (d5 > 0.0) {
               d5 *= d5;
               this.motX += d1 / d4 * d5 * 0.1;
               this.motY += d2 / d4 * d5 * 0.1;
               this.motZ += d3 / d4 * d5 * 0.1;
            }
         }
      }

      this.move(this.motX, this.motY, this.motZ);
      float f = 0.98F;
      if (this.onGround) {
         f = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ)).getBlock().frictionFactor
            * 0.98F;
      }

      this.motX *= f;
      this.motY *= 0.98F;
      this.motZ *= f;
      if (this.onGround) {
         this.motY *= -0.9F;
      }

      this.a++;
      this.b++;
      if (this.b >= 6000) {
         this.die();
      }
   }

   @Override
   public boolean W() {
      return this.world.a(this.getBoundingBox(), Material.WATER, this);
   }

   protected void burn(int i) {
      this.damageEntity(DamageSource.FIRE, i);
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (!this.isInvulnerable(damagesource)) {
         this.ac();
         this.d = (int)(this.d - f);
         if (this.d <= 0) {
            this.die();
         }
      }

      return false;
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      nbttagcompound.setShort("Health", (byte)this.d);
      nbttagcompound.setShort("Age", (short)this.b);
      nbttagcompound.setInt("Value", this.value);
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      this.d = nbttagcompound.getShort("Health") & 255;
      this.b = nbttagcompound.getShort("Age");
      this.value = nbttagcompound.getInt("Value");
   }

   @Override
   public void d(EntityHuman entityhuman) {
      if (this.c == 0 && entityhuman.bp == 0) {
         entityhuman.bp = 2;
         this.world.makeSound(entityhuman, "random.orb", 0.1F, 0.5F * ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.8F));
         entityhuman.receive(this, 1);
         entityhuman.giveExp(CraftEventFactory.callPlayerExpChangeEvent(entityhuman, this.value).getAmount());
         this.die();
      }
   }

   public int j() {
      return this.value;
   }

   @Override
   public boolean aD() {
      return false;
   }
}
