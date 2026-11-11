package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public interface Packet<T extends PacketListener> {
   void a(PacketDataSerializer var1) throws IOException;

   void b(PacketDataSerializer var1) throws IOException;

   void a(T var1);
}
