package net.minecraft.server.v1_8_R3;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;

public class ServerPing {
   private IChatBaseComponent a;
   private ServerPing.ServerPingPlayerSample b;
   private ServerPing.ServerData c;
   private String d;

   public IChatBaseComponent a() {
      return this.a;
   }

   public void setMOTD(IChatBaseComponent ichatbasecomponent) {
      this.a = ichatbasecomponent;
   }

   public ServerPing.ServerPingPlayerSample b() {
      return this.b;
   }

   public void setPlayerSample(ServerPing.ServerPingPlayerSample serverping_serverpingplayersample) {
      this.b = serverping_serverpingplayersample;
   }

   public ServerPing.ServerData c() {
      return this.c;
   }

   public void setServerInfo(ServerPing.ServerData serverping_serverdata) {
      this.c = serverping_serverdata;
   }

   public void setFavicon(String s) {
      this.d = s;
   }

   public String d() {
      return this.d;
   }

   public static class Serializer implements JsonDeserializer<ServerPing>, JsonSerializer<ServerPing> {
      public ServerPing a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
         JsonObject jsonobject = ChatDeserializer.l(jsonelement, "status");
         ServerPing serverping = new ServerPing();
         if (jsonobject.has("description")) {
            serverping.setMOTD((IChatBaseComponent)jsondeserializationcontext.deserialize(jsonobject.get("description"), IChatBaseComponent.class));
         }

         if (jsonobject.has("players")) {
            serverping.setPlayerSample(
               (ServerPing.ServerPingPlayerSample)jsondeserializationcontext.deserialize(jsonobject.get("players"), ServerPing.ServerPingPlayerSample.class)
            );
         }

         if (jsonobject.has("version")) {
            serverping.setServerInfo((ServerPing.ServerData)jsondeserializationcontext.deserialize(jsonobject.get("version"), ServerPing.ServerData.class));
         }

         if (jsonobject.has("favicon")) {
            serverping.setFavicon(ChatDeserializer.h(jsonobject, "favicon"));
         }

         return serverping;
      }

      public JsonElement a(ServerPing serverping, Type type, JsonSerializationContext jsonserializationcontext) {
         JsonObject jsonobject = new JsonObject();
         if (serverping.a() != null) {
            jsonobject.add("description", jsonserializationcontext.serialize(serverping.a()));
         }

         if (serverping.b() != null) {
            jsonobject.add("players", jsonserializationcontext.serialize(serverping.b()));
         }

         if (serverping.c() != null) {
            jsonobject.add("version", jsonserializationcontext.serialize(serverping.c()));
         }

         if (serverping.d() != null) {
            jsonobject.addProperty("favicon", serverping.d());
         }

         return jsonobject;
      }

      public JsonElement serialize(ServerPing serverPing, Type type, JsonSerializationContext jsonSerializationContext) {
         return this.a(serverPing, type, jsonSerializationContext);
      }

      public ServerPing deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
         return this.a(jsonelement, type, jsondeserializationcontext);
      }
   }

   public static class ServerData {
      private final String a;
      private final int b;

      public ServerData(String s, int i) {
         this.a = s;
         this.b = i;
      }

      public String a() {
         return this.a;
      }

      public int b() {
         return this.b;
      }
   }

   public static class ServerPingPlayerSample {
      private final int a;
      private final int b;
      private GameProfile[] c;

      public ServerPingPlayerSample(int i, int j) {
         this.a = i;
         this.b = j;
      }

      public int a() {
         return this.a;
      }

      public int b() {
         return this.b;
      }

      public GameProfile[] c() {
         return this.c;
      }

      public void a(GameProfile[] agameprofile) {
         this.c = agameprofile;
      }
   }
}
