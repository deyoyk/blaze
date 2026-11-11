package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RegionFileCache {
   public static final Map<File, RegionFile> a = Maps.newHashMap();

   public static RegionFile a(File file, int i, int j) {
      return a(file, i, j, true);
   }

   public static RegionFile a(File file, int i, int j, boolean create) {
      File file1 = new File(file, "region");
      File file2 = new File(file1, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
      RegionFile regionfile = a.get(file2);
      if (regionfile != null) {
         return regionfile;
      } else if (!create && !file2.exists()) {
         return null;
      } else {
         if (!file1.exists()) {
            file1.mkdirs();
         }

         if (a.size() >= 256) {
            a();
         }

         RegionFile regionfile1 = new RegionFile(file2);
         a.put(file2, regionfile1);
         return regionfile1;
      }
   }

   public static void a() {
      for (RegionFile regionfile : a.values()) {
         try {
            if (regionfile != null) {
               regionfile.c();
            }
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

      a.clear();
   }

   public static DataInputStream c(File file, int i, int j) {
      return a(file, i, j).a(i & 31, j & 31);
   }

   public static DataOutputStream d(File file, int i, int j) {
      return a(file, i, j).b(i & 31, j & 31);
   }
}
