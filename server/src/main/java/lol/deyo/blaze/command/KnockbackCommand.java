package lol.deyo.blaze.command;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.api.knockback.IKnockback;
import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.framework.commands.Command;
import lol.deyo.blaze.knockback.Knockback;
import lol.deyo.blaze.knockback.KnockbackManager;
import lol.deyo.blaze.knockback.KnockbackType;
import lol.deyo.blaze.knockback.extra.KnockbackEdit;
import lol.deyo.blaze.menu.ConfigEditMenu;
import lol.deyo.blaze.utils.fanciful.FancyMessage;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KnockbackCommand extends Command {
   public KnockbackCommand() {
      super("knockback");
      this.setPermission("blaze.knockback");
      this.setAliases(Arrays.asList("kb", "kbprofiles"));
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      if (args.length == 0) {
         this.sendUsage(sender);
      } else {
         KnockbackManager manager = Blaze.getInstance().getKnockbackManager();
         String var4;
         switch ((var4 = args[0].toLowerCase()).hashCode()) {
            case -1352294148:
               if (var4.equals("create")) {
                  if (args.length < 2) {
                     this.sendMessage(sender, new String[]{LanguageConfig.createUsage.getValue()});
                     return;
                  }

                  String create = args[1];
                  if (manager.getByName(create) != null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.createExists.getValue().replace("%name%", create)});
                     return;
                  }

                  IKnockback knockback = manager.createProfile(create);
                  this.sendMessage(sender, new String[]{LanguageConfig.createProfile.getValue().replace("%name%", knockback.getName())});
                  return;
               }
               break;
            case -1335458389:
               if (var4.equals("delete")) {
                  if (args.length < 2) {
                     this.sendMessage(sender, new String[]{LanguageConfig.deleteUsage.getValue()});
                     return;
                  }

                  String delete = args[1];
                  if (manager.getByName(delete) == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.profileNotFound.getValue().replace("%name%", delete)});
                     return;
                  }

                  if (manager.getActiveProfile().getName().equalsIgnoreCase(delete)) {
                     this.sendMessage(sender, new String[]{LanguageConfig.deleteActive.getValue().replace("%name%", delete)});
                     return;
                  }

                  manager.deleteProfile(delete);
                  this.sendMessage(sender, new String[]{LanguageConfig.deleteProfile.getValue().replace("%name%", delete)});
                  return;
               }
               break;
            case 113762:
               if (var4.equals("set")) {
                  if (args.length < 3) {
                     this.sendMessage(sender, new String[]{LanguageConfig.setUsage.getValue()});
                     return;
                  }

                  IKnockback setting = manager.getByName(args[1]);
                  Player settingPlayer = Bukkit.getPlayer(args[2]);
                  if (setting == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.profileNotFound.getValue().replace("%name%", args[1])});
                     return;
                  }

                  if (settingPlayer == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.playerNotFound.getValue().replace("%player%", args[2])});
                     return;
                  }

                  settingPlayer.setKnockbackProfile(setting);
                  this.sendMessage(
                     sender,
                     new String[]{LanguageConfig.setProfile.getValue().replace("%profile%", setting.getName()).replace("%player%", settingPlayer.getName())}
                  );
                  return;
               }
               break;
            case 3108362:
               if (var4.equals("edit")) {
                  if (args.length < 3) {
                     this.sendMessage(sender, new String[]{LanguageConfig.editUsage.getValue()});
                     return;
                  }

                  Knockback editing = (Knockback)manager.getByName(args[1]);
                  if (editing == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.profileNotFound.getValue().replace("%name%", args[1])});
                     return;
                  }

                  ConfigSetting<?> configSetting = editing.getActiveSetting(args[2]);
                  if (configSetting == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.editSettingNotFound.getValue().replace("%setting%", args[2])});
                     return;
                  }

                  if (args.length == 4) {
                     Object value = configSetting.parseValue(args[3]);
                     if (value == null) {
                        this.sendMessage(
                           sender,
                           new String[]{
                              LanguageConfig.notValidType
                                 .getValue()
                                 .replace("%value%", args[3])
                                 .replace("%type%", configSetting.getValue().getClass().getSimpleName())
                           }
                        );
                        return;
                     }

                     configSetting.set(value, true);
                     this.sendMessage(
                        sender,
                        new String[]{
                           LanguageConfig.editSettingValue
                              .getValue()
                              .replace("%setting%", configSetting.getName())
                              .replace("%value%", args[3])
                              .replace("%profile%", editing.getName())
                        }
                     );
                     return;
                  }

                  if (!(sender instanceof Player)) {
                     this.sendMessage(sender, new String[]{LanguageConfig.onlyPlayer.getValue()});
                     return;
                  }

                  Player player = (Player)sender;
                  manager.getEditing().put(player.getUniqueId(), new KnockbackEdit(editing.getName(), configSetting));
                  this.sendMessage(
                     sender,
                     new String[]{
                        LanguageConfig.editTypeNewValue
                           .getValue()
                           .replace("%setting%", configSetting.getName())
                           .replace("%current%", configSetting.getValue().toString())
                           .replace("%profile%", editing.getName())
                     }
                  );
                  return;
               }
               break;
            case 3322014:
               if (var4.equals("list")) {
                  for (String sx : LanguageConfig.listMessage.getValue()) {
                     if (!sx.equalsIgnoreCase("%profiles%")) {
                        this.sendMessage(sender, new String[]{sx});
                     } else {
                        for (IKnockback profile : manager.getProfiles()) {
                           String name = profile.isActive() ? LanguageConfig.listNameActive.getValue() : LanguageConfig.listName.getValue();
                           new FancyMessage(name.replace("%name%", profile.getName()).replace("%type%", profile.getType()))
                              .tooltip(LanguageConfig.listNameHover.getValue().replace("%name%", profile.getName()))
                              .command("/knockback view " + profile.getName())
                              .send(sender);
                        }
                     }
                  }

                  return;
               }
               break;
            case 3619493:
               if (var4.equals("view")) {
                  if (args.length < 2) {
                     this.sendMessage(sender, new String[]{LanguageConfig.viewUsage.getValue()});
                     return;
                  }

                  IKnockback view = manager.getByName(args[1]);
                  if (view == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.profileNotFound.getValue().replace("%name%", args[1])});
                     return;
                  }

                  for (String s : LanguageConfig.viewMessage.getValue()) {
                     if (s.equalsIgnoreCase("%values%")) {
                        for (ConfigSetting<?> settingx : ((Knockback)view).getActiveSettings()) {
                           UnaryOperator<String> replacer = string -> string.replace("%setting%", settingx.getName())
                              .replace("%type%", view.getType())
                              .replace("%value%", settingx.getValue().toString());
                           new FancyMessage(replacer.apply(LanguageConfig.viewSetting.getValue()))
                              .tooltip(replacer.apply(LanguageConfig.viewSettingHover.getValue()))
                              .command("/knockback edit " + view.getName() + " " + settingx.getName())
                              .send(sender);
                        }
                     } else if (s.equalsIgnoreCase("%clickables%")) {
                        UnaryOperator<String> replacer = string -> string.replace("%name%", view.getName());
                        new FancyMessage(LanguageConfig.viewSetActiveClick.getValue())
                           .tooltip(replacer.apply(LanguageConfig.viewSetActiveClickHover.getValue()))
                           .command("/knockback setactive " + view.getName())
                           .then(" ")
                           .then(LanguageConfig.viewDeleteClick.getValue())
                           .tooltip(replacer.apply(LanguageConfig.viewDeleteClickHover.getValue()))
                           .command("/knockback delete " + view.getName())
                           .then(" ")
                           .then(LanguageConfig.viewEditClick.getValue())
                           .tooltip(replacer.apply(LanguageConfig.viewEditHover.getValue()))
                           .command("/knockback editmenu " + view.getName())
                           .send(sender);
                     } else if (s.equalsIgnoreCase("%settype%")) {
                        UnaryOperator<String> replacer = string -> string.replace("%name%", view.getName());
                        new FancyMessage(LanguageConfig.viewSetTypeClick.getValue())
                           .tooltip(replacer.apply(LanguageConfig.viewSetTypeHover.getValue()))
                           .command("/knockback settype " + view.getName())
                           .send(sender);
                     } else {
                        this.sendMessage(sender, new String[]{s.replace("%name%", view.getName()).replace("%type%", view.getType())});
                     }
                  }

                  return;
               }
               break;
            case 94746189:
               if (var4.equals("clear")) {
                  if (args.length < 2) {
                     this.sendMessage(sender, new String[]{LanguageConfig.clearUsage.getValue()});
                     return;
                  }

                  Player clearingPlayer = Bukkit.getPlayer(args[1]);
                  if (clearingPlayer == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.playerNotFound.getValue().replace("%player%", args[1])});
                     return;
                  }

                  clearingPlayer.setKnockbackProfile(null);
                  this.sendMessage(sender, new String[]{LanguageConfig.clearProfile.getValue().replace("%player%", clearingPlayer.getName())});
                  return;
               }
               break;
            case 974580200:
               if (var4.equals("setactive")) {
                  if (args.length < 2) {
                     this.sendMessage(sender, new String[]{LanguageConfig.setActiveUsage.getValue()});
                     return;
                  }

                  IKnockback active = manager.getByName(args[1]);
                  if (active == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.profileNotFound.getValue().replace("%name%", args[1])});
                     return;
                  }

                  if (manager.getActiveProfile().getName().equalsIgnoreCase(active.getName())) {
                     this.sendMessage(sender, new String[]{LanguageConfig.setActiveAlready.getValue().replace("%name%", active.getName())});
                     return;
                  }

                  manager.setActiveProfile(active);
                  this.sendMessage(sender, new String[]{LanguageConfig.setActive.getValue().replace("%name%", active.getName())});
                  return;
               }
               break;
            case 1602776681:
               if (var4.equals("editmenu")) {
                  if (args.length < 2) {
                     this.sendMessage(sender, new String[]{LanguageConfig.editMenuUsage.getValue()});
                     return;
                  }

                  if (!(sender instanceof Player)) {
                     this.sendMessage(sender, new String[]{LanguageConfig.onlyPlayer.getValue()});
                     return;
                  }

                  Player opening = (Player)sender;
                  Knockback editMenu = (Knockback)manager.getByName(args[1]);
                  if (editMenu == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.profileNotFound.getValue().replace("%name%", args[1])});
                     return;
                  }

                  new ConfigEditMenu(opening, editMenu, null, editMenu.getActiveSettings()).open();
                  return;
               }
               break;
            case 1985956508:
               if (var4.equals("settype")) {
                  if (args.length < 2) {
                     this.sendMessage(sender, new String[]{LanguageConfig.setTypeUsage.getValue()});
                     return;
                  }

                  Knockback changingType = (Knockback)manager.getByName(args[1]);
                  if (changingType == null) {
                     this.sendMessage(sender, new String[]{LanguageConfig.profileNotFound.getValue().replace("%name%", args[1])});
                     return;
                  }

                  if (args.length == 2) {
                     new FancyMessage(LanguageConfig.setTypePrefix.getValue())
                        .then(LanguageConfig.setTypeSimple.getValue())
                        .tooltip(LanguageConfig.setTypeSimpleHover.getValue())
                        .command("/knockback settype " + changingType.getName() + " SIMPLE")
                        .then(" ")
                        .then(LanguageConfig.setTypeAdvanced.getValue())
                        .tooltip(LanguageConfig.setTypeAdvancedHover.getValue())
                        .command("/knockback settype " + changingType.getName() + " ADVANCED")
                        .send(sender);
                     return;
                  }

                  KnockbackType type;
                  try {
                     type = KnockbackType.valueOf(args[2]);
                  } catch (IllegalArgumentException var20) {
                     this.sendMessage(sender, new String[]{LanguageConfig.setTypeNotFound.getValue()});
                     return;
                  }

                  if (changingType.getKnockbackType() == type) {
                     this.sendMessage(
                        sender,
                        new String[]{
                           LanguageConfig.setTypeAlready.getValue().replace("%name%", changingType.getName()).replace("%type%", type.getDisplayName())
                        }
                     );
                     return;
                  }

                  changingType.knockbackType.set(type.name(), true);
                  this.sendMessage(
                     sender, new String[]{LanguageConfig.setType.getValue().replace("%name%", changingType.getName()).replace("%type%", type.getDisplayName())}
                  );
                  return;
               }
         }

         this.sendUsage(sender);
      }
   }

   private void sendUsage(CommandSender sender) {
      for (String s : LanguageConfig.knockbackUsage.getValue()) {
         if (!s.equalsIgnoreCase("%commands%")) {
            this.sendMessage(sender, new String[]{s});
         } else {
            Arrays.asList(
                  new FancyMessage(LanguageConfig.createUsage.getValue()).tooltip(LanguageConfig.createHover.getValue()).suggest("/knockback create "),
                  new FancyMessage(LanguageConfig.deleteUsage.getValue()).tooltip(LanguageConfig.deleteHover.getValue()).suggest("/knockback delete "),
                  new FancyMessage(LanguageConfig.viewUsage.getValue()).tooltip(LanguageConfig.viewHover.getValue()).suggest("/knockback view "),
                  new FancyMessage(LanguageConfig.editUsage.getValue()).tooltip(LanguageConfig.editHover.getValue()).suggest("/knockback edit "),
                  new FancyMessage(LanguageConfig.editMenuUsage.getValue()).tooltip(LanguageConfig.editMenuHover.getValue()).suggest("/knockback editmenu "),
                  new FancyMessage(LanguageConfig.setUsage.getValue()).tooltip(LanguageConfig.setHover.getValue()).suggest("/knockback set "),
                  new FancyMessage(LanguageConfig.clearUsage.getValue()).tooltip(LanguageConfig.clearHover.getValue()).suggest("/knockback clear "),
                  new FancyMessage(LanguageConfig.setTypeUsage.getValue()).tooltip(LanguageConfig.setTypeHover.getValue()).suggest("/knockback settype "),
                  new FancyMessage(LanguageConfig.setActiveUsage.getValue()).tooltip(LanguageConfig.setActiveHover.getValue()).suggest("/knockback setactive "),
                  new FancyMessage(LanguageConfig.listUsage.getValue()).tooltip(LanguageConfig.listHover.getValue()).command("/knockback list")
               )
               .forEach(message -> message.send(sender));
         }
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String[] args) {
      if (args[0].equalsIgnoreCase("create")) {
         return null;
      } else if (args[0].equalsIgnoreCase("clear")) {
         return super.tabComplete(sender, args);
      } else if (args[0].equalsIgnoreCase("settype") && args.length == 3) {
         String string = args[args.length - 1];
         return Arrays.stream(KnockbackType.values())
            .map(Enum::name)
            .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
            .collect(Collectors.toList());
      } else {
         if (args[0].equalsIgnoreCase("edit") && args.length == 3) {
            IKnockback knockback = Blaze.getInstance().getKnockbackManager().getByName(args[1]);
            if (knockback != null) {
               String string = args[args.length - 1];
               return knockback.getValues().keySet().stream().filter(s -> s.regionMatches(true, 0, string, 0, string.length())).collect(Collectors.toList());
            }
         }

         if (args.length == 1) {
            String string = args[args.length - 1];
            return Stream.of("delete", "create", "setactive", "list", "edit", "editmenu", "set", "view", "clear")
               .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
               .collect(Collectors.toList());
         } else if (args.length == 2) {
            String string = args[args.length - 1];
            return Blaze.getInstance()
               .getKnockbackManager()
               .getProfiles()
               .stream()
               .map(IKnockback::getName)
               .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
               .collect(Collectors.toList());
         } else {
            return super.tabComplete(sender, args);
         }
      }
   }
}
