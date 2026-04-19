package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CarDetailsPage extends BasePage {

    public CarDetailsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCarDetailsDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(.,'Mileage')]"),
                By.xpath("//*[contains(.,'Fuel policy')]"),
                By.xpath("//*[contains(.,'Cancellation policy')]"),
                By.xpath("//*[contains(.,'Included in the price')]"),
                By.xpath("//*[contains(.,'Car rental terms')]")
        );
    }

    public boolean isPolicyInformationDisplayed() {
        return isAnyVisible(
                By.xpath("//*[contains(.,'Fuel policy')]"),
                By.xpath("//*[contains(.,'Cancellation policy')]"),
                By.xpath("//*[contains(.,'Rental terms')]")
        );
    }
}
