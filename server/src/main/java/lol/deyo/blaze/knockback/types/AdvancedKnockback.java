package lol.deyo.blaze.knockback.types;

import lol.deyo.blaze.api.knockback.types.IAdvancedKnockback;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.knockback.Knockback;
import lol.deyo.blaze.knockback.KnockbackType;
import lol.deyo.blaze.knockback.extra.KnockbackParent;

public class AdvancedKnockback extends KnockbackParent implements IAdvancedKnockback {
   public final ConfigSetting<Double> horizontal = new ConfigSetting<>(0.45, "", "Horizontal knockback: horizontal = value");
   public final ConfigSetting<Double> horizontalOnGround = new ConfigSetting<>(1.1, "", "On ground horizontal kb modifier: horizontal *= multiplier");
   public final ConfigSetting<Double> horizontalSprinting = new ConfigSetting<>(0.95, "", "Sprinting horizontal kb modifier: horizontal *= multiplier");
   public final ConfigSetting<Boolean> horizontalInherit = new ConfigSetting<>(true, "", "Inherit victim motion");
   public final ConfigSetting<Double> horizontalFriction = new ConfigSetting<>(
      0.35, "", "Horizontal friction: horizontal = (motion * frictionHorizontal) + victimYaw"
   );
   public final ConfigSetting<Double> horizontalKnockbackDeduction = new ConfigSetting<>(
      0.8, "", "Knockback enchantment modifier: horizontal *= ((enchLevel + 1) - kbDeductionValue)"
   );
   public final ConfigSetting<Double> vertical = new ConfigSetting<>(0.35, "", "Vertical knockback. vertical = value");
   public final ConfigSetting<Double> verticalOnGround = new ConfigSetting<>(1.1, "", "On ground vertical kb modifier: vertical *= multiplier");
   public final ConfigSetting<Double> verticalInAir = new ConfigSetting<>(0.85, "", "In air vertical kb modifier: vertical *= multiplier");
   public final ConfigSetting<Double> verticalSprinting = new ConfigSetting<>(1.0, "", "Sprinting vertical kb modifier: vertical *= multiplier");
   public final ConfigSetting<Boolean> verticalInherit = new ConfigSetting<>(false, "", "Inherit victim vertical motion");
   public final ConfigSetting<Double> verticalFriction = new ConfigSetting<>(0.35, "", "Vertical friction: vertical += (motion * frictionHorizontal)");
   public final ConfigSetting<Boolean> enableOnePointSeven = new ConfigSetting<>(true, "", "Help players with victim chasing");
   public final ConfigSetting<Double> onePointSevenHorizontal = new ConfigSetting<>(0.37, "", "Horizontal knockback on back-hit: horizontal = value");
   public final ConfigSetting<Boolean> enableVerticalLimit = new ConfigSetting<>(false, "", "Limit vertical if it exceeds yLimit");
   public final ConfigSetting<Double> verticalLimit = new ConfigSetting<>(1.2, "", "Vertical limit: vertical = 0");
   public final ConfigSetting<Double> slowdown = new ConfigSetting<>(0.6, "", "Slowdown player on hit");
   public final ConfigSetting<Boolean> cancelSprint = new ConfigSetting<>(true, "", "Cancel sprint for player on hit");

   public AdvancedKnockback(Knockback knockback) {
      super(knockback, KnockbackType.ADVANCED);
   }

   @Override
   public double getHorizontal() {
      return this.horizontal.getValue();
   }

   @Override
   public void setHorizontal(double amount) {
      this.horizontal.set(amount, true);
   }

   @Override
   public double getHorizontalOnGround() {
      return this.horizontalOnGround.getValue();
   }

   @Override
   public void setHorizontalOnGround(double amount) {
      this.horizontalOnGround.set(amount, true);
   }

   @Override
   public double getHorizontalSprinting() {
      return this.horizontalSprinting.getValue();
   }

   @Override
   public void setHorizontalSprinting(double amount) {
      this.horizontalSprinting.set(amount, true);
   }

   @Override
   public boolean isHorizontalInherit() {
      return this.horizontalInherit.getValue();
   }

   @Override
   public void setHorizontalInherit(boolean value) {
      this.horizontalInherit.set(value, true);
   }

   @Override
   public double getHorizontalFriction() {
      return this.horizontalFriction.getValue();
   }

   @Override
   public void setHorizontalFriction(double amount) {
      this.horizontalFriction.set(amount, true);
   }

   @Override
   public double getHorizontalKnockbackDeduction() {
      return this.horizontalKnockbackDeduction.getValue();
   }

   @Override
   public void setHorizontalKnockbackDeduction(double amount) {
      this.horizontalKnockbackDeduction.set(amount, true);
   }

   @Override
   public double getVertical() {
      return this.vertical.getValue();
   }

   @Override
   public void setVertical(double amount) {
      this.vertical.set(amount, true);
   }

   @Override
   public double getVerticalOnGround() {
      return this.verticalOnGround.getValue();
   }

   @Override
   public void setVerticalOnGround(double amount) {
      this.verticalOnGround.set(amount, true);
   }

   @Override
   public double getVerticalInAir() {
      return this.verticalInAir.getValue();
   }

   @Override
   public void setVerticalInAir(double amount) {
      this.verticalInAir.set(amount, true);
   }

   @Override
   public double getVerticalSprinting() {
      return this.verticalSprinting.getValue();
   }

   @Override
   public void setVerticalSprinting(double amount) {
      this.verticalSprinting.set(amount, true);
   }

   @Override
   public boolean isVerticalInherit() {
      return this.verticalInherit.getValue();
   }

   @Override
   public void setVerticalInherit(boolean value) {
      this.verticalInherit.set(value, true);
   }

   @Override
   public double getVerticalFriction() {
      return this.verticalFriction.getValue();
   }

   @Override
   public void setVerticalFriction(double amount) {
      this.verticalFriction.set(amount, true);
   }

   @Override
   public boolean isOnePointSevenEnabled() {
      return this.enableOnePointSeven.getValue();
   }

   @Override
   public void setOnePointSevenEnabled(boolean value) {
      this.enableOnePointSeven.set(value, true);
   }

   @Override
   public double getOnePointSevenHorizontal() {
      return this.onePointSevenHorizontal.getValue();
   }

   @Override
   public void setOnePointSevenHorizontal(double amount) {
      this.onePointSevenHorizontal.set(amount, true);
   }

   @Override
   public boolean isEnabledVerticalLimit() {
      return this.enableVerticalLimit.getValue();
   }

   @Override
   public void setEnableVerticalLimit(boolean value) {
      this.enableVerticalLimit.set(value, true);
   }

   @Override
   public double getVerticalLimit() {
      return this.verticalLimit.getValue();
   }

   @Override
   public void setVerticalLimit(double amount) {
      this.verticalLimit.set(amount, true);
   }

   @Override
   public double getSlowdown() {
      return this.slowdown.getValue();
   }

   @Override
   public void setSlowdown(double amount) {
      this.slowdown.set(amount, true);
   }

   @Override
   public boolean isCancelSprint() {
      return this.cancelSprint.getValue();
   }

   @Override
   public void setCancelSprint(boolean value) {
      this.cancelSprint.set(value, true);
   }
}
