package com.mycompany.roaster;

import com.guimattiello.utils.Util;
import java.util.List;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Annotation;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.PropertySource;

//https://github.com/forge/roaster
public class Refatoracao2 {

    public static void main(String[] args) {

        //JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, "public class SomeClass {}");
        String sourceCode = (new Util()).readFile("/Users/guimat/NetBeansProjects/refatoracao/src/main/java/com/mycompany/example/IndexPage.java");
        JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, sourceCode);
        
        MethodSource md = javaClass.addMethod()
                .setPublic()
                .setStatic(true)
                .setName("main")
                .setReturnTypeVoid()
                .setBody("System.out.println(\"Hello World\");");

        md.addParameter("String[]", "args");
        md.addAnnotation("Test");
        
        List<MethodSource<JavaClassSource>> methods = javaClass.getMethods();
        
        for (MethodSource<JavaClassSource> method : methods) {
            System.out.println(method.getInternal());
            System.out.println(method.getReturnType());
            System.out.println(method.getName());
        }

        System.out.println("----------");
      
        javaClass.addProperty("String", "teste").setType("int");
        List<PropertySource<JavaClassSource>> props = javaClass.getProperties();
        
        for (PropertySource<JavaClassSource> prop : props) {
                
            System.out.println(prop.getType() + " - " + prop.getName());
            
            System.out.println(prop.getAnnotation("FindBy"));
            //List<? extends Annotation<JavaClassSource>> annotations = prop.getAnnotations();
            //System.out.println(annotations.get(0).getAnnotationValue());
        }
        
        //System.out.println(javaClass);
        //System.out.println(javaClass.getMethods().get(0).getName());
    }

}
