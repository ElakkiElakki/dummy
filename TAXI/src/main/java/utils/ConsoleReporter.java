package utils;

public final class ConsoleReporter {

    private ConsoleReporter() {
    }

    public static void step(String scenarioId, String testCaseId, String message) {
        System.out.println("[STEP][" + scenarioId + "][" + testCaseId + "] " + message);
    }

    public static void pass(String scenarioId, String testCaseId, String message) {
        System.out.println("[PASS][" + scenarioId + "][" + testCaseId + "] " + message);
    }

    public static void fail(String scenarioId, String testCaseId, String message) {
        System.out.println("[FAIL][" + scenarioId + "][" + testCaseId + "] " + message);
    }

    public static void info(String scenarioId, String testCaseId, String message) {
        System.out.println("[INFO][" + scenarioId + "][" + testCaseId + "] " + message);
    }

    public static void result(String scenarioId, String testCaseId, String status) {
        System.out.println("[RESULT][" + scenarioId + "][" + testCaseId + "][" + status + "]");
    }
}
