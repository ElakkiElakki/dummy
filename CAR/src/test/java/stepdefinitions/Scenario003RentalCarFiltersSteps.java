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

public class Scenario003RentalCarFiltersSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final RentalCarsHomePage homePage = new RentalCarsHomePage(DriverFactory.getDriver());
    private final RentalCarsResultsPage resultsPage = new RentalCarsResultsPage(DriverFactory.getDriver());

    @Given("user prepares rental cars SC003 scenario with test case {string}")
    public void userPreparesRentalCarsSC003ScenarioWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestCaseData(ConfigReader.get("excel.file.path"), ConfigReader.get("excel.sheet.name"), testCaseId);
        TestContext.setCurrentTestData(data);
        loginPage.loginWithManualOtpIfNeeded();
        homePage.open();
        homePage.searchCars(
                TestContext.getValue("pickup"),
                TestContext.getValue("drop_off"),
                TestContext.getValue("pickup_date"),
                TestContext.getValue("drop_off_date"),
                TestContext.getValue("driver_age"));
        Assert.assertTrue("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed(),
                "SC003 precondition results page not shown");
    }

    @When("user executes rental car filter flow for SC003")
    public void userExecutesRentalCarFilterFlowForSC003() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_009".equalsIgnoreCase(tcId)) {
            resultsPage.applyCarTypeFilter(TestContext.getValue("car_type"));
        } else if ("TC_010".equalsIgnoreCase(tcId)) {
            resultsPage.applySupplierFilter(TestContext.getValue("supplier"));
            resultsPage.applyTransmissionFilter(TestContext.getValue("transmission"));
        } else if ("TC_011".equalsIgnoreCase(tcId)) {
            resultsPage.applyTransmissionFilter("Automatic");
            resultsPage.clearAllFilters();
        } else {
            resultsPage.applyCarTypeFilter(TestContext.getValue("car_type"));
            resultsPage.applyFuelFilter(TestContext.getValue("fuel_type"));
        }
    }

    @Then("rental cars SC003 expected outcome should be validated")
    public void rentalCarsSC003ExpectedOutcomeShouldBeValidated() {
        String expectedOutcome = TestContext.getValue("expected_outcome");
        String tcId = TestContext.getValue("test_case_id");

        if ("FILTER_RESULTS".equalsIgnoreCase(expectedOutcome)) {
            boolean status = resultsPage.isFilterApplied(TestContext.getValue("car_type"))
                    || resultsPage.isFilterApplied(TestContext.getValue("supplier"))
                    || resultsPage.isFilterApplied(TestContext.getValue("transmission"));
            Assert.assertTrue(status, "SC003 filter application failed");
        } else if ("FILTER_RESET".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue(resultsPage.isFilterReset(), "SC003 filter reset failed");
        } else {
            Assert.assertTrue(resultsPage.isNoCarsMessageDisplayed() || resultsPage.isFilterApplied(TestContext.getValue("fuel_type")),
                    "SC003 filter no-results expectation failed");
        }

        ConsoleReporter.pass("SC_003", tcId, "Rental car filter validation completed");
        TestContext.markTestCasePassed();
    }
}
