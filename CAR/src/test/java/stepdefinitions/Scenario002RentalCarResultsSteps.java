package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.LoginPage;
import pages.RentalCarsHomePage;
import pages.RentalCarsResultsPage;
import utils.ConfigReader;
import utils.ConsoleReporter;
import utils.DriverFactory;
import utils.ExcelUtils;
import utils.TestContext;

import java.util.Map;

public class Scenario002RentalCarResultsSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final RentalCarsHomePage homePage = new RentalCarsHomePage(DriverFactory.getDriver());
    private final RentalCarsResultsPage resultsPage = new RentalCarsResultsPage(DriverFactory.getDriver());

    @Given("user prepares rental cars SC002 scenario with test case {string}")
    public void userPreparesRentalCarsSC002ScenarioWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestCaseData(ConfigReader.get("excel.file.path"), ConfigReader.get("excel.sheet.name"), testCaseId);
        TestContext.setCurrentTestData(data);
        loginPage.loginWithManualOtpIfNeeded();
        homePage.open();
    }

    @When("user executes rental car results flow for SC002")
    public void userExecutesRentalCarResultsFlowForSC002() {
        homePage.searchCars(
                TestContext.getValue("pickup"),
                TestContext.getValue("drop_off"),
                TestContext.getValue("pickup_date"),
                TestContext.getValue("drop_off_date"),
                TestContext.getValue("driver_age"));

        if ("TC_008".equalsIgnoreCase(TestContext.getValue("test_case_id"))
                && ("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed())) {
            DriverFactory.getDriver().navigate().back();
        }
    }

    @Then("rental cars SC002 expected outcome should be validated")
    public void rentalCarsSC002ExpectedOutcomeShouldBeValidated() {
        String expectedOutcome = TestContext.getValue("expected_outcome");
        String tcId = TestContext.getValue("test_case_id");

        if ("SEARCH_RESULTS".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue(("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed())
                    && resultsPage.areVehicleCardsDisplayed(), "SC002 results expectation failed");
        } else if ("NO_RESULTS".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue("NO_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || resultsPage.isNoCarsMessageDisplayed(), "SC002 no-results expectation failed");
        } else if ("PERFORMANCE".equalsIgnoreCase(expectedOutcome)) {
            long duration = TestContext.getSearchDurationMillis();
            ConsoleReporter.info("SC_002", tcId, "Rental cars load duration ms: " + duration);
            Assert.assertTrue(("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed())
                    && duration > 0 && duration < 15000, "SC002 performance expectation failed");
        } else {
            Assert.assertTrue(homePage.areSearchFieldsRetained(), "SC002 browser back retained-data validation failed");
        }

        ConsoleReporter.pass("SC_002", tcId, "Rental car results validation completed");
        TestContext.markTestCasePassed();
    }
}
