package org.bukkit.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import net.jafama.FastMath;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Vector")
public class Vector implements Cloneable, ConfigurationSerializable {
   private static final long serialVersionUID = -2657651106777219169L;
   private static Random random = new Random();
   private static final double epsilon = 1.0E-6;
   protected double x;
   protected double y;
   protected double z;

   public Vector() {
      this.x = 0.0;
      this.y = 0.0;
      this.z = 0.0;
   }

   public Vector(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vector(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vector(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vector add(Vector vec) {
      this.x = this.x + vec.x;
      this.y = this.y + vec.y;
      this.z = this.z + vec.z;
      return this;
   }

   public Vector subtract(Vector vec) {
      this.x = this.x - vec.x;
      this.y = this.y - vec.y;
      this.z = this.z - vec.z;
      return this;
   }

   public Vector multiply(Vector vec) {
      this.x = this.x * vec.x;
      this.y = this.y * vec.y;
      this.z = this.z * vec.z;
      return this;
   }

   public Vector divide(Vector vec) {
      this.x = this.x / vec.x;
      this.y = this.y / vec.y;
      this.z = this.z / vec.z;
      return this;
   }

   public Vector copy(Vector vec) {
      this.x = vec.x;
      this.y = vec.y;
      this.z = vec.z;
      return this;
   }

   public double length() {
      return Math.sqrt(NumberConversions.square(this.x) + NumberConversions.square(this.y) + NumberConversions.square(this.z));
   }

   public double lengthSquared() {
      return NumberConversions.square(this.x) + NumberConversions.square(this.y) + NumberConversions.square(this.z);
   }

   public double distance(Vector o) {
      return Math.sqrt(NumberConversions.square(this.x - o.x) + NumberConversions.square(this.y - o.y) + NumberConversions.square(this.z - o.z));
   }

   public double distanceSquared(Vector o) {
      return NumberConversions.square(this.x - o.x) + NumberConversions.square(this.y - o.y) + NumberConversions.square(this.z - o.z);
   }

   public float angle(Vector other) {
      double dot = this.dot(other) / (this.length() * other.length());
      return (float)FastMath.acos(dot);
   }

   public Vector midpoint(Vector other) {
      this.x = (this.x + other.x) / 2.0;
      this.y = (this.y + other.y) / 2.0;
      this.z = (this.z + other.z) / 2.0;
      return this;
   }

   public Vector getMidpoint(Vector other) {
      double x = (this.x + other.x) / 2.0;
      double y = (this.y + other.y) / 2.0;
      double z = (this.z + other.z) / 2.0;
      return new Vector(x, y, z);
   }

   public Vector multiply(int m) {
      this.x *= m;
      this.y *= m;
      this.z *= m;
      return this;
   }

   public Vector multiply(double m) {
      this.x *= m;
      this.y *= m;
      this.z *= m;
      return this;
   }

   public Vector multiply(float m) {
      this.x *= m;
      this.y *= m;
      this.z *= m;
      return this;
   }

   public double dot(Vector other) {
      return this.x * other.x + this.y * other.y + this.z * other.z;
   }

   public Vector crossProduct(Vector o) {
      double newX = this.y * o.z - o.y * this.z;
      double newY = this.z * o.x - o.z * this.x;
      double newZ = this.x * o.y - o.x * this.y;
      this.x = newX;
      this.y = newY;
      this.z = newZ;
      return this;
   }

   public Vector normalize() {
      double length = this.length();
      this.x /= length;
      this.y /= length;
      this.z /= length;
      return this;
   }

   public Vector zero() {
      this.x = 0.0;
      this.y = 0.0;
      this.z = 0.0;
      return this;
   }

   public boolean isInAABB(Vector min, Vector max) {
      return this.x >= min.x && this.x <= max.x && this.y >= min.y && this.y <= max.y && this.z >= min.z && this.z <= max.z;
   }

   public boolean isInSphere(Vector origin, double radius) {
      return NumberConversions.square(origin.x - this.x) + NumberConversions.square(origin.y - this.y) + NumberConversions.square(origin.z - this.z)
         <= NumberConversions.square(radius);
   }

   public double getX() {
      return this.x;
   }

   public int getBlockX() {
      return NumberConversions.floor(this.x);
   }

   public double getY() {
      return this.y;
   }

   public int getBlockY() {
      return NumberConversions.floor(this.y);
   }

   public double getZ() {
      return this.z;
   }

   public int getBlockZ() {
      return NumberConversions.floor(this.z);
   }

   public Vector setX(int x) {
      this.x = x;
      return this;
   }

   public Vector setX(double x) {
      this.x = x;
      return this;
   }

   public Vector setX(float x) {
      this.x = x;
      return this;
   }

   public Vector setY(int y) {
      this.y = y;
      return this;
   }

   public Vector setY(double y) {
      this.y = y;
      return this;
   }

   public Vector setY(float y) {
      this.y = y;
      return this;
   }

   public Vector setZ(int z) {
      this.z = z;
      return this;
   }

   public Vector setZ(double z) {
      this.z = z;
      return this;
   }

   public Vector setZ(float z) {
      this.z = z;
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Vector)) {
         return false;
      } else {
         Vector other = (Vector)obj;
         return Math.abs(this.x - other.x) < 1.0E-6
            && Math.abs(this.y - other.y) < 1.0E-6
            && Math.abs(this.z - other.z) < 1.0E-6
            && this.getClass().equals(obj.getClass());
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 79 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
      hash = 79 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
      return 79 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
   }

   public Vector clone() {
      try {
         return (Vector)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new Error(var2);
      }
   }

   @Override
   public String toString() {
      return this.x + "," + this.y + "," + this.z;
   }

   public org.bukkit.Location toLocation(org.bukkit.World world) {
      return new org.bukkit.Location(world, this.x, this.y, this.z);
   }

   public org.bukkit.Location toLocation(org.bukkit.World world, float yaw, float pitch) {
      return new org.bukkit.Location(world, this.x, this.y, this.z, yaw, pitch);
   }

   public BlockVector toBlockVector() {
      return new BlockVector(this.x, this.y, this.z);
   }

   public static double getEpsilon() {
      return 1.0E-6;
   }

   public static Vector getMinimum(Vector v1, Vector v2) {
      return new Vector(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y), Math.min(v1.z, v2.z));
   }

   public static Vector getMaximum(Vector v1, Vector v2) {
      return new Vector(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z));
   }

   public static Vector getRandom() {
      return new Vector(random.nextDouble(), random.nextDouble(), random.nextDouble());
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap<>();
      result.put("x", this.getX());
      result.put("y", this.getY());
      result.put("z", this.getZ());
      return result;
   }

   public static Vector deserialize(Map<String, Object> args) {
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      if (args.containsKey("x")) {
         x = (Double)args.get("x");
      }

      if (args.containsKey("y")) {
         y = (Double)args.get("y");
      }

      if (args.containsKey("z")) {
         z = (Double)args.get("z");
      }

      return new Vector(x, y, z);
   }
}
