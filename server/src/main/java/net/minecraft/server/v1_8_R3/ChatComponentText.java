package net.minecraft.server.v1_8_R3;

public class ChatComponentText extends ChatBaseComponent {
   private final String b;

   public ChatComponentText(String var1) {
      this.b = var1;
   }

   public String g() {
      return this.b;
   }

   @Override
   public String getText() {
      return this.b;
   }

   @Override
   public ChatComponentText f() {
      ChatComponentText var1 = new ChatComponentText(this.b);
      var1.setChatModifier(this.getChatModifier().clone());

      for (IChatBaseComponent var3 : this.a()) {
         var1.addSibling(var3.f());
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChatComponentText)) {
         return false;
      } else {
         ChatComponentText var2 = (ChatComponentText)var1;
         return this.b.equals(var2.g()) && super.equals(var1);
      }
   }

   @Override
   public String toString() {
      return "TextComponent{text='" + this.b + '\'' + ", siblings=" + this.a + ", style=" + this.getChatModifier() + '}';
   }
}
