package com.fe1w0.analysis.util;

import soot.*;
import soot.options.Options;
import soot.util.Chain;

import java.util.Collections;
import java.util.List;

public class PointerAnalysis {

    public static void setupSoot(String sourceDirectory) {
        G.reset();
        Options.v().set_verbose(true);
        Options.v().set_prepend_classpath(true);
        // 允许 allow_phantom_refs
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(sourceDirectory);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_process_dir(Collections.singletonList(sourceDirectory));
        // 我不太理解为什么要开启 cg.spark
        // Spark is a flexible points-to analysis (pointer analysis) framework. Aside from building a call graph,
        // it also generates information about the targets of pointers.
        // For details about Spark, please see Ondrej Lhotak's M.Sc. thesis.
        // 不是说，不让直接用 soot 的 pointer analysis 分析工具吗
        Options.v().setPhaseOption("cg.spark", "enabled:true");
        // 处理对象是 whole-jimple transformation pack
        Options.v().set_whole_program(true);
        // 在 wjtp Pack 中添加新的 Transform，并开启
//        PackManager.v().getPack("wjtp").add(new Transform("wjtp.printAll", new PrintTransform()));
//        Options.v().setPhaseOption("wjtp.printAll", "enabled:true");
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.ownPta", new AnalysisTransform()));
        Options.v().setPhaseOption("wjtp.ownPta", "enabled:true");
        // https://github.com/soot-oss/soot/issues/1061
        Options.v().set_keep_line_number(true);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();
    }

    public PointerAnalysis(String sourceDirectory) {
        // 设置 soot 环境
        setupSoot(sourceDirectory);
    }
}
