package net.minecraft.server.v1_8_R3;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom {
   public static int a(Collection<? extends WeightedRandom.WeightedRandomChoice> collection) {
      int i = 0;

      for (WeightedRandom.WeightedRandomChoice weightedrandom_weightedrandomchoice : collection) {
         i += weightedrandom_weightedrandomchoice.a;
      }

      return i;
   }

   public static <T extends WeightedRandom.WeightedRandomChoice> T a(Random random, Collection<T> collection, int i) {
      if (i <= 0) {
         throw new IllegalArgumentException();
      } else {
         int j = random.nextInt(i);
         return a(collection, j);
      }
   }

   public static <T extends WeightedRandom.WeightedRandomChoice> T a(Collection<T> collection, int i) {
      for (T weightedrandom_weightedrandomchoice : collection) {
         i -= weightedrandom_weightedrandomchoice.a;
         if (i < 0) {
            return weightedrandom_weightedrandomchoice;
         }
      }

      return null;
   }

   public static <T extends WeightedRandom.WeightedRandomChoice> T a(Random random, Collection<T> collection) {
      return a(random, collection, a(collection));
   }

   public static class WeightedRandomChoice {
      protected int a;

      public WeightedRandomChoice(int i) {
         this.a = i;
      }
   }
}
