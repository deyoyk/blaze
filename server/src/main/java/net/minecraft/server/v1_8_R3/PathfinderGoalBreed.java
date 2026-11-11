package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.Random;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class PathfinderGoalBreed extends PathfinderGoal {
   private final EntityAnimal d;
   World a;
   private EntityAnimal e;
   int b;
   double c;

   public PathfinderGoalBreed(EntityAnimal entityanimal, double d0) {
      this.d = entityanimal;
      this.a = entityanimal.world;
      this.c = d0;
      this.a(3);
   }

   @Override
   public boolean a() {
      if (!this.d.isInLove()) {
         return false;
      } else {
         this.e = this.f();
         return this.e != null;
      }
   }

   @Override
   public boolean b() {
      return this.e.isAlive() && this.e.isInLove() && this.b < 60;
   }

   @Override
   public void d() {
      this.e = null;
      this.b = 0;
   }

   @Override
   public void e() {
      this.d.getControllerLook().a(this.e, 10.0F, this.d.bQ());
      this.d.getNavigation().a(this.e, this.c);
      this.b++;
      if (this.b >= 60 && this.d.h(this.e) < 9.0) {
         this.g();
      }
   }

   private EntityAnimal f() {
      float f = 8.0F;
      List<? extends EntityAnimal> list = this.a.a((Class<? extends EntityAnimal>)this.d.getClass(), this.d.getBoundingBox().grow(f, f, f));
      double d0 = Double.MAX_VALUE;
      EntityAnimal entityanimal = null;

      for (EntityAnimal entityanimal1 : list) {
         if (this.d.mate(entityanimal1) && this.d.h(entityanimal1) < d0) {
            entityanimal = entityanimal1;
            d0 = this.d.h(entityanimal1);
         }
      }

      return entityanimal;
   }

   private void g() {
      EntityAgeable entityageable = this.d.createChild(this.e);
      if (entityageable != null) {
         if (entityageable instanceof EntityTameableAnimal && ((EntityTameableAnimal)entityageable).isTamed()) {
            entityageable.persistent = true;
         }

         this.d.setAgeRaw(6000);
         this.e.setAgeRaw(6000);
         this.d.cs();
         this.e.cs();
         entityageable.setAgeRaw(-24000);
         entityageable.setPositionRotation(this.d.locX, this.d.locY, this.d.locZ, 0.0F, 0.0F);
         this.a.addEntity(entityageable, SpawnReason.BREEDING);
         Random random = this.d.bc();

         for (int i = 0; i < 7; i++) {
            double d0 = random.nextGaussian() * 0.02;
            double d1 = random.nextGaussian() * 0.02;
            double d2 = random.nextGaussian() * 0.02;
            double d3 = random.nextDouble() * this.d.width * 2.0 - this.d.width;
            double d4 = 0.5 + random.nextDouble() * this.d.length;
            double d5 = random.nextDouble() * this.d.width * 2.0 - this.d.width;
            this.a.addParticle(EnumParticle.HEART, this.d.locX + d3, this.d.locY + d4, this.d.locZ + d5, d0, d1, d2, EnumParticle.EMPTY_ARRAY);
         }

         if (this.a.getGameRules().getBoolean("doMobLoot")) {
            this.a.addEntity(new EntityExperienceOrb(this.a, this.d.locX, this.d.locY, this.d.locZ, random.nextInt(7) + 1));
         }
      }
   }
}
