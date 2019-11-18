public class Solution {

    public static void main(String[] args) {
        System.out.println("Welcome to password master 5000 mark 42!");
        start();
    }

    public static void start() {

        PassVault vault = new PassVault(5);

        if(vault.login()) {
            Session session = new Session(1);
            Menu menu = new Menu(vault, session);

            while(session.isActive()) {
                menu.listOptions();
            }
        }







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
