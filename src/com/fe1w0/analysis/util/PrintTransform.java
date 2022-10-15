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
                for (Unit unit : jimpleBody.getUnits()) {
                    System.out.println("(" + c + ") " + unit.toString());
                    c++;
                    // Get DefinitionStmt Information
                    if (unit instanceof DefinitionStmt) {
                        // System.out.println("RightOP: " + ((DefinitionStmt) util).getRightOp().getType().toString());
                        if (unit instanceof IdentityUnit) {
                            System.out.println("Is IdentityUnit: " + unit.toString());
                        } else if (unit instanceof AssignStmt) {
                            System.out.println("Is AssignStmt: " + unit.toString());
                            System.out.println("Stmt Right Value: " + ((DefinitionStmt) unit).getRightOp().toString());
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
