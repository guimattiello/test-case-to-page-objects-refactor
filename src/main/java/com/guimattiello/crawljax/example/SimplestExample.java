/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.crawljax.example;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration.CrawljaxConfigurationBuilder;
import com.crawljax.core.configuration.Form;
import com.crawljax.core.configuration.InputSpecification;
import com.crawljax.plugins.crawloverview.CrawlOverview;

/**
 * Crawls our demo site with the default configuration. The crawl will log what
 * it's doing but will not produce any output.
 */
public class SimplestExample {

    /**
     * Run this method to start the crawl.
     */
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/Applications/MAMP/htdocs/chromedriver");
        CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor("http://localhost/Environment-Simulated-Study-Room/index.html");
        //CrawljaxConfigurationBuilder cc = CrawljaxConfiguration.builderFor("http://demo.crawljax.com");
        builder.setBrowserConfig(new BrowserConfiguration(BrowserType.CHROME, 2));

        builder.crawlRules().insertRandomDataInInputForms(true);
        
        InputSpecification input = new InputSpecification();
        Form contactForm = new Form();
        contactForm.field("loginUsername").setValue("admin");
        contactForm.field("loginPassword").setValue("Admin!@#3010");
        input.setValuesInForm(contactForm).beforeClickElement("button").withText("Sign in");
        builder.crawlRules().setInputSpec(input);
        
        //builder.addPlugin(new CrawlOverview());
        
        CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());

        crawljax.call();
        
    }
}
