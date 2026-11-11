package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;

public class WorldBorder {
   private final List<IWorldBorderListener> a = Lists.newArrayList();
   private double b = 0.0;
   private double c = 0.0;
   private double d = 6.0E7;
   private double e = this.d;
   private long f;
   private long g;
   private int h = 29999984;
   private double i = 0.2;
   private double j = 5.0;
   private int k = 15;
   private int l = 5;
   public WorldServer world;

   public boolean a(BlockPosition blockposition) {
      return blockposition.getX() + 1 > this.b() && blockposition.getX() < this.d() && blockposition.getZ() + 1 > this.c() && blockposition.getZ() < this.e();
   }

   public boolean a(int x, int z) {
      return x + 1 > this.b() && x < this.d() && z + 1 > this.c() && z < this.e();
   }

   public boolean isInBounds(ChunkCoordIntPair chunkcoordintpair) {
      return this.isInBounds(chunkcoordintpair.x, chunkcoordintpair.z);
   }

   public boolean isInBounds(long chunkcoords) {
      return this.isInBounds(LongHash.msw(chunkcoords), LongHash.lsw(chunkcoords));
   }

   public boolean isInBounds(int x, int z) {
      return (x << 4) + 15 > this.b() && x << 4 < this.d() && (z << 4) + 15 > this.c() && x << 4 < this.e();
   }

   public boolean a(AxisAlignedBB axisalignedbb) {
      return axisalignedbb.d > this.b() && axisalignedbb.a < this.d() && axisalignedbb.f > this.c() && axisalignedbb.c < this.e();
   }

   public double a(Entity entity) {
      return this.b(entity.locX, entity.locZ);
   }

   public double b(double d0, double d1) {
      double d2 = d1 - this.c();
      double d3 = this.e() - d1;
      double d4 = d0 - this.b();
      double d5 = this.d() - d0;
      double d6 = Math.min(d4, d5);
      d6 = Math.min(d6, d2);
      return Math.min(d6, d3);
   }

   public EnumWorldBorderState getState() {
      return this.e < this.d ? EnumWorldBorderState.SHRINKING : (this.e > this.d ? EnumWorldBorderState.GROWING : EnumWorldBorderState.STATIONARY);
   }

   public double b() {
      double d0 = this.getCenterX() - this.getSize() / 2.0;
      if (d0 < -this.h) {
         d0 = -this.h;
      }

      return d0;
   }

   public double c() {
      double d0 = this.getCenterZ() - this.getSize() / 2.0;
      if (d0 < -this.h) {
         d0 = -this.h;
      }

      return d0;
   }

   public double d() {
      double d0 = this.getCenterX() + this.getSize() / 2.0;
      if (d0 > this.h) {
         d0 = this.h;
      }

      return d0;
   }

   public double e() {
      double d0 = this.getCenterZ() + this.getSize() / 2.0;
      if (d0 > this.h) {
         d0 = this.h;
      }

      return d0;
   }

   public double getCenterX() {
      return this.b;
   }

   public double getCenterZ() {
      return this.c;
   }

   public void setCenter(double d0, double d1) {
      this.b = d0;
      this.c = d1;

      for (IWorldBorderListener iWorldBorderListener : this.k()) {
         iWorldBorderListener.a(this, d0, d1);
      }
   }

   public double getSize() {
      if (this.getState() != EnumWorldBorderState.STATIONARY) {
         double d0 = (float)(System.currentTimeMillis() - this.g) / (float)(this.f - this.g);
         if (d0 < 1.0) {
            return this.d + (this.e - this.d) * d0;
         }

         this.setSize(this.e);
      }

      return this.d;
   }

   public long i() {
      return this.getState() != EnumWorldBorderState.STATIONARY ? this.f - System.currentTimeMillis() : 0L;
   }

   public double j() {
      return this.e;
   }

   public void setSize(double d0) {
      this.d = d0;
      this.e = d0;
      this.f = System.currentTimeMillis();
      this.g = this.f;

      for (IWorldBorderListener iWorldBorderListener : this.k()) {
         iWorldBorderListener.a(this, d0);
      }
   }

   public void transitionSizeBetween(double d0, double d1, long i) {
      this.d = d0;
      this.e = d1;
      this.g = System.currentTimeMillis();
      this.f = this.g + i;

      for (IWorldBorderListener iWorldBorderListener : this.k()) {
         iWorldBorderListener.a(this, d0, d1, i);
      }
   }

   protected List<IWorldBorderListener> k() {
      return Lists.newArrayList(this.a);
   }

   public void a(IWorldBorderListener iworldborderlistener) {
      if (!this.a.contains(iworldborderlistener)) {
         this.a.add(iworldborderlistener);
      }
   }

   public void follow(WorldBorder source) {
      double size = source.getSize();
      long remaining = source.i();
      if (remaining > 0L) {
         this.transitionSizeBetween(size, source.j(), remaining);
      } else {
         this.setSize(size);
      }

      this.setCenter(source.getCenterX(), source.getCenterZ());
      this.setDamageAmount(source.getDamageAmount());
      this.setDamageBuffer(source.getDamageBuffer());
      this.setWarningDistance(source.getWarningDistance());
      this.setWarningTime(source.getWarningTime());
      source.a(new IWorldBorderListener() {
         @Override
         public void a(WorldBorder border, double value) {
            WorldBorder.this.setSize(value);
         }

         @Override
         public void a(WorldBorder border, double start, double end, long time) {
            WorldBorder.this.transitionSizeBetween(start, end, time);
         }

         @Override
         public void a(WorldBorder border, double x, double z) {
            WorldBorder.this.setCenter(x, z);
         }

         @Override
         public void a(WorldBorder border, int time) {
            WorldBorder.this.setWarningTime(time);
         }

         @Override
         public void b(WorldBorder border, int distance) {
            WorldBorder.this.setWarningDistance(distance);
         }

         @Override
         public void b(WorldBorder border, double amount) {
            WorldBorder.this.setDamageAmount(amount);
         }

         @Override
         public void c(WorldBorder border, double buffer) {
            WorldBorder.this.setDamageBuffer(buffer);
         }
      });
   }

   public void a(int i) {
      this.h = i;
   }

   public int l() {
      return this.h;
   }

   public double getDamageBuffer() {
      return this.j;
   }

   public void setDamageBuffer(double d0) {
      this.j = d0;

      for (IWorldBorderListener iWorldBorderListener : this.k()) {
         iWorldBorderListener.c(this, d0);
      }
   }

   public double getDamageAmount() {
      return this.i;
   }

   public void setDamageAmount(double d0) {
      this.i = d0;

      for (IWorldBorderListener iWorldBorderListener : this.k()) {
         iWorldBorderListener.b(this, d0);
      }
   }

   public int getWarningTime() {
      return this.k;
   }

   public void setWarningTime(int i) {
      this.k = i;

      for (IWorldBorderListener iWorldBorderListener : this.k()) {
         iWorldBorderListener.a(this, i);
      }
   }

   public int getWarningDistance() {
      return this.l;
   }

   public void setWarningDistance(int i) {
      this.l = i;

      for (IWorldBorderListener iWorldBorderListener : this.k()) {
         iWorldBorderListener.b(this, i);
      }
   }
}
