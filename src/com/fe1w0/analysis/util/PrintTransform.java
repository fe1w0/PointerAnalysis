package com.fe1w0.analysis.util;

import soot.*;
import soot.jimple.*;

import java.util.Map;

public class PrintTransform extends SceneTransformer {
    // Transform 打印所有的指令，主要方便后面写分析代码
    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        // SootClass
        for (SootClass sc: Scene.v().getApplicationClasses()){
            // SootMethod
            for (SootMethod sootMethod: sc.getMethods()) {
                // Jimple body
                JimpleBody jimpleBody = (JimpleBody) sootMethod.retrieveActiveBody();
                System.out.println(sc.getName() + "." + sootMethod.getName()+ "." + sootMethod.getSignature() + " Units:");
                // Print all units
                int c = 1;
                for (Unit unit : jimpleBody.getUnits()) {
                    System.out.println("(" + c + ") " + unit.toString());
                    c++;
                    // Get DefinitionStmt Information
                    if (unit instanceof DefinitionStmt) {
                        Value rightValue = ((DefinitionStmt) unit).getRightOp();
                        Value leftValue = ((DefinitionStmt) unit).getLeftOp();
                        // System.out.println("RightOP: " + ((DefinitionStmt) util).getRightOp().getType().toString());
                        if (unit instanceof IdentityUnit) {
                            System.out.println("Is IdentityUnit: " + unit.toString());
                        } else if (unit instanceof AssignStmt) {
                            System.out.println("Is AssignStmt: " + unit.toString());
                            if (rightValue instanceof FieldRef) {
                                System.out.println("[*] Right Value FieldRef: " + rightValue);
                                // 真正的 FieldRef？
                                FieldRef tmpFieldRef = ((FieldRef) rightValue);
                                System.out.println("[*] GetFieldRef: " + tmpFieldRef.toString());
                                SootField sootField = tmpFieldRef.getField();
                                System.out.println("[*] GetField: " + sootField.toString());
                                for (ValueBox valueBox : rightValue.getUseBoxes()) {
                                    System.out.println(valueBox.getValue().toString());
                                }
                            }
                            if (leftValue instanceof  FieldRef) {
                                System.out.println("[*] Left Value FieldRef: " + leftValue);
                                FieldRef tmpFieldRef = ((FieldRef) leftValue);
                                System.out.println("[*] GetFieldRef: " + tmpFieldRef.toString());
                                SootField sootField = tmpFieldRef.getField();
                                System.out.println("[*] GetField: " + sootField.toString());
                                for (ValueBox valueBox : leftValue.getUseBoxes()) {
                                    System.out.println(valueBox.getValue().toString());
                                }
                            }
                        }
                        Stmt stmt = (Stmt) unit;
                        if(stmt.containsFieldRef()) {
                            FieldRef fieldRef = stmt.getFieldRef();
                            // 
                            System.out.println("Stmt: " + stmt + ". " + fieldRef);
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
