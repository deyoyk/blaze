package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import org.junit.Test;

public class EntityLivingEffectsTest {
   @Test
   public void effectStorageIsAllocatedOnlyWhenNeeded() {
      TestLiving living = new TestLiving();
      assertSame(Collections.emptyMap(), living.effects);
      assertTrue(living.getEffects().isEmpty());

      NBTTagCompound effect = new NBTTagCompound();
      effect.setByte("Id", (byte)MobEffectList.FASTER_MOVEMENT.id);
      effect.setByte("Amplifier", (byte)1);
      effect.setInt("Duration", 200);
      effect.setBoolean("Ambient", false);
      effect.setBoolean("ShowParticles", true);
      NBTTagList effects = new NBTTagList();
      effects.add(effect);
      NBTTagCompound root = new NBTTagCompound();
      root.set("ActiveEffects", effects);

      living.a(root);
      assertFalse(living.effects.isEmpty());
      MobEffect loaded = living.getEffect(MobEffectList.FASTER_MOVEMENT);
      assertNotNull(loaded);
      assertEquals(200, loaded.getDuration());
      assertEquals(1, loaded.getAmplifier());
   }

   private static final class TestLiving extends EntityLiving {
      private TestLiving() {
         super(null);
      }

      @Override
      public ItemStack bA() {
         return null;
      }

      @Override
      public ItemStack getEquipment(int slot) {
         return null;
      }

      @Override
      public void setEquipment(int slot, ItemStack itemStack) {
      }

      @Override
      public ItemStack[] getEquipment() {
         return new ItemStack[5];
      }
   }
}
