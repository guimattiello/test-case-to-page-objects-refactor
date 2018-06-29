/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

/**
 *
 * @author guimat
 */
public class PageObject {

    private String className;
    private CtClass pageClass;
    private String html;

    public PageObject(String className, String javaSource) {

        this.className = className;

        this.pageClass = Launcher.parseClass(javaSource);

    }
    
    public void createConstructor(CtFieldReference driver, CtFieldReference baseUrl) {
        
        final CtTypeReference<List<WebDriver>> driverRef = this.pageClass.getFactory().Code().createCtTypeReference(WebDriver.class);        
        final CtTypeReference<List<String>> urlRef = this.pageClass.getFactory().Code().createCtTypeReference(String.class);        
        final CtCodeSnippetStatement statementInConstructorDriver = this.pageClass.getFactory().Code().createCodeSnippetStatement("this." + driver.toString() + " = " + driver.toString());
        final CtCodeSnippetStatement statementInConstructorBaseUrl = this.pageClass.getFactory().Code().createCodeSnippetStatement("this." + baseUrl.toString() + " = " + baseUrl.toString());
        final CtCodeSnippetStatement statementPageFactoryInit = this.pageClass.getFactory().Code().createCodeSnippetStatement("PageFactory.initElements(" + driver.toString() + ", this)");
        final CtBlock<?> ctBlockOfConstructor = this.pageClass.getFactory().Code().createCtBlock(statementInConstructorDriver);
        ctBlockOfConstructor.addStatement(statementInConstructorBaseUrl);
        ctBlockOfConstructor.addStatement(statementPageFactoryInit);
        final CtParameter<List<WebDriver>> parameterDriver = this.pageClass.getFactory().Core().<List<WebDriver>>createParameter();
        final CtParameter<List<String>> parameterBaseUrl = this.pageClass.getFactory().Core().<List<String>>createParameter();
        parameterDriver.<CtParameter>setType(driverRef);
        parameterDriver.setSimpleName(driver.toString());
        parameterBaseUrl.<CtParameter>setType(urlRef);
        parameterBaseUrl.setSimpleName(baseUrl.toString());
        final CtConstructor constructor = this.pageClass.getFactory().Core().createConstructor();
        constructor.setBody(ctBlockOfConstructor);
        ArrayList<CtParameter> params = new ArrayList<CtParameter>();
        params.add(parameterDriver);
        params.add(parameterBaseUrl);
        constructor.setParameters(params);
        constructor.addModifier(ModifierKind.PUBLIC);

        // Apply transformation.
        this.pageClass.addConstructor(constructor);
        
    }

    public boolean fieldIsInHtml(String findBy, String name) {

        Pattern pattern = Pattern.compile(".*<input.*" + findBy + "=\"" + name + "\".*>.*");

        Matcher matcher = pattern.matcher(this.html);

        if (matcher.find()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a field fieldName exists in the class pageClass
     *
     * @param fieldName the simple name of the field you want to check
     * @return
     */
    public boolean fieldExistsInCtClass(String fieldName) {

        List<CtField> fields = pageClass.getFields();

        for (CtField field : fields) {

            if (field.getSimpleName().equals(fieldName)) {
                return true;
            }

        }

        return false;

    }

    public CtStatement getStatementToInstantiate() {

        return this.pageClass.getFactory().createCodeSnippetStatement(this.pageClass.getSimpleName() + " " + this.pageClass.getSimpleName().toLowerCase() + " = new " + this.pageClass.getSimpleName() + "()");

    }

    public boolean createFieldAndSetter(String fieldName, String findBy) {

        Factory factory = pageClass.getFactory();

        if (!this.fieldExistsInCtClass(fieldName)) {
            CtField field = factory.createField();

            //Create field annotation
            CtTypeReference ctr = factory.createTypeReference();
            ctr.setSimpleName("FindBy(" + findBy + " = \"" + fieldName.toLowerCase() + "\")");
            CtAnnotation annotation = factory.Code().createAnnotation(ctr);

            //Set annotation to field
            field.addAnnotation(annotation);

            CtTypeReference ctrField = factory.createTypeReference();
            ctrField.setSimpleName("WebElement");

            //Set field params
            field.setSimpleName(fieldName.toLowerCase());
            field.setType(ctrField);
            field.setVisibility(ModifierKind.PRIVATE);

            //Add field to the page class
            pageClass.addField(field);

            //Create method to set value to field
            CtMethod method = factory.createMethod();
            method.setSimpleName("set" + fieldName);
            CtTypeReference<String> stringRef = factory.Code().createCtTypeReference(String.class);
            CtTypeReference voidRef = factory.createCtTypeReference(void.class);
            CtCodeSnippetStatement statementInMethod = factory.Code().createCodeSnippetStatement("this." + fieldName.toLowerCase() + " = " + fieldName.toLowerCase());
            CtBlock<?> ctBlockOfMethod = factory.Code().createCtBlock(statementInMethod);
            CtParameter<String> parameter = factory.Core().<String>createParameter();
            parameter.<CtParameter>setType(stringRef);
            parameter.setSimpleName(fieldName.toLowerCase());
            method.setBody(ctBlockOfMethod);
            method.addParameter(parameter);
            method.addModifier(ModifierKind.PUBLIC);
            method.setType(voidRef);

            //Add method to the page class
            pageClass.addMethod(method);

            return true;
        }

        return false;
    }

    public String createUniqueField(String fieldName, String findBy) {

        Factory factory = pageClass.getFactory();

        String newFieldName = fieldName.toLowerCase().replace(" ", "");
        int cont = 1;

        if (this.fieldExistsInCtClass(newFieldName)) {
            while (this.fieldExistsInCtClass(newFieldName + cont)) {
                cont++;
            }
            newFieldName += cont;
        }

        CtField field = factory.createField();

        //Create field annotation
        CtTypeReference ctr = factory.createTypeReference();
        ctr.setSimpleName("FindBy(" + findBy + " = \"" + fieldName + "\")");
        CtAnnotation annotation = factory.Code().createAnnotation(ctr);

        //Set annotation to field
        field.addAnnotation(annotation);

        CtTypeReference ctrField = factory.createTypeReference();
        ctrField.setSimpleName("WebElement");

        //Set field params
        field.setSimpleName(newFieldName);
        field.setType(ctrField);
        field.setVisibility(ModifierKind.PRIVATE);

        //Add field to the page class
        pageClass.addField(field);

        return newFieldName;
    }

    public CtClass getPageClass() {
        return pageClass;
    }

    public void setPageClass(CtClass pageClass) {
        this.pageClass = pageClass;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
