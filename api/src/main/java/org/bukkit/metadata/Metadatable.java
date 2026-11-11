package org.bukkit.metadata;

import java.util.List;
import org.bukkit.plugin.Plugin;

public interface Metadatable {
   void setMetadata(String var1, MetadataValue var2);

   List<MetadataValue> getMetadata(String var1);

   boolean hasMetadata(String var1);

   void removeMetadata(String var1, Plugin var2);
}
