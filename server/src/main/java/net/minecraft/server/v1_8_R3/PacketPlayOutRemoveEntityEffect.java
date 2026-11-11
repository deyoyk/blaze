package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutRemoveEntityEffect implements Packet<PacketListenerPlayOut> {
   private int a;
   private int b;

   public PacketPlayOutRemoveEntityEffect() {
   }

   public PacketPlayOutRemoveEntityEffect(int var1, MobEffect var2) {
      this.a = var1;
      this.b = var2.getEffectId();
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.b = var1.readUnsignedByte();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.writeByte(this.b);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
