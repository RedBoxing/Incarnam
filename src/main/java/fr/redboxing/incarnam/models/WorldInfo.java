package fr.redboxing.incarnam.models;

import fr.redboxing.incarnam.database.entities.World;
import fr.redboxing.incarnam.utils.ByteArray;
import fr.redboxing.incarnam.utils.SystemConfiguration;
import fr.redboxing.incarnam.utils.Utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class WorldInfo {
    private final int m_serverId;
    private final SystemConfiguration m_config;
    private final byte[] m_version;
    private boolean m_locked;

    public WorldInfo(final int serverId, final SystemConfiguration config, final byte[] version) {
        super();
        this.m_serverId = serverId;
        this.m_config = config;
        this.m_version = version.clone();
    }

    public static WorldInfo fromBuild(final ByteBuffer bb) {
        final int id = bb.getInt();
        final byte[] version = new byte[bb.getInt()];
        bb.get(version);;
        final byte[] dispatchData = new byte[bb.getInt()];
        bb.get(dispatchData);
        final SystemConfiguration config = new SystemConfiguration();
        config.unserialize(dispatchData);
        final boolean locked = bb.get() != 0;
        final WorldInfo info = new WorldInfo(id, config, version);
        info.m_locked = locked;
        return info;
    }

    public byte[] build() {
        final ByteArray bb = new ByteArray();
        bb.putInt(this.m_serverId);
        bb.putInt(this.m_version.length);
        bb.put(this.m_version);
        final byte[] dispatchData = this.m_config.serializeForDispatch();
        bb.putInt(dispatchData.length);
        bb.put(dispatchData);
        bb.putBoolean(this.m_locked);
        return bb.toArray();
    }

    public static WorldInfo fromEntity(World world) {
        return new WorldInfo(Math.toIntExact(world.getId()), world.getSystemConfiguration(), Utils.versionToBytes(world.getVersionMajor(), world.getVersionMinor(), world.getVersionPatch()));
    }

    public int getServerId() {
        return this.m_serverId;
    }

    public byte[] getVersion() {
        return this.m_version.clone();
    }

    public SystemConfiguration getConfig() {
        return this.m_config;
    }

    public boolean isLocked() {
        return this.m_locked;
    }

    public void setLocked(final boolean locked) {
        this.m_locked = locked;
    }

    @Override
    public String toString() {
        return "WorldInfo{m_serverId=" + this.m_serverId + ", m_config=" + this.m_config + ", m_version=" + Arrays.toString(this.m_version) + ", m_locked=" + this.m_locked + '}';
    }
}
