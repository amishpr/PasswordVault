import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

  private static List<EncryptedPassword> getAllEncryptedPasswordObjects() {
    boolean cont = true;
    EncryptedPassword tmpObj = null;

    List<EncryptedPassword> encryptedPasswordList = new ArrayList<>();

    try {
      FileInputStream fileIn = new FileInputStream(fileName);
      ObjectInputStream in = new ObjectInputStream(fileIn);

      while (cont) {
        try {
          tmpObj = (EncryptedPassword) in.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("Error Code: #00007");
        }
        if (tmpObj != null) {
          encryptedPasswordList.add(tmpObj);
        } else {
          cont = false;
        }
      }
      fileIn.close();
      in.close();
    } catch (EOFException e) {
      return encryptedPasswordList;
    } catch (IOException e) {
        System.out.println("Error code: #00001");
    }
    return encryptedPasswordList;
  }

  static List<char[]> getListOfIds() throws IOException {

    List<char[]> idList = new ArrayList<>();

    boolean cont = true;
    EncryptedPassword obj = null;

    try {
        FileInputStream fileIn = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(fileIn);

        while (cont) {
            try {
                obj = (EncryptedPassword) in.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Error Code: #00007");
            }
            if (obj != null) {
                idList.add(obj.id);
            } else {
                cont = false;
            }
        }
        fileIn.close();
        in.close();
        return idList;
    } catch (EOFException e) {
        return idList;
    }
  }

  static char[] getCipherText(char[] id) throws IOException {
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
          System.out.println("Error Code: #00007");
      }
      if (obj != null) {
//        System.out.println("Object has been deserialized ");
//        System.out.println("id = " + obj.id);
//        System.out.println("user = " + Arrays.toString(obj.cipherText));

        if (Arrays.equals(id, obj.id)) {
          cipherText = obj.cipherText;

          // Clear obj
//            CharArrayUtils.clear(obj.id);
//            CharArrayUtils.clear(obj.cipherText);

          cont = false;
        }

      } else {
        cont = false;
      }
    }
    return cipherText;
  }

  static void createPassFile() throws IOException {
      try {
          new FileReader(fileName);
      } catch (FileNotFoundException e) {
          FileWriter writer = new FileWriter(fileName);
          BufferedWriter bufferedWriter = new BufferedWriter(writer);
          bufferedWriter.close();
      }
  }

  static void rewritePasswords(char[] newMasterPassword)
      throws Exception {

    List<EncryptedPassword> encryptedPasswordList = getAllEncryptedPasswordObjects();
    if (encryptedPasswordList.size() > 0) {
      List<UnencryptedPassword> unencryptedPasswordList = new ArrayList<>();
      List<EncryptedPassword> newEncryptedPasswordList = new ArrayList<>();

      for(EncryptedPassword encryptedPassword: encryptedPasswordList) {

        char[] id = encryptedPassword.id;

        char[] decryptedCipherText = EncryptDecrypt.decrypt(id, encryptedPassword.cipherText);

        List<char[]> spiltList = CharArrayUtils.spilt(decryptedCipherText);

        char[] username = spiltList.get(0);
        char[] password = spiltList.get(1);

        unencryptedPasswordList.add(new UnencryptedPassword(id, username, password));

      }

      // Set new master password
      Master.setMasterPassword(newMasterPassword);

      for(UnencryptedPassword unencryptedPassword : unencryptedPasswordList) {
        EncryptedPassword newEncryptedPassword = makePasswordObject(unencryptedPassword.getId(), unencryptedPassword
            .getUser(), unencryptedPassword.getPassword());
        newEncryptedPasswordList.add(newEncryptedPassword);
      }

      // Clear passwords.txt
      PrintWriter writer = new PrintWriter(fileName);
      writer.print("");
      writer.close();

      saveListOfPasswordsToFile(newEncryptedPasswordList);
    }
  }

   private static EncryptedPassword makePasswordObject(char[] id, char[] user, char[] password)
       throws NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {

     char[] spaceCharacter = " ".toCharArray();
     user = CharArrayUtils.concat(user, spaceCharacter);

     char[] encryptedText = CharArrayUtils.concat(user, password);

     return new EncryptedPassword(id, EncryptDecrypt.encryptText(id, encryptedText));

   }

   private static void saveListOfPasswordsToFile(List<EncryptedPassword> encryptedPasswordList)
       throws IOException {

     //Saving of password in a file
     FileOutputStream fileOut = new FileOutputStream(fileName);
     ObjectOutputStream out = new ObjectOutputStream(fileOut);

     for (EncryptedPassword encryptedPass: encryptedPasswordList) {
       out.writeObject(encryptedPass);
     }

     out.close();
     fileOut.close();
   }

   static void addPassword(char[] id, char[] user, char[] password) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {

    List<EncryptedPassword> encryptedPasswordList = getAllEncryptedPasswordObjects();

    EncryptedPassword encryptedPassword = makePasswordObject(id, user, password);

    encryptedPasswordList.add(encryptedPassword);

    saveListOfPasswordsToFile(encryptedPasswordList);

//    // Clear out char[]
//    CharArrayUtils.clear(user);
//    CharArrayUtils.clear(password);
//    CharArrayUtils.clear(encryptedText);
//    CharArrayUtils.clear(spaceCharacter);

    System.out.println("Password saved to file.");
  }

}
