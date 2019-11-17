import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Session {

    private int timeLimit;
    private Timestamp timeout;

    public Session(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void startSession() {

    }

    public void endSession() {

    }

    public void extend(int duration) {
        long then = TimeUnit.MINUTES.toMillis(duration);
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
