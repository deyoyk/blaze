package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaLeatherArmor extends CraftMetaItem implements LeatherArmorMeta {
   static final CraftMetaItem.ItemMetaKey COLOR = new CraftMetaItem.ItemMetaKey("color");
   private org.bukkit.Color color = CraftItemFactory.DEFAULT_LEATHER_COLOR;

   CraftMetaLeatherArmor(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaLeatherArmor) {
         CraftMetaLeatherArmor armorMeta = (CraftMetaLeatherArmor)meta;
         this.color = armorMeta.color;
      }
   }

   CraftMetaLeatherArmor(NBTTagCompound tag) {
      super(tag);
      if (tag.hasKey(DISPLAY.NBT)) {
         NBTTagCompound display = tag.getCompound(DISPLAY.NBT);
         if (display.hasKey(COLOR.NBT)) {
            this.color = org.bukkit.Color.fromRGB(display.getInt(COLOR.NBT));
         }
      }
   }

   CraftMetaLeatherArmor(Map<String, Object> map) {
      super(map);
      this.setColor(SerializableMeta.getObject(org.bukkit.Color.class, map, COLOR.BUKKIT, true));
   }

   @Override
   void applyToItem(NBTTagCompound itemTag) {
      super.applyToItem(itemTag);
      if (this.hasColor()) {
         this.setDisplayTag(itemTag, COLOR.NBT, new NBTTagInt(this.color.asRGB()));
      }
   }

   @Override
   boolean isEmpty() {
      return super.isEmpty() && this.isLeatherArmorEmpty();
   }

   boolean isLeatherArmorEmpty() {
      return !this.hasColor();
   }

   @Override
   boolean applicableTo(org.bukkit.Material type) {
      switch (type) {
         case LEATHER_HELMET:
         case LEATHER_CHESTPLATE:
         case LEATHER_LEGGINGS:
         case LEATHER_BOOTS:
            return true;
         default:
            return false;
      }
   }

   public CraftMetaLeatherArmor clone() {
      return (CraftMetaLeatherArmor)super.clone();
   }

   @Override
   public org.bukkit.Color getColor() {
      return this.color;
   }

   @Override
   public void setColor(org.bukkit.Color color) {
      this.color = color == null ? CraftItemFactory.DEFAULT_LEATHER_COLOR : color;
   }

   boolean hasColor() {
      return !CraftItemFactory.DEFAULT_LEATHER_COLOR.equals(this.color);
   }

   @Override
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasColor()) {
         builder.put(COLOR.BUKKIT, this.color);
      }

      return builder;
   }

   @Override
   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaLeatherArmor) {
         CraftMetaLeatherArmor that = (CraftMetaLeatherArmor)meta;
         return this.color.equals(that.color);
      } else {
         return true;
      }
   }

   @Override
   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaLeatherArmor || this.isLeatherArmorEmpty());
   }

   @Override
   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasColor()) {
         hash ^= this.color.hashCode();
      }

      return original != hash ? CraftMetaSkull.class.hashCode() ^ hash : hash;
   }
}
