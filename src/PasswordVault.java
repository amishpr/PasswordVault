/*
 *   Christian Overton (cto5068@psu.edu) & Amish Prajapati (avp5564@psu.edu)
 *   Assignment 1
 *   CMPSC 444
 *   09/12/19
 */

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class PasswordVault {

  // ========================================
  // Config
  // ========================================
  private static String lineBreak = "--------------------------------------------------";

  private int attemptLimit = 5;
  private int sessionTimeLimit = 1; // in minutes

  // ========================================
  // Globals
  // ========================================

  public int getAttemptLimit() {
    return attemptLimit;
  }

  public int getSessionTimeLimit() {
    return sessionTimeLimit;
  }

  public PasswordVault(int attemptLimit, int sessionTimeLimit) {
    this.attemptLimit = attemptLimit;
    this.sessionTimeLimit = sessionTimeLimit;
  }

  public PasswordVault() {}

  // ========================================
  // Authorization
  // ========================================

  private char[] getSecureInput() {

    Scanner input = new Scanner(System.in);

    Console cons;
    char[] line;

    if ((cons = System.console()) != null) {
      line = cons.readPassword("[%s]", "Password:");
    } else {
      System.out.println(
          "*WARNING* Your IDE does not support System.console(), using unsafe password read");
      System.out.println("Password:");
      line = input.nextLine().replaceAll("\\s+", "").toCharArray();
    }

    return line;
  }

  private String getUnsecuredInput() {
    Scanner input = new Scanner(System.in);
    return input.nextLine().replaceAll("\\s+", "");
  }

  private void createMasterPassword() {
    System.out.println("Please set the master password");

    char[] master = getSecureInput();
    Master.setMasterPassword(master);

    // Clear
    CharArrayUtils.clear(master);
  }

  private boolean authUser() throws NoSuchAlgorithmException {
    System.out.println("Please type the current master password");

    int currentAttempts = getAttemptLimit();
    while (currentAttempts > 0) {

      char[] attempt = getSecureInput();
      char[] hashedAttempt = Master.hash(attempt);

      if (Arrays.equals(hashedAttempt, Master.getMasterPassword())) {
        // Clear
        CharArrayUtils.clear(attempt);
        CharArrayUtils.clear(hashedAttempt);

        return true;
      } else {
        CharArrayUtils.clear(attempt);
        currentAttempts--;
        System.out.println(
            "Unfortunately that password is incorrect, you have "
                + currentAttempts
                + " attempt"
                + ((currentAttempts == 1) ? "" : "s")
                + " left");
      }
    }

    System.out.println("You have reached your maximum allowed attempts, the program will now exit");
    System.exit(-1);
    return false;
  }

  public void login() throws NoSuchAlgorithmException {
    if (authUser()) {
      new MainMenu();
    } else {
      System.out.println("The password you entered was incorrect");
      login();
    }
  }

  public void signUp()
      throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException {
    System.out.println("Looks like this is your first time");
    createMasterPassword();
    new MainMenu();
  }

  // ========================================
  // Main Menu and Options
  // ========================================

  void addPassword() throws IOException, NoSuchAlgorithmException {
    System.out.println("Add Password");
    System.out.println("====================");

    if (authUser()) {
      boolean complete = false;

      while (!complete) {

        System.out.print("Enter id: ");
        char[] id = getSecureInput();

        List<char[]> listOfIds = EncryptedPassword.getListOfIds();

        if (!listOfIds.contains(id)) {
          System.out.print("Enter user: ");
          char[] user = getSecureInput();

          System.out.println("Would you like to generate a password? [Y/n]: ");
          String response = getUnsecuredInput();

          char[] password = {};

          if (response.equals("Y") || response.equals("YES")) {
            password = PasswordGenerator.generatePassword().toCharArray();
          } else {
            password = getSecureInput();
          }

          EncryptedPassword.addPassword(id, user, password);

          // Clear
          CharArrayUtils.clear(id);
          CharArrayUtils.clear(user);
          CharArrayUtils.clear(password);

          complete = true;
        } else {
          System.out.println("The id you entered already exists");
        }
      }
    } else {
      System.out.println("The password you entered was incorrect");
      new MainMenu();
    }
  }

  void listAllIds() throws IOException {
    System.out.println("List of ids");
    System.out.println("====================");
    System.out.println(lineBreak);
    for (char[] id : EncryptedPassword.getListOfIds()) {
      System.out.println(id);
      CharArrayUtils.clear(id);
    }
  }

  void findPassword() throws Exception {
    System.out.println("Find Password");
    System.out.println("====================");

    if (authUser()) {
      boolean complete = false;

      while (!complete) {
        System.out.println("Enter id of password: ");
        char[] id = getSecureInput();

        List<char[]> listOfIds = EncryptedPassword.getListOfIds();

        if (listOfIds.contains(id)) {
          char[] foundPassword = EncryptedPassword.getCipherText(id);

          System.out.print("id = ");
          System.out.println(id);

          char[] decryptedCipherText = Decrypt.decrypt(id, foundPassword);
          CharArrayUtils.clear(id);

          List<char[]> spiltList = CharArrayUtils.spilt(decryptedCipherText);
          CharArrayUtils.clear(decryptedCipherText);

          System.out.print("user = ");
          System.out.println(spiltList.get(0));

          System.out.print("password = ");
          System.out.print(spiltList.get(1));

          CharArrayUtils.clearList(spiltList);

          complete = true;
        } else {
          System.err.println("There are no passwords with that id"); // id not found.
        }
      }
    }
  }

  private void exportPassword() throws Exception {

    System.out.println("Export Password");
    System.out.println("====================");

    if (authUser()) {
      boolean complete = false;

      while (!complete) {
        System.out.println("Enter id of password: ");
        char[] id = getSecureInput();

        if (EncryptedPassword.getListOfIds().contains(id) && CheckCert.checkFriendCert()) {

          System.out.println("Enter file name: ");
          char[] fileName = getUnsecuredInput().toCharArray();

          try {
            FileWriter writer = new FileWriter(Arrays.toString(fileName), true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            // Write id to file
            bufferedWriter.write("id=");
            bufferedWriter.write(id);
            bufferedWriter.newLine();

            // Decrypt username and password
            char[] cipherText = EncryptedPassword.getCipherText(id);
            char[] decryptedCipherText = Decrypt.decrypt(id, cipherText);

            List<char[]> spiltList = CharArrayUtils.spilt(decryptedCipherText);
            CharArrayUtils.clear(decryptedCipherText);

            // Clear id
            CharArrayUtils.clear(id);

            bufferedWriter.write("user=");
            bufferedWriter.write(spiltList.get(0)); // username
            bufferedWriter.newLine();
            bufferedWriter.write("password=");
            bufferedWriter.write(spiltList.get(1)); // password
            bufferedWriter.newLine();
            bufferedWriter.close();

            CharArrayUtils.clearList(spiltList); // Clear username and password

          } catch (IOException e) {
            System.err.println("Error creating shared password file.");
            e.printStackTrace();
          }
          complete = true;
        } else {
          System.err.println("Error #00012"); // id not found.
        }
      }
    } else {
      System.out.println("The password you entered was incorrect");
      new MainMenu();
    }
  }

  public void changeMasterPassword()
      throws NoSuchAlgorithmException
  {
    System.out.println("Change Master Password");
    System.out.println("====================");

    if (authUser()) {
      createMasterPassword();
    } else {
      System.out.println("The password you entered was incorrect");
      new MainMenu();
    }
  }
}
