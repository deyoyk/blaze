package net.minecraft.server.v1_8_R3;

import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.plugin.PluginManager;

public class BlockPressurePlateBinary extends BlockPressurePlateAbstract {
   public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
   private final BlockPressurePlateBinary.EnumMobType b;

   protected BlockPressurePlateBinary(Material material, BlockPressurePlateBinary.EnumMobType blockpressureplatebinary_enummobtype) {
      super(material);
      this.j(this.blockStateList.getBlockData().set(POWERED, Boolean.FALSE));
      this.b = blockpressureplatebinary_enummobtype;
   }

   @Override
   protected int e(IBlockData iblockdata) {
      return iblockdata.get(POWERED) ? 15 : 0;
   }

   @Override
   protected IBlockData a(IBlockData iblockdata, int i) {
      return iblockdata.set(POWERED, i > 0);
   }

   @Override
   protected int f(World world, BlockPosition blockposition) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox(blockposition);
      List<Entity> list;
      switch (BlockPressurePlateBinary.SyntheticClass_1.a[this.b.ordinal()]) {
         case 1:
            list = world.getEntities(null, axisalignedbb);
            break;
         case 2:
            list = world.a(EntityLiving.class, axisalignedbb);
            break;
         default:
            return 0;
      }

      if (!list.isEmpty()) {
         for (Entity entity : list) {
            if (this.e(world.getType(blockposition)) == 0) {
               org.bukkit.World bworld = world.getWorld();
               PluginManager manager = world.getServer().getPluginManager();
               Cancellable cancellable;
               if (entity instanceof EntityHuman) {
                  cancellable = CraftEventFactory.callPlayerInteractEvent((EntityHuman)entity, Action.PHYSICAL, blockposition, null, null);
               } else {
                  cancellable = new EntityInteractEvent(
                     entity.getBukkitEntity(), bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ())
                  );
                  manager.callEvent((EntityInteractEvent)cancellable);
               }

               if (cancellable.isCancelled()) {
                  continue;
               }
            }

            if (!entity.aI()) {
               return 15;
            }
         }
      }

      return 0;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(POWERED, i == 1);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(POWERED) ? 1 : 0;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, POWERED);
   }

   public static enum EnumMobType {
      EVERYTHING,
      MOBS;
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[BlockPressurePlateBinary.EnumMobType.values().length];

      static {
         try {
            a[BlockPressurePlateBinary.EnumMobType.EVERYTHING.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[BlockPressurePlateBinary.EnumMobType.MOBS.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
