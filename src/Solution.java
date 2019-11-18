import java.util.Scanner;

public class Solution {

    public static void main(String[] args) {
        System.out.println("Welcome to password master 5000 mark 42!");
        start();
    }

    public static void start() {

        Scanner in = new Scanner(System.in);
        PassVault vault = new PassVault(5, in);

//        if(vault.login()) {
        Menu menu = new Menu(vault);
        VaultSession session = new VaultSession(vault, 1);
        session.startSession();

        while(session.isActive()) {
            menu.listOptions();
        }
        System.out.println("No longer active");
//        }







/*
        PasswordVault passwordVault = new PasswordVault(5,1);

        System.out.println("Welcome to password master 5000 mark 42!");

        try {
            FileReader reader = new FileReader("master.txt");
            reader.close();
            passwordVault.login();
        } catch (FileNotFoundException e) {
            passwordVault.signUp();
        } catch (IOException e) {
            System.err.println("Error #00003"); // Unknown
//            e.printStackTrace();
        }*/
    }
}
