package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutSpawnPosition implements Packet<PacketListenerPlayOut> {
   public BlockPosition position;

   public PacketPlayOutSpawnPosition() {
   }

   public PacketPlayOutSpawnPosition(BlockPosition var1) {
      this.position = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.position = var1.c();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.position);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
