import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {

  static SecretKey getSecretKey(byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    // Make the secret key using the master password
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
    KeySpec passwordBasedEncryptionKeySpec = new PBEKeySpec(Master.getMasterPassword(), salt, 10000, 256);
    SecretKey secretKeyFromPBKDF2 = secretKeyFactory.generateSecret(passwordBasedEncryptionKeySpec);

    return new SecretKeySpec(secretKeyFromPBKDF2.getEncoded(), "AES");
  }

  static IvParameterSpec getInitializationVector(byte[] salt) {
    return new IvParameterSpec(Arrays.copyOfRange(salt, 0, 16));
  }

  public static char[] encryptText(char[] id, char[] plainText) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
          BadPaddingException, IOException, InvalidKeySpecException, InvalidAlgorithmParameterException {

    // Take the id, hash it, convert it to a byte[], and use it as a salt
    byte[] salt = CharArrayUtils.charsToBytes(Master.hash(id));

    SecretKey secretKey = getSecretKey(salt);

    // Make initializationVector using the hashed id/salt
    IvParameterSpec initializationVector = getInitializationVector(salt);

    // Make Cipher
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, initializationVector);

    byte[] cipherTextBytes = cipher.doFinal(CharArrayUtils.charsToBytes(plainText));

    char[] encryptedText = Base64.getEncoder().encodeToString(cipherTextBytes).toCharArray();

    // Clear
    cipher = null;
    secretKey = null;
    initializationVector = null;
    CharArrayUtils.clear(id);
    CharArrayUtils.clearBytes(cipherTextBytes);

    return encryptedText;
  }

}
