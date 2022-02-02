package fr.redboxing.incarnam.network.handlers;

import fr.redboxing.incarnam.Constant;
import fr.redboxing.incarnam.crypto.RSACertificateManager;
import fr.redboxing.incarnam.network.packets.client.PacketLanguage;
import fr.redboxing.incarnam.network.packets.server.*;
import fr.redboxing.incarnam.session.ClientSession;
import fr.redboxing.incarnam.network.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyServerHandler extends PacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) {
        super.channelRead0(ctx, msg);
        //ClientSession session = ctx.channel().attr(CLIENT_SESSION_ATTR).get();
        this.getSession().write(msg);
    }

    @Override
    public void handle(PacketVersionResult packet) {
        LOGGER.info("Received version result packet ({}.{}.{} (build {}), accepted={})", packet.getMajor(), packet.getMinor(), packet.getPatch(), packet.getBuildVersion(), packet.isAccepted());
    }

    @Override
    public void handle(PacketClientPublicKey packet) {
        LOGGER.info("Received RSA result packet ({})", packet.getKey());
        this.session.setSalt(packet.getSalt());
        this.session.setKey(packet.getKey());
        packet.setSalt(Constant.RSA_VERIFICATION);
        packet.setKey(RSACertificateManager.INSTANCE.getPublicKey());
    }

    @Override
    public void handle(PacketClientIp packet) {
        LOGGER.info("Received set IP packet ({})", packet.getIp());
    }

    @Override
    public void handle(PacketDispatchAuthenticationResult packet) {
        LOGGER.info("Received dispatch authentication result packet ({})", packet.getCode());
        LOGGER.info(packet.getAccountInformation().toString());
    }

    @Override
    public void handle(PacketAuthenticationResult packet) {
        LOGGER.info("Received authentication result packet ({})", packet.getCode());
    }

    @Override
    public void handle(PacketAuthenticationTokenResult packet) {
        LOGGER.info("Received authentication token result packet ({})", packet.getToken());
    }

    @Override
    public void handle(PacketProxiesResult packet) {
        LOGGER.info("Received proxies result packet ({}, {})", packet.getProxies(), packet.getWorldInfos());
        packet.getProxies().forEach(proxy -> {
            proxy.getServers().getUber().setAddress("127.0.0.1");
        });
    }

    @Override
    public void handle(PacketProxyRelayError packet) {
        LOGGER.info("Received proxy relay error packet ({})", packet.getReason());
        this.session.disconnectFromServer();
        this.session.close();
    }

    @Override
    public void handle(PacketWorldSelectionResult packet) {
        LOGGER.info("Received world selection result packet ({})", packet.getErrorCode());
    }

    @Override
    public void handle(PacketClientSystemConfiguration packet) {
        LOGGER.info("Received system configuration packet ({})", packet.getConfiguration());
    }

    @Override
    public void handle(PacketClientCalendarSynchronization packet) {
        LOGGER.info("Received calendar synchronization packet ({})", packet.getM_synchronizationTime());
    }

    @Override
    public void handle(PacketClientAdditionalCharacterSlotsUpdate packet) {
        LOGGER.info("Received additional character slots update packet ({})", packet.getAdditionalSlots());
    }

    @Override
    public void handle(PacketFreeCompanionBreedId packet) {
        LOGGER.info("Received free companion breed id packet ({})", packet.getFreeCompanionBreedId());
    }

    @Override
    public void handle(PacketEquipmentInventory packet) {
        LOGGER.info("Received equipment inventory packet ()");
    }

    @Override
    public void handle(PacketCharacterList packet) {
        LOGGER.info("Received character list packet ()");
    }

    @Override
    public void handle(PacketLanguage packet) {
        LOGGER.info("Received language packet ({})", packet.getLang());
    }

    @Override
    public void handle(PacketForcedDisconnectionReason packet) {
        LOGGER.info("Received forced disconnection reason packet ({})", packet.getReason());
        this.session.disconnectFromServer();
        this.session.close();
    }
}
