package com.fe1w0.analysis.util;

import soot.*;
import soot.jimple.JimpleBody;

import java.util.Map;

public class PrintTransform extends SceneTransformer {
    // Transform 打印所有的指令，主要方便后面写分析代码
    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        // SootClass
        for (SootClass sc: Scene.v().getApplicationClasses()){
            // SootMethod
            System.out.println("[+] Class: " + sc.getName());
            for (SootMethod sootMethod: sc.getMethods()) {
                System.out.println("[+] Method: " + sootMethod.getName());
                System.out.println("--------------");
                // Jimple body
                JimpleBody jimpleBody = (JimpleBody) sootMethod.retrieveActiveBody();
                System.out.println("Units:");
                // Print all units
                int c = 1;
                for (Unit u : jimpleBody.getUnits()) {
                    System.out.println("(" + c + ") " + u.toString());
                    c++;
                }
                System.out.println("--------------");

                // Print all locals
                System.out.println("Locals:");
                c = 1;
                for (Local local: jimpleBody.getLocals() ){
                    System.out.println("(" + c + ") " + local.toString());
                    c++;
                }
                System.out.println("--------------");
            }
        }
    }
}
