package fr.redboxing.incarnam.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

public class DataUtils {
    public static final String readString(ByteBuf buf) {
        byte[] str = new byte[buf.readUnsignedByte()];
        buf.readBytes(str);
        return new String(str);
    }

    public static final String readBigString(ByteBuf buf) {
        int len = buf.readUnsignedShort();
        byte[] str = new byte[len];
        buf.readBytes(str);
        return new String(str);
    }

    public static final String readLargeString(ByteBuf buf) {
        int len = buf.readInt();
        byte[] str = new byte[len];
        buf.readBytes(str);
        return new String(str);
    }

    public static final void writeString(ByteBuf buf, String s) {
        buf.writeByte(s.length());
        buf.writeBytes(s.getBytes());
    }

    public static final void writeBigString(ByteBuf buf, String s) {
        buf.writeShort(s.length());
        buf.writeBytes(s.getBytes());
    }

    public static final ByteBuf bufferFromBytes(byte[] data) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(data);
        buffer.markReaderIndex();
        buffer.markWriterIndex();
        buffer.resetReaderIndex();
        return buffer;
    }

    public static byte[] toUTF8(final String s) {
        if (s == null) {
            return PrimitiveArrays.EMPTY_BYTE_ARRAY;
        }
        try {
            return s.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return s.getBytes();
        }
    }
}
