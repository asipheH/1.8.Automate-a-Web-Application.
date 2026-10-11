package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
public class ChromeConnectionTest {

    private WebDriver driver;

    @Before 
    public void setUp(){
        driver = new ChromeDriver();
    }

    @Test
    public void shouldOpenLoginPage(){

        driver.get("http://localhost:8080/login");
        String pageTitle = driver.getTitle();

        assertTrue("Login page title should be displayed", pageTitle.contains("Employee Login"));
    }

    @After 
    public void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }

}
