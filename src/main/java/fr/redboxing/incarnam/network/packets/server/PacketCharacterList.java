package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;

public class PacketCharacterList extends Packet {
    public PacketCharacterList() {
        super(PacketType.SERVER_MSG);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
