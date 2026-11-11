package net.minecraft.server.v1_8_R3;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHashSet;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.github.paperspigot.PaperWorldEntityList;

public final class SpawnerCreature {
   private final LongHashSet b = new LongHashSet();

   private static BlockPosition getRandomPosition(World world, int i, int j) {
      Chunk chunk = world.getChunkIfLoaded(i, j);
      if (chunk != null) {
         int k = i * 16 + world.random.nextInt(16);
         int l = j * 16 + world.random.nextInt(16);
         int i1 = MathHelper.c(chunk.f(k, l) + 1, 16);
         int j1 = world.random.nextInt(i1 > 0 ? i1 : chunk.g() + 16 - 1);
         return new BlockPosition(k, j1, l);
      } else {
         return null;
      }
   }

   public static boolean a(EntityInsentient.EnumEntityPositionType entityinsentient_enumentitypositiontype, World world, BlockPosition blockposition) {
      int x = blockposition.getX();
      int y = blockposition.getY();
      int z = blockposition.getZ();
      if (!world.getWorldBorder().a(x, z)) {
         return false;
      } else {
         Block block = world.getType(x, y, z).getBlock();
         if (entityinsentient_enumentitypositiontype == EntityInsentient.EnumEntityPositionType.IN_WATER) {
            return block.getMaterial().isLiquid()
               && world.getType(x, y - 1, z).getBlock().getMaterial().isLiquid()
               && !world.getType(x, y + 1, z).getBlock().isOccluding();
         } else if (!World.a(world, x, y - 1, z)) {
            return false;
         } else {
            Block block1 = world.getType(x, y - 1, z).getBlock();
            return block1 != Blocks.BEDROCK
               && block1 != Blocks.BARRIER
               && !block.isOccluding()
               && !block.getMaterial().isLiquid()
               && !world.getType(x, y + 1, z).getBlock().isOccluding();
         }
      }
   }

   public static void a(World world, BiomeBase biomebase, int i, int j, int k, int l, Random random) {
      List<BiomeBase.BiomeMeta> list = biomebase.getMobs(EnumCreatureType.CREATURE);
      if (!list.isEmpty()) {
         while (random.nextFloat() < biomebase.g()) {
            BiomeBase.BiomeMeta biomebase_biomemeta = WeightedRandom.a(world.random, list);
            int i1 = biomebase_biomemeta.c + random.nextInt(1 + biomebase_biomemeta.d - biomebase_biomemeta.c);
            GroupDataEntity groupdataentity = null;
            int j1 = i + random.nextInt(k);
            int k1 = j + random.nextInt(l);
            int l1 = j1;
            int i2 = k1;

            for (int j2 = 0; j2 < i1; j2++) {
               boolean flag = false;

               for (int k2 = 0; !flag && k2 < 4; k2++) {
                  BlockPosition blockposition = world.r(j1, k1);
                  if (a(EntityInsentient.EnumEntityPositionType.ON_GROUND, world, blockposition)) {
                     EntityInsentient entityinsentient;
                     try {
                        entityinsentient = biomebase_biomemeta.constructor.newInstance(world);
                     } catch (Exception var21) {
                        var21.printStackTrace();
                        continue;
                     }

                     entityinsentient.setPositionRotation(j1 + 0.5F, blockposition.getY(), k1 + 0.5F, random.nextFloat() * 360.0F, 0.0F);
                     groupdataentity = entityinsentient.prepare(
                        world.E(MathHelper.floor(entityinsentient.locX), MathHelper.floor(entityinsentient.locY), MathHelper.floor(entityinsentient.locZ)),
                        groupdataentity
                     );
                     world.addEntity(entityinsentient, SpawnReason.CHUNK_GEN);
                     flag = true;
                  }

                  j1 += random.nextInt(5) - random.nextInt(5);

                  for (k1 += random.nextInt(5) - random.nextInt(5);
                     j1 < i || j1 >= i + k || k1 < j || k1 >= j + k;
                     k1 = i2 + random.nextInt(5) - random.nextInt(5)
                  ) {
                     j1 = l1 + random.nextInt(5) - random.nextInt(5);
                  }
               }
            }
         }
      }
   }

   public int getEntityCount(WorldServer worldServer, EnumCreatureType enumCreatureType) {
      return ((PaperWorldEntityList)worldServer.entityList).getCreatureCount(enumCreatureType);
   }

   public int a(WorldServer worldserver, boolean flag, boolean flag1, boolean flag2) {
      if (!flag && !flag1) {
         return 0;
      } else {
         this.b.clear();
         int i = 0;

         for (EntityHuman entityhuman : worldserver.players) {
            if (!entityhuman.isSpectator() || !entityhuman.affectsSpawning) {
               int l = MathHelper.floor(entityhuman.locX / 16.0);
               int j = MathHelper.floor(entityhuman.locZ / 16.0);
               byte b0 = worldserver.spigotConfig.mobSpawnRange;
               b0 = b0 > worldserver.spigotConfig.viewDistance ? (byte)worldserver.spigotConfig.viewDistance : b0;
               b0 = b0 > 8 ? 8 : b0;

               for (int i1 = -b0; i1 <= b0; i1++) {
                  for (int k = -b0; k <= b0; k++) {
                     boolean flag3 = i1 == -b0 || i1 == b0 || k == -b0 || k == b0;
                     long chunkCoords = LongHash.toLong(i1 + l, k + j);
                     if (!this.b.contains(chunkCoords)) {
                        i++;
                        if (!flag3 && worldserver.getWorldBorder().isInBounds(i1 + l, k + j)) {
                           this.b.add(chunkCoords);
                        }
                     }
                  }
               }
            }
         }

         int j1 = 0;
         BlockPosition blockposition = worldserver.getSpawn();

         EnumCreatureType[] var44;
         for (EnumCreatureType enumcreaturetype : var44 = EnumCreatureType.values()) {
            int limit = enumcreaturetype.b();
            switch (enumcreaturetype) {
               case MONSTER:
                  limit = worldserver.getWorld().getMonsterSpawnLimit();
                  break;
               case CREATURE:
                  limit = worldserver.getWorld().getAnimalSpawnLimit();
                  break;
               case AMBIENT:
                  limit = worldserver.getWorld().getAmbientSpawnLimit();
                  break;
               case WATER_CREATURE:
                  limit = worldserver.getWorld().getWaterAnimalSpawnLimit();
            }

            int mobcnt;
            if (limit != 0
               && (!enumcreaturetype.d() || flag1)
               && (enumcreaturetype.d() || flag)
               && (!enumcreaturetype.e() || flag2)
               && (mobcnt = this.getEntityCount(worldserver, enumcreaturetype)) <= limit * i / 289) {
               Iterator<Long> iterator1 = this.b.iterator();
               int moblimit = limit * i / 256 - mobcnt + 1;

               label155:
               while (iterator1.hasNext() && moblimit > 0) {
                  long key = iterator1.next();
                  BlockPosition blockposition1 = getRandomPosition(worldserver, LongHash.msw(key), LongHash.lsw(key));
                  if (blockposition1 != null) {
                     int i2 = blockposition1.getX();
                     int j2 = blockposition1.getY();
                     int k2 = blockposition1.getZ();
                     Block block = worldserver.getType(blockposition1).getBlock();
                     if (!block.isOccluding()) {
                        int l2 = 0;

                        for (int i3 = 0; i3 < 3; i3++) {
                           int j3 = i2;
                           int l3 = k2;
                           byte b1 = 6;
                           BiomeBase.BiomeMeta biomebase_biomemeta = null;
                           GroupDataEntity groupdataentity = null;

                           for (int i4 = 0; i4 < 4; i4++) {
                              j3 += worldserver.random.nextInt(b1) - worldserver.random.nextInt(b1);
                              l3 += worldserver.random.nextInt(b1) - worldserver.random.nextInt(b1);
                              BlockPosition blockposition2 = new BlockPosition(j3, j2, l3);
                              float f = j3 + 0.5F;
                              float f1 = l3 + 0.5F;
                              if (!worldserver.isPlayerNearbyWhoAffectsSpawning(f, j2, f1, 24.0) && blockposition.c(f, j2, f1) >= 576.0) {
                                 if (biomebase_biomemeta == null) {
                                    biomebase_biomemeta = worldserver.a(enumcreaturetype, blockposition2);
                                    if (biomebase_biomemeta == null) {
                                       break;
                                    }
                                 }

                                 if (worldserver.a(enumcreaturetype, biomebase_biomemeta, blockposition2)
                                    && a(EntityPositionTypes.a(biomebase_biomemeta.b), worldserver, blockposition2)) {
                                    EntityInsentient entityinsentient;
                                    try {
                                       entityinsentient = biomebase_biomemeta.constructor.newInstance(worldserver);
                                    } catch (Exception var36) {
                                       var36.printStackTrace();
                                       return j1;
                                    }

                                    entityinsentient.setPositionRotation(f, j2, f1, worldserver.random.nextFloat() * 360.0F, 0.0F);
                                    if (entityinsentient.bR() && entityinsentient.canSpawn()) {
                                       groupdataentity = entityinsentient.prepare(
                                          worldserver.E(
                                             MathHelper.floor(entityinsentient.locX),
                                             MathHelper.floor(entityinsentient.locY),
                                             MathHelper.floor(entityinsentient.locZ)
                                          ),
                                          groupdataentity
                                       );
                                       if (entityinsentient.canSpawn()) {
                                          l2++;
                                          worldserver.addEntity(entityinsentient, SpawnReason.NATURAL);
                                       }

                                       if (--moblimit <= 0 || l2 >= entityinsentient.bV()) {
                                          continue label155;
                                       }
                                    }

                                    j1 += l2;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return j1;
      }
   }
}
