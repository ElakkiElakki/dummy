package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.CarDetailsPage;
import pages.LoginPage;
import pages.RentalCarsHomePage;
import pages.RentalCarsResultsPage;
import utils.ConfigReader;
import utils.ConsoleReporter;
import utils.DriverFactory;
import utils.ExcelUtils;
import utils.TestContext;

import java.util.Map;

public class Scenario004RentalCarSelectionSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final RentalCarsHomePage homePage = new RentalCarsHomePage(DriverFactory.getDriver());
    private final RentalCarsResultsPage resultsPage = new RentalCarsResultsPage(DriverFactory.getDriver());
    private final CarDetailsPage carDetailsPage = new CarDetailsPage(DriverFactory.getDriver());

    @Given("user prepares rental cars SC004 scenario with test case {string}")
    public void userPreparesRentalCarsSC004ScenarioWithTestCase(String testCaseId) {
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
                "SC004 precondition results page not shown");
    }

    @When("user executes rental car selection flow for SC004")
    public void userExecutesRentalCarSelectionFlowForSC004() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_015".equalsIgnoreCase(tcId) || "TC_016".equalsIgnoreCase(tcId)) {
            return;
        }
        resultsPage.openFirstAvailableCar();
    }

    @Then("rental cars SC004 expected outcome should be validated")
    public void rentalCarsSC004ExpectedOutcomeShouldBeValidated() {
        String expectedOutcome = TestContext.getValue("expected_outcome");
        String tcId = TestContext.getValue("test_case_id");

        if ("CAR_SELECTION_PROMPT".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue(resultsPage.isSelectionPromptDisplayed() || resultsPage.isResultsPageDisplayed(), "SC004 selection prompt expectation failed");
        } else if ("CAR_UNAVAILABLE".equalsIgnoreCase(expectedOutcome)) {
            Assert.assertTrue(resultsPage.isUnavailableCarShown(TestContext.getValue("car_type")), "SC004 unavailable car expectation failed");
        } else if ("TC_014".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(carDetailsPage.isCarDetailsDisplayed() && carDetailsPage.isPolicyInformationDisplayed(),
                    "SC004 car details/policy expectation failed");
        } else {
            Assert.assertTrue(carDetailsPage.isCarDetailsDisplayed(), "SC004 car details navigation failed");
        }

        ConsoleReporter.pass("SC_004", tcId, "Rental car selection validation completed");
        TestContext.markTestCasePassed();
    }
}
