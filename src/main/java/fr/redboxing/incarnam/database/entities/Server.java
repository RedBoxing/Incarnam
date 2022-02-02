package fr.redboxing.incarnam.database.entities;

import fr.redboxing.incarnam.models.account.Community;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue
    @Getter
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(name = "host", nullable = false)
    private String host;

    @Getter
    @Setter
    @Column(name = "port", nullable = false)
    private int port;

    @Getter
    @Setter
    @Column(name = "comunity", nullable = false)
    private Community comunity;
}
