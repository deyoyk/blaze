package net.minecraft.server.v1_8_R3;

import org.jetbrains.annotations.NotNull;

public class NextTickListEntry implements Comparable<NextTickListEntry> {
   private static long d;
   private final Block e;
   public final BlockPosition a;
   public long b;
   public int c;
   private final long f;

   public NextTickListEntry(BlockPosition blockposition, Block block) {
      if (blockposition instanceof BlockPosition.MutableBlockPosition) {
         throw new IllegalArgumentException("Next Tick list entry should not have mutable block position.");
      } else {
         this.f = d++;
         this.a = blockposition;
         this.e = block;
      }
   }

   public NextTickListEntry a(long i) {
      this.b = i;
      return this;
   }

   public void a(int i) {
      this.c = i;
   }

   public int a(NextTickListEntry nextticklistentry) {
      return this.b < nextticklistentry.b
         ? -1
         : (this.b > nextticklistentry.b ? 1 : (this.c != nextticklistentry.c ? this.c - nextticklistentry.c : Long.compare(this.f, nextticklistentry.f)));
   }

   public Block a() {
      return this.e;
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry nextticklistentry = (NextTickListEntry)object;
         return this.a.equals(nextticklistentry.a) && Block.a(this.e, nextticklistentry.e);
      }
   }

   @Override
   public String toString() {
      return Block.getId(this.e) + ": " + this.a + ", " + this.b + ", " + this.c + ", " + this.f;
   }

   @Override
   public int hashCode() {
      return this.a.hashCode();
   }

   public int compareTo(@NotNull NextTickListEntry object) {
      return this.a(object);
   }
}
