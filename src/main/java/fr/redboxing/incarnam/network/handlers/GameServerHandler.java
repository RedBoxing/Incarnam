package fr.redboxing.incarnam.network.handlers;

import fr.redboxing.incarnam.Constant;
import fr.redboxing.incarnam.crypto.SHA256Hasher;
import fr.redboxing.incarnam.database.entities.Account;
import fr.redboxing.incarnam.manager.AccountManager;
import fr.redboxing.incarnam.manager.ProxyManager;
import fr.redboxing.incarnam.manager.WorldManager;
import fr.redboxing.incarnam.network.packets.client.*;
import fr.redboxing.incarnam.network.packets.server.*;
import fr.redboxing.incarnam.crypto.RSACertificateManager;
import fr.redboxing.incarnam.models.account.AccountInformations;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.UUID;

public class GameServerHandler extends PacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //ClientSession session = ctx.channel().attr(CLIENT_SESSION_ATTR).get();
        this.session.write(new PacketClientIp(BigInteger.valueOf(((InetSocketAddress) this.getSession().getAddress()).getAddress().hashCode()).toByteArray()));
    }

    @Override
    public void handle(PacketVersion packet) {
        LOGGER.info("Received version result packet: {build={}.{}.{} ({})}", packet.getMajor(), packet.getMinor(), packet.getPatch(), packet.getBuildVersion());
        boolean accepted = packet.getMajor() == Constant.VERSION_MAJOR && packet.getMinor() == Constant.VERSION_MINOR && packet.getPatch() == Constant.VERSION_PATCH && packet.getBuildVersion().equals(Constant.VERSION_BUILD);
        this.session.write(new PacketVersionResult(accepted, Constant.VERSION_MAJOR, Constant.VERSION_MINOR, Constant.VERSION_PATCH, Constant.VERSION_BUILD));
        LOGGER.info("Version result: {}", accepted ? "accepted" : "rejected");
    }

    @Override
    public void handle(PacketClientPublicKeyRequest packet) {
        this.session.write(new PacketClientPublicKey(Constant.RSA_VERIFICATION, RSACertificateManager.INSTANCE.getPublicKey()));
    }

    @Override
    public void handle(PacketClientDispatchAuthentication packet) {
        if (packet.getSalt() != Constant.RSA_VERIFICATION) {
            LOGGER.info("Received login packet with incorrect rsa verification !");
        } else {
            LOGGER.info("Received login packet: { username: {}, password: {} }", packet.getUsername(), packet.getPassword());
            Optional<Account> optionalAccount = AccountManager.INSTANCE.findByUsername(packet.getUsername());
            if(optionalAccount.isEmpty()) {
                this.session.write(new PacketAuthenticationResult(PacketAuthenticationResult.LoginResponseCode.INVALID_LOGIN, 0, null));
                return;
            }
            Account user = optionalAccount.get();
            try {
                if (SHA256Hasher.validate(packet.getPassword(), user.getPassword())) {
                    AccountInformations accountInformations = AccountManager.INSTANCE.getAccountInfos(user.getId());
                    this.session.setAccountInformations(accountInformations);
                    this.session.write(new PacketDispatchAuthenticationResult(PacketAuthenticationResult.LoginResponseCode.CORRECT_LOGIN, accountInformations));
                } else {
                    this.session.write(new PacketDispatchAuthenticationResult(PacketAuthenticationResult.LoginResponseCode.INVALID_LOGIN, null));
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
                this.session.write(new PacketDispatchAuthenticationResult(PacketAuthenticationResult.LoginResponseCode.INVALID_LOGIN, null));
            }
        }
    }

    @Override
    public void handle(PacketAuthenticationTokenRequest packet) {
        Account account = this.session.getAccount();
        if(account.getSessionId() != null) {
            this.session.write(new PacketAuthenticationTokenResult(PacketAuthenticationResult.LoginResponseCode.TOO_MANY_CONNECTIONS, null));
        } else {
            String token = UUID.randomUUID().toString();
            account.setSessionId(token);
            AccountManager.INSTANCE.saveAccount(account);
            this.session.write(new PacketAuthenticationTokenResult(PacketAuthenticationResult.LoginResponseCode.CORRECT_LOGIN, token));
        }
    }

    @Override
    public void handle(PacketAuthenticationTokenRedeem packet) {
        this.session.setAccountInformations(AccountManager.INSTANCE.getAccountInfosFromSessionId(packet.getTicket()));
        this.session.write(new PacketAuthenticationResult(PacketAuthenticationResult.LoginResponseCode.CORRECT_LOGIN, (int)session.getAccountInformations().getBanEndDate(), session.getAccountInformations()));
        this.session.write(new PacketWorldSelectionResult((byte)1));
    }

    @Override
    public void handle(PacketProxiesRequest packet) {
        this.session.write(new PacketProxiesResult(ProxyManager.INSTANCE.getProxies(), WorldManager.INSTANCE.getWorlds()));
    }
}
