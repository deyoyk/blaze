package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

public abstract class StructureGenerator extends WorldGenBase {
   private PersistentStructure d;
   protected Map<Long, StructureStart> e = Maps.newHashMap();

   public abstract String a();

   @Override
   protected final void a(World world, final int i, final int j, int k, int l, ChunkSnapshot chunksnapshot) {
      this.a(world);
      final long hash = ChunkCoordIntPair.a(i, j);
      if (!this.e.containsKey(hash)) {
         try {
            if (this.a(i, j)) {
               StructureStart structurestart = this.b(i, j);
               this.e.put(hash, structurestart);
               this.a(i, j, structurestart);
            }
         } catch (Throwable var12) {
            CrashReport crashreport = CrashReport.a(var12, "Exception preparing structure feature");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Feature being prepared");
            crashreportsystemdetails.a("Is feature chunk", new Callable() {
               public String a() {
                  return StructureGenerator.this.a(i, j) ? "True" : "False";
               }

               @Override
               public Object call() {
                  return this.a();
               }
            });
            crashreportsystemdetails.a("Chunk location", String.format("%d,%d", i, j));
            crashreportsystemdetails.a("Chunk pos hash", new Callable() {
               public String a() {
                  return String.valueOf(hash);
               }

               @Override
               public Object call() {
                  return this.a();
               }
            });
            crashreportsystemdetails.a("Structure type", new Callable() {
               public String a() {
                  return StructureGenerator.this.getClass().getCanonicalName();
               }

               @Override
               public Object call() {
                  return this.a();
               }
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public boolean a(World world, Random random, ChunkCoordIntPair chunkcoordintpair) {
      this.a(world);
      int i = (chunkcoordintpair.x << 4) + 8;
      int j = (chunkcoordintpair.z << 4) + 8;
      boolean flag = false;

      for (StructureStart structurestart : this.e.values()) {
         if (structurestart.d() && structurestart.a(chunkcoordintpair) && structurestart.a().a(i, j, i + 15, j + 15)) {
            structurestart.a(world, random, new StructureBoundingBox(i, j, i + 15, j + 15));
            structurestart.b(chunkcoordintpair);
            flag = true;
            this.a(structurestart.e(), structurestart.f(), structurestart);
         }
      }

      return flag;
   }

   public boolean b(BlockPosition blockposition) {
      if (this.c == null) {
         return false;
      } else {
         this.a(this.c);
         return this.c(blockposition) != null;
      }
   }

   protected StructureStart c(BlockPosition blockposition) {
      for (StructureStart structurestart : this.e.values()) {
         if (structurestart.d() && structurestart.a().b(blockposition)) {
            for (StructurePiece structurepiece : structurestart.b()) {
               if (structurepiece.c().b(blockposition)) {
                  return structurestart;
               }
            }
         }
      }

      return null;
   }

   public boolean a(World world, BlockPosition blockposition) {
      if (this.c == null) {
         return false;
      } else {
         this.a(world);

         for (StructureStart structurestart : this.e.values()) {
            if (structurestart.d() && structurestart.a().b(blockposition)) {
               return true;
            }
         }

         return false;
      }
   }

   public BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition) {
      this.c = world;
      this.a(world);
      this.b.setSeed(world.getSeed());
      long i = this.b.nextLong();
      long j = this.b.nextLong();
      long k = (blockposition.getX() >> 4) * i;
      long l = (blockposition.getZ() >> 4) * j;
      this.b.setSeed(k ^ l ^ world.getSeed());
      this.a(world, blockposition.getX() >> 4, blockposition.getZ() >> 4, 0, 0, null);
      double d0 = Double.MAX_VALUE;
      BlockPosition blockposition1 = null;

      for (StructureStart structurestart : this.e.values()) {
         if (structurestart.d()) {
            StructurePiece structurepiece = structurestart.b().get(0);
            BlockPosition blockposition2 = structurepiece.a();
            double d1 = blockposition2.i(blockposition);
            if (d1 < d0) {
               d0 = d1;
               blockposition1 = blockposition2;
            }
         }
      }

      if (blockposition1 != null) {
         return blockposition1;
      } else {
         List<BlockPosition> list = this.z_();
         if (list != null) {
            BlockPosition blockposition3 = null;

            for (BlockPosition blockPosition : list) {
               double d1 = blockPosition.i(blockposition);
               if (d1 < d0) {
                  d0 = d1;
                  blockposition3 = blockPosition;
               }
            }

            return blockposition3;
         } else {
            return null;
         }
      }
   }

   protected List<BlockPosition> z_() {
      return null;
   }

   private void a(World world) {
      if (this.d == null) {
         if (world.spigotConfig.saveStructureInfo && !this.a().equals("Mineshaft")) {
            this.d = (PersistentStructure)world.a(PersistentStructure.class, this.a());
         } else {
            this.d = new PersistentStructure(this.a());
         }

         if (this.d == null) {
            this.d = new PersistentStructure(this.a());
            world.a(this.a(), this.d);
         } else {
            NBTTagCompound nbttagcompound = this.d.a();

            for (String s : nbttagcompound.c()) {
               NBTBase nbtbase = nbttagcompound.get(s);
               if (nbtbase.getTypeId() == 10) {
                  NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbtbase;
                  if (nbttagcompound1.hasKey("ChunkX") && nbttagcompound1.hasKey("ChunkZ")) {
                     int i = nbttagcompound1.getInt("ChunkX");
                     int j = nbttagcompound1.getInt("ChunkZ");
                     StructureStart structurestart = WorldGenFactory.a(nbttagcompound1, world);
                     if (structurestart != null) {
                        this.e.put(ChunkCoordIntPair.a(i, j), structurestart);
                     }
                  }
               }
            }
         }
      }
   }

   private void a(int i, int j, StructureStart structurestart) {
      this.d.a(structurestart.a(i, j), i, j);
      this.d.c();
   }

   protected abstract boolean a(int var1, int var2);

   protected abstract StructureStart b(int var1, int var2);
}
