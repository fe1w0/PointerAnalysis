package com.fe1w0.analysis.util;

import java.io.*;

public class HandleOutput {
    /*
    将 结果 保存 到 result.txt 文件中
     */
    public static void SaveOutput(String outputs, String folderName) {
        try {
            File folderFile = new File(folderName);
            if (!folderFile.exists()){
                folderFile.mkdirs();
            }
            File file = new File(folderName + File.separator + "result.txt");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(outputs);
            fileWriter.flush();
            fileWriter.close();
            System.out.println("[+] Finish Write Outputs: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
