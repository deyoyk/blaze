package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;

public abstract class TileEntity {
   private static final Logger a = LogManager.getLogger();
   private static final Map<String, Class<? extends TileEntity>> f = Maps.newHashMap();
   private static final Map<Class<? extends TileEntity>, String> g = Maps.newHashMap();
   protected World world;
   public BlockPosition position = BlockPosition.ZERO;
   protected boolean d;
   private int h = -1;
   protected Block e;

   static {
      a(TileEntityFurnace.class, "Furnace");
      a(TileEntityChest.class, "Chest");
      a(TileEntityEnderChest.class, "EnderChest");
      a(BlockJukeBox.TileEntityRecordPlayer.class, "RecordPlayer");
      a(TileEntityDispenser.class, "Trap");
      a(TileEntityDropper.class, "Dropper");
      a(TileEntitySign.class, "Sign");
      a(TileEntityMobSpawner.class, "MobSpawner");
      a(TileEntityNote.class, "Music");
      a(TileEntityPiston.class, "Piston");
      a(TileEntityBrewingStand.class, "Cauldron");
      a(TileEntityEnchantTable.class, "EnchantTable");
      a(TileEntityEnderPortal.class, "Airportal");
      a(TileEntityCommand.class, "Control");
      a(TileEntityBeacon.class, "Beacon");
      a(TileEntitySkull.class, "Skull");
      a(TileEntityLightDetector.class, "DLDetector");
      a(TileEntityHopper.class, "Hopper");
      a(TileEntityComparator.class, "Comparator");
      a(TileEntityFlowerPot.class, "FlowerPot");
      a(TileEntityBanner.class, "Banner");
   }

   private static void a(Class<? extends TileEntity> oclass, String s) {
      if (f.containsKey(s)) {
         throw new IllegalArgumentException("Duplicate id: " + s);
      } else {
         f.put(s, oclass);
         g.put(oclass, s);
      }
   }

   public World getWorld() {
      return this.world;
   }

   public void a(World world) {
      this.world = world;
   }

   public boolean t() {
      return this.world != null;
   }

   public void a(NBTTagCompound nbttagcompound) {
      this.position = new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));
   }

   public void b(NBTTagCompound nbttagcompound) {
      String s = g.get(this.getClass());
      if (s == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         nbttagcompound.setString("id", s);
         nbttagcompound.setInt("x", this.position.getX());
         nbttagcompound.setInt("y", this.position.getY());
         nbttagcompound.setInt("z", this.position.getZ());
      }
   }

   public static TileEntity c(NBTTagCompound nbttagcompound) {
      TileEntity tileentity = null;

      try {
         Class<? extends TileEntity> oclass = f.get(nbttagcompound.getString("id"));
         if (oclass != null) {
            tileentity = oclass.newInstance();
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      if (tileentity != null) {
         tileentity.a(nbttagcompound);
      } else {
         a.warn("Skipping BlockEntity with id {}", new Object[]{nbttagcompound.getString("id")});
      }

      return tileentity;
   }

   public int u() {
      if (this.h == -1) {
         IBlockData iblockdata = this.world.getType(this.position);
         this.h = iblockdata.getBlock().toLegacyData(iblockdata);
      }

      return this.h;
   }

   public void update() {
      if (this.world != null) {
         IBlockData iblockdata = this.world.getType(this.position);
         Block block = iblockdata.getBlock();
         this.h = block.toLegacyData(iblockdata);
         this.world.b(this.position, this);
         if (this.w(block) != Blocks.AIR) {
            this.world.updateAdjacentComparators(this.position, this.w(block));
         }
      }
   }

   public BlockPosition getPosition() {
      return this.position;
   }

   public Block w() {
      if (this.e == null) {
         this.e = this.world.getType(this.position).getBlock();
      }

      return this.e;
   }

   public Block w(Block block) {
      if (this.e == null) {
         this.e = block;
      }

      return this.e;
   }

   public Packet<?> getUpdatePacket() {
      return null;
   }

   public boolean x() {
      return this.d;
   }

   public void y() {
      this.d = true;
   }

   public void D() {
      this.d = false;
   }

   public boolean c(int i, int j) {
      return false;
   }

   public void E() {
      this.e = null;
      this.h = -1;
   }

   public void a(CrashReportSystemDetails crashreportsystemdetails) {
      crashreportsystemdetails.a("Name", () -> g.get(this.getClass()) + " // " + this.getClass().getCanonicalName());
      if (this.world != null) {
         Block block = this.w();
         if (block != null) {
            CrashReportSystemDetails.a(crashreportsystemdetails, this.position, this.w(), this.u());
         }

         crashreportsystemdetails.a("Actual block type", () -> {
            int i = Block.getId(this.world.getType(this.position).getBlock());

            try {
               return String.format("ID #%d (%s // %s)", i, Block.getById(i).a(), Block.getById(i).getClass().getCanonicalName());
            } catch (Throwable var3) {
               return "ID #" + i;
            }
         });
         crashreportsystemdetails.a("Actual block data value", () -> {
            IBlockData iblockdata = this.world.getType(this.position);
            int i = iblockdata.getBlock().toLegacyData(iblockdata);
            if (i < 0) {
               return "Unknown? (Got " + i + ")";
            } else {
               String s = String.format("%4s", Integer.toBinaryString(i)).replace(" ", "0");
               return String.format("%1$d / 0x%1$X / 0b%2$s", i, s);
            }
         });
      }
   }

   public void a(BlockPosition blockposition) {
      this.position = blockposition;
   }

   public boolean F() {
      return false;
   }

   public InventoryHolder getOwner() {
      if (this.world == null) {
         return null;
      } else {
         org.bukkit.block.Block block = this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ());
         if (block == null) {
            Bukkit.getLogger()
               .log(
                  Level.WARNING,
                  "No block for owner at %s %d %d %d",
                  new Object[]{this.world.getWorld(), this.position.getX(), this.position.getY(), this.position.getZ()}
               );
            return null;
         } else {
            org.bukkit.block.BlockState state = block.getState();
            return state instanceof InventoryHolder ? (InventoryHolder)state : null;
         }
      }
   }
}
