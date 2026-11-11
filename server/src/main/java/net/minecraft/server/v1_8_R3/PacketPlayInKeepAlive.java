package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInKeepAlive implements Packet<PacketListenerPlayIn> {
   private int a;

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
   }

   public int a() {
      return this.a;
   }
}
