package lol.deyo.blaze.framework.menu;

import lol.deyo.blaze.framework.menu.button.Button;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MenuManager {
   private final Map<UUID, Menu> menus = new HashMap<>();

   public Map<UUID, Menu> getMenus() {
      return this.menus;
   }

   public void tick() {
      if (!this.menus.isEmpty()) {
         try {
            if (MinecraftServer.currentTick % 500 == 0) {
               for (Menu menu : this.menus.values()) {
                  if (menu.update) {
                     menu.update();
                  }
               }
            }
         } catch (Exception var3) {
            MinecraftServer.LOGGER.info("Error updating blaze menus {}", new Object[]{var3.getMessage()});
         }
      }
   }

   public void handleClick(InventoryClickEvent e) {
      if (e.getWhoClicked() instanceof Player) {
         if (e.getClickedInventory() != null) {
            Player player = (Player)e.getWhoClicked();
            Menu menu = this.menus.get(player.getUniqueId());
            if (menu != null) {
               if (!menu.allowInteract) {
                  e.setCancelled(true);
               }

               menu.onClick(e);
               if (e.getClickedInventory() != player.getInventory()) {
                  Button button = menu.getButtons().get(e.getSlot() + 1);
                  if (button != null) {
                     button.onClick(e);
                  }
               }

               if (e.getClickedInventory() == player.getInventory()) {
                  menu.onClickOwn(e);
               }
            }
         }
      }
   }

   public void handleJoin(PlayerJoinEvent e) {
      Player player = e.getPlayer();
      Menu menu = this.menus.get(player.getUniqueId());
      if (menu != null) {
         menu.onClose();
         menu.destroy();
      }
   }

   public void handleInventoryClose(InventoryCloseEvent e) {
      Player player = (Player)e.getPlayer();
      Menu menu = this.menus.remove(player.getUniqueId());
      if (menu != null) {
         menu.onClose();
         menu.destroy();
      }
   }
}
