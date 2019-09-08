import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class PasswordVault {

    public static String defaultPassword = "password";

    public static Scanner input = new Scanner(System.in);

    private String masterPassword;

    public static HashMap<String, Password> listOfPasswords = new HashMap<>();

    public PasswordVault(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public PasswordVault() {

    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
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

    public String generatePassword() {
        return "yeahBoi";
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
            password = generatePassword();
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

    public void changeMasterPassword(String newMasterPassword) {

    }

    public void readAllStoredPasswords() {

    }

    public static void main(String[] args) throws IOException {

        PasswordVault passwordVault = new PasswordVault();

        // ========================================
        // Intro Section
        // ========================================

        System.out.println("Welcome to password master 5000 mark 23!");

        System.out.println("Please enter the master password:");

        String masterPasswordAttempt = input.nextLine();

        try {
            FileReader reader = new FileReader("data.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            // Set first line of txt file as the master password
            passwordVault.setMasterPassword(bufferedReader.readLine());

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();

        } catch (IOException e) {
            FileWriter writer = new FileWriter("data.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(defaultPassword);
            bufferedWriter.close();
        }

        // ========================================
        //
        // ========================================

    }


}
