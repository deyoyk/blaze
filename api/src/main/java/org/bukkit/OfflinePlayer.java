package org.bukkit;

import java.util.UUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

public interface OfflinePlayer extends ServerOperator, AnimalTamer, ConfigurationSerializable {
   boolean isOnline();

   @Override
   String getName();

   @Override
   UUID getUniqueId();

   boolean isBanned();

   @Deprecated
   void setBanned(boolean var1);

   boolean isWhitelisted();

   void setWhitelisted(boolean var1);

   Player getPlayer();

   long getFirstPlayed();

   long getLastPlayed();

   boolean hasPlayedBefore();

   org.bukkit.Location getBedSpawnLocation();
}
