import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class Encrypt {

  public static EncryptedText encryptText(String plainText) throws NoSuchAlgorithmException,
      NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
      BadPaddingException, UnsupportedEncodingException
  {
    // Create Key
    KeyGenerator keygen = KeyGenerator.getInstance("AES");
    SecretKey aesKey = keygen.generateKey();

    // Encrypt Message
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
    byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));
    byte[] iv = cipher.getIV();
    byte[] secret = aesKey.getEncoded();

    String cipherTextString = DatatypeConverter.printBase64Binary(cipherText);
    String initializationVectorString = DatatypeConverter.printBase64Binary(iv);
    String secretKeyString = DatatypeConverter.printBase64Binary(secret);

    return new EncryptedText(cipherTextString, initializationVectorString, secretKeyString);
  }
}
