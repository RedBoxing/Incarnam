package fr.redboxing.incarnam.network.packets.server;

import fr.redboxing.incarnam.models.account.AccountInformations;
import fr.redboxing.incarnam.models.account.LocalAccountInformations;
import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;

public class PacketAuthenticationResult extends Packet {
    private LoginResponseCode code;
    private int banDuration;
    private AccountInformations accountInformations;

    public PacketAuthenticationResult(LoginResponseCode code, int banDuration, AccountInformations accountInformations) {
        super(PacketType.SERVER_MSG);
        this.code = code;
        this.banDuration = banDuration;
        this.accountInformations = accountInformations;
    }

    public PacketAuthenticationResult() {
        this(LoginResponseCode.INVALID_LOGIN, 0, null);
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.code = LoginResponseCode.getByID(packet.readByte());

        if(this.code == LoginResponseCode.ACCOUNT_BANNED) {
            this.banDuration = packet.readInt();
        }else if(this.code == LoginResponseCode.CORRECT_LOGIN) {
            int accountInfosSize = packet.readableBytes();//packet.readByte();
            byte[] accountInfosBytes = packet.readBytes(accountInfosSize);

            accountInformations = new LocalAccountInformations();
            try {
                accountInformations.fromBuild(accountInfosBytes);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeByte(this.code.getCode());
        if(this.code == LoginResponseCode.ACCOUNT_BANNED) {
            packet.writeInt(this.banDuration);
        } else if(this.code == LoginResponseCode.CORRECT_LOGIN) {
            packet.writeBytes(accountInformations.serializeForClient());
        }

        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public LoginResponseCode getCode() {
        return code;
    }

    public void setCode(LoginResponseCode code) {
        this.code = code;
    }

    public int getBanDuration() {
        return banDuration;
    }

    public void setBanDuration(int banDuration) {
        this.banDuration = banDuration;
    }

    public AccountInformations getAccountInformations() {
        return accountInformations;
    }

    public void setAccountInformations(AccountInformations accountInformations) {
        this.accountInformations = accountInformations;
    }

    @Override
    public String toString() {
        return "{ code: " + code + (banDuration != 0 ? ", banDuration: " + banDuration : "") + (accountInformations != null ? ", accountInfos: " + accountInformations.toString() : "") + " }";
    }

    public enum LoginResponseCode {
        CORRECT_LOGIN(0),
        INVALID_LOGIN(2),
        ALREADY_CONNECTED(3),
        SAVE_IN_PROGRESS(4),
        ACCOUNT_BANNED(5),
        ACCOUNT_LOCKED(9),
        LOGIN_SERVER_DOWN(10),
        TOO_MANY_CONNECTIONS(11),
        INVALID_PARTNER(12),
        INVALID_EMAIL(20),
        ACCOUNT_UNDER_MODERATION(21),
        CLOSED_BETA(127);

        private int code;

        private LoginResponseCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static LoginResponseCode getByID(int id) {
            for(LoginResponseCode code : values()) {
                if(code.code == id) return code;
            }

            return null;
        }
    }
}
