package net.minecraft.server.v1_8_R3;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldLoader implements Convertable {
   private static final Logger b = LogManager.getLogger();
   protected final File a;

   public WorldLoader(File file) {
      if (!file.exists()) {
         file.mkdirs();
      }

      this.a = file;
   }

   @Override
   public void d() {
   }

   public WorldData c(String s) {
      File file = new File(this.a, s);
      if (file.exists()) {
         File file1 = new File(file, "level.dat");
         if (file1.exists()) {
            try {
               NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(Files.newInputStream(file1.toPath()));
               NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
               return new WorldData(nbttagcompound1);
            } catch (Exception var8) {
               b.error("Exception reading " + file1, var8);
            }
         }

         file1 = new File(file, "level.dat_old");
         if (file1.exists()) {
            try {
               NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(Files.newInputStream(file1.toPath()));
               NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
               return new WorldData(nbttagcompound1);
            } catch (Exception var7) {
               b.error("Exception reading " + file1, var7);
            }
         }
      }

      return null;
   }

   @Override
   public boolean e(String s) {
      File file = new File(this.a, s);
      if (!file.exists()) {
         return true;
      } else {
         b.info("Deleting level " + s);

         for (int i = 1; i <= 5; i++) {
            b.info("Attempt " + i + "...");
            if (a(Objects.requireNonNull(file.listFiles()))) {
               break;
            }

            b.warn("Unsuccessful in deleting contents.");
            if (i < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
               }
            }
         }

         return file.delete();
      }
   }

   protected static boolean a(File[] afile) {
      for (File file : afile) {
         b.debug("Deleting " + file);
         if (file.isDirectory() && !a(Objects.requireNonNull(file.listFiles()))) {
            b.warn("Couldn't delete directory " + file);
            return false;
         }

         if (!file.delete()) {
            b.warn("Couldn't delete file " + file);
            return false;
         }
      }

      return true;
   }

   @Override
   public IDataManager a(String s, boolean flag) {
      return new WorldNBTStorage(this.a, s, flag);
   }

   @Override
   public boolean isConvertable(String s) {
      return false;
   }

   @Override
   public boolean convert(String s, IProgressUpdate iprogressupdate) {
      return false;
   }
}
