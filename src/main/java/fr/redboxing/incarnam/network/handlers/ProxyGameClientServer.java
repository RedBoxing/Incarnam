package fr.redboxing.incarnam.network.handlers;

import fr.redboxing.incarnam.network.packets.client.*;
import fr.redboxing.incarnam.session.ClientSession;
import fr.redboxing.incarnam.network.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyGameClientServer extends PacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyGameClientServer.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) {
        super.channelRead0(ctx, msg);
        //ClientSession session = ctx.channel().attr(CLIENT_SESSION_ATTR).get();
        this.getSession().writeToServer(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
       //ClientSession session = ctx.channel().attr(CLIENT_SESSION_ATTR).get();
        this.getSession().connectToWakfuServer("pandora.platforms.wakfu.com", 5556);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //ClientSession session = ctx.channel().attr(CLIENT_SESSION_ATTR).get();
        this.getSession().disconnectFromServer();
    }

    @Override
    public void handle(PacketVersion packet) {
        LOGGER.info("Received version packet ({}.{}.{} (build {}))", packet.getMajor(), packet.getMinor(), packet.getPatch(), packet.getBuildVersion());
    }

    @Override
    public void handle(PacketClientPublicKeyRequest packet) {
        LOGGER.info("Received RSA request packet");
    }

    @Override
    public void handle(PacketClientDispatchAuthentication packet) {
        LOGGER.info("Received authentication packet (username: {}, password: {})", packet.getUsername(), packet.getPassword());
    }

    @Override
    public void handle(PacketAuthenticationTokenRequest packet) {
        LOGGER.info("Received authentication token request packet");
    }

    @Override
    public void handle(PacketProxiesRequest packet) {
        LOGGER.info("Received proxies request packet");
    }
}
