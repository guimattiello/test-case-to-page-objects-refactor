/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.testrefactor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 *
 * @author guimat
 */
public class AdapterPage {

    private WebDriver driver;

    private String baseUrl;

    public AdapterPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "loginUsername")
    private WebElement loginusername;

    @FindBy(id = "loginUsername")
    private WebElement loginusername1;

    @FindBy(id = "loginPassword")
    private WebElement loginpassword;

    @FindBy(id = "loginPassword")
    private WebElement loginpassword1;

    @FindBy(id = "login")
    private WebElement login;

    @FindBy(linkText = "Edit Scenes")
    private WebElement editscenes;

    @FindBy(id = "editScenes")
    private WebElement editscenes1;

    @FindBy(id = "edit")
    private WebElement edit;

    @FindBy(css = "button.btn.btn-primary")
    private WebElement button_btn_btn_primary;

    public AdapterPage get1() {
        driver.get(((baseUrl) + "/Environment-Simulated-Study-Room/index.html"));
        return this;
    }

    public AdapterPage clearLoginUsername2() {
        loginusername.clear();
        return this;
    }

    public AdapterPage setLoginUsername3() {
        loginusername1.sendKeys("admin");
        return this;
    }

    public AdapterPage clearLoginPassword4() {
        loginpassword.clear();
        return this;
    }

    public AdapterPage setLoginPassword5() {
        loginpassword1.sendKeys("Admin!@#3010");
        return this;
    }

    public AdapterPage clickLogin6() {
        login.click();
        return this;
    }

    public AdapterPage clickEditScenes7() {
        editscenes.click();
        return this;
    }

    public AdapterPage sleep8() throws InterruptedException {
        Thread.sleep(5000);
        return this;
    }

    public boolean contains9() {
        return driver.getCurrentUrl().contains("/editScene.html");
    }

    public AdapterPage clickEditScenes10() {
        editscenes1.click();
        return this;
    }

    public AdapterPage clickEdit11() {
        edit.click();
        return this;
    }

    public AdapterPage clickButton_btn_btn_primary12() {
        button_btn_btn_primary.click();
        return this;
    }
}
