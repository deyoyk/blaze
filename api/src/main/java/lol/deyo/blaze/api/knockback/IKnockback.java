package lol.deyo.blaze.api.knockback;

import lol.deyo.blaze.api.knockback.types.IAdvancedKnockback;
import lol.deyo.blaze.api.knockback.types.ISimpleKnockback;
import java.util.Map;

public interface IKnockback {
   String getName();

   String getType();

   IAdvancedKnockback getAdvancedKnockback();

   ISimpleKnockback getSimpleKnockback();

   boolean isActive();

   double getRodHorizontal();

   void setRodHorizontal(double var1);

   double getRodVertical();

   void setRodVertical(double var1);

   double getRodSpeed();

   void setRodSpeed(double var1);

   boolean getBowBoostDirectional();

   void setBowBoostDirectional(boolean var1);

   boolean isBowBoostOnlyShooter();

   void setBowBoostOnlyShooter(boolean var1);

   double getBowHorizontal();

   void setBowHorizontal(double var1);

   double getBowVertical();

   void setBowVertical(double var1);

   double getBowPunchMultiplier();

   void setBowPunchMultiplier(double var1);

   float getPotionFall();

   void setPotionFall(float var1);

   float getPotionSpeed();

   void setPotionSpeed(float var1);

   float getPotionOffset();

   void setPotionOffset(float var1);

   boolean isPotionFast();

   void setPotionFast(boolean var1);

   int getHitDelay();

   void setHitDelay(int var1);

   int getHitDelayArrow();

   void setHitDelayArrow(int var1);

   boolean isComboMode();

   void setComboMode(boolean var1);

   double getComboHeight();

   void setComboHeight(double var1);

   int getComboTicks();

   void setComboTicks(int var1);

   double getComboVelocity();

   void setComboVelocity(double var1);

   Map<String, Object> getValues();
}
