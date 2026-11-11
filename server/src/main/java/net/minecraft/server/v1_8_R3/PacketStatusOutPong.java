package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketStatusOutPong implements Packet<PacketStatusOutListener> {
   private long a;

   public PacketStatusOutPong() {
   }

   public PacketStatusOutPong(long var1) {
      this.a = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readLong();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeLong(this.a);
   }

   public void a(PacketStatusOutListener var1) {
      var1.a(this);
   }
}
