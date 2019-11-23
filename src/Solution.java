public class Solution {

  public static void main(String[] args) throws Exception {
    System.out.println("Welcome to password master 5000 mark 42!");
    PassVault vault = new PassVault(5);
    VaultSession session = new VaultSession(30);
    Menu menu = new Menu(vault, session);

    if (vault.login()) {
      session.startSession();

      while (session.isActive()) {
        menu.listOptions();
      }
      System.out.println("No longer active");
    }
  }
}
