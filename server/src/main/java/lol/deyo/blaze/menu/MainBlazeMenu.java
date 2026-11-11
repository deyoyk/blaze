package lol.deyo.blaze.menu;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.framework.menu.Menu;
import lol.deyo.blaze.framework.menu.button.Button;
import lol.deyo.blaze.menu.extra.ProfileType;
import lol.deyo.blaze.utils.ItemBuilder;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MainBlazeMenu extends Menu {
   public MainBlazeMenu(Player player) {
      super(player, "&dSpigot Configurations", 45, false);
      this.fillEnabled = true;
      this.filler = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("&7").data(7).toItemStack();
   }

   @Override
   public Map<Integer, Button> getButtons(final Player player) {
      Map<Integer, Button> map = new HashMap<>();
      final Blaze instance = Blaze.getInstance();
      map.put(
         11,
         new Button() {
            @Override
            public void onClick(InventoryClickEvent e) {
               e.setCancelled(true);
               new ProfileListMenu(player, ProfileType.KNOCKBACK).open();
               MainBlazeMenu.this.playClick();
            }

            @Override
            public ItemStack getItemStack() {
               return new ItemBuilder(Material.DIAMOND_SWORD)
                  .setName("&4Knockback Profiles")
                  .setLore("&eClick to edit knockback profiles.")
                  .hideAttributes()
                  .toItemStack();
            }
         }
      );
      map.put(12, new Button() {
         @Override
         public void onClick(InventoryClickEvent e) {
            e.setCancelled(true);
            new ConfigEditMenu(player, instance.getPearlConfig()).open();
            MainBlazeMenu.this.playClick();
         }

         @Override
         public ItemStack getItemStack() {
            return new ItemBuilder(Material.ENDER_PEARL).setName("&5Enderpearl Config").setLore("&eClick to edit enderpearl config.").toItemStack();
         }
      });
      map.put(14, new Button() {
         @Override
         public void onClick(InventoryClickEvent e) {
            e.setCancelled(true);
            new ConfigEditMenu(player, instance.getBlazeConfig()).open();
            MainBlazeMenu.this.playClick();
         }

         @Override
         public ItemStack getItemStack() {
            return new ItemBuilder(Material.BOOK).setName("&dMain Blaze Config").setLore("&eClick to edit the main blaze config.").toItemStack();
         }
      });
      map.put(
         16,
         new Button() {
            @Override
            public void onClick(InventoryClickEvent e) {
               e.setCancelled(true);
               new ConfigEditMenu(player, instance.getEnchantConfig()).open();
               MainBlazeMenu.this.playClick();
            }

            @Override
            public ItemStack getItemStack() {
               return new ItemBuilder(Material.ENCHANTMENT_TABLE)
                  .setName("&bEnchant Limit Config")
                  .setLore("&eClick to edit the enchants limit config.")
                  .toItemStack();
            }
         }
      );
      map.put(17, new Button() {
         @Override
         public void onClick(InventoryClickEvent e) {
            e.setCancelled(true);
            new ProfileListMenu(player, ProfileType.GENERATOR).open();
            MainBlazeMenu.this.playClick();
         }

         @Override
         public ItemStack getItemStack() {
            return new ItemBuilder(Material.GRASS).setName("&aWorld Generator Profiles").setLore("&eClick to edit world generator profiles.").toItemStack();
         }
      });
      map.put(32, new Button() {
         @Override
         public void onClick(InventoryClickEvent e) {
            e.setCancelled(true);
            player.closeInventory();
            MainBlazeMenu.this.playClick();
         }

         @Override
         public ItemStack getItemStack() {
            return new ItemBuilder(Material.BED).setName("&cClose").toItemStack();
         }
      });
      return map;
   }
}
