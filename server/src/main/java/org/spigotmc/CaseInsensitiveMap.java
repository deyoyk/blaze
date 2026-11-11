package org.spigotmc;

import gnu.trove.map.hash.TCustomHashMap;
import java.util.Map;

public class CaseInsensitiveMap<V> extends TCustomHashMap<String, V> {
   public CaseInsensitiveMap() {
      super(org.spigotmc.CaseInsensitiveHashingStrategy.INSTANCE);
   }

   public CaseInsensitiveMap(Map<? extends String, ? extends V> map) {
      super(org.spigotmc.CaseInsensitiveHashingStrategy.INSTANCE, map);
   }
}
