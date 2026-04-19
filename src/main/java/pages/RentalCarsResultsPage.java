package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;
import utils.TestContext;

public class RentalCarsResultsPage extends BasePage {

    public RentalCarsResultsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isResultsPageDisplayed() {
        sleep(1200);
        return (driver.getCurrentUrl().contains("cars") || driver.getCurrentUrl().contains("car-rental"))
                && isAnyVisible(
                By.xpath("//*[contains(@data-testid,'sort')]"),
                By.xpath("//*[contains(@data-testid,'filter')]"),
                By.xpath("//*[contains(@data-testid,'vehicle-card')]"),
                By.xpath("//*[self::button or self::a][contains(.,'View deal') or contains(.,'Select')]")
        );
    }

    public boolean areVehicleCardsDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(@data-testid,'vehicle-card')]"),
                By.xpath("//*[contains(.,'Automatic') and contains(.,'doors')]"),
                By.xpath("//*[contains(.,'Supplier') or contains(.,'supplied by')]"),
                By.xpath("//*[self::button or self::a][contains(.,'View deal') or contains(.,'Select')]")
        );
    }

    public boolean isNoCarsMessageDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(.,'No rental cars available')]"),
                By.xpath("//*[contains(.,'No cars available')]"),
                By.xpath("//*[contains(.,'No results found')]")
        );
    }

    public void applyCarTypeFilter(String carType) {
        applyFilterOption(carType);
    }

    public void applySupplierFilter(String supplier) {
        applyFilterOption(supplier);
    }

    public void applyTransmissionFilter(String transmission) {
        applyFilterOption(transmission);
    }

    public void applyFuelFilter(String fuelType) {
        applyFilterOption(fuelType);
    }

    public boolean isFilterApplied(String label) {
        return isAnyVisible(
                By.xpath("//*[contains(.,'" + label + "') and (contains(@class,'chip') or contains(@class,'tag'))]"),
                By.xpath("//*[contains(.,'" + label + "') and contains(.,'Remove')]"),
                By.xpath("//*[contains(.,'" + label + "') and contains(.,'selected')]")
        );
    }

    public void clearAllFilters() {
        clickFirstAvailable(
                By.xpath("//*[self::button or self::a][contains(.,'Clear all')]"),
                By.xpath("//*[self::button or self::a][contains(.,'Reset filters')]"),
                By.xpath("//*[self::button or self::a][contains(.,'Remove all')]")
        );
        sleep(1000);
    }

    public boolean isFilterReset() {
        return !isAnyVisible(
                By.xpath("//*[contains(@class,'chip')]"),
                By.xpath("//*[contains(@class,'tag') and contains(.,'Remove')]")
        );
    }

    public void openFirstAvailableCar() {
        logStep("Opening first available rental car");
        clickFirstAvailable(
                By.xpath("(//*[self::button or self::a][contains(.,'View deal')])[1]"),
                By.xpath("(//*[self::button or self::a][contains(.,'Select')])[1]"),
                By.xpath("(//*[self::button or self::a][contains(.,'See deal')])[1]")
        );
    }

    public boolean isSelectionPromptDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(.,'Select a car')]"),
                By.xpath("//*[contains(.,'Choose a car')]"),
                By.xpath("//*[contains(.,'Choose a vehicle')]")
        );
    }

    public boolean isUnavailableCarShown(String carType) {
        return isAnyVisible(
                By.xpath("//*[contains(.,'" + carType + "') and contains(.,'Sold out')]"),
                By.xpath("//*[contains(.,'" + carType + "') and contains(.,'Unavailable')]"),
                By.xpath("//*[contains(.,'" + carType + "') and contains(.,'Not available')]")
        );
    }

    public void applySort(String sortLabel) {
        clickFirstAvailable(
                By.xpath("//*[self::button or self::a][contains(.,'Sort by')]"),
                By.xpath("//*[contains(@data-testid,'sort')]")
        );
        sleep(500);
        clickFirstAvailable(
                By.xpath("//*[self::button or self::a][contains(.,'" + sortLabel + "')]"),
                By.xpath("//*[contains(.,'" + sortLabel + "') and (@role='option' or self::li or self::button)]")
        );
        sleep(1200);
    }

    public boolean isSortApplied(String sortLabel) {
        return isAnyVisible(
                By.xpath("//*[contains(.,'" + sortLabel + "') and (contains(@class,'selected') or contains(@class,'active'))]"),
                By.xpath("//*[contains(.,'Sorted by') and contains(.,'" + sortLabel + "')]")
        );
    }

    public boolean canCompareVisibleSpecifications() {
        return driver.findElements(By.xpath("//*[self::button or self::a][contains(.,'View deal') or contains(.,'Select')]")).size() >= 2
                && isAnyVisible(By.xpath("//*[contains(.,'Automatic') or contains(.,'Manual')]"));
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

    private void applyFilterOption(String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        logStep("Applying filter: " + value);
        clickFirstAvailable(
                By.xpath("//*[self::label or self::button or self::span][contains(.,'" + value + "')]"),
                By.xpath("//*[contains(@aria-label,'" + value + "')]")
        );
        sleep(1000);
    }
}
