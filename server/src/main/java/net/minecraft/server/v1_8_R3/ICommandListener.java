package net.minecraft.server.v1_8_R3;

public interface ICommandListener {
   String getName();

   IChatBaseComponent getScoreboardDisplayName();

   void sendMessage(IChatBaseComponent var1);

   boolean a(int var1, String var2);

   BlockPosition getChunkCoordinates();

   Vec3D d();

   World getWorld();

   Entity f();

   boolean getSendCommandFeedback();

   void a(CommandObjectiveExecutor.EnumCommandResult var1, int var2);
}
