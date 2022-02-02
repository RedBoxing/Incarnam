package fr.redboxing.incarnam.manager;

import lombok.Getter;

public class RepositoryManager {
    @Getter
    private AccountManager accountManager;

    @Getter
    private ProxyManager proxyManager;

    @Getter
    private WorldManager worldManager;

    public RepositoryManager() {
        this.accountManager = new AccountManager();
        this.proxyManager = new ProxyManager();
        this.worldManager = new WorldManager();
    }
}
