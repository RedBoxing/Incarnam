package fr.redboxing.incarnam.network.codec;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import fr.redboxing.incarnam.network.packets.Protocol;
import fr.redboxing.incarnam.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketDecoder.class);
    private final Packet.PacketType packetType;
    private final PacketHandler handler;

    public PacketDecoder(Packet.PacketType packetType, PacketHandler handler) {
        this.packetType = packetType;
        this.handler = handler;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (msg.readableBytes() > 0) {
            int size = msg.readShort();
            int type = 0;
            if(this.packetType == Packet.PacketType.CLIENT_MSG) {
                type = msg.readByte();
            }
            int opcode = msg.readShort();
            msg.resetReaderIndex();

            Packet packet = Protocol.getInstance().getPacket(opcode);
            packet.decode(new PacketBuffer(msg, opcode, this.packetType, type));
            LOGGER.info((this.packetType == Packet.PacketType.CLIENT_MSG ? "Proxy <- Client" : "Proxy <- Server") + " : " + packet.getClass().getSimpleName() + " (" + packet + ")");
            LOGGER.info("Decoded packet " + packet.getClass().getSimpleName() + " : " + Utils.bufToHex(msg));
            out.add(packet);
        }
    }
}
