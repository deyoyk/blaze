package lol.deyo.blaze.anticheat.menu;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.AnticheatGuiConfig;
import lol.deyo.blaze.anticheat.PunishmentManager;
import lol.deyo.blaze.framework.menu.Menu;
import lol.deyo.blaze.framework.menu.button.Button;
import lol.deyo.blaze.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PunishmentsMenu extends Menu {
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
   private final String targetName;

   public PunishmentsMenu(Player player, String targetName) {
      super(player, ((String)AnticheatGuiConfig.punishmentsTitle.getValue())
         .replace("%player%", targetName == null ? "all" : targetName), 54, true);
      this.targetName = targetName;
      this.filler = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("&7").data(7).toItemStack();
      this.fillEnabled = true;
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> buttons = new HashMap<>();
      List<PunishmentManager.Record> records = this.targetName != null
         ? ACManager.INSTANCE.getPunishments().getPunishmentsByName(this.targetName)
         : ACManager.INSTANCE.getPunishments().getActiveBans();

      int slot = 10;
      for (PunishmentManager.Record record : records) {
         if (slot > 43) {
            break;
         }

         buttons.put(slot + 1, this.recordButton(record));
         slot++;
         if (slot % 9 == 8) {
            slot++;
         }
      }

      if (this.targetName != null) {
         buttons.put(49, this.allButton());
      }

      return buttons;
   }

   private Button recordButton(PunishmentManager.Record record) {
      String status = record.active
         ? (String)AnticheatGuiConfig.punishmentsStatusActive.getValue()
         : (String)AnticheatGuiConfig.punishmentsStatusLifted.getValue();
      String expires = record.expires > 0 ? DATE_FORMAT.format(new Date(record.expires)) : "permanent";
      String nameFormat = ((String)AnticheatGuiConfig.punishmentsItemName.getValue()).replace("%id%", String.valueOf(record.id));
      List<String> lore = ((List<String>)AnticheatGuiConfig.punishmentsItemLore.getValue()).stream()
         .map(line -> line.replace("%id%", String.valueOf(record.id))
            .replace("%player%", record.name)
            .replace("%check%", record.check)
            .replace("%action%", record.type.name())
            .replace("%issued%", DATE_FORMAT.format(new Date(record.createdAt)))
            .replace("%expires%", expires)
            .replace("%status%", status))
         .collect(Collectors.toList());

      ItemBuilder builder = new ItemBuilder(Material.valueOf((String)AnticheatGuiConfig.punishmentsItemType.getValue()))
         .setName(nameFormat)
         .setLore(lore);

      return new Button() {
         @Override
         public void onClick(InventoryClickEvent e) {
            e.setCancelled(true);
            if (e.getClick() == ClickType.SHIFT_LEFT && record.active && record.type == PunishmentManager.PunishmentType.BAN) {
               String by = PunishmentsMenu.this.player.getName();
               if (ACManager.INSTANCE.getPunishments().unban(record.id, by)) {
                  PunishmentsMenu.this.playClick();
                  PunishmentsMenu.this.update();
               }
            }
         }

         @Override
         public org.bukkit.inventory.ItemStack getItemStack() {
            return builder.toItemStack();
         }
      };
   }

   private Button allButton() {
      ItemBuilder builder = new ItemBuilder(Material.BOOK)
         .setName("&6All punishments")
         .setLore("&7View every active anticheat ban");

      return new Button() {
         @Override
         public void onClick(InventoryClickEvent e) {
            e.setCancelled(true);
            new PunishmentsMenu(PunishmentsMenu.this.player, null).open();
         }

         @Override
         public org.bukkit.inventory.ItemStack getItemStack() {
            return builder.toItemStack();
         }
      };
   }
}
