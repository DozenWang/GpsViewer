package com.ledongli;

import com.ledongli.logParser.AndroidLog;
import com.ledongli.logParser.ILocationParser;
import com.ledongli.util.GpsValidate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingjiu on 11/10/15.
 */
public class ViewGenerator {

    private static Options m_ParamsOptions;

    public static void main(String[] args) {

        initOptions();

        String helpStr = "java -jar gpsViewer.jar <logfile> [-o/--output][-l/--label][-h/--help]";
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cl;

        if (null == args || args.length < 1) {
            helpFormatter.printHelp(helpStr, m_ParamsOptions);
            return;
        }

        try {
            // 处理Options和参数
            String[] extArgs = null;
            if (args.length>1) {
                extArgs = new String[args.length-1];
            }

            for (int i = 1; i < args.length; i++) {
                extArgs[i-1] = args[i];
            }
            cl = parser.parse(m_ParamsOptions, extArgs);
        } catch (ParseException e) {
            helpFormatter.printHelp(helpStr, m_ParamsOptions); // 如果发生异常，则打印出帮助信息
            return;
        }

        if (cl.hasOption(PARAM_HELP)) {
            helpFormatter.printHelp(helpStr, m_ParamsOptions);
            return;
        }

        // 主要处理逻辑
        String labelType = cl.hasOption(PARAM_LABEL)?cl.getOptionValue(PARAM_LABEL):"none";
        String outPutFilePath = cl.hasOption(PARAM_OUTPUTDIR) ? cl.getOptionValue(PARAM_OUTPUTDIR) : null;
        String logFile = getAbsPath(args[0]);
        String platform = cl.hasOption(PARAM_PLATFORM) ? cl.getOptionValue(PARAM_PLATFORM) : "android";
        String pointInterval = cl.hasOption(PARAM_INTERVAL)?cl.getOptionValue(PARAM_INTERVAL):null;

        if (null == outPutFilePath) {
            File appFilePath = new File(ViewGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath());

            outPutFilePath = appFilePath.getParent() + "/" + "output";
        }

        ILocationParser locationParser = null;
        if (platform.equals("android")) {
            locationParser = new AndroidLog();
        }

        if (null == locationParser) {
            System.err.println("暂不支持此log文件的解析");
            return;
        }

        // 写json文件
        List<Location> locationList = locationParser.parseLocations(logFile);
        if (null == locationList || locationList.size() == 0) {
            System.err.println("没解析到location数据");
            return;
        }

        if (cl.hasOption(PARAM_FILTER)) {
            locationList = filterLoc(locationList);
        }

        if (cl.hasOption(PARAM_VERBOS)) {
            printVerbos(locationList);
        }

        int begin=0,end=0;
        if (cl.hasOption(PARAM_INTERVAL)) {
            String interval[] = pointInterval.split(",");
            if (null == interval || interval.length < 2) {
                System.err.println("i 的参数错误，正确格式如 i=5,9");
                return;
            }

            begin = Integer.parseInt(interval[0]);
            end = Integer.parseInt(interval[1]);
            if (end < begin) {
                System.err.println("起始位置必须小于结束为止");
                return;
            }
            locationList = locationList.subList(begin, end);
        }

        JSONArray json = JSONArray.fromObject(locationList);

        try {
            // 写locations
            writeFile("gLocations=" + json.toString(), outPutFilePath + "/locations.json.js");

            // 写配置文件
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("labelType", labelType);
            jsonObject.put("index", begin);
            writeFile("gConf=" + jsonObject.toString(), outPutFilePath + "/conf.json.js");
        } catch (IOException e) {
            System.err.println("erro : 写位置数据失败");
            e.printStackTrace();
        }

        // 写静态的js和html文件
        outputStaticFiles(outPutFilePath);
    }

    private static final String PARAM_OUTPUTDIR = "o";
    private static final String PARAM_LABEL = "l";
    private static final String PARAM_INTERVAL = "i";
    private static final String PARAM_HELP = "h";
    private static final String PARAM_PLATFORM = "p";
    private static final String PARAM_FILTER = "f";
    private static final String PARAM_VERBOS = "v";

    private static void initOptions() {

        Option outputOpt = Option.builder(PARAM_OUTPUTDIR).desc("结果输出路径")
                .longOpt("output")
                .hasArg()
                .type(String.class)
                .valueSeparator(' ')
                .build();

        Option filterOpt = Option.builder(PARAM_FILTER).desc("使用android上的gps过滤策略")
                .longOpt("filter")
                .build();

        Option platformOpt = Option.builder(PARAM_PLATFORM).desc("日志文件类型[android/ios],默认为android")
                .longOpt("platform")
                .hasArg()
                .type(String.class)
                .valueSeparator(' ')
                .build();

        Option labelOpt = Option.builder(PARAM_LABEL).desc("输出标签的种类[time/num],默认不显示标签")
                .longOpt("label")
                .hasArg()
                .type(String.class)
                .valueSeparator(' ')
                .build();

        Option intervalOpt = Option.builder(PARAM_INTERVAL).desc("显示某个区间内的点 i=[begin,end]")
                .longOpt("interval")
                .hasArg()
                .type(String.class)
                .valueSeparator('=')
                .build();

        Option verbosOpt = Option.builder(PARAM_VERBOS).desc("处理后的gps点和距离等信息")
                .longOpt("verbos")
                .build();

        Option helpOpt = Option.builder(PARAM_HELP).desc("显示帮助")
                .longOpt("help")
                .build();

        m_ParamsOptions = new Options();
        m_ParamsOptions.addOption(outputOpt);
        m_ParamsOptions.addOption(filterOpt);
        m_ParamsOptions.addOption(platformOpt);
        m_ParamsOptions.addOption(labelOpt);
        m_ParamsOptions.addOption(intervalOpt);
        m_ParamsOptions.addOption(verbosOpt);
        m_ParamsOptions.addOption(helpOpt);
    }

    /**
     * 获取文件的绝对位置
     *
     * @param filePath
     * @return
     */
    private static String getAbsPath(String filePath) {
        if (filePath.startsWith("/")) {
            return filePath;
        } else {
            File appFilePath = new File(ViewGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            return (appFilePath.getParent() + "/" + filePath);
        }
    }

    /**
     *
     */
    private static void outputStaticFiles(String folderPath) {

        String staticFile[] = {"index.html", "css/index.css", "js/jquery-2.1.4.js", "js/index.js"};

        try {
            for (String sf : staticFile) {
                InputStream is = ClassLoader.getSystemResourceAsStream("html/" + sf);

                writeFile(is, folderPath + "/" + sf);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeFile(InputStream is, String targetFile) throws IOException {
        ensureFolderExist(targetFile);

        BufferedReader br = null;
        PrintStream ps = null;

        try {
            br = new BufferedReader(new InputStreamReader(is));
            ps = new PrintStream(new FileOutputStream(targetFile));
            String s;
            while ((s = br.readLine()) != null) {
                ps.println(s);
                ps.flush();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (br != null) br.close();
                if (ps != null) ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void writeFile(String str, String targetFile) throws IOException {
        ensureFolderExist(targetFile);
        FileWriter fileWriter = new FileWriter(targetFile);
        fileWriter.write(str);
        fileWriter.flush();
        fileWriter.close();
    }

    private static void ensureFolderExist(String targetFile) {
        File file = new File(targetFile);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    private static List<Location> filterLoc(List<Location> locations) {
        GpsValidate<Location> m_gpsValidate = new GpsValidate<Location>(new GpsValidate.LocationPicker<Location>() {
            @Override
            public double getLongitude(Location obj) {
                return obj.getLongitude();
            }

            @Override
            public double getLatitude(Location obj) {
                return obj.getLatitude();
            }

            @Override
            public float getSpeed(Location obj) {
                return obj.getSpeed();
            }

            @Override
            public long getTime(Location obj) {
                return obj.getTime();
            }

            @Override
            public float getAccuracy(Location obj) {

                return obj.getAccuracy();
            }

        });

        Location lastLoc = null;
        List<Location> result = new ArrayList<Location>();
        for (Location loc : locations) {
            if (m_gpsValidate.validate(lastLoc, loc)) {
                lastLoc = loc;
                result.add(loc);
            }
        }
        return result;
    }

    private static void printVerbos(List<Location> locationList) {
        System.out.println("-------------------处理后GPS序列--------------------");
        double distance = 0;

        for (int i = 0; i < locationList.size(); i++) {
            System.out.print((i + 1) + ": " + locationList.get(i).toString());

            if (i >= 1) {
                long timeinterval = (locationList.get(i).getTime() - locationList.get(i-1).getTime());
                double tdis=0;
                tdis = locationList.get(i).distanceTo(locationList.get(i-1));
                distance += tdis;

                System.out.println("\t (timeInterval="+ (timeinterval/1000) +" distance="+tdis+")");

            } else {
                System.out.println();
            }
        }

        System.out.println("------------------总距离---------------------");
        System.out.println("距离: " + distance);
    }

}
