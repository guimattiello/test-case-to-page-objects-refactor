package com.mycompany.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AdminPage extends BasePage {
   
    @FindBy(linkText = "Add Scenes")
    private WebElement addScene;
    
    public AdminPage(WebDriver driver) {
        super(driver);
    }
    
    public boolean isCorrectUrl() throws InterruptedException {
        Thread.sleep(5000);
        return driver.getCurrentUrl().contains("/admin.html");
    }
    
}
