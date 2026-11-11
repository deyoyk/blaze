package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.pathsearch.PathSearchJobEntity;
import lol.deyo.blaze.pathsearch.PathSearchJobPosition;
import lol.deyo.blaze.pathsearch.PositionPathSearchType;
import lol.deyo.blaze.utils.WeakChunkCache;
import java.util.List;

public abstract class NavigationAbstract {
   protected EntityInsentient b;
   protected World c;
   protected PathEntity d;
   protected double e;
   private final AttributeInstance a;
   private int f;
   private int g;
   private Vec3D h = new Vec3D(0.0, 0.0, 0.0);
   private float i = 1.0F;
   private final Pathfinder j;

   public NavigationAbstract(EntityInsentient entityinsentient, World world) {
      this.b = entityinsentient;
      this.c = world;
      this.a = entityinsentient.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
      this.j = this.a();
   }

   protected abstract Pathfinder a();

   public void a(double d0) {
      this.e = d0;
   }

   public float i() {
      return (float)this.a.getValue();
   }

   public final PathEntity a(double d0, double d1, double d2) {
      return this.a(new BlockPosition(MathHelper.floor(d0), (int)d1, MathHelper.floor(d2)));
   }

   public PathEntity a(BlockPosition blockposition) {
      if (!this.b()) {
         return null;
      } else {
         float f = this.i();
         this.c.methodProfiler.a("pathfind");
         BlockPosition blockposition1 = new BlockPosition(this.b);
         int i = (int)(f + 8.0F);
         BlockPosition lower = blockposition1.a(-i, -i, -i);
         BlockPosition upper = blockposition1.a(i, i, i);
         WeakChunkCache chunkcache = new WeakChunkCache(this.c, lower.getX(), lower.getZ(), upper.getX(), upper.getZ(), 0);
         PathEntity pathentity = this.j.a(chunkcache, this.b, blockposition, f);
         this.c.methodProfiler.b();
         return pathentity;
      }
   }

   public boolean a(double d0, double d1, double d2, double d3) {
      PathEntity pathentity = this.a(MathHelper.floor(d0), (int)d1, MathHelper.floor(d2));
      return this.a(pathentity, d3);
   }

   public void a(float f) {
      this.i = f;
   }

   public PathEntity a(Entity entity) {
      if (!this.b()) {
         return null;
      } else {
         float f = this.i();
         this.c.methodProfiler.a("pathfind");
         BlockPosition blockposition = new BlockPosition(this.b).up();
         int i = (int)(f + 16.0F);
         BlockPosition lower = blockposition.a(-i, -i, -i);
         BlockPosition upper = blockposition.a(i, i, i);
         WeakChunkCache chunkcache = new WeakChunkCache(this.c, lower.getX(), lower.getZ(), upper.getX(), upper.getZ(), 0);
         PathEntity pathentity = this.j.a(chunkcache, this.b, entity, f);
         this.c.methodProfiler.b();
         return pathentity;
      }
   }

   public boolean a(Entity entity, double d0) {
      PathEntity pathentity = this.a(entity);
      return pathentity != null && this.a(pathentity, d0);
   }

   public boolean a(PathEntity pathentity, double d0) {
      if (pathentity == null) {
         this.d = null;
         return false;
      } else {
         if (!pathentity.a(this.d)) {
            this.d = pathentity;
         }

         this.d();
         if (this.d.d() == 0) {
            return false;
         } else {
            this.e = d0;
            Vec3D vec3d = this.c();
            this.g = this.f;
            this.h = vec3d;
            return true;
         }
      }
   }

   public PathEntity j() {
      return this.d;
   }

   public void k() {
      this.f++;
      if (!this.m()) {
         if (this.b()) {
            this.l();
         } else if (this.d != null && this.d.e() < this.d.d()) {
            Vec3D vec3d = this.c();
            Vec3D vec3d1 = this.d.a(this.b, this.d.e());
            if (vec3d.b > vec3d1.b
               && !this.b.onGround
               && MathHelper.floor(vec3d.a) == MathHelper.floor(vec3d1.a)
               && MathHelper.floor(vec3d.c) == MathHelper.floor(vec3d1.c)) {
               this.d.c(this.d.e() + 1);
            }
         }

         if (!this.m()) {
            Vec3D vec3d = this.d.a(this.b);
            if (vec3d != null) {
               AxisAlignedBB axisalignedbb = new AxisAlignedBB(vec3d.a, vec3d.b, vec3d.c, vec3d.a, vec3d.b, vec3d.c).grow(0.5, 0.5, 0.5);
               List<AxisAlignedBB> list = this.c.getCubes(this.b, axisalignedbb.a(0.0, -1.0, 0.0));
               double d0 = -1.0;
               axisalignedbb = axisalignedbb.c(0.0, 1.0, 0.0);

               for (AxisAlignedBB axisalignedbb1 : list) {
                  d0 = axisalignedbb1.b(axisalignedbb, d0);
               }

               this.b.getControllerMove().a(vec3d.a, vec3d.b + d0, vec3d.c, this.e);
            }
         }
      }
   }

   protected void l() {
      Vec3D vec3d = this.c();
      int i = this.d.d();

      for (int j = this.d.e(); j < this.d.d(); j++) {
         if (this.d.a(j).b != (int)vec3d.b) {
            i = j;
            break;
         }
      }

      float f = this.b.width * this.b.width * this.i;

      for (int k = this.d.e(); k < i; k++) {
         Vec3D vec3d1 = this.d.a(this.b, k);
         if (vec3d.distanceSquared(vec3d1) < f) {
            this.d.c(k + 1);
         }
      }

      int var9 = MathHelper.f(this.b.width);
      int l = (int)this.b.length + 1;
      int i1 = var9;

      for (int j1 = i - 1; j1 >= this.d.e(); j1--) {
         if (this.a(vec3d, this.d.a(this.b, j1), var9, l, i1)) {
            this.d.c(j1);
            break;
         }
      }

      this.a(vec3d);
   }

   protected void a(Vec3D vec3d) {
      if (this.f - this.g > 100) {
         if (vec3d.distanceSquared(this.h) < 2.25) {
            this.n();
         }

         this.g = this.f;
         this.h = vec3d;
      }
   }

   public boolean m() {
      return this.d == null || this.d.b();
   }

   public void n() {
      this.d = null;
   }

   protected abstract Vec3D c();

   protected abstract boolean b();

   protected boolean o() {
      return this.b.V() || this.b.ab();
   }

   protected void d() {
   }

   protected abstract boolean a(Vec3D var1, Vec3D var2, int var3, int var4, int var5);

   public EntityInsentient getEntity() {
      return this.b;
   }

   @Override
   public int hashCode() {
      return this.b.getUniqueID().hashCode();
   }

   public void cleanUpExpiredSearches() {
   }

   public void setSearchResult(PathSearchJobEntity pathSearch) {
   }

   public void setSearchResult(PathSearchJobPosition pathSearch) {
   }

   public boolean a(double d0, double d1, double d2, double d3, PositionPathSearchType type) {
      return this.a(d0, d1, d2, d3);
   }

   public PathEntity a(double d0, double d1, double d2, PositionPathSearchType type) {
      return this.a(d0, d1, d2);
   }

   public WeakChunkCache createChunkCache(boolean forEntitySearch) {
      if (this.b()) {
         float f = this.i();
         BlockPosition blockposition1 = new BlockPosition(this.b);
         int i = (int)(f + (forEntitySearch ? 16.0F : 8.0F));
         BlockPosition lower = blockposition1.a(-i, -i, -i);
         BlockPosition upper = blockposition1.a(i, i, i);
         return new WeakChunkCache(this.c, lower.getX(), lower.getZ(), upper.getX(), upper.getZ(), 0);
      } else {
         return null;
      }
   }

   public PathEntity doPathSearch(WeakChunkCache chunkCache, BlockPosition blockposition) {
      if (this.b()) {
         float f = this.i();
         return this.j.a(chunkCache, this.b, blockposition, f);
      } else {
         return null;
      }
   }

   public PathEntity doPathSearch(WeakChunkCache chunkCache, Entity entity) {
      return this.doPathSearch(chunkCache, new BlockPosition(entity).up());
   }
}
