package net.minecraft.server.v1_8_R3;

public abstract class PathfinderGoalDoorInteract extends PathfinderGoal {
   protected EntityInsentient a;
   protected BlockPosition b = BlockPosition.ZERO;
   protected BlockDoor c;
   boolean d;
   float e;
   float f;

   public PathfinderGoalDoorInteract(EntityInsentient entityinsentient) {
      this.a = entityinsentient;
      if (!(entityinsentient.getNavigation() instanceof Navigation)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   @Override
   public boolean a() {
      if (!this.a.positionChanged) {
         return false;
      } else {
         Navigation navigation = (Navigation)this.a.getNavigation();
         PathEntity pathentity = navigation.j();
         if (pathentity != null && !pathentity.b() && navigation.g()) {
            for (int i = 0; i < Math.min(pathentity.e() + 2, pathentity.d()); i++) {
               PathPoint pathpoint = pathentity.a(i);
               this.b = new BlockPosition(pathpoint.a, pathpoint.b + 1, pathpoint.c);
               if (this.a.e(this.b.getX(), this.a.locY, this.b.getZ()) <= 2.25) {
                  this.c = this.a(this.b);
                  if (this.c != null) {
                     return true;
                  }
               }
            }

            this.b = new BlockPosition(this.a).up();
            this.c = this.a(this.b);
            return this.c != null;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean b() {
      return !this.d;
   }

   @Override
   public void c() {
      this.d = false;
      this.e = (float)(this.b.getX() + 0.5F - this.a.locX);
      this.f = (float)(this.b.getZ() + 0.5F - this.a.locZ);
   }

   @Override
   public void e() {
      float f = (float)(this.b.getX() + 0.5F - this.a.locX);
      float f1 = (float)(this.b.getZ() + 0.5F - this.a.locZ);
      float f2 = this.e * f + this.f * f1;
      if (f2 < 0.0F) {
         this.d = true;
      }
   }

   private BlockDoor a(BlockPosition blockposition) {
      Block block = this.a.world.getType(blockposition).getBlock();
      return block instanceof BlockDoor && block.getMaterial() == Material.WOOD ? (BlockDoor)block : null;
   }
}
