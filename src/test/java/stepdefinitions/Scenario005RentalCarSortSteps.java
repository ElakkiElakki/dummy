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

public class Scenario005RentalCarSortSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final RentalCarsHomePage homePage = new RentalCarsHomePage(DriverFactory.getDriver());
    private final RentalCarsResultsPage resultsPage = new RentalCarsResultsPage(DriverFactory.getDriver());

    @Given("user prepares rental cars SC005 scenario with test case {string}")
    public void userPreparesRentalCarsSC005ScenarioWithTestCase(String testCaseId) {
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
                "SC005 precondition results page not shown");
    }

    @When("user executes rental car sort flow for SC005")
    public void userExecutesRentalCarSortFlowForSC005() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_017".equalsIgnoreCase(tcId)) {
            resultsPage.applySort("Price");
        } else if ("TC_018".equalsIgnoreCase(tcId)) {
            resultsPage.applySort("Best rated");
        } else if ("TC_019".equalsIgnoreCase(tcId)) {
            // comparison is validated from visible cards
        } else {
            resultsPage.applyCarTypeFilter(TestContext.getValue("car_type"));
            resultsPage.applySupplierFilter(TestContext.getValue("supplier"));
            resultsPage.applyTransmissionFilter(TestContext.getValue("transmission"));
            resultsPage.openFirstAvailableCar();
            DriverFactory.getDriver().navigate().back();
        }
    }

    @Then("rental cars SC005 expected outcome should be validated")
    public void rentalCarsSC005ExpectedOutcomeShouldBeValidated() {
        String expectedOutcome = TestContext.getValue("expected_outcome");
        String tcId = TestContext.getValue("test_case_id");

        if ("SORT_APPLIED".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue(resultsPage.isSortApplied("Price") || resultsPage.isSortApplied("Best rated"), "SC005 sort application failed");
        } else if ("COMPARISON_VIEW".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue(resultsPage.canCompareVisibleSpecifications(), "SC005 comparison expectation failed");
        } else {
            Assert.assertTrue(resultsPage.isFilterApplied(TestContext.getValue("car_type"))
                    || resultsPage.isFilterApplied(TestContext.getValue("supplier"))
                    || resultsPage.isFilterApplied(TestContext.getValue("transmission")), "SC005 back retained filter state failed");
        }

        ConsoleReporter.pass("SC_005", tcId, "Rental car sort/comparison validation completed");
        TestContext.markTestCasePassed();
    }
}
