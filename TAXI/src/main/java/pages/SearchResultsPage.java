package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;
import utils.TestContext;

public class SearchResultsPage extends BasePage {

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isResultsPageDisplayed() {
        sleep(1500);
        return driver.getCurrentUrl().contains("taxi")
                && isAnyVisible(
                By.xpath("//*[contains(@data-testid,'sort')]"),
                By.xpath("//*[contains(@data-testid,'vehicle-card')]"),
                By.xpath("//*[self::h1 or self::h2][contains(.,'Choose your ride')]"),
                By.xpath("//*[self::h1 or self::h2][contains(.,'Available vehicles')]"),
                By.xpath("//*[self::button or self::a][contains(.,'Select') or contains(.,'Choose') or contains(.,'Reserve')]")
        );
    }

    public boolean areVehicleCardsDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(@data-testid,'vehicle-card')]"),
                By.xpath("//*[contains(.,'passengers') and contains(.,'bags')]"),
                By.xpath("//*[contains(.,'Free cancellation') and (self::div or self::section or self::article)]"),
                By.xpath("//*[self::button or self::a][contains(.,'Select') or contains(.,'Choose') or contains(.,'Reserve')]")
        );
    }

    public boolean isNoCarsMessageDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(.,'No cars available')]"),
                By.xpath("//*[contains(.,'No matching rides')]"),
                By.xpath("//*[contains(.,'No results found')]"),
                By.xpath("//*[contains(.,'couldn’t find any rides') or contains(.,'couldn't find any rides')]")
        );
    }

    public void selectFirstAvailableCar() {
        logStep("Selecting first available car");
        clickFirstAvailable(
                By.xpath("(//*[self::button or self::a][contains(.,'Select')])[1]"),
                By.xpath("(//*[self::button or self::a][contains(.,'Choose')])[1]"),
                By.xpath("(//*[self::button or self::a][contains(.,'Book')])[1]"),
                By.xpath("(//*[self::button or self::a][contains(.,'Reserve')])[1]"),
                By.xpath("(//*[self::button or self::a][contains(.,'See details')])[1]")
        );
    }

    public boolean trySelectingUnavailableCar(String carType) {
        return isElementPresent(By.xpath("//*[contains(.,'" + carType + "') and contains(.,'Sold Out')]"))
                || isElementPresent(By.xpath("//*[contains(.,'" + carType + "') and contains(.,'Unavailable')]"))
                || isElementPresent(By.xpath("//*[contains(.,'" + carType + "') and contains(.,'Not available')]"));
    }

    public boolean isSelectionPromptDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(.,'Select a car')]"),
                By.xpath("//*[contains(.,'Choose a ride')]")
        );
    }

    public boolean isCarOrBookingDetailsPageDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(.,'Book now')]"),
                By.xpath("//*[contains(.,'Passenger details')]"),
                By.xpath("//*[contains(.,'Your booking')]"),
                By.xpath("//*[contains(.,'Booking summary')]")
        );
    }

    public boolean waitForKnownOutcome() {
        long end = System.currentTimeMillis() + (ConfigReader.getInt("explicit.wait.seconds", 25) * 1000L);
        while (System.currentTimeMillis() < end) {
            if (isResultsPageDisplayed()) {
                TestContext.setLastSearchOutcome("SEARCH_RESULTS");
                return true;
            }
            if (isNoCarsMessageDisplayed()) {
                TestContext.setLastSearchOutcome("NO_RESULTS");
                return true;
            }
            sleep(700);
        }
        return false;
    }
}
