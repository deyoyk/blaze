package net.minecraft.server.v1_8_R3;

public interface PacketStatusOutListener extends PacketListener {
   void a(PacketStatusOutServerInfo var1);

   void a(PacketStatusOutPong var1);
}
