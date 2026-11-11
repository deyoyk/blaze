package net.minecraft.server.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class GameProfileBanList extends JsonList<GameProfile, GameProfileBanEntry> {
   public GameProfileBanList(File file) {
      super(file);
   }

   @Override
   protected JsonListEntry<GameProfile> a(JsonObject jsonobject) {
      return new GameProfileBanEntry(jsonobject);
   }

   public boolean isBanned(GameProfile gameprofile) {
      return this.d(gameprofile);
   }

   @Override
   public String[] getEntries() {
      String[] astring = new String[this.e().size()];
      int i = 0;

      for (GameProfileBanEntry gameprofilebanentry : this.e().values()) {
         astring[i++] = gameprofilebanentry.getKey().getName();
      }

      return astring;
   }

   protected String b(GameProfile gameprofile) {
      return FastUUID.toString(gameprofile.getId());
   }

   public GameProfile a(String s) {
      for (GameProfileBanEntry gameprofilebanentry : this.e().values()) {
         if (s.equalsIgnoreCase(gameprofilebanentry.getKey().getName())) {
            return gameprofilebanentry.getKey();
         }
      }

      return null;
   }

   protected String a(GameProfile object) {
      return this.b(object);
   }
}
