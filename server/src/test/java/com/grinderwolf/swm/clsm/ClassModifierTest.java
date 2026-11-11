package com.grinderwolf.swm.clsm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

public class ClassModifierTest {
   @After
   public void clearLoader() {
      ClassModifier.setLoader(null);
   }

   @Test
   public void delegatesToTheSlimeWorldLoader() {
      Object[] worlds = new Object[]{"normal", "nether", "end"};
      ClassModifier.setLoader(new CLSMBridge() {
         @Override
         public Object[] getDefaultWorlds() {
            return worlds;
         }

         @Override
         public boolean isCustomWorld(Object world) {
            return world.equals("normal");
         }

         @Override
         public boolean skipWorldAdd(Object world) {
            return world.equals("nether");
         }
      });

      assertArrayEquals(worlds, ClassModifier.getDefaultWorlds());
      assertTrue(ClassModifier.isCustomWorld("normal"));
      assertFalse(ClassModifier.isCustomWorld("nether"));
      assertTrue(ClassModifier.skipWorldAdd("nether"));
      assertFalse(ClassModifier.skipWorldAdd("end"));
   }

   @Test
   public void behavesLikeNormalWhenSlimeWorldManagerIsAbsent() {
      assertNull(ClassModifier.getDefaultWorlds());
      assertFalse(ClassModifier.isCustomWorld("world"));
      assertFalse(ClassModifier.skipWorldAdd("world"));
      assertFalse(ClassModifier.saveChunk("world", "chunk"));
   }
}
