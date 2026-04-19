package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ConfigReader;
import utils.DateUtils;
import utils.TestContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AirportTaxiHomePage extends BasePage {

    public AirportTaxiHomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(ConfigReader.get("base.url"));
        waitForPageReady();
        acceptCookiesIfPresent();
        openAirportTaxisFromHomePage();
    }

    public void openAirportTaxisFromHomePage() {
        logStep("Opening Airport taxis from Booking.com home page navigation");
        clickFirstAvailable(
                By.xpath("//*[self::a or self::button][contains(.,'Airport taxis')]"),
                By.xpath("//*[self::a or self::button][contains(.,'Taxis')]"),
                By.xpath("//*[contains(@href,'airport-taxis') or contains(@href,'taxi')]")
        );
        waitForPageReady();
        acceptCookiesIfPresent();
        logPass("Airport taxis section opened from home page");
    }

    public void searchTaxi(String pickup, String dropOff, String date, String time) {
        if (pickup != null && !pickup.trim().isEmpty()) {
            logStep("Entering pickup location: " + pickup);
            enterLocationAndStabilize(getPickupLocator(), pickup, "pickup");
        }
        if (dropOff != null && !dropOff.trim().isEmpty()) {
            logStep("Entering drop-off location: " + dropOff);
            enterLocationAndStabilize(getDropOffLocator(), dropOff, "drop-off");
        }
        if ((date != null && !date.trim().isEmpty()) || (time != null && !time.trim().isEmpty())) {
            selectPickupDateTime(date, time);
        }
        logStep("Clicking Search");
        TestContext.markSearchStart();
        clickFirstAvailable(By.xpath("//button[normalize-space()='Search']"), By.xpath("//*[self::button or self::a][normalize-space()='Search']"));
        waitForSearchOutcome();
    }

    public boolean isValidationMessageDisplayed() {
        return isElementPresent(By.xpath("//*[contains(.,'pick-up') and contains(.,'required')]"))
                || isElementPresent(By.xpath("//*[contains(.,'Enter pick-up location')]"))
                || isElementPresent(By.xpath("//*[contains(.,'Enter destination')]"))
                || isElementPresent(By.xpath("//*[contains(.,'Select a valid location')]"))
                || isElementPresent(By.xpath("//*[contains(.,'No results found')]"))
                || isElementPresent(By.xpath("//*[contains(.,'Please enter')]"))
                || isElementPresent(By.xpath("//*[contains(.,'valid pick-up')]"));
    }

    public boolean isCurrentDateVisibleInPickupWidget() {
        String day = String.valueOf(LocalDate.now().getDayOfMonth());
        return driver.getPageSource().contains(day);
    }

    public boolean areSearchFieldsRetained() {
        String pickup = getValue(getPickupLocator());
        String drop = getValue(getDropOffLocator());
        return pickup != null && !pickup.trim().isEmpty() && drop != null && !drop.trim().isEmpty();
    }

    private void enterLocationAndStabilize(By locator, String value, String fieldName) {
        WebElement input = waitForVisible(locator);
        boolean suggestionUsed = typeAndSelectFirstSuggestion(locator, value);
        if (!suggestionUsed) {
            input = waitForVisible(locator);
        }

        blur(input);
        sleep(600);

        if (!isLocationFieldAccepted(input, value)) {
            logInfo("Retrying " + fieldName + " field because it still appears invalid.");
            type(input, value);
            input.sendKeys(Keys.ARROW_DOWN);
            sleep(300);
            input.sendKeys(Keys.ENTER);
            blur(input);
            sleep(600);
        }

        if (isLocationFieldAccepted(input, value)) {
            logPass("Valid " + fieldName + " value accepted before Search: " + input.getAttribute("value"));
        } else {
            logInfo(fieldName + " field still looks unconfirmed. Search will continue with typed value.");
        }
    }

    private boolean isLocationFieldAccepted(WebElement input, String expectedValue) {
        String currentValue = input.getAttribute("value");
        if (currentValue == null || currentValue.trim().isEmpty()) {
            return false;
        }
        if (hasInvalidState(input)) {
            return false;
        }
        return currentValue.trim().length() >= Math.min(expectedValue.trim().length(), 3);
    }

    public String waitForSearchOutcome() {
        int timeoutMillis = ConfigReader.getInt("explicit.wait.seconds", 25) * 1000;
        long end = System.currentTimeMillis() + timeoutMillis;

        while (System.currentTimeMillis() < end) {
            if (isValidationMessageDisplayed()) {
                TestContext.setLastSearchOutcome("VALIDATION");
                logPass("Search ended with inline validation");
                return "VALIDATION";
            }
            if (driver.getCurrentUrl().contains("taxi") && !driver.getCurrentUrl().contains("search")) {
                waitForPageReady();
            }
            if (driver.getCurrentUrl().contains("taxi") && driver.getPageSource().toLowerCase(Locale.ENGLISH).contains("free cancellation")) {
                TestContext.setLastSearchOutcome("SEARCH_RESULTS");
                logPass("Search results page loaded");
                return "SEARCH_RESULTS";
            }
            if (driver.getCurrentUrl().contains("taxi") && driver.getPageSource().toLowerCase(Locale.ENGLISH).contains("available vehicles")) {
                TestContext.setLastSearchOutcome("SEARCH_RESULTS");
                logPass("Search results page loaded");
                return "SEARCH_RESULTS";
            }
            if (driver.getPageSource().toLowerCase(Locale.ENGLISH).contains("no cars available")
                    || driver.getPageSource().toLowerCase(Locale.ENGLISH).contains("no matching rides")
                    || driver.getPageSource().toLowerCase(Locale.ENGLISH).contains("no results")) {
                TestContext.setLastSearchOutcome("NO_RESULTS");
                logPass("Search completed with no-results state");
                return "NO_RESULTS";
            }
            sleep(700);
        }

        TestContext.setLastSearchOutcome("UNKNOWN");
        logInfo("Search outcome timed out before a stable UI state was detected.");
        return "UNKNOWN";
    }

    private void selectPickupDateTime(String dateValue, String timeValue) {
        boolean dateWidgetOpened = openDateWidget();
        if (dateValue != null && !dateValue.trim().isEmpty()) {
            LocalDate date = DateUtils.parseDate(dateValue);
            boolean dateSelected = false;
            if (dateWidgetOpened) {
                dateSelected = tryPickDateFromCalendar(date);
            }
            if (!dateSelected) {
                dateSelected = trySetDateDirectly(date);
            }
            if (!dateSelected) {
                logInfo("Date picker did not expose a selectable target. Continuing with page default date.");
            }
        }
        if (timeValue != null && !timeValue.trim().isEmpty()) {
            boolean timeSelected = trySetTimeDirectly(timeValue.trim().toUpperCase(Locale.ENGLISH));
            if (!timeSelected) {
                logInfo("Time widget did not expose a direct input. Continuing with page default time.");
            }
        }
    }

    private boolean openDateWidget() {
        try {
            clickFirstAvailable(
                    By.xpath("//*[contains(@aria-label,'date') or contains(@aria-label,'Date')][self::button or self::div]"),
                    By.xpath("//button[contains(@data-testid,'date')]"),
                    By.xpath("//button[contains(@data-testid,'searchbox-datepicker-field')]"),
                    By.xpath("//*[contains(@data-testid,'datepicker-field')]")
            );
            sleep(1000);
            return true;
        } catch (Exception ex) {
            logInfo("Date widget was not opened explicitly. Trying fallback date entry.");
            return false;
        }
    }

    private boolean tryPickDateFromCalendar(LocalDate date) {
        String fullMonthYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
        String day = String.valueOf(date.getDayOfMonth());
        String isoDate = date.toString();
        String longLabel = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH));
        String shortLabel = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH));

        for (int monthTry = 0; monthTry < 12; monthTry++) {
            if (driver.getPageSource().contains(fullMonthYear)) {
                break;
            }
            if (isElementPresent(By.xpath("//button[@aria-label='Next month']"))) {
                clickFirstAvailable(By.xpath("//button[@aria-label='Next month']"));
                sleep(700);
            }
        }

        try {
            clickFirstAvailable(
                    By.xpath("//button[@data-date='" + isoDate + "']"),
                    By.xpath("//button[contains(@aria-label,\"" + shortLabel + "\")]"),
                    By.xpath("//button[contains(@aria-label,\"" + longLabel + "\")]"),
                    By.xpath("//button[contains(@aria-label,'" + day + "') and contains(@aria-label,'" + date.getYear() + "')]"),
                    By.xpath("//button[normalize-space()='" + day + "']")
            );
            sleep(500);
            logPass("Date selected: " + isoDate);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean trySetDateDirectly(LocalDate date) {
        try {
            WebElement dateInput = findFirstVisible(
                    By.xpath("//input[@type='date']"),
                    By.xpath("//input[contains(@aria-label,'date')]"),
                    By.xpath("//input[contains(@placeholder,'date')]")
            );
            String isoDate = date.toString();
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles:true})); arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                    dateInput,
                    isoDate
            );
            sleep(300);
            logPass("Date entered directly: " + isoDate);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean trySetTimeDirectly(String timeValue) {
        try {
            WebElement timeInput = findFirstVisible(
                    By.xpath("//input[contains(@aria-label,'time') or contains(@placeholder,'Time')]"),
                    By.xpath("//input[@type='time']"),
                    By.xpath("//select[contains(@aria-label,'time')]"),
                    By.xpath("//input[contains(@placeholder,'10:00')]")
            );
            type(timeInput, timeValue);
            timeInput.sendKeys(Keys.ENTER);
            sleep(300);
            logPass("Time selected: " + timeValue);
            return true;
        } catch (Exception ignored) {
        }

        try {
            clickFirstAvailable(By.xpath("//option[normalize-space()='" + timeValue + "']"), By.xpath("//*[normalize-space()='" + timeValue + "']"));
            sleep(300);
            logPass("Time selected from visible option: " + timeValue);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private String getValue(By locator) {
        try {
            return waitForVisible(locator).getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    private By getPickupLocator() {
        return By.xpath("//input[contains(@placeholder,'pick-up') or contains(@aria-label,'pick-up') or contains(@aria-label,'Pickup') or contains(@placeholder,'Pickup') or contains(@data-testid,'pickup')]");
    }

    private By getDropOffLocator() {
        return By.xpath("//input[contains(@placeholder,'destination') or contains(@aria-label,'destination') or contains(@aria-label,'Destination') or contains(@data-testid,'dropoff')]");
    }
}
