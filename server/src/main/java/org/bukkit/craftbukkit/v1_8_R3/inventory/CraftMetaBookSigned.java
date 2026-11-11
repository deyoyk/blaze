package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.BookMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaBookSigned extends CraftMetaBook implements BookMeta {
   CraftMetaBookSigned(CraftMetaItem meta) {
      super(meta);
   }

   CraftMetaBookSigned(NBTTagCompound tag) {
      super(tag, false);
      boolean resolved = true;
      if (tag.hasKey(RESOLVED.NBT)) {
         resolved = tag.getBoolean(RESOLVED.NBT);
      }

      if (tag.hasKey(BOOK_PAGES.NBT)) {
         NBTTagList pages = tag.getList(BOOK_PAGES.NBT, 8);

         for (int i = 0; i < pages.size(); i++) {
            String page = pages.getString(i);
            if (resolved) {
               try {
                  this.pages.add(ChatSerializer.a(page));
                  continue;
               } catch (Exception var7) {
               }
            }

            this.addPage(new String[]{page});
         }
      }
   }

   CraftMetaBookSigned(Map<String, Object> map) {
      super(map);
   }

   @Override
   void applyToItem(NBTTagCompound itemData) {
      super.applyToItem(itemData, false);
      if (this.hasTitle()) {
         itemData.setString(BOOK_TITLE.NBT, this.title);
      } else {
         itemData.setString(BOOK_TITLE.NBT, " ");
      }

      if (this.hasAuthor()) {
         itemData.setString(BOOK_AUTHOR.NBT, this.author);
      } else {
         itemData.setString(BOOK_AUTHOR.NBT, " ");
      }

      if (this.hasPages()) {
         NBTTagList list = new NBTTagList();

         for (IChatBaseComponent page : this.pages) {
            list.add(new NBTTagString(ChatSerializer.a(page)));
         }

         itemData.set(BOOK_PAGES.NBT, list);
      }

      itemData.setBoolean(RESOLVED.NBT, true);
      if (this.generation != null) {
         itemData.setInt(GENERATION.NBT, this.generation);
      } else {
         itemData.setInt(GENERATION.NBT, 0);
      }
   }

   @Override
   boolean isEmpty() {
      return super.isEmpty();
   }

   @Override
   boolean applicableTo(org.bukkit.Material type) {
      switch (type) {
         case BOOK_AND_QUILL:
         case WRITTEN_BOOK:
            return true;
         default:
            return false;
      }
   }

   public CraftMetaBookSigned clone() {
      return (CraftMetaBookSigned)super.clone();
   }

   @Override
   int applyHash() {
      return super.applyHash();
   }

   @Override
   boolean equalsCommon(CraftMetaItem meta) {
      return super.equalsCommon(meta);
   }

   @Override
   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaBookSigned || this.isBookEmpty());
   }

   @Override
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      super.serialize(builder);
      return builder;
   }
}
