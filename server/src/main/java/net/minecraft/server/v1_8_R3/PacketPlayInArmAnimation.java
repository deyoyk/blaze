package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInArmAnimation implements Packet<PacketListenerPlayIn> {
   public long timestamp;

   @Override
   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      this.timestamp = System.currentTimeMillis();
   }

   @Override
   public void b(PacketDataSerializer packetdataserializer) throws IOException {
   }

   public void a(PacketListenerPlayIn packetlistenerplayin) {
      packetlistenerplayin.a(this);
   }
}
