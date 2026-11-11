package org.bukkit.plugin.messaging;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class StandardMessenger implements Messenger {
   private final Map<String, Set<PluginMessageListenerRegistration>> incomingByChannel = new HashMap<>();
   private final Map<Plugin, Set<PluginMessageListenerRegistration>> incomingByPlugin = new HashMap<>();
   private final Map<String, Set<Plugin>> outgoingByChannel = new HashMap<>();
   private final Map<Plugin, Set<String>> outgoingByPlugin = new HashMap<>();
   private final Object incomingLock = new Object();
   private final Object outgoingLock = new Object();

   private void addToOutgoing(Plugin plugin, String channel) {
      synchronized (this.outgoingLock) {
         Set<Plugin> plugins = this.outgoingByChannel.get(channel);
         Set<String> channels = this.outgoingByPlugin.get(plugin);
         if (plugins == null) {
            plugins = new HashSet<>();
            this.outgoingByChannel.put(channel, plugins);
         }

         if (channels == null) {
            channels = new HashSet<>();
            this.outgoingByPlugin.put(plugin, channels);
         }

         plugins.add(plugin);
         channels.add(channel);
      }
   }

   private void removeFromOutgoing(Plugin plugin, String channel) {
      synchronized (this.outgoingLock) {
         Set<Plugin> plugins = this.outgoingByChannel.get(channel);
         Set<String> channels = this.outgoingByPlugin.get(plugin);
         if (plugins != null) {
            plugins.remove(plugin);
            if (plugins.isEmpty()) {
               this.outgoingByChannel.remove(channel);
            }
         }

         if (channels != null) {
            channels.remove(channel);
            if (channels.isEmpty()) {
               this.outgoingByChannel.remove(channel);
            }
         }
      }
   }

   private void removeFromOutgoing(Plugin plugin) {
      synchronized (this.outgoingLock) {
         Set<String> channels = this.outgoingByPlugin.get(plugin);
         if (channels != null) {
            String[] toRemove = channels.toArray(new String[0]);
            this.outgoingByPlugin.remove(plugin);

            for (String channel : toRemove) {
               this.removeFromOutgoing(plugin, channel);
            }
         }
      }
   }

   private void addToIncoming(PluginMessageListenerRegistration registration) {
      synchronized (this.incomingLock) {
         Set<PluginMessageListenerRegistration> registrations = this.incomingByChannel.get(registration.getChannel());
         if (registrations == null) {
            registrations = new HashSet<>();
            this.incomingByChannel.put(registration.getChannel(), registrations);
         } else if (registrations.contains(registration)) {
            throw new IllegalArgumentException("This registration already exists");
         }

         registrations.add(registration);
         registrations = this.incomingByPlugin.get(registration.getPlugin());
         if (registrations == null) {
            registrations = new HashSet<>();
            this.incomingByPlugin.put(registration.getPlugin(), registrations);
         } else if (registrations.contains(registration)) {
            throw new IllegalArgumentException("This registration already exists");
         }

         registrations.add(registration);
      }
   }

   private void removeFromIncoming(PluginMessageListenerRegistration registration) {
      synchronized (this.incomingLock) {
         Set<PluginMessageListenerRegistration> registrations = this.incomingByChannel.get(registration.getChannel());
         if (registrations != null) {
            registrations.remove(registration);
            if (registrations.isEmpty()) {
               this.incomingByChannel.remove(registration.getChannel());
            }
         }

         registrations = this.incomingByPlugin.get(registration.getPlugin());
         if (registrations != null) {
            registrations.remove(registration);
            if (registrations.isEmpty()) {
               this.incomingByPlugin.remove(registration.getPlugin());
            }
         }
      }
   }

   private void removeFromIncoming(Plugin plugin, String channel) {
      synchronized (this.incomingLock) {
         Set<PluginMessageListenerRegistration> registrations = this.incomingByPlugin.get(plugin);
         if (registrations != null) {
            PluginMessageListenerRegistration[] toRemove = registrations.toArray(new PluginMessageListenerRegistration[0]);

            for (PluginMessageListenerRegistration registration : toRemove) {
               if (registration.getChannel().equals(channel)) {
                  this.removeFromIncoming(registration);
               }
            }
         }
      }
   }

   private void removeFromIncoming(Plugin plugin) {
      synchronized (this.incomingLock) {
         Set<PluginMessageListenerRegistration> registrations = this.incomingByPlugin.get(plugin);
         if (registrations != null) {
            PluginMessageListenerRegistration[] toRemove = registrations.toArray(new PluginMessageListenerRegistration[0]);
            this.incomingByPlugin.remove(plugin);

            for (PluginMessageListenerRegistration registration : toRemove) {
               this.removeFromIncoming(registration);
            }
         }
      }
   }

   @Override
   public boolean isReservedChannel(String channel) {
      validateChannel(channel);
      return channel.equals("REGISTER") || channel.equals("UNREGISTER");
   }

   @Override
   public void registerOutgoingPluginChannel(Plugin plugin, String channel) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         validateChannel(channel);
         if (this.isReservedChannel(channel)) {
            throw new ReservedChannelException(channel);
         } else {
            this.addToOutgoing(plugin, channel);
         }
      }
   }

   @Override
   public void unregisterOutgoingPluginChannel(Plugin plugin, String channel) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         validateChannel(channel);
         this.removeFromOutgoing(plugin, channel);
      }
   }

   @Override
   public void unregisterOutgoingPluginChannel(Plugin plugin) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         this.removeFromOutgoing(plugin);
      }
   }

   @Override
   public PluginMessageListenerRegistration registerIncomingPluginChannel(Plugin plugin, String channel, PluginMessageListener listener) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         validateChannel(channel);
         if (this.isReservedChannel(channel)) {
            throw new ReservedChannelException(channel);
         } else if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
         } else {
            PluginMessageListenerRegistration result = new PluginMessageListenerRegistration(this, plugin, channel, listener);
            this.addToIncoming(result);
            return result;
         }
      }
   }

   @Override
   public void unregisterIncomingPluginChannel(Plugin plugin, String channel, PluginMessageListener listener) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else if (listener == null) {
         throw new IllegalArgumentException("Listener cannot be null");
      } else {
         validateChannel(channel);
         this.removeFromIncoming(new PluginMessageListenerRegistration(this, plugin, channel, listener));
      }
   }

   @Override
   public void unregisterIncomingPluginChannel(Plugin plugin, String channel) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         validateChannel(channel);
         this.removeFromIncoming(plugin, channel);
      }
   }

   @Override
   public void unregisterIncomingPluginChannel(Plugin plugin) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         this.removeFromIncoming(plugin);
      }
   }

   @Override
   public Set<String> getOutgoingChannels() {
      synchronized (this.outgoingLock) {
         Set<String> keys = this.outgoingByChannel.keySet();
         return ImmutableSet.copyOf(keys);
      }
   }

   @Override
   public Set<String> getOutgoingChannels(Plugin plugin) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         synchronized (this.outgoingLock) {
            Set<String> channels = this.outgoingByPlugin.get(plugin);
            return channels != null ? ImmutableSet.copyOf(channels) : ImmutableSet.of();
         }
      }
   }

   @Override
   public Set<String> getIncomingChannels() {
      synchronized (this.incomingLock) {
         Set<String> keys = this.incomingByChannel.keySet();
         return ImmutableSet.copyOf(keys);
      }
   }

   @Override
   public Set<String> getIncomingChannels(Plugin plugin) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         synchronized (this.incomingLock) {
            Set<PluginMessageListenerRegistration> registrations = this.incomingByPlugin.get(plugin);
            if (registrations == null) {
               return ImmutableSet.of();
            } else {
               Builder<String> builder = ImmutableSet.builder();

               for (PluginMessageListenerRegistration registration : registrations) {
                  builder.add(registration.getChannel());
               }

               return builder.build();
            }
         }
      }
   }

   @Override
   public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(Plugin plugin) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         synchronized (this.incomingLock) {
            Set<PluginMessageListenerRegistration> registrations = this.incomingByPlugin.get(plugin);
            return registrations != null ? ImmutableSet.copyOf(registrations) : ImmutableSet.of();
         }
      }
   }

   @Override
   public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(String channel) {
      validateChannel(channel);
      synchronized (this.incomingLock) {
         Set<PluginMessageListenerRegistration> registrations = this.incomingByChannel.get(channel);
         return registrations != null ? ImmutableSet.copyOf(registrations) : ImmutableSet.of();
      }
   }

   @Override
   public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(Plugin plugin, String channel) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         validateChannel(channel);
         synchronized (this.incomingLock) {
            Set<PluginMessageListenerRegistration> registrations = this.incomingByPlugin.get(plugin);
            if (registrations != null) {
               Builder<PluginMessageListenerRegistration> builder = ImmutableSet.builder();

               for (PluginMessageListenerRegistration registration : registrations) {
                  if (registration.getChannel().equals(channel)) {
                     builder.add(registration);
                  }
               }

               return builder.build();
            } else {
               return ImmutableSet.of();
            }
         }
      }
   }

   @Override
   public boolean isRegistrationValid(PluginMessageListenerRegistration registration) {
      if (registration == null) {
         throw new IllegalArgumentException("Registration cannot be null");
      } else {
         synchronized (this.incomingLock) {
            Set<PluginMessageListenerRegistration> registrations = this.incomingByPlugin.get(registration.getPlugin());
            return registrations != null ? registrations.contains(registration) : false;
         }
      }
   }

   @Override
   public boolean isIncomingChannelRegistered(Plugin plugin, String channel) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         validateChannel(channel);
         synchronized (this.incomingLock) {
            Set<PluginMessageListenerRegistration> registrations = this.incomingByPlugin.get(plugin);
            if (registrations != null) {
               for (PluginMessageListenerRegistration registration : registrations) {
                  if (registration.getChannel().equals(channel)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean isOutgoingChannelRegistered(Plugin plugin, String channel) {
      if (plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         validateChannel(channel);
         synchronized (this.outgoingLock) {
            Set<String> channels = this.outgoingByPlugin.get(plugin);
            return channels != null ? channels.contains(channel) : false;
         }
      }
   }

   @Override
   public void dispatchIncomingMessage(Player source, String channel, byte[] message) {
      if (source == null) {
         throw new IllegalArgumentException("Player source cannot be null");
      } else if (message == null) {
         throw new IllegalArgumentException("Message cannot be null");
      } else {
         validateChannel(channel);

         for (PluginMessageListenerRegistration registration : this.getIncomingChannelRegistrations(channel)) {
            try {
               registration.getListener().onPluginMessageReceived(channel, source, message);
            } catch (Throwable var8) {
               org.bukkit.Bukkit.getLogger().log(Level.WARNING, "Could not pass incoming plugin message to " + registration.getPlugin(), var8);
            }
         }
      }
   }

   public static void validateChannel(String channel) {
      if (channel == null) {
         throw new IllegalArgumentException("Channel cannot be null");
      } else if (channel.length() > 20) {
         throw new ChannelNameTooLongException(channel);
      }
   }

   public static void validatePluginMessage(Messenger messenger, Plugin source, String channel, byte[] message) {
      if (messenger == null) {
         throw new IllegalArgumentException("Messenger cannot be null");
      } else if (source == null) {
         throw new IllegalArgumentException("Plugin source cannot be null");
      } else if (!source.isEnabled()) {
         throw new IllegalArgumentException("Plugin must be enabled to send messages");
      } else if (message == null) {
         throw new IllegalArgumentException("Message cannot be null");
      } else if (!messenger.isOutgoingChannelRegistered(source, channel)) {
         throw new ChannelNotRegisteredException(channel);
      } else if (message.length > 32766) {
         throw new MessageTooLargeException(message);
      } else {
         validateChannel(channel);
      }
   }
}
