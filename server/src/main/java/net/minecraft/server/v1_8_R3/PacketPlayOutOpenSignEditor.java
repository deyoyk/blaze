package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutOpenSignEditor implements Packet<PacketListenerPlayOut> {
   private BlockPosition a;

   public PacketPlayOutOpenSignEditor() {
   }

   public PacketPlayOutOpenSignEditor(BlockPosition var1) {
      this.a = var1;
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.c();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
   }
}
