package net.minecraft.server.v1_8_R3;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;

public enum EnumProtocol {
   HANDSHAKING(-1) {
      {
         this.registerServerBound(PacketHandshakingInSetProtocol.class, PacketHandshakingInSetProtocol::new);
      }
   },
   PLAY(0) {
      {
         this.registerClientBound(PacketPlayOutKeepAlive.class);
         this.registerClientBound(PacketPlayOutLogin.class);
         this.registerClientBound(PacketPlayOutChat.class);
         this.registerClientBound(PacketPlayOutUpdateTime.class);
         this.registerClientBound(PacketPlayOutEntityEquipment.class);
         this.registerClientBound(PacketPlayOutSpawnPosition.class);
         this.registerClientBound(PacketPlayOutUpdateHealth.class);
         this.registerClientBound(PacketPlayOutRespawn.class);
         this.registerClientBound(PacketPlayOutPosition.class);
         this.registerClientBound(PacketPlayOutHeldItemSlot.class);
         this.registerClientBound(PacketPlayOutBed.class);
         this.registerClientBound(PacketPlayOutAnimation.class);
         this.registerClientBound(PacketPlayOutNamedEntitySpawn.class);
         this.registerClientBound(PacketPlayOutCollect.class);
         this.registerClientBound(PacketPlayOutSpawnEntity.class);
         this.registerClientBound(PacketPlayOutSpawnEntityLiving.class);
         this.registerClientBound(PacketPlayOutSpawnEntityPainting.class);
         this.registerClientBound(PacketPlayOutSpawnEntityExperienceOrb.class);
         this.registerClientBound(PacketPlayOutEntityVelocity.class);
         this.registerClientBound(PacketPlayOutEntityDestroy.class);
         this.registerClientBound(PacketPlayOutEntity.class);
         this.registerClientBound(PacketPlayOutEntity.PacketPlayOutRelEntityMove.class);
         this.registerClientBound(PacketPlayOutEntity.PacketPlayOutEntityLook.class);
         this.registerClientBound(PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook.class);
         this.registerClientBound(PacketPlayOutEntityTeleport.class);
         this.registerClientBound(PacketPlayOutEntityHeadRotation.class);
         this.registerClientBound(PacketPlayOutEntityStatus.class);
         this.registerClientBound(PacketPlayOutAttachEntity.class);
         this.registerClientBound(PacketPlayOutEntityMetadata.class);
         this.registerClientBound(PacketPlayOutEntityEffect.class);
         this.registerClientBound(PacketPlayOutRemoveEntityEffect.class);
         this.registerClientBound(PacketPlayOutExperience.class);
         this.registerClientBound(PacketPlayOutUpdateAttributes.class);
         this.registerClientBound(PacketPlayOutMapChunk.class);
         this.registerClientBound(PacketPlayOutMultiBlockChange.class);
         this.registerClientBound(PacketPlayOutBlockChange.class);
         this.registerClientBound(PacketPlayOutBlockAction.class);
         this.registerClientBound(PacketPlayOutBlockBreakAnimation.class);
         this.registerClientBound(PacketPlayOutMapChunkBulk.class);
         this.registerClientBound(PacketPlayOutExplosion.class);
         this.registerClientBound(PacketPlayOutWorldEvent.class);
         this.registerClientBound(PacketPlayOutNamedSoundEffect.class);
         this.registerClientBound(PacketPlayOutWorldParticles.class);
         this.registerClientBound(PacketPlayOutGameStateChange.class);
         this.registerClientBound(PacketPlayOutSpawnEntityWeather.class);
         this.registerClientBound(PacketPlayOutOpenWindow.class);
         this.registerClientBound(PacketPlayOutCloseWindow.class);
         this.registerClientBound(PacketPlayOutSetSlot.class);
         this.registerClientBound(PacketPlayOutWindowItems.class);
         this.registerClientBound(PacketPlayOutWindowData.class);
         this.registerClientBound(PacketPlayOutTransaction.class);
         this.registerClientBound(PacketPlayOutUpdateSign.class);
         this.registerClientBound(PacketPlayOutMap.class);
         this.registerClientBound(PacketPlayOutTileEntityData.class);
         this.registerClientBound(PacketPlayOutOpenSignEditor.class);
         this.registerClientBound(PacketPlayOutStatistic.class);
         this.registerClientBound(PacketPlayOutPlayerInfo.class);
         this.registerClientBound(PacketPlayOutAbilities.class);
         this.registerClientBound(PacketPlayOutTabComplete.class);
         this.registerClientBound(PacketPlayOutScoreboardObjective.class);
         this.registerClientBound(PacketPlayOutScoreboardScore.class);
         this.registerClientBound(PacketPlayOutScoreboardDisplayObjective.class);
         this.registerClientBound(PacketPlayOutScoreboardTeam.class);
         this.registerClientBound(PacketPlayOutCustomPayload.class);
         this.registerClientBound(PacketPlayOutKickDisconnect.class);
         this.registerClientBound(PacketPlayOutServerDifficulty.class);
         this.registerClientBound(PacketPlayOutCombatEvent.class);
         this.registerClientBound(PacketPlayOutCamera.class);
         this.registerClientBound(PacketPlayOutWorldBorder.class);
         this.registerClientBound(PacketPlayOutTitle.class);
         this.registerClientBound(PacketPlayOutSetCompression.class);
         this.registerClientBound(PacketPlayOutPlayerListHeaderFooter.class);
         this.registerClientBound(PacketPlayOutResourcePackSend.class);
         this.registerClientBound(PacketPlayOutUpdateEntityNBT.class);
         this.registerServerBound(PacketPlayInKeepAlive.class, PacketPlayInKeepAlive::new);
         this.registerServerBound(PacketPlayInChat.class, PacketPlayInChat::new);
         this.registerServerBound(PacketPlayInUseEntity.class, PacketPlayInUseEntity::new);
         this.registerServerBound(PacketPlayInFlying.class, PacketPlayInFlying::new);
         this.registerServerBound(PacketPlayInFlying.PacketPlayInPosition.class, PacketPlayInFlying.PacketPlayInPosition::new);
         this.registerServerBound(PacketPlayInFlying.PacketPlayInLook.class, PacketPlayInFlying.PacketPlayInLook::new);
         this.registerServerBound(PacketPlayInFlying.PacketPlayInPositionLook.class, PacketPlayInFlying.PacketPlayInPositionLook::new);
         this.registerServerBound(PacketPlayInBlockDig.class, PacketPlayInBlockDig::new);
         this.registerServerBound(PacketPlayInBlockPlace.class, PacketPlayInBlockPlace::new);
         this.registerServerBound(PacketPlayInHeldItemSlot.class, PacketPlayInHeldItemSlot::new);
         this.registerServerBound(PacketPlayInArmAnimation.class, PacketPlayInArmAnimation::new);
         this.registerServerBound(PacketPlayInEntityAction.class, PacketPlayInEntityAction::new);
         this.registerServerBound(PacketPlayInSteerVehicle.class, PacketPlayInSteerVehicle::new);
         this.registerServerBound(PacketPlayInCloseWindow.class, PacketPlayInCloseWindow::new);
         this.registerServerBound(PacketPlayInWindowClick.class, PacketPlayInWindowClick::new);
         this.registerServerBound(PacketPlayInTransaction.class, PacketPlayInTransaction::new);
         this.registerServerBound(PacketPlayInSetCreativeSlot.class, PacketPlayInSetCreativeSlot::new);
         this.registerServerBound(PacketPlayInEnchantItem.class, PacketPlayInEnchantItem::new);
         this.registerServerBound(PacketPlayInUpdateSign.class, PacketPlayInUpdateSign::new);
         this.registerServerBound(PacketPlayInAbilities.class, PacketPlayInAbilities::new);
         this.registerServerBound(PacketPlayInTabComplete.class, PacketPlayInTabComplete::new);
         this.registerServerBound(PacketPlayInSettings.class, PacketPlayInSettings::new);
         this.registerServerBound(PacketPlayInClientCommand.class, PacketPlayInClientCommand::new);
         this.registerServerBound(PacketPlayInCustomPayload.class, PacketPlayInCustomPayload::new);
         this.registerServerBound(PacketPlayInSpectate.class, PacketPlayInSpectate::new);
         this.registerServerBound(PacketPlayInResourcePackStatus.class, PacketPlayInResourcePackStatus::new);
      }
   },
   STATUS(1) {
      {
         this.registerServerBound(PacketStatusInStart.class, PacketStatusInStart::new);
         this.registerClientBound(PacketStatusOutServerInfo.class);
         this.registerServerBound(PacketStatusInPing.class, PacketStatusInPing::new);
         this.registerClientBound(PacketStatusOutPong.class);
      }
   },
   LOGIN(2) {
      {
         this.registerClientBound(PacketLoginOutDisconnect.class);
         this.registerClientBound(PacketLoginOutEncryptionBegin.class);
         this.registerClientBound(PacketLoginOutSuccess.class);
         this.registerClientBound(PacketLoginOutSetCompression.class);
         this.registerServerBound(PacketLoginInStart.class, PacketLoginInStart::new);
         this.registerServerBound(PacketLoginInEncryptionBegin.class, PacketLoginInEncryptionBegin::new);
      }
   };

   private static final int e = -1;
   private static final int f = 2;
   private static final EnumProtocol[] g = new EnumProtocol[4];
   private static final Map<Class<? extends Packet<?>>, EnumProtocol> h = new HashMap<>();
   private final int i;
   private final Map<EnumProtocolDirection, BiMap<Integer, Class<? extends Packet<?>>>> j = Maps.newEnumMap(EnumProtocolDirection.class);
   private final Map<Class<? extends Packet<?>>, Integer> packet2Id = new Object2IntOpenHashMap(16, 0.5F);
   private final Map<Integer, Supplier<Packet<?>>> idToPacketServerBound = new Int2ObjectOpenHashMap();

   static {
      EnumProtocol[] var3;
      for (EnumProtocol enumprotocol : var3 = values()) {
         int k = enumprotocol.a();
         if (k < -1 || k > 2) {
            throw new Error("Invalid protocol ID " + k);
         }

         g[k - -1] = enumprotocol;

         for (EnumProtocolDirection direction : enumprotocol.j.keySet()) {
            for (Class<? extends Packet<?>> oclass : enumprotocol.j.get(direction).values()) {
               if (h.containsKey(oclass) && h.get(oclass) != enumprotocol) {
                  throw new Error("Packet " + oclass + " is already assigned to protocol " + h.get(oclass) + " - can't reassign to " + enumprotocol);
               }

               try {
                  oclass.newInstance();
               } catch (Throwable var10) {
                  throw new Error("Packet " + oclass + " fails instantiation checks! " + oclass);
               }

               h.put(oclass, enumprotocol);
            }
         }
      }
   }

   private EnumProtocol(int i) {
      this.i = i;
   }

   protected void registerClientBound(Class<? extends Packet<?>> clazz) {
      this.registerMapping(EnumProtocolDirection.CLIENTBOUND, clazz);
      this.packet2Id.put(clazz, this.packet2Id.size());
   }

   protected void registerServerBound(Class<? extends Packet<?>> clazz, Supplier<Packet<?>> supplier) {
      this.registerMapping(EnumProtocolDirection.SERVERBOUND, clazz);
      this.idToPacketServerBound.put(this.idToPacketServerBound.size(), supplier);
   }

   private EnumProtocol registerMapping(EnumProtocolDirection direction, Class<? extends Packet<?>> clazz) {
      BiMap<Integer, Class<? extends Packet<?>>> object = this.j.computeIfAbsent(direction, k -> HashBiMap.create());
      if (object.containsValue(clazz)) {
         String s = direction + " packet " + clazz + " is already known to ID " + object.inverse().get(clazz);
         LogManager.getLogger().fatal(s);
         throw new IllegalArgumentException(s);
      } else {
         object.put(object.size(), clazz);
         return this;
      }
   }

   public int getClientBoundPacketId(Packet<?> packet) {
      return this.packet2Id.getOrDefault(packet.getClass(), -1);
   }

   public Packet<?> getServerBoundPacketForId(int id) {
      Supplier<Packet<?>> supplier = this.idToPacketServerBound.get(id);
      return supplier == null ? null : supplier.get();
   }

   public int getProtocolId() {
      return this.i;
   }

   public static EnumProtocol getProtocol(int i) {
      return i >= -1 && i <= 2 ? g[i - -1] : null;
   }

   public static EnumProtocol getProtocolForPacket(Packet<?> packet) {
      return h.get(packet.getClass());
   }

   public int a() {
      return this.i;
   }

   public Integer a(EnumProtocolDirection enumprotocoldirection, Packet<?> packet) {
      return (Integer)this.j.get(enumprotocoldirection).inverse().get(packet.getClass());
   }

   protected EnumProtocol a(EnumProtocolDirection enumprotocoldirection, Class<? extends Packet<?>> oclass) {
      return this.registerMapping(enumprotocoldirection, oclass);
   }

   public Packet<?> a(EnumProtocolDirection enumprotocoldirection, int i) throws IllegalAccessException, InstantiationException {
      Class<? extends Packet<?>> oclass = (Class<? extends Packet<?>>)this.j.get(enumprotocoldirection).get(i);
      return (Packet<?>)(oclass == null ? null : oclass.newInstance());
   }

   public static EnumProtocol a(int i) {
      return i >= -1 && i <= 2 ? g[i - -1] : null;
   }

   public static EnumProtocol a(Packet<?> packet) {
      return h.get(packet.getClass());
   }
}
