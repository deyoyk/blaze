package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;

public class AnticheatMessagesConfig extends Config {
   public static final ConfigSetting<String> alertPermission = new ConfigSetting<>(
      "blaze.ac.alerts", "alerts", "Permission required to receive and toggle alerts."
   );
   public static final ConfigSetting<Boolean> alertJoinMessage = new ConfigSetting<>(
      true, "alerts", "Tell players with alert permission that alerts are available on join."
   );
   public static final ConfigSetting<Integer> alertCooldownMs = new ConfigSetting<>(
      400, "alerts", "Minimum millis between alerts for the same player and check."
   );
   public static final ConfigSetting<Boolean> alertOnConsole = new ConfigSetting<>(
      true, "alerts", "Print violations to the console."
   );
   public static final ConfigSetting<String> alertFormat = new ConfigSetting<>(
      "&6[Blaze AC]&e &f%player% &7failed &c%check% &7vl=%vl% &8%detail%", "alerts",
      "Alert format. %player% %check% %vl% %detail% are replaced."
   );
   public static final ConfigSetting<String> alertJoinText = new ConfigSetting<>(
      "&6[Blaze AC]&e You have alert permission. Use /blazeac alerts.", "alerts",
      "Join notice sent to players with alert permission."
   );

   public static final ConfigSetting<String> banMessage = new ConfigSetting<>(
      "&8&m----------------------------\n"
         + "&6Blaze Anticheat\n\n"
         + "&fYou have been banned for unfair advantage.\n"
         + "&7Reason: &f%reason%\n"
         + "&7Ban ID: &6%id%\n"
         + "&7Expires: &f%expires%\n\n"
         + "&7Ban ID %id% &7is needed to appeal or to /blazeac unban.\n"
         + "&8&m----------------------------",
      "bans", "Kick-screen shown to banned players. %player% %reason% %id% %expires% %check% are replaced."
   );
   public static final ConfigSetting<Boolean> banMessageEnabled = new ConfigSetting<>(
      true, "bans", "Send the kick-screen message when a player is banned by the anticheat."
   );
   public static final ConfigSetting<String> banBroadcast = new ConfigSetting<>(
      "&6[Blaze AC] &f%player% &7was banned &7(&c%check%&7) &8[%id%]", "bans",
      "Broadcast sent to everyone when a player is banned. %player% %check% %id% %reason% %expires% are replaced."
   );
   public static final ConfigSetting<Boolean> banBroadcastEnabled = new ConfigSetting<>(
      true, "bans", "Announce anticheat bans to the whole server."
   );

   public static final ConfigSetting<String> kickMessage = new ConfigSetting<>(
      "&8&m----------------------------\n"
         + "&6Blaze Anticheat\n\n"
         + "&fYou were kicked by the anticheat.\n"
         + "&7Reason: &f%reason%\n"
         + "&7Punishment ID: &6%id%\n"
         + "&7Next violation leads to harsher punishment.\n"
         + "&8&m----------------------------",
      "kick", "Kick-screen shown to kicked players. %player% %reason% %id% %check% are replaced."
   );
   public static final ConfigSetting<Boolean> kickMessageEnabled = new ConfigSetting<>(
      true, "kick", "Send the kick-screen message when a player is kicked by the anticheat."
   );
   public static final ConfigSetting<String> kickBroadcast = new ConfigSetting<>(
      "&6[Blaze AC] &f%player% &7was kicked &7(&c%check%&7) &8[%id%]", "kick",
      "Broadcast sent to everyone when a player is kicked. %player% %check% %id% %reason% are replaced."
   );
   public static final ConfigSetting<Boolean> kickBroadcastEnabled = new ConfigSetting<>(
      false, "kick", "Announce anticheat kicks to the whole server."
   );

   public static final ConfigSetting<String> warnMessage = new ConfigSetting<>(
      "&6[Blaze AC] &fYou were warned: &7%reason% &8[%id%]", "warn",
      "Chat message sent to the punished player. %player% %reason% %id% %check% are replaced."
   );
   public static final ConfigSetting<Boolean> warnMessageEnabled = new ConfigSetting<>(
      true, "warn", "Send players a warning chat message on warn punishments."
   );

   public static final ConfigSetting<String> guiNoPermission = new ConfigSetting<>(
      "&6[Blaze AC]&c You do not have permission for the anticheat GUI.", "misc",
      "Sent when a player without permission tries to open the GUI."
   );

   public static final ConfigSetting<Boolean> statsEnabled = new ConfigSetting<>(
      true, "stats", "Periodically broadcast anticheat statistics to everyone."
   );
   public static final ConfigSetting<Integer> statsIntervalMinutes = new ConfigSetting<>(
      240, "stats", "Minutes between stats broadcasts."
   );
   public static final ConfigSetting<Integer> statsDays = new ConfigSetting<>(
      7, "stats", "Window in days for the counts (e.g. 7 = past week)."
   );
   public static final ConfigSetting<Boolean> statsIncludeBans = new ConfigSetting<>(
      true, "stats", "Include ban count in the stats message."
   );
   public static final ConfigSetting<Boolean> statsIncludeKicks = new ConfigSetting<>(
      true, "stats", "Include kick count in the stats message."
   );
   public static final ConfigSetting<Boolean> statsIncludeChecks = new ConfigSetting<>(
      false, "stats", "Include top check name in the stats message."
   );
   public static final ConfigSetting<String> statsMessage = new ConfigSetting<>(
      "&6[Blaze AC] &eIn the past %window%: &f%bans% &7bans, &f%kicks% &7kicks.",
      "stats", "Stats broadcast. %bans% %kicks% %window% %topcheck% are replaced."
   );

   public AnticheatMessagesConfig(Blaze blaze) {
      super(
         blaze,
         "anticheat/messages",
         CC.getConfigHeader("Blaze Anticheat Messages", "Every message can be enabled or disabled", "individually below.")
      );
   }
}
