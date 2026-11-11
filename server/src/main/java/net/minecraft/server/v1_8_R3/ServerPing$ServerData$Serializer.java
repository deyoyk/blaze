package net.minecraft.server.v1_8_R3;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class ServerPing$ServerData$Serializer implements JsonDeserializer<ServerPing.ServerData>, JsonSerializer<ServerPing.ServerData> {
   @Override
   public ServerPing.ServerData deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = ChatDeserializer.l(var1, "version");
      return new ServerPing.ServerData(ChatDeserializer.h(var4, "name"), ChatDeserializer.m(var4, "protocol"));
   }

   @Override
   public JsonElement serialize(ServerPing.ServerData var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("name", var1.a());
      var4.addProperty("protocol", var1.b());
      return var4;
   }
}
