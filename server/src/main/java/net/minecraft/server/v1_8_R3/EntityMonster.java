package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityCombustByEntityEvent;

public abstract class EntityMonster extends EntityCreature implements IMonster {
   public EntityMonster(World world) {
      super(world);
      this.b_ = 5;
   }

   @Override
   public void m() {
      this.bx();
      float f = this.c(1.0F);
      if (f > 0.5F) {
         this.ticksFarFromPlayer += 2;
      }

      super.m();
   }

   @Override
   public void t_() {
      super.t_();
      if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.die();
      }
   }

   @Override
   protected String P() {
      return "game.hostile.swim";
   }

   @Override
   protected String aa() {
      return "game.hostile.swim.splash";
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      return this.isInvulnerable(damagesource) ? false : super.damageEntity(damagesource, f);
   }

   @Override
   protected String bo() {
      return "game.hostile.hurt";
   }

   @Override
   protected String bp() {
      return "game.hostile.die";
   }

   @Override
   protected String n(int i) {
      return i > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
   }

   @Override
   public boolean r(Entity entity) {
      float f = (float)this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
      int i = 0;
      if (entity instanceof EntityLiving) {
         f += EnchantmentManager.a(this.bA(), ((EntityLiving)entity).getMonsterType());
         i += EnchantmentManager.a(this);
      }

      boolean flag = entity.damageEntity(DamageSource.mobAttack(this), f);
      if (flag) {
         if (i > 0) {
            entity.g(-MathHelper.sin(this.yaw * (float) Math.PI / 180.0F) * i * 0.5F, 0.1, MathHelper.cos(this.yaw * (float) Math.PI / 180.0F) * i * 0.5F);
            this.motX *= 0.6;
            this.motZ *= 0.6;
         }

         int j = EnchantmentManager.getFireAspectEnchantmentLevel(this);
         if (j > 0) {
            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), j * 4);
            Bukkit.getPluginManager().callEvent(combustEvent);
            if (!combustEvent.isCancelled()) {
               entity.setOnFire(combustEvent.getDuration());
            }
         }

         this.a(this, entity);
      }

      return flag;
   }

   @Override
   public float a(BlockPosition blockposition) {
      return 0.5F - this.world.o(blockposition);
   }

   protected boolean n_() {
      if ((Boolean)BlazeConfig.disableBlockLighting.getValue()) {
         return true;
      } else {
         int x = MathHelper.floor(this.locX);
         int y = MathHelper.floor(this.boundingBox.b);
         int z = MathHelper.floor(this.locZ);
         if (this.world.b(EnumSkyBlock.SKY, x, y, z) > this.random.nextInt(32)) {
            return false;
         } else {
            int i = this.world.getLightLevel(x, y, z);
            if (this.world.R()) {
               int j = this.world.ab();
               this.world.c(10);
               i = this.world.getLightLevel(x, y, z);
               this.world.c(j);
            }

            return i <= this.random.nextInt(8);
         }
      }
   }

   @Override
   public boolean bR() {
      return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.n_() && super.bR();
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
   }

   @Override
   protected boolean ba() {
      return true;
   }
}
