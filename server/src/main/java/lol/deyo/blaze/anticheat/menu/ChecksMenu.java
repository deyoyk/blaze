package lol.deyo.blaze.anticheat.menu;

import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatGuiConfig;
import lol.deyo.blaze.framework.menu.Menu;
import lol.deyo.blaze.framework.menu.button.Button;
import lol.deyo.blaze.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChecksMenu extends Menu {
   private final String targetName;
   private final ACPlayer data;

   public ChecksMenu(Player player, String targetName, ACPlayer data) {
      super(player, ((String)AnticheatGuiConfig.checksTitle.getValue()).replace("%player%", targetName), 45, true);
      this.targetName = targetName;
      this.data = data;
      this.filler = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("&7").data(7).toItemStack();
      this.fillEnabled = true;
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> buttons = new HashMap<>();
      int slot = 10;
      for (int i = 0; i < this.data.violations.size(); i++) {
         if (slot > 34) {
            break;
         }

         String checkName = this.data.violations.nameAt(i);
         double vl = this.data.violations.levelAt(i);
         int violations = this.data.violations.violationCountAt(i);
         buttons.put(slot + 1, this.checkButton(checkName, vl, violations));
         slot++;
         if (slot % 9 == 8) {
            slot++;
         }
      }

      buttons.put(40, this.punishmentsButton());
      return buttons;
   }

   private Button checkButton(String checkName, double vl, int violations) {
      String status = vl > 0.0
         ? (String)AnticheatGuiConfig.checksStatusFlagged.getValue()
         : (String)AnticheatGuiConfig.checksStatusClean.getValue();
      String nameFormat = ((String)AnticheatGuiConfig.checksItemName.getValue()).replace("%check%", checkName);
      List<String> lore = ((List<String>)AnticheatGuiConfig.checksItemLore.getValue()).stream()
         .map(line -> line.replace("%check%", checkName)
            .replace("%vl%", String.format("%.1f", vl))
            .replace("%violations%", String.valueOf(violations))
            .replace("%status%", status))
         .collect(Collectors.toList());

      Material material = vl > 0.0
         ? Material.valueOf((String)AnticheatGuiConfig.checksItemType.getValue())
         : Material.valueOf((String)AnticheatGuiConfig.checksCleanItemType.getValue());
      ItemBuilder builder = new ItemBuilder(material).setName(nameFormat).setLore(lore);

      return new Button() {
         @Override
         public void onClick(InventoryClickEvent e) {
            e.setCancelled(true);
         }

         @Override
         public org.bukkit.inventory.ItemStack getItemStack() {
            return builder.toItemStack();
         }
      };
   }

   private Button punishmentsButton() {
      ItemBuilder builder = new ItemBuilder(Material.BOOK)
         .setName("&6Punishments")
         .setLore("&7View anticheat punishments for &f" + this.targetName);

      return new Button() {
         @Override
         public void onClick(InventoryClickEvent e) {
            e.setCancelled(true);
            new PunishmentsMenu(ChecksMenu.this.player, ChecksMenu.this.targetName).open();
         }

         @Override
         public org.bukkit.inventory.ItemStack getItemStack() {
            return builder.toItemStack();
         }
      };
   }
}
