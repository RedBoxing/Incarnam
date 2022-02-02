package fr.redboxing.incarnam.crypto;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;

public class RSACertificateManager {
    public static final RSACertificateManager INSTANCE = new RSACertificateManager();

    private SecureRandom random;
    private KeyPairGenerator kpGen;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private RSACryptoCipher encrypt;
    private RSACryptoCipher decrypt;

    public RSACertificateManager() {
        random = new SecureRandom();

        try {
            kpGen = KeyPairGenerator.getInstance("RSA");
            kpGen.initialize(1024, random);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        KeyPair pair = kpGen.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();

        encrypt = new RSACryptoCipher("RSA", RSACryptoCipher.parameterSpec);
        decrypt = new RSACryptoCipher("RSA", RSACryptoCipher.parameterSpec);

        encrypt.init(publicKey.getEncoded());
        decrypt.initRSA(((RSAPrivateKey)privateKey).getModulus(), ((RSAPrivateKey)privateKey).getPrivateExponent());
    }

    public byte[] encode(byte[] data) {
        return encrypt.encode(data);
    }

    public byte[] decode(byte[] data) {
        return decrypt.decode(data);
    }

    public byte[] getPublicKey() {
        return publicKey.getEncoded();
    }
}
