package utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TestContext {

    private static final ThreadLocal<Map<String, String>> CURRENT_TEST_DATA = new ThreadLocal<Map<String, String>>();
    private static final ThreadLocal<Long> SEARCH_START_TIME = new ThreadLocal<Long>();
    private static final ThreadLocal<String> LAST_SEARCH_OUTCOME = new ThreadLocal<String>();
    private static final ThreadLocal<Boolean> TEST_CASE_PASSED = new ThreadLocal<Boolean>();

    private TestContext() {
    }

    public static void setCurrentTestData(Map<String, String> testData) {
        CURRENT_TEST_DATA.set(new HashMap<String, String>(testData));
        TEST_CASE_PASSED.set(Boolean.FALSE);
    }

    public static Map<String, String> getCurrentTestData() {
        Map<String, String> data = CURRENT_TEST_DATA.get();
        return data == null ? Collections.<String, String>emptyMap() : Collections.unmodifiableMap(data);
    }

    public static String getValue(String key) {
        Map<String, String> data = CURRENT_TEST_DATA.get();
        if (data == null) {
            return "";
        }
        String value = data.get(key);
        return value == null ? "" : value;
    }

    public static void markSearchStart() {
        SEARCH_START_TIME.set(System.currentTimeMillis());
    }

    public static long getSearchDurationMillis() {
        Long start = SEARCH_START_TIME.get();
        return start == null ? -1L : System.currentTimeMillis() - start.longValue();
    }

    public static void setLastSearchOutcome(String outcome) {
        LAST_SEARCH_OUTCOME.set(outcome);
    }

    public static String getLastSearchOutcome() {
        String outcome = LAST_SEARCH_OUTCOME.get();
        return outcome == null ? "" : outcome;
    }

    public static void markTestCasePassed() {
        TEST_CASE_PASSED.set(Boolean.TRUE);
    }

    public static boolean isTestCasePassed() {
        return Boolean.TRUE.equals(TEST_CASE_PASSED.get());
    }

    public static void clear() {
        CURRENT_TEST_DATA.remove();
        SEARCH_START_TIME.remove();
        LAST_SEARCH_OUTCOME.remove();
        TEST_CASE_PASSED.remove();
    }
}
