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

public class RentalCarsHomePage extends BasePage {

    public RentalCarsHomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(ConfigReader.get("base.url"));
        waitForPageReady();
        acceptCookiesIfPresent();
        openRentalCarsFromHomePage();
    }

    public void openRentalCarsFromHomePage() {
        logStep("Opening Car rentals from Booking.com home page navigation");
        clickFirstAvailable(
                By.xpath("//*[self::a or self::button][contains(.,'Car rentals')]"),
                By.xpath("//*[self::a or self::button][contains(.,'Cars')]"),
                By.xpath("//*[contains(@href,'cars') or contains(@href,'car-rental')]")
        );
        waitForPageReady();
        acceptCookiesIfPresent();
        waitForRentalSearchForm();
        logPass("Rental cars section opened from home page");
    }

    public void searchCars(String pickup, String dropOff, String pickupDate, String dropOffDate, String driverAge) {
        waitForRentalSearchForm();
        if (pickup != null && !pickup.trim().isEmpty()) {
            logStep("Entering pickup location: " + pickup);
            enterLocationAndStabilize(getPickupLocators(), valueOrEmpty(pickup), "pickup");
        }

        handleDropOffSelection(dropOff);
        setDriverAgeIfPresent(driverAge);
        selectDatesIfPresent(pickupDate, dropOffDate);

        logStep("Clicking Search");
        TestContext.markSearchStart();
        clickFirstAvailable(
                By.xpath("//button[normalize-space()='Search']"),
                By.xpath("//*[self::button or self::a][normalize-space()='Search']"),
                By.xpath("//*[self::button or self::a][contains(.,'Search cars')]")
        );
        waitForSearchOutcome();
    }

    public boolean isValidationMessageDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(.,'Enter a pick-up location')]"),
                By.xpath("//*[contains(.,'Enter a drop-off location')]"),
                By.xpath("//*[contains(.,'Please enter')]"),
                By.xpath("//*[contains(.,'Select a valid location')]"),
                By.xpath("//*[contains(.,'required')]")
        );
    }

    public boolean isDateValidationDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(.,'Pick-up date must be before drop-off date')]"),
                By.xpath("//*[contains(.,'Select a valid date')]"),
                By.xpath("//*[contains(.,'Pick-up date') and contains(.,'before')]"),
                By.xpath("//*[contains(.,'past date')]")
        );
    }

    public boolean areSearchFieldsRetained() {
        String pickup = getFieldValue(getPickupLocators());
        String dropOff = getFieldValue(getDropOffLocators());
        return pickup != null && !pickup.trim().isEmpty() && dropOff != null && !dropOff.trim().isEmpty();
    }

    private void waitForRentalSearchForm() {
        waitForFirstVisible(
                getPickupLocators()[0],
                getPickupLocators()[1],
                getPickupLocators()[2],
                getPickupLocators()[3],
                getPickupLocators()[4],
                By.xpath("//*[contains(.,'Pick-up location')]"),
                By.xpath("//*[contains(.,'Where are you going?')]"),
                By.xpath("//*[self::button or self::div][contains(.,'Search cars')]")
        );
    }

    public String waitForSearchOutcome() {
        int timeoutMillis = ConfigReader.getInt("explicit.wait.seconds", 25) * 1000;
        long end = System.currentTimeMillis() + timeoutMillis;

        while (System.currentTimeMillis() < end) {
            if (isDateValidationDisplayed()) {
                TestContext.setLastSearchOutcome("DATE_VALIDATION");
                logPass("Rental car search ended with date validation");
                return "DATE_VALIDATION";
            }
            if (isValidationMessageDisplayed()) {
                TestContext.setLastSearchOutcome("VALIDATION");
                logPass("Rental car search ended with inline validation");
                return "VALIDATION";
            }

            String page = driver.getPageSource().toLowerCase(Locale.ENGLISH);
            if (page.contains("no cars available") || page.contains("no rental cars available") || page.contains("no results found")) {
                TestContext.setLastSearchOutcome("NO_RESULTS");
                logPass("Rental car search completed with no-results state");
                return "NO_RESULTS";
            }
            if ((driver.getCurrentUrl().contains("cars") || driver.getCurrentUrl().contains("car-rental"))
                    && (page.contains("sort by") || page.contains("filter by") || page.contains("supplier") || page.contains("automatic"))) {
                TestContext.setLastSearchOutcome("SEARCH_RESULTS");
                logPass("Rental car results page loaded");
                return "SEARCH_RESULTS";
            }
            sleep(700);
        }

        TestContext.setLastSearchOutcome("UNKNOWN");
        logInfo("Rental car search outcome timed out before a stable UI state was detected.");
        return "UNKNOWN";
    }

    private void handleDropOffSelection(String dropOff) {
        if (dropOff == null || dropOff.trim().isEmpty()) {
            return;
        }

        try {
            WebElement sameDropOffCheckbox = findFirstVisible(
                    By.xpath("//input[@type='checkbox' and (contains(@name,'same') or contains(@id,'same'))]"),
                    By.xpath("//*[self::input or self::button][contains(@aria-label,'same drop-off')]")
            );
            if (sameDropOffCheckbox.isSelected()) {
                clickFirstAvailable(
                        By.xpath("//label[contains(.,'Return car to same location')]"),
                        By.xpath("//label[contains(.,'Same drop-off')]"),
                        By.xpath("//input[@type='checkbox' and (contains(@name,'same') or contains(@id,'same'))]")
                );
                sleep(500);
            }
        } catch (Exception ignored) {
        }

        logStep("Entering drop-off location: " + dropOff);
        enterLocationAndStabilize(getDropOffLocators(), valueOrEmpty(dropOff), "drop-off");
    }

    private void enterLocationAndStabilize(By[] locators, String value, String fieldName) {
        WebElement input = waitForFirstVisible(locators);
        activateFieldIfNeeded(input, fieldName);
        input = waitForFirstVisible(locators);
        boolean suggestionUsed = typeAndSelectFirstSuggestion(input, value);
        if (!suggestionUsed) {
            input = waitForFirstVisible(locators);
        }

        blur(input);
        sleep(600);

        if (!isLocationFieldAccepted(input, value)) {
            logInfo("Retrying " + fieldName + " field because it still appears invalid.");
            activateFieldIfNeeded(input, fieldName);
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

    private void activateFieldIfNeeded(WebElement field, String fieldName) {
        try {
            String tag = field.getTagName();
            if (!"input".equalsIgnoreCase(tag)) {
                field.click();
                sleep(500);
            }
        } catch (Exception ignored) {
            logInfo("Could not explicitly activate " + fieldName + " field before typing.");
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

    private void selectDatesIfPresent(String pickupDateValue, String dropOffDateValue) {
        if ((pickupDateValue == null || pickupDateValue.trim().isEmpty()) && (dropOffDateValue == null || dropOffDateValue.trim().isEmpty())) {
            return;
        }

        LocalDate pickupDate = DateUtils.parseDate(pickupDateValue);
        LocalDate dropOffDate = DateUtils.parseDate(dropOffDateValue);
        openDateWidget();

        if (!pickDateFromCalendar(pickupDate) && !setDateDirectly(getPickupDateLocator(), pickupDate)) {
            logInfo("Pick-up date did not expose a direct selector. Continuing with page default.");
        }
        if (!pickDateFromCalendar(dropOffDate) && !setDateDirectly(getDropOffDateLocator(), dropOffDate)) {
            logInfo("Drop-off date did not expose a direct selector. Continuing with page default.");
        }
    }

    private void setDriverAgeIfPresent(String driverAge) {
        if (driverAge == null || driverAge.trim().isEmpty()) {
            return;
        }
        try {
            WebElement ageInput = findFirstVisible(
                    By.xpath("//input[contains(@name,'age') or contains(@id,'age')]"),
                    By.xpath("//select[contains(@name,'age') or contains(@id,'age')]"),
                    By.xpath("//input[contains(@aria-label,'Driver age')]")
            );
            type(ageInput, driverAge);
            blur(ageInput);
            sleep(300);
            logPass("Driver age entered: " + driverAge);
        } catch (Exception ignored) {
            logInfo("Driver age field not available on current rental cars page state.");
        }
    }

    private void openDateWidget() {
        try {
            clickFirstAvailable(
                    By.xpath("//*[contains(@aria-label,'Pick-up date') or contains(@aria-label,'pick-up date')]"),
                    By.xpath("//*[contains(@aria-label,'Pick-up date') or contains(@aria-label,'pickup date')]"),
                    By.xpath("//*[contains(@aria-label,'Drop-off date') or contains(@aria-label,'drop-off date')]"),
                    By.xpath("//*[contains(@data-testid,'date-field')]"),
                    By.xpath("//button[contains(@data-testid,'searchbox-datepicker-field')]"),
                    By.xpath("//*[contains(.,'Pick-up date')]"),
                    By.xpath("//*[contains(.,'Drop-off date')]")
            );
            sleep(800);
        } catch (Exception ignored) {
        }
    }

    private boolean pickDateFromCalendar(LocalDate date) {
        String isoDate = date.toString();
        String shortLabel = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH));
        try {
            clickFirstAvailable(
                    By.xpath("//button[@data-date='" + isoDate + "']"),
                    By.xpath("//span[@data-date='" + isoDate + "']"),
                    By.xpath("//button[contains(@aria-label,\"" + shortLabel + "\")]")
            );
            sleep(400);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean setDateDirectly(By locator, LocalDate date) {
        try {
            WebElement dateInput = findFirstVisible(locator);
            String isoDate = date.toString();
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles:true})); arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                    dateInput,
                    isoDate
            );
            blur(dateInput);
            sleep(300);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private String getFieldValue(By... locators) {
        try {
            WebElement element = waitForFirstVisible(locators);
            String value = element.getAttribute("value");
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
            return element.getText();
        } catch (Exception ex) {
            return "";
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private By[] getPickupLocators() {
        return new By[]{
                By.xpath("//input[contains(@placeholder,'Pick-up location')]"),
                By.xpath("//input[contains(@aria-label,'Pick-up location')]"),
                By.xpath("//input[contains(@placeholder,'pick-up')]"),
                By.xpath("//input[contains(@placeholder,'pickup')]"),
                By.xpath("//input[contains(@name,'pickup') or contains(@id,'pickup')]"),
                By.xpath("//*[self::input or self::textarea][contains(@data-testid,'pickup')]"),
                By.xpath("//*[self::button or self::div][contains(.,'Pick-up location')]"),
                By.xpath("//*[self::button or self::div][contains(.,'Pick-up')]")
        };
    }

    private By[] getDropOffLocators() {
        return new By[]{
                By.xpath("//input[contains(@placeholder,'Drop-off location')]"),
                By.xpath("//input[contains(@aria-label,'Drop-off location')]"),
                By.xpath("//input[contains(@placeholder,'drop-off')]"),
                By.xpath("//input[contains(@placeholder,'dropoff')]"),
                By.xpath("//input[contains(@name,'drop') or contains(@id,'drop')]"),
                By.xpath("//*[self::input or self::textarea][contains(@data-testid,'dropoff')]"),
                By.xpath("//*[self::button or self::div][contains(.,'Drop-off location')]"),
                By.xpath("//*[self::button or self::div][contains(.,'Drop-off')]")
        };
    }

    private By getPickupDateLocator() {
        return By.xpath("//input[contains(@name,'pickup') and @type='date'] | //input[contains(@aria-label,'Pick-up date')] | //input[contains(@placeholder,'Pick-up date')] | //input[contains(@aria-label,'pickup date')]");
    }

    private By getDropOffDateLocator() {
        return By.xpath("//input[contains(@name,'drop') and @type='date'] | //input[contains(@aria-label,'Drop-off date')] | //input[contains(@placeholder,'Drop-off date')] | //input[contains(@aria-label,'drop-off date')]");
    }
}
