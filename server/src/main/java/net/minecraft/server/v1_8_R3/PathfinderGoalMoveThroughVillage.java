package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import lol.deyo.blaze.pathsearch.PositionPathSearchType;
import java.util.List;

public class PathfinderGoalMoveThroughVillage extends PathfinderGoal {
   private final EntityCreature a;
   private final double b;
   private PathEntity c;
   private VillageDoor d;
   private final boolean e;
   private final List<VillageDoor> f = Lists.newArrayList();

   public PathfinderGoalMoveThroughVillage(EntityCreature entitycreature, double d0, boolean flag) {
      this.a = entitycreature;
      this.b = d0;
      this.e = flag;
      this.a(1);
      if (!(entitycreature.getNavigation() instanceof Navigation)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   @Override
   public boolean a() {
      this.f();
      if (this.e && this.a.world.w()) {
         return false;
      } else {
         Village village = this.a.world.ae().getClosestVillage(new BlockPosition(this.a), 0);
         if (village == null) {
            return false;
         } else {
            this.d = this.a(village);
            if (this.d == null) {
               return false;
            } else {
               Navigation navigation = (Navigation)this.a.getNavigation();
               boolean flag = navigation.g();
               navigation.b(false);
               this.c = navigation.a(this.d.d());
               navigation.b(flag);
               if (this.c != null) {
                  return true;
               } else {
                  Vec3D vec3d = RandomPositionGenerator.a(this.a, 10, 7, new Vec3D(this.d.d().getX(), this.d.d().getY(), this.d.d().getZ()));
                  if (vec3d == null) {
                     return false;
                  } else {
                     navigation.b(false);
                     this.c = this.a.getNavigation().a(vec3d.a, vec3d.b, vec3d.c, PositionPathSearchType.MOVETHROUGHVILLAGE);
                     navigation.b(flag);
                     return this.c != null;
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean b() {
      if (this.a.getNavigation().m()) {
         return false;
      } else {
         float f = this.a.width + 4.0F;
         return this.a.b(this.d.d()) > f * f;
      }
   }

   @Override
   public void c() {
      this.a.getNavigation().a(this.c, this.b);
   }

   @Override
   public void d() {
      if (this.a.getNavigation().m() || this.a.b(this.d.d()) < 16.0) {
         this.f.add(this.d);
      }
   }

   private VillageDoor a(Village village) {
      VillageDoor villagedoor = null;
      int i = Integer.MAX_VALUE;

      for (VillageDoor villagedoor1 : village.f()) {
         int j = villagedoor1.b(MathHelper.floor(this.a.locX), MathHelper.floor(this.a.locY), MathHelper.floor(this.a.locZ));
         if (j < i && !this.a(villagedoor1)) {
            villagedoor = villagedoor1;
            i = j;
         }
      }

      return villagedoor;
   }

   private boolean a(VillageDoor villagedoor) {
      for (VillageDoor villagedoor1 : this.f) {
         if (villagedoor.d().equals(villagedoor1.d())) {
            return true;
         }
      }

      return false;
   }

   private void f() {
      if (this.f.size() > 15) {
         this.f.remove(0);
      }
   }
}
