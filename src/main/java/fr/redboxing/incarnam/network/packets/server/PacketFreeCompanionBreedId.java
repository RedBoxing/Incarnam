package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;

public class PacketFreeCompanionBreedId extends Packet {
    private short freeCompanionBreedId;

    public PacketFreeCompanionBreedId(short freeCompanionBreedId) {
        super(PacketType.SERVER_MSG);
        this.freeCompanionBreedId = freeCompanionBreedId;
    }

    public PacketFreeCompanionBreedId() {
        this((short) 0);
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.freeCompanionBreedId = packet.readShort();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeShort(this.freeCompanionBreedId);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public short getFreeCompanionBreedId() {
        return freeCompanionBreedId;
    }

    public void setFreeCompanionBreedId(short freeCompanionBreedId) {
        this.freeCompanionBreedId = freeCompanionBreedId;
    }

    @Override
    public String toString() {
        return "{ freeCompanionBreedId: " + this.freeCompanionBreedId + " }";
    }
}
