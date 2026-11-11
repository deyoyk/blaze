package org.bukkit.metadata;

import java.util.List;
import org.bukkit.plugin.Plugin;

public interface MetadataStore<T> {
   void setMetadata(T var1, String var2, MetadataValue var3);

   List<MetadataValue> getMetadata(T var1, String var2);

   boolean hasMetadata(T var1, String var2);

   void removeMetadata(T var1, String var2, Plugin var3);

   void invalidateAll(Plugin var1);
}
