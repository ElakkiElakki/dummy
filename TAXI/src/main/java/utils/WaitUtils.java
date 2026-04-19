package utils;

public final class WaitUtils {

    private WaitUtils() {
    }

    public interface Condition {
        boolean evaluate();
    }

    public static boolean waitUntil(Condition condition, int timeoutSeconds, long pollingMillis) {
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        while (System.currentTimeMillis() < endTime) {
            try {
                if (condition.evaluate()) {
                    return true;
                }
                Thread.sleep(pollingMillis);
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
