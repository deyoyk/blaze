package net.minecraft.server.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public final class GameProfileSerializer {
   public static GameProfile deserialize(NBTTagCompound nbttagcompound) {
      String s = null;
      String s1 = null;
      if (nbttagcompound.hasKeyOfType("Name", 8)) {
         s = nbttagcompound.getString("Name");
      }

      if (nbttagcompound.hasKeyOfType("Id", 8)) {
         s1 = nbttagcompound.getString("Id");
      }

      if (StringUtils.isEmpty(s) && StringUtils.isEmpty(s1)) {
         return null;
      } else {
         UUID uuid;
         try {
            uuid = FastUUID.parseUUID(s1);
         } catch (Throwable var12) {
            uuid = null;
         }

         GameProfile gameprofile = new GameProfile(uuid, s);
         if (nbttagcompound.hasKeyOfType("Properties", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Properties");

            for (String s2 : nbttagcompound1.c()) {
               NBTTagList nbttaglist = nbttagcompound1.getList(s2, 10);

               for (int i = 0; i < nbttaglist.size(); i++) {
                  NBTTagCompound nbttagcompound2 = nbttaglist.get(i);
                  String s3 = nbttagcompound2.getString("Value");
                  if (nbttagcompound2.hasKeyOfType("Signature", 8)) {
                     gameprofile.getProperties().put(s2, new Property(s2, s3, nbttagcompound2.getString("Signature")));
                  } else {
                     gameprofile.getProperties().put(s2, new Property(s2, s3));
                  }
               }
            }
         }

         return gameprofile;
      }
   }

   public static NBTTagCompound serialize(NBTTagCompound nbttagcompound, GameProfile gameprofile) {
      if (!StringUtils.isEmpty(gameprofile.getName())) {
         nbttagcompound.setString("Name", gameprofile.getName());
      }

      if (gameprofile.getId() != null) {
         nbttagcompound.setString("Id", FastUUID.toString(gameprofile.getId()));
      }

      if (!gameprofile.getProperties().isEmpty()) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();

         for (String s : gameprofile.getProperties().keySet()) {
            NBTTagList nbttaglist = new NBTTagList();

            for (Property property : gameprofile.getProperties().get(s)) {
               NBTTagCompound nbttagcompound2 = new NBTTagCompound();
               nbttagcompound2.setString("Value", property.getValue());
               if (property.hasSignature()) {
                  nbttagcompound2.setString("Signature", property.getSignature());
               }

               nbttaglist.add(nbttagcompound2);
            }

            nbttagcompound1.set(s, nbttaglist);
         }

         nbttagcompound.set("Properties", nbttagcompound1);
      }

      return nbttagcompound;
   }

   public static boolean a(NBTBase nbtbase, NBTBase nbtbase1, boolean flag) {
      if (nbtbase == nbtbase1) {
         return true;
      } else if (nbtbase == null) {
         return true;
      } else if (nbtbase1 == null) {
         return false;
      } else if (!nbtbase.getClass().equals(nbtbase1.getClass())) {
         return false;
      } else if (nbtbase instanceof NBTTagCompound) {
         NBTTagCompound nbttagcompound = (NBTTagCompound)nbtbase;
         NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbtbase1;

         for (String s : nbttagcompound.c()) {
            NBTBase nbtbase2 = nbttagcompound.get(s);
            if (!a(nbtbase2, nbttagcompound1.get(s), flag)) {
               return false;
            }
         }

         return true;
      } else if (nbtbase instanceof NBTTagList && flag) {
         NBTTagList nbttaglist = (NBTTagList)nbtbase;
         NBTTagList nbttaglist1 = (NBTTagList)nbtbase1;
         if (nbttaglist.size() == 0) {
            return nbttaglist1.size() == 0;
         } else {
            for (int i = 0; i < nbttaglist.size(); i++) {
               NBTBase nbtbase3 = nbttaglist.g(i);
               boolean flag1 = false;

               for (int j = 0; j < nbttaglist1.size(); j++) {
                  if (a(nbtbase3, nbttaglist1.g(j), true)) {
                     flag1 = true;
                     break;
                  }
               }

               if (!flag1) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return nbtbase.equals(nbtbase1);
      }
   }
}
