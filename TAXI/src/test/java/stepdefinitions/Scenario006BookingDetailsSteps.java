package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.AirportTaxiHomePage;
import pages.BookingDetailsPage;
import pages.LoginPage;
import pages.SearchResultsPage;
import utils.ConfigReader;
import utils.ConsoleReporter;
import utils.DriverFactory;
import utils.ExcelUtils;
import utils.TestContext;

import java.util.Map;

public class Scenario006BookingDetailsSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final AirportTaxiHomePage homePage = new AirportTaxiHomePage(DriverFactory.getDriver());
    private final SearchResultsPage resultsPage = new SearchResultsPage(DriverFactory.getDriver());
    private final BookingDetailsPage bookingDetailsPage = new BookingDetailsPage(DriverFactory.getDriver());

    @Given("user prepares SC006 scenario with test case {string}")
    public void userPreparesSC006ScenarioWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestCaseData(ConfigReader.get("excel.file.path"), ConfigReader.get("excel.sheet.name"), testCaseId);
        TestContext.setCurrentTestData(data);
        loginPage.loginWithManualOtpIfNeeded();
        homePage.open();
        homePage.searchTaxi(TestContext.getValue("pickup"), TestContext.getValue("drop"), TestContext.getValue("date"), TestContext.getValue("time"));
        Assert.assertTrue("SEARCH_RESULTS".equalsIgnoreCase(TestContext.getLastSearchOutcome()) || resultsPage.isResultsPageDisplayed(), "SC006 precondition search results not shown");
        resultsPage.selectFirstAvailableCar();
        Assert.assertTrue(resultsPage.isCarOrBookingDetailsPageDisplayed(), "SC006 booking details page not opened");
    }

    @When("user executes booking details flow for SC006")
    public void userExecutesBookingDetailsFlowForSC006() {
        bookingDetailsPage.fillPassengerDetails(
                TestContext.getValue("first_name"),
                TestContext.getValue("last_name"),
                TestContext.getValue("email"),
                TestContext.getValue("phone"));
        bookingDetailsPage.proceedWithBooking();
    }

    @Then("SC006 expected outcome should be validated")
    public void sc006ExpectedOutcomeShouldBeValidated() {
        String tcId = TestContext.getValue("test_case_id");
        if ("TC_022".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(bookingDetailsPage.isBookingSummaryOrConfirmationDisplayed(), "SC006 valid booking details flow failed");
        } else if ("TC_023".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(bookingDetailsPage.isEmailValidationDisplayed(), "SC006 invalid email validation failed");
        } else {
            Assert.assertTrue(bookingDetailsPage.isPassengerValidationDisplayed(), "SC006 passenger details validation failed");
        }
        ConsoleReporter.pass("SC_006", tcId, "Booking details validation completed");
        TestContext.markTestCasePassed();
    }
}
