/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.refactor;

import java.io.File;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;

/**
 *
 * @author guimat
 */
public class ClassProcessor extends AbstractProcessor<CtClass> {

    @Override
    public void process(CtClass e) {
        Launcher spoon = new Launcher();
        spoon.addInputResource("/Users/guimat/NetBeansProjects/Environment-Simulated-Study-Room/test/com/example/tests/adminClicksCreateSceneTest.java");
        spoon.getFactory().getEnvironment().setAutoImports(true);
        Environment env = spoon.getFactory().getEnvironment();
        File f = new File("/Users/guimat/NetBeansProjects/test-case-to-page-objects-refactor/src/test/java/com/guimattiello/testoutput/");
        JavaOutputProcessor processor = new JavaOutputProcessor(f, new DefaultJavaPrettyPrinter(env));
        processor.createJavaFile(e);
        spoon.getEnvironment().setSourceOutputDirectory(f);
        CtModel model = spoon.buildModel();
        
        spoon.run();
        spoon.prettyprint();
    }
    
}
