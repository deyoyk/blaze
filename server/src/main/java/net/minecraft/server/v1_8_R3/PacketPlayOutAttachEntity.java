package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutAttachEntity implements Packet<PacketListenerPlayOut> {
   private int a;
   private int b;
   private int c;

   public PacketPlayOutAttachEntity(int leash, Entity entity, Entity vehicle) {
      this.a = leash;
      this.b = entity.getId();
      this.c = vehicle != null ? vehicle.getId() : -1;
   }

   public PacketPlayOutAttachEntity() {
   }

   @Override
   public void a(PacketDataSerializer serializer) throws IOException {
      this.b = serializer.readInt();
      this.c = serializer.readInt();
      this.a = serializer.readUnsignedByte();
   }

   @Override
   public void b(PacketDataSerializer serializer) throws IOException {
      serializer.writeInt(this.b);
      serializer.writeInt(this.c);
      serializer.writeByte(this.a);
   }

   public void a(PacketListenerPlayOut listener) {
      listener.a(this);
   }
}
