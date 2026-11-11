package net.minecraft.server.v1_8_R3;

public class PathfinderGoalLookAtTradingPlayer extends PathfinderGoalLookAtPlayer {
   private final EntityVillager e;

   public PathfinderGoalLookAtTradingPlayer(EntityVillager var1) {
      super(var1, EntityHuman.class, 8.0F);
      this.e = var1;
   }

   @Override
   public boolean a() {
      if (this.e.co()) {
         this.b = this.e.v_();
         return true;
      } else {
         return false;
      }
   }
}
