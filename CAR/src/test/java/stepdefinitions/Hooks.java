package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ConsoleReporter;
import utils.DriverFactory;
import utils.TestContext;

public class Hooks {

    @Before(order = 0)
    public void setUp() {
        DriverFactory.getDriver();
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        boolean passed = !scenario.isFailed() && TestContext.isTestCasePassed();
        if (!passed) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
            ConsoleReporter.fail(TestContext.getValue("scenario_id"), TestContext.getValue("test_case_id"), "Scenario failed: " + scenario.getName());
            ConsoleReporter.result(TestContext.getValue("scenario_id"), TestContext.getValue("test_case_id"), "FAILED");
        } else {
            ConsoleReporter.result(TestContext.getValue("scenario_id"), TestContext.getValue("test_case_id"), "PASSED");
        }
        TestContext.clear();
        if (ConfigReader.getBoolean("quit.driver.after.scenario", false)) {
            DriverFactory.quitDriver();
            LoginPage.resetLoginState();
        }
    }
}
