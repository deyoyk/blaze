package lol.deyo.blaze.utils;

public class QueuedTeleport {
   private final double x;
   private final double y;
   private final double z;
   private final boolean relative;

   public QueuedTeleport(double x, double y, double z, boolean relative) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.relative = relative;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public boolean isRelative() {
      return this.relative;
   }
}
