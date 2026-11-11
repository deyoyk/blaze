package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.utils.Utils;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.NumberConversions;

public class Explosion {
   public static final List<double[]> densityVectors = Lists.newArrayList();
   public static final List<double[]> blockVectors = Lists.newArrayListWithCapacity(1352);
   public final Entity source;
   private final boolean a;
   private final boolean b;
   private final World world;
   private final double posX;
   private final double posY;
   private final double posZ;
   private final float size;
   private final List<BlockPosition> blocks = Lists.newArrayList();
   private final Map<EntityHuman, Vec3D> k = Maps.newHashMap();
   public boolean wasCanceled;

   static {
      for (float f = 0.0F; f <= 1.0F; f += 0.33783785F) {
         for (float f1 = 0.0F; f1 <= 1.0F; f1 += 0.33783785F) {
            for (float f2 = 0.0F; f2 <= 1.0F; f2 += 0.33783785F) {
               densityVectors.add(new double[]{f, f1, f2});
            }
         }
      }

      for (int k = 0; k < 16; k++) {
         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               if (k == 0 || k == 15 || i == 0 || i == 15 || j == 0 || j == 15) {
                  double d0 = k / 15.0F * 2.0F - 1.0F;
                  double d1 = i / 15.0F * 2.0F - 1.0F;
                  double d2 = j / 15.0F * 2.0F - 1.0F;
                  double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                  blockVectors.add(new double[]{d0 / d3 * 0.3F, d1 / d3 * 0.3F, d2 / d3 * 0.3F});
               }
            }
         }
      }
   }

   public Explosion(World world, Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
      this.world = world;
      this.source = entity;
      this.size = (float)Math.max((double)f, 0.0);
      this.posX = d0;
      this.posY = d1;
      this.posZ = d2;
      this.a = flag;
      this.b = flag1;
   }

   public void a() {
      if (!(this.size < 0.1F)) {
         int floorPosX = MathHelper.floor(this.posX);
         int floorPosY = MathHelper.floor(this.posY);
         int floorPosZ = MathHelper.floor(this.posZ);
         Chunk chunk = this.world.getChunkAt(floorPosX >> 4, floorPosZ >> 4);
         if (!chunk.getBlockData(floorPosX, floorPosY, floorPosZ).getBlock().getMaterial().isLiquid()) {
            this.handleBlocks(chunk);
         }

         float strength = this.size * 2.0F;
         Vec3D vec3d = new Vec3D(this.posX, this.posY, this.posZ);
         int minX = MathHelper.floor(this.posX - strength - 1.0) >> 4;
         int maxX = MathHelper.floor(this.posX + strength + 1.0) >> 4;
         int minY = MathHelper.clamp(MathHelper.floor(this.posY - strength - 1.0) >> 4, 0, 15);
         int maxY = MathHelper.clamp(MathHelper.floor(this.posY + strength + 1.0) >> 4, 0, 15);
         int minZ = MathHelper.floor(this.posZ - strength - 1.0) >> 4;
         int maxZ = MathHelper.floor(this.posZ + strength + 1.0) >> 4;

         for (int chunkX = minX; chunkX <= maxX; chunkX++) {
            for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
               if (Utils.isInvalidChunk(chunk, chunkX, chunkZ)) {
                  chunk = this.world.getChunkIfLoaded(chunkX, chunkZ);
               }

               if (chunk != null) {
                  for (int chunkY = minY; chunkY <= maxY; chunkY++) {
                     this.handleEntities(new ArrayList<>(chunk.entitySlices[chunkY]), vec3d, strength);
                  }
               }
            }
         }
      }
   }

   public void a(boolean flag) {
      if ((Boolean)BlazeConfig.explosionSounds.getValue()) {
         this.world
            .makeSound(
               this.posX,
               this.posY,
               this.posZ,
               "random.explode",
               this.source instanceof EntityTNTPrimed ? this.world.paperSpigotConfig.tntExplosionVolume : 4.0F,
               (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F
            );
      }

      if ((Boolean)BlazeConfig.explosionParticles.getValue()) {
         if (this.size >= 2.0F && this.b) {
            this.world.addParticle(EnumParticle.EXPLOSION_HUGE, this.posX, this.posY, this.posZ, 1.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
         } else {
            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 1.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
         }
      }

      Chunk chunk = null;
      if (this.b) {
         CraftWorld bworld = this.world.getWorld();
         CraftEntity explode = this.source == null ? null : this.source.getBukkitEntity();
         Location location = new Location(bworld, this.posX, this.posY, this.posZ);
         List<org.bukkit.block.Block> blockList = Lists.newArrayList();

         for (int i = this.blocks.size() - 1; i >= 0; i--) {
            BlockPosition cpos = this.blocks.get(i);
            org.bukkit.block.Block bblock = bworld.getBlockAt(cpos.getX(), cpos.getY(), cpos.getZ());
            if (bblock.getType() != org.bukkit.Material.AIR) {
               blockList.add(bblock);
            }
         }

         List<org.bukkit.block.Block> bukkitBlocks;
         boolean cancelled;
         float yield;
         if (explode != null) {
            EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, 0.3F);
            this.world.getServer().getPluginManager().callEvent(event);
            cancelled = event.isCancelled();
            bukkitBlocks = event.blockList();
            yield = event.getYield();
         } else {
            BlockExplodeEvent event = new BlockExplodeEvent(location.getBlock(), blockList, 0.3F);
            this.world.getServer().getPluginManager().callEvent(event);
            cancelled = event.isCancelled();
            bukkitBlocks = event.blockList();
            yield = event.getYield();
         }

         this.blocks.clear();

         for (org.bukkit.block.Block bblock : bukkitBlocks) {
            this.blocks.add(new BlockPosition(bblock.getX(), bblock.getY(), bblock.getZ()));
         }

         if (cancelled) {
            this.wasCanceled = true;
            return;
         }

         for (BlockPosition blockposition : this.blocks) {
            int x = blockposition.getX();
            int y = blockposition.getY();
            int z = blockposition.getZ();
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            if (Utils.isInvalidChunk(chunk, chunkX, chunkZ)) {
               chunk = this.world.getChunkAt(chunkX, chunkZ);
            }

            IBlockData iblockdata = this.world.getType(chunk, x, y, z, true);
            Block block = iblockdata.getBlock();
            if (block.getMaterial() != Material.AIR) {
               if (block.explosionDurability > 0) {
                  int durability = this.world.getDurability(chunk, x, y, z) - 1;
                  if (durability > 0) {
                     this.world.setDurability(chunk, x, y, z, durability);
                     continue;
                  }
               }

               if ((Boolean)BlazeConfig.dropExplosionBlocks.getValue() && block.a(this)) {
                  block.dropNaturally(this.world, blockposition, iblockdata, yield, 0);
               }

               this.world.setTypeAndData(chunk, blockposition, Blocks.AIR.getBlockData(), 3, true, false);
               block.wasExploded(this.world, blockposition, this);
            }
         }
      }

      if (this.a) {
         for (BlockPosition blockposition : this.blocks) {
            int xx = blockposition.getX();
            int yx = blockposition.getY();
            int zx = blockposition.getZ();
            int chunkXx = xx >> 4;
            int chunkZx = zx >> 4;
            if (Utils.isInvalidChunk(chunk, chunkXx, chunkZx)) {
               chunk = this.world.getChunkAt(chunkXx, chunkZx);
            }

            if (this.world.getType(chunk, xx, yx, zx, true).getBlock().getMaterial() == Material.AIR
               && this.world.getType(chunk, xx, yx - 1, zx, true).getBlock().o()
               && this.world.random.nextInt(3) == 0
               && !CraftEventFactory.callBlockIgniteEvent(this.world, chunk, xx, yx, zx, this).isCancelled()) {
               this.world.setTypeAndData(chunk, blockposition, Blocks.FIRE.getBlockData(), 3, true, false);
            }
         }
      }
   }

   public void handleEntities(List<Entity> list, Vec3D vec3d, float strength) {
      double lastX = 2.147483647E9;
      double lastY = 0.0;
      double lastZ = 0.0;
      double density = 0.0;
      double distance = 0.0;

      for (Entity entity : list) {
         if (!entity.aW() && !entity.dead) {
            double distX = entity.locX - this.posX;
            double distY = entity.locY + entity.getHeadHeight() - this.posY;
            double distZ = entity.locZ - this.posZ;
            if (lastX != entity.locX || lastY != entity.locY || lastZ != entity.locZ) {
               double distSquared = distX * distX + distY * distY + distZ * distZ;
               if (distSquared > 64.0 || distSquared == 0.0) {
                  continue;
               }

               lastX = entity.locX;
               lastY = entity.locY;
               lastZ = entity.locZ;
               distance = Math.sqrt(distSquared);
               density = (1.0 - distance / strength) * this.getBlockDensity(vec3d, entity.boundingBox);
            }

            distX /= distance;
            distY /= distance;
            distZ /= distance;
            if (!(entity instanceof EntityTNTPrimed) && !(entity instanceof EntityFallingBlock)) {
               CraftEventFactory.entityDamage = this.source;
               entity.forceExplosionKnockback = false;
               boolean wasDamaged = entity.damageEntity(DamageSource.explosion(this), (int)((density * density + density) / 2.0 * 8.0 * strength + 1.0));
               CraftEventFactory.entityDamage = null;
               if (wasDamaged || entity.forceExplosionKnockback) {
                  double d14 = entity instanceof EntityPlayer && this.world.paperSpigotConfig.disableExplosionKnockback
                     ? 0.0
                     : EnchantmentProtection.a(entity, density);
                  entity.g(distX * d14, distY * d14, distZ * d14);
                  if (entity instanceof EntityHuman
                     && !((EntityHuman)entity).abilities.isInvulnerable
                     && !this.world.paperSpigotConfig.disableExplosionKnockback) {
                     this.k.put((EntityHuman)entity, new Vec3D(distX * density, distY * density, distZ * density));
                  }
               }
            } else {
               entity.g(distX * density, distY * density, distZ * density);
            }
         }
      }

      this.world.rayTraceCache.clear();
   }

   private void handleBlocks(Chunk chunk) {
      LongOpenHashSet set = new LongOpenHashSet();
      int lastX = 0;
      int lastY = 0;
      int lastZ = 0;

      for (double[] vector : blockVectors) {
         float power = this.size * 1.12F;
         float resistance = 0.0F;
         double stepX = this.posX;
         double stepY = this.posY;

         for (double stepZ = this.posZ; power > 0.0F; power -= 0.22500001F) {
            int floorX = NumberConversions.floor(stepX);
            int floorY = NumberConversions.floor(stepY);
            int floorZ = NumberConversions.floor(stepZ);
            if (lastX == floorX && lastY == floorY && lastZ == floorZ) {
               power -= resistance;
            } else {
               lastX = floorX;
               lastY = floorY;
               lastZ = floorZ;
               int chunkX = floorX >> 4;
               int chunkZ = floorZ >> 4;
               if (Utils.isInvalidChunk(chunk, chunkX, chunkZ)) {
                  chunk = this.world.getChunkAt(chunkX, chunkZ);
               }

               Block block = chunk.getBlockData(floorX, floorY, floorZ).getBlock();
               if (block.getMaterial().isLiquid()) {
                  break;
               }

               if (block != Blocks.AIR && floorY < 256 && floorY >= 0) {
                  if (block.explosionDurability > 0) {
                     if ((Boolean)BlazeConfig.damageOnlyByTNT.getValue() && !(this.source instanceof EntityTNTPrimed)) {
                        break;
                     }

                     int durability = this.world.getDurability(chunk, floorX, floorY, floorZ);
                     if (durability < 0) {
                        break;
                     }

                     if (durability <= 1) {
                        resistance = block.explosionResistance;
                     }
                  } else {
                     resistance = block.explosionResistance;
                  }

                  power -= resistance;
                  if (power > 0.0F) {
                     set.add(BlockPosition.asLong(floorX, floorY, floorZ));
                  }
               }
            }

            stepX += vector[0];
            stepY += vector[1];
            stepZ += vector[2];
         }
      }

      LongIterator iterator = set.iterator();

      while (iterator.hasNext()) {
         this.blocks.add(BlockPosition.fromLong(iterator.nextLong()));
      }
   }

   public Map<EntityHuman, Vec3D> b() {
      return this.k;
   }

   public List<BlockPosition> getBlocks() {
      return this.blocks;
   }

   private float getBlockDensity(Vec3D vec3d, AxisAlignedBB aabb) {
      int key = this.genKey(this, aabb);
      float blockDensity = this.world.explosionDensityCache.get(key);
      if (blockDensity == -1.0F) {
         blockDensity = this.world.getExplosionDensity(vec3d, aabb);
         this.world.explosionDensityCache.put(key, blockDensity);
      }

      return blockDensity;
   }

   private int genKey(Explosion explosion, AxisAlignedBB aabb) {
      int result = explosion.world.hashCode();
      long temp = Double.doubleToLongBits(explosion.posX);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(explosion.posY);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(explosion.posZ);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(aabb.a);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(aabb.b);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(aabb.c);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(aabb.d);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(aabb.e);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(aabb.f);
      return 31 * result + (int)(temp ^ temp >>> 32);
   }

   public EntityLiving getSource() {
      return this.source == null
         ? null
         : (
            this.source instanceof EntityTNTPrimed
               ? ((EntityTNTPrimed)this.source).getSource()
               : (
                  this.source instanceof EntityLiving
                     ? (EntityLiving)this.source
                     : (this.source instanceof EntityFireball ? ((EntityFireball)this.source).shooter : null)
               )
         );
   }

   public Explosion cancel() {
      this.wasCanceled = true;
      return this;
   }

   public void clearBlocks() {
      this.blocks.clear();
   }
}
