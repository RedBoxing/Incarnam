package fr.redboxing.incarnam.network.codec;

import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import fr.redboxing.incarnam.network.packets.Protocol;
import fr.redboxing.incarnam.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketEncoder.class);

    public PacketEncoder() {
        super(Packet.class);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        msg.encode(new PacketBuffer(out, Protocol.getInstance().getPacketId(msg), msg.getPacketType(), msg.getType()));
        ctx.channel().flush();

        LOGGER.info((msg.getPacketType() == Packet.PacketType.CLIENT_MSG ? "Proxy -> Server" : "Proxy -> Client") + " : " + msg.getClass().getSimpleName() + " (" + msg + ")");
        LOGGER.info("Encoded packet " + msg.getClass().getSimpleName() + " : " + Utils.bufToHex(out));
    }
}