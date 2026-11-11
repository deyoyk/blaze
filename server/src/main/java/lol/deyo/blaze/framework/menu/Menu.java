package lol.deyo.blaze.framework.menu;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.framework.menu.button.Button;
import lol.deyo.blaze.utils.CC;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Menu {
   protected Map<Integer, Button> buttons;
   protected Player player;
   protected Inventory inventory;
   protected String title;
   protected int size;
   protected boolean update;
   protected ItemStack filler;
   protected boolean fillEnabled;
   protected boolean allowInteract;

   public Menu(Player player, String title, int size, boolean update) {
      this.player = player;
      this.title = CC.t(title);
      this.size = size;
      this.inventory = Bukkit.createInventory(null, size, this.title);
      this.update = update;
      this.fillEnabled = false;
      this.allowInteract = false;
   }

   public Map<Integer, Button> getButtons() {
      return this.buttons;
   }

   public void playClick() {
      this.player.playSound(Sound.CLICK, 1.0F, 1.0F);
   }

   public void open() {
      this.buttons = this.getButtons(this.player);

      for (Entry<Integer, Button> entry : this.buttons.entrySet()) {
         this.inventory.setItem(entry.getKey() - 1, entry.getValue().getItemStack());
      }

      if (this.fillEnabled) {
         for (int i = 0; i < this.inventory.getSize(); i++) {
            ItemStack item = this.inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
               this.inventory.setItem(i, this.filler);
            }
         }
      }

      this.player.openInventory(this.inventory);
      Blaze.getInstance().getMenuManager().getMenus().put(this.player.getUniqueId(), this);
   }

   public void update() {
      if (!this.player.isOnline()) {
         this.destroy();
      } else {
         Map<Integer, Button> buttons = this.buttons = this.getButtons(this.player);

         for (Entry<Integer, Button> entry : buttons.entrySet()) {
            this.inventory.setItem(entry.getKey() - 1, entry.getValue().getItemStack());
         }

         if (this.fillEnabled) {
            for (int i = 0; i < this.inventory.getSize(); i++) {
               ItemStack item = this.inventory.getItem(i);
               if (item == null || item.getType() == Material.AIR) {
                  this.inventory.setItem(i, this.filler);
               }
            }
         }
      }
   }

   public void destroy() {
      this.buttons.clear();
      this.inventory.clear();
      Blaze.getInstance().getMenuManager().getMenus().remove(this.player.getUniqueId());
      this.update = false;
   }

   public void onClick(InventoryClickEvent e) {
      if (!this.allowInteract) {
         e.setCancelled(true);
      }
   }

   public void onClickOwn(InventoryClickEvent e) {
   }

   public void onClose() {
   }

   public abstract Map<Integer, Button> getButtons(Player var1);
}
