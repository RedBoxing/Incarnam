package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;

public class PacketClientIp extends Packet {
    @Getter
    @Setter
    private byte[] ip;

    public PacketClientIp(byte[] ip) {
        super(PacketType.SERVER_MSG);
        this.ip = ip;
    }

    public PacketClientIp() {
        this(new byte[] { 0, 0, 0, 0});
    }

    @Override
    public void decode(PacketBuffer packet) {
        try {
            this.ip = packet.readBytes(4);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeBytes(this.ip);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        String ipaddress = "0.0.0.0";
        try {
            ipaddress = InetAddress.getByName(String.valueOf(new BigInteger(ip).intValue())).getHostAddress();
        }catch (IOException ex) {
            ex.printStackTrace();
        }

        return "{ ip: " + ipaddress + " }";
    }
}
