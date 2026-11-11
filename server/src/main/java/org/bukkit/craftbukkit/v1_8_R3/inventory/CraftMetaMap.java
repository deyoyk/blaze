package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.MapMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaMap extends CraftMetaItem implements MapMeta {
   static final CraftMetaItem.ItemMetaKey MAP_SCALING = new CraftMetaItem.ItemMetaKey("map_is_scaling", "scaling");
   static final byte SCALING_EMPTY = 0;
   static final byte SCALING_TRUE = 1;
   static final byte SCALING_FALSE = 2;
   private byte scaling = 0;

   CraftMetaMap(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaMap) {
         CraftMetaMap map = (CraftMetaMap)meta;
         this.scaling = map.scaling;
      }
   }

   CraftMetaMap(NBTTagCompound tag) {
      super(tag);
      if (tag.hasKey(MAP_SCALING.NBT)) {
         this.scaling = (byte)(tag.getBoolean(MAP_SCALING.NBT) ? 1 : 2);
      }
   }

   CraftMetaMap(Map<String, Object> map) {
      super(map);
      Boolean scaling = SerializableMeta.getObject(Boolean.class, map, MAP_SCALING.BUKKIT, true);
      if (scaling != null) {
         this.setScaling(scaling);
      }
   }

   @Override
   void applyToItem(NBTTagCompound tag) {
      super.applyToItem(tag);
      if (this.hasScaling()) {
         tag.setBoolean(MAP_SCALING.NBT, this.isScaling());
      }
   }

   @Override
   boolean applicableTo(org.bukkit.Material type) {
      return type == org.bukkit.Material.MAP;
   }

   @Override
   boolean isEmpty() {
      return super.isEmpty() && this.isMapEmpty();
   }

   boolean isMapEmpty() {
      return !this.hasScaling();
   }

   boolean hasScaling() {
      return this.scaling != 0;
   }

   @Override
   public boolean isScaling() {
      return this.scaling == 1;
   }

   @Override
   public void setScaling(boolean scaling) {
      this.scaling = (byte)(scaling ? 1 : 2);
   }

   @Override
   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaMap) {
         CraftMetaMap that = (CraftMetaMap)meta;
         return this.scaling == that.scaling;
      } else {
         return true;
      }
   }

   @Override
   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaMap || this.isMapEmpty());
   }

   @Override
   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasScaling()) {
         hash ^= 572662306 << (this.isScaling() ? 1 : -1);
      }

      return original != hash ? CraftMetaMap.class.hashCode() ^ hash : hash;
   }

   public CraftMetaMap clone() {
      return (CraftMetaMap)super.clone();
   }

   @Override
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasScaling()) {
         builder.put(MAP_SCALING.BUKKIT, this.isScaling());
      }

      return builder;
   }
}
