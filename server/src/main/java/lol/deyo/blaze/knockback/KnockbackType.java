package lol.deyo.blaze.knockback;

import lol.deyo.blaze.configs.extra.ConfigSetting;

public enum KnockbackType {
   SIMPLE("Simple", "knockback-simple"),
   ADVANCED("Advanced", "knockback-advanced");

   private final String displayName;
   private final String path;

   private KnockbackType(String displayName, String path) {
      this.displayName = displayName;
      this.path = path;
   }

   public boolean isApplicable(ConfigSetting<?> setting) {
      return setting.getPath().isEmpty() || setting.getPath().equals(this.path);
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getPath() {
      return this.path;
   }
}
