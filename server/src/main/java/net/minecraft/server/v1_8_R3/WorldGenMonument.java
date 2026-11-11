package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class WorldGenMonument extends StructureGenerator {
   private int f = 32;
   private int g = 5;
   public static final List<BiomeBase> d = Arrays.asList(BiomeBase.OCEAN, BiomeBase.DEEP_OCEAN, BiomeBase.RIVER, BiomeBase.FROZEN_OCEAN, BiomeBase.FROZEN_RIVER);
   private static final List<BiomeBase.BiomeMeta> h = Lists.newArrayList();

   static {
      h.add(new BiomeBase.BiomeMeta(EntityGuardian.class, 1, 2, 4));
   }

   public WorldGenMonument() {
   }

   public WorldGenMonument(Map<String, String> map) {
      this();

      for (Entry<String, String> entry : map.entrySet()) {
         if (entry.getKey().equals("spacing")) {
            this.f = MathHelper.a(entry.getValue(), this.f, 1);
         } else if (entry.getKey().equals("separation")) {
            this.g = MathHelper.a(entry.getValue(), this.g, 1);
         }
      }
   }

   @Override
   public String a() {
      return "Monument";
   }

   @Override
   protected boolean a(int i, int j) {
      int k = i;
      int l = j;
      if (i < 0) {
         i -= this.f - 1;
      }

      if (j < 0) {
         j -= this.f - 1;
      }

      int i1 = i / this.f;
      int j1 = j / this.f;
      Random random = this.c.a(i1, j1, 10387313);
      i1 *= this.f;
      j1 *= this.f;
      i1 += (random.nextInt(this.f - this.g) + random.nextInt(this.f - this.g)) / 2;
      j1 += (random.nextInt(this.f - this.g) + random.nextInt(this.f - this.g)) / 2;
      if (k == i1 && l == j1) {
         return this.c.getWorldChunkManager().getBiome(k * 16 + 8, l * 16 + 8) != BiomeBase.DEEP_OCEAN
            ? false
            : this.c.getWorldChunkManager().a(k * 16 + 8, l * 16 + 8, 29, d);
      } else {
         return false;
      }
   }

   @Override
   protected StructureStart b(int i, int j) {
      return new WorldGenMonument.WorldGenMonumentStart(this.c, this.b, i, j);
   }

   public List<BiomeBase.BiomeMeta> b() {
      return h;
   }

   public static class WorldGenMonumentStart extends StructureStart {
      private final Set<ChunkCoordIntPair> c = Sets.newHashSet();
      private boolean d;

      public WorldGenMonumentStart() {
      }

      public WorldGenMonumentStart(World world, Random random, int i, int j) {
         super(i, j);
         this.b(world, random, i, j);
      }

      private void b(World world, Random random, int i, int j) {
         random.setSeed(world.getSeed());
         long k = random.nextLong();
         long l = random.nextLong();
         long i1 = i * k;
         long j1 = j * l;
         random.setSeed(i1 ^ j1 ^ world.getSeed());
         int k1 = i * 16 + 8 - 29;
         int l1 = j * 16 + 8 - 29;
         EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
         this.a.add(new WorldGenMonumentPieces.WorldGenMonumentPiece1(random, k1, l1, enumdirection));
         this.c();
         this.d = true;
      }

      @Override
      public void a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (!this.d) {
            this.a.clear();
            this.b(world, random, this.e(), this.f());
         }

         super.a(world, random, structureboundingbox);
      }

      @Override
      public boolean a(ChunkCoordIntPair chunkcoordintpair) {
         return !this.c.contains(chunkcoordintpair) && super.a(chunkcoordintpair);
      }

      @Override
      public void b(ChunkCoordIntPair chunkcoordintpair) {
         super.b(chunkcoordintpair);
         this.c.add(chunkcoordintpair);
      }

      @Override
      public void a(NBTTagCompound nbttagcompound) {
         super.a(nbttagcompound);
         NBTTagList nbttaglist = new NBTTagList();

         for (ChunkCoordIntPair chunkcoordintpair : this.c) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setInt("X", chunkcoordintpair.x);
            nbttagcompound1.setInt("Z", chunkcoordintpair.z);
            nbttaglist.add(nbttagcompound1);
         }

         nbttagcompound.set("Processed", nbttaglist);
      }

      @Override
      public void b(NBTTagCompound nbttagcompound) {
         super.b(nbttagcompound);
         if (nbttagcompound.hasKeyOfType("Processed", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Processed", 10);

            for (int i = 0; i < nbttaglist.size(); i++) {
               NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
               this.c.add(new ChunkCoordIntPair(nbttagcompound1.getInt("X"), nbttagcompound1.getInt("Z")));
            }
         }
      }
   }
}
