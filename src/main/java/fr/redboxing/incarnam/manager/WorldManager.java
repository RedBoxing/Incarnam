package fr.redboxing.incarnam.manager;

import fr.redboxing.incarnam.IncarnamApplication;
import fr.redboxing.incarnam.database.entities.World;
import fr.redboxing.incarnam.models.WorldInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class WorldManager {
    public static WorldManager INSTANCE;

    private final SessionFactory sessionFactory = IncarnamApplication.getSessionFactory();

    public WorldManager() {
        WorldManager.INSTANCE = this;
    }

    public World findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        World world = session.get(World.class, id);
        session.getTransaction().commit();
        return world;
    }

    public WorldInfo getForServer(int serverId) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        World world = session.createQuery("from World where server_id = :server_id", World.class)
                    .setParameter("server_id", serverId)
                    .getSingleResult();

        session.getTransaction().commit();
        return WorldInfo.fromEntity(world);
    }

    public List<WorldInfo> getWorlds() {
        Session session = sessionFactory.getCurrentSession();
        List<World> worlds = session.createQuery("from worlds", World.class).getResultList();
        List<WorldInfo> worldInfos = new ArrayList<>();

        for (World world : worlds) {
            worldInfos.add(WorldInfo.fromEntity(world));
        }

        return worldInfos;
    }
}
