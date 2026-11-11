package net.minecraft.server.v1_8_R3;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.configs.PearlConfig;
import lol.deyo.blaze.configs.PearlConfig.BlockAliases;
import lol.deyo.blaze.utils.PearlUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPearlRefundEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;
import org.bukkit.material.TrapDoor;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class EntityEnderPearl extends EntityProjectile {
   private final ImmutableSet<org.bukkit.Material> blocked = Sets.immutableEnumSet(
      org.bukkit.Material.THIN_GLASS, new org.bukkit.Material[]{org.bukkit.Material.STAINED_GLASS_PANE, org.bukkit.Material.STAINED_GLASS}
   );
   private final Random random = new Random();
   private EntityLiving c;
   private boolean isEdited;
   private boolean crosspearl;
   private boolean ar;
   private boolean a;

   public EntityEnderPearl(World world) {
      super(world);
      this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls;
   }

   public EntityEnderPearl(World world, EntityLiving entityliving) {
      super(world, entityliving);
      this.c = entityliving;
      this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls;
   }

   @Override
   public float getRandomness() {
      return (Float)BlazeConfig.pearlTrajectoryRandomness.getValue();
   }

   @Override
   protected void a(MovingObjectPosition moving) {
      double pearlDamage = (Double)PearlConfig.pearlDamage.getValue();
      EntityLiving entityliving = this.getShooter();
      BlockPosition pos = moving.a();
      org.bukkit.block.Block block = pos != null ? this.getBukkitEntity().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()) : null;
      if (block != null && this.skipThruBlock(block)) {
         org.bukkit.Material type = block.getType();
         Location bukkitLoc = this.getBukkitEntity().getLocation();
         BlockIterator blockIterator = null;

         try {
            Vector v1 = new Vector(this.locX, this.locY, this.locZ);
            Vector v2 = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            Vector v3 = new Vector(v2.getX() - v1.getX(), v2.getY() - v1.getY(), v2.getZ() - v1.getZ()).normalize();
            blockIterator = new BlockIterator(this.world.getWorld(), v1, v3, 0.0, 1);
         } catch (IllegalStateException var17) {
         }

         if (blockIterator != null) {
            boolean open = false;
            boolean solid = false;
            boolean webOrString = false;
            boolean webOrStringGood = true;

            while (blockIterator.hasNext()) {
               org.bukkit.block.Block blockIter = blockIterator.next();
               if (blockIter.getType().isSolid() && blockIter.getType().isOccluding()) {
                  solid = true;
               }

               if (type == org.bukkit.Material.WEB || type == org.bukkit.Material.TRIPWIRE) {
                  webOrString = true;
                  org.bukkit.block.Block ent = bukkitLoc.getBlock();
                  org.bukkit.Material entT = bukkitLoc.getBlock().getType();
                  if (entT == org.bukkit.Material.AIR || entT == org.bukkit.Material.WEB || entT == org.bukkit.Material.TRIPWIRE) {
                     if (PearlUtil.customTransparent(blockIter) && !this.skipThruBlock(blockIter)) {
                        webOrStringGood = false;
                     }

                     if (entT == org.bukkit.Material.AIR
                        && (
                           ent.getRelative(BlockFace.UP).getType() == org.bukkit.Material.WEB
                              || ent.getRelative(BlockFace.UP).getType() == org.bukkit.Material.TRIPWIRE
                        )
                        && !webOrStringGood) {
                        webOrStringGood = true;
                     }
                  }

                  if (blockIter.getState().getData() instanceof Gate && !((Gate)blockIter.getState().getData()).isOpen()) {
                     webOrStringGood = false;
                  }

                  if (BlockAliases.DOORS.isApplicable(blockIter.getType())) {
                     webOrStringGood = false;
                  }

                  if (this.blocked.contains(blockIter.getType())) {
                     webOrStringGood = false;
                  }
               }

               if (PearlUtil.isOpenFenceGate(blockIter)) {
                  open = true;
               }

               if (open) {
                  if (PearlUtil.customTransparent(blockIter)) {
                     solid = true;
                  }

                  if (blockIter.getState().getData() instanceof Gate && !((Gate)blockIter.getState().getData()).isOpen()) {
                     solid = true;
                  }
               }
            }

            if (open && !solid || webOrString && webOrStringGood) {
               return;
            }
         }
      }

      if (pearlDamage > 0.0 && moving.entity != null) {
         if (moving.entity == this.c) {
            return;
         }

         moving.entity.damageEntity(DamageSource.projectile(this, entityliving), 0.0F);
      }

      if (this.inUnloadedChunk && this.world.paperSpigotConfig.removeUnloadedEnderPearls) {
         this.die();
      }

      if (!this.world.isClientSide) {
         if (entityliving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityliving;
            if (entityplayer.playerConnection.a().g() && entityplayer.world == this.world && !entityplayer.isSleeping()) {
               CraftPlayer player = entityplayer.getBukkitEntity();
               Location pl = player.getLocation();
               Location location = this.getBukkitEntity().getLocation();
               location.setPitch(pl.getPitch());
               location.setYaw(pl.getYaw());
               org.bukkit.block.Block block2 = location.getBlock();
               org.bukkit.Material mat = block2.getType();
               String t = mat.toString();
               if (this.checkForEntity(location, player, moving)) {
                  return;
               }

               org.bukkit.block.Block b = block2.getRelative(BlockFace.UP);
               if (t.contains("CHEST") && b.getType().toString().contains("FENCE_GATE") && this.distance(b, pl, 2.0)) {
                  this.refundPearl(player);
                  this.die();
                  return;
               }

               if (!PearlUtil.thruEnabled(mat) && PearlUtil.isApplicable(mat)) {
                  this.refundPearl(player);
                  this.die();
                  return;
               }

               if (t.equals("IRON_TRAPDOOR")
                  || t.equals("TRAP_DOOR")
                     && (
                        pl.getBlock().getRelative(BlockFace.UP).getType() == org.bukkit.Material.TRAP_DOOR
                           || pl.getBlock().getRelative(BlockFace.UP).getType().toString().equals("IRON_TRAPDOOR")
                     )) {
                  this.refundPearl(player);
                  this.die();
                  return;
               }

               if (mat == org.bukkit.Material.FENCE_GATE && PearlUtil.customTransparent(block2.getRelative(BlockFace.UP))) {
                  this.refundPearl(player);
                  this.die();
                  return;
               }

               if (!this.checkPearlThru(location, player)) {
                  this.refundPearl(player);
                  this.die();
                  return;
               }

               if (this.checkForTrapDoor(location)) {
                  this.isEdited = true;
                  this.setToBlock(location);
               }

               if (!this.isEdited) {
                  if ((Boolean)PearlConfig.refundRiskyPearl.getValue() && !this.a && !this.checkRisky(location, player, moving)) {
                     this.refundPearl(player);
                     this.die();
                     return;
                  }

                  if ((Boolean)PearlConfig.refundIfSoClose.getValue() && !this.checkClose(location, player, moving)) {
                     this.refundPearl(player);
                     this.die();
                     return;
                  }

                  if ((Boolean)PearlConfig.refundSuffocation.getValue() && PearlUtil.customTransparent(block2)) {
                     this.refundPearl(player);
                     this.die();
                     return;
                  }
               }

               if ((Boolean)PearlConfig.fixBlockGlitch.getValue()) {
                  if (!this.isEdited) {
                     this.setToBlock(location);
                  } else if (this.blocked.contains(mat)) {
                     this.refundPearl(player);
                     this.die();
                     return;
                  }

                  if (block != null
                     && block2.getRelative(BlockFace.DOWN).getType() == org.bukkit.Material.AIR
                     && (this.blocked.contains(block.getType()) || block.getType() == org.bukkit.Material.GLASS)) {
                     location.setY(location.getY() - 0.5);
                  }
               }

               if ((Boolean)PearlConfig.fixFenceGlitch.getValue() && !this.a && !this.checkFenceGate(location)) {
                  this.refundPearl(player);
                  this.die();
                  return;
               }

               PlayerTeleportEvent playerTeleportEvent = new PlayerTeleportEvent(player, player.getLocation(), location, TeleportCause.ENDER_PEARL);
               Bukkit.getPluginManager().callEvent(playerTeleportEvent);
               if (!playerTeleportEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
                  if ((Boolean)PearlConfig.spawnEndermite.getValue()
                     && this.random.nextFloat() < 0.05F
                     && this.world.getGameRules().getBoolean("doMobSpawning")) {
                     EntityEndermite entityendermite = new EntityEndermite(this.world);
                     entityendermite.a(true);
                     entityendermite.setPositionRotation(entityliving.locX, entityliving.locY, entityliving.locZ, entityliving.yaw, entityliving.pitch);
                     this.world.addEntity(entityendermite);
                  }

                  if (entityplayer.au()) {
                     entityplayer.mount(null);
                  }

                  entityplayer.playerConnection.teleport(playerTeleportEvent.getTo(), false);
                  entityplayer.fallDistance = 0.0F;
                  if (pearlDamage > 0.0) {
                     CraftEventFactory.entityDamage = this;
                     entityplayer.damageEntity(DamageSource.FALL, (float)pearlDamage);
                     CraftEventFactory.entityDamage = null;
                  }
               }
            }
         }

         if ((Boolean)PearlConfig.pearlParticlesEnabled.getValue()) {
            for (int i = 0; i < PearlConfig.pearlParticleCount.getValue(); i++) {
               this.world
                  .addParticle(
                     EnumParticle.PORTAL,
                     this.locX,
                     this.locY + this.random.nextDouble() * 2.0,
                     this.locZ,
                     this.random.nextGaussian(),
                     0.0,
                     this.random.nextGaussian()
                  );
            }
         }

         this.die();
      }
   }

   private boolean skipThruBlock(org.bukkit.block.Block block) {
      org.bukkit.Material type = block.getType();
      return (Boolean)PearlConfig.pearlThruPlants.getValue() && PearlUtil.isPlantBukkit(type)
         || (Boolean)PearlConfig.pearlThruFence.getValue() && PearlUtil.isOpenFenceGate(block)
         || (Boolean)PearlConfig.pearlThruWeb.getValue() && type == org.bukkit.Material.WEB
         || (Boolean)PearlConfig.pearlThruString.getValue() && type == org.bukkit.Material.TRIPWIRE;
   }

   protected boolean checkForEntity(Location location, Player player, MovingObjectPosition moving) {
      if (moving.entity != null) {
         if (moving.entity == this.c || location.getBlock().getType().toString().contains("FENCE_GATE")) {
            return false;
         }

         if (moving.entity.getBukkitEntity().getLocation().distance(player.getLocation()) <= 0.5 && (Boolean)PearlConfig.entityTpGetOutOneByOne.getValue()) {
            return true;
         }

         if ((Boolean)PearlConfig.entityTpHitDetection.getValue()) {
            String direction = PearlUtil.direction(location);
            org.bukkit.block.Block block = location.getBlock();
            List<BlockFace> blockface = this.getBFByDirection(direction);
            org.bukkit.entity.Entity bukkitEntity = moving.entity.getBukkitEntity();
            Location a = bukkitEntity.getLocation();
            if (a.getBlock().getType() == org.bukkit.Material.AIR) {
               if (!this.isntDiagonal(direction)) {
                  if (this.isGood(block.getRelative(blockface.get(0))) && this.isGood(block.getRelative(blockface.get(1)))) {
                     location.setX(moving.entity.locX);
                     location.setZ(moving.entity.locZ);
                  }
               } else {
                  org.bukkit.block.Block next = block.getRelative(blockface.get(0));
                  org.bukkit.block.Block nextnext = next.getRelative(blockface.get(0));
                  if (a.distance(player.getLocation()) <= 2.1 && !PearlUtil.customTransparent(block) && this.isGood(next) && this.isGood(nextnext)) {
                     location.setX(moving.entity.locX);
                     location.setZ(moving.entity.locZ);
                  }

                  if (a.distance(location) <= 2.05 && !PearlUtil.customTransparent(block) && this.isGood(next)) {
                     location.setX(moving.entity.locX);
                     location.setZ(moving.entity.locZ);
                  }
               }
            }
         }
      }

      return false;
   }

   protected boolean isGood(org.bukkit.block.Block block) {
      return !PearlUtil.customTransparent(block) && !block.getType().toString().contains("FENCE_GATE");
   }

   protected boolean checkClose(Location location, Player player, MovingObjectPosition mov) {
      if (mov.entity != null) {
         return true;
      } else {
         org.bukkit.block.Block tBlock = player.getTargetBlock((java.util.Set<org.bukkit.Material>)null, 1);
         org.bukkit.block.Block block = location.getBlock();
         String dr = PearlUtil.direction(player.getLocation());
         org.bukkit.block.Block west = block.getRelative(BlockFace.WEST);
         org.bukkit.block.Block east = block.getRelative(BlockFace.EAST);
         org.bukkit.block.Block north = block.getRelative(BlockFace.NORTH);
         org.bukkit.block.Block south = block.getRelative(BlockFace.SOUTH);
         if (location.distance(player.getLocation()) <= 1.75 && player.getLocation().distance(tBlock.getLocation().clone().add(0.5, 0.0, 0.5)) <= 1.75) {
            if (PearlUtil.thruEnabled(block.getType()) || PearlUtil.thruEnabled(tBlock.getRelative(BlockFace.DOWN).getType())) {
               return true;
            }

            if (PearlUtil.customTransparent(tBlock)) {
               return false;
            }
         }

         if (PearlUtil.customTransparent(block)) {
            return true;
         } else {
            Location pLocation = player.getLocation();
            double d = (Double)PearlConfig.refundCloseDistance.getValue();
            return dr.contains("N")
               ? !PearlUtil.customTransparent(north) || !this.distance(north, pLocation, d)
               : (
                  dr.contains("S")
                     ? !PearlUtil.customTransparent(south) || !this.distance(south, pLocation, d)
                     : (
                        dr.contains("W")
                           ? !PearlUtil.customTransparent(west) || !this.distance(west, pLocation, d)
                           : !dr.contains("E") || !PearlUtil.customTransparent(east) || !this.distance(east, pLocation, d)
                     )
               );
         }
      }
   }

   protected boolean checkRisky(Location location, Player player, MovingObjectPosition mov) {
      org.bukkit.block.Block block = location.getBlock();
      org.bukkit.block.Block west = block.getRelative(BlockFace.WEST);
      org.bukkit.block.Block east = block.getRelative(BlockFace.EAST);
      org.bukkit.block.Block north = block.getRelative(BlockFace.NORTH);
      org.bukkit.block.Block south = block.getRelative(BlockFace.SOUTH);
      if (!PearlUtil.customTransparent(block)
         && !block.getType().toString().contains("CHEST")
         && !block.getType().toString().contains("SIGN")
         && PearlUtil.isRiskyBlock(block.getRelative(BlockFace.DOWN))
         && PearlUtil.isRiskyBlock(block.getRelative(BlockFace.UP))
         && mov.entity == null) {
         String direction = PearlUtil.direction(player.getLocation());
         if (direction.contains("E")
            && !PearlUtil.customTransparent(east)
            && !east.getType().toString().contains("CHEST")
            && !east.getType().toString().contains("SIGN")
            && !PearlUtil.customTransparent(east.getRelative(BlockFace.UP))) {
            return true;
         } else if (direction.contains("W")
            && !PearlUtil.customTransparent(west)
            && !west.getType().toString().contains("CHEST")
            && !west.getType().toString().contains("SIGN")
            && !PearlUtil.customTransparent(west.getRelative(BlockFace.UP))) {
            return true;
         } else {
            return direction.contains("N")
                  && !PearlUtil.customTransparent(north)
                  && !north.getType().toString().contains("CHEST")
                  && !north.getType().toString().contains("SIGN")
                  && !PearlUtil.customTransparent(north.getRelative(BlockFace.UP))
               ? true
               : direction.contains("S")
                  && !PearlUtil.customTransparent(south)
                  && !south.getType().toString().contains("CHEST")
                  && !south.getType().toString().contains("SIGN")
                  && !PearlUtil.customTransparent(south.getRelative(BlockFace.UP));
         }
      } else {
         return true;
      }
   }

   protected boolean checkFenceGate(Location location) {
      org.bukkit.block.Block block = location.getBlock();
      org.bukkit.block.Block down = block.getRelative(BlockFace.DOWN);
      org.bukkit.Material mat = block.getType();
      if (mat.toString().contains("FENCE_GATE")) {
         if (down.getType().toString().contains("CHEST") && (Boolean)PearlConfig.pearlChestUnderFence.getValue()) {
            return true;
         } else if (down.getType() == org.bukkit.Material.HOPPER && (Boolean)PearlConfig.pearlHopperUnderFence.getValue()) {
            return true;
         } else if (!((Gate)block.getState().getData()).isOpen()) {
            return down.getType().toString().contains("FENCE_GATE") && ((Gate)down.getState().getData()).isOpen();
         } else {
            this.isEdited = true;
            location.setX(location.getBlockX() + 0.5);
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ() + 0.5);
            return true;
         }
      } else {
         return true;
      }
   }

   protected boolean checkForTrapDoor(Location location) {
      org.bukkit.block.Block a = location.getBlock();
      String d = PearlUtil.direction(location);
      if (!PearlUtil.customTransparent(a) && this.isntDiagonal(d)) {
         org.bukkit.block.Block e = a.getRelative(this.getBFByDirection(d).get(0));
         org.bukkit.block.Block k = e.getRelative(BlockFace.UP);
         if ((e.getType().toString().contains("FENCE_GATE") && k.getType().toString().equals("TRAP_DOOR") || k.getType().toString().equals("IRON_TRAPDOOR"))
            && ((TrapDoor)k.getState().getData()).isOpen()) {
            this.addToLocation(d, location, 1.0);
            this.a = true;
            return true;
         }
      }

      return false;
   }

   protected boolean checkPearlThru(Location location, Player player) {
      Location plocation = player.getLocation();
      String direction = PearlUtil.direction(plocation);
      org.bukkit.block.Block block = this.getThruBlock(location, player);
      if (block != null) {
         org.bukkit.Material mat = block.getType();
         BlockFace blockface = null;
         boolean stairFacing = PearlUtil.isStairs(mat);
         if (stairFacing) {
            Stairs stairs = (Stairs)block.getState().getData();
            blockface = stairs.getFacing();
         }

         if (this.crosspearl) {
            return true;
         }

         boolean b = !(Boolean)PearlConfig.refundOnCritblock.getValue();
         boolean i = (Boolean)PearlConfig.instantlyTaliTeleport.getValue();
         Location loc = block.getLocation().clone();
         boolean diagonal = PearlUtil.diagonalPearl(mat);
         org.bukkit.block.Block west = block.getRelative(BlockFace.WEST);
         org.bukkit.block.Block east = block.getRelative(BlockFace.EAST);
         org.bukkit.block.Block north = block.getRelative(BlockFace.NORTH);
         org.bukkit.block.Block south = block.getRelative(BlockFace.SOUTH);
         if (direction.contains("E")) {
            if (direction.equals("E")) {
               if (stairFacing && blockface != null && blockface != BlockFace.SOUTH && blockface != BlockFace.NORTH && blockface != BlockFace.WEST) {
                  return false;
               }

               boolean qe = false;
               if (!PearlUtil.thruEnabled(east.getType())
                  && (PearlUtil.customTransparent(east) || !this.ar && PearlUtil.customTransparent(east.getRelative(BlockFace.DOWN)))) {
                  if (i) {
                     location.add(1.0, 0.0, 0.0);
                  }

                  qe = true;
               }

               if (!qe) {
                  for (int ia = 0; ia < PearlConfig.maxTeleportBlocks.getValue(); ia++) {
                     org.bukkit.block.Block nextcurrent = block.getRelative(BlockFace.EAST);
                     if (PearlUtil.thruEnabled(nextcurrent.getType())) {
                        org.bukkit.block.Block l = nextcurrent.getRelative(BlockFace.EAST);
                        if (!PearlUtil.thruEnabled(l.getType())) {
                           if (PearlUtil.customTransparent(l)
                              || !this.ar && l.getRelative(BlockFace.UP).getType().isSolid() && PearlUtil.customTransparent(l.getRelative(BlockFace.DOWN))) {
                              return false;
                           }

                           if (!this.isStairGood(nextcurrent, direction)) {
                              return false;
                           }

                           location.add(1.0, 0.0, 0.0);
                           break;
                        }

                        if (!this.isStairGood(nextcurrent, direction)) {
                           return false;
                        }

                        block = nextcurrent;
                        location.add(1.0, 0.0, 0.0);
                     }
                  }
               }

               org.bukkit.block.Block abc;
               if (this.isClosed(abc = block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)) || PearlUtil.isCritBlock(abc)) {
                  if (!PearlUtil.isCritblock(block.getType()) || qe) {
                     return b;
                  }

                  location.add(1.0, -0.5, 0.0);
                  this.setToBlock(location);
               }
            }

            if (direction.equals("SE") && PearlUtil.customTransparent(east) && PearlUtil.customTransparent(south)) {
               return false;
            }

            if (direction.equals("NE") && PearlUtil.customTransparent(east) && PearlUtil.customTransparent(north)) {
               return false;
            }

            if (direction.equals("NE") && diagonal) {
               if (PearlUtil.diagonalPearl(south.getType()) || PearlUtil.diagonalPearl(west.getType())) {
                  if (!PearlUtil.customTransparent(north)) {
                     location.setZ(loc.add(0.0, 0.0, -1.0).getZ());
                     location.setX(loc.getX() + 0.5);
                     location.setZ(loc.getZ() + 0.5);
                     this.isEdited = true;
                     return true;
                  }

                  location.setX(loc.add(1.0, 0.0, 0.0).getX());
                  location.setX(loc.getX() + 0.5);
                  location.setZ(loc.getZ() + 0.5);
                  this.isEdited = true;
                  return true;
               }
            } else if (direction.equals("SE") && diagonal && (PearlUtil.diagonalPearl(north.getType()) || PearlUtil.diagonalPearl(west.getType()))) {
               if (!PearlUtil.customTransparent(south)) {
                  location.setZ(loc.add(0.0, 0.0, 1.0).getZ());
                  location.setX(loc.getX() + 0.5);
                  location.setZ(loc.getZ() + 0.5);
                  this.isEdited = true;
                  return true;
               }

               location.setX(loc.add(1.0, 0.0, 0.0).getX());
               location.setX(loc.getX() + 0.5);
               location.setZ(loc.getZ() + 0.5);
               this.isEdited = true;
               return true;
            }

            if (!this.isEdited) {
               location.setX(location.getBlockX() + 0.5);
               location.setZ(location.getBlockZ() + 0.5);
               this.isEdited = true;
            }

            return true;
         }

         if (direction.contains("W")) {
            if (direction.equals("W")) {
               if (stairFacing && blockface != null && blockface != BlockFace.SOUTH && blockface != BlockFace.NORTH && blockface != BlockFace.EAST) {
                  return false;
               }

               boolean qex = false;
               if (!PearlUtil.thruEnabled(west.getType())
                  && (PearlUtil.customTransparent(west) || !this.ar && PearlUtil.customTransparent(west.getRelative(BlockFace.DOWN)))) {
                  if (i) {
                     location.add(-1.0, 0.0, 0.0);
                  }

                  qex = true;
               }

               if (!qex) {
                  for (int iax = 0; iax < PearlConfig.maxTeleportBlocks.getValue(); iax++) {
                     org.bukkit.block.Block nextcurrent = block.getRelative(BlockFace.WEST);
                     if (PearlUtil.thruEnabled(nextcurrent.getType())) {
                        org.bukkit.block.Block lx = nextcurrent.getRelative(BlockFace.WEST);
                        if (!PearlUtil.thruEnabled(lx.getType())) {
                           if (PearlUtil.customTransparent(lx)
                              || !this.ar && lx.getRelative(BlockFace.UP).getType().isSolid() && PearlUtil.customTransparent(lx.getRelative(BlockFace.DOWN))) {
                              return false;
                           }

                           if (!this.isStairGood(nextcurrent, direction)) {
                              return false;
                           }

                           location.add(-1.0, 0.0, 0.0);
                           break;
                        }

                        if (!this.isStairGood(nextcurrent, direction)) {
                           return false;
                        }

                        block = nextcurrent;
                        location.add(-1.0, 0.0, 0.0);
                     }
                  }
               }

               org.bukkit.block.Block abc;
               if (this.isClosed(abc = block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP)) || PearlUtil.isCritBlock(abc)) {
                  if (!PearlUtil.isCritblock(block.getType()) || qex) {
                     return b;
                  }

                  location.add(-1.0, -0.5, 0.0);
                  this.setToBlock(location);
               }
            }

            if (direction.equals("SW")
               ? !PearlUtil.customTransparent(west) || !PearlUtil.customTransparent(south)
               : !direction.equals("NW") || !PearlUtil.customTransparent(west) || !PearlUtil.customTransparent(north)) {
               if (direction.equals("NW") && diagonal) {
                  if (PearlUtil.diagonalPearl(east.getType()) || PearlUtil.diagonalPearl(south.getType())) {
                     if (!PearlUtil.customTransparent(north)) {
                        location.setZ(loc.add(0.0, 0.0, -1.0).getZ());
                        location.setX(loc.getX() + 0.5);
                        location.setZ(loc.getZ() + 0.5);
                        this.isEdited = true;
                        return true;
                     }

                     location.setX(loc.add(-1.0, 0.0, 0.0).getX());
                     location.setX(loc.getX() + 0.5);
                     location.setZ(loc.getZ() + 0.5);
                     this.isEdited = true;
                     return true;
                  }
               } else if (direction.equals("SW") && diagonal && (PearlUtil.diagonalPearl(north.getType()) || PearlUtil.diagonalPearl(east.getType()))) {
                  if (!PearlUtil.customTransparent(south)) {
                     location.setZ(loc.add(0.0, 0.0, 1.0).getZ());
                     location.setX(loc.getX() + 0.5);
                     location.setZ(loc.getZ() + 0.5);
                     this.isEdited = true;
                     return true;
                  }

                  location.setX(loc.add(-1.0, 0.0, 0.0).getX());
                  location.setX(loc.getX() + 0.5);
                  location.setZ(loc.getZ() + 0.5);
                  this.isEdited = true;
                  return true;
               }

               if (!this.isEdited) {
                  location.setX(location.getBlockX() + 0.5);
                  location.setZ(location.getBlockZ() + 0.5);
                  this.isEdited = true;
               }

               return true;
            }

            return false;
         }

         if (direction.equals("N")) {
            if (stairFacing && blockface != null && blockface != BlockFace.EAST && blockface != BlockFace.WEST && blockface != BlockFace.SOUTH) {
               return false;
            }

            boolean qexx = false;
            if (!PearlUtil.thruEnabled(north.getType())
               && (PearlUtil.customTransparent(north) || !this.ar && PearlUtil.customTransparent(north.getRelative(BlockFace.DOWN)))) {
               if (i) {
                  location.add(0.0, 0.0, -1.0);
               }

               qexx = true;
            }

            if (!qexx) {
               for (int iaxx = 0; iaxx < PearlConfig.maxTeleportBlocks.getValue(); iaxx++) {
                  org.bukkit.block.Block nextcurrent = block.getRelative(BlockFace.NORTH);
                  if (PearlUtil.thruEnabled(nextcurrent.getType())) {
                     org.bukkit.block.Block lxx = nextcurrent.getRelative(BlockFace.NORTH);
                     if (!PearlUtil.thruEnabled(lxx.getType())) {
                        if (PearlUtil.customTransparent(lxx)
                           || !this.ar && lxx.getRelative(BlockFace.UP).getType().isSolid() && PearlUtil.customTransparent(lxx.getRelative(BlockFace.DOWN))) {
                           return false;
                        }

                        if (!this.isStairGood(nextcurrent, direction)) {
                           return false;
                        }

                        location.add(0.0, 0.0, -1.0);
                        break;
                     }

                     if (!this.isStairGood(nextcurrent, direction)) {
                        return false;
                     }

                     block = nextcurrent;
                     location.add(0.0, 0.0, -1.0);
                  }
               }
            }

            org.bukkit.block.Block abc;
            if (this.isClosed(abc = block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP)) || PearlUtil.isCritBlock(abc)) {
               if (!PearlUtil.isCritblock(block.getType()) || qexx) {
                  return b;
               }

               location.add(0.0, -0.5, -1.0);
               this.setToBlock(location);
            }

            if (!this.isEdited) {
               location.setX(location.getBlockX() + 0.5);
               location.setZ(location.getBlockZ() + 0.5);
               this.isEdited = true;
            }

            return true;
         }

         if (direction.equals("S")) {
            if (stairFacing && blockface != null && blockface != BlockFace.EAST && blockface != BlockFace.WEST) {
               return false;
            }

            boolean qexxx = false;
            if (!PearlUtil.thruEnabled(south.getType())
               && (PearlUtil.customTransparent(south) || !this.ar && PearlUtil.customTransparent(south.getRelative(BlockFace.DOWN)))) {
               if (i) {
                  location.add(0.0, 0.0, 1.0);
               }

               qexxx = true;
            }

            int iaxxx = 0;

            while (true) {
               label587: {
                  if (iaxxx < (Integer)PearlConfig.maxTeleportBlocks.getValue()) {
                     org.bukkit.block.Block nextcurrent = block.getRelative(BlockFace.SOUTH);
                     if (!PearlUtil.thruEnabled(nextcurrent.getType())) {
                        break label587;
                     }

                     org.bukkit.block.Block lxxx = nextcurrent.getRelative(BlockFace.SOUTH);
                     if (PearlUtil.thruEnabled(lxxx.getType())) {
                        if (!this.isStairGood(nextcurrent, direction)) {
                           return false;
                        }

                        block = nextcurrent;
                        location.add(0.0, 0.0, 1.0);
                        break label587;
                     }

                     if (PearlUtil.customTransparent(lxxx)
                        || !this.ar && lxxx.getRelative(BlockFace.UP).getType().isSolid() && PearlUtil.customTransparent(lxxx.getRelative(BlockFace.DOWN))) {
                        return false;
                     }

                     if (!this.isStairGood(nextcurrent, direction)) {
                        return false;
                     }

                     location.add(0.0, 0.0, 1.0);
                  }

                  org.bukkit.block.Block abcx = block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP);
                  if (this.isClosed(abcx) || PearlUtil.isCritBlock(abcx)) {
                     if (!PearlUtil.isCritblock(block.getType()) || qexxx) {
                        return b;
                     }

                     location.add(0.0, -0.5, 1.0);
                     this.setToBlock(location);
                  }

                  if (!this.isEdited) {
                     location.setX(location.getBlockX() + 0.5);
                     location.setZ(location.getBlockZ() + 0.5);
                     this.isEdited = true;
                  }

                  return true;
               }

               iaxxx++;
            }
         }
      }

      return true;
   }

   private boolean isClosed(org.bukkit.block.Block b) {
      String t = b.getType().toString();
      return (t.equals("IRON_TRAPDOOR") || t.equals("TRAP_DOOR")) && ((TrapDoor)b.getState().getData()).isOpen();
   }

   protected org.bukkit.block.Block getThruBlock(Location location, Player player) {
      org.bukkit.block.Block block = location.getBlock();
      Location pLoc = player.getLocation();
      String dr = PearlUtil.direction(pLoc);
      org.bukkit.Material mat = block.getType();
      org.bukkit.block.Block down = block.getRelative(BlockFace.DOWN);
      org.bukkit.block.Block west = block.getRelative(BlockFace.WEST);
      org.bukkit.block.Block east = block.getRelative(BlockFace.EAST);
      org.bukkit.block.Block north = block.getRelative(BlockFace.NORTH);
      org.bukkit.block.Block south = block.getRelative(BlockFace.SOUTH);
      org.bukkit.Material eastType = east.getType();
      org.bukkit.Material westType = west.getType();
      org.bukkit.Material northType = north.getType();
      org.bukkit.Material southType = south.getType();
      if (this.distance(block, pLoc, 6.0)) {
         if (PearlUtil.thruEnabled(mat)) {
            switch (dr.hashCode()) {
               case 2487:
                  if (dr.equals("NE")) {
                     if (PearlUtil.thruEnabled(eastType)) {
                        return east;
                     }

                     if (PearlUtil.thruEnabled(northType)) {
                        return north;
                     }
                  }
                  break;
               case 2505:
                  if (dr.equals("NW")) {
                     if (PearlUtil.thruEnabled(westType)) {
                        return west;
                     }

                     if (PearlUtil.thruEnabled(northType)) {
                        return north;
                     }
                  }
                  break;
               case 2642:
                  if (dr.equals("SE")) {
                     if (PearlUtil.thruEnabled(eastType)) {
                        return east;
                     }

                     if (PearlUtil.thruEnabled(southType)) {
                        return south;
                     }
                  }
                  break;
               case 2660:
                  if (dr.equals("SW")) {
                     if (PearlUtil.thruEnabled(westType)) {
                        return west;
                     }

                     if (PearlUtil.thruEnabled(southType)) {
                        return south;
                     }
                  }
            }

            return block;
         }

         if (!PearlUtil.customTransparent(block)) {
            org.bukkit.block.Block q = player.getTargetBlock((java.util.Set<org.bukkit.Material>)null, 4);
            if (!PearlUtil.customTransparent(down)) {
               org.bukkit.block.Block tBlock = player.getTargetBlock((java.util.Set<org.bukkit.Material>)null, 1);
               if ((Boolean)PearlConfig.hitBlockTeleport.getValue()) {
                  if (dr.equals("E")) {
                     org.bukkit.block.Block a2 = down.getRelative(BlockFace.EAST);
                     if (PearlUtil.thruEnabled(a2.getType()) && tBlock.getType() == a2.getType()) {
                        if (!this.isStairGood(a2, dr)) {
                           return null;
                        }

                        location.add(0.5, 0.0, 0.0);
                        location.setY(a2.getY());
                        return a2;
                     }
                  } else if (dr.equals("W")) {
                     org.bukkit.block.Block a3 = down.getRelative(BlockFace.WEST);
                     if (PearlUtil.thruEnabled(a3.getType()) && tBlock.getType() == a3.getType()) {
                        if (!this.isStairGood(a3, dr)) {
                           return null;
                        }

                        location.add(-0.5, 0.0, 0.0);
                        location.setY(a3.getY());
                        return a3;
                     }
                  } else if (dr.equals("N")) {
                     org.bukkit.block.Block a4 = down.getRelative(BlockFace.NORTH);
                     if (PearlUtil.thruEnabled(a4.getType()) && tBlock.getType() == a4.getType()) {
                        if (!this.isStairGood(a4, dr)) {
                           return null;
                        }

                        location.setY(a4.getY());
                        location.add(0.0, 0.0, -0.5);
                        return a4;
                     }
                  } else {
                     org.bukkit.block.Block a;
                     if (dr.equals("S") && PearlUtil.thruEnabled((a = down.getRelative(BlockFace.SOUTH)).getType()) && tBlock.getType() == a.getType()) {
                        if (!this.isStairGood(a, dr)) {
                           return null;
                        }

                        location.add(0.0, 0.0, 0.5);
                        location.setY(a.getY());
                        return a;
                     }
                  }
               }
            }

            if ((q.getType().toString().contains("WALL") || q.getType().toString().equals("IRON_TRAPDOOR") || q.getType().toString().equals("TRAP_DOOR"))
               && !PearlUtil.customTransparent(q.getRelative(BlockFace.UP))
               && PearlUtil.getBehind(dr) != null
               && !PearlUtil.customTransparent(q.getRelative(PearlUtil.getBehind(dr)).getRelative(BlockFace.UP))
               && q.getRelative(PearlUtil.getBehind(dr)).getRelative(BlockFace.UP).getLocation().add(0.5, 0.0, 0.5).distance(pLoc) <= 2.0) {
               if (!dr.equals("W") && !dr.equals("E")) {
                  this.ar = true;
                  if (dr.equals("N")) {
                     location.setZ(location.getZ() - 0.7);
                  } else {
                     location.setZ(location.getZ() + 0.7);
                  }

                  return q;
               }

               this.ar = true;
               if (dr.equals("E")) {
                  location.setX(location.getX() + 0.7);
               } else {
                  location.setX(location.getX() - 0.7);
               }

               return q;
            }

            if (down.getType() != org.bukkit.Material.AIR) {
               switch (dr.hashCode()) {
                  case 2487:
                     if (dr.equals("NE")) {
                        double dxxx = (Double)PearlConfig.crossPearlMoveHelper.getValue();
                        if (this.isCannable(north, location)) {
                           if (!PearlUtil.thruEnabled(block.getRelative(BlockFace.NORTH_EAST).getType())
                              && PearlUtil.crossPearl(north.getType())
                              && !east.getType().toString().contains("FENCE_GATE")) {
                              location.setZ(location.getZ() - dxxx);
                              location.setX(location.getX() + dxxx);
                              this.crosspearl = true;
                              this.isEdited = true;
                              return north;
                           }

                           return block.getRelative(BlockFace.NORTH_EAST);
                        }

                        if (this.isCannable(east, location)) {
                           if (!PearlUtil.thruEnabled(block.getRelative(BlockFace.NORTH_EAST).getType())
                              && PearlUtil.crossPearl(east.getType())
                              && !north.getType().toString().contains("FENCE_GATE")) {
                              location.setZ(location.getZ() - dxxx);
                              location.setX(location.getX() + dxxx);
                              this.crosspearl = true;
                              this.isEdited = true;
                              return east;
                           }

                           return block.getRelative(BlockFace.NORTH_EAST);
                        }
                     }
                     break;
                  case 2505:
                     if (dr.equals("NW")) {
                        double dxx = (Double)PearlConfig.crossPearlMoveHelper.getValue();
                        if (this.isCannable(west, location)) {
                           if (!PearlUtil.thruEnabled(block.getRelative(BlockFace.NORTH_WEST).getType())
                              && PearlUtil.crossPearl(west.getType())
                              && !north.getType().toString().contains("FENCE_GATE")) {
                              location.setZ(location.getZ() - dxx);
                              location.setX(location.getX() - dxx);
                              this.crosspearl = true;
                              this.isEdited = true;
                              return west;
                           }

                           return block.getRelative(BlockFace.NORTH_WEST);
                        }

                        if (this.isCannable(north, location)) {
                           if (!PearlUtil.thruEnabled(block.getRelative(BlockFace.NORTH_WEST).getType())
                              && PearlUtil.crossPearl(north.getType())
                              && !west.getType().toString().contains("FENCE_GATE")) {
                              location.setZ(location.getZ() - dxx);
                              location.setX(location.getX() - dxx);
                              this.crosspearl = true;
                              this.isEdited = true;
                              return north;
                           }

                           return block.getRelative(BlockFace.NORTH_WEST);
                        }
                     }
                     break;
                  case 2642:
                     if (dr.equals("SE")) {
                        double dx = (Double)PearlConfig.crossPearlMoveHelper.getValue();
                        if (this.isCannable(east, location)) {
                           if (!PearlUtil.thruEnabled(block.getRelative(BlockFace.SOUTH_EAST).getType())
                              && PearlUtil.crossPearl(east.getType())
                              && !south.getType().toString().contains("FENCE_GATE")) {
                              location.setZ(location.getZ() + dx);
                              location.setX(location.getX() + dx);
                              this.crosspearl = true;
                              this.isEdited = true;
                              return east;
                           }

                           return block.getRelative(BlockFace.SOUTH_EAST);
                        }

                        if (this.isCannable(south, location)) {
                           if (!PearlUtil.thruEnabled(block.getRelative(BlockFace.SOUTH_EAST).getType())
                              && PearlUtil.crossPearl(south.getType())
                              && !east.getType().toString().contains("FENCE_GATE")) {
                              location.setZ(location.getZ() + dx);
                              location.setX(location.getX() + dx);
                              this.crosspearl = true;
                              this.isEdited = true;
                              return south;
                           }

                           return block.getRelative(BlockFace.SOUTH_EAST);
                        }
                     }
                     break;
                  case 2660:
                     if (dr.equals("SW")) {
                        double d = (Double)PearlConfig.crossPearlMoveHelper.getValue();
                        if (this.isCannable(south, location)) {
                           if (!PearlUtil.thruEnabled(block.getRelative(BlockFace.SOUTH_WEST).getType())
                              && PearlUtil.crossPearl(south.getType())
                              && !west.getType().toString().contains("FENCE_GATE")) {
                              location.setZ(location.getZ() + d);
                              location.setX(location.getX() - d);
                              this.crosspearl = true;
                              this.isEdited = true;
                              return south;
                           }

                           return block.getRelative(BlockFace.SOUTH_WEST);
                        }

                        if (this.isCannable(west, location)) {
                           if (!PearlUtil.thruEnabled(block.getRelative(BlockFace.SOUTH_WEST).getType())
                              && PearlUtil.crossPearl(west.getType())
                              && !south.getType().toString().contains("FENCE_GATE")) {
                              location.setZ(location.getZ() + d);
                              location.setX(location.getX() - d);
                              this.crosspearl = true;
                              this.isEdited = true;
                              return west;
                           }

                           return block.getRelative(BlockFace.SOUTH_WEST);
                        }
                     }
               }

               if ((Boolean)PearlConfig.beforeBlockTeleport.getValue()) {
                  switch (dr.hashCode()) {
                     case 69:
                        if (dr.equals("E") && PearlUtil.thruEnabled(eastType) && this.distance(east, location, 0.8)) {
                           if (!this.isStairGood(east, dr)) {
                              return null;
                           }

                           if (PearlUtil.customTransparent(east.getRelative(BlockFace.DOWN))) {
                              location.setX(location.getX() + 0.3);
                              return east;
                           }
                        }
                        break;
                     case 78:
                        if (dr.equals("N") && PearlUtil.thruEnabled(northType) && this.distance(north, location, 0.8)) {
                           if (!this.isStairGood(north, dr)) {
                              return null;
                           }

                           if (PearlUtil.customTransparent(north.getRelative(BlockFace.DOWN))) {
                              location.setZ(location.getZ() - 0.3);
                              return north;
                           }
                        }
                        break;
                     case 83:
                        if (dr.equals("S") && PearlUtil.thruEnabled(southType) && this.distance(south, location, 0.8)) {
                           if (!this.isStairGood(south, dr)) {
                              return null;
                           }

                           if (PearlUtil.customTransparent(south.getRelative(BlockFace.DOWN))) {
                              location.setZ(location.getZ() + 0.3);
                              return south;
                           }
                        }
                        break;
                     case 87:
                        if (dr.equals("W") && PearlUtil.thruEnabled(westType) && this.distance(west, location, 0.8)) {
                           if (!this.isStairGood(west, dr)) {
                              return null;
                           }

                           if (PearlUtil.customTransparent(west.getRelative(BlockFace.DOWN))) {
                              location.setX(location.getX() - 0.3);
                              return west;
                           }
                        }
                  }
               }
            }
         }
      }

      return null;
   }

   private List<BlockFace> getBFByDirection(String d) {
      ArrayList<BlockFace> l = new ArrayList<>();
      switch (d.hashCode()) {
         case 69:
            if (d.equals("E")) {
               l.add(BlockFace.EAST);
               return l;
            }
            break;
         case 78:
            if (d.equals("N")) {
               l.add(BlockFace.NORTH);
               return l;
            }
            break;
         case 83:
            if (d.equals("S")) {
               l.add(BlockFace.SOUTH);
               return l;
            }
            break;
         case 87:
            if (d.equals("W")) {
               l.add(BlockFace.WEST);
               return l;
            }
            break;
         case 2487:
            if (d.equals("NE")) {
               l.add(BlockFace.NORTH);
               l.add(BlockFace.EAST);
               return l;
            }
            break;
         case 2505:
            if (d.equals("NW")) {
               l.add(BlockFace.NORTH);
               l.add(BlockFace.WEST);
               return l;
            }
            break;
         case 2642:
            if (d.equals("SE")) {
               l.add(BlockFace.SOUTH);
               l.add(BlockFace.EAST);
               return l;
            }
            break;
         case 2660:
            if (d.equals("SW")) {
               l.add(BlockFace.SOUTH);
               l.add(BlockFace.WEST);
               return l;
            }
      }

      return l;
   }

   public void refundPearl(Player player) {
      if ((Boolean)PearlConfig.glitchReturnPearl.getValue()) {
         player.getInventory().addItem(new org.bukkit.inventory.ItemStack[]{new org.bukkit.inventory.ItemStack(org.bukkit.Material.ENDER_PEARL)});
         player.updateInventory();
      }

      String message = (String)PearlConfig.glitchReturnMessage.getValue();
      String consoleCommand = (String)PearlConfig.glitchReturnCommand.getValue();
      if (message != null && !message.isEmpty()) {
         player.sendMessage(message);
      }

      if (consoleCommand != null && !consoleCommand.isEmpty()) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand.replace("%player%", player.getName()));
      }

      if (PlayerPearlRefundEvent.getHandlerList().getRegisteredListeners().length != 0) {
         Bukkit.getPluginManager().callEvent(new PlayerPearlRefundEvent(player));
      }
   }

   private boolean isntDiagonal(String d) {
      return d.equals("W") || d.equals("S") || d.equals("E") || d.equals("N");
   }

   private boolean isStairGood(org.bukkit.block.Block block, String dr) {
      boolean u = PearlUtil.isStairs(block.getType());
      BlockFace m = null;
      if (u) {
         Stairs t = (Stairs)block.getState().getData();
         m = t.getFacing();
      }

      if (u && m != null) {
         if (dr.equals("E") && (m == BlockFace.WEST || m == BlockFace.EAST)) {
            this.isEdited = true;
            return false;
         }

         if (dr.equals("W") && (m == BlockFace.WEST || m == BlockFace.EAST)) {
            this.isEdited = true;
            return false;
         }

         if (dr.equals("N") && (m == BlockFace.NORTH || m == BlockFace.SOUTH)) {
            this.isEdited = true;
            return false;
         }

         if (dr.equals("S") && (m == BlockFace.NORTH || m == BlockFace.SOUTH)) {
            this.isEdited = true;
            return false;
         }
      }

      return true;
   }

   private boolean isCannable(org.bukkit.block.Block b, Location l) {
      return this.distance(b, l, 1.0) ? PearlUtil.thruEnabled(b.getType()) : false;
   }

   private void setToBlock(Location l) {
      l.setX(l.getBlockX() + 0.5);
      l.setY(l.getBlockY());
      l.setZ(l.getBlockZ() + 0.5);
   }

   protected void addToLocation(String d, Location location, double x) {
      switch (d.hashCode()) {
         case 69:
            if (d.equals("E")) {
               location.setX(location.getX() + x);
            }
            break;
         case 78:
            if (d.equals("N")) {
               location.setZ(location.getZ() - x);
            }
            break;
         case 83:
            if (d.equals("S")) {
               location.setZ(location.getZ() + x);
            }
            break;
         case 87:
            if (d.equals("W")) {
               location.setX(location.getX() - x);
            }
      }
   }

   private boolean distance(org.bukkit.block.Block b, Location l, double d) {
      return b.getLocation().clone().add(0.5, 0.0, 0.5).distance(l) <= d;
   }
}
