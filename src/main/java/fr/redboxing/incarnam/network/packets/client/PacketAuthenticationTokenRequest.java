package fr.redboxing.incarnam.network.packets.client;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import fr.redboxing.incarnam.network.packets.Packet;

public class PacketAuthenticationTokenRequest extends Packet {
    private int server_id;
    private long account_id;

    public PacketAuthenticationTokenRequest(int server_id, long account_id) {
        super(PacketType.CLIENT_MSG, 8);
        this.server_id = server_id;
        this.account_id = account_id;
    }

    public PacketAuthenticationTokenRequest() {
        this(0, 0);
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.server_id = packet.readInt();
        this.account_id = packet.readLong();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeInt(this.server_id);
        packet.writeLong(this.account_id);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    @Override
    public String toString() {
        return "{ server_id: " + this.server_id + ", account_id: " + this.account_id + " }";
    }
}
