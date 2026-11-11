package net.minecraft.server.v1_8_R3;

public class TileEntityMobSpawnerData extends WeightedRandom.WeightedRandomChoice {
   private final MobSpawnerAbstract b;
   private final NBTTagCompound c;
   private final String d;

   public TileEntityMobSpawnerData(MobSpawnerAbstract var1, NBTTagCompound var2) {
      this(var1, var2.getCompound("Properties"), var2.getString("Type"), var2.getInt("Weight"));
   }

   public TileEntityMobSpawnerData(MobSpawnerAbstract var1, NBTTagCompound var2, String var3) {
      this(var1, var2, var3, 1);
   }

   private TileEntityMobSpawnerData(MobSpawnerAbstract var1, NBTTagCompound var2, String var3, int var4) {
      super(var4);
      this.b = var1;
      if (var3.equals("Minecart")) {
         if (var2 != null) {
            var3 = EntityMinecartAbstract.EnumMinecartType.a(var2.getInt("Type")).b();
         } else {
            var3 = "MinecartRideable";
         }
      }

      this.c = var2;
      this.d = var3;
   }

   public NBTTagCompound a() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.set("Properties", this.c);
      var1.setString("Type", this.d);
      var1.setInt("Weight", this.a);
      return var1;
   }
}
