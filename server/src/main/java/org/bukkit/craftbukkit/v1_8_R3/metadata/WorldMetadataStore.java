package org.bukkit.craftbukkit.v1_8_R3.metadata;

import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;

public class WorldMetadataStore extends MetadataStoreBase<org.bukkit.World> implements MetadataStore<org.bukkit.World> {
   protected String disambiguate(org.bukkit.World world, String metadataKey) {
      return world.getUID().toString() + ":" + metadataKey;
   }
}
