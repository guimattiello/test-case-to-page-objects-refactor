/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guimattiello.refactor;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

/**
 *
 * @author guimat
 */
public class Refatoracao {
    
    public static void main(String[] args) {
        
        Refactor r = new Refactor();
        //r.refactor();
        
        Launcher spooner = new Launcher();
        
        spooner.addInputResource("/Users/guimat/NetBeansProjects/Environment-Simulated-Study-Room/test/com/example/tests/adminClicksCreateSceneTest.java");
        spooner.process();
        spooner.prettyprint();
        
        String classToRefactorCode = (new Util()).readFile("/Users/guimat/NetBeansProjects/Environment-Simulated-Study-Room/test/com/example/tests/adminClicksCreateSceneTest.java");
        CtClass rfClass = Launcher.parseClass(classToRefactorCode);
        ClassProcessor cp = new ClassProcessor();
        cp.process(rfClass);
        
    }
    
}
