package net.minecraft.server.v1_8_R3;

public class DemoWorldServer extends WorldServer {
   private static final long I = "North Carolina".hashCode();
   public static final WorldSettings a = new WorldSettings(I, WorldSettings.EnumGamemode.SURVIVAL, true, false, WorldType.NORMAL).a();

   public DemoWorldServer(MinecraftServer var1, IDataManager var2, WorldData var3, int var4, MethodProfiler var5) {
      super(var1, var2, var3, var4, var5);
      this.worldData.a(a);
   }
}
