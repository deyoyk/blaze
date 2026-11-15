package lol.deyo.blaze.anticheat.menu;

import lol.deyo.blaze.anticheat.ACManager;
import lol.deyo.blaze.anticheat.ACPlayer;
import lol.deyo.blaze.anticheat.AnticheatGuiConfig;
import lol.deyo.blaze.framework.menu.Menu;
import lol.deyo.blaze.framework.menu.button.Button;
import lol.deyo.blaze.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnticheatMainMenu extends Menu {
   private static final int SIZE = 54;

   public AnticheatMainMenu(Player player) {
      super(player, (String)AnticheatGuiConfig.mainTitle.getValue(), SIZE, true);
      this.filler = new ItemBuilder(Material.valueOf((String)AnticheatGuiConfig.mainFillerType.getValue()))
         .setName("&7")
         .data((Integer)AnticheatGuiConfig.mainFillerData.getValue())
         .toItemStack();
      this.fillEnabled = true;
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> buttons = new HashMap<>();
      int slot = 10;
      for (Player online : Bukkit.getOnlinePlayers()) {
         if (slot > 43) {
            break;
         }

         ACPlayer data = ACManager.INSTANCE.get(online.getUniqueId());
         buttons.put(slot + 1, this.playerButton(online, data));
         slot++;
         if (slot % 9 == 8) {
            slot++;
         }
      }

      return buttons;
   }

   private Button playerButton(Player target, ACPlayer data) {
      double totalVl = 0.0;
      String topCheck = "none";
      double topVl = 0.0;
      for (int i = 0; i < data.violations.size(); i++) {
         double vl = data.violations.levelAt(i);
         totalVl += vl;
         if (vl > topVl) {
            topVl = vl;
            topCheck = data.violations.nameAt(i);
         }
      }

      final String topCheckFinal = topCheck;

      String nameFormat = ((String)AnticheatGuiConfig.mainPlayerName.getValue()).replace("%player%", target.getName());
      String vlString = String.format("%.1f", totalVl);
      List<String> lore = ((List<String>)AnticheatGuiConfig.mainPlayerLore.getValue()).stream()
         .map(line -> line.replace("%player%", target.getName())
            .replace("%vl%", vlString)
            .replace("%topcheck%", topCheckFinal)
            .replace("%newline%", "\n"))
         .collect(Collectors.toList());

      ItemBuilder builder = new ItemBuilder(Material.valueOf((String)AnticheatGuiConfig.mainPlayerItemType.getValue()))
         .setName(nameFormat)
         .setLore(lore);
      try {
         builder.setSkullOwner(target.getName());
      } catch (Exception ignored) {
      }

      final String targetName = target.getName();
      return new Button() {
         @Override
         public void onClick(InventoryClickEvent e) {
            e.setCancelled(true);
            new ChecksMenu(AnticheatMainMenu.this.player, targetName, data).open();
         }

         @Override
         public org.bukkit.inventory.ItemStack getItemStack() {
            return builder.toItemStack();
         }
      };
   }
}
