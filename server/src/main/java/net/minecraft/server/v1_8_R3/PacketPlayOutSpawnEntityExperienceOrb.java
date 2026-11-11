package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutSpawnEntityExperienceOrb implements Packet<PacketListenerPlayOut> {
   private int a;
   private int b;
   private int c;
   private int d;
   private int e;

   public PacketPlayOutSpawnEntityExperienceOrb() {
   }

   public PacketPlayOutSpawnEntityExperienceOrb(EntityExperienceOrb orb) {
      this.a = orb.getId();
      this.b = (int)Math.round(orb.locX * 32.0);
      this.c = (int)Math.round(orb.locY * 32.0);
      this.d = (int)Math.round(orb.locZ * 32.0);
      this.e = orb.j();
   }

   @Override
   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      this.a = packetdataserializer.e();
      this.b = packetdataserializer.readInt();
      this.c = packetdataserializer.readInt();
      this.d = packetdataserializer.readInt();
      this.e = packetdataserializer.readShort();
   }

   @Override
   public void b(PacketDataSerializer packetdataserializer) throws IOException {
      packetdataserializer.b(this.a);
      packetdataserializer.writeInt(this.b);
      packetdataserializer.writeInt(this.c);
      packetdataserializer.writeInt(this.d);
      packetdataserializer.writeShort(this.e);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
