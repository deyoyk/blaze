package net.minecraft.server.v1_8_R3;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;

public class ServerPing$ServerPingPlayerSample$Serializer
   implements JsonDeserializer<ServerPing.ServerPingPlayerSample>,
   JsonSerializer<ServerPing.ServerPingPlayerSample> {
   @Override
   public ServerPing.ServerPingPlayerSample deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = ChatDeserializer.l(var1, "players");
      ServerPing.ServerPingPlayerSample var5 = new ServerPing.ServerPingPlayerSample(ChatDeserializer.m(var4, "max"), ChatDeserializer.m(var4, "online"));
      if (ChatDeserializer.d(var4, "sample")) {
         JsonArray var6 = ChatDeserializer.t(var4, "sample");
         if (var6.size() > 0) {
            GameProfile[] var7 = new GameProfile[var6.size()];

            for (int var8 = 0; var8 < var7.length; var8++) {
               JsonObject var9 = ChatDeserializer.l(var6.get(var8), "player[" + var8 + "]");
               String var10 = ChatDeserializer.h(var9, "id");
               var7[var8] = new GameProfile(UUID.fromString(var10), ChatDeserializer.h(var9, "name"));
            }

            var5.a(var7);
         }
      }

      return var5;
   }

   @Override
   public JsonElement serialize(ServerPing.ServerPingPlayerSample var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("max", var1.a());
      var4.addProperty("online", var1.b());
      if (var1.c() != null && var1.c().length > 0) {
         JsonArray var5 = new JsonArray();

         for (int var6 = 0; var6 < var1.c().length; var6++) {
            JsonObject var7 = new JsonObject();
            UUID var8 = var1.c()[var6].getId();
            var7.addProperty("id", var8 == null ? "" : var8.toString());
            var7.addProperty("name", var1.c()[var6].getName());
            var5.add(var7);
         }

         var4.add("sample", var5);
      }

      return var4;
   }
}
