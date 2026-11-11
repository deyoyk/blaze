package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import java.util.UUID;

public class PacketPlayInSpectate implements Packet<PacketListenerPlayIn> {
   private UUID a;

   public PacketPlayInSpectate() {
   }

   public PacketPlayInSpectate(UUID var1) {
      this.a = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.g();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
   }

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   public Entity a(WorldServer var1) {
      return var1.getEntity(this.a);
   }
}
