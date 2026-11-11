package net.minecraft.server.v1_8_R3;

public class ChatComponentScore extends ChatBaseComponent {
   private final String b;
   private final String c;
   private String d = "";

   public ChatComponentScore(String var1, String var2) {
      this.b = var1;
      this.c = var2;
   }

   public String g() {
      return this.b;
   }

   public String h() {
      return this.c;
   }

   public void b(String var1) {
      this.d = var1;
   }

   @Override
   public String getText() {
      MinecraftServer var1 = MinecraftServer.getServer();
      if (var1 != null && var1.O() && UtilColor.b(this.d)) {
         Scoreboard var2 = var1.getWorldServer(0).getScoreboard();
         ScoreboardObjective var3 = var2.getObjective(this.c);
         if (var2.b(this.b, var3)) {
            ScoreboardScore var4 = var2.getPlayerScoreForObjective(this.b, var3);
            this.b(String.format("%d", var4.getScore()));
         } else {
            this.d = "";
         }
      }

      return this.d;
   }

   @Override
   public ChatComponentScore f() {
      ChatComponentScore var1 = new ChatComponentScore(this.b, this.c);
      var1.b(this.d);
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
      } else if (!(var1 instanceof ChatComponentScore)) {
         return false;
      } else {
         ChatComponentScore var2 = (ChatComponentScore)var1;
         return this.b.equals(var2.b) && this.c.equals(var2.c) && super.equals(var1);
      }
   }

   @Override
   public String toString() {
      return "ScoreComponent{name='" + this.b + '\'' + "objective='" + this.c + '\'' + ", siblings=" + this.a + ", style=" + this.getChatModifier() + '}';
   }
}
