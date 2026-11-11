package lol.deyo.blaze.knockback.extra;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.api.knockback.IKnockback;
import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.knockback.KnockbackManager;
import lol.deyo.blaze.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class KnockbackEdit {
   private final String profile;
   private final ConfigSetting<?> setting;

   public KnockbackEdit(String profile, ConfigSetting<?> setting) {
      this.profile = profile;
      this.setting = setting;
   }

   public void handleChat(AsyncPlayerChatEvent e) {
      KnockbackManager manager = Blaze.getInstance().getKnockbackManager();
      String message = e.getMessage();
      Player player = e.getPlayer();
      IKnockback knockback = manager.getByName(this.profile);
      e.setCancelled(true);
      if (message.equalsIgnoreCase("cancel")) {
         manager.getEditing().remove(player.getUniqueId());
         CC.sendMessage(player, LanguageConfig.editCancelled.getValue().replace("%setting%", this.setting.getName()));
      } else if (knockback == null) {
         manager.getEditing().remove(player.getUniqueId());
         CC.sendMessage(player, LanguageConfig.profileNotFound.getValue().replace("%name%", this.profile));
      } else {
         Object value = this.setting.parseValue(message);
         if (value == null) {
            CC.sendMessage(
               player, LanguageConfig.notValidType.getValue().replace("%value%", message).replace("%type%", this.setting.getValue().getClass().getSimpleName())
            );
         } else {
            manager.getEditing().remove(player.getUniqueId());
            this.setting.set(value, true);
            CC.sendMessage(
               player,
               LanguageConfig.editSettingValue
                  .getValue()
                  .replace("%setting%", this.setting.getName())
                  .replace("%value%", value.toString())
                  .replace("%profile%", knockback.getName())
            );
         }
      }
   }
}
