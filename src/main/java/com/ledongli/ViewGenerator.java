package com.ledongli;

import com.ledongli.logParser.AndroidLog;
import com.ledongli.logParser.ILocationParser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.cli.*;

import java.io.*;
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

        JSONArray json = JSONArray.fromObject(locationList);

        try {
            // 写locations
            writeFile("gLocations=" + json.toString(), outPutFilePath + "/locations.json.js");

            // 写配置文件
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("labelType", labelType);
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
    private static final String PARAM_HELP = "h";
    private static final String PARAM_PLATFORM = "p";

    private static void initOptions() {

        Option outputOpt = Option.builder(PARAM_OUTPUTDIR).desc("结果输出路径")
                .longOpt("output")
                .hasArg()
                .type(String.class)
                .valueSeparator('=')
                .build();

        Option platformOpt = Option.builder(PARAM_PLATFORM).desc("日志文件类型[android/ios],默认为android")
                .longOpt("platform")
                .hasArg()
                .type(String.class)
                .valueSeparator('=')
                .build();

        Option labelOpt = Option.builder(PARAM_LABEL).desc("输出标签的种类[time/num],默认不显示标签")
                .longOpt("label")
                .hasArg()
                .type(String.class)
                .valueSeparator('=')
                .build();

        Option helpOpt = Option.builder(PARAM_HELP).desc("显示帮助")
                .longOpt("help")
                .build();

        m_ParamsOptions = new Options();
        m_ParamsOptions.addOption(outputOpt);
        m_ParamsOptions.addOption(platformOpt);
        m_ParamsOptions.addOption(labelOpt);
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

}
