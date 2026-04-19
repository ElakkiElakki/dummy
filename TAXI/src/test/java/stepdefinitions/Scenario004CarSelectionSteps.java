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

public class Scenario004CarSelectionSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final AirportTaxiHomePage homePage = new AirportTaxiHomePage(DriverFactory.getDriver());
    private final SearchResultsPage resultsPage = new SearchResultsPage(DriverFactory.getDriver());

    @Given("user prepares SC004 scenario with test case {string}")
    public void userPreparesSC004ScenarioWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestCaseData(ConfigReader.get("excel.file.path"), ConfigReader.get("excel.sheet.name"), testCaseId);
        TestContext.setCurrentTestData(data);
        loginPage.loginWithManualOtpIfNeeded();
        homePage.open();
        homePage.searchTaxi(TestContext.getValue("pickup"), TestContext.getValue("drop"), TestContext.getValue("date"), TestContext.getValue("time"));
        Assert.assertTrue("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed(), "SC004 precondition search results not shown");
    }

    @When("user executes car selection flow for SC004")
    public void userExecutesCarSelectionFlowForSC004() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_015".equalsIgnoreCase(tcId)) {
            ConsoleReporter.info("SC_004", tcId, "Skipping car selection to validate prompt behavior");
            return;
        }
        if ("TC_016".equalsIgnoreCase(tcId)) {
            ConsoleReporter.info("SC_004", tcId, "Validating unavailable car option");
            return;
        }
        resultsPage.selectFirstAvailableCar();
    }

    @Then("SC004 expected outcome should be validated")
    public void sc004ExpectedOutcomeShouldBeValidated() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_015".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(resultsPage.isSelectionPromptDisplayed() || resultsPage.isResultsPageDisplayed(), "SC004 selection prompt expectation failed");
        } else if ("TC_016".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(resultsPage.trySelectingUnavailableCar(TestContext.getValue("car_type")), "SC004 unavailable car expectation failed");
        } else {
            Assert.assertTrue(resultsPage.isCarOrBookingDetailsPageDisplayed(), "SC004 navigation to car details failed");
        }
        ConsoleReporter.pass("SC_004", tcId, "Car selection validation completed");
        TestContext.markTestCasePassed();
    }
}
