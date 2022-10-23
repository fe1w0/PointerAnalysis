package com.fe1w0.analysis.util;

import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.JimpleBody;
import soot.Local;
import soot.Unit;

import java.util.Map;

public class AnalysisTransform extends SceneTransformer {

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {

        for (SootClass sc: Scene.v().getApplicationClasses()){
            for (SootMethod sootMethod: sc.getMethods()) {
                // Intraprocedural Analysis
                SimpleAnderson simpleAnderson = new SimpleAnderson();
                // JimpleBody
                JimpleBody jimpleBody = (JimpleBody) sootMethod.retrieveActiveBody();
                for (Unit unit : jimpleBody.getUnits()) {
                    if (unit instanceof IdentityUnit) {
                        // Example r0 := @this: objects.B
                        try {
                            Local tmpLocal = (Local) ((DefinitionStmt) unit).getLeftOp();
                            simpleAnderson.addAssignConstraints(new AssignConstraint(tmpLocal, tmpLocal));
                        } catch (ClassCastException classCastException) {
                            System.out.println("[*] ClassCastException (IdentityUnit startsWith new):" + unit);
                            System.out.println(classCastException);
                        }
                    } else if (unit instanceof AssignStmt) {
                        Value leftValue = ((DefinitionStmt) unit).getLeftOp();
                        Value rightValue = ((DefinitionStmt) unit).getRightOp();
                        if (rightValue.toString().startsWith("new")) {
                            // Example $r1 = new objects.B
                            try {
                                Local tmpLocal = (Local) leftValue;
                                simpleAnderson.addAssignConstraints(new AssignConstraint(tmpLocal, tmpLocal));
                            } catch (ClassCastException classCastException) {
                                System.out.println("[*] ClassCastException (rightValue startsWith new):" + unit);
                                System.out.println(classCastException);
                            }
                        } else if (leftValue instanceof Local && rightValue instanceof Local) {
                            // Example r4 = $r0
                            try {
                                Local leftLocal = (Local) leftValue;
                                Local rightLocal  = (Local) rightValue;
                                simpleAnderson.addAssignConstraints(new AssignConstraint(leftLocal, rightLocal));
                            } catch (ClassCastException classCastException) {
                                System.out.println("[*] ClassCastException (Local, Local):" + unit);
                                System.out.println(classCastException);
                            }
                        } else if (leftValue instanceof Local && rightValue instanceof FieldRef) {
                            // Example $r2 = r0.<objects.A: objects.B g>
                            try {
                                Local leftLocal = (Local) leftValue;
                                FieldRef rightFieldRef = (FieldRef) rightValue;
                                simpleAnderson.addAssignConstraints(new AssignConstraint(leftLocal, rightFieldRef));
                            } catch (ClassCastException classCastException) {
                                System.out.println("[*] ClassCastException (Local, FieldRef):" + unit);
                                System.out.println(classCastException);
                            }
                        } else if (leftValue instanceof FieldRef && rightValue instanceof Local) {
                            // Example r0.<objects.A: objects.B g> = r1
                            try {
                                FieldRef leftFieldRef = (FieldRef) leftValue;
                                Local rightLocal = (Local) rightValue;
                                simpleAnderson.addAssignConstraints(new AssignConstraint(leftFieldRef, rightLocal));
                            } catch (ClassCastException classCastException) {
                                System.out.println("[*] ClassCastException (FieldRef, Local):" + unit);
                                System.out.println(classCastException);
                            }
                        } else if (leftValue instanceof FieldRef && rightValue instanceof FieldRef) {
                            // Example r0.<objects.A: objects.B g> = r0.<objects.A: objects.B f>
                            try {
                                FieldRef leftFieldRef = (FieldRef) leftValue;
                                FieldRef rightFieldRef = (FieldRef) rightValue;
                                simpleAnderson.addAssignConstraints(new AssignConstraint(leftFieldRef, rightFieldRef));
                            } catch (ClassCastException classCastException){
                                System.out.println("[*] ClassCastException (FieldRef, FieldRef):" + unit);
                                System.out.println(classCastException);
                            }
                        }
                    }
                }

                // Anderson run
                simpleAnderson.solver();
                System.out.println("---------------");
                System.out.println("Analysis Result (" + sc.getName() + "." +
                        sootMethod.getName() + "." + sootMethod.getSignature() + "): ");
                System.out.println(simpleAnderson.getStringResult());
            }
        }
    }
}
