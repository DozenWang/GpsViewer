package com.ledongli.logParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingjiu on 11/11/15.
 */
public class LogPickers {

    public static List<String> getLogWithoutTag(String path, String tag) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            List<String> result = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (null == tag || "".equals(tag)) {
                    result.add(line);
                } else if (line.contains(tag)) {
                    String record = line.substring(line.indexOf(tag) + tag.length()).trim();
                    result.add(record);
                }
            }
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getLogWithTag(String path, String tag) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            List<String> result = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (null == tag || "".equals(tag)) {
                    result.add(line);
                } else if (line.contains(tag)) {
                    String record = line.substring(line.indexOf(tag)).trim();
                    result.add(record);
                }
            }
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getLineRecordBetween(String path, String pre, String next) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            List<String> result = new ArrayList<String>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains(pre) && line.contains(next)) {
                    String rec = line.substring(line.indexOf(pre)+pre.length(), line.indexOf(next));
                    result.add(rec);
                }
            }
            return  result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
