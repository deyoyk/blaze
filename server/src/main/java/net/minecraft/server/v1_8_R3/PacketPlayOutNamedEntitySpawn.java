package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PacketPlayOutNamedEntitySpawn implements Packet<PacketListenerPlayOut> {
   private int a;
   private UUID b;
   private int c;
   private int d;
   private int e;
   private byte f;
   private byte g;
   private int h;
   private DataWatcher i;
   private List<DataWatcher.WatchableObject> j;

   public PacketPlayOutNamedEntitySpawn() {
   }

   public PacketPlayOutNamedEntitySpawn(EntityHuman entityHuman) {
      this.a = entityHuman.getId();
      this.b = entityHuman.getProfile().getId();
      this.c = (int)Math.round(entityHuman.locX * 32.0);
      this.e = (int)Math.round(entityHuman.locZ * 32.0);
      this.d = (int)Math.round(entityHuman.locY * 32.0);
      this.f = (byte)(entityHuman.yaw * 256.0F / 360.0F);
      this.g = (byte)(entityHuman.pitch * 256.0F / 360.0F);
      ItemStack itemStack = entityHuman.inventory.getItemInHand();
      this.h = itemStack == null ? 0 : Item.getId(itemStack.getItem());
      this.i = entityHuman.getDataWatcher();
   }

   @Override
   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      this.a = packetdataserializer.e();
      this.b = packetdataserializer.g();
      this.c = packetdataserializer.readInt();
      this.d = packetdataserializer.readInt();
      this.e = packetdataserializer.readInt();
      this.f = packetdataserializer.readByte();
      this.g = packetdataserializer.readByte();
      this.h = packetdataserializer.readShort();
      this.j = DataWatcher.b(packetdataserializer);
   }

   @Override
   public void b(PacketDataSerializer packetdataserializer) throws IOException {
      packetdataserializer.b(this.a);
      packetdataserializer.a(this.b);
      packetdataserializer.writeInt(this.c);
      packetdataserializer.writeInt(this.d);
      packetdataserializer.writeInt(this.e);
      packetdataserializer.writeByte(this.f);
      packetdataserializer.writeByte(this.g);
      packetdataserializer.writeShort(this.h);
      this.i.a(packetdataserializer);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
