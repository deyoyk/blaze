package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.plugin.PluginManager;

public class BlockTripwire extends Block {
   public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
   public static final BlockStateBoolean SUSPENDED = BlockStateBoolean.of("suspended");
   public static final BlockStateBoolean ATTACHED = BlockStateBoolean.of("attached");
   public static final BlockStateBoolean DISARMED = BlockStateBoolean.of("disarmed");
   public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
   public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
   public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
   public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");

   public BlockTripwire() {
      super(Material.ORIENTABLE);
      this.j(
         this.blockStateList
            .getBlockData()
            .set(POWERED, false)
            .set(SUSPENDED, false)
            .set(ATTACHED, false)
            .set(DISARMED, false)
            .set(NORTH, false)
            .set(EAST, false)
            .set(SOUTH, false)
            .set(WEST, false)
      );
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
      this.a(true);
   }

   public static boolean c(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      BlockPosition blockposition1 = blockposition.shift(enumdirection);
      IBlockData iblockdata1 = iblockaccess.getType(blockposition1);
      Block block = iblockdata1.getBlock();
      if (block == Blocks.TRIPWIRE_HOOK) {
         EnumDirection enumdirection1 = enumdirection.opposite();
         return iblockdata1.get(BlockTripwireHook.FACING) == enumdirection1;
      } else if (block == Blocks.TRIPWIRE) {
         boolean flag = iblockdata.get(SUSPENDED);
         boolean flag1 = iblockdata1.get(SUSPENDED);
         return flag == flag1;
      } else {
         return false;
      }
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      return iblockdata.set(NORTH, c(iblockaccess, blockposition, iblockdata, EnumDirection.NORTH))
         .set(EAST, c(iblockaccess, blockposition, iblockdata, EnumDirection.EAST))
         .set(SOUTH, c(iblockaccess, blockposition, iblockdata, EnumDirection.SOUTH))
         .set(WEST, c(iblockaccess, blockposition, iblockdata, EnumDirection.WEST));
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return null;
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Items.STRING;
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      boolean flag = iblockdata.get(SUSPENDED);
      boolean flag1 = !World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
      if (flag != flag1) {
         this.b(world, blockposition, iblockdata, 0);
         world.setAir(blockposition);
      }
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      IBlockData iblockdata = iblockaccess.getType(blockposition);
      boolean flag = iblockdata.get(ATTACHED);
      boolean flag1 = iblockdata.get(SUSPENDED);
      if (!flag1) {
         this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
      } else if (!flag) {
         this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      } else {
         this.a(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
      }
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      iblockdata = iblockdata.set(SUSPENDED, !World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()));
      world.setTypeAndData(blockposition, iblockdata, 3);
      this.e(world, blockposition, iblockdata);
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.e(world, blockposition, iblockdata.set(POWERED, true));
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
      if (entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
         world.setTypeAndData(blockposition, iblockdata.set(DISARMED, true), 4);
      }
   }

   private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      for (EnumDirection enumdirection : new EnumDirection[]{EnumDirection.SOUTH, EnumDirection.WEST}) {
         for (int k = 1; k < 42; k++) {
            BlockPosition blockposition1 = blockposition.shift(enumdirection, k);
            IBlockData iblockdata1 = world.getType(blockposition1);
            if (iblockdata1.getBlock() == Blocks.TRIPWIRE_HOOK) {
               if (iblockdata1.get(BlockTripwireHook.FACING) == enumdirection.opposite()) {
                  Blocks.TRIPWIRE_HOOK.a(world, blockposition1, iblockdata1, false, true, k, iblockdata);
               }
               break;
            }

            if (iblockdata1.getBlock() != Blocks.TRIPWIRE) {
               break;
            }
         }
      }
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
      if (!iblockdata.get(POWERED)) {
         this.e(world, blockposition);
      }
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if (world.getType(blockposition).get(POWERED)) {
         this.e(world, blockposition);
      }
   }

   private void e(World world, BlockPosition blockposition) {
      IBlockData iblockdata = world.getType(blockposition);
      boolean flag = iblockdata.get(POWERED);
      boolean flag1 = false;
      List<Entity> list = world.getEntities(
         null,
         new AxisAlignedBB(
            blockposition.getX() + this.minX,
            blockposition.getY() + this.minY,
            blockposition.getZ() + this.minZ,
            blockposition.getX() + this.maxX,
            blockposition.getY() + this.maxY,
            blockposition.getZ() + this.maxZ
         )
      );
      if (!list.isEmpty()) {
         for (Entity entity : list) {
            if (!entity.aI()) {
               flag1 = true;
               break;
            }
         }
      }

      if (flag != flag1 && flag1 && iblockdata.get(ATTACHED)) {
         org.bukkit.World bworld = world.getWorld();
         PluginManager manager = world.getServer().getPluginManager();
         org.bukkit.block.Block block = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
         boolean allowed = false;

         for (Entity object : list) {
            if (object != null) {
               Cancellable cancellable;
               if (object instanceof EntityHuman) {
                  cancellable = CraftEventFactory.callPlayerInteractEvent((EntityHuman)object, Action.PHYSICAL, blockposition, null, null);
               } else {
                  cancellable = new EntityInteractEvent(object.getBukkitEntity(), block);
                  manager.callEvent((EntityInteractEvent)cancellable);
               }

               if (!cancellable.isCancelled()) {
                  allowed = true;
                  break;
               }
            }
         }

         if (!allowed) {
            return;
         }
      }

      if (flag1 != flag) {
         iblockdata = iblockdata.set(POWERED, flag1);
         world.setTypeAndData(blockposition, iblockdata, 3);
         this.e(world, blockposition, iblockdata);
      }

      if (flag1) {
         world.a(blockposition, this, this.a(world));
      }
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(POWERED, (i & 1) > 0).set(SUSPENDED, (i & 2) > 0).set(ATTACHED, (i & 4) > 0).set(DISARMED, (i & 8) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      int i = 0;
      if (iblockdata.get(POWERED)) {
         i |= 1;
      }

      if (iblockdata.get(SUSPENDED)) {
         i |= 2;
      }

      if (iblockdata.get(ATTACHED)) {
         i |= 4;
      }

      if (iblockdata.get(DISARMED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, POWERED, SUSPENDED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }
}
