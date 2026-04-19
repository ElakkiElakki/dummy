package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateUtils {

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    private DateUtils() {
    }

    public static LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return LocalDate.now().plusDays(1);
        }
        String trimmed = value.trim();
        if (trimmed.toLowerCase().startsWith("today")) {
            return LocalDate.now();
        }
        return LocalDate.parse(trimmed, INPUT_FORMAT);
    }
}
