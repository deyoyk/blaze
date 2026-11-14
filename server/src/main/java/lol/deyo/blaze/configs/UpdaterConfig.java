package lol.deyo.blaze.configs;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;

public class UpdaterConfig extends Config {
   public static ConfigSetting<Boolean> enabled = new ConfigSetting<>(
      true, "updater", "Check for newer GitHub releases on startup and install them on shutdown"
   );
   public static ConfigSetting<Boolean> keepBackup = new ConfigSetting<>(
      true, "updater", "Keep a copy of the replaced server jar in blaze/updater/backup"
   );
   public static ConfigSetting<Boolean> restart = new ConfigSetting<>(
      true, "updater", "Restart the server automatically so an update is applied in a single restart"
   );

   public UpdaterConfig(Blaze blaze) {
      super(blaze, "updater", CC.getConfigHeader("Automatic updates", "Checks GitHub on startup and installs newer releases after shutdown"));
   }
}
