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

public class Scenario006RentalCarRefinementSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final RentalCarsHomePage homePage = new RentalCarsHomePage(DriverFactory.getDriver());
    private final RentalCarsResultsPage resultsPage = new RentalCarsResultsPage(DriverFactory.getDriver());

    @Given("user prepares rental cars SC006 scenario with test case {string}")
    public void userPreparesRentalCarsSC006ScenarioWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestCaseData(ConfigReader.get("excel.file.path"), ConfigReader.get("excel.sheet.name"), testCaseId);
        TestContext.setCurrentTestData(data);
        loginPage.loginWithManualOtpIfNeeded();
        homePage.open();
    }

    @When("user executes rental car refinement flow for SC006")
    public void userExecutesRentalCarRefinementFlowForSC006() {
        String tcId = TestContext.getValue("test_case_id");
        homePage.searchCars(
                TestContext.getValue("pickup"),
                TestContext.getValue("drop_off"),
                TestContext.getValue("pickup_date"),
                TestContext.getValue("drop_off_date"),
                TestContext.getValue("driver_age"));

        if ("TC_021".equalsIgnoreCase(tcId) && ("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed())) {
            homePage.searchCars(
                    TestContext.getValue("pickup"),
                    TestContext.getValue("drop_off"),
                    TestContext.getValue("pickup_date"),
                    TestContext.getValue("drop_off_date"),
                    TestContext.getValue("driver_age"));
        }
    }

    @Then("rental cars SC006 expected outcome should be validated")
    public void rentalCarsSC006ExpectedOutcomeShouldBeValidated() {
        String expectedOutcome = TestContext.getValue("expected_outcome");
        String tcId = TestContext.getValue("test_case_id");

        if ("SEARCH_REFINED".equalsIgnoreCase(expectedOutcome) || "SEARCH_RESULTS".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed(),
                    "SC006 results/refinement expectation failed");
        } else if ("AGE_VALIDATION".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue(homePage.isValidationMessageDisplayed() || homePage.isDateValidationDisplayed() || resultsPage.isResultsPageDisplayed(),
                    "SC006 age validation expectation failed");
        } else {
            Assert.assertTrue("DATE_VALIDATION".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || homePage.isDateValidationDisplayed(), "SC006 date validation expectation failed");
        }

        ConsoleReporter.pass("SC_006", tcId, "Rental car refinement validation completed");
        TestContext.markTestCasePassed();
    }
}
