package org.bukkit.permissions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.PluginManager;

public class Permission {
   public static final PermissionDefault DEFAULT_PERMISSION = PermissionDefault.OP;
   private final String name;
   private final Map<String, Boolean> children = new LinkedHashMap<>();
   private PermissionDefault defaultValue = DEFAULT_PERMISSION;
   private String description;

   public Permission(String name) {
      this(name, null, null, null);
   }

   public Permission(String name, String description) {
      this(name, description, null, null);
   }

   public Permission(String name, PermissionDefault defaultValue) {
      this(name, null, defaultValue, null);
   }

   public Permission(String name, String description, PermissionDefault defaultValue) {
      this(name, description, defaultValue, null);
   }

   public Permission(String name, Map<String, Boolean> children) {
      this(name, null, null, children);
   }

   public Permission(String name, String description, Map<String, Boolean> children) {
      this(name, description, null, children);
   }

   public Permission(String name, PermissionDefault defaultValue, Map<String, Boolean> children) {
      this(name, null, defaultValue, children);
   }

   public Permission(String name, String description, PermissionDefault defaultValue, Map<String, Boolean> children) {
      Validate.notNull(name, "Name cannot be null");
      this.name = name;
      this.description = description == null ? "" : description;
      if (defaultValue != null) {
         this.defaultValue = defaultValue;
      }

      if (children != null) {
         this.children.putAll(children);
      }

      this.recalculatePermissibles();
   }

   public String getName() {
      return this.name;
   }

   public Map<String, Boolean> getChildren() {
      return this.children;
   }

   public PermissionDefault getDefault() {
      return this.defaultValue;
   }

   public void setDefault(PermissionDefault value) {
      if (this.defaultValue == null) {
         throw new IllegalArgumentException("Default value cannot be null");
      } else {
         this.defaultValue = value;
         this.recalculatePermissibles();
      }
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String value) {
      if (value == null) {
         this.description = "";
      } else {
         this.description = value;
      }
   }

   public Set<Permissible> getPermissibles() {
      return org.bukkit.Bukkit.getServer().getPluginManager().getPermissionSubscriptions(this.name);
   }

   public void recalculatePermissibles() {
      Set<Permissible> perms = this.getPermissibles();
      org.bukkit.Bukkit.getServer().getPluginManager().recalculatePermissionDefaults(this);

      for (Permissible p : perms) {
         p.recalculatePermissions();
      }
   }

   public Permission addParent(String name, boolean value) {
      PluginManager pm = org.bukkit.Bukkit.getServer().getPluginManager();
      String lname = name.toLowerCase();
      Permission perm = pm.getPermission(lname);
      if (perm == null) {
         perm = new Permission(lname);
         pm.addPermission(perm);
      }

      this.addParent(perm, value);
      return perm;
   }

   public void addParent(Permission perm, boolean value) {
      perm.getChildren().put(this.getName(), value);
      perm.recalculatePermissibles();
   }

   public static List<Permission> loadPermissions(Map<?, ?> data, String error, PermissionDefault def) {
      List<Permission> result = new ArrayList<>();

      for (Entry<?, ?> entry : data.entrySet()) {
         try {
            result.add(loadPermission(entry.getKey().toString(), (Map<?, ?>)entry.getValue(), def, result));
         } catch (Throwable var7) {
            org.bukkit.Bukkit.getServer().getLogger().log(Level.SEVERE, String.format(error, entry.getKey()), var7);
         }
      }

      return result;
   }

   public static Permission loadPermission(String name, Map<String, Object> data) {
      return loadPermission(name, data, DEFAULT_PERMISSION, null);
   }

   public static Permission loadPermission(String name, Map<?, ?> data, PermissionDefault def, List<Permission> output) {
      Validate.notNull(name, "Name cannot be null");
      Validate.notNull(data, "Data cannot be null");
      String desc = null;
      Map<String, Boolean> children = null;
      if (data.get("default") != null) {
         PermissionDefault value = PermissionDefault.getByName(data.get("default").toString());
         if (value == null) {
            throw new IllegalArgumentException("'default' key contained unknown value");
         }

         def = value;
      }

      if (data.get("children") != null) {
         Object childrenNode = data.get("children");
         if (childrenNode instanceof Iterable) {
            children = new LinkedHashMap<>();

            for (Object child : (Iterable)childrenNode) {
               if (child != null) {
                  children.put(child.toString(), Boolean.TRUE);
               }
            }
         } else {
            if (!(childrenNode instanceof Map)) {
               throw new IllegalArgumentException("'children' key is of wrong type");
            }

            children = extractChildren((Map<?, ?>)childrenNode, name, def, output);
         }
      }

      if (data.get("description") != null) {
         desc = data.get("description").toString();
      }

      return new Permission(name, desc, def, children);
   }

   private static Map<String, Boolean> extractChildren(Map<?, ?> input, String name, PermissionDefault def, List<Permission> output) {
      Map<String, Boolean> children = new LinkedHashMap<>();

      for (Entry<?, ?> entry : input.entrySet()) {
         if (entry.getValue() instanceof Boolean) {
            children.put(entry.getKey().toString(), (Boolean)entry.getValue());
         } else {
            if (!(entry.getValue() instanceof Map)) {
               throw new IllegalArgumentException("Child '" + entry.getKey().toString() + "' contains invalid value");
            }

            try {
               Permission perm = loadPermission(entry.getKey().toString(), (Map<?, ?>)entry.getValue(), def, output);
               children.put(perm.getName(), Boolean.TRUE);
               if (output != null) {
                  output.add(perm);
               }
            } catch (Throwable var8) {
               throw new IllegalArgumentException("Permission node '" + entry.getKey().toString() + "' in child of " + name + " is invalid", var8);
            }
         }
      }

      return children;
   }
}
