package com.grinderwolf.swm.clsm;

public class ClassModifier {
   private static CLSMBridge customLoader;

   public static Object[] getDefaultWorlds() {
      return customLoader != null ? customLoader.getDefaultWorlds() : null;
   }

   public static boolean saveChunk(Object world, Object chunkAccess) {
      return customLoader != null && customLoader.saveChunk(world, chunkAccess);
   }

   public static boolean isCustomWorld(Object world) {
      return customLoader != null && customLoader.isCustomWorld(world);
   }

   public static boolean skipWorldAdd(Object world) {
      return customLoader != null && customLoader.skipWorldAdd(world);
   }

   public static void setLoader(CLSMBridge loader) {
      customLoader = loader;
   }
}
