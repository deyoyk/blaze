package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;

public interface IMonster extends IAnimal {
   Predicate<Entity> d = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1 instanceof IMonster;
      }
   };
   Predicate<Entity> e = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1 instanceof IMonster && !var1.isInvisible();
      }
   };
}
