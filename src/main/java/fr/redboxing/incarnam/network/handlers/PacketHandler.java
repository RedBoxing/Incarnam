package fr.redboxing.incarnam.network.handlers;

import fr.redboxing.incarnam.network.packets.client.*;
import fr.redboxing.incarnam.network.packets.server.*;
import fr.redboxing.incarnam.session.ClientSession;
import fr.redboxing.incarnam.network.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

public class PacketHandler extends SimpleChannelInboundHandler<Packet> {
    //public final AttributeKey<ClientSession> CLIENT_SESSION_ATTR = AttributeKey.newInstance(this.getClass().getSimpleName() + "_CLIENT_SESSION" + System.currentTimeMillis() + new Random().nextInt());
    @Getter
    @Setter
    protected ClientSession session;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) {
        handlePacket(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //ctx.channel().attr(CLIENT_SESSION_ATTR).set(new ClientSession(ctx.channel()));
        this.session = new ClientSession(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //ctx.channel().attr(CLIENT_SESSION_ATTR).set(null);
    }

    public void handlePacket(Packet packet) {
        packet.handle(this);
    }

    public void handle(PacketVersion packet) {}
    public void handle(PacketVersionResult packet) {}
    public void handle(PacketClientPublicKeyRequest packet) {}
    public void handle(PacketClientPublicKey packet) {}
    public void handle(PacketClientIp packet) {}
    public void handle(PacketClientDispatchAuthentication packet) {}
    public void handle(PacketDispatchAuthenticationResult packet) {}
    public void handle(PacketAuthenticationResult packet) {}
    public void handle(PacketAuthenticationTokenRequest packet) {}
    public void handle(PacketAuthenticationTokenResult packet) {}
    public void handle(PacketAuthenticationTokenRedeem packet) {}
    public void handle(PacketProxiesRequest packet) {}
    public void handle(PacketProxiesResult packet) {}
    public void handle(PacketProxyRelayError packet) {}
    public void handle(PacketWorldSelectionResult packet) {}
    public void handle(PacketClientSystemConfiguration packet) {}
    public void handle(PacketClientCalendarSynchronization packet) {}
    public void handle(PacketClientAdditionalCharacterSlotsUpdate packet) {}
    public void handle(PacketFreeCompanionBreedId packet) {}
    public void handle(PacketEquipmentInventory packet) {}
    public void handle(PacketCharacterList packet) {}
    public void handle(PacketLanguage packet) {}
    public void handle(PacketForcedDisconnectionReason packet) {}
}
