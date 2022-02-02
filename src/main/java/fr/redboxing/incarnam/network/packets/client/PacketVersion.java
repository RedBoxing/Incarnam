package fr.redboxing.incarnam.network.packets.client;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import fr.redboxing.incarnam.network.packets.Packet;
import lombok.Getter;
import lombok.Setter;

public class PacketVersion extends Packet {
    @Getter
    @Setter
    private int major;
    @Getter
    @Setter
    private int minor;
    @Getter
    @Setter
    private int patch;
    @Getter
    @Setter
    private String buildVersion;

    public PacketVersion(int major, int minor, int patch, String buildVersion) {
        super(PacketType.CLIENT_MSG);
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.buildVersion = buildVersion;
    }

    public PacketVersion() {
        this(0, 0, 0, "");
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.major = packet.readUnsignedByte();
        this.minor = packet.readShort();
        this.patch = packet.readUnsignedByte();
        this.buildVersion = packet.readString();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeByte(major);
        packet.writeShort(minor);
        packet.writeByte(patch);
        packet.writeString(buildVersion);

        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "{ major: " + major + ", minor: " + minor + ", patch: " + patch + ", build: " + buildVersion + " }";
    }
}
