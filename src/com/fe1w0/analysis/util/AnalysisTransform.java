package com.fe1w0.analysis.util;

import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.JimpleBody;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;

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
                        Local tmpLocal = (Local) ((DefinitionStmt) unit).getLeftOp();
                        System.out.println("IdentityUnit startsWith new:" + tmpLocal);
                        simpleAnderson.addAssignConstraints(new AssignConstraint(tmpLocal, tmpLocal));
                    } else if (unit instanceof AssignStmt) {
                        Value rightValue = ((DefinitionStmt) unit).getRightOp();
                        if (rightValue.toString().startsWith("new")) {
                            Local tmpLocal = (Local) ((DefinitionStmt) unit).getLeftOp();
                            System.out.println("rightValue startsWith new:" + tmpLocal);
                            simpleAnderson.addAssignConstraints(new AssignConstraint(tmpLocal, tmpLocal));
                        } else if (rightValue instanceof Local) {
                            System.out.println("rightValue Instanceof Local:" + rightValue);
                            Local tmpLocalFrom = (Local) ((DefinitionStmt) unit).getLeftOp();
                            Local tmpLocalTo = (Local) ((DefinitionStmt) unit).getRightOp();
                            simpleAnderson.addAssignConstraints(new AssignConstraint(tmpLocalFrom, tmpLocalTo));
                        }
                    }
                }
                // Anderson run
                simpleAnderson.run();
            }
        }
    }
}
