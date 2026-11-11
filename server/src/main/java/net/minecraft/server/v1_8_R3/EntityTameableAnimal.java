package net.minecraft.server.v1_8_R3;

import java.util.UUID;

public abstract class EntityTameableAnimal extends EntityAnimal implements EntityOwnable {
   protected PathfinderGoalSit bm = new PathfinderGoalSit(this);

   public EntityTameableAnimal(World var1) {
      super(var1);
      this.cm();
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(16, (byte)0);
      this.datawatcher.a(17, "");
   }

   @Override
   public void b(NBTTagCompound var1) {
      super.b(var1);
      if (this.getOwnerUUID() == null) {
         var1.setString("OwnerUUID", "");
      } else {
         var1.setString("OwnerUUID", this.getOwnerUUID());
      }

      var1.setBoolean("Sitting", this.isSitting());
   }

   @Override
   public void a(NBTTagCompound var1) {
      super.a(var1);
      String var2 = "";
      if (var1.hasKeyOfType("OwnerUUID", 8)) {
         var2 = var1.getString("OwnerUUID");
      } else {
         String var3 = var1.getString("Owner");
         var2 = NameReferencingFileConverter.a(var3);
      }

      if (var2.length() > 0) {
         this.setOwnerUUID(var2);
         this.setTamed(true);
      }

      this.bm.setSitting(var1.getBoolean("Sitting"));
      this.setSitting(var1.getBoolean("Sitting"));
   }

   protected void l(boolean var1) {
      EnumParticle var2 = EnumParticle.HEART;
      if (!var1) {
         var2 = EnumParticle.SMOKE_NORMAL;
      }

      for (int var3 = 0; var3 < 7; var3++) {
         double var4 = this.random.nextGaussian() * 0.02;
         double var6 = this.random.nextGaussian() * 0.02;
         double var8 = this.random.nextGaussian() * 0.02;
         this.world
            .addParticle(
               var2,
               this.locX + this.random.nextFloat() * this.width * 2.0F - this.width,
               this.locY + 0.5 + this.random.nextFloat() * this.length,
               this.locZ + this.random.nextFloat() * this.width * 2.0F - this.width,
               var4,
               var6,
               var8
            );
      }
   }

   public boolean isTamed() {
      return (this.datawatcher.getByte(16) & 4) != 0;
   }

   public void setTamed(boolean var1) {
      byte var2 = this.datawatcher.getByte(16);
      if (var1) {
         this.datawatcher.watch(16, (byte)(var2 | 4));
      } else {
         this.datawatcher.watch(16, (byte)(var2 & -5));
      }

      this.cm();
   }

   protected void cm() {
   }

   public boolean isSitting() {
      return (this.datawatcher.getByte(16) & 1) != 0;
   }

   public void setSitting(boolean var1) {
      byte var2 = this.datawatcher.getByte(16);
      if (var1) {
         this.datawatcher.watch(16, (byte)(var2 | 1));
      } else {
         this.datawatcher.watch(16, (byte)(var2 & -2));
      }
   }

   @Override
   public String getOwnerUUID() {
      return this.datawatcher.getString(17);
   }

   public void setOwnerUUID(String var1) {
      this.datawatcher.watch(17, var1);
   }

   public EntityLiving getOwner() {
      try {
         UUID var1 = UUID.fromString(this.getOwnerUUID());
         return var1 == null ? null : this.world.b(var1);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public boolean e(EntityLiving var1) {
      return var1 == this.getOwner();
   }

   public PathfinderGoalSit getGoalSit() {
      return this.bm;
   }

   public boolean a(EntityLiving var1, EntityLiving var2) {
      return true;
   }

   @Override
   public ScoreboardTeamBase getScoreboardTeam() {
      if (this.isTamed()) {
         EntityLiving var1 = this.getOwner();
         if (var1 != null) {
            return var1.getScoreboardTeam();
         }
      }

      return super.getScoreboardTeam();
   }

   @Override
   public boolean c(EntityLiving var1) {
      if (this.isTamed()) {
         EntityLiving var2 = this.getOwner();
         if (var1 == var2) {
            return true;
         }

         if (var2 != null) {
            return var2.c(var1);
         }
      }

      return super.c(var1);
   }

   @Override
   public void die(DamageSource var1) {
      if (!this.world.isClientSide
         && this.world.getGameRules().getBoolean("showDeathMessages")
         && this.hasCustomName()
         && this.getOwner() instanceof EntityPlayer) {
         ((EntityPlayer)this.getOwner()).sendMessage(this.bs().b());
      }

      super.die(var1);
   }
}
