import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Decrypt {

  public static char[] decrypt(char[] id, char[] encryptedText) throws Exception {

    // Take the id, hash it, convert it to a byte[], and use it as a salt
    byte[] salt = CharArrayUtils.charsToBytes(Master.hashMasterPassword(id));

    SecretKey secretKey = Encrypt.getSecretKey(salt);

    // Make initializationVector using the hashed id/salt
    IvParameterSpec initializationVector = Encrypt.getInitializationVector(salt);

    // Make Cipher
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, secretKey, initializationVector);

    byte[] decryptedCipherTextBytes = cipher.doFinal(Base64.getDecoder().decode(CharArrayUtils.charsToBytes(encryptedText)));

    char[] decryptedCipherText = CharArrayUtils.bytesToChars(decryptedCipherTextBytes);

    // Clear values
    cipher = null;
    secretKey = null;
    initializationVector = null;
    CharArrayUtils.clear(id);
    CharArrayUtils.clearBytes(decryptedCipherTextBytes);

    return decryptedCipherText;
  }
}