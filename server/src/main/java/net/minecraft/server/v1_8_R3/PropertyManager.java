package net.minecraft.server.v1_8_R3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyManager {
   private static final Logger a = LogManager.getLogger();
   public final Properties properties = new Properties();
   private final File file;
   private OptionSet options = null;

   public PropertyManager(File file) {
      this.file = file;
      if (file.exists()) {
         try (FileInputStream fileinputstream = new FileInputStream(file)) {
            this.properties.load(fileinputstream);
         } catch (Exception var13) {
            a.warn("Failed to load " + file, var13);
            this.a();
         }
      } else {
         a.warn(file + " does not exist");
         this.a();
      }
   }
   public PropertyManager(OptionSet options) {
      this((File)options.valueOf("config"));
      this.options = options;
   }

   private <T> T getOverride(String name, T value) {
      return (T)(this.options != null && this.options.has(name) && !name.equals("online-mode") ? this.options.valueOf(name) : value);
   }

   public void a() {
      a.info("Generating new properties file");
      this.savePropertiesFile();
   }

   public void savePropertiesFile() {
      FileOutputStream fileoutputstream = null;

      try {
         if (!this.file.exists() || this.file.canWrite()) {
            fileoutputstream = new FileOutputStream(this.file);
            this.properties.store(fileoutputstream, "Minecraft server properties");
            return;
         }
      } catch (Exception var12) {
         a.warn("Failed to save " + this.file, var12);
         this.a();
         return;
      } finally {
         if (fileoutputstream != null) {
            try {
               fileoutputstream.close();
            } catch (IOException var11) {
            }
         }
      }
   }

   public File c() {
      return this.file;
   }

   public String getString(String s, String s1) {
      if (!this.properties.containsKey(s)) {
         this.properties.setProperty(s, s1);
         this.savePropertiesFile();
         this.savePropertiesFile();
      }

      return this.getOverride(s, this.properties.getProperty(s, s1));
   }

   public int getInt(String s, int i) {
      try {
         return this.getOverride(s, Integer.parseInt(this.getString(s, "" + i)));
      } catch (Exception var4) {
         this.properties.setProperty(s, "" + i);
         this.savePropertiesFile();
         return this.getOverride(s, i);
      }
   }

   public long getLong(String s, long i) {
      try {
         return this.getOverride(s, Long.parseLong(this.getString(s, "" + i)));
      } catch (Exception var5) {
         this.properties.setProperty(s, "" + i);
         this.savePropertiesFile();
         return this.getOverride(s, i);
      }
   }

   public boolean getBoolean(String s, boolean flag) {
      try {
         return this.getOverride(s, Boolean.parseBoolean(this.getString(s, "" + flag)));
      } catch (Exception var4) {
         this.properties.setProperty(s, "" + flag);
         this.savePropertiesFile();
         return this.getOverride(s, flag);
      }
   }

   public void setProperty(String s, Object object) {
      this.properties.setProperty(s, "" + object);
   }
}
