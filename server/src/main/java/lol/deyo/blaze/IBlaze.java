package lol.deyo.blaze;

import lol.deyo.blaze.api.knockback.IKnockbackManager;
import lol.deyo.blaze.api.pearls.PearlHandler;
import lol.deyo.blaze.api.protocol.MovementHandler;
import lol.deyo.blaze.api.protocol.PacketHandler;
import java.util.List;

public interface IBlaze {
   void addMovementHandler(MovementHandler var1);

   void addPacketHandler(PacketHandler var1);

   void addPearlHandler(PearlHandler var1);

   List<MovementHandler> getMovementHandlers();

   List<PacketHandler> getPacketHandlers();

   List<PearlHandler> getPearlHandlers();

   IKnockbackManager getKnockbackManager();
}
