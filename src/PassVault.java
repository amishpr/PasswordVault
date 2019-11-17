import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PassVault {
    // ========================================
    // Globals
    // ========================================

    private final int passAttempts;
    private final int sessionLimit;

    private String sessionID;
    private Timestamp endSessionTime;

    // ========================================
    // Constructor
    // ========================================

    public PassVault(int passAttempts, int sessionLimit) {
        this.passAttempts = passAttempts;
        this.sessionLimit = sessionLimit;
    }

    public PassVault() {
        // Defaults
        this.passAttempts = 5;
        this.sessionLimit = 5;
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

    // ========================================
    // Session
    // ========================================

    private void extendSession(int duration) {
        long then = TimeUnit.MINUTES.toMillis(duration);
        endSessionTime = new Timestamp(System.currentTimeMillis() + then);
        new Timer().schedule(new checkSession(), then);
    }

    private class checkSession extends TimerTask {
        public void run() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (endSessionTime.getTime() <= now.getTime()) {
                // End Session
                login();
            }
        }
    }
}
