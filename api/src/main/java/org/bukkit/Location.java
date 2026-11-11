package org.bukkit;

import java.util.HashMap;
import java.util.Map;
import net.jafama.FastMath;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class Location implements Cloneable, ConfigurationSerializable {
   private org.bukkit.World world;
   private double x;
   private double y;
   private double z;
   private float pitch;
   private float yaw;

   public Location(org.bukkit.World world, double x, double y, double z) {
      this(world, x, y, z, 0.0F, 0.0F);
   }

   public Location(org.bukkit.World world, double x, double y, double z, float yaw, float pitch) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.pitch = pitch;
      this.yaw = yaw;
   }

   public void setWorld(org.bukkit.World world) {
      this.world = world;
   }

   public org.bukkit.World getWorld() {
      return this.world;
   }

   public org.bukkit.Chunk getChunk() {
      return this.world.getChunkAt(this);
   }

   public Block getBlock() {
      return this.world.getBlockAt(this);
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getX() {
      return this.x;
   }

   public int getBlockX() {
      return locToBlock(this.x);
   }

   public void setY(double y) {
      this.y = y;
   }

   public double getY() {
      return this.y;
   }

   public int getBlockY() {
      return locToBlock(this.y);
   }

   public void setZ(double z) {
      this.z = z;
   }

   public double getZ() {
      return this.z;
   }

   public int getBlockZ() {
      return locToBlock(this.z);
   }

   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   public float getYaw() {
      return this.yaw;
   }

   public void setPitch(float pitch) {
      this.pitch = pitch;
   }

   public float getPitch() {
      return this.pitch;
   }

   public Vector getDirection() {
      Vector vector = new Vector();
      double rotX = this.getYaw();
      double rotY = this.getPitch();
      vector.setY(-FastMath.sin(Math.toRadians(rotY)));
      double xz = FastMath.cos(Math.toRadians(rotY));
      vector.setX(-xz * FastMath.sin(Math.toRadians(rotX)));
      vector.setZ(xz * FastMath.cos(Math.toRadians(rotX)));
      return vector;
   }

   public org.bukkit.Location setDirection(Vector vector) {
      double _2PI = Math.PI * 2;
      double x = vector.getX();
      double z = vector.getZ();
      if (x == 0.0 && z == 0.0) {
         this.pitch = vector.getY() > 0.0 ? -90.0F : 90.0F;
         return this;
      } else {
         double theta = FastMath.atan2(-x, z);
         this.yaw = (float)Math.toDegrees((theta + (Math.PI * 2)) % (Math.PI * 2));
         double x2 = NumberConversions.square(x);
         double z2 = NumberConversions.square(z);
         double xz = Math.sqrt(x2 + z2);
         this.pitch = (float)Math.toDegrees(FastMath.atan(-vector.getY() / xz));
         return this;
      }
   }

   public org.bukkit.Location add(org.bukkit.Location vec) {
      if (vec != null && vec.getWorld() == this.getWorld()) {
         this.x = this.x + vec.x;
         this.y = this.y + vec.y;
         this.z = this.z + vec.z;
         return this;
      } else {
         throw new IllegalArgumentException("Cannot add Locations of differing worlds");
      }
   }

   public org.bukkit.Location add(Vector vec) {
      this.x = this.x + vec.getX();
      this.y = this.y + vec.getY();
      this.z = this.z + vec.getZ();
      return this;
   }

   public org.bukkit.Location add(double x, double y, double z) {
      this.x += x;
      this.y += y;
      this.z += z;
      return this;
   }

   public org.bukkit.Location subtract(org.bukkit.Location vec) {
      if (vec != null && vec.getWorld() == this.getWorld()) {
         this.x = this.x - vec.x;
         this.y = this.y - vec.y;
         this.z = this.z - vec.z;
         return this;
      } else {
         throw new IllegalArgumentException("Cannot add Locations of differing worlds");
      }
   }

   public org.bukkit.Location subtract(Vector vec) {
      this.x = this.x - vec.getX();
      this.y = this.y - vec.getY();
      this.z = this.z - vec.getZ();
      return this;
   }

   public org.bukkit.Location subtract(double x, double y, double z) {
      this.x -= x;
      this.y -= y;
      this.z -= z;
      return this;
   }

   public double length() {
      return Math.sqrt(NumberConversions.square(this.x) + NumberConversions.square(this.y) + NumberConversions.square(this.z));
   }

   public double lengthSquared() {
      return NumberConversions.square(this.x) + NumberConversions.square(this.y) + NumberConversions.square(this.z);
   }

   public double distance(org.bukkit.Location o) {
      return Math.sqrt(this.distanceSquared(o));
   }

   public double distanceSquared(org.bukkit.Location o) {
      if (o == null) {
         throw new IllegalArgumentException("Cannot measure distance to a null location");
      } else if (o.getWorld() != null && this.getWorld() != null) {
         if (o.getWorld() != this.getWorld()) {
            throw new IllegalArgumentException("Cannot measure distance between " + this.getWorld().getName() + " and " + o.getWorld().getName());
         } else {
            return NumberConversions.square(this.x - o.x) + NumberConversions.square(this.y - o.y) + NumberConversions.square(this.z - o.z);
         }
      } else {
         throw new IllegalArgumentException("Cannot measure distance to a null world");
      }
   }

   public org.bukkit.Location multiply(double m) {
      this.x *= m;
      this.y *= m;
      this.z *= m;
      return this;
   }

   public org.bukkit.Location zero() {
      this.x = 0.0;
      this.y = 0.0;
      this.z = 0.0;
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         org.bukkit.Location other = (org.bukkit.Location)obj;
         if (this.world == other.world || this.world != null && this.world.equals(other.world)) {
            if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
               return false;
            } else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
               return false;
            } else if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
               return false;
            } else {
               return Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)
                  ? false
                  : Float.floatToIntBits(this.yaw) == Float.floatToIntBits(other.yaw);
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 19 * hash + (this.world != null ? this.world.hashCode() : 0);
      hash = 19 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
      hash = 19 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
      hash = 19 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
      hash = 19 * hash + Float.floatToIntBits(this.pitch);
      return 19 * hash + Float.floatToIntBits(this.yaw);
   }

   @Override
   public String toString() {
      return "Location{world=" + this.world + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ",pitch=" + this.pitch + ",yaw=" + this.yaw + '}';
   }

   public Vector toVector() {
      return new Vector(this.x, this.y, this.z);
   }

   public org.bukkit.Location clone() {
      try {
         return (org.bukkit.Location)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new Error(var2);
      }
   }

   public static int locToBlock(double loc) {
      return NumberConversions.floor(loc);
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> data = new HashMap<>();
      data.put("world", this.world.getName());
      data.put("x", this.x);
      data.put("y", this.y);
      data.put("z", this.z);
      data.put("yaw", this.yaw);
      data.put("pitch", this.pitch);
      return data;
   }

   public static org.bukkit.Location deserialize(Map<String, Object> args) {
      org.bukkit.World world = org.bukkit.Bukkit.getWorld((String)args.get("world"));
      if (world == null) {
         throw new IllegalArgumentException("unknown world");
      } else {
         return new org.bukkit.Location(
            world,
            NumberConversions.toDouble(args.get("x")),
            NumberConversions.toDouble(args.get("y")),
            NumberConversions.toDouble(args.get("z")),
            NumberConversions.toFloat(args.get("yaw")),
            NumberConversions.toFloat(args.get("pitch"))
         );
      }
   }
}
