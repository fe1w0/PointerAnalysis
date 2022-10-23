package com.fe1w0.analysis.util;

import soot.Local;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;

import java.util.*;


class ConstraintValue {
    // from, to 我们都认为是一个 ConstraintValue
    // ConstraintValue 是由一个 Local 和 一个 FieldRef
    Local localValue;
    FieldRef fieldRefValue;

    ConstraintValue (Local local){
        // 约束的变量为 Local
        localValue = local;
        fieldRefValue = null;
    }

    ConstraintValue (FieldRef fieldRef) throws ClassCastException {
        List<Local> localList = ConstraintValue.getLocalFromFieldRef(fieldRef);
        if (localList.size() == 1) {
            localValue = localList.get(0);
            fieldRefValue = fieldRef;
        } else {
            throw new ClassCastException("Owning to localList.size() not equal 1, program fails to construct AssignConstraint object");
        }
    }

    ConstraintValue (Local local, FieldRef fieldRef) {
        localValue = local;
        fieldRefValue = fieldRef;
    }
    public static List<Local> getLocalFromFieldRef(FieldRef fieldRef) {
        // 得到fieldRef 的 所有 Local，但为什么我感觉FieldRef只有一个Local？
        List<Local> localList = new ArrayList<Local>();
        for (ValueBox valueBox : fieldRef.getUseBoxes()) {
            localList.add((Local)valueBox.getValue());
        }
        return localList;
    }
}


class AssignConstraint {
    ConstraintValue fromConstraintValue;
    ConstraintValue toConstraintValue;

    AssignConstraint (ConstraintValue from, ConstraintValue to) {
        fromConstraintValue = from;
        toConstraintValue  = to;
    }

    AssignConstraint (Local to, Local from) {
        fromConstraintValue = new ConstraintValue(to);
        toConstraintValue = new ConstraintValue(from);
    }

    AssignConstraint (Local to, FieldRef from) throws ClassCastException {
        toConstraintValue = new ConstraintValue(to);
        fromConstraintValue = new ConstraintValue(from);
    }

    AssignConstraint (FieldRef to, Local from) throws ClassCastException {
        toConstraintValue = new ConstraintValue(to);
        fromConstraintValue = new ConstraintValue(from);
    }

    AssignConstraint (FieldRef to, FieldRef from) throws ClassCastException {
        toConstraintValue = new ConstraintValue(to);
        fromConstraintValue = new ConstraintValue(from);
    }
}

public class SimpleAnderson {

    // 存储所有的约束信息
    private List<AssignConstraint> assignConstraints = new ArrayList<AssignConstraint>();

    // Anderson求解结果, constraintsResults anderson 约束求解结果
    Map<ConstraintValue, List<ConstraintValue>> constraintsResults = new HashMap<ConstraintValue, List<ConstraintValue>>();

    // assignConstraints 添加新约束信息，用于后续分析
    void addAssignConstraints(AssignConstraint assignConstraint) {
        assignConstraints.add(assignConstraint);
    }

    void solver() {
        try {
            for (AssignConstraint assignConstraint : assignConstraints) {
                if (assignConstraint.toConstraintValue == assignConstraint.fromConstraintValue) {
                    List<ConstraintValue> tmpLocals = new ArrayList<ConstraintValue>();
                    tmpLocals.add(assignConstraint.fromConstraintValue);
                    constraintsResults.put(assignConstraint.fromConstraintValue, tmpLocals);
                } else {
                    if (!constraintsResults.containsKey(assignConstraint.fromConstraintValue)) {
                        List<ConstraintValue> tmpLocals = new ArrayList<ConstraintValue>();
                        tmpLocals.add(assignConstraint.toConstraintValue);
                        constraintsResults.put(assignConstraint.fromConstraintValue, tmpLocals);
                    }
                    if (constraintsResults.containsKey(assignConstraint.toConstraintValue)) {
                        for (ConstraintValue tmpValue : constraintsResults.get(assignConstraint.toConstraintValue)) {
                            if (!constraintsResults.get(assignConstraint.fromConstraintValue).contains(tmpValue)) {
                                constraintsResults.get(assignConstraint.fromConstraintValue).add(tmpValue);
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            System.out.println("[*] Solver Failed.");
            System.out.println(exception);
        }
    }

        String getStringResult(){
            StringBuffer stringResult  = new StringBuffer();
            for(Map.Entry<ConstraintValue, List<ConstraintValue>> item : constraintsResults.entrySet()) {
                ConstraintValue fromConstraintValue = item.getKey();
                List<ConstraintValue> toConstraintValueList = item.getValue();
                if (fromConstraintValue.fieldRefValue != null) {
                    stringResult.append(fromConstraintValue.fieldRefValue + " : ");
                } else {
                    stringResult.append(fromConstraintValue.localValue.toString() + " : ");
                }
                for (ConstraintValue toConstraintValue : toConstraintValueList) {
                    if (toConstraintValue.fieldRefValue != null) {
                        stringResult.append(toConstraintValue.fieldRefValue + " ");
                    } else {
                        stringResult.append(toConstraintValue.localValue.toString() + " ");
                    }
                }
                stringResult.append("\n");
            }
            return stringResult.toString();
    }
}