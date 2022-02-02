package fr.redboxing.incarnam.manager;

import fr.redboxing.incarnam.IncarnamApplication;
import fr.redboxing.incarnam.database.entities.Server;
import fr.redboxing.incarnam.models.Proxy;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class ProxyManager {
    public static ProxyManager INSTANCE;

    private final SessionFactory sessionFactory = IncarnamApplication.getSessionFactory();

    public ProxyManager() {
        ProxyManager.INSTANCE = this;
    }

    public Proxy findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Proxy proxy = session.get(Proxy.class, id);
        session.getTransaction().commit();
        return proxy;
    }

    public List<Proxy> getProxies() {
        Session session = sessionFactory.getCurrentSession();
        List<Server> servers = session.createQuery("from servers", Server.class).getResultList();
        List<Proxy> proxies = new ArrayList<>();

        for (Server server : servers) {
            proxies.add(Proxy.fromEntity(server));
        }

        return proxies;
    }
}
