package org.bukkit.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

public class SimpleServicesManager implements ServicesManager {
   private final Map<Class<?>, List<RegisteredServiceProvider<?>>> providers = new HashMap<>();

   @Override
   public <T> void register(Class<T> service, T provider, Plugin plugin, ServicePriority priority) {
      RegisteredServiceProvider<T> registeredProvider = null;
      synchronized (this.providers) {
         List<RegisteredServiceProvider<?>> registered = this.providers.get(service);
         if (registered == null) {
            registered = new ArrayList<>();
            this.providers.put(service, registered);
         }

         registeredProvider = new RegisteredServiceProvider<>(service, provider, priority, plugin);
         int position = Collections.binarySearch(registered, registeredProvider);
         if (position < 0) {
            registered.add(-(position + 1), registeredProvider);
         } else {
            registered.add(position, registeredProvider);
         }
      }

      org.bukkit.Bukkit.getServer().getPluginManager().callEvent(new ServiceRegisterEvent(registeredProvider));
   }

   @Override
   public void unregisterAll(Plugin plugin) {
      ArrayList<ServiceUnregisterEvent> unregisteredEvents = new ArrayList<>();
      synchronized (this.providers) {
         Iterator<Entry<Class<?>, List<RegisteredServiceProvider<?>>>> it = this.providers.entrySet().iterator();

         try {
            while (it.hasNext()) {
               Entry<Class<?>, List<RegisteredServiceProvider<?>>> entry = it.next();
               Iterator<RegisteredServiceProvider<?>> it2 = entry.getValue().iterator();

               try {
                  while (it2.hasNext()) {
                     RegisteredServiceProvider<?> registered = it2.next();
                     if (registered.getPlugin().equals(plugin)) {
                        it2.remove();
                        unregisteredEvents.add(new ServiceUnregisterEvent(registered));
                     }
                  }
               } catch (NoSuchElementException var9) {
               }

               if (entry.getValue().size() == 0) {
                  it.remove();
               }
            }
         } catch (NoSuchElementException var10) {
         }
      }

      for (ServiceUnregisterEvent event : unregisteredEvents) {
         org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
      }
   }

   @Override
   public void unregister(Class<?> service, Object provider) {
      ArrayList<ServiceUnregisterEvent> unregisteredEvents = new ArrayList<>();
      synchronized (this.providers) {
         Iterator<Entry<Class<?>, List<RegisteredServiceProvider<?>>>> it = this.providers.entrySet().iterator();

         try {
            while (it.hasNext()) {
               Entry<Class<?>, List<RegisteredServiceProvider<?>>> entry = it.next();
               if (entry.getKey() == service) {
                  Iterator<RegisteredServiceProvider<?>> it2 = entry.getValue().iterator();

                  try {
                     while (it2.hasNext()) {
                        RegisteredServiceProvider<?> registered = it2.next();
                        if (registered.getProvider() == provider) {
                           it2.remove();
                           unregisteredEvents.add(new ServiceUnregisterEvent(registered));
                        }
                     }
                  } catch (NoSuchElementException var10) {
                  }

                  if (entry.getValue().size() == 0) {
                     it.remove();
                  }
               }
            }
         } catch (NoSuchElementException var11) {
         }
      }

      for (ServiceUnregisterEvent event : unregisteredEvents) {
         org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
      }
   }

   @Override
   public void unregister(Object provider) {
      ArrayList<ServiceUnregisterEvent> unregisteredEvents = new ArrayList<>();
      synchronized (this.providers) {
         Iterator<Entry<Class<?>, List<RegisteredServiceProvider<?>>>> it = this.providers.entrySet().iterator();

         try {
            while (it.hasNext()) {
               Entry<Class<?>, List<RegisteredServiceProvider<?>>> entry = it.next();
               Iterator<RegisteredServiceProvider<?>> it2 = entry.getValue().iterator();

               try {
                  while (it2.hasNext()) {
                     RegisteredServiceProvider<?> registered = it2.next();
                     if (registered.getProvider().equals(provider)) {
                        it2.remove();
                        unregisteredEvents.add(new ServiceUnregisterEvent(registered));
                     }
                  }
               } catch (NoSuchElementException var9) {
               }

               if (entry.getValue().size() == 0) {
                  it.remove();
               }
            }
         } catch (NoSuchElementException var10) {
         }
      }

      for (ServiceUnregisterEvent event : unregisteredEvents) {
         org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
      }
   }

   @Override
   public <T> T load(Class<T> service) {
      synchronized (this.providers) {
         List<RegisteredServiceProvider<?>> registered = this.providers.get(service);
         return registered == null ? null : service.cast(registered.get(0).getProvider());
      }
   }

   @Override
   public <T> RegisteredServiceProvider<T> getRegistration(Class<T> service) {
      synchronized (this.providers) {
         List<RegisteredServiceProvider<?>> registered = this.providers.get(service);
         return registered == null ? null : (RegisteredServiceProvider)registered.get(0);
      }
   }

   @Override
   public List<RegisteredServiceProvider<?>> getRegistrations(Plugin plugin) {
      Builder<RegisteredServiceProvider<?>> ret = ImmutableList.builder();
      synchronized (this.providers) {
         for (List<RegisteredServiceProvider<?>> registered : this.providers.values()) {
            for (RegisteredServiceProvider<?> provider : registered) {
               if (provider.getPlugin().equals(plugin)) {
                  ret.add(provider);
               }
            }
         }
      }

      return ret.build();
   }

   public <T> List<RegisteredServiceProvider<T>> getRegistrations(Class<T> service) {
      Builder<RegisteredServiceProvider<T>> ret;
      synchronized (this.providers) {
         List<RegisteredServiceProvider<?>> registered = this.providers.get(service);
         if (registered == null) {
            return ImmutableList.of();
         }

         ret = ImmutableList.builder();

         for (RegisteredServiceProvider<?> provider : registered) {
            ret.add((RegisteredServiceProvider<T>) provider);
         }
      }

      return ret.build();
   }

   public Set<Class<?>> getKnownServices() {
      synchronized (this.providers) {
         return ImmutableSet.copyOf(this.providers.keySet());
      }
   }

   @Override
   public <T> boolean isProvidedFor(Class<T> service) {
      synchronized (this.providers) {
         return this.providers.containsKey(service);
      }
   }
}
