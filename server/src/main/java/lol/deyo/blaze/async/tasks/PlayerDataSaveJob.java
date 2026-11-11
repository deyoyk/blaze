package lol.deyo.blaze.async.tasks;

import java.io.File;
import java.io.FileOutputStream;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class PlayerDataSaveJob implements Runnable {
   private File target;
   private NBTTagCompound data;

   public PlayerDataSaveJob(File target, NBTTagCompound data) {
      this.target = target;
      this.data = data;
   }

   @Override
   public void run() {
      File temp = new File(this.target.getPath() + ".tmp");

      try (FileOutputStream fileoutputstream = new FileOutputStream(temp)) {
         NBTCompressedStreamTools.a(this.data, fileoutputstream);
         if (this.target.exists()) {
            this.target.delete();
         }

         temp.renameTo(this.target);
      } catch (Exception var13) {
         var13.printStackTrace();
      }

      this.target = null;
      this.data = null;
   }}
