package lol.deyo.blaze.api.knockback.types;

public interface ISimpleKnockback {
   void setFriction(double var1);

   double getFriction();

   void setHorizontal(double var1);

   double getHorizontal();

   void setVertical(double var1);

   double getVertical();

   void setVerticalLimit(double var1);

   double getVerticalLimit();

   void setExtraHorizontal(double var1);

   double getExtraHorizontal();

   void setExtraVertical(double var1);

   double getExtraVertical();

   void setSlowdown(double var1);

   double getSlowdown();

   void setOnePointSevenEnabled(boolean var1);

   boolean isOnePointSevenEnabled();

   void setOnePointSevenMultiplier(double var1);

   double getOnePointSevenMultiplier();
}
