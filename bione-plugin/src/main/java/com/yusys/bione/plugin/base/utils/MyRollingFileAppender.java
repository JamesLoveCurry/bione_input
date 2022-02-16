package com.yusys.bione.plugin.base.utils;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.RollingFileAppender;

import org.apache.log4j.helpers.CountingQuietWriter;

import org.apache.log4j.helpers.LogLog;

import org.apache.log4j.spi.LoggingEvent;

public class MyRollingFileAppender extends RollingFileAppender {
    private long nextRollover = 0;

    private static Map<String, BeginFileData> fileMaps = new HashMap<String, BeginFileData>();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

    public void rollOver() {
        File target;
        File file;
        if (qw != null) {
            long size = ((CountingQuietWriter) qw).getCount();
            LogLog.debug("rolling over count=" + size);
            nextRollover = size + maxFileSize;
        }
        //1
        LogLog.debug("maxBackupIndex=" + maxBackupIndex);
        String nowDateString = sdf.format(new Date());
        String newFileName = (fileName.indexOf(".") != -1 ? fileName.substring(0,
                fileName.lastIndexOf(".")) : fileName);
        boolean renameSucceeded = true;
        if (maxBackupIndex > 0) {
            /**
             * 删除最后一个文件，文件名+1
             * */
            for (int i = 0; (i <= maxBackupIndex && renameSucceeded); i++) {
                file = new File(getNewFileName(newFileName, i));
                if (!file.exists()) {
                    //行动文件名
                    this.closeFile();
                    target = new File(fileName);
                    LogLog.debug("Renaming file " + file + " to " + target);
                    renameSucceeded = target.renameTo(file);
                    break;
                }
            }

            if (renameSucceeded) {
                BeginFileData beginFileData = fileMaps.get(fileName);
                // 在每天一个日志目录的方式下，检测日期是否变更了，如果变更了就要把变更后的日志文件拷贝到变更后的日期目录下。
                String pattern = "yyyyMMdd";
                String oldFileName = "";
                if (newFileName.indexOf(nowDateString) == -1
                        && beginFileData.getFileName().indexOf(pattern) != -1) {
                    oldFileName = newFileName;
                    newFileName = beginFileData.getFileName().replace(pattern,
                            nowDateString);

                    target = new File(newFileName);
                    this.closeFile();
                    file = new File(fileName);
                    LogLog.debug("Renaming file " + file + " to " + target);

                    renameSucceeded = file.renameTo(target);
                }
                if (StringUtils.isNotEmpty(oldFileName)) {
                    for (int i = 0; (i <= maxBackupIndex && renameSucceeded); i++) {
                        file = new File(getNewFileName(newFileName, i));
                        if (!file.exists()) {
                            target = new File(oldFileName);
                            LogLog.debug("Renaming file " + file + " to " + target);
                            renameSucceeded = target.renameTo(file);
                            break;
                        }
                    }
                }
                if (!renameSucceeded) {
                    try {
                        this.setFile(fileName, true, bufferedIO, bufferSize);
                    } catch (IOException e) {
                        LogLog.error("setFile(" + fileName + ", true) call failed.", e);
                    }
                }
            }
        }
        if (renameSucceeded) {

            try {

                this.setFile(fileName, false, bufferedIO, bufferSize);
                nextRollover = 0;
            } catch (IOException e) {
                LogLog.error("setFile(" + fileName + ", false) call failed.", e);
            }
        }

    }


    /**
     * This method differentiates RollingFileAppender from its super class.
     *
     * @since 0.9.0
     */
    protected void subAppend(LoggingEvent event) {
        super.subAppend(event);
        if (fileName != null && qw != null) {

            String nowDate = sdf.format(new Date());
            // 检测日期是否已经变更了，如果变更了就要重创建日期目录
            if (!fileMaps.get(fileName).getDate().equals(nowDate)) {
                rollOver();
                return;
            }

            long size = ((CountingQuietWriter) qw).getCount();
            if (size >= maxFileSize && size >= nextRollover) {
                rollOver();
            }
        }
    }

    /**
     * @方法描述: 储存file变量。
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/3/13 17:01
     * @Param: fileName
     * @Param: append
     * @Param: bufferedIO
     * @Param: bufferSize
     * @return: void
     */
    @Override
    public synchronized void setFile(String fileName, boolean append,
                                     boolean bufferedIO, int bufferSize) throws IOException {

        String pattern = "yyyyMMdd";
        String nowDate = sdf.format(new Date());
        // 如果文件路径包含了“yyyy-MM-dd”就是每天一个日志目录的方式记录日志(第一次的时候)
        if (fileName.indexOf(pattern) != -1) {
            String beginFileName = fileName;
            fileName = fileName.replace(pattern, nowDate);
            fileMaps.put(fileName, new BeginFileData(beginFileName, nowDate));
        }
        BeginFileData beginFileData = fileMaps.get(fileName);
        // 检测日期是否已经变更了，如果变更了就要把原始的字符串给fileName变量，把变更后的日期做为开始日期
        if (!beginFileData.getDate().equals(nowDate)) {
            // 获取出第一次的文件名
            beginFileData.setDate(nowDate);
            fileName = beginFileData.getFileName().replace(pattern, nowDate);
            fileMaps.put(fileName, beginFileData);
        }

        // D:/data/test/yyyy-MM-dd/test.log 替换yyyy-MM-dd为当前日期。
        File file = new File(fileName);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
    }


    private class BeginFileData {

        public BeginFileData(String fileName, String date) {
            super();
            this.fileName = fileName;
            this.date = date;
        }

        private String fileName;
        private String date;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
    /**
     * @方法描述: 拼接日需求的日志名称
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/3/11 9:57
     * @Param: fileName
     * @Param: num
     * @return: java.lang.String
     */
    public String getNewFileName(String fileName, int num) {
        fileName = fileName.substring(0, fileName.lastIndexOf("_") - 2);
        if (num < 10) {
            fileName = fileName + "00_" + num + ".log";
        } else if (num < 100) {
            fileName = fileName + "0" + num/10%10 + "_" + num%10 + ".log";
        } else {
            fileName = fileName + num/100%10 + num/10%10 + "_" + num%10 + ".log";
        }
        return fileName;
    }
}