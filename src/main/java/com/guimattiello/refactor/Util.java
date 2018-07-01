/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.refactor;

import com.guimattiello.utils.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

/**
 *
 * @author guimat
 */
public class Util {

    /**
     * Read the class file and returns the source code
     * @param path class file that needs refactoring
     * @return 
     */
    public String readFile(String path) {

        BufferedReader br = null;
        FileReader fr = null;

        String sourceCode = "";

        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(path);
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                sourceCode += sCurrentLine;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (br != null) {
                    br.close();
                }

                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        return sourceCode;
    }

    /**
     * Checks if a field fieldName exists in the class clazz
     * @param clazz the class you want to check if the field exists
     * @param fieldName the simple name of the field you want to check
     * @return
     */
    public boolean fieldExists(CtClass clazz, String fieldName) {

        List<CtField> fields = clazz.getFields();

        for (CtField field : fields) {

            if (field.getSimpleName().equals(fieldName)) {
                return true;
            }

        }

        return false;

    }

    /**
     * Converts a statement into a method of the Page Object class
     * @param templatePageClass the resulting Page Object class
     * @param cs the statement which needs to be refactored into the Page Object
     * resulting class
     */
    public CtStatement refactor(CtClass templatePageClass, CtStatement cs) {

        //Refactor findElement to new attribute
        String statement = cs.toString();
        Pattern pattern = Pattern.compile(".*findElement\\(By.(.*?)\\(\"(.*?)\"\\)\\)(.*?)");
        Matcher matcher = pattern.matcher(statement);

        if (matcher.matches()) {

            String findBy = matcher.group(1);
            String fieldName = matcher.group(2);
            String action = matcher.group(3);

            Factory factory = templatePageClass.getFactory();
            
            if (!this.fieldExists(templatePageClass, fieldName)) {
                
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
                field.setSimpleName(fieldName);
                field.setType(ctrField);
                field.setVisibility(ModifierKind.PRIVATE);
                
                //Add field to the template page object class
                templatePageClass.addField(field);
                
            }
            
            CtStatement cts = factory.createCodeSnippetStatement("this." + fieldName + action);

            return cts;
            
        }

        return cs;
    }
    
    public String firstLetterToUpperCase(String word) {
        
        return word.substring(0, 1).toUpperCase() + word.substring(1);
        
    }

    public PageObject findFieldInPageObjects(String fieldName, String findBy, List<PageObject> pages) {
        
        for (PageObject page : pages) {
            if (page.fieldIsInHtml(findBy, fieldName)) {
                return page;
            }
        }
        
        return null;
    }
    
}
