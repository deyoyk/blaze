package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher {
   private static final Object2IntOpenHashMap<Class<?>> classToId = new Object2IntOpenHashMap(10, 0.5F);
   private final Entity a;
   private boolean b = true;
   private final WatchableArrayMap dataValues = new WatchableArrayMap();
   private final Map<Integer, DataWatcher.WatchableObject> d = this.dataValues;
   private boolean e;

   static {
      classToId.put(Byte.class, 0);
      classToId.put(Short.class, 1);
      classToId.put(Integer.class, 2);
      classToId.put(Float.class, 3);
      classToId.put(String.class, 4);
      classToId.put(ItemStack.class, 5);
      classToId.put(BlockPosition.class, 6);
      classToId.put(Vector3f.class, 7);
   }

   public DataWatcher(Entity entity) {
      this.a = entity;
   }

   public <T> void a(int i, T t0) {
      int integer = classToId.getInt(t0.getClass());
      if (integer == -1) {
         throw new IllegalArgumentException("Unknown data type: " + t0.getClass());
      } else if (i > 31) {
         throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 31 + ")");
      } else if (this.dataValues.containsKey(i)) {
         throw new IllegalArgumentException("Duplicate id value for " + i + "!");
      } else {
         this.dataValues.put(i, new DataWatcher.WatchableObject(integer, i, t0));
         this.b = false;
      }
   }

   public void add(int i, int j) {
      this.dataValues.put(i, new DataWatcher.WatchableObject(j, i, null));
      this.b = false;
   }

   public byte getByte(int i) {
      return (Byte)this.j(i).b();
   }

   public short getShort(int i) {
      return (Short)this.j(i).b();
   }

   public int getInt(int i) {
      return (Integer)this.j(i).b();
   }

   public float getFloat(int i) {
      return (Float)this.j(i).b();
   }

   public String getString(int i) {
      return (String)this.j(i).b();
   }

   public ItemStack getItemStack(int i) {
      return (ItemStack)this.j(i).b();
   }

   private DataWatcher.WatchableObject j(int i) {
      return this.dataValues.get(i);
   }

   public Vector3f h(int i) {
      return (Vector3f)this.j(i).b();
   }

   public <T> void watch(int i, T t0) {
      DataWatcher.WatchableObject datawatcher_watchableobject = this.j(i);
      if (ObjectUtils.notEqual(t0, datawatcher_watchableobject.b())) {
         datawatcher_watchableobject.a(t0);
         this.a.i(i);
         datawatcher_watchableobject.a(true);
         this.e = true;
      }
   }

   public void update(int i) {
      this.j(i).d = true;
      this.e = true;
   }

   public boolean a() {
      return this.e;
   }

   public static void a(List<DataWatcher.WatchableObject> list, PacketDataSerializer packetdataserializer) throws IOException {
      if (list != null) {
         for (DataWatcher.WatchableObject watchableObject : list) {
            a(packetdataserializer, watchableObject);
         }
      }

      packetdataserializer.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> b() {
      ArrayList<DataWatcher.WatchableObject> arraylist = null;
      if (this.e) {
         for (DataWatcher.WatchableObject datawatcher_watchableobject : this.dataValues) {
            if (datawatcher_watchableobject.d()) {
               datawatcher_watchableobject.a(false);
               if (arraylist == null) {
                  arraylist = Lists.newArrayList();
               }

               if (datawatcher_watchableobject.b() instanceof ItemStack) {
                  datawatcher_watchableobject = new DataWatcher.WatchableObject(
                     datawatcher_watchableobject.c(), datawatcher_watchableobject.a(), ((ItemStack)datawatcher_watchableobject.b()).cloneItemStack()
                  );
               }

               arraylist.add(datawatcher_watchableobject);
            }
         }
      }

      this.e = false;
      return arraylist;
   }

   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      for (DataWatcher.WatchableObject watchableObject : this.dataValues) {
         a(packetdataserializer, watchableObject);
      }

      packetdataserializer.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> c() {
      ArrayList<DataWatcher.WatchableObject> arraylist = Lists.newArrayListWithCapacity(this.dataValues.size());
      for (DataWatcher.WatchableObject watchableObject : this.dataValues) {
         arraylist.add(watchableObject);
      }

      for (int i = 0; i < arraylist.size(); i++) {
         DataWatcher.WatchableObject watchableobject = arraylist.get(i);
         if (watchableobject.b() instanceof ItemStack) {
            watchableobject = new DataWatcher.WatchableObject(watchableobject.c(), watchableobject.a(), ((ItemStack)watchableobject.b()).cloneItemStack());
            arraylist.set(i, watchableobject);
         }
      }

      return arraylist;
   }

   private static void a(PacketDataSerializer packetdataserializer, DataWatcher.WatchableObject datawatcher_watchableobject) {
      int i = (datawatcher_watchableobject.c() << 5 | datawatcher_watchableobject.a() & 31) & 0xFF;
      packetdataserializer.writeByte(i);
      switch (datawatcher_watchableobject.c()) {
         case 0:
            packetdataserializer.writeByte((Byte)datawatcher_watchableobject.b());
            break;
         case 1:
            packetdataserializer.writeShort((Short)datawatcher_watchableobject.b());
            break;
         case 2:
            packetdataserializer.writeInt((Integer)datawatcher_watchableobject.b());
            break;
         case 3:
            packetdataserializer.writeFloat((Float)datawatcher_watchableobject.b());
            break;
         case 4:
            packetdataserializer.a((String)datawatcher_watchableobject.b());
            break;
         case 5:
            packetdataserializer.a((ItemStack)datawatcher_watchableobject.b());
            break;
         case 6:
            BlockPosition blockposition = (BlockPosition)datawatcher_watchableobject.b();
            packetdataserializer.writeInt(blockposition.getX());
            packetdataserializer.writeInt(blockposition.getY());
            packetdataserializer.writeInt(blockposition.getZ());
            break;
         case 7:
            Vector3f vector3f = (Vector3f)datawatcher_watchableobject.b();
            packetdataserializer.writeFloat(vector3f.getX());
            packetdataserializer.writeFloat(vector3f.getY());
            packetdataserializer.writeFloat(vector3f.getZ());
      }
   }

   public static List<DataWatcher.WatchableObject> b(PacketDataSerializer packetdataserializer) throws IOException {
      ArrayList<DataWatcher.WatchableObject> arraylist = null;

      for (byte b0 = packetdataserializer.readByte(); b0 != 127; b0 = packetdataserializer.readByte()) {
         if (arraylist == null) {
            arraylist = Lists.newArrayList();
         }

         int i = (b0 & 224) >> 5;
         int j = b0 & 31;
         DataWatcher.WatchableObject datawatcher_watchableobject = null;
         switch (i) {
            case 0:
               datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, packetdataserializer.readByte());
               break;
            case 1:
               datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, packetdataserializer.readShort());
               break;
            case 2:
               datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, packetdataserializer.readInt());
               break;
            case 3:
               datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, packetdataserializer.readFloat());
               break;
            case 4:
               datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, packetdataserializer.c(32767));
               break;
            case 5:
               datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, packetdataserializer.i());
               break;
            case 6:
               datawatcher_watchableobject = new DataWatcher.WatchableObject(
                  i, j, new BlockPosition(packetdataserializer.readInt(), packetdataserializer.readInt(), packetdataserializer.readInt())
               );
               break;
            case 7:
               datawatcher_watchableobject = new DataWatcher.WatchableObject(
                  i, j, new Vector3f(packetdataserializer.readFloat(), packetdataserializer.readFloat(), packetdataserializer.readFloat())
               );
         }

         arraylist.add(datawatcher_watchableobject);
      }

      return arraylist;
   }

   public boolean d() {
      return this.b;
   }

   public void e() {
      this.e = false;
   }

   public static class WatchableObject {
      private final int a;
      private final int b;
      private Object c;
      private boolean d;

      public WatchableObject(int i, int j, Object object) {
         this.b = j;
         this.c = object;
         this.a = i;
         this.d = true;
      }

      public int a() {
         return this.b;
      }

      public void a(Object object) {
         this.c = object;
      }

      public Object b() {
         return this.c;
      }

      public int c() {
         return this.a;
      }

      public boolean d() {
         return this.d;
      }

      public void a(boolean flag) {
         this.d = flag;
      }

      public DataWatcher.WatchableObject clone() {
         DataWatcher.WatchableObject watchableObject = new DataWatcher.WatchableObject(this.a, this.b, this.c);
         watchableObject.a(this.d);
         return watchableObject;
      }
   }
}
