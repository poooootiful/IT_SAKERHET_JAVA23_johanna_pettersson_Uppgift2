import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Security {
    public static String hashing(String pass) {
        String hashpass = BCrypt.hashpw(pass, BCrypt.gensalt());
        return hashpass;
    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    public static String encrypt(String message, SecretKey key) throws Exception {
        byte[] iv = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encryptedMessage = cipher.doFinal(message.getBytes());

        byte[] ivAndCiphertext = new byte[12 + encryptedMessage.length];
        System.arraycopy(iv, 0, ivAndCiphertext, 0, 12);
        System.arraycopy(encryptedMessage, 0, ivAndCiphertext,12, encryptedMessage.length);

        return Base64.getEncoder().encodeToString(ivAndCiphertext);
    }

    public static String decrypt(String encryptedMessage, SecretKey key) throws Exception {
        byte[] decodedMessage = Base64.getDecoder().decode(encryptedMessage);

        byte[] iv = new byte[12];
        System.arraycopy(decodedMessage, 0, iv, 0, 12);

        byte[] ciphertext = new byte[decodedMessage.length - 12];
        System.arraycopy(decodedMessage, 12, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decryptedMessage = cipher.doFinal(ciphertext);
        return new String(decryptedMessage);
    }
}
