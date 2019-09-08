import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PasswordVault {

    public static String defaultPassword = "password";

    public static Scanner input = new Scanner(System.in);

    private String masterPassword;

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

    public static void main(String[] args) throws IOException {

        PasswordVault passwordVault = new PasswordVault();

        System.out.println("Welcome to password master 5000 mark 23!");

        System.out.println("Please enter the master password:");

        String masterPasswordAttempt = input.nextLine();

        try {
            FileReader reader = new FileReader("data.txt");
            int character;


            if (reader.read() != -1) {
                passwordVault.setMasterPassword("");
            }

            while ((character = reader.read()) != -1) {
                System.out.print((char) character);
            }
            reader.close();

        } catch (IOException e) {
            FileWriter writer = new FileWriter("data.txt");
            writer.write(defaultPassword);
        }


    }
}
