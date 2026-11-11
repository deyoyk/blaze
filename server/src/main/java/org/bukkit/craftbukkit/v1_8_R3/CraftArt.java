package org.bukkit.craftbukkit.v1_8_R3;

import net.minecraft.server.v1_8_R3.EntityPainting.EnumArt;

public class CraftArt {
   public static org.bukkit.Art NotchToBukkit(EnumArt art) {
      switch (art) {
         case KEBAB:
            return org.bukkit.Art.KEBAB;
         case AZTEC:
            return org.bukkit.Art.AZTEC;
         case ALBAN:
            return org.bukkit.Art.ALBAN;
         case AZTEC_2:
            return org.bukkit.Art.AZTEC2;
         case BOMB:
            return org.bukkit.Art.BOMB;
         case PLANT:
            return org.bukkit.Art.PLANT;
         case WASTELAND:
            return org.bukkit.Art.WASTELAND;
         case POOL:
            return org.bukkit.Art.POOL;
         case COURBET:
            return org.bukkit.Art.COURBET;
         case SEA:
            return org.bukkit.Art.SEA;
         case SUNSET:
            return org.bukkit.Art.SUNSET;
         case CREEBET:
            return org.bukkit.Art.CREEBET;
         case WANDERER:
            return org.bukkit.Art.WANDERER;
         case GRAHAM:
            return org.bukkit.Art.GRAHAM;
         case MATCH:
            return org.bukkit.Art.MATCH;
         case BUST:
            return org.bukkit.Art.BUST;
         case STAGE:
            return org.bukkit.Art.STAGE;
         case VOID:
            return org.bukkit.Art.VOID;
         case SKULL_AND_ROSES:
            return org.bukkit.Art.SKULL_AND_ROSES;
         case WITHER:
            return org.bukkit.Art.WITHER;
         case FIGHTERS:
            return org.bukkit.Art.FIGHTERS;
         case POINTER:
            return org.bukkit.Art.POINTER;
         case PIGSCENE:
            return org.bukkit.Art.PIGSCENE;
         case BURNING_SKULL:
            return org.bukkit.Art.BURNINGSKULL;
         case SKELETON:
            return org.bukkit.Art.SKELETON;
         case DONKEY_KONG:
            return org.bukkit.Art.DONKEYKONG;
         default:
            throw new AssertionError(art);
      }
   }

   public static EnumArt BukkitToNotch(org.bukkit.Art art) {
      switch (art) {
         case KEBAB:
            return EnumArt.KEBAB;
         case AZTEC:
            return EnumArt.AZTEC;
         case ALBAN:
            return EnumArt.ALBAN;
         case AZTEC2:
            return EnumArt.AZTEC_2;
         case BOMB:
            return EnumArt.BOMB;
         case PLANT:
            return EnumArt.PLANT;
         case WASTELAND:
            return EnumArt.WASTELAND;
         case POOL:
            return EnumArt.POOL;
         case COURBET:
            return EnumArt.COURBET;
         case SEA:
            return EnumArt.SEA;
         case SUNSET:
            return EnumArt.SUNSET;
         case CREEBET:
            return EnumArt.CREEBET;
         case WANDERER:
            return EnumArt.WANDERER;
         case GRAHAM:
            return EnumArt.GRAHAM;
         case MATCH:
            return EnumArt.MATCH;
         case BUST:
            return EnumArt.BUST;
         case STAGE:
            return EnumArt.STAGE;
         case VOID:
            return EnumArt.VOID;
         case SKULL_AND_ROSES:
            return EnumArt.SKULL_AND_ROSES;
         case WITHER:
            return EnumArt.WITHER;
         case FIGHTERS:
            return EnumArt.FIGHTERS;
         case POINTER:
            return EnumArt.POINTER;
         case PIGSCENE:
            return EnumArt.PIGSCENE;
         case BURNINGSKULL:
            return EnumArt.BURNING_SKULL;
         case SKELETON:
            return EnumArt.SKELETON;
         case DONKEYKONG:
            return EnumArt.DONKEY_KONG;
         default:
            throw new AssertionError(art);
      }
   }
}
