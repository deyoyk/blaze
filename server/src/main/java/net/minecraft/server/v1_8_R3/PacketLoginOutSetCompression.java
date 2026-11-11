package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketLoginOutSetCompression implements Packet<PacketLoginOutListener> {
   private int a;

   public PacketLoginOutSetCompression() {
   }

   public PacketLoginOutSetCompression(int var1) {
      this.a = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
   }

   public void a(PacketLoginOutListener var1) {
      var1.a(this);
   }
}
