import java.io.*;

public class Solution {

    public static void main(String[] args) throws IOException {

        PasswordVault passwordVault = new PasswordVault();

        System.out.println("Welcome to password master 5000 mark 23!");

        try {
            FileReader reader = new FileReader("data.txt");
            reader.close();
            passwordVault.login();

        } catch (FileNotFoundException e) {
            passwordVault.signUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
