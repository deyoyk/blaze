package lol.deyo.blaze.async.tasks;

import java.io.File;
import java.io.FileOutputStream;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class NBTFileSaveJob implements Runnable {
   private NBTTagCompound compound;
   private File file;

   public NBTFileSaveJob(NBTTagCompound compound, File file) {
      this.compound = compound;
      this.file = file;
   }

   @Override
   public void run() {
      try (FileOutputStream fileoutputstream = new FileOutputStream(this.file)) {
         NBTCompressedStreamTools.a(this.compound, fileoutputstream);
      } catch (Exception var12) {
         var12.printStackTrace();
      }

      this.compound = null;
      this.file = null;
   }}
