package fr.redboxing.incarnam;

import fr.redboxing.incarnam.crypto.SHA256Hasher;
import fr.redboxing.incarnam.database.entities.Account;
import fr.redboxing.incarnam.manager.AccountManager;
import fr.redboxing.incarnam.manager.RepositoryManager;
import fr.redboxing.incarnam.models.account.Community;
import fr.redboxing.incarnam.network.handlers.BotClientHandler;
import fr.redboxing.incarnam.network.handlers.GameServerHandler;
import fr.redboxing.incarnam.network.BaseInitializer;
import fr.redboxing.incarnam.network.handlers.ProxyClientHandler;
import fr.redboxing.incarnam.network.handlers.ProxyGameClientServer;
import fr.redboxing.incarnam.network.packets.Packet;
import fr.redboxing.incarnam.utils.serializers.CustomObjectMapperSupplier;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.cli.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.util.Properties;

public class IncarnamApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(IncarnamApplication.class);;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private final EventLoopGroup bossGroup2 = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup2 = new NioEventLoopGroup();

    private Thread authThread;
    private Thread gameThread;

    private static final Properties env = new Properties();

    public static void main(String[] sourceArgs) {
        loadProperties();

        Options options = new Options();
        options.addOption("h", "help", false, "Print this message.");
        options.addOption("a", "auth", false, "Start the auth server.");
        options.addOption("g", "game", false, "Start the game server.");
        options.addOption("P", "proxy", false, "Start the proxy server.");
        options.addOption("p", "port", true, "The port to listen to.");
        options.addOption("p2", "port2", true, "The second port to listen to. (proxy only)");
        options.addOption("b", "bot", false, "Start the bot.");
        options.addOption("r", "register", false, "Register.");
        options.addOption("e", "email", true, "The email to register.");
        options.addOption("u", "username", true, "The username to register.");
        options.addOption("pw", "password", true, "The password to register.");

        CommandLineParser parser = new DefaultParser();
        CommandLine args = null;

        try {
            args = parser.parse(options, sourceArgs);
        } catch (ParseException e) {
            LOGGER.error("Error while parsing arguments : {}", e.getMessage());
        }

        if(args != null) {
            new IncarnamApplication().run(args);
        }
    }

    public void run(CommandLine args) {
        if(args.hasOption("auth")) {
            if(args.hasOption("port")) {
                this.authThread = new Thread(() -> {
                    startAuthServer(Integer.parseInt(args.getOptionValue("port")));
                });
            } else {
                LOGGER.error("You must specify a port for the auth server.");
            }
        } else if(args.hasOption("game")) {
            if(args.hasOption("port")) {
                this.gameThread = new Thread(() -> {
                    startGameServer(Integer.parseInt(args.getOptionValue("port")));
                });
            } else {
                LOGGER.error("You must specify a port for the game server.");
            }
        } else if(args.hasOption("proxy")) {
            if(args.hasOption("port") && args.hasOption("port2")) {
                startProxyServer(Integer.parseInt(args.getOptionValue("port")), Integer.parseInt(args.getOptionValue("port2")));
            } else {
                LOGGER.error("You must specify auth and game ports for the proxy server.");
            }
        } else if(args.hasOption("bot")) {
            startBot();
        } else if(args.hasOption("register") && args.hasOption("email") && args.hasOption("username") && args.hasOption("password")) {
            String email = args.getOptionValue("email");
            String username = args.getOptionValue("username");
            String password = args.getOptionValue("password");

            try {
                new RepositoryManager();
                Account account = new Account();
                account.setEmail(email);
                account.setUsername(username);
                account.setPassword(SHA256Hasher.hash(password));
                account.setCommunity(Community.FR);
                account.setSubscriptionLevel(0);
                account.setSubscriptionExpiration(0);
                account.setAntiAddictionLevel(0);
                account.setAccountCreation((int) System.currentTimeMillis());
                account.setAccountExpiration(-1);
                account.setBanDuration(-1);

                AccountManager.INSTANCE.saveAccount(account);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        } else {
            System.out.println("Usage: java -jar Incarnam.jar [--server | --game | --proxy] [--port | --port2]");
            return;
        }

        new RepositoryManager();

        if(this.authThread != null) {
            this.authThread.start();
        }

        if(this.gameThread != null) {
            this.gameThread.start();
        }

        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadProperties() {
        try {
            env.load(IncarnamApplication.class.getResourceAsStream("/application.properties"));
        } catch (Exception e) {
            LOGGER.error("Error while loading properties", e);
        }
    }

    private void startAuthServer(int port) {
        long start = System.currentTimeMillis();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).localAddress(port).childHandler(new BaseInitializer(new GameServerHandler(), Packet.PacketType.CLIENT_MSG));

        try {
            LOGGER.info("Auth Server Listening on port {}.", port);
            LOGGER.info("Auth Server took " + (System.currentTimeMillis() - start) + " milliseconds to start up.");
            b.bind().awaitUninterruptibly().channel().closeFuture().awaitUninterruptibly();
        } catch (Exception e) {
            LOGGER.error("Could not listen to port {}.", port, e);
        }
    }

    private void stopAuthServer() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        LOGGER.info("Auth Server Stopped.");
    }

    private void startGameServer(int port) {
        long start = System.currentTimeMillis();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup2, workerGroup2).channel(NioServerSocketChannel.class).localAddress(port).childHandler(new BaseInitializer(new GameServerHandler(), Packet.PacketType.CLIENT_MSG));

        try {
            LOGGER.info("Game Server Listening on port {}.", port);
            LOGGER.info("Game Server took " + (System.currentTimeMillis() - start) + " milliseconds to start up.");
            b.bind().awaitUninterruptibly().channel().closeFuture().awaitUninterruptibly();
        } catch (Exception e) {
            LOGGER.error("Could not listen to port {}.", port, e);
        }
    }

    private void stopGameServer() {
        bossGroup2.shutdownGracefully();
        workerGroup2.shutdownGracefully();
        LOGGER.info("Game Server Stopped.");
    }

    private void startProxyServer(int authPort, int gamePort) {
        long start = System.currentTimeMillis();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).localAddress(authPort).childHandler(new BaseInitializer(new ProxyClientHandler(), Packet.PacketType.CLIENT_MSG));

        ServerBootstrap b1 = new ServerBootstrap();
        b1.group(bossGroup2, workerGroup2).channel(NioServerSocketChannel.class).localAddress(gamePort).childHandler(new BaseInitializer(new ProxyGameClientServer(), Packet.PacketType.CLIENT_MSG));

        try {
            LOGGER.info("Proxy Server Listening on ports {} and {}.", authPort, gamePort);
            LOGGER.info("Proxy Server took " + (System.currentTimeMillis() - start) + " milliseconds to start up.");
            b.bind().awaitUninterruptibly().channel().closeFuture().awaitUninterruptibly();
            b1.bind().awaitUninterruptibly().channel().closeFuture().awaitUninterruptibly();
        } catch (Exception e) {
            LOGGER.error("Could not listen to port {} or {}.", authPort, gamePort, e);
        }
    }

    private void stopProxyServer() {
        bossGroup2.shutdownGracefully();
        workerGroup2.shutdownGracefully();
        LOGGER.info("Proxy Server Stopped.");
    }

    public void startBot() {
        long start = System.currentTimeMillis();
        Bootstrap b = new Bootstrap();
        b.group(bossGroup).channel(NioSocketChannel.class).remoteAddress("dispatch.platforms.wakfu.com", 5558).handler(new BaseInitializer(new BotClientHandler(), Packet.PacketType.SERVER_MSG));

        try {
            LOGGER.info("Bot connected to wakfu server.");
            LOGGER.info("Bot took " + (System.currentTimeMillis() - start) + " milliseconds to start up.");
            b.connect().sync().channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("Bot started.", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getProperty("jpa.properties.hibernate.dialect"));
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.current_session_context_class", "thread");
        properties.put("connection.driver_class", env.getProperty("datasource.driver-class-name"));
        properties.put("hibernate.connection.url", env.getProperty("datasource.url"));
        properties.put("hibernate.connection.username", env.getProperty("datasource.username"));
        properties.put("hibernate.connection.password", env.getProperty("datasource.password"));
        properties.put("hibernate.types.jackson.object.mapper", CustomObjectMapperSupplier.class.getName());

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(properties).build();
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);

        Reflections reflections = new Reflections("fr.redboxing.incarnam.database.entities");
        reflections.getTypesAnnotatedWith(Entity.class).forEach(metadataSources::addAnnotatedClass);

        Metadata metadata = metadataSources.buildMetadata();
        return metadata.getSessionFactoryBuilder().build();
    }
}
