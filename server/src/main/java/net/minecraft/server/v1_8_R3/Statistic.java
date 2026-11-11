package net.minecraft.server.v1_8_R3;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Statistic {
   public final String name;
   private final IChatBaseComponent a;
   public boolean f;
   private final Counter b;
   private final IScoreboardCriteria c;
   private Class<? extends IJsonStatistic> d;
   private static NumberFormat k = NumberFormat.getIntegerInstance(Locale.US);
   public static Counter g = new Counter() {};
   private static DecimalFormat l = new DecimalFormat("########0.00");
   public static Counter h = new Counter() {};
   public static Counter i = new Counter() {};
   public static Counter j = new Counter() {};

   public Statistic(String var1, IChatBaseComponent var2, Counter var3) {
      this.name = var1;
      this.a = var2;
      this.b = var3;
      this.c = new ScoreboardStatisticCriteria(this);
      IScoreboardCriteria.criteria.put(this.c.getName(), this.c);
   }

   public Statistic(String var1, IChatBaseComponent var2) {
      this(var1, var2, g);
   }

   public Statistic i() {
      this.f = true;
      return this;
   }

   public Statistic h() {
      if (StatisticList.a.containsKey(this.name)) {
         throw new RuntimeException("Duplicate stat id: \"" + StatisticList.a.get(this.name).a + "\" and \"" + this.a + "\" at id " + this.name);
      } else {
         StatisticList.stats.add(this);
         StatisticList.a.put(this.name, this);
         return this;
      }
   }

   public boolean d() {
      return false;
   }

   public IChatBaseComponent e() {
      IChatBaseComponent var1 = this.a.f();
      var1.getChatModifier().setColor(EnumChatFormat.GRAY);
      var1.getChatModifier().setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_ACHIEVEMENT, new ChatComponentText(this.name)));
      return var1;
   }

   public IChatBaseComponent j() {
      IChatBaseComponent var1 = this.e();
      IChatBaseComponent var2 = new ChatComponentText("[").addSibling(var1).a("]");
      var2.setChatModifier(var1.getChatModifier());
      return var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Statistic var2 = (Statistic)var1;
         return this.name.equals(var2.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public String toString() {
      return "Stat{id=" + this.name + ", nameId=" + this.a + ", awardLocallyOnly=" + this.f + ", formatter=" + this.b + ", objectiveCriteria=" + this.c + '}';
   }

   public IScoreboardCriteria k() {
      return this.c;
   }

   public Class<? extends IJsonStatistic> l() {
      return this.d;
   }

   public Statistic b(Class<? extends IJsonStatistic> var1) {
      this.d = var1;
      return this;
   }
}
