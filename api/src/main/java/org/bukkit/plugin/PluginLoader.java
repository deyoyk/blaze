package org.bukkit.plugin;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public interface PluginLoader {
   Plugin loadPlugin(File var1) throws InvalidPluginException, UnknownDependencyException;

   PluginDescriptionFile getPluginDescription(File var1) throws InvalidDescriptionException;

   Pattern[] getPluginFileFilters();

   Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener var1, Plugin var2);

   void enablePlugin(Plugin var1);

   void disablePlugin(Plugin var1);
}
