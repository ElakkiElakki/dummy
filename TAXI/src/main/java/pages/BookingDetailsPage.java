package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BookingDetailsPage extends BasePage {

    public BookingDetailsPage(WebDriver driver) {
        super(driver);
    }

    public void fillPassengerDetails(String firstName, String lastName, String email, String phone) {
        typeIfPresent(firstName, By.xpath("//input[contains(@name,'first') or contains(@placeholder,'First name') or contains(@aria-label,'First name')]"));
        typeIfPresent(lastName, By.xpath("//input[contains(@name,'last') or contains(@placeholder,'Last name') or contains(@aria-label,'Last name')]"));
        typeIfPresent(email, By.cssSelector("input[type='email']"), By.xpath("//input[contains(@name,'email')]"));
        typeIfPresent(phone, By.xpath("//input[contains(@name,'phone') or @type='tel']"));
    }

    public void proceedWithBooking() {
        clickFirstAvailable(By.xpath("//button[contains(.,'Book now')]"), By.xpath("//button[contains(.,'Continue')]"), By.xpath("//button[contains(.,'Confirm')]"));
    }

    public boolean isBookingSummaryOrConfirmationDisplayed() {
        return isElementPresent(By.xpath("//*[contains(.,'Booking confirmed')]"))
                || isElementPresent(By.xpath("//*[contains(.,'Booking summary')]"))
                || isElementPresent(By.xpath("//*[contains(.,'Confirmation')]"))
                || isElementPresent(By.xpath("//*[contains(.,'Your booking')]"));
    }

    public boolean isEmailValidationDisplayed() {
        return isElementPresent(By.xpath("//*[contains(.,'valid email')]"))
                || isElementPresent(By.xpath("//*[contains(.,'Enter a valid email')]"));
    }

    public boolean isPassengerValidationDisplayed() {
        return isElementPresent(By.xpath("//*[contains(.,'Required field')]"))
                || isElementPresent(By.xpath("//*[contains(.,'Enter your first name')]"))
                || isElementPresent(By.xpath("//*[contains(.,'valid name')]"))
                || isElementPresent(By.xpath("//*[contains(.,'letters only')]"));
    }

    private void typeIfPresent(String value, By... locators) {
        if (value == null) {
            return;
        }
        WebElement element = findFirstVisible(locators);
        type(element, value);
    }
}
