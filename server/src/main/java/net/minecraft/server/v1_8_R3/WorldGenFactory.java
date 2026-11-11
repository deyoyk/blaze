package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFactory {
   private static final Logger a = LogManager.getLogger();
   private static final Map<String, Class<? extends StructureStart>> b = Maps.newHashMap();
   private static final Map<Class<? extends StructureStart>, String> c = Maps.newHashMap();
   private static final Map<String, Class<? extends StructurePiece>> d = Maps.newHashMap();
   private static final Map<Class<? extends StructurePiece>, String> e = Maps.newHashMap();

   static {
      b(WorldGenMineshaftStart.class, "Mineshaft");
      b(WorldGenVillage.WorldGenVillageStart.class, "Village");
      b(WorldGenNether.WorldGenNetherStart.class, "Fortress");
      b(WorldGenStronghold.WorldGenStronghold2Start.class, "Stronghold");
      b(WorldGenLargeFeature.WorldGenLargeFeatureStart.class, "Temple");
      b(WorldGenMonument.WorldGenMonumentStart.class, "Monument");
      WorldGenMineshaftPieces.a();
      WorldGenVillagePieces.a();
      WorldGenNetherPieces.a();
      WorldGenStrongholdPieces.a();
      WorldGenRegistration.a();
      WorldGenMonumentPieces.a();
   }

   private static void b(Class<? extends StructureStart> oclass, String s) {
      b.put(s, oclass);
      c.put(oclass, s);
   }

   static void a(Class<? extends StructurePiece> oclass, String s) {
      d.put(s, oclass);
      e.put(oclass, s);
   }

   public static String a(StructureStart structurestart) {
      return c.get(structurestart.getClass());
   }

   public static String a(StructurePiece structurepiece) {
      return e.get(structurepiece.getClass());
   }

   public static StructureStart a(NBTTagCompound nbttagcompound, World world) {
      StructureStart structurestart = null;

      try {
         Class<? extends StructureStart> oclass = b.get(nbttagcompound.getString("id"));
         if (oclass != null) {
            structurestart = oclass.newInstance();
         }
      } catch (Exception var4) {
         a.warn("Failed Start with id " + nbttagcompound.getString("id"));
         var4.printStackTrace();
      }

      if (structurestart != null) {
         structurestart.a(world, nbttagcompound);
      } else {
         a.warn("Skipping Structure with id " + nbttagcompound.getString("id"));
      }

      return structurestart;
   }

   public static StructurePiece b(NBTTagCompound nbttagcompound, World world) {
      StructurePiece structurepiece = null;

      try {
         Class<? extends StructurePiece> oclass = d.get(nbttagcompound.getString("id"));
         if (oclass != null) {
            structurepiece = oclass.newInstance();
         }
      } catch (Exception var4) {
         a.warn("Failed Piece with id " + nbttagcompound.getString("id"));
         var4.printStackTrace();
      }

      if (structurepiece != null) {
         structurepiece.a(world, nbttagcompound);
      } else {
         a.warn("Skipping Piece with id " + nbttagcompound.getString("id"));
      }

      return structurepiece;
   }
}
