package com.parameter.tools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class LogCommon {


    public static void WriteLogNormal(String logStr, String suffix) {
        String s = String.format(" 消息：%s\r\n", logStr);
        WriteLog(s, suffix);
    }


    public String getPath() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1, path.length());
        }
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "").replace("/WEB-INF/classes/","");
    }

    private static void WriteLog(String content, String suffix) {
        try {
            StringBuffer sb = new StringBuffer();
            String str = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS")) + "\n";
            sb.append(str);
            sb.append(content);
            String logPath = System.getProperty("user.dir") + "\\LogFile\\";
            logPath = new LogCommon().getPath() + "/LogFile/";
            File logPathFile = new File(logPath);
            if (!logPathFile.isDirectory()) {
                Boolean f = logPathFile.mkdir();
//                System.out.println(f);
            }
            String fileDic = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            File filedir = new File(logPath + fileDic);
            if (!filedir.isDirectory()) {
                filedir.mkdir();
                File[] fs = logPathFile.listFiles();
                LocalDateTime now = LocalDateTime.now();
                int delFlag = Integer.parseInt(now.minus(10, ChronoUnit.DAYS).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                for (File f : fs) {
                    int deldir = Integer.parseInt(f.getName());
                    if (deldir < delFlag) {
                        if (f.isDirectory()) {
                            deleteDirectory(f);
                        }
                    }
                }
            }
            String newFileName = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date());
            String fullFilePath = logPath + fileDic + "/" + newFileName + "_" + suffix + ".txt";
            try (RandomAccessFile reader = new RandomAccessFile(new File(fullFilePath), "rw");
                 FileLock lock = reader.getChannel().lock()) {
                reader.seek(reader.length());
                reader.write(sb.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void deleteDirectory(File file) {
        if (file.isFile()) {// 表示该文件不是文件夹
            file.delete();
        } else {
            // 首先得到当前的路径
            String[] childFilePaths = file.list();
            for (String childFilePath : childFilePaths) {
                File childFile = new File(file.getAbsolutePath() + "/" + childFilePath);
                deleteDirectory(childFile);
            }
            file.delete();
        }
    }
}
