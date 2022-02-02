package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import lombok.Getter;
import lombok.Setter;

public class PacketForcedDisconnectionReason extends Packet {
    @Getter
    private DisconnectReason reason;

    @Getter
    private byte reasonByte;

    public PacketForcedDisconnectionReason(DisconnectReason reason) {
        super(PacketType.SERVER_MSG);
        setReason(reason);
    }

    public PacketForcedDisconnectionReason() {
        this(DisconnectReason.CLOSED);
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.reasonByte = packet.readByte();
        this.reason = DisconnectReason.getByID(this.reasonByte);
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeByte(this.reasonByte);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public void setReason(DisconnectReason reason) {
        this.reason = reason;
        this.reasonByte = reason.getReason();
    }

    public void setReasonByte(byte reasonByte) {
        this.reasonByte = reasonByte;
        this.reason = DisconnectReason.getByID(this.reasonByte);
    }

    @Override
    public String toString() {
        return "{ reason: " + this.reason + " }";
    }

    public static enum DisconnectReason {
        SPAM(99),
        TIMEOUT(84),
        KICKED_BY_RECO(2),
        KICKED_BY_ADMIN(62),
        ACCOUNT_BANNED(60),
        ACCOUNT_BANNED2(66),
        BANNED_BY_ADMIN(18),
        ARCHITECTURE_NOT_READY(13),
        SESSION_DESTROYED(49),
        REMOTE_SERVER_DOES_NOT_ANSWER(77),
        SERVER_SHUTDOWN(95),
        INVALID_POSITION(34),
        OPEN_OFFLINE_FLEA(85),
        UNKNOWN(32),
        SERVER_ERROR(44),
        SYNCHRONISATION_ERROR(59),
        TIMED_SESSION_END(55),
        SERVER_FULL(44),
        CLOSED(-1);

        @Getter
        private final byte reason;

        DisconnectReason(int reason) {
            this.reason = (byte) reason;
        }

        public static DisconnectReason getByID(byte reason) {
            for (DisconnectReason disconnectReason : values()) {
                if (disconnectReason.getReason() == reason) {
                    return disconnectReason;
                }
            }
            return CLOSED;
        }
    }
}
