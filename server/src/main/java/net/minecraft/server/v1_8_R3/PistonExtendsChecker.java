package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import lol.deyo.blaze.configs.BlazeConfig;
import java.util.ArrayList;
import java.util.List;

public class PistonExtendsChecker {
   private final World a;
   private final BlockPosition b;
   private final BlockPosition c;
   private final EnumDirection d;
   private final List<BlockPosition> e = Lists.newArrayList();
   private final List<BlockPosition> f = Lists.newArrayList();

   public PistonExtendsChecker(World paramWorld, BlockPosition paramBlockPosition, EnumDirection paramEnumDirection, boolean paramBoolean) {
      this.a = paramWorld;
      this.b = paramBlockPosition;
      if (paramBoolean) {
         this.d = paramEnumDirection;
         this.c = paramBlockPosition.shift(paramEnumDirection);
      } else {
         this.d = paramEnumDirection.opposite();
         this.c = paramBlockPosition.shift(paramEnumDirection, 2);
      }
   }

   public boolean a() {
      this.e.clear();
      this.f.clear();
      Block block = this.a.getType(this.c).getBlock();
      if (!BlockPiston.a(block, this.a, this.c, this.d, false)) {
         if (block.k() != 1) {
            return false;
         } else {
            this.f.add(this.c);
            return true;
         }
      } else if (!this.a(this.c)) {
         return false;
      } else {
         for (byte b = 0; b < this.e.size(); b++) {
            BlockPosition blockPosition = this.e.get(b);
            if (this.a.getType(blockPosition).getBlock() == Blocks.SLIME && !this.b(blockPosition)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean a(BlockPosition paramBlockPosition) {
      Block block = this.a.getType(paramBlockPosition).getBlock();
      if (block.getMaterial() != Material.AIR
         && BlockPiston.a(block, this.a, paramBlockPosition, this.d, false)
         && !paramBlockPosition.equals(this.b)
         && !this.e.contains(paramBlockPosition)) {
         byte b1 = 1;
         if (b1 + this.e.size() > (Integer)BlazeConfig.pistonMaxBlockPush.getValue()) {
            return false;
         } else {
            while (block == Blocks.SLIME) {
               BlockPosition blockPosition = paramBlockPosition.shift(this.d.opposite(), b1);
               block = this.a.getType(blockPosition).getBlock();
               if (block.getMaterial() == Material.AIR || !BlockPiston.a(block, this.a, blockPosition, this.d, false) || blockPosition.equals(this.b)) {
                  break;
               }

               if (++b1 + this.e.size() > (Integer)BlazeConfig.pistonMaxBlockPush.getValue()) {
                  return false;
               }
            }

            byte b2 = 0;

            for (int i = b1 - 1; i >= 0; i--) {
               this.e.add(paramBlockPosition.shift(this.d.opposite(), i));
               b2++;
            }

            int var12 = 1;

            while (true) {
               BlockPosition blockPositionx = paramBlockPosition.shift(this.d, var12);
               int j = this.e.indexOf(blockPositionx);
               if (j > -1) {
                  this.a(b2, j);

                  for (byte b = 0; b <= j + b2; b++) {
                     BlockPosition blockPosition1 = this.e.get(b);
                     if (this.a.getType(blockPosition1).getBlock() == Blocks.SLIME && !this.b(blockPosition1)) {
                        return false;
                     }
                  }

                  return true;
               }

               block = this.a.getType(blockPositionx).getBlock();
               if (block.getMaterial() == Material.AIR) {
                  return true;
               }

               if (!BlockPiston.a(block, this.a, blockPositionx, this.d, true) || blockPositionx.equals(this.b)) {
                  return false;
               }

               if (block.k() == 1) {
                  this.f.add(blockPositionx);
                  return true;
               }

               if (this.e.size() >= (Integer)BlazeConfig.pistonMaxBlockPush.getValue()) {
                  return false;
               }

               this.e.add(blockPositionx);
               b2++;
               var12++;
            }
         }
      } else {
         return true;
      }
   }

   private void a(int paramInt1, int paramInt2) {
      ArrayList<BlockPosition> arrayList1 = Lists.newArrayList();
      ArrayList<BlockPosition> arrayList2 = Lists.newArrayList();
      ArrayList<BlockPosition> arrayList3 = Lists.newArrayList();
      arrayList1.addAll(this.e.subList(0, paramInt2));
      arrayList2.addAll(this.e.subList(this.e.size() - paramInt1, this.e.size()));
      arrayList3.addAll(this.e.subList(paramInt2, this.e.size() - paramInt1));
      this.e.clear();
      this.e.addAll(arrayList1);
      this.e.addAll(arrayList2);
      this.e.addAll(arrayList3);
   }

   private boolean b(BlockPosition paramBlockPosition) {
      EnumDirection[] var5;
      for (EnumDirection enumDirection : var5 = EnumDirection.values()) {
         if (enumDirection.k() != this.d.k() && !this.a(paramBlockPosition.shift(enumDirection))) {
            return false;
         }
      }

      return true;
   }

   public List<BlockPosition> getMovedBlocks() {
      return this.e;
   }

   public List<BlockPosition> getBrokenBlocks() {
      return this.f;
   }
}
