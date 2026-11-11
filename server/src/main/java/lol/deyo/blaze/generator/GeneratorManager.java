package lol.deyo.blaze.generator;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.ConfigFolder;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class GeneratorManager {
   private final Map<String, Generator> generators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   private final ConfigFolder parentFile = new ConfigFolder("generator");
   private Generator defaultConfig;

   public GeneratorManager() {
      this.load();
   }

   private void load() {
      File[] files = this.parentFile.listFiles();
      if (files != null) {
         for (File file : files) {
            this.createGenerator(file.getName());
         }
      }

      this.defaultConfig = this.getOrCreate("Default");
   }

   public void save(boolean async) {
      for (Generator generator : this.generators.values()) {
         generator.save(async);
      }
   }

   private Generator createGenerator(String name) {
      Generator generator = new Generator(Blaze.getInstance(), name, this.parentFile);
      generator.load();
      generator.save(false);
      this.generators.put(generator.getName(), generator);
      return generator;
   }

   public Generator getOrCreate(String name) {
      Generator generator = this.generators.get(name);
      if (generator == null) {
         generator = this.createGenerator(name);
      }

      return generator;
   }

   public Generator getDefaultConfig() {
      return this.defaultConfig;
   }

   public Map<String, Generator> getGenerators() {
      return this.generators;
   }
}
