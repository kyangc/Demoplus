package com.kyangc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Author:  kyangc
 * Email:   kyangc@gmail.com
 * Date:    2016-03-25
 * Project: Demoplus
 */
public class MonthSetter {

    public static final String TAG = "MonthSetter";

    public void doSetter(String input, String output) {
        try {
            File out = new File(output);
            if (!out.exists()) {
                out.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(output, true));
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                String[] date = item[0].split("-");
                int month = Integer.parseInt(date[1]);
                StringBuilder sb = new StringBuilder(line);
                for (int i = 1; i <= 12; i++) {
                    sb.append(i == month ? ",1" : ",0");
                }
                bw.write(sb.toString());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
