package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutEntityEffect implements Packet<PacketListenerPlayOut> {
   private int a;
   private byte b;
   private byte c;
   private int d;
   private byte e;

   public PacketPlayOutEntityEffect() {
   }

   public PacketPlayOutEntityEffect(int var1, MobEffect var2) {
      this.a = var1;
      this.b = (byte)(var2.getEffectId() & 0xFF);
      this.c = (byte)(var2.getAmplifier() & 0xFF);
      if (var2.getDuration() > 32767) {
         this.d = 32767;
      } else {
         this.d = var2.getDuration();
      }

      this.e = (byte)(var2.isShowParticles() ? 1 : 0);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.b = var1.readByte();
      this.c = var1.readByte();
      this.d = var1.e();
      this.e = var1.readByte();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.writeByte(this.b);
      var1.writeByte(this.c);
      var1.b(this.d);
      var1.writeByte(this.e);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
