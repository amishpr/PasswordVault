public class Solution {

    public static void main(String[] args) {
        System.out.println("Welcome to password master 5000 mark 42!");
        PassVault vault = new PassVault(5);
        Menu menu = new Menu(vault);
        VaultSession session = new VaultSession(vault, 1);

        if (vault.login()) {
            session.startSession();

            while (session.isActive()) {
                menu.listOptions();
            }
            System.out.println("No longer active");
        }
    }
}
