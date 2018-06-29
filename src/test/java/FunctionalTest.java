

import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author guimat
 * https://github.com/kschiller/page-object-pattern-tutorial/blob/Initial/FunctionalTest.java
 */
public class FunctionalTest {
    
    protected static WebDriver driver;
    protected static String baseUrl;
    
    public FunctionalTest() {
    }
    
    @BeforeClass
    public static void setUp() {
        System.setProperty("webdriver.gecko.driver", "/Applications/MAMP/htdocs/geckodriver");
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        baseUrl = "http://localhost:8888/";
    }
    
    @After
    public void cleanUp() {
        driver.manage().deleteAllCookies();
    }
    
    @AfterClass
    public static void tearDown() {
        driver.close();
    }

}
