package com.kyangc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author:  kyangc
 * Email:   kyangc@gmail.com
 * Date:    2016-03-24
 * Project: Demoplus
 */
public class PrepareData {

    public static final String TAG = "PrepareData";

    public void prepare(String inputLong, String inputShort, String output) {
        TreeMap<String, Integer> mDataCount = new TreeMap<>();
        TreeMap<String, Double> mDataMap = new TreeMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputShort));
            String line;
            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                String[] date = item[0].split("-");
                String key = date[0] + (date[1].length() == 1 ? "0" + date[1] : date[1]);
                if (!mDataCount.containsKey(key)) {
                    mDataCount.put(key, 1);
                }else{
                    int count = mDataCount.get(key);
                    mDataCount.put(key, ++count);
                }
            }

            reader = new BufferedReader(new FileReader(inputLong));
            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                String[] date = item[0].split("-");
                String key = date[0] + (date[1].length() == 1 ? "0" + date[1] : date[1]);
                mDataMap.put(key, Double.parseDouble(item[1]));
            }

            //out put
            File out = new File(output);
            if (!out.exists()) {
                out.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(output, true));
            for (Map.Entry<String, Integer> entry : mDataCount.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    bw.write(entry.getKey() + "," + mDataMap.get(entry.getKey()));
                    bw.newLine();
                }
            }
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
