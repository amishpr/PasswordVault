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
    private Scanner in;

    // ========================================
    // Constructor
    // ========================================

    public PassVault(int passAttempts, Scanner in) {
        this.passAttempts = passAttempts;
        this.in = in;
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
        String i = in.next();
        System.out.println(i);
        return false;
    }

    public boolean signUp() { return false; }

    public void logout() {

    }

    public boolean authorized() { return false; }

    // extendSession(getSessionLimit());

    // ========================================
    // Options
    // ========================================

    public void addPass() {}

    public void listIds() {}

    public void findPass() {}

    public void sharePass() {}

    public void changeMasterPass() {}
}
