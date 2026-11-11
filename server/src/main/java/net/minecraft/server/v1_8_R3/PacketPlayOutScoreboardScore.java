package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutScoreboardScore implements Packet<PacketListenerPlayOut> {
   private String a = "";
   private String b = "";
   private int c;
   private PacketPlayOutScoreboardScore.EnumScoreboardAction d;

   public PacketPlayOutScoreboardScore(ScoreboardScore score) {
      this.a = score.getPlayerName();
      this.b = score.getObjective().getName();
      this.c = score.getScore();
      this.d = PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE;
   }

   public PacketPlayOutScoreboardScore(String player) {
      this.a = player;
      this.c = 0;
      this.d = PacketPlayOutScoreboardScore.EnumScoreboardAction.REMOVE;
   }

   public PacketPlayOutScoreboardScore(String player, ScoreboardObjective objective) {
      this.a = player;
      this.b = objective.getName();
      this.c = 0;
      this.d = PacketPlayOutScoreboardScore.EnumScoreboardAction.REMOVE;
   }

   public PacketPlayOutScoreboardScore() {
   }

   @Override
   public void a(PacketDataSerializer serializer) throws IOException {
      this.a = serializer.c(40);
      this.d = serializer.a(PacketPlayOutScoreboardScore.EnumScoreboardAction.class);
      this.b = serializer.c(16);
      if (this.d != PacketPlayOutScoreboardScore.EnumScoreboardAction.REMOVE) {
         this.c = serializer.e();
      }
   }

   @Override
   public void b(PacketDataSerializer serializer) throws IOException {
      serializer.a(this.a);
      serializer.a(this.d);
      serializer.a(this.b);
      if (this.d != PacketPlayOutScoreboardScore.EnumScoreboardAction.REMOVE) {
         serializer.b(this.c);
      }
   }

   public void a(PacketListenerPlayOut listener) {
      listener.a(this);
   }

   public static enum EnumScoreboardAction {
      CHANGE,
      REMOVE;
   }
}
