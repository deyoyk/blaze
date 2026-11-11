package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import java.util.List;

public class PacketPlayOutMapChunkBulk implements Packet<PacketListenerPlayOut> {
   private int[] a;
   private int[] b;
   private PacketPlayOutMapChunk.ChunkMap[] c;
   private boolean d;

   public PacketPlayOutMapChunkBulk() {
   }

   public PacketPlayOutMapChunkBulk(List<Chunk> list) {
      int i = list.size();
      this.a = new int[i];
      this.b = new int[i];
      this.c = new PacketPlayOutMapChunk.ChunkMap[i];

      for (int j = 0; j < i; j++) {
         Chunk chunk = list.get(j);
         if (j == 0) {
            this.d = !chunk.getWorld().worldProvider.o();
         }

         this.a[j] = chunk.locX;
         this.b[j] = chunk.locZ;
         this.c[j] = chunk.getChunkMap(true, 65535);
      }
   }

   @Override
   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      this.d = packetdataserializer.readBoolean();
      int i = packetdataserializer.e();
      this.a = new int[i];
      this.b = new int[i];
      this.c = new PacketPlayOutMapChunk.ChunkMap[i];

      for (int j = 0; j < i; j++) {
         this.a[j] = packetdataserializer.readInt();
         this.b[j] = packetdataserializer.readInt();
         this.c[j] = new PacketPlayOutMapChunk.ChunkMap();
         this.c[j].b = packetdataserializer.readShort() & '\uffff';
         this.c[j].a = new byte[PacketPlayOutMapChunk.getChunkDataSize(Integer.bitCount(this.c[j].b), this.d, true)];
      }

      for (int var4 = 0; var4 < i; var4++) {
         packetdataserializer.readBytes(this.c[var4].a);
      }
   }

   @Override
   public void b(PacketDataSerializer packetdataserializer) throws IOException {
      packetdataserializer.writeBoolean(this.d);
      packetdataserializer.b(this.c.length);

      for (int i = 0; i < this.a.length; i++) {
         packetdataserializer.writeInt(this.a[i]);
         packetdataserializer.writeInt(this.b[i]);
         packetdataserializer.writeShort((short)(this.c[i].b & 65535));
      }

      for (int var4 = 0; var4 < this.a.length; var4++) {
         PacketPlayOutMapChunk.ChunkMap chunkMap = this.c[var4];
         packetdataserializer.writeBytes(chunkMap.a);
      }
   }

   public void a(PacketListenerPlayOut packetlistenerplayout) {
      packetlistenerplayout.a(this);
   }
}
