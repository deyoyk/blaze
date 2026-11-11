package org.bukkit.configuration;

import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.Validate;

public class MemoryConfiguration extends MemorySection implements Configuration {
   protected Configuration defaults;
   protected MemoryConfigurationOptions options;

   public MemoryConfiguration() {
   }

   public MemoryConfiguration(Configuration defaults) {
      this.defaults = defaults;
   }

   @Override
   public void addDefault(String path, Object value) {
      Validate.notNull(path, "Path may not be null");
      if (this.defaults == null) {
         this.defaults = new MemoryConfiguration();
      }

      this.defaults.set(path, value);
   }

   @Override
   public void addDefaults(Map<String, Object> defaults) {
      Validate.notNull(defaults, "Defaults may not be null");

      for (Entry<String, Object> entry : defaults.entrySet()) {
         this.addDefault(entry.getKey(), entry.getValue());
      }
   }

   @Override
   public void addDefaults(Configuration defaults) {
      Validate.notNull(defaults, "Defaults may not be null");
      this.addDefaults(defaults.getValues(true));
   }

   @Override
   public void setDefaults(Configuration defaults) {
      Validate.notNull(defaults, "Defaults may not be null");
      this.defaults = defaults;
   }

   @Override
   public Configuration getDefaults() {
      return this.defaults;
   }

   @Override
   public ConfigurationSection getParent() {
      return null;
   }

   public MemoryConfigurationOptions options() {
      if (this.options == null) {
         this.options = new MemoryConfigurationOptions(this);
      }

      return this.options;
   }
}
