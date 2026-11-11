package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInEntityAction implements Packet<PacketListenerPlayIn> {
   private int a;
   private PacketPlayInEntityAction.EnumPlayerAction animation;
   private int c;

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.animation = var1.a(PacketPlayInEntityAction.EnumPlayerAction.class);
      this.c = var1.e();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.a(this.animation);
      var1.b(this.c);
   }

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   public PacketPlayInEntityAction.EnumPlayerAction b() {
      return this.animation;
   }

   public int c() {
      return this.c;
   }

   public static enum EnumPlayerAction {
      START_SNEAKING,
      STOP_SNEAKING,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      RIDING_JUMP,
      OPEN_INVENTORY;
   }
}
