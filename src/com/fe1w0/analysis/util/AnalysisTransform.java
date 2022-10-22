package com.fe1w0.analysis.util;

import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.JimpleBody;
import soot.Local;
import soot.Unit;
import soot.jimple.internal.JInstanceFieldRef;
import sun.management.counter.Units;

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
                        try {
                            Local tmpLocal = (Local) ((DefinitionStmt) unit).getLeftOp();
                            simpleAnderson.addAssignConstraints(new AssignConstraint(tmpLocal, tmpLocal));
                        } catch (ClassCastException classCastException) {
                            System.out.println("[*] ClassCastException (IdentityUnit startsWith new):" + unit);
                            System.out.println(classCastException);
                        }
                    } else if (unit instanceof AssignStmt) {
                        Value rightValue = ((DefinitionStmt) unit).getRightOp();
                        Value leftValue = ((DefinitionStmt) unit).getLeftOp();
                        if (rightValue.toString().startsWith("new")) {
                            try {
                                Local tmpLocal = (Local) ((DefinitionStmt) unit).getLeftOp();
                                simpleAnderson.addAssignConstraints(new AssignConstraint(tmpLocal, tmpLocal));
                            } catch (ClassCastException classCastException) {
                                System.out.println("[*] ClassCastException (rightValue startsWith new):" + unit);
                                System.out.println(classCastException);
                            }
                        } else if (rightValue instanceof FieldRef) {
                            try {
                                Value tmpFrom = leftValue;
                                FieldRef tmpTo = (FieldRef) ((FieldRef) rightValue).getFieldRef().resolve();
                                simpleAnderson.addAssignConstraints(new AssignConstraint(tmpFrom, tmpTo));
                            } catch (Exception exception) {
                                System.out.println("[*] Exception (rightValue Instanceof FieldRef):" + unit);
                                System.out.println(((FieldRef) rightValue).getFieldRef().resolve());
                                System.out.println(exception);
                            }
                        } else if (leftValue instanceof FieldRef) {
                            try {
                                Value tmpTo = rightValue;
                                FieldRef tmpFrom = (FieldRef) ((FieldRef) leftValue).getFieldRef().resolve();
                                simpleAnderson.addAssignConstraints(new AssignConstraint(tmpFrom, tmpTo));
                            } catch (Exception exception) {
                                System.out.println("[*] Exception (leftValue Instanceof FieldRef):" + unit);
                                System.out.println(((FieldRef) leftValue).getFieldRef().resolve());
                                System.out.println(exception);
                            }
                        } else if (rightValue instanceof Local) {
                            try {
                                Local tmpLocalFrom = (Local) ((DefinitionStmt) unit).getLeftOp();
                                Local tmpLocalTo = (Local) ((DefinitionStmt) unit).getRightOp();
                                simpleAnderson.addAssignConstraints(new AssignConstraint(tmpLocalFrom, tmpLocalTo));
                            } catch (ClassCastException classCastException) {
                                System.out.println("[*] ClassCastException (rightValue Instanceof Local):" + unit);
                                System.out.println(classCastException);
                            }
                        }
                    }
                }
                // Anderson run
                simpleAnderson.run();
                System.out.println("---------------");
                System.out.println("Analysis Result (" + sc.getName() + "." + sootMethod.getName() + "): ");
                System.out.println(simpleAnderson.getStringResult());
            }
        }
    }
}
