package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PacketPlayOutEntityMetadata implements Packet<PacketListenerPlayOut> {
   private int a;
   private List<DataWatcher.WatchableObject> b;

   public PacketPlayOutEntityMetadata(int entityId, DataWatcher watcher, boolean flag) {
      this.a = entityId;
      this.b = flag ? watcher.c() : watcher.b();
   }

   public PacketPlayOutEntityMetadata() {
   }

   public PacketPlayOutEntityMetadata(int i, List<DataWatcher.WatchableObject> list) {
      this.a = i;
      this.b = list;
   }

   public PacketPlayOutEntityMetadata obfuscateHealth() {
      Iterator<DataWatcher.WatchableObject> iter = this.b.iterator();
      boolean found = false;

      while (iter.hasNext()) {
         DataWatcher.WatchableObject watchable = iter.next();
         if (watchable.a() == 6) {
            iter.remove();
            found = true;
         }
      }

      if (found) {
         this.b.add(new DataWatcher.WatchableObject(3, 6, 1.0F));
      }

      return this;
   }

   @Override
   public void a(PacketDataSerializer serializer) throws IOException {
      this.a = serializer.e();
      this.b = DataWatcher.b(serializer);
   }

   @Override
   public void b(PacketDataSerializer serializer) throws IOException {
      serializer.b(this.a);
      DataWatcher.a(this.b, serializer);
   }

   public void a(PacketListenerPlayOut listener) {
      listener.a(this);
   }

   public List<DataWatcher.WatchableObject> getData() {
      return this.b;
   }
}
