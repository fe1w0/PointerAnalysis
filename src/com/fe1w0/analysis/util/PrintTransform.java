package com.fe1w0.analysis.util;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;

import java.util.Map;

public class PrintTransform extends SceneTransformer {
    // Transform 打印所有的指令，主要方便 后面 写 分析代码
    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        for (SootClass sc: Scene.v().getClasses()){
            System.out.println(sc.getName());
        }
    }
}
