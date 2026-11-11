package net.minecraft.server.v1_8_R3;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class OpList extends JsonList<GameProfile, OpListEntry> {
   public OpList(File var1) {
      super(var1);
   }

   @Override
   protected JsonListEntry<GameProfile> a(JsonObject var1) {
      return new OpListEntry(var1);
   }

   @Override
   public String[] getEntries() {
      String[] var1 = new String[this.e().size()];
      int var2 = 0;

      for (OpListEntry var4 : this.e().values()) {
         var1[var2++] = var4.getKey().getName();
      }

      return var1;
   }

   public boolean b(GameProfile var1) {
      OpListEntry var2 = this.get(var1);
      return var2 != null ? var2.b() : false;
   }

   protected String c(GameProfile var1) {
      return var1.getId().toString();
   }

   public GameProfile a(String var1) {
      for (OpListEntry var3 : this.e().values()) {
         if (var1.equalsIgnoreCase(var3.getKey().getName())) {
            return var3.getKey();
         }
      }

      return null;
   }
}
