package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;
import java.util.Locale;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<WebDriver>();

    private DriverFactory() {
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            WebDriver driver = createDriver();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("page.load.timeout.seconds", 60)));
            DRIVER.set(driver);
        }
        return DRIVER.get();
    }

    private static WebDriver createDriver() {
        String browser = ConfigReader.get("browser").toLowerCase(Locale.ENGLISH);
        if ("edge".equals(browser)) {
            WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            if (ConfigReader.getBoolean("headless", false)) {
                options.addArguments("--headless=new");
                options.addArguments("--window-size=1920,1080");
            }
            return new EdgeDriver(options);
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        if (ConfigReader.getBoolean("headless", false)) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        return new ChromeDriver(options);
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
