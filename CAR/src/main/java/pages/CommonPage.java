package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ConsoleReporter;
import utils.TestContext;

public class CommonPage {

    protected final WebDriver driver;

    protected CommonPage(WebDriver driver) {
        this.driver = driver;
    }

    protected void logStep(String message) {
        ConsoleReporter.step(TestContext.getValue("scenario_id"), TestContext.getValue("test_case_id"), message);
    }

    protected void logInfo(String message) {
        ConsoleReporter.info(TestContext.getValue("scenario_id"), TestContext.getValue("test_case_id"), message);
    }

    protected void logPass(String message) {
        ConsoleReporter.pass(TestContext.getValue("scenario_id"), TestContext.getValue("test_case_id"), message);
    }

    protected void logFail(String message) {
        ConsoleReporter.fail(TestContext.getValue("scenario_id"), TestContext.getValue("test_case_id"), message);
    }
}
