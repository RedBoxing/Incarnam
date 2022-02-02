package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;

public class PacketWorldSelectionResult extends Packet {
    private byte errorCode;

    public PacketWorldSelectionResult(byte errorCode) {
        super(PacketType.SERVER_MSG);
        this.errorCode = errorCode;
    }

    public PacketWorldSelectionResult() {
        this((byte) 0);
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.errorCode = packet.readByte();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeByte(this.errorCode);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public byte getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(byte errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "{ errorCode: " + this.errorCode + " }";
    }
}
