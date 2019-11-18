import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PassVault {
    // ========================================
    // Globals
    // ========================================

    private final int passAttempts;

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

    public boolean login() { return false; }

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
