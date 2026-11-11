package org.bukkit.craftbukkit.v1_8_R3.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityEgg;
import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLargeFireball;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityPotion;
import net.minecraft.server.v1_8_R3.EntitySmallFireball;
import net.minecraft.server.v1_8_R3.EntitySnowball;
import net.minecraft.server.v1_8_R3.EntityThrownExpBottle;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.EntityWitherSkull;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.MobEffect;
import net.minecraft.server.v1_8_R3.MobEffectList;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftEntityEquipment;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class CraftLivingEntity extends CraftEntity implements LivingEntity {
   private CraftEntityEquipment equipment;

   public CraftLivingEntity(CraftServer server, EntityLiving entity) {
      super(server, entity);
      if (entity instanceof EntityInsentient || entity instanceof EntityArmorStand) {
         this.equipment = new CraftEntityEquipment(this);
      }
   }

   @Override
   public double getHealth() {
      return Math.min((double)Math.max(0.0F, this.getHandle().getHealth()), this.getMaxHealth());
   }

   @Override
   public void setHealth(double health) {
      if (!(health < 0.0) && !(health > this.getMaxHealth())) {
         if (this.entity instanceof EntityPlayer && health == 0.0) {
            ((EntityPlayer)this.entity).die(DamageSource.GENERIC);
         }

         this.getHandle().setHealth((float)health);
      } else {
         throw new IllegalArgumentException(
            "Health must be between 0 and "
               + this.getMaxHealth()
               + ", but was "
               + health
               + ". (attribute base value: "
               + this.getHandle().getAttributeInstance(GenericAttributes.maxHealth).b()
               + (this instanceof CraftPlayer ? ", player: " + this.getName() + ')' : ')')
         );
      }
   }

   @Override
   public double getMaxHealth() {
      return this.getHandle().getMaxHealth();
   }

   @Override
   public void setMaxHealth(double amount) {
      Validate.isTrue(amount > 0.0, "Max health must be greater than 0");
      this.getHandle().getAttributeInstance(GenericAttributes.maxHealth).setValue(amount);
      if (this.getHealth() > amount) {
         this.setHealth(amount);
      }
   }

   @Override
   public void resetMaxHealth() {
      this.setMaxHealth(this.getHandle().getMaxHealth());
   }

   @Deprecated
   @Override
   public Egg throwEgg() {
      return this.launchProjectile(Egg.class);
   }

   @Deprecated
   @Override
   public Snowball throwSnowball() {
      return this.launchProjectile(Snowball.class);
   }

   @Override
   public double getEyeHeight() {
      return this.getHandle().getHeadHeight();
   }

   @Override
   public double getEyeHeight(boolean ignoreSneaking) {
      return this.getEyeHeight();
   }

   private List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance, int maxLength) {
      if (maxDistance > 120) {
         maxDistance = 120;
      }

      ArrayList<Block> blocks = new ArrayList<>();
      Iterator<Block> itr = new BlockIterator(this, maxDistance);

      while (itr.hasNext()) {
         Block block = itr.next();
         blocks.add(block);
         if (maxLength != 0 && blocks.size() > maxLength) {
            blocks.remove(0);
         }

         int id = block.getTypeId();
         if (transparent == null ? id != 0 : !transparent.contains((byte)id)) {
            break;
         }
      }

      return blocks;
   }

   private List<Block> getLineOfSight(Set<org.bukkit.Material> transparent, int maxDistance, int maxLength) {
      if (maxDistance > 120) {
         maxDistance = 120;
      }

      ArrayList<Block> blocks = new ArrayList<>();
      Iterator<Block> itr = new BlockIterator(this, maxDistance);

      while (itr.hasNext()) {
         Block block = itr.next();
         blocks.add(block);
         if (maxLength != 0 && blocks.size() > maxLength) {
            blocks.remove(0);
         }

         org.bukkit.Material material = block.getType();
         if (transparent == null ? !material.equals(org.bukkit.Material.AIR) : !transparent.contains(material)) {
            break;
         }
      }

      return blocks;
   }

   @Override
   public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
      return this.getLineOfSight(transparent, maxDistance, 0);
   }

   @Override
   public List<Block> getLineOfSight(Set<org.bukkit.Material> transparent, int maxDistance) {
      return this.getLineOfSight(transparent, maxDistance, 0);
   }

   @Override
   public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
      List<Block> blocks = this.getLineOfSight(transparent, maxDistance, 1);
      return blocks.get(0);
   }

   @Override
   public Block getTargetBlock(Set<org.bukkit.Material> transparent, int maxDistance) {
      List<Block> blocks = this.getLineOfSight(transparent, maxDistance, 1);
      return blocks.get(0);
   }

   @Override
   public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
      return this.getLineOfSight(transparent, maxDistance, 2);
   }

   @Override
   public List<Block> getLastTwoTargetBlocks(Set<org.bukkit.Material> transparent, int maxDistance) {
      return this.getLineOfSight(transparent, maxDistance, 2);
   }

   @Deprecated
   @Override
   public Arrow shootArrow() {
      return this.launchProjectile(Arrow.class);
   }

   @Override
   public int getRemainingAir() {
      return this.getHandle().getAirTicks();
   }

   @Override
   public void setRemainingAir(int ticks) {
      this.getHandle().setAirTicks(ticks);
   }

   @Override
   public int getMaximumAir() {
      return this.getHandle().maxAirTicks;
   }

   @Override
   public void setMaximumAir(int ticks) {
      this.getHandle().maxAirTicks = ticks;
   }

   @Override
   public void damage(double amount) {
      this.damage(amount, null);
   }

   @Override
   public void damage(double amount, Entity source) {
      DamageSource reason = DamageSource.GENERIC;
      if (source instanceof HumanEntity) {
         reason = DamageSource.playerAttack(((CraftHumanEntity)source).getHandle());
      } else if (source instanceof LivingEntity) {
         reason = DamageSource.mobAttack(((CraftLivingEntity)source).getHandle());
      }

      this.entity.damageEntity(reason, (float)amount);
   }

   @Override
   public org.bukkit.Location getEyeLocation() {
      org.bukkit.Location loc = this.getLocation();
      loc.setY(loc.getY() + this.getEyeHeight());
      return loc;
   }

   @Override
   public int getMaximumNoDamageTicks() {
      return this.getHandle().maxNoDamageTicks;
   }

   @Override
   public void setMaximumNoDamageTicks(int ticks) {
      this.getHandle().maxNoDamageTicks = ticks;
   }

   @Override
   public double getLastDamage() {
      return this.getHandle().lastDamage;
   }

   @Override
   public void setLastDamage(double damage) {
      this.getHandle().lastDamage = (float)damage;
   }

   @Override
   public int getNoDamageTicks() {
      return this.getHandle().noDamageTicks;
   }

   @Override
   public void setNoDamageTicks(int ticks) {
      this.getHandle().noDamageTicks = ticks;
   }

   public EntityLiving getHandle() {
      return (EntityLiving)this.entity;
   }

   public void setHandle(EntityLiving entity) {
      super.setHandle(entity);
   }

   @Override
   public String toString() {
      return "CraftLivingEntity{id=" + this.getEntityId() + '}';
   }

   @Override
   public Player getKiller() {
      return this.getHandle().killer == null ? null : (Player)this.getHandle().killer.getBukkitEntity();
   }

   @Override
   public boolean addPotionEffect(PotionEffect effect) {
      return this.addPotionEffect(effect, false);
   }

   @Override
   public boolean addPotionEffect(PotionEffect effect, boolean force) {
      return this.addPotionEffect(effect, force, PotionEffectAddEvent.EffectCause.PLUGIN);
   }

   @Override
   public boolean addPotionEffect(PotionEffect effect, boolean force, PotionEffectAddEvent.EffectCause cause) {
      if (this.hasPotionEffect(effect.getType())) {
         if (!force) {
            return false;
         }

         this.removePotionEffect(effect.getType());
      }

      this.getHandle()
         .addEffect(new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()), cause);
      return true;
   }

   @Override
   public boolean addPotionEffects(Collection<PotionEffect> effects) {
      boolean success = true;

      for (PotionEffect effect : effects) {
         success &= this.addPotionEffect(effect);
      }

      return success;
   }

   @Override
   public boolean hasPotionEffect(PotionEffectType type) {
      return this.getHandle().hasEffect(MobEffectList.byId[type.getId()]);
   }

   @Override
   public void removePotionEffect(PotionEffectType type) {
      this.getHandle().removeEffect(type.getId());
   }

   @Override
   public Collection<PotionEffect> getActivePotionEffects() {
      List<PotionEffect> effects = new ArrayList<>();

      for (MobEffect handle : this.getHandle().effects.values()) {
         effects.add(
            new PotionEffect(
               PotionEffectType.getById(handle.getEffectId()), handle.getDuration(), handle.getAmplifier(), handle.isAmbient(), handle.isShowParticles()
            )
         );
      }

      return effects;
   }

   @Override
   public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
      return this.launchProjectile(projectile, null);
   }

   @Override
   public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
      net.minecraft.server.v1_8_R3.World world = ((CraftWorld)this.getWorld()).getHandle();
      net.minecraft.server.v1_8_R3.Entity launch = null;
      if (Snowball.class.isAssignableFrom(projectile)) {
         launch = new EntitySnowball(world, this.getHandle());
      } else if (Egg.class.isAssignableFrom(projectile)) {
         launch = new EntityEgg(world, this.getHandle());
      } else if (EnderPearl.class.isAssignableFrom(projectile)) {
         launch = new EntityEnderPearl(world, this.getHandle());
      } else if (Arrow.class.isAssignableFrom(projectile)) {
         launch = new EntityArrow(world, this.getHandle(), 1.0F);
      } else if (ThrownPotion.class.isAssignableFrom(projectile)) {
         launch = new EntityPotion(world, this.getHandle(), CraftItemStack.asNMSCopy(new ItemStack(org.bukkit.Material.POTION, 1)));
      } else if (ThrownExpBottle.class.isAssignableFrom(projectile)) {
         launch = new EntityThrownExpBottle(world, this.getHandle());
      } else if (Fish.class.isAssignableFrom(projectile) && this.getHandle() instanceof EntityHuman) {
         launch = new EntityFishingHook(world, (EntityHuman)this.getHandle());
      } else if (Fireball.class.isAssignableFrom(projectile)) {
         org.bukkit.Location location = this.getEyeLocation();
         Vector direction = location.getDirection().multiply(10);
         if (SmallFireball.class.isAssignableFrom(projectile)) {
            launch = new EntitySmallFireball(world, this.getHandle(), direction.getX(), direction.getY(), direction.getZ());
         } else if (WitherSkull.class.isAssignableFrom(projectile)) {
            launch = new EntityWitherSkull(world, this.getHandle(), direction.getX(), direction.getY(), direction.getZ());
         } else {
            launch = new EntityLargeFireball(world, this.getHandle(), direction.getX(), direction.getY(), direction.getZ());
         }

         launch.projectileSource = this;
         launch.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
      }

      Validate.notNull(launch, "Projectile not supported");
      if (velocity != null) {
         ((Projectile)launch.getBukkitEntity()).setVelocity(velocity);
      }

      world.addEntity(launch);
      return (T)launch.getBukkitEntity();
   }

   @Override
   public EntityType getType() {
      return EntityType.UNKNOWN;
   }

   @Override
   public boolean hasLineOfSight(Entity other) {
      return this.getHandle().hasLineOfSight(((CraftEntity)other).getHandle());
   }

   @Override
   public boolean getRemoveWhenFarAway() {
      return this.getHandle() instanceof EntityInsentient && !((EntityInsentient)this.getHandle()).persistent;
   }

   @Override
   public void setRemoveWhenFarAway(boolean remove) {
      if (this.getHandle() instanceof EntityInsentient) {
         ((EntityInsentient)this.getHandle()).persistent = !remove;
      }
   }

   @Override
   public EntityEquipment getEquipment() {
      return this.equipment;
   }

   @Override
   public void setCanPickupItems(boolean pickup) {
      if (this.getHandle() instanceof EntityInsentient) {
         ((EntityInsentient)this.getHandle()).canPickUpLoot = pickup;
      }
   }

   @Override
   public boolean getCanPickupItems() {
      return this.getHandle() instanceof EntityInsentient && ((EntityInsentient)this.getHandle()).canPickUpLoot;
   }

   @Override
   public boolean teleport(org.bukkit.Location location, PlayerTeleportEvent.TeleportCause cause) {
      return this.getHealth() == 0.0 ? false : super.teleport(location, cause);
   }

   @Override
   public boolean isLeashed() {
      return !(this.getHandle() instanceof EntityInsentient) ? false : ((EntityInsentient)this.getHandle()).getLeashHolder() != null;
   }

   @Override
   public Entity getLeashHolder() throws IllegalStateException {
      if (!this.isLeashed()) {
         throw new IllegalStateException("Entity not leashed");
      } else {
         return ((EntityInsentient)this.getHandle()).getLeashHolder().getBukkitEntity();
      }
   }

   private boolean unleash() {
      if (!this.isLeashed()) {
         return false;
      } else {
         ((EntityInsentient)this.getHandle()).unleash(true, false);
         return true;
      }
   }

   @Override
   public boolean setLeashHolder(Entity holder) {
      if (this.getHandle() instanceof EntityWither || !(this.getHandle() instanceof EntityInsentient)) {
         return false;
      } else if (holder == null) {
         return this.unleash();
      } else if (holder.isDead()) {
         return false;
      } else {
         this.unleash();
         ((EntityInsentient)this.getHandle()).setLeashHolder(((CraftEntity)holder).getHandle(), true);
         return true;
      }
   }

   @Deprecated
   @Override
   public int _INVALID_getLastDamage() {
      return NumberConversions.ceil(this.getLastDamage());
   }

   @Deprecated
   @Override
   public void _INVALID_setLastDamage(int damage) {
      this.setLastDamage(damage);
   }

   @Deprecated
   @Override
   public void _INVALID_damage(int amount) {
      this.damage(amount);
   }

   @Deprecated
   @Override
   public void _INVALID_damage(int amount, Entity source) {
      this.damage(amount, source);
   }

   @Deprecated
   @Override
   public int _INVALID_getHealth() {
      return NumberConversions.ceil(this.getHealth());
   }

   @Deprecated
   @Override
   public void _INVALID_setHealth(int health) {
      this.setHealth(health);
   }

   @Deprecated
   @Override
   public int _INVALID_getMaxHealth() {
      return NumberConversions.ceil(this.getMaxHealth());
   }

   @Deprecated
   @Override
   public void _INVALID_setMaxHealth(int health) {
      this.setMaxHealth(health);
   }
}
