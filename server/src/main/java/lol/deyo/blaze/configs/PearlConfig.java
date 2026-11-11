package lol.deyo.blaze.configs;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.api.pearls.PearlHandler;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;
import lol.deyo.blaze.utils.Utils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Gate;

public class PearlConfig extends Config {
   public static final ConfigSetting<Boolean> fixBlockGlitch = new ConfigSetting<>(true, "antiGlitch");
   public static final ConfigSetting<Boolean> fixFenceGlitch = new ConfigSetting<>(true, "antiGlitch");
   public static final ConfigSetting<Boolean> refundSuffocation = new ConfigSetting<>(true, "antiGlitch");
   public static final ConfigSetting<Boolean> refundRiskyPearl = new ConfigSetting<>(true, "antiGlitch");
   public static final ConfigSetting<Boolean> refundOnCritblock = new ConfigSetting<>(false, "antiGlitch");
   public static final ConfigSetting<Boolean> refundIfSoClose = new ConfigSetting<>(
      true, "antiGlitch", "Allow for players to spam pearls on a block close to them"
   );
   public static final ConfigSetting<Double> refundCloseDistance = new ConfigSetting<>(1.0, "antiGlitch");
   public static final ConfigSetting<Boolean> glitchReturnPearl = new ConfigSetting<>(true, "antiGlitch");
   public static final ConfigSetting<String> glitchReturnMessage = new ConfigSetting<>("", "antiGlitch", false);
   public static final ConfigSetting<String> glitchReturnCommand = new ConfigSetting<>("timer remove %player% Enderpearl", "antiGlitch", false);
   public static final ConfigSetting<Boolean> instantlyTaliTeleport = new ConfigSetting<>(false, "teleport.misc");
   public static final ConfigSetting<Boolean> entityTpHitDetection = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<Boolean> entityTpGetOutOneByOne = new ConfigSetting<>(
      true, "teleport.misc", "Deny entity TP system if you are in a one by one"
   );
   public static final ConfigSetting<Boolean> spawnEndermite = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<Boolean> pearlThruFence = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<Boolean> pearlThruString = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<Boolean> pearlThruWeb = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<Boolean> pearlThruPlants = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<Boolean> pearlHopperUnderFence = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<Boolean> pearlChestUnderFence = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<Boolean> pearlParticlesEnabled = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<Integer> pearlParticleCount = new ConfigSetting<>(32, "teleport.misc");
   public static final ConfigSetting<Double> pearlDamage = new ConfigSetting<>(5.0, "teleport.misc");
   public static final ConfigSetting<Double> crossPearlMoveHelper = new ConfigSetting<>(
      0.35, "teleport.misc", "How much should we move the player inside the block when cross-pearling"
   );
   public static final ConfigSetting<Boolean> hitBlockTeleport = new ConfigSetting<>(
      true, "teleport.misc", "Should we teleport them if they throw an enderpearl directly on the block"
   );
   public static final ConfigSetting<Boolean> beforeBlockTeleport = new ConfigSetting<>(
      true, "teleport.misc", "Should we teleport them if they throw an enderpearl just before the block (Like ViperMC)"
   );
   public static final ConfigSetting<Integer> maxTeleportBlocks = new ConfigSetting<>(
      3, "teleport.misc", "Maximum amount of blocks you can tali teleport through"
   );
   public static final ConfigSetting<Boolean> launchOnFenceClick = new ConfigSetting<>(true, "teleport.misc");
   public static final ConfigSetting<List<String>> launchOnOtherClick = new ConfigSetting<>(
      Arrays.asList(
         "FURNACE",
         "ANVIL",
         "SIGN",
         "SIGN_POST",
         "WALL_SIGN",
         "HOPPER",
         "CHEST",
         "ENDER_CHEST",
         "TRAPPED_CHEST",
         "WORKBENCH",
         "ENCHANTMENT_TABLE",
         "BEACON",
         "ANVIL"
      ),
      "teleport.misc",
      false
   );
   public static final List<PearlConfig.PearlConfigSetting> pearlSettings = Arrays.asList(
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.SLABS, "teleport.slabs"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.STAIRS, "teleport.stairs"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.CHESTS, "teleport.chests"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.BEDS, "teleport.beds"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.COBBLE_WALLS, "teleport.cobblewalls"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.PISTONS, "teleport.pistons"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.END_FRAMES, "teleport.end-frames"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.ENCHANT_TABLES, "teleport.enchant-tables"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.ANVILS, "teleport.anvils"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.DAYLIGHT_SENSORS, "teleport.daylight-sensors"),
      new PearlConfig.PearlConfigSetting(PearlConfig.BlockAliases.TRAPDOORS, "teleport.trapdoors")
   );

   public PearlConfig(Blaze instance) {
      super(instance, "pearls", CC.getConfigHeader("HCF Enderpearl Configuration", "All the options for enderpearl mechanics"));
   }

   public static void handlePearl(PlayerInteractEvent e) {
      if (e.getClickedBlock() != null) {
         if (e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL) {
            Player player = e.getPlayer();
            Block block = e.getClickedBlock();
            String material = block.getType().name();
            boolean shouldPearl = material.contains("FENCE_GATE") && ((Gate)block.getState().getData()).isOpen() && launchOnFenceClick.getValue()
               || launchOnOtherClick.getValue().contains(material);
            if (shouldPearl) {
               for (PearlHandler pearlHandler : Blaze.getInstance().getPearlHandlers()) {
                  if (pearlHandler.hasCooldown(player)) {
                     return;
                  }
               }

               e.setCancelled(true);
               player.launchProjectile(EnderPearl.class);
               Utils.takeItemInHand(player, 1);
            }
         }
      }
   }

   public List<ConfigSetting<?>> fetchPearlConfigs() {
      List<ConfigSetting<?>> pearlConfigs = new ArrayList<>();

      try {
         for (PearlConfig.PearlConfigSetting pearlSetting : pearlSettings) {
            Field[] var7;
            for (Field field : var7 = pearlSetting.getClass().getDeclaredFields()) {
               if (field.getType() == ConfigSetting.class) {
                  ConfigSetting<?> configSetting = (ConfigSetting<?>)field.get(pearlSetting);
                  configSetting.setConfig(this);
                  configSetting.setName(field.getName());
                  pearlConfigs.add(configSetting);
               }
            }
         }

         return pearlConfigs;
      } catch (IllegalAccessException var9) {
         throw new RuntimeException(var9);
      }
   }

   public static enum BlockAliases {
      FENCES(
         Material.FENCE,
         Material.NETHER_FENCE,
         Material.IRON_FENCE,
         Material.SPRUCE_FENCE,
         Material.BIRCH_FENCE,
         Material.JUNGLE_FENCE,
         Material.DARK_OAK_FENCE,
         Material.ACACIA_FENCE
      ),
      FENCE_GATES(
         Material.FENCE_GATE,
         Material.JUNGLE_FENCE_GATE,
         Material.SPRUCE_FENCE_GATE,
         Material.DARK_OAK_FENCE_GATE,
         Material.ACACIA_FENCE_GATE,
         Material.BIRCH_FENCE_GATE
      ),
      CHESTS(Material.CHEST, Material.ENDER_CHEST, Material.TRAPPED_CHEST),
      SLABS(Material.STEP, Material.STONE_SLAB2, Material.WOOD_STEP),
      COBBLE_WALLS(Material.COBBLE_WALL),
      ENCHANT_TABLES(Material.ENCHANTMENT_TABLE),
      PISTONS(Material.PISTON_EXTENSION, Material.PISTON_MOVING_PIECE),
      STAIRS(
         Material.RED_SANDSTONE_STAIRS,
         Material.BRICK_STAIRS,
         Material.DARK_OAK_STAIRS,
         Material.ACACIA_STAIRS,
         Material.QUARTZ_STAIRS,
         Material.JUNGLE_WOOD_STAIRS,
         Material.BIRCH_WOOD_STAIRS,
         Material.SPRUCE_WOOD_STAIRS,
         Material.SANDSTONE_STAIRS,
         Material.NETHER_BRICK_STAIRS,
         Material.WOOD_STAIRS,
         Material.COBBLESTONE_STAIRS,
         Material.BRICK_STAIRS,
         Material.SMOOTH_STAIRS
      ),
      BEDS(Material.BED_BLOCK, Material.BED),
      END_FRAMES(Material.ENDER_PORTAL_FRAME),
      ANVILS(Material.ANVIL),
      DAYLIGHT_SENSORS(Material.DAYLIGHT_DETECTOR, Material.DAYLIGHT_DETECTOR_INVERTED),
      SIGNS(Material.WALL_SIGN, Material.SIGN_POST),
      BANNERS(Material.STANDING_BANNER, Material.WALL_BANNER),
      SAPLINGS(Material.SAPLING),
      TRAPDOORS(Material.TRAP_DOOR, Material.IRON_TRAPDOOR),
      PLATES(Material.STONE_PLATE, Material.WOOD_PLATE, Material.GOLD_PLATE, Material.IRON_PLATE),
      DOORS(
         Material.WOOD_DOOR,
         Material.IRON_DOOR,
         Material.IRON_DOOR_BLOCK,
         Material.WOODEN_DOOR,
         Material.ACACIA_DOOR,
         Material.BIRCH_DOOR,
         Material.SPRUCE_DOOR,
         Material.JUNGLE_DOOR,
         Material.DARK_OAK_DOOR
      ),
      BUTTONS(Material.STONE_BUTTON, Material.WOOD_BUTTON);

      private final List<Material> ids;

      private BlockAliases(Material... materials) {
         this.ids = Arrays.asList(materials);
      }

      public boolean isApplicable(Material material) {
         return this.ids.contains(material);
      }
   }

   public static class PearlConfigSetting {
      public PearlConfig.BlockAliases aliases;
      public String path;
      public ConfigSetting<Boolean> enabled;
      public ConfigSetting<Boolean> crosspearl;
      public ConfigSetting<Boolean> diagonal;
      public ConfigSetting<Boolean> critblock;

      public PearlConfigSetting(PearlConfig.BlockAliases aliases, String path) {
         this.aliases = aliases;
         this.path = path;
         this.enabled = new ConfigSetting<>(true, path);
         this.crosspearl = new ConfigSetting<>(true, path);
         this.diagonal = new ConfigSetting<>(true, path);
         this.critblock = new ConfigSetting<>(true, path);
      }
   }
}
