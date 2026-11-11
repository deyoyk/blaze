package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class EntityLightning extends EntityWeather {
   public long a;
   public boolean isEffect;
   public boolean isSilent = false;
   private int lifeTicks;
   private int c;

   public EntityLightning(World world, double d0, double d1, double d2) {
      this(world, d0, d1, d2, false);
   }

   public EntityLightning(World world, double d0, double d1, double d2, boolean isEffect) {
      super(world);
      this.isEffect = isEffect;
      this.setPositionRotation(d0, d1, d2, 0.0F, 0.0F);
      this.lifeTicks = 2;
      this.a = this.random.nextLong();
      this.c = this.random.nextInt(3) + 1;
      BlockPosition blockposition = new BlockPosition(this);
      if (!isEffect
         && world.getGameRules().getBoolean("doFireTick")
         && (world.getDifficulty() == EnumDifficulty.NORMAL || world.getDifficulty() == EnumDifficulty.HARD)
         && world.areChunksLoaded(blockposition, 10)) {
         if (world.getType(blockposition).getBlock().getMaterial() == Material.AIR
            && Blocks.FIRE.canPlace(world, blockposition)
            && !CraftEventFactory.callBlockIgniteEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this).isCancelled()) {
            world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
         }

         for (int i = 0; i < 4; i++) {
            int x = blockposition.getX() + this.random.nextInt(3) - 1;
            int y = blockposition.getY() + this.random.nextInt(3) - 1;
            int z = blockposition.getZ() + this.random.nextInt(3) - 1;
            if (world.getType(x, y, z).getBlock().getMaterial() == Material.AIR
               && Blocks.FIRE.canPlace(world, x, y, z)
               && !CraftEventFactory.callBlockIgniteEvent(world, x, y, z, this).isCancelled()) {
               world.setTypeUpdate(new BlockPosition(x, y, z), Blocks.FIRE.getBlockData());
            }
         }
      }
   }

   public EntityLightning(World world, double d0, double d1, double d2, boolean isEffect, boolean isSilent) {
      this(world, d0, d1, d2, isEffect);
      this.isSilent = isSilent;
   }

   @Override
   public void t_() {
      super.t_();
      if (!this.isSilent && this.lifeTicks == 2) {
         float pitch = 0.8F + this.random.nextFloat() * 0.2F;
         int viewDistance = this.world.getServer().getViewDistance() * 16;
         int squaredDistance = viewDistance * viewDistance;

         for (EntityHuman human : this.world.players) {
            EntityPlayer player = (EntityPlayer)human;
            double deltaX = this.locX - player.locX;
            double deltaZ = this.locZ - player.locZ;
            double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
            if (distanceSquared > squaredDistance) {
               double deltaLength = Math.sqrt(distanceSquared);
               double relativeX = player.locX + deltaX / deltaLength * viewDistance;
               double relativeZ = player.locZ + deltaZ / deltaLength * viewDistance;
               player.playerConnection
                  .sendPacket(new PacketPlayOutNamedSoundEffect("ambient.weather.thunder", relativeX, this.locY, relativeZ, 10000.0F, pitch));
            } else {
               player.playerConnection
                  .sendPacket(new PacketPlayOutNamedSoundEffect("ambient.weather.thunder", this.locX, this.locY, this.locZ, 10000.0F, pitch));
            }
         }

         this.world.makeSound(this.locX, this.locY, this.locZ, "random.explode", 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
      }

      this.lifeTicks--;
      if (this.lifeTicks < 0) {
         if (this.c == 0) {
            this.die();
         } else if (this.lifeTicks < -this.random.nextInt(10)) {
            this.c--;
            this.lifeTicks = 1;
            this.a = this.random.nextLong();
            BlockPosition blockposition = new BlockPosition(this);
            if (this.world.getGameRules().getBoolean("doFireTick")
               && this.world.areChunksLoaded(blockposition, 10)
               && this.world.getType(blockposition).getBlock().getMaterial() == Material.AIR
               && Blocks.FIRE.canPlace(this.world, blockposition)
               && !this.isEffect
               && !CraftEventFactory.callBlockIgniteEvent(this.world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this).isCancelled()) {
               this.world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
            }
         }
      }

      if (this.lifeTicks >= 0 && !this.isEffect) {
         double d0 = 3.0;

         for (Entity entity : this.world
            .getEntities(this, new AxisAlignedBB(this.locX - d0, this.locY - d0, this.locZ - d0, this.locX + d0, this.locY + 6.0 + d0, this.locZ + d0))) {
            entity.onLightningStrike(this);
         }
      }
   }

   @Override
   protected void h() {
   }

   @Override
   protected void a(NBTTagCompound nbttagcompound) {
   }

   @Override
   protected void b(NBTTagCompound nbttagcompound) {
   }
}
