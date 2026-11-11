package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChunkProviderFlat implements IChunkProvider {
   private final World a;
   private final Random b;
   private final IBlockData[] c = new IBlockData[256];
   private final WorldGenFlatInfo d;
   private final List<StructureGenerator> e = Lists.newArrayList();
   private final boolean f;
   private final boolean g;
   private WorldGenLakes h;
   private WorldGenLakes i;

   public ChunkProviderFlat(World world, long i, boolean flag, String s) {
      this.a = world;
      this.b = new Random(i);
      this.d = WorldGenFlatInfo.a(s);
      if (flag) {
         Map<String, Map<String, String>> map = this.d.b();
         if (map.containsKey("village") && world.paperSpigotConfig.generateVillage) {
            Map<String, String> map1 = map.get("village");
            if (!map1.containsKey("size")) {
               map1.put("size", "1");
            }

            this.e.add(new WorldGenVillage(map1));
         }

         if (map.containsKey("biome_1") && world.paperSpigotConfig.generateTemple) {
            this.e.add(new WorldGenLargeFeature(map.get("biome_1")));
         }

         if (map.containsKey("mineshaft") && world.paperSpigotConfig.generateMineshaft) {
            this.e.add(new WorldGenMineshaft(map.get("mineshaft")));
         }

         if (map.containsKey("stronghold") && world.paperSpigotConfig.generateStronghold) {
            this.e.add(new WorldGenStronghold(map.get("stronghold")));
         }

         if (map.containsKey("oceanmonument") && world.paperSpigotConfig.generateMonument) {
            this.e.add(new WorldGenMonument(map.get("oceanmonument")));
         }
      }

      if (this.d.b().containsKey("lake")) {
         this.h = new WorldGenLakes(Blocks.WATER);
      }

      if (this.d.b().containsKey("lava_lake")) {
         this.i = new WorldGenLakes(Blocks.LAVA);
      }

      this.g = world.paperSpigotConfig.generateDungeon && this.d.b().containsKey("dungeon");
      int j = 0;
      int k = 0;
      boolean flag1 = true;

      for (WorldGenFlatLayerInfo worldgenflatlayerinfo : this.d.c()) {
         for (int l = worldgenflatlayerinfo.d(); l < worldgenflatlayerinfo.d() + worldgenflatlayerinfo.b(); l++) {
            IBlockData iblockdata = worldgenflatlayerinfo.c();
            if (iblockdata.getBlock() != Blocks.AIR) {
               flag1 = false;
               this.c[l] = iblockdata;
            }
         }

         if (worldgenflatlayerinfo.c().getBlock() == Blocks.AIR) {
            k += worldgenflatlayerinfo.b();
         } else {
            j += worldgenflatlayerinfo.b() + k;
            k = 0;
         }
      }

      world.b(j);
      this.f = !flag1 && this.d.b().containsKey("decoration");
   }

   @Override
   public Chunk getOrCreateChunk(int i, int j) {
      ChunkSnapshot chunksnapshot = new ChunkSnapshot();

      for (int l = 0; l < this.c.length; l++) {
         IBlockData iblockdata = this.c[l];
         if (iblockdata != null) {
            for (int i1 = 0; i1 < 16; i1++) {
               for (int k = 0; k < 16; k++) {
                  chunksnapshot.a(i1, l, k, iblockdata);
               }
            }
         }
      }

      for (StructureGenerator structureGenerator : this.e) {
         structureGenerator.a(this, this.a, i, j, chunksnapshot);
      }

      Chunk chunk = new Chunk(this.a, chunksnapshot, i, j);
      BiomeBase[] abiomebase = this.a.getWorldChunkManager().getBiomeBlock(null, i * 16, j * 16, 16, 16);
      byte[] abyte = chunk.getBiomeIndex();

      for (int k = 0; k < abyte.length; k++) {
         abyte[k] = (byte)abiomebase[k].id;
      }

      chunk.initLighting();
      return chunk;
   }

   @Override
   public boolean isChunkLoaded(int i, int j) {
      return true;
   }

   @Override
   public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
      int k = i * 16;
      int l = j * 16;
      BlockPosition blockposition = new BlockPosition(k, 0, l);
      BiomeBase biomebase = this.a.getBiome(k + 16, 0, l + 16);
      boolean flag = false;
      this.b.setSeed(this.a.getSeed());
      long i1 = this.b.nextLong() / 2L * 2L + 1L;
      long j1 = this.b.nextLong() / 2L * 2L + 1L;
      this.b.setSeed(i * i1 + j * j1 ^ this.a.getSeed());
      ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);

      for (StructureGenerator structuregenerator : this.e) {
         boolean flag1 = structuregenerator.a(this.a, this.b, chunkcoordintpair);
         if (structuregenerator instanceof WorldGenVillage) {
            flag |= flag1;
         }
      }

      if (this.h != null && !flag && this.b.nextInt(4) == 0) {
         this.h.generate(this.a, this.b, blockposition.a(this.b.nextInt(16) + 8, this.b.nextInt(256), this.b.nextInt(16) + 8));
      }

      if (this.i != null && !flag && this.b.nextInt(8) == 0) {
         BlockPosition blockposition1 = blockposition.a(this.b.nextInt(16) + 8, this.b.nextInt(this.b.nextInt(248) + 8), this.b.nextInt(16) + 8);
         if (blockposition1.getY() < this.a.F() || this.b.nextInt(10) == 0) {
            this.i.generate(this.a, this.b, blockposition1);
         }
      }

      if (this.g) {
         for (int k1 = 0; k1 < 8; k1++) {
            new WorldGenDungeons().generate(this.a, this.b, blockposition.a(this.b.nextInt(16) + 8, this.b.nextInt(256), this.b.nextInt(16) + 8));
         }
      }

      if (this.f) {
         biomebase.a(this.a, this.b, blockposition);
      }
   }

   @Override
   public boolean a(IChunkProvider ichunkprovider, Chunk chunk, int i, int j) {
      return false;
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
      return "FlatLevelSource";
   }

   @Override
   public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
      return this.a.getBiome(blockposition).getMobs(enumcreaturetype);
   }

   @Override
   public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition) {
      if ("Stronghold".equals(s)) {
         for (StructureGenerator structuregenerator : this.e) {
            if (structuregenerator instanceof WorldGenStronghold) {
               return structuregenerator.getNearestGeneratedFeature(world, blockposition);
            }
         }
      }

      return null;
   }

   @Override
   public int getLoadedChunks() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk chunk, int i, int j) {
      for (StructureGenerator structuregenerator : this.e) {
         structuregenerator.a(this, this.a, i, j, null);
      }
   }

   @Override
   public Chunk getChunkAt(BlockPosition blockposition) {
      return this.getOrCreateChunk(blockposition.getX() >> 4, blockposition.getZ() >> 4);
   }
}
