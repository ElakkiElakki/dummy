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

public class Scenario001PickupDropSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final AirportTaxiHomePage homePage = new AirportTaxiHomePage(DriverFactory.getDriver());
    private final SearchResultsPage resultsPage = new SearchResultsPage(DriverFactory.getDriver());

    @Given("user prepares SC001 scenario with test case {string}")
    public void userPreparesSC001ScenarioWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestCaseData(ConfigReader.get("excel.file.path"), ConfigReader.get("excel.sheet.name"), testCaseId);
        TestContext.setCurrentTestData(data);
        loginPage.loginWithManualOtpIfNeeded();
        homePage.open();
    }

    @When("user executes pickup and drop search for SC001")
    public void userExecutesPickupAndDropSearchForSC001() {
        homePage.searchTaxi(TestContext.getValue("pickup"), TestContext.getValue("drop"), TestContext.getValue("date"), TestContext.getValue("time"));
    }

    @Then("SC001 expected outcome should be validated")
    public void sc001ExpectedOutcomeShouldBeValidated() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_001".equalsIgnoreCase(tcId)) {
            boolean status = "SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || (resultsPage.isResultsPageDisplayed() && resultsPage.areVehicleCardsDisplayed());
            ConsoleReporter.info("SC_001", tcId, "Expected: search results with available cars");
            Assert.assertTrue(status, "SC001 positive flow failed");
            ConsoleReporter.pass("SC_001", tcId, "Search results displayed");
        } else {
            boolean status = "VALIDATION".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || homePage.isValidationMessageDisplayed()
                    || resultsPage.isNoCarsMessageDisplayed();
            ConsoleReporter.info("SC_001", tcId, "Expected: validation or rejection for invalid input");
            Assert.assertTrue(status, "SC001 negative flow failed");
            ConsoleReporter.pass("SC_001", tcId, "Validation/no-results displayed");
        }
        TestContext.markTestCasePassed();
    }
}
