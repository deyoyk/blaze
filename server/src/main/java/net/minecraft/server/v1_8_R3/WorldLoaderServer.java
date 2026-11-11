package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.jafama.FastMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldLoaderServer extends WorldLoader {
   private static final Logger b = LogManager.getLogger();

   public WorldLoaderServer(File file) {
      super(file);
   }

   protected int c() {
      return 19133;
   }

   @Override
   public void d() {
      RegionFileCache.a();
   }

   @Override
   public IDataManager a(String s, boolean flag) {
      return new ServerNBTManager(this.a, s, flag);
   }

   @Override
   public boolean isConvertable(String s) {
      WorldData worlddata = this.c(s);
      return worlddata != null && worlddata.l() != this.c();
   }

   @Override
   public boolean convert(String s, IProgressUpdate iprogressupdate) {
      iprogressupdate.a(0);
      ArrayList<File> arraylist = Lists.newArrayList();
      ArrayList<File> arraylist1 = Lists.newArrayList();
      ArrayList<File> arraylist2 = Lists.newArrayList();
      File file = new File(this.a, s);
      File file1 = new File(file, "DIM-1");
      File file2 = new File(file, "DIM1");
      b.info("Scanning folders...");
      this.a(file, arraylist);
      if (file1.exists()) {
         this.a(file1, arraylist1);
      }

      if (file2.exists()) {
         this.a(file2, arraylist2);
      }

      int i = arraylist.size() + arraylist1.size() + arraylist2.size();
      b.info("Total conversion count is {}", new Object[]{i});
      WorldData worlddata = this.c(s);
      WorldChunkManager chunkManager;
      if (worlddata.getType() == WorldType.FLAT) {
         chunkManager = new WorldChunkManagerHell(BiomeBase.PLAINS, 0.5F);
      } else {
         chunkManager = new WorldChunkManager(worlddata.getSeed(), worlddata.getType(), worlddata.getGeneratorOptions(), MinecraftServer.getServer().getWorld());
      }

      this.a(new File(file, "region"), arraylist, chunkManager, 0, i, iprogressupdate);
      this.a(new File(file1, "region"), arraylist1, new WorldChunkManagerHell(BiomeBase.HELL, 0.0F), arraylist.size(), i, iprogressupdate);
      this.a(new File(file2, "region"), arraylist2, new WorldChunkManagerHell(BiomeBase.SKY, 0.0F), arraylist.size() + arraylist1.size(), i, iprogressupdate);
      worlddata.e(19133);
      if (worlddata.getType() == WorldType.NORMAL_1_1) {
         worlddata.a(WorldType.NORMAL);
      }

      this.g(s);
      IDataManager idatamanager = this.a(s, false);
      idatamanager.saveWorldData(worlddata);
      return true;
   }

   private void g(String s) {
      File file = new File(this.a, s);
      if (!file.exists()) {
         b.warn("Unable to create level.dat_mcr backup");
      } else {
         File file1 = new File(file, "level.dat");
         if (!file1.exists()) {
            b.warn("Unable to create level.dat_mcr backup");
         } else {
            File file2 = new File(file, "level.dat_mcr");
            if (!file1.renameTo(file2)) {
               b.warn("Unable to create level.dat_mcr backup");
            }
         }
      }
   }

   private void a(File file, Iterable<File> iterable, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
      for (File file1 : iterable) {
         this.a(file, file1, worldchunkmanager, i, j, iprogressupdate);
         i++;
         int k = (int)FastMath.round(100.0 * i / j);
         iprogressupdate.a(k);
      }
   }

   private void a(File file, File file1, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
      try {
         String s = file1.getName();
         RegionFile regionfile = new RegionFile(file1);
         RegionFile regionfile1 = new RegionFile(new File(file, s.substring(0, s.length() - ".mcr".length()) + ".mca"));

         for (int k = 0; k < 32; k++) {
            for (int l = 0; l < 32; l++) {
               if (regionfile.c(k, l) && !regionfile1.c(k, l)) {
                  DataInputStream datainputstream = regionfile.a(k, l);
                  if (datainputstream == null) {
                     b.warn("Failed to fetch input stream");
                  } else {
                     NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(datainputstream);
                     datainputstream.close();
                     NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
                     OldChunkLoader.OldChunk oldchunkloader_oldchunk = OldChunkLoader.a(nbttagcompound1);
                     NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                     NBTTagCompound nbttagcompound3 = new NBTTagCompound();
                     nbttagcompound2.set("Level", nbttagcompound3);
                     OldChunkLoader.a(oldchunkloader_oldchunk, nbttagcompound3, worldchunkmanager);
                     DataOutputStream dataoutputstream = regionfile1.b(k, l);
                     NBTCompressedStreamTools.a(nbttagcompound2, (DataOutput)dataoutputstream);
                     dataoutputstream.close();
                  }
               }
            }

            int var20 = (int)FastMath.round(100.0 * (i * 1024) / (j * 1024));
            int i1 = (int)FastMath.round(100.0 * ((k + 1) * 32 + i * 1024) / (j * 1024));
            if (i1 > var20) {
               iprogressupdate.a(i1);
            }
         }

         regionfile.c();
         regionfile1.c();
      } catch (IOException var19) {
         var19.printStackTrace();
      }
   }

   private void a(File file, Collection<File> collection) {
      File file1 = new File(file, "region");
      File[] afile = file1.listFiles((file2, s) -> s.endsWith(".mcr"));
      if (afile != null) {
         Collections.addAll(collection, afile);
      }
   }
}
