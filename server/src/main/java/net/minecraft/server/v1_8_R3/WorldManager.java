package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;

public class WorldManager implements IWorldAccess {
   private final MinecraftServer a;
   private final WorldServer world;

   public WorldManager(MinecraftServer minecraftserver, WorldServer worldserver) {
      this.a = minecraftserver;
      this.world = worldserver;
   }

   @Override
   public void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {
   }

   @Override
   public void a(Entity entity) {
      this.world.getTracker().track(entity);
   }

   @Override
   public void b(Entity entity) {
      boolean tnt = entity instanceof EntityTNTPrimed;
      boolean sand = entity instanceof EntityFallingBlock;
      if ((!(Boolean)BlazeConfig.hideTntEntity.getValue() || !tnt) && (!(Boolean)BlazeConfig.hideSandEntity.getValue() || !sand)) {
         this.world.getTracker().untrackEntity(entity);
      }

      if (!tnt && !sand) {
         this.world.getScoreboard().a(entity);
      }
   }

   @Override
   public void a(String s, double d0, double d1, double d2, float f, float f1) {
      this.world.playerMap.sendPacketNearby(null, d0, d1, d2, f > 1.0F ? 16.0F * f : 16.0, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
   }

   @Override
   public void a(EntityHuman entityhuman, String s, double d0, double d1, double d2, float f, float f1) {
      this.world
         .playerMap
         .sendPacketNearby((EntityPlayer)entityhuman, d0, d1, d2, f > 1.0F ? 16.0F * f : 16.0, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
   }

   @Override
   public void a(EntityHuman entityhuman, String s, double d0, double d1, double d2, float f, float f1, boolean self) {
      this.world
         .playerMap
         .sendPacketNearby((EntityPlayer)entityhuman, d0, d1, d2, f > 1.0F ? 16.0F * f : 16.0, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1), self);
   }

   @Override
   public void a(int i, int j, int k, int l, int i1, int j1) {
   }

   @Override
   public void a(int x, int y, int z) {
      this.world.getPlayerChunkMap().flagDirty(x, y, z);
   }

   @Override
   public void a(BlockPosition blockposition) {
      this.world.getPlayerChunkMap().flagDirty(blockposition);
   }

   @Override
   public void b(BlockPosition blockposition) {
   }

   @Override
   public void a(String s, BlockPosition blockposition) {
   }

   @Override
   public void a(EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
      this.world
         .playerMap
         .sendPacketNearby(
            (EntityPlayer)entityhuman,
            blockposition.getX(),
            blockposition.getY(),
            blockposition.getZ(),
            64.0,
            new PacketPlayOutWorldEvent(i, blockposition, j, false)
         );
   }

   @Override
   public void a(int i, BlockPosition blockposition, int j) {
      this.a.getPlayerList().sendAll(new PacketPlayOutWorldEvent(i, blockposition, j, true));
   }

   @Override
   public void b(int i, BlockPosition blockposition, int j) {
      Entity entity = this.world.a(i);
      EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
      this.world
         .playerMap
         .sendPacketNearby(
            player, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 32.0, new PacketPlayOutBlockBreakAnimation(i, blockposition, j)
         );
   }
}
