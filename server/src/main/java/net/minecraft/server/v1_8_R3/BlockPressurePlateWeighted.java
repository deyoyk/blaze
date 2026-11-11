package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;

public class BlockPressurePlateWeighted extends BlockPressurePlateAbstract {
   public static final BlockStateInteger POWER = BlockStateInteger.of("power", 0, 15);
   private final int weight;

   protected BlockPressurePlateWeighted(Material material, int i) {
      this(material, i, material.r());
   }

   protected BlockPressurePlateWeighted(Material material, int i, MaterialMapColor materialmapcolor) {
      super(material, materialmapcolor);
      this.j(this.blockStateList.getBlockData().set(POWER, 0));
      this.weight = i;
   }

   @Override
   protected int f(World world, BlockPosition blockposition) {
      int i = 0;

      for (Entity entity : world.a(Entity.class, this.getBoundingBox(blockposition))) {
         Cancellable cancellable;
         if (entity instanceof EntityHuman) {
            cancellable = CraftEventFactory.callPlayerInteractEvent((EntityHuman)entity, Action.PHYSICAL, blockposition, null, null);
         } else {
            cancellable = new EntityInteractEvent(
               entity.getBukkitEntity(), world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ())
            );
            world.getServer().getPluginManager().callEvent((EntityInteractEvent)cancellable);
         }

         if (!cancellable.isCancelled()) {
            i++;
         }
      }

      i = Math.min(i, this.weight);
      if (i > 0) {
         float f = (float)i / this.weight;
         return MathHelper.f(f * 15.0F);
      } else {
         return 0;
      }
   }

   @Override
   protected int e(IBlockData iblockdata) {
      return iblockdata.get(POWER);
   }

   @Override
   protected IBlockData a(IBlockData iblockdata, int i) {
      return iblockdata.set(POWER, i);
   }

   @Override
   public int a(World world) {
      return 10;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(POWER, i);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(POWER);
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, POWER);
   }
}
