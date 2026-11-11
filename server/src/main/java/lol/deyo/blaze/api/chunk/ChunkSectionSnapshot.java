package lol.deyo.blaze.api.chunk;

import net.minecraft.server.v1_8_R3.NibbleArray;

public class ChunkSectionSnapshot {
   private final int yPos;
   private final int nonEmptyBlockCount;
   private final int tickingBlockCount;
   private final char[] blockIds;
   private final NibbleArray emittedLight;
   private final NibbleArray skyLight;
   boolean isDirty;

   public ChunkSectionSnapshot(
      int yPos, int nonEmptyBlockCount, int tickingBlockCount, char[] blockIds, NibbleArray emittedLight, NibbleArray skyLight, boolean isDirty
   ) {
      this.yPos = yPos;
      this.nonEmptyBlockCount = nonEmptyBlockCount;
      this.tickingBlockCount = tickingBlockCount;
      this.blockIds = blockIds;
      this.emittedLight = emittedLight;
      this.skyLight = skyLight;
      this.isDirty = isDirty;
   }

   public int getYPos() {
      return this.yPos;
   }

   public int getNonEmptyBlockCount() {
      return this.nonEmptyBlockCount;
   }

   public int getTickingBlockCount() {
      return this.tickingBlockCount;
   }

   public char[] getBlockIds() {
      return this.blockIds;
   }

   public NibbleArray getEmittedLight() {
      return this.emittedLight;
   }

   public NibbleArray getSkyLight() {
      return this.skyLight;
   }

   public boolean isDirty() {
      return this.isDirty;
   }
}
