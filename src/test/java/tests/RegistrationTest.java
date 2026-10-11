package tests;

import static org.junit.Assert.*;

import java.time.Duration;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegistrationTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String testEmail;

    private final String baseUrl = "http://localhost:8080";

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().window().maximize();

        testEmail =
                "employee"
                + UUID.randomUUID().toString().substring(0, 8)
                + "ahashe@test.com";
    }

    @Test
    public void shouldRegisterNewEmployee() {
        driver.get(baseUrl + "/register");

        fillRegistrationForm(testEmail);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("registerButton")
        )).click();

        WebElement successMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("success-message")
                )
        );

        assertTrue(
                "Registration success message should appear",
                successMessage.getText()
                        .contains("Registration successful")
        );
    }

    @Test
    public void shouldRejectDuplicateEmail() {
        driver.get(baseUrl + "/register");

        // First registration
        fillRegistrationForm(testEmail);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("registerButton")
        )).click();

        // Confirm that the first registration succeeded
        wait.until(ExpectedConditions.urlContains("/login?registered"));

        // Open registration page again
        driver.get(baseUrl + "/register");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("email")
        ));

        // Attempt registration with the same email
        fillRegistrationForm(testEmail);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("registerButton")
        )).click();

        try {
            WebElement errorMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("error-message")
                    )
            );

            assertTrue(
                    "Duplicate email should be rejected",
                    errorMessage.getText()
                            .toLowerCase()
                            .contains("already exists")
            );

        } catch (TimeoutException exception) {
            System.out.println(
                    "URL after duplicate submission: "
                    + driver.getCurrentUrl()
            );

            System.out.println(
                    "Page title: "
                    + driver.getTitle()
            );

            System.out.println(
                    "Page text after duplicate submission:\n"
                    + driver.findElement(By.tagName("body")).getText()
            );

            fail(
                    "No visible element with id='error-message' was found. "
                    + "Check the duplicate-email response and the page HTML."
            );
        }
    }

    private void fillRegistrationForm(String email) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("fullName")
        )).sendKeys("Test Employee");

        driver.findElement(By.id("email"))
                .sendKeys(email);

        driver.findElement(By.id("password"))
                .sendKeys("TestPass123");

        driver.findElement(By.id("confirmPassword"))
                .sendKeys("TestPass123");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}