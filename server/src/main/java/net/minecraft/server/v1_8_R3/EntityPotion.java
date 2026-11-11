package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.knockback.Knockback;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.PotionEffectAddEvent.EffectCause;

public class EntityPotion extends EntityProjectile {
   public ItemStack item;

   public EntityPotion(World world) {
      super(world);
   }

   public EntityPotion(World world, EntityLiving entityliving, int i) {
      this(world, entityliving, new ItemStack(Items.POTION, 1, i));
   }

   public EntityPotion(World world, EntityLiving entityliving, ItemStack itemstack) {
      super(world, entityliving);
      this.item = itemstack;
      this.healPotion = this.item.getData() == 16421;
   }

   public EntityPotion(World world, double d0, double d1, double d2, ItemStack itemstack) {
      super(world, d0, d1, d2);
      this.item = itemstack;
      this.healPotion = this.item.getData() == 16421;
   }

   @Override
   protected float m() {
      Knockback knockback = this.getProfile();
      return (Float)knockback.potionFall.getValue();
   }

   @Override
   protected float j() {
      Knockback knockback = this.getProfile();
      return (Float)knockback.potionSpeed.getValue();
   }

   @Override
   protected float l() {
      Knockback knockback = this.getProfile();
      return (Float)knockback.potionVerticalOffset.getValue();
   }

   @Override
   public float getRandomness() {
      return (Float)BlazeConfig.potionTrajectoryRandomness.getValue();
   }

   public int getPotionValue() {
      if (this.item == null) {
         this.item = new ItemStack(Items.POTION, 1, 0);
      }

      return this.item.getData();
   }

   public void setPotionValue(int i) {
      if (this.item == null) {
         this.item = new ItemStack(Items.POTION, 1, 0);
      }

      this.item.setData(i);
   }

   @Override
   protected void a(MovingObjectPosition movingobjectposition) {
      List<MobEffect> list = Items.POTION.h(this.item);
      AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0, 2.0, 4.0);
      List<EntityLiving> list1 = this.world.a(EntityLiving.class, axisalignedbb);
      Iterator<EntityLiving> iterator = list1.iterator();
      HashMap<LivingEntity, Double> affected = new HashMap<>();

      while (iterator.hasNext()) {
         EntityLiving entityliving = iterator.next();
         double d0 = this.h(entityliving);
         if (d0 < 16.0 && (!(entityliving instanceof EntityPlayer) || ((EntityPlayer)entityliving).canSee(this))) {
            double d1 = 1.0 - Math.sqrt(d0) / 4.0;
            if (entityliving == movingobjectposition.entity) {
               d1 = 1.0;
            }

            affected.put((LivingEntity)entityliving.getBukkitEntity(), d1);
         }
      }

      PotionSplashEvent event = CraftEventFactory.callPotionSplashEvent(this, affected);
      EntityLiving shooter = this.getShooter();
      if (!event.isCancelled() && list != null && !list.isEmpty()) {
         for (LivingEntity victim : event.getAffectedEntities()) {
            if (victim instanceof CraftLivingEntity) {
               EntityLiving entityliving = ((CraftLivingEntity)victim).getHandle();
               double d1 = event.getIntensity(victim);

               for (MobEffect mobeffect : list) {
                  int i = mobeffect.getEffectId();
                  if (this.world.pvpMode
                     || !(shooter instanceof EntityPlayer)
                     || !(entityliving instanceof EntityPlayer)
                     || entityliving == shooter
                     || i != 2 && i != 4 && i != 7 && i != 15 && i != 17 && i != 18 && i != 19) {
                     if (MobEffectList.byId[i].isInstant()) {
                        MobEffectList.byId[i].applyInstantEffect(this, this.getShooter(), entityliving, mobeffect.getAmplifier(), d1);
                     } else {
                        int j = (int)(d1 * mobeffect.getDuration() + 0.5);
                        if (j > 20) {
                           entityliving.addEffect(new MobEffect(i, j, mobeffect.getAmplifier()), EffectCause.POTION_SPLASH);
                        }
                     }
                  }
               }
            }
         }
      }

      this.world.triggerEffect(2002, new BlockPosition(this), this.getPotionValue());
      this.die();
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      if (nbttagcompound.hasKeyOfType("Potion", 10)) {
         this.item = ItemStack.createStack(nbttagcompound.getCompound("Potion"));
      } else {
         this.setPotionValue(nbttagcompound.getInt("potionValue"));
      }

      if (this.item == null) {
         this.die();
      }
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      if (this.item != null) {
         nbttagcompound.set("Potion", this.item.save(new NBTTagCompound()));
      }
   }
}
