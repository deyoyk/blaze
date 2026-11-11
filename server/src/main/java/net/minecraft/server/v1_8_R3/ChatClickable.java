package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Map;

public class ChatClickable {
   private final ChatClickable.EnumClickAction a;
   private final String b;

   public ChatClickable(ChatClickable.EnumClickAction var1, String var2) {
      this.a = var1;
      this.b = var2;
   }

   public ChatClickable.EnumClickAction a() {
      return this.a;
   }

   public String b() {
      return this.b;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ChatClickable var2 = (ChatClickable)var1;
         if (this.a != var2.a) {
            return false;
         } else {
            return this.b != null ? this.b.equals(var2.b) : var2.b == null;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "ClickEvent{action=" + this.a + ", value='" + this.b + '\'' + '}';
   }

   @Override
   public int hashCode() {
      int var1 = this.a.hashCode();
      return 31 * var1 + (this.b != null ? this.b.hashCode() : 0);
   }

   public static enum EnumClickAction {
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      TWITCH_USER_INFO("twitch_user_info", false),
      SUGGEST_COMMAND("suggest_command", true),
      CHANGE_PAGE("change_page", true);

      private static final Map<String, ChatClickable.EnumClickAction> g = Maps.newHashMap();
      private final boolean h;
      private final String i;

      private EnumClickAction(String var3, boolean var4) {
         this.i = var3;
         this.h = var4;
      }

      public boolean a() {
         return this.h;
      }

      public String b() {
         return this.i;
      }

      public static ChatClickable.EnumClickAction a(String var0) {
         return g.get(var0);
      }

      static {
         for (ChatClickable.EnumClickAction var3 : values()) {
            g.put(var3.b(), var3);
         }
      }
   }
}
