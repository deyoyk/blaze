package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.Blaze;
import java.io.IOException;

public class PacketPlayInChat implements Packet<PacketListenerPlayIn> {
   private String a;

   public PacketPlayInChat() {
   }

   public PacketPlayInChat(String s) {
      if (s.length() > 100) {
         s = s.substring(0, 100);
      }

      this.a = s;
   }

   @Override
   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      this.a = packetdataserializer.c(100);
   }

   @Override
   public void b(PacketDataSerializer packetdataserializer) throws IOException {
      packetdataserializer.a(this.a);
   }

   public void a(PacketListenerPlayIn packetlistenerplayin) {
      if (!this.a.startsWith("/")) {
         Blaze.getInstance().getThreadingManager().getAsyncExecutor().queueChat(() -> packetlistenerplayin.a(this));
      } else {
         packetlistenerplayin.a(this);
      }
   }

   public String a() {
      return this.a;
   }
}
