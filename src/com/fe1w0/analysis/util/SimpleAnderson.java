package com.fe1w0.analysis.util;

import soot.Local;
import soot.ValueBox;
import soot.jimple.FieldRef;

import java.util.*;


class ConstraintValue {
    // to, from 我们都认为是一个 ConstraintValue
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
//            System.out.println("TEST: " + localValue + " ; " + fieldRefValue);
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

    ConstraintValue toConstraintValue;
    ConstraintValue fromConstraintValue;

    AssignConstraint (ConstraintValue to, ConstraintValue from) {
        toConstraintValue  = to;
        fromConstraintValue = from;
    }

    AssignConstraint (Local to, Local from) {
        toConstraintValue = new ConstraintValue(to);
        fromConstraintValue = new ConstraintValue(from);
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
    private final List<AssignConstraint> assignConstraints = new ArrayList<AssignConstraint>();

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
                    List<ConstraintValue> tmpValues = new ArrayList<ConstraintValue>();
                    tmpValues.add(assignConstraint.fromConstraintValue);
                    constraintsResults.put(assignConstraint.fromConstraintValue, tmpValues);
                } else {
                    if (!constraintsResults.containsKey(assignConstraint.toConstraintValue)) {
                        // to 之前不存在
                        List<ConstraintValue> tmpValues = new ArrayList<ConstraintValue>();
                        if (constraintsResults.containsKey(assignConstraint.fromConstraintValue)) {
                            // from 之前已经有保存
                            for (ConstraintValue tmpValue : constraintsResults.get(assignConstraint.fromConstraintValue)) {
                                if (!tmpValues.contains(tmpValue)) {
                                    tmpValues.add(tmpValue);
                                }
                            }
                        } else {
                            // from 之前没有保存
                            tmpValues.add(assignConstraint.fromConstraintValue);
                        }
                        constraintsResults.put(assignConstraint.fromConstraintValue, tmpValues);
                    } else {
                        // to 之前存在
//                        List<ConstraintValue> currentValues = constraintsResults.get(assignConstraint.toConstraintValue);
                        if (constraintsResults.containsKey(assignConstraint.fromConstraintValue)) {
                            // from 之前已经有保存
                            for (ConstraintValue tmpValue : constraintsResults.get(assignConstraint.fromConstraintValue)) {
                                if ( (constraintsResults.get(assignConstraint.toConstraintValue)).contains(tmpValue)) {
                                    (constraintsResults.get(assignConstraint.toConstraintValue)).add(tmpValue);
                                }
                            }
                        } else {
                            // from 之前没有保存
                            (constraintsResults.get(assignConstraint.toConstraintValue)).add(assignConstraint.fromConstraintValue);
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
                ConstraintValue toConstraintValue = item.getKey();
                List<ConstraintValue> fromConstraintValueList = item.getValue();
                if (toConstraintValue.fieldRefValue != null) {
                    stringResult.append(toConstraintValue.fieldRefValue.toString() + " : ");
                } else {
                    stringResult.append(toConstraintValue.localValue.toString() + " : ");
                }
                for (ConstraintValue fromConstraintValue : fromConstraintValueList) {
                    if (fromConstraintValue.fieldRefValue != null) {
                        stringResult.append(fromConstraintValue.fieldRefValue.toString() + " ");
                    } else {
                        stringResult.append(fromConstraintValue.localValue.toString() + " ");
                    }
                }
                stringResult.append("\n");
            }
            return stringResult.toString();
    }
}