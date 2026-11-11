package org.bukkit.craftbukkit.v1_8_R3.metadata;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class SpecializedBlockMetadataStore implements MetadataStore<Block> {
   private static final int MASK_26_BITS = 67108863;
   private final Map<SpecializedBlockMetadataStore.StoreKey, Map<Plugin, MetadataValue>> metadata = new HashMap<>();
   private final org.bukkit.World owningWorld;
   private final SpecializedBlockMetadataStore.StoreKey cachedKey = new SpecializedBlockMetadataStore.StoreKey(null, 0L);

   public SpecializedBlockMetadataStore(org.bukkit.World owningWorld) {
      this.owningWorld = owningWorld;
   }

   public synchronized List<MetadataValue> getMetadata(Block block, String metadataKey) {
      Preconditions.checkArgument(block.getWorld() == this.owningWorld, "block is from another world");
      List<MetadataValue> list = Collections.emptyList();
      Map<Plugin, MetadataValue> pluginMetadata = this.getEntry(metadataKey, block);
      return (List<MetadataValue>)(pluginMetadata == null ? list : ImmutableList.copyOf(pluginMetadata.values()));
   }

   public synchronized boolean hasMetadata(Block block, String metadataKey) {
      Preconditions.checkArgument(block.getWorld() == this.owningWorld, "block is from another world");
      Map<Plugin, MetadataValue> pluginMetadata = this.getEntry(metadataKey, block);
      return pluginMetadata != null && !pluginMetadata.isEmpty();
   }

   public synchronized void removeMetadata(Block block, String metadataKey, Plugin owningPlugin) {
      Preconditions.checkArgument(block.getWorld() == this.owningWorld, "block is from another world");
      Preconditions.checkNotNull(owningPlugin, "plugin is null");
      long id = getBlockId(block);
      Map<Plugin, MetadataValue> pluginMetadata = this.getEntry(metadataKey, id);
      if (pluginMetadata != null) {
         if (pluginMetadata.remove(owningPlugin) != null && pluginMetadata.isEmpty()) {
            SpecializedBlockMetadataStore.StoreKey key = this.getCachedKey(metadataKey, id);
            this.metadata.remove(key);
            key.metadataKey = null;
         }
      }
   }

   public synchronized void setMetadata(Block block, String metadataKey, MetadataValue newMetadataValue) {
      Preconditions.checkArgument(block.getWorld() == this.owningWorld, "block is from another world");
      Preconditions.checkNotNull(newMetadataValue, "value is null");
      Plugin owningPlugin = newMetadataValue.getOwningPlugin();
      Preconditions.checkNotNull(owningPlugin, "plugin is null");
      Map<Plugin, MetadataValue> entry = this.metadata
         .computeIfAbsent(new SpecializedBlockMetadataStore.StoreKey(metadataKey, getBlockId(block)), k -> new WeakHashMap<>(1));
      entry.put(owningPlugin, newMetadataValue);
   }

   @Override
   public synchronized void invalidateAll(Plugin owningPlugin) {
      Preconditions.checkNotNull(owningPlugin, "plugin is null");

      for (Map<Plugin, MetadataValue> values : this.metadata.values()) {
         MetadataValue value = values.get(owningPlugin);
         if (value != null) {
            value.invalidate();
         }
      }
   }

   private Map<Plugin, MetadataValue> getEntry(String key, Block block) {
      return this.getEntry(key, getBlockId(block));
   }

   private Map<Plugin, MetadataValue> getEntry(String key, long id) {
      SpecializedBlockMetadataStore.StoreKey storeKey = this.getCachedKey(key, id);
      Map<Plugin, MetadataValue> ret = this.metadata.get(storeKey);
      storeKey.metadataKey = null;
      return ret;
   }

   private SpecializedBlockMetadataStore.StoreKey getCachedKey(String metadataKey, long id) {
      this.cachedKey.block = id;
      this.cachedKey.metadataKey = metadataKey;
      return this.cachedKey;
   }

   static long getBlockId(Block block) {
      int x = block.getX();
      int y = block.getY();
      int z = block.getZ();
      Preconditions.checkArgument(x >= -30000000 && x <= 30000000, "block X out of range");
      Preconditions.checkArgument(y >= 0 && y <= 255, "block Y out of range");
      Preconditions.checkArgument(z >= -30000000 && z <= 30000000, "block Z out of range");
      return (long)(x & 67108863) << 34 | (long)(y & 0xFF) << 26 | z & 67108863;
   }

   static org.bukkit.Location toLocation(org.bukkit.World world, long id) {
      int x = (int)(id >> 34);
      int y = (int)(id >> 26 & 255L);
      int z = (int)id;
      if ((x & 33554432) > 0) {
         x |= -67108864;
      } else {
         x &= 67108863;
      }

      if ((z & 33554432) > 0) {
         z |= -67108864;
      } else {
         z &= 67108863;
      }

      return new org.bukkit.Location(world, x, y, z);
   }

   private static class StoreKey {
      private String metadataKey;
      private long block;

      private StoreKey(String metadataKey, long block) {
         this.metadataKey = metadataKey;
         this.block = block;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            SpecializedBlockMetadataStore.StoreKey storeKey = (SpecializedBlockMetadataStore.StoreKey)o;
            return this.block == storeKey.block && Objects.equal(this.metadataKey, storeKey.metadataKey);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(new Object[]{this.metadataKey, this.block});
      }
   }
}
