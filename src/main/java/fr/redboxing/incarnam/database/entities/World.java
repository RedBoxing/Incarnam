package fr.redboxing.incarnam.database.entities;

import fr.redboxing.incarnam.utils.SystemConfiguration;
import fr.redboxing.incarnam.utils.serializers.SystemConfigurationConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "worlds")
public class World {
    @Id
    @GeneratedValue
    @Getter
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter
    @Setter
    @Column(name = "server_id", nullable = false)
    private int serverId;

    @Getter
    @Setter
    @Convert(converter = SystemConfigurationConverter.class)
    @Column(name = "system_configuration", nullable = false)
    private SystemConfiguration systemConfiguration;

    @Getter
    @Setter
    @Column(name = "version_major", nullable = false)
    private byte versionMajor;

    @Getter
    @Setter
    @Column(name = "version_minor", nullable = false)
    private short versionMinor;

    @Getter
    @Setter
    @Column(name = "version_patch", nullable = false)
    private byte versionPatch;

    @Getter
    @Setter
    @Column(name = "version_build", nullable = false)
    private String versionBuild;
}
