package lol.deyo.blaze.configs.extra;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.framework.menu.Menu;
import lol.deyo.blaze.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ConfigEdit {
   private final ConfigSetting<?> setting;
   private final Menu menu;

   public ConfigEdit(ConfigSetting<?> setting, Menu menu) {
      this.setting = setting;
      this.menu = menu;
   }

   public void handleChat(AsyncPlayerChatEvent e) {
      String message = e.getMessage();
      Player player = e.getPlayer();
      e.setCancelled(true);
      if (message.equalsIgnoreCase("cancel")) {
         Blaze.getInstance().getConfigEdits().remove(player.getUniqueId());
         CC.sendMessage(player, LanguageConfig.configEditCancelled.getValue().replace("%setting%", this.setting.getName()));
      } else {
         Object value = this.setting.parseValue(message);
         if (value == null) {
            CC.sendMessage(
               player, LanguageConfig.notValidType.getValue().replace("%value%", message).replace("%type%", this.setting.getValue().getClass().getSimpleName())
            );
         } else {
            Blaze.getInstance().getConfigEdits().remove(player.getUniqueId());
            this.setting.set(value, true, false);
            this.menu.open();
            Blaze.getInstance().reload();
            CC.sendMessage(
               player, LanguageConfig.configEditSetValue.getValue().replace("%setting%", this.setting.getName()).replace("%value%", value.toString())
            );
         }
      }
   }
}
