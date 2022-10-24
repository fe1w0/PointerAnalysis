package com.fe1w0.analysis.main;

import com.fe1w0.analysis.util.PointerAnalysis;

import java.io.File;

import static com.fe1w0.analysis.util.HandleOutput.SaveOutput;

public class Main {
    // Main 先不着急写，先看看他们指针分析是怎么做的
    // Main 主要接受 两个参数
    // arg 1 : sourceDirectory, class 地址
    // arg 2 : analysisClassName 检测函数名
    // 先输出 所有的 body 中的语句

    public static void main(String[] args) {
        String sourceDirectory = "sample";
        PointerAnalysis pointerAnalysis = new PointerAnalysis(sourceDirectory);

    }
}
