package net.minecraft.server.v1_8_R3;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class BlockTNT extends Block {
   public static final BlockStateBoolean EXPLODE = BlockStateBoolean.of("explode");

   public BlockTNT() {
      super(Material.TNT);
      this.j(this.blockStateList.getBlockData().set(EXPLODE, false));
      this.a(CreativeModeTab.d);
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.onPlace(world, blockposition, iblockdata);
      if (world.isBlockIndirectlyPowered(blockposition)) {
         this.postBreak(world, blockposition, iblockdata.set(EXPLODE, true));
         world.setAir(blockposition);
      }
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      if (world.isBlockIndirectlyPowered(blockposition)) {
         this.postBreak(world, blockposition, iblockdata.set(EXPLODE, true));
         world.setAir(blockposition);
      }
   }

   @Override
   public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
      Location loc = explosion.source instanceof EntityTNTPrimed
         ? ((EntityTNTPrimed)explosion.source).sourceLoc
         : new Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ());
      double y = blockposition.getY();
      if (!world.paperSpigotConfig.fixCannons) {
         y += 0.5;
      }

      EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(loc, world, blockposition.getX() + 0.5F, y, blockposition.getZ() + 0.5F, explosion.getSource());
      entitytntprimed.fuseTicks = world.random.nextInt(entitytntprimed.fuseTicks / 4) + entitytntprimed.fuseTicks / 8;
      world.addEntity(entitytntprimed);
   }

   @Override
   public void postBreak(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.a(world, blockposition, iblockdata, (EntityLiving)null);
   }

   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving) {
      if (iblockdata.get(EXPLODE)) {
         Location loc = new Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ());
         double y = blockposition.getY();
         if (!world.paperSpigotConfig.fixCannons) {
            y += 0.5;
         }

         EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(loc, world, blockposition.getX() + 0.5F, y, blockposition.getZ() + 0.5F, entityliving);
         world.addEntity(entitytntprimed);
         world.makeSound(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
      }
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (entityhuman.bZ() != null) {
         Item item = entityhuman.bZ().getItem();
         if (item == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE) {
            this.a(world, blockposition, iblockdata.set(EXPLODE, Boolean.TRUE), (EntityLiving)entityhuman);
            world.setAir(blockposition);
            if (item == Items.FLINT_AND_STEEL) {
               entityhuman.bZ().damage(1, entityhuman);
            } else if (!entityhuman.abilities.canInstantlyBuild) {
               entityhuman.bZ().count--;
            }

            return true;
         }
      }

      return super.interact(world, blockposition, iblockdata, entityhuman, enumdirection, f, f1, f2);
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
      if (entity instanceof EntityArrow) {
         EntityArrow entityarrow = (EntityArrow)entity;
         if (entityarrow.isBurning()) {
            if (CraftEventFactory.callEntityChangeBlockEvent(entityarrow, blockposition.getX(), blockposition.getY(), blockposition.getZ(), Blocks.AIR, 0)
               .isCancelled()) {
               return;
            }

            this.a(
               world,
               blockposition,
               world.getType(blockposition).set(EXPLODE, Boolean.TRUE),
               entityarrow.shooter instanceof EntityLiving ? (EntityLiving)entityarrow.shooter : null
            );
            world.setAir(blockposition);
         }
      }
   }

   @Override
   public boolean a(Explosion explosion) {
      return false;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(EXPLODE, (i & 1) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(EXPLODE) ? 1 : 0;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, EXPLODE);
   }
}
