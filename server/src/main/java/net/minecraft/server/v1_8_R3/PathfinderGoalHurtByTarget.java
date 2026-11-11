package net.minecraft.server.v1_8_R3;

import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class PathfinderGoalHurtByTarget extends PathfinderGoalTarget {
   private final boolean a;
   private int b;
   private final Class[] c;

   public PathfinderGoalHurtByTarget(EntityCreature entitycreature, boolean flag, Class... aclass) {
      super(entitycreature, false);
      this.a = flag;
      this.c = aclass;
      this.a(1);
   }

   @Override
   public boolean a() {
      int i = this.e.be();
      return i != this.b && this.a(this.e.getLastDamager(), false);
   }

   @Override
   public void c() {
      this.e.setGoalTarget(this.e.getLastDamager(), TargetReason.TARGET_ATTACKED_ENTITY, true);
      this.b = this.e.be();
      if (this.a) {
         double d0 = this.f();

         for (EntityCreature entitycreature : this.e
            .world
            .a(
               this.e.getClass(),
               new AxisAlignedBB(this.e.locX, this.e.locY, this.e.locZ, this.e.locX + 1.0, this.e.locY + 1.0, this.e.locZ + 1.0).grow(d0, 10.0, d0)
            )) {
            if (this.e != entitycreature && entitycreature.getGoalTarget() == null && !entitycreature.c(this.e.getLastDamager())) {
               boolean flag = false;

               for (Class oclass : this.c) {
                  if (entitycreature.getClass() == oclass) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  this.a(entitycreature, this.e.getLastDamager());
               }
            }
         }
      }

      super.c();
   }

   protected void a(EntityCreature entitycreature, EntityLiving entityliving) {
      entitycreature.setGoalTarget(entityliving, TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true);
   }
}
