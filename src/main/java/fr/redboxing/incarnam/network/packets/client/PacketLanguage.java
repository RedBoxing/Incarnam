package fr.redboxing.incarnam.network.packets.client;

import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import fr.redboxing.incarnam.network.packets.Packet;

public class PacketLanguage extends Packet {
    private String lang;

    public PacketLanguage(String lang) {
        super(PacketType.CLIENT_MSG);
    }

    public PacketLanguage() {
        this("fr");
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.lang = packet.readLargeString();
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeLargeString(lang);
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {}

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "{ lang: " + lang + " }";
    }
}
