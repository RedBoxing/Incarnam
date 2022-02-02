package fr.redboxing.incarnam.network.packets;

import fr.redboxing.incarnam.network.packets.client.*;
import fr.redboxing.incarnam.network.packets.server.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Protocol {
    private static final HashMap<Integer, Class<? extends Packet>> PACKETS_MAP = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(Protocol.class.getSimpleName());
    private static final Protocol INSTANCE = new Protocol();

    public Protocol() {
        register(6, PacketForcedDisconnectionReason.class);
        register(13, PacketProxyRelayError.class);
        register(37, PacketVersion.class);
        register(41, PacketVersionResult.class);
        register(514, PacketClientPublicKeyRequest.class);
        register(401, PacketClientPublicKey.class);
        register(367, PacketClientIp.class);
        register(503, PacketClientDispatchAuthentication.class);
        register(417, PacketAuthenticationResult.class);
        register(508, PacketDispatchAuthenticationResult.class);
        register(481, PacketProxiesRequest.class);
        register(425, PacketProxiesResult.class);
        register(528, PacketAuthenticationTokenRequest.class);
        register(445, PacketAuthenticationTokenResult.class);
        register(546, PacketAuthenticationTokenRedeem.class);
        register(450, PacketWorldSelectionResult.class);
        register(23669, PacketClientSystemConfiguration.class);
        register(23635, PacketClientCalendarSynchronization.class);
        register(22820, PacketFreeCompanionBreedId.class);
        register(22738, PacketClientAdditionalCharacterSlotsUpdate.class);
        register(12853, PacketEquipmentInventory.class);
    }

    private void register(int id, Class<? extends Packet> packet) {
        try {
            packet.getDeclaredConstructor().newInstance();
            PACKETS_MAP.put(id, packet);
        } catch (Exception e) {
            LOGGER.warn("Class " + packet.getSimpleName() + " does not contain a default Constructor, this might break the game :/");
        }
    }

    public int getPacketId(Packet packet) {
        for (Map.Entry<Integer, Class<? extends Packet>> entry : PACKETS_MAP.entrySet()) {
            Class<? extends Packet> clazz = entry.getValue();
            if (clazz.isInstance(packet))
                return entry.getKey();
        }

        throw new RuntimeException("Packet " + packet + " is not registered.");
    }

    public Packet getPacket(int id) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (!PACKETS_MAP.containsKey(id))
            throw new RuntimeException("Packet with id " + id + " is not registered.");
        return PACKETS_MAP.get(id).getDeclaredConstructor().newInstance();
    }

    public boolean hasPacket(int id) {
        return PACKETS_MAP.containsKey(id);
    }

    public static Protocol getInstance() {
        return INSTANCE;
    }
}
