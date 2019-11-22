public class UnencryptedPassword {
  private char[] id;
  private char[] user;
  private char[] password;

  public UnencryptedPassword(char[] id, char[] user, char[] password) {
    this.id = id;
    this.user = user;
    this.password = password;
  }

  public char[] getId() {
    return id;
  }

  public char[] getUser() {
    return user;
  }

  public char[] getPassword() {
    return password;
  }
}
