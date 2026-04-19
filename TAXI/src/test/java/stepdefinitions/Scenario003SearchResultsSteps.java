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

public class Scenario003SearchResultsSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final AirportTaxiHomePage homePage = new AirportTaxiHomePage(DriverFactory.getDriver());
    private final SearchResultsPage resultsPage = new SearchResultsPage(DriverFactory.getDriver());

    @Given("user prepares SC003 scenario with test case {string}")
    public void userPreparesSC003ScenarioWithTestCase(String testCaseId) {

        Map<String, String> data = ExcelUtils.getTestCaseData(
                ConfigReader.get("excel.file.path"),
                ConfigReader.get("excel.sheet.name"),
                testCaseId
        );

        TestContext.setCurrentTestData(data);

        // Login
        loginPage.loginWithManualOtpIfNeeded();

        // ===== LOGIN VALIDATION AFTER OTP =====
        boolean loggedIn = loginPage.waitForSuccessfulLogin();

        Assert.assertTrue(loggedIn,
                "Login failed. OTP not entered or login not completed.");

        homePage.open();
    }

    @When("user executes search results flow for SC003")
    public void userExecutesSearchResultsFlowForSC003() {
        homePage.searchTaxi(TestContext.getValue("pickup"), TestContext.getValue("drop"), TestContext.getValue("date"), TestContext.getValue("time"));
    }

    @Then("SC003 expected outcome should be validated")
    public void sc003ExpectedOutcomeShouldBeValidated() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_010".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed())
                    && resultsPage.areVehicleCardsDisplayed(), "SC003 valid route failed");
        } else if ("TC_011".equalsIgnoreCase(tcId)) {
            Assert.assertTrue("VALIDATION".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || homePage.isValidationMessageDisplayed() || resultsPage.isNoCarsMessageDisplayed(), "SC003 invalid route validation failed");
        } else if ("TC_012".equalsIgnoreCase(tcId)) {
            Assert.assertTrue("NO_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome())
                    || resultsPage.isNoCarsMessageDisplayed() || !resultsPage.areVehicleCardsDisplayed(), "SC003 no-cars expectation failed");
        } else {
            long duration = TestContext.getSearchDurationMillis();
            ConsoleReporter.info("SC_003", tcId, "Search load duration ms: " + duration);
            Assert.assertTrue(("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed())
                    && duration > 0 && duration < 15000, "SC003 performance expectation failed");
        }
        ConsoleReporter.pass("SC_003", tcId, "Search results validation completed");
        TestContext.markTestCasePassed();
    }
}
