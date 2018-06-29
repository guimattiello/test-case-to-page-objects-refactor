/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

/**
 *
 * @author guimat
 */
public class PageObject {

    private String className;
    private CtClass pageClass;
    private String html;

    public PageObject(String className, String html) {

        this.className = className;
        this.html = html;

        String classSource = "public class " + className + " extends BasePage {}";

        this.pageClass = Launcher.parseClass(classSource);
        
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
    
    public boolean createField(String fieldName, String findBy) {

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
