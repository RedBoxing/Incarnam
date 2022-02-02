package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;

public class PacketClientAdditionalCharacterSlotsUpdate extends Packet {
    private byte additionalSlots;

    public PacketClientAdditionalCharacterSlotsUpdate(byte additionalSlots) {
        super(PacketType.SERVER_MSG);
        this.additionalSlots = additionalSlots;
    }

    public PacketClientAdditionalCharacterSlotsUpdate() {
        this((byte)0);
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.additionalSlots = packet.readByte();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeByte(this.additionalSlots);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public byte getAdditionalSlots() {
        return additionalSlots;
    }

    public void setAdditionalSlots(byte additionalSlots) {
        this.additionalSlots = additionalSlots;
    }

    @Override
    public String toString() {
        return "{ additionalSlots: " + this.additionalSlots + " }";
    }
}
