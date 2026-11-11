package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Map;

public class ChatHoverable {
   private final ChatHoverable.EnumHoverAction a;
   private final IChatBaseComponent b;

   public ChatHoverable(ChatHoverable.EnumHoverAction var1, IChatBaseComponent var2) {
      this.a = var1;
      this.b = var2;
   }

   public ChatHoverable.EnumHoverAction a() {
      return this.a;
   }

   public IChatBaseComponent b() {
      return this.b;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ChatHoverable var2 = (ChatHoverable)var1;
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
      return "HoverEvent{action=" + this.a + ", value='" + this.b + '\'' + '}';
   }

   @Override
   public int hashCode() {
      int var1 = this.a.hashCode();
      return 31 * var1 + (this.b != null ? this.b.hashCode() : 0);
   }

   public static enum EnumHoverAction {
      SHOW_TEXT("show_text", true),
      SHOW_ACHIEVEMENT("show_achievement", true),
      SHOW_ITEM("show_item", true),
      SHOW_ENTITY("show_entity", true);

      private static final Map<String, ChatHoverable.EnumHoverAction> e = Maps.newHashMap();
      private final boolean f;
      private final String g;

      private EnumHoverAction(String var3, boolean var4) {
         this.g = var3;
         this.f = var4;
      }

      public boolean a() {
         return this.f;
      }

      public String b() {
         return this.g;
      }

      public static ChatHoverable.EnumHoverAction a(String var0) {
         return e.get(var0);
      }

      static {
         for (ChatHoverable.EnumHoverAction var3 : values()) {
            e.put(var3.b(), var3);
         }
      }
   }
}
