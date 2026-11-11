package lol.deyo.blaze.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
   private final ItemStack is;

   public ItemBuilder(Material m) {
      this(m, 1);
   }

   public ItemBuilder(ItemStack is) {
      this.is = is;
   }

   public ItemBuilder(Material m, int amount) {
      this.is = new ItemStack(m, amount);
   }

   public ItemBuilder(Material m, int amount, byte durability) {
      this.is = new ItemStack(m, amount, durability);
   }

   public ItemBuilder clone() {
      return new ItemBuilder(this.is);
   }

   public ItemBuilder setName(String name) {
      ItemMeta im = this.is.getItemMeta();
      im.setDisplayName(CC.t(name));
      this.is.setItemMeta(im);
      return this;
   }

   public String getName() {
      ItemMeta im = this.is.getItemMeta();
      String string = im.getDisplayName();
      return string != null ? string : "";
   }

   public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
      this.is.addUnsafeEnchantment(ench, level);
      return this;
   }

   public ItemBuilder data(int data) {
      this.is.setDurability((short)data);
      return this;
   }

   public ItemBuilder removeEnchantment(Enchantment ench) {
      this.is.removeEnchantment(ench);
      return this;
   }

   public ItemBuilder setSkullOwner(String owner) {
      if (!(this.is.getItemMeta() instanceof SkullMeta)) {
         return this;
      } else {
         try {
            SkullMeta im = (SkullMeta)this.is.getItemMeta();
            im.setOwner(owner);
            this.is.setItemMeta(im);
         } catch (ClassCastException var3) {
         }

         return this;
      }
   }

   public ItemBuilder hideAttributes() {
      ItemMeta im = this.is.getItemMeta();
      im.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      this.is.setItemMeta(im);
      return this;
   }

   public ItemBuilder addEnchant(Enchantment ench, int level) {
      ItemMeta im = this.is.getItemMeta();
      im.addEnchant(ench, level, true);
      this.is.setItemMeta(im);
      return this;
   }

   public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
      this.is.addEnchantments(enchantments);
      return this;
   }

   public ItemBuilder setInfinityDurability() {
      this.is.setDurability((short)32767);
      return this;
   }

   public ItemBuilder setLore(String... lore) {
      ItemMeta im = this.is.getItemMeta();
      im.setLore(CC.t(Arrays.asList(lore)));
      this.is.setItemMeta(im);
      return this;
   }

   public ItemBuilder setLore(List<String> lore) {
      ItemMeta im = this.is.getItemMeta();
      im.setLore(CC.t(lore));
      this.is.setItemMeta(im);
      return this;
   }

   public ItemBuilder removeLoreLine(String line) {
      ItemMeta im = this.is.getItemMeta();
      List<String> lore = new ArrayList<>(im.getLore());
      if (!lore.contains(line)) {
         return this;
      } else {
         lore.remove(line);
         im.setLore(lore);
         this.is.setItemMeta(im);
         return this;
      }
   }

   public ItemBuilder removeLoreLine(int index) {
      ItemMeta im = this.is.getItemMeta();
      List<String> lore = new ArrayList<>(im.getLore());
      if (index >= 0 && index <= lore.size()) {
         lore.remove(index);
         im.setLore(lore);
         this.is.setItemMeta(im);
         return this;
      } else {
         return this;
      }
   }

   public ItemBuilder addLoreLine(String line) {
      ItemMeta im = this.is.getItemMeta();
      List<String> lore = new ArrayList<>();
      if (this.is.hasItemMeta() && this.is.getItemMeta().hasLore()) {
         lore = new ArrayList<>(im.getLore());
      }

      lore.add(CC.t(line));
      im.setLore(lore);
      this.is.setItemMeta(im);
      return this;
   }

   public List<String> getLore() {
      return this.is.hasItemMeta() && this.is.getItemMeta().hasLore() ? this.is.getItemMeta().getLore() : Collections.emptyList();
   }

   public ItemBuilder addLoreLine(String line, int pos) {
      ItemMeta im = this.is.getItemMeta();
      List<String> lore = new ArrayList<>(im.getLore());
      lore.set(pos, line);
      im.setLore(lore);
      this.is.setItemMeta(im);
      return this;
   }

   public ItemBuilder setLeatherArmorColor(Color color) {
      try {
         LeatherArmorMeta im = (LeatherArmorMeta)this.is.getItemMeta();
         im.setColor(color);
         this.is.setItemMeta(im);
      } catch (ClassCastException var3) {
      }

      return this;
   }

   public ItemStack toItemStack() {
      return this.is;
   }
}
