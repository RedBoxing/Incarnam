package fr.redboxing.incarnam.network.packets.server;

import com.google.common.base.Optional;
import fr.redboxing.incarnam.models.account.AccountInformations;
import fr.redboxing.incarnam.models.account.Community;
import fr.redboxing.incarnam.models.account.admin.Admin;
import fr.redboxing.incarnam.utils.AdminUtils;
import fr.redboxing.incarnam.utils.DataUtils;
import fr.redboxing.incarnam.network.handlers.PacketHandler;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.network.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketDispatchAuthenticationResult extends Packet {
    private PacketAuthenticationResult.LoginResponseCode code;
    private AccountInformations accountInformations;

    public PacketDispatchAuthenticationResult(PacketAuthenticationResult.LoginResponseCode code, AccountInformations accountInformations) {
        super(PacketType.SERVER_MSG);
        this.code = code;
        this.accountInformations = accountInformations;
    }

    public PacketDispatchAuthenticationResult() {
        this(PacketAuthenticationResult.LoginResponseCode.INVALID_LOGIN, null);
    }

    @Override
    public void decode(PacketBuffer packet) {
        this.code = PacketAuthenticationResult.LoginResponseCode.getByID(packet.readByte());
        boolean success = packet.readBoolean();

        if(success) {
            try {
                //this.accountInformation = AccountInformation.unSerialize(packet.getData());
                this.accountInformations = new AccountInformations();
                this.accountInformations.fromBuild(packet.readBytes(packet.readableBytes()));
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void encode(PacketBuffer packet) {
        packet.writeByte(this.code.getCode());
        packet.writeBoolean(this.code == PacketAuthenticationResult.LoginResponseCode.CORRECT_LOGIN);
        if(this.code == PacketAuthenticationResult.LoginResponseCode.CORRECT_LOGIN) {
            packet.writeBytes(this.accountInformations.serializeForClient());
        }
        packet.finish();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public PacketAuthenticationResult.LoginResponseCode getCode() {
        return code;
    }

    public void setCode(PacketAuthenticationResult.LoginResponseCode code) {
        this.code = code;
    }

    public AccountInformations getAccountInformation() {
        return accountInformations;
    }

    public void setAccountInformations(AccountInformations accountInformation) {
        this.accountInformations = accountInformations;
    }

    @Override
    public String toString() {
        return "{ code: " + code + ", accountInformations: " + accountInformations.toString() + " }";
    }

    private static class AccountInformation
    {
        private final long m_account_id;
        private final Community m_community;
        private final Optional<Admin> m_adminInformation;
        private final String m_accountNickName;

        AccountInformation(final long account_id, final Community community, final Optional<Admin> adminInformation, String accountNickName) {
            super();
            this.m_account_id = account_id;
            this.m_community = community;
            this.m_adminInformation = adminInformation;
            this.m_accountNickName = accountNickName;
        }

        public long get_account_id() {
            return this.m_account_id;
        }

        public Community getCommunity() {
            return this.m_community;
        }

        public Optional<Admin> getAdminInformation() {
            return this.m_adminInformation;
        }

        public String get_accountNickName() {
            return this.m_accountNickName;
        }

        public static AccountInformation unSerialize(final ByteBuf buf) {
            final long id = buf.readLong();
            final Community community = Community.getFromId(buf.readInt());
            final String nickname = DataUtils.readBigString(buf);
            Optional<Admin> admin;
            if (buf.readBoolean()) {
                final byte[] sAdmin = new byte[buf.readInt()];
                buf.readBytes(sAdmin);
                admin = Optional.of(AdminUtils.unSerialize(sAdmin));
            }
            else {
                admin = Optional.absent();
            }
            return new AccountInformation(id, community, admin, nickname);
        }

        public static byte[] serialize(final AccountInformation accountInformation) {
            final ByteBuf buf = Unpooled.buffer();
            buf.writeLong(accountInformation.get_account_id());
            buf.writeInt(accountInformation.getCommunity().getId());
            DataUtils.writeBigString(buf, accountInformation.get_accountNickName());
            if (accountInformation.getAdminInformation().isPresent()) {
                buf.writeBoolean(true);
                buf.writeInt(accountInformation.getAdminInformation().get().serialize().length);
                buf.writeBytes(accountInformation.getAdminInformation().get().serialize());
            }
            else {
                buf.writeBoolean(false);
            }
            return buf.array();
        }

        @Override
        public String toString() {
            return "AccountInformation{m_account_id=" + this.m_account_id + ", m_community=" + this.m_community + ", m_adminInformation=" + this.m_adminInformation + ", m_accountNickName=" + this.m_accountNickName + '}';
        }
    }
}
