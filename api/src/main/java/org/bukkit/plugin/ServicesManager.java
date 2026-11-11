package org.bukkit.plugin;

import java.util.Collection;
import java.util.List;

public interface ServicesManager {
   <T> void register(Class<T> var1, T var2, Plugin var3, ServicePriority var4);

   void unregisterAll(Plugin var1);

   void unregister(Class<?> var1, Object var2);

   void unregister(Object var1);

   <T> T load(Class<T> var1);

   <T> RegisteredServiceProvider<T> getRegistration(Class<T> var1);

   List<RegisteredServiceProvider<?>> getRegistrations(Plugin var1);

   <T> Collection<RegisteredServiceProvider<T>> getRegistrations(Class<T> var1);

   Collection<Class<?>> getKnownServices();

   <T> boolean isProvidedFor(Class<T> var1);
}
