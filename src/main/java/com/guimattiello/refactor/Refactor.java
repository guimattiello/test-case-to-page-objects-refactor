/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.refactor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;

/**
 *
 * @author guimat
 */
public class Refactor {

    int methodCont;
    
    public void refactor(String pathToTestCaseFile) {

        methodCont = 1;
        
        Launcher spoon = new Launcher();
        
        //String classToRefactorCode = (new Util()).readFile("/Users/guimat/NetBeansProjects/test-case-to-page-objects-refactor/src/test/java/adminClicksCreateSceneTest.java");
        String classToRefactorCode = (new Util()).readFile(pathToTestCaseFile);

        String adapterPage = "public class AdapterPage {}";
        CtClass rfClass = Launcher.parseClass(classToRefactorCode);
        PageObject adapterPO = new PageObject("AdapterPage", adapterPage);

        Factory rfFactory = rfClass.getFactory();
        Factory poFactory = adapterPO.getPageClass().getFactory();
        poFactory.getEnvironment().setAutoImports(true);
        File f = new File("/Users/guimat/NetBeansProjects/test-case-to-page-objects-refactor/src/test/java/com/guimattiello/testrefactor/");
        spoon.getEnvironment().setSourceOutputDirectory(f);
        spoon.run();
        
        //Find driver and base url attributes in test case class
        CtFieldReference driverAttrName = this.identifyDriveAttrName(rfClass);
        CtFieldReference urlAttrName = this.identifySutUrl(rfClass);
        
        //Creates adapter constructor
        adapterPO.createConstructor(driverAttrName, urlAttrName);
        
        //Add driver and base url attributes to adapter to use the same sintax of the test cases
        adapterPO.getPageClass().addField(driverAttrName.getFieldDeclaration());
        adapterPO.getPageClass().addField(urlAttrName.getFieldDeclaration());
        
        
        List<CtMethod> adapterMethods = new ArrayList<CtMethod>();
        
        Set<CtMethod> ctMethods = rfClass.getMethods();

        //Run through all methods
        for (CtMethod method : ctMethods) {

            //Get the annotations to look for test methods
            method.getAnnotations().forEach(ann -> {
                
                //If the method is a Test case, then refactor the statements into the Page Object                
                if (ann.toString().contains("@Test")) {
                    
                    //Cria método refatorado com chamadas ao Adapter
                    CtMethod newMethod = rfFactory.createMethod();

                    //Set to new method the same annotations of the old method
                    newMethod.setAnnotations(method.getAnnotations());
                    
                    newMethod.setSimpleName(method.getSimpleName() + "PO");
                    newMethod.setType(method.getType());
                    newMethod.setVisibility(ModifierKind.PUBLIC);

                    String objectAdapterName = "a";
                    CtStatement ctsNewAdapter = rfFactory.createCodeSnippetStatement("AdapterPage a = new AdapterPage(" + driverAttrName + ", " + urlAttrName.toString() + ")");
                    
                    CtBlock block = method.getBody();
                    List<CtStatement> statements = block.getStatements();
                    
                    CtBlock newBlock = rfFactory.createBlock();
                    newBlock.addStatement(ctsNewAdapter);

                    for (CtStatement statement : statements) {

                        //Cria método novo com a instrução no Adapter
                        String newMethodName = this.choseNewMethodNameByStatement(statement);
                        CtMethod newAdapterMethod = rfFactory.createMethod();
                        newAdapterMethod.setSimpleName(newMethodName);
                        newAdapterMethod.setType(method.getType());
                        newAdapterMethod.setType(rfFactory.Code().createCtTypeReference(AdapterPage.class));
                        newAdapterMethod.setVisibility(ModifierKind.PUBLIC);
                        
                        CtBlock newAdapterBlock = poFactory.createBlock();
                        
                        boolean addMethodToAdapter = false;
                        boolean addReturnThisToAdapter = false;
                        
                        //Rule 1 - Refactor findElement to new attribute
                        Pattern patternFindBy = Pattern.compile(".*findElement\\(By.(.*?)\\(\"(.*?)\"\\)\\)(.*?)");
                        Matcher matcherFindBy = patternFindBy.matcher(statement.toString());
                        
                        //Rule 2 - Refactor Thread.* to new adapter method
                        Pattern patternThread = Pattern.compile("Thread\\..*");
                        Matcher matcherThread = patternThread.matcher(statement.toString());
                        
                        //Rule 3 - Refactor Assert Equals to new adapter method
                        Pattern patternAssertEquals = Pattern.compile("assertEquals\\((.*?),(.*?)\\)");
                        Matcher matcherAssertEquals = patternAssertEquals.matcher(statement.toString());
                        
                        //Rule 4 - Refactor Assert True to new adapter method
                        Pattern patternAssertTrue = Pattern.compile("assertTrue\\((.*?)\\)");
                        Matcher matcherAssertTrue = patternAssertTrue.matcher(statement.toString());
                        
                        //Rule 5 - Refactor driver.get() to new adapter method
                        Pattern patternDriverGet = Pattern.compile("(.*?).get\\((.*?)\\)");
                        Matcher matcherDriverGet = patternDriverGet.matcher(statement.toString());
                        
                        if (matcherFindBy.matches()) {
                            
                            String findBy = matcherFindBy.group(1);
                            String fieldName = matcherFindBy.group(2);
                            String action = matcherFindBy.group(3);

                            //Test Class statement
                            CtStatement cts = rfFactory.createCodeSnippetStatement(objectAdapterName + "." + newMethodName + "()");
                            newBlock.addStatement(cts);
                            
                            String attributeName = adapterPO.createUniqueField(fieldName, findBy);
                            
                            //Adapter method statement
                            CtStatement ctsAdapter = poFactory.createCodeSnippetStatement(attributeName + action);
                            newAdapterBlock.addStatement(ctsAdapter); 
                            
                            addMethodToAdapter = true;
                            addReturnThisToAdapter = true;
                            
                        } else if (matcherThread.matches()) {
                            
                            CtStatement cts = rfFactory.createCodeSnippetStatement(objectAdapterName + "." + newMethodName + "()");
                            newBlock.addStatement(cts);
                            
                            //Adapter method statement
                            newAdapterBlock.addStatement(statement); 
                            
                            addMethodToAdapter = true;
                            addReturnThisToAdapter = true;
                            
                        } else if (matcherAssertEquals.matches()) {
                            
                            String assertParameterExpected = matcherAssertEquals.group(1);
                            String assertParameterActual = matcherAssertEquals.group(2);
                            
                            CtStatement cts = rfFactory.createCodeSnippetStatement("assertEquals(" + assertParameterExpected + ", " + objectAdapterName + "." + newMethodName + "())");
                            newBlock.addStatement(cts);
                            
                            //Adapter method statement
                            CtStatement ctsAdapter = poFactory.createCodeSnippetStatement("return " + assertParameterActual);
                            newAdapterBlock.addStatement(ctsAdapter);
                            
                            newAdapterMethod.setType(rfFactory.Code().createCtTypeReference(boolean.class));
                            
                            addMethodToAdapter = true;
                            
                        } else if (matcherAssertTrue.matches()) {
                            
                            String assertParameter = matcherAssertTrue.group(1);
                            
                            CtStatement cts = rfFactory.createCodeSnippetStatement("assertTrue(" + objectAdapterName + "." + newMethodName + "())");
                            newBlock.addStatement(cts);
                            
                            //Adapter method statement
                            CtStatement ctsAdapter = poFactory.createCodeSnippetStatement("return " + assertParameter);
                            newAdapterBlock.addStatement(ctsAdapter);
                            
                            newAdapterMethod.setType(rfFactory.Code().createCtTypeReference(boolean.class));
                            
                            addMethodToAdapter = true;

                        } else if (matcherDriverGet.matches()) {
                            
                            String assertParameter = matcherDriverGet.group(2);
                            
                            //Test Class statement
                            CtStatement cts = rfFactory.createCodeSnippetStatement(objectAdapterName + "." + newMethodName + "()");
                            newBlock.addStatement(cts);
                            
                            //Adapter method statement
                            CtStatement ctsAdapter = poFactory.createCodeSnippetStatement(assertParameter);
                            newAdapterBlock.addStatement(statement);
                            
                            addMethodToAdapter = true;
                            addReturnThisToAdapter = true;
                            
                        } else {
                            
                            newBlock.addStatement(statement); 
                            
                            newAdapterBlock.addStatement(statement); 
                            
                        }
                        
                        if (addReturnThisToAdapter)
                            newAdapterBlock.addStatement(poFactory.createCodeSnippetStatement("return this"));
                        
                        newAdapterMethod.setBody(newAdapterBlock);
                        
                        if (addMethodToAdapter)
                            adapterMethods.add(newAdapterMethod);
                            //adapterPO.getPageClass().addMethod(newAdapterMethod);
                        
                    }
                    
                    newMethod.setBody(newBlock);
                    
                    //Add refactored method to the target class
                    rfClass.addMethod(newMethod);
                }
            });

        }
        
        for (CtMethod m : adapterMethods)
            adapterPO.getPageClass().addMethod(m);

        System.out.println(rfClass);
        System.out.println(adapterPO.getPageClass());
    }

    public CtFieldReference identifyDriveAttrName(CtClass clazz) {
        
        Collection<CtFieldReference<?>> fields = clazz.getAllFields();
        
        for (CtFieldReference<?> field : fields) {
            if (field.getType().toString().equals("org.openqa.selenium.WebDriver")) {
                return field;
            }
        }

        return null;
    }
    
    public CtFieldReference identifySutUrl(CtClass clazz) {
        
        Collection<CtFieldReference<?>> fields = clazz.getAllFields();
        
        for (CtFieldReference<?> field : fields) {
            
            if (field.toString().toLowerCase().contains("url"))
                return field;
            
            Pattern patternUrl = Pattern.compile(".*" + field.toString() + " = \\\"http.*");
            Matcher matcherUrl = patternUrl.matcher(clazz.toString());
            
            if (matcherUrl.matches()) {
                System.out.println("teste");
                return field;
            }
        }

        return null;
    }
    
    public String choseNewMethodNameByStatement(CtStatement s) {
        String statement = s.toString();
        
        Pattern patternFindBy = Pattern.compile(".*findElement\\(By.(.*?)\\(\"(.*?)\"\\)\\)(.*?)");
        //findElement(By.id("loginUsername"))
        Matcher matcherFindBy = patternFindBy.matcher(statement.toString());
        
        Pattern patternSendKeys = Pattern.compile(".*sendKeys\\(.*\\)");
        Matcher matcherSendKeys = patternSendKeys.matcher(statement.toString());
        
        Pattern pattern = Pattern.compile(".*\\.(.*?)\\((.*?)\\).*");
        Matcher matcher = pattern.matcher(statement.toString());
        
        if (matcherSendKeys.matches()) {
            
            statement = "set";
            
        } else if (matcher.matches()) {
            
            statement = matcher.group(1);
            
        }
        if (matcherFindBy.matches()) {
            
            String fieldName = matcherFindBy.group(2);
            fieldName = fieldName
                    .replace(" ", "")
                    .replace(".", "_")
                    .replace("-", "_")
                    .replace("<", "_")
                    .replace(">", "");;
            fieldName = fieldName.substring(0,1).toUpperCase().concat(fieldName.substring(1));
            statement += fieldName;
            
        }
        
        return statement + methodCont++;
    }
    
}
