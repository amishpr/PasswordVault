import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class EncryptedPassword implements Serializable {

  private int id;
  private char[] cipherText;

  private static String fileName = "passwords.txt";

  public EncryptedPassword(int id, char[] cipherText) {
    this.id = id;
    this.cipherText = cipherText;
  }

  public int getLastId() throws IOException {
    int lastId = 0;
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
        System.out.println("Object has been deserialized ");
        System.out.println("id = " + obj.id);
        System.out.println("user = " + Arrays.toString(obj.cipherText));

        obj.id = lastId;
      } else {
        cont = false;
      }
    }

    return lastId;
  }

  public char[] getPassword(int id) throws IOException {
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

        if (id == obj.id) {
          cipherText = obj.cipherText;

          // Clear obj
          obj.id = 0;
          CharArrayUtils.clear(obj.cipherText);
          cont = false;
        }

      } else {
        cont = false;
      }
    }
    return cipherText;
  }

  public void addPassword(char[] user, char[] password) throws IOException {

    FileInputStream fileIn;
    ObjectInputStream in;

    boolean cont = true;
    // Reading the object from a file
    fileIn = new FileInputStream(fileName);
    in = new ObjectInputStream(fileIn);

    EncryptedPassword obj = null;

    int lastId = getLastId();

    //Saving of password1 in a file
    FileOutputStream fileOut = new FileOutputStream(fileName);
    ObjectOutputStream out = new ObjectOutputStream(fileOut);

    char[] spaceCharacter = " ".toCharArray();
    user = CharArrayUtils.concat(user, spaceCharacter);

    char[] encryptedText = CharArrayUtils.concat(user, password);
    out.writeObject(new EncryptedPassword(lastId + 1, encryptedText));

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
