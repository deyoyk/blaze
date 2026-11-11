package net.minecraft.server.v1_8_R3;

import com.google.gson.JsonObject;
import java.util.Date;

public class IpBanEntry extends ExpirableListEntry<String> {
   public IpBanEntry(String var1) {
      this(var1, null, null, null, null);
   }

   public IpBanEntry(String var1, Date var2, String var3, Date var4, String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public IpBanEntry(JsonObject var1) {
      super(b(var1), var1);
   }

   private static String b(JsonObject var0) {
      return var0.has("ip") ? var0.get("ip").getAsString() : null;
   }

   @Override
   protected void a(JsonObject var1) {
      if (this.getKey() != null) {
         var1.addProperty("ip", this.getKey());
         super.a(var1);
      }
   }
}
