package lol.deyo.blaze.framework.menu.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Button {
   public abstract void onClick(InventoryClickEvent var1);

   public abstract ItemStack getItemStack();
}
