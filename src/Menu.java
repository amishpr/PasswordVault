import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {

    private String lineBreak =
            "--------------------------------------------------";

    private PassVault vault;
    private Scanner in = new Scanner(System.in);

    public Menu(PassVault vault) {
        this.vault = vault;
    }

    public void listOptions() {
        // Display options
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

        boolean next = false;

        while (!next) {
            try {

                System.out.print("Your choice: ");
                int choice = in.nextInt();
                System.out.println();

                switch (choice) {
                    case 1:
                        vault.addPass();
                        next = true;
                        break;
                    case 2:
                        vault.listIds();
                        next = true;
                        break;
                    case 3:
                        vault.findPass();
                        next = true;
                        break;
                    case 4:
                        vault.sharePass();
                        next = true;
                        break;
                    case 5:
                        vault.changeMasterPass();
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
            }
        }
    }
}
