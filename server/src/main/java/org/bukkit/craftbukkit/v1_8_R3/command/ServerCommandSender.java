package org.bukkit.craftbukkit.v1_8_R3.command;

import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public abstract class ServerCommandSender implements CommandSender {
   private static PermissibleBase blockPermInst;
   private final PermissibleBase perm;

   public ServerCommandSender() {
      if (this instanceof CraftBlockCommandSender) {
         if (blockPermInst == null) {
            blockPermInst = new PermissibleBase(this);
         }

         this.perm = blockPermInst;
      } else {
         this.perm = new PermissibleBase(this);
      }
   }

   @Override
   public boolean isPermissionSet(String name) {
      return this.perm.isPermissionSet(name);
   }

   @Override
   public boolean isPermissionSet(Permission perm) {
      return this.perm.isPermissionSet(perm);
   }

   @Override
   public boolean hasPermission(String name) {
      return this.perm.hasPermission(name);
   }

   @Override
   public boolean hasPermission(Permission perm) {
      return this.perm.hasPermission(perm);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
      return this.perm.addAttachment(plugin, name, value);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin) {
      return this.perm.addAttachment(plugin);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
      return this.perm.addAttachment(plugin, name, value, ticks);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
      return this.perm.addAttachment(plugin, ticks);
   }

   @Override
   public void removeAttachment(PermissionAttachment attachment) {
      this.perm.removeAttachment(attachment);
   }

   @Override
   public void recalculatePermissions() {
      this.perm.recalculatePermissions();
   }

   @Override
   public Set<PermissionAttachmentInfo> getEffectivePermissions() {
      return this.perm.getEffectivePermissions();
   }

   public boolean isPlayer() {
      return false;
   }

   @Override
   public org.bukkit.Server getServer() {
      return org.bukkit.Bukkit.getServer();
   }
}
