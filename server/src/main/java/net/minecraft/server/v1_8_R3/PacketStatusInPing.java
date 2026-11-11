package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketStatusInPing implements Packet<PacketStatusInListener> {
   private long a;

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readLong();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeLong(this.a);
   }

   public void a(PacketStatusInListener var1) {
      var1.a(this);
   }

   public long a() {
      return this.a;
   }
}
