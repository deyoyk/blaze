package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.jafama.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.PortalType;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.util.BlockStateListPopulator;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class EntityEnderDragon extends EntityInsentient implements IComplex, IMonster {
   public double a;
   public double b;
   public double c;
   public double[][] bk = new double[64][3];
   public int bl = -1;
   public EntityComplexPart[] children;
   public EntityComplexPart bn;
   public EntityComplexPart bo;
   public EntityComplexPart bp;
   public EntityComplexPart bq;
   public EntityComplexPart br;
   public EntityComplexPart bs;
   public EntityComplexPart bt;
   public float bu;
   public float bv;
   public boolean bw;
   public boolean bx;
   public Entity target;
   public int by;
   public EntityEnderCrystal bz;
   private final Explosion explosionSource = new Explosion(null, this, Double.NaN, Double.NaN, Double.NaN, Float.NaN, true, true);

   public EntityEnderDragon(World world) {
      super(world);
      this.children = new EntityComplexPart[]{
         this.bn = new EntityComplexPart(this, "head", 6.0F, 6.0F),
         this.bo = new EntityComplexPart(this, "body", 8.0F, 8.0F),
         this.bp = new EntityComplexPart(this, "tail", 4.0F, 4.0F),
         this.bq = new EntityComplexPart(this, "tail", 4.0F, 4.0F),
         this.br = new EntityComplexPart(this, "tail", 4.0F, 4.0F),
         this.bs = new EntityComplexPart(this, "wing", 4.0F, 4.0F),
         this.bt = new EntityComplexPart(this, "wing", 4.0F, 4.0F)
      };
      this.setHealth(this.getMaxHealth());
      this.setSize(16.0F, 8.0F);
      this.noclip = true;
      this.fireProof = true;
      this.b = 100.0;
      this.ah = true;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(200.0);
   }

   @Override
   protected void h() {
      super.h();
   }

   public double[] b(int i, float f) {
      if (this.getHealth() <= 0.0F) {
         f = 0.0F;
      }

      f = 1.0F - f;
      int j = this.bl - i & 63;
      int k = this.bl - i - 1 & 63;
      double[] adouble = new double[3];
      double d0 = this.bk[j][0];
      double d1 = MathHelper.g(this.bk[k][0] - d0);
      adouble[0] = d0 + d1 * f;
      d0 = this.bk[j][1];
      d1 = this.bk[k][1] - d0;
      adouble[1] = d0 + d1 * f;
      adouble[2] = this.bk[j][2] + (this.bk[k][2] - this.bk[j][2]) * f;
      return adouble;
   }

   @Override
   public void m() {
      this.bu = this.bv;
      if (this.getHealth() <= 0.0F) {
         float f = (this.random.nextFloat() - 0.5F) * 8.0F;
         float f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
         float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
         this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.locX + f, this.locY + 2.0 + f1, this.locZ + f2, 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
      } else {
         this.n();
         float f = 0.2F / (MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 10.0F + 1.0F);
         f *= (float)FastMath.pow(2.0, this.motY);
         if (this.bx) {
            this.bv += f * 0.5F;
         } else {
            this.bv += f;
         }

         this.yaw = MathHelper.g(this.yaw);
         if (this.ce()) {
            this.bv = 0.5F;
         } else {
            if (this.bl < 0) {
               for (int i = 0; i < this.bk.length; i++) {
                  this.bk[i][0] = this.yaw;
                  this.bk[i][1] = this.locY;
               }
            }

            if (++this.bl == this.bk.length) {
               this.bl = 0;
            }

            this.bk[this.bl][0] = this.yaw;
            this.bk[this.bl][1] = this.locY;
            double d0 = this.b - this.locY;
            double d1 = this.c - this.locZ;
            double d3 = this.a - this.locX;
            double d2 = d3 * d3 + d0 * d0 + d1 * d1;
            if (this.target != null) {
               this.a = this.target.locX;
               this.c = this.target.locZ;
               double d5 = this.a - this.locX;
               double d6 = this.c - this.locZ;
               double d7 = Math.sqrt(d5 * d5 + d6 * d6);
               double d4 = 0.4F + d7 / 80.0 - 1.0;
               if (d4 > 10.0) {
                  d4 = 10.0;
               }

               this.b = this.target.boundingBox.b + d4;
            } else {
               this.a = this.a + this.random.nextGaussian() * 2.0;
               this.c = this.c + this.random.nextGaussian() * 2.0;
            }

            if (this.bw || d2 < 100.0 || d2 > 22500.0 || this.positionChanged || this.E) {
               this.cf();
            }

            d0 /= MathHelper.sqrt(d3 * d3 + d1 * d1);
            float f3 = 0.6F;
            d0 = MathHelper.a(d0, (double)(-f3), (double)f3);
            this.motY += d0 * 0.1F;
            this.yaw = MathHelper.g(this.yaw);
            double d8 = 180.0 - MathHelper.b(d3, d1) * 180.0 / (float) Math.PI;
            double d9 = MathHelper.g(d8 - this.yaw);
            if (d9 > 50.0) {
               d9 = 50.0;
            }

            if (d9 < -50.0) {
               d9 = -50.0;
            }

            Vec3D vec3d = new Vec3D(this.a - this.locX, this.b - this.locY, this.c - this.locZ).a();
            double d4 = -MathHelper.cos(this.yaw * (float) Math.PI / 180.0F);
            Vec3D vec3d1 = new Vec3D(MathHelper.sin(this.yaw * (float) Math.PI / 180.0F), this.motY, d4).a();
            float f4 = ((float)vec3d1.b(vec3d) + 0.5F) / 1.5F;
            if (f4 < 0.0F) {
               f4 = 0.0F;
            }

            this.bb *= 0.8F;
            float f5 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) + 1.0F;
            double d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ) + 1.0;
            if (d10 > 40.0) {
               d10 = 40.0;
            }

            this.bb = (float)(this.bb + d9 * (0.7F / d10 / f5));
            this.yaw = this.yaw + this.bb * 0.1F;
            float f6 = (float)(2.0 / (d10 + 1.0));
            float f7 = 0.06F;
            this.a(0.0F, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));
            if (this.bx) {
               this.move(this.motX * 0.8F, this.motY * 0.8F, this.motZ * 0.8F);
            } else {
               this.move(this.motX, this.motY, this.motZ);
            }

            Vec3D vec3d2 = new Vec3D(this.motX, this.motY, this.motZ).a();
            float f8 = ((float)vec3d2.b(vec3d1) + 1.0F) / 2.0F;
            f8 = 0.8F + 0.15F * f8;
            this.motX *= f8;
            this.motZ *= f8;
            this.motY *= 0.91F;
            this.aI = this.yaw;
            this.bn.width = this.bn.length = 3.0F;
            this.bp.width = this.bp.length = 2.0F;
            this.bq.width = this.bq.length = 2.0F;
            this.br.width = this.br.length = 2.0F;
            this.bo.length = 3.0F;
            this.bo.width = 5.0F;
            this.bs.length = 2.0F;
            this.bs.width = 4.0F;
            this.bt.length = 3.0F;
            this.bt.width = 4.0F;
            float f1 = (float)(this.b(5, 1.0F)[1] - this.b(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
            float f2 = MathHelper.cos(f1);
            float f9 = -MathHelper.sin(f1);
            float f10 = this.yaw * (float) Math.PI / 180.0F;
            float f11 = MathHelper.sin(f10);
            float f12 = MathHelper.cos(f10);
            this.bo.t_();
            this.bo.setPositionRotation(this.locX + f11 * 0.5F, this.locY, this.locZ - f12 * 0.5F, 0.0F, 0.0F);
            this.bs.t_();
            this.bs.setPositionRotation(this.locX + f12 * 4.5F, this.locY + 2.0, this.locZ + f11 * 4.5F, 0.0F, 0.0F);
            this.bt.t_();
            this.bt.setPositionRotation(this.locX - f12 * 4.5F, this.locY + 2.0, this.locZ - f11 * 4.5F, 0.0F, 0.0F);
            if (this.hurtTicks == 0) {
               this.a(this.world.getEntities(this, this.bs.boundingBox.grow(4.0, 2.0, 4.0).c(0.0, -2.0, 0.0)));
               this.a(this.world.getEntities(this, this.bt.boundingBox.grow(4.0, 2.0, 4.0).c(0.0, -2.0, 0.0)));
               this.b(this.world.getEntities(this, this.bn.boundingBox.grow(1.0, 1.0, 1.0)));
            }

            double[] adouble = this.b(5, 1.0F);
            double[] adouble1 = this.b(0, 1.0F);
            f3 = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F - this.bb * 0.01F);
            float f13 = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F - this.bb * 0.01F);
            this.bn.t_();
            this.bn
               .setPositionRotation(this.locX + f3 * 5.5F * f2, this.locY + (adouble1[1] - adouble[1]) + f9 * 5.5F, this.locZ - f13 * 5.5F * f2, 0.0F, 0.0F);

            for (int j = 0; j < 3; j++) {
               EntityComplexPart entitycomplexpart = null;
               if (j == 0) {
                  entitycomplexpart = this.bp;
               }

               if (j == 1) {
                  entitycomplexpart = this.bq;
               }

               if (j == 2) {
                  entitycomplexpart = this.br;
               }

               double[] adouble2 = this.b(12 + j * 2, 1.0F);
               float f14 = this.yaw * (float) Math.PI / 180.0F + this.b(adouble2[0] - adouble[0]) * (float) Math.PI / 180.0F;
               float f15 = MathHelper.sin(f14);
               float f16 = MathHelper.cos(f14);
               float f17 = 1.5F;
               float f18 = (j + 1) * 2.0F;
               entitycomplexpart.t_();
               entitycomplexpart.setPositionRotation(
                  this.locX - (f11 * f17 + f15 * f18) * f2,
                  this.locY + (adouble2[1] - adouble[1]) - (f18 + f17) * f9 + 1.5,
                  this.locZ + (f12 * f17 + f16 * f18) * f2,
                  0.0F,
                  0.0F
               );
            }

            this.bx = this.b(this.bn.boundingBox) | this.b(this.bo.boundingBox);
         }
      }
   }

   private void n() {
      if (this.bz != null) {
         if (this.bz.dead) {
            CraftEventFactory.entityDamage = this.bz;
            this.a(this.bn, DamageSource.explosion(null), 10.0F);
            CraftEventFactory.entityDamage = null;
            this.bz = null;
         } else if (this.ticksLived % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), 1.0, RegainReason.ENDER_CRYSTAL);
            this.world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               this.setHealth((float)(this.getHealth() + event.getAmount()));
            }
         }
      }

      if (this.random.nextInt(10) == 0) {
         float f = 32.0F;
         List<EntityEnderCrystal> list = this.world.a(EntityEnderCrystal.class, this.boundingBox.grow(f, f, f));
         EntityEnderCrystal entityendercrystal = null;
         double d0 = Double.MAX_VALUE;

         for (EntityEnderCrystal entityendercrystal1 : list) {
            double d1 = entityendercrystal1.h(this);
            if (d1 < d0) {
               d0 = d1;
               entityendercrystal = entityendercrystal1;
            }
         }

         this.bz = entityendercrystal;
      }
   }

   private void a(List<Entity> list) {
      double d0 = (this.bo.boundingBox.a + this.bo.boundingBox.d) / 2.0;
      double d1 = (this.bo.boundingBox.c + this.bo.boundingBox.f) / 2.0;

      for (Entity entity : list) {
         if (entity instanceof EntityLiving) {
            double d2 = entity.locX - d0;
            double d3 = entity.locZ - d1;
            double d4 = d2 * d2 + d3 * d3;
            entity.g(d2 / d4 * 4.0, 0.2F, d3 / d4 * 4.0);
         }
      }
   }

   private void b(List<Entity> list) {
      for (Entity entity : list) {
         if (entity instanceof EntityLiving) {
            entity.damageEntity(DamageSource.mobAttack(this), 10.0F);
            this.a(this, entity);
         }
      }
   }

   private void cf() {
      this.bw = false;
      ArrayList<EntityHuman> arraylist = Lists.newArrayList(this.world.players);
      arraylist.removeIf(EntityHuman::isSpectator);
      if (this.random.nextInt(2) == 0 && !arraylist.isEmpty()) {
         Entity target = this.world.players.get(this.random.nextInt(this.world.players.size()));
         EntityTargetEvent event = new EntityTargetEvent(this.getBukkitEntity(), target.getBukkitEntity(), TargetReason.RANDOM_TARGET);
         this.world.getServer().getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            if (event.getTarget() == null) {
               this.target = null;
            } else {
               this.target = ((CraftEntity)event.getTarget()).getHandle();
            }
         }
      } else {
         boolean flag;
         do {
            this.a = 0.0;
            this.b = 70.0F + this.random.nextFloat() * 50.0F;
            this.c = 0.0;
            this.a = this.a + (this.random.nextFloat() * 120.0F - 60.0F);
            this.c = this.c + (this.random.nextFloat() * 120.0F - 60.0F);
            double d0 = this.locX - this.a;
            double d1 = this.locY - this.b;
            double d2 = this.locZ - this.c;
            flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0;
         } while (!flag);

         this.target = null;
      }
   }

   private float b(double d0) {
      return (float)MathHelper.g(d0);
   }

   private boolean b(AxisAlignedBB axisalignedbb) {
      int i = MathHelper.floor(axisalignedbb.a);
      int j = MathHelper.floor(axisalignedbb.b);
      int k = MathHelper.floor(axisalignedbb.c);
      int l = MathHelper.floor(axisalignedbb.d);
      int i1 = MathHelper.floor(axisalignedbb.e);
      int j1 = MathHelper.floor(axisalignedbb.f);
      boolean flag = false;
      boolean flag1 = false;
      List<org.bukkit.block.Block> destroyedBlocks = new ArrayList<>();
      CraftWorld craftWorld = this.world.getWorld();

      for (int k1 = i; k1 <= l; k1++) {
         for (int l1 = j; l1 <= i1; l1++) {
            for (int i2 = k; i2 <= j1; i2++) {
               Block block = this.world.getType(k1, l1, i2).getBlock();
               if (block.getMaterial() != Material.AIR) {
                  if (block != Blocks.BARRIER
                     && block != Blocks.OBSIDIAN
                     && block != Blocks.END_STONE
                     && block != Blocks.BEDROCK
                     && block != Blocks.COMMAND_BLOCK
                     && this.world.getGameRules().getBoolean("mobGriefing")) {
                     flag1 = true;
                     destroyedBlocks.add(craftWorld.getBlockAt(k1, l1, i2));
                  } else {
                     flag = true;
                  }
               }
            }
         }
      }

      if (flag1) {
         org.bukkit.entity.Entity bukkitEntity = this.getBukkitEntity();
         EntityExplodeEvent event = new EntityExplodeEvent(bukkitEntity, bukkitEntity.getLocation(), destroyedBlocks, 0.0F);
         Bukkit.getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return flag;
         }

         if (event.getYield() == 0.0F) {
            for (org.bukkit.block.Block block : event.blockList()) {
               this.world.setAir(new BlockPosition(block.getX(), block.getY(), block.getZ()));
            }
         } else {
            for (org.bukkit.block.Block block : event.blockList()) {
               org.bukkit.Material blockId = block.getType();
               if (blockId != org.bukkit.Material.AIR) {
                  Block nmsBlock = CraftMagicNumbers.getBlock(blockId);
                  BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
                  if (nmsBlock.a(this.explosionSource)) {
                     nmsBlock.dropNaturally(this.world, pos, nmsBlock.fromLegacyData(block.getData()), event.getYield(), 0);
                  }

                  nmsBlock.wasExploded(this.world, pos, this.explosionSource);
                  this.world.setAir(pos);
               }
            }
         }

         double d0 = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * this.random.nextFloat();
         double d1 = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * this.random.nextFloat();
         double d2 = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * this.random.nextFloat();
         this.world.addParticle(EnumParticle.EXPLOSION_LARGE, d0, d1, d2, 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
      }

      return flag;
   }

   @Override
   public boolean a(EntityComplexPart entitycomplexpart, DamageSource damagesource, float f) {
      if (entitycomplexpart != this.bn) {
         f = f / 4.0F + 1.0F;
      }

      float f1 = this.yaw * (float) Math.PI / 180.0F;
      float f2 = MathHelper.sin(f1);
      float f3 = MathHelper.cos(f1);
      this.a = this.locX + f2 * 5.0F + (this.random.nextFloat() - 0.5F) * 2.0F;
      this.b = this.locY + this.random.nextFloat() * 3.0F + 1.0;
      this.c = this.locZ - f3 * 5.0F + (this.random.nextFloat() - 0.5F) * 2.0F;
      this.target = null;
      if (damagesource.getEntity() instanceof EntityHuman || damagesource.isExplosion()) {
         this.dealDamage(damagesource, f);
      }

      return true;
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (damagesource instanceof EntityDamageSource && ((EntityDamageSource)damagesource).w()) {
         this.dealDamage(damagesource, f);
      }

      return false;
   }

   protected boolean dealDamage(DamageSource damagesource, float f) {
      return super.damageEntity(damagesource, f);
   }

   @Override
   public void G() {
      this.die();
   }

   @Override
   protected void aZ() {
      if (!this.dead) {
         this.by++;
         if (this.by >= 180 && this.by <= 200) {
            float f = (this.random.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.world.addParticle(EnumParticle.EXPLOSION_HUGE, this.locX + f, this.locY + 2.0 + f1, this.locZ + f2, 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
         }

         boolean flag = this.world.getGameRules().getBoolean("doMobLoot");
         if (this.by > 150 && this.by % 5 == 0 && flag) {
            int i = this.expToDrop / 12;

            while (i > 0) {
               int j = EntityExperienceOrb.getOrbValue(i);
               i -= j;
               this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
            }
         }

         if (this.by == 1) {
            int viewDistance = this.world.getServer().getViewDistance() * 16;

            for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players) {
               double deltaX = this.locX - player.locX;
               double deltaZ = this.locZ - player.locZ;
               double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
               if (this.world.spigotConfig.dragonDeathSoundRadius <= 0
                  || !(distanceSquared > this.world.spigotConfig.dragonDeathSoundRadius * this.world.spigotConfig.dragonDeathSoundRadius)) {
                  if (distanceSquared > viewDistance * viewDistance) {
                     double deltaLength = Math.sqrt(distanceSquared);
                     double relativeX = player.locX + deltaX / deltaLength * viewDistance;
                     double relativeZ = player.locZ + deltaZ / deltaLength * viewDistance;
                     player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1018, new BlockPosition(relativeX, this.locY, relativeZ), 0, true));
                  } else {
                     player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1018, new BlockPosition(this.locX, this.locY, this.locZ), 0, true));
                  }
               }
            }
         }

         this.move(0.0, 0.1F, 0.0);
         this.aI = this.yaw += 20.0F;
         if (this.by == 200) {
            if (flag) {
               int i = this.expToDrop - 10 * this.expToDrop / 12;

               while (i > 0) {
                  int j = EntityExperienceOrb.getOrbValue(i);
                  i -= j;
                  this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
               }
            }

            this.a(new BlockPosition(this.locX, 64.0, this.locZ));
            this.die();
         }
      }
   }

   private void a(BlockPosition blockposition) {
      BlockStateListPopulator world = new BlockStateListPopulator(this.world.getWorld());

      for (int i = -1; i <= 32; i++) {
         for (int j = -4; j <= 4; j++) {
            for (int k = -4; k <= 4; k++) {
               double d2 = j * j + k * k;
               if (d2 <= 12.25) {
                  BlockPosition blockposition1 = blockposition.a(j, i, k);
                  if (i < 0) {
                     if (d2 <= 6.25) {
                        world.setTypeUpdate(blockposition1, Blocks.BEDROCK.getBlockData());
                     }
                  } else if (i > 0) {
                     world.setTypeUpdate(blockposition1, Blocks.AIR.getBlockData());
                  } else if (d2 > 6.25) {
                     world.setTypeUpdate(blockposition1, Blocks.BEDROCK.getBlockData());
                  } else {
                     world.setTypeUpdate(blockposition1, Blocks.END_PORTAL.getBlockData());
                  }
               }
            }
         }
      }

      world.setTypeUpdate(blockposition, Blocks.BEDROCK.getBlockData());
      world.setTypeUpdate(blockposition.up(), Blocks.BEDROCK.getBlockData());
      BlockPosition blockposition2 = blockposition.up(2);
      world.setTypeUpdate(blockposition2, Blocks.BEDROCK.getBlockData());
      world.setTypeUpdate(blockposition2.west(), Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.EAST));
      world.setTypeUpdate(blockposition2.east(), Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.WEST));
      world.setTypeUpdate(blockposition2.north(), Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.SOUTH));
      world.setTypeUpdate(blockposition2.south(), Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.NORTH));
      world.setTypeUpdate(blockposition.up(3), Blocks.BEDROCK.getBlockData());
      world.setTypeUpdate(blockposition.up(4), Blocks.DRAGON_EGG.getBlockData());
      EntityCreatePortalEvent event = new EntityCreatePortalEvent(
         (LivingEntity)this.getBukkitEntity(), Collections.unmodifiableList(world.getList()), PortalType.ENDER
      );
      this.world.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         for (org.bukkit.block.BlockState state : event.getBlocks()) {
            state.update(true);
         }
      } else {
         for (org.bukkit.block.BlockState state : event.getBlocks()) {
            PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(this.world, new BlockPosition(state.getX(), state.getY(), state.getZ()));

            for (EntityHuman entity : this.world.players) {
               if (entity instanceof EntityPlayer) {
                  ((EntityPlayer)entity).playerConnection.sendPacket(packet);
               }
            }
         }
      }
   }

   @Override
   protected void D() {
   }

   @Override
   public Entity[] aB() {
      return this.children;
   }

   @Override
   public boolean ad() {
      return false;
   }

   @Override
   public World a() {
      return this.world;
   }

   @Override
   protected String z() {
      return "mob.enderdragon.growl";
   }

   @Override
   protected String bo() {
      return "mob.enderdragon.hit";
   }

   @Override
   protected float bB() {
      return 5.0F;
   }

   @Override
   public int getExpReward() {
      return 12000;
   }
}
