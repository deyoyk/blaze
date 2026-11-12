package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInUseEntity implements Packet<PacketListenerPlayIn> {
   private int a;
   private PacketPlayInUseEntity.EnumEntityUseAction action;
   private Vec3D c;
   public long receiveNanos;

   @Override
   public void a(PacketDataSerializer serializer) throws IOException {
      this.a = serializer.e();
      this.action = serializer.a(PacketPlayInUseEntity.EnumEntityUseAction.class);
      if (this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
         this.c = new Vec3D(serializer.readFloat(), serializer.readFloat(), serializer.readFloat());
      }
   }

   @Override
   public void b(PacketDataSerializer serializer) throws IOException {
      serializer.b(this.a);
      serializer.a(this.action);
      if (this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
         serializer.writeFloat((float)this.c.a);
         serializer.writeFloat((float)this.c.b);
         serializer.writeFloat((float)this.c.c);
      }
   }

   public void a(PacketListenerPlayIn listener) {
      listener.a(this);
   }

   public Entity a(World world) {
      return world.a(this.a);
   }

   public PacketPlayInUseEntity.EnumEntityUseAction a() {
      return this.action;
   }

   public Vec3D b() {
      return this.c;
   }

   public static enum EnumEntityUseAction {
      INTERACT,
      ATTACK,
      INTERACT_AT;
   }
}
