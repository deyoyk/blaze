package net.minecraft.server.v1_8_R3;

public interface PacketListenerPlayIn extends PacketListener {
   void a(PacketPlayInArmAnimation var1);

   void a(PacketPlayInChat var1);

   void a(PacketPlayInTabComplete var1);

   void a(PacketPlayInClientCommand var1);

   void a(PacketPlayInSettings var1);

   void a(PacketPlayInTransaction var1);

   void a(PacketPlayInEnchantItem var1);

   void a(PacketPlayInWindowClick var1);

   void a(PacketPlayInCloseWindow var1);

   void a(PacketPlayInCustomPayload var1);

   void a(PacketPlayInUseEntity var1);

   void a(PacketPlayInKeepAlive var1);

   void a(PacketPlayInFlying var1);

   void a(PacketPlayInAbilities var1);

   void a(PacketPlayInBlockDig var1);

   void a(PacketPlayInEntityAction var1);

   void a(PacketPlayInSteerVehicle var1);

   void a(PacketPlayInHeldItemSlot var1);

   void a(PacketPlayInSetCreativeSlot var1);

   void a(PacketPlayInUpdateSign var1);

   void a(PacketPlayInBlockPlace var1);

   void a(PacketPlayInSpectate var1);

   void a(PacketPlayInResourcePackStatus var1);
}
