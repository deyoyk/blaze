package lol.deyo.blaze.api.protocol;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface MovementHandler {
   void handleUpdateLocation(Player var1, Location var2, Location var3, Object var4);

   void handleBlockUpdateLocation(Player var1, Location var2, Location var3);

   void handleUpdateRotation(Player var1, Location var2, Location var3, Object var4);
}
