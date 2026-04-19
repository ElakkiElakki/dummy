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

public class Scenario005NavigationFlowSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final AirportTaxiHomePage homePage = new AirportTaxiHomePage(DriverFactory.getDriver());
    private final SearchResultsPage resultsPage = new SearchResultsPage(DriverFactory.getDriver());

    @Given("user prepares SC005 scenario with test case {string}")
    public void userPreparesSC005ScenarioWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestCaseData(ConfigReader.get("excel.file.path"), ConfigReader.get("excel.sheet.name"), testCaseId);
        TestContext.setCurrentTestData(data);
        loginPage.loginWithManualOtpIfNeeded();
        homePage.open();
    }

    @When("user executes navigation flow for SC005")
    public void userExecutesNavigationFlowForSC005() {
        String tcId = TestContext.getValue("test_case_id");
        homePage.searchTaxi(TestContext.getValue("pickup"), TestContext.getValue("drop"), TestContext.getValue("date"), TestContext.getValue("time"));
        if ("TC_018".equalsIgnoreCase(tcId) || "TC_021".equalsIgnoreCase(tcId)) {
            Assert.assertTrue("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed(), "SC005 results page should display first");
            if ("TC_018".equalsIgnoreCase(tcId)) {
                resultsPage.selectFirstAvailableCar();
            } else {
                DriverFactory.getDriver().navigate().back();
            }
        }
    }

    @Then("SC005 expected outcome should be validated")
    public void sc005ExpectedOutcomeShouldBeValidated() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_018".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(resultsPage.isCarOrBookingDetailsPageDisplayed(), "SC005 valid navigation flow failed");
        } else if ("TC_019".equalsIgnoreCase(tcId) || "TC_020".equalsIgnoreCase(tcId)) {
            Assert.assertTrue("VALIDATION".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || homePage.isValidationMessageDisplayed() || !resultsPage.isResultsPageDisplayed(), "SC005 invalid navigation blocking failed");
        } else {
            Assert.assertTrue(homePage.areSearchFieldsRetained(), "SC005 browser back retained-data validation failed");
        }
        ConsoleReporter.pass("SC_005", tcId, "Navigation flow validation completed");
        TestContext.markTestCasePassed();
    }
}
