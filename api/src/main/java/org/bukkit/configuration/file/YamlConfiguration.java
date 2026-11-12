package org.bukkit.configuration.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

public class YamlConfiguration extends FileConfiguration {
   protected static final String COMMENT_PREFIX = "# ";
   protected static final String BLANK_CONFIG = "{}\n";
   private final DumperOptions yamlOptions = new DumperOptions();
   private final Representer yamlRepresenter = new YamlRepresenter();
   private final Yaml yaml = new Yaml(new YamlConstructor(), this.yamlRepresenter, this.yamlOptions);

   @Override
   public String saveToString() {
      this.yamlOptions.setIndent(this.options().indent());
      this.yamlOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
      this.yamlOptions.setAllowUnicode(SYSTEM_UTF);
      this.yamlRepresenter.setDefaultFlowStyle(FlowStyle.BLOCK);
      String header = this.buildHeader();
      String dump = this.yaml.dump(this.getValues(false));
      if (dump.equals("{}\n")) {
         dump = "";
      }

      return header + dump;
   }

   @Override
   public void loadFromString(String contents) throws InvalidConfigurationException {
      Validate.notNull(contents, "Contents cannot be null");
      Validate.notNull(contents, "Contents cannot be null");

      Map<?, ?> input;
      try {
         input = (Map<?, ?>)this.yaml.load(contents);
      } catch (YAMLException var4) {
         throw new InvalidConfigurationException(var4);
      } catch (ClassCastException var5) {
         throw new InvalidConfigurationException("Top level is not a Map.");
      }

      String header = this.parseHeader(contents);
      if (header.length() > 0) {
         this.options().header(header);
      }

      if (input != null) {
         this.convertMapsToSections(input, this);
      }
   }

   protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
      for (Entry<?, ?> entry : input.entrySet()) {
         String key = entry.getKey().toString();
         Object value = entry.getValue();
         if (value instanceof Map) {
            this.convertMapsToSections((Map<?, ?>)value, section.createSection(key));
         } else {
            section.set(key, value);
         }
      }
   }

   protected String parseHeader(String input) {
      String[] lines = input.split("\r?\n", -1);
      StringBuilder result = new StringBuilder();
      boolean readingHeader = true;
      boolean foundHeader = false;

      for (int i = 0; i < lines.length && readingHeader; i++) {
         String line = lines[i];
         if (line.startsWith("# ")) {
            if (i > 0) {
               result.append("\n");
            }

            if (line.length() > "# ".length()) {
               result.append(line.substring("# ".length()));
            }

            foundHeader = true;
         } else if (foundHeader && line.length() == 0) {
            result.append("\n");
         } else if (foundHeader) {
            readingHeader = false;
         }
      }

      return result.toString();
   }

   @Override
   protected String buildHeader() {
      String header = this.options().header();
      if (this.options().copyHeader()) {
         Configuration def = this.getDefaults();
         if (def instanceof FileConfiguration) {
            FileConfiguration filedefaults = (FileConfiguration)def;
            String defaultsHeader = filedefaults.buildHeader();
            if (defaultsHeader != null && defaultsHeader.length() > 0) {
               return defaultsHeader;
            }
         }
      }

      if (header == null) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder();
         String[] lines = header.split("\r?\n", -1);
         boolean startedHeader = false;

         for (int i = lines.length - 1; i >= 0; i--) {
            builder.insert(0, "\n");
            if (startedHeader || lines[i].length() != 0) {
               builder.insert(0, lines[i]);
               builder.insert(0, "# ");
               startedHeader = true;
            }
         }

         return builder.toString();
      }
   }

   public YamlConfigurationOptions options() {
      if (this.options == null) {
         this.options = new YamlConfigurationOptions(this);
      }

      return (YamlConfigurationOptions)this.options;
   }

   public static YamlConfiguration loadConfiguration(File file) {
      Validate.notNull(file, "File cannot be null");
      YamlConfiguration config = new YamlConfiguration();

      try {
         config.load(file);
      } catch (FileNotFoundException var3) {
      } catch (InvalidConfigurationException | IOException var4) {
         if (org.bukkit.Bukkit.getServer() != null) {
            org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, (Throwable)var4);
         } else {
            var4.printStackTrace();
         }
      }

      return config;
   }

   @Deprecated
   public static YamlConfiguration loadConfiguration(InputStream stream) {
      Validate.notNull(stream, "Stream cannot be null");
      YamlConfiguration config = new YamlConfiguration();

      try {
         config.load(stream);
      } catch (InvalidConfigurationException | IOException var3) {
         org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", (Throwable)var3);
      }

      return config;
   }

   public static YamlConfiguration loadConfiguration(Reader reader) {
      Validate.notNull(reader, "Stream cannot be null");
      YamlConfiguration config = new YamlConfiguration();

      try {
         config.load(reader);
      } catch (InvalidConfigurationException | IOException var3) {
         org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", (Throwable)var3);
      }

      return config;
   }
}
