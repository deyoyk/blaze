package lol.deyo.blaze.framework.menu.paginated;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.framework.menu.Menu;
import lol.deyo.blaze.framework.menu.button.Button;
import lol.deyo.blaze.utils.ItemBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PaginatedMenu extends Menu {
   protected Map<Integer, Map<Integer, Button>> pages;
   protected Map<Integer, Button> defaultButtons;
   protected Menu previousMenu;
   protected int currentPage;
   protected boolean surroundGlass;

   public PaginatedMenu(Player player, String title, int size, boolean update) {
      super(player, title, size, update);
      this.surroundGlass = false;
      this.pages = new HashMap<>();
      this.defaultButtons = this.getDefaultButtons();
      this.previousMenu = null;
      this.currentPage = 1;
   }

   public PaginatedMenu(Player player, String title, int size, boolean update, boolean surroundGlass) {
      super(player, title, size, update);
      this.surroundGlass = surroundGlass;
      this.pages = new HashMap<>();
      this.defaultButtons = this.getDefaultButtons();
      this.previousMenu = null;
      this.currentPage = 1;
   }

   public PaginatedMenu(Player player, String title, Menu previousMenu, int size, boolean update, boolean surroundGlass) {
      super(player, title, size, update);
      this.pages = new HashMap<>();
      this.previousMenu = previousMenu;
      this.surroundGlass = surroundGlass;
      this.defaultButtons = this.getDefaultButtons();
      this.currentPage = 1;
   }

   @Override
   public void open() {
      this.calcPages(this.getButtons(this.player));
      this.buttons = this.pages.get(this.currentPage);
      this.inventory = Bukkit.createInventory(null, this.size, this.title);

      for (Entry<Integer, Button> entry : this.buttons.entrySet()) {
         this.inventory.setItem(entry.getKey() - 1, entry.getValue().getItemStack());
      }

      this.player.openInventory(this.inventory);
      Blaze.getInstance().getMenuManager().getMenus().put(this.player.getUniqueId(), this);
   }

   @Override
   public void update() {
      if (!this.player.isOnline()) {
         this.destroy();
      } else {
         this.inventory.clear();
         this.pages.clear();
         this.calcPages(this.getButtons(this.player));
         this.buttons = this.pages.get(this.currentPage);

         for (Entry<Integer, Button> entry : this.buttons.entrySet()) {
            this.inventory.setItem(entry.getKey() - 1, entry.getValue().getItemStack());
         }
      }
   }

   @Override
   public void destroy() {
      super.destroy();
      this.pages.clear();
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      return null;
   }

   private void calcPages(Map<Integer, Button> buttons) {
      if (buttons.isEmpty()) {
         this.pages.computeIfAbsent(1, k -> new HashMap<>(this.defaultButtons));
      } else {
         this.handleButtons(buttons);
      }
   }

   private void handleButtons(Map<Integer, Button> buttons) {
      Set<Integer> exceptions = new HashSet<>();

      for (int i = 1; i <= 9; i++) {
         exceptions.add(i);
      }

      if (this.surroundGlass) {
         for (int i = 10; i <= this.size; i++) {
            if (i % 9 == 0 || (i - 1) % 9 == 0) {
               exceptions.add(i);
            }
         }
      }

      int counter = 9;
      int page = 1;
      int bottomSize = this.surroundGlass ? this.size - 9 : this.size;
      int maxPerPage = this.size - (exceptions.size() + (this.surroundGlass ? 9 : 0));
      int totalPages = buttons.size() < maxPerPage ? 1 : buttons.size() / maxPerPage;

      for (int ix = 1; ix <= this.size * totalPages; ix++) {
         Button button = buttons.get(ix);
         if (button != null) {
            if (counter >= bottomSize) {
               counter = 9;
               page++;
            }

            while (exceptions.contains(counter)) {
               if (++counter >= bottomSize) {
                  counter = 9;
                  page++;
               }
            }

            Map<Integer, Button> pageButtons = this.pages.computeIfAbsent(page, k -> new HashMap<>(this.defaultButtons));
            pageButtons.put(counter++, button);
         }
      }
   }

   private Map<Integer, Button> getDefaultButtons() {
      Map<Integer, Button> map = new HashMap<>();
      if (BlazeConfig.fillerEnabled.getValue()) {
         for (int i = 2; i <= 8; i++) {
            map.put(
               i,
               new Button() {
                  @Override
                  public void onClick(InventoryClickEvent e) {
                     e.setCancelled(true);
                  }

                  @Override
                  public ItemStack getItemStack() {
                     return new ItemBuilder(Material.valueOf(BlazeConfig.fillerType.getValue()))
                        .setName("&7")
                        .data(BlazeConfig.fillerData.getValue())
                        .toItemStack();
                  }
               }
            );
         }

         if (this.surroundGlass) {
            Button button = new Button() {
               @Override
               public void onClick(InventoryClickEvent e) {
                  e.setCancelled(true);
               }

               @Override
               public ItemStack getItemStack() {
                  return new ItemBuilder(Material.valueOf(BlazeConfig.fillerType.getValue()))
                     .setName("&7")
                     .data(BlazeConfig.fillerData.getValue())
                     .toItemStack();
               }
            };

            for (int i = 9; i <= this.size; i++) {
               if (i >= this.size - 9) {
                  map.put(i, button);
               } else if (i % 9 == 0) {
                  map.put(i, button);
                  map.put(i + 1, button);
               }
            }
         }
      }

      map.put(
         1,
         new Button() {
            @Override
            public void onClick(InventoryClickEvent e) {
               e.setCancelled(true);
               if (PaginatedMenu.this.currentPage > 1) {
                  PaginatedMenu.this.currentPage--;
                  PaginatedMenu.this.playClick();
                  PaginatedMenu.this.update();
               }
            }

            @Override
            public ItemStack getItemStack() {
               UnaryOperator<String> replacer = s -> s.replace("%page%", String.valueOf(PaginatedMenu.this.currentPage))
                  .replace("%max-pages%", String.valueOf(PaginatedMenu.this.pages.size()))
                  .replace("%back-page%", PaginatedMenu.this.currentPage == 1 ? "None" : String.valueOf(PaginatedMenu.this.currentPage - 1));
               return new ItemBuilder(Material.valueOf(BlazeConfig.pageType.getValue()))
                  .setName(BlazeConfig.pageBackDisplay.getValue())
                  .setLore(BlazeConfig.pageBackLore.getValue().stream().map(replacer).collect(Collectors.toList()))
                  .data(BlazeConfig.pageData.getValue())
                  .toItemStack();
            }
         }
      );
      if (this.previousMenu != null) {
         map.put(
            this.surroundGlass ? this.size - 4 : 5,
            new Button() {
               @Override
               public void onClick(InventoryClickEvent e) {
                  e.setCancelled(true);
                  PaginatedMenu.this.playClick();
                  PaginatedMenu.this.previousMenu.open();
               }

               @Override
               public ItemStack getItemStack() {
                  return new ItemBuilder(Material.valueOf(BlazeConfig.backButtonType.getValue()))
                     .setName(BlazeConfig.backButtonDisplay.getValue())
                     .data(BlazeConfig.backButtonData.getValue())
                     .toItemStack();
               }
            }
         );
      }

      map.put(
         9,
         new Button() {
            @Override
            public void onClick(InventoryClickEvent e) {
               e.setCancelled(true);
               if (PaginatedMenu.this.pages.containsKey(PaginatedMenu.this.currentPage + 1)) {
                  PaginatedMenu.this.currentPage++;
                  PaginatedMenu.this.playClick();
                  PaginatedMenu.this.update();
               }
            }

            @Override
            public ItemStack getItemStack() {
               UnaryOperator<String> replacer = s -> s.replace("%page%", String.valueOf(PaginatedMenu.this.currentPage))
                  .replace("%max-pages%", String.valueOf(PaginatedMenu.this.pages.size()))
                  .replace(
                     "%next-page%",
                     PaginatedMenu.this.pages.size() == PaginatedMenu.this.currentPage ? "None" : String.valueOf(PaginatedMenu.this.currentPage + 1)
                  );
               return new ItemBuilder(Material.valueOf(BlazeConfig.pageType.getValue()))
                  .setName(BlazeConfig.pageForwardDisplay.getValue())
                  .setLore(BlazeConfig.pageForwardLore.getValue().stream().map(replacer).collect(Collectors.toList()))
                  .data(BlazeConfig.pageData.getValue())
                  .toItemStack();
            }
         }
      );
      return map;
   }
}
