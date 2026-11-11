package org.bukkit.plugin.messaging;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface Messenger {
   int MAX_MESSAGE_SIZE = 32766;
   int MAX_CHANNEL_SIZE = 20;

   boolean isReservedChannel(String var1);

   void registerOutgoingPluginChannel(Plugin var1, String var2);

   void unregisterOutgoingPluginChannel(Plugin var1, String var2);

   void unregisterOutgoingPluginChannel(Plugin var1);

   PluginMessageListenerRegistration registerIncomingPluginChannel(Plugin var1, String var2, PluginMessageListener var3);

   void unregisterIncomingPluginChannel(Plugin var1, String var2, PluginMessageListener var3);

   void unregisterIncomingPluginChannel(Plugin var1, String var2);

   void unregisterIncomingPluginChannel(Plugin var1);

   Set<String> getOutgoingChannels();

   Set<String> getOutgoingChannels(Plugin var1);

   Set<String> getIncomingChannels();

   Set<String> getIncomingChannels(Plugin var1);

   Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(Plugin var1);

   Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(String var1);

   Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(Plugin var1, String var2);

   boolean isRegistrationValid(PluginMessageListenerRegistration var1);

   boolean isIncomingChannelRegistered(Plugin var1, String var2);

   boolean isOutgoingChannelRegistered(Plugin var1, String var2);

   void dispatchIncomingMessage(Player var1, String var2, byte[] var3);
}
