import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncryptedPassword implements Serializable {

  private char[] id;
  private char[] cipherText;

  private static String fileName = "passwords.txt";

  public static String getFileName() {
    return fileName;
  }

  private EncryptedPassword(char[] id, char[] cipherText) {
    this.id = id;
    this.cipherText = cipherText;
  }

  public static List<char[]> getListOfIds() throws IOException {

    List<char[]> idList = new ArrayList<>();

    boolean cont = true;
    EncryptedPassword obj = null;

    FileInputStream fileIn = new FileInputStream(fileName);
    ObjectInputStream in = new ObjectInputStream(fileIn);

    while (cont) {
      try {
        obj = (EncryptedPassword) in.readObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      if (obj != null) {
        idList.add(obj.id);
      } else {
        cont = false;
      }
    }
    return idList;
  }

  public static char[] getCipherText(char[] id) throws IOException {
    FileInputStream file;
    ObjectInputStream in;

    boolean cont = true;
    // Reading the object from a file
    file = new FileInputStream(fileName);
    in = new ObjectInputStream(file);

    EncryptedPassword obj = null;
    char[] cipherText = {};
    while (cont) {
      try {
        obj = (EncryptedPassword) in.readObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      if (obj != null) {
        System.out.println("Object has been deserialized ");
        System.out.println("id = " + obj.id);
        System.out.println("user = " + Arrays.toString(obj.cipherText));

        if (Arrays.equals(id, obj.id)) {
          cipherText = obj.cipherText;

          // Clear obj
          CharArrayUtils.clear(obj.id);
          CharArrayUtils.clear(obj.cipherText);

          cont = false;
        }

      } else {
        cont = false;
      }
    }
    return cipherText;
  }

   public static void addPassword(char[] id, char[] user, char[] password) throws IOException {

    FileInputStream fileIn;
    ObjectInputStream in;

    boolean cont = true;
    // Reading the object from a file
    fileIn = new FileInputStream(fileName);
    in = new ObjectInputStream(fileIn);

    EncryptedPassword obj = null;

    //Saving of password1 in a file
    FileOutputStream fileOut = new FileOutputStream(fileName);
    ObjectOutputStream out = new ObjectOutputStream(fileOut);

    char[] spaceCharacter = " ".toCharArray();
    user = CharArrayUtils.concat(user, spaceCharacter);

    char[] encryptedText = CharArrayUtils.concat(user, password);
    out.writeObject(new EncryptedPassword(id, encryptedText));

    // Clear out char[]
    CharArrayUtils.clear(user);
    CharArrayUtils.clear(password);
    CharArrayUtils.clear(encryptedText);
    CharArrayUtils.clear(spaceCharacter);

    out.close();
    fileOut.close();

    System.out.println("Object has been serialized");

  }

}
