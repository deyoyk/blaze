package lol.deyo.blaze.async.tasks;

import java.io.File;
import java.nio.file.Files;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.WorldData;

public class WorldDataSaveJob implements Runnable {
   private WorldData target;
   private NBTTagCompound data;
   private File dir;

   public WorldDataSaveJob(WorldData target, NBTTagCompound data, File dir) {
      this.target = target;
      this.data = data;
      this.dir = dir;
   }

   @Override
   public void run() {
      NBTTagCompound nbttagcompound1 = this.target.a(this.data);
      NBTTagCompound nbttagcompound2 = new NBTTagCompound();
      nbttagcompound2.set("Data", nbttagcompound1);

      try {
         File file1 = new File(this.dir, "level.dat_new");
         File file2 = new File(this.dir, "level.dat_old");
         File file3 = new File(this.dir, "level.dat");
         NBTCompressedStreamTools.a(nbttagcompound2, Files.newOutputStream(file1.toPath()));
         if (file2.exists()) {
            file2.delete();
         }

         file3.renameTo(file2);
         if (file3.exists()) {
            file3.delete();
         }

         file1.renameTo(file3);
         if (file1.exists()) {
            file1.delete();
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      this.target = null;
      this.data = null;
      this.dir = null;
   }
}
