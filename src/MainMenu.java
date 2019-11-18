import java.io.IOException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainMenu {

    private static String lineBreak =
            "--------------------------------------------------";

    private int sessionTimeLimit = 1; // in minutes
    private static Timer timer = new Timer();
    private static Timestamp endSession;

    public int getSessionTimeLimit() {
        return sessionTimeLimit;
    }

    public void MainMenu()
        throws Exception {
        extendSession();

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
        System.out.println("6) Exit");
        System.out.println();

        PasswordVault vault = new PasswordVault();

        boolean next = false;

        while (!next) {
            try {

                System.out.print("Your choice: ");
                Scanner in = new Scanner(System.in);
                int choice = in.nextInt();
                System.out.println();

                switch (choice) {
                    case 1:
                        vault.addPassword();
                        next = true;
                        break;
                    case 2:
                        vault.listAllIds();
                        next = true;
                        break;
                    case 3:
                        vault.findPassword();
                        next = true;
                        break;
                    case 4:
//                        vault.exportPassword();
                        next = true;
                        break;
                    case 5:
                        vault.changeMasterPassword();
                        next = true;
                        break;
                    case 6:
                        System.exit(0);
                        break;
                    default:
                        System.out.println(choice + " is not a valid choice! Please enter a number from 1 to 6.");
                        break;
                }
            } catch (InputMismatchException | IOException e) {
                System.err.println("Not a valid input. Error :" + e.getMessage());
                continue;
            }
        }

        new MainMenu();
    }

    // ========================================
    // Session Timeout
    // ========================================

    private void extendSession() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        endSession = new Timestamp(now.getTime() + TimeUnit.MINUTES.toMillis(getSessionTimeLimit()));
        TimerTask task = new checkActiveSession();
        timer.schedule(task, TimeUnit.MINUTES.toMillis(getSessionTimeLimit()));
    }

    private class checkActiveSession extends TimerTask {
        public void run() {

            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (endSession.getTime() <= now.getTime()) {
                for (int i=0; i<100; i++) {
                    System.out.println(); // Definitely Clears the console
                }
                try {
                    PasswordVault vault = new PasswordVault();
                    vault.login();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

