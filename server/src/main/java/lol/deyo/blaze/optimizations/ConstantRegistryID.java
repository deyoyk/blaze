package lol.deyo.blaze.optimizations;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.RegistryID;

public class ConstantRegistryID extends RegistryID<IBlockData> {
   private final Reference2IntOpenHashMap<IBlockData> identityMap = new Reference2IntOpenHashMap(7806);
   private final IBlockData[] objectList = new IBlockData[3164];

   public void a(IBlockData key, int value) {
      this.identityMap.put(key, value);
      this.objectList[value] = key;
   }

   public int b(IBlockData key) {
      return this.identityMap.getOrDefault(key, -1);
   }

   public IBlockData a(int value) {
      return value >= 0 && value < 3164 ? this.objectList[value] : null;
   }

   public IBlockData[] getAllStates() {
      return this.objectList;
   }
}
