package tests;

import environment.EnvironmentManager;
import environment.RunEnvironment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DemoTest {

    @Before
    public void startBrowser() {
        EnvironmentManager.initWebDriver();
    }

    @Test
    public void demo() {
        WebDriver driver = RunEnvironment.getWebDriver();
        driver.get("https://www.blazemeter.com/selenium");
        WebElement element = driver.findElement(By.id("main-header")).findElement(By.tagName("a"));
        String homeUrl = element.getAttribute("href");
        assertEquals(homeUrl, "https://www.blazemeter.com/");
    }

    @After
    public void tearDown() { EnvironmentManager.shutDownDriver(); }
}
