/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.spoon;

import com.company.example.Person;
import com.guimattiello.utils.Util;
import java.util.List;
import java.util.Set;
import spoon.Launcher;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.AnnotationFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FieldFactory;
import spoon.reflect.factory.MethodFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

/**
 *
 * @author guimat
 */
public class RefatoracaoSpoon {
    
    public static void main(String[] args) throws ClassNotFoundException {
        
        /*Class<Person> person = Person.class;
        person = (Class<Person>) new Person().getClass();
        
        Class p = Person.class;
        System.out.println(Class.forName("com.mycompany.example.Person"));
        CtClass l = Launcher.parseClass(p.toString());
        System.out.println(l);
        */
        String sourceCode = (new Util()).readFile("/Users/guimat/NetBeansProjects/refatoracao/src/test/java/adminLoginTest.java");
        String templatePage = (new Util()).readFile("/Users/guimat/NetBeansProjects/refatoracao/src/main/java/com/mycompany/example/IndexPage.java");
        
        //CtClass l2 = Launcher.parseClass("class A { String nome; public void m() { System.out.println(\"yeah\");} }");
        CtClass l2 = Launcher.parseClass(sourceCode);
        CtClass templateClass = Launcher.parseClass(templatePage);
        
        Set<CtMethod> methods = l2.getMethods();
        List<CtField> fields = l2.getFields();
        
        Factory factory = l2.getFactory();
        
        FieldFactory ff = new FieldFactory(l2.getFactory());
        
        AnnotationFactory af = new AnnotationFactory(l2.getFactory());
 
        for (CtMethod method : methods) {
            
            //Pega os metodos @Test
            //Verifica se tem FindBy e cria um atributo
            //Se tiver sendKeys cria um método setAtributo
            
            method.getBody().addStatement(l2.getFactory().Code().createCodeSnippetStatement("Thread.sleep(2000)"));
            
            CtTypeReference ctr = factory.createTypeReference();
            ctr.setSimpleName("Test(expected = IOException.class)");
            CtAnnotation cs = factory.Code().createAnnotation(ctr);
            method.getFactory().createAnnotation(ctr);

            method.getAnnotations().forEach(n -> {
                if (n.toString().equals("@Test")) {
                    method.getBody().forEach(m -> {
                        System.out.println(m.toString());
                        System.out.println("------");
                        
                    });
                }
            });
            //System.out.println(method.getSimpleName());
        }
        System.out.println("@@@@@@@@@@@@");
        for (CtMethod method : methods) {
            System.out.println(method.getAnnotations());
        }
    }
    
}
