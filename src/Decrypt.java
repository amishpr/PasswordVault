import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Decrypt {

  public static String decryptText(String cipherEncryptText, String initializationVector, String secretKey)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
      InvalidAlgorithmParameterException, UnsupportedEncodingException,
      IllegalBlockSizeException, BadPaddingException {

    byte[] cipherText = DatatypeConverter.parseBase64Binary(cipherEncryptText);
    byte[] iv = DatatypeConverter.parseBase64Binary(initializationVector);
    byte[] secret_key = DatatypeConverter.parseBase64Binary(secretKey);

    IvParameterSpec receiver_iv = new IvParameterSpec(iv);
    SecretKey receiver_secret = new SecretKeySpec(secret_key, "AES");

    Cipher receiver_cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    receiver_cipher.init(Cipher.DECRYPT_MODE, receiver_secret, receiver_iv);

    String plaintext = new String(receiver_cipher.doFinal(cipherText), "UTF-8");

    return plaintext;
  }
}