package net.minecraft.server.v1_8_R3;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class WhiteList extends JsonList<GameProfile, WhiteListEntry> {
   public WhiteList(File file) {
      super(file);
   }

   @Override
   protected JsonListEntry<GameProfile> a(JsonObject jsonobject) {
      return new WhiteListEntry(jsonobject);
   }

   public boolean isWhitelisted(GameProfile gameprofile) {
      return this.d(gameprofile);
   }

   @Override
   public String[] getEntries() {
      String[] astring = new String[this.e().size()];
      int i = 0;

      for (WhiteListEntry whitelistentry : this.e().values()) {
         astring[i++] = whitelistentry.getKey().getName();
      }

      return astring;
   }

   protected String b(GameProfile gameprofile) {
      return gameprofile.getId().toString();
   }

   public GameProfile a(String s) {
      for (WhiteListEntry whitelistentry : this.e().values()) {
         if (s.equalsIgnoreCase(whitelistentry.getKey().getName())) {
            return whitelistentry.getKey();
         }
      }

      return null;
   }

   protected String a(GameProfile object) {
      return this.b(object);
   }
}
