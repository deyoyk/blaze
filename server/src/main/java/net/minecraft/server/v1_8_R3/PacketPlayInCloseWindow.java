package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInCloseWindow implements Packet<PacketListenerPlayIn> {
   public int id;

   public PacketPlayInCloseWindow() {
   }

   public PacketPlayInCloseWindow(int id) {
      this.id = id;
   }

   public void a(PacketListenerPlayIn packetlistenerplayin) {
      packetlistenerplayin.a(this);
   }

   @Override
   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      this.id = packetdataserializer.readByte();
   }

   @Override
   public void b(PacketDataSerializer packetdataserializer) throws IOException {
      packetdataserializer.writeByte(this.id);
   }
}
