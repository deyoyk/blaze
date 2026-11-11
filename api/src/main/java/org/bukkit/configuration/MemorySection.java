package org.bukkit.configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class MemorySection implements ConfigurationSection {
   protected final Map<String, Object> map = new LinkedHashMap<>();
   private final Configuration root;
   private final ConfigurationSection parent;
   private final String path;
   private final String fullPath;

   protected MemorySection() {
      if (!(this instanceof Configuration)) {
         throw new IllegalStateException("Cannot construct a root MemorySection when not a Configuration");
      } else {
         this.path = "";
         this.fullPath = "";
         this.parent = null;
         this.root = (Configuration)this;
      }
   }

   protected MemorySection(ConfigurationSection parent, String path) {
      Validate.notNull(parent, "Parent cannot be null");
      Validate.notNull(path, "Path cannot be null");
      this.path = path;
      this.parent = parent;
      this.root = parent.getRoot();
      Validate.notNull(this.root, "Path cannot be orphaned");
      this.fullPath = createPath(parent, path);
   }

   @Override
   public Set<String> getKeys(boolean deep) {
      Set<String> result = new LinkedHashSet<>();
      Configuration root = this.getRoot();
      if (root != null && root.options().copyDefaults()) {
         ConfigurationSection defaults = this.getDefaultSection();
         if (defaults != null) {
            result.addAll(defaults.getKeys(deep));
         }
      }

      this.mapChildrenKeys(result, this, deep);
      return result;
   }

   @Override
   public Map<String, Object> getValues(boolean deep) {
      Map<String, Object> result = new LinkedHashMap<>();
      Configuration root = this.getRoot();
      if (root != null && root.options().copyDefaults()) {
         ConfigurationSection defaults = this.getDefaultSection();
         if (defaults != null) {
            result.putAll(defaults.getValues(deep));
         }
      }

      this.mapChildrenValues(result, this, deep);
      return result;
   }

   @Override
   public boolean contains(String path) {
      return this.get(path) != null;
   }

   @Override
   public boolean isSet(String path) {
      Configuration root = this.getRoot();
      if (root == null) {
         return false;
      } else {
         return root.options().copyDefaults() ? this.contains(path) : this.get(path, null) != null;
      }
   }

   @Override
   public String getCurrentPath() {
      return this.fullPath;
   }

   @Override
   public String getName() {
      return this.path;
   }

   @Override
   public Configuration getRoot() {
      return this.root;
   }

   @Override
   public ConfigurationSection getParent() {
      return this.parent;
   }

   @Override
   public void addDefault(String path, Object value) {
      Validate.notNull(path, "Path cannot be null");
      Configuration root = this.getRoot();
      if (root == null) {
         throw new IllegalStateException("Cannot add default without root");
      } else if (root == this) {
         throw new UnsupportedOperationException("Unsupported addDefault(String, Object) implementation");
      } else {
         root.addDefault(createPath(this, path), value);
      }
   }

   @Override
   public ConfigurationSection getDefaultSection() {
      Configuration root = this.getRoot();
      Configuration defaults = root == null ? null : root.getDefaults();
      return defaults != null && defaults.isConfigurationSection(this.getCurrentPath()) ? defaults.getConfigurationSection(this.getCurrentPath()) : null;
   }

   @Override
   public void set(String path, Object value) {
      Validate.notEmpty(path, "Cannot set to an empty path");
      Configuration root = this.getRoot();
      if (root == null) {
         throw new IllegalStateException("Cannot use section without a root");
      } else {
         char separator = root.options().pathSeparator();
         int i1 = -1;
         ConfigurationSection section = this;

         int i2;
         while ((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
            String node = path.substring(i2, i1);
            ConfigurationSection subSection = section.getConfigurationSection(node);
            if (subSection == null) {
               section = section.createSection(node);
            } else {
               section = subSection;
            }
         }

         String key = path.substring(i2);
         if (section == this) {
            if (value == null) {
               this.map.remove(key);
            } else {
               this.map.put(key, value);
            }
         } else {
            section.set(key, value);
         }
      }
   }

   @Override
   public Object get(String path) {
      return this.get(path, this.getDefault(path));
   }

   @Override
   public Object get(String path, Object def) {
      Validate.notNull(path, "Path cannot be null");
      if (path.length() == 0) {
         return this;
      } else {
         Configuration root = this.getRoot();
         if (root == null) {
            throw new IllegalStateException("Cannot access section without a root");
         } else {
            char separator = root.options().pathSeparator();
            int i1 = -1;
            ConfigurationSection section = this;

            int i2;
            while ((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
               section = section.getConfigurationSection(path.substring(i2, i1));
               if (section == null) {
                  return def;
               }
            }

            String key = path.substring(i2);
            if (section == this) {
               Object result = this.map.get(key);
               return result == null ? def : result;
            } else {
               return section.get(key, def);
            }
         }
      }
   }

   @Override
   public ConfigurationSection createSection(String path) {
      Validate.notEmpty(path, "Cannot create section at empty path");
      Configuration root = this.getRoot();
      if (root == null) {
         throw new IllegalStateException("Cannot create section without a root");
      } else {
         char separator = root.options().pathSeparator();
         int i1 = -1;
         ConfigurationSection section = this;

         int i2;
         while ((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
            String node = path.substring(i2, i1);
            ConfigurationSection subSection = section.getConfigurationSection(node);
            if (subSection == null) {
               section = section.createSection(node);
            } else {
               section = subSection;
            }
         }

         String key = path.substring(i2);
         if (section == this) {
            ConfigurationSection result = new MemorySection(this, key);
            this.map.put(key, result);
            return result;
         } else {
            return section.createSection(key);
         }
      }
   }

   @Override
   public ConfigurationSection createSection(String path, Map<?, ?> map) {
      ConfigurationSection section = this.createSection(path);

      for (Entry<?, ?> entry : map.entrySet()) {
         if (entry.getValue() instanceof Map) {
            section.createSection(entry.getKey().toString(), (Map<?, ?>)entry.getValue());
         } else {
            section.set(entry.getKey().toString(), entry.getValue());
         }
      }

      return section;
   }

   @Override
   public String getString(String path) {
      Object def = this.getDefault(path);
      return this.getString(path, def != null ? def.toString() : null);
   }

   @Override
   public String getString(String path, String def) {
      Object val = this.get(path, def);
      return val != null ? val.toString() : def;
   }

   @Override
   public boolean isString(String path) {
      Object val = this.get(path);
      return val instanceof String;
   }

   @Override
   public int getInt(String path) {
      Object def = this.getDefault(path);
      return this.getInt(path, def instanceof Number ? NumberConversions.toInt(def) : 0);
   }

   @Override
   public int getInt(String path, int def) {
      Object val = this.get(path, def);
      return val instanceof Number ? NumberConversions.toInt(val) : def;
   }

   @Override
   public boolean isInt(String path) {
      Object val = this.get(path);
      return val instanceof Integer;
   }

   @Override
   public boolean getBoolean(String path) {
      Object def = this.getDefault(path);
      return this.getBoolean(path, def instanceof Boolean ? (Boolean)def : false);
   }

   @Override
   public boolean getBoolean(String path, boolean def) {
      Object val = this.get(path, def);
      return val instanceof Boolean ? (Boolean)val : def;
   }

   @Override
   public boolean isBoolean(String path) {
      Object val = this.get(path);
      return val instanceof Boolean;
   }

   @Override
   public double getDouble(String path) {
      Object def = this.getDefault(path);
      return this.getDouble(path, def instanceof Number ? NumberConversions.toDouble(def) : 0.0);
   }

   @Override
   public double getDouble(String path, double def) {
      Object val = this.get(path, def);
      return val instanceof Number ? NumberConversions.toDouble(val) : def;
   }

   @Override
   public boolean isDouble(String path) {
      Object val = this.get(path);
      return val instanceof Double;
   }

   @Override
   public float getFloat(String path) {
      Object def = this.getDefault(path);
      return this.getFloat(path, def instanceof Float ? NumberConversions.toFloat(def) : 0.0F);
   }

   @Override
   public float getFloat(String path, float def) {
      Object val = this.get(path, def);
      return val instanceof Float ? NumberConversions.toFloat(val) : def;
   }

   @Override
   public boolean isFloat(String path) {
      Object val = this.get(path);
      return val instanceof Float;
   }

   @Override
   public long getLong(String path) {
      Object def = this.getDefault(path);
      return this.getLong(path, def instanceof Number ? NumberConversions.toLong(def) : 0L);
   }

   @Override
   public long getLong(String path, long def) {
      Object val = this.get(path, def);
      return val instanceof Number ? NumberConversions.toLong(val) : def;
   }

   @Override
   public boolean isLong(String path) {
      Object val = this.get(path);
      return val instanceof Long;
   }

   @Override
   public List<?> getList(String path) {
      Object def = this.getDefault(path);
      return this.getList(path, def instanceof List ? (List)def : null);
   }

   @Override
   public List<?> getList(String path, List<?> def) {
      Object val = this.get(path, def);
      return (List<?>)(val instanceof List ? val : def);
   }

   @Override
   public boolean isList(String path) {
      Object val = this.get(path);
      return val instanceof List;
   }

   @Override
   public List<String> getStringList(String path) {
      List<?> list = this.getList(path);
      if (list == null) {
         return new ArrayList<>(0);
      } else {
         List<String> result = new ArrayList<>();

         for (Object object : list) {
            if (object instanceof String || this.isPrimitiveWrapper(object)) {
               result.add(String.valueOf(object));
            }
         }

         return result;
      }
   }

   @Override
   public List<Integer> getIntegerList(String path) {
      List<?> list = this.getList(path);
      if (list == null) {
         return new ArrayList<>(0);
      } else {
         List<Integer> result = new ArrayList<>();

         for (Object object : list) {
            if (object instanceof Integer) {
               result.add((Integer)object);
            } else if (object instanceof String) {
               try {
                  result.add(Integer.valueOf((String)object));
               } catch (Exception var7) {
               }
            } else if (object instanceof Character) {
               result.add(Integer.valueOf((Character)object));
            } else if (object instanceof Number) {
               result.add(((Number)object).intValue());
            }
         }

         return result;
      }
   }

   @Override
   public List<Boolean> getBooleanList(String path) {
      List<?> list = this.getList(path);
      if (list == null) {
         return new ArrayList<>(0);
      } else {
         List<Boolean> result = new ArrayList<>();

         for (Object object : list) {
            if (object instanceof Boolean) {
               result.add((Boolean)object);
            } else if (object instanceof String) {
               if (Boolean.TRUE.toString().equals(object)) {
                  result.add(true);
               } else if (Boolean.FALSE.toString().equals(object)) {
                  result.add(false);
               }
            }
         }

         return result;
      }
   }

   @Override
   public List<Double> getDoubleList(String path) {
      List<?> list = this.getList(path);
      if (list == null) {
         return new ArrayList<>(0);
      } else {
         List<Double> result = new ArrayList<>();

         for (Object object : list) {
            if (object instanceof Double) {
               result.add((Double)object);
            } else if (object instanceof String) {
               try {
                  result.add(Double.valueOf((String)object));
               } catch (Exception var7) {
               }
            } else if (object instanceof Character) {
               result.add((double)((Character)object).charValue());
            } else if (object instanceof Number) {
               result.add(((Number)object).doubleValue());
            }
         }

         return result;
      }
   }

   @Override
   public List<Float> getFloatList(String path) {
      List<?> list = this.getList(path);
      if (list == null) {
         return new ArrayList<>(0);
      } else {
         List<Float> result = new ArrayList<>();

         for (Object object : list) {
            if (object instanceof Float) {
               result.add((Float)object);
            } else if (object instanceof String) {
               try {
                  result.add(Float.valueOf((String)object));
               } catch (Exception var7) {
               }
            } else if (object instanceof Character) {
               result.add((float)((Character)object).charValue());
            } else if (object instanceof Number) {
               result.add(((Number)object).floatValue());
            }
         }

         return result;
      }
   }

   @Override
   public List<Long> getLongList(String path) {
      List<?> list = this.getList(path);
      if (list == null) {
         return new ArrayList<>(0);
      } else {
         List<Long> result = new ArrayList<>();

         for (Object object : list) {
            if (object instanceof Long) {
               result.add((Long)object);
            } else if (object instanceof String) {
               try {
                  result.add(Long.valueOf((String)object));
               } catch (Exception var7) {
               }
            } else if (object instanceof Character) {
               result.add((long)((Character)object).charValue());
            } else if (object instanceof Number) {
               result.add(((Number)object).longValue());
            }
         }

         return result;
      }
   }

   @Override
   public List<Byte> getByteList(String path) {
      List<?> list = this.getList(path);
      if (list == null) {
         return new ArrayList<>(0);
      } else {
         List<Byte> result = new ArrayList<>();

         for (Object object : list) {
            if (object instanceof Byte) {
               result.add((Byte)object);
            } else if (object instanceof String) {
               try {
                  result.add(Byte.valueOf((String)object));
               } catch (Exception var7) {
               }
            } else if (object instanceof Character) {
               result.add((byte)((Character)object).charValue());
            } else if (object instanceof Number) {
               result.add(((Number)object).byteValue());
            }
         }

         return result;
      }
   }

   @Override
   public List<Character> getCharacterList(String path) {
      List<?> list = this.getList(path);
      if (list == null) {
         return new ArrayList<>(0);
      } else {
         List<Character> result = new ArrayList<>();

         for (Object object : list) {
            if (object instanceof Character) {
               result.add((Character)object);
            } else if (object instanceof String) {
               String str = (String)object;
               if (str.length() == 1) {
                  result.add(str.charAt(0));
               }
            } else if (object instanceof Number) {
               result.add((char)((Number)object).intValue());
            }
         }

         return result;
      }
   }

   @Override
   public List<Short> getShortList(String path) {
      List<?> list = this.getList(path);
      if (list == null) {
         return new ArrayList<>(0);
      } else {
         List<Short> result = new ArrayList<>();

         for (Object object : list) {
            if (object instanceof Short) {
               result.add((Short)object);
            } else if (object instanceof String) {
               try {
                  result.add(Short.valueOf((String)object));
               } catch (Exception var7) {
               }
            } else if (object instanceof Character) {
               result.add((short)((Character)object).charValue());
            } else if (object instanceof Number) {
               result.add(((Number)object).shortValue());
            }
         }

         return result;
      }
   }

   @Override
   public List<Map<?, ?>> getMapList(String path) {
      List<?> list = this.getList(path);
      List<Map<?, ?>> result = new ArrayList<>();
      if (list == null) {
         return result;
      } else {
         for (Object object : list) {
            if (object instanceof Map) {
               result.add((Map<?, ?>)object);
            }
         }

         return result;
      }
   }

   @Override
   public Vector getVector(String path) {
      Object def = this.getDefault(path);
      return this.getVector(path, def instanceof Vector ? (Vector)def : null);
   }

   @Override
   public Vector getVector(String path, Vector def) {
      Object val = this.get(path, def);
      return val instanceof Vector ? (Vector)val : def;
   }

   @Override
   public boolean isVector(String path) {
      Object val = this.get(path);
      return val instanceof Vector;
   }

   @Override
   public org.bukkit.OfflinePlayer getOfflinePlayer(String path) {
      Object def = this.getDefault(path);
      return this.getOfflinePlayer(path, def instanceof org.bukkit.OfflinePlayer ? (org.bukkit.OfflinePlayer)def : null);
   }

   @Override
   public org.bukkit.OfflinePlayer getOfflinePlayer(String path, org.bukkit.OfflinePlayer def) {
      Object val = this.get(path, def);
      return val instanceof org.bukkit.OfflinePlayer ? (org.bukkit.OfflinePlayer)val : def;
   }

   @Override
   public boolean isOfflinePlayer(String path) {
      Object val = this.get(path);
      return val instanceof org.bukkit.OfflinePlayer;
   }

   @Override
   public ItemStack getItemStack(String path) {
      Object def = this.getDefault(path);
      return this.getItemStack(path, def instanceof ItemStack ? (ItemStack)def : null);
   }

   @Override
   public ItemStack getItemStack(String path, ItemStack def) {
      Object val = this.get(path, def);
      return val instanceof ItemStack ? (ItemStack)val : def;
   }

   @Override
   public boolean isItemStack(String path) {
      Object val = this.get(path);
      return val instanceof ItemStack;
   }

   @Override
   public org.bukkit.Color getColor(String path) {
      Object def = this.getDefault(path);
      return this.getColor(path, def instanceof org.bukkit.Color ? (org.bukkit.Color)def : null);
   }

   @Override
   public org.bukkit.Color getColor(String path, org.bukkit.Color def) {
      Object val = this.get(path, def);
      return val instanceof org.bukkit.Color ? (org.bukkit.Color)val : def;
   }

   @Override
   public boolean isColor(String path) {
      Object val = this.get(path);
      return val instanceof org.bukkit.Color;
   }

   @Override
   public ConfigurationSection getConfigurationSection(String path) {
      Object val = this.get(path, null);
      if (val != null) {
         return val instanceof ConfigurationSection ? (ConfigurationSection)val : null;
      } else {
         val = this.get(path, this.getDefault(path));
         return val instanceof ConfigurationSection ? this.createSection(path) : null;
      }
   }

   @Override
   public boolean isConfigurationSection(String path) {
      Object val = this.get(path);
      return val instanceof ConfigurationSection;
   }

   protected boolean isPrimitiveWrapper(Object input) {
      return input instanceof Integer
         || input instanceof Boolean
         || input instanceof Character
         || input instanceof Byte
         || input instanceof Short
         || input instanceof Double
         || input instanceof Long
         || input instanceof Float;
   }

   protected Object getDefault(String path) {
      Validate.notNull(path, "Path cannot be null");
      Configuration root = this.getRoot();
      Configuration defaults = root == null ? null : root.getDefaults();
      return defaults == null ? null : defaults.get(createPath(this, path));
   }

   protected void mapChildrenKeys(Set<String> output, ConfigurationSection section, boolean deep) {
      if (section instanceof MemorySection) {
         MemorySection sec = (MemorySection)section;

         for (Entry<String, Object> entry : sec.map.entrySet()) {
            output.add(createPath(section, entry.getKey(), this));
            if (deep && entry.getValue() instanceof ConfigurationSection) {
               ConfigurationSection subsection = (ConfigurationSection)entry.getValue();
               this.mapChildrenKeys(output, subsection, deep);
            }
         }
      } else {
         for (String key : section.getKeys(deep)) {
            output.add(createPath(section, key, this));
         }
      }
   }

   protected void mapChildrenValues(Map<String, Object> output, ConfigurationSection section, boolean deep) {
      if (section instanceof MemorySection) {
         MemorySection sec = (MemorySection)section;

         for (Entry<String, Object> entry : sec.map.entrySet()) {
            output.put(createPath(section, entry.getKey(), this), entry.getValue());
            if (entry.getValue() instanceof ConfigurationSection && deep) {
               this.mapChildrenValues(output, (ConfigurationSection)entry.getValue(), deep);
            }
         }
      } else {
         Map<String, Object> values = section.getValues(deep);

         for (Entry<String, Object> entryx : values.entrySet()) {
            output.put(createPath(section, entryx.getKey(), this), entryx.getValue());
         }
      }
   }

   public static String createPath(ConfigurationSection section, String key) {
      return createPath(section, key, section == null ? null : section.getRoot());
   }

   public static String createPath(ConfigurationSection section, String key, ConfigurationSection relativeTo) {
      Validate.notNull(section, "Cannot create path without a section");
      Configuration root = section.getRoot();
      if (root == null) {
         throw new IllegalStateException("Cannot create path without a root");
      } else {
         char separator = root.options().pathSeparator();
         StringBuilder builder = new StringBuilder();
         if (section != null) {
            for (ConfigurationSection parent = section; parent != null && parent != relativeTo; parent = parent.getParent()) {
               if (builder.length() > 0) {
                  builder.insert(0, separator);
               }

               builder.insert(0, parent.getName());
            }
         }

         if (key != null && key.length() > 0) {
            if (builder.length() > 0) {
               builder.append(separator);
            }

            builder.append(key);
         }

         return builder.toString();
      }
   }

   @Override
   public String toString() {
      Configuration root = this.getRoot();
      return this.getClass().getSimpleName() + "[path='" + this.getCurrentPath() + "', root='" + (root == null ? null : root.getClass().getSimpleName()) + "']";
   }
}
