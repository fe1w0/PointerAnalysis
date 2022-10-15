package com.fe1w0.analysis.util;

import com.sun.org.apache.bcel.internal.generic.NEW;
import soot.Local;

import java.util.*;

/*
   产生新的约束
 */
class AssignConstraint {
    // AssignConstraint 指 Jimple 中
    // $r0 = new hello.A 这类 new 操作
    // 以及 r4 = $r0 这类 AssignConstraint
    // 产生的约束信息为: Constraint($r0, $r0);
    // 以及 Constraint(r4, $r0);
    // 这里的存储的是 Local，还是String
    // 我的想法是存储 Local，从而尽可能保留信息，String 可以在输出的时候进行处理
    Local to, from;
    AssignConstraint(Local to, Local from) {
        this.to = to;
        this.from = from;
    }
}

public class SimpleAnderson {
    // 存储所有的约束信息
    private List<AssignConstraint> assignConstraints = new ArrayList<AssignConstraint>();

    // Anderson求解结果, constraintsResults anderson 约束求解结果
    Map<Local, List<Local>> constraintsResults = new HashMap<Local, List<Local>>();

    // assignConstraints 添加新约束信息，用于后续分析
    void addAssignConstraints(AssignConstraint currentAssignConstraint) {
        assignConstraints.add(currentAssignConstraint);
    }

    void run(){

    }

}
