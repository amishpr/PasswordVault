import java.sql.Timestamp;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PassVault {
    // ========================================
    // Globals
    // ========================================

    private final int passAttempts;
    private Scanner in = new Scanner(System.in);
    private boolean authorized = false;

    // ========================================
    // Constructor
    // ========================================

    public PassVault(int passAttempts) {
        this.passAttempts = passAttempts;
    }

    public PassVault() {
        // Defaults
        this.passAttempts = 5;
    }

    // ========================================
    // Authorization
    // ========================================

    public boolean login() {
        System.out.println("Login");
        if (in.next().equals("true")) {
            authorized = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean signUp() { return false; }

    public void logout() {
        authorized = false;
    }

    public boolean isAuthorized() { return authorized; }

    // extendSession(getSessionLimit());

    // ========================================
    // Options
    // ========================================

    public void addPass() {
        System.out.println("addPass");
    }

    public void listIds() {
        System.out.println("listIds");
    }

    public void findPass() {
        System.out.println("findPass");
    }

    public void sharePass() {
        System.out.println("sharePass");
    }

    public void changeMasterPass() {
        System.out.println("changeMasterPass");
    }
}
