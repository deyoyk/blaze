package net.minecraft.server.v1_8_R3;

public abstract class PathfinderAbstract {
   protected IBlockAccess a;
   protected IntHashMap<PathPoint> b = new IntHashMap<>();
   protected int c;
   protected int d;
   protected int e;

   public void a(IBlockAccess iblockaccess, Entity entity) {
      this.a = iblockaccess;
      this.b.c();
      this.c = MathHelper.d(entity.width + 1.0F);
      this.d = MathHelper.d(entity.length + 1.0F);
      this.e = MathHelper.d(entity.width + 1.0F);
   }

   public void a() {
   }

   protected PathPoint a(int i, int j, int k) {
      int l = PathPoint.a(i, j, k);
      PathPoint pathpoint = this.b.get(l);
      if (pathpoint == null) {
         pathpoint = new PathPoint(i, j, k);
         this.b.a(l, pathpoint);
      }

      return pathpoint;
   }

   public abstract PathPoint a(Entity var1);

   public abstract PathPoint a(Entity var1, double var2, double var4, double var6);

   public abstract int a(PathPoint[] var1, Entity var2, PathPoint var3, PathPoint var4, float var5);
}
