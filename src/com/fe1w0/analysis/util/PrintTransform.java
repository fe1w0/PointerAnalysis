package com.fe1w0.analysis.util;

import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
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
                System.out.println(sc.getName() + " Units:");
                // Print all units
                int c = 1;
                for (Unit util : jimpleBody.getUnits()) {
                    System.out.println("(" + c + ") " + util.toString());
                    c++;
                    // Get DefinitionStmt Information
                    if (util instanceof DefinitionStmt) {
                        // System.out.println("RightOP: " + ((DefinitionStmt) util).getRightOp().getType().toString());
                        if (util instanceof IdentityUnit) {
                            System.out.println("Is IdentityUnit: " + util.toString());
                        } else if (util instanceof AssignStmt) {
                            System.out.println("Is AssignStmt: " + util.toString());
                            System.out.println("Stmt Right Value: " + ((DefinitionStmt) util).getRightOp().toString());
                            //
                        }
                    }
                }
                System.out.println("--------------");

                // Print all locals
                System.out.println(sc.getName() + " Locals:");
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
