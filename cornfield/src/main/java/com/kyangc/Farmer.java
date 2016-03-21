package com.kyangc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Farmer {
    public static void main(String[] args) {
        String filePath = "/Users/chengkangyang/Desktop/data.csv";
        String resultPath = "/Users/chengkangyang/Desktop/4.csv";
        TreeMap<String, List<Double>> mMonthData = new TreeMap<>();
        TreeMap<String, Double> mResultMap = new TreeMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                String[] date = item[0].split("-");
                String key = date[0] + (date[1].length() == 1 ? "0" + date[1] : date[1]);
                if (!mMonthData.containsKey(key)) {
                    mMonthData.put(key, new ArrayList<>());
                }
                mMonthData.get(key).add(Double.parseDouble(item[4]));
            }

            for (Map.Entry<String, List<Double>> entry : mMonthData.entrySet()) {
                //Mean
                double mean = 0;
                List<Double> list = entry.getValue();
                for (Double i : list) {
                    mean += i;
                }
                mean = mean / list.size();

                //Square sum
                double squareSum = 0;
                for (double i : list) {
                    squareSum += Math.pow(i - mean, 2);
                }
                squareSum = Math.sqrt(squareSum);

                //Result
                mResultMap.put(entry.getKey(), squareSum / Math.sqrt(list.size() - 1));
            }

            //out put
            File output = new File(resultPath);
            if (!output.exists()) {
                output.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(output, true));
            for (Map.Entry<String, Double> entry : mResultMap.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
