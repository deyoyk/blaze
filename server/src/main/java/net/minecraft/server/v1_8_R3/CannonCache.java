package net.minecraft.server.v1_8_R3;

public class CannonCache {
   private AxisAlignedBB boundingBox;
   private double locX;
   private double locY;
   private double locZ;
   private double lastX;
   private double lastY;
   private double lastZ;
   private double motX;
   private double motY;
   private double motZ;
   private double lastMotX;
   private double lastMotY;
   private double lastMotZ;
   private boolean onGround;

   public boolean move(Entity entity) {
      if (entity.locX == this.lastX
         && entity.locY == this.lastY
         && entity.locZ == this.lastZ
         && entity.motX == this.lastMotX
         && entity.motY == this.lastMotY
         && entity.motZ == this.lastMotZ) {
         entity.boundingBox = this.boundingBox;
         entity.onGround = this.onGround;
         entity.locX = this.locX;
         entity.locY = this.locY;
         entity.locZ = this.locZ;
         entity.motX = this.motX;
         entity.motY = this.motY;
         entity.motZ = this.motZ;
         return true;
      } else {
         return false;
      }
   }

   public void cache(Entity entity) {
      this.onGround = entity.onGround;
      this.boundingBox = entity.boundingBox;
      this.lastX = entity.lastX;
      this.lastY = entity.lastY;
      this.lastZ = entity.lastZ;
      this.lastMotX = entity.lastMotX;
      this.lastMotY = entity.lastMotY;
      this.lastMotZ = entity.lastMotZ;
      this.locX = entity.locX;
      this.locY = entity.locY;
      this.locZ = entity.locZ;
      this.motX = entity.motX;
      this.motY = entity.motY;
      this.motZ = entity.motZ;
   }

   public void clear() {
      this.lastX = Double.MAX_VALUE;
   }
}
