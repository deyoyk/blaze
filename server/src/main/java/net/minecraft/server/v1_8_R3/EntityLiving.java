package net.minecraft.server.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.knockback.Knockback;
import lol.deyo.blaze.knockback.KnockbackType;
import lol.deyo.blaze.knockback.types.SimpleKnockback;
import lol.deyo.blaze.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.TrigMath;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.entity.PotionEffectExtendEvent;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PotionEffectAddEvent.EffectCause;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

public abstract class EntityLiving extends Entity {
   private static final UUID a = FastUUID.parseUUID("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
   private static final AttributeModifier b = new AttributeModifier(a, "Sprinting speed boost", 0.3F, 2).a(false);
   private static final Map<Integer, MobEffect> EMPTY_EFFECTS = Collections.emptyMap();
   public Map<Integer, MobEffect> effects = EMPTY_EFFECTS;
   private final ItemStack[] h = new ItemStack[5];
   public CombatTracker combatTracker = new CombatTracker(this);
   public boolean ar;
   public int as;
   public int at;
   public int hurtTicks;
   public int av;
   public float aw;
   public int deathTicks;
   public float ay;
   public float az;
   public float aA;
   public float aB;
   public float aC;
   public int maxNoDamageTicks = 20;
   public float aE;
   public float aF;
   public float aG;
   public float aH;
   public float aI;
   public float aJ;
   public float aK;
   public float aL;
   public float aM = 0.02F;
   public EntityHuman killer;
   public float lastDamage;
   public float aZ;
   public float ba;
   public boolean updateEffects = true;
   public EntityLiving lastDamager;
   public int hurtTimestamp;
   public int expToDrop;
   public int maxAirTicks = 300;
   protected int lastDamageByPlayerTime;
   protected boolean aP;
   protected int ticksFarFromPlayer;
   protected float aR;
   protected float aS;
   protected float aT;
   protected float aU;
   protected float aV;
   protected int aW;
   protected boolean aY;
   protected float bb;
   protected int bc;
   protected double bd;
   protected double be;
   protected double bf;
   protected double bg;
   protected double bh;
   ArrayList<org.bukkit.inventory.ItemStack> drops = null;
   private AttributeMapBase c;
   private EntityLiving bk;
   private int bl;
   private float bm;
   private int bn;
   private float bo;

   private Map<Integer, MobEffect> effectsMutable() {
      if (this.effects == EMPTY_EFFECTS) {
         this.effects = new Int2ObjectOpenHashMap<>();
      }

      return this.effects;
   }

   public EntityLiving(World world) {
      super(world);
      this.initAttributes();
      this.datawatcher.watch(6, (float)this.getAttributeInstance(GenericAttributes.maxHealth).getValue());
      this.k = true;
      this.aH = (float)((Math.random() + 1.0) * 0.01F);
      this.setPosition(this.locX, this.locY, this.locZ);
      this.aG = (float)Math.random() * 12398.0F;
      this.yaw = (float)(Math.random() * (float) Math.PI * 2.0);
      this.aK = this.yaw;
      this.S = 0.6F;
   }

   @Override
   public void inactiveTick() {
      super.inactiveTick();
      this.ticksFarFromPlayer++;
   }

   @Override
   public void G() {
      this.damageEntity(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
   }

   @Override
   protected void h() {
      this.datawatcher.a(7, 0);
      this.datawatcher.a(8, (byte)0);
      this.datawatcher.a(9, (byte)0);
      this.datawatcher.a(6, 1.0F);
   }

   protected void initAttributes() {
      this.getAttributeMap().b(GenericAttributes.maxHealth);
      this.getAttributeMap().b(GenericAttributes.c);
      this.getAttributeMap().b(GenericAttributes.MOVEMENT_SPEED);
   }

   @Override
   protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
      if (!this.V()) {
         this.W();
      }

      super.a(d0, flag, block, blockposition);
   }

   public boolean aY() {
      return false;
   }

   @Override
   public void K() {
      this.ay = this.az;
      super.K();
      boolean flag = this instanceof EntityHuman;
      if (this.isAlive()) {
         if (this.inBlock()) {
            this.damageEntity(DamageSource.STUCK, 1.0F);
         } else if ((Boolean)BlazeConfig.playerBorderDamage.getValue() && flag && !this.world.getWorldBorder().a(this.boundingBox)) {
            double d0 = this.world.getWorldBorder().a(this) + this.world.getWorldBorder().getDamageBuffer();
            if (d0 < 0.0) {
               this.damageEntity(DamageSource.STUCK, Math.max(1, MathHelper.floor(-d0 * this.world.getWorldBorder().getDamageAmount())));
            }
         }
      }

      if (this.isFireProof()) {
         this.extinguish();
      }

      boolean flag1 = flag && ((EntityHuman)this).abilities.isInvulnerable;
      if (this.isAlive()) {
         if (this.a(Material.WATER)) {
            if (!this.aY() && !this.hasEffect(MobEffectList.WATER_BREATHING.id) && !flag1) {
               this.setAirTicks(this.j(this.getAirTicks()));
               if (this.getAirTicks() == -20) {
                  this.setAirTicks(0);
                  this.damageEntity(DamageSource.DROWN, 2.0F);
               }
            }

            if (this.au() && this.vehicle instanceof EntityLiving) {
               this.mount(null);
            }
         } else if (this.getAirTicks() != 300) {
            this.setAirTicks(this.maxAirTicks);
         }
      }

      if (this.isAlive() && this.U()) {
         this.extinguish();
      }

      this.aE = this.aF;
      if (this.hurtTicks > 0) {
         this.hurtTicks--;
      }

      if (this.noDamageTicks > 0 && !(this instanceof EntityPlayer)) {
         this.noDamageTicks--;
      }

      if (this.noDamageTicksArrow > 0 && !(this instanceof EntityPlayer)) {
         this.noDamageTicksArrow--;
      }

      if (this.getHealth() <= 0.0F) {
         this.aZ();
      }

      if (this.lastDamageByPlayerTime > 0) {
         this.lastDamageByPlayerTime--;
      } else {
         this.killer = null;
      }

      if (this.bk != null && !this.bk.isAlive()) {
         this.bk = null;
      }

      if (this.lastDamager != null) {
         if (!this.lastDamager.isAlive()) {
            this.b((EntityLiving)null);
         } else if (this.ticksLived - this.hurtTimestamp > 100) {
            this.b((EntityLiving)null);
         }
      }

      this.bi();
      this.aU = this.aT;
      this.aJ = this.aI;
      this.aL = this.aK;
      this.lastYaw = this.yaw;
      this.lastPitch = this.pitch;
   }

   public int getExpReward() {
      int exp = this.getExpValue(this.killer);
      return (this.lastDamageByPlayerTime > 0 || this.alwaysGivesExp()) && this.ba() && this.world.getGameRules().getBoolean("doMobLoot") ? exp : 0;
   }

   public boolean isBaby() {
      return false;
   }

   protected void aZ() {
      this.deathTicks++;
      if (this.deathTicks >= 20 && !this.dead) {
         int i = this.expToDrop;

         while (i > 0) {
            int j = EntityExperienceOrb.getOrbValue(i);
            i -= j;
            this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
         }

         this.expToDrop = 0;
         this.die();

         for (int var3 = 0; var3 < 20; var3++) {
            this.world
               .addParticle(
                  EnumParticle.EXPLOSION_NORMAL,
                  this.locX + this.random.nextFloat() * this.width * 2.0F - this.width,
                  this.locY + this.random.nextFloat() * this.length,
                  this.locZ + this.random.nextFloat() * this.width * 2.0F - this.width,
                  this.random.nextGaussian() * 0.02,
                  this.random.nextGaussian() * 0.02,
                  this.random.nextGaussian() * 0.02,
                  EnumParticle.EMPTY_ARRAY
               );
         }
      }
   }

   protected boolean ba() {
      return !this.isBaby();
   }

   protected int j(int i) {
      int j = EnchantmentManager.getOxygenEnchantmentLevel(this);
      return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
   }

   protected int getExpValue(EntityHuman entityhuman) {
      return 0;
   }

   protected boolean alwaysGivesExp() {
      return false;
   }

   public Random bc() {
      return this.random;
   }

   public EntityLiving getLastDamager() {
      return this.lastDamager;
   }

   public int be() {
      return this.hurtTimestamp;
   }

   public void b(EntityLiving entityliving) {
      this.lastDamager = entityliving;
      this.hurtTimestamp = this.ticksLived;
   }

   public EntityLiving bf() {
      return this.bk;
   }

   public int bg() {
      return this.bl;
   }

   public void p(Entity entity) {
      if (entity instanceof EntityLiving) {
         this.bk = (EntityLiving)entity;
      } else {
         this.bk = null;
      }

      this.bl = this.ticksLived;
   }

   public int bh() {
      return this.ticksFarFromPlayer;
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      nbttagcompound.setFloat("HealF", this.getHealth());
      nbttagcompound.setShort("Health", (short)Math.ceil(this.getHealth()));
      nbttagcompound.setShort("HurtTime", (short)this.hurtTicks);
      nbttagcompound.setInt("HurtByTimestamp", this.hurtTimestamp);
      nbttagcompound.setShort("DeathTime", (short)this.deathTicks);
      nbttagcompound.setFloat("AbsorptionAmount", this.getAbsorptionHearts());

      ItemStack[] var5;
      for (ItemStack itemstack : var5 = this.getEquipment()) {
         if (itemstack != null) {
            Multimap<String, AttributeModifier> tag = itemstack.B();
            this.c.a(tag);
            this.c.b(tag);
         }
      }

      nbttagcompound.set("Attributes", GenericAttributes.a(this.getAttributeMap()));
      if (!this.effects.isEmpty()) {
         NBTTagList nbttaglist = new NBTTagList();

         for (MobEffect mobEffect : this.effects.values()) {
            nbttaglist.add(mobEffect.a(new NBTTagCompound()));
         }

         nbttagcompound.set("ActiveEffects", nbttaglist);
      }
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      this.setAbsorptionHearts(nbttagcompound.getFloat("AbsorptionAmount"));
      if (nbttagcompound.hasKeyOfType("Attributes", 9) && this.world != null) {
         GenericAttributes.a(this.getAttributeMap(), nbttagcompound.getList("Attributes", 10));
      }

      if (nbttagcompound.hasKeyOfType("ActiveEffects", 9)) {
         NBTTagList nbttaglist = nbttagcompound.getList("ActiveEffects", 10);

         for (int i = 0; i < nbttaglist.size(); i++) {
            NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
            MobEffect mobeffect = MobEffect.b(nbttagcompound1);
            if (mobeffect != null) {
               this.effectsMutable().put(mobeffect.getEffectId(), mobeffect);
            }
         }
      }

      if (nbttagcompound.hasKey("Bukkit.MaxHealth")) {
         NBTBase nbtbase = nbttagcompound.get("Bukkit.MaxHealth");
         if (nbtbase.getTypeId() == 5) {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(((NBTTagFloat)nbtbase).c());
         } else if (nbtbase.getTypeId() == 3) {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(((NBTTagInt)nbtbase).d());
         }
      }

      if (nbttagcompound.hasKeyOfType("HealF", 99)) {
         this.setHealth(nbttagcompound.getFloat("HealF"));
      } else {
         NBTBase nbtbase = nbttagcompound.get("Health");
         if (nbtbase == null) {
            this.setHealth(this.getMaxHealth());
         } else if (nbtbase.getTypeId() == 5) {
            this.setHealth(((NBTTagFloat)nbtbase).h());
         } else if (nbtbase.getTypeId() == 2) {
            this.setHealth(((NBTTagShort)nbtbase).e());
         }
      }

      this.hurtTicks = nbttagcompound.getShort("HurtTime");
      this.deathTicks = nbttagcompound.getShort("DeathTime");
      this.hurtTimestamp = nbttagcompound.getInt("HurtByTimestamp");
   }

   public void bi() {
      Iterator<Integer> iterator = this.effects.keySet().iterator();

      while (iterator.hasNext()) {
         Integer integer = iterator.next();
         MobEffect mobeffect = this.effects.get(integer);
         if (!mobeffect.tick(this)) {
            PotionEffectExpireEvent event = new PotionEffectExpireEvent(
               (LivingEntity)this.getBukkitEntity(),
               new PotionEffect(PotionEffectType.getById(mobeffect.getEffectId()), mobeffect.getDuration(), mobeffect.getAmplifier(), mobeffect.isAmbient())
            );
            this.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               mobeffect.a(
                  new MobEffect(mobeffect.getEffectId(), event.getDuration(), mobeffect.getAmplifier(), mobeffect.isAmbient(), mobeffect.isShowParticles())
               );
            } else {
               iterator.remove();
               this.b(mobeffect);
            }
         } else if (mobeffect.getDuration() % 600 == 0) {
            this.a(mobeffect, false);
         }
      }

      if (this.updateEffects) {
         this.B();
         this.updateEffects = false;
      }

      int i = this.datawatcher.getInt(7);
      boolean flag = this.datawatcher.getByte(8) > 0;
      if (i > 0) {
         boolean flag1;
         if (!this.isInvisible()) {
            flag1 = this.random.nextBoolean();
         } else {
            flag1 = this.random.nextInt(15) == 0;
         }

         if (flag) {
            flag1 &= this.random.nextInt(5) == 0;
         }

         if (flag1) {
            this.world
               .addParticle(
                  flag ? EnumParticle.SPELL_MOB_AMBIENT : EnumParticle.SPELL_MOB,
                  this.locX + (this.random.nextDouble() - 0.5) * this.width,
                  this.locY + this.random.nextDouble() * this.length,
                  this.locZ + (this.random.nextDouble() - 0.5) * this.width,
                  (i >> 16 & 0xFF) / 255.0,
                  (i >> 8 & 0xFF) / 255.0,
                  (i & 0xFF) / 255.0,
                  EnumParticle.EMPTY_ARRAY
               );
         }
      }
   }

   protected void B() {
      if (this.effects.isEmpty()) {
         this.bj();
         this.setInvisible(false);
      } else {
         int i = PotionBrewer.a(this.effects.values());
         this.datawatcher.watch(8, (byte)(PotionBrewer.b(this.effects.values()) ? 1 : 0));
         this.datawatcher.watch(7, i);
         this.setInvisible(this.hasEffect(MobEffectList.INVISIBILITY.id));
      }
   }

   protected void bj() {
      this.datawatcher.watch(8, (byte)0);
      this.datawatcher.watch(7, 0);
   }

   public void removeAllEffects() {
      Iterator<Integer> iterator = this.effects.keySet().iterator();

      while (iterator.hasNext()) {
         Integer integer = iterator.next();
         MobEffect mobeffect = this.effects.get(integer);
         PotionEffectRemoveEvent event = new PotionEffectRemoveEvent(
            (LivingEntity)this.getBukkitEntity(),
            new PotionEffect(PotionEffectType.getById(mobeffect.getEffectId()), mobeffect.getDuration(), mobeffect.getAmplifier(), mobeffect.isAmbient())
         );
         this.world.getServer().getPluginManager().callEvent(event);
         iterator.remove();
         this.b(mobeffect);
      }
   }

   public Collection<MobEffect> getEffects() {
      return this.effects.values();
   }

   public boolean hasEffect(int i) {
      return !this.effects.isEmpty() && this.effects.containsKey(i);
   }

   public boolean hasEffect(MobEffectList mobeffectlist) {
      return this.effects.containsKey(mobeffectlist.id);
   }

   public MobEffect getEffect(MobEffectList mobeffectlist) {
      return this.effects.get(mobeffectlist.id);
   }

   public void addEffect(MobEffect mobeffect) {
      this.addEffect(mobeffect, EffectCause.UNKNOWN);
   }

   public void addEffect(MobEffect mobeffect, EffectCause cause) {
      if (this.d(mobeffect)) {
         LivingEntity entity = (LivingEntity)this.getBukkitEntity();
         PotionEffect effect = Utils.toBukkitPotion(mobeffect);
         PluginManager manager = this.world.getServer().getPluginManager();
         MobEffect current = this.effects.get(mobeffect.getEffectId());
         if (current != null) {
            PotionEffectExtendEvent event = new PotionEffectExtendEvent(entity, effect, Utils.toBukkitPotion(current), cause);
            manager.callEvent(event);
            if (event.isCancelled()) {
               return;
            }

            manager.callEvent(new PotionEffectRemoveEvent(entity, effect));
            current.a(mobeffect);
            this.a(current, true);
         } else {
            PotionEffectAddEvent event = new PotionEffectAddEvent(entity, effect, cause);
            manager.callEvent(event);
            if (event.isCancelled()) {
               return;
            }

            this.effectsMutable().put(mobeffect.getEffectId(), mobeffect);
            this.a(mobeffect);
         }
      }
   }

   public boolean d(MobEffect mobeffect) {
      if (this.getMonsterType() == EnumMonsterType.UNDEAD) {
         int i = mobeffect.getEffectId();
         return i != MobEffectList.REGENERATION.id && i != MobEffectList.POISON.id;
      } else {
         return true;
      }
   }

   public boolean bm() {
      return this.getMonsterType() == EnumMonsterType.UNDEAD;
   }

   public void removeEffect(int i) {
      MobEffect mobeffect = this.effects.remove(i);
      if (mobeffect != null) {
         PotionEffect effect = new PotionEffect(
            PotionEffectType.getById(mobeffect.getEffectId()), mobeffect.getDuration(), mobeffect.getAmplifier(), mobeffect.isAmbient()
         );
         PotionEffectRemoveEvent event = new PotionEffectRemoveEvent((LivingEntity)this.getBukkitEntity(), effect);
         this.world.getServer().getPluginManager().callEvent(event);
         this.b(mobeffect);
      }
   }

   protected void a(MobEffect mobeffect) {
      this.updateEffects = true;
      MobEffectList.byId[mobeffect.getEffectId()].b(this, this.getAttributeMap(), mobeffect.getAmplifier());
   }

   protected void a(MobEffect mobeffect, boolean flag) {
      this.updateEffects = true;
      if (flag) {
         MobEffectList.byId[mobeffect.getEffectId()].a(this, this.getAttributeMap(), mobeffect.getAmplifier());
         MobEffectList.byId[mobeffect.getEffectId()].b(this, this.getAttributeMap(), mobeffect.getAmplifier());
      }
   }

   protected void b(MobEffect mobeffect) {
      this.updateEffects = true;
      MobEffectList.byId[mobeffect.getEffectId()].a(this, this.getAttributeMap(), mobeffect.getAmplifier());
   }

   public void heal(float f) {
      this.heal(f, RegainReason.CUSTOM);
   }

   public void heal(float f, RegainReason regainReason) {
      float f1 = this.getHealth();
      if (f1 > 0.0F) {
         EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), f, regainReason);
         this.world.getServer().getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            this.setHealth((float)(this.getHealth() + event.getAmount()));
         }
      }
   }

   public final float getHealth() {
      return this instanceof EntityPlayer ? (float)((EntityPlayer)this).getBukkitEntity().getHealth() : this.datawatcher.getFloat(6);
   }

   public void setHealth(float f) {
      if (this instanceof EntityPlayer) {
         CraftPlayer player = ((EntityPlayer)this).getBukkitEntity();
         if (f < 0.0F) {
            player.setRealHealth(0.0);
         } else if (f > player.getMaxHealth()) {
            player.setRealHealth(player.getMaxHealth());
         } else {
            player.setRealHealth(f);
         }

         if (player.getHealth() != 0.0 || !(Boolean)BlazeConfig.autoRespawnPlayer.getValue()) {
            this.datawatcher.watch(6, player.getScaledHealth());
         }
      } else {
         this.datawatcher.watch(6, MathHelper.a(f, 0.0F, this.getMaxHealth()));
      }
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (this.isInvulnerable(damagesource)) {
         return false;
      } else {
         this.ticksFarFromPlayer = 0;
         if (!(this.getHealth() <= 0.0F) && (!damagesource.o() || !this.hasEffect(MobEffectList.FIRE_RESISTANCE))) {
            this.aB = 1.5F;
            boolean flag = true;
            boolean arrow = (Boolean)BlazeConfig.patchArrowBounceBug.getValue() && damagesource.translationIndex.equals("arrow");
            int currentTicks = arrow ? this.noDamageTicksArrow : this.noDamageTicks;
            int customMaxNoDamageTicks = this.maxNoDamageTicks;
            Knockback knockback = null;
            if (this instanceof EntityHuman) {
               knockback = ((EntityHuman)this).getKnockbackProfile();
               customMaxNoDamageTicks = arrow ? (Integer)knockback.hitDelayArrow.getValue() : (Integer)knockback.hitDelay.getValue();
            }

            if (customMaxNoDamageTicks > 0 && currentTicks > customMaxNoDamageTicks / 2.0F) {
               if (f <= this.lastDamage
                  || (!(Boolean)BlazeConfig.patchDoubleHitBug.getValue() || !damagesource.translationIndex.equals("player"))
                     && !this.d(damagesource, f - this.lastDamage)) {
                  this.forceExplosionKnockback = true;
                  return false;
               }

               this.lastDamage = f;
               flag = false;
            } else {
               if (!this.d(damagesource, f)) {
                  return false;
               }

               this.lastDamage = f;
               this.noDamageTicks = knockback != null ? (Integer)knockback.hitDelay.getValue() : this.maxNoDamageTicks;
               this.hurtTicks = this.av = 10;
               if (arrow && knockback != null) {
                  this.noDamageTicksArrow = (Integer)knockback.hitDelayArrow.getValue();
               }
            }

            if (this instanceof EntityAnimal) {
               ((EntityAnimal)this).cq();
               if (this instanceof EntityTameableAnimal) {
                  ((EntityTameableAnimal)this).getGoalSit().setSitting(false);
               }
            }

            this.aw = 0.0F;
            Entity entity = damagesource.getEntity();
            if (entity != null) {
               if (entity instanceof EntityLiving) {
                  this.b((EntityLiving)entity);
               }

               if (entity instanceof EntityHuman) {
                  this.lastDamageByPlayerTime = 100;
                  this.killer = (EntityHuman)entity;
               } else if (entity instanceof EntityWolf) {
                  EntityWolf entitywolf = (EntityWolf)entity;
                  if (entitywolf.isTamed()) {
                     this.lastDamageByPlayerTime = 100;
                     this.killer = null;
                  }
               }
            }

            boolean knockbackCancelled = false;
            if (flag
               && !(knockbackCancelled = this.world.paperSpigotConfig.disableExplosionKnockback && damagesource.isExplosion() && this instanceof EntityHuman)) {
               this.world.broadcastEntityEffect(this, (byte)2);
               if (damagesource != DamageSource.DROWN) {
                  this.ac();
               }

               if (entity != null) {
                  double d0 = entity.locX - this.locX;

                  double d1;
                  for (d1 = entity.locZ - this.locZ; d0 * d0 + d1 * d1 < 1.0E-4; d1 = (Math.random() - Math.random()) * 0.01) {
                     d0 = (Math.random() - Math.random()) * 0.01;
                  }

                  this.aw = (float)(MathHelper.b(d1, d0) * 180.0 / (float) Math.PI - this.yaw);
                  this.a(entity, f, d0, d1);
               } else {
                  this.aw = (int)(Math.random() * 2.0) * 180;
               }
            }

            if (knockbackCancelled) {
               this.world.broadcastEntityEffect(this, (byte)2);
            }

            if (this.getHealth() <= 0.0F) {
               String s = this.bp();
               if (flag && s != null) {
                  this.makeSound(s, this.bB(), this.bC());
               }

               this.die(damagesource);
            } else {
               String s = this.bo();
               if (flag && s != null) {
                  this.makeSound(s, this.bB(), this.bC());
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public void b(ItemStack itemstack) {
      this.makeSound("random.break", 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F, true);

      for (int i = 0; i < 5; i++) {
         Vec3D vec3d = new Vec3D((this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
         vec3d = vec3d.a(-this.pitch * (float) Math.PI / 180.0F);
         vec3d = vec3d.b(-this.yaw * (float) Math.PI / 180.0F);
         Vec3D vec3d1 = new Vec3D((this.random.nextFloat() - 0.5) * 0.3, -this.random.nextFloat() * 0.6 - 0.3, 0.6);
         vec3d1 = vec3d1.a(-this.pitch * (float) Math.PI / 180.0F);
         vec3d1 = vec3d1.b(-this.yaw * (float) Math.PI / 180.0F);
         vec3d1 = vec3d1.add(this.locX, this.locY + this.getHeadHeight(), this.locZ);
         this.world.addParticle(EnumParticle.ITEM_CRACK, vec3d1.a, vec3d1.b, vec3d1.c, vec3d.a, vec3d.b + 0.05, vec3d.c, Item.getId(itemstack.getItem()));
      }
   }

   public void die(DamageSource damagesource) {
      Entity entity = damagesource.getEntity();
      EntityLiving entityliving = this.bt();
      if (this.aW >= 0 && entityliving != null) {
         entityliving.b(this, this.aW);
      }

      if (entity != null) {
         entity.a(this);
      }

      this.aP = true;
      this.bs().g();
      int i = 0;
      if (entity instanceof EntityHuman) {
         i = EnchantmentManager.getBonusMonsterLootEnchantmentLevel((EntityLiving)entity);
      }

      if (this.ba() && this.world.getGameRules().getBoolean("doMobLoot")) {
         this.drops = new ArrayList<>();
         this.dropDeathLoot(this.lastDamageByPlayerTime > 0, i);
         this.dropEquipment(this.lastDamageByPlayerTime > 0, i);
         if (this.lastDamageByPlayerTime > 0 && this.random.nextFloat() < 0.025F + i * 0.01F) {
            this.getRareDrop();
         }

         CraftEventFactory.callEntityDeathEvent(this, this.drops);
         this.drops = null;
      } else {
         CraftEventFactory.callEntityDeathEvent(this);
      }

      this.world.broadcastEntityEffect(this, (byte)3);
   }

   protected void dropEquipment(boolean flag, int i) {
   }

   public void a(Entity attacker, float f, double d0, double d1) {
      if (this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.c).getValue()) {
         this.ai = true;
         float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1);
         if (this instanceof EntityHuman) {
            Knockback profile = ((EntityHuman)this).getKnockbackProfile();
            if (profile.getKnockbackType() == KnockbackType.SIMPLE) {
               SimpleKnockback knockback = profile.simpleKnockback;
               this.motX = this.motX / (Double)knockback.friction.getValue();
               this.motY = this.motY / (Double)knockback.friction.getValue();
               this.motZ = this.motZ / (Double)knockback.friction.getValue();
               this.motX = this.motX - d0 / f1 * (Double)knockback.horizontal.getValue();
               this.motY = this.motY + (Double)knockback.vertical.getValue();
               this.motZ = this.motZ - d1 / f1 * (Double)knockback.horizontal.getValue();
               double verticalLimit = (Double)knockback.verticalLimit.getValue();
               if (this.motY > verticalLimit) {
                  this.motY = verticalLimit;
               }

               return;
            }
         }

         this.motX /= 2.0;
         this.motY /= 2.0;
         this.motZ /= 2.0;
         this.motX -= d0 / f1 * 0.385F;
         this.motY += 0.325F;
         this.motZ -= d1 / f1 * 0.385F;
         if (this.motY > 0.4F) {
            this.motY = 0.4F;
         }
      }
   }

   protected String bo() {
      return "game.neutral.hurt";
   }

   protected String bp() {
      return "game.neutral.die";
   }

   protected void getRareDrop() {
   }

   protected void dropDeathLoot(boolean flag, int i) {
   }

   public boolean k_() {
      if (this instanceof EntityHuman && ((EntityHuman)this).isSpectator()) {
         return false;
      } else {
         Block block = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b), MathHelper.floor(this.locZ)).getBlock();
         return block == Blocks.LADDER || block == Blocks.VINE;
      }
   }

   @Override
   public boolean isAlive() {
      return !this.dead && this.getHealth() > 0.0F;
   }

   @Override
   public void e(float f, float f1) {
      super.e(f, f1);
      MobEffect mobeffect = this.getEffect(MobEffectList.JUMP);
      float f2 = mobeffect != null ? mobeffect.getAmplifier() + 1 : 0.0F;
      int i = MathHelper.f((f - 3.0F - f2) * f1);
      if (i > 0) {
         if (!this.damageEntity(DamageSource.FALL, (float)(i * (Double)BlazeConfig.fallDamageModifier.getValue()))) {
            return;
         }

         this.makeSound(this.n(i), 1.0F, 1.0F);
         if ((Boolean)BlazeConfig.footstepSounds.getValue()) {
            Block block = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.locY - 0.2F), MathHelper.floor(this.locZ)).getBlock();
            if (block.getMaterial() != Material.AIR) {
               Block.StepSound block_stepsound = block.stepSound;
               this.makeSound(block_stepsound.getStepSound(), block_stepsound.getVolume1() * 0.5F, block_stepsound.getVolume2() * 0.75F);
            }
         }
      }
   }

   protected String n(int i) {
      return i > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
   }

   public int br() {
      int i = 0;

      ItemStack[] var5;
      for (ItemStack itemstack : var5 = this.getEquipment()) {
         if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
            i += ((ItemArmor)itemstack.getItem()).c;
         }
      }

      return i;
   }

   protected void damageArmor(float f) {
   }

   protected float applyArmorModifier(DamageSource damagesource, float f) {
      if (!damagesource.ignoresArmor()) {
         int i = 25 - this.br();
         float f1 = f * i;
         f = f1 / 25.0F;
      }

      return f;
   }

   protected float applyMagicModifier(DamageSource damagesource, float f) {
      if (damagesource.isStarvation()) {
         return f;
      } else if (f <= 0.0F) {
         return 0.0F;
      } else {
         int i = EnchantmentManager.a(this.getEquipment(), damagesource);
         if (i > 20) {
            i = 20;
         }

         if (i > 0) {
            int j = 25 - i;
            float f1 = f * j;
            f = f1 / ((Double)BlazeConfig.protectionModifier.getValue()).floatValue();
         }

         return f;
      }
   }

   protected boolean d(DamageSource damagesource, float f) {
      if (this.isInvulnerable(damagesource)) {
         return false;
      } else {
         boolean human = this instanceof EntityHuman;
         ItemStack helmet = damagesource != DamageSource.ANVIL && damagesource != DamageSource.FALLING_BLOCK ? null : this.getEquipment(4);
         Function<Double, Double> hardHat = f12 -> helmet != null ? -(f12 - f12 * 0.75) : -0.0;
         float hardHatModifier = ((Double)hardHat.apply((double)f)).floatValue();
         float var20 = f + hardHatModifier;
         Function<Double, Double> blocking = f13 -> human && !damagesource.ignoresArmor() && ((EntityHuman)this).isBlocking() && f13 > 0.0
            ? -(f13 - (1.0 + f13) * this.world.paperSpigotConfig.playerBlockingDamageMultiplier)
            : -0.0;
         float blockingModifier = ((Double)blocking.apply((double)var20)).floatValue();
         float var21 = var20 + blockingModifier;
         Function<Double, Double> armor = f17 -> -(f17 - this.applyArmorModifier(damagesource, f17.floatValue()));
         float armorModifier = ((Double)armor.apply((double)var21)).floatValue();
         float var22 = var21 + armorModifier;
         Function<Double, Double> resistance = f16 -> {
            if (!damagesource.isStarvation() && this.hasEffect(MobEffectList.RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
               int i = (this.getEffect(MobEffectList.RESISTANCE).getAmplifier() + 1) * 5;
               int j = 25 - i;
               float f1 = f16.floatValue() * j;
               return -(f16 - f1 / 25.0F);
            } else {
               return -0.0;
            }
         };
         float resistanceModifier = ((Double)resistance.apply((double)var22)).floatValue();
         float var23 = var22 + resistanceModifier;
         Function<Double, Double> magic = f15 -> -(f15 - this.applyMagicModifier(damagesource, f15.floatValue()));
         float magicModifier = ((Double)magic.apply((double)var23)).floatValue();
         float var24 = var23 + magicModifier;
         Function<Double, Double> absorption = f14 -> -Math.max(f14 - Math.max(f14 - this.getAbsorptionHearts(), 0.0), 0.0);
         float absorptionModifier = ((Double)absorption.apply((double)var24)).floatValue();
         EntityDamageEvent event = CraftEventFactory.handleLivingEntityDamageEvent(
            this,
            damagesource,
            f,
            hardHatModifier,
            blockingModifier,
            armorModifier,
            resistanceModifier,
            magicModifier,
            absorptionModifier,
            hardHat,
            blocking,
            armor,
            resistance,
            magic,
            absorption
         );
         if (event.isCancelled()) {
            return false;
         } else {
            f = (float)event.getFinalDamage();
            if (helmet != null) {
               helmet.damage((int)(event.getDamage() * 4.0 + this.random.nextFloat() * event.getDamage() * 2.0), this);
            }

            if (!damagesource.ignoresArmor()) {
               float armorDamage = (float)(event.getDamage() + event.getDamage(DamageModifier.BLOCKING) + event.getDamage(DamageModifier.HARD_HAT));
               this.damageArmor(armorDamage);
            }

            absorptionModifier = (float)(-event.getDamage(DamageModifier.ABSORPTION));
            this.setAbsorptionHearts(Math.max(this.getAbsorptionHearts() - absorptionModifier, 0.0F));
            if (f != 0.0F) {
               if (human) {
                  ((EntityHuman)this).applyExhaustion(damagesource.getExhaustionCost());
               }

               float f2 = this.getHealth();
               this.setHealth(f2 - f);
               this.bs().a(damagesource, f2, f);
               if (human) {
                  return true;
               }

               this.setAbsorptionHearts(this.getAbsorptionHearts() - f);
            }

            return true;
         }
      }
   }

   public CombatTracker bs() {
      return this.combatTracker;
   }

   public EntityLiving bt() {
      return (EntityLiving)(this.combatTracker.c() != null
         ? this.combatTracker.c()
         : (this.killer != null ? this.killer : (this.lastDamager != null ? this.lastDamager : null)));
   }

   public final float getMaxHealth() {
      return (float)this.getAttributeInstance(GenericAttributes.maxHealth).getValue();
   }

   public final int bv() {
      return this.datawatcher.getByte(9);
   }

   public final void o(int i) {
      this.datawatcher.watch(9, (byte)i);
   }

   private int n() {
      return this.hasEffect(MobEffectList.FASTER_DIG)
         ? 6 - (1 + this.getEffect(MobEffectList.FASTER_DIG).getAmplifier())
         : (this.hasEffect(MobEffectList.SLOWER_DIG) ? 6 + (1 + this.getEffect(MobEffectList.SLOWER_DIG).getAmplifier()) * 2 : 6);
   }

   public void bw() {
      if (!this.ar || this.as >= this.n() / 2 || this.as < 0) {
         this.as = -1;
         this.ar = true;
         if (this.world instanceof WorldServer) {
            EntityTrackerEntry entry = ((WorldServer)this.world).getTracker().trackedEntities.get(this.getId());
            if (entry != null) {
               entry.broadcastLazily(new PacketPlayOutAnimation(this, 0));
            }
         }
      }
   }

   @Override
   protected void O() {
      this.damageEntity(DamageSource.OUT_OF_WORLD, 4.0F);
   }

   protected void bx() {
      int i = this.n();
      if (this.ar) {
         this.as++;
         if (this.as >= i) {
            this.as = 0;
            this.ar = false;
         }
      } else {
         this.as = 0;
      }

      this.az = (float)this.as / i;
   }

   public AttributeInstance getAttributeInstance(IAttribute iattribute) {
      return this.getAttributeMap().a(iattribute);
   }

   public AttributeMapBase getAttributeMap() {
      if (this.c == null) {
         this.c = new AttributeMapServer();
      }

      return this.c;
   }

   public EnumMonsterType getMonsterType() {
      return EnumMonsterType.UNDEFINED;
   }

   public abstract ItemStack bA();

   public abstract ItemStack getEquipment(int var1);

   @Override
   public abstract void setEquipment(int var1, ItemStack var2);

   @Override
   public void setSprinting(boolean flag) {
      super.setSprinting(flag);
      AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
      if (attributeinstance.a(a) != null) {
         attributeinstance.c(b);
      }

      if (flag) {
         attributeinstance.b(b);
      }
   }

   @Override
   public abstract ItemStack[] getEquipment();

   protected float bB() {
      return 1.0F;
   }

   protected float bC() {
      return this.isBaby()
         ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F
         : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
   }

   protected boolean bD() {
      return this.getHealth() <= 0.0F;
   }

   public void q(Entity entity) {
      double d0 = entity.locX;
      double d1 = entity.boundingBox.b + entity.length;
      double d2 = entity.locZ;
      byte b0 = 1;

      for (int i = -b0; i <= b0; i++) {
         for (int j = -b0; j < b0; j++) {
            if (i != 0 || j != 0) {
               int k = (int)(this.locX + i);
               int l = (int)(this.locZ + j);
               AxisAlignedBB axisalignedbb = this.boundingBox.c(i, 1.0, j);
               if (this.world.a(axisalignedbb).isEmpty()) {
                  if (World.a(this.world, k, (int)this.locY, l)) {
                     this.enderTeleportTo(this.locX + i, this.locY + 1.0, this.locZ + j);
                     return;
                  }

                  if (World.a(this.world, k, (int)this.locY - 1, l) || this.world.getType(k, (int)this.locY - 1, l).getBlock().getMaterial() == Material.WATER) {
                     d0 = this.locX + i;
                     d1 = this.locY + 1.0;
                     d2 = this.locZ + j;
                  }
               }
            }
         }
      }

      this.enderTeleportTo(d0, d1, d2);
   }

   protected float bE() {
      return 0.42F;
   }

   protected void bF() {
      this.motY = this.bE();
      if (this.hasEffect(MobEffectList.JUMP)) {
         this.motY = this.motY + (this.getEffect(MobEffectList.JUMP).getAmplifier() + 1) * 0.1F;
      }

      if (this.isSprinting()) {
         float f = this.yaw * (float) (Math.PI / 180.0);
         this.motX = this.motX - MathHelper.sin(f) * 0.2;
         this.motZ = this.motZ + MathHelper.cos(f) * 0.2;
      }

      this.ai = true;
   }

   protected void bG() {
      this.motY += 0.04F;
   }

   protected void bH() {
      this.motY += 0.04F;
   }

   public void g(float f, float f1) {
      if (this.bM()) {
         if (!this.V() || this instanceof EntityHuman && ((EntityHuman)this).abilities.isFlying) {
            if (this.ab() && (!(this instanceof EntityHuman) || !((EntityHuman)this).abilities.isFlying)) {
               double d0 = this.locY;
               this.a(f, f1, 0.02F);
               this.move(this.motX, this.motY, this.motZ);
               this.motX *= 0.5;
               this.motY *= 0.5;
               this.motZ *= 0.5;
               this.motY -= 0.02;
               if (this.positionChanged && this.c(this.motX, this.motY + 0.6F - this.locY + d0, this.motZ)) {
                  this.motY = 0.3F;
               }
            } else {
               float f5 = 0.91F;
               if (this.onGround) {
                  f5 = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ)).getBlock().frictionFactor
                     * 0.91F;
               }

               float f6 = 0.16277136F / (f5 * f5 * f5);
               float f3;
               if (this.onGround) {
                  f3 = this.bI() * f6;
               } else {
                  f3 = this.aM;
               }

               this.a(f, f1, f3);
               f5 = 0.91F;
               if (this.onGround) {
                  f5 = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ)).getBlock().frictionFactor
                     * 0.91F;
               }

               if (this.k_()) {
                  float f4 = 0.15F;
                  this.motX = MathHelper.a(this.motX, (double)(-f4), (double)f4);
                  this.motZ = MathHelper.a(this.motZ, (double)(-f4), (double)f4);
                  this.fallDistance = 0.0F;
                  if (this.motY < -0.15) {
                     this.motY = -0.15;
                  }

                  if (this.isSneaking() && this instanceof EntityHuman && this.motY < 0.0) {
                     this.motY = 0.0;
                  }
               }

               this.move(this.motX, this.motY, this.motZ);
               if (this.positionChanged && this.k_()) {
                  this.motY = 0.2;
               }

               this.motY -= 0.08;
               this.motY *= 0.98F;
               this.motX *= f5;
               this.motZ *= f5;
            }
         } else {
            double d0 = this.locY;
            float f3x = 0.8F;
            float f4x = 0.02F;
            float f2 = EnchantmentManager.b(this);
            if (f2 > 3.0F) {
               f2 = 3.0F;
            }

            if (!this.onGround) {
               f2 *= 0.5F;
            }

            if (f2 > 0.0F) {
               f3x += (0.54600006F - f3x) * f2 / 3.0F;
               f4x += (this.bI() - f4x) * f2 / 3.0F;
            }

            this.a(f, f1, f4x);
            this.move(this.motX, this.motY, this.motZ);
            this.motX *= f3x;
            this.motY *= 0.8F;
            this.motZ *= f3x;
            this.motY -= 0.02;
            if (this.positionChanged && this.c(this.motX, this.motY + 0.6F - this.locY + d0, this.motZ)) {
               this.motY = 0.3F;
            }
         }
      }

      this.aA = this.aB;
      double d0x = this.locX - this.lastX;
      double d1 = this.locZ - this.lastZ;
      float f2x = MathHelper.sqrt(d0x * d0x + d1 * d1) * 4.0F;
      if (f2x > 1.0F) {
         f2x = 1.0F;
      }

      this.aB = this.aB + (f2x - this.aB) * 0.4F;
      this.aC = this.aC + this.aB;
   }

   public float bI() {
      return this.bm;
   }

   public void k(float f) {
      this.bm = f;
   }

   public boolean r(Entity entity) {
      this.p(entity);
      return false;
   }

   public boolean isSleeping() {
      return false;
   }

   @Override
   public void t_() {
      super.t_();
      int i = this.bv();
      if (i > 0) {
         if (this.at <= 0) {
            this.at = 20 * (30 - i);
         }

         this.at--;
         if (this.at <= 0) {
            this.o(i - 1);
         }
      }

      for (int j = 0; j < 5; j++) {
         ItemStack itemstack = this.h[j];
         ItemStack itemstack1 = this.getEquipment(j);
         if (!ItemStack.matches(itemstack1, itemstack)) {
            ((WorldServer)this.world)
               .getTracker()
               .a(this, new PacketPlayOutEntityEquipment(this.getId(), j, itemstack1, (Boolean)BlazeConfig.obfuscateEntityEquipment.getValue()));
            if (itemstack != null) {
               this.c.a(itemstack.B());
            }

            if (itemstack1 != null) {
               this.c.b(itemstack1.B());
            }

            this.h[j] = itemstack1 == null ? null : itemstack1.cloneItemStack();
         }
      }

      if (this.ticksLived % 20 == 0) {
         this.bs().g();
      }

      this.m();
      double d0 = this.locX - this.lastX;
      double d1 = this.locZ - this.lastZ;
      float f = (float)(d0 * d0 + d1 * d1);
      float f1 = this.aI;
      float f2 = 0.0F;
      this.aR = this.aS;
      float f3 = 0.0F;
      if (f > 0.0025000002F) {
         f3 = 1.0F;
         f2 = (float)Math.sqrt(f) * 3.0F;
         f1 = (float)TrigMath.atan2(d1, d0) * 180.0F / (float) Math.PI - 90.0F;
      }

      if (this.az > 0.0F) {
         f1 = this.yaw;
      }

      if (!this.onGround) {
         f3 = 0.0F;
      }

      this.aS = this.aS + (f3 - this.aS) * 0.3F;
      f2 = this.h(f1, f2);

      while (this.yaw - this.lastYaw < -180.0F) {
         this.lastYaw -= 360.0F;
      }

      while (this.yaw - this.lastYaw >= 180.0F) {
         this.lastYaw += 360.0F;
      }

      while (this.aI - this.aJ < -180.0F) {
         this.aJ -= 360.0F;
      }

      while (this.aI - this.aJ >= 180.0F) {
         this.aJ += 360.0F;
      }

      while (this.pitch - this.lastPitch < -180.0F) {
         this.lastPitch -= 360.0F;
      }

      while (this.pitch - this.lastPitch >= 180.0F) {
         this.lastPitch += 360.0F;
      }

      while (this.aK - this.aL < -180.0F) {
         this.aL -= 360.0F;
      }

      while (this.aK - this.aL >= 180.0F) {
         this.aL += 360.0F;
      }

      this.aT += f2;
   }

   protected float h(float f, float f1) {
      float f2 = MathHelper.g(f - this.aI);
      this.aI += f2 * 0.3F;
      float f3 = MathHelper.g(this.yaw - this.aI);
      boolean flag = f3 < -90.0F || f3 >= 90.0F;
      if (f3 < -75.0F) {
         f3 = -75.0F;
      }

      if (f3 >= 75.0F) {
         f3 = 75.0F;
      }

      this.aI = this.yaw - f3;
      if (f3 * f3 > 2500.0F) {
         this.aI += f3 * 0.2F;
      }

      if (flag) {
         f1 *= -1.0F;
      }

      return f1;
   }

   public void m() {
      if (this.bn > 0) {
         this.bn--;
      }

      if (this.bc > 0) {
         double d0 = this.locX + (this.bd - this.locX) / this.bc;
         double d1 = this.locY + (this.be - this.locY) / this.bc;
         double d2 = this.locZ + (this.bf - this.locZ) / this.bc;
         double d3 = MathHelper.g(this.bg - this.yaw);
         this.yaw = (float)(this.yaw + d3 / this.bc);
         this.pitch = (float)(this.pitch + (this.bh - this.pitch) / this.bc);
         this.bc--;
         this.setPosition(d0, d1, d2);
         this.setYawPitch(this.yaw, this.pitch);
      } else if (!this.bM()) {
         this.motX *= 0.98;
         this.motY *= 0.98;
         this.motZ *= 0.98;
      }

      if (Math.abs(this.motX) < 0.005) {
         this.motX = 0.0;
      }

      if (Math.abs(this.motY) < 0.005) {
         this.motY = 0.0;
      }

      if (Math.abs(this.motZ) < 0.005) {
         this.motZ = 0.0;
      }

      if (this.bD()) {
         this.aY = false;
         this.aZ = 0.0F;
         this.ba = 0.0F;
         this.bb = 0.0F;
      } else if (this.bM()) {
         this.doTick();
      }

      if (this.aY) {
         if (this.V()) {
            this.bG();
         } else if (this.ab()) {
            this.bH();
         } else if (this.onGround && this.bn == 0) {
            this.bF();
            this.bn = 10;
         }
      } else {
         this.bn = 0;
      }

      this.aZ *= 0.98F;
      this.ba *= 0.98F;
      this.bb *= 0.9F;
      this.g(this.aZ, this.ba);
      if ((Boolean)BlazeConfig.entityCollisions.getValue() && this.ticksLived % (Integer)BlazeConfig.entityCollisionsDelay.getValue() == 0) {
         this.bL();
      }
   }

   protected void doTick() {
   }

   protected void bL() {
      List<Entity> list = this.world.a(this, this.boundingBox.grow(0.2F, 0.0, 0.2F), Predicates.and(IEntitySelector.d, Entity::ae));
      if (this.ad() && !list.isEmpty()) {
         this.numCollisions = this.numCollisions - this.world.spigotConfig.maxCollisionsPerEntity;

         for (Entity value : list) {
            if (this.numCollisions > this.world.spigotConfig.maxCollisionsPerEntity) {
               break;
            }

            if (!(value instanceof EntityLiving) || this instanceof EntityPlayer || this.ticksLived % 2 != 0) {
               value.numCollisions++;
               this.numCollisions++;
               this.s(value);
            }
         }

         this.numCollisions = 0;
      }
   }

   protected void s(Entity entity) {
      entity.collide(this);
   }

   @Override
   public void mount(Entity entity) {
      if (this.vehicle != null && entity == null) {
         Entity originalVehicle = this.vehicle;
         if (this.bukkitEntity instanceof LivingEntity && this.vehicle.getBukkitEntity() instanceof Vehicle) {
            VehicleExitEvent event = new VehicleExitEvent((Vehicle)this.vehicle.getBukkitEntity(), (LivingEntity)this.bukkitEntity);
            this.getBukkitEntity().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled() || this.vehicle != originalVehicle) {
               return;
            }
         }

         EntityDismountEvent dismountEvent = new EntityDismountEvent(this.getBukkitEntity(), this.vehicle.getBukkitEntity());
         Bukkit.getPluginManager().callEvent(dismountEvent);
         if (dismountEvent.isCancelled()) {
            return;
         }

         this.q(this.vehicle);
         if (this.vehicle != null) {
            this.vehicle.passenger = null;
         }

         this.vehicle = null;
      } else {
         super.mount(entity);
      }
   }

   @Override
   public void ak() {
      super.ak();
      this.aR = this.aS;
      this.aS = 0.0F;
      this.fallDistance = 0.0F;
   }

   public void i(boolean flag) {
      this.aY = flag;
   }

   public void receive(Entity entity, int i) {
      if (!entity.dead) {
         EntityTracker entitytracker = ((WorldServer)this.world).getTracker();
         if (entity instanceof EntityItem) {
            entitytracker.a(entity, new PacketPlayOutCollect(entity.getId(), this.getId()));
         } else if (entity instanceof EntityArrow) {
            entitytracker.a(entity, new PacketPlayOutCollect(entity.getId(), this.getId()));
         } else if (entity instanceof EntityExperienceOrb) {
            entitytracker.a(entity, new PacketPlayOutCollect(entity.getId(), this.getId()));
         }
      }
   }

   public boolean hasLineOfSight(Entity entity) {
      return this.world
            .rayTrace(
               new Vec3D(this.locX, this.locY + this.getHeadHeight(), this.locZ), new Vec3D(entity.locX, entity.locY + entity.getHeadHeight(), entity.locZ)
            )
         == null;
   }

   @Override
   public Vec3D ap() {
      return this.d(1.0F);
   }

   @Override
   public Vec3D d(float f) {
      if (f == 1.0F) {
         return this.f(this.pitch, this.aK);
      } else {
         float f1 = this.lastPitch + (this.pitch - this.lastPitch) * f;
         float f2 = this.aL + (this.aK - this.aL) * f;
         return this.f(f1, f2);
      }
   }

   public boolean bM() {
      return true;
   }

   @Override
   public boolean ad() {
      return !this.dead;
   }

   @Override
   public boolean ae() {
      return !this.dead;
   }

   @Override
   protected void ac() {
      this.velocityChanged = this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.c).getValue();
   }

   @Override
   public float getHeadRotation() {
      return this.aK;
   }

   @Override
   public void f(float f) {
      this.aK = f;
   }

   @Override
   public void g(float f) {
      this.aI = f;
   }

   public float getAbsorptionHearts() {
      return this.bo;
   }

   public void setAbsorptionHearts(float f) {
      if (f < 0.0F) {
         f = 0.0F;
      }

      this.bo = f;
   }

   public ScoreboardTeamBase getScoreboardTeam() {
      return this.world.getScoreboard().getPlayerTeam(FastUUID.toString(this.getUniqueID()));
   }

   public boolean c(EntityLiving entityliving) {
      return this.a(entityliving.getScoreboardTeam());
   }

   public boolean a(ScoreboardTeamBase scoreboardteambase) {
      ScoreboardTeamBase team = this.getScoreboardTeam();
      return team != null && team.isAlly(scoreboardteambase);
   }

   public void enterCombat() {
   }

   public void exitCombat() {
   }

   protected void bP() {
      this.updateEffects = true;
   }
}
