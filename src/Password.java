import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Password {
  private String id;
  private String user;
  private String password;
  private String iv;
  private String secretKey;

  Password(String id, String user, String password, String iv, String secretKey) {
    this.id = id;
    this.user = user;
    this.password = password;
    this.iv = iv;
    this.secretKey = secretKey;
  }

  String getId() {
    return id;
  }

  String getUser() {
    return user;
  }

  String getPassword() {
    return password;
  }

  public String getIv() {
    return iv;
  }

  public String getSecretKey() {
    return secretKey;
  }

  String getDecryptedPassword()
      throws NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException,
      NoSuchAlgorithmException, IllegalBlockSizeException,
      UnsupportedEncodingException, InvalidKeyException
  {
    return Decrypt.decryptText(password, iv, secretKey);
  }
}