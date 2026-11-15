package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.configs.extra.ConfigSetting;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class AnticheatToggleTest {
   @Test
   public void setFlipsInMemoryValueWithoutRestart() {
      ConfigSetting<Boolean> setting = new ConfigSetting<>(true, "general");
      setting.set(false, false);
      assertFalse(setting.getValue());
      setting.set(true, false);
      assertTrue(setting.getValue());
   }

   @Test
   public void flagGateReadsLiveConfigValue() {
      ConfigSetting<Boolean> enabled = new ConfigSetting<>(true, "general");
      assertTrue(enabled.getValue());
      enabled.set(false, false);
      assertFalse((Boolean)enabled.getValue());
   }
}
