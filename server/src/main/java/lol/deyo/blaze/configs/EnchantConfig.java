package lol.deyo.blaze.configs;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigSetting;
import lol.deyo.blaze.utils.CC;

public class EnchantConfig extends Config {
   public static ConfigSetting<Integer> unbreaking = new ConfigSetting<>(3, "enchant-limits");
   public static ConfigSetting<Integer> protectionAll = new ConfigSetting<>(4, "enchant-limits.armor");
   public static ConfigSetting<Integer> protectionFire = new ConfigSetting<>(4, "enchant-limits.armor");
   public static ConfigSetting<Integer> protectionFall = new ConfigSetting<>(4, "enchant-limits.armor");
   public static ConfigSetting<Integer> protectionExplosion = new ConfigSetting<>(4, "enchant-limits.armor");
   public static ConfigSetting<Integer> protectionProjectile = new ConfigSetting<>(4, "enchant-limits.armor");
   public static ConfigSetting<Integer> thorns = new ConfigSetting<>(3, "enchant-limits.armor");
   public static ConfigSetting<Integer> aquaAffinity = new ConfigSetting<>(1, "enchant-limits.armor");
   public static ConfigSetting<Integer> waterBreathing = new ConfigSetting<>(3, "enchant-limits.armor");
   public static ConfigSetting<Integer> depthStrider = new ConfigSetting<>(3, "enchant-limits.armor");
   public static ConfigSetting<Integer> efficiency = new ConfigSetting<>(5, "enchant-limits.tools");
   public static ConfigSetting<Integer> silkTouch = new ConfigSetting<>(1, "enchant-limits.tools");
   public static ConfigSetting<Integer> fortune = new ConfigSetting<>(3, "enchant-limits.tools");
   public static ConfigSetting<Integer> sharpness = new ConfigSetting<>(5, "enchant-limits.swords");
   public static ConfigSetting<Integer> baneOfArthropods = new ConfigSetting<>(5, "enchant-limits.swords");
   public static ConfigSetting<Integer> smite = new ConfigSetting<>(5, "enchant-limits.swords");
   public static ConfigSetting<Integer> fireAspect = new ConfigSetting<>(2, "enchant-limits.swords");
   public static ConfigSetting<Integer> knockback = new ConfigSetting<>(2, "enchant-limits.swords");
   public static ConfigSetting<Integer> looting = new ConfigSetting<>(3, "enchant-limits.swords");
   public static ConfigSetting<Integer> power = new ConfigSetting<>(5, "enchant-limits.bows");
   public static ConfigSetting<Integer> punch = new ConfigSetting<>(2, "enchant-limits.bows");
   public static ConfigSetting<Integer> flame = new ConfigSetting<>(1, "enchant-limits.bows");
   public static ConfigSetting<Integer> infinity = new ConfigSetting<>(1, "enchant-limits.bows");
   public static ConfigSetting<Integer> lure = new ConfigSetting<>(3, "enchant-limits.rods");
   public static ConfigSetting<Integer> luckOfTheSea = new ConfigSetting<>(3, "enchant-limits.rods");

   public EnchantConfig(Blaze blaze) {
      super(blaze, "enchants", CC.getConfigHeader("Enchantment limit configuration", "Increase or decrease the limit of any enchant"));
   }
}
