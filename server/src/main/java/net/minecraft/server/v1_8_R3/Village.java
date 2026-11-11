package net.minecraft.server.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class Village {
   private final List<VillageDoor> b = Lists.newArrayList();
   private World a;
   private BlockPosition c;
   private BlockPosition d;
   private int e;
   private int f;
   private int g;
   private int h;
   private int i;
   private final TreeMap<String, Integer> j;
   private final List<Village.Aggressor> k;
   private int l;
   private BlockPosition[] positions = null;

   public Village() {
      this.c = BlockPosition.ZERO;
      this.d = BlockPosition.ZERO;
      this.j = new TreeMap<>();
      this.k = Lists.newArrayList();
   }

   public Village(World world) {
      this.c = BlockPosition.ZERO;
      this.d = BlockPosition.ZERO;
      this.j = new TreeMap<>();
      this.k = Lists.newArrayList();
      this.a = world;
   }

   public void a(World world) {
      this.a = world;
   }

   private void calculateNewCheckPositions() {
      if (this.d != null && !this.d.equals(BlockPosition.ZERO)) {
         this.positions = new BlockPosition[]{
            this.d.a(-this.e, 0, -this.e), this.d.a(-this.e, 0, this.e), this.d.a(this.e, 0, -this.e), this.d.a(this.e, 0, this.e), this.d
         };
      } else {
         this.positions = null;
      }
   }

   public boolean isVillageAreaLoaded() {
      for (int i = 0; this.positions != null && i < this.positions.length; i++) {
         if (this.a.isLoaded(this.positions[i])) {
            return true;
         }
      }

      return false;
   }

   public void a(int i) {
      if (this.isVillageAreaLoaded()) {
         this.g = i;
         this.m();
         this.l();
         if (i % 20 == 0) {
            this.k();
         }

         if (i % 30 == 0) {
            this.j();
         }

         int j = this.h / 10;
         if (this.l < j && this.b.size() > 20 && this.a.random.nextInt(7000) == 0) {
            Vec3D vec3d = this.a(this.d, 2, 4, 2);
            if (vec3d != null) {
               EntityIronGolem entityirongolem = new EntityIronGolem(this.a);
               entityirongolem.setPosition(vec3d.a, vec3d.b, vec3d.c);
               this.a.addEntity(entityirongolem, SpawnReason.VILLAGE_DEFENSE);
               this.l++;
            }
         }
      }
   }

   private Vec3D a(BlockPosition blockposition, int i, int j, int k) {
      for (int l = 0; l < 10; l++) {
         int x = blockposition.getX() + this.a.random.nextInt(16) - 8;
         int y = blockposition.getY() + this.a.random.nextInt(6) - 3;
         int z = blockposition.getZ() + this.a.random.nextInt(16) - 8;
         if (this.a(x, y, z) && this.a(i, j, k, x, y, z)) {
            return new Vec3D(x, y, z);
         }
      }

      return null;
   }

   private boolean a(int x, int y, int z, int x1, int y1, int z1) {
      if (!World.a(this.a, x1, y1 - 1, z1)) {
         return false;
      } else {
         int i = x1 - x / 2;
         int j = z1 - z / 2;

         for (int k = i; k < i + x; k++) {
            for (int l = y1; l < y1 + y; l++) {
               for (int i1 = j; i1 < j + z; i1++) {
                  if (this.a.getType(k, l, i1).getBlock().isOccluding()) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private void j() {
      List<EntityIronGolem> list = this.a
         .a(
            EntityIronGolem.class,
            new AxisAlignedBB(
               this.d.getX() - this.e, this.d.getY() - 4, this.d.getZ() - this.e, this.d.getX() + this.e, this.d.getY() + 4, this.d.getZ() + this.e
            )
         );
      this.l = list.size();
   }

   private void k() {
      List<EntityVillager> list = this.a
         .a(
            EntityVillager.class,
            new AxisAlignedBB(
               this.d.getX() - this.e, this.d.getY() - 4, this.d.getZ() - this.e, this.d.getX() + this.e, this.d.getY() + 4, this.d.getZ() + this.e
            )
         );
      this.h = list.size();
      if (this.h == 0) {
         this.j.clear();
      }
   }

   public BlockPosition a() {
      return this.d;
   }

   public int b() {
      return this.e;
   }

   public int c() {
      return this.b.size();
   }

   public int d() {
      return this.g - this.f;
   }

   public int e() {
      return this.h;
   }

   public boolean a(BlockPosition blockposition) {
      return this.d.i(blockposition) < this.e * this.e;
   }

   public boolean a(int x, int y, int z) {
      return this.d.i(x, y, z) < this.e * this.e;
   }

   public List<VillageDoor> f() {
      return this.b;
   }

   public VillageDoor b(BlockPosition blockposition) {
      VillageDoor villagedoor = null;
      int i = Integer.MAX_VALUE;

      for (VillageDoor villagedoor1 : this.b) {
         int j = villagedoor1.a(blockposition);
         if (j < i) {
            villagedoor = villagedoor1;
            i = j;
         }
      }

      return villagedoor;
   }

   public VillageDoor c(BlockPosition blockposition) {
      VillageDoor villagedoor = null;
      int i = Integer.MAX_VALUE;

      for (VillageDoor villagedoor1 : this.b) {
         int j = villagedoor1.a(blockposition);
         if (j > 256) {
            j *= 1000;
         } else {
            j = villagedoor1.c();
         }

         if (j < i) {
            villagedoor = villagedoor1;
            i = j;
         }
      }

      return villagedoor;
   }

   public VillageDoor e(BlockPosition blockposition) {
      if (this.d.i(blockposition) > this.e * this.e) {
         return null;
      } else {
         for (VillageDoor villagedoor : this.b) {
            if (villagedoor.d().getX() == blockposition.getX()
               && villagedoor.d().getZ() == blockposition.getZ()
               && Math.abs(villagedoor.d().getY() - blockposition.getY()) <= 1) {
               return villagedoor;
            }
         }

         return null;
      }
   }

   public void a(VillageDoor villagedoor) {
      this.b.add(villagedoor);
      this.c = this.c.a(villagedoor.d());
      this.n();
      this.f = villagedoor.h();
   }

   public boolean g() {
      return this.b.isEmpty();
   }

   public void a(EntityLiving entityliving) {
      for (Village.Aggressor village_aggressor : this.k) {
         if (village_aggressor.a == entityliving) {
            village_aggressor.b = this.g;
            return;
         }
      }

      this.k.add(new Village.Aggressor(entityliving, this.g));
   }

   public EntityLiving b(EntityLiving entityliving) {
      double d0 = Double.MAX_VALUE;
      Village.Aggressor village_aggressor = null;

      for (Village.Aggressor village_aggressor1 : this.k) {
         double d1 = village_aggressor1.a.h(entityliving);
         if (d1 <= d0) {
            village_aggressor = village_aggressor1;
            d0 = d1;
         }
      }

      return village_aggressor != null ? village_aggressor.a : null;
   }

   public EntityHuman c(EntityLiving entityliving) {
      double d0 = Double.MAX_VALUE;
      EntityHuman entityhuman = null;

      for (String s : this.j.keySet()) {
         if (this.d(s)) {
            EntityHuman entityhuman1 = this.a.a(s);
            if (entityhuman1 != null) {
               double d1 = entityhuman1.h(entityliving);
               if (d1 <= d0) {
                  entityhuman = entityhuman1;
                  d0 = d1;
               }
            }
         }
      }

      return entityhuman;
   }

   private void l() {
      this.k.removeIf(village_aggressor -> !village_aggressor.a.isAlive() || Math.abs(this.g - village_aggressor.b) > 300);
   }

   private void m() {
      boolean flag = false;
      boolean flag1 = this.a.random.nextInt(50) == 0;
      Iterator<VillageDoor> iterator = this.b.iterator();

      while (iterator.hasNext()) {
         VillageDoor villagedoor = iterator.next();
         if (flag1) {
            villagedoor.a();
         }

         if (!this.f(villagedoor.d()) || Math.abs(this.g - villagedoor.h()) > 1200) {
            this.c = this.c.b(villagedoor.d());
            flag = true;
            villagedoor.a(true);
            iterator.remove();
         }
      }

      if (flag) {
         this.n();
      }
   }

   private boolean f(BlockPosition blockposition) {
      IBlockData iblockdata = this.a.getTypeIfLoaded(blockposition.getX(), blockposition.getY(), blockposition.getZ());
      if (iblockdata == null) {
         return false;
      } else {
         Block block = iblockdata.getBlock();
         return block instanceof BlockDoor && block.getMaterial() == Material.WOOD;
      }
   }

   private void n() {
      int i = this.b.size();
      if (i == 0) {
         this.d = new BlockPosition(0, 0, 0);
         this.e = 0;
      } else {
         this.d = new BlockPosition(this.c.getX() / i, this.c.getY() / i, this.c.getZ() / i);
         int j = 0;

         for (VillageDoor villagedoor : this.b) {
            j = Math.max(villagedoor.a(this.d), j);
         }

         this.e = Math.max(32, (int)Math.sqrt(j) + 1);
         this.calculateNewCheckPositions();
      }
   }

   public int a(String s) {
      Integer integer = this.j.get(s);
      return integer != null ? integer : 0;
   }

   public int a(String s, int i) {
      int j = this.a(s);
      int k = MathHelper.clamp(j + i, -30, 10);
      this.j.put(s, k);
      return k;
   }

   public boolean d(String s) {
      return this.a(s) <= -15;
   }

   public void a(NBTTagCompound nbttagcompound) {
      this.h = nbttagcompound.getInt("PopSize");
      this.e = nbttagcompound.getInt("Radius");
      this.l = nbttagcompound.getInt("Golems");
      this.f = nbttagcompound.getInt("Stable");
      this.g = nbttagcompound.getInt("Tick");
      this.i = nbttagcompound.getInt("MTick");
      this.d = new BlockPosition(nbttagcompound.getInt("CX"), nbttagcompound.getInt("CY"), nbttagcompound.getInt("CZ"));
      this.c = new BlockPosition(nbttagcompound.getInt("ACX"), nbttagcompound.getInt("ACY"), nbttagcompound.getInt("ACZ"));
      NBTTagList nbttaglist = nbttagcompound.getList("Doors", 10);

      for (int i = 0; i < nbttaglist.size(); i++) {
         NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
         VillageDoor villagedoor = new VillageDoor(
            new BlockPosition(nbttagcompound1.getInt("X"), nbttagcompound1.getInt("Y"), nbttagcompound1.getInt("Z")),
            nbttagcompound1.getInt("IDX"),
            nbttagcompound1.getInt("IDZ"),
            nbttagcompound1.getInt("TS")
         );
         this.b.add(villagedoor);
      }

      NBTTagList nbttaglist1 = nbttagcompound.getList("Players", 10);

      for (int j = 0; j < nbttaglist1.size(); j++) {
         NBTTagCompound nbttagcompound2 = nbttaglist1.get(j);
         if (nbttagcompound2.hasKey("UUID")) {
            UserCache usercache = MinecraftServer.getServer().getUserCache();
            GameProfile gameprofile = usercache.a(FastUUID.parseUUID(nbttagcompound2.getString("UUID")));
            if (gameprofile != null) {
               this.j.put(gameprofile.getName(), nbttagcompound2.getInt("S"));
            }
         } else {
            this.j.put(nbttagcompound2.getString("Name"), nbttagcompound2.getInt("S"));
         }
      }

      this.calculateNewCheckPositions();
   }

   public void b(NBTTagCompound nbttagcompound) {
      nbttagcompound.setInt("PopSize", this.h);
      nbttagcompound.setInt("Radius", this.e);
      nbttagcompound.setInt("Golems", this.l);
      nbttagcompound.setInt("Stable", this.f);
      nbttagcompound.setInt("Tick", this.g);
      nbttagcompound.setInt("MTick", this.i);
      nbttagcompound.setInt("CX", this.d.getX());
      nbttagcompound.setInt("CY", this.d.getY());
      nbttagcompound.setInt("CZ", this.d.getZ());
      nbttagcompound.setInt("ACX", this.c.getX());
      nbttagcompound.setInt("ACY", this.c.getY());
      nbttagcompound.setInt("ACZ", this.c.getZ());
      NBTTagList nbttaglist = new NBTTagList();

      for (VillageDoor villagedoor : this.b) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         nbttagcompound1.setInt("X", villagedoor.d().getX());
         nbttagcompound1.setInt("Y", villagedoor.d().getY());
         nbttagcompound1.setInt("Z", villagedoor.d().getZ());
         nbttagcompound1.setInt("IDX", villagedoor.f());
         nbttagcompound1.setInt("IDZ", villagedoor.g());
         nbttagcompound1.setInt("TS", villagedoor.h());
         nbttaglist.add(nbttagcompound1);
      }

      nbttagcompound.set("Doors", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for (String s : this.j.keySet()) {
         NBTTagCompound nbttagcompound2 = new NBTTagCompound();
         UserCache usercache = MinecraftServer.getServer().getUserCache();
         GameProfile gameprofile = usercache.getProfile(s);
         if (gameprofile != null) {
            nbttagcompound2.setString("UUID", FastUUID.toString(gameprofile.getId()));
            nbttagcompound2.setInt("S", this.j.get(s));
            nbttaglist1.add(nbttagcompound2);
         }
      }

      nbttagcompound.set("Players", nbttaglist1);
   }

   public void h() {
      this.i = this.g;
   }

   public boolean i() {
      return this.i == 0 || this.g - this.i >= 3600;
   }

   public void b(int i) {
      for (String s : this.j.keySet()) {
         this.a(s, i);
      }
   }

   static class Aggressor {
      public EntityLiving a;
      public int b;

      Aggressor(EntityLiving entityliving, int i) {
         this.a = entityliving;
         this.b = i;
      }
   }
}
