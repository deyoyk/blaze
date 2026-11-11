package org.bukkit.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public class HandlerList {
   private volatile RegisteredListener[] handlers = null;
   private final EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerslots = new EnumMap<>(EventPriority.class);
   private static ArrayList<HandlerList> allLists = new ArrayList<>();

   public static void bakeAll() {
      synchronized (allLists) {
         for (HandlerList h : allLists) {
            h.bake();
         }
      }
   }

   public static void unregisterAll() {
      synchronized (allLists) {
         for (HandlerList h : allLists) {
            synchronized (h) {
               for (List<RegisteredListener> list : h.handlerslots.values()) {
                  list.clear();
               }

               h.handlers = null;
            }
         }
      }
   }

   public static void unregisterAll(Plugin plugin) {
      synchronized (allLists) {
         for (HandlerList h : allLists) {
            h.unregister(plugin);
         }
      }
   }

   public static void unregisterAll(Listener listener) {
      synchronized (allLists) {
         for (HandlerList h : allLists) {
            h.unregister(listener);
         }
      }
   }

   public HandlerList() {
      for (EventPriority o : EventPriority.values()) {
         this.handlerslots.put(o, new ArrayList<>());
      }

      synchronized (allLists) {
         allLists.add(this);
      }
   }

   public synchronized void register(RegisteredListener listener) {
      if (this.handlerslots.get(listener.getPriority()).contains(listener)) {
         throw new IllegalStateException("This listener is already registered to priority " + listener.getPriority().toString());
      } else {
         this.handlers = null;
         this.handlerslots.get(listener.getPriority()).add(listener);
      }
   }

   public void registerAll(Collection<RegisteredListener> listeners) {
      for (RegisteredListener listener : listeners) {
         this.register(listener);
      }
   }

   public synchronized void unregister(RegisteredListener listener) {
      if (this.handlerslots.get(listener.getPriority()).remove(listener)) {
         this.handlers = null;
      }
   }

   public synchronized void unregister(Plugin plugin) {
      boolean changed = false;

      for (List<RegisteredListener> list : this.handlerslots.values()) {
         ListIterator<RegisteredListener> i = list.listIterator();

         while (i.hasNext()) {
            if (i.next().getPlugin().equals(plugin)) {
               i.remove();
               changed = true;
            }
         }
      }

      if (changed) {
         this.handlers = null;
      }
   }

   public synchronized void unregister(Listener listener) {
      boolean changed = false;

      for (List<RegisteredListener> list : this.handlerslots.values()) {
         ListIterator<RegisteredListener> i = list.listIterator();

         while (i.hasNext()) {
            if (i.next().getListener().equals(listener)) {
               i.remove();
               changed = true;
            }
         }
      }

      if (changed) {
         this.handlers = null;
      }
   }

   public synchronized void bake() {
      if (this.handlers == null) {
         List<RegisteredListener> entries = new ArrayList<>();

         for (Entry<EventPriority, ArrayList<RegisteredListener>> entry : this.handlerslots.entrySet()) {
            entries.addAll(entry.getValue());
         }

         this.handlers = entries.toArray(new RegisteredListener[entries.size()]);
      }
   }

   public RegisteredListener[] getRegisteredListeners() {
      while (true) {
         RegisteredListener[] handlers = this.handlers;
         if (this.handlers != null) {
            return handlers;
         }

         this.bake();
      }
   }

   public static ArrayList<RegisteredListener> getRegisteredListeners(Plugin plugin) {
      ArrayList<RegisteredListener> listeners = new ArrayList<>();
      synchronized (allLists) {
         for (HandlerList h : allLists) {
            synchronized (h) {
               for (List<RegisteredListener> list : h.handlerslots.values()) {
                  for (RegisteredListener listener : list) {
                     if (listener.getPlugin().equals(plugin)) {
                        listeners.add(listener);
                     }
                  }
               }
            }
         }

         return listeners;
      }
   }

   public static ArrayList<HandlerList> getHandlerLists() {
      synchronized (allLists) {
         return (ArrayList<HandlerList>)allLists.clone();
      }
   }
}
