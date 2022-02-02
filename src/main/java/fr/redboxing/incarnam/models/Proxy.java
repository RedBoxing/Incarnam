package fr.redboxing.incarnam.models;

import com.google.gson.annotations.SerializedName;
import fr.redboxing.incarnam.database.entities.Server;
import fr.redboxing.incarnam.models.account.Community;
import fr.redboxing.incarnam.utils.ByteArray;
import fr.redboxing.incarnam.utils.Utils;

import java.nio.ByteBuffer;

public class Proxy {
    @SerializedName("id")
    private int m_id;
    @SerializedName("name")
    private String m_name;
    @SerializedName("community")
    private Community m_community;
    @SerializedName("servers")
    private ProxyServers servers;
    private byte m_order;

    public Proxy(int m_id, String m_name, Community m_community, ProxyServers servers, byte m_order) {
        this.m_id = m_id;
        this.m_name = m_name;
        this.m_community = m_community;
        this.servers = servers;
        this.m_order = m_order;
    }

    public Proxy() {
        this(0, "Default", Community.DEFAULT_COMMUNITY, new ProxyServers(new ProxyServer("127.0.0.1", 5556, 443)), (byte)0);
    }

    public int getId() {
        return this.m_id;
    }

    public String getName() {
        return this.m_name;
    }

    public Community getCommunity() {
        return this.m_community;
    }

    public ProxyServers getServers() {
        return this.servers;
    }

    public int getOrder() {
        return this.m_order;
    }

    public void setOrder(final byte order) {
        this.m_order = order;
    }

    public byte[] build() {
        final ByteArray bb = new ByteArray();
        bb.putInt(this.m_id);
        final byte[] utfName = Utils.toUTF8(this.m_name);
        bb.putInt(utfName.length);
        bb.put(utfName);
        bb.putInt(this.m_community.getId());
        encodeServer(bb, this.servers.getUber());
        bb.put(this.m_order);
        return bb.toArray();
    }

    private static void encodeServer(ByteArray bb, ProxyServer server) {
        final byte[] utfAddress = Utils.toUTF8(server.getAddress());
        bb.putInt(utfAddress.length);
        bb.put(utfAddress);
        bb.putInt(server.getPorts().length);
        for (int i = 0, length = server.getPorts().length; i < length; ++i) {
            final int port = server.getPorts()[i];
            bb.putInt(port);
        }
    }

    public static Proxy fromBuild(final ByteBuffer bb) {
        final int id = bb.getInt();
        final byte[] utfName = new byte[bb.getInt()];
        bb.get(utfName);
        final String name = Utils.fromUTF8(utfName);
        final Community community = Community.getFromId(bb.getInt());
        final ProxyServer server = decodeServer(bb);
        final byte order = bb.get();
        final Proxy proxy = new Proxy();
        proxy.m_id = id;
        proxy.m_name = name;
        proxy.m_community = community;
        proxy.servers = new ProxyServers(server);
        proxy.m_order = order;
        return proxy;
    }

    private static ProxyServer decodeServer(ByteBuffer bb) {
        final byte[] utfAddress = new byte[bb.getInt()];
        bb.get(utfAddress);
        final String address = Utils.fromUTF8(utfAddress);
        final int[] ports = new int[bb.getInt()];
        for (int i = 0, length = ports.length; i < length; ++i) {
            ports[i] = bb.getInt();
        }

        return new ProxyServer(address, ports);
    }

    public static Proxy fromEntity(Server server) {
         Proxy proxy = new Proxy();
        proxy.m_id = Math.toIntExact(server.getId());
        proxy.m_name = server.getName();
        proxy.m_community = server.getComunity();
        proxy.servers = new ProxyServers(new ProxyServer(server.getHost(), server.getPort()));
        proxy.m_order = (byte) proxy.m_id;
        return proxy;
    }

    @Override
    public String toString() {
        return "Proxy{m_id=" + this.m_id + ", m_name='" + this.m_name + '\'' + ", m_community=" + this.m_community + ", m_proxyAddresses='" + this.servers + ", m_order=" + this.m_order + '}';
    }
}
