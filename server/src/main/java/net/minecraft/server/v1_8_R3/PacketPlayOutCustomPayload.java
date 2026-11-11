package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutCustomPayload implements Packet<PacketListenerPlayOut> {
   private String a;
   private PacketDataSerializer b;

   public PacketPlayOutCustomPayload() {
   }

   public PacketPlayOutCustomPayload(String var1, PacketDataSerializer var2) {
      this.a = var1;
      this.b = var2;
      if (var2.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.c(20);
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 1048576) {
         this.b = new PacketDataSerializer(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
      var1.writeBytes(this.b);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
