package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutCloseWindow implements Packet<PacketListenerPlayOut> {
   private int a;

   public PacketPlayOutCloseWindow() {
   }

   public PacketPlayOutCloseWindow(int var1) {
      this.a = var1;
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readUnsignedByte();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeByte(this.a);
   }
}
