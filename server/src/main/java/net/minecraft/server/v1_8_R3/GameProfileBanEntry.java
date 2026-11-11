package net.minecraft.server.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class GameProfileBanEntry extends ExpirableListEntry<GameProfile> {
   public GameProfileBanEntry(GameProfile gameprofile) {
      this(gameprofile, null, null, null, null);
   }

   public GameProfileBanEntry(GameProfile gameprofile, Date date, String s, Date date1, String s1) {
      super(gameprofile, date, s, date1, s1);
   }

   public GameProfileBanEntry(JsonObject jsonobject) {
      super(b(jsonobject), jsonobject);
   }

   @Override
   protected void a(JsonObject jsonobject) {
      if (this.getKey() != null) {
         jsonobject.addProperty("uuid", this.getKey().getId() == null ? "" : FastUUID.toString(this.getKey().getId()));
         jsonobject.addProperty("name", this.getKey().getName());
         super.a(jsonobject);
      }
   }

   private static GameProfile b(JsonObject jsonobject) {
      UUID uuid = null;
      String name = null;
      if (jsonobject.has("uuid")) {
         String s = jsonobject.get("uuid").getAsString();

         try {
            uuid = FastUUID.parseUUID(s);
         } catch (Throwable var5) {
         }
      }

      if (jsonobject.has("name")) {
         name = jsonobject.get("name").getAsString();
      }

      return uuid == null && name == null ? null : new GameProfile(uuid, name);
   }
}
