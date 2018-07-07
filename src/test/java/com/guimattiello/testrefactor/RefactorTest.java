/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.testrefactor;

import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author guimat
 */
public class RefactorTest {

    private WebDriver driver;

    private String baseUrl;

    private boolean acceptNextAlert = true;

    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.gecko.driver", "/Applications/MAMP/htdocs/geckodriver");
        driver = new FirefoxDriver();
        baseUrl = "http://localhost/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    //@Test
    public void test() throws Exception {
        this.driver.get(((this.baseUrl) + "/Environment-Simulated-Study-Room/index.html"));
        driver.findElement(By.id("loginUsername")).clear();
        driver.findElement(By.id("loginUsername")).sendKeys("admin");
        driver.findElement(By.id("loginPassword")).clear();
        driver.findElement(By.id("loginPassword")).sendKeys("Admin!@#3010");
        driver.findElement(By.id("login")).click();
        driver.findElement(By.linkText("Add Scenes")).click();
        Thread.sleep(5000);
        assertTrue(driver.getCurrentUrl().contains("/addScene.html"));
        driver.findElement(By.id("create")).click();
        Thread.sleep(5000);
        assertTrue(driver.getCurrentUrl().contains("/addScene.html"));
        assertTrue(((driver.findElements(By.cssSelector("button.btn.btn-primary")).size()) > 0));
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!("".equals(verificationErrorString))) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }

    @Test
    public void testPO() throws InterruptedException {
        AdapterPage a = new AdapterPage(driver, baseUrl);
        a.get1();
        a.clearLoginUsername2();
        a.setLoginUsername3();
        a.clearLoginPassword4();
        a.setLoginPassword5();
        a.clickLogin6();
        a.sleep7();
        assertTrue(a.contains8());
    }
}
