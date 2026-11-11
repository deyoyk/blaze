package org.spigotmc;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.EntityGhast;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.EntityPainting;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class TrackingRange {
   public static int getEntityTrackingRange(Entity entity, int defaultRange) {
      org.spigotmc.SpigotWorldConfig config = entity.world.spigotConfig;
      if (entity instanceof EntityPlayer) {
         return config.playerTrackingRange;
      } else if (entity.activationType == 1) {
         return config.monsterTrackingRange;
      } else if (entity instanceof EntityGhast) {
         return Math.max(config.monsterTrackingRange, config.monsterActivationRange);
      } else if (entity.activationType == 2) {
         return config.animalTrackingRange;
      } else {
         return !(entity instanceof EntityItemFrame)
               && !(entity instanceof EntityPainting)
               && !(entity instanceof EntityItem)
               && !(entity instanceof EntityExperienceOrb)
            ? config.otherTrackingRange
            : config.miscTrackingRange;
      }
   }
}
