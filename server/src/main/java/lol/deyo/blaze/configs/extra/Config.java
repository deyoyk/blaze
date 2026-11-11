package lol.deyo.blaze.configs.extra;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.PearlConfig;
import lol.deyo.blaze.knockback.Knockback;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
   private final List<ConfigSetting<?>> configSettings = new ArrayList<>();
   private final File file;
   private final YamlConfiguration bukkitConfig;
   private final ConfigCommenter configCommenter;

   public Config(Blaze blaze, String name, List<String> header) {
      this.file = new File(ConfigFolder.BLAZE_DIRECTORY, name + (name.endsWith(".yml") ? "" : ".yml"));
      this.bukkitConfig = YamlConfiguration.loadConfiguration(this.file);
      this.configCommenter = new ConfigCommenter();
      this.configCommenter.setHeader(String.join("\n", header));
      blaze.getConfigs().add(this);
   }

   public String getName() {
      return this.file.getName();
   }

   public ConfigSetting<?> getSetting(String name) {
      for (ConfigSetting<?> setting : this.configSettings) {
         if (setting.getName().equalsIgnoreCase(name)) {
            return setting;
         }
      }

      return null;
   }

   public List<ConfigSetting<?>> getSettings() {
      return this.configSettings;
   }

   public void delete() {
      if (this.file.exists()) {
         this.file.delete();
      }
   }

   public void save(boolean async) {
      if (async) {
         Blaze.getInstance().getThreadingManager().getAsyncExecutor().execute(() -> this.save(false));
      } else {
         try {
            for (ConfigSetting<?> setting : this.getSettings()) {
               this.bukkitConfig.set(setting.getFullPath(), setting.getValue());
            }

            this.bukkitConfig.save(this.file);
            this.configCommenter.saveComments(this.file);
         } catch (IOException var4) {
            MinecraftServer.LOGGER.error(var4);
         }
      }
   }

   public void reload() {
      try {
         this.bukkitConfig.load(this.file);

         for (ConfigSetting<?> configSetting : this.configSettings) {
            Object fetch = this.fetchValue(configSetting);
            if (fetch == null) {
               fetch = configSetting.getValue();
            }

            configSetting.set(fetch, false);
         }
      } catch (InvalidConfigurationException | IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   public void load() {
      try {
         List<ConfigSetting<?>> loading = new ArrayList<>();

         Field[] fields = this.getClass().getDeclaredFields();
         for (Field field : fields) {
            if (field.getType() == ConfigSetting.class) {
               ConfigSetting<?> configSetting = (ConfigSetting<?>)field.get(this);
               configSetting.setConfig(this);
               configSetting.setName(field.getName());
               loading.add(configSetting);
               if (configSetting.getName().equals("knockbackType") && this instanceof Knockback) {
                  Knockback knockback = (Knockback)this;
                  loading.addAll(knockback.getKnockbackSettings());
               }
            }
         }

         if (this instanceof PearlConfig) {
            PearlConfig pearlConfig = (PearlConfig)this;
            loading.addAll(pearlConfig.fetchPearlConfigs());
         }

         for (ConfigSetting<?> configSetting : loading) {
            String path = configSetting.getFullPath();
            Object fetch = this.fetchValue(configSetting);
            if (fetch == null) {
               fetch = configSetting.getValue();
            }

            if (configSetting.getCommentAbove() != null) {
               this.configCommenter.addComment(path, configSetting.getCommentAbove());
            }

            if (configSetting.getComment() != null) {
               this.configCommenter.addCommentNextTo(path, configSetting.getComment());
            }

            configSetting.set(fetch, false);
            this.configSettings.add(configSetting);
         }

         this.save(false);
      } catch (Exception var8) {
         MinecraftServer.LOGGER.error(var8);
      }
   }

   private Object fetchValue(ConfigSetting<?> setting) {
      Object defaultValue = setting.getValue();
      String path = setting.getFullPath();
      if (defaultValue instanceof String) {
         return this.bukkitConfig.getString(path);
      } else if (defaultValue instanceof List) {
         return this.bukkitConfig.getList(path);
      } else {
         if (this.bukkitConfig.contains(path)) {
            if (defaultValue instanceof Boolean) {
               return this.bukkitConfig.getBoolean(path);
            }

            if (defaultValue instanceof Number) {
               Number number = (Number)this.bukkitConfig.get(path);
               if (defaultValue instanceof Integer) {
                  return number.intValue();
               }

               if (defaultValue instanceof Float) {
                  return number.floatValue();
               }

               if (defaultValue instanceof Double) {
                  return number.doubleValue();
               }
            }
         }

         return null;
      }
   }
}
