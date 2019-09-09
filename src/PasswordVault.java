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
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.HashMap;
import java.util.Scanner;

public class PasswordVault {

    private static String lineBreak =
            "--------------------------------------------------";

    private static String defaultPassword = "password";

    private static Scanner input = new Scanner(System.in);

    private static String masterPassword;

    private static HashMap<String, Password> listOfPasswords = new HashMap<>();

    public PasswordVault(String masterPassword) {
        setMasterPassword(masterPassword);
    }

    public PasswordVault() {

    }

    public class Password {
        String id;
        String user;
        String password;

        public Password(String id, String user, String password) {
            this.id = id;
            this.user = user;
            this.password = password;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public void readAllStoredPasswords() {
        try {
          FileReader reader = new FileReader("data.txt");
          BufferedReader bufferedReader = new BufferedReader(reader);

          String line;

          bufferedReader.readLine(); // ignore first line / old master password

          while ((line = bufferedReader.readLine()) != null) {
            String id = line;
            String user = bufferedReader.readLine();
            String password = bufferedReader.readLine();

            Password currentPassword = new Password(id, user, password);
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

    public String getMasterPassword() {
        return masterPassword;
    }

    private void setMasterPassword(String pass) {
        masterPassword = pass;

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

          bufferedWriter.write(masterPassword);
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

    private void createMasterPassword() {
        System.out.println("Please set the master password");
        setMasterPassword(input.nextLine());
    }

    private boolean authUser() {
        boolean authorized = false;

        System.out.println("Please type the current master password:");
        String attempt = input.nextLine();

        if (attempt.equals(masterPassword)) {
            authorized = true;
        }

        return authorized;
    }

    public void login() {
        try {
            FileReader reader = new FileReader("data.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            setMasterPassword(bufferedReader.readLine());

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

    public void signUp() {
        try {
            FileWriter writer = new FileWriter("data.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(defaultPassword);
            bufferedWriter.newLine();
            bufferedWriter.close();
            masterPassword = defaultPassword;

            System.out.println("The default password sucks... (It's just 'password') Would you like to change it?");
            System.out.println("1) Yes please!");
            System.out.println("2) I'm sure it will be fine...");

            Scanner in = new Scanner(System.in);

            System.out.print("Your choice: ");
            int choice = in.nextInt();
            System.out.println();

            if (choice == 1) {
                createMasterPassword();
            }
            mainMenu();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // ========================================
    // Main Menu and Options
    // ========================================

    public static void mainMenu() {
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

    private void addPassword() {
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

                Password currentPassword = new Password(id, user, password);

                listOfPasswords.put(id, currentPassword);

                try {
                    FileWriter writer = new FileWriter( "data.txt", true);
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);

                    bufferedWriter.write(id);
                    bufferedWriter.newLine();
                    bufferedWriter.write(user);
                    bufferedWriter.newLine();
                    bufferedWriter.write(password);
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

    private void findPassword() {
        System.out.println("Find Password");
        System.out.println("====================");

        String id;
        boolean complete = false;

        while(!complete) {
            System.out.println("Enter id of password: ");
            id = input.nextLine();

            if (listOfPasswords.containsKey(id)) {
                Password foundPassword = listOfPasswords.get(id);

                System.out.println("id = " + foundPassword.getId());
                System.out.println("user = " + foundPassword.getUser());
                System.out.println("password = " + foundPassword.getPassword());

                complete = true;
            } else {
                System.err.println("Error id not found.");
            }
        }
    }

    private void exportPassword() {
        System.out.println("Export Password");
        System.out.println("====================");

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

                    bufferedWriter.write("id=" + sharedPassword.getId());
                    bufferedWriter.newLine();
                    bufferedWriter.write("user=" + sharedPassword.getUser());
                    bufferedWriter.newLine();
                    bufferedWriter.write("password=" + sharedPassword.getPassword());
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.err.println("Error creating shared password file.");
                    e.printStackTrace();
                }

                complete = true;
            } else {
                System.err.println("Error id not found.");
            }
        }
    }

    private void changeMasterPassword() {
        System.out.println("Change Master Password");
        System.out.println("====================");

        if (authUser()) {
            createMasterPassword();
        } else {
            System.out.println("The password you entered was incorrect");
            mainMenu();
        }
    }
}
