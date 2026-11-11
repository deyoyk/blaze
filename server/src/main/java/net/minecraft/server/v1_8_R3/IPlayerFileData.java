package net.minecraft.server.v1_8_R3;

public interface IPlayerFileData {
   void save(EntityHuman var1);

   NBTTagCompound load(EntityHuman var1);

   String[] getSeenPlayers();
}
