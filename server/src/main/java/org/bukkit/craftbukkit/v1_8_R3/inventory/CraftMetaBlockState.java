package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityBanner;
import net.minecraft.server.v1_8_R3.TileEntityBeacon;
import net.minecraft.server.v1_8_R3.TileEntityBrewingStand;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import net.minecraft.server.v1_8_R3.TileEntityCommand;
import net.minecraft.server.v1_8_R3.TileEntityDispenser;
import net.minecraft.server.v1_8_R3.TileEntityDropper;
import net.minecraft.server.v1_8_R3.TileEntityFurnace;
import net.minecraft.server.v1_8_R3.TileEntityHopper;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;
import net.minecraft.server.v1_8_R3.TileEntityNote;
import net.minecraft.server.v1_8_R3.TileEntitySign;
import net.minecraft.server.v1_8_R3.TileEntitySkull;
import net.minecraft.server.v1_8_R3.BlockJukeBox.TileEntityRecordPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBanner;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBeacon;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBrewingStand;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftChest;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCommandBlock;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftDispenser;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftDropper;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftFurnace;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftHopper;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftJukebox;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftNoteBlock;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSkull;
import org.bukkit.inventory.meta.BlockStateMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBlockState extends CraftMetaItem implements BlockStateMeta {
   static final CraftMetaItem.ItemMetaKey BLOCK_ENTITY_TAG = new CraftMetaItem.ItemMetaKey("BlockEntityTag");
   final org.bukkit.Material material;
   NBTTagCompound blockEntityTag;

   CraftMetaBlockState(CraftMetaItem meta, org.bukkit.Material material) {
      super(meta);
      this.material = material;
      if (meta instanceof CraftMetaBlockState
         && ((CraftMetaBlockState)meta).material == material
         && material != org.bukkit.Material.SIGN
         && material != org.bukkit.Material.COMMAND) {
         CraftMetaBlockState te = (CraftMetaBlockState)meta;
         this.blockEntityTag = te.blockEntityTag;
      } else {
         this.blockEntityTag = null;
      }
   }

   CraftMetaBlockState(NBTTagCompound tag, org.bukkit.Material material) {
      super(tag);
      this.material = material;
      if (tag.hasKeyOfType(BLOCK_ENTITY_TAG.NBT, 10)) {
         this.blockEntityTag = tag.getCompound(BLOCK_ENTITY_TAG.NBT);
      } else {
         this.blockEntityTag = null;
      }
   }

   CraftMetaBlockState(Map<String, Object> map) {
      super(map);
      String matName = SerializableMeta.getString(map, "blockMaterial", true);
      org.bukkit.Material m = org.bukkit.Material.getMaterial(matName);
      if (m != null) {
         this.material = m;
      } else {
         this.material = org.bukkit.Material.AIR;
      }
   }

   @Override
   void applyToItem(NBTTagCompound tag) {
      super.applyToItem(tag);
      if (this.blockEntityTag != null) {
         tag.set(BLOCK_ENTITY_TAG.NBT, this.blockEntityTag);
      }
   }

   @Override
   void deserializeInternal(NBTTagCompound tag) {
      if (tag.hasKeyOfType(BLOCK_ENTITY_TAG.NBT, 10)) {
         this.blockEntityTag = tag.getCompound(BLOCK_ENTITY_TAG.NBT);
      }
   }

   @Override
   void serializeInternal(Map<String, NBTBase> internalTags) {
      if (this.blockEntityTag != null) {
         internalTags.put(BLOCK_ENTITY_TAG.NBT, this.blockEntityTag);
      }
   }

   @Override
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      super.serialize(builder);
      builder.put("blockMaterial", this.material.name());
      return builder;
   }

   @Override
   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.blockEntityTag != null) {
         hash = 61 * hash + this.blockEntityTag.hashCode();
      }

      return original != hash ? CraftMetaBlockState.class.hashCode() ^ hash : hash;
   }

   @Override
   public boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaBlockState) {
         CraftMetaBlockState that = (CraftMetaBlockState)meta;
         return Objects.equal(this.blockEntityTag, that.blockEntityTag);
      } else {
         return true;
      }
   }

   @Override
   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaBlockState || this.blockEntityTag == null);
   }

   @Override
   boolean isEmpty() {
      return super.isEmpty() && this.blockEntityTag == null;
   }

   @Override
   boolean applicableTo(org.bukkit.Material type) {
      switch (type) {
         case DISPENSER:
         case NOTE_BLOCK:
         case PISTON_BASE:
         case MOB_SPAWNER:
         case CHEST:
         case FURNACE:
         case JUKEBOX:
         case ENCHANTMENT_TABLE:
         case COMMAND:
         case BEACON:
         case TRAPPED_CHEST:
         case DAYLIGHT_DETECTOR:
         case HOPPER:
         case DROPPER:
         case DAYLIGHT_DETECTOR_INVERTED:
         case SIGN:
         case BREWING_STAND_ITEM:
         case FLOWER_POT_ITEM:
         case REDSTONE_COMPARATOR:
            return true;
         default:
            return false;
      }
   }

   @Override
   public boolean hasBlockState() {
      return this.blockEntityTag != null;
   }

   @Override
   public BlockState getBlockState() {
      TileEntity te = this.blockEntityTag == null ? null : TileEntity.c(this.blockEntityTag);
      switch (this.material) {
         case DISPENSER:
            if (te == null) {
               te = new TileEntityDispenser();
            }

            return new CraftDispenser(this.material, (TileEntityDispenser)te);
         case NOTE_BLOCK:
            if (te == null) {
               te = new TileEntityNote();
            }

            return new CraftNoteBlock(this.material, (TileEntityNote)te);
         case MOB_SPAWNER:
            if (te == null) {
               te = new TileEntityMobSpawner();
            }

            return new CraftCreatureSpawner(this.material, (TileEntityMobSpawner)te);
         case CHEST:
         case TRAPPED_CHEST:
            if (te == null) {
               te = new TileEntityChest();
            }

            return new CraftChest(this.material, (TileEntityChest)te);
         case FURNACE:
         case BURNING_FURNACE:
            if (te == null) {
               te = new TileEntityFurnace();
            }

            return new CraftFurnace(this.material, (TileEntityFurnace)te);
         case SIGN_POST:
         case WALL_SIGN:
         case SIGN:
            if (te == null) {
               te = new TileEntitySign();
            }

            return new CraftSign(this.material, (TileEntitySign)te);
         case JUKEBOX:
            if (te == null) {
               te = new TileEntityRecordPlayer();
            }

            return new CraftJukebox(this.material, (TileEntityRecordPlayer)te);
         case BREWING_STAND:
            if (te == null) {
               te = new TileEntityBrewingStand();
            }

            return new CraftBrewingStand(this.material, (TileEntityBrewingStand)te);
         case COMMAND:
            if (te == null) {
               te = new TileEntityCommand();
            }

            return new CraftCommandBlock(this.material, (TileEntityCommand)te);
         case BEACON:
            if (te == null) {
               te = new TileEntityBeacon();
            }

            return new CraftBeacon(this.material, (TileEntityBeacon)te);
         case SKULL:
            if (te == null) {
               te = new TileEntitySkull();
            }

            return new CraftSkull(this.material, (TileEntitySkull)te);
         case HOPPER:
            if (te == null) {
               te = new TileEntityHopper();
            }

            return new CraftHopper(this.material, (TileEntityHopper)te);
         case DROPPER:
            if (te == null) {
               te = new TileEntityDispenser();
            }

            return new CraftDropper(this.material, (TileEntityDropper)te);
         case STANDING_BANNER:
         case WALL_BANNER:
         case BANNER:
            if (te == null) {
               te = new TileEntityBanner();
            }

            return new CraftBanner(this.material, (TileEntityBanner)te);
         default:
            throw new IllegalStateException("Missing blockState for " + this.material);
      }
   }

   @Override
   public void setBlockState(BlockState blockState) {
      Validate.notNull(blockState, "blockState must not be null");
      TileEntity te = ((CraftBlockState)blockState).getTileEntity();
      Validate.notNull(te, "Invalid blockState");
      boolean valid;
      switch (this.material) {
         case DISPENSER:
            valid = te instanceof TileEntityDispenser;
            break;
         case NOTE_BLOCK:
            valid = te instanceof TileEntityNote;
            break;
         case MOB_SPAWNER:
            valid = te instanceof TileEntityMobSpawner;
            break;
         case CHEST:
         case TRAPPED_CHEST:
            valid = te instanceof TileEntityChest;
            break;
         case FURNACE:
         case BURNING_FURNACE:
            valid = te instanceof TileEntityFurnace;
            break;
         case SIGN_POST:
         case WALL_SIGN:
         case SIGN:
            valid = te instanceof TileEntitySign;
            break;
         case JUKEBOX:
            valid = te instanceof TileEntityRecordPlayer;
            break;
         case BREWING_STAND:
            valid = te instanceof TileEntityBrewingStand;
            break;
         case COMMAND:
            valid = te instanceof TileEntityCommand;
            break;
         case BEACON:
            valid = te instanceof TileEntityBeacon;
            break;
         case SKULL:
            valid = te instanceof TileEntitySkull;
            break;
         case HOPPER:
            valid = te instanceof TileEntityHopper;
            break;
         case DROPPER:
            valid = te instanceof TileEntityDropper;
            break;
         case STANDING_BANNER:
         case WALL_BANNER:
         case BANNER:
            valid = te instanceof TileEntityBanner;
            break;
         default:
            valid = false;
      }

      Validate.isTrue(valid, "Invalid blockState for " + this.material);
      this.blockEntityTag = new NBTTagCompound();
      te.b(this.blockEntityTag);
   }
}
