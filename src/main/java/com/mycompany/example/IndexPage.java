package com.mycompany.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class IndexPage extends BasePage {
   
    @FindBy(id = "loginUsername")
    private WebElement loginUsername;
    
    @FindBy(id = "loginPassword")
    private WebElement loginPassword;
    
    @FindBy(id = "login")
    private WebElement login;
    
    private String url = "/Environment-Simulated-Study-Room/index.html";
    
    public IndexPage(WebDriver driver, String baseUrl) {
        super(driver);
        
        driver.get(baseUrl + this.getUrl());
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public IndexPage setLoginUsername(String loginUsername) {

        this.loginUsername.clear();
        this.loginUsername.sendKeys(loginUsername);
        return this;
    }
    
    public IndexPage setLoginPassword(String loginPassword) {
        
        this.loginPassword.clear();
        this.loginPassword.sendKeys(loginPassword);
        return this;
    }
    
    public IndexPage loginFailure(String loginUsername, String loginPassword) {
        
        this.setLoginUsername(loginUsername);
        this.setLoginPassword(loginPassword);
        this.login.click();
        return this;
    }
    
    public boolean isError() throws InterruptedException {
        Thread.sleep(5000);
        return driver.getCurrentUrl().contains("/index.html?error=1");
    }
    
    public AdminPage login(String loginUsername, String loginPassword) {
        
        this.setLoginUsername(loginUsername);
        this.setLoginPassword(loginPassword);
        this.login.click();
        return new AdminPage(driver);
    }
    
}
