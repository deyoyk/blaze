package net.minecraft.server.v1_8_R3;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Painting;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;

public abstract class EntityHanging extends Entity {
   private int c;
   public BlockPosition blockPosition;
   public EnumDirection direction;

   public EntityHanging(World world) {
      super(world);
      this.setSize(0.5F, 0.5F);
   }

   public EntityHanging(World world, BlockPosition blockposition) {
      this(world);
      this.blockPosition = blockposition;
   }

   @Override
   protected void h() {
   }

   public void setDirection(EnumDirection enumdirection) {
      Validate.notNull(enumdirection);
      Validate.isTrue(enumdirection.k().c());
      this.direction = enumdirection;
      this.lastYaw = this.yaw = this.direction.b() * 90;
      this.updateBoundingBox();
   }

   public static AxisAlignedBB calculateBoundingBox(BlockPosition blockPosition, EnumDirection direction, int width, int height) {
      double d0 = blockPosition.getX() + 0.5;
      double d1 = blockPosition.getY() + 0.5;
      double d2 = blockPosition.getZ() + 0.5;
      double d4 = width % 32 == 0 ? 0.5 : 0.0;
      double d5 = height % 32 == 0 ? 0.5 : 0.0;
      d0 -= direction.getAdjacentX() * 0.111146875;
      d2 -= direction.getAdjacentZ() * 0.111146875;
      d1 += d5;
      EnumDirection enumdirection = direction.f();
      d0 += d4 * enumdirection.getAdjacentX();
      d2 += d4 * enumdirection.getAdjacentZ();
      double d6 = width;
      double d7 = height;
      double d8 = width;
      if (direction.k() == EnumDirection.EnumAxis.Z) {
         d8 = 1.0;
      } else {
         d6 = 1.0;
      }

      d6 /= 32.0;
      d7 /= 32.0;
      d8 /= 32.0;
      return new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8);
   }

   private void updateBoundingBox() {
      if (this.direction != null) {
         AxisAlignedBB bb = calculateBoundingBox(this.blockPosition, this.direction, this.l(), this.m());
         this.locX = (bb.a + bb.d) / 2.0;
         this.locY = (bb.b + bb.e) / 2.0;
         this.locZ = (bb.c + bb.f) / 2.0;
         this.a(bb);
      }
   }

   @Override
   public void t_() {
      this.lastX = this.locX;
      this.lastY = this.locY;
      this.lastZ = this.locZ;
      if (this.c++ == this.world.spigotConfig.hangingTickFrequency) {
         this.c = 0;
         if (!this.dead && !this.survives()) {
            Material material = this.world
               .getType(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))
               .getBlock()
               .getMaterial();
            RemoveCause cause;
            if (!material.equals(Material.AIR)) {
               cause = RemoveCause.OBSTRUCTION;
            } else {
               cause = RemoveCause.PHYSICS;
            }

            HangingBreakEvent event = new HangingBreakEvent((Hanging)this.getBukkitEntity(), cause);
            this.world.getServer().getPluginManager().callEvent(event);
            PaintingBreakEvent paintingEvent = null;
            if (this instanceof EntityPainting) {
               paintingEvent = new PaintingBreakEvent(
                  (Painting)this.getBukkitEntity(), org.bukkit.event.painting.PaintingBreakEvent.RemoveCause.valueOf(cause.name())
               );
               paintingEvent.setCancelled(event.isCancelled());
               this.world.getServer().getPluginManager().callEvent(paintingEvent);
            }

            if (this.dead || event.isCancelled() || paintingEvent != null && paintingEvent.isCancelled()) {
               return;
            }

            this.die();
            this.b((Entity)null);
         }
      }
   }

   public boolean survives() {
      if (this.world.getCubes(this, this.getBoundingBox()).isEmpty()) {
         int i = Math.max(1, this.l() / 16);
         int j = Math.max(1, this.m() / 16);
         BlockPosition blockposition = this.blockPosition.shift(this.direction.opposite());
         EnumDirection enumdirection = this.direction.f();

         for (int k = 0; k < i; k++) {
            for (int l = 0; l < j; l++) {
               BlockPosition blockposition1 = blockposition.shift(enumdirection, k).up(l);
               Block block = this.world.getType(blockposition1).getBlock();
               if (!block.getMaterial().isBuildable() && !BlockDiodeAbstract.d(block)) {
                  return false;
               }
            }
         }

         List<Entity> list = this.world.getEntities(this, this.getBoundingBox());
         Iterator<Entity> iterator = list.iterator();

         Entity entity;
         do {
            if (!iterator.hasNext()) {
               return true;
            }

            entity = iterator.next();
         } while (!(entity instanceof EntityHanging));
      }

      return false;
   }

   @Override
   public boolean ad() {
      return true;
   }

   @Override
   public boolean l(Entity entity) {
      return entity instanceof EntityHuman && this.damageEntity(DamageSource.playerAttack((EntityHuman)entity), 0.0F);
   }

   @Override
   public EnumDirection getDirection() {
      return this.direction;
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (this.isInvulnerable(damagesource)) {
         return false;
      } else {
         if (!this.dead) {
            HangingBreakEvent event = new HangingBreakEvent((Hanging)this.getBukkitEntity(), RemoveCause.DEFAULT);
            PaintingBreakEvent paintingEvent = null;
            if (damagesource.getEntity() != null) {
               event = new HangingBreakByEntityEvent(
                  (Hanging)this.getBukkitEntity(), damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity()
               );
               if (this instanceof EntityPainting) {
                  paintingEvent = new PaintingBreakByEntityEvent(
                     (Painting)this.getBukkitEntity(), damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity()
                  );
               }
            } else if (damagesource.isExplosion()) {
               event = new HangingBreakEvent((Hanging)this.getBukkitEntity(), RemoveCause.EXPLOSION);
            }

            this.world.getServer().getPluginManager().callEvent(event);
            if (paintingEvent != null) {
               paintingEvent.setCancelled(event.isCancelled());
               this.world.getServer().getPluginManager().callEvent(paintingEvent);
            }

            if (this.dead || event.isCancelled() || paintingEvent != null && paintingEvent.isCancelled()) {
               return true;
            }

            this.die();
            this.ac();
            this.b(damagesource.getEntity());
         }

         return true;
      }
   }

   @Override
   public void move(double d0, double d1, double d2) {
      if (!this.dead && d0 * d0 + d1 * d1 + d2 * d2 > 0.0) {
         HangingBreakEvent event = new HangingBreakEvent((Hanging)this.getBukkitEntity(), RemoveCause.PHYSICS);
         this.world.getServer().getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return;
         }

         this.die();
         this.b((Entity)null);
      }
   }

   @Override
   public void g(double d0, double d1, double d2) {
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      nbttagcompound.setByte("Facing", (byte)this.direction.b());
      nbttagcompound.setInt("TileX", this.getBlockPosition().getX());
      nbttagcompound.setInt("TileY", this.getBlockPosition().getY());
      nbttagcompound.setInt("TileZ", this.getBlockPosition().getZ());
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      this.blockPosition = new BlockPosition(nbttagcompound.getInt("TileX"), nbttagcompound.getInt("TileY"), nbttagcompound.getInt("TileZ"));
      EnumDirection enumdirection;
      if (nbttagcompound.hasKeyOfType("Direction", 99)) {
         enumdirection = EnumDirection.fromType2(nbttagcompound.getByte("Direction"));
         this.blockPosition = this.blockPosition.shift(enumdirection);
      } else if (nbttagcompound.hasKeyOfType("Facing", 99)) {
         enumdirection = EnumDirection.fromType2(nbttagcompound.getByte("Facing"));
      } else {
         enumdirection = EnumDirection.fromType2(nbttagcompound.getByte("Dir"));
      }

      this.setDirection(enumdirection);
   }

   public abstract int l();

   public abstract int m();

   public abstract void b(Entity var1);

   @Override
   protected boolean af() {
      return false;
   }

   @Override
   public void setPosition(double d0, double d1, double d2) {
      this.locX = d0;
      this.locY = d1;
      this.locZ = d2;
      BlockPosition blockposition = this.blockPosition;
      this.blockPosition = new BlockPosition(d0, d1, d2);
      if (!this.blockPosition.equals(blockposition)) {
         this.updateBoundingBox();
         this.ai = true;
      }
   }

   public BlockPosition getBlockPosition() {
      return this.blockPosition;
   }
}
