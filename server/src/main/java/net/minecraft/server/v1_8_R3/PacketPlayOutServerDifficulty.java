package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutServerDifficulty implements Packet<PacketListenerPlayOut> {
   private EnumDifficulty a;
   private boolean b;

   public PacketPlayOutServerDifficulty() {
   }

   public PacketPlayOutServerDifficulty(EnumDifficulty var1, boolean var2) {
      this.a = var1;
      this.b = var2;
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = EnumDifficulty.getById(var1.readUnsignedByte());
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeByte(this.a.a());
   }
}
