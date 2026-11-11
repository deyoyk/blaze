package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ScoreboardServer extends Scoreboard {
   private final Set<ScoreboardObjective> b = Sets.newHashSet();
   private PersistentScoreboard c;

   public ScoreboardServer(MinecraftServer minecraftserver) {
   }

   @Override
   public void handleScoreChanged(ScoreboardScore scoreboardscore) {
      super.handleScoreChanged(scoreboardscore);
      if (this.b.contains(scoreboardscore.getObjective())) {
         this.sendAll(new PacketPlayOutScoreboardScore(scoreboardscore));
      }

      this.b();
   }

   @Override
   public void handlePlayerRemoved(String s) {
      super.handlePlayerRemoved(s);
      this.sendAll(new PacketPlayOutScoreboardScore(s));
      this.b();
   }

   @Override
   public void a(String s, ScoreboardObjective scoreboardobjective) {
      super.a(s, scoreboardobjective);
      this.sendAll(new PacketPlayOutScoreboardScore(s, scoreboardobjective));
      this.b();
   }

   @Override
   public void setDisplaySlot(int i, ScoreboardObjective scoreboardobjective) {
      ScoreboardObjective scoreboardobjective1 = this.getObjectiveForSlot(i);
      super.setDisplaySlot(i, scoreboardobjective);
      if (scoreboardobjective1 != scoreboardobjective && scoreboardobjective1 != null) {
         if (this.h(scoreboardobjective1) > 0) {
            this.sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
         } else {
            this.g(scoreboardobjective1);
         }
      }

      if (scoreboardobjective != null) {
         if (this.b.contains(scoreboardobjective)) {
            this.sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
         } else {
            this.e(scoreboardobjective);
         }
      }

      this.b();
   }

   @Override
   public boolean addPlayerToTeam(String s, String s1) {
      if (super.addPlayerToTeam(s, s1)) {
         ScoreboardTeam scoreboardteam = this.getTeam(s1);
         this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, Collections.singletonList(s), 3));
         this.b();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void removePlayerFromTeam(String s, ScoreboardTeam scoreboardteam) {
      super.removePlayerFromTeam(s, scoreboardteam);
      this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, Collections.singletonList(s), 4));
      this.b();
   }

   @Override
   public void handleObjectiveAdded(ScoreboardObjective scoreboardobjective) {
      super.handleObjectiveAdded(scoreboardobjective);
      this.b();
   }

   @Override
   public void handleObjectiveChanged(ScoreboardObjective scoreboardobjective) {
      super.handleObjectiveChanged(scoreboardobjective);
      if (this.b.contains(scoreboardobjective)) {
         this.sendAll(new PacketPlayOutScoreboardObjective(scoreboardobjective, 2));
      }

      this.b();
   }

   @Override
   public void handleObjectiveRemoved(ScoreboardObjective scoreboardobjective) {
      super.handleObjectiveRemoved(scoreboardobjective);
      if (this.b.contains(scoreboardobjective)) {
         this.g(scoreboardobjective);
      }

      this.b();
   }

   @Override
   public void handleTeamAdded(ScoreboardTeam scoreboardteam) {
      super.handleTeamAdded(scoreboardteam);
      this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 0));
      this.b();
   }

   @Override
   public void handleTeamChanged(ScoreboardTeam scoreboardteam) {
      super.handleTeamChanged(scoreboardteam);
      this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 2));
      this.b();
   }

   @Override
   public void handleTeamRemoved(ScoreboardTeam scoreboardteam) {
      super.handleTeamRemoved(scoreboardteam);
      this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 1));
      this.b();
   }

   public void a(PersistentScoreboard persistentscoreboard) {
      this.c = persistentscoreboard;
   }

   protected void b() {
      if (this.c != null) {
         this.c.c();
      }
   }

   public List<Packet<PacketListenerPlayOut>> getScoreboardScorePacketsForObjective(ScoreboardObjective scoreboardobjective) {
      ArrayList<Packet<PacketListenerPlayOut>> arraylist = Lists.newArrayList();
      arraylist.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 0));

      for (int i = 0; i < 19; i++) {
         if (this.getObjectiveForSlot(i) == scoreboardobjective) {
            arraylist.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
         }
      }

      for (ScoreboardScore scoreboardScore : this.getScoresForObjective(scoreboardobjective)) {
         arraylist.add(new PacketPlayOutScoreboardScore(scoreboardScore));
      }

      return arraylist;
   }

   public void e(ScoreboardObjective scoreboardobjective) {
      List<Packet<PacketListenerPlayOut>> list = this.getScoreboardScorePacketsForObjective(scoreboardobjective);
      if (this.viewers != null) {
         for (EntityPlayer entityplayer : this.viewers) {
            for (Packet<PacketListenerPlayOut> packet : list) {
               entityplayer.playerConnection.sendPacket(packet);
            }
         }
      }

      this.b.add(scoreboardobjective);
   }

   public List<Packet<PacketListenerPlayOut>> f(ScoreboardObjective scoreboardobjective) {
      ArrayList<Packet<PacketListenerPlayOut>> arraylist = Lists.newArrayList();
      arraylist.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 1));

      for (int i = 0; i < 19; i++) {
         if (this.getObjectiveForSlot(i) == scoreboardobjective) {
            arraylist.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
         }
      }

      return arraylist;
   }

   public void g(ScoreboardObjective scoreboardobjective) {
      List<Packet<PacketListenerPlayOut>> list = this.f(scoreboardobjective);
      if (this.viewers != null) {
         for (EntityPlayer entityplayer : this.viewers) {
            for (Packet<PacketListenerPlayOut> packet : list) {
               entityplayer.playerConnection.sendPacket(packet);
            }
         }
      }

      this.b.remove(scoreboardobjective);
   }

   public int h(ScoreboardObjective scoreboardobjective) {
      int i = 0;

      for (int j = 0; j < 19; j++) {
         if (this.getObjectiveForSlot(j) == scoreboardobjective) {
            i++;
         }
      }

      return i;
   }

   private void sendAll(Packet<PacketListenerPlayOut> packet) {
      if (this.viewers != null) {
         for (EntityPlayer entityplayer : this.viewers) {
            entityplayer.playerConnection.sendPacket(packet);
         }
      }
   }
}
