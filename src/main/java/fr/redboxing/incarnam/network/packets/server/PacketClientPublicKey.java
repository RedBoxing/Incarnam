package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.utils.Utils;
import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import fr.redboxing.incarnam.network.packets.Packet;
import lombok.Getter;
import lombok.Setter;


public class PacketClientPublicKey extends Packet {
    @Getter
    @Setter
    private long salt;
    @Getter
    @Setter
    private byte[] key;

    public PacketClientPublicKey(long salt, byte[] key) {
        super(PacketType.SERVER_MSG);
        this.salt = salt;
        this.key = key;
    }

    public PacketClientPublicKey() {
        this(0, null);
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.salt = packet.readLong();
        this.key = packet.readBytes(packet.readableBytes());
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeLong(this.salt);
        packet.writeBytes(this.key);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "{ salt: " + this.salt + ", key: " + Utils.toHex(this.key) + " }";
    }
}
