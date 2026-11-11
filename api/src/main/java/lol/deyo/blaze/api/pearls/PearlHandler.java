package lol.deyo.blaze.api.pearls;

import org.bukkit.entity.Player;

public interface PearlHandler {
   default boolean hasCooldown(Player player) {
      return false;
   }
}
