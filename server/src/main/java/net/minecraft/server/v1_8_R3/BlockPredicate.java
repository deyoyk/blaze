package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;

public class BlockPredicate implements Predicate<IBlockData> {
   private final Block a;

   private BlockPredicate(Block var1) {
      this.a = var1;
   }

   public static BlockPredicate a(Block var0) {
      return new BlockPredicate(var0);
   }

   @Override
   public boolean apply(IBlockData var1) {
      return var1 != null && var1.getBlock() == this.a;
   }
}
