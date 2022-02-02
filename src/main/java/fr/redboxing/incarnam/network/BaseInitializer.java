package fr.redboxing.incarnam.network;

import fr.redboxing.incarnam.network.codec.PacketDecoder;
import fr.redboxing.incarnam.network.codec.PacketEncoder;
import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.utils.SSLUtils;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.ssl.SslHandler;

import java.net.InetSocketAddress;

public class BaseInitializer extends ChannelInitializer<SocketChannel> {
    private final PacketHandler handler;
    private final Packet.PacketType packetType;

    public BaseInitializer(PacketHandler handler, Packet.PacketType packetType) {
        this.handler = handler;
        this.packetType = packetType;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pl = ch.pipeline();

        SslHandler sslHandler = (this.packetType == Packet.PacketType.SERVER_MSG ? SSLUtils.generateSelfSignedCertificateForClient() : SSLUtils.generateSelfSignedCertificateForServer()).newHandler(ch.alloc());

        pl.addLast(sslHandler);
        pl.addLast("decoder", new PacketDecoder(this.packetType, this.handler));
        pl.addLast("encoder", new PacketEncoder());
        pl.addLast("handler", this.handler);
    }
}
