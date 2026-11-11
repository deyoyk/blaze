package net.minecraft.server.v1_8_R3;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public class BlockPosition extends BaseBlockPosition {
   public static final BlockPosition ZERO = new BlockPosition(0, 0, 0);
   private static final int c = 1 + MathHelper.c(MathHelper.b(30000000));
   private static final int d = c;
   private static final int e = 64 - c - d;
   private static final int f = d;
   private static final int g = f + e;
   private static final long h = (1L << c) - 1L;
   private static final long i = (1L << e) - 1L;
   private static final long j = (1L << d) - 1L;

   public BlockPosition(int i, int j, int k) {
      super(i, j, k);
   }

   public BlockPosition(double d0, double d1, double d2) {
      super(d0, d1, d2);
   }

   public BlockPosition(Entity entity) {
      this(entity.locX, entity.locY, entity.locZ);
   }

   public BlockPosition(Vec3D vec3d) {
      this(vec3d.a, vec3d.b, vec3d.c);
   }

   public BlockPosition(BaseBlockPosition baseblockposition) {
      this(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
   }

   public BlockPosition a(double d0, double d1, double d2) {
      return d0 == 0.0 && d1 == 0.0 && d2 == 0.0 ? this : new BlockPosition(this.getX() + d0, this.getY() + d1, this.getZ() + d2);
   }

   public BlockPosition a(int i, int j, int k) {
      return i == 0 && j == 0 && k == 0 ? this : new BlockPosition(this.getX() + i, this.getY() + j, this.getZ() + k);
   }

   public BlockPosition a(BaseBlockPosition baseblockposition) {
      return baseblockposition.getX() == 0 && baseblockposition.getY() == 0 && baseblockposition.getZ() == 0
         ? this
         : new BlockPosition(this.getX() + baseblockposition.getX(), this.getY() + baseblockposition.getY(), this.getZ() + baseblockposition.getZ());
   }

   public BlockPosition b(BaseBlockPosition baseblockposition) {
      return baseblockposition.getX() == 0 && baseblockposition.getY() == 0 && baseblockposition.getZ() == 0
         ? this
         : new BlockPosition(this.getX() - baseblockposition.getX(), this.getY() - baseblockposition.getY(), this.getZ() - baseblockposition.getZ());
   }

   public BlockPosition up() {
      return this.up(1);
   }

   public BlockPosition up(int i) {
      return i == 0 ? this : new BlockPosition(this.getX(), this.getY() + i, this.getZ());
   }

   public BlockPosition down() {
      return this.down(1);
   }

   public BlockPosition down(int i) {
      return i == 0 ? this : new BlockPosition(this.getX(), this.getY() - i, this.getZ());
   }

   public BlockPosition north() {
      return this.north(1);
   }

   public BlockPosition north(int i) {
      return i == 0 ? this : new BlockPosition(this.getX(), this.getY(), this.getZ() - i);
   }

   public BlockPosition south() {
      return this.south(1);
   }

   public BlockPosition south(int i) {
      return i == 0 ? this : new BlockPosition(this.getX(), this.getY(), this.getZ() + i);
   }

   public BlockPosition west() {
      return this.west(1);
   }

   public BlockPosition west(int i) {
      return i == 0 ? this : new BlockPosition(this.getX() - i, this.getY(), this.getZ());
   }

   public BlockPosition east() {
      return this.east(1);
   }

   public BlockPosition east(int i) {
      return i == 0 ? this : new BlockPosition(this.getX() + i, this.getY(), this.getZ());
   }

   public BlockPosition shift(EnumDirection enumdirection) {
      return this.shift(enumdirection, 1);
   }

   public BlockPosition shift(EnumDirection enumdirection, int i) {
      if (i == 0) {
         return this;
      } else {
         switch (enumdirection) {
            case DOWN:
               return new BlockPosition(this.getX(), this.getY() - i, this.getZ());
            case UP:
               return new BlockPosition(this.getX(), this.getY() + i, this.getZ());
            case NORTH:
               return new BlockPosition(this.getX(), this.getY(), this.getZ() - i);
            case SOUTH:
               return new BlockPosition(this.getX(), this.getY(), this.getZ() + i);
            case WEST:
               return new BlockPosition(this.getX() - i, this.getY(), this.getZ());
            case EAST:
               return new BlockPosition(this.getX() + i, this.getY(), this.getZ());
            default:
               return new BlockPosition(
                  this.getX() + enumdirection.getAdjacentX() * i,
                  this.getY() + enumdirection.getAdjacentY() * i,
                  this.getZ() + enumdirection.getAdjacentZ() * i
               );
         }
      }
   }

   public BlockPosition c(BaseBlockPosition baseblockposition) {
      return new BlockPosition(
         this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(),
         this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(),
         this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX()
      );
   }

   public long asLong() {
      return (this.getX() & h) << g | (this.getY() & i) << f | this.getZ() & j;
   }

   public static long asLong(int x, int y, int z) {
      return (x & h) << g | (y & i) << f | z & j;
   }

   public static BlockPosition fromLong(long i) {
      return new BlockPosition((int)(i << 64 - g - c >> 64 - c), (int)(i << 64 - f - e >> 64 - e), (int)(i << 64 - d >> 64 - d));
   }

   public static Iterable<BlockPosition> a(final BlockPosition blockposition, final BlockPosition blockposition1) {
      return new Iterable<BlockPosition>() {
         @NotNull
         @Override
         public Iterator<BlockPosition> iterator() {
            return new AbstractIterator<BlockPosition>() {
               private BlockPosition b = null;

               protected BlockPosition computeNext() {
                  if (this.b == null) {
                     this.b = blockposition;
                     return this.b;
                  } else if (this.b.equals(blockposition1)) {
                     return (BlockPosition)this.endOfData();
                  } else {
                     int i = this.b.getX();
                     int j = this.b.getY();
                     int k = this.b.getZ();
                     if (i < blockposition1.getX()) {
                        i++;
                     } else if (j < blockposition1.getY()) {
                        i = blockposition.getX();
                        j++;
                     } else if (k < blockposition1.getZ()) {
                        i = blockposition.getX();
                        j = blockposition.getY();
                        k++;
                     }

                     this.b = new BlockPosition(i, j, k);
                     return this.b;
                  }
               }
            };
         }
      };
   }

   public static Iterable<BlockPosition.MutableBlockPosition> b(final BlockPosition blockposition, final BlockPosition blockposition1) {
      return new Iterable<BlockPosition.MutableBlockPosition>() {
         @NotNull
         @Override
         public Iterator<BlockPosition.MutableBlockPosition> iterator() {
            return new AbstractIterator<BlockPosition.MutableBlockPosition>() {
               private BlockPosition.MutableBlockPosition b = null;

               protected BlockPosition.MutableBlockPosition computeNext() {
                  if (this.b == null) {
                     this.b = new BlockPosition.MutableBlockPosition(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                     return this.b;
                  } else if (this.b.equals(blockposition1)) {
                     return (BlockPosition.MutableBlockPosition)this.endOfData();
                  } else {
                     int i = this.b.getX();
                     int j = this.b.getY();
                     int k = this.b.getZ();
                     if (i < blockposition1.getX()) {
                        i++;
                     } else if (j < blockposition1.getY()) {
                        i = blockposition.getX();
                        j++;
                     } else if (k < blockposition1.getZ()) {
                        i = blockposition.getX();
                        j = blockposition.getY();
                        k++;
                     }

                     this.b.setX(i);
                     this.b.setY(j);
                     this.b.setZ(k);
                     return this.b;
                  }
               }
            };
         }
      };
   }

   @Override
   public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
      return this.c(baseblockposition);
   }

   public static final class MutableBlockPosition extends BlockPosition {
      public MutableBlockPosition() {
         this(0, 0, 0);
      }

      public MutableBlockPosition(int i, int j, int k) {
         super(0, 0, 0);
         this.setX(i);
         this.setY(j);
         this.setZ(k);
      }

      @Override
      public void setY(int y) {
         super.setY(y);
      }

      @Override
      public void setZ(int z) {
         super.setZ(z);
      }

      public BlockPosition.MutableBlockPosition setValues(int x, int y, int z) {
         return this.c(x, y, z);
      }

      public BlockPosition.MutableBlockPosition c(int i, int j, int k) {
         this.setX(i);
         this.setY(j);
         this.setZ(k);
         return this;
      }
   }
}
