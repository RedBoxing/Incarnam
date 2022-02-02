package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.models.Proxy;
import fr.redboxing.incarnam.models.WorldInfo;
import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import fr.redboxing.incarnam.network.packets.Packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PacketProxiesResult extends Packet {
    private List<Proxy> proxies;
    private List<WorldInfo> worldInfos;

    public PacketProxiesResult(List<Proxy> proxies, List<WorldInfo> worldInfos) {
        super(PacketType.SERVER_MSG);
        this.proxies = proxies;
        this.worldInfos = worldInfos;
    }

    public PacketProxiesResult() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public void decode(PacketBuffer packet) {
        try {
            ByteBuffer bb = packet.toByteBuffer();

            int proxySize = bb.getInt();
            for (int i = 0; i < proxySize; ++i) {
                final Proxy proxy = Proxy.fromBuild(bb);
                this.proxies.add(proxy.getId(), proxy);
            }

            int infoSize = bb.getInt();
            for (int j = 0; j < infoSize; ++j) {
                final WorldInfo info = WorldInfo.fromBuild(bb);
                this.worldInfos.add(info.getServerId(), info);
            }

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeInt(this.proxies.size());
        for(Proxy proxy : this.proxies) {
            packet.writeBytes(proxy.build());
        }

        packet.writeInt(this.worldInfos.size());
        for(WorldInfo worldInfo : this.worldInfos) {
            packet.writeBytes(worldInfo.build());
        }

        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public List<Proxy> getProxies() {
        return proxies;
    }

    public void setProxies(List<Proxy> proxies) {
        this.proxies = proxies;
    }

    public List<WorldInfo> getWorldInfos() {
        return worldInfos;
    }

    public void setWorldInfos(List<WorldInfo> worldInfos) {
        this.worldInfos = worldInfos;
    }

    @Override
    public String toString() {
        return "{ proxies: " + proxies.toString() + ", worlds: " + worldInfos.toString() + " }";
    }
}
