package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketStatusInStart implements Packet<PacketStatusInListener> {
   @Override
   public void a(PacketDataSerializer var1) throws IOException {
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
   }

   public void a(PacketStatusInListener var1) {
      var1.a(this);
   }
}
