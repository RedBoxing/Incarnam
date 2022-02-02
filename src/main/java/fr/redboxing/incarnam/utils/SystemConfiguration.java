package fr.redboxing.incarnam.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.*;
import java.io.*;
import java.util.*;
import java.nio.*;

public class SystemConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfiguration.class);
    public static final EnumSet<SystemConfigurationType> DISPATCH_SERIALIZATION = EnumSet.of(SystemConfigurationType.SERVER_ID, SystemConfigurationType.SUBSCRIPTION_CHECK_GAME_ID, SystemConfigurationType.BETA_CHECK_ENABLED, SystemConfigurationType.COMMUNITY_CHECK_ENABLE, SystemConfigurationType.COMMUNITY_REQUIRED, SystemConfigurationType.COMMUNITY_FORBIDDEN, SystemConfigurationType.AUTHORIZED_PARTNERS);
    public static final SystemConfiguration INSTANCE = new SystemConfiguration();
    private static final Pattern SPLIT_PATTERN = Pattern.compile(";");;
    private Map<SystemConfigurationType, String> m_properties;
    private byte[] m_clientData;
    private byte[] m_dispatchData;

    public SystemConfiguration() {
        super();
        this.m_properties = new EnumMap<>(SystemConfigurationType.class);
    }

    public SystemConfiguration(int server_id, boolean beta_check_enabled, boolean community_check_enabled, int community_required, String community_forbidden, String subscription_check_game_id, String authorized_partner) {
        this();

        this.m_properties.put(SystemConfigurationType.SERVER_ID, String.valueOf(server_id));
        this.m_properties.put(SystemConfigurationType.BETA_CHECK_ENABLED, String.valueOf(beta_check_enabled));
        this.m_properties.put(SystemConfigurationType.COMMUNITY_CHECK_ENABLE, String.valueOf(community_check_enabled));
        this.m_properties.put(SystemConfigurationType.COMMUNITY_REQUIRED, String.valueOf(community_required));
        this.m_properties.put(SystemConfigurationType.COMMUNITY_FORBIDDEN, community_forbidden);
        this.m_properties.put(SystemConfigurationType.SUBSCRIPTION_CHECK_GAME_ID, String.valueOf(subscription_check_game_id));
        this.m_properties.put(SystemConfigurationType.AUTHORIZED_PARTNERS, authorized_partner);
    }

    public Map<SystemConfigurationType, String> getProperties() {
        return this.m_properties;
    }

    public void setProerties(Map<SystemConfigurationType, String> properties) {
        this.m_properties = properties;
    }

    public void setProperty(final SystemConfigurationType type, final String value) {
        this.m_properties.put(type, value);
    }

    public static void configure(final String filename) {
        try {
            SystemConfiguration.INSTANCE.load(filename);
        }
        catch (IOException | RuntimeException e) {
            LOGGER.error("Erreur lors du chargement du fichier de propri\u00e9t\u00e9s : " + filename, e);
        }
    }

    public void load(final String filename) throws IOException {
        final Properties props = new Properties();
        props.load(new FileInputStream(filename));
        this.m_properties.clear();
        for (final SystemConfigurationType type : SystemConfigurationType.values()) {
            String value = props.getProperty(type.getKey());
            if (value == null) {
                value = type.getDefaultValue();
            }
            this.m_properties.put(type, value);
        }
        this.buildSerialisation();
    }

    private void buildSerialisation() {
        this.buildClientSerialization();
        this.buildDispatchSerialization();
    }

    private void buildClientSerialization() {
        ByteArray bb = new ByteArray();
        bb.putInt(SystemConfigurationType.NUM_SHARED_PROPERTIES);
        for (final SystemConfigurationType type : SystemConfigurationType.values()) {
            if (type.isShareWithClient()) {
                final String value = this.m_properties.get(type);
                if (value != null) {
                    bb.putShort(type.getId());
                    final byte[] bytes = Utils.toUTF8(value);
                    bb.putInt(bytes.length);
                    bb.put(bytes);
                }
            }
        }
        this.m_clientData = bb.toArray();
    }

    private void buildDispatchSerialization() {
        ByteArray bb = new ByteArray();
        bb.putInt(DISPATCH_SERIALIZATION.size());
        for (SystemConfigurationType type : DISPATCH_SERIALIZATION) {
            String value = this.m_properties.get(type);
            if (value == null)
                continue;

            bb.putShort(type.getId());
            byte[] bytes = Utils.toUTF8(value);
            bb.putInt(bytes.length);
            bb.put(bytes);
        }

        this.m_dispatchData = bb.toArray();
    }

    public byte[] serializeForClient() {
        if (this.m_clientData == null) {
            this.buildClientSerialization();
        }
        return this.m_clientData.clone();
    }

    public byte[] serializeForDispatch() {
        if (this.m_dispatchData == null) {
            this.buildDispatchSerialization();
        }
        return this.m_dispatchData.clone();
    }

    public void unserialize(final byte[] data) {
        this.m_properties.clear();
        final ByteBuffer bb = ByteBuffer.wrap(data);
        int numProperties = bb.getInt();
        for (int i = 0; i < numProperties; ++i) {
            short typeId = bb.getShort();
            final SystemConfigurationType type = SystemConfigurationType.getById(typeId);
            final byte[] bytes = new byte[bb.getInt()];
            bb.get(bytes);
            final String value = Utils.fromUTF8(bytes);
            this.m_properties.put(type, value);
        }
    }

    public boolean containsKey(final SystemConfigurationType key) {
        return this.m_properties.containsKey(key);
    }

    public int getIntValue(final SystemConfigurationType type) {
        String value = this.m_properties.get(type);
        if (value == null) {
            value = type.getDefaultValue();
        }
        return Utils.getInteger(value, 0);
    }

    public long getLongValue(final SystemConfigurationType type) {
        String value = this.m_properties.get(type);
        if (value == null) {
            value = type.getDefaultValue();
        }
        return Utils.getLong(value, 0L);
    }

    public boolean getBooleanValue(final SystemConfigurationType type) {
        String value = this.m_properties.get(type);
        if (value == null) {
            value = type.getDefaultValue();
        }
        return Utils.getBoolean(value, false);
    }

    public String getStringValue(final SystemConfigurationType type) {
        String value = this.m_properties.get(type);
        if (value == null) {
            value = type.getDefaultValue();
        }
        return value;
    }

    public ArrayList<String> getStringArrayList(final SystemConfigurationType type) {
        String value = this.m_properties.get(type);
        if (value == null) {
            value = type.getDefaultValue();
        }
        final ArrayList<String> list = new ArrayList<String>();
        for (final String s : SystemConfiguration.SPLIT_PATTERN.split(value)) {
            if (!s.isEmpty()) {
                list.add(s);
            }
        }
        return list;
    }

    public ArrayList<Integer> getIntArrayList(final SystemConfigurationType type) {
        String value = this.m_properties.get(type);
        if (value == null) {
            value = type.getDefaultValue();
        }
        final ArrayList<Integer> list = new ArrayList<>();
        for (final String s : SystemConfiguration.SPLIT_PATTERN.split(value)) {
            if (!s.isEmpty()) {
                final int i = Utils.getInteger(s, Integer.MIN_VALUE);
                if (i != Integer.MIN_VALUE) {
                    list.add(i);
                }
            }
        }
        return list;
    }

    public HashSet<Integer> getIntHashSet(final SystemConfigurationType type) {
        String value = this.m_properties.get(type);
        if (value == null) {
            value = type.getDefaultValue();
        }
        final HashSet<Integer> list = new HashSet<>();
        for (final String s : SystemConfiguration.SPLIT_PATTERN.split(value)) {
            if (!s.isEmpty()) {
                final int i = Utils.getInteger(s, Integer.MIN_VALUE);
                if (i != Integer.MIN_VALUE) {
                    list.add(i);
                }
            }
        }
        return list;
    }

    public HashSet<Long> getLongHashSet(final SystemConfigurationType type) {
        String value = this.m_properties.get(type);
        if (value == null) {
            value = type.getDefaultValue();
        }
        final HashSet<Long> list = new HashSet<>();
        for (final String s : SystemConfiguration.SPLIT_PATTERN.split(value)) {
            if (!s.isEmpty()) {
                final long l = Utils.getLong(s, Long.MIN_VALUE);
                if (l != Long.MIN_VALUE) {
                    list.add(l);
                }
            }
        }
        return list;
    }

    public String changePropertyValue(final SystemConfigurationType key, final String value) throws IllegalArgumentException {
        final String res = this.m_properties.put(key, value);
        this.buildSerialisation();
        return res;
    }

    @Override
    public String toString() {
        return "SystemConfiguration{m_properties=" + this.m_properties.size() + '}';
    }

    public String getValueToString(final String sysConfKey) {
        final SystemConfigurationType confParam = SystemConfigurationType.getByKey(sysConfKey);
        if (confParam == null) {
            return "null";
        }
        final ValueType type = confParam.getType();
        switch (type) {
            case BOOLEAN: {
                return Boolean.toString(getBooleanValue(confParam));
            }
            case NUMBER: {
                return Long.toString(getLongValue(confParam));
            }
            case STRING: {
                return getStringValue(confParam);
            }
            case STRINGLIST: {
                return Utils.join(getStringArrayList(confParam), ",");
            }
            case NUMBERLIST: {
                return Utils.join(getIntArrayList(confParam), ",");
            }
            default: {
                return "null";
            }
        }
    }

    public static enum SystemConfigurationType {
        MONITORED_PROPERTIES(1, "monitoredproperties.enable", "true", false, ValueType.BOOLEAN),
        CALENDAR_DELTA(2, "calendar.delta", "0", true, ValueType.NUMBER),
        CALENDAR_TZ(3, "calendar.timezone", "UTC", true, ValueType.STRING),
        CENSORSHIP_ENABLE(4, "censor.enable", "false", ValueType.BOOLEAN),
        SERVER_LANGUAGE(5, "serverLanguage", "", true, ValueType.STRINGLIST),
        CLIENT_CAN_DISABLE_PROFANITY_FILTER(6, "clientCanDisableProfanityFilter", "true", true, ValueType.BOOLEAN),
        PLAYER_LEVEL_CAP(7, "playerLevelCap", "-1", true, ValueType.NUMBER),
        AUTHORIZED_CHARACTER_CLASS(8, "authorizedCharacterClass", "", true, ValueType.NUMBERLIST),
        WORLD_INSTANCES_FORBIDDEN(9, "worldInstances.forbidden", "", true, ValueType.NUMBERLIST),
        KROSMOZ_GAMES_ENABLE(10, "krosmozGames.enable", "true", true, ValueType.BOOLEAN),
        SHOP_INGAME_INTERACTIONS_ENABLE(11, "shopInGameInteractions.enable", "false", true, ValueType.BOOLEAN),
        CONTACT_MODERATOR_ENABLE(12, "contactModerator.enable", "true", true, ValueType.BOOLEAN),
        DISPLAY_SUBSCRIPTION_END_POPUP(13, "display.subscription.end.popup.enable", "true", true, ValueType.BOOLEAN),
        PARTNER(14, "partner", "", false, ValueType.STRING),
        SHOP_ENABLED(15, "shop.enable", "true", true, ValueType.BOOLEAN),
        SOAP_AUTHENTICATION_URL(16, "soap.authenticationUrl", "", true, ValueType.STRING),
        SOAP_ACCOUNT_URL(17, "soap.accountUrl", "", true, ValueType.STRING),
        SOAP_SHOP_URL(18, "soap.shopUrl", "", true, ValueType.STRING),
        METRICS_REPORTER_ENABLE(19, "metrics.reporter.enable", "false", false, ValueType.BOOLEAN),
        BETA_CHECK_ENABLED(20, "beta.check.enabled", "false", false, ValueType.BOOLEAN),
        PLATFORM_NAME(101, "platform.name", "", false, ValueType.STRING),
        PLATFORM_COMMUNITY(102, "platform.community", "fr", false, ValueType.STRING),
        GAME_ID(201, "game.id", "3", false, ValueType.NUMBER),
        EXPO_MODE_ENABLE(202, "expomode.enable", "false", ValueType.BOOLEAN),
        ADMIN_RIGHTS_FORCE_ALL(203, "adminrights.forceAll", "false", ValueType.BOOLEAN),
        SERVER_LOCK_ENABLE(204, "serverlock.enable", "false", ValueType.BOOLEAN),
        SUBSCRIPTION_REQUIRED(206, "subscription.required", "", ValueType.NUMBERLIST),
        SUBSCRIPTION_FORCE(207, "subscription.force", "", ValueType.NUMBER),
        COMMUNITY_CHECK_ENABLE(208, "community.check.enabled", "false", ValueType.BOOLEAN),
        COMMUNITY_REQUIRED(209, "community.required", "", ValueType.NUMBERLIST),
        COMMUNITY_FORBIDDEN(210, "community.forbidden", "", ValueType.NUMBERLIST),
        ANTI_ADDICTION_ENABLE(211, "antiAddiction.enable", "false", true, ValueType.BOOLEAN),
        ANTI_ADDICTION_FORCED_ACCOUNTS(212, "antiAddiction.force.accounts", "", ValueType.NUMBERLIST),
        SUBSCRIPTION_FORCED_DURATION_IN_SECOND(215, "subscription.forced.duration.in.second", "-1", ValueType.NUMBER),
        SUBSCRIPTION_CHECK_SERVER_LIST(217, "subscription.check.server.list", "", ValueType.NUMBERLIST),
        SUBSCRIPTION_CHECK_GAME_ID(221, "subscription.check.game.id", "", ValueType.NUMBER),
        QUEUE_ACTIVATED(218, "queue.activated", "true", ValueType.BOOLEAN),
        QUEUE_PLAYER_LIMIT(219, "queue.player.limit", "0", ValueType.NUMBER),
        AUTHORIZED_PARTNERS(220, "partners.authorized", "default;steam", ValueType.STRINGLIST),
        INSTANCE_STATIC_DISTRIBUTION(301, "instances.staticDistribution", "true", false, ValueType.BOOLEAN),
        ASK_FOR_SECRET_QUESTION_ON_CHARACTER_DELETION(302, "askSecretQuestionToDelete", "false", ValueType.BOOLEAN),
        PREBOOST_CHARACTER_ENABLE(401, "preboostCharacter.enable", "false", ValueType.BOOLEAN),
        ACHIEVEMENTS_FORBIDDEN(402, "achievements.forbidden", "", ValueType.NUMBERLIST),
        INTERACTIVE_ELEMENTS_FORBIDDEN(403, "interactiveElements.forbidden", "", ValueType.NUMBERLIST),
        FIGHT_CHALLENGE_ENABLE(404, "fightChallenge.enable", "true", true, ValueType.BOOLEAN),
        DUNGEON_DAILY_LOCK_BYPASS(405, "dungeonDailyLockBypass", "", true, ValueType.NUMBERLIST),
        COMPANIONS_ENABLE(406, "companions.enable", "true", true, ValueType.BOOLEAN),
        BETA_MODE(407, "beta.mode", "false", true, ValueType.BOOLEAN),
        SUBSCRIPTION_DEFAULT_VALUE(408, "subscription.defaultValue", "0", false, ValueType.NUMBER),
        SUBSCRIPTION_DATE_TIMEZONE(409, "subscription.dateTimezone", "Europe/Paris", false, ValueType.STRING),
        HAVEN_WORLDS_ENABLE(410, "havenWorld.enable", "true", true, ValueType.BOOLEAN),
        FREE_COMPANION_ENABLE(411, "freeCompanion.enable", "true", false, ValueType.BOOLEAN),
        REROLL_XP_BONUS_ENABLE(412, "rerollXpBonus.enable", "true", true, ValueType.BOOLEAN),
        SHOP_KEY(413, "shop.key", "WAKFU_INGAME", true, ValueType.STRING),
        INSTANCES_NEEDING_ACCESS_RIGHTS(414, "instancesNeedingAccessRights", "", true, ValueType.NUMBERLIST),
        INSTANCES_NEEDING_INTERACTION_RIGHTS(415, "instancesNeedingInteractionRights", "", true, ValueType.NUMBERLIST),
        COLLECT_FIGHT_ENABLED(416, "collectFightEnabled", "false", false, ValueType.BOOLEAN),
        FIGHT_PREMIUM_DISPLAY(417, "fightPremiumEnabled", "false", true, ValueType.BOOLEAN),
        TIMER_FOR_FIRST_COLLECT(418, "timerForFirstCollect", "false", true, ValueType.BOOLEAN),
        FORCE_BIND_ON_PICKUP(419, "forceBindOnPickup.enable", "false", true, ValueType.BOOLEAN),
        SERVER_ID(420, "server.id", "", true, ValueType.NUMBER),
        SHOP_ENABLE_KROSZ(421, "shop.krosz.enable", "true", true, ValueType.BOOLEAN),
        FIGHT_REWORK_ENABLED(422, "fight.rework.enabled", "true", true, ValueType.BOOLEAN),
        ITEM_TRACKER_LOG_LEVEL(423, "itemTracker.logLevel", "10000", false, ValueType.NUMBER),
        RECO_IN_FIGHT_ENABLED(424, "reco.in.fight.enabled", "false", true, ValueType.BOOLEAN),
        NEW_HP_LOSS_FORMULA(425, "new.hpLoss.formula", "true", true, ValueType.BOOLEAN),
        STEAM_ENABLED(426, "steam.enabled", "false", false, ValueType.BOOLEAN),
        VAULT_ENABLED(427, "vault.enabled", "true", true, ValueType.BOOLEAN),
        ZAAP_FREE(428, "zaap.free", "false", true, ValueType.BOOLEAN),
        NEW_APTITUDE_ENABLED(429, "newAptitude.enabled", "true", true, ValueType.BOOLEAN),
        PANDA_NEW_BARREL(430, "pandaNewBarrel.enabled", "true", true, ValueType.BOOLEAN),
        HEROES_ENABLED(431, "heroes.enabled", "false", true, ValueType.BOOLEAN),
        VERSION_CHECK(500, "version.check", "true", false, ValueType.BOOLEAN),
        PROXY_LIST(600, "proxy.list", "proxies.json", false, ValueType.STRING),
        ADMIN_LIST(601, "admin.list", "admins.json", false, ValueType.STRING),
        HEROES_FORCE_ADD_TO_PARTY(602, "heroes.forceAddToParty", "false", true, ValueType.BOOLEAN);

        private static final HashMap<Short, SystemConfigurationType> BY_IDS;
        private static final HashMap<String, SystemConfigurationType> BY_KEYS;
        static final int NUM_SHARED_PROPERTIES;
        private final short m_id;
        private final String m_key;
        private final boolean m_shareWithClient;
        private final String m_defaultValue;
        private final ValueType m_type;

        private SystemConfigurationType(final int id, final String key, final String defaultValue, final ValueType type) {
            this.m_id = MathHelper.ensureShort(id);
            this.m_key = key;
            this.m_defaultValue = defaultValue;
            this.m_shareWithClient = false;
            this.m_type = type;
        }

        private SystemConfigurationType(final int id, final String key, final String defaultValue, final boolean shareWithClient, final ValueType type) {
            this.m_id = MathHelper.ensureShort(id);
            this.m_key = key;
            this.m_shareWithClient = shareWithClient;
            this.m_defaultValue = defaultValue;
            this.m_type = type;
        }

        public static SystemConfigurationType getByKey(final String key) {
            return SystemConfigurationType.BY_KEYS.get(key);
        }

        public static SystemConfigurationType getById(final short id) {
            return SystemConfigurationType.BY_IDS.get(id);
        }

        public short getId() {
            return this.m_id;
        }

        public String getKey() {
            return this.m_key;
        }

        public boolean isShareWithClient() {
            return this.m_shareWithClient;
        }

        public String getDefaultValue() {
            return this.m_defaultValue;
        }

        public ValueType getType() {
            return this.m_type;
        }

        static {
            BY_IDS = new HashMap<>();
            BY_KEYS = new HashMap<>();
            int num = 0;
            for (final SystemConfigurationType type : values()) {
                SystemConfigurationType.BY_IDS.put(type.m_id, type);
                SystemConfigurationType.BY_KEYS.put(type.m_key, type);
                if (type.m_shareWithClient) {
                    ++num;
                }
            }
            NUM_SHARED_PROPERTIES = num;
        }
    }

    public enum ValueType
    {
        BOOLEAN,
        NUMBER,
        STRING,
        STRINGLIST,
        NUMBERLIST;
    }

}