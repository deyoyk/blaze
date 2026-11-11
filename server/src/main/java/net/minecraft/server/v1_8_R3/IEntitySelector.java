package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;

public final class IEntitySelector {
   public static final Predicate<Entity> a = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1.isAlive();
      }
   };
   public static final Predicate<Entity> b = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1.isAlive() && var1.passenger == null && var1.vehicle == null;
      }
   };
   public static final Predicate<Entity> c = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1 instanceof IInventory && var1.isAlive();
      }
   };
   public static final Predicate<Entity> d = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return !(var1 instanceof EntityHuman) || !((EntityHuman)var1).isSpectator();
      }
   };

   public static class EntitySelectorEquipable implements Predicate<Entity> {
      private final ItemStack a;

      public EntitySelectorEquipable(ItemStack var1) {
         this.a = var1;
      }

      public boolean apply(Entity var1) {
         if (!var1.isAlive()) {
            return false;
         } else if (!(var1 instanceof EntityLiving)) {
            return false;
         } else {
            EntityLiving var2 = (EntityLiving)var1;
            if (var2.getEquipment(EntityInsentient.c(this.a)) != null) {
               return false;
            } else if (var2 instanceof EntityInsentient) {
               return ((EntityInsentient)var2).bY();
            } else {
               return var2 instanceof EntityArmorStand ? true : var2 instanceof EntityHuman;
            }
         }
      }
   }
}
