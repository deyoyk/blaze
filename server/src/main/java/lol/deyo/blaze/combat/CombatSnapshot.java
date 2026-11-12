package lol.deyo.blaze.combat;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Entity;

/**
 * One sampled combat state. Instances are reused by PlayerPositionHistory.
 */
public final class CombatSnapshot {
   public boolean valid;
   public long timeNanos;
   public double minX;
   public double minY;
   public double minZ;
   public double maxX;
   public double maxY;
   public double maxZ;
   public double x;
   public double y;
   public double z;
   public double motX;
   public double motY;
   public double motZ;
   public float yaw;
   public float pitch;
   public boolean onGround;
   public boolean sprinting;

   public void set(Entity entity, long timeNanos) {
      AxisAlignedBB box = entity.getBoundingBox();
      this.valid = true;
      this.timeNanos = timeNanos;
      this.minX = box.a;
      this.minY = box.b;
      this.minZ = box.c;
      this.maxX = box.d;
      this.maxY = box.e;
      this.maxZ = box.f;
      this.x = entity.locX;
      this.y = entity.locY;
      this.z = entity.locZ;
      this.motX = entity.motX;
      this.motY = entity.motY;
      this.motZ = entity.motZ;
      this.yaw = entity.yaw;
      this.pitch = entity.pitch;
      this.onGround = entity.onGround;
      this.sprinting = entity.isSprinting();
   }

   public void copyFrom(CombatSnapshot other) {
      this.valid = other.valid;
      this.timeNanos = other.timeNanos;
      this.minX = other.minX;
      this.minY = other.minY;
      this.minZ = other.minZ;
      this.maxX = other.maxX;
      this.maxY = other.maxY;
      this.maxZ = other.maxZ;
      this.x = other.x;
      this.y = other.y;
      this.z = other.z;
      this.motX = other.motX;
      this.motY = other.motY;
      this.motZ = other.motZ;
      this.yaw = other.yaw;
      this.pitch = other.pitch;
      this.onGround = other.onGround;
      this.sprinting = other.sprinting;
   }

   public void interpolate(CombatSnapshot before, CombatSnapshot after, double t) {
      this.valid = true;
      this.timeNanos = before.timeNanos + (long)((after.timeNanos - before.timeNanos) * t);
      this.minX = lerp(before.minX, after.minX, t);
      this.minY = lerp(before.minY, after.minY, t);
      this.minZ = lerp(before.minZ, after.minZ, t);
      this.maxX = lerp(before.maxX, after.maxX, t);
      this.maxY = lerp(before.maxY, after.maxY, t);
      this.maxZ = lerp(before.maxZ, after.maxZ, t);
      this.x = lerp(before.x, after.x, t);
      this.y = lerp(before.y, after.y, t);
      this.z = lerp(before.z, after.z, t);
      this.motX = lerp(before.motX, after.motX, t);
      this.motY = lerp(before.motY, after.motY, t);
      this.motZ = lerp(before.motZ, after.motZ, t);
      this.yaw = (float)lerp(before.yaw, after.yaw, t);
      this.pitch = (float)lerp(before.pitch, after.pitch, t);
      this.onGround = before.onGround;
      this.sprinting = before.sprinting;
   }

   public void clear() {
      this.valid = false;
      this.timeNanos = 0L;
   }

   private static double lerp(double from, double to, double t) {
      return from + (to - from) * t;
   }
}
