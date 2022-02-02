package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;

public class PacketClientCalendarSynchronization extends Packet {
    private long m_synchronizationTime;

    public PacketClientCalendarSynchronization(long synchronizationTime) {
        super(PacketType.SERVER_MSG);
        this.m_synchronizationTime = synchronizationTime;
    }

    public PacketClientCalendarSynchronization() {
        this(0);
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.m_synchronizationTime = packet.readLong();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeLong(this.m_synchronizationTime);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public long getM_synchronizationTime() {
        return m_synchronizationTime;
    }

    public void setM_synchronizationTime(long m_synchronizationTime) {
        this.m_synchronizationTime = m_synchronizationTime;
    }

    @Override
    public String toString() {
        return "{}";
    }
}
