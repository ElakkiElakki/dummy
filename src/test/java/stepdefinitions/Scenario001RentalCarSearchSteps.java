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

public class Scenario001RentalCarSearchSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final RentalCarsHomePage homePage = new RentalCarsHomePage(DriverFactory.getDriver());
    private final RentalCarsResultsPage resultsPage = new RentalCarsResultsPage(DriverFactory.getDriver());

    @Given("user prepares rental cars SC001 scenario with test case {string}")
    public void userPreparesRentalCarsSC001ScenarioWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestCaseData(ConfigReader.get("excel.file.path"), ConfigReader.get("excel.sheet.name"), testCaseId);
        TestContext.setCurrentTestData(data);
        loginPage.loginWithManualOtpIfNeeded();
        homePage.open();
    }

    @When("user executes rental car search flow for SC001")
    public void userExecutesRentalCarSearchFlowForSC001() {
        homePage.searchCars(
                TestContext.getValue("pickup"),
                TestContext.getValue("drop_off"),
                TestContext.getValue("pickup_date"),
                TestContext.getValue("drop_off_date"),
                TestContext.getValue("driver_age"));
    }

    @Then("rental cars SC001 expected outcome should be validated")
    public void rentalCarsSC001ExpectedOutcomeShouldBeValidated() {
        String expectedOutcome = TestContext.getValue("expected_outcome");
        String tcId = TestContext.getValue("test_case_id");

        if ("SEARCH_RESULTS".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || (resultsPage.isResultsPageDisplayed() && resultsPage.areVehicleCardsDisplayed()), "SC001 valid search failed");
        } else if ("DATE_VALIDATION".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue("DATE_VALIDATION".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || homePage.isDateValidationDisplayed(), "SC001 date validation failed");
        } else {
            Assert.assertTrue("VALIDATION".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || homePage.isValidationMessageDisplayed(), "SC001 mandatory/invalid location validation failed");
        }

        ConsoleReporter.pass("SC_001", tcId, "Rental car search validation completed");
        TestContext.markTestCasePassed();
    }
}
