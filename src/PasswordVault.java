import java.io.*;
import java.util.InputMismatchException;
import java.util.HashMap;
import java.util.Scanner;

public class PasswordVault {

    static String lineBreak =
            "--------------------------------------------------";

    private static String defaultPassword = "password";

    private static Scanner input = new Scanner(System.in);

    private String masterPassword;

    private static HashMap<String, Password> listOfPasswords = new HashMap<>();

    public PasswordVault(String masterPassword) {
        setMasterPassword(masterPassword);
    }

    public PasswordVault() {

    }

    public String getMasterPassword() {
        return masterPassword;
    }

    private void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
        System.out.println("The Master Password has been changed");
    }

    private void changeMasterPassword() {
        if (authUser()) {
            createMasterPassword();
        } else {
            System.out.println("The password you entered was incorrect");
            mainMenu();
        }
    }

    private void createMasterPassword() {
        System.out.println("Please set the master password");
        setMasterPassword(input.nextLine());
    }

    private boolean authUser() {
        boolean authorized = false;

        System.out.println("Please type the current master password");
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
            // TODO make sure this works to create file for the first time
            new FileWriter("data.txt", true);
//            BufferedWriter bufferedWriter = new BufferedWriter(writer);
//            bufferedWriter.write(defaultPassword);
//            bufferedWriter.close();
            setMasterPassword(defaultPassword);

            System.out.println("The default password sucks... (It's just 'password') Would you like to change it?");
            System.out.println("1) Yes please!");
            System.out.println("2) I'm sure it will be fine...");

            Scanner in = new Scanner(System.in);

            System.out.print("Your choice? ");
            int choice = in.nextInt();
            System.out.println();

            if (choice == 1) {
                createMasterPassword();
            } else {
                mainMenu();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public void addPassword() {
        String id, user, password;

        System.out.print("Enter id: ");
        id = input.nextLine();

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
    }

    public void listAllIds() {

    }

    public Password findPassword() {
        return null;
    }

    public void sharePassword(String id) {

    }

    public void readAllStoredPasswords() {

    }

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
        System.out.println();


        while (true) {
            try {
                Scanner in = new Scanner(System.in);

                System.out.print("Your choice? ");
                int choice = in.nextInt();
                System.out.println();

                switch (choice) {
                    case 1:
                        // TODO Add function for Add Password
                        break;
                    case 2:
                        // TODO Add function for List Ids
                        break;
                    case 3:
                        // TODO Add function for Find Password
                        break;
                    case 4:
                        // TODO Add function for Export Password
                        break;
                    case 5:
                        // TODO Add function for Change Master Password
                        break;
                    default:
                        System.out.println(choice + " is not a valid choice! Please enter a number from 1 to 5.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.err.println("Not a valid input. Error :" + e.getMessage());
                continue;
            }
        }
    }

}
