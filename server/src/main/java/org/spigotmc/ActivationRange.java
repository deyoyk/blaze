package org.spigotmc;

import lol.deyo.blaze.optimizations.FastActivationRange;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityAmbient;
import net.minecraft.server.v1_8_R3.EntityAnimal;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityComplexPart;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityEnderCrystal;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.EntityFireball;
import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMonster;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityProjectile;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.EntityWeather;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.World;

public class ActivationRange {
   public static byte initializeEntityActivationType(Entity entity) {
      if (entity instanceof EntityMonster || entity instanceof EntitySlime) {
         return 1;
      } else {
         return (byte)(!(entity instanceof EntityCreature) && !(entity instanceof EntityAmbient) ? 3 : 2);
      }
   }

   public static boolean initializeEntityActivationState(Entity entity, org.spigotmc.SpigotWorldConfig config) {
      return entity.activationType == 3 && config.miscActivationRange == 0
         || entity.activationType == 2 && config.animalActivationRange == 0
         || entity.activationType == 1 && config.monsterActivationRange == 0
         || entity instanceof EntityPlayer
         || entity instanceof EntityTNTPrimed
         || entity instanceof EntityArrow
         || entity instanceof EntityFishingHook
         || entity instanceof EntityFallingBlock
         || entity instanceof EntityProjectile
         || entity instanceof EntityEnderDragon
         || entity instanceof EntityComplexPart
         || entity instanceof EntityWither
         || entity instanceof EntityFireball
         || entity instanceof EntityWeather
         || entity instanceof EntityEnderCrystal
         || entity instanceof EntityFireworks;
   }

   public static void activateEntities(World world) {
      FastActivationRange.activateEntities(world);
   }

   public static boolean checkEntityImmunities(Entity entity) {
      if (!entity.inWater && entity.fireTicks <= 0) {
         if (!(entity instanceof EntityArrow)) {
            if (!entity.onGround || entity.passenger != null || entity.vehicle != null) {
               return true;
            }
         } else if (!((EntityArrow)entity).inGround) {
            return true;
         }

         if (!(entity instanceof EntityLiving)) {
            return false;
         } else {
            EntityLiving living = (EntityLiving)entity;
            if (living.hurtTicks > 0 || !living.effects.isEmpty()) {
               return true;
            } else if (entity instanceof EntityCreature && ((EntityCreature)entity).getGoalTarget() != null) {
               return true;
            } else if (entity instanceof EntityVillager && ((EntityVillager)entity).cm()) {
               return true;
            } else {
               if (entity instanceof EntityAnimal) {
                  EntityAnimal animal = (EntityAnimal)entity;
                  if (animal.isBaby() || animal.isInLove()) {
                     return true;
                  }

                  if (entity instanceof EntitySheep && ((EntitySheep)entity).isSheared()) {
                     return true;
                  }
               }

               return entity instanceof EntityCreeper && ((EntityCreeper)entity).cn();
            }
         }
      } else {
         return true;
      }
   }

   public static boolean checkIfActive(Entity entity) {
      if (entity.isAddedToChunk() && !(entity instanceof EntityFireworks) && !entity.loadChunks) {
         boolean isActive = entity.activatedTick >= MinecraftServer.currentTick || entity.defaultActivationState;
         if (!isActive) {
            if ((MinecraftServer.currentTick - entity.activatedTick - 1L) % 20L == 0L) {
               if (checkEntityImmunities(entity)) {
                  entity.activatedTick = MinecraftServer.currentTick + 20;
               }

               isActive = true;
            }
         } else if (!entity.defaultActivationState && entity.ticksLived % 4 == 0 && !checkEntityImmunities(entity)) {
            isActive = false;
         }

         Chunk chunk = entity.world.getChunkIfLoaded(MathHelper.floor(entity.locX) >> 4, MathHelper.floor(entity.locZ) >> 4);
         if (isActive && (chunk == null || !chunk.areNeighborsLoaded(1))) {
            isActive = false;
         }

         return isActive;
      } else {
         return true;
      }
   }
}
