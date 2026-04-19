package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class BasePage extends CommonPage {

    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds", 25)));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected boolean isAnyVisible(By... locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    protected WebElement findFirstVisible(By... locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    return element;
                }
            }
        }
        throw new NoSuchElementException("No visible element found");
    }

    protected void clickFirstAvailable(By... locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                if (element.isDisplayed() && element.isEnabled()) {
                    try {
                        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                        return;
                    } catch (Exception ignored) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                        return;
                    }
                }
            }
        }
        throw new NoSuchElementException("No clickable element found");
    }

    protected void type(WebElement element, String value) {
        element.click();
        element.clear();
        element.sendKeys(value);
    }

    protected void blur(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", element);
        } catch (Exception ignored) {
            element.sendKeys(Keys.TAB);
        }
    }

    protected void waitForPageReady() {
        wait.until((ExpectedCondition<Boolean>) webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    public void acceptCookiesIfPresent() {
        By[] cookieButtons = new By[]{
                By.cssSelector("#onetrust-accept-btn-handler"),
                By.xpath("//button[contains(.,'Accept all')]"),
                By.xpath("//button[contains(.,'Accept')]"),
                By.xpath("//button[contains(.,'I agree')]")
        };
        for (By locator : cookieButtons) {
            try {
                if (isElementPresent(locator)) {
                    clickFirstAvailable(locator);
                    sleep(1000);
                    logInfo("Cookie popup handled");
                    return;
                }
            } catch (Exception ignored) {
            }
        }
    }
    protected boolean typeAndSelectFirstSuggestion(By inputLocator, String value) {
        WebElement input = waitForVisible(inputLocator);
        input.click();
        input.clear();
        input.sendKeys(value);
        sleep(1200);

        try {
            WebElement firstSuggestion = findFirstVisible(
                    By.xpath("(//li[@role='option'])[1]"),
                    By.xpath("(//li[contains(@id,'autocomplete-result')])[1]"),
                    By.xpath("(//div[@role='option'])[1]"),
                    By.xpath("(//*[contains(@data-testid,'autocomplete-result')])[1]"),
                    By.xpath("(//*[contains(@class,'suggest') or contains(@class,'autocomplete')])[1]")
            );

            firstSuggestion.click();
            logPass("Suggestion selected for: " + value);
            return true;
        } catch (Exception e) {
            try {
                input.sendKeys(Keys.ARROW_DOWN);
                sleep(500);
                input.sendKeys(Keys.ENTER);
                logPass("Suggestion selected using keyboard for: " + value);
                return true;
            } catch (Exception ex) {
                logInfo("Suggestion popup did not appear for: " + value + ". Keeping typed value.");
                return false;
            }
        }
    }

    protected boolean hasInvalidState(WebElement element) {
        try {
            String ariaInvalid = element.getAttribute("aria-invalid");
            if ("true".equalsIgnoreCase(ariaInvalid)) {
                return true;
            }

            String classValue = element.getAttribute("class");
            if (classValue != null) {
                String normalized = classValue.toLowerCase(Locale.ENGLISH);
                if (normalized.contains("error") || normalized.contains("invalid")) {
                    return true;
                }
            }

            String borderColor = element.getCssValue("border-color");
            if (borderColor != null) {
                String normalizedColor = borderColor.toLowerCase(Locale.ENGLISH);
                if (normalizedColor.contains("255, 0, 0")
                        || normalizedColor.contains("214, 40, 40")
                        || normalizedColor.contains("220, 38, 38")) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    protected List<By> asList(By... locators) {
        return Arrays.asList(locators);
    }
    protected boolean waitForUrlContains(String value) {
        try {
            wait.until(ExpectedConditions.urlContains(value));
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
