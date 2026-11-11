package lol.deyo.blaze.configs;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LanguageConfig extends Config {
   public static final ConfigSetting<String> insufficientPerm = new ConfigSetting<>("&dBlaze &7| &cYou do not have access to this command.", "messages");
   public static final ConfigSetting<String> onlyPlayer = new ConfigSetting<>("&dThis command can only be used by players.", "messages");
   public static final ConfigSetting<String> playerNotFound = new ConfigSetting<>("&ePlayer &d%player% &ecould not be found.", "messages");
   public static final ConfigSetting<String> notValidType = new ConfigSetting<>("&eValue &d%value% &eis not type &a%type%&e.", "messages");
   public static final ConfigSetting<String> disconnectSpam = new ConfigSetting<>("disconnect.spam", "messages.player");
   public static final ConfigSetting<String> loggedInAnotherLocation = new ConfigSetting<>("You logged in from another location", "messages.player");
   public static final ConfigSetting<String> factionsDurabilityChecker = new ConfigSetting<>("&eDurability: &d%durability%", "messages.player");
   public static final ConfigSetting<String> playerPing = new ConfigSetting<>("&d[Ping] &eYour Ping: &d%ping%ms", "messages.command.ping-command");
   public static final ConfigSetting<String> otherPlayerPing = new ConfigSetting<>("&d[Ping] &e%player%'s Ping: &d%ping%ms", "messages.command.ping-command");
   public static final ConfigSetting<String> configReloaded = new ConfigSetting<>(
      "&eConfigs have been reloaded in &d%ms%ms&e.", "messages.command.config-command"
   );
   public static final ConfigSetting<String> clearedLag = new ConfigSetting<>(
      "&eYou have cleared &d%entities% &eentities.", "messages.command.clear-lag-command"
   );
   public static final ConfigSetting<String> setMaxPlayersUsage = new ConfigSetting<>(
      "&cUsage: /setmaxplayers <amount>", "messages.command.set-max-players-command"
   );
   public static final ConfigSetting<String> setMaxPlayers = new ConfigSetting<>(
      "&eSet max players to &d%amount% &eplayers.", "messages.command.set-max-players-command"
   );
   public static final ConfigSetting<String> unloadedChunks = new ConfigSetting<>(
      "&eYou have unloaded &d%amount% &echunks.", "messages.command.unload-chunks-command"
   );
   public static final ConfigSetting<List<String>> versionMessage = new ConfigSetting<>(
      Collections.singletonList("&fThis server is running &dBlaze &fversion 1.8.8-R0.1-SNAPSHOT."), "messages.command.version-command"
   );
   public static final ConfigSetting<List<String>> tpsMessage = new ConfigSetting<>(
      Arrays.asList(
         "&7&m-------------------------",
         "&d[Server TPS]",
         "&f(TPS from 1s, 5s, 15s: %server_tps%&f)",
         "&f(Memory: &e%free_memory%&7/&e%max_memory% MB&f) &7- &f(Uptime: &6%uptime%&f)",
         "&f(Threads: &b%threads%&f) &7- &f(Alive: &a%alive%&f, Interrupted: &c%interrupted%&f, Daemon: &9%daemon%&f)",
         "&f(Chunks: &2%chunks%&f) &7- &f(Players: &c%players%&7/&c%max_players%&f)",
         "&f(Entities: &3%entities%&f) &7- &f(Tile Entities: &e%tiles%&f)",
         "&7&m-------------------------"
      ),
      "messages.command.tps-command"
   );
   public static final ConfigSetting<List<String>> knockbackUsage = new ConfigSetting<>(
      Arrays.asList("&7&m-------------------------", "&d[Knockback Usage]", "%commands%", "&7&m-------------------------"), "messages.knockback"
   );
   public static final ConfigSetting<String> profileNotFound = new ConfigSetting<>("&eProfile &d%name% &ecould not be found.", "messages.knockback");
   public static final ConfigSetting<String> createUsage = new ConfigSetting<>("&d- &f/knockback create <name>", "messages.knockback.knockback-create");
   public static final ConfigSetting<String> createHover = new ConfigSetting<>("&eClick to create a new profile.", "messages.knockback.knockback-create");
   public static final ConfigSetting<String> createExists = new ConfigSetting<>("&eProfile &d%name% &ealready exists.", "messages.knockback.knockback-create");
   public static final ConfigSetting<String> createProfile = new ConfigSetting<>(
      "&eProfile &d%name% &ehas been created.", "messages.knockback.knockback-create"
   );
   public static final ConfigSetting<String> deleteUsage = new ConfigSetting<>("&d- &f/knockback delete <name>", "messages.knockback.knockback-delete");
   public static final ConfigSetting<String> deleteHover = new ConfigSetting<>("&eClick to delete a profile.", "messages.knockback.knockback-delete");
   public static final ConfigSetting<String> deleteActive = new ConfigSetting<>(
      "&eYou cannot delete &d%name% &eas it is a active profile.", "messages.knockback.knockback-delete"
   );
   public static final ConfigSetting<String> deleteProfile = new ConfigSetting<>(
      "&eProfile &d%name% &ehas been deleted.", "messages.knockback.knockback-delete"
   );
   public static final ConfigSetting<String> listUsage = new ConfigSetting<>("&d- &f/knockback list", "messages.knockback.knockback-list");
   public static final ConfigSetting<String> listHover = new ConfigSetting<>("&eClick to list profiles.", "messages.knockback.knockback-list");
   public static final ConfigSetting<String> listName = new ConfigSetting<>("&d- &f%name%", "messages.knockback.knockback-list");
   public static final ConfigSetting<String> listNameActive = new ConfigSetting<>("&d- &2%name% &7(Active)", "messages.knockback.knockback-list");
   public static final ConfigSetting<String> listNameHover = new ConfigSetting<>("&eClick to view &d%name%''s &evalues.", "messages.knockback.knockback-list");
   public static final ConfigSetting<List<String>> listMessage = new ConfigSetting<>(
      Arrays.asList("&7&m-------------------------", "&d[Knockback Profiles]", "%profiles%", "&7&m-------------------------"),
      "messages.knockback.knockback-list"
   );
   public static final ConfigSetting<String> setTypeUsage = new ConfigSetting<>(
      "&d- &f/knockback settype <name> <ADVANCED|SIMPLE>", "messages.knockback.knockback-settype"
   );
   public static final ConfigSetting<String> setTypeHover = new ConfigSetting<>("&eClick to change a profiles type.", "messages.knockback.knockback-settype");
   public static final ConfigSetting<String> setTypeAlready = new ConfigSetting<>(
      "&eProfile &d%name% &eis already type &c%type%&e.", "messages.knockback.knockback-settype"
   );
   public static final ConfigSetting<String> setTypeNotFound = new ConfigSetting<>(
      "&eProfile type needs to be: &dSimple/Advanced", "messages.knockback.knockback-settype"
   );
   public static final ConfigSetting<String> setTypePrefix = new ConfigSetting<>("&eClick new type: ", "messages.knockback.knockback-settype");
   public static final ConfigSetting<String> setTypeSimple = new ConfigSetting<>("&b[Simple]", "messages.knockback.knockback-settype");
   public static final ConfigSetting<String> setTypeAdvanced = new ConfigSetting<>("&4[Advanced]", "messages.knockback.knockback-settype");
   public static final ConfigSetting<String> setTypeSimpleHover = new ConfigSetting<>(
      "&eClick to change profile to &dSimple&e.", "messages.knockback.knockback-settype"
   );
   public static final ConfigSetting<String> setTypeAdvancedHover = new ConfigSetting<>(
      "&eClick to change profile to &dAdvanced&e.", "messages.knockback.knockback-settype"
   );
   public static final ConfigSetting<String> setType = new ConfigSetting<>(
      "&eProfile &d%name% &eis now type &c%type%&e.", "messages.knockback.knockback-settype"
   );
   public static final ConfigSetting<String> setActiveUsage = new ConfigSetting<>("&d- &f/knockback setactive <name>", "messages.knockback.knockback-setactive");
   public static final ConfigSetting<String> setActiveHover = new ConfigSetting<>("&eClick to set an active profile.", "messages.knockback.knockback-setactive");
   public static final ConfigSetting<String> setActiveAlready = new ConfigSetting<>(
      "&eProfile &d%name% &eis already active.", "messages.knockback.knockback-setactive"
   );
   public static final ConfigSetting<String> setActive = new ConfigSetting<>(
      "&eProfile &d%name% &eis now globally active.", "messages.knockback.knockback-setactive"
   );
   public static final ConfigSetting<String> viewUsage = new ConfigSetting<>("&d- &f/knockback view <name>", "messages.knockback.knockback-view");
   public static final ConfigSetting<String> viewHover = new ConfigSetting<>("&eClick to view a profile.", "messages.knockback.knockback-view");
   public static final ConfigSetting<String> viewSetting = new ConfigSetting<>("&d- &f%setting%&7: &d%value%", "messages.knockback.knockback-view");
   public static final ConfigSetting<String> viewSettingHover = new ConfigSetting<>(
      "&eClick to set a new value for &d%setting%&e.", "messages.knockback.knockback-view"
   );
   public static final ConfigSetting<String> viewSetActiveClick = new ConfigSetting<>("&a[Set Active]", "messages.knockback.knockback-view");
   public static final ConfigSetting<String> viewSetActiveClickHover = new ConfigSetting<>(
      "&eClick to set profile &d%name% &eactive.", "messages.knockback.knockback-view"
   );
   public static final ConfigSetting<String> viewDeleteClick = new ConfigSetting<>("&c[Delete]", "messages.knockback.knockback-view");
   public static final ConfigSetting<String> viewDeleteClickHover = new ConfigSetting<>("&eClick to delete &d%name%&e.", "messages.knockback.knockback-view");
   public static final ConfigSetting<String> viewEditClick = new ConfigSetting<>("&6[Edit]", "messages.knockback.knockback-view");
   public static final ConfigSetting<String> viewEditHover = new ConfigSetting<>("&eClick to edit &d%name%&e.", "messages.knockback.knockback-view");
   public static final ConfigSetting<String> viewSetTypeClick = new ConfigSetting<>("&d[Change Knockback Type]", "messages.knockback.knockback-view");
   public static final ConfigSetting<String> viewSetTypeHover = new ConfigSetting<>(
      "&eClick to change knockback type for &d%name%&e.", "messages.knockback.knockback-view"
   );
   public static final ConfigSetting<List<String>> viewMessage = new ConfigSetting<>(
      Arrays.asList("&7&m-------------------------", "&d[%name%] &7(%type%)", "%values%", "%clickables%", "%settype%", "&7&m-------------------------"),
      "messages.knockback.knockback-view"
   );
   public static final ConfigSetting<String> editUsage = new ConfigSetting<>(
      "&d- &f/knockback edit <name> <setting> <value>", "messages.knockback.knockback-edit"
   );
   public static final ConfigSetting<String> editHover = new ConfigSetting<>("&eClick to edit a profiles values.", "messages.knockback.knockback-edit");
   public static final ConfigSetting<String> editSettingNotFound = new ConfigSetting<>(
      "&eSetting &d%setting% &ecould not be found.", "messages.knockback.knockback-edit"
   );
   public static final ConfigSetting<String> editTypeNewValue = new ConfigSetting<>(
      "&eType a value for &d%setting% &e(Current: &2%current%&e) &eType &ccancel &eto cancel.", "messages.knockback.knockback-edit"
   );
   public static final ConfigSetting<String> editCancelled = new ConfigSetting<>(
      "&eEditing for value &d%setting% &ehas been cancelled.", "messages.knockback.knockback-edit"
   );
   public static final ConfigSetting<String> editSettingValue = new ConfigSetting<>(
      "&eSet the value of &d%setting% &eto &2%value% &efor profile &c%profile%&e.", "messages.knockback.knockback-edit"
   );
   public static final ConfigSetting<String> editMenuUsage = new ConfigSetting<>("&d- &f/knockback editmenu <name>", "messages.knockback.knockback-editmenu");
   public static final ConfigSetting<String> editMenuHover = new ConfigSetting<>("&eClick to edit a profile.", "messages.knockback.knockback-editmenu");
   public static final ConfigSetting<String> setUsage = new ConfigSetting<>("&d- &f/knockback set <name> <player>", "messages.knockback.knockback-set");
   public static final ConfigSetting<String> setHover = new ConfigSetting<>("&eClick to set a players profile.", "messages.knockback.knockback-set");
   public static final ConfigSetting<String> setProfile = new ConfigSetting<>(
      "&eSet profile &d%profile% &eto player &2%player%&e.", "messages.knockback.knockback-set"
   );
   public static final ConfigSetting<String> clearUsage = new ConfigSetting<>("&d- &f/knockback clear <player>", "messages.knockback.knockback-clear");
   public static final ConfigSetting<String> clearHover = new ConfigSetting<>("&eClick to clear a players profile.", "messages.knockback.knockback-clear");
   public static final ConfigSetting<String> clearProfile = new ConfigSetting<>("&eReset &d%player%&e''s active profile.", "messages.knockback.knockback-clear");
   public static final ConfigSetting<String> configEditNewValue = new ConfigSetting<>(
      "&eType a value for &d%setting% &e(Current: &2%current%&e) &eType &ccancel &eto cancel.", "messages.config-editing"
   );
   public static final ConfigSetting<String> configEditCancelled = new ConfigSetting<>(
      "&eEditing for value &d%setting% &ehas been cancelled.", "messages.config-editing"
   );
   public static final ConfigSetting<String> configEditSetValue = new ConfigSetting<>(
      "&eSet the value of &d%setting% &eto &2%value%&e.", "messages.config-editing"
   );

   public LanguageConfig(Blaze blaze) {
      super(blaze, "language", CC.getConfigHeader("Messages configuration", "You will find all the language here"));
   }
}
