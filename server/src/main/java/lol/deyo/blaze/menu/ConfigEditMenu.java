package lol.deyo.blaze.menu;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigEdit;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.framework.menu.Menu;
import lol.deyo.blaze.framework.menu.button.Button;
import lol.deyo.blaze.framework.menu.paginated.PaginatedMenu;
import lol.deyo.blaze.utils.CC;
import lol.deyo.blaze.utils.ItemBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ConfigEditMenu extends PaginatedMenu {
   private final Config config;
   private final List<ConfigSetting<?>> settings;

   public ConfigEditMenu(final Player player, Config config, Menu previousMenu, List<ConfigSetting<?>> settings) {
      super(player, "&eEditing: &d" + config.getName(), previousMenu, 45, true, true);
      this.config = config;
      this.surroundGlass = true;
      this.settings = settings;
      if (previousMenu == null) {
         this.defaultButtons.put(this.size - 4, new Button() {
            @Override
            public void onClick(InventoryClickEvent e) {
               e.setCancelled(true);
               player.closeInventory();
            }

            @Override
            public ItemStack getItemStack() {
               return new ItemBuilder(Material.BED).setName("&cClose").toItemStack();
            }
         });
      }
   }

   public ConfigEditMenu(Player player, Config config) {
      this(player, config, new MainBlazeMenu(player), config.getSettings());
   }

   @Override
   public Map<Integer, Button> getButtons(final Player player) {
      Map<Integer, Button> map = new HashMap<>();
      int i = 1;

      for (final ConfigSetting<?> setting : this.settings) {
         if (setting.isEditable()) {
            final Object value = setting.getValue();
            map.put(
               i++,
               new Button() {
                  @Override
                  public void onClick(InventoryClickEvent e) {
                     e.setCancelled(true);
                     ConfigEditMenu.this.playClick();
                     if (value instanceof Boolean) {
                        setting.set(!(Boolean)value, true, false);
                        Blaze.getInstance().reload();
                        ConfigEditMenu.this.update();
                     } else if (value instanceof Number) {
                        ConfigEdit configEdit = new ConfigEdit(
                           setting, new ConfigEditMenu(player, ConfigEditMenu.this.config, ConfigEditMenu.this.previousMenu, ConfigEditMenu.this.settings)
                        );
                        Blaze.getInstance().getConfigEdits().put(player.getUniqueId(), configEdit);
                        player.closeInventory();
                        player.sendMessage(
                           CC.t(LanguageConfig.configEditNewValue.getValue())
                              .replace("%setting%", setting.getName())
                              .replace("%current%", setting.getValue().toString())
                        );
                     }
                  }

                  @Override
                  public ItemStack getItemStack() {
                     Material material = Material.BOOK;
                     String name = "&d" + setting.getName();
                     String loreLine = "";
                     int data = 0;
                     if (value instanceof Boolean) {
                        Boolean toggle = (Boolean)value;
                        material = toggle ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
                        name = name + " &6(Status: " + (toggle ? "&aEnabled" : "&cDisabled") + "&6)";
                        loreLine = "&eClick to &6toggle &ethis feature.";
                     } else if (value instanceof Number) {
                        name = name + " &6(Count: &b" + value + "&6)";
                        loreLine = "&eClick to &6edit &ethis feature.";
                     }

                     List<String> lore = new ArrayList<>();
                     lore.add("&7&o(" + setting.getFullPath() + ")");
                     lore.add("");
                     lore.add(loreLine);
                     return new ItemBuilder(material).setName(name).data(data).setLore(lore).toItemStack();
                  }
               }
            );
         }
      }

      return map;
   }
}
