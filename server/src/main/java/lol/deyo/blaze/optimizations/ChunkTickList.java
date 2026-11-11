package lol.deyo.blaze.optimizations;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.Arrays;
import net.minecraft.server.v1_8_R3.Chunk;

public final class ChunkTickList {
   private final Long2IntOpenHashMap objectToIndex;
   private static final Chunk[] EMPTY_LIST = new Chunk[0];
   private Chunk[] elements = EMPTY_LIST;
   private int count;

   public ChunkTickList() {
      this(1024, 0.8F);
   }

   public ChunkTickList(int expectedSize, float loadFactor) {
      this.objectToIndex = new Long2IntOpenHashMap(expectedSize, loadFactor);
      this.objectToIndex.defaultReturnValue(Integer.MIN_VALUE);
   }

   public int size() {
      return this.count;
   }

   public Chunk get(int index) {
      return this.elements[index];
   }

   public boolean remove(Chunk chunk) {
      return this.remove(chunk.chunkHashKey);
   }

   public boolean remove(long hash) {
      int index = this.objectToIndex.remove(hash);
      if (index == Integer.MIN_VALUE) {
         return false;
      } else {
         int endIndex = --this.count;
         Chunk end = this.elements[endIndex];
         if (index != endIndex) {
            this.objectToIndex.put(end.chunkHashKey, index);
         }

         this.elements[index] = end;
         this.elements[endIndex] = null;
         return true;
      }
   }

   public boolean add(Chunk chunk) {
      int count = this.count;
      int currIndex = this.objectToIndex.putIfAbsent(chunk.chunkHashKey, count);
      if (currIndex != Integer.MIN_VALUE) {
         return false;
      } else {
         Chunk[] list = this.elements;
         if (list.length == count) {
            list = this.elements = Arrays.copyOf(list, (int)Math.max(4L, (long)count << 1));
         }

         list[count] = chunk;
         this.count = count + 1;
         return true;
      }
   }

   public boolean contains(Chunk chunk) {
      return this.contains(chunk.chunkHashKey);
   }

   public boolean contains(long hashKey) {
      return this.objectToIndex.containsKey(hashKey);
   }

   public void clear() {
      this.objectToIndex.clear();
      Arrays.fill(this.elements, 0, this.count, null);
      this.count = 0;
   }
}
