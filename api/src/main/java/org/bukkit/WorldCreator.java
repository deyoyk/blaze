package org.bukkit;

import java.util.Random;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

public class WorldCreator {
   private final String name;
   private long seed;
   private org.bukkit.World.Environment environment = org.bukkit.World.Environment.NORMAL;
   private ChunkGenerator generator = null;
   private org.bukkit.WorldType type = org.bukkit.WorldType.NORMAL;
   private boolean generateStructures = true;
   private String generatorSettings = "";

   public WorldCreator(String name) {
      if (name == null) {
         throw new IllegalArgumentException("World name cannot be null");
      } else {
         this.name = name;
         this.seed = new Random().nextLong();
      }
   }

   public org.bukkit.WorldCreator copy(org.bukkit.World world) {
      if (world == null) {
         throw new IllegalArgumentException("World cannot be null");
      } else {
         this.seed = world.getSeed();
         this.environment = world.getEnvironment();
         this.generator = world.getGenerator();
         return this;
      }
   }

   public org.bukkit.WorldCreator copy(org.bukkit.WorldCreator creator) {
      if (creator == null) {
         throw new IllegalArgumentException("Creator cannot be null");
      } else {
         this.seed = creator.seed();
         this.environment = creator.environment();
         this.generator = creator.generator();
         return this;
      }
   }

   public String name() {
      return this.name;
   }

   public long seed() {
      return this.seed;
   }

   public org.bukkit.WorldCreator seed(long seed) {
      this.seed = seed;
      return this;
   }

   public org.bukkit.World.Environment environment() {
      return this.environment;
   }

   public org.bukkit.WorldCreator environment(org.bukkit.World.Environment env) {
      this.environment = env;
      return this;
   }

   public org.bukkit.WorldType type() {
      return this.type;
   }

   public org.bukkit.WorldCreator type(org.bukkit.WorldType type) {
      this.type = type;
      return this;
   }

   public ChunkGenerator generator() {
      return this.generator;
   }

   public org.bukkit.WorldCreator generator(ChunkGenerator generator) {
      this.generator = generator;
      return this;
   }

   public org.bukkit.WorldCreator generator(String generator) {
      this.generator = getGeneratorForName(this.name, generator, org.bukkit.Bukkit.getConsoleSender());
      return this;
   }

   public org.bukkit.WorldCreator generator(String generator, CommandSender output) {
      this.generator = getGeneratorForName(this.name, generator, output);
      return this;
   }

   public org.bukkit.WorldCreator generatorSettings(String generatorSettings) {
      this.generatorSettings = generatorSettings;
      return this;
   }

   public String generatorSettings() {
      return this.generatorSettings;
   }

   public org.bukkit.WorldCreator generateStructures(boolean generate) {
      this.generateStructures = generate;
      return this;
   }

   public boolean generateStructures() {
      return this.generateStructures;
   }

   public org.bukkit.World createWorld() {
      return org.bukkit.Bukkit.createWorld(this);
   }

   public static org.bukkit.WorldCreator name(String name) {
      return new org.bukkit.WorldCreator(name);
   }

   public static ChunkGenerator getGeneratorForName(String world, String name, CommandSender output) {
      ChunkGenerator result = null;
      if (world == null) {
         throw new IllegalArgumentException("World name must be specified");
      } else {
         if (output == null) {
            output = org.bukkit.Bukkit.getConsoleSender();
         }

         if (name != null) {
            String[] split = name.split(":", 2);
            String id = split.length > 1 ? split[1] : null;
            Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin(split[0]);
            if (plugin == null) {
               output.sendMessage("Could not set generator for world '" + world + "': Plugin '" + split[0] + "' does not exist");
            } else if (!plugin.isEnabled()) {
               output.sendMessage("Could not set generator for world '" + world + "': Plugin '" + plugin.getDescription().getFullName() + "' is not enabled");
            } else {
               result = plugin.getDefaultWorldGenerator(world, id);
            }
         }

         return result;
      }
   }
}
