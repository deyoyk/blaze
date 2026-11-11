package net.minecraft.server.v1_8_R3;

import java.io.File;

public class ServerNBTManager extends WorldNBTStorage {
   public ServerNBTManager(File var1, String var2, boolean var3) {
      super(var1, var2, var3);
   }

   @Override
   public IChunkLoader createChunkLoader(WorldProvider var1) {
      File var2 = this.getDirectory();
      if (var1 instanceof WorldProviderHell) {
         File var4 = new File(var2, "DIM-1");
         var4.mkdirs();
         return new ChunkRegionLoader(var4);
      } else if (var1 instanceof WorldProviderTheEnd) {
         File var3 = new File(var2, "DIM1");
         var3.mkdirs();
         return new ChunkRegionLoader(var3);
      } else {
         return new ChunkRegionLoader(var2);
      }
   }

   @Override
   public void saveWorldData(WorldData var1, NBTTagCompound var2) {
      var1.e(19133);
      super.saveWorldData(var1, var2);
   }

   @Override
   public void a() {
      try {
         FileIOThread.a().b();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }

      RegionFileCache.a();
   }
}
