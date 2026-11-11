package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import lol.deyo.blaze.pathsearch.PositionPathSearchType;
import java.util.List;

public class PathfinderGoalAvoidTarget<T extends Entity> extends PathfinderGoal {
   private final Predicate<Entity> c = new Predicate<Entity>() {
      public boolean a(Entity entity) {
         return entity.isAlive() && PathfinderGoalAvoidTarget.this.a.getEntitySenses().a(entity);
      }

      public boolean apply(Entity object) {
         return this.a(object);
      }
   };
   protected EntityCreature a;
   private final double d;
   private final double e;
   protected T b;
   private final float f;
   private PathEntity g;
   private final NavigationAbstract h;
   private final Class<T> i;
   private final Predicate<? super T> j;

   public PathfinderGoalAvoidTarget(EntityCreature entitycreature, Class<T> oclass, float f, double d0, double d1) {
      this(entitycreature, oclass, Predicates.alwaysTrue(), f, d0, d1);
   }

   public PathfinderGoalAvoidTarget(EntityCreature entitycreature, Class<T> oclass, Predicate<? super T> predicate, float f, double d0, double d1) {
      this.a = entitycreature;
      this.i = oclass;
      this.j = predicate;
      this.f = f;
      this.d = d0;
      this.e = d1;
      this.h = entitycreature.getNavigation();
      this.a(1);
   }

   @Override
   public boolean a() {
      List<Entity> list = this.a
         .world
         .a(this.i, this.a.getBoundingBox().grow(this.f, 3.0, this.f), Predicates.and(new Predicate[]{IEntitySelector.d, this.c, this.j}));
      if (list.isEmpty()) {
         return false;
      } else {
         this.b = (T)list.get(0);
         Vec3D vec3d = RandomPositionGenerator.b(this.a, 16, 7, new Vec3D(this.b.locX, this.b.locY, this.b.locZ));
         if (vec3d == null) {
            return false;
         } else if (this.b.e(vec3d.a, vec3d.b, vec3d.c) < this.b.h(this.a)) {
            return false;
         } else {
            this.g = this.h.a(vec3d.a, vec3d.b, vec3d.c, PositionPathSearchType.AVOIDTARGET);
            return this.g != null && this.g.b(vec3d);
         }
      }
   }

   @Override
   public boolean b() {
      return !this.h.m();
   }

   @Override
   public void c() {
      this.h.a(this.g, this.d);
   }

   @Override
   public void d() {
      this.b = null;
   }

   @Override
   public void e() {
      if (this.a.h(this.b) < 49.0) {
         this.a.getNavigation().a(this.e);
      } else {
         this.a.getNavigation().a(this.d);
      }
   }
}
