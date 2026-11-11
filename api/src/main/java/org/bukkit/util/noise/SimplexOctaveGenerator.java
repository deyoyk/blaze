package org.bukkit.util.noise;

import java.util.Random;

public class SimplexOctaveGenerator extends OctaveGenerator {
   private double wScale = 1.0;

   public SimplexOctaveGenerator(org.bukkit.World world, int octaves) {
      this(new Random(world.getSeed()), octaves);
   }

   public SimplexOctaveGenerator(long seed, int octaves) {
      this(new Random(seed), octaves);
   }

   public SimplexOctaveGenerator(Random rand, int octaves) {
      super(createOctaves(rand, octaves));
   }

   @Override
   public void setScale(double scale) {
      super.setScale(scale);
      this.setWScale(scale);
   }

   public double getWScale() {
      return this.wScale;
   }

   public void setWScale(double scale) {
      this.wScale = scale;
   }

   public double noise(double x, double y, double z, double w, double frequency, double amplitude) {
      return this.noise(x, y, z, w, frequency, amplitude, false);
   }

   public double noise(double x, double y, double z, double w, double frequency, double amplitude, boolean normalized) {
      double result = 0.0;
      double amp = 1.0;
      double freq = 1.0;
      double max = 0.0;
      x *= this.xScale;
      y *= this.yScale;
      z *= this.zScale;
      w *= this.wScale;

      for (NoiseGenerator octave : this.octaves) {
         result += ((SimplexNoiseGenerator)octave).noise(x * freq, y * freq, z * freq, w * freq) * amp;
         max += amp;
         freq *= frequency;
         amp *= amplitude;
      }

      if (normalized) {
         result /= max;
      }

      return result;
   }

   private static NoiseGenerator[] createOctaves(Random rand, int octaves) {
      NoiseGenerator[] result = new NoiseGenerator[octaves];

      for (int i = 0; i < octaves; i++) {
         result[i] = new SimplexNoiseGenerator(rand);
      }

      return result;
   }
}
