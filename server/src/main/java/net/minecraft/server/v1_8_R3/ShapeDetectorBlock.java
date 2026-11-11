package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;

public class ShapeDetectorBlock {
   private final World a;
   private final BlockPosition b;
   private final boolean c;
   private IBlockData d;
   private TileEntity e;
   private boolean f;

   public ShapeDetectorBlock(World var1, BlockPosition var2, boolean var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   public IBlockData a() {
      if (this.d == null && (this.c || this.a.isLoaded(this.b))) {
         this.d = this.a.getType(this.b);
      }

      return this.d;
   }

   public TileEntity b() {
      if (this.e == null && !this.f) {
         this.e = this.a.getTileEntity(this.b);
         this.f = true;
      }

      return this.e;
   }

   public BlockPosition getPosition() {
      return this.b;
   }

   public static Predicate<ShapeDetectorBlock> a(final Predicate<IBlockData> var0) {
      return new Predicate<ShapeDetectorBlock>() {
         @Override
         public boolean apply(ShapeDetectorBlock var1) {
            return var1 != null && var0.apply(var1.a());
         }
      };
   }
}
