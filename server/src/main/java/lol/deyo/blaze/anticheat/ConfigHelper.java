package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.configs.extra.ConfigFolder;

public final class ConfigHelper {
   private ConfigHelper() {
   }

   public static String anticheatDirectory() {
      return new ConfigFolder("anticheat").getPath();
   }
}
