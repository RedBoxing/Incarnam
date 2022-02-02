package fr.redboxing.incarnam.utils;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class SSLUtils {
    public static SslContext generateSelfSignedCertificateForServer() throws SSLException, CertificateException {
        SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
        SslContextBuilder sslContextBuilder = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey());
        return sslContextBuilder.build();
    }

    public static SslContext generateSelfSignedCertificateForClient() throws SSLException {
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
        return sslContextBuilder.build();
    }
}
