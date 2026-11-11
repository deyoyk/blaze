package net.minecraft.server.v1_8_R3;

import com.mojang.authlib.GameProfile;
import java.io.IOException;

public class PacketLoginInStart implements Packet<PacketLoginInListener> {
   private GameProfile a;

   public PacketLoginInStart() {
   }

   public PacketLoginInStart(GameProfile var1) {
      this.a = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = new GameProfile(null, var1.c(16));
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a.getName());
   }

   public void a(PacketLoginInListener var1) {
      var1.a(this);
   }

   public GameProfile a() {
      return this.a;
   }
}
