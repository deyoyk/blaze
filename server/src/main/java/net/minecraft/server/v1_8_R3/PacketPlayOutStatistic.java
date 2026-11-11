package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public class PacketPlayOutStatistic implements Packet<PacketListenerPlayOut> {
   private Map<Statistic, Integer> a;

   public PacketPlayOutStatistic() {
   }

   public PacketPlayOutStatistic(Map<Statistic, Integer> var1) {
      this.a = var1;
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      int var2 = var1.e();
      this.a = Maps.newHashMap();

      for (int var3 = 0; var3 < var2; var3++) {
         Statistic var4 = StatisticList.getStatistic(var1.c(32767));
         int var5 = var1.e();
         if (var4 != null) {
            this.a.put(var4, var5);
         }
      }
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a.size());

      for (Entry var3 : this.a.entrySet()) {
         var1.a(((Statistic)var3.getKey()).name);
         var1.b((Integer)var3.getValue());
      }
   }
}
