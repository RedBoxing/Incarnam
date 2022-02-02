package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;

public class PacketAuthenticationTokenResult extends Packet {
    private PacketAuthenticationResult.LoginResponseCode code;
    private String token;

    public PacketAuthenticationTokenResult(PacketAuthenticationResult.LoginResponseCode code, String token) {
        super(PacketType.SERVER_MSG);
        this.code = code;
        this.token = token;
    }

    public PacketAuthenticationTokenResult() {
        this(PacketAuthenticationResult.LoginResponseCode.INVALID_LOGIN, "");
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.code = PacketAuthenticationResult.LoginResponseCode.getByID(packet.readByte());
        this.token = packet.readLargeString();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeByte(code.getCode());
        packet.writeLargeString(token);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public PacketAuthenticationResult.LoginResponseCode getCode() {
        return code;
    }

    public void setCode(PacketAuthenticationResult.LoginResponseCode code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "{ code: " + code + ", token: " + token + " }";
    }
}
