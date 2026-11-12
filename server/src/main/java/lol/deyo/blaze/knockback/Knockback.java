package lol.deyo.blaze.knockback;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.api.knockback.IKnockback;
import lol.deyo.blaze.api.knockback.types.IAdvancedKnockback;
import lol.deyo.blaze.api.knockback.types.ISimpleKnockback;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.knockback.extra.KnockbackParent;
import lol.deyo.blaze.knockback.types.AdvancedKnockback;
import lol.deyo.blaze.knockback.types.SimpleKnockback;
import lol.deyo.blaze.utils.CC;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Knockback extends Config implements IKnockback {
   public final String name;
   public List<KnockbackParent> knockbackTypes;
   public SimpleKnockback simpleKnockback;
   public AdvancedKnockback advancedKnockback;
   public final ConfigSetting<String> knockbackType = new ConfigSetting<>("SIMPLE", "type", "");
   public final ConfigSetting<Double> rodHorizontal = new ConfigSetting<>(1.1, "", "Horizontal fishing rod knockback");
   public final ConfigSetting<Double> rodVertical = new ConfigSetting<>(1.0, "", "Vertical fishing rod knockback");
   public final ConfigSetting<Double> rodSpeed = new ConfigSetting<>(1.0, "", "Multiplies the number given with the rod's vector magnitude");
   public final ConfigSetting<Boolean> bowBoostDirectional = new ConfigSetting<>(
      false, "", "Should bow boosting be enabled using the direction player is facing"
   );
   public final ConfigSetting<Boolean> bowBoostKbOnlyShooter = new ConfigSetting<>(false, "", "Should bow boosting direction only be enabled for shooter");
   public final ConfigSetting<Double> bowHorizontal = new ConfigSetting<>(0.9, "", "Horizontal bow knockback");
   public final ConfigSetting<Double> bowVertical = new ConfigSetting<>(0.9, "", "Vertical bow knockback");
   public final ConfigSetting<Double> bowBoostHorizontal = new ConfigSetting<>(1.1, "", "Horizontal bow boost knockback");
   public final ConfigSetting<Double> bowBoostVertical = new ConfigSetting<>(0.35, "", "Horizontal bow boost knockback");
   public final ConfigSetting<Double> bowPunchMultiplier = new ConfigSetting<>(
      1.1, "", "Punch enchantment modifier: horizontal *= (punchLevel + 1) * bowPunchMultiplier"
   );
   public final ConfigSetting<Float> potionFall = new ConfigSetting<>(0.05F, "", "Fall speed value");
   public final ConfigSetting<Float> potionSpeed = new ConfigSetting<>(0.5F, "", "Throwing multiplier value");
   public final ConfigSetting<Float> potionVerticalOffset = new ConfigSetting<>(-10.0F, "", "Vertical throwing offset value");
   public final ConfigSetting<Boolean> potionFast = new ConfigSetting<>(true, "", "Make heal potions throwing smoother");
   public final ConfigSetting<Integer> hitDelay = new ConfigSetting<>(2, "", "Delay between victim can get hit (2 ticks for responsive 1.8 PvP)");
   public final ConfigSetting<Integer> hitDelayArrow = new ConfigSetting<>(20, "", "Delay between victim can get hit by an arrow (Patches arrow bounce bug)");
   public final ConfigSetting<Boolean> comboMode = new ConfigSetting<>(false, "", "Enable combo mode");
   public final ConfigSetting<Double> comboHeight = new ConfigSetting<>(2.3, "", "Maximum height of combo");
   public final ConfigSetting<Integer> comboTicks = new ConfigSetting<>(10, "", "Maximum combo ticks");
   public final ConfigSetting<Double> comboVelocity = new ConfigSetting<>(-0.05, "", "Vertical deduction on hit");

   public Knockback(Blaze instance, String name, File parent) {
      super(instance, parent.getName() + File.separator + name, CC.getConfigHeader(name + " (Knockback)"));
      this.name = name.replace(".yml", "");
      this.knockbackTypes = new ArrayList<>();
      this.simpleKnockback = new SimpleKnockback(this);
      this.advancedKnockback = new AdvancedKnockback(this);
   }

   public List<ConfigSetting<?>> getKnockbackSettings() {
      List<ConfigSetting<?>> knockbackSettings = new ArrayList<>();

      try {
         for (KnockbackParent parent : this.knockbackTypes) {
            Field[] var7;
            for (Field field : var7 = parent.getClass().getDeclaredFields()) {
               if (field.getType() == ConfigSetting.class) {
                  ConfigSetting<?> configSetting = (ConfigSetting<?>)field.get(parent);
                  configSetting.setConfig(this);
                  configSetting.setName(field.getName());
                  configSetting.setPath(parent.getType().getPath());
                  knockbackSettings.add(configSetting);
               }
            }
         }

         return knockbackSettings;
      } catch (IllegalAccessException var9) {
         throw new RuntimeException(var9);
      }
   }

   public KnockbackType getKnockbackType() {
      return KnockbackType.valueOf(this.knockbackType.getValue());
   }

   public ConfigSetting<?> getActiveSetting(String name) {
      for (ConfigSetting<?> activeSetting : this.getActiveSettings()) {
         if (activeSetting.getName().equalsIgnoreCase(name)) {
            return activeSetting;
         }
      }

      return null;
   }

   public List<ConfigSetting<?>> getActiveSettings() {
      return this.getSettings().stream().filter(setting -> this.getKnockbackType().isApplicable((ConfigSetting<?>)setting)).collect(Collectors.toList());
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getType() {
      return this.getKnockbackType().getDisplayName();
   }

   @Override
   public IAdvancedKnockback getAdvancedKnockback() {
      return this.advancedKnockback;
   }

   @Override
   public ISimpleKnockback getSimpleKnockback() {
      return this.simpleKnockback;
   }

   @Override
   public boolean isActive() {
      return Blaze.getInstance().getKnockbackManager().getActiveProfile().getName().equals(this.name);
   }

   @Override
   public double getRodHorizontal() {
      return this.rodHorizontal.getValue();
   }

   @Override
   public void setRodHorizontal(double amount) {
      this.rodHorizontal.set(amount, true);
   }

   @Override
   public double getRodVertical() {
      return this.rodVertical.getValue();
   }

   @Override
   public void setRodVertical(double amount) {
      this.rodVertical.set(amount, true);
   }

   @Override
   public double getRodSpeed() {
      return this.rodSpeed.getValue();
   }

   @Override
   public void setRodSpeed(double amount) {
      this.rodSpeed.set(amount, true);
   }

   @Override
   public boolean getBowBoostDirectional() {
      return this.bowBoostDirectional.getValue();
   }

   @Override
   public void setBowBoostDirectional(boolean value) {
      this.bowBoostDirectional.set(value, true);
   }

   @Override
   public boolean isBowBoostOnlyShooter() {
      return this.bowBoostKbOnlyShooter.getValue();
   }

   @Override
   public void setBowBoostOnlyShooter(boolean value) {
      this.bowBoostKbOnlyShooter.set(value, true);
   }

   @Override
   public double getBowHorizontal() {
      return this.bowHorizontal.getValue();
   }

   @Override
   public void setBowHorizontal(double amount) {
      this.bowHorizontal.set(amount, true);
   }

   @Override
   public double getBowVertical() {
      return this.bowVertical.getValue();
   }

   @Override
   public void setBowVertical(double amount) {
      this.bowVertical.set(amount, true);
   }

   @Override
   public double getBowPunchMultiplier() {
      return this.bowPunchMultiplier.getValue();
   }

   @Override
   public void setBowPunchMultiplier(double amount) {
      this.bowPunchMultiplier.set(amount, true);
   }

   @Override
   public float getPotionFall() {
      return this.potionFall.getValue();
   }

   @Override
   public void setPotionFall(float amount) {
      this.potionFall.set(amount, true);
   }

   @Override
   public float getPotionSpeed() {
      return this.potionSpeed.getValue();
   }

   @Override
   public void setPotionSpeed(float amount) {
      this.potionSpeed.set(amount, true);
   }

   @Override
   public float getPotionOffset() {
      return this.potionVerticalOffset.getValue();
   }

   @Override
   public void setPotionOffset(float amount) {
      this.potionVerticalOffset.set(amount, true);
   }

   @Override
   public boolean isPotionFast() {
      return this.potionFast.getValue();
   }

   @Override
   public void setPotionFast(boolean value) {
      this.potionFast.set(value, true);
   }

   @Override
   public int getHitDelay() {
      return this.hitDelay.getValue();
   }

   @Override
   public void setHitDelay(int amount) {
      this.hitDelay.set(amount, true);
   }

   @Override
   public int getHitDelayArrow() {
      return this.hitDelayArrow.getValue();
   }

   @Override
   public void setHitDelayArrow(int amount) {
      this.hitDelayArrow.set(amount, true);
   }

   @Override
   public boolean isComboMode() {
      return this.comboMode.getValue();
   }

   @Override
   public void setComboMode(boolean value) {
      this.comboMode.set(value, true);
   }

   @Override
   public double getComboHeight() {
      return this.comboHeight.getValue();
   }

   @Override
   public void setComboHeight(double amount) {
      this.comboHeight.set(amount, true);
   }

   @Override
   public int getComboTicks() {
      return this.comboTicks.getValue();
   }

   @Override
   public void setComboTicks(int amount) {
      this.comboTicks.set(amount, true);
   }

   @Override
   public double getComboVelocity() {
      return this.comboVelocity.getValue();
   }

   @Override
   public void setComboVelocity(double amount) {
      this.comboVelocity.set(amount, true);
   }

   @Override
   public Map<String, Object> getValues() {
      Map<String, Object> map = new LinkedHashMap<>();

      for (ConfigSetting<?> setting : this.getActiveSettings()) {
         map.put(setting.getName(), setting.getValue());
      }

      return map;
   }
}
