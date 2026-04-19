# Booking.com Rental Cars BDD Framework

Java Selenium BDD framework for Booking.com Rental Cars flows using Maven, Cucumber, TestNG, Apache POI, and WebDriverManager.

## Highlights

- Login starts from `https://www.booking.com`
- Manual OTP entry is supported, with automatic `Enter` after all OTP digits are filled
- Car rentals is opened from Booking.com home page navigation, never by direct internal URL
- Test data is driven from `src/test/resources/testdata/booking_rental_cars_testdata.xlsx`
- Parallel execution is enabled through TestNG and the Cucumber data provider
- Console logging is emitted per step, pass, fail, and testcase result

## Project Structure

- `src/main/java/pages`: reusable page objects
- `src/main/java/utils`: config, driver, Excel, waits, and reporting
- `src/test/java/stepdefinitions`: Cucumber step definitions and hooks
- `src/test/java/runners`: TestNG Cucumber runner
- `src/test/resources/features`: feature files for SC001-SC006
- `src/test/resources/testdata`: Excel-driven testcase data

## Config

Update [`config.properties`](C:/Users/Hp/Documents/Codex/2026-04-19-files-mentioned-by-the-user-airport/src/test/resources/config.properties) before running:

- `booking.email`
- `browser`
- `headless`
- `explicit.wait.seconds`
- `page.load.timeout.seconds`
- `otp.wait.timeout.seconds`

## Run

`mvn test`
