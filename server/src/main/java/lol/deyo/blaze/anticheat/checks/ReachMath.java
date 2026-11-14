package lol.deyo.blaze.anticheat.checks;

public final class ReachMath {
   private ReachMath() {
   }

   public static double minAttackDistance(
      double ox, double oy, double oz,
      double dirX, double dirY, double dirZ,
      double minX, double minY, double minZ,
      double maxX, double maxY, double maxZ,
      double fallback
   ) {
      double invX = dirX != 0.0 ? 1.0 / dirX : Double.POSITIVE_INFINITY;
      double invY = dirY != 0.0 ? 1.0 / dirY : Double.POSITIVE_INFINITY;
      double invZ = dirZ != 0.0 ? 1.0 / dirZ : Double.POSITIVE_INFINITY;
      double tx1 = (minX - ox) * invX;
      double tx2 = (maxX - ox) * invX;
      double tmin = Math.min(tx1, tx2);
      double tmax = Math.max(tx1, tx2);
      double ty1 = (minY - oy) * invY;
      double ty2 = (maxY - oy) * invY;
      tmin = Math.max(tmin, Math.min(ty1, ty2));
      tmax = Math.min(tmax, Math.max(ty1, ty2));
      double tz1 = (minZ - oz) * invZ;
      double tz2 = (maxZ - oz) * invZ;
      tmin = Math.max(tmin, Math.min(tz1, tz2));
      tmax = Math.min(tmax, Math.max(tz1, tz2));
      if (tmax < 0.0 || tmin > tmax) {
         return fallback;
      }

      return Math.max(0.0, tmin);
   }

   public static double pointBoxDistance(
      double ox, double oy, double oz,
      double minX, double minY, double minZ,
      double maxX, double maxY, double maxZ
   ) {
      double cx = Math.max(minX, Math.min(ox, maxX));
      double cy = Math.max(minY, Math.min(oy, maxY));
      double cz = Math.max(minZ, Math.min(oz, maxZ));
      double dx = ox - cx;
      double dy = oy - cy;
      double dz = oz - cz;
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
   }

   public static boolean rayIntersectsBox(
      double ox, double oy, double oz,
      double dirX, double dirY, double dirZ,
      double minX, double minY, double minZ,
      double maxX, double maxY, double maxZ
   ) {
      double invX = dirX != 0.0 ? 1.0 / dirX : Double.POSITIVE_INFINITY;
      double invY = dirY != 0.0 ? 1.0 / dirY : Double.POSITIVE_INFINITY;
      double invZ = dirZ != 0.0 ? 1.0 / dirZ : Double.POSITIVE_INFINITY;
      double tx1 = (minX - ox) * invX;
      double tx2 = (maxX - ox) * invX;
      double tmin = Math.min(tx1, tx2);
      double tmax = Math.max(tx1, tx2);
      double ty1 = (minY - oy) * invY;
      double ty2 = (maxY - oy) * invY;
      tmin = Math.max(tmin, Math.min(ty1, ty2));
      tmax = Math.min(tmax, Math.max(ty1, ty2));
      double tz1 = (minZ - oz) * invZ;
      double tz2 = (maxZ - oz) * invZ;
      tmin = Math.max(tmin, Math.min(tz1, tz2));
      tmax = Math.min(tmax, Math.max(tz1, tz2));
      return tmax >= 0.0 && tmin <= tmax && tmax >= tmin;
   }
}
