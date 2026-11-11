package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.jafama.FastMath;

public class WorldGenStronghold extends StructureGenerator {
   private final List<BiomeBase> d;
   private boolean f;
   private ChunkCoordIntPair[] g = new ChunkCoordIntPair[3];
   private double h = 32.0;
   private int i = 3;

   public WorldGenStronghold() {
      this.d = Lists.newArrayList();

      BiomeBase[] var4;
      for (BiomeBase biomebase : var4 = BiomeBase.getBiomes()) {
         if (biomebase != null && biomebase.an > 0.0F) {
            this.d.add(biomebase);
         }
      }
   }

   public WorldGenStronghold(Map<String, String> map) {
      this();

      for (Entry<String, String> entry : map.entrySet()) {
         String var4;
         switch ((var4 = entry.getKey()).hashCode()) {
            case -895684237:
               if (var4.equals("spread")) {
                  this.i = MathHelper.a(entry.getValue(), this.i, 1);
               }
               break;
            case 94851343:
               if (var4.equals("count")) {
                  this.g = new ChunkCoordIntPair[MathHelper.a(entry.getValue(), this.g.length, 1)];
               }
               break;
            case 288459765:
               if (var4.equals("distance")) {
                  this.h = MathHelper.a(entry.getValue(), this.h, 1.0);
               }
         }
      }
   }

   @Override
   public String a() {
      return "Stronghold";
   }

   @Override
   protected boolean a(int i, int j) {
      if (!this.f) {
         Random random = new Random();
         random.setSeed(this.c.getSeed());
         double d0 = random.nextDouble() * Math.PI * 2.0;
         int k = 1;

         for (int l = 0; l < this.g.length; l++) {
            double d1 = (1.25 * k + random.nextDouble()) * this.h * k;
            int i1 = (int)FastMath.round(FastMath.cos(d0) * d1);
            int j1 = (int)FastMath.round(FastMath.sin(d0) * d1);
            BlockPosition blockposition = this.c.getWorldChunkManager().a((i1 << 4) + 8, (j1 << 4) + 8, 112, this.d, random);
            if (blockposition != null) {
               i1 = blockposition.getX() >> 4;
               j1 = blockposition.getZ() >> 4;
            }

            this.g[l] = new ChunkCoordIntPair(i1, j1);
            d0 += (Math.PI * 2) * k / this.i;
            if (l == this.i) {
               k += 2 + random.nextInt(5);
               this.i = this.i + 1 + random.nextInt(2);
            }
         }

         this.f = true;
      }

      for (ChunkCoordIntPair chunkcoordintpair : this.g) {
         if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected List<BlockPosition> z_() {
      ArrayList<BlockPosition> arraylist = Lists.newArrayList();

      for (ChunkCoordIntPair chunkcoordintpair : this.g) {
         if (chunkcoordintpair != null) {
            arraylist.add(chunkcoordintpair.a(64));
         }
      }

      return arraylist;
   }

   @Override
   protected StructureStart b(int i, int j) {
      WorldGenStronghold.WorldGenStronghold2Start worldgenstronghold_worldgenstronghold2start = new WorldGenStronghold.WorldGenStronghold2Start(
         this.c, this.b, i, j
      );

      while (
         worldgenstronghold_worldgenstronghold2start.b().isEmpty()
            || ((WorldGenStrongholdPieces.WorldGenStrongholdStart)worldgenstronghold_worldgenstronghold2start.b().get(0)).b == null
      ) {
         worldgenstronghold_worldgenstronghold2start = new WorldGenStronghold.WorldGenStronghold2Start(this.c, this.b, i, j);
      }

      return worldgenstronghold_worldgenstronghold2start;
   }

   public static class WorldGenStronghold2Start extends StructureStart {
      public WorldGenStronghold2Start() {
      }

      public WorldGenStronghold2Start(World world, Random random, int i, int j) {
         super(i, j);
         WorldGenStrongholdPieces.b();
         WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart = new WorldGenStrongholdPieces.WorldGenStrongholdStart(
            0, random, (i << 4) + 2, (j << 4) + 2
         );
         this.a.add(worldgenstrongholdpieces_worldgenstrongholdstart);
         worldgenstrongholdpieces_worldgenstrongholdstart.a(worldgenstrongholdpieces_worldgenstrongholdstart, this.a, random);
         List<StructurePiece> list = worldgenstrongholdpieces_worldgenstrongholdstart.c;

         while (!list.isEmpty()) {
            int k = random.nextInt(list.size());
            StructurePiece structurepiece = list.remove(k);
            structurepiece.a(worldgenstrongholdpieces_worldgenstrongholdstart, this.a, random);
         }

         this.c();
         this.a(world, random, 10);
      }
   }
}
