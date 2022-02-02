package fr.redboxing.incarnam.network.packets.client;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import fr.redboxing.incarnam.network.packets.Packet;

public class PacketAuthenticationTokenRedeem extends Packet {
    private String ticket;

    public PacketAuthenticationTokenRedeem(String ticket) {
        super(PacketType.CLIENT_MSG, 1);
        this.ticket = ticket;
    }

    public PacketAuthenticationTokenRedeem() {
        this("");
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.ticket = packet.readBigString();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeString(this.ticket);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "{ ticket: " + ticket + " }";
    }
}
