package fr.redboxing.incarnam.crypto;

import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class SHA256Hasher {
    public static String hash(String input) throws NoSuchAlgorithmException {
        return hash(input, getSalt());
    }

    public static String hash(String input, String salt) throws NoSuchAlgorithmException {
        return hash(input, Hex.decode(salt));
    }

    public static String hash(String input, byte[] salt) {
        String hashed = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            sb.append(Hex.toHexString(salt));
            sb.append(":");
            sb.append(Hex.toHexString(bytes));

            hashed = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashed;
    }

    public static boolean validate(String input, String stored) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hashed = hash(input);
        String[] parts = hashed.split(":");
        String[] storedHashParts = stored.split(":");
        return storedHashParts[1].equals(parts[1]);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}
