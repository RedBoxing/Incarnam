package fr.redboxing.incarnam.manager;

import fr.redboxing.incarnam.IncarnamApplication;
import fr.redboxing.incarnam.database.entities.Account;
import fr.redboxing.incarnam.models.account.AccountInformations;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class AccountManager {
    public static AccountManager INSTANCE;

    private final SessionFactory sessionFactory = IncarnamApplication.getSessionFactory();

    public AccountManager() {
        INSTANCE = this;
    }

    public Account findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Account.class, id);
    }

    public Optional<Account> findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Optional<Account> account = session.createQuery("from Account where username = :username", Account.class).setParameter("username", username).uniqueResultOptional();
        session.getTransaction().commit();
        return account;
    }

    public Optional<Account> findBySessionId(String sessionId) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Optional<Account> account = session.createQuery("from Account where session_id = :sessionId", Account.class).setParameter("sessionId", sessionId).uniqueResultOptional();
        session.getTransaction().commit();
        return account;
    }

    public void saveAccount(Account account) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(account);
        session.getTransaction().commit();
    }

    public AccountInformations getAccountInfos(Long id) {
        Account account = findById(id);

        AccountInformations accountInformations = new AccountInformations();
        accountInformations.setAccount_id(id);
        accountInformations.setAccountNickName(account.getUsername());
        accountInformations.setAccountCommunity(account.getCommunity());
        accountInformations.setSubscriptionLevel(account.getSubscriptionLevel());
        accountInformations.setAccountCreationDate(account.getAccountCreation());
        accountInformations.setAccountExpirationDate(account.getAccountExpiration());
        accountInformations.setBanEndDate(account.getBanDuration());

        return accountInformations;
    }

    public AccountInformations getAccountInfosFromSessionId(String sessionId) {
        Optional<Account> accountOptional = findBySessionId(sessionId);
        if(accountOptional.isPresent()) {
            Account account = accountOptional.get();

            AccountInformations accountInformations = new AccountInformations();
            accountInformations.setAccount_id(account.getId());
            accountInformations.setAccountNickName(account.getUsername());
            accountInformations.setAccountCommunity(account.getCommunity());
            accountInformations.setSubscriptionLevel(account.getSubscriptionLevel());
            accountInformations.setAccountCreationDate(account.getAccountCreation());
            accountInformations.setAccountExpirationDate(account.getAccountExpiration());
            accountInformations.setBanEndDate(account.getBanDuration());

            return accountInformations;
        }

        return null;
    }
}
