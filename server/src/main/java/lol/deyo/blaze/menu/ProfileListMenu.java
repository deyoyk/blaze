package lol.deyo.blaze.menu;

import lol.deyo.blaze.framework.menu.button.Button;
import lol.deyo.blaze.framework.menu.paginated.PaginatedMenu;
import lol.deyo.blaze.menu.extra.ProfileType;
import lol.deyo.blaze.utils.ItemBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ProfileListMenu extends PaginatedMenu {
   private final ProfileType profileType;

   public ProfileListMenu(Player player, ProfileType profileType) {
      super(player, profileType.getTitle(), new MainBlazeMenu(player), 18, true, false);
      this.profileType = profileType;
   }

   @Override
   public Map<Integer, Button> getButtons(final Player player) {
      Map<Integer, Button> map = new HashMap<>();
      List<String> profiles = new ArrayList<>();
      int i = 1;
      this.profileType.addProfiles(profiles);

      for (final String profile : profiles) {
         map.put(
            i++,
            new Button() {
               @Override
               public void onClick(InventoryClickEvent e) {
                  e.setCancelled(true);
                  ProfileListMenu.this.playClick();
                  ProfileListMenu.this.profileType.openEdit(player, profile, new ProfileListMenu(player, ProfileListMenu.this.profileType));
               }

               @Override
               public ItemStack getItemStack() {
                  return new ItemBuilder(Material.BOOK)
                     .setName(ProfileListMenu.this.profileType.getColor() + profile)
                     .setLore("&eClick to edit this profile.")
                     .toItemStack();
               }
            }
         );
      }

      return map;
   }
}
