package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ConfigReader;
import utils.WaitUtils;

public class LoginPage extends BasePage {

    private static final ThreadLocal<Boolean> LOGGED_IN = new ThreadLocal<Boolean>();

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void loginWithManualOtpIfNeeded() {
        if (Boolean.TRUE.equals(LOGGED_IN.get())) {
            logInfo("Login already completed for this thread");
            return;
        }

        driver.get(ConfigReader.get("base.url"));
        waitForPageReady();
        acceptCookiesIfPresent();
        openSignIn();
        enterEmailAndContinue();
        waitForManualOtpCompletionAndAutoSubmit();
        LOGGED_IN.set(Boolean.TRUE);
        logPass("Login completed");
    }

    private void openSignIn() {
        logStep("Opening sign-in");
        clickFirstAvailable(
                By.xpath("//a[normalize-space()='Sign in']"),
                By.xpath("//button[normalize-space()='Sign in']"),
                By.xpath("//*[self::a or self::button][contains(.,'Sign in')]"));
    }

    private void enterEmailAndContinue() {
        logStep("Entering email for OTP login");
        WebElement emailInput = findFirstVisible(
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[name='email']"),
                By.xpath("//input[contains(@placeholder,'Email') or contains(@aria-label,'Email')]"));
        type(emailInput, ConfigReader.get("booking.email"));
        clickFirstAvailable(
                By.xpath("//button[contains(.,'Continue with email')]"),
                By.xpath("//button[contains(.,'Continue')]"),
                By.xpath("//button[contains(.,'Sign in')]"));
    }

    private void waitForManualOtpCompletionAndAutoSubmit() {
        logInfo("Waiting for manual OTP entry. Enter will be pressed automatically after all digits are filled.");
        final boolean[] enterPressed = new boolean[]{false};
        boolean loginCompleted = WaitUtils.waitUntil(new WaitUtils.Condition() {
            @Override
            public boolean evaluate() {
                if (!enterPressed[0]) {
                    enterPressed[0] = tryPressEnterAfterOtpFilled();
                }
                return isLoggedInStateDetected();
            }
        }, ConfigReader.getInt("otp.wait.timeout.seconds", 180), 2000L);
        if (!loginCompleted) {
            throw new IllegalStateException("OTP login was not completed within timeout");
        }
    }

    private boolean tryPressEnterAfterOtpFilled() {
        List<WebElement> otpInputs = driver.findElements(By.xpath(
                "//input[@inputmode='numeric' or @autocomplete='one-time-code' or contains(@aria-label,'digit') or contains(@name,'otp') or contains(@id,'otp')]"
        ));
        if (otpInputs.isEmpty()) {
            return false;
        }

        WebElement lastFilledInput = null;
        for (WebElement input : otpInputs) {
            if (!input.isDisplayed()) {
                continue;
            }
            String value = input.getAttribute("value");
            if (value == null || value.trim().length() != 1) {
                return false;
            }
            lastFilledInput = input;
        }

        if (lastFilledInput == null) {
            return false;
        }

        lastFilledInput.sendKeys(Keys.ENTER);
        logPass("OTP digits completed. Enter pressed automatically.");
        return true;
    }

    private boolean isLoggedInStateDetected() {
        if (isAnyVisible(
                By.xpath("//*[contains(@aria-label,'Account') or contains(@aria-label,'account menu')]"),
                By.xpath("//*[contains(@data-testid,'header-profile')]"),
                By.xpath("//a[contains(@href,'myaccount')]"),
                By.xpath("//*[contains(.,'Genius') and (self::div or self::span or self::a)]")
        )) {
            return true;
        }

        if (isAnyVisible(
                By.cssSelector("input[type='email']"),
                By.xpath("//button[contains(.,'Continue with email')]"),
                By.xpath("//input[@autocomplete='one-time-code']"),
                By.xpath("//input[@inputmode='numeric']")
        )) {
            return false;
        }

        String currentUrl = driver.getCurrentUrl().toLowerCase();
        return currentUrl.contains("booking.com") && !currentUrl.contains("sign-in") && !currentUrl.contains("login");
    }

    public static void resetLoginState() {
        LOGGED_IN.remove();
    }
    public boolean waitForSuccessfulLogin() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
            wait.until(webDriver -> isLoggedInStateDetected());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
