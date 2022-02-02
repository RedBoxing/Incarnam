package fr.redboxing.incarnam.models.account.admin;

import fr.redboxing.incarnam.utils.AdminUtils;
import com.google.gson.annotations.*;
import com.google.common.base.Optional;
import java.util.*;

public class Admin
{
    @SerializedName("account")
    private final long m_accountId;
    @SerializedName("name")
    private final String m_adminName;
    @SerializedName("rights")
    private final List<Right> m_rights;

    public Admin(final long accountId, final String name) {
        super();
        this.m_rights = new ArrayList<Right>();
        this.m_accountId = accountId;
        this.m_adminName = name;
    }

    public byte[] serialize() {
        return AdminUtils.serialize(this);
    }

    public boolean addRights(final Right right) {
        try {
            return this.m_rights.add(right);
        }
        finally {
            Collections.sort(this.m_rights, AdminUtils.RIGHT_COMPARATOR);
        }
    }

    public long getAccountId() {
        return this.m_accountId;
    }

    public String getAdminName() {
        return this.m_adminName;
    }

    public List<Right> getRights() {
        return Collections.unmodifiableList((List<? extends Right>)this.m_rights);
    }

    public Optional<Right> getRight(final int serverId) {
        for (final Right right : this.m_rights) {
            if (right.getServerId() == serverId) {
                return Optional.of(right);
            }
        }
        return Optional.absent();
    }

    @Override
    public String toString() {
        return "Admin{m_accountId=" + this.m_accountId + ", m_adminName='" + this.m_adminName + '\'' + ", m_rights=" + this.m_rights + '}';
    }
}