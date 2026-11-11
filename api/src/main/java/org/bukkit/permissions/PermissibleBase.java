package org.bukkit.permissions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public class PermissibleBase implements Permissible {
   private ServerOperator opable = null;
   private Permissible parent = this;
   private final List<PermissionAttachment> attachments = new LinkedList<>();
   private final Map<String, PermissionAttachmentInfo> permissions = new HashMap<>();

   public PermissibleBase(ServerOperator opable) {
      this.opable = opable;
      if (opable instanceof Permissible) {
         this.parent = (Permissible)opable;
      }

      this.recalculatePermissions();
   }

   @Override
   public boolean isOp() {
      return this.opable == null ? false : this.opable.isOp();
   }

   @Override
   public void setOp(boolean value) {
      if (this.opable == null) {
         throw new UnsupportedOperationException("Cannot change op value as no ServerOperator is set");
      } else {
         this.opable.setOp(value);
      }
   }

   @Override
   public boolean isPermissionSet(String name) {
      if (name == null) {
         throw new IllegalArgumentException("Permission name cannot be null");
      } else {
         return this.permissions.containsKey(name.toLowerCase());
      }
   }

   @Override
   public boolean isPermissionSet(Permission perm) {
      if (perm == null) {
         throw new IllegalArgumentException("Permission cannot be null");
      } else {
         return this.isPermissionSet(perm.getName());
      }
   }

   @Override
   public boolean hasPermission(String inName) {
      if (inName == null) {
         throw new IllegalArgumentException("Permission name cannot be null");
      } else {
         String name = inName.toLowerCase();
         if (this.isPermissionSet(name)) {
            return this.permissions.get(name).getValue();
         } else {
            Permission perm = org.bukkit.Bukkit.getServer().getPluginManager().getPermission(name);
            return perm != null ? perm.getDefault().getValue(this.isOp()) : Permission.DEFAULT_PERMISSION.getValue(this.isOp());
         }
      }
   }

   @Override
   public boolean hasPermission(Permission perm) {
      if (perm == null) {
         throw new IllegalArgumentException("Permission cannot be null");
      } else {
         String name = perm.getName().toLowerCase();
         return this.isPermissionSet(name) ? this.permissions.get(name).getValue() : perm.getDefault().getValue(this.isOp());
      }
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
      if (name == null) {
         throw new IllegalArgumentException("Permission name cannot be null");
      } else if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else if (!plugin.isEnabled()) {
         throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
      } else {
         PermissionAttachment result = this.addAttachment(plugin);
         result.setPermission(name, value);
         this.recalculatePermissions();
         return result;
      }
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else if (!plugin.isEnabled()) {
         throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
      } else {
         PermissionAttachment result = new PermissionAttachment(plugin, this.parent);
         this.attachments.add(result);
         this.recalculatePermissions();
         return result;
      }
   }

   @Override
   public void removeAttachment(PermissionAttachment attachment) {
      if (attachment == null) {
         throw new IllegalArgumentException("Attachment cannot be null");
      } else if (this.attachments.contains(attachment)) {
         this.attachments.remove(attachment);
         PermissionRemovedExecutor ex = attachment.getRemovalCallback();
         if (ex != null) {
            ex.attachmentRemoved(attachment);
         }

         this.recalculatePermissions();
      } else {
         throw new IllegalArgumentException("Given attachment is not part of Permissible object " + this.parent);
      }
   }

   @Override
   public void recalculatePermissions() {
      this.clearPermissions();
      Set<Permission> defaults = org.bukkit.Bukkit.getServer().getPluginManager().getDefaultPermissions(this.isOp());
      org.bukkit.Bukkit.getServer().getPluginManager().subscribeToDefaultPerms(this.isOp(), this.parent);

      for (Permission perm : defaults) {
         String name = perm.getName().toLowerCase();
         this.permissions.put(name, new PermissionAttachmentInfo(this.parent, name, null, true));
         org.bukkit.Bukkit.getServer().getPluginManager().subscribeToPermission(name, this.parent);
         this.calculateChildPermissions(perm.getChildren(), false, null);
      }

      for (PermissionAttachment attachment : this.attachments) {
         this.calculateChildPermissions(attachment.getPermissions(), false, attachment);
      }
   }

   public synchronized void clearPermissions() {
      for (String name : this.permissions.keySet()) {
         org.bukkit.Bukkit.getServer().getPluginManager().unsubscribeFromPermission(name, this.parent);
      }

      org.bukkit.Bukkit.getServer().getPluginManager().unsubscribeFromDefaultPerms(false, this.parent);
      org.bukkit.Bukkit.getServer().getPluginManager().unsubscribeFromDefaultPerms(true, this.parent);
      this.permissions.clear();
   }

   private void calculateChildPermissions(Map<String, Boolean> children, boolean invert, PermissionAttachment attachment) {
      for (String name : children.keySet()) {
         Permission perm = org.bukkit.Bukkit.getServer().getPluginManager().getPermission(name);
         boolean value = children.get(name) ^ invert;
         String lname = name.toLowerCase();
         this.permissions.put(lname, new PermissionAttachmentInfo(this.parent, lname, attachment, value));
         org.bukkit.Bukkit.getServer().getPluginManager().subscribeToPermission(name, this.parent);
         if (perm != null) {
            this.calculateChildPermissions(perm.getChildren(), !value, attachment);
         }
      }
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
      if (name == null) {
         throw new IllegalArgumentException("Permission name cannot be null");
      } else if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else if (!plugin.isEnabled()) {
         throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
      } else {
         PermissionAttachment result = this.addAttachment(plugin, ticks);
         if (result != null) {
            result.setPermission(name, value);
         }

         return result;
      }
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else if (!plugin.isEnabled()) {
         throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
      } else {
         PermissionAttachment result = this.addAttachment(plugin);
         if (org.bukkit.Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new PermissibleBase.RemoveAttachmentRunnable(result), (long)ticks)
            == -1) {
            org.bukkit.Bukkit.getServer()
               .getLogger()
               .log(
                  Level.WARNING,
                  "Could not add PermissionAttachment to " + this.parent + " for plugin " + plugin.getDescription().getFullName() + ": Scheduler returned -1"
               );
            result.remove();
            return null;
         } else {
            return result;
         }
      }
   }

   @Override
   public Set<PermissionAttachmentInfo> getEffectivePermissions() {
      return new HashSet<>(this.permissions.values());
   }

   private class RemoveAttachmentRunnable implements Runnable {
      private PermissionAttachment attachment;

      public RemoveAttachmentRunnable(PermissionAttachment attachment) {
         this.attachment = attachment;
      }

      @Override
      public void run() {
         this.attachment.remove();
      }
   }
}
