package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketHandshakingInSetProtocol implements Packet<PacketHandshakingInListener> {
   private int a;
   public String hostname;
   public int port;
   private EnumProtocol d;

   @Override
   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      this.a = packetdataserializer.e();
      this.hostname = packetdataserializer.c(32767);
      this.port = packetdataserializer.readUnsignedShort();
      this.d = EnumProtocol.getProtocol(packetdataserializer.e());
   }

   @Override
   public void b(PacketDataSerializer packetdataserializer) throws IOException {
      packetdataserializer.b(this.a);
      packetdataserializer.a(this.hostname);
      packetdataserializer.writeShort(this.port);
      packetdataserializer.b(this.d.getProtocolId());
   }

   public void a(PacketHandshakingInListener packethandshakinginlistener) {
      packethandshakinginlistener.a(this);
   }

   public EnumProtocol a() {
      return this.d;
   }

   public int b() {
      return this.a;
   }
}
