package org.bukkit.craftbukkit.v1_8_R3.inventory;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.GameProfileSerializer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntitySkull;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.SkullMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaSkull extends CraftMetaItem implements SkullMeta {
   static final CraftMetaItem.ItemMetaKey SKULL_PROFILE = new CraftMetaItem.ItemMetaKey("SkullProfile");
   static final CraftMetaItem.ItemMetaKey SKULL_OWNER = new CraftMetaItem.ItemMetaKey("SkullOwner", "skull-owner");
   static final int MAX_OWNER_LENGTH = 16;
   private GameProfile profile;

   CraftMetaSkull(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaSkull) {
         CraftMetaSkull skullMeta = (CraftMetaSkull)meta;
         this.profile = skullMeta.profile;
      }
   }

   CraftMetaSkull(NBTTagCompound tag) {
      super(tag);
      if (tag.hasKeyOfType(SKULL_OWNER.NBT, 10)) {
         this.profile = GameProfileSerializer.deserialize(tag.getCompound(SKULL_OWNER.NBT));
      } else if (tag.hasKeyOfType(SKULL_OWNER.NBT, 8) && !tag.getString(SKULL_OWNER.NBT).isEmpty()) {
         this.profile = new GameProfile(null, tag.getString(SKULL_OWNER.NBT));
      }
   }

   CraftMetaSkull(Map<String, Object> map) {
      super(map);
      if (this.profile == null) {
         this.setOwner(SerializableMeta.getString(map, SKULL_OWNER.BUKKIT, true));
      }
   }

   @Override
   void deserializeInternal(NBTTagCompound tag) {
      if (tag.hasKeyOfType(SKULL_PROFILE.NBT, 10)) {
         this.profile = GameProfileSerializer.deserialize(tag.getCompound(SKULL_PROFILE.NBT));
      }
   }

   @Override
   void serializeInternal(Map<String, NBTBase> internalTags) {
      if (this.profile != null) {
         NBTTagCompound nbtData = new NBTTagCompound();
         GameProfileSerializer.serialize(nbtData, this.profile);
         internalTags.put(SKULL_PROFILE.NBT, nbtData);
      }
   }

   @Override
   void applyToItem(NBTTagCompound tag) {
      super.applyToItem(tag);
      if (this.profile != null) {
         NBTTagCompound owner = new NBTTagCompound();
         GameProfileSerializer.serialize(owner, this.profile);
         tag.set(SKULL_OWNER.NBT, owner);
         TileEntitySkull.b(this.profile, input -> {
            NBTTagCompound owner1 = new NBTTagCompound();
            GameProfileSerializer.serialize(owner1, input);
            tag.set(SKULL_OWNER.NBT, owner1);
            return false;
         });
      }
   }

   @Override
   boolean isEmpty() {
      return super.isEmpty() && this.isSkullEmpty();
   }

   boolean isSkullEmpty() {
      return this.profile == null;
   }

   @Override
   boolean applicableTo(org.bukkit.Material type) {
      return type == org.bukkit.Material.SKULL_ITEM;
   }

   public CraftMetaSkull clone() {
      return (CraftMetaSkull)super.clone();
   }

   @Override
   public boolean hasOwner() {
      return this.profile != null && this.profile.getName() != null;
   }

   @Override
   public String getOwner() {
      return this.hasOwner() ? this.profile.getName() : null;
   }

   @Override
   public boolean setOwner(String name) {
      if (name != null && name.length() > 16) {
         return false;
      } else {
         if (name == null) {
            this.profile = null;
         } else {
            EntityPlayer player = MinecraftServer.getServer().getPlayerList().getPlayer(name);
            this.profile = player != null ? player.getProfile() : new GameProfile(null, name);
         }

         return true;
      }
   }

   @Override
   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasOwner()) {
         hash = 61 * hash + this.profile.hashCode();
      }

      return original != hash ? CraftMetaSkull.class.hashCode() ^ hash : hash;
   }

   @Override
   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaSkull)) {
         return true;
      } else {
         CraftMetaSkull that = (CraftMetaSkull)meta;
         return this.hasOwner() ? that.hasOwner() && this.profile.equals(that.profile) : !that.hasOwner();
      }
   }

   @Override
   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaSkull || this.isSkullEmpty());
   }

   @Override
   Builder<String, Object> serialize(Builder<String, Object> builder) {
      super.serialize(builder);
      return this.hasOwner() ? builder.put(SKULL_OWNER.BUKKIT, this.profile.getName()) : builder;
   }
}
