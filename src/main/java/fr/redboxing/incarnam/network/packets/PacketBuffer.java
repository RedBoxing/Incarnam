package fr.redboxing.incarnam.network.packets;

import fr.redboxing.incarnam.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class PacketBuffer {
    private ByteBuf data;
    private int packetId;
    private HashMap<Integer, Integer> marks = new HashMap<Integer, Integer>();

    public PacketBuffer(Packet.PacketType packetType,  int opcode, int type) {
        this(Unpooled.buffer(), opcode, packetType, type);
    }

    public PacketBuffer(Packet.PacketType packetType,  int opcode) {
        this(Unpooled.buffer(), opcode, packetType, 0);
    }

    public PacketBuffer(ByteBuf buffer, int opcode, Packet.PacketType packetType) {
        this(buffer, opcode, packetType, 0);
    }

    public PacketBuffer(ByteBuf buffer, int opcode, Packet.PacketType packetType, int type) {
        this.data = buffer;
        this.packetId = opcode;

        if(data.readableBytes() > 4) {
            data.readShort();
            if(packetType == Packet.PacketType.CLIENT_MSG) {
                data.readByte();
            }

            this.packetId = data.readShort();
        } else {
            data.writeShort(0); //Size placeholder
            if(packetType == Packet.PacketType.CLIENT_MSG) {
                data.writeByte(type);
            }
            data.writeShort(packetId);
        }
    }

    public void finish() {
        data.setShort(0, data.writerIndex());
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf buffer) {
        this.data = buffer;
    }

    public ByteBuffer toByteBuffer() {
        byte[] bytes = readBytes(readableBytes());
        return ByteBuffer.wrap(bytes);
    }

    public int getPacketId() {
        return packetId;
    }

    public PacketBuffer writeByte(int b) {
        data.writeByte(b);
        return this;
    }

    public byte readByte() {
        return data.readByte();
    }

    public short readUnsignedByte() {
        return data.readUnsignedByte();
    }

    public PacketBuffer writeShort(int s) {
        data.writeShort(s);
        return this;
    }

    public short readShort() {
        return data.readShort();
    }

    public int readUnsignedShort() {
        return data.readUnsignedShort();
    }

    public PacketBuffer writeInt(int i) {
        data.writeInt(i);
        return this;
    }

    public int readInt() {
        return data.readInt();
    }

    public long readUnsignedInt() {
        return data.readUnsignedInt();
    }

    public PacketBuffer writeLong(long l) {
        data.writeLong(l);
        return this;
    }

    public long readLong() {
        return data.readLong();
    }

    public PacketBuffer writeBytes(byte[] b) {
        data.writeBytes(b);
        return this;
    }

    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        data.readBytes(bytes);
        return bytes;
    }

    public PacketBuffer writeBoolean(boolean b) {
        data.writeBoolean(b);
        return this;
    }

    public boolean readBoolean() {
        return data.readBoolean();
    }

    public PacketBuffer writeString(String s) {
        data.writeByte(s.length());
        data.writeBytes(s.getBytes());
        return this;
    }

    public String readString() {
        byte[] str = new byte[data.readUnsignedByte()];
        data.readBytes(str);
        return new String(str);
    }

    public PacketBuffer writeBigString(String s) {
        data.writeShort(s.length());
        data.writeBytes(s.getBytes());
        return this;
    }

    public String readBigString() {
        int len = data.readUnsignedShort();
        byte[] str = new byte[len];
        data.readBytes(str);
        return new String(str);
    }

    public PacketBuffer writeLargeString(String s) {
        data.writeInt(s.length());
        data.writeBytes(s.getBytes());
        return this;
    }

    public String readLargeString() {
        int len = data.readInt();
        byte[] str = new byte[len];
        data.readBytes(str);
        return new String(str);
    }

    public int readableBytes() {
        return data.readableBytes();
    }

    public int writableBytes() {
        return data.writableBytes();
    }

    public void markShort(int index) {
        writeShort(0);
        marks.put(index, data.writerIndex());
    }

    public void markByte(int index) {
        writeByte(0);
        marks.put(index, data.writerIndex());
    }

    public void markInt(int index) {
        writeInt(0);
        marks.put(index, data.writerIndex());
    }

    public void endMarkShort(int index) {
        data.setShort(marks.get(index) - 2, data.writerIndex() - marks.get(index));
    }

    public void endMarkShort(int index, int add) {
        data.setShort(marks.get(index) - 2, data.writerIndex() - marks.get(index) + add);
    }

    public void endMarkByte(int index) {
        data.setByte(marks.get(index) - 1, data.writerIndex() - marks.get(index));
    }

    public void endMarkByte(int index, int add) {
        data.setByte(marks.get(index) - 1, data.writerIndex() - marks.get(index) + add);
    }

    public void endMarkInt(int index) {
        data.setInt(marks.get(index) - 4, data.writerIndex() - marks.get(index));
    }

    public void endMarkShortAbsolute(int index) {
        data.setShort(marks.get(index) - 2, data.writerIndex());
    }

    public void endMarkByteAbsolute(int index) {
        data.setByte(marks.get(index) - 1, data.writerIndex());
    }

    public void endMarkIntAbsolute(int index) {
        data.setInt(marks.get(index) - 4, data.writerIndex());
    }

    public void endMarkShortAbsolute(int index, int add) {
        data.setShort(marks.get(index) - 2, data.writerIndex() + add);
    }

    public void endMarkByteAbsolute(int index, int add) {
        data.setByte(marks.get(index) - 1, data.writerIndex() + add);
    }

    public void endMarkIntAbsolute(int index, int add) {
        data.setInt(marks.get(index) - 4, data.writerIndex() + add);
    }

    public void printBuffer() {
        printBuffer(true);
    }

    public void printBuffer(boolean hex) {
        byte[] buf = new byte[data.writerIndex()];
        data.getBytes(0, buf);

        System.out.print("[");
        for (int i=0; i<buf.length; i++)
            System.out.print((hex ? "0x" + Integer.toHexString(buf[i] & 0xFF).toUpperCase() : String.valueOf(buf[i])) + (i == buf.length-1 ? "" : ", "));
        System.out.println("]");
    }
}
