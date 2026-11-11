package org.bukkit.craftbukkit.v1_8_R3.metadata;

import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;

public class PlayerMetadataStore extends MetadataStoreBase<org.bukkit.OfflinePlayer> implements MetadataStore<org.bukkit.OfflinePlayer> {
   protected String disambiguate(org.bukkit.OfflinePlayer player, String metadataKey) {
      return player.getName().toLowerCase() + ":" + metadataKey;
   }
}
