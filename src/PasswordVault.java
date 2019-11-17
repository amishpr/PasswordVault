/*
 *   Christian Overton (cto5068@psu.edu) & Amish Prajapati (avp5564@psu.edu)
 *   Assignment 1
 *   CMPSC 444
 *   09/12/19
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.HashMap;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class PasswordVault {

    // ========================================
    // Config
    // ========================================
    private static String lineBreak =
            "--------------------------------------------------";

    private int attemptLimit = 5;
    private int sessionTimeLimit = 1; // in minutes

    // ========================================
    // Globals
    // ========================================

    private static Scanner input = new Scanner(System.in);

    private static String masterPassword;
    private static Timer timer = new Timer();
    private static Timestamp endSession;

    private static HashMap<String, Password> listOfPasswords = new HashMap<>();

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

    public PasswordVault() {

    }

    private void readAllStoredPasswords() {
        try {
          FileReader reader = new FileReader("data.txt");
          BufferedReader bufferedReader = new BufferedReader(reader);

          String line;

          bufferedReader.readLine(); // ignore first line / old master password

          while ((line = bufferedReader.readLine()) != null) {
            String id = line;
            String user = bufferedReader.readLine();
            String password = bufferedReader.readLine();
            String iv = bufferedReader.readLine();
            String secretKey = bufferedReader.readLine();

            Password currentPassword = new Password(id, user, password, iv, secretKey);
            listOfPasswords.put(id, currentPassword);
          }
          reader.close();
        } catch (IOException e) {
            System.err.println("Error #00008"); // Error loading data file.
          e.printStackTrace();
        }
    }

    // ========================================
    // Authorization
    // ========================================

    public String getMasterPassword() {
        return masterPassword;
    }

    private void setMasterPassword(char[] pass)
        throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
        NoSuchAlgorithmException, NoSuchPaddingException {
        masterPassword = Master.hashMasterPassword(pass).toString();

        ArrayList<String> oldFileContents = new ArrayList<>();

        try {
            FileReader reader = new FileReader("data.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            bufferedReader.readLine(); // ignore first line / old master password

            while ((line = bufferedReader.readLine()) != null) {
                oldFileContents.add(line);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading file contents.");
            e.printStackTrace();
        }

        try {
          FileWriter writer = new FileWriter("data.txt");
          BufferedWriter bufferedWriter = new BufferedWriter(writer);

          bufferedWriter.write(Master.hashMasterPassword(pass));
          bufferedWriter.newLine();

          for (String line : oldFileContents) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
          }
          bufferedWriter.close();

        } catch (IOException e) {
            System.err.println("Error #00010"); // Error saving new master password.
            e.printStackTrace();
        }
    }

    private void createMasterPassword()
        throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException {
        System.out.println("Please set the master password");
        setMasterPassword(input.nextLine().toCharArray());
    }

    private boolean authUser() throws NoSuchAlgorithmException {
        System.out.println("Please type the current master password");

        int currentAttempts = getAttemptLimit();
        while (currentAttempts > 0) {
            Console cons;
            char[] attempt;

            if ((cons = System.console()) != null) {
                attempt = cons.readPassword("[%s]", "Password:");
            } else {
                System.out.println("*WARNING* Your IDE does not support System.console(), using unsafe password read");
                System.out.println("Password:");
                attempt = input.nextLine().toCharArray();
            }

            // TODO fix this for char array
            char[] hashedAttempt = Master.hashMasterPassword(attempt);

            System.out.println("attempt: " + Arrays.toString(attempt));
            System.out.println("hashedAttempt: " + Arrays.toString(hashedAttempt));
            System.out.println("master: " + masterPassword);

            masterPassword = hashedAttempt.toString();

            if (Arrays.equals(hashedAttempt, masterPassword.toCharArray())) {
                CharArrayUtils.clear(attempt);
                return true;
            } else {
                CharArrayUtils.clear(attempt);
                currentAttempts--;
                System.out.println("Unfortunately that password is incorrect, you have " + currentAttempts + " attempt" + ((currentAttempts == 1) ? "" : "s") + " left");
            }
        }

        System.out.println("You have reached your maximum allowed attempts, the program will now exit");
        System.exit(-1);
        return false;
    }

    public void login()
        throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
        IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        try {
            FileReader reader = new FileReader("master.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String masterPwd = bufferedReader.readLine();

            System.out.println("Master: " + masterPwd);

            reader.close();

            if (authUser()) {
//                readAllStoredPasswords();
                new MainMenu();
            } else {
                System.out.println("The password you entered was incorrect");
                login();
            }

        } catch (FileNotFoundException e) {
            signUp();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void signUp()
        throws NoSuchPaddingException, IllegalBlockSizeException,
        BadPaddingException, NoSuchAlgorithmException, InvalidKeyException,
        InvalidAlgorithmParameterException {
        try {
            FileWriter writer = new FileWriter("data.txt", true);
            System.out.println("Looks like this is your first time");
            createMasterPassword();
            new MainMenu();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========================================
    // Main Menu and Options
    // ========================================

    void addPassword()
            throws NoSuchPaddingException, UnsupportedEncodingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        System.out.println("Add Password");
        System.out.println("====================");

        if (authUser()) {
            boolean complete = false;

            while(!complete) {
                String id, user;
                char[] passwd;

                System.out.print("Enter id: ");
                id = input.nextLine();

                if (!listOfPasswords.containsKey(id)) {
                    System.out.print("Enter user: ");
                    user = input.nextLine();

                    System.out.println("Would you like to generate a password? [Y/n]: ");
                    String response = input.nextLine().toUpperCase();

                    if (response.equals("Y") || response.equals("YES")) {
                        passwd = PasswordGenerator.generatePassword().toCharArray();
                    } else {
                        Console cons;
                        if ((cons = System.console()) != null) {
                            passwd = cons.readPassword("[%s]", "Password:");
                        } else {
                            System.out.println("*WARNING* Your IDE does not support System.console(), using unsafe password read");
                            System.out.println("Password:");
                            passwd = input.nextLine().toCharArray();
                        }
                    }
                    complete = true;
                } else {
                    System.err.println("Error #00011"); // id already exists
                }
            }
        } else {
            System.out.println("The password you entered was incorrect");
            new MainMenu();
        }

    }

    void listAllIds() {
        System.out.println("List of ids");
        System.out.println("====================");
        System.out.println(lineBreak);
        for (String id : listOfPasswords.keySet()) {
            System.out.println(id);
        }
    }

    void findPassword()
        throws NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException,
        NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
        InvalidAlgorithmParameterException
    {
        System.out.println("Find Password");
        System.out.println("====================");

        if (authUser()) {
            String id;
            boolean complete = false;

            while(!complete) {
                System.out.println("Enter id of password: ");
                id = input.nextLine();

                if (listOfPasswords.containsKey(id)) {
                    Password foundPassword = listOfPasswords.get(id);

                System.out.println("id = " + foundPassword.getId());
                System.out.println("user = " + foundPassword.getUser());
                System.out.println("password = " + foundPassword.getDecryptedPassword());

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

    void exportPassword()
            throws NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException {

        System.out.println("Export Password");
        System.out.println("====================");

        if (authUser()) {
            String id, fileName;
            boolean complete = false;

            while(!complete) {
                System.out.println("Enter id of password: ");
                id = input.nextLine();

                if (listOfPasswords.containsKey(id)) {

                    System.out.println("Enter file name: ");
                    fileName = input.nextLine();

                    try {
                        FileWriter writer = new FileWriter(fileName + ".txt", true);
                        BufferedWriter bufferedWriter = new BufferedWriter(writer);

                        Password sharedPassword = listOfPasswords.get(id);

                    EncryptedText encryptedSharedPassword = Encrypt.encryptText(sharedPassword.getPassword());

                    bufferedWriter.write("id=" + sharedPassword.getId());
                    bufferedWriter.newLine();
                    bufferedWriter.write("user=" + sharedPassword.getUser());
                    bufferedWriter.newLine();
                    bufferedWriter.write("password=" + encryptedSharedPassword.getCipherText());
                    bufferedWriter.newLine();
                    bufferedWriter.write("iv=" + encryptedSharedPassword.getInitializationVector());
                    bufferedWriter.newLine();
                    bufferedWriter.write("secretKey=" + encryptedSharedPassword.getSecretKey());
                    bufferedWriter.newLine();
//                    String plainText = Decrypt.decryptText(encryptedSharedPassword.getCipherText(),
//                        encryptedSharedPassword.getInitializationVector(), encryptedSharedPassword.getSecretKey());
//                    bufferedWriter.write("plainText=" + plainText);
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.err.println("Error creating shared password file.");
                    e.printStackTrace();
                }
                complete = true;
            } else {
                System.err.println("Error #00012"); // id not found.
            }
        } } else {
            System.out.println("The password you entered was incorrect");
            new MainMenu();
        }
    }

    void changeMasterPassword()
        throws NoSuchPaddingException, InvalidAlgorithmParameterException,
        UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException,
        NoSuchAlgorithmException, InvalidKeyException
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
