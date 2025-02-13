package utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;


public class PasswordUtils {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String salt = generateSalt();
        byte[] hash = hashPasswordWithSalt(password, salt);
        return Base64.getEncoder().encodeToString(hash) + ":" + salt;
    }

    public static boolean verifyPassword(String inputPassword, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid stored hash format");
            }
            String storedPasswordHash = parts[0];
            String storedSalt = parts[1];

            byte[] inputHash = hashPasswordWithSalt(inputPassword, storedSalt);
            String inputHashBase64 = Base64.getEncoder().encodeToString(inputHash);

            return storedPasswordHash.equals(inputHashBase64);
        } catch (Exception e) {
            throw new RuntimeException("Error while verifying password", e);
        }
    }

    private static byte[] hashPasswordWithSalt(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return keyFactory.generateSecret(spec).getEncoded();
    }
}




