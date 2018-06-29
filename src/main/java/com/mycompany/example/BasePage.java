package com.mycompany.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class BasePage {
    
    protected WebDriver driver;
    
    public BasePage (WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    //COLOCAR OS MÉTODOS PARA RETORNAREM A PAGE OBJECT
}
