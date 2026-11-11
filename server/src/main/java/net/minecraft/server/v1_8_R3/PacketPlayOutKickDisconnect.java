package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutKickDisconnect implements Packet<PacketListenerPlayOut> {
   private IChatBaseComponent a;

   public PacketPlayOutKickDisconnect() {
   }

   public PacketPlayOutKickDisconnect(IChatBaseComponent var1) {
      this.a = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.d();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
