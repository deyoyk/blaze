package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutCamera implements Packet<PacketListenerPlayOut> {
   public int a;

   public PacketPlayOutCamera() {
   }

   public PacketPlayOutCamera(Entity var1) {
      this.a = var1.getId();
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
