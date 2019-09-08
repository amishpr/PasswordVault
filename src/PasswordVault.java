import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
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
        System.out.println("List of ids: ");
        System.out.println("-------------");
        for (String id : listOfPasswords.keySet()) {
            System.out.println(id);
        }
    }

    public void findPassword() {
    }

    public void sharePassword() {
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

    public void setMasterPassword() {
        String newMasterPassword;

        System.out.print("Enter new master password: ");
        newMasterPassword = input.nextLine();

        StringBuilder oldFileContents = new StringBuilder();

        try {
            FileReader reader = new FileReader("data.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            bufferedReader.readLine(); // ignore first line / old master password

            while ((line = bufferedReader.readLine()) != null) {
                oldFileContents.append(line);
                oldFileContents.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading file contents.");
            e.printStackTrace();
        }

        try {
            FileOutputStream fileOut = new FileOutputStream("data.txt");
            fileOut.write(newMasterPassword.getBytes());
            fileOut.write(oldFileContents.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error saving new master password.");
            e.printStackTrace();
        }
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
