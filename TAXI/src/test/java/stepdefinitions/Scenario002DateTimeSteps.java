package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.AirportTaxiHomePage;
import pages.LoginPage;
import pages.SearchResultsPage;
import utils.ConfigReader;
import utils.ConsoleReporter;
import utils.DriverFactory;
import utils.ExcelUtils;
import utils.TestContext;

import java.util.Map;

public class Scenario002DateTimeSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final AirportTaxiHomePage homePage = new AirportTaxiHomePage(DriverFactory.getDriver());
    private final SearchResultsPage resultsPage = new SearchResultsPage(DriverFactory.getDriver());

    @Given("user prepares SC002 scenario with test case {string}")
    public void userPreparesSC002ScenarioWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestCaseData(ConfigReader.get("excel.file.path"), ConfigReader.get("excel.sheet.name"), testCaseId);
        TestContext.setCurrentTestData(data);
        loginPage.loginWithManualOtpIfNeeded();
        homePage.open();
    }

    @When("user executes pickup date and time flow for SC002")
    public void userExecutesPickupDateAndTimeFlowForSC002() {
        Assert.assertTrue(homePage.isCurrentDateVisibleInPickupWidget(), "Current date widget did not show current date");
        homePage.searchTaxi(TestContext.getValue("pickup"), TestContext.getValue("drop"), TestContext.getValue("date"), TestContext.getValue("time"));
    }

    @Then("SC002 expected outcome should be validated")
    public void sc002ExpectedOutcomeShouldBeValidated() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_006".equalsIgnoreCase(tcId)) {
            boolean status = "SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed();
            ConsoleReporter.info("SC_002", tcId, "Expected: valid future date/time should proceed");
            Assert.assertTrue(status, "SC002 valid date/time failed");
        } else {
            boolean status = "VALIDATION".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || homePage.isValidationMessageDisplayed()
                    || !resultsPage.isResultsPageDisplayed();
            ConsoleReporter.info("SC_002", tcId, "Expected: past date should be blocked or validated");
            Assert.assertTrue(status, "SC002 invalid past date validation failed");
        }
        ConsoleReporter.pass("SC_002", tcId, "Date/time validation completed");
        TestContext.markTestCasePassed();
    }
}
