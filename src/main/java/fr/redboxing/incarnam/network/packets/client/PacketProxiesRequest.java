package fr.redboxing.incarnam.network.packets.client;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;

public class PacketProxiesRequest extends Packet {
    public PacketProxiesRequest() {
        super(PacketType.CLIENT_MSG, 8);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "{}";
    }
}
