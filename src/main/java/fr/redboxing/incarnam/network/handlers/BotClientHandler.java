package fr.redboxing.incarnam.network.handlers;

import fr.redboxing.incarnam.Constant;
import fr.redboxing.incarnam.network.packets.client.PacketClientDispatchAuthentication;
import fr.redboxing.incarnam.network.packets.client.PacketLanguage;
import fr.redboxing.incarnam.network.packets.client.PacketClientPublicKeyRequest;
import fr.redboxing.incarnam.network.packets.client.PacketVersion;
import fr.redboxing.incarnam.network.packets.server.*;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Not working
 */
public class BotClientHandler extends PacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LOGGER.info("Bot connected");
        //ClientSession session = ctx.channel().attr(CLIENT_SESSION_ATTR).get();
        this.session.write(new PacketVersion(Constant.VERSION_MAJOR, Constant.VERSION_MINOR, Constant.VERSION_PATCH, Constant.VERSION_BUILD));
        this.session.write(new PacketClientPublicKeyRequest());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOGGER.info("Bot disconnected");
    }

    @Override
    public void handle(PacketVersionResult packet) {
        if(packet.isAccepted()) {
            LOGGER.info("Server accepted bot version ({}.{}.{} (build {}))", packet.getMajor(), packet.getMinor(), packet.getPatch(), packet.getBuildVersion());
        } else {
            LOGGER.info("Server refused bot version ({}.{}.{} (build {}))", packet.getMajor(), packet.getMinor(), packet.getPatch(), packet.getBuildVersion());
        }
    }

    @Override
    public void handle(PacketClientPublicKey packet) {
        LOGGER.info("Bot received RSA key (verification: {}, key: {})", packet.getSalt(), packet.getKey());
        this.session.write(new PacketClientDispatchAuthentication("RedBoxingg", "thomas2005",  packet.getKey(), packet.getSalt()));
    }

    @Override
    public void handle(PacketClientIp packet) {
        LOGGER.info("Bot received IP: {}", packet);
    }

    @Override
    public void handle(PacketDispatchAuthenticationResult packet) {
        LOGGER.info("Bot received dispatch authentication result: {}", packet.getCode());
        LOGGER.info(packet.getAccountInformation().toString());
    }

    @Override
    public void handle(PacketAuthenticationResult packet) {
        LOGGER.info("Bot received authentication result: {}", packet.getCode());
        LOGGER.info(packet.getAccountInformations().toString());
    }

    @Override
    public void handle(PacketAuthenticationTokenResult packet) {
        LOGGER.info("Bot received authentication token result: {}", packet.getToken());
    }

    @Override
    public void handle(PacketProxiesResult packet) {
        LOGGER.info("Bot received proxies: {}", packet.getProxies());
        LOGGER.info("Bot received worlds: {}", packet.getWorldInfos());
    }

    @Override
    public void handle(PacketProxyRelayError packet) {
        LOGGER.error("Bot received proxy error : {}", packet.getReason());
    }

    @Override
    public void handle(PacketWorldSelectionResult packet) {
        LOGGER.info("Bot received world selection result: {}", packet.getErrorCode());
    }

    @Override
    public void handle(PacketClientSystemConfiguration packet) {
        LOGGER.info("Bot received system configuration: {}", packet.getConfiguration());
    }

    @Override
    public void handle(PacketClientCalendarSynchronization packet) {
        LOGGER.info("Bot received calendar synchronization: {}", packet.getM_synchronizationTime());
    }

    @Override
    public void handle(PacketClientAdditionalCharacterSlotsUpdate packet) {
        LOGGER.info("Bot received additional character slots update: {}", packet.getAdditionalSlots());
    }

    @Override
    public void handle(PacketFreeCompanionBreedId packet) {
        LOGGER.info("Bot received free companion breed id: {}", packet.getFreeCompanionBreedId());
    }

    @Override
    public void handle(PacketEquipmentInventory packet) {
        LOGGER.info("Bot received equipment inventory: ");
    }

    @Override
    public void handle(PacketCharacterList packet) {
        LOGGER.info("Bot received character list: ");
    }

    @Override
    public void handle(PacketLanguage packet) {
        LOGGER.info("Bot received language: {}", packet.getLang());
    }

    @Override
    public void handle(PacketForcedDisconnectionReason packet) {
        LOGGER.info("Bot received forced disconnection reason: {}", packet.getReason());
    }
}
