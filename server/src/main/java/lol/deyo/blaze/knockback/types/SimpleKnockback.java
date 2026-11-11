package lol.deyo.blaze.knockback.types;

import lol.deyo.blaze.api.knockback.types.ISimpleKnockback;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.knockback.Knockback;
import lol.deyo.blaze.knockback.KnockbackType;
import lol.deyo.blaze.knockback.extra.KnockbackParent;

public class SimpleKnockback extends KnockbackParent implements ISimpleKnockback {
   public final ConfigSetting<Double> friction = new ConfigSetting<>(2.5, "", "Friction knockback: friction = value");
   public final ConfigSetting<Double> horizontal = new ConfigSetting<>(0.425, "", "Horizontal knockback: horizontal = value");
   public final ConfigSetting<Double> vertical = new ConfigSetting<>(0.5, "", "Vertical knockback: vertical = value");
   public final ConfigSetting<Double> verticalLimit = new ConfigSetting<>(0.38, "", "Limit of vertical knockback");
   public final ConfigSetting<Double> extraHorizontal = new ConfigSetting<>(0.25, "", "Extra horizontal knockback");
   public final ConfigSetting<Double> extraVertical = new ConfigSetting<>(0.01, "", "Extra vertical knockback");
   public final ConfigSetting<Double> slowdown = new ConfigSetting<>(0.6, "", "Slowdown player on hit");
   public final ConfigSetting<Boolean> enableOnePointSeven = new ConfigSetting<>(true, "", "Help players with victim chasing");
   public final ConfigSetting<Double> onePointSevenMultiplier = new ConfigSetting<>(0.4, "", "Horizontal knockback multiplier for 1.7");

   public SimpleKnockback(Knockback knockback) {
      super(knockback, KnockbackType.SIMPLE);
   }

   @Override
   public void setFriction(double amount) {
      this.friction.set(amount, true);
   }

   @Override
   public double getFriction() {
      return this.friction.getValue();
   }

   @Override
   public void setHorizontal(double amount) {
      this.horizontal.set(amount, true);
   }

   @Override
   public double getHorizontal() {
      return this.horizontal.getValue();
   }

   @Override
   public void setVertical(double amount) {
      this.vertical.set(amount, true);
   }

   @Override
   public double getVertical() {
      return this.vertical.getValue();
   }

   @Override
   public void setVerticalLimit(double amount) {
      this.verticalLimit.set(amount, true);
   }

   @Override
   public double getVerticalLimit() {
      return this.verticalLimit.getValue();
   }

   @Override
   public void setExtraHorizontal(double amount) {
      this.extraHorizontal.set(amount, true);
   }

   @Override
   public double getExtraHorizontal() {
      return this.extraHorizontal.getValue();
   }

   @Override
   public void setExtraVertical(double amount) {
      this.extraVertical.set(amount, true);
   }

   @Override
   public double getExtraVertical() {
      return this.extraVertical.getValue();
   }

   @Override
   public void setSlowdown(double amount) {
      this.slowdown.set(amount, true);
   }

   @Override
   public double getSlowdown() {
      return this.slowdown.getValue();
   }

   @Override
   public void setOnePointSevenEnabled(boolean value) {
      this.enableOnePointSeven.set(value, true);
   }

   @Override
   public boolean isOnePointSevenEnabled() {
      return this.enableOnePointSeven.getValue();
   }

   @Override
   public void setOnePointSevenMultiplier(double amount) {
      this.onePointSevenMultiplier.set(amount, true);
   }

   @Override
   public double getOnePointSevenMultiplier() {
      return this.onePointSevenMultiplier.getValue();
   }
}
