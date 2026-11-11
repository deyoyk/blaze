package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutCollect implements Packet<PacketListenerPlayOut> {
   private int a;
   private int b;

   public PacketPlayOutCollect() {
   }

   public PacketPlayOutCollect(int var1, int var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.b = var1.e();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.b(this.b);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
