package fr.redboxing.incarnam.network.packets;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.session.ClientSession;
import lombok.Getter;
import lombok.Setter;

public class Packet {
    @Getter
    private PacketType packetType;
    @Getter
    private int type;

    public Packet(PacketType packetType, int type) {
        this.packetType = packetType;
        this.type = type;
    }

    public Packet(PacketType packetType) {
        this(packetType, 0);
    }

    public void decode(PacketBuffer packet) {}
    public void encode(PacketBuffer packet) {
        packet.finish();
    }
    public void handle(PacketHandler handler) {}

    public enum PacketType {
        CLIENT_MSG, SERVER_MSG
    }
}
