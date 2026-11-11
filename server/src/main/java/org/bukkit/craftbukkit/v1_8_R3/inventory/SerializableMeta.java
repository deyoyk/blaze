package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.meta.ItemMeta;

@SerializableAs("ItemMeta")
public class SerializableMeta implements ConfigurationSerializable {
   static final String TYPE_FIELD = "meta-type";
   static final ImmutableMap<Class<? extends CraftMetaItem>, String> classMap = ImmutableMap.<Class<? extends CraftMetaItem>, String>builder()
      .put(CraftMetaBanner.class, "BANNER")
      .put(CraftMetaBlockState.class, "TILE_ENTITY")
      .put(CraftMetaBook.class, "BOOK")
      .put(CraftMetaBookSigned.class, "BOOK_SIGNED")
      .put(CraftMetaSkull.class, "SKULL")
      .put(CraftMetaLeatherArmor.class, "LEATHER_ARMOR")
      .put(CraftMetaMap.class, "MAP")
      .put(CraftMetaPotion.class, "POTION")
      .put(CraftMetaEnchantedBook.class, "ENCHANTED")
      .put(CraftMetaFirework.class, "FIREWORK")
      .put(CraftMetaCharge.class, "FIREWORK_EFFECT")
      .put(CraftMetaItem.class, "UNSPECIFIC")
      .build();
   static final ImmutableMap<String, Constructor<? extends CraftMetaItem>> constructorMap;

   static {
      Builder<String, Constructor<? extends CraftMetaItem>> classConstructorBuilder = ImmutableMap.builder();

      for (Entry<Class<? extends CraftMetaItem>, String> mapping : classMap.entrySet()) {
         try {
            classConstructorBuilder.put(mapping.getValue(), mapping.getKey().getDeclaredConstructor(Map.class));
         } catch (NoSuchMethodException var4) {
            throw new AssertionError(var4);
         }
      }

      constructorMap = classConstructorBuilder.build();
   }

   private SerializableMeta() {
   }

   public static ItemMeta deserialize(Map<String, Object> map) throws Throwable {
      Validate.notNull(map, "Cannot deserialize null map");
      String type = getString(map, "meta-type", false);
      Constructor<? extends CraftMetaItem> constructor = (Constructor<? extends CraftMetaItem>)constructorMap.get(type);
      if (constructor == null) {
         throw new IllegalArgumentException(type + " is not a valid " + "meta-type");
      } else {
         try {
            return constructor.newInstance(map);
         } catch (IllegalAccessException | InstantiationException var4) {
            throw new AssertionError(var4);
         } catch (InvocationTargetException var5) {
            throw var5.getCause();
         }
      }
   }

   static String getString(Map<?, ?> map, Object field, boolean nullable) {
      return getObject(String.class, map, field, nullable);
   }

   static boolean getBoolean(Map<?, ?> map, Object field) {
      Boolean value = getObject(Boolean.class, map, field, true);
      return value != null && value;
   }

   static <T> T getObject(Class<T> clazz, Map<?, ?> map, Object field, boolean nullable) {
      Object object = map.get(field);
      if (clazz.isInstance(object)) {
         return clazz.cast(object);
      } else if (object == null) {
         if (!nullable) {
            throw new NoSuchElementException(map + " does not contain " + field);
         } else {
            return null;
         }
      } else {
         throw new IllegalArgumentException(field + "(" + object + ") is not a valid " + clazz);
      }
   }

   @Override
   public Map<String, Object> serialize() {
      throw new AssertionError();
   }
}
