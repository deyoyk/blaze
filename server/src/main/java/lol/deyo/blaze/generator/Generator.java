package lol.deyo.blaze.generator;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Generator extends Config {
   private final String name;
   public final ConfigSetting<Boolean> generateOceans = new ConfigSetting<>(true, "world", "Generate any oceans");
   public final ConfigSetting<Boolean> generateWaterLakes = new ConfigSetting<>(true, "world", "Generate any water lakes");
   public final ConfigSetting<Boolean> generateLavaLakes = new ConfigSetting<>(true, "world", "Generate any lava lakes");
   public final ConfigSetting<Double> cavesMultiplier = new ConfigSetting<>(1.0, "world", "Multiply the amount of cave generation");
   public final ConfigSetting<Double> sugarCaneMultiplier = new ConfigSetting<>(1.0, "world", "Multiply the amount of cane generation");
   public final ConfigSetting<List<String>> biomeReplacements = new ConfigSetting<>(
      Stream.of(
            "Ocean",
            "Plains",
            "Desert",
            "Extreme Hills",
            "Forest",
            "Tauga",
            "Swampland",
            "River",
            "Hell",
            "The End",
            "FrozenOcean",
            "FrozenRiver",
            "Ice Plains",
            "Ice Mountains",
            "MushroomIsland",
            "MushroomIslandShore",
            "Beach",
            "DesertHills",
            "ForestHills",
            "TaigaHills",
            "Extreme Hills Edge",
            "Jungle",
            "JungleHills",
            "JungleEdge",
            "Deep Ocean",
            "Stone Beach",
            "Cold Beach",
            "Birch Forest",
            "Birch Forest Hills",
            "Roofed Forest",
            "Cold Taiga",
            "Cold Taiga Hills",
            "Mega Taiga",
            "Mega Taiga Hills",
            "Extreme Hills+",
            "Savanna",
            "Savanna Plateau",
            "Mesa",
            "Mesa Plateau F",
            "Mesa Plateau"
         )
         .map(s -> s + ";none")
         .collect(Collectors.toList()),
      "world",
      false
   );
   public final ConfigSetting<Integer> spawnBiomeRadius = new ConfigSetting<>(0, "world.spawn", "Radius of spawn biome (0, 0 coords)");
   public final ConfigSetting<String> spawnBiome = new ConfigSetting<>("plains", "world.spawn", "Use custom biome to spawn within a certain radius", false);
   public final ConfigSetting<Boolean> spawnBiomeRivers = new ConfigSetting<>(true, "world.spawn", "Generate rivers in spawn biome");
   public final ConfigSetting<Boolean> spawnBiomeLakes = new ConfigSetting<>(true, "world.spawn", "Generate lakes in spawn biome");
   public final ConfigSetting<Double> emeraldMultiplier = new ConfigSetting<>(1.0, "ores.emerald", "Multiply the amount of emerald generation");
   public final ConfigSetting<Double> coalMultiplier = new ConfigSetting<>(1.0, "ores.coal", "Multiply the amount of coal generation");
   public final ConfigSetting<Integer> coalSize = new ConfigSetting<>(17, "ores.coal", "Maximum amount of coal can be generated in 1 chunk");
   public final ConfigSetting<Boolean> coalMustTouchAir = new ConfigSetting<>(false, "ores.coal", "Generate coal only in caves");
   public final ConfigSetting<Double> ironMultiplier = new ConfigSetting<>(1.0, "ores.iron", "Multiply the amount of iron generation");
   public final ConfigSetting<Integer> ironSize = new ConfigSetting<>(9, "ores.iron", "Maximum amount of iron can be generated in 1 chunk");
   public final ConfigSetting<Boolean> ironMustTouchAir = new ConfigSetting<>(false, "ores.iron", "Generate iron only in caves");
   public final ConfigSetting<Double> goldMultiplier = new ConfigSetting<>(1.0, "ores.gold", "Multiply the amount of gold generation");
   public final ConfigSetting<Integer> goldSize = new ConfigSetting<>(9, "ores.gold", "Maximum amount of gold can be generated in 1 chunk");
   public final ConfigSetting<Boolean> goldMustTouchAir = new ConfigSetting<>(false, "ores.gold", "Generate gold only in caves");
   public final ConfigSetting<Double> redstoneMultiplier = new ConfigSetting<>(1.0, "ores.redstone", "Multiply the amount of redstone generation");
   public final ConfigSetting<Integer> redstoneSize = new ConfigSetting<>(8, "ores.redstone", "Maximum amount of redstone can be generated in 1 chunk");
   public final ConfigSetting<Boolean> redstoneMustTouchAir = new ConfigSetting<>(false, "ores.redstone", "Generate redstone only in caves");
   public final ConfigSetting<Double> diamondMultiplier = new ConfigSetting<>(1.0, "ores.diamond", "Multiply the amount of diamond generation");
   public final ConfigSetting<Integer> diamondSize = new ConfigSetting<>(8, "ores.diamond", "Maximum amount of diamonds can be generated in 1 chunk");
   public final ConfigSetting<Boolean> diamondMustTouchAir = new ConfigSetting<>(false, "ores.diamond", "Generate diamonds only in caves");
   public final ConfigSetting<Double> lapisMultiplier = new ConfigSetting<>(1.0, "ores.lapis", "Multiply the amount of lapis generation");
   public final ConfigSetting<Integer> lapisSize = new ConfigSetting<>(7, "ores.lapis", "Maximum amount of lapis can be generated in 1 chunk");
   public final ConfigSetting<Boolean> lapisMustTouchAir = new ConfigSetting<>(false, "ores.lapis", "Generate lapis only in caves");

   public Generator(Blaze blaze, String name, File file) {
      super(blaze, file.getName() + File.separator + name, CC.getConfigHeader(name + " (Generator)"));
      this.name = name.replace(".yml", "");
   }

   @Override
   public String getName() {
      return this.name;
   }
}
