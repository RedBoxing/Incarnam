package fr.redboxing.incarnam.models.account;

import java.util.HashMap;

public enum AccountDataFlagType
{
    STEAMER_BETA((byte)1, "WKSTEAMER"),
    ANTI_ADDICTION((byte)2, "ANTIADDICT"),
    CHARACTER_SLOTS((byte)3, "WKCHARACTERS"),
    VAULT_UPGRADE((byte)4, "WKVAULTUP");

    private static final HashMap<Byte, AccountDataFlagType> m_flagsById = new HashMap<>();
    private static final HashMap<String, AccountDataFlagType> m_flagsByWebRepresentation = new HashMap<String, AccountDataFlagType>();
    private final byte m_id;
    private final String m_webRepresentation;

    private AccountDataFlagType(final byte id, final String webRepresentation) {
        this.m_id = id;
        this.m_webRepresentation = webRepresentation;
    }

    public byte getId() {
        return this.m_id;
    }

    public String getWebRepresentation() {
        return this.m_webRepresentation;
    }

    public static AccountDataFlagType fromId(final byte id) {
        return AccountDataFlagType.m_flagsById.get(id);
    }

    public static AccountDataFlagType fromWebRepresentation(final String webRepresentation) {
        return AccountDataFlagType.m_flagsByWebRepresentation.get(webRepresentation);
    }

    static {
        for (final AccountDataFlagType flag : values()) {
            AccountDataFlagType.m_flagsById.put(flag.getId(), flag);
            AccountDataFlagType.m_flagsByWebRepresentation.put(flag.getWebRepresentation(), flag);
        }
    }
}
