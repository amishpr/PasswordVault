public class EncryptedText {

  private String cipherText;
  private String initializationVector;
  private String secretKey;

  public EncryptedText(String cipherText, String initializationVector, String secretKey) {
    this.cipherText = cipherText;
    this.initializationVector = initializationVector;
    this.secretKey = secretKey;
  }

  public String getCipherText() {
    return cipherText;
  }

  public void setCipherText(String cipherText) {
    this.cipherText = cipherText;
  }

  public String getInitializationVector() {
    return initializationVector;
  }

  public void setInitializationVector(String initializationVector) {
    this.initializationVector = initializationVector;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }
}
