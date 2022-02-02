package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;

public class PacketVersionResult extends Packet {
    private boolean accepted;
    private int major;
    private int minor;
    private int patch;
    private String buildVersion;

    public PacketVersionResult(boolean accepted, int major, int minor, int patch, String buildVersion) {
        super(PacketType.SERVER_MSG);
        this.accepted = accepted;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.buildVersion = buildVersion;
    }

    public PacketVersionResult() {
        this(false, 0, 0, 0, "");
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.accepted = packet.readUnsignedByte() == 1;
        this.major = packet.readUnsignedByte();
        this.minor = packet.readUnsignedShort();
        this.patch = packet.readUnsignedByte();
        this.buildVersion = packet.readString();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeByte(accepted ? 1 : 0);
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

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getPatch() {
        return patch;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    @Override
    public String toString() {
        return "{ accepted: " + accepted + ", major: " + major + ", minor: " + minor + ", patch: " + patch + ", build: " + buildVersion + " }";
    }
}
