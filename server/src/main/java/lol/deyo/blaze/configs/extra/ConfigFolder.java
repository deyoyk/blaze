package lol.deyo.blaze.configs.extra;

import java.io.File;
import org.jetbrains.annotations.NotNull;

public class ConfigFolder extends File {
   public static final File BLAZE_DIRECTORY = new File("blaze");

   static {
      if (!BLAZE_DIRECTORY.exists()) {
         BLAZE_DIRECTORY.mkdir();
      }
   }

   public ConfigFolder(@NotNull String pathname) {
      super(BLAZE_DIRECTORY, pathname);
      if (!this.exists()) {
         this.mkdir();
      }
   }
}
