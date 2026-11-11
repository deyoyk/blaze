package lol.deyo.blaze.knockback.extra;

import lol.deyo.blaze.knockback.Knockback;
import lol.deyo.blaze.knockback.KnockbackType;

public class KnockbackParent {
   private final Knockback knockback;
   private final KnockbackType type;

   public KnockbackParent(Knockback knockback, KnockbackType type) {
      this.knockback = knockback;
      this.type = type;
      knockback.knockbackTypes.add(this);
   }

   public Knockback getKnockback() {
      return this.knockback;
   }

   public KnockbackType getType() {
      return this.type;
   }
}
