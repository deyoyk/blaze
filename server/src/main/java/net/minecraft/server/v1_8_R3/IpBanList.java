package net.minecraft.server.v1_8_R3;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class IpBanList extends JsonList<String, IpBanEntry> {
   public IpBanList(File var1) {
      super(var1);
   }

   @Override
   protected JsonListEntry<String> a(JsonObject var1) {
      return new IpBanEntry(var1);
   }

   public boolean isBanned(SocketAddress var1) {
      String var2 = this.c(var1);
      return this.d(var2);
   }

   public IpBanEntry get(SocketAddress var1) {
      String var2 = this.c(var1);
      return this.get(var2);
   }

   private String c(SocketAddress var1) {
      String var2 = var1.toString();
      if (var2.contains("/")) {
         var2 = var2.substring(var2.indexOf(47) + 1);
      }

      if (var2.contains(":")) {
         var2 = var2.substring(0, var2.indexOf(58));
      }

      return var2;
   }
}
