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

    private static int attemptLimit = 5;
    private static int sessionTimeLimit = 1; // in minutes

    // ========================================
    // Globals
    // ========================================

    private static Scanner input = new Scanner(System.in);

    private static String masterPassword;
    private static Timer timer = new Timer();
    private static Timestamp endSession;

    private static HashMap<String, Password> listOfPasswords = new HashMap<>();

    public PasswordVault(String masterPassword)
        throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException {
        setMasterPassword(masterPassword);
    }

    public PasswordVault() {

    }

    public class Password {
        String id;
        String user;
        String password;
        String iv;
        String secretKey;

        public Password(String id, String user, String password, String iv, String secretKey) {
            this.id = id;
            this.user = user;
            this.password = password;
            this.iv = iv;
            this.secretKey = secretKey;
        }

        public String getId() {
            return id;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public String getIv() {
            return iv;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public String getDecryptedPassword()
            throws NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, IllegalBlockSizeException,
            UnsupportedEncodingException, InvalidKeyException
        {
            return Decrypt.decryptText(password, iv, secretKey);
        }
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
          System.err.println("Error loading data file.");
          e.printStackTrace();
        }
    }

    // ========================================
    // Authorization
    // ========================================

    public String hashMasterPassword(String masterPassword) throws NoSuchAlgorithmException {
        String salt = "TheBestSaltEver";
        String saltedPassword = salt + masterPassword;

        return new String(Base64.getEncoder()
                .encode(MessageDigest.getInstance("SHA-256")
                .digest(saltedPassword.getBytes(StandardCharsets.UTF_8))));
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    private void setMasterPassword(String pass)
        throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
        NoSuchAlgorithmException, NoSuchPaddingException {
        masterPassword = hashMasterPassword(pass);

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

          bufferedWriter.write(hashMasterPassword(pass));
          bufferedWriter.newLine();

          for (String line : oldFileContents) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
          }
          bufferedWriter.close();

        } catch (IOException e) {
            System.err.println("Error saving new master password.");
            e.printStackTrace();
        }
    }

    private void createMasterPassword()
        throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException {
        System.out.println("Please set the master password");
        setMasterPassword(input.nextLine());
    }

    private boolean authUser() throws NoSuchAlgorithmException {
        System.out.println("Please type the current master password:");

        while (attemptLimit > 0) {
            String attempt = input.nextLine();
            String hashedAttempt = hashMasterPassword(attempt);
            if (hashedAttempt.equals(masterPassword)) {
                return true;
            } else {
                attemptLimit--;
                System.out.println("Unfortunately that password is incorrect, you have " + attemptLimit + " attempt" + ((attemptLimit == 1) ? "" : "s") + " left");

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
            FileReader reader = new FileReader("data.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String masterPwd = bufferedReader.readLine();
//            String iv = bufferedReader.readLine();
//            String secretKey = bufferedReader.readLine();

//            String decryptedPwd = Decrypt.decryptText(masterPwd, iv, secretKey);

//            setMasterPassword();

            masterPassword = masterPwd;

            reader.close();

            if (authUser()) {
                readAllStoredPasswords();
                mainMenu();
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
            mainMenu();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========================================
    // Main Menu and Options
    // ========================================

    public static void mainMenu()
        throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException,
        BadPaddingException, NoSuchAlgorithmException, InvalidKeyException,
        InvalidAlgorithmParameterException
    {
        extendSession();

        // Display menu
        System.out.println(lineBreak);
        System.out.println("Main Menu");
        System.out.println();
        System.out.println("Choose an option.");
        System.out.println("1) Add Password");
        System.out.println("2) List Ids");
        System.out.println("3) Find Password");
        System.out.println("4) Export Password");
        System.out.println("5) Change Master Password");
        System.out.println("6) Exit");
        System.out.println();

        PasswordVault vault = new PasswordVault();

        boolean next = false;

        while (!next) {
            try {

                System.out.print("Your choice: ");
                Scanner in = new Scanner(System.in);
                int choice = in.nextInt();
                System.out.println();

                switch (choice) {
                    case 1:
                        vault.addPassword();
                        next = true;
                        break;
                    case 2:
                        vault.listAllIds();
                        next = true;
                        break;
                    case 3:
                        vault.findPassword();
                        next = true;
                        break;
                    case 4:
                        vault.exportPassword();
                        next = true;
                        break;
                    case 5:
                        vault.changeMasterPassword();
                        next = true;
                        break;
                    case 6:
                        System.exit(0);
                        break;
                    default:
                        System.out.println(choice + " is not a valid choice! Please enter a number from 1 to 6.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.err.println("Not a valid input. Error :" + e.getMessage());
                continue;
            }
        }

        mainMenu();
    }

    private void addPassword()
        throws NoSuchPaddingException, UnsupportedEncodingException, NoSuchAlgorithmException,
        IllegalBlockSizeException, BadPaddingException, InvalidKeyException
    {
        System.out.println("Add Password");
        System.out.println("====================");
        boolean complete = false;

        while(!complete) {
            String id, user, password;

            System.out.print("Enter id: ");
            id = input.nextLine();

            if (!listOfPasswords.containsKey(id)) {
                System.out.print("Enter user: ");
                user = input.nextLine();

                System.out.println("Would you like to generate a password? [Y/n]: ");
                String response = input.nextLine().toUpperCase();

                if (response.equals("Y") || response.equals("YES")) {
                    password = PasswordGenerator.generatePassword();
                } else {
                    System.out.println("Enter password: ");
                    password = input.nextLine();
                }

                EncryptedText encryptedCurrentPassword = Encrypt.encryptText(password);

                Password currentPassword = new Password(id, user,
                    encryptedCurrentPassword.getCipherText(),
                    encryptedCurrentPassword.getInitializationVector(),
                    encryptedCurrentPassword.getSecretKey());

                listOfPasswords.put(id, currentPassword);

                try {
                    FileWriter writer = new FileWriter( "data.txt", true);
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);

                    bufferedWriter.write(id);
                    bufferedWriter.newLine();
                    bufferedWriter.write(user);
                    bufferedWriter.newLine();
                    bufferedWriter.write(encryptedCurrentPassword.getCipherText());
                    bufferedWriter.newLine();
                    bufferedWriter.write(encryptedCurrentPassword.getInitializationVector());
                    bufferedWriter.newLine();
                    bufferedWriter.write(encryptedCurrentPassword.getSecretKey());
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.err.println("Error saving password to file.");
                    e.printStackTrace();
                }

                complete = true;
            } else {
                System.err.println("Error id already exists!");
            }
        }
    }

    private void listAllIds() {
        System.out.println("List of ids");
        System.out.println("====================");
        System.out.println(lineBreak);
        for (String id : listOfPasswords.keySet()) {
            System.out.println(id);
        }
    }

    private void findPassword()
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
                    System.err.println("Error id not found.");
                }
            }
        } else {
            System.out.println("The password you entered was incorrect");
            mainMenu();
        }


    }

    private void exportPassword()
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
                System.err.println("Error id not found.");
            }
        } } else {
            System.out.println("The password you entered was incorrect");
            mainMenu();
        }
    }

    private void changeMasterPassword()
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
            mainMenu();
        }
    }

    // ========================================
    // Session Timeout
    // ========================================

    private static void extendSession() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        endSession = new Timestamp(now.getTime() + TimeUnit.MINUTES.toMillis(sessionTimeLimit));
        TimerTask task = new checkActiveSession();
        timer.schedule(task, TimeUnit.MINUTES.toMillis(sessionTimeLimit));
    }

    private static class checkActiveSession extends TimerTask {
        public void run() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (endSession.getTime() <= now.getTime()) {
                System.out.println("Your Session has timed out");
                System.exit(-1);
            }
        }
    }
}
