package com.mycompany.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class TemplatePage extends BasePage {
   
    @FindBy(id = "exampleid")
    private WebElement example;
    
    private String url = "/Environment-Simulated-Study-Room/index.html";
    
    public TemplatePage(WebDriver driver, String baseUrl) {
        super(driver);
        
        driver.get(baseUrl + this.getUrl());
    }
    
    public String getUrl() {
        return this.url;
    }
    
}
