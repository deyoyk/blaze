package org.bukkit.permissions;

import java.util.Set;
import org.bukkit.plugin.Plugin;

public interface Permissible extends ServerOperator {
   boolean isPermissionSet(String var1);

   boolean isPermissionSet(Permission var1);

   boolean hasPermission(String var1);

   boolean hasPermission(Permission var1);

   PermissionAttachment addAttachment(Plugin var1, String var2, boolean var3);

   PermissionAttachment addAttachment(Plugin var1);

   PermissionAttachment addAttachment(Plugin var1, String var2, boolean var3, int var4);

   PermissionAttachment addAttachment(Plugin var1, int var2);

   void removeAttachment(PermissionAttachment var1);

   void recalculatePermissions();

   Set<PermissionAttachmentInfo> getEffectivePermissions();
}
