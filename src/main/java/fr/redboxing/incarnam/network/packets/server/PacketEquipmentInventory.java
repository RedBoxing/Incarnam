package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;

public class PacketEquipmentInventory extends Packet {
    public PacketEquipmentInventory() {
        super(PacketType.SERVER_MSG);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
