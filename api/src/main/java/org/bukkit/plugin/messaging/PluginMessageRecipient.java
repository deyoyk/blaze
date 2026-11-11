package org.bukkit.plugin.messaging;

import java.util.Set;
import org.bukkit.plugin.Plugin;

public interface PluginMessageRecipient {
   void sendPluginMessage(Plugin var1, String var2, byte[] var3);

   Set<String> getListeningPluginChannels();
}
