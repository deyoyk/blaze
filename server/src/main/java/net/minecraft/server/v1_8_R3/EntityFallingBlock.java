package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class EntityFallingBlock extends Entity {
   public int ticksLived;
   public boolean dropItem = true;
   public boolean hurtEntities;
   public NBTTagCompound tileEntityData;
   public Location sourceLoc;
   private IBlockData block;
   private boolean e;
   private int fallHurtMax = 40;
   private float fallHurtAmount = 2.0F;

   public EntityFallingBlock(World world) {
      this(null, world);
   }

   public EntityFallingBlock(Location loc, World world) {
      super(world);
      this.sourceLoc = loc;
      this.loadChunks = world.paperSpigotConfig.loadUnloadedFallingBlocks;
   }

   public EntityFallingBlock(World world, double d0, double d1, double d2, IBlockData iblockdata) {
      this(null, world, d0, d1, d2, iblockdata);
   }

   public EntityFallingBlock(Location loc, World world, double d0, double d1, double d2, IBlockData iblockdata) {
      super(world);
      this.sourceLoc = loc;
      this.block = iblockdata;
      this.k = true;
      this.setSize(0.98F, 0.98F);
      this.setPosition(d0, d1, d2);
      this.motX = 0.0;
      this.motY = 0.0;
      this.motZ = 0.0;
      this.lastX = d0;
      this.lastY = d1;
      this.lastZ = d2;
      this.loadChunks = world.paperSpigotConfig.loadUnloadedFallingBlocks;
   }

   @Override
   protected boolean s_() {
      return false;
   }

   @Override
   protected void h() {
   }

   @Override
   public boolean ad() {
      return !this.dead;
   }

   @Override
   public void t_() {
      Block block = this.block.getBlock();
      if (block.getMaterial() == Material.AIR) {
         this.die();
      } else {
         this.lastX = this.locX;
         this.lastY = this.locY;
         this.lastZ = this.locZ;
         if (this.ticksLived++ == 0) {
            int x = MathHelper.floor(this.locX);
            int y = MathHelper.floor(this.locY);
            int z = MathHelper.floor(this.locZ);
            if (this.world.getType(x, y, z).getBlock() != block || CraftEventFactory.callEntityChangeBlockEvent(this, x, y, z, Blocks.AIR, 0).isCancelled()) {
               this.die();
               return;
            }

            this.world.setTypeAndData(new BlockPosition(x, y, z), Blocks.AIR.getBlockData(), 3, false, true);
         }

         this.motY -= 0.04F;
         this.move(this.motX, this.motY, this.motZ);
         if (this.world.paperSpigotConfig.fallingBlockHeightNerf != 0 && this.locY > this.world.paperSpigotConfig.fallingBlockHeightNerf
            || this.inUnloadedChunk && this.world.paperSpigotConfig.removeUnloadedFallingBlocks) {
            this.die();
            if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
               this.a(new ItemStack(block, 1, block.getDropData(this.block)), 0.0F);
            }

            return;
         }

         this.motX *= 0.98F;
         this.motY *= 0.98F;
         this.motZ *= 0.98F;
         BlockPosition blockposition = new BlockPosition(this);
         if (this.onGround) {
            this.motX *= 0.7F;
            this.motZ *= 0.7F;
            this.motY *= -0.5;
            IBlockData currentBlock = this.world.getType(blockposition);
            if (currentBlock.getBlock() != Blocks.PISTON_EXTENSION) {
               this.die();
               if (!this.e) {
                  if (this.world.a(block, blockposition, true, EnumDirection.UP, null, null)
                     && !BlockFalling.canFall(this.world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ())
                     && blockposition.getX() >= -30000000
                     && blockposition.getZ() >= -30000000
                     && blockposition.getX() < 30000000
                     && blockposition.getZ() < 30000000
                     && blockposition.getY() >= 0
                     && blockposition.getY() < 256
                     && currentBlock != this.block) {
                     if ((Boolean)BlazeConfig.fireSandBlockChangeEvent.getValue()
                        && CraftEventFactory.callEntityChangeBlockEvent(
                              this, blockposition, this.block.getBlock(), this.block.getBlock().toLegacyData(this.block)
                           )
                           .isCancelled()) {
                        return;
                     }

                     this.world.setTypeAndData(blockposition, this.block, 3, false, (Boolean)BlazeConfig.optimizeSandMovement.getValue());
                     this.respawn();
                     if (block instanceof BlockFalling) {
                        ((BlockFalling)block).a_(this.world, blockposition);
                     }

                     if (this.tileEntityData != null && block instanceof IContainer) {
                        TileEntity tileentity = this.world.getTileEntity(blockposition);
                        if (tileentity != null) {
                           NBTTagCompound nbttagcompound = new NBTTagCompound();
                           tileentity.b(nbttagcompound);

                           for (String s : this.tileEntityData.c()) {
                              NBTBase nbtbase = this.tileEntityData.get(s);
                              if (!s.equals("x") && !s.equals("y") && !s.equals("z")) {
                                 nbttagcompound.set(s, nbtbase.clone());
                              }
                           }

                           tileentity.a(nbttagcompound);
                           tileentity.update();
                        }
                     }
                  } else if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                     this.a(new ItemStack(block, 1, block.getDropData(this.block)), 0.0F);
                  }
               }
            }
         } else if (this.ticksLived > 100 && (blockposition.getY() < 1 || blockposition.getY() > 256) || this.ticksLived > 600) {
            if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
               this.a(new ItemStack(block, 1, block.getDropData(this.block)), 0.0F);
            }

            this.die();
         }
      }
   }

   @Override
   public void e(float f, float f1) {
      if (this.hurtEntities) {
         int i = MathHelper.f(f - 1.0F);
         if (i > 0) {
            boolean flag = this.block.getBlock() == Blocks.ANVIL;
            DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

            for (Entity en : this.world.getEntities(this, this.boundingBox)) {
               CraftEventFactory.entityDamage = this;
               en.damageEntity(damagesource, Math.min(MathHelper.d(i * this.fallHurtAmount), this.fallHurtMax));
               CraftEventFactory.entityDamage = null;
            }

            if (flag && this.random.nextFloat() < 0.05F + i * 0.05) {
               int j = this.block.get(BlockAnvil.DAMAGE);
               if (++j > 2) {
                  this.e = true;
               } else {
                  this.block = this.block.set(BlockAnvil.DAMAGE, j);
               }
            }
         }
      }
   }

   @Override
   public boolean merge(Entity entity) {
      return (Boolean)BlazeConfig.sandMerging.getValue()
         && entity.ticksLived == this.ticksLived + 1
         && entity.lastX == this.locX
         && entity.lastY == this.locY
         && entity.lastZ == this.locZ
         && entity.lastMotX == this.motX
         && entity.lastMotY == this.motY - 0.04F
         && entity.lastMotZ == this.motZ
         && entity.getClass() == this.getClass()
         && ((EntityFallingBlock)entity).getBlock() == this.getBlock();
   }

   @Override
   protected void respawn() {
      if (this.potential > 1) {
         int amount = this.potential;

         for (int i = 1; i < amount; i++) {
            this.setPosition(this.lastX, this.lastY, this.lastZ);
            this.motX = this.lastMotX;
            this.motY = this.lastMotY + 0.04F;
            this.motZ = this.lastMotZ;
            this.potential = 1;
            this.t_();
         }
      }
   }

   @Override
   protected void b(NBTTagCompound nbttagcompound) {
      Block block = this.block != null ? this.block.getBlock() : Blocks.AIR;
      MinecraftKey minecraftkey = Block.REGISTRY.c(block);
      nbttagcompound.setString("Block", minecraftkey == null ? "" : minecraftkey.toString());
      nbttagcompound.setByte("Data", (byte)block.toLegacyData(this.block));
      nbttagcompound.setByte("Time", (byte)this.ticksLived);
      nbttagcompound.setBoolean("DropItem", this.dropItem);
      nbttagcompound.setBoolean("HurtEntities", this.hurtEntities);
      nbttagcompound.setFloat("FallHurtAmount", this.fallHurtAmount);
      nbttagcompound.setInt("FallHurtMax", this.fallHurtMax);
      if (this.tileEntityData != null) {
         nbttagcompound.set("TileEntityData", this.tileEntityData);
      }

      if (this.sourceLoc != null) {
         nbttagcompound.setInt("SourceLoc_x", this.sourceLoc.getBlockX());
         nbttagcompound.setInt("SourceLoc_y", this.sourceLoc.getBlockY());
         nbttagcompound.setInt("SourceLoc_z", this.sourceLoc.getBlockZ());
      }
   }

   @Override
   protected void a(NBTTagCompound nbttagcompound) {
      int i = nbttagcompound.getByte("Data") & 255;
      if (nbttagcompound.hasKeyOfType("Block", 8)) {
         this.block = Block.getByName(nbttagcompound.getString("Block")).fromLegacyData(i);
      } else if (nbttagcompound.hasKeyOfType("TileID", 99)) {
         this.block = Block.getById(nbttagcompound.getInt("TileID")).fromLegacyData(i);
      } else {
         this.block = Block.getById(nbttagcompound.getByte("Tile") & 255).fromLegacyData(i);
      }

      this.ticksLived = nbttagcompound.getByte("Time") & 255;
      Block block = this.block.getBlock();
      if (nbttagcompound.hasKeyOfType("HurtEntities", 99)) {
         this.hurtEntities = nbttagcompound.getBoolean("HurtEntities");
         this.fallHurtAmount = nbttagcompound.getFloat("FallHurtAmount");
         this.fallHurtMax = nbttagcompound.getInt("FallHurtMax");
      } else if (block == Blocks.ANVIL) {
         this.hurtEntities = true;
      }

      if (nbttagcompound.hasKeyOfType("DropItem", 99)) {
         this.dropItem = nbttagcompound.getBoolean("DropItem");
      }

      if (nbttagcompound.hasKeyOfType("TileEntityData", 10)) {
         this.tileEntityData = nbttagcompound.getCompound("TileEntityData");
      }

      if (block == null || block.getMaterial() == Material.AIR) {
         this.block = Blocks.SAND.getBlockData();
      }

      if (nbttagcompound.hasKey("SourceLoc_x")) {
         int srcX = nbttagcompound.getInt("SourceLoc_x");
         int srcY = nbttagcompound.getInt("SourceLoc_y");
         int srcZ = nbttagcompound.getInt("SourceLoc_z");
         this.sourceLoc = new Location(this.world.getWorld(), srcX, srcY, srcZ);
      }
   }

   public void a(boolean flag) {
      this.hurtEntities = flag;
   }

   @Override
   public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
      super.appendEntityCrashDetails(crashreportsystemdetails);
      if (this.block != null) {
         Block block = this.block.getBlock();
         crashreportsystemdetails.a("Immitating block ID", Block.getId(block));
         crashreportsystemdetails.a("Immitating block data", block.toLegacyData(this.block));
      }
   }

   public IBlockData getBlock() {
      return this.block;
   }

   @Override
   public double f(double d0, double d1, double d2) {
      if (!this.world.paperSpigotConfig.fixCannons) {
         return super.f(d0, d1, d2);
      } else {
         double d3 = this.locX - d0;
         double d4 = this.locY + this.getHeadHeight() - d1;
         double d5 = this.locZ - d2;
         return MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
      }
   }

   @Override
   public float getHeadHeight() {
      return this.world.paperSpigotConfig.fixCannons ? this.length / 2.0F : super.getHeadHeight();
   }

   @Override
   public void move(double d0, double d1, double d2) {
      if (this.loadChunks) {
         this.loadChunks();
      }

      if (this.noclip) {
         this.a(this.boundingBox.c(d0, d1, d2));
         this.recalcPosition();
      } else {
         this.lastMotX = this.motX;
         this.lastMotY = this.motY;
         this.lastMotZ = this.motZ;
         if (this.world.movementCache.move(this)) {
            return;
         }

         try {
            this.checkBlockCollisions();
         } catch (Throwable var27) {
            CrashReport crashreport = CrashReport.a(var27, "Checking entity block collision");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");
            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
         }

         if (d0 == 0.0 && d1 == 0.0 && d2 == 0.0 && this.vehicle == null && this.passenger == null) {
            return;
         }

         if (this.H) {
            this.H = false;
            d0 *= 0.25;
            d1 *= 0.05F;
            d2 *= 0.25;
            this.motX = 0.0;
            this.motY = 0.0;
            this.motZ = 0.0;
         }

         double d6 = d0;
         double d7 = d1;
         double d8 = d2;
         AxisAlignedBB totalArea = this.boundingBox.a(d0, d1, d2);
         double xLength = totalArea.d - totalArea.a;
         double yLength = totalArea.e - totalArea.b;
         double zLength = totalArea.f - totalArea.c;
         boolean axisScan = (Boolean)BlazeConfig.optimizeTNTMovement.getValue() && xLength * yLength * zLength > 10.0;
         List<AxisAlignedBB> list = this.world.getCubes(this, axisScan ? this.boundingBox.a(0.0, d1, 0.0) : totalArea);

         for (AxisAlignedBB axisalignedbb1 : list) {
            d1 = axisalignedbb1.b(this.boundingBox, d1);
         }

         this.a(this.boundingBox.c(0.0, d1, 0.0));
         if ((Boolean)BlazeConfig.fixEastWestCannon.getValue() && Math.abs(d0) > Math.abs(d2)) {
            if (axisScan) {
               list = this.world.getCubes(this, this.boundingBox.a(0.0, 0.0, d2));
            }

            for (AxisAlignedBB axisalignedbb2 : list) {
               d2 = axisalignedbb2.c(this.boundingBox, d2);
            }

            this.a(this.boundingBox.c(0.0, 0.0, d2));
            if (axisScan) {
               list = this.world.getCubes(this, this.boundingBox.a(d0, 0.0, 0.0));
            }

            for (AxisAlignedBB axisalignedbb2 : list) {
               d0 = axisalignedbb2.a(this.boundingBox, d0);
            }

            this.a(this.boundingBox.c(d0, 0.0, 0.0));
         } else {
            if (axisScan) {
               list = this.world.getCubes(this, this.boundingBox.a(d0, 0.0, 0.0));
            }

            for (AxisAlignedBB axisalignedbb2 : list) {
               d0 = axisalignedbb2.a(this.boundingBox, d0);
            }

            this.a(this.boundingBox.c(d0, 0.0, 0.0));
            if (axisScan) {
               list = this.world.getCubes(this, this.boundingBox.a(0.0, 0.0, d2));
            }

            for (AxisAlignedBB axisalignedbb2 : list) {
               d2 = axisalignedbb2.c(this.boundingBox, d2);
            }

            this.a(this.boundingBox.c(0.0, 0.0, d2));
         }

         this.recalcPosition();
         this.positionChanged = d6 != d0 || d8 != d2;
         this.E = d7 != d1;
         this.onGround = this.E && d7 < 0.0;
         this.F = this.positionChanged || this.E;
         BlockPosition blockposition = new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.locY - 0.2F), MathHelper.floor(this.locZ));
         Block block = this.world.getType(blockposition).getBlock();
         if (block.getMaterial() == Material.AIR) {
            Block block1 = this.world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
            if (block1 instanceof BlockFence || block1 instanceof BlockCobbleWall || block1 instanceof BlockFenceGate) {
               block = block1;
               blockposition.setY(blockposition.getY() - 1);
            }
         }

         this.a(d1, this.onGround, block, blockposition);
         if (d6 != d0) {
            this.motX = 0.0;
         }

         if (d8 != d2) {
            this.motZ = 0.0;
         }

         if (d7 != d1) {
            block.a(this.world, this);
         }

         this.world.movementCache.cache(this);
      }
   }

   @Override
   public EntityTrackerEntry getEntry(int i, int j, boolean flag) {
      return new EntityTrackerCannonEntry(this, i, j, flag);
   }
}
