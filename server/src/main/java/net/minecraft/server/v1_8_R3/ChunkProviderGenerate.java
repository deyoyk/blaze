package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.generator.Generator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChunkProviderGenerate implements IChunkProvider {
   private final Random h;
   private final NoiseGeneratorOctaves i;
   private final NoiseGeneratorOctaves j;
   private final NoiseGeneratorOctaves k;
   private final NoiseGenerator3 l;
   public NoiseGeneratorOctaves a;
   public NoiseGeneratorOctaves b;
   public NoiseGeneratorOctaves c;
   private final World m;
   private final boolean n;
   private final WorldType o;
   private final double[] p;
   private final float[] q;
   private CustomWorldSettingsFinal r;
   private Block s = Blocks.WATER;
   private double[] t = new double[256];
   private final WorldGenBase u = new WorldGenCaves();
   private final WorldGenStronghold v = new WorldGenStronghold();
   private final WorldGenVillage w = new WorldGenVillage();
   private final WorldGenMineshaft x = new WorldGenMineshaft();
   private final WorldGenLargeFeature y = new WorldGenLargeFeature();
   private final WorldGenBase z = new WorldGenCanyon();
   private final WorldGenMonument A = new WorldGenMonument();
   private BiomeBase[] B;
   double[] d;
   double[] e;
   double[] f;
   double[] g;
   private final Map<String, String> biomeReplacements;

   public ChunkProviderGenerate(World world, long i, boolean flag, String s) {
      this.m = world;
      this.n = flag;
      this.o = world.getWorldData().getType();
      this.h = new Random(i);
      this.i = new NoiseGeneratorOctaves(this.h, 16);
      this.j = new NoiseGeneratorOctaves(this.h, 16);
      this.k = new NoiseGeneratorOctaves(this.h, 8);
      this.l = new NoiseGenerator3(this.h, 4);
      this.a = new NoiseGeneratorOctaves(this.h, 10);
      this.b = new NoiseGeneratorOctaves(this.h, 16);
      this.c = new NoiseGeneratorOctaves(this.h, 8);
      this.p = new double[825];
      this.q = new float[25];

      for (int j = -2; j <= 2; j++) {
         for (int k = -2; k <= 2; k++) {
            this.q[j + 2 + (k + 2) * 5] = 10.0F / MathHelper.c(j * j + k * k + 0.2F);
         }
      }

      if (s != null) {
         this.r = CustomWorldSettingsFinal.CustomWorldSettings.a(s).b();
         this.s = this.r.E ? Blocks.LAVA : Blocks.WATER;
         world.b(this.r.q);
      }

      this.biomeReplacements = new HashMap<>();

      for (String string : (List<String>)world.generatorConfig.biomeReplacements.getValue()) {
         String[] split = string.split(";");
         String replace = split[1];
         if (!replace.equalsIgnoreCase("none")) {
            this.biomeReplacements.put(split[0], replace);
         }
      }
   }

   public void a(int i, int j, ChunkSnapshot chunksnapshot) {
      this.B = this.m.getWorldChunkManager().getBiomes(this.B, i * 4 - 2, j * 4 - 2, 10, 10);
      int radius = (Integer)this.m.generatorConfig.spawnBiomeRadius.getValue() >> 4;
      if (i < radius && j < radius && i > -radius && j > -radius) {
         Arrays.fill(this.B, BiomeBase.o.getOrDefault(this.m.generatorConfig.spawnBiome.getValue(), BiomeBase.PLAINS));
      }

      for (int index = 0; index < this.B.length; index++) {
         BiomeBase biome = this.B[index];
         String replacement = this.biomeReplacements.get(biome.ah);
         if (replacement != null) {
            this.B[index] = BiomeBase.o.get(replacement);
         }
      }

      this.a(i * 4, 0, j * 4);

      for (int k = 0; k < 4; k++) {
         int l = k * 5;
         int i1 = (k + 1) * 5;

         for (int j1 = 0; j1 < 4; j1++) {
            int k1 = (l + j1) * 33;
            int l1 = (l + j1 + 1) * 33;
            int i2 = (i1 + j1) * 33;
            int j2 = (i1 + j1 + 1) * 33;

            for (int k2 = 0; k2 < 32; k2++) {
               double d1 = this.p[k1 + k2];
               double d2 = this.p[l1 + k2];
               double d3 = this.p[i2 + k2];
               double d4 = this.p[j2 + k2];
               double d5 = (this.p[k1 + k2 + 1] - d1) * 0.125;
               double d6 = (this.p[l1 + k2 + 1] - d2) * 0.125;
               double d7 = (this.p[i2 + k2 + 1] - d3) * 0.125;
               double d8 = (this.p[j2 + k2 + 1] - d4) * 0.125;

               for (int l2 = 0; l2 < 8; l2++) {
                  double d10 = d1;
                  double d11 = d2;
                  double d12 = (d3 - d1) * 0.25;
                  double d13 = (d4 - d2) * 0.25;

                  for (int i3 = 0; i3 < 4; i3++) {
                     double d15 = (d11 - d10) * 0.25;
                     double d16 = d10 - d15;

                     for (int j3 = 0; j3 < 4; j3++) {
                        if ((d16 += d15) > 0.0) {
                           chunksnapshot.a(k * 4 + i3, k2 * 8 + l2, j1 * 4 + j3, Blocks.STONE.getBlockData());
                        } else if (k2 * 8 + l2 < this.r.q) {
                           chunksnapshot.a(k * 4 + i3, k2 * 8 + l2, j1 * 4 + j3, this.s.getBlockData());
                        }
                     }

                     d10 += d12;
                     d11 += d13;
                  }

                  d1 += d5;
                  d2 += d6;
                  d3 += d7;
                  d4 += d8;
               }
            }
         }
      }
   }

   public void a(int i, int j, ChunkSnapshot chunksnapshot, BiomeBase[] abiomebase) {
      this.t = this.l.a(this.t, i * 16, j * 16, 16, 16, 0.0625, 0.0625, 1.0);

      for (int k = 0; k < 16; k++) {
         for (int l = 0; l < 16; l++) {
            abiomebase[l + k * 16].a(this.m, this.h, chunksnapshot, i * 16 + k, j * 16 + l, this.t[l + k * 16]);
         }
      }
   }

   @Override
   public Chunk getOrCreateChunk(int i, int j) {
      this.h.setSeed(i * 341873128712L + j * 132897987541L);
      ChunkSnapshot chunksnapshot = new ChunkSnapshot();
      this.a(i, j, chunksnapshot);
      this.B = this.m.getWorldChunkManager().getBiomeBlock(this.B, i * 16, j * 16, 16, 16);
      this.a(i, j, chunksnapshot, this.B);
      if (this.r.r && this.m.paperSpigotConfig.generateCaves && (Double)this.m.generatorConfig.cavesMultiplier.getValue() > 0.0) {
         this.u.a(this, this.m, i, j, chunksnapshot);
      }

      if (this.r.z && this.m.paperSpigotConfig.generateCanyon) {
         this.z.a(this, this.m, i, j, chunksnapshot);
      }

      if (this.r.w && this.n && this.m.paperSpigotConfig.generateMineshaft) {
         this.x.a(this, this.m, i, j, chunksnapshot);
      }

      if (this.r.v && this.n && this.m.paperSpigotConfig.generateVillage) {
         this.w.a(this, this.m, i, j, chunksnapshot);
      }

      if (this.r.u && this.n && this.m.paperSpigotConfig.generateStronghold) {
         this.v.a(this, this.m, i, j, chunksnapshot);
      }

      if (this.r.x && this.n && this.m.paperSpigotConfig.generateTemple) {
         this.y.a(this, this.m, i, j, chunksnapshot);
      }

      if (this.r.y && this.n && this.m.paperSpigotConfig.generateMonument) {
         this.A.a(this, this.m, i, j, chunksnapshot);
      }

      Chunk chunk = new Chunk(this.m, chunksnapshot, i, j);
      byte[] abyte = chunk.getBiomeIndex();

      for (int k = 0; k < abyte.length; k++) {
         abyte[k] = (byte)this.B[k].id;
      }

      chunk.initLighting();
      return chunk;
   }

   private void a(int i, int j, int k) {
      this.g = this.b.a(this.g, i, k, 5, 5, this.r.e, this.r.f, this.r.g);
      float f = this.r.a;
      float f1 = this.r.b;
      this.d = this.k.a(this.d, i, j, k, 5, 33, 5, f / this.r.h, f1 / this.r.i, f / this.r.j);
      this.e = this.i.a(this.e, i, j, k, 5, 33, 5, f, f1, f);
      this.f = this.j.a(this.f, i, j, k, 5, 33, 5, f, f1, f);
      int l = 0;
      int i1 = 0;

      for (int j1 = 0; j1 < 5; j1++) {
         for (int k1 = 0; k1 < 5; k1++) {
            float f2 = 0.0F;
            float f3 = 0.0F;
            float f4 = 0.0F;
            byte b0 = 2;
            BiomeBase biomebase = this.B[j1 + 2 + (k1 + 2) * 10];

            for (int l1 = -b0; l1 <= b0; l1++) {
               for (int i2 = -b0; i2 <= b0; i2++) {
                  BiomeBase biomebase1 = this.B[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
                  float f5 = this.r.n + biomebase1.an * this.r.m;
                  float f6 = this.r.p + biomebase1.ao * this.r.o;
                  if (this.o == WorldType.AMPLIFIED && f5 > 0.0F) {
                     f5 = 1.0F + f5 * 2.0F;
                     f6 = 1.0F + f6 * 4.0F;
                  }

                  float f7 = this.q[l1 + 2 + (i2 + 2) * 5] / (f5 + 2.0F);
                  if (biomebase1.an > biomebase.an) {
                     f7 /= 2.0F;
                  }

                  f2 += f6 * f7;
                  f3 += f5 * f7;
                  f4 += f7;
               }
            }

            f2 /= f4;
            f3 /= f4;
            f2 = f2 * 0.9F + 0.1F;
            f3 = (f3 * 4.0F - 1.0F) / 8.0F;
            double d0 = this.g[i1] / 8000.0;
            if (d0 < 0.0) {
               d0 = -d0 * 0.3;
            }

            d0 = d0 * 3.0 - 2.0;
            if (d0 < 0.0) {
               d0 /= 2.0;
               if (d0 < -1.0) {
                  d0 = -1.0;
               }

               d0 /= 1.4;
               d0 /= 2.0;
            } else {
               if (d0 > 1.0) {
                  d0 = 1.0;
               }

               d0 /= 8.0;
            }

            i1++;
            double d1 = f3;
            double d2 = f2;
            d1 += d0 * 0.2;
            d1 = d1 * this.r.k / 8.0;
            double d3 = this.r.k + d1 * 4.0;

            for (int j2 = 0; j2 < 33; j2++) {
               double d4 = (j2 - d3) * this.r.l * 128.0 / 256.0 / d2;
               if (d4 < 0.0) {
                  d4 *= 4.0;
               }

               double d5 = this.e[l] / this.r.d;
               double d6 = this.f[l] / this.r.c;
               double d7 = (this.d[l] / 10.0 + 1.0) / 2.0;
               double d8 = MathHelper.b(d5, d6, d7) - d4;
               if (j2 > 29) {
                  double d9 = (j2 - 29) / 3.0F;
                  d8 = d8 * (1.0 - d9) + -10.0 * d9;
               }

               this.p[l] = d8;
               l++;
            }
         }
      }
   }

   @Override
   public boolean isChunkLoaded(int i, int j) {
      return true;
   }

   @Override
   public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
      BlockFalling.instaFall = true;
      int k = i * 16;
      int l = j * 16;
      Generator gen = this.m.generatorConfig;
      int radius = (Integer)gen.spawnBiomeRadius.getValue() >> 4;
      boolean inSpawn = i < radius && j < radius && i > -radius && j > -radius;
      BlockPosition blockposition = new BlockPosition(k, 0, l);
      BiomeBase biomebase = inSpawn ? BiomeBase.o.getOrDefault(gen.spawnBiome.getValue(), BiomeBase.PLAINS) : this.m.getBiome(k + 16, 0, l + 16);
      if (!inSpawn) {
         String replace = this.biomeReplacements.get(biomebase.ah);
         if (replace != null) {
            biomebase = BiomeBase.o.getOrDefault(replace, biomebase);
         }
      }

      this.h.setSeed(this.m.getSeed());
      long i1 = this.h.nextLong() / 2L * 2L + 1L;
      long j1 = this.h.nextLong() / 2L * 2L + 1L;
      this.h.setSeed(i * i1 + j * j1 ^ this.m.getSeed());
      boolean flag = false;
      ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
      if (this.r.w && this.n && this.m.paperSpigotConfig.generateMineshaft) {
         this.x.a(this.m, this.h, chunkcoordintpair);
      }

      if (this.r.v && this.n && this.m.paperSpigotConfig.generateVillage) {
         flag = this.w.a(this.m, this.h, chunkcoordintpair);
      }

      if (this.r.u && this.n && this.m.paperSpigotConfig.generateStronghold) {
         this.v.a(this.m, this.h, chunkcoordintpair);
      }

      if (this.r.x && this.n && this.m.paperSpigotConfig.generateTemple) {
         this.y.a(this.m, this.h, chunkcoordintpair);
      }

      if (this.r.y && this.n && this.m.paperSpigotConfig.generateMonument) {
         this.A.a(this.m, this.h, chunkcoordintpair);
      }

      if (!inSpawn || (Boolean)gen.spawnBiomeLakes.getValue()) {
         if ((Boolean)gen.generateWaterLakes.getValue()
            && biomebase != BiomeBase.DESERT
            && biomebase != BiomeBase.DESERT_HILLS
            && this.r.A
            && !flag
            && this.h.nextInt(this.r.B) == 0) {
            int k1 = this.h.nextInt(16) + 8;
            int l1 = this.h.nextInt(256);
            int i2 = this.h.nextInt(16) + 8;
            new WorldGenLakes(Blocks.WATER).generate(this.m, this.h, blockposition.a(k1, l1, i2));
         }

         if ((Boolean)gen.generateLavaLakes.getValue() && !flag && this.h.nextInt(this.r.D / 10) == 0 && this.r.C) {
            int k1 = this.h.nextInt(16) + 8;
            int l1 = this.h.nextInt(this.h.nextInt(248) + 8);
            int i2 = this.h.nextInt(16) + 8;
            if (l1 < this.m.F() || this.h.nextInt(this.r.D / 8) == 0) {
               new WorldGenLakes(Blocks.LAVA).generate(this.m, this.h, blockposition.a(k1, l1, i2));
            }
         }
      }

      if (this.r.s && this.m.paperSpigotConfig.generateDungeon) {
         for (int k1 = 0; k1 < this.r.t; k1++) {
            int l1 = this.h.nextInt(16) + 8;
            int i2 = this.h.nextInt(256);
            int j2 = this.h.nextInt(16) + 8;
            new WorldGenDungeons().generate(this.m, this.h, blockposition.a(l1, i2, j2));
         }
      }

      biomebase.a(this.m, this.h, new BlockPosition(k, 0, l));
      SpawnerCreature.a(this.m, biomebase, k + 8, l + 8, 16, 16, this.h);
      blockposition = blockposition.a(8, 0, 8);

      for (int k1 = 0; k1 < 16; k1++) {
         for (int l1 = 0; l1 < 16; l1++) {
            BlockPosition blockposition1 = this.m.q(blockposition.a(k1, 0, l1));
            BlockPosition blockposition2 = blockposition1.down();
            if (this.m.v(blockposition2)) {
               this.m.setTypeAndData(blockposition2, Blocks.ICE.getBlockData(), 2);
            }

            if (this.m.f(blockposition1, true)) {
               this.m.setTypeAndData(blockposition1, Blocks.SNOW_LAYER.getBlockData(), 2);
            }
         }
      }

      BlockFalling.instaFall = false;
   }

   @Override
   public boolean a(IChunkProvider ichunkprovider, Chunk chunk, int i, int j) {
      boolean flag = false;
      if (this.r.y && this.n && chunk.w() < 3600L) {
         flag = this.A.a(this.m, this.h, new ChunkCoordIntPair(i, j));
      }

      return flag;
   }

   @Override
   public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
      return true;
   }

   @Override
   public void c() {
   }

   @Override
   public boolean unloadChunks() {
      return false;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public String getName() {
      return "RandomLevelSource";
   }

   @Override
   public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
      BiomeBase biomebase = this.m.getBiome(blockposition);
      if (this.n) {
         if (enumcreaturetype == EnumCreatureType.MONSTER && this.y.a(blockposition)) {
            return this.y.b();
         }

         if (enumcreaturetype == EnumCreatureType.MONSTER && this.r.y && this.A.a(this.m, blockposition)) {
            return this.A.b();
         }
      }

      return biomebase.getMobs(enumcreaturetype);
   }

   @Override
   public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition) {
      return "Stronghold".equals(s) && this.v != null ? this.v.getNearestGeneratedFeature(world, blockposition) : null;
   }

   @Override
   public int getLoadedChunks() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk chunk, int i, int j) {
      if (this.r.w && this.n && this.m.paperSpigotConfig.generateMineshaft) {
         this.x.a(this, this.m, i, j, null);
      }

      if (this.r.v && this.n && this.m.paperSpigotConfig.generateVillage) {
         this.w.a(this, this.m, i, j, null);
      }

      if (this.r.u && this.n && this.m.paperSpigotConfig.generateStronghold) {
         this.v.a(this, this.m, i, j, null);
      }

      if (this.r.x && this.n && this.m.paperSpigotConfig.generateTemple) {
         this.y.a(this, this.m, i, j, null);
      }

      if (this.r.y && this.n && this.m.paperSpigotConfig.generateMonument) {
         this.A.a(this, this.m, i, j, null);
      }
   }

   @Override
   public Chunk getChunkAt(BlockPosition blockposition) {
      return this.getOrCreateChunk(blockposition.getX() >> 4, blockposition.getZ() >> 4);
   }
}
