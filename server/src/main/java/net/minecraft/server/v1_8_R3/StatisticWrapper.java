package net.minecraft.server.v1_8_R3;

public class StatisticWrapper {
   private int a;
   private IJsonStatistic b;

   public int a() {
      return this.a;
   }

   public void a(int var1) {
      this.a = var1;
   }

   public <T extends IJsonStatistic> T b() {
      return (T)this.b;
   }

   public void a(IJsonStatistic var1) {
      this.b = var1;
   }
}
