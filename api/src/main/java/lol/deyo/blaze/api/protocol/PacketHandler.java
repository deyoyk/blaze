package lol.deyo.blaze.api.protocol;

public interface PacketHandler {
   default boolean handleReceivedPacket(Object connection, Object packet) {
      return true;
   }

   default boolean handleSentPacket(Object connection, Object packet) {
      return true;
   }
}
