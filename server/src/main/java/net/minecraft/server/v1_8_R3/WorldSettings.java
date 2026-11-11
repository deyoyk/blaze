package net.minecraft.server.v1_8_R3;

public final class WorldSettings {
   private final long a;
   private final WorldSettings.EnumGamemode b;
   private final boolean c;
   private final boolean d;
   private final WorldType e;
   private boolean g;
   private String h = "";

   public WorldSettings(long paramLong, WorldSettings.EnumGamemode paramEnumGamemode, boolean paramBoolean1, boolean paramBoolean2, WorldType paramWorldType) {
      this.a = paramLong;
      this.b = paramEnumGamemode;
      this.c = paramBoolean1;
      this.d = paramBoolean2;
      this.e = paramWorldType;
   }

   public WorldSettings(WorldData paramWorldData) {
      this(
         paramWorldData.getSeed(),
         paramWorldData.getGameType(),
         paramWorldData.shouldGenerateMapFeatures(),
         paramWorldData.isHardcore(),
         paramWorldData.getType()
      );
   }

   public WorldSettings a() {
      this.g = true;
      return this;
   }

   public WorldSettings setGeneratorSettings(String paramString) {
      this.h = paramString;
      return this;
   }

   public boolean c() {
      return this.g;
   }

   public long d() {
      return this.a;
   }

   public WorldSettings.EnumGamemode e() {
      return this.b;
   }

   public boolean f() {
      return this.d;
   }

   public boolean g() {
      return this.c;
   }

   public WorldType h() {
      return this.e;
   }

   public boolean i() {
      return false;
   }

   public static WorldSettings.EnumGamemode a(int paramInt) {
      return WorldSettings.EnumGamemode.getById(paramInt);
   }

   public String j() {
      return this.h;
   }

   public static enum EnumGamemode {
      NOT_SET(-1, ""),
      SURVIVAL(0, "survival"),
      CREATIVE(1, "creative"),
      ADVENTURE(2, "adventure"),
      SPECTATOR(3, "spectator");

      final int f;
      final String g;

      private EnumGamemode(int param1Int1, String param1String1) {
         this.f = param1Int1;
         this.g = param1String1;
      }

      public int getId() {
         return this.f;
      }

      public String b() {
         return this.g;
      }

      public void a(PlayerAbilities param1PlayerAbilities) {
         if (this == CREATIVE) {
            param1PlayerAbilities.canFly = true;
            param1PlayerAbilities.canInstantlyBuild = true;
            param1PlayerAbilities.isInvulnerable = true;
         } else if (this == SPECTATOR) {
            param1PlayerAbilities.canFly = true;
            param1PlayerAbilities.canInstantlyBuild = false;
            param1PlayerAbilities.isInvulnerable = true;
            param1PlayerAbilities.isFlying = true;
         } else {
            param1PlayerAbilities.canFly = false;
            param1PlayerAbilities.canInstantlyBuild = false;
            param1PlayerAbilities.isInvulnerable = false;
            param1PlayerAbilities.isFlying = false;
         }

         param1PlayerAbilities.mayBuild = !this.c();
      }

      public boolean c() {
         return this == ADVENTURE || this == SPECTATOR;
      }

      public boolean d() {
         return this == CREATIVE;
      }

      public boolean e() {
         return this == SURVIVAL || this == ADVENTURE;
      }

      public static WorldSettings.EnumGamemode getById(int param1Int) {
         WorldSettings.EnumGamemode[] var4;
         for (WorldSettings.EnumGamemode enumGamemode : var4 = values()) {
            if (enumGamemode.f == param1Int) {
               return enumGamemode;
            }
         }

         return SURVIVAL;
      }
   }
}
