package org.bukkit.plugin;

import java.io.File;
import java.util.Set;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;

public interface PluginManager {
   void registerInterface(Class<? extends PluginLoader> var1) throws IllegalArgumentException;

   Plugin getPlugin(String var1);

   Plugin[] getPlugins();

   boolean isPluginEnabled(String var1);

   boolean isPluginEnabled(Plugin var1);

   boolean useTimings();

   Plugin loadPlugin(File var1) throws InvalidPluginException, InvalidDescriptionException, UnknownDependencyException;

   Plugin[] loadPlugins(File var1);

   void disablePlugins();

   void clearPlugins();

   void callEvent(Event var1) throws IllegalStateException;

   void registerEvents(Listener var1, Plugin var2);

   void registerEvent(Class<? extends Event> var1, Listener var2, EventPriority var3, EventExecutor var4, Plugin var5);

   void registerEvent(Class<? extends Event> var1, Listener var2, EventPriority var3, EventExecutor var4, Plugin var5, boolean var6);

   void enablePlugin(Plugin var1);

   void disablePlugin(Plugin var1);

   Permission getPermission(String var1);

   void addPermission(Permission var1);

   void removePermission(Permission var1);

   void removePermission(String var1);

   Set<Permission> getDefaultPermissions(boolean var1);

   void recalculatePermissionDefaults(Permission var1);

   void subscribeToPermission(String var1, Permissible var2);

   void unsubscribeFromPermission(String var1, Permissible var2);

   Set<Permissible> getPermissionSubscriptions(String var1);

   void subscribeToDefaultPerms(boolean var1, Permissible var2);

   void unsubscribeFromDefaultPerms(boolean var1, Permissible var2);

   Set<Permissible> getDefaultPermSubscriptions(boolean var1);

   Set<Permission> getPermissions();
}
