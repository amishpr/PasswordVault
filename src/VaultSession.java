import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VaultSession {

    private PassVault vault;
    private int timeLimit;
    private Timestamp timeout;

    private boolean active = false;

    public boolean isActive() {
        return active;
    }


    public VaultSession(PassVault vault, int timeLimit) {
        this.vault = vault;
        this.timeLimit = timeLimit;
    }

    public void startSession() {
        active = true;
        extend(timeLimit);
    }

    public void endSession() {
        active = false;
        vault.logout();
    }

    public void extend(int duration) {
//        long then = TimeUnit.MINUTES.toMillis(duration);
        long then = TimeUnit.SECONDS.toMillis(10);
        timeout = new Timestamp(System.currentTimeMillis() + then);
        new Timer().schedule(new checkSession(), then);
    }

    private class checkSession extends TimerTask {
        public void run() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (timeout.getTime() <= now.getTime()) {
                endSession();
            }
        }
    }
}
