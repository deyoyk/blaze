package net.minecraft.server.v1_8_R3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;

public class PacketStatusOutServerInfo implements Packet<PacketStatusOutListener> {
   private static final Gson a = new GsonBuilder()
      .registerTypeAdapter(ServerPing.ServerData.class, new ServerPing$ServerData$Serializer())
      .registerTypeAdapter(ServerPing.ServerPingPlayerSample.class, new ServerPing$ServerPingPlayerSample$Serializer())
      .registerTypeAdapter(ServerPing.class, new ServerPing.Serializer())
      .registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer())
      .registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer())
      .registerTypeAdapterFactory(new ChatTypeAdapterFactory())
      .create();
   private ServerPing b;

   public PacketStatusOutServerInfo() {
   }

   public PacketStatusOutServerInfo(ServerPing var1) {
      this.b = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.b = (ServerPing)a.fromJson(var1.c(32767), ServerPing.class);
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(a.toJson(this.b));
   }

   public void a(PacketStatusOutListener var1) {
      var1.a(this);
   }
}
