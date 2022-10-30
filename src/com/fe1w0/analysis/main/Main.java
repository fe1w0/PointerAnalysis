package com.fe1w0.analysis.main;

import com.fe1w0.analysis.util.PointerAnalysis;

public class Main {
    // Main 先不着急写，先完成指针分析
    // arg 1 : sourceDirectory, class 地址
    // arg 2 : analysisClassName 检测函数名
    // Phase1 : 输出 所有的 body 中的语句
    // Phase2 : Pointer Analysis

    public static void main(String[] args) {
        String sourceDirectory = "sample";
        PointerAnalysis pointerAnalysis = new PointerAnalysis(sourceDirectory);

    }
}
