package com.fe1w0.analysis.util;

import fj.Hash;
import soot.Local;
import soot.SootField;
import soot.ValueBox;
import soot.jimple.FieldRef;

import java.util.*;


class ConstraintValue {
    // to, from 我们都认为是一个 ConstraintValue
    // ConstraintValue 是由一个 Local 和 一个 FieldRef
    Local localValue;
//    FieldRef fieldRefValue;
    SootField fieldValue;

    ConstraintValue (Local local){
        // 约束的变量为 Local
        localValue = local;
        fieldValue = null;
//        System.out.println("Local HashCode: " + localValue.hashCode());
    }

    ConstraintValue (FieldRef fieldRef) throws ClassCastException {
        List<Local> localList = ConstraintValue.getLocalFromFieldRef(fieldRef);
        if (localList.size() == 1) {
            localValue = localList.get(0);
            fieldValue = fieldRef.getField();
//            System.out.println("Local HashCode: " + localValue.hashCode() +
//                    ";FieldRef HashCode: " + fieldRef.hashCode() +
//                    ";FieldRef Field HashCode: " + fieldRef.getField().hashCode());
        } else {
            throw new ClassCastException("Owning to localList.size() not equal 1, program fails to construct AssignConstraint object");
        }
    }

    public static List<Local> getLocalFromFieldRef(FieldRef fieldRef) {
        // 得到fieldRef 的 所有 Local，但为什么我感觉FieldRef只有一个Local？
        List<Local> localList = new ArrayList<Local>();
        for (ValueBox valueBox : fieldRef.getUseBoxes()) {
            localList.add((Local)valueBox.getValue());
        }
        return localList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintValue that = (ConstraintValue) o;
        return Objects.equals(localValue, that.localValue) && Objects.equals(fieldValue, that.fieldValue);
    }

    @Override
    public int hashCode() {
        if (fieldValue == null) {
            return localValue.hashCode();
        } else {
            return fieldValue.hashCode();
        }
    }
}


class AssignConstraint {

    ConstraintValue toConstraintValue;
    ConstraintValue fromConstraintValue;


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
    private final LinkedHashSet<AssignConstraint> assignConstraints = new LinkedHashSet<AssignConstraint>();

    // Anderson求解结果, constraintsResults anderson 约束求解结果
    LinkedHashMap<ConstraintValue, LinkedHashSet<ConstraintValue>> constraintsResults = new LinkedHashMap<ConstraintValue, LinkedHashSet<ConstraintValue>>();

    // assignConstraints 添加新约束信息，用于后续分析
    void addAssignConstraints(AssignConstraint assignConstraint) {
        assignConstraints.add(assignConstraint);
    }

    void solver() {
//        System.out.println(printValues());
        try {
            for (AssignConstraint assignConstraint : assignConstraints) {
                if (assignConstraint.fromConstraintValue.equals(assignConstraint.toConstraintValue)){
                    LinkedHashSet<ConstraintValue> tmpValues = new LinkedHashSet<ConstraintValue>();
                    tmpValues.add(assignConstraint.toConstraintValue);
                    constraintsResults.put(assignConstraint.toConstraintValue, tmpValues);
                } else {
                    if (!constraintsResults.containsKey(assignConstraint.toConstraintValue)) {
                        // to 之前不存在
                        LinkedHashSet<ConstraintValue> tmpValues = new LinkedHashSet<ConstraintValue>();
                        tmpValues.add(assignConstraint.toConstraintValue);
                        if (constraintsResults.containsKey(assignConstraint.fromConstraintValue)) {
                            // from 之前已经有保存 constraintsResults.containsKey。
                            for (ConstraintValue tmpValue : constraintsResults.get(assignConstraint.fromConstraintValue)) {
                                if (!tmpValues.contains(tmpValue)) {
                                    tmpValues.add(tmpValue);
                                }
                            }
                        } else {
                            // from 之前没有保存
                            tmpValues.add(assignConstraint.fromConstraintValue);
                        }
                        constraintsResults.put(assignConstraint.toConstraintValue, tmpValues);
                    } else {
                        // to 之前存在
                        if (constraintsResults.containsKey(assignConstraint.fromConstraintValue)) {
                            // from 之前已经有保存
                            for (ConstraintValue tmpValue : constraintsResults.get(assignConstraint.fromConstraintValue)) {
                                if ( !(constraintsResults.get(assignConstraint.toConstraintValue)).contains(tmpValue)) {
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

    public String printValues() {
        StringBuilder stringResult  = new StringBuilder();
        for (AssignConstraint assignConstraint : assignConstraints) {
            if (assignConstraint.toConstraintValue.fieldValue != null) {
                stringResult.append(assignConstraint.toConstraintValue.localValue.toString() + "." +
                        assignConstraint.toConstraintValue.fieldValue.toString()).append(" : ");
            } else {
                stringResult.append(assignConstraint.toConstraintValue.localValue.toString()).append(" : ");
            }
            if (assignConstraint.fromConstraintValue.fieldValue != null) {
                stringResult.append(assignConstraint.toConstraintValue.localValue.toString() + "." +
                        assignConstraint.fromConstraintValue.fieldValue.toString()).append("\n");
            } else {
                stringResult.append(assignConstraint.fromConstraintValue.localValue.toString()).append("\n");
            }
        }
        stringResult.append("\n");
        return stringResult.toString();
    }

    String getStringResult(){
        StringBuilder stringResult  = new StringBuilder();
        for(Map.Entry<ConstraintValue, LinkedHashSet<ConstraintValue>> item : constraintsResults.entrySet()) {
            ConstraintValue toConstraintValue = item.getKey();
            LinkedHashSet<ConstraintValue> fromConstraintValueList = item.getValue();
            if (toConstraintValue.fieldValue != null) {
                stringResult.append(toConstraintValue.localValue.toString() + "." + toConstraintValue.fieldValue.toString()).append(" : ");
            } else {
                stringResult.append(toConstraintValue.localValue.toString()).append(" : ");
            }
//            stringResult.append("(hashCode:" + toConstraintValue.hashCode() + ") ");
            for (ConstraintValue fromConstraintValue : fromConstraintValueList) {
                if (fromConstraintValue.fieldValue != null) {
                    stringResult.append(fromConstraintValue.localValue.toString() + "." + fromConstraintValue.fieldValue.toString()).append(" ");
                } else {
                    stringResult.append(fromConstraintValue.localValue.toString()).append(" ");
                }
            }
            stringResult.append("\n");
        }
        return stringResult.toString();
    }
}