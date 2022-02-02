package fr.redboxing.incarnam.session;

import fr.redboxing.incarnam.database.entities.Account;
import fr.redboxing.incarnam.manager.AccountManager;
import fr.redboxing.incarnam.models.account.AccountInformations;
import fr.redboxing.incarnam.network.BaseInitializer;
import fr.redboxing.incarnam.network.handlers.ProxyServerHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.server.PacketForcedDisconnectionReason;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class ClientSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSession.class);
    private final Channel channel;

    private final EventLoopGroup workerGroup = new NioEventLoopGroup(1);
    private Channel serverChannel;

    @Getter
    @Setter
    private AccountInformations accountInformations;

    @Getter
    @Setter
    private long salt;

    @Getter
    @Setter
    private byte[] key;

    public ClientSession(Channel channel) {
        this.channel = channel;
    }

    public void connectToWakfuServer(String host, int port) {
        try {
            final Bootstrap b = new Bootstrap();
            b.group(workerGroup).channel(NioSocketChannel.class).handler(new BaseInitializer(new ProxyServerHandler(), Packet.PacketType.SERVER_MSG));

            this.serverChannel = b.connect(host, port).sync().channel();
        }  catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void disconnectFromServer() {
        this.workerGroup.shutdownGracefully();
    }

    public void disconnect(PacketForcedDisconnectionReason.DisconnectReason reason) {
        this.write(new PacketForcedDisconnectionReason(reason));
        this.close();
    }

    public void write(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void writeToServer(Packet packet) {
        this.serverChannel.writeAndFlush(packet);
    }

    public void close() {
        this.channel.close();
    }

    public SocketAddress getAddress() {
        return this.channel.remoteAddress();
    }

    public Account getAccount() {
        return AccountManager.INSTANCE.findById(this.accountInformations.getAccountId());
    }
}
