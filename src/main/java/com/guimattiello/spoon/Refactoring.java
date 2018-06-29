/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.spoon;

import com.guimattiello.crawler4j.BasicCrawlController;
import com.guimattiello.utils.PageObject;
import com.guimattiello.utils.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

/**
 *
 * @author guimat
 */
public class Refactoring {

    public static void main(String[] args) throws Exception {

        //Run the crawler to get the html from pages
        //BasicCrawlController.runCrawler();
        ArrayList<PageObject> pages = new ArrayList<PageObject>();

        File folder = new File("htmlpages/");

        Util util = new Util();

        //Create the page objects classes from the html extracted with the crawler
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.getName().endsWith(".txt")) {
                String[] split = (fileEntry.getName().split("\\."));
                String className = split[0];
                PageObject newPo = new PageObject(util.firstLetterToUpperCase(className) + "Page", util.readFile(fileEntry.getAbsolutePath()));
                pages.add(newPo);
            }
        }

        for (PageObject page : pages) {
            page.createField(util.firstLetterToUpperCase("username"), "id");
            //System.out.println(page.getHtml());
        }

        String classToRefactorCode = (new Util()).readFile("/Users/guimat/NetBeansProjects/refatoracao/src/test/java/adminClicksCreateSceneTest.java");
        String templatePage = (new Util()).readFile("/Users/guimat/NetBeansProjects/refatoracao/src/main/java/com/mycompany/example/TemplatePage.java");

        CtClass rfClass = Launcher.parseClass(classToRefactorCode);
        CtClass templateClass = Launcher.parseClass(templatePage);

        Factory rfFactory = rfClass.getFactory();

        Set<CtMethod> ctMethods = rfClass.getMethods();

        for (CtMethod method : ctMethods) {

            CtMethod newMethod = rfFactory.createMethod();
            newMethod.setSimpleName(method.getSimpleName() + "PO");
            newMethod.setType(method.getType());

            System.out.println(method.getAnnotations());
            
            method.getAnnotations().forEach(ann -> {
                //If the method is a Test case, then refactor the statements into the Page Object                
                if (ann.toString().contains("@Test")) {
                //if (method.getAnnotations().toString().contains("@Test")) {

                    CtBlock block = method.getBody();
                    List<CtStatement> statements = block.getStatements();

                    for (CtStatement statement : statements) {

                        //Refactor findElement to new attribute
                        Pattern pattern = Pattern.compile(".*findElement\\(By.(.*?)\\(\"(.*?)\"\\)\\)(.*?)");
                        Matcher matcher = pattern.matcher(statement.toString());

                        if (matcher.matches()) {

                            String findBy = matcher.group(1);
                            String fieldName = matcher.group(2);
                            String action = matcher.group(3);

                            //Encontra o Page Object
                            PageObject po = util.findFieldInPageObjects(fieldName, findBy, pages);
                            
                            //Cria o field
                            if (po != null) {
                                
                                po.createField(fieldName, findBy);

                                CtStatement cts = rfFactory.createCodeSnippetStatement(po.getClassName().toLowerCase() + "." + fieldName + action);
                                
                                System.out.println(cts.toString());
                                
                            } else {
                                
                                System.out.println(statement.toString());
                                
                            }

                            
                        }

                    }
                }
            });

        }

        System.out.println(pages.get(1).getPageClass());
    }

}
