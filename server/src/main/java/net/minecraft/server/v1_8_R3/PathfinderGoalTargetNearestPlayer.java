package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class PathfinderGoalTargetNearestPlayer extends PathfinderGoal {
   private static final Logger a = LogManager.getLogger();
   private final EntityInsentient b;
   private final Predicate<Entity> c;
   private final PathfinderGoalNearestAttackableTarget.DistanceComparator d;
   private EntityLiving e;

   public PathfinderGoalTargetNearestPlayer(EntityInsentient entityinsentient) {
      this.b = entityinsentient;
      if (entityinsentient instanceof EntityCreature) {
         a.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
      }

      this.c = new Predicate() {
         public boolean a(Entity entity) {
            if (!(entity instanceof EntityHuman)) {
               return false;
            } else if (((EntityHuman)entity).abilities.isInvulnerable) {
               return false;
            } else {
               double d0 = PathfinderGoalTargetNearestPlayer.this.f();
               if (entity.isSneaking()) {
                  d0 *= 0.8F;
               }

               if (entity.isInvisible()) {
                  float f = ((EntityHuman)entity).bY();
                  if (f < 0.1F) {
                     f = 0.1F;
                  }

                  d0 *= 0.7F * f;
               }

               return !(entity.g(PathfinderGoalTargetNearestPlayer.this.b) > d0)
                  && PathfinderGoalTarget.a(PathfinderGoalTargetNearestPlayer.this.b, (EntityLiving)entity, false, true);
            }
         }

         public boolean apply(Object object) {
            return this.a((Entity)object);
         }
      };
      this.d = new PathfinderGoalNearestAttackableTarget.DistanceComparator(entityinsentient);
   }

   @Override
   public boolean a() {
      double d0 = this.f();
      List<EntityHuman> list = this.b.world.a(EntityHuman.class, this.b.getBoundingBox().grow(d0, 4.0, d0), this.c);
      list.sort(this.d);
      if (list.isEmpty()) {
         return false;
      } else {
         this.e = list.get(0);
         return true;
      }
   }

   @Override
   public boolean b() {
      EntityLiving entityliving = this.b.getGoalTarget();
      if (entityliving == null) {
         return false;
      } else if (!entityliving.isAlive()) {
         return false;
      } else if (entityliving instanceof EntityHuman && ((EntityHuman)entityliving).abilities.isInvulnerable) {
         return false;
      } else {
         ScoreboardTeamBase scoreboardteambase = this.b.getScoreboardTeam();
         ScoreboardTeamBase scoreboardteambase1 = entityliving.getScoreboardTeam();
         if (scoreboardteambase != null && scoreboardteambase1 == scoreboardteambase) {
            return false;
         } else {
            double d0 = this.f();
            return !(this.b.h(entityliving) > d0 * d0)
               && (!(entityliving instanceof EntityPlayer) || !((EntityPlayer)entityliving).playerInteractManager.isCreative());
         }
      }
   }

   @Override
   public void c() {
      this.b.setGoalTarget(this.e, TargetReason.CLOSEST_PLAYER, true);
      super.c();
   }

   @Override
   public void d() {
      this.b.setGoalTarget(null);
      super.c();
   }

   protected double f() {
      AttributeInstance attributeinstance = this.b.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
      return attributeinstance == null ? 16.0 : attributeinstance.getValue();
   }
}
