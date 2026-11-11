package net.minecraft.server.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import java.util.UUID;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;

public abstract class EntityCreature extends EntityInsentient {
   public static final UUID bk = FastUUID.parseUUID("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
   public static final AttributeModifier bl = new AttributeModifier(bk, "Fleeing speed bonus", 2.0, 2).a(false);
   private BlockPosition a = BlockPosition.ZERO;
   private float b = -1.0F;
   private final PathfinderGoal c = new PathfinderGoalMoveTowardsRestriction(this, 1.0);
   private boolean bm;

   public EntityCreature(World world) {
      super(world);
   }

   public float a(BlockPosition blockposition) {
      return 0.0F;
   }

   @Override
   public boolean bR() {
      return super.bR() && this.a(new BlockPosition(this.locX, this.getBoundingBox().b, this.locZ)) >= 0.0F;
   }

   public boolean cf() {
      return !this.navigation.m();
   }

   public boolean cg() {
      return this.e(new BlockPosition(this));
   }

   public boolean e(BlockPosition blockposition) {
      return this.b == -1.0F || this.a.i(blockposition) < this.b * this.b;
   }

   public void a(BlockPosition blockposition, int i) {
      this.a = blockposition;
      this.b = i;
   }

   public BlockPosition ch() {
      return this.a;
   }

   public float ci() {
      return this.b;
   }

   public void cj() {
      this.b = -1.0F;
   }

   public boolean ck() {
      return this.b != -1.0F;
   }

   @Override
   protected void ca() {
      super.ca();
      if (this.cc() && this.getLeashHolder() != null && this.getLeashHolder().world == this.world) {
         Entity entity = this.getLeashHolder();
         this.a(new BlockPosition((int)entity.locX, (int)entity.locY, (int)entity.locZ), 5);
         float f = this.g(entity);
         if (this instanceof EntityTameableAnimal && ((EntityTameableAnimal)this).isSitting()) {
            if (f > 10.0F) {
               this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), UnleashReason.DISTANCE));
               this.unleash(true, true);
            }

            return;
         }

         if (!this.bm) {
            this.goalSelector.a(2, this.c);
            if (this.getNavigation() instanceof Navigation) {
               ((Navigation)this.getNavigation()).a(false);
            }

            this.bm = true;
         }

         this.o(f);
         if (f > 4.0F) {
            this.getNavigation().a(entity, 1.0);
         }

         if (f > 6.0F) {
            double d0 = (entity.locX - this.locX) / f;
            double d1 = (entity.locY - this.locY) / f;
            double d2 = (entity.locZ - this.locZ) / f;
            this.motX = this.motX + d0 * Math.abs(d0) * 0.4;
            this.motY = this.motY + d1 * Math.abs(d1) * 0.4;
            this.motZ = this.motZ + d2 * Math.abs(d2) * 0.4;
         }

         if (f > 10.0F) {
            this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), UnleashReason.DISTANCE));
            this.unleash(true, true);
         }
      } else if (!this.cc() && this.bm) {
         this.bm = false;
         this.goalSelector.a(this.c);
         if (this.getNavigation() instanceof Navigation) {
            ((Navigation)this.getNavigation()).a(true);
         }

         this.cj();
      }
   }

   protected void o(float f) {
   }
}
