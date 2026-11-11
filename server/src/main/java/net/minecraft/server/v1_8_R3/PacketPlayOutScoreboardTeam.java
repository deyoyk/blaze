package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;

public class PacketPlayOutScoreboardTeam implements Packet<PacketListenerPlayOut> {
   private String a = "";
   private String b = "";
   private String c = "";
   private String d = "";
   private String e;
   private int f;
   private Collection<String> g;
   private int h;
   private int i;

   public PacketPlayOutScoreboardTeam(ScoreboardTeam team, int action) {
      this.e = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e;
      this.f = -1;
      this.g = Lists.newArrayList();
      this.a = team.getName();
      this.h = action;
      if (action == 0 || action == 2) {
         this.b = team.getDisplayName();
         this.c = team.getPrefix();
         this.d = team.getSuffix();
         this.i = team.packOptionData();
         this.e = team.getNameTagVisibility().e;
         this.f = team.l().b();
      }

      if (action == 0) {
         this.g.addAll(team.getPlayerNameSet());
      }
   }

   public PacketPlayOutScoreboardTeam(ScoreboardTeam team, Collection<String> players, int action) {
      this.e = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e;
      this.f = -1;
      this.g = Lists.newArrayList();
      if (action != 3 && action != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if (players != null && !players.isEmpty()) {
         this.h = action;
         this.a = team.getName();
         this.g.addAll(players);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   @Override
   public void a(PacketDataSerializer serializer) throws IOException {
      this.a = serializer.c(16);
      this.h = serializer.readByte();
      if (this.h == 0 || this.h == 2) {
         this.b = serializer.c(32);
         this.c = serializer.c(16);
         this.d = serializer.c(16);
         this.i = serializer.readByte();
         this.e = serializer.c(32);
         this.f = serializer.readByte();
      }

      if (this.h == 0 || this.h == 3 || this.h == 4) {
         int j = serializer.e();

         for (byte b1 = 0; b1 < j; b1++) {
            this.g.add(serializer.c(40));
         }
      }
   }

   @Override
   public void b(PacketDataSerializer serializer) throws IOException {
      serializer.a(this.a);
      serializer.writeByte(this.h);
      if (this.h == 0 || this.h == 2) {
         serializer.a(this.b);
         serializer.a(this.c);
         serializer.a(this.d);
         serializer.writeByte(this.i);
         serializer.a(this.e);
         serializer.writeByte(this.f);
      }

      if (this.h == 0 || this.h == 3 || this.h == 4) {
         serializer.b(this.g.size());

         for (String str : this.g) {
            serializer.a(str);
         }
      }
   }

   public void a(PacketListenerPlayOut listener) {
      listener.a(this);
   }

   public PacketPlayOutScoreboardTeam() {
      this.e = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e;
      this.f = -1;
      this.g = Lists.newArrayList();
   }
}
