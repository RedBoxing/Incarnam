package fr.redboxing.incarnam.database.entities;

import fr.redboxing.incarnam.models.account.Community;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter
    @Setter
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Getter
    @Setter
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Getter
    @Setter
    @Column(name = "password", nullable = false)
    private String password;

    @Getter
    @Setter
    @Column(name = "community", nullable = false)
    private Community community;

    @Getter()
    @Setter
    @Column(name = "subscription_level", nullable = false)
    private int subscriptionLevel;

    @Getter
    @Setter
    @Column(name = "subscription_expiration", nullable = false)
    private int subscriptionExpiration;

    @Getter
    @Setter
    @Column(name = "anti_addiction_level", nullable = false)
    private int antiAddictionLevel;

    @Getter
    @Setter
    @Column(name = "account_creation", nullable = false)
    private int accountCreation;

    @Getter
    @Setter
    @Column(name = "account_expiration", nullable = false)
    private int accountExpiration;

    @Getter
    @Setter
    @Column(name = "session_id")
    private String sessionId;

    @Getter
    @Setter
    @Column(name = "ban_duration", nullable = false)
    private long banDuration;
}
