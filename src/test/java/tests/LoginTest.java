package tests;

import static org.junit.Assert.*;

import java.time.Duration;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String baseUrl = "http://localhost:8080";
    private final String password = "TestPass123";

    private String testEmail;

    @Before 
    public void setUp(){
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        testEmail = "login" + UUID.randomUUID().toString().substring(0,8) + "ahashe@sars.gov.za";
    }

    @Test 
    public void shouldLoginWithValidCredentials(){
        registerEmployee();

        driver.get(baseUrl + "/login");
        driver.findElement(By.id("email")).sendKeys(testEmail);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("loginButton")).click();

        WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("welcomeMessage")));

        assertTrue("Welcome message should be displayed", welcomeMessage.getText().contains("Welcome"));

        assertTrue("Employee name should be displayed", driver.findElement(By.tagName("body")).getText().contains("Test Employee"));
    }

    @Test 
    public void shouldRejectIncorrectPassword(){
        registerEmployee();

        driver.get(baseUrl + "/login");
        driver.findElement(By.id("email")).sendKeys(testEmail);
        driver.findElement(By.id("password")).sendKeys("WrongPassword123");
        driver.findElement(By.id("loginButton")).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error-message")));

        assertTrue("Incorrect credentials should be rejected", errorMessage.getText().contains("Invalid email or password"));
    }

    private void registerEmployee(){

        driver.get(baseUrl + "/register");
        driver.findElement(By.id("fullName")).sendKeys("Test Employee");
		driver.findElement(By.id("email")).sendKeys(testEmail);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.id("confirmPassword")).sendKeys(password);

        driver.findElement(By.id("registerButton")).click();

        wait.until(ExpectedConditions.urlContains("/login?registered"));
    }

    @After 
    public void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }

}
