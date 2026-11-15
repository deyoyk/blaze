package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;

import java.util.List;

public class AnticheatGuiConfig extends Config {
   public static final ConfigSetting<String> mainTitle = new ConfigSetting<>(
      "&8Blaze AC", "main", "Title of the main anticheat GUI."
   );
   public static final ConfigSetting<String> mainFillerType = new ConfigSetting<>(
      "STAINED_GLASS_PANE", "main", "Filler item type."
   );
   public static final ConfigSetting<Integer> mainFillerData = new ConfigSetting<>(
      7, "main", "Filler item data value."
   );
   public static final ConfigSetting<String> mainPlayerItemType = new ConfigSetting<>(
      "SKULL_ITEM", "main.players", "Item type for player entries."
   );
   public static final ConfigSetting<String> mainPlayerName = new ConfigSetting<>(
      "&6%player%", "main.players", "Display name for player entries. %player% is replaced."
   );
   public static final ConfigSetting<List<String>> mainPlayerLore = new ConfigSetting<>(
      java.util.Collections.singletonList(
         "&7Total VL: &f%vl%%newline%&7Top check: &c%topcheck%%newline%%newline%&eClick to inspect"
      ),
      "main.players", "Lore for player entries. %player% %vl% %topcheck% %newline% are replaced."
   );

   public static final ConfigSetting<String> checksTitle = new ConfigSetting<>(
      "&8Blaze AC &7%player%", "checks", "Title of the per-player checks GUI. %player% is replaced."
   );
   public static final ConfigSetting<String> checksItemType = new ConfigSetting<>(
      "PAPER", "checks.item", "Item type for check entries."
   );
   public static final ConfigSetting<String> checksItemName = new ConfigSetting<>(
      "&c%check%", "checks.item", "Display name for check entries. %check% is replaced."
   );
   public static final ConfigSetting<List<String>> checksItemLore = new ConfigSetting<>(
      java.util.Arrays.asList(
         "&7VL: &f%vl%",
         "&7Violations: &f%violations%",
         "&7Status: %status%"
      ),
      "checks.item", "Lore for check entries. %check% %vl% %violations% %status% are replaced."
   );
   public static final ConfigSetting<String> checksStatusClean = new ConfigSetting<>(
      "&aclean", "checks", "Status text when the check VL is zero."
   );
   public static final ConfigSetting<String> checksStatusFlagged = new ConfigSetting<>(
      "&cflagged", "checks", "Status text when the check VL is above zero."
   );
   public static final ConfigSetting<String> checksCleanItemType = new ConfigSetting<>(
      "PAPER", "checks", "Item type for check entries with zero VL."
   );

   public static final ConfigSetting<String> punishmentsTitle = new ConfigSetting<>(
      "&8Blaze AC &7Punishments", "punishments", "Title of the punishments GUI."
   );
   public static final ConfigSetting<String> punishmentsItemType = new ConfigSetting<>(
      "BOOK", "punishments.item", "Item type for punishment entries."
   );
   public static final ConfigSetting<String> punishmentsItemName = new ConfigSetting<>(
      "&6#%id%", "punishments.item", "Display name for punishment entries. %id% is replaced."
   );
   public static final ConfigSetting<List<String>> punishmentsItemLore = new ConfigSetting<>(
      java.util.Arrays.asList(
         "&7Player: &f%player%",
         "&7Check: &c%check%",
         "&7Action: &f%action%",
         "&7Issued: &f%issued%",
         "&7Expires: &f%expires%",
         "&7Status: %status%",
         "",
         "&eLeft-click to inspect, &6Shift-click to unban"
      ),
      "punishments.item", "Lore for punishment entries. %id% %player% %check% %action% %issued% %expires% %status% are replaced."
   );
   public static final ConfigSetting<String> punishmentsStatusActive = new ConfigSetting<>(
      "&aactive", "punishments", "Status text for active punishments."
   );
   public static final ConfigSetting<String> punishmentsStatusLifted = new ConfigSetting<>(
      "&8lifted", "punishments", "Status text for lifted punishments."
   );

   public AnticheatGuiConfig(Blaze blaze) {      super(
         blaze,
         "anticheat/gui",
         CC.getConfigHeader("Blaze Anticheat GUI", "Configure every menu, item and", "message of the anticheat GUI here.")
      );
   }
}
