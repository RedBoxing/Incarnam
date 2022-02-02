package fr.redboxing.incarnam.network.packets.client;

import fr.redboxing.incarnam.utils.DataUtils;
import fr.redboxing.incarnam.crypto.RSACertificateManager;
import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

public class PacketClientDispatchAuthentication extends Packet {
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private byte[] publicKey;
    @Getter
    @Setter
    private long salt;

    public PacketClientDispatchAuthentication(String username, String password, byte[] publicKey, long salt) {
        super(PacketType.CLIENT_MSG, 8);
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
        this.salt = salt;
    }

    public PacketClientDispatchAuthentication() {
        this("", "", null, 0);
    }

    @Override
    public void decode(PacketBuffer packet) {
        try {
            byte[] b = packet.readBytes(packet.readInt());
            byte[] decoded = RSACertificateManager.INSTANCE.decode(b);
            ByteBuf decbuffer = DataUtils.bufferFromBytes(decoded);

            this.salt = decbuffer.readLong();
            this.username = DataUtils.readString(decbuffer);
            this.password = DataUtils.readString(decbuffer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void encode(PacketBuffer packet) {
        try {
            /*ByteBuf buffer = Unpooled.buffer(9 + username.length() + 1 + password.length());

            buffer.writeLong(this.salt);
            buffer.writeString(encbuffer, username);
            buffer.writeString(encbuffer, password);*/

            byte[] usernameBytes = DataUtils.toUTF8(username);
            byte[] passwordBytes = DataUtils.toUTF8(password);

            ByteBuffer buffer = ByteBuffer.allocate(9 + usernameBytes.length + 1 + passwordBytes.length);
            buffer.putLong(this.salt);
            buffer.put((byte)usernameBytes.length);
            buffer.put(usernameBytes);
            buffer.put((byte)passwordBytes.length);
            buffer.put(passwordBytes);

            byte[] bytes = buffer.array();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(this.publicKey)));

            byte[] encrypted = cipher.doFinal(bytes);

            packet.writeByte(encrypted.length);
            packet.writeBytes(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "{ username: " + username + ", password: " + password + " }";
    }
}
