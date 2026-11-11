package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.BannerMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBanner extends CraftMetaItem implements BannerMeta {
   static final CraftMetaItem.ItemMetaKey BASE = new CraftMetaItem.ItemMetaKey("Base", "base-color");
   static final CraftMetaItem.ItemMetaKey PATTERNS = new CraftMetaItem.ItemMetaKey("Patterns", "patterns");
   static final CraftMetaItem.ItemMetaKey COLOR = new CraftMetaItem.ItemMetaKey("Color", "color");
   static final CraftMetaItem.ItemMetaKey PATTERN = new CraftMetaItem.ItemMetaKey("Pattern", "pattern");
   private org.bukkit.DyeColor base;
   private List<Pattern> patterns = new ArrayList<>();

   CraftMetaBanner(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaBanner) {
         CraftMetaBanner banner = (CraftMetaBanner)meta;
         this.base = banner.base;
         this.patterns = new ArrayList<>(banner.patterns);
      }
   }

   CraftMetaBanner(NBTTagCompound tag) {
      super(tag);
      if (tag.hasKey("BlockEntityTag")) {
         NBTTagCompound entityTag = tag.getCompound("BlockEntityTag");
         this.base = entityTag.hasKey(BASE.NBT) ? org.bukkit.DyeColor.getByDyeData((byte)entityTag.getInt(BASE.NBT)) : null;
         if (entityTag.hasKey(PATTERNS.NBT)) {
            NBTTagList patterns = entityTag.getList(PATTERNS.NBT, 10);

            for (int i = 0; i < Math.min(patterns.size(), 20); i++) {
               NBTTagCompound p = patterns.get(i);
               org.bukkit.DyeColor color = org.bukkit.DyeColor.getByDyeData((byte)p.getInt(COLOR.NBT));
               PatternType pattern = PatternType.getByIdentifier(p.getString(PATTERN.NBT));
               if (color != null && pattern != null) {
                  this.patterns.add(new Pattern(color, pattern));
               }
            }
         }
      }
   }

   CraftMetaBanner(Map<String, Object> map) {
      super(map);
      String baseStr = SerializableMeta.getString(map, BASE.BUKKIT, true);
      if (baseStr != null) {
         this.base = org.bukkit.DyeColor.valueOf(baseStr);
      }

      Iterable<?> rawPatternList = SerializableMeta.getObject(Iterable.class, map, PATTERNS.BUKKIT, true);
      if (rawPatternList != null) {
         for (Object obj : rawPatternList) {
            if (!(obj instanceof Pattern)) {
               throw new IllegalArgumentException("Object in pattern list is not valid. " + obj.getClass());
            }

            this.addPattern((Pattern)obj);
         }
      }
   }

   @Override
   void applyToItem(NBTTagCompound tag) {
      super.applyToItem(tag);
      NBTTagCompound entityTag = new NBTTagCompound();
      if (this.base != null) {
         entityTag.setInt(BASE.NBT, this.base.getDyeData());
      }

      NBTTagList newPatterns = new NBTTagList();

      for (Pattern p : this.patterns) {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setInt(COLOR.NBT, p.getColor().getDyeData());
         compound.setString(PATTERN.NBT, p.getPattern().getIdentifier());
         newPatterns.add(compound);
      }

      entityTag.set(PATTERNS.NBT, newPatterns);
      tag.set("BlockEntityTag", entityTag);
   }

   @Override
   public org.bukkit.DyeColor getBaseColor() {
      return this.base;
   }

   @Override
   public void setBaseColor(org.bukkit.DyeColor color) {
      this.base = color;
   }

   @Override
   public List<Pattern> getPatterns() {
      return new ArrayList<>(this.patterns);
   }

   @Override
   public void setPatterns(List<Pattern> patterns) {
      this.patterns = new ArrayList<>(patterns);
   }

   @Override
   public void addPattern(Pattern pattern) {
      this.patterns.add(pattern);
   }

   @Override
   public Pattern getPattern(int i) {
      return this.patterns.get(i);
   }

   @Override
   public Pattern removePattern(int i) {
      return this.patterns.remove(i);
   }

   @Override
   public void setPattern(int i, Pattern pattern) {
      this.patterns.set(i, pattern);
   }

   @Override
   public int numberOfPatterns() {
      return this.patterns.size();
   }

   @Override
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.base != null) {
         builder.put(BASE.BUKKIT, this.base.toString());
      }

      if (!this.patterns.isEmpty()) {
         builder.put(PATTERNS.BUKKIT, ImmutableList.copyOf(this.patterns));
      }

      return builder;
   }

   @Override
   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.base != null) {
         hash = 31 * hash + this.base.hashCode();
      }

      if (!this.patterns.isEmpty()) {
         hash = 31 * hash + this.patterns.hashCode();
      }

      return original != hash ? CraftMetaBanner.class.hashCode() ^ hash : hash;
   }

   @Override
   public boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaBanner) {
         CraftMetaBanner that = (CraftMetaBanner)meta;
         return this.base == that.base && this.patterns.equals(that.patterns);
      } else {
         return true;
      }
   }

   @Override
   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaBanner || this.patterns.isEmpty() && this.base == null);
   }

   @Override
   boolean isEmpty() {
      return super.isEmpty() && this.patterns.isEmpty() && this.base == null;
   }

   @Override
   boolean applicableTo(org.bukkit.Material type) {
      return type == org.bukkit.Material.BANNER;
   }
}
