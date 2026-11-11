package lol.deyo.blaze.optimizations;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.World;

public class FastActivationRange {
   public static void activateEntities(World world) {
      int miscActivationRange = world.spigotConfig.miscActivationRange;
      int animalActivationRange = world.spigotConfig.animalActivationRange;
      int monsterActivationRange = world.spigotConfig.monsterActivationRange;
      int maxRange = Math.max(monsterActivationRange, animalActivationRange);
      maxRange = Math.max(maxRange, miscActivationRange);
      maxRange = Math.min((world.spigotConfig.viewDistance << 4) - 8, maxRange);

      for (Entity entity : world.entityList) {
         if (MinecraftServer.currentTick >= entity.activatedTick) {
            if (!entity.defaultActivationState && !(entity instanceof EntityPlayer)) {
               double x = entity.locX;
               double y = entity.locY;
               double z = entity.locZ;
               EntityPlayer player = world.playerMap.getNearbyPlayer(x, y, z, maxRange + 0.4, true, false);
               if (player != null) {
                  int range = entity.activationType == 1 ? monsterActivationRange : (entity.activationType == 2 ? animalActivationRange : miscActivationRange);
                  AxisAlignedBB playerBox = player.getBoundingBox();
                  double minX = playerBox.a - range;
                  double minY = playerBox.b - 256.0;
                  double minZ = playerBox.c - range;
                  double maxX = playerBox.d + range;
                  double maxY = playerBox.e + 256.0;
                  double maxZ = playerBox.f + range;
                  double entityMinX = entity.getBoundingBox().a;
                  double entityMinY = entity.getBoundingBox().b;
                  double entityMinZ = entity.getBoundingBox().c;
                  double entityMaxX = entity.getBoundingBox().d;
                  double entityMaxY = entity.getBoundingBox().e;
                  double entityMaxZ = entity.getBoundingBox().f;
                  if (entityMaxX > minX && entityMinX < maxX && entityMaxY > minY && entityMinY < maxY && entityMaxZ > minZ && entityMinZ < maxZ) {
                     entity.activatedTick = MinecraftServer.currentTick;
                  }
               }
            } else {
               entity.activatedTick = MinecraftServer.currentTick;
            }
         }
      }
   }
}
