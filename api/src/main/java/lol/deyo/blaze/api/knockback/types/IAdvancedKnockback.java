package lol.deyo.blaze.api.knockback.types;

public interface IAdvancedKnockback {
   double getHorizontal();

   void setHorizontal(double var1);

   double getHorizontalOnGround();

   void setHorizontalOnGround(double var1);

   double getHorizontalSprinting();

   void setHorizontalSprinting(double var1);

   boolean isHorizontalInherit();

   void setHorizontalInherit(boolean var1);

   double getHorizontalFriction();

   void setHorizontalFriction(double var1);

   double getHorizontalKnockbackDeduction();

   void setHorizontalKnockbackDeduction(double var1);

   double getVertical();

   void setVertical(double var1);

   double getVerticalOnGround();

   void setVerticalOnGround(double var1);

   double getVerticalInAir();

   void setVerticalInAir(double var1);

   double getVerticalSprinting();

   void setVerticalSprinting(double var1);

   boolean isVerticalInherit();

   void setVerticalInherit(boolean var1);

   double getVerticalFriction();

   void setVerticalFriction(double var1);

   boolean isOnePointSevenEnabled();

   void setOnePointSevenEnabled(boolean var1);

   double getOnePointSevenHorizontal();

   void setOnePointSevenHorizontal(double var1);

   boolean isEnabledVerticalLimit();

   void setEnableVerticalLimit(boolean var1);

   double getVerticalLimit();

   void setVerticalLimit(double var1);

   double getSlowdown();

   void setSlowdown(double var1);

   boolean isCancelSprint();

   void setCancelSprint(boolean var1);
}
