package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.api.chunk.ChunkSectionSnapshot;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.optimizations.SectionTickList;

public class ChunkSection {
   boolean isDirty;
   private int yPos;
   private int nonEmptyBlockCount;
   private int tickingBlockCount;
   private final ChunkSectionStorage blockStorage;
   private int[] blockDurabilities;
   private NibbleArray emittedLight;
   private NibbleArray skyLight;
   private final SectionTickList tickList;

   public ChunkSection(int i, boolean flag) {
      this.yPos = i;
      this.blockStorage = new ChunkSectionStorage();
      if ((Boolean)BlazeConfig.explosionDestroyer.getValue()) {
         this.blockDurabilities = new int[4096];
      }

      this.emittedLight = new NibbleArray();
      if (flag) {
         this.skyLight = new NibbleArray();
      }

      this.tickList = new SectionTickList();
   }

   public ChunkSection(int y, boolean flag, char[] blockIds) {
      this.yPos = y;
      this.blockStorage = new ChunkSectionStorage(blockIds);
      if ((Boolean)BlazeConfig.explosionDestroyer.getValue()) {
         this.blockDurabilities = new int[4096];
      }

      this.emittedLight = new NibbleArray();
      if (flag) {
         this.skyLight = new NibbleArray();
      }

      this.tickList = new SectionTickList();
      this.recalcBlockCounts();
   }

   public char getBlockId(int id) {
      return this.blockStorage.get(id);
   }

   public IBlockData getType(int x, int y, int z) {
      IBlockData iblockdata = Block.d.a(this.blockStorage.get(y << 8 | z << 4 | x));
      return iblockdata != null ? iblockdata : Blocks.AIR.getBlockData();
   }

   public IBlockData getType(int id) {
      IBlockData iblockdata = Block.d.a(this.blockStorage.get(id));
      return iblockdata != null ? iblockdata : Blocks.AIR.getBlockData();
   }

   public void setType(int x, int y, int z, IBlockData iblockdata) {
      IBlockData iblockdata1 = this.getType(x, y, z);
      Block block = iblockdata1.getBlock();
      Block block1 = iblockdata.getBlock();
      int id = y << 8 | z << 4 | x;
      if (block != Blocks.AIR) {
         this.nonEmptyBlockCount--;
         if (block.isTicking()) {
            this.tickingBlockCount--;
            this.tickList.remove((short)id);
         }

         if (block.explosionDurability > 0) {
            this.blockDurabilities[id] = 0;
         }
      }

      if (block1 != Blocks.AIR) {
         this.nonEmptyBlockCount++;
         if (block1.isTicking()) {
            this.tickingBlockCount++;
            this.tickList.add((short)id);
         }

         if (block1.explosionDurability > 0) {
            this.blockDurabilities[id] = block1.explosionDurability;
         }
      }

      this.blockStorage.set(id, (char)Block.d.b(iblockdata));
      this.isDirty = true;
   }

   public void setBlockDurability(int i, int j, int k, int durability) {
      this.blockDurabilities[j << 8 | k << 4 | i] = durability;
   }

   public int getBlockDurability(int i, int j, int k) {
      int id = j << 8 | k << 4 | i;
      int durability = this.blockDurabilities[id];
      if (durability == 0) {
         int explosionDurability = this.getType(i, j, k).getBlock().explosionDurability;
         if (explosionDurability > 0) {
            this.blockDurabilities[id] = explosionDurability;
            return explosionDurability;
         } else {
            return -1;
         }
      } else {
         return durability;
      }
   }

   public Block b(int i, int j, int k) {
      return this.getType(i, j, k).getBlock();
   }

   public int c(int i, int j, int k) {
      IBlockData iblockdata = this.getType(i, j, k);
      return iblockdata.getBlock().toLegacyData(iblockdata);
   }

   public boolean a() {
      return this.nonEmptyBlockCount == 0;
   }

   public boolean shouldTick() {
      return this.tickingBlockCount > 0;
   }

   public int getTickingBlockCount() {
      return this.tickingBlockCount;
   }

   public int getYPosition() {
      return this.yPos;
   }

   public void a(int i, int j, int k, int l) {
      this.skyLight.a(i, j, k, l);
      this.isDirty = true;
   }

   public int d(int i, int j, int k) {
      return this.skyLight.a(i, j, k);
   }

   public void b(int i, int j, int k, int l) {
      this.emittedLight.a(i, j, k, l);
      this.isDirty = true;
   }

   public int e(int i, int j, int k) {
      return this.emittedLight.a(i, j, k);
   }

   public void setBlockCounts(int nonEmptyBlockCount, int tickingBlockCount) {
      this.nonEmptyBlockCount = nonEmptyBlockCount;
      this.tickingBlockCount = tickingBlockCount;
   }

   public void recalcBlockCounts() {
      this.nonEmptyBlockCount = 0;
      this.tickingBlockCount = 0;
      this.tickList.clear();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
               int loc = j << 8 | k << 4 | i;
               Block block = this.getType(loc).getBlock();
               if (block != Blocks.AIR) {
                  this.nonEmptyBlockCount++;
                  if (block.isTicking()) {
                     this.tickingBlockCount++;
                     this.tickList.add((short)loc);
                  }

                  if (block.explosionDurability > 0) {
                     this.blockDurabilities[loc] = block.explosionDurability;
                  }
               }
            }
         }
      }
   }

   public char[] getIdArray() {
      return this.blockStorage.materialize();
   }

   public void a(char[] achar) {
      this.blockStorage.setValues(achar);
   }

   public NibbleArray getEmittedLightArray() {
      return this.emittedLight;
   }

   public NibbleArray getSkyLightArray() {
      return this.skyLight;
   }

   public void a(NibbleArray nibblearray) {
      this.emittedLight = nibblearray;
   }

   public void b(NibbleArray nibblearray) {
      this.skyLight = nibblearray;
   }

   private NibbleArray clone(NibbleArray array) {
      return array != null ? array.clone() : null;
   }

   public ChunkSectionSnapshot createSnapshot() {
      return new ChunkSectionSnapshot(
         this.yPos,
         this.nonEmptyBlockCount,
         this.tickingBlockCount,
         this.blockStorage.copy(),
         this.clone(this.emittedLight),
         this.clone(this.skyLight),
         this.isDirty
      );
   }

   public void restoreSnapshot(ChunkSectionSnapshot snap) {
      this.yPos = snap.getYPos();
      this.nonEmptyBlockCount = snap.getNonEmptyBlockCount();
      this.tickingBlockCount = snap.getTickingBlockCount();
      this.blockStorage.setValues((char[])snap.getBlockIds().clone());
      this.emittedLight = this.clone(snap.getEmittedLight());
      this.skyLight = this.clone(snap.getSkyLight());
      this.isDirty = snap.isDirty();
   }

   public SectionTickList getTickList() {
      return this.tickList;
   }
}
