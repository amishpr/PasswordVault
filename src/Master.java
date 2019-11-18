import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Master {

  private char[] masterPassword;
  private static String fileName = "master.txt";

  public static String getFileName() {
    return fileName;
  }

  static char[] hash(char[] masterPassword) throws NoSuchAlgorithmException {
    char[] salt = "TheBestSaltEver".toCharArray();
    char[] pepper = "AmishAndChristian".toCharArray();

    char[] saltedPassword = CharArrayUtils
        .concat(CharArrayUtils.concat(salt, masterPassword), pepper);

    CharArrayUtils.clear(salt);
    CharArrayUtils.clear(pepper);
    CharArrayUtils.clear(masterPassword);

    return CharArrayUtils.bytesToChars(Base64.getEncoder()
        .encode(MessageDigest.getInstance("SHA-256")
            .digest(CharArrayUtils.charsToBytes(saltedPassword))));
  }

  static void setMasterPassword(char[] masterPassword) {
    try {
      FileWriter writer = new FileWriter(fileName);
      BufferedWriter bufferedWriter = new BufferedWriter(writer);

      bufferedWriter.write(hash(masterPassword));
      bufferedWriter.newLine();
    } catch (IOException | NoSuchAlgorithmException e) {
      System.err.println("Error #00010"); // Error saving new master password.
    }
  }

  static char[] getMasterPassword() {
    char[] password = new char[1];
    try {
      FileReader reader = new FileReader(fileName);
      BufferedReader bufferedReader = new BufferedReader(reader);

      password = bufferedReader.readLine().toCharArray();
      reader.close();
    } catch (IOException e) {
      System.err.println("Error #00008"); // Error loading data file.
    }
    return password;
  }
}
