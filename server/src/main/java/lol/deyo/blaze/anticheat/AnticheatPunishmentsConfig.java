package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;

public class AnticheatPunishmentsConfig extends Config {
   public static final ConfigSetting<String> globalKick = new ConfigSetting<>(
      "6:KICK", "global", "Entry format: '<violationCount>:<WARN|KICK|BAN>[:duration]'. Duration like 30m, 2h, 7d, permanent."
   );
   public static final ConfigSetting<String> globalBan = new ConfigSetting<>(
      "12:BAN:7d", "global", "Second global schedule entry."
   );
   public static final ConfigSetting<String> speedWarn = new ConfigSetting<>(
      "3:WARN", "Speed", "Speed check schedule entry."
   );
   public static final ConfigSetting<String> speedBan = new ConfigSetting<>(
      "5:BAN:2d", "Speed", "Speed check schedule entry."
   );
   public static final ConfigSetting<String> flightWarn = new ConfigSetting<>(
      "3:WARN", "Flight", "Flight check schedule entry."
   );
   public static final ConfigSetting<String> flightBan = new ConfigSetting<>(
      "5:BAN:2d", "Flight", "Flight check schedule entry."
   );
   public static final ConfigSetting<String> killauraKick = new ConfigSetting<>(
      "4:KICK", "Killaura", "Killaura check schedule entry."
   );
   public static final ConfigSetting<String> killauraBan = new ConfigSetting<>(
      "8:BAN:14d", "Killaura", "Killaura check schedule entry."
   );
   public static final ConfigSetting<String> autoclickerWarn = new ConfigSetting<>(
      "4:WARN", "AutoClicker", "AutoClicker check schedule entry."
   );
   public static final ConfigSetting<String> autoclickerKick = new ConfigSetting<>(
      "8:KICK", "AutoClicker", "AutoClicker check schedule entry."
   );
   public static final ConfigSetting<String> autoclickerBan = new ConfigSetting<>(
      "12:BAN:7d", "AutoClicker", "AutoClicker check schedule entry."
   );
   public static final ConfigSetting<String> reachBan = new ConfigSetting<>(
      "5:BAN:3d", "Reach", "Reach check schedule entry."
   );
   public static final ConfigSetting<String> timerBan = new ConfigSetting<>(
      "5:BAN:3d", "Timer", "Timer check schedule entry."
   );
   public static final ConfigSetting<String> phaseBan = new ConfigSetting<>(
      "4:BAN:7d", "Phase", "Phase check schedule entry."
   );

   public AnticheatPunishmentsConfig(Blaze blaze) {
      super(
         blaze,
         "anticheat/punishments",
         CC.getConfigHeader("Blaze Anticheat Punishments",
            "Each entry: '<violationCount>:<WARN|KICK|BAN>[:duration]'",
            "Durations: 30m, 2h, 7d or permanent. Entries are per check,",
            "the global section applies to every check without its own entries.")
      );
   }
}
